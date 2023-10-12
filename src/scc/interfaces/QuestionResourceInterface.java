package scc.interfaces;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;

public interface QuestionResourceInterface {

    @POST
    public void createQuestion();

    @GET
    public void listQuestions();
}
