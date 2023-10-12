package scc.db;

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

import scc.utils.HouseDAO;

public class HouseDBLayer {
    private static final String CONNECTION_URL = "https://cosmos-scc.documents.azure.com:443/";
    private static final String DB_KEY = "3yJ9ApcLNiJpbJJEPHDS04cbSBZygR3mmcUfeCGnJeJNP1opXQ4Nsofyec5WUtIGxN9NlxszDSZ4ACDb38RUqg==";
    private static final String DB_NAME = "scc-58201";

    private static HouseDBLayer instance;

    public static synchronized HouseDBLayer getInstance() {
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
        instance = new HouseDBLayer(client);
        return instance;

    }

    private CosmosClient client;
    private CosmosDatabase db;
    private CosmosContainer house;

    public HouseDBLayer(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if (db != null)
            return;
        db = client.getDatabase(DB_NAME);
        house = db.getContainer("houses");

    }

    public CosmosItemResponse<Object> delHouseById(String id) {
        init();
        PartitionKey key = new PartitionKey(id);
        return house.deleteItem(id, key, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<Object> delHouse(HouseDAO h) {
        init();
        return house.deleteItem(h, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<HouseDAO> putHouse(HouseDAO h) {
        init();
        return house.createItem(h);
    }

    public CosmosItemResponse<HouseDAO> updateHouse(String id, HouseDAO h) {
        init();
        PartitionKey key = new PartitionKey(id);
        return house.replaceItem(h, id, key, new CosmosItemRequestOptions());
    }

    public CosmosPagedIterable<HouseDAO> getHouseById(String id) {
        init();
        return house.queryItems("SELECT * FROM houses WHERE house.id=\"" + id + "\"", new CosmosQueryRequestOptions(),
                HouseDAO.class);
    }

    public CosmosPagedIterable<HouseDAO> getHouses() {
        init();
        return house.queryItems("SELECT * FROM houses ", new CosmosQueryRequestOptions(), HouseDAO.class);
    }

    public void close() {
        client.close();
    }

}
