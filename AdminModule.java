public class AdminModule {

    /*
     * Admin的流程操作
     * 1. 增加餐廳
     * 2. 刪除餐廳
     * 3. 修改時間
     * 4. 返回到Main
     */

    private static Admin admin = Admin.getInstance();

    public static void run() {
        int select = 0;
        String input = "";

        System.out.println("\nCommands: ");

        System.out.println("[1] Set Food Court's Opening and Closing Time");
        System.out.println("[2] Check Customer's Orders");
        System.out.println("[3] Check Customer's Reservation");
        System.out.println("[4] Upgrade Customer to Super VIP State");
        System.out.println("[5] Downgrade Customer to VIP State");
        System.out.println("[6] Set Customer's Discount Value");
        System.out.println("[7] Add Restaurant");
        System.out.println("[8] Remove Restaurant");
        System.out.println("[9] Add Table");
        System.out.println("[10] Remove Table");

        do {
            System.out.print("\nPlease select your operations: ");
            input = Main.in.next("Input: ");
            select = Integer.parseInt(input);

            Restaurants temp = null;
            String customerId = null;
            Customers customer = null;
            CustomerState customerState = null;
            int tableId = 0, tableCapacity = 0;

            switch (select) {
                case 1:
                    setOpenHours();
                    break;
                case 2:
                    System.out.println("\nPlease input the CustomerId to check order: ");
                    customerId = Main.in.next("Input: ");
                    customer = Main.matchCId(customerId);
                    admin.checkCustomerOrder(customer);
                    break;
                case 3:
                    System.out.println("\nPlease input the CustomerId to set state: ");
                    customerId = Main.in.next("Input: ");
                    customer = Main.matchCId(customerId);
                    System.out.println(customer.getReserveInfo());
                    break;
                case 4:
                    System.out.println("\nPlease input the CustomerId to set state: ");
                    customerId = Main.in.next("Input: ");
                    customer = Main.matchCId(customerId);
                    customerState = new CustomerSuperVIPstate();
                    admin.setCustomerState(customer, customerState);
                    break;
                case 5:
                    System.out.println("\nPlease input the CustomerId to set state: ");
                    customerId = Main.in.next("Input: ");
                    customer = Main.matchCId(customerId);
                    customerState = new CustomerVIPstate();
                    admin.setCustomerState(customer, customerState);
                    break;
                case 6:
                    System.out.println("\nPlease input the CustomerId to set state: ");
                    customerId = Main.in.next("Input: ");
                    customer = Main.matchCId(customerId);
                    System.out.println("\nPlease input the new discount value: ");
                    input = Main.in.next("Input: ");
                    double discount = Double.parseDouble(input);
                    admin.setCustomerDiscount(customer, discount);
                    break;
                case 7:
                    temp = addNewRestaurant();
                    admin.addRestaurant(temp);
                    showListOfRestaurants();
                    break;
                case 8:
                    temp = removeRestaurant();
                    admin.deleteRestaurant(temp);
                    showListOfRestaurants();
                    break;
                case 9:
                    System.out.println("\nPlease input the new tableId: ");
                    input = Main.in.next("Input: ");
                    tableId = Integer.parseInt(input);
                    System.out.println("\nPlease input the capacity of new table: ");
                    input = Main.in.next("Input: ");
                    tableCapacity = Integer.parseInt(input);
                    admin.forceAddTable(tableId, tableCapacity);
                    tableId = 0;
                    break;
                case 10:
                    System.out.println("\nPlease input the CustomerId to set state: ");
                    input = Main.in.next("Input: ");
                    tableId = Integer.parseInt(input);
                    admin.forceDeleteTable(tableId);
                    tableId = 0;
                    break;
            }

        } while (select != 1 && select != 2 && select != 3 && select != 4);
    }

    private static void setOpenHours() {
        String sHour = null, fHour = null;
        System.out.println("\nPlease input new opening hour: ");
        sHour = Main.in.next("Input: ");
        System.out.println("\nPlease input new closing hour: ");
        fHour = Main.in.next("Input: ");
        admin.forceSetOpenAndClosingTime(sHour, fHour);
    }

    public static Restaurants addNewRestaurant() {

        System.out.println("\nPlease input the name of the new Restaurant: ");
        String input = Main.in.nextLine("\nInput:");
        String rName = input;

        return new Restaurants(rName);
    }

    public static Restaurants removeRestaurant() {

        String rName = "";
        Restaurants restaurant = null;

        do {
            System.out.println("\nPlease input the name of the Restaurant to remove: ");
            String input = Main.in.nextLine("\nInput: ");
            rName = input;

            restaurant = Main.matchRestaurant(rName);
        } while (restaurant == null);

        return restaurant;
    }

    public static void showListOfRestaurants() {
        System.out.println("\nList of Restaurants: ");

        for (int i = 0; i < Main.listOfRestaurants.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + Main.listOfRestaurants.get(i).toString());
        }
    }

    // TODO: Modify Time
    public static void modifyTime() {

    }
}
