package scc.srv;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import scc.db.CosmosDBLayer;
import scc.utils.User;
import scc.utils.UserDAO;


import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/users")
public class UserResource {
  CosmosDBLayer db = CosmosDBLayer.getInstance();

    @Path("/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String createUser(User u) {
        UserDAO user = new UserDAO(u);
     var res =  db.putUser(user);

            return "User created";

    }


}
