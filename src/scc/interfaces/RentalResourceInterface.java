package scc.interfaces;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.utils.Rental;

public interface RentalResourceInterface {

    @Path("/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRental(@CookieParam("scc:session") Cookie session, Rental rental);

    @Path("/update")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRental(@CookieParam("scc:session") Cookie session, Rental rental);

    @Path("/getInfo")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getRentalInfo(String id);

    @Path("/listDiscontedRentals")
    @GET
    public void listDiscountedRentals();
}
