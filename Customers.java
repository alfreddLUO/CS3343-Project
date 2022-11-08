import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Customers implements UserType, TimeObserver {

    private LocalTime now;
    private static LocalDate currDate;
    private static LocalTime currTime;
    private static TablesManagement tm = TablesManagement.getInstance();

    private String username;
    protected String password;
    private String CId;
    private String billno;
    private double billAmount;

    protected CustomerState customerState;
    private CustomerMembership membership; // State

    private Reservation reserve;
    private ArrayList<Integer> occupiedTableId = new ArrayList<>();
    private ArrayList<Integer> waitingtableNumList = new ArrayList<>();
    private ArrayList<Integer> reserveChosedTableIds = new ArrayList<>();
    // private Table occupiedtable;

    private Restaurants restaurantChosed;
    private ArrayList<Dish> orders = new ArrayList<>();

    // constructor
    public Customers(String username, String userid) {
        this.username = username;
        this.CId = userid;
        this.membership = new CustomerMembership(this);
        setBillNo();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getUserId() {
        return CId;
    }

    public void clearReservation() {
        this.reserve = null;
    }

    public ArrayList<Integer> getOccupiedTableId() {
        return occupiedTableId;
    }

    public void clearOccupiedTableId() {
        this.occupiedTableId.clear();
    }

    // Reserve
    public void setReserve(String timeslotString, ArrayList<Integer> desiredTableIds) {

        this.reserve = new Reservation(CId, timeslotString, desiredTableIds);
    }

    public void setReserveChosedTableIds(ArrayList<Integer> chosedTableIds) {
        this.reserveChosedTableIds = chosedTableIds;
    }

    public ArrayList<Integer> getReserveChosedTableIds() {
        return reserveChosedTableIds;
    }

    // Dine-in
    public void addTableId(int tId) {
        this.occupiedTableId.add(tId);
    }

    public void addTableNumList(int tId) {
        this.waitingtableNumList.add(tId);
    }

    public int getithTableNumList(int i) {
        return waitingtableNumList.get(i);
    }

    public void minusOneTableNumList(int capacityIndex) {
        int i = waitingtableNumList.get(capacityIndex);
        waitingtableNumList.set(capacityIndex, i - 1);

    }

    public void addOccupiedTable(int tableId) {
        occupiedTableId.add(tableId);
    }

    public String getReserveInfo() {
        if (reserve != null) {
            return reserve.toString();
        } else {
            return null;
        }
    }

    public boolean checkisReserved() {
        return (reserve != null);
    }

    public Reservation getReserve() {
        return reserve;
    }

    public void cancelReservation() {
        this.reserve.cancel();
    }

    public boolean isReserveTime() {
        boolean isTime = false;

        // TODO: check time now == reserve time
        ArrayList<Table> tables = tm.getReservedTablesfromId(reserveChosedTableIds);

        for (Table t : tables) {
            if (t.getTodayReservationTimeSlot().checkReservedStatus(now) == 0) {
                isTime = true;
            }
        }
        return isTime;
    }

    public String getID() {
        return this.CId;
    }

    public CustomerMembership getMembership() {
        return membership;
    }

    public void setBillNo() {
        GenerateCustomerBillNo genBillNo = GenerateCustomerBillNo.getInstance();
        this.billno = genBillNo.getNextBillNo();
    }

    public String printBillNo() {
        return this.billno;
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }

    // for Payment module
    public double getBillAmount() {
        return billAmount;
    }

    // customer choose restaurant to order
    public void chooseRestaurant(Restaurants restaurants) {
        this.restaurantChosed = restaurants;
    }

    // get chosed restaurants
    public Restaurants getRestaurantChosed() {
        return this.restaurantChosed;
    }

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

    // get ArrayList of customer's official-confirmed orders
    public ArrayList<Dish> customerOrders() {
        return this.orders;
    }

    // print official customer's orders
    public void printOrders() {
        if (orders.size() != 0) {
            System.out.println("\nOfficial Orders: ");
            for (int i = 0; i < orders.size(); i++) {
                System.out.println("[" + (i + 1) + "] " + orders.get(i).toString());
            }
        } else {
            System.out.println("There is no orders made by this customer.");
        }

    }

    @Override
    public void timeUpdate(LocalTime newTime) {
        currTime = newTime;
        tm.updateStatusAccordingToTime();
    }

    @Override
    public void dateUpdate(LocalDate newDate) {
        currDate = newDate;
        tm.updateStatusAccordingToDate();

    }

    // About table reservation
    /*
     * public void reserveTable() {
     * this.reservedtable.setReserved();
     * }
     * 
     * public void deleteReserveTable() {
     * this.reservedtable.cancelReserved();
     * }
     */
}
