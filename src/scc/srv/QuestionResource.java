package scc.srv;

import java.util.ArrayList;
import java.util.List;

import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import redis.clients.jedis.Jedis;
import scc.cache.RedisCache;
import scc.db.QuestionDBLayer;
import scc.interfaces.QuestionResourceInterface;
import scc.utils.Question;
import scc.utils.QuestionDAO;

@Path("/house/{id}/question") // TODO: instead of inserting house on json, use id to get it
public class QuestionResource implements QuestionResourceInterface {

    ObjectMapper mapper = new ObjectMapper();
    QuestionDBLayer questionDb = QuestionDBLayer.getInstance();

    @Override
    // TODO: recebe user sem casa associada, vai buscar pelo id e guarda no DAO
    public Response createQuestion(String id, Question question) {
        try (Jedis jedis = RedisCache.getCachePool().getResource()) {
            QuestionDAO qDAo = new QuestionDAO(question);
            CosmosItemResponse<QuestionDAO> q = questionDb.putQuestion(qDAo);
            jedis.set(question.getId(), mapper.writeValueAsString(question));

            return Response.ok(mapper.writeValueAsString(q)).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response listQuestions(String id) {
        List<QuestionDAO> questions = new ArrayList<>();
        try {

            CosmosPagedIterable<QuestionDAO> u = questionDb.getQuestions();

            while (u.iterator().hasNext()) {
                questions.add(u.iterator().next());
            }

            return Response.ok(questions).build();
        } catch (CosmosException c) {
            return Response.status(c.getStatusCode()).entity(c.getLocalizedMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getLocalizedMessage()).build();
        }
    }

}
