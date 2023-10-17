package scc.interfaces;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.utils.Question;

public interface QuestionResourceInterface {

    @Path("/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createQuestion(Question question);

    @Path("/listQuestions")
    @GET
    public Response listQuestions();
}
