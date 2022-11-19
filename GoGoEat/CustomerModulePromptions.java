package GoGoEat;

class CustomerModulePromptions implements AbstractModulePromptions {
    private Customers customer;

    // constructor
    public CustomerModulePromptions(Customers customer) {
        this.customer = customer;
    }

    // customer module
    public void promptOptionStart() {

        System.out.print("\n--------------------------------------------------");

        // Check if customer is sit-down
        
        // Not Sit down
        if (customer.getOccupiedTableId().isEmpty()) {
        	
        	// Check if customer has reserved
        	if (customer.checkisReserved()) {

        		// reserved -> print reservation info
                System.out.println(customer.getReserveReminder());
                promptOptionReserved();

            } else {
            	// Not reserved -> Get Customer's choice to dine or reserve
                promptOptionNotReserved();
            }
        } else {
        	// Already Sit down
        	
        	// Reserved
            if (customer.checkisReserved()) {
               
            	System.out.println(customer.getReserveReminder());
                promptOptionReservedStillSitting();
                
            } else {
            	// Not reserved (customer dining & haven't get up from seat)
                promptOptionNotReservedStillSitting();
            }

        }
    }

    public void promptOptionReserved() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Dine in");
        System.out.println("[3] Cancel Reservation");
        System.out.println("[5] Logout");
    }

    public void promptOptionNotReserved() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Dine in");
        System.out.println("[2] Reserve");
        System.out.println("[5] Logout");
    }

    public void promptOptionNotReservedStillSitting() {
        System.out.println("\nCommands: ");
        System.out.println("[2] Reserve");
        System.out.println("[4] Check Out");
        System.out.println("[5] Logout");
    }

    public void promptOptionReservedStillSitting() {
        System.out.println("\nCommands: ");
        System.out.println("[3] Cancel Reservation");
        System.out.println("[4] Check Out");
        System.out.println("[5] Logout");
    }

    public void promptNoRecommendedResult() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Queue");
        System.out.println("[2] Leave");
    }

    public void promptHasRecommendedResult() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Queue");
        System.out.println("[2] Walk in with recommended arrangement");
        System.out.println("[3] Leave");
    }

    public void promptWalkInLeave() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Walk in");
        System.out.println("[2] Leave");
    }

    public void promptConfirmOrder() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Yes: Please input 'True'/'true'/'TRUE'");
        System.out.println("[2] No: Please input 'False'/'false'/'FALSE'");
    }

    public void promptEditOrder() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Add Order");
        System.out.println("[2] Delete Order");
        System.out.println("[3] Confirm Order");
    }
}
