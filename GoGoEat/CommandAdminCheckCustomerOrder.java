package GoGoEat;

public class CommandAdminCheckCustomerOrder implements Commands {

    private static final Database database = Database.getInstance();
    private static final Admin admin = Admin.getInstance();

    CommandAdminCheckCustomerOrder() {
    }

    @Override
    public void exe() throws ExCustomersIdNotFound {

    	/*
    	 * 1. Input Customer ID -> Check and get existing Instance
    	 * 2. Call admin.checkCustomerOrder to print
    	 */
    	
        System.out.print("\nPlease input the CustomerId to check order: ");
        String customerId = Main.in.next("\nPlease input the CustomerId to check order: ");
        try {
            Customers customer = database.matchCId(customerId);
            admin.checkCustomerOrder(customer);
        } catch (NullPointerException e) {
            throw new ExCustomersIdNotFound(customerId);
        }

    }

}
