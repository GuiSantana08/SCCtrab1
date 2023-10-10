package scc.srv.interfaces;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;

public interface UserResourceInterface {

    @POST
    public void createUser();

    @DELETE
    public void deleteUser();

    @PUT
    public void updateUser();

    @GET
    public void listHouses();
}
