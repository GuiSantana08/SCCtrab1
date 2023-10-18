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

@Path("/house")
public class HouseResource implements HouseResourceInterface {

    ObjectMapper mapper = new ObjectMapper();
    HouseDBLayer houseDb = HouseDBLayer.getInstance();

    UserDBLayer userDb = UserDBLayer.getInstance();



    @Override
    public Response createHouse(House house) {
        try {
                HouseDAO hDAO = new HouseDAO(house);
                CosmosItemResponse<HouseDAO> h = houseDb.putHouse(hDAO);
                //jedis.set(hDAO.getId(), mapper.writeValueAsString(hDAO));
                   CosmosPagedIterable userCosmos = userDb.getUserById(house.getUserId());

                   //get userDao
                   UserDAO userDAO = (UserDAO) userCosmos.iterator().next();
                   //add house id to user houseIds
                   userDAO.getHouseIds().add(house.getId());
                   //update user
                   userDb.updateUser(userDAO);


                //TODO update user with the house id


                // TODO: should update the user to insert houseID into array of houseIDs
                return Response.ok("House created").build();
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
            //jedis.del(id);
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
            var h =houseDb.getHouseById(id);
            return  Response.ok(h.iterator().next().getId()).build();
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

                //jedis.set(house.getId(), mapper.writeValueAsString(house));
                return Response.ok(h.getItem().toString()).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response listAvailableHouses(String location) {
        try {
            return Response.ok().build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public void searchAvailableHouses(String period) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchAvailableHouses'");
    }



}
