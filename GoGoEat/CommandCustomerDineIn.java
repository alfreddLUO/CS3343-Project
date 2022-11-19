package GoGoEat;

import java.util.ArrayList;
import java.util.Collections;

public class CommandCustomerDineIn implements Commands {
    private static final TablesManagement tm = TablesManagement.getInstance();
    private static final Database database = Database.getInstance();
    private static Customers customer;
    private Restaurants restaurant = null;
    private ArrayList<Dish> menu = new ArrayList<>();
    private CustomerModulePromptions promptions = new CustomerModulePromptions(customer);

    public CommandCustomerDineIn(Customers commandingCustomer) {
        customer = commandingCustomer;
    }

    @Override
    public void exe()
            throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist, ExTimeSlotNotReservedYet,
            ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {
        if (dineInOperation()) {
            // After Dine-in(get ticket and sit down) -> Ordering
            ordering();

            // display official-confirmed-ordered dish
            customer.printOrderofCurrentRound();

            // About Payment

            Payment pay = new Payment(customer, restaurant);
            pay.payProcess(customer.getOrderOfCurrentRound());
            // tm.checkOutByCustomer(customer.getOccupiedTableId());
        }

    }

    public boolean dineInOperation() {
        boolean success = false;
        String str;

        // if no reservation or it is not time to dine(from reserve)
        if (!customer.checkisReserved() || (customer.checkisReserved() && !customer.isReserveTime())) {
            tm.showAvailableTables();

            // Input number of people to dine in
            System.out.print("Please input the number of people: ");
            int numOfPeople;
            try {
                ArrayList<Integer> result = new ArrayList<>();
                str = Main.in.next("Please input the number of people: ");
                numOfPeople = Integer.parseInt(str);
                result = tm.arrangeTableAccordingToNumOfPeople(numOfPeople);

                if (tm.canDirectlyDineIn(result)) {
                    // 彈message dine-in或者leave
                    success = directWalkIn(result);
                } else {
                    success = waitQueue(numOfPeople, result);
                }
            } catch (NumberFormatException e) {
                System.out.println("Error! Wrong input for selection! Please input an integer!");
            } catch (ExPeopleNumExceedTotalCapacity e) {
                System.out.println(e.getMessage());
            }

        } else if (customer.checkisReserved() && customer.isReserveTime()) {
            // Sit in
            TimeSlot ts = customer.getReservationTimeSlot();

            success = reserveWalkIn(customer.getReservedTableIDs(), ts, customer.getID());
            customer.reservationCheckIn();

        }
        return success;

    }

    public boolean directWalkIn(ArrayList<Integer> result) {
        int select = 0;
        String str = "";
        boolean success = false;

        do {
            try {
                System.out.println("You can now directly walk in.");

                promptions.promptWalkInLeave();

                System.out.print("\nPlease choose your operation: ");
                try {
                    str = Main.in.next("\nPlease choose your operation: ");
                    select = Integer.parseInt(str);
                } catch (NumberFormatException e) {
                    System.out.println("Error! Wrong input for selection! Please input an integer!");
                }

                if (select == 1) {
                    ArrayList<Integer> checkinTableId = tm.setWalkInStatus(result);

                    // TODO: 拆成两个
                    addCheckInAndWaitingInfo(str, checkinTableId, null);
                    success = true;

                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
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

    public boolean reserveWalkIn(ArrayList<Integer> tableIds, TimeSlot ts, String cId) {

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

    public void noRecommendedResultAndQueue(ArrayList<Integer> result) {
        int select = 0;
        String str = "";
        do {
            promptions.promptNoRecommendedResult();

            System.out.print("\nPlease choose your operation: ");
            try {
                str = Main.in.next("\nPlease choose your operation: ");
                select = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                System.out.println("Error! Wrong input for selection! Please input an integer!");
            }

            if (select == 1) {
                tm.setWaitingTables(database.getCustomerCid(customer), result);
            }
        } while (select != 1 && select != 2);
    }

    public boolean hasRecommendedResult(ArrayList<Integer> result) {
        String str = "";
        int select = 0;
        boolean success = false;

        do {
            promptions.promptHasRecommendedResult();

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
                ArrayList<Integer> checkinTableId = tm.setWalkInStatus(result);

                // TODO: break into two sub-functions
                addCheckInAndWaitingInfo(str, checkinTableId, null);
                success = true;
            }
        } while (select != 1 && select != 2 && select != 3);

        return success;
    }

    public void ordering() {
        /*
         * 1. Choose Restaurant to order
         * 2. Choose Food to order from the menu of the chosen restaurant
         */

        // Clear previous Pending order
        customer.clearPendingOrder();

        // CHOOSE RESTAURANTS

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

        // show menu of the chosen restaurant
        menu = restaurant.getMenu();

        restaurant.printMenu();

        // customer ordering
        addDishtoPending(
                "\nPlease choose from the menu of restaurant " + restaurant.toString() + " (separate by a COMMA): ");

        // Check if confirm order, if confirm order add into customer orders
        confirmOrder();
    }

    public void addDishtoPending(String perviousString) {
        String strDish;
        String[] tokens;
        int[] idx;

        System.out.print(perviousString);
        strDish = Main.in.nextLine(perviousString); // input multiple dishes
        tokens = strDish.split(",");
        idx = new int[20];

        for (int i = 0; i < tokens.length; i++) {
            idx[i] = Integer.parseInt(tokens[i]);
        }
        // Add input to dish list pending to order
        for (int i = 0; i < tokens.length; i++) {
            customer.addPendingOrder(menu.get(idx[i] - 1));
        }
    }

    public void confirmOrder() {
        boolean confirmOrder = false;
        int addDel = 0;
        String input = "";

        do {
            // Check ordered dish
            customer.outputPendingDish("\nYour pending orders: ");

            System.out.println("\nDo you want to confirm order?");
            promptions.promptConfirmOrder();

            System.out.print("\nYour option: ");
            input = Main.in.next("\nYour option: ");
            confirmOrder = Boolean.parseBoolean(input);

            if (confirmOrder) {
                break;
            } else {
                customer.outputPendingDish("\nYour pending orders: ");

                promptions.promptEditOrder();

                System.out.print("\nPlease choose your operation: ");
                try {
                    input = Main.in.next("\nPlease choose your operation:  ");
                    addDel = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Error! Wrong input for selection! Please input an integer!");
                }

                if (addDel == 3) {
                    confirmOrder = true;
                    break;
                } else if (addDel == 1) {
                    restaurant.printMenu();

                    // System.out.print("\nInput the dish number to add: (separate by a COMMA): ");
                    addDishtoPending("\nInput the dish number to add: (separate by a COMMA): ");
                } else if (addDel == 2) {
                    removeDishfromPending();
                }
            }
        } while (!confirmOrder);

        // Official order
        customer.updateOrder(restaurant);
        customer.setBillNo();
        String billno = customer.getBillno();
        customer.updateBillNumberToRestaurant(billno, restaurant);
    }

    public void removeDishfromPending() {

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
            // TODO: Modified on 16 Nov 23:11
            Collections.sort(idx, Collections.reverseOrder());
            for (int i : idx) {
            	dishModified.remove(i - 1);
            }

            customer.updatePendingOrder(dishModified);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }

}
