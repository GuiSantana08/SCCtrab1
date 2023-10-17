package scc.interfaces;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.utils.House;
import scc.utils.HouseDAO;

public interface HouseResourceInterface {
    @Path("/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createHouse(House house);


    @Path("/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteHouse(String json);

    @Path("/update")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateHouse(House house);

    @Path("/getHouseByLocation")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Response listAvailableHouses(@QueryParam("location") String location);

    @Path("/searchAvailableHouses")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public void searchAvailableHouses(@QueryParam("period") String period);
}
