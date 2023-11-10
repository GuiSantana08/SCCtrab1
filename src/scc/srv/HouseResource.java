package scc.srv;

import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchDocument;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.util.SearchPagedIterable;
import com.azure.search.documents.util.SearchPagedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import scc.cache.RedisCache;
import scc.db.HouseDBLayer;
import scc.db.RentalDBLayer;
import scc.db.UserDBLayer;
import scc.interfaces.HouseResourceInterface;
import scc.search.Props;
import scc.utils.House;
import scc.utils.HouseDAO;
import scc.utils.RentalDAO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/house")
public class HouseResource implements HouseResourceInterface {

    ObjectMapper mapper = new ObjectMapper();

    HouseDBLayer houseDb = HouseDBLayer.getInstance();
    RentalDBLayer rentDb = RentalDBLayer.getInstance();
    UserDBLayer userDb = UserDBLayer.getInstance();

    static RedisCache cache = RedisCache.getInstance();

    @Override
    public Response createHouse(boolean isCacheActive, boolean isAuthActive, Cookie session, House house) {
        try {
            if (isAuthActive) {
                UserResource.checkCookieUser(session, house.getUserId());
            }

            HouseDAO hDAO = new HouseDAO(house);
            CosmosItemResponse<HouseDAO> h = houseDb.putHouse(hDAO);

            if (isCacheActive) {
                cache.setValue(hDAO.getId(), hDAO);
            }

            return Response.ok(h.getItem().toString()).build();
        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response deleteHouse(boolean isCacheActive, boolean isAuthActive, Cookie session, String id) {
        try {
            if (isAuthActive) {
                // UserResource.checkCookieUser(session, house.getUserId()); TODO
            }

            houseDb.delHouseById(id);

            if (isCacheActive) {
                cache.delete(id, HouseDAO.class);
            }

            return Response.ok().build();
        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response getHouse(boolean isCacheActive, boolean isAuthActive, String id) {
        try {
            HouseDAO h = null;
            if (isCacheActive) {
                h = cache.getValue(id, HouseDAO.class);
            }

            if (h == null) {
                var newH = houseDb.getHouseById(id);

                if (isCacheActive) {
                    cache.setValue(id, newH);
                }

                return Response.ok(newH.iterator().next()).build();
            }

            return Response.ok(h).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response updateHouse(boolean isCacheActive, boolean isAuthActive, Cookie session, House house) {
        try {
            if (isAuthActive) {
                UserResource.checkCookieUser(session, house.getUserId());
            }

            HouseDAO hDAO = new HouseDAO(house);
            CosmosItemResponse<HouseDAO> h = houseDb.updateHouse(house.getId(), hDAO);

            if (isCacheActive) {
                cache.setValue(house.getId(), house);
            }

            return Response.ok(h.getItem().toString()).build();
        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response listAvailableHouses(String location) {
        List<HouseDAO> housesList = new ArrayList<>();
        try {
            CosmosPagedIterable<HouseDAO> houseCosmos = houseDb.getHouseByLocation(location);
            String currentMonth = LocalDate.now().getMonth().toString().toLowerCase();

            for (HouseDAO h : houseCosmos) {
                CosmosPagedIterable<RentalDAO> rentals = rentDb.getHouseById(h.getId());
                boolean isOn = true;
                for (RentalDAO r : rentals) {
                    if (r.getRentalPeriod().contains(currentMonth))
                        isOn = false;
                }
                if (isOn)
                    housesList.add(h);
            }

            return Response.ok(housesList).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response searchAvailableHouses(String period, String location) {
        List<HouseDAO> availableHouses = new ArrayList<>();
        try {
            CosmosPagedIterable<HouseDAO> houseCosmos = houseDb.getHouseByLocation(location);
            String[] months = period.split("-");

            for (HouseDAO h : filterAvailableHouses(houseCosmos, months)) {
                CosmosPagedIterable<RentalDAO> rentals = rentDb.getHouseById(h.getId());
                boolean isOn = true;
                for (RentalDAO r : rentals) {
                    for (String month : months) {
                        if (r.getRentalPeriod().contains(month))
                            isOn = false;
                    }
                }
                if (isOn)
                    availableHouses.add(h);
            }

            return Response.ok(availableHouses).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/trysearch")
    @Produces(MediaType.APPLICATION_JSON)
    public Response trySearch() { // TODO make a better function
        SearchClient searchClient = Props.searchClient();

        String queryText = "anibal";
        SearchOptions options = new SearchOptions()
                .setIncludeTotalCount(true)
                .setFilter("location eq 'Elvas'")
                .setSelect("rid", "userId", "name", "location", "description")
                .setSearchFields("name")
                .setTop(5);

        SearchPagedIterable searchPagedIterable = searchClient.search(queryText, options, null);

        Map<String, Object> map = new HashMap<>();
        for (SearchPagedResponse resultResponse : searchPagedIterable.iterableByPage()) {
            resultResponse.getValue().forEach(searchResult -> {
                for (Map.Entry<String, Object> res : searchResult.getDocument(SearchDocument.class).entrySet()) {
                    map.put(res.getKey(), res.getValue());
                }
            });
        }

        return Response.ok(map).build();
    }

    private List<HouseDAO> filterAvailableHouses(CosmosPagedIterable<HouseDAO> houseCosmos, String[] months) {
        List<HouseDAO> availableHouses = new ArrayList<>();

        for (HouseDAO h : houseCosmos) {
            for (String m : months) {
                if (h.getAvailability().contains(m)) {
                    availableHouses.add(h);
                    break;
                }
            }
        }

        return availableHouses;
    }

}
