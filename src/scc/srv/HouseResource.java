package scc.srv;

import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    public void deleteHouse(String id) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            houseDb.delHouseById(id);
            jedis.del(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String updateHouse(HouseDAO house, String oldId) {
        try {
            try (Jedis jedis = RedisCache.getCachePool().getResource()) {
                CosmosItemResponse<HouseDAO> h = houseDb.updateHouse(oldId, house);
                jedis.set(house.getId(), mapper.writeValueAsString(house));
                return mapper.writeValueAsString(h);
            }
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }

    @Override
    public void listAvailableHouses(String location) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            String res = jedis.get(location);
            if (res == null) {
                CosmosPagedIterable<HouseDAO> h = houseDb.getHouseById(location);
                if (h.iterator().hasNext()) {
                    for (String hId : h.iterator().next().getHouseLocations()) {
                        // TODO
                    }
                }
            }
            HouseDAO h = mapper.readValue(res, HouseDAO.class);
            for (String hId : h.getHouseLocations()) {
                // TODO
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void searchAvailableHouses(String location, String period) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchAvailableHouses'");
    }

}
