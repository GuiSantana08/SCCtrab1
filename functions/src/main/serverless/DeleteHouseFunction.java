package src.main.serverless;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.*;
import scc.azure.db.QuestionDBLayer;
import scc.azure.db.RentalDBLayer;
import scc.data.QuestionDAO;
import scc.data.RentalDAO;

import java.util.Optional;
import java.util.logging.Logger;

public class DeleteHouseFunction {
	private static final Logger logger = Logger.getLogger(DeleteHouseFunction.class.getName());

	@FunctionName("DeleteHouseFunction")
	public void run(
			@HttpTrigger(name = "delete", methods = {
					HttpMethod.DELETE }, route = "house/delete") HttpRequestMessage<Optional<String>> idHTTP,
			final ExecutionContext context) {
		Optional<String> id = idHTTP.getBody();
		if (id.isPresent()) {
			String houseId = id.get();

			// Delete associated rentals
			deleteRentals(houseId);

			// Delete associated questions
			deleteQuestions(houseId);

			// Delete the house itself
			// Add your logic to delete the house

			logger.info("House deleted successfully.");
		} else {
			logger.warning("Invalid request. House ID not provided.");
		}
	}

	private void deleteRentals(String houseId) {
		// delete rentals associated with the house
		RentalDBLayer rentalDB = RentalDBLayer.getInstance();
		CosmosPagedIterable<RentalDAO> rentals = rentalDB.getRentalsByHouseId(houseId);
		for (RentalDAO rental : rentals) {
			rentalDB.delRentalById(rental.getId());
		}
	}

	private void deleteQuestions(String houseId) {
		// copy from deleteRentals but for questions
		QuestionDBLayer questionDB = QuestionDBLayer.getInstance();
		CosmosPagedIterable<QuestionDAO> questions = questionDB.getQuestionsByHouseId(houseId);
		for (QuestionDAO question : questions) {
			questionDB.delQuestionById(question.getId());
		}

	}
}
