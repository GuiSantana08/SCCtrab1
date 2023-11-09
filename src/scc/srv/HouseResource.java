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
import redis.clients.jedis.Jedis;

import scc.cache.RedisCache;
import scc.db.HouseDBLayer;
import scc.db.UserDBLayer;
import scc.interfaces.HouseResourceInterface;
import scc.search.Props;
import scc.utils.House;
import scc.utils.HouseDAO;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/house")
public class HouseResource implements HouseResourceInterface {

    ObjectMapper mapper = new ObjectMapper();
    HouseDBLayer houseDb = HouseDBLayer.getInstance();

    UserDBLayer userDb = UserDBLayer.getInstance();

    @Override
    public Response createHouse(Cookie session, House house) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            UserResource.checkCookieUser(session, house.getUserId());

            HouseDAO hDAO = new HouseDAO(house);
            CosmosItemResponse<HouseDAO> h = houseDb.putHouse(hDAO);
            jedis.set(hDAO.getId(), mapper.writeValueAsString(hDAO));

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
    public Response deleteHouse(Cookie session, String id) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            // UserResource.checkCookieUser(session, house.getUserId()); TODO

            houseDb.delHouseById(id);
            jedis.del(id);
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
    public Response getHouse(String id) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            String res = jedis.get(id);
            if (res == null) {
                var h = houseDb.getHouseById(id);
                jedis.set(id, mapper.writeValueAsString(h));
                return Response.ok(h.iterator().next()).build();
            }

            HouseDAO h = mapper.readValue(res, HouseDAO.class);

            return Response.ok(h).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response updateHouse(Cookie session, House house) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            UserResource.checkCookieUser(session, house.getUserId());

            HouseDAO hDAO = new HouseDAO(house);
            CosmosItemResponse<HouseDAO> h = houseDb.updateHouse(house.getId(), hDAO);

            jedis.set(house.getId(), mapper.writeValueAsString(house));
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
                if (!h.getAvailable().get(currentMonth).isOcupied())
                    housesList.add(h);
            }

            return Response.ok(housesList.toString()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response searchAvailableHouses(String period, String location) {
        List<HouseDAO> housesList = new ArrayList<>();
        try {
            CosmosPagedIterable<HouseDAO> houseCosmos = houseDb.getHouseByLocation(location);
            String[] months = period.split(":");
            Month startMonth = Month.valueOf(months[0].toUpperCase());
            Month endMonth = Month.valueOf(months[1].toUpperCase());

            for (HouseDAO h : houseCosmos) {
                boolean isOcupied = false;
                do {
                    if (h.getAvailable().get(startMonth.toString().toLowerCase()).isOcupied())
                        isOcupied = true;

                    startMonth = startMonth.plus(1);
                } while (!startMonth.equals(endMonth));
                if (!isOcupied)
                    housesList.add(h);
            }

            return Response.ok(housesList.toString()).build();
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

}
