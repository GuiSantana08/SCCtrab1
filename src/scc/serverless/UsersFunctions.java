package scc.serverless;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.*;

import redis.clients.jedis.Jedis;
import scc.cache.RedisCache;

public class UsersFunctions {

    // TODO: should add all users to a list in cache, replicate this to all
    // resources if needed.
    @FunctionName("cosmosDBtest")
    public void updateMostRecentUsers(
            @CosmosDBTrigger(name = "cosmosTest", databaseName = "scc2324", collectionName = "users", preferredLocations = "North Europe", createLeaseCollectionIfNotExists = true, connectionStringSetting = "AzureCosmosDBConnection") String[] users,
            final ExecutionContext context) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            jedis.incr("usersCounter");
            for (String u : users) {
                jedis.lpush("usersList", u);
            }
        }
    }
}
