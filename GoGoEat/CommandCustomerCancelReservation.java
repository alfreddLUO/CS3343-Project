package GoGoEat;

public class CommandCustomerCancelReservation extends CommandCustomer {

    public CommandCustomerCancelReservation(Customers customer) {
        super(customer);
    }

    @Override
    public void exe()
            throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist, ExTimeSlotNotReservedYet {

        // Check if the customer has a reservation
        // Not reserved
        if (!customer.checkisReserved()) {
            System.out.println("\nError! You have not yet make a reservation.");

        } else {
            // Reserved
            if (cancelReservation()) {
                System.out.println("\nCancel Success!");
            } else {
                System.out.println("\nError! Cancellation unsuccessful.");
            }
        }
    }

    private boolean cancelReservation() throws ExTableNotExist, ExTimeSlotNotReservedYet {
        customer.getReservation().cancel();
        customer.clearReservation();
        // Check again for reservation info
        return !customer.checkisReserved();
    }

}
