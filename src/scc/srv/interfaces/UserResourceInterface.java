package scc.srv.interfaces;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import scc.utils.UserDAO;

public interface UserResourceInterface {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String createUser(UserDAO user);

    @DELETE
    public void deleteUser(@QueryParam("userId") String userId);

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateUser(UserDAO user, @QueryParam("userId") String userId);

    @GET
    public void listHouses(@QueryParam("userId") String userId);
}
