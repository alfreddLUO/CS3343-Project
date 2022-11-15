public class CommandCustomerCheckOut implements Commands{
    private static final TablesManagement tm = TablesManagement.getInstance();
    private Customers customer;
    public CommandCustomerCheckOut(Customers customer){
        this.customer=customer;
    }
    @Override
    public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist, ExTimeSlotNotReservedYet {
        tm.checkOutByCustomer(customer.getOccupiedTableId());
        customer.clearOccupiedTableId();
        System.out.println("\nYou have successfully check out.");        
    }
}
