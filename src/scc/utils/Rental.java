package scc.utils;

public class Rental {
    private String id;
    private String houseId;
    private String userId;
    private int rentalPeriod;
    private int price;

    public Rental(String id, String houseId, String userId, int rentalPeriod, int price) {
        this.id = id;
        this.houseId = houseId;
        this.userId = userId;
        this.rentalPeriod = rentalPeriod;
        this.price = price;
    }

    public Rental() {
        super();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHouse() {
        return this.houseId;
    }

    public void setHouse(String houseId) {
        this.houseId = houseId;
    }

    public String getRentingUser() {
        return this.userId;
    }

    public void setRentingUser(String userId) {
        this.userId = userId;
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
}
