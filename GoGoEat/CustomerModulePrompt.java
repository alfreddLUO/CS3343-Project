package GoGoEat;

class CustomerModulePrompt implements AbstractModulePrompt {
    private CustomerModule cm;

    // constructor
    public CustomerModulePrompt(CustomerModule cm) {
        this.cm = cm;
    }

    // customer module
    public void promptOptionStart() {

        System.out.print("\n--------------------------------------------------");

        // Check if customer is sit-down

        // Not Sit down
        if (cm.isSitDown()) {

            // Check if customer has reserved
            if (cm.checkisReserved()) {

                // reserved -> print reservation info
                System.out.println(cm.getReserveReminder());
                promptOptionReserved();

            } else {
                // Not reserved -> Get Customer's choice to dine or reserve
                promptOptionNotReserved();
            }
        } else {
            // Already Sit down

            // Reserved
            if (cm.checkisReserved()) {

                System.out.println(cm.getReserveReminder());
                promptOptionReservedStillSitting();

            } else {
                // Not reserved (customer dining & haven't get up from seat)
                promptOptionNotReservedStillSitting();
            }

        }
    }

    public static void promptOptionReserved() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Dine in");
        System.out.println("[3] Cancel Reservation");
        System.out.println("[5] Logout");
    }

    public static void promptOptionNotReserved() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Dine in");
        System.out.println("[2] Reserve");
        System.out.println("[5] Logout");
    }

    public static void promptOptionNotReservedStillSitting() {
        System.out.println("\nCommands: ");
        System.out.println("[2] Reserve");
        System.out.println("[4] Check Out");
        System.out.println("[5] Logout");
    }

    public static void promptOptionReservedStillSitting() {
        System.out.println("\nCommands: ");
        System.out.println("[3] Cancel Reservation");
        System.out.println("[4] Check Out");
        System.out.println("[5] Logout");
    }

    public static void promptNoRecommendedResult() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Queue");
        System.out.println("[2] Leave");
    }

    public static void promptHasRecommendedResult() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Queue");
        System.out.println("[2] Walk in with recommended arrangement");
        System.out.println("[3] Leave");
    }

    public static void promptWalkInLeave() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Walk in");
        System.out.println("[2] Leave");
    }

    public static void promptConfirmOrder() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Yes: Please input 'True'/'true'/'TRUE'");
        System.out.println("[2] No: Please input 'False'/'false'/'FALSE'");
    }

    public static void promptEditOrder() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Add Order");
        System.out.println("[2] Delete Order");
        System.out.println("[3] Confirm Order");
    }
}
