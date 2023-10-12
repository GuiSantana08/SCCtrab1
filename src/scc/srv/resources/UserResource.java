package scc.srv.resources;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import scc.srv.cache.RedisCache;
import scc.srv.interfaces.UserResourceInterface;
import scc.utils.UserDAO;

public class UserResource implements UserResourceInterface {

    private static final String CONNECTION_URL = "https://scc23244204.documents.azure.com:443/";
    private static final String DB_KEY = "wuVEJ3C1xiWYt0iLDTBEJGZVnOmEQ9r5YXB89MwbpePC2vOxNFctI1hm5BGh6evq7k4FUpCYq6TmACDbt6X1mg==";
    private static final String DB_NAME = "scc2324db";

    private static UserResource instance;
    ObjectMapper mapper = new ObjectMapper();

    public static synchronized UserResource getInstance() {
        if (instance != null)
            return instance;

        CosmosClient client = new CosmosClientBuilder()
                .endpoint(CONNECTION_URL)
                .key(DB_KEY)
                // .directMode()
                .gatewayMode()
                // replace by .directMode() for better performance
                .consistencyLevel(ConsistencyLevel.SESSION)
                .connectionSharingAcrossClientsEnabled(true)
                .contentResponseOnWriteEnabled(true)
                .buildClient();
        instance = new UserResource(client);
        return instance;

    }

    private CosmosClient client;
    private CosmosDatabase db;
    private CosmosContainer users;

    public UserResource(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if (db != null)
            return;
        db = client.getDatabase(DB_NAME);
        users = db.getContainer("users");

    }

    @Override
    public String createUser(UserDAO user) {
        try {
            try (Jedis jedis = RedisCache.getCachePool().getResource()) {
                init();
                CosmosItemResponse<UserDAO> u = users.createItem(user);
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
            init();
            PartitionKey key = new PartitionKey(id);
            users.deleteItem(id, key, new CosmosItemRequestOptions());
            jedis.del(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String updateUser(UserDAO user, String oldId) {
        try {
            try (Jedis jedis = RedisCache.getCachePool().getResource()) {
                init();
                PartitionKey key = new PartitionKey(oldId);
                CosmosItemResponse<UserDAO> u = users.replaceItem(user, oldId, key, new CosmosItemRequestOptions());
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
                init();
                CosmosPagedIterable<UserDAO> u = users.queryItems("SELECT * FROM users WHERE users.id=\"" + id + "\"",
                        new CosmosQueryRequestOptions(), UserDAO.class);

                if (u.iterator().hasNext()) {
                    // TODO
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
