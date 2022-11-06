import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Map.Entry;

class Main {

    public static final InputScanner1 in = InputScanner1.getInstance();

    // Temporary Database
    protected static ArrayList<Restaurants> listOfRestaurants = new ArrayList<>();

    // CustomerId, Customer instance
    protected static HashMap<String, Customers> listofCustomers = new HashMap<>();

    // MerchantId, Merchant instance
    protected static HashMap<String, Merchants> listofMerchants = new HashMap<>();

    // MerchantId, Restaurant
    protected static HashMap<String, Restaurants> listofMerchantsnRestaurant = new HashMap<>();

    protected static Admin admin = null;

    private static AccountManagement accManager = AccountManagement.getInstance();

    /*
     * Main Function
     */

    public static void main(String[] args) {
        Initialization initialization = Initialization.getInstance();

        initialization.initiateRestaurants();
        initialization.initiateDish();
        initialization.initializeAdmin();
        initialization.initiateMerchants();
        initialization.initiateCustomers();
        initialization.initiateTables();

        while (true) {
            accManager.printAllActiveAccounts();
            loginNRegisterNDelete();
        }
    }

    public static void loginNRegisterNDelete() {
        int select = 0;
        boolean success = false;

        String ID = null, input = null;

        do {
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

                    // 分辨login的account是哪一個UserType, 再Module.run()
                    if (accManager.distinguishMerchantandCustomer(ID).equals("C")) {
                        CustomerModule.run(listofCustomers.get(ID));
                    } else if (accManager.distinguishMerchantandCustomer(ID).equals("M")) {
                        MerchantModule.run(listofMerchants.get(ID));
                    } else if (accManager.distinguishMerchantandCustomer(ID).equals("A")) {
                        AdminModule.run();
                    } else {
                        System.out.println("There is some error in running corresponding module.");
                    }

                } else {
                    System.out.println("\nLogin failed!");
                }
            } else if (select == 2) {

                // register -> 完成後返回到這裡選擇login
                // 返回成功不成功
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

                System.out.print("\nPlease input the password: ");
                input = Main.in.next("Input: ");
                password = input;

                registerFinished = accManager.registerCustomer(username, password);

            } else if (select == 2) {

                String rName;
                System.out.println("\nPlease input the name of the Restaurant: ");
                input = Main.in.next("Input: ");
                rName = input;

                registerRestaurant(rName);
                registerFinished = accManager.registerMerchant(username, password, matchRestaurant(rName));

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

    public static void registerRestaurant(String rName) {
        listOfRestaurants.add(new Restaurants(rName));
    }

    public static void addTolistOfRestaurants(Restaurants restaurant) {
        listOfRestaurants.add(restaurant);
    }

    // find customer instance by using customerId
    public static Customers matchCId(String cid) {
        return listofCustomers.get(cid);
    }

    // find merchant instance by using merchantId
    public static Merchants matchMId(String mid) {
        return listofMerchants.get(mid);
    }

    // in AdminModule.removeRestaurant()
    public static Restaurants matchRestaurant(String rName) {
        for (int i = 0; i < listOfRestaurants.size(); i++) {
            if (listOfRestaurants.get(i).toString().equals(rName)) {
                return listOfRestaurants.get(i);
            }
        }
        return null;
    }

    public static <T, E> T getKeyByValue(HashMap<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    // find customerId by using customer instance
    public static String getCustomerCid(Customers customer) {
        return getKeyByValue(listofCustomers, customer);
    }
}
