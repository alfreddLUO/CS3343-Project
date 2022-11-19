package GoGoEat;

import java.util.ArrayList;
import java.util.Collections;

public class CommandCustomerDineIn implements Commands {

    private static final TablesManagement tm = TablesManagement.getInstance();
    private static final Database database = Database.getInstance();

    private static Customers customer;
    private Restaurants restaurant = null;
    private ArrayList<Dish> menu = new ArrayList<>();

    public CommandCustomerDineIn(Customers commandingCustomer) {
        customer = commandingCustomer;
    }

    @Override
    public void exe()
            throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist, ExTimeSlotNotReservedYet,
            ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {

        // If Dine in operation completed (get ticket and sit down) -> Goto Ordering
        if (dineInOperation()) {

            // After ordering, will goto ConfirmOrder() directly
            ordering();

            // Display official-confirmed-ordered dish
            customer.printOrderofCurrentRound();

            // About Payment
            Payment pay = new Payment(customer, restaurant);
            pay.payProcess(customer.getOrderOfCurrentRound());
        }
    }

    public boolean dineInOperation() {
        boolean success = false;
        String str;

        // if no reservation found OR has reservation but not time to dine (from
        // reserve) yet
        if (!customer.checkisReserved() || (customer.checkisReserved() && !customer.isReserveTime())) {

            // Show all available table with capacity
            tm.showAvailableTables();

            // Input number of people to dine in
            System.out.print("Please input the number of people: ");
            int numOfPeople;
            try {
                ArrayList<Integer> result = new ArrayList<>();
                str = Main.in.next("Please input the number of people: ");
                numOfPeople = Integer.parseInt(str);

                // Get result of suggested table of capacity to sit in
                result = tm.arrangeTableAccordingToNumOfPeople(numOfPeople);

                if (tm.canDirectlyDineIn(result)) {
                    // Directly Sit in and eat
                    success = directWalkIn(result);
                } else {
                    // Online Queue
                    success = waitQueue(numOfPeople, result);
                }
            } catch (NumberFormatException e) {
                System.out.println("Error! Wrong input for selection! Please input an integer!");
            } catch (ExPeopleNumExceedTotalCapacity e) {
                System.out.println(e.getMessage());
            }

            // Has reservation and now is reservation time
        } else if (customer.checkisReserved() && customer.isReserveTime()) {

            TimeSlot ts = customer.getReservationTimeSlot();
            success = reserveWalkIn(customer.getReservedTableIDs(), ts, customer.getID());
            customer.reservationCheckIn();
        }
        return success;

    }

