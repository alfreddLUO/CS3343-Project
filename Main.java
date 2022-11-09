class Main {

    public static final InputScanner in = InputScanner.getInstance();
    private static Database database = Database.getInstance();
    private static AccountManagement accManager = AccountManagement.getInstance();

    /*
     * Main Function
     */

    public static void main(String[] args) {
        Initialization initialization = Initialization.getInstance();
        initialization.initialize();

        while (true) {
            loginNRegisterNDelete();
        }
    }

    public static void loginNRegisterNDelete() {
        int select = 0;
        boolean success = false;

        String ID = null, input = null;

        do {
            accManager.printAllActiveAccounts();

            try {
                System.out.println("\nCommands: ");
                System.out.println("[1] Login");
                System.out.println("[2] Register");
                System.out.println("[3] Delete Account");

                System.out.print("\nPlease select your operation: ");
                input = Main.in.next("Input: ");
                select = Integer.parseInt(input);

            } catch (NumberFormatException e) {
                e.getStackTrace();
            }

            if (select == 1) {

                success = false;
                // Login
                ID = login();
                if (ID != null) {
                    success = true; // 成功登入
                }

                // Enter module run() after successful login
                if (success == true) {

                    // Identify which userType belong to the login account, then Module.run()
                    if (accManager.distinguishMerchantandCustomer(ID).equals("C")) {
                        CustomerModule customerModule = CustomerModule.getInstance();
                        customerModule.run(database.matchCId(ID));
                    } else if (accManager.distinguishMerchantandCustomer(ID).equals("M")) {
                        MerchantModule merchantModule = MerchantModule.getInstance();
                        merchantModule.run(database.matchMId(ID));
                    } else if (accManager.distinguishMerchantandCustomer(ID).equals("A")) {
                        AdminModule adminModule = AdminModule.getInstance();
                        adminModule.run();
                    } else {
                        System.out.println("There is some error in running corresponding module.");
                    }

                } else {
                    System.out.println("\nLogin failed!");
                }
            } else if (select == 2) {

                // register -> Come back here after successful registration
                // return if successful
                success = register();

            } else if (select == 3) {

                // delete account
                success = deleteAcc();
            }

        } while (select != 1 && select != 3 || success == false);

    }

    // Login, And return UserId
    public static String login() {

        String input = "", username = "", password = "";

        System.out.print("\nPlease input the username: ");
        input = Main.in.next("Input: ");
        username = input;

        System.out.print("Please input the password: ");
        input = Main.in.next("Input: ");
        password = input;

        // 返回ID，需判断是Customer/Merchant/Admin
        String temp = accManager.login(username, password);

        return temp;
    }

    public static boolean register() {

        String input = "", username = "", password = "";
        int select = 0;
        boolean registerFinished = false;

        // 1: Register Customer Account
        // 2: Register Merchant Account (And register Restaurant at the same time)
        do {
            try {
                System.out.print(
                        "\nPlease choose the type of account to register [1 Customer | 2 Merchant | 3 Cancel]: ");
                input = Main.in.next("Input: ");
                select = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (select == 1) {

                System.out.print("\nPlease input the username: ");
                input = Main.in.next("Input: ");
                username = input;

                System.out.print("Please input the password: ");
                input = Main.in.next("Input: ");
                password = input;

                registerFinished = accManager.registerCustomer(username, password);

            } else if (select == 2) {

                String rName;
                System.out.println("\nPlease input the name of the Restaurant: ");
                input = Main.in.next("Input: ");
                rName = input;

                database.registerRestaurant(rName);
                registerFinished = accManager.registerMerchant(username, password, database.matchRestaurant(rName));

            } else if (select == 3) {

                break;

            } else {
                // TODO： 重新输入
            }
        } while (select != 1 && select != 2 || registerFinished == false);

        if (registerFinished) {
            System.out.println("\nRegistration Completed. Please return to login.");
        }
        return registerFinished;
    }

    public static boolean deleteAcc() {

        String username = "", input = "";

        System.out.print("\nPlease input the username to delete account: ");
        input = Main.in.next("Input: ");
        username = input;

        boolean success = accManager.deleteaccountinUserNameAndAccount(username);

        return success;
    }

}
