package scc.srv;

import jakarta.ws.rs.Path;
import scc.interfaces.QuestionResourceInterface;

@Path("/house/{id}/question")
public class QuestionResource implements QuestionResourceInterface {

    @Override
    public void createQuestion() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createQuestion'");
    }

    @Override
    public void listQuestions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listQuestions'");
    }

}
