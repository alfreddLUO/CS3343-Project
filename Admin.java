public class Admin implements UserType {

    private final String adminId = "A0001";
    private final String adminUsername = "Admin";

    private static final Admin instance = new Admin();
    private static final Database database = Database.getInstance();

    public Admin() {
    }

    public static Admin getInstance() {
        return instance;
    }

    @Override
    public String getUsername() {
        return adminUsername;
    }

    @Override
    public String getUserId() {
        return adminId;
    }

    // Admin force-add Restaurants into Main.listOfRestaurants
    public void addRestaurant(Restaurants res) {
        database.addTolistOfRestaurants(res);
        System.out.println("Add new restaurant success.");
    }

    // Admin force-delete Restaurants into Main.listOfRestaurants
    public void deleteRestaurant(Restaurants res) {
        database.removeFromlistOfRestaurants(res);
        System.out.println("Delete restaurant success.");
    }

    /*
     * For Customer Force Edit
     */

    // Force set customer state
    public void setCustomerState(Customers customers, CustomerState state) {
        CustomerMembership customerMembership = new CustomerMembership(customers);
        customerMembership.adminUpdateState(state);
    }

    // Force set customer discount
    public void setCustomerDiscount(Customers customer, double discount) {
        customer.getMembership().setdiscount(discount);
    }

    // Output the order only
    public void checkCustomerOrder(Customers customers) {
        customers.printOrders();
    }

    // Check reserve info
    public void checkReserveInfo(String cid) {
        Customers customer;
        try {
            customer = database.matchCId(cid);
            customer.getReserveInfo();
        } catch (ExCustomersIdNotFound e) {
            System.out.println(e.getMessage());
        }

    }

    /*
     * For Table and Reservation Force Edit
     */

    // Set food court open and close (start of reservation and end of reservation)

    public boolean forceSetOpenAndClosingTime(String open, String close) {
        // open, close -> format: xx:xx
        Boolean setOpenCloseTime = false;
        try {
            TablesManagement tm = TablesManagement.getInstance();
            setOpenCloseTime = tm.setOpenAndCloseTime(open, close);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return setOpenCloseTime;
    }

    // addTable
    public void forceAddTable(int tableId, int tableCapacity) throws ExTableIdAlreadyInUse {
        try {
            TablesManagement tm = TablesManagement.getInstance();
            tm.addNewTable(tableId, tableCapacity);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // deleteTable
    // 只有桌子在available的情況下可以刪除
    public void forceDeleteTable(int tableId) throws ExTableNotExist {
        try {
            TablesManagement tm = TablesManagement.getInstance();
            tm.removeTable(tableId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
