package scc.srv;

import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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
import java.util.Calendar;
import java.util.List;

@Path("/house")
public class HouseResource implements HouseResourceInterface {

    ObjectMapper mapper = new ObjectMapper();
    HouseDBLayer houseDb = HouseDBLayer.getInstance();

    UserDBLayer userDb = UserDBLayer.getInstance();

    @Override
    public Response createHouse(House house) {
        try {
            CosmosPagedIterable userCosmos = userDb.getUserById(house.getUserId());
            if (!userCosmos.iterator().hasNext())
                return Response.status(404).entity("User not found").build();
            HouseDAO hDAO = new HouseDAO(house);
            CosmosItemResponse<HouseDAO> h = houseDb.putHouse(hDAO);
            // jedis.set(hDAO.getId(), mapper.writeValueAsString(hDAO));

            // get userDao
            UserDAO userDAO = (UserDAO) userCosmos.iterator().next();
            // add house id to user houseIds
            userDAO.getHouseIds().add(house.getId());
            // update user

            userDb.updateUser(userDAO);

            return Response.ok(house.toString()).build();

        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response deleteHouse(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            String id = jsonNode.get("id").asText();

            houseDb.delHouseById(id);
            // TODO change the id to "Deleted User”
            // jedis.del(id);
            return Response.ok().build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response getHouse(String id) {
        try {
            var h = houseDb.getHouseById(id);
            return Response.ok(h.iterator().next().getId()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response updateHouse(House house) {
        try {

            HouseDAO hDAO = new HouseDAO(house);
            CosmosItemResponse<HouseDAO> h = houseDb.updateHouse(house.getId(), hDAO);

            // jedis.set(house.getId(), mapper.writeValueAsString(house));
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
                while (!startMonth.equals(endMonth)) {
                    if (h.getAvailable().get(startMonth.toString().toLowerCase()).isOcupied())
                        isOcupied = true;

                    startMonth = startMonth.plus(1);
                }
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
