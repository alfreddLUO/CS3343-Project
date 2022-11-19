package GoGoEat;

public class CommandCustomerCheckOut implements Commands {
    private static final TablesManagement tm = TablesManagement.getInstance();
    private Customers customer;
    private String outputString;

    public CommandCustomerCheckOut(Customers customer, String outputString) {
        this.customer = customer;
        this.outputString = outputString;
    }

    @Override
    public void exe()
            throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist, ExTimeSlotNotReservedYet {
        tm.checkOutByCustomer(customer.getOccupiedTableId());
        customer.clearOccupiedTableId();

        // TODO: Modified 16 Nov 23:18
        System.out.printf("\n%s\n", outputString);
    }
}
