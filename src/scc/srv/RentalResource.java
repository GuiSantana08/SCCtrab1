package scc.srv;

import jakarta.ws.rs.Path;
import scc.interfaces.RentalResourceInterface;

@Path("/house/{id}/rental")
public class RentalResource implements RentalResourceInterface {

    @Override
    public void createRental() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createRental'");
    }

    @Override
    public void updateRental() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateRental'");
    }

    @Override
    public void getRentalInfo() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRentalInfo'");
    }

    @Override
    public void listDiscountedRentals() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listDiscountedRentals'");
    }

}
