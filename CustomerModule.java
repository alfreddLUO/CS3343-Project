import java.util.ArrayList;

public class CustomerModule {
    private static Customers customer;
    static ArrayList<Dish> pendingOrder = new ArrayList<>();
    static ArrayList<Dish> menu = new ArrayList<>();
    static Restaurants restaurant = null;
    static ArrayList<Restaurants> availableRestaurants = Main.listOfRestaurants;
    static TableManagement tm = TableManagement.getInstance();
    /*
     * 流程：
     * 登入在login module
     * 1. 登入后顾客选择dine-in / reserve
     * 1.1 Dine-in的话查看有无空位
     * 1.2 Reserve的话查看空余时间段
     * 
     * 2. 下单
     * 2.1 展示餐厅列表
     * 2.2 选择餐厅
     * 2.3 展示餐单
     * 2.4 选择菜品 （可多个菜品）
     * 
     * 2.5 确认下单
     * 2.5.1 取消（加单）
     * 2.5.2 取消（减单）
     * 2.5.3 确认（把pendingOrder加到customer里的orders）
     * 
     * 2.6 付款 (in customers.checkout())
     * 2.6.1 展示已下单项目
     * 2.6.2 计算总额 （包含VIP/superVIp的不同折扣）
     * 2.6.3 选择付款方式
     * 2.6.4 导到不同付款平台
     * 
     * 2.7 完成 - 退出
     */

    // Constructor
    public CustomerModule() {
    }

    public static void run(Customers c) {

        customer = c;
        String input = "";
        int select = 0;

        // 如果customerState根據消費總額算，就disable以下一行：
        clearOrderNPrice();

        while (select != 5) {
            do {
                // check if customer is sit-down
                if (customer.getOccupiedTableId().isEmpty()) {
                    if (customer.checkisReserved()) {
                        System.out.println(customer.getReserveInfo());

                        System.out.println("\nCommands: ");
                        System.out.println("[1] Dine in");
                        System.out.println("[3] Cancel Reservation");
                        System.out.println("[5] Logout");

                    } else {
                        // Get Customer's choice to dine or reserve
                        System.out.println("\nCommands: ");
                        System.out.println("[1] Dine in");
                        System.out.println("[2] Reserve");
                        System.out.println("[5] Logout");
                    }

                } else {
                    System.out.println("\nCommands: ");
                    System.out.println("[2] Reserve");
                    System.out.println("[4] Check Out");
                    System.out.println("[5] Logout");
                }

                System.out.print("\nPlease select your operation: ");
                input = Main.in.next("\nInput: ");
                select = Integer.parseInt(input);

                if (select == 5) {
                    break;
                } else if (select == 1 || select == 2 || select == 3) {

                    if (select == 1) {

                        if (dineInOperation()) {
                            // After Dine-in(get ticket and sit down) -> About Ordering
                            ordering();

                            // display official-confirmed-ordered dish
                            customer.printOrders();

                            // About Payment
                            Payment pay = new Payment(customer);
                            pay.payProcess();
                            // tm.checkOutByCustomer(customer.getOccupiedTableId());
                        }
                    } else if (select == 2) {
                        if (customer.checkisReserved()) {
                            System.out.println("\nError! You already has a reservation.");
                            break;
                        } else {
                            reservationOperation();
                        }
                    } else if (select == 3) {
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
                    }

                } else if (select == 4) {
                    tm.checkOutByCustomer(customer.getOccupiedTableId());
                    System.out.println("\nYou have successfully check out.");
                    select = 0;
                }

                if (select == 5) {
                    break;
                }
            } while (select != 1 && select != 2 && select != 3);
        }
    }

