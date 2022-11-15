public class CommandCustomerCancelReservation implements Commands{
    
    private Customers customer;
    public CommandCustomerCancelReservation(Customers customer){
        this.customer=customer;
    }
    @Override
    public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist, ExTimeSlotNotReservedYet {
        if (!customer.checkisReserved()) {
            System.out.println("\nError! You have not yet make a reservation.");
       
        } else {
            if (cancelReservation()) {
                System.out.println("\nCancel Success!");
            } else {
                System.out.println("\nError! Cancellation unsuccessful.");
            }
        }
        
    }

    public boolean cancelReservation() throws ExTableNotExist, ExTimeSlotNotReservedYet {
        customer.cancelReservation();
        customer.clearReservation();
        return customer.getReserve() == null;
    }
    
}
