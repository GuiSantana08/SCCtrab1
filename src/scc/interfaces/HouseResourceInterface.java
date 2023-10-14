package scc.interfaces;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import scc.utils.House;
import scc.utils.HouseDAO;

public interface HouseResourceInterface {

    @POST
    public Response createHouse(House house);

    @DELETE
    public Response deleteHouse(String id);

    @PUT
    public Response updateHouse(HouseDAO house, String oldId);

    @GET
    public Response listAvailableHouses(@QueryParam("location") String location);

    @GET
    public void searchAvailableHouses(@QueryParam("location") String location, @QueryParam("period") String period);
}