    public static boolean reservationOperation() {

        ArrayList<Integer> chosedTableIds = new ArrayList<>();
        String reserveTime = null, chosedTable = null;
        int chosedTableId = 0;

        do {
            // Show available time slot for booking
            tm.showReservationTable();

            // Input the time slot you want to reserve, format: 11:23-12:22
            System.out.print("\nPlease input time slot to reserve (Format: xx:xx-xx:xx): ");
            reserveTime = Main.in.next("Input: ");

            // choose table by tableId
            System.out.print("Please input the table ids you want to reserve: (separate by comma): ");
            chosedTable = Main.in.next("Input: ");
            String[] idx = chosedTable.split(",");

            for (int i = 0; i < idx.length; i++) {
                chosedTableId = Integer.parseInt(idx[i]);
                chosedTableIds.add(chosedTableId);
            }

        } while (chosedTableId == 0);

        // set reserve in customer.java
        customer.setReserve(reserveTime, chosedTableIds);

        // indicator for successful booking
        if (customer.getReserve() != null) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean dineInOperation() {
        tm.showAvailableTables();
        String str;

        // Input number of people to dine in
        System.out.print("Please input the number of people: ");
        int numOfPeople;
        str = Main.in.next("Input: ");
        numOfPeople = Integer.parseInt(str);

        ArrayList<Integer> result = new ArrayList<>();
        // result = tm.makeTableArrangements(numOfPeople);
        result = tm.arrangeTableAccordingToNumOfPeople(numOfPeople);

        if (tm.canDirectlyDineIn(result)) { // 彈message dine-in或者leave

            int select = 0;

            do {
                System.out.println("You can now directly walk in.");

                System.out.println("\nCommands: ");
                System.out.println("[1] Walk in");
                System.out.println("[2] Leave");

                System.out.print("\nPlease choose your operation: ");
                str = Main.in.next("Input: ");
                select = Integer.parseInt(str);

                if (select == 2) {
                    return false;
                } else if (select == 1) {
                    ArrayList<Integer> checkinTableId = tm.setWalkInStatus(result);

                    // TODO: 拆成两个
                    addCheckInAndWaitingInfo(str, checkinTableId, null);

                    return true;
                } else {

                }
            } while (select != 1 && select != 2);

        } else {
            ArrayList<Integer> recommendedResult = tm.recommendedArrangementAccordingToWaitingTime(numOfPeople);

            if (recommendedResult == null) {

                int select = 0;

                do {
                    System.out.println("");

                    System.out.println("\nCommands: ");
                    System.out.println("[1] Queue");
                    System.out.println("[2] Leave");

                    System.out.print("\nPlease choose your operation: ");
                    str = Main.in.next("Input: ");
                    select = Integer.parseInt(str);

                    if (select == 1) {
                        tm.setWaitingTables(Main.getCustomerCid(customer), result);
                    } else if (select == 2) {

                    }

                } while (select != 1 && select != 2);
            } else {

                int select = 0;
                do {
                    System.out.println("");

                    System.out.println("\nCommands: ");
                    System.out.println("[1] Queue");
                    System.out.println("[2] Walk in with recommended arrangement");
                    System.out.println("[3] Leave");
                    System.out.print("\nPlease choose your operation: ");
                    str = Main.in.next("Input: ");
                    select = Integer.parseInt(str);

                    if (select == 1) {
                        tm.setWaitingTables(Main.getCustomerCid(customer), result);

                    } else if (select == 2) {
                        ArrayList<Integer> checkinTableId = tm.setWalkInStatus(result);

                        // TODO: 拆成两个
                        addCheckInAndWaitingInfo(str, checkinTableId, null);

                    } else if (select == 3) {
                        break;
                    }

                } while (select != 1 && select != 2);
            }
        }
        return false;
    }

    public static boolean cancelReservation() {
        customer.cancelReservation();
        customer.clearReservation();

        if (customer.getReserve() == null) {
            return true;
        } else {
            return false;
        }
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

    public static void ordering() {
        /*
         * 1. Choose Restaurant to order
         * 2. Choose Food to order from the menu of the chosen restaurant
         */

        // CHOOSE RESTAURANTS
        outputRestaurant();

        String input = "";

        // Match string input with arraylist to find the restaurant in list
        do {
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

    public static void confirmOrder() {
        boolean confirmOrder = false;
        int addDel = 0;
        String input = "";

        do {
            // Check ordered dish
            outputPendingDish();

            System.out.println("\nDo you want to confirm order?");
            System.out.println("\nCommands: ");
            System.out.println("[1] Yes: Please input 'True'/'true'/'TRUE'");
            System.out.println("[2] No: Please input 'False'/'false'/'FALSE'");

            System.out.print("\nYour option: ");
            input = Main.in.next("Input: ");
            confirmOrder = Boolean.parseBoolean(input);

            if (confirmOrder) {
                break;
            } else {
                outputPendingDish();

                System.out.print("\nDo you want to add or delete order?");

                System.out.println("\nCommands: ");
                System.out.println("[1] Add Order");
                System.out.println("[2] Delete Order");
                System.out.println("[3] Confirm Order");

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
                } else {

                }
            }
        } while (!confirmOrder);

        // confirm to order (OFFICIAL ORDER)
        for (int i = 0; i < pendingOrder.size(); i++) {
            customer.orderdish(pendingOrder.get(i));
        }
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

    public static void outputRestaurant() {
        System.out.println("\nAvailable restaurants: ");
        for (int i = 0; i < availableRestaurants.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + availableRestaurants.get(i).toString());
        }
    }

    public static void outputMenu() {
        System.out.println("\nMenu: ");
        for (int i = 0; i < menu.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + menu.get(i));
        }
    }

    public static void outputPendingDish() {
        System.out.println("\nYour pending orders: ");
        for (int i = 0; i < pendingOrder.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + pendingOrder.get(i));
        }
    }

    // CustomerState為累積消費總額時用：
    public static void clearOrderNPrice() {
        pendingOrder.clear();
        if (customer.customerOrders() != null) {
            customer.customerOrders().clear();
        }
        customer.getMembership().clearState();
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
}
