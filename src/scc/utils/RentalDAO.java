package scc.utils;

public class RentalDAO {
    private String _rid;
    private String _ts;
    private String id;
    private String houseId;
    private String userId;
    private int rentalPeriod;
    private int price;

    public RentalDAO(Rental r) {
        this(r.getId(), r.getHouse(), r.getRentingUser(), r.getRentalPeriod(), r.getPrice());
    }

    public RentalDAO(String id, String houseId, String userId, int rentalPeriod, int price) {
        this.id = id;
        this.houseId = houseId;
        this.userId = userId;
        this.rentalPeriod = rentalPeriod;
        this.price = price;
    }

    public String get_rid() {
        return this._rid;
    }

    public void set_rid(String _rid) {
        this._rid = _rid;
    }

    public String get_ts() {
        return this._ts;
    }

    public void set_ts(String _ts) {
        this._ts = _ts;
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

    public void setPrice(int price) {
        this.price = price;
    }

}
