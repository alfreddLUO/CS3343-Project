public class CommandAdminCheckReservation implements Commands{

    private static final Database database = Database.getInstance();
    private static final Admin admin = Admin.getInstance();
    CommandAdminCheckReservation(){}
    @Override
    public void exe() throws ExCustomersIdNotFound {
        System.out.print("\nPlease input the CustomerId to check reservation info: ");
        String customerId = Main.in.next("\nPlease input the CustomerId to check reservation info: ");
        Customers customer = database.matchCId(customerId);
        String result = customer.getReserveInfo();
        if (result != null) {
            System.out.println(result);
        } else {
            System.out.println("There is no reservation for this customer.");
        }
        
    }
    
}
