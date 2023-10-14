package scc.interfaces;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.Response;
import scc.utils.Question;

public interface QuestionResourceInterface {

    @POST
    public Response createQuestion(Question question);

    @GET
    public Response listQuestions();
}
