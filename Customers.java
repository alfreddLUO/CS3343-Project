import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Customers implements UserType, TimeObserver {

    private static LocalDate currDate;
    private static LocalTime currTime;
    private static final TablesManagement tm = TablesManagement.getInstance();

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
    // private ArrayList<Dish> orders = new ArrayList<>();
    private HashMap<Dish, Restaurants> DishToRestaurant = new HashMap<>();

    // constructor
    public Customers(String username, String userid) {
        this.username = username;
        this.CId = userid;
        this.membership = new CustomerMembership(this);
        ManualClock.getInstance().addObserver(this);
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
        try {
            this.reserve = new Reservation(CId, timeslotString, desiredTableIds);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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

        if (reserve.getReserveString() != null) {
            String str = "";
            str += "\nReminder: You have a reserved for tomorrow: ";
            str += reserve.getReserveString();
            return str;
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

    public void cancelReservation() throws ExTableNotExist, ExTimeSlotNotReservedYet {
        this.reserve.cancel();
    }

    public boolean isReserveTime() {
        boolean isTime = false;

        ArrayList<Table> tables = tm.getReservedTablesfromId(reserveChosedTableIds);

        for (Table t : tables) {
            if (t.getTodayReservationTimeSlot().checkReservedStatus(currTime) == 0) {
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
        this.billno = genBillNo.getNextId();
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

    public void updateOrder(ArrayList<Dish> pendingOrder, Restaurants restaurant) {
        for (Dish d : pendingOrder) {
            DishToRestaurant.put(d, restaurant);
        }
    }

    // get ArrayList of customer's official-confirmed orders
    // get dish with respect to restaurant
    public ArrayList<Dish> customerOrdersAccordingToRestaurant(Restaurants restaurant) {
        ArrayList<Dish> resultOrders = new ArrayList<>();

        DishToRestaurant.forEach((key, value) -> {
            if (value.equals(restaurant)) {
                resultOrders.add(key);
            }
        });
        return resultOrders;
    }

    // print official customer's orders
    public void printOrders() {
        System.out.println("\nOfficial Orders: ");
        int i = 1;
        if (DishToRestaurant != null) {
            DishToRestaurant.forEach((key, value) -> System.out.printf("[%d] %s\n", i, key));
        } else {
            System.out.println("There is no orders made by this customer.");
        }

    }

    @Override
    public void timeUpdate(LocalTime newTime) {
        currTime = newTime;
    }

    @Override
    public void dateUpdate(LocalDate newDate) {
        currDate = newDate;
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
