package GoGoEat;

public class Dish {
    private String dishname;
    private double dishprice;

    public Dish(String dishname, double dishprice) {
        this.dishname = dishname;
        this.dishprice = dishprice;
    }

    @Override
    public String toString() {
        return this.dishname;
    }

    public double getdishPrice() {
        return this.dishprice;
    }

    public String getdishname() {
        return this.dishname;
    }

    public void setDishName(String dishname) {
        this.dishname = dishname;
    }

    public void setDishPrice(double newPrice) {
        this.dishprice = newPrice;
    }
}
