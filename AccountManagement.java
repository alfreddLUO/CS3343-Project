import java.util.HashMap;

public class AccountManagement {

    protected static HashMap<String, String> allaccounts = new HashMap<>();// username-> userId
    protected static HashMap<String, String> usernamesandPasswords = new HashMap<>();// username->password

    private final Database database = Database.getInstance();

    protected AccountManagement() {
    }

    private static final AccountManagement instance = new AccountManagement();

    public static AccountManagement getInstance() {
        return instance;
    }

    // Register account and generate customerid
    public void customerIDPutHashMap(String username, String Id) {
        AccountManagement.allaccounts.put(username, Id);
    }

    // login
    public String login(String username, String password) {

        String userId = "";

        if (AccountManagement.usernamesandPasswords.containsKey(username)
                && AccountManagement.usernamesandPasswords.get(username).equals(password)) {

            System.out.print("\nYou have succeeded in logging in the system.");

            userId = AccountManagement.allaccounts.get(username);
            System.out.println("\nUserId: " + userId);
            return userId;

        } else if (AccountManagement.usernamesandPasswords.containsKey(username)
                && AccountManagement.usernamesandPasswords.get(username).equals(password)) {

            System.out.println("\nYour password is wrong. Please try again.");

        } else if (!AccountManagement.usernamesandPasswords.containsKey(username)) {

            System.out.println("\nWe cannot find the username. Please register first.");
        }
        return null;
    }

    public void registerAdmin(String username, String password) {

        // 隨機生成UserId並放到HashMap
        customerIDPutHashMap(username, "A0001");

        AccountManagement.usernamesandPasswords.put(username, password);

        // 加到Main的customer hashmap中
        database.setAdmin(Admin.getInstance());

        // System.out.println("\nRegistration completed. Please return to login.");

    }

    // register Customer Account
    public boolean registerCustomer(String username, String password) {
        if (AccountManagement.usernamesandPasswords.containsKey(username)) {
            System.out.println("\nThis username has been registered. Please choose another username.");

            return false;
        } else {
            GenerateCustomerId genCId = GenerateCustomerId.getInstance();
            String customerId = genCId.getNextId();

            // 隨機生成UserId並放到HashMap
            customerIDPutHashMap(username, customerId);

            AccountManagement.usernamesandPasswords.put(username, password);

            // 加到Main的customer hashmap中
            database.addTolistofCustomers(allaccounts.get(username),
                    new Customers(username, allaccounts.get(username)));

            // System.out.println("\nRegistration completed. Please return to login.");

            return true;
        }
    }

    // Register Merchant account
    public boolean registerMerchant(String username, String password, Restaurants restaurant) {
        if (AccountManagement.usernamesandPasswords.containsKey(username)) {
            System.out.println("\nThis username has been registered. Please choose another username.");
            return false;
        } else {
            GenerateMerchantId genMId = GenerateMerchantId.getInstance();
            String merchantID = genMId.getNextId();

            customerIDPutHashMap(username, merchantID);

            AccountManagement.usernamesandPasswords.put(username, password);

            // Add to merchants hashmap in database
            database.addTolistofMerchants(allaccounts.get(username),
                    new Merchants(username, allaccounts.get(username), restaurant));
            database.addTolistofMerchantsnRestaurant(allaccounts.get(username), restaurant);

            // System.out.println("\nRegistration completed. Please return to login.");

            return true;
        }
    }

    // Delete Account
    public boolean deleteaccountinUserNameAndAccount(String username) {
        if (AccountManagement.usernamesandPasswords.containsKey(username)) {
            AccountManagement.usernamesandPasswords.remove(username);
            return deleteaccountinUserNameAndCustomerid(username);

        } else {
            System.out.println("\nPlease enter the correct username.");
            return false;
        }
    }

    public boolean deleteaccountinUserNameAndCustomerid(String username) {
        if (AccountManagement.allaccounts.containsKey(username)) {
            AccountManagement.allaccounts.remove(username);
            return true;
        } else {
            System.out.println("\nPlease enter the correct username.");
            return false;
        }
    }

    public void printAllActiveAccounts() {
        System.out.println("\nList of Active Accounts: ");

        allaccounts.forEach((key, value) -> System.out.printf("%20s | %4s\n", key, value));
    }

    public void printAllMerchantActiveAccounts() {
        System.out.println("\nList of Active Accounts: ");
        allaccounts.forEach((key, value) -> {
            if (value.charAt(0) == 'M') {
                System.out.printf("\n%20s | %4s", key, value);
            }
        });

        System.out.print("\n");
    }

    public UserModule distinguishMerchantandCustomer(String userid) {
        if (userid.charAt(0) == 'C') {
            return CustomerModule.getInstance();
        } else if (userid.charAt(0) == 'M') {
            return MerchantModule.getInstance();
        } else if (userid.charAt(0) == 'A') {
            return AdminModule.getInstance();
        }
        return null;
    }
}
