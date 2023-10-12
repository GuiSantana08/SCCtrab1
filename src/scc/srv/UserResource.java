package scc.srv;

import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import scc.cache.RedisCache;
import scc.db.UserDBLayer;
import scc.interfaces.UserResourceInterface;
import scc.utils.User;
import scc.utils.UserDAO;

public class UserResource implements UserResourceInterface {

  ObjectMapper mapper = new ObjectMapper();
  UserDBLayer userDb = UserDBLayer.getInstance();

  @Override
  public String createUser(User us) {
    try {
      try (Jedis jedis = RedisCache.getCachePool().getResource()) {
        UserDAO user = new UserDAO(us);
        CosmosItemResponse<UserDAO> u = userDb.putUser(user);
        jedis.set(user.getId(), mapper.writeValueAsString(user));
        return mapper.writeValueAsString(u);
      }
    } catch (Exception e) {
      return e.getMessage();
    }
  }

  @Override
  public void deleteUser(String id) {
    try (Jedis jedis = RedisCache.getCachePool().getResource()) {
      userDb.delUserById(id);
      jedis.del(id);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public String updateUser(UserDAO user, String oldId) {
    try {
      try (Jedis jedis = RedisCache.getCachePool().getResource()) {
        CosmosItemResponse<UserDAO> u = userDb.updateUser(oldId, user);
        jedis.set(user.getId(), mapper.writeValueAsString(user));
        return mapper.writeValueAsString(u);
      }
    } catch (JsonProcessingException e) {
      return e.getMessage();
    }
  }

  @Override
  public void listHouses(String id) {
    try (Jedis jedis = RedisCache.getCachePool().getResource()) {
      String res = jedis.get(id);
      if (res == null) {
        CosmosPagedIterable<UserDAO> u = userDb.getUserById(id);

        if (u.iterator().hasNext()) {
          for (String hId : u.iterator().next().getHouseIds()) {
            // TODO
          }
        }
      }

      UserDAO u = mapper.readValue(res, UserDAO.class);

      for (String hId : u.getHouseIds()) {
        // TODO
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}