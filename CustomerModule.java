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
        int temp = 0;

        // 如果customerState根據消費總額算，就disable以下一行：
        clearOrderNPrice();

        do {
            // Get Customer's choice to dine or reserve
            System.out.print("\nPlease choose your option: [1 Dine-in | 2 Reserve]: ");
            input = Main.in.next("Input: ");
            temp = Integer.parseInt(input);

        } while (temp != 1 && temp != 2);

        // Choosed Dine-in or Reserve -> Goto each function to do own things...
        DineReserve(temp);

        // After Dine-in(get ticket and sit down) or Reserve -> 下單流程
        Ordering();

        // display official-confirmed-ordered dish
        customer.printOrders();

        // 付款流程
        Payment pay = new Payment(customer);
        pay.payProcess();
    }

    /*
     * 1. Choose Restaurant to order
     * 2. Choose Food to order from the menu of the chosen restaurant
     */
    public static void Ordering() {
        // CHOOSE RESTAURANTS
        outputRestaurant();

        String input = "";

        // Match string input with arraylist to find the restaurant in list
        do {
            System.out.println("\nPlease choose the restaurant to order:");
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
        System.out.println(
                "\nPlease choose from the menu of restaurant " + restaurant.toString() + " (separate by a COMMA): ");
        addDishtoPending();

        // Check if confirm order, if confirm order add into customer orders
        confirmOrder();
    }

    public static void confirmOrder() {
        int confirmOrder = 2;
        int addDel = 0;
        String input = "";

        do {
            // Check ordered dish
            outputPendingDish();

            System.out.print("\nDo you want to add or delete order? [1 ADD | 2 DELETE | 3 Continue]: ");
            input = Main.in.next("Input: ");
            addDel = Integer.parseInt(input);

            if (addDel == 1) {
                outputMenu();
                System.out.print("\nInput the dish number to add: (separate by a COMMA): ");
                addDishtoPending();
            } else if (addDel == 2) {
                removeDishfromPending();
            } else {
                break;
            }

            outputPendingDish();

            System.out.print("\nDo you confirm to order? [1 YES | 2 NO]: ");
            input = Main.in.next("Input: ");
            confirmOrder = Integer.parseInt(input);

        } while (confirmOrder != 1);

        // confirm to order (OFFICIAL ORDER)
        for (int i = 0; i < pendingOrder.size(); i++) {
            customer.orderdish(pendingOrder.get(i));
        }
    }

    /*
     * 真的要用到的Function
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

    public static void DineReserve(int temp) {

        if (temp == 1) {

            dineInOperation();

        } else if (temp == 2) {
            ArrayList<Integer> chosedTableIds = new ArrayList<>();
            String reserveTime = null, chosedTable = null;
            int chosedTableId = 0;

            // TODO: Reserve
            do {
                // 展示可預約桌子的時間段
                tm.showReservationTable();

                // 輸入想要預訂的時間段, format: 11:23-12:22
                System.out.print("\nPlease input time slot to reserve (Format: xx:xx-xx:xx): ");
                reserveTime = Main.in.next("Input: ");

                // 選table
                System.out.print("\nPlease input the table ids you want to reserve: (separate by comma): ");
                chosedTable = Main.in.next("Input: ");
                String[] idx = chosedTable.split(",");
                for (int i = 0; i < idx.length; i++) {
                    chosedTableId = Integer.parseInt(idx[i]);
                    chosedTableIds.add(chosedTableId);
                }

                // 設置customer的reserve

            } while (chosedTableId == 0);

            reserveOperation(reserveTime, chosedTableIds);

        } else {
            // Do nothing & Keep inputing
        }
    }

    public static void dineInOperation() {
        tm.showAvailableTables();

        // 輸入人數
        String str;
        System.out.println("\nPlease input the number of people: ");
        int numOfPeople;
        str = Main.in.next("Input: ");
        numOfPeople = Integer.parseInt(str);

        ArrayList<Integer> result = new ArrayList<>();
        result = tm.makeTableArrangements(numOfPeople);

        if (tm.canDirectlyDineIn(result)) { // 彈message dine-in或者leave

            boolean dineIn = false;
            int select = 0;

            do {
                System.out.printf("\nYou can now directly dine in. Please indicate your option: [1 Dine In | 2 Leave]");
                str = Main.in.next("Input: ");
                select = Integer.parseInt(str);
                if (select == 1) {
                    dineIn = true;
                }
            } while (select != 1 && select != 2);

            if (dineIn) {
                tm.setWalkInStatus(result);
            }

        } else {
            // 等待Queue

            // 顾客需要等待的对应桌型的数量的list
            tm.setWaitingTables(Main.getCustomerCid(customer), result);
        }
    }

    // TODO:
    public static void reserveOperation(String reserveTime, ArrayList<Integer> chosedTableIds) {

        // input
        customer.setReserve(reserveTime, chosedTableIds);
    }

    public static void addCheckInAndWaitingInfo(String CId, ArrayList<Integer> checkInTable,
            ArrayList<Integer> waitingTableNumList) {
        // 找Customer Instance
        // checkInTable加到customer occupiedtable
        // 把waitingtableNumList 加到customer 相同的arrayList裡面
        Customers customer = Main.matchUserName(CId);
        for (int i : checkInTable) {
            customer.addTableId(i);
        }
        for (int i : waitingTableNumList) {
            customer.addTableNumList(i);
        }

    }

    // 把菜加到Pending Order裡面
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

    // 把菜從Pending Order裡面刪除
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
