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
import scc.db.HouseDBLayer;
import scc.db.UserDBLayer;
import scc.interfaces.UserResourceInterface;
import scc.utils.Constants;
import scc.utils.User;
import scc.utils.UserDAO;
import scc.utils.HouseDAO;

@Path("/user")
public class UserResource implements UserResourceInterface {

    ObjectMapper mapper = new ObjectMapper();
    UserDBLayer userDb = UserDBLayer.getInstance();
    HouseDBLayer houseDb = HouseDBLayer.getInstance();


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
            CosmosPagedIterable<UserDAO> userIt = userDb.getUserById(id) ;
            if(!userIt.iterator().hasNext())
                return Response.status(404).entity("User not found").build();
            if (userIt.iterator().hasNext()) {
                for (String hId : userIt.iterator().next().getHouseIds()) {
                    CosmosPagedIterable<HouseDAO> houseCosmos = houseDb.getHouseById(hId);
                    if (houseCosmos.iterator().hasNext()) {
                        var upHouse = houseCosmos.iterator().next();
                        upHouse.setUserId(Constants.deletedUser.getString());
                        houseDb.updateHouse(upHouse.getId(),upHouse);

                    }
                }
            }
            userDb.delUserById(id);
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
        try{
            CosmosPagedIterable<UserDAO> userCosmos = userDb.getUserById(id);
            if (userCosmos.iterator().hasNext()) {
                for (String hId : userCosmos.iterator().next().getHouseIds()) {
                    CosmosPagedIterable<HouseDAO> houseCosmos = houseDb.getHouseById(hId);
                    if (houseCosmos.iterator().hasNext()) {
                        userHouses.add(houseCosmos.iterator().next());
                    }
                }
            }
            return Response.ok(userHouses.toString()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }


    }
}