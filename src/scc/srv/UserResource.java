package scc.srv;

import java.util.ArrayList;
import java.util.List;

import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import redis.clients.jedis.Jedis;
import scc.cache.RedisCache;
import scc.db.UserDBLayer;
import scc.interfaces.UserResourceInterface;
import scc.utils.User;
import scc.utils.UserDAO;
import scc.utils.HouseDAO;

@Path("/user")
public class UserResource implements UserResourceInterface {

    ObjectMapper mapper = new ObjectMapper();
    UserDBLayer userDb = UserDBLayer.getInstance();

    @Override
    public Response createUser(User us) {
        try {
            UserDAO user = new UserDAO(us);
            CosmosItemResponse<UserDAO> u = userDb.putUser(user);
            //jedis.set(user.getId(), mapper.writeValueAsString(user));
            return Response.ok(us.toString()).build();

        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response deleteUser(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            String id = jsonNode.get("id").asText();
            userDb.delUserById(id);
            //TODO: change the owner id to "

            return Response.ok().build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response updateUser(User user) {
        try {
                UserDAO uDAO = new UserDAO(user);
                CosmosItemResponse<UserDAO> u = userDb.updateUser(uDAO);
                //jedis.set(user.getId(), mapper.writeValueAsString(user));
                return Response.ok().build();

        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response listHouses(String id) {
        List<HouseDAO> userHouses = new ArrayList<>();
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            String res = jedis.get(id);
            if (res == null) {
                CosmosPagedIterable<UserDAO> u = userDb.getUserById(id);

                if (u.iterator().hasNext()) {
                    for (String hId : u.iterator().next().getHouseIds()) {
                        HouseResource hR = new HouseResource();
                        userHouses.add(hR.getHouse(hId).readEntity(HouseDAO.class));
                    }
                    return Response.ok(userHouses).build();
                }
            }

            UserDAO u = mapper.readValue(res, UserDAO.class);

            for (String hId : u.getHouseIds()) {
                HouseResource hR = new HouseResource();
                userHouses.add(hR.getHouse(hId).readEntity(HouseDAO.class));
            }
            return Response.ok(userHouses).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
}