package GoGoEat;

public class Admin implements UserType {

    private final String adminId = "A0001";
    private final String adminUsername = "Admin";
    private static final Admin instance = new Admin();
    
    private Commands command;
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

    public void setCommand(Commands command) {
        this.command = command;
    }

    public void callCommand() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
            ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {
        command.exe();
    }

    // Add Restaurants by passing instance
    public void addRestaurant(Restaurants res) {
        database.addTolistOfRestaurants(res);
        System.out.println("Add new restaurant success.");
    }

    // Delete Restaurants by passing instance
    public void deleteRestaurant(Restaurants res) {
        database.removeFromlistOfRestaurants(res);
        System.out.println("Delete restaurant success.");
    }

    // Output the order of customer
    public void checkCustomerOrder(Customers customers) {
        customers.printAllOrders();
    }

    // Check reserve info
    // UPDATE: Modified 18 Nov 00:01
    public String checkReserveInfo(String cid) throws ExNoReservationFound {
    	
    	/*
    	 * 1. Pass CID into method to check if there is a matching instance of customer
    	 * 2. IF customer has a match -> get Reserve Info
    	 * 3. Print reserve info
    	 */
    	
        Customers customer;
        String result = null;
        try {
            customer = database.matchCId(cid);
            result = customer.getReserveInfo();
            System.out.println(result);
        } catch (NullPointerException e) {
            throw new ExNoReservationFound();
        } catch (ExCustomersIdNotFound e) {
            System.out.println(e.getMessage());
        }
        return result;
    }


    // Set food court open and close (start of reservation and end of reservation)
    public boolean forceSetOpenAndClosingTime(String timeString) {

    	// format: xx:xx-xx:xx
        Boolean setOpenCloseTime = false;
        try {
            TablesManagement tm = TablesManagement.getInstance();
            setOpenCloseTime = tm.setOpenAndCloseTime(timeString);
        } catch (ExTimeFormatInvalid e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        // return to CommandAdminSetOpenCloseHour.java
        return setOpenCloseTime;
    }

    // addTable -> abort if tableId already in use (exist)
    public void forceAddTable(int tableId, int tableCapacity) throws ExTableIdAlreadyInUse {
        try {
            TablesManagement tm = TablesManagement.getInstance();
            tm.addNewTable(tableId, tableCapacity);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // deleteTable -> Abort if Table reserved/occupied or not exist 
    public void forceDeleteTable(int tableId) throws ExTableNotExist {
        try {
            TablesManagement tm = TablesManagement.getInstance();
            tm.removeTable(tableId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
