package GoGoEat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class Customers implements TimeObserver {

    private static LocalDate currDate = LocalDate.now();
    private static LocalTime currTime;

    private String username;
    protected String password;
    private String CId;
    private String billno;
    private double billAmount;
    protected CustomerState customerState;

    private Reservation reserve;
    private ArrayList<Integer> occupiedTableId = new ArrayList<>();
    private ArrayList<Integer> waitingtableNumList = new ArrayList<>();
    private ArrayList<Dish> pendingOrder = new ArrayList<>();
    private String reservationDayString = "tomorrow";

    private Restaurants restaurantChosed;

    private HashMap<String, Restaurants> billNumberToRestaurant = null;
    private Multimap<Dish, Restaurants> DishToRestaurant = null;
    private Commands command;

    // constructor
    public Customers(String username, String userid) {
        this.username = username;
        this.CId = userid;
        this.customerState = new CustomerVIPstate();
        ManualClock.getInstance().addObserver(this);
        this.billNumberToRestaurant = new HashMap<>();
    }

    public void updateBillNumberToRestaurant(String billString, Restaurants restaurant) {
        this.billNumberToRestaurant.put(billString, restaurant);
    }

    public HashMap<String, Restaurants> getBillNumberToRestaurant(Restaurants restaurant) {
        HashMap<String, Restaurants> result = new HashMap<>();
        for (HashMap.Entry<String, Restaurants> set : billNumberToRestaurant.entrySet()) {
            if (billNumberToRestaurant.containsValue(restaurant)) {
                result.put(set.getKey(), set.getValue());
            }
        }
        return result;
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

    // @Override
    public String getUsername() {
        return username;
    }

    // @Override
    public String getUserId() {
        return CId;
    }

    public void clearPendingOrder() {
        this.pendingOrder.clear();
    }

    // dish
    public void addPendingOrder(Dish dish) {
        this.pendingOrder.add(dish);
    }

    public void removePendingOrder(Dish dish) {
        this.pendingOrder.remove(dish);
    }

    public void updatePendingOrder(ArrayList<Dish> dishOrder) {
        this.pendingOrder = dishOrder;
    }

    public void outputPendingDish(String str) {
        System.out.println(str);
        if (pendingOrder.isEmpty()) {
            System.out.println("There is no pending order.");
        } else {
            for (int i = 0; i < this.pendingOrder.size(); i++) {
                System.out.println("[" + (i + 1) + "] " + pendingOrder.get(i));
            }
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
    public void setReserve(Reservation r) {
        this.reserve = r;
    }

    public ArrayList<Integer> getReservedTableIDs() {
        return reserve.getReservedTableIDs();
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

    public String getReserveReminder() {

        if (reserve.getReserveString() != null) {
            String str = "";

            str += String.format("\nReminder: You have a reservation for %s: ", reservationDayString);
            str += reserve.getReserveString();
            return str;
        }
        return null;
    }

    public String getReserveInfo() {

        if (reserve.getReserveString() != null) {
            String str = "";

            str += String.format("\nThe upcoming reservation of Customer %s for %s is: ", CId, reservationDayString);
            str += reserve.getReserveString();
            return str;
        }
        return null;
    }

    public String getReserveSuccessInfo() {
        if (reserve.getReserveString() != null) {
            String str = "\nReserve Success.";
            str += reserve.getReserveString();
            return str;
        }
        return null;
    }

    public boolean checkisReserved() {
        return (reserve != null);
    }

    public Reservation getReservation() {
        return reserve;
    }

    public TimeSlot getReservationTimeSlot() {
        return this.reserve.getReservedTimeSlot();
    }

    public boolean isReserveTime() {
        boolean isTime = false;

        TablesManagement tm = TablesManagement.getInstance();
        ArrayList<Table> tables = tm.getReservedTablesfromId(reserve.getReservedTableIDs());

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

    public String getBillno() {
        return billno;
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

    public void updateState() {
        // update state according to amount

        if (this.billAmount >= 88) {
            setState(new CustomerSuperVIPstate());
        } else {
            setState(new CustomerVIPstate());
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
            if (DishToRestaurant == null) {
                DishToRestaurant = ArrayListMultimap.create();
                for (Dish d : pendingOrder) {
                    DishToRestaurant.put(d, restaurant);
                }
            } else {
                for (Dish d : pendingOrder) {
                    DishToRestaurant.put(d, restaurant);
                }
            }

        }
    }

    // Use when CustomerState is Calculating total overall paid price
    public void clearOrderNPrice() {
        pendingOrder.clear();
        if (customerOrdersAccordingToRestaurant(getRestaurantChosed()) != null) {
            customerOrdersAccordingToRestaurant(getRestaurantChosed()).clear();
        }
        clearState();
    }

    // Clear State -> Change state to VIP as default
    public void clearState() {
        setState(new CustomerVIPstate());
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
    public void printAllOrders() {
        int i = 1;
        if (DishToRestaurant != null) {
            System.out.println("\nOfficial Orders: ");

            for (Entry<Dish, Restaurants> e : DishToRestaurant.entries()) {
                System.out.printf("[%d] %s\n", i, e.getKey());
                i++;
            }

        } else {
            System.out.println("There is no orders made by this customer.");
        }
    }

    public void printOrderofCurrentRound() {
        outputPendingDish("\nYour orders are: ");
    }

    public ArrayList<Dish> getOrderOfCurrentRound() {
        return pendingOrder;
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
        if (this.checkisReserved()) {
            if (reserve.checkValid(currDate) == 1) {
                reservationDayString = "tomorrow";
            } else if (reserve.checkValid(currDate) == 0) {
                reservationDayString = "today";
            } else {
                reserve.cancel();
                reserve = null;
            }
        }
    }

    public boolean isDishToRestaurant(Multimap<Dish, Restaurants> dToR) {
        return DishToRestaurant.equals(dToR);
    }
}
