package scc.utils;

public class Accessibility {
    private int price;
    private boolean ocupied;

    public Accessibility(int price, boolean ocupied) {
        this.price = price;
        this.ocupied = ocupied;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isOcupied() {
        return this.ocupied;
    }

    public boolean getOcupied() {
        return this.ocupied;
    }

    public void setOcupied(boolean ocupied) {
        this.ocupied = ocupied;
    }

}
