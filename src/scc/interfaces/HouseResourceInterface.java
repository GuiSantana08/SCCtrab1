package scc.interfaces;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.QueryParam;

public interface HouseResourceInterface {

    @POST
    public void createHouse();

    @DELETE
    public void deleteHouse();

    @PUT
    public void updateHouse();

    @GET
    public void listAvailableHouses(@QueryParam("location") String location);

    @GET
    public void searchAvailableHouses(@QueryParam("location") String location, @QueryParam("period") String period);
}
