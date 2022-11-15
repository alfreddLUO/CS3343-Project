import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class Customers implements UserType, TimeObserver {

    private static LocalDate currDate;

    private static LocalTime currTime;
    private static final TablesManagement tm = TablesManagement.getInstance();

    private String username;
    protected String password;
    private String CId;
    private String billno;
    private double billAmount;
    private double discount;
    protected CustomerState customerState;
    protected String reservationDayString = "tomorrow";

    private Reservation reserve;
    private ArrayList<Integer> occupiedTableId = new ArrayList<>();
    private ArrayList<Integer> waitingtableNumList = new ArrayList<>();
    private ArrayList<Integer> reserveChosedTableIds = new ArrayList<>();
    private ArrayList<Dish> pendingOrder = new ArrayList<>();
    // private Table occupiedtable;

    private Restaurants restaurantChosed;
    // private ArrayList<Dish> orders = new ArrayList<>();
    private Multimap<Dish, Restaurants> DishToRestaurant = null;
    private Commands command;

    // constructor
    public Customers(String username, String userid) {
        this.username = username;
        this.CId = userid;
        this.customerState = new CustomerVIPstate();
        ManualClock.getInstance().addObserver(this);
        setBillNo();
    }

    public void setCommand(Commands command) {
        this.command = command;
    }

    public void callCommand() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
            ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {
        command.exe();
    }

    public void setState(CustomerState state) {
        this.customerState = state;
    }

    public CustomerState getState() {
        return this.customerState;
    }

    public void setdiscount(double discount) {
        this.discount = discount;
    }

    public void newDayReservationDayString() {
        this.reservationDayString = "today";
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getUserId() {
        return CId;
    }

    // dish
    public void addPendingOrder(Dish dish) {
        this.pendingOrder.add(dish);
    }

    public void removePendingOrder(Dish dish) {
        this.pendingOrder.remove(dish);
    }

    public void outputPendingDish() {
        System.out.println("\nYour pending orders: ");
        for (int i = 0; i < this.pendingOrder.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + pendingOrder.get(i));
        }
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

            // TODO: Reserved day: today or tmr (Ideal: Change DayString when NewDay())
            // TODO: newDayReservationDayString()
            str += String.format("\nReminder: You have a reservation for %s: ", reservationDayString);
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

    // update state according to amount
    public void updateState() {
        if (this.billAmount >= 88) {
            setState(new CustomerSuperVIPstate());
        }
    }

    public void updateStateOutput() {
        System.out.println(this.customerState.toString());
    }

    // customer choose restaurant to order
    public void chooseRestaurant(Restaurants restaurants) {
        this.restaurantChosed = restaurants;
    }

    // get chosed restaurants
    public Restaurants getRestaurantChosed() {
        return this.restaurantChosed;
    }

    public void updateOrder(Restaurants restaurant) {
        if (pendingOrder != null) {
            DishToRestaurant = ArrayListMultimap.create();
            for (Dish d : pendingOrder) {
                DishToRestaurant.put(d, restaurant);
            }
        }

    }

    // get ArrayList of customer's official-confirmed orders
    // get dish with respect to restaurant
    public ArrayList<Dish> customerOrdersAccordingToRestaurant(Restaurants restaurant) {
        if (DishToRestaurant != null) {
            ArrayList<Dish> resultOrders = new ArrayList<>();

            DishToRestaurant.forEach((key, value) -> {
                if (value.equals(restaurant)) {
                    resultOrders.add(key);
                }
            });
            return resultOrders;
        }
        return null;
    }

    // print official customer's orders
    public void printOrders() {
        int i = 1;
        if (DishToRestaurant != null) {
            System.out.println("\nOfficial Orders: ");

            // for (Entry<Dish, Collection<Restaurants>> e :
            // DishToRestaurant.asMap().entrySet()) {
            // System.out.printf("[%d] %s\n", i, e.getKey());
            // i++;
            // }
            for (Entry<Dish, Restaurants> e : DishToRestaurant.entries()) {
                System.out.printf("[%d] %s\n", i, e.getKey());
                i++;
            }

        } else {
            System.out.println("There is no orders made by this customer.");
        }
    }

    public void reservationCheckIn() {
        reserve = null;
    }

    @Override
    public void timeUpdate(LocalTime newTime) {
        currTime = newTime;
    }

    @Override
    public void dateUpdate(LocalDate newDate) {
        currDate = newDate;
    }

}
