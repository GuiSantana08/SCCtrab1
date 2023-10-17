package scc.interfaces;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.utils.User;
import scc.utils.UserDAO;

public interface UserResourceInterface {

    @Path("/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(User user);


    @Path("/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(String json);

    @Path("/update")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(User user);

    @Path("/listHouses")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Response listHouses(@QueryParam("userId") String userId);
}
