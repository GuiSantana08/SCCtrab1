package scc.interfaces;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.utils.User;
import scc.utils.UserDAO;

public interface UserResourceInterface {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(User user);

    @DELETE
    public Response deleteUser(@QueryParam("userId") String userId);

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(UserDAO user, @QueryParam("userId") String userId);

    @GET
    public Response listHouses(@QueryParam("userId") String userId);
}
