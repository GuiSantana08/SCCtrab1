package scc.interfaces;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.utils.Rental;
import scc.utils.RentalDAO;

public interface RentalResourceInterface {


    @Path("/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRental(Rental rental);

    @Path("/update")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRental(Rental rental);

    @Path("/getInfo")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getRentalInfo(String id);

    @Path("/listDiscontedRentals")
    @GET
    public void listDiscountedRentals();
}
