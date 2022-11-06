public class Admin implements UserType {

    private String adminId = "A0001";

    private String adminUsername = "Admin";
    protected String adminPassword = "admin1234";
    private static Admin instance = new Admin();

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
        Main.listOfRestaurants.add(res);
    }

    // Admin force-delete Restaurants into Main.listOfRestaurants
    public void deleteRestaurant(Restaurants res) {
        Main.listOfRestaurants.remove(res);
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
        Customers customer = Main.matchCId(cid);
        customer.getReserveInfo();
    }

    /*
     * For Table and Reservation Force Edit
     */

    // Set food court open and close (start of reservation and end of reservation)

    public void forceSetOpenAndClosingTime(String open, String close) {
        // open, close -> format: xx:xx
        TimeSlots.setOpenAndCloseTime(open, close);
    }

    // addTable
    public void forceAddTable(int tableId, int tableCapacity) {
        TableManagement tm = TableManagement.getInstance();
        tm.addNewTable(tableId, tableCapacity);
    }

    // deleteTable
    // 只有桌子在available的情況下可以刪除
    public void forceDeleteTable(int tableId) {
        TableManagement tm = TableManagement.getInstance();
        tm.removeTable(tableId);
    }

}
