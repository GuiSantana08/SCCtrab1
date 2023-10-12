package scc.interfaces;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;

public interface RentalResourceInterface {

    @POST
    public void createRental();

    @PUT
    public void updateRental();

    @GET
    public void getRentalInfo();

    @GET
    public void listDiscountedRentals();
}
