package scc.db;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import scc.utils.RentalDAO;

public class RentalDBLayer {

    private static final String CONNECTION_URL = "https://cosmos-scc.documents.azure.com:443/";
    private static final String DB_KEY = "3yJ9ApcLNiJpbJJEPHDS04cbSBZygR3mmcUfeCGnJeJNP1opXQ4Nsofyec5WUtIGxN9NlxszDSZ4ACDb38RUqg==";
    private static final String DB_NAME = "scc-58201";

    private static RentalDBLayer instance;

    public static synchronized RentalDBLayer getInstance() {
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
        instance = new RentalDBLayer(client);
        return instance;
    }

    private CosmosClient client;

    private CosmosDatabase db;

    private CosmosContainer rentals;

    public RentalDBLayer(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if (db != null)
            return;
        db = client.getDatabase(DB_NAME);
        rentals = db.getContainer("rentals");
    }

    public CosmosItemResponse<Object> delRentalById(String id) {
        init();
        PartitionKey key = new PartitionKey(id);
        CosmosItemRequestOptions options = new CosmosItemRequestOptions();
        return rentals.deleteItem(id, key, options);
    }

    public CosmosItemResponse<Object> delRental(RentalDAO r) {
        init();
        CosmosItemRequestOptions options = new CosmosItemRequestOptions();
        return rentals.deleteItem(r, options);
    }

    public CosmosItemResponse<RentalDAO> putRental(RentalDAO r) {
        init();
        return rentals.createItem(r);
    }

    public CosmosItemResponse<RentalDAO> updateRental(String id, RentalDAO r) {
        init();
        PartitionKey key = new PartitionKey(id);
        return rentals.replaceItem(r, id, key, new CosmosItemRequestOptions());
    }

    public CosmosPagedIterable<RentalDAO> getRentalById(String id) {
        init();
        PartitionKey key = new PartitionKey(id);
        return rentals.queryItems("SELECT * FROM rentals WHERE rentals.id=\"" + id + "\"", new CosmosQueryRequestOptions(),
                RentalDAO.class);
    }

    public CosmosPagedIterable<RentalDAO> getRentals() {
        init();
        return rentals.queryItems("SELECT * FROM rentals ", new CosmosQueryRequestOptions(), RentalDAO.class);
    }

    public void close() {
        client.close();
    }

}
