package scc.interfaces;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.utils.Question;

public interface QuestionResourceInterface {

    @Path("/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createQuestion(@HeaderParam("isCacheActive") boolean isCacheActive,
            @HeaderParam("isAuthActive") boolean isAuthActive, @CookieParam("scc:session") Cookie session,
            @PathParam("id") String id,
            Question question);

    @Path("/listQuestions")
    @GET
    public Response listQuestions(@PathParam("id") String id);
}