    public boolean directWalkIn(ArrayList<Integer> result) {
        // Prompt message : 1. dine-in 2. leave
        int select = 0;
        String str = "";
        boolean success = false;

        do {
            try {
                System.out.println("You can now directly walk in.");

                // Prompt 1. Walkin / 2. Leave
                CustomerModulePrompt.promptWalkInLeave();

                System.out.print("\nPlease choose your operation: ");
                try {
                    str = Main.in.next("\nPlease choose your operation: ");
                    select = Integer.parseInt(str);
                } catch (NumberFormatException e) {
                    System.out.println("Error! Wrong input for selection! Please input an integer!");
                }

                if (select == 1) {
                    // Walk in
                    ArrayList<Integer> checkinTableId = tm.setWalkInStatus(result);

                    // TODO: Separate into two method
                    addCheckInInfo(checkinTableId);
                    success = true;
                } else if (select == 2) {
                    // Leave
                    break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } while (select != 1 && select != 2);

        return success;
    }

    public boolean waitQueue(int numOfPeople, ArrayList<Integer> result) {

        /*
         * If no suitable table arrangement -> Algorithm to compute table assignment
         * If No recommended arrangement -> Queue / Leave
         * If HAS recommended arrangement -> Queue / Walk-in / Leave
         */

        // Compute Recommended Result
        ArrayList<Integer> recommendedResult = tm.recommendedArrangementAccordingToWaitingTime(numOfPeople);

        boolean success = false;

        if (recommendedResult == null) {
            // Queue / Leave
            noRecommendedResultAndQueue(result);
        } else {
            // Walk in / Queue / Leave
            success = hasRecommendedResult(result);
        }
        return success;
    }

    public void noRecommendedResultAndQueue(ArrayList<Integer> result) {
        // No Recommended Arrangement of Table -> 1. Queue / 2. Leave
        int select = 0;
        String str = "";

        do {
            // 1. Queue / 2. Leave
            CustomerModulePrompt.promptNoRecommendedResult();

            System.out.print("\nPlease choose your operation: ");
            try {
                str = Main.in.next("\nPlease choose your operation: ");
                select = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                System.out.println("Error! Wrong input for selection! Please input an integer!");
            }

            if (select == 1) {
                tm.setWaitingTables(database.getCustomerCid(customer), result);
            } else if (select == 2) {
                break;
            }
        } while (select != 1 && select != 2);
    }

    public boolean hasRecommendedResult(ArrayList<Integer> result) {

        // Has recommended Arrangement of Table -> 1. Queue / 2. Walkin / 3. Leave

        String str = "";
        int select = 0;
        boolean success = false;

        do {
            // 1. Queue 2. Walkin 3. Leave
            CustomerModulePrompt.promptHasRecommendedResult();

            System.out.print("\nPlease choose your operation: ");
            try {
                str = Main.in.next("\nPlease choose your operation: ");
                select = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                System.out.println("Error! Wrong input for selection! Please input an integer!");
            }

            if (select == 1) {
                tm.setWaitingTables(database.getCustomerCid(customer), result);
            } else if (select == 2) {

                // Set table from available to occupied
                ArrayList<Integer> checkinTableId = tm.setWalkInStatus(result);

                // TODO: Separate into two method
                addCheckInInfo(checkinTableId);

                success = true;
            } else if (select == 3) {
                break;
            }
        } while (select != 1 && select != 2 && select != 3);

        return success;
    }

    public boolean reserveWalkIn(ArrayList<Integer> tableIds, TimeSlot ts, String cId) {

        // Customer reserved -> Check-in table

        boolean success = false;

        System.out.println("You can now walk in, our supreme reserved customer.");

        for (Integer tableId : tableIds) {
            try {
                tm.reserverCheckIn(tableId, ts, cId);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        success = true;

        return success;
    }

    // TODO: CID传进来了没用到
    public static void addCheckInInfo(ArrayList<Integer> checkInTable) {

        /*
         * 1. Find Customer Instance
         * 2. Add Check-in table into customer's occupiedtable
         * 3. Add waiting table into customer's waitTableNumList
         */

        // TODO: 为什么comment掉了??
        // Customers customer = Main.matchCId(CId);

        for (int i : checkInTable) {
            customer.addTableId(i);
        }

    }

    public static void addWaitingInfo(ArrayList<Integer> waitingTableNumList) {
        if (waitingTableNumList != null) {
            for (int i : waitingTableNumList) {
                customer.addTableNumList(i);
            }
        }

    }

    public void ordering() {
        /*
         * CHOOSE RESTAURANTS
         * 1. Show all available restaurants
         * 2. Choose 1 restaurant to order
         * 3. Show menu of the restaurant
         * 4. Choose multiple dish at once from menu
         * 5. Decide if confirm order
         * - Not confirm order -> Add or Delete Dish
         * - Confirm order
         */

        // Clear previous Pending order
        customer.clearPendingOrder();

        // Show all restaurants
        database.outputRestaurant();

        String input = "";

        // Match string input with arraylist to find the restaurant in list
        do {
            ArrayList<Restaurants> availableRestaurants = database.getListofRestaurants();
            System.out.print("\nPlease choose the restaurant to order: ");
            try {
                input = Main.in.next("\nPlease choose the restaurant to order: ");
                int idx = Integer.parseInt(input);
                restaurant = availableRestaurants.get(idx - 1);
                customer.chooseRestaurant(restaurant);
            } catch (NumberFormatException e) {
                System.out.println("Error! Wrong input for selection! Please input an integer!");
            }

        } while (restaurant == null);

        System.out.println("\nYou have chosed restaurant " + customer.getRestaurantChosed() + ".");

        // Show menu of the chosen restaurant
        menu = restaurant.getMenu();

        // Print menu of the restaurant
        restaurant.printMenu();

        // customer ordering -> add to pendingOrder
        addDishtoPending(
                "\nPlease choose from the menu of restaurant " + restaurant.toString() + " (separate by a COMMA): ");

        // Check if confirm order, if confirm order add into customer orders
        confirmOrder();
    }

    public void confirmOrder() {
        /*
         * 1. Confirm Order -> Break
         * 2. Not confirm Order
         * - Add dish
         * - Remove dish
         */
        boolean confirmOrder = false;
        int addDel = 0;
        String input = "";

        do {
            // Check ordered dish
            customer.outputPendingDish("\nYour pending orders: ");

            System.out.println("\nDo you want to confirm order?");
            CustomerModulePrompt.promptConfirmOrder();

            System.out.print("\nYour option: ");
            input = Main.in.next("\nYour option: ");
            confirmOrder = Boolean.parseBoolean(input);

            if (confirmOrder) {
                break;
            } else {
                // Output current pending dish
                customer.outputPendingDish("\nYour pending orders: ");

                // prompt 1. Add dish 2. Delete dish
                CustomerModulePrompt.promptEditOrder();

                System.out.print("\nPlease choose your operation: ");
                try {
                    input = Main.in.next("\nPlease choose your operation:  ");
                    addDel = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Error! Wrong input for selection! Please input an integer!");
                }

                if (addDel == 3) {

                    // Continue to payment
                    confirmOrder = true;
                    break;

                } else if (addDel == 1) {

                    // Print menu of restaurant choose
                    restaurant.printMenu();

                    // Add new dish to pending order
                    addDishtoPending("\nInput the dish number to add: (separate by a COMMA): ");

                } else if (addDel == 2) {

                    // Remove dish from pending
                    removeDishfromPending();
                }
            }
        } while (!confirmOrder);

        // Official order
        customer.updateOrder(restaurant);

        // Generate New Bill number for payment
        customer.setBillNo();

        // Pass new billNumber to reference Map: Billno <-> Restaurant
        String billno = customer.getBillno();
        customer.updateBillNumberToRestaurant(billno, restaurant);
    }

    public void addDishtoPending(String perviousString) {
        // Can Input multiple dishes

        String strDish;
        String[] tokens;
        int[] idx;

        System.out.print(perviousString);
        strDish = Main.in.nextLine(perviousString);
        tokens = strDish.split(",");
        idx = new int[20];

        for (int i = 0; i < tokens.length; i++) {
            idx[i] = Integer.parseInt(tokens[i]);
        }

        // Add input to pending order
        for (int i = 0; i < tokens.length; i++) {
            customer.addPendingOrder(menu.get(idx[i] - 1));
        }
    }

    public void removeDishfromPending() {

        // Output original pending order
        customer.outputPendingDish("\nYour pending orders: ");

        String strDish;
        String[] tokens;
        ArrayList<Integer> idx = new ArrayList<>();

        System.out.print("\nInput the dish to delete: ");
        try {
            strDish = Main.in.nextLine("\nInput the dish to delete: ");
            tokens = strDish.split(",");

            for (int i = 0; i < tokens.length; i++) {
                idx.add(Integer.parseInt(tokens[i]));
            }

            // UPDATE: Modified on 16 Nov
            ArrayList<Dish> dishModified = new ArrayList<>(customer.getOrderOfCurrentRound());
            Collections.sort(idx, Collections.reverseOrder());
            for (int i : idx) {
                dishModified.remove(i - 1);
            }

            // Update the pending order to dishModified
            customer.updatePendingOrder(dishModified);

        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }

}
