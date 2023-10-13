package scc.utils;

public class RentalDAO {
    String id;
    House house;
    User rentingUser;
    int rentalPeriod;
    int price;

    public RentalDAO(Rental r) {
        this(r.getId(), r.getHouse(), r.getRentingUser(), r.getRentalPeriod(), r.getPrice());
    }

    public RentalDAO(String id, House house, User rentingUser, int rentalPeriod, int price) {
        this.id = id;
        this.house = house;
        this.rentingUser = rentingUser;
        this.rentalPeriod = rentalPeriod;
        this.price = price;
    }

    public String getId() {return this.id;}

    public void setId(String id) {this.id = id;}

    public House getHouse() {
        return this.house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public User getRentingUser() {
        return this.rentingUser;
    }

    public void setRentingUser(User rentingUser) {
        this.rentingUser = rentingUser;
    }

    public int getRentalPeriod() {
        return this.rentalPeriod;
    }

    public void setRentalPeriod(int rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}
