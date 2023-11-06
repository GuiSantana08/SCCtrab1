package scc.srv;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CancellationException;

import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import redis.clients.jedis.Jedis;
import scc.cache.RedisCache;
import scc.db.HouseDBLayer;
import scc.db.UserDBLayer;
import scc.interfaces.UserResourceInterface;
import scc.utils.Constants;
import scc.utils.User;
import scc.utils.UserDAO;
import scc.utils.HouseDAO;
import scc.utils.Login;
import scc.utils.Session;

@Path("/user")
public class UserResource implements UserResourceInterface {

    ObjectMapper mapper = new ObjectMapper();
    UserDBLayer userDb = UserDBLayer.getInstance();
    HouseDBLayer houseDb = HouseDBLayer.getInstance();

    @Override
    public Response createUser(Cookie session, User us) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            checkCookieUser(session, us.getId());

            UserDAO user = new UserDAO(us);
            CosmosItemResponse<UserDAO> u = userDb.putUser(user);
            jedis.set(user.getId(), mapper.writeValueAsString(user));
            return Response.ok(u.getItem().toString()).build();
        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getLocalizedMessage()).build();
        }
    }

    @Override
    public Response deleteUser(Cookie session, String userId) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            checkCookieUser(session, userId);

            for (HouseDAO h : houseDb.getHouseByUserId(userId)) {
                CosmosPagedIterable<HouseDAO> houseCosmos = houseDb.getHouseById(h.getId());
                if (houseCosmos.iterator().hasNext()) {
                    HouseDAO upHouse = houseCosmos.iterator().next();
                    upHouse.setUserId(Constants.deletedUser.getDbName());
                    houseDb.updateHouse(upHouse.getId(), upHouse);
                    jedis.set(upHouse.getId(), mapper.writeValueAsString(upHouse));
                }
            }

            jedis.del(userId);
            userDb.delUserById(userId);
            return Response.ok(userId).build();
        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response updateUser(Cookie session, User user) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            checkCookieUser(session, user.getId());

            UserDAO uDAO = new UserDAO(user);
            CosmosItemResponse<UserDAO> u = userDb.updateUser(uDAO);
            jedis.set(user.getId(), mapper.writeValueAsString(user));
            return Response.ok(u.getItem().toString()).build();
        } catch (NotAuthorizedException c) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(c.getLocalizedMessage()).build();
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
            CosmosPagedIterable<HouseDAO> houses = houseDb.getHouseByUserId(id);

            for (HouseDAO h : houses) {
                userHouses.add(h);
            }

            return Response.ok(userHouses.toString()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

    // TODO
    public Response auth(Login user) {
        boolean pwdOk = false;

        // Check pwd

        if (pwdOk) {
            String uid = UUID.randomUUID().toString();
            NewCookie cookie = new NewCookie.Builder("scc:session")
                    .value(uid)
                    .path("/")
                    .comment("sessionid")
                    .maxAge(3600)
                    .secure(false)
                    .httpOnly(true)
                    .build();

            RedisCache.putSession(new Session(uid, user.getUsername()));
            return Response.ok().cookie(cookie).build();
        } else
            throw new NotAuthorizedException("Incorrect login");
    }

    /**
     * Throws exception if not appropriate user for operation on Hopuse
     */
    public static Session checkCookieUser(Cookie session, String id)
            throws NotAuthorizedException {
        if (session == null || session.getValue() == null)
            throw new NotAuthorizedException("No session initialized");
        Session s;
        try {
            s = RedisCache.getSession(session.getValue());
        } catch (CancellationException e) {
            throw new NotAuthorizedException("No valid session initialized");
        }
        if (s == null || s.getUsername() == null || s.getUsername().length() == 0)
            throw new NotAuthorizedException("No valid session initialized");
        if (!s.getUsername().equals(id) && !s.getUsername().equals("admin"))
            throw new NotAuthorizedException("Invalid user : " + s.getUsername());
        return s;
    }
}