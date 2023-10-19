package scc.srv;

import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import redis.clients.jedis.Jedis;
import scc.cache.RedisCache;
import scc.db.RentalDBLayer;
import scc.interfaces.RentalResourceInterface;
import scc.utils.HouseDAO;
import scc.utils.Rental;
import scc.utils.RentalDAO;

@Path("/house/{id}/rental") // TODO: instead of inserting house on json, use id to get it
public class RentalResource implements RentalResourceInterface {

    ObjectMapper mapper = new ObjectMapper();
    RentalDBLayer rentalDB = RentalDBLayer.getInstance();

    @Override
    public Response createRental(Rental rental) {
        try {
            RentalDAO rDAO = new RentalDAO(rental);
            CosmosItemResponse<RentalDAO> h = rentalDB.putRental(rDAO);
            return Response.ok().build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response updateRental(Rental rental) {
        try {
            RentalDAO rDAO = new RentalDAO(rental);
            CosmosItemResponse<RentalDAO> r = rentalDB.updateRental(rDAO);
            // jedis.set(rental.getId(), mapper.writeValueAsString(rental));
            return Response.ok().build();

        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response getRentalInfo(String id) {
        try {
            try (Jedis jedis = RedisCache.getCachePool().getResource()) {
                String res = jedis.get(id);
                if (res == null) {
                    CosmosPagedIterable<RentalDAO> rental = rentalDB.getRentalById(id);
                    if (rental.iterator().hasNext()) {
                        return Response.ok(rental.iterator().next()).build();
                    }
                }

                RentalDAO r = mapper.readValue(res, RentalDAO.class);
                return Response.ok(r).build();
            }

        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public void listDiscountedRentals() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listDiscountedRentals'");
    }

}
