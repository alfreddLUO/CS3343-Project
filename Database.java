import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Map.Entry;

public class Database {

    private static Database instance = null;

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /*
     * Storage
     */

    // Temporary Database
    private ArrayList<Restaurants> listOfRestaurants = new ArrayList<>();

    // CustomerId, Customer instance
    private HashMap<String, Customers> listofCustomers = new HashMap<>();

    // MerchantId, Merchant instance
    private HashMap<String, Merchants> listofMerchants = new HashMap<>();

    // MerchantId, Restaurant
    private HashMap<String, Restaurants> listofMerchantsnRestaurant = new HashMap<>();

    private Admin admin = null;

    /*
     * Functions
     */

    public ArrayList<Restaurants> getListofRestaurants() {
        return listOfRestaurants;
    }

    public void outputRestaurant() {
        System.out.println("\nAvailable restaurants: ");
        for (int i = 0; i < listOfRestaurants.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + listOfRestaurants.get(i).toString());
        }
    }

    public void addTolistOfRestaurants(Restaurants restaurant) {
        listOfRestaurants.add(restaurant);
    }

    public void registerRestaurant(String rName) {
        listOfRestaurants.add(new Restaurants(rName));
    }

    public void removeFromlistOfRestaurants(Restaurants restaurant) {
        listOfRestaurants.remove(restaurant);
    }

    public void showListOfRestaurants() {
        System.out.println("\nList of Restaurants: ");

        for (int i = 0; i < listOfRestaurants.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + listOfRestaurants.get(i).toString());
        }
    }

    // in AdminModule.removeRestaurant()
    public Restaurants matchRestaurant(String rName) {
        for (int i = 0; i < listOfRestaurants.size(); i++) {
            if (listOfRestaurants.get(i).toString().equals(rName)) {
                return listOfRestaurants.get(i);
            }
        }
        return null;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public void addTolistofMerchants(String MId, Merchants merchant) {
        listofMerchants.put(MId, merchant);
    }

    public void addTolistofMerchantsnRestaurant(String MId, Restaurants restaurant) {
        listofMerchantsnRestaurant.put(MId, restaurant);
    }

    // find merchant instance by using merchantId
    public Merchants matchMId(String mid) {
        return listofMerchants.get(mid);
    }

    public void addTolistofCustomers(String CId, Customers customer) {
        listofCustomers.put(CId, customer);
    }

    // find customer instance by using customerId
    public Customers matchCId(String cid) {
        return listofCustomers.get(cid);
    }

    public <T, E> T getKeyByValue(HashMap<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    // find customerId by using customer instance
    public String getCustomerCid(Customers customer) {
        return getKeyByValue(listofCustomers, customer);
    }

}
