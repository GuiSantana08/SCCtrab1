package scc.srv;

import com.azure.cosmos.models.CosmosItemResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.Path;
import redis.clients.jedis.Jedis;
import scc.cache.RedisCache;
import scc.db.RentalDBLayer;
import scc.interfaces.RentalResourceInterface;
import scc.utils.House;
import scc.utils.Rental;
import scc.utils.RentalDAO;
import scc.utils.User;

@Path("/house/{id}/rental")
public class RentalResource implements RentalResourceInterface {

    ObjectMapper mapper = new ObjectMapper();

    RentalDBLayer rentalDBLayer = RentalDBLayer.getInstance();


    @Override
    public String createRental(Rental rental) {
        try{
            try(Jedis jedis = RedisCache.getCachePool().getResource()){
                RentalDAO rDAO = new RentalDAO(rental);
                CosmosItemResponse<RentalDAO> r = rentalDBLayer.putRental(rDAO);
                jedis.set(rDAO.getId(), mapper.writeValueAsString(rDAO));
                return mapper.writeValueAsString(r);
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String updateRental(RentalDAO rental, String oldId) {
    try{
        try(Jedis jedis = RedisCache.getCachePool().getResource()){
            CosmosItemResponse<RentalDAO> r = rentalDBLayer.updateRental(oldId, rental);
            jedis.set(rental.getId(), mapper.writeValueAsString(rental));
            return mapper.writeValueAsString(r);
        }
    } catch (JsonProcessingException e) {
        return e.getMessage();
    }
    }

    @Override
    public void getRentalInfo(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRentalInfo'");
    }

    @Override
    public void listDiscountedRentals() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listDiscountedRentals'");
    }

}
