package scc.interfaces;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import scc.utils.House;
import scc.utils.Rental;
import scc.utils.RentalDAO;
import scc.utils.User;

public interface RentalResourceInterface {

    @POST
    public String createRental(Rental rental);

    @PUT
    public String updateRental(RentalDAO rental, String oldId);

    @GET
    public void getRentalInfo(String id);

    @GET
    public void listDiscountedRentals();
}
