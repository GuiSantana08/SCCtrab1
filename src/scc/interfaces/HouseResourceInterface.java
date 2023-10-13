package scc.interfaces;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.QueryParam;
import scc.utils.House;
import scc.utils.HouseDAO;

public interface HouseResourceInterface {

    @POST
    public String createHouse(House house);

    @DELETE
    public void deleteHouse(String id);

    @PUT
    public String updateHouse(HouseDAO house, String oldId);

    @GET
    public void listAvailableHouses(@QueryParam("location") String location);

    @GET
    public void searchAvailableHouses(@QueryParam("location") String location, @QueryParam("period") String period);
}
