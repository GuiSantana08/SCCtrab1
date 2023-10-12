package scc.srv;

import com.azure.cosmos.models.CosmosItemResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import scc.cache.RedisCache;
import scc.db.HouseDBLayer;
import scc.db.UserDBLayer;
import scc.interfaces.HouseResourceInterface;
import scc.utils.House;
import scc.utils.HouseDAO;
import scc.utils.UserDAO;

public class HouseResource implements HouseResourceInterface {

    ObjectMapper mapper = new ObjectMapper();
    HouseDBLayer houseDb = HouseDBLayer.getInstance();

    @Override
    public String createHouse(House house) {
        try {
            try (Jedis jedis = RedisCache.getCachePool().getResource()) {
                HouseDAO hDAO = new HouseDAO(house);
                CosmosItemResponse<HouseDAO> h = houseDb.putHouse(hDAO);
                jedis.set(hDAO.getId(), mapper.writeValueAsString(hDAO));
                return mapper.writeValueAsString(h);
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public void deleteHouse() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteHouse'");
    }

    @Override
    public void updateHouse() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateHouse'");
    }

    @Override
    public void listAvailableHouses(String location) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listAvailableHouses'");
    }

    @Override
    public void searchAvailableHouses(String location, String period) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchAvailableHouses'");
    }

}
