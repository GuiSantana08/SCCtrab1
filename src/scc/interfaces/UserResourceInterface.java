package scc.interfaces;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.utils.Login;
import scc.utils.User;

public interface UserResourceInterface {

    @Path("/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(User user);

    @Path("/delete")
    @DELETE
    public Response deleteUser(@QueryParam("userId") String userId);

    @Path("/update")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(User user);

    @Path("/listHouses")
    @GET
    public Response listHouses(@QueryParam("userId") String userId);

    @POST
    @Path("/auth")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response auth(Login user);
}
