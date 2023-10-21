package scc.srv;

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
import scc.interfaces.HouseResourceInterface;
import scc.utils.House;
import scc.utils.HouseDAO;
import scc.utils.UserDAO;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Path("/house")
public class HouseResource implements HouseResourceInterface {

    ObjectMapper mapper = new ObjectMapper();
    HouseDBLayer houseDb = HouseDBLayer.getInstance();

    UserDBLayer userDb = UserDBLayer.getInstance();

    @Override
    public Response createHouse(House house) {
        UserDAO userDAO;
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            String res = jedis.get(house.getUserId());

            if (res == null) {
                CosmosPagedIterable<UserDAO> userCosmos = userDb.getUserById(house.getUserId());
                if (!userCosmos.iterator().hasNext())
                    return Response.status(404).entity("User not found").build();

                userDAO = (UserDAO) userCosmos.iterator().next();
            } else {
                userDAO = mapper.readValue(res, UserDAO.class);
            }

            HouseDAO hDAO = new HouseDAO(house);
            CosmosItemResponse<HouseDAO> h = houseDb.putHouse(hDAO);
            jedis.set(hDAO.getId(), mapper.writeValueAsString(hDAO));

            userDAO.getHouseIds().add(house.getId());

            userDb.updateUser(userDAO);
            jedis.set(userDAO.getId(), mapper.writeValueAsString(userDAO));

            return Response.ok(h.getItem().toString()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response deleteHouse(String id) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            houseDb.delHouseById(id);
            jedis.del(id);
            return Response.ok().build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response getHouse(String id) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            String res = jedis.get(id);
            if (res == null) {
                var h = houseDb.getHouseById(id);
                jedis.set(id, mapper.writeValueAsString(h));
                return Response.ok(h.iterator().next()).build();
            }

            HouseDAO h = mapper.readValue(res, HouseDAO.class);

            return Response.ok(h).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response updateHouse(House house) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {

            HouseDAO hDAO = new HouseDAO(house);
            CosmosItemResponse<HouseDAO> h = houseDb.updateHouse(house.getId(), hDAO);

            jedis.set(house.getId(), mapper.writeValueAsString(house));
            return Response.ok(h.getItem().toString()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response listAvailableHouses(String location) {
        List<HouseDAO> housesList = new ArrayList<>();
        try {
            CosmosPagedIterable<HouseDAO> houseCosmos = houseDb.getHouseByLocation(location);
            String currentMonth = LocalDate.now().getMonth().toString().toLowerCase();

            for (HouseDAO h : houseCosmos) {
                if (!h.getAvailable().get(currentMonth).isOcupied())
                    housesList.add(h);
            }

            return Response.ok(housesList.toString()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response searchAvailableHouses(String period, String location) {
        List<HouseDAO> housesList = new ArrayList<>();
        try {
            CosmosPagedIterable<HouseDAO> houseCosmos = houseDb.getHouseByLocation(location);
            String[] months = period.split(":");
            Month startMonth = Month.valueOf(months[0].toUpperCase());
            Month endMonth = Month.valueOf(months[1].toUpperCase());

            for (HouseDAO h : houseCosmos) {
                boolean isOcupied = false;
                do {
                    if (h.getAvailable().get(startMonth.toString().toLowerCase()).isOcupied())
                        isOcupied = true;

                    startMonth = startMonth.plus(1);
                } while (!startMonth.equals(endMonth));
                if (!isOcupied)
                    housesList.add(h);
            }

            return Response.ok(housesList.toString()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

}
