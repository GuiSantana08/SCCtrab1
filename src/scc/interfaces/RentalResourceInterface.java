package scc.interfaces;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.core.Response;
import scc.utils.Rental;
import scc.utils.RentalDAO;

public interface RentalResourceInterface {

    @POST
    public Response createRental(Rental rental);

    @PUT
    public Response updateRental(RentalDAO rental, String oldId);

    @GET
    public Response getRentalInfo(String id);

    @GET
    public void listDiscountedRentals();
}
