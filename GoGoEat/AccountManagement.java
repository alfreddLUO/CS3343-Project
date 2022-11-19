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

    public static Set<Entry<String, String>> sortByValue(HashMap<String, String> hashmap) {

        Set<Entry<String, String>> entries = hashmap.entrySet();
        // Sort Method
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
        Collections.sort(listOfEntries,
                valueComparator);
        LinkedHashMap<String, String> sortedByValue = new LinkedHashMap<String, String>(listOfEntries.size());

        // List to Map
        for (Entry<String, String> entry : listOfEntries) {
            sortedByValue.put(entry.getKey(), entry.getValue());
        }

        Set<Entry<String, String>> entrySetSortedByValue = sortedByValue.entrySet();

        return entrySetSortedByValue;
    }

    public void printAllActiveAccounts() {
        Set<Entry<String, String>> sortedAllAccounts = sortByValue(allaccounts);
        System.out.print("\nList of All Active Accounts: ");
        int i = 1;
        for (Entry<String, String> mapping : sortedAllAccounts) {
            System.out.printf("\n[%02d] %5s | %s", i, mapping.getValue(), mapping.getKey());
            i++;
        }
        System.out.print("\n");
    }

    public void printAllMerchantActiveAccounts() {
        Set<Entry<String, String>> sortedAllAccounts = sortByValue(allaccounts);
        System.out.print("\nList of all active merchant accounts: ");

        int i = 1;
        for (Entry<String, String> mapping : sortedAllAccounts) {
            if (mapping.getValue().charAt(0) == 'M') {
                System.out.printf("\n[%02d] %5s | %s", i, mapping.getValue(), mapping.getKey());
                i++;
            }
        }
        System.out.print("\n");
    }

    public void printMerchantOfTheRestaurant(Restaurants restaurant) {
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
