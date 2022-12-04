package GoGoEat;

import java.util.ArrayList;
import java.util.Collections;

public class CommandCustomerDineIn extends CommandCustomer {

    protected CommandCustomerDineIn(Customers commandingCustomer) {
        super(commandingCustomer);
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
            Payment pay = new Payment(customer);
            pay.payProcess(customer.getOrderOfCurrentRound());
        }
    }

    protected boolean dineInOperation() {
        boolean success = false;
        String str;

        // if no reservation found OR has reservation but not time to dine (from
        // reserve) yet
        if (!customer.checkisReserved()
                || (customer.checkisReserved() && !customer.isReserveTime())) {

            // Show all available table with capacity
            TablesManagement tm = TablesManagement.getInstance();
            tm.showAvailableTables();

            // Input number of people to dine in
            System.out.print("Please input the number of people: ");
            int numOfPeople;
            try {
                ArrayList<Integer> result = new ArrayList<>();
                str = Main.in.next("Please input the number of people: ");
                numOfPeople = Integer.parseInt(str);

                // Get result of suggested table of capacity to sit in
                tm.toDefaultAlgo();
                result = tm.getTableArrangement(numOfPeople);

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

    protected boolean directWalkIn(ArrayList<Integer> result) {
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
                    System.out.println("Error! Wrong input for selection! Please input an integer!\n");
                }

                if (select == 1) {
                    // Walk in
                    TablesManagement tm = TablesManagement.getInstance();
                    ArrayList<Integer> checkinTableId = tm.setWalkInStatus(result);

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

    private boolean waitQueue(int numOfPeople, ArrayList<Integer> result) {

        /*
         * If no suitable table arrangement -> Algorithm to compute table assignment
         * If No recommended arrangement -> Queue / Leave
         * If HAS recommended arrangement -> Queue / Walk-in / Leave
         */

        // Compute Recommended Result
        TablesManagement tm = TablesManagement.getInstance();
        tm.toRecommendAlgo();
        ArrayList<Integer> recommendedResult;
        try {
            recommendedResult = tm.getTableArrangement(numOfPeople);
            boolean success = false;

            if (recommendedResult == null) {
                // Queue / Leave
                noRecommendedResultAndQueue(result);
            } else {
                // Walk in / Queue / Leave
                success = hasRecommendedResult(result);
            }
            return success;
        } catch (ExPeopleNumExceedTotalCapacity e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private void noRecommendedResultAndQueue(ArrayList<Integer> result) {
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
                TablesManagement tm = TablesManagement.getInstance();
                tm.setWaitingTables(super.database.getCustomerCid(customer), result);
            } else if (select == 2) {
                break;
            }
        } while (select != 1 && select != 2);
    }

    private boolean hasRecommendedResult(ArrayList<Integer> result) {

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

            TablesManagement tm = TablesManagement.getInstance();
            if (select == 1) {
                tm.setWaitingTables(super.database.getCustomerCid(customer), result);
            } else if (select == 2) {

                // Set table from available to occupied
                ArrayList<Integer> checkinTableId = tm.setWalkInStatus(result);

                addCheckInInfo(checkinTableId);

                success = true;
            } else if (select == 3) {
                break;
            }
        } while (select != 1 && select != 2 && select != 3);

        return success;
    }

    private boolean reserveWalkIn(ArrayList<Integer> tableIds, TimeSlot ts, String cId) {

        // Customer reserved -> Check-in table

        boolean success = false;

        System.out.println("You can now walk in, our supreme reserved customer.");

        for (Integer tableId : tableIds) {
            try {
                TablesManagement tm = TablesManagement.getInstance();
                tm.reserverCheckIn(tableId, ts, cId);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        success = true;

        return success;
    }

    public static void addCheckInInfo(ArrayList<Integer> checkInTable) {
        /*
         * Add Check-in table into customer's occupiedtable
         */
        for (int i : checkInTable) {
            customer.addTableId(i);
        }
    }

    public static void addWaitingInfo(ArrayList<Integer> waitingTableNumList) {
        /*
         * Add waiting table into customer's waitTableNumList
         */
        if (waitingTableNumList != null) {
            for (int i : waitingTableNumList) {
                customer.addTableNumList(i);
            }
        }
    }

    private void ordering() {
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
        super.database.outputRestaurant();

        String input = "";

        // Match string input with arraylist to find the restaurant in list
        while (customer.getRestaurantChosed() == null) {
            ArrayList<Restaurants> availableRestaurants = super.database.getListofRestaurants();
            System.out.print("\nPlease choose the restaurant to order: ");
            try {
                input = Main.in.next("\nPlease choose the restaurant to order: ");
                int idx = Integer.parseInt(input);
                customer.chooseRestaurant(availableRestaurants.get(idx - 1));
            } catch (NumberFormatException e) {
                System.out.println("Error! Wrong input for selection! Please input an integer!");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Please input the valid restaurant number!!!");
            }

        }

        System.out.println("\nYou have chosed restaurant " + customer.getRestaurantChosed() + ".");

        // Show menu of the chosen restaurant

        // Print menu of the restaurant
        customer.getRestaurantChosed().printMenu();

        // customer ordering -> add to pendingOrder
        try {
            addDishtoPending(
                    "\nPlease choose from the menu of restaurant " + customer.getRestaurantChosed().toString()
                            + " (separate by a COMMA): ");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input!!");
        }

        // Check if confirm order, if confirm order add into customer orders
        confirmOrder();
    }

    private void confirmOrder() {
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
                    customer.getRestaurantChosed().printMenu();

                    // Add new dish to pending order
                    addDishtoPending("\nInput the dish number to add: (separate by a COMMA): ");

                } else if (addDel == 2) {

                    // Remove dish from pending
                    removeDishfromPending();
                }
            }
        } while (!confirmOrder);

        // Official order
        customer.updateOrder(customer.getRestaurantChosed());

        // Generate New Bill number for payment
        customer.setBillNo();

        // Pass new billNumber to reference Map: Billno <-> Restaurant
        String billno = customer.getBillno();
        customer.updateBillNumberToRestaurant(billno, customer.getRestaurantChosed());
    }

    private void addDishtoPending(String perviousString) {
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
            customer.addPendingOrder(customer.getRestaurantChosed().getMenu().get(idx[i] - 1));
        }
    }

    private void removeDishfromPending() {

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
