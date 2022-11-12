import java.util.InputMismatchException;

class Main {

    public static final InputScanner in = InputScanner.getInstance();
    private static final Database database = Database.getInstance();
    private static final AccountManagement accManager = AccountManagement.getInstance();

    /*
     * Main Function
     */

    public static void main(String[] args) throws ExTableIdAlreadyInUse, ExTableNotExist, ExTimeSlotAlreadyBeReserved,
            ExTimeSlotNotReservedYet, ExUnableToSetOpenCloseTime {
        Initialization initialization = Initialization.getInstance();
        initialization.initialize();

        while (true) {
            loginNRegisterNDelete();
        }
    }

    public static void promptLoginNRegisterNDelete() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Login");
        System.out.println("[2] Register");
        System.out.println("[3] Delete Account");
    }

    public static void loginNRegisterNDelete() throws ExTableNotExist, ExTimeSlotAlreadyBeReserved,
            ExTimeSlotNotReservedYet, ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse {
        int select = 0;
        boolean success = false;

        String input = null;

        do {
            accManager.printAllActiveAccounts();

            try {
                promptLoginNRegisterNDelete();

                System.out.print("\nPlease select your operation: ");
                input = Main.in.next("Input: ");
                select = Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.println("\nError! Please input an integer!");
            }

            if (select == 1) {
                login();
            } else if (select == 2) {
                // register -> Come back here after successful registration
                success = register();

            } else if (select == 3) {
                success = deleteAcc();// delete account
            }

        } while (select != 1 && select != 3 || !success);

    }

    // Login, And return UserId
    public static boolean login() throws ExTableNotExist, ExTimeSlotAlreadyBeReserved, ExTimeSlotNotReservedYet,
            ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse {

        String input = "", username = "", password = "";

        System.out.print("\nPlease input the username: ");
        input = Main.in.next("Input: ");
        username = input;

        System.out.print("Please input the password: ");
        input = Main.in.next("Input: ");
        password = input;

        // return ID
        String ID = accManager.login(username, password);

        if (ID != null) {
            // Identify which userType belong to the login account, then run Module
            UserModule module = accManager.distinguishMerchantandCustomer(ID);
            if (module != null) {
                module.run(ID);
                return true;
            } else {
                System.out.println("There is some error in running corresponding module.");
                return false;
            }
        } else {
            System.out.println("\nLogin failed!");
            return false;
        }
    }

    public static boolean registerCustomer() {
        String username = null, password = null;
        try {
            System.out.print("\nPlease input the username: ");
            username = Main.in.next("Input: ");

            System.out.print("Please input the password: ");
            password = Main.in.next("Input: ");
        } catch (InputMismatchException ex) {
            System.out.println("\nError! Wrong input type!");
        }

        if (username != null && password != null) {
            return accManager.registerCustomer(username, password);
        } else {
            return false;
        }

    }

    public static boolean registerMerchant() {
        String username, password, rName;

        System.out.print("\nPlease input the username: ");
        username = Main.in.next("Input: ");

        System.out.print("Please input the password: ");
        password = Main.in.next("Input: ");

        System.out.print("\nPlease input the name of the Restaurant: ");
        rName = Main.in.next("Input: ");

        database.registerRestaurant(rName);
        return accManager.registerMerchant(username, password, database.matchRestaurant(rName));
    }

    public static boolean register() {

        String input = "";
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
            } catch (NumberFormatException ex) {
                System.out.println("\nError! NumberFormatException!");
            } catch (InputMismatchException ex) {
                System.out.println("\nError! Please input an integer of choice!");
            }

            if (select == 1) {
                registerFinished = registerCustomer();
            } else if (select == 2) {
                registerFinished = registerMerchant();
            } else if (select == 3) {
                break;
            }

        } while (select != 1 && select != 2 || !registerFinished);

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

        return accManager.deleteaccountinUserNameAndAccount(username);
    }

}
