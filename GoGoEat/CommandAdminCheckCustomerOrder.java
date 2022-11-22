package GoGoEat;

public class CommandAdminCheckCustomerOrder extends CommandAdmin {

    CommandAdminCheckCustomerOrder() {
        super();
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
            database.matchCId(customerId).printAllOrders();
        } catch (NullPointerException e) {
            throw new ExCustomersIdNotFound(customerId);
        } catch (ExCustomersIdNotFound e) {
            System.out.println(e.getMessage());
        }

    }

}
