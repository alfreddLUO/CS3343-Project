// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileNotFoundException;
// import java.io.FileOutputStream;
// import java.io.IOException;

// import java.io.ObjectInputStream;
// import java.io.ObjectOutputStream;

// import java.util.ArrayList;
import java.util.HashMap;

public class AccountManagement {
    /*
     * 先加载txt文件到hashmap里面
     */

    protected static HashMap<String, String> allaccounts = new HashMap<>();// username-> userId
    protected static HashMap<String, String> usernamesandPasswords = new HashMap<>();// username->password

    protected AccountManagement() {
        // try {

        // initializationALLACCOUNTStxt();
        // initializationUsernameandPasswordtxt();

        // } catch (IOException e) {
        // e.printStackTrace();
        // }

    }

    private static final AccountManagement instance = new AccountManagement();

    public static AccountManagement getInstance() {
        return instance;
    }

    // initialize into the hashmap

    // public void initializationUsernameandPasswordtxt() throws IOException {
    // FileInputStream freader;
    // try {
    // freader = new FileInputStream("UsernameAndPassword.txt");
    // ObjectInputStream objectInputStream = new ObjectInputStream(freader);

    // List<HashMap<String, String>> list = (List<HashMap<String, String>>)
    // objectInputStream.readObject();
    // for (HashMap<String, String> map : list) {
    // Visitor.usernamesandPasswords = map;
    // }

    // objectInputStream.close();

    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // } catch (ClassNotFoundException e) {
    // e.printStackTrace();
    // }
    // }

    // public void initializationALLACCOUNTStxt() throws IOException {
    // FileInputStream freader;
    // try {
    // freader = new FileInputStream("ALLACCOUNTS.txt");
    // ObjectInputStream objectInputStream = new ObjectInputStream(freader);

    // List<HashMap<String, String>> list = (List<HashMap<String, String>>)
    // objectInputStream.readObject();
    // for (HashMap<String, String> map : list) {
    // Visitor.allaccounts = map;
    // }

    // objectInputStream.close();

    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // } catch (ClassNotFoundException e) {
    // e.printStackTrace();
    // }
    // }

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

    public boolean registerAdmin(String username, String password) {

        // 隨機生成UserId並放到HashMap
        customerIDPutHashMap(username, "A0001");

        AccountManagement.usernamesandPasswords.put(username, password);

        // 加到Main的customer hashmap中
        Main.admin = Admin.getInstance();
        // System.out.println("\nRegistration completed. Please return to login.");

        return true;
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
            Main.listofCustomers.put(allaccounts.get(username), new Customers(username, allaccounts.get(username)));
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

            // Add to customer hashmap in Main
            Main.listofMerchants.put(allaccounts.get(username),
                    new Merchants(username, allaccounts.get(username), restaurant));
            Main.listofMerchantsnRestaurant.put(allaccounts.get(username), restaurant);

            // System.out.println("\nRegistration completed. Please return to login.");

            return true;
        }
    }

    // Delete Account
    public boolean deleteaccountinUserNameAndAccount(String username) {
        if (AccountManagement.usernamesandPasswords.containsKey(username)) {
            AccountManagement.usernamesandPasswords.remove(username);
            return true;
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

        allaccounts.entrySet().forEach(entry -> {
            System.out.printf("%20s | %4s\n", entry.getKey(), entry.getValue());
        });

        System.out.printf("\n");
    }

    public void printAllMerchantActiveAccounts() {
        System.out.println("\nList of Active Accounts: ");
        allaccounts.entrySet().forEach(entry -> {
            if (entry.getValue().substring(0, 1).equals("M")) {
                System.out.printf("\n%20s | %4s", entry.getKey(), entry.getValue());
            }

        });

        System.out.printf("\n");
    }

    public String distinguishMerchantandCustomer(String userid) {
        return userid.substring(0, 1);
    }

    // 退出时将hashmap中的数据存入对应的txt文件中
    // public void exit1andStoreAllaccounts() throws Exception {
    // try {
    // File file = new File("ALLACCOUNTS.txt");
    // file.delete();
    // List<HashMap<String, String>> list = new ArrayList<HashMap<String,
    // String>>();
    // list.add(AccountManagement.allaccounts);
    // FileOutputStream fileOutputStream = new FileOutputStream("ALLACCOUNTS.txt");
    // ObjectOutputStream objectOutputStream = new
    // ObjectOutputStream(fileOutputStream);
    // objectOutputStream.writeObject(list);
    // fileOutputStream.close();
    // System.out.println("\nYou have succeeded in input the data to the
    // ALLACCOUNTS.txt");
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    // public void exit2andStoreUsernameAndPassword() throws Exception {
    // try {
    // File file = new File("UsernameAndPassword.txt");
    // file.delete();
    // List<HashMap<String, String>> list = new ArrayList<HashMap<String,
    // String>>();
    // list.add(AccountManagement.usernamesandPasswords);
    // FileOutputStream fileOutputStream = new
    // FileOutputStream("UsernameAndPassword.txt");
    // ObjectOutputStream objectOutputStream = new
    // ObjectOutputStream(fileOutputStream);
    // objectOutputStream.writeObject(list);
    // fileOutputStream.close();
    // System.out.println("\nYou have succeeded in input the data to the
    // UsernameAndPassword.txt");
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
}
