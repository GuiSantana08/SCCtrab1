package scc.srv;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;
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
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            UserDAO user = new UserDAO(us);
            CosmosItemResponse<UserDAO> u = userDb.putUser(user);
            jedis.set(user.getId(), mapper.writeValueAsString(user));
            return Response.ok(u.getItem().toString()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getLocalizedMessage()).build();
        }
    }

    @Override
    public Response deleteUser(String userId) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            String userRes = jedis.get(userId);

            if (userRes == null) {
                CosmosPagedIterable<UserDAO> user = userDb.getUserById(userId);
                Iterator<UserDAO> userIt = user.iterator();

                if (!userIt.hasNext())
                    return Response.status(404).entity("User not found").build();
                jedis.set(userIt.next().getId(), mapper.writeValueAsString(userIt.next()));

                for (String hId : userIt.next().getHouseIds()) {
                    CosmosPagedIterable<HouseDAO> houseCosmos = houseDb.getHouseById(hId);
                    if (houseCosmos.iterator().hasNext()) {
                        var upHouse = houseCosmos.iterator().next();
                        upHouse.setUserId(Constants.deletedUser.getDbName());
                        houseDb.updateHouse(upHouse.getId(), upHouse);
                    }
                }

                jedis.del(userId);
                userDb.delUserById(userId);
                return Response.ok(userId).build();
            }

            UserDAO userIt = mapper.readValue(userRes, UserDAO.class);
            for (String hId : userIt.getHouseIds()) {
                CosmosPagedIterable<HouseDAO> houseCosmos = houseDb.getHouseById(hId);
                if (houseCosmos.iterator().hasNext()) {
                    var upHouse = houseCosmos.iterator().next();
                    upHouse.setUserId(Constants.deletedUser.getDbName());
                    houseDb.updateHouse(upHouse.getId(), upHouse);
                }
            }

            jedis.del(userId);
            userDb.delUserById(userId);
            return Response.ok("CACHED " + userId).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response updateUser(User user) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            UserDAO uDAO = new UserDAO(user);
            CosmosItemResponse<UserDAO> u = userDb.updateUser(uDAO);
            jedis.set(user.getId(), mapper.writeValueAsString(user));
            return Response.ok(u.getItem().toString()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response listHouses(String id) {
        List<HouseDAO> userHouses = new ArrayList<>();

        // TODO maybe should verify if user exists
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            String res = jedis.get(id);

            if (res == null) {
                CosmosPagedIterable<UserDAO> user = userDb.getUserById(id);
                Iterator<UserDAO> userIt = user.iterator();

                if (!userIt.hasNext())
                    return Response.status(404).entity("User not found").build();
                jedis.set(userIt.next().getId(), mapper.writeValueAsString(userIt.next()));

                if (userIt.hasNext()) {
                    for (String hId : userIt.next().getHouseIds()) {
                        CosmosPagedIterable<HouseDAO> houseCosmos = houseDb.getHouseById(hId);
                        if (houseCosmos.iterator().hasNext()) {
                            userHouses.add(houseCosmos.iterator().next());
                        }
                    }
                }
                return Response.ok(userHouses.toString()).build();
            }

            UserDAO u = mapper.readValue(res, UserDAO.class);
            for (String hId : u.getHouseIds()) {
                CosmosPagedIterable<HouseDAO> houseCosmos = houseDb.getHouseById(hId);
                if (houseCosmos.iterator().hasNext()) {
                    userHouses.add(houseCosmos.iterator().next());
                }
            }

            return Response.ok("CACHED " + userHouses.toString()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

    }
}