package GoGoEat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class AccountManagement {

    // Username -> UserId mapping
    protected static HashMap<String, String> allaccounts = new HashMap<>();

    // Username -> Password mapping
    protected static HashMap<String, String> usernamesandPasswords = new HashMap<>();// username->password

    private final Database database = Database.getInstance();
    private GenerateId genCId = GenerateCustomerId.getInstance();
    private GenerateId genMId = GenerateMerchantId.getInstance();

    protected AccountManagement() {
    }

    private static final AccountManagement instance = new AccountManagement();

    public static AccountManagement getInstance() {
        return instance;
    }

    // login and return userId
    public String login(String username, String password) {

        /*
         * Case 1: Successful login with username and password -> return ID
         * Case 2: Wrong Password -> return null
         * Case 3: Username not found -> return null
         */

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

    // Register Admin account
    public void registerAdmin(String username, String password) {

        // Fixed UserId since only 1 admin instance allowed
        // Put admin ID into allaccounts Hashmap
        customerIDPutHashMap(username, "A0001");

        // Put username and password into usernamesandPasswords Hashmap
        AccountManagement.usernamesandPasswords.put(username, password);

        // Add admin instance into database as the only admin
        database.setAdmin(Admin.getInstance());
    }

    // register Customer Account
    public boolean registerCustomer(String username, String password) {

        /*
         * 1: Username already exist -> Not successfully registered -> return false
         * 2: Username not exist ->
         * Generate CustomerID with prefix 'C' and get next numbering -> return true
         */

        if (AccountManagement.usernamesandPasswords.containsKey(username)) {
            System.out.println("\nThis username has been registered. Please choose another username.");
            return false;
        } else {
            // Generate CustomerID with prefix 'C'

            String customerId = genCId.getNextId();

            // Put CID into allaccounts Hashmap
            customerIDPutHashMap(username, customerId);

            // Put username and password into usernameandPasswords Hashmap
            AccountManagement.usernamesandPasswords.put(username, password);

            // Add into listofCustomers in database
            database.addTolistofCustomers(allaccounts.get(username),
                    new Customers(username, allaccounts.get(username)));

            return true;
        }
    }

    // Register Merchant account
    public boolean registerMerchant(String username, String password, Restaurants restaurant) {

        /*
         * 1: Username already exist -> Not successfully registered -> return false
         * 2: Username not exist ->
         * Generate MerchantId with prefix 'M' and get next numbering -> return true
         */

        if (AccountManagement.usernamesandPasswords.containsKey(username)) {
            System.out.println("\nThis username has been registered. Please choose another username.");
            return false;
        } else {
            // Generate MerchantId with prefix 'M'
            String merchantID = genMId.getNextId();

            // Put MID into allaccounts Hashmap
            customerIDPutHashMap(username, merchantID);

            // Put username and password into usernameandPasswords Hashmap
            AccountManagement.usernamesandPasswords.put(username, password);

            // Add into listofMerchants in database
            database.addTolistofMerchants(allaccounts.get(username),
                    new Merchants(username, allaccounts.get(username), restaurant));

            // Add into listofMerchantsnRestaurant in database for mapping merchant and
            // restaurants
            database.addTolistofMerchantsnRestaurant(allaccounts.get(username), restaurant);

            return true;
        }
    }

    // Delete Account -> Delete from both usernameandPasswords and allaccounts
    public boolean deleteaccountinUserNameAndAccount(String username) {
        if (AccountManagement.usernamesandPasswords.containsKey(username)) {
            AccountManagement.usernamesandPasswords.remove(username);

            // Delete from allaccounts
            return deleteaccountinUserNameAndCustomerid(username);

        } else {
            System.out.println("\nPlease enter the correct username.");
            return false;
        }
    }

    // Delete from allaccounts
    public boolean deleteaccountinUserNameAndCustomerid(String username) {
        if (AccountManagement.allaccounts.containsKey(username)) {
            AccountManagement.allaccounts.remove(username);
            return true;
        } else {
            System.out.println("\nPlease enter the correct username.");
            return false;
        }
    }

    // Put the generated UserId and username into allaccounts
    public void customerIDPutHashMap(String username, String Id) {
        AccountManagement.allaccounts.put(username, Id);
    }

    public static Set<Entry<String, String>> sortByValue(HashMap<String, String> hashmap) {

        /*
         * Account List Sorting by UserId
         * 1. Sort by prefix: A, C, M
         * 2. Sort by ID: 4-index number
         */

        Set<Entry<String, String>> entries = hashmap.entrySet();

        // Sort Method comparator
        Comparator<Entry<String, String>> valueComparator = new Comparator<Entry<String, String>>() {
            @Override
            public int compare(Entry<String, String> e1, Entry<String, String> e2) {
                String v1 = e1.getValue();
                String v2 = e2.getValue();
                return v1.compareTo(v2);
            }
        };

        List<Entry<String, String>> listOfEntries = new ArrayList<Entry<String, String>>(entries);

        // sorting HashMap by values using comparator
        Collections.sort(listOfEntries, valueComparator);
        LinkedHashMap<String, String> sortedByValue = new LinkedHashMap<String, String>(listOfEntries.size());

        // List to Map
        for (Entry<String, String> entry : listOfEntries) {
            sortedByValue.put(entry.getKey(), entry.getValue());
        }

        Set<Entry<String, String>> entrySetSortedByValue = sortedByValue.entrySet();

        return entrySetSortedByValue;
    }

    public void printAllActiveAccounts() {

        /*
         * Print all active accounts with numbering
         * [numbering] [username] [userId]
         */

        Set<Entry<String, String>> sortedAllAccounts = sortByValue(allaccounts);
        System.out.print("\nList of All Active Accounts: ");
        int i = 1;
        for (Entry<String, String> mapping : sortedAllAccounts) {
            System.out.printf("\n[%02d] %5s | %s", i, mapping.getValue(), mapping.getKey());
            i++;
        }
        System.out.print("\n");
    }

    public void printMerchantOfTheRestaurant(Restaurants restaurant) {

        /*
         * Print all active accounts with numbering
         * [numbering] [username] [userId]
         */

        Set<Entry<String, String>> sortedAllAccounts = sortByValue(allaccounts);

        System.out.print("\nList of Merchants of this restaurant: ");
        int i = 1;
        for (Entry<String, String> mapping : sortedAllAccounts) {
            if (mapping.getValue().charAt(0) == 'M') {
                if (database.matchMId(mapping.getValue()).getRestaurantOwned() == restaurant) {
                    System.out.printf("\n[%02d] %5s | %s", i, mapping.getValue(), mapping.getKey());
                    i++;
                }

            }
        }
        System.out.print("\n");
    }

    // Identify which userType belong to the login account, then return Module to
    // Main to run
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
