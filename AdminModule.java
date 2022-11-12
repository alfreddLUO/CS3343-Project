public class AdminModule implements UserModule {

    /*
     * Admin's routine
     * 1. Set food court's opening and closing time
     * 2. Check customer's orders
     * 3. Check customer's reservation status
     *
     * 4,5,6 Deleted
     * 4. Upgrade customer from VIP state to SuperVIP state
     * 5. Downgrade customer from SuperVIP to VIP state
     * 6. Set customer's discount value
     *
     * 7. Add restaurant into allRestaurant list
     * 8. Remove restaurant from allRestaurant list
     * 9. Add Table
     * 10. Remove Table
     */

    private AdminModule() {
    }

    private static final AdminModule instance = new AdminModule();

    public static AdminModule getInstance() {
        return instance;
    }

    private static final Admin admin = Admin.getInstance();

    public void promptOptionStart() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Set Food Court's Opening and Closing Time");
        System.out.println("[2] Check Customer's Orders");
        System.out.println("[3] Check Customer's Reservation");
        // System.out.println("[4] Upgrade Customer to Super VIP State");
        // System.out.println("[5] Downgrade Customer to VIP State");
        // System.out.println("[6] Set Customer's Discount Value");
        System.out.println("[4] Add Restaurant");
        System.out.println("[5] Remove Restaurant");
        System.out.println("[6] Add Table");
        System.out.println("[7] Remove Table");
        System.out.println("[8] Logout");
    }

    public void run(String Id) {

        int select = 0;
        String input = "";

        while (select != 8) {

            promptOptionStart();

            System.out.print("\nPlease select your operations: ");
            input = Main.in.next("Input: ");
            select = Integer.parseInt(input);

            Restaurants temp = null;
            String customerId = null;
            Customers customer = null;
            int tableId = 0, tableCapacity = 0;

            switch (select) {
                case 1:
                    setOpenHours();
                    break;
                case 2:
                    System.out.print("\nPlease input the CustomerId to check order: ");
                    customerId = Main.in.next("Input: ");
                    customer = database.matchCId(customerId);
                    admin.checkCustomerOrder(customer);
                    break;
                case 3:
                    System.out.print("\nPlease input the CustomerId to check reservation info: ");
                    customerId = Main.in.next("Input: ");
                    customer = database.matchCId(customerId);
                    String result = customer.getReserveInfo();
                    if (result != null) {
                        System.out.println(result);
                    } else {
                        System.out.println("There is no reservation for this customer.");
                    }
                    break;
                // case 4:
                // System.out.println("\nPlease input the CustomerId to set state: ");
                // customerId = Main.in.next("Input: ");
                // customer = database.matchCId(customerId);
                // customerState = new CustomerSuperVIPstate();
                // admin.setCustomerState(customer, customerState);
                // break;
                // case 5:
                // System.out.println("\nPlease input the CustomerId to set state: ");
                // customerId = Main.in.next("Input: ");
                // customer = database.matchCId(customerId);
                // customerState = new CustomerVIPstate();
                // admin.setCustomerState(customer, customerState);
                // break;
                // case 6:
                // System.out.println("\nPlease input the CustomerId to set state: ");
                // customerId = Main.in.next("Input: ");
                // customer = database.matchCId(customerId);
                // System.out.println("\nPlease input the new discount value: ");
                // input = Main.in.next("Input: ");
                // double discount = Double.parseDouble(input);
                // admin.setCustomerDiscount(customer, discount);
                // break;
                case 4:
                    temp = addNewRestaurant();
                    admin.addRestaurant(temp);
                    database.showListOfRestaurants();
                    break;
                case 5:
                    temp = removeRestaurant();
                    if (temp == null) {
                        System.out.println("No such restaurant. Please check again.");
                    } else {
                        admin.deleteRestaurant(temp);
                    }
                    database.showListOfRestaurants();
                    break;
                case 6:
                    System.out.print("\nPlease input the new tableId: ");
                    input = Main.in.next("Input: ");
                    tableId = Integer.parseInt(input);
                    System.out.print("\nPlease input the capacity of new table: ");
                    input = Main.in.next("Input: ");
                    tableCapacity = Integer.parseInt(input);
                    admin.forceAddTable(tableId, tableCapacity);
                    tableId = 0;
                    break;
                case 7:
                    System.out.print("\nPlease input the TableId to delete table: ");
                    input = Main.in.next("Input: ");
                    tableId = Integer.parseInt(input);
                    admin.forceDeleteTable(tableId);
                    tableId = 0;
                    break;
                case 8:
                    break;
            }

        }
    }

    private void setOpenHours() throws ExUnableToSetOpenCloseTime {
        String sHour = null, fHour = null;
        boolean success = false;

        System.out.print("\nPlease input new opening hour: ");
        sHour = Main.in.next("Input: ");
        System.out.print("Please input new closing hour: ");
        fHour = Main.in.next("Input: ");
        success = admin.forceSetOpenAndClosingTime(sHour, fHour);

        if (success) {
            System.out.println("\nChange opening and closing time success!");
            System.out.printf("The new opening time is %s.\nThe new closing time is %s.\n", sHour, fHour);
        }
    }

    public Restaurants addNewRestaurant() {

        System.out.print("\nPlease input the name of the new Restaurant: ");
        String rName = Main.in.nextLine("\nInput:");

        return new Restaurants(rName);
    }

    public Restaurants removeRestaurant() {

        String rName = "";
        Restaurants restaurant = null;

        System.out.print("\nPlease input the name of the Restaurant to remove: ");
        rName = Main.in.nextLine("\nInput: ");

        restaurant = database.matchRestaurant(rName);

        return restaurant;
    }

}
