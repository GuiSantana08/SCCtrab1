package scc.srv;

import java.util.ArrayList;
import java.util.List;

import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import scc.cache.RedisCache;
import scc.db.HouseDBLayer;
import scc.db.RentalDBLayer;
import scc.interfaces.RentalResourceInterface;
import scc.utils.HouseDAO;
import scc.utils.Rental;
import scc.utils.RentalDAO;

@Path("/house/{id}/rental") // TODO: instead of inserting house on json, use id to get it
public class RentalResource implements RentalResourceInterface {

    ObjectMapper mapper = new ObjectMapper();
    RentalDBLayer rentalDB = RentalDBLayer.getInstance();
    HouseDBLayer houseDB = HouseDBLayer.getInstance();

    static RedisCache cache = RedisCache.getInstance();

    @Override
    public Response createRental(boolean isCacheActive, boolean isAuthActive, Cookie session, Rental rental,
            String id) {
        try {
            if (isAuthActive) {
                // UserResource.checkCookieUser(session, ); TODO
            }

            RentalDAO rDAO = new RentalDAO(rental, id);

            if (!isValidRental(rDAO))
                return Response.status(Status.BAD_REQUEST).build();

            rentalDB.putRental(rDAO);

            if (isCacheActive) {
                cache.setValue(rental.getId(), rental);
            }

            return Response.ok(rDAO).build();
        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response updateRental(boolean isCacheActive, boolean isAuthActive, Cookie session, Rental rental,
            String id) {
        try {
            if (isAuthActive) {
                // UserResource.checkCookieUser(session, ); TODO
            }

            RentalDAO rDAO = new RentalDAO(rental, id);

            CosmosItemResponse<RentalDAO> r = rentalDB.updateRental(rDAO);

            if (isCacheActive) {
                cache.setValue(rental.getId(), rental);
            }

            return Response.ok(mapper.writeValueAsString(r)).build();

        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response getRentalInfo(boolean isCacheActive, String id) {
        try {
            RentalDAO r = null;
            if (isCacheActive) {
                r = cache.getValue(id, RentalDAO.class);
            }

            if (r == null) {
                CosmosPagedIterable<RentalDAO> rental = rentalDB.getRentalById(id);

                if (rental.iterator().hasNext()) {

                    if (isCacheActive) {
                        cache.setValue(id, rental.iterator().next());
                    }

                    return Response.ok(rental.iterator().next()).build();
                }

                return Response.status(Status.NOT_FOUND).build();
            }

            return Response.ok(r).build();

        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response listDiscountedRentals() {
        List<RentalDAO> discountedRentals = new ArrayList<>();
        try {
            CosmosPagedIterable<HouseDAO> houseCosmos = houseDB.getHouses();

            for (HouseDAO h : houseCosmos) {
                CosmosPagedIterable<RentalDAO> rentals = rentalDB.getHouseById(h.getId());
                for (RentalDAO r : rentals) {
                    if (r.getPrice() < h.getBasePrice())
                        discountedRentals.add(r);
                }
            }

            return Response.ok(discountedRentals).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    private boolean isValidRental(RentalDAO rental) {
        String[] args = rental.getRentalPeriod().split("-");
        String first = args[0];
        String last = args[args.length - 1];

        CosmosPagedIterable<RentalDAO> rentalsIt = rentalDB.getRentalById(rental.getHouseId());

        for (RentalDAO r : rentalsIt) {
            for (String month : rental.getRentalPeriod().split("-")) {
                if (r.getRentalPeriod().contains(month)) {
                    return false;
                }
            }
        }

        CosmosPagedIterable<HouseDAO> houseIt = houseDB.getHouseById(rental.getHouseId());

        if (!houseIt.iterator().hasNext())
            return false;

        HouseDAO house = houseIt.iterator().next();

        if (rental.getPrice() > house.getBasePrice())
            return false;

        if (!house.getAvailability().contains(first) || !house.getAvailability().contains(last)) {
            return false;
        }

        return true;
    }

}
