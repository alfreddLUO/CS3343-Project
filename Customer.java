
import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String username;
    private int userid;
    private CustomerState customerState;

    private PaymentMethod paymentMethod;
    private Table reservedtable;
    private ArrayList<Dish> orders = new ArrayList<>();

    // constructor
    public Customer(String username, int userid) {
        this.username = username;
        this.userid = userid;
        this.customerState = null;
    }

    // FUNCTION
    // customer order dish
    public ArrayList<Dish> orderdish(Dish dish) {
        this.orders.add(dish);
        return this.orders;
    }

    // customer delete dish
    public ArrayList<Dish> deletedish(Dish dish) {
        this.orders.remove(dish);
        return this.orders;
    }

    // customer all orders
    public ArrayList<Dish> customerOrders() {
        return this.orders;
    }

    // customer pay the bill
    public void paythebill(int price) {

        System.out.println("You have completed the payment.");
    }

    public void reserveTable() {
        this.reservedtable.setReserved();
    }

    public void deleteReserveTable() {
        this.reservedtable.cancelReserved();
    }

    public void setState(CustomerState state) {
        this.customerState = state;
    }

    public void occupyTable(Table t) {
    }

    public void addWaitingTable(Integer tableCapacity, int i) {
    }
}
