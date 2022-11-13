import java.util.ArrayList;

public class CustomerModule implements UserModule {
    /*
     * RunDown：
     * 1. Choose Dine-in / reserve after login
     * 1.1 Dine-in -> Check if seat available
     * 1.2 Reserve -> Check available time slot
     *
     * 2. Order
     * 2.1 Show restaurant list
     * 2.2 Choose restaurant
     * 2.3 Show menu of the restaurant choosed
     * 2.4 Choose dish from menu (Can select multiple dishes)
     *
     * 2.5 Confirm Order
     * 2.5.1 Cancel -> Add Dish
     * 2.5.2 Cancel -> Delete Dish
     * 2.5.3 Confirm (Add pendingOrder into orders array in Customers)
     *
     * 2.6 Payment (in customers.checkout())
     * 2.6.1 Show order
     * 2.6.2 Calculate total price (According to discount of VIP/SuperVIP)
     * 2.6.3 Choose payment method
     * 2.6.4 Goto payment platform to finish payment
     */

    private CustomerModule() {
    }

    private static final CustomerModule instance = new CustomerModule();

    public static CustomerModule getInstance() {
        return instance;
    }

    private static Customers customer = null;

    static ArrayList<Dish> pendingOrder = new ArrayList<>();
    static ArrayList<Dish> menu = new ArrayList<>();
    static Restaurants restaurant = null;

    public void promptOptionStart() {
        // check if customer is sit-down
        System.out.print("\n--------------------------------------------------");
        if (customer.getOccupiedTableId().isEmpty()) {
            if (customer.checkisReserved()) {
                // reserved -> cannot reserve again
                System.out.println(customer.getReserveInfo());
                promptOptionReserved();

            } else {
                // Get Customer's choice to dine or reserve
                promptOptionNotReserved();
            }
        } else {
            if (customer.checkisReserved()) {
                System.out.println(customer.getReserveInfo());
                promptOptionReservedStillSitting();
            } else {
                // customer dining & haven't get up from seat
                promptOptionNotReservedStillSitting();
            }

        }
    }

