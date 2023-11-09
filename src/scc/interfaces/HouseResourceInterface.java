package scc.interfaces;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.utils.House;

public interface HouseResourceInterface {
    @Path("/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createHouse(@CookieParam("scc:session") Cookie session, House house);

    @Path("/delete")
    @DELETE
    public Response deleteHouse(@CookieParam("scc:session") Cookie session, @QueryParam("id") String id);

    @Path("/get")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHouse(@QueryParam("id") String id);

    @Path("/update")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateHouse(@CookieParam("scc:session") Cookie session, House house);

    @Path("/getHouseByLocation")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Response listAvailableHouses(@QueryParam("location") String location);

    @Path("/searchAvailableHouses")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Response searchAvailableHouses(@QueryParam("period") String period, @QueryParam("location") String location);
}