    public void promptOptionReserved() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Dine in");
        System.out.println("[3] Cancel Reservation");
        System.out.println("[5] Logout");
    }

    public void promptOptionNotReserved() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Dine in");
        System.out.println("[2] Reserve");
        System.out.println("[5] Logout");
    }

    public void promptOptionNotReservedStillSitting() {
        System.out.println("\nCommands: ");
        System.out.println("[2] Reserve");
        System.out.println("[4] Check Out");
        System.out.println("[5] Logout");
    }

    public void promptOptionReservedStillSitting() {
        System.out.println("\nCommands: ");
        System.out.println("[3] Cancel Reservation");
        System.out.println("[4] Check Out");
        System.out.println("[5] Logout");
    }

    public void promptNoRecommendedResult() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Queue");
        System.out.println("[2] Leave");
    }

    public void promptHasRecommendedResult() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Queue");
        System.out.println("[2] Walk in with recommended arrangement");
        System.out.println("[3] Leave");
    }

    public void promptWalkInLeave() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Walk in");
        System.out.println("[2] Leave");
    }

    public void promptConfirmOrder() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Yes: Please input 'True'/'true'/'TRUE'");
        System.out.println("[2] No: Please input 'False'/'false'/'FALSE'");
    }

    public void promptEditOrder() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Add Order");
        System.out.println("[2] Delete Order");
        System.out.println("[3] Confirm Order");
    }

    @Override
    public void run(String Id) throws ExWrongSelectionNum {
        try {
            customer = Database.getInstance().matchCId(Id);
            String input = "";
            int select = 0;

            // if customerState -> calculate total consumption -> disable below line：
            clearOrderNPrice();

            while (select != 5) {

                promptOptionStart();

                System.out.print("\nPlease select your operation: ");

                input = Main.in.next("\nInput: ");
                select = Integer.parseInt(input);

                switch (select) {
                    case 1:
                        if (dineInOperation()) {
                            // After Dine-in(get ticket and sit down) -> Ordering
                            ordering();

                            // display official-confirmed-ordered dish
                            customer.printOrders();

                            // About Payment

                            Payment pay = new Payment(customer, restaurant);
                            pay.payProcess();
                            // tm.checkOutByCustomer(customer.getOccupiedTableId());
                        }
                        break;
                    case 2:
                        if (customer.checkisReserved()) {
                            System.out.println("\nError! You already has a reservation.");
                            break;
                        } else {
                            if (!reservationOperation())
                                System.out.println("Something is wrong in reservation!");
                            else
                                System.out.println(customer.getReserve().toString());
                        }
                        break;
                    case 3:
                        if (!customer.checkisReserved()) {
                            System.out.println("\nError! You have not yet make a reservation.");
                            break;
                        } else {
                            if (cancelReservation()) {
                                System.out.println("\nCancel Success!");
                            } else {
                                System.out.println("\nError! Cancellation unsuccessful.");
                            }
                        }
                        break;
                    case 4:
                        tm.checkOutByCustomer(customer.getOccupiedTableId());
                        customer.clearOccupiedTableId();
                        System.out.println("\nYou have successfully check out.");
                        select = 0;
                        break;
                    case 5:
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static boolean reservationOperation() throws ExTableNotExist, ExTimeSlotAlreadyBeReserved {

        ArrayList<Integer> chosedTableIds = new ArrayList<>();
        String reserveTime = null, chosedTable = null;
        int chosedTableId = 0;

        do {
            // Show available time slot for booking
            try {
                tm.showReservationTable();

                // Input the time slot you want to reserve, format: 11:23-12:22
                System.out.print("\nPlease input time slot to reserve (Format: xx:xx-xx:xx): ");
                reserveTime = Main.in.next("Input: ");

                // choose table by tableId
                System.out.print("Please input the table ids you want to reserve: (separate by comma): ");
                chosedTable = Main.in.next("Input: ");
                String[] idx = chosedTable.split(",");

                for (String s : idx) {
                    chosedTableId = Integer.parseInt(s);
                    chosedTableIds.add(chosedTableId);
                }
                customer.setReserveChosedTableIds(chosedTableIds);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } while (chosedTableId == 0);

        // set reserve in customer.java
        customer.setReserve(reserveTime, chosedTableIds);

        // indicator for successful booking
        return customer.getReserve() != null;
    }

    public boolean directWalkIn(ArrayList<Integer> result) throws ExWrongSelectionNum {
        int select = 0;
        String str = "";
        boolean success = false;

        do {
            try {
                System.out.println("You can now directly walk in.");

                promptWalkInLeave();

                System.out.print("\nPlease choose your operation: ");
                str = Main.in.next("Input: ");
                select = Integer.parseInt(str);

                if (select == 1) {
                    ArrayList<Integer> checkinTableId = tm.setWalkInStatus(result);

                    // TODO: break into two sub-functions
                    addCheckInAndWaitingInfo(str, checkinTableId, null);
                    success = true;

                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } while (select != 1 && select != 2);

        return success;
    }

    public boolean reserveWalkIn(ArrayList<Integer> tableIds) {

        boolean success = false;

        System.out.println("You can now walk in.");

        for (Integer tableId : tableIds) {
            tm.setTableFromAvailableToOccupiedStatus(tableId);
        }
        success = true;

        return success;
    }

    public void noRecommendedResultAndQueue(ArrayList<Integer> result) throws ExWrongSelectionNum {
        int select = 0;
        String str = "";
        do {
            promptNoRecommendedResult();

            System.out.print("\nPlease choose your operation: ");
            str = Main.in.next("Input: ");
            select = Integer.parseInt(str);

            if (select == 1) {
                tm.setWaitingTables(database.getCustomerCid(customer), result);
            }
        } while (select != 1 && select != 2);
    }

    public boolean hasRecommendedResult(ArrayList<Integer> result) throws ExWrongSelectionNum {
        String str = "";
        int select = 0;
        boolean success = false;

        do {
            promptHasRecommendedResult();

            System.out.print("\nPlease choose your operation: ");
            str = Main.in.next("Input: ");
            select = Integer.parseInt(str);

            if (select == 1) {
                tm.setWaitingTables(database.getCustomerCid(customer), result);

            } else if (select == 2) {
                ArrayList<Integer> checkinTableId = tm.setWalkInStatus(result);

                // TODO: break into two sub-functions
                addCheckInAndWaitingInfo(str, checkinTableId, null);
                success = true;
            }
        } while (select != 1 && select != 2 && select != 3);

        return success;
    }

    public boolean waitQueue(int numOfPeople, ArrayList<Integer> result) {
        ArrayList<Integer> recommendedResult = tm.recommendedArrangementAccordingToWaitingTime(numOfPeople);

        boolean success = false;

        if (recommendedResult == null) {
            noRecommendedResultAndQueue(result);
        } else {
            success = hasRecommendedResult(result);
        }
        return success;
    }

    public boolean dineInOperation() {
        boolean success = false;
        String str;

        // if no reservation or it is not time to dine(from reserve)
        if (customer.getReserve() == null || customer.getReserve() != null && !customer.isReserveTime()) {
            tm.showAvailableTables();

            // Input number of people to dine in
            System.out.print("Please input the number of people: ");
            int numOfPeople;
            str = Main.in.next("Input: ");
            numOfPeople = Integer.parseInt(str);

            ArrayList<Integer> result = new ArrayList<>();
            // result = tm.makeTableArrangements(numOfPeople);
            try {
                result = tm.arrangeTableAccordingToNumOfPeople(numOfPeople);

                if (tm.canDirectlyDineIn(result)) {
                    // 彈message dine-in或者leave
                    success = directWalkIn(result);
                } else {
                    success = waitQueue(numOfPeople, result);
                }
            } catch (ExPeopleNumExceedTotalCapacity e) {
                System.out.println(e.getMessage());
            }

        } else if (customer.getReserve() != null && customer.isReserveTime()) {
            // Sit in
            success = reserveWalkIn(customer.getReserveChosedTableIds());
        }
        return success;

    }

    public static boolean cancelReservation() {
        try {
            customer.cancelReservation();
            customer.clearReservation();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return customer.getReserve() == null;
    }

    public static void addCheckInAndWaitingInfo(String CId, ArrayList<Integer> checkInTable,
            ArrayList<Integer> waitingTableNumList) {
        // 找Customer Instance
        // checkInTable加到customer occupiedtable
        // 把waitingtableNumList 加到customer 相同的arrayList裡面
        // Customers customer = Main.matchCId(CId);

        for (int i : checkInTable) {
            customer.addTableId(i);
        }
        if (waitingTableNumList != null) {
            for (int i : waitingTableNumList) {
                customer.addTableNumList(i);
            }
        }
    }

    public void ordering() {
        /*
         * 1. Choose Restaurant to order
         * 2. Choose Food to order from the menu of the chosen restaurant
         */

        // CHOOSE RESTAURANTS
        database.outputRestaurant();

        String input = "";

        // Match string input with arraylist to find the restaurant in list
        do {
            ArrayList<Restaurants> availableRestaurants = database.getListofRestaurants();
            System.out.print("\nPlease choose the restaurant to order: ");
            input = Main.in.next("Input: ");
            int idx = Integer.parseInt(input);

            restaurant = availableRestaurants.get(idx - 1);
            customer.chooseRestaurant(restaurant);

        } while (restaurant == null);

        System.out.println("\nYou have chosed restaurant " + customer.getRestaurantChosed() + ".");

        // show menu of the chosen restaurant
        menu = restaurant.getMenu();

        outputMenu();

        // customer ordering
        System.out.print(
                "\nPlease choose from the menu of restaurant " + restaurant.toString() + " (separate by a COMMA): ");
        addDishtoPending();

        // Check if confirm order, if confirm order add into customer orders
        confirmOrder();
    }

    public void confirmOrder() {
        boolean confirmOrder = false;
        int addDel = 0;
        String input = "";

        do {
            // Check ordered dish
            outputPendingDish();

            System.out.println("\nDo you want to confirm order?");
            promptConfirmOrder();

            System.out.print("\nYour option: ");
            input = Main.in.next("Input: ");
            confirmOrder = Boolean.parseBoolean(input);

            if (confirmOrder) {
                break;
            } else {
                outputPendingDish();

                promptEditOrder();

                System.out.print("\nDo you want to add or delete order? ");
                input = Main.in.next("Input: ");
                addDel = Integer.parseInt(input);

                if (addDel == 3) {
                    confirmOrder = true;
                    break;
                } else if (addDel == 1) {
                    outputMenu();

                    System.out.print("\nInput the dish number to add: (separate by a COMMA): ");
                    addDishtoPending();

                    outputPendingDish();
                } else if (addDel == 2) {
                    removeDishfromPending();

                    outputPendingDish();
                }
            }
        } while (!confirmOrder);

        // Official order
        customer.updateOrder(pendingOrder, restaurant);
    }

    /*
     * Tool Functions
     */

    public static void addPendingOrder(Dish dish) {
        pendingOrder.add(dish);
    }

    public static void removePendingOrder(Dish dish) {
        pendingOrder.remove(dish);
    }

    public static void outputPendingDish() {
        System.out.println("\nYour pending orders: ");
        for (int i = 0; i < pendingOrder.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + pendingOrder.get(i));
        }
    }

    public static void outputMenu() {
        restaurant.printMenu();
    }

    // Add dish into Pending Order
    public static void addDishtoPending() {
        String strDish;
        String[] tokens;
        int[] idx;

        strDish = Main.in.nextLine("Input: "); // input multiple dishes
        tokens = strDish.split(",");
        idx = new int[20];

        for (int i = 0; i < tokens.length; i++) {
            idx[i] = Integer.parseInt(tokens[i]);
        }
        // Add input to dish list pending to order
        for (int i = 0; i < tokens.length; i++) {
            addPendingOrder(menu.get(idx[i] - 1));
        }
    }

    // Delete dish from Pending Order
    public static void removeDishfromPending() {

        outputPendingDish();

        String strDish;
        String[] tokens;
        int[] idx;

        System.out.print("\nInput the dish to delete: ");
        strDish = Main.in.nextLine("Input: ");
        tokens = strDish.split(",");
        idx = new int[20];

        for (int i = 0; i < tokens.length; i++) {
            idx[i] = Integer.parseInt(tokens[i]);
        }

        for (int i = 0; i < tokens.length; i++) {
            removePendingOrder(menu.get(idx[i] - 1));
        }
    }

    // CustomerState為累積消費總額時用：
    public static void clearOrderNPrice() {
        pendingOrder.clear();
        if (customer.customerOrdersAccordingToRestaurant(restaurant) != null) {
            customer.customerOrdersAccordingToRestaurant(restaurant).clear();
        }
        customer.getMembership().clearState();
    }
}