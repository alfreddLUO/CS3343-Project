package GoGoEat;

public class CustomerModule implements UserModule {
    /*
     * RunDown：
     * 1. Choose Dine-in / reserve after login
     * 1.1 Dine-in -> Check if seat available
     * 1.2 Reserve -> Check available time slot
     *
     * 2. Order
     * 2.1 Show restaurant list
     * 2.2 Choose restaurant
     * 2.3 Show menu of the restaurant choosed
     * 2.4 Choose dish from menu (Can select multiple dishes)
     *
     * 2.5 Confirm Order
     * 2.5.1 Cancel -> Add Dish
     * 2.5.2 Cancel -> Delete Dish
     * 2.5.3 Confirm (Add pendingOrder into orders array in Customers)
     *
     * 2.6 Payment (in customers.checkout())
     * 2.6.1 Show order
     * 2.6.2 Calculate total price (According to discount of VIP/SuperVIP)
     * 2.6.3 Choose payment method
     * 2.6.4 Goto payment platform to finish payment
     */

    private CustomerModule() {
    }

    private static final CustomerModule instance = new CustomerModule();

    public static CustomerModule getInstance() {
        return instance;
    }

    private static Customers customer = null;

    @Override
    public void run(String Id) {

        try {
            // Customer run customerModule
            customer = Database.getInstance().matchCId(Id);

            CustomerModulePrompt prompt = new CustomerModulePrompt(this);
            String input = "";
            int select = 0;

            // if customerState -> calculate total consumption -> disable below line：
            customer.clearOrderNPrice();

            while (select != 5) {

                prompt.promptOptionStart();

                System.out.print("\nPlease select your operation: ");
                try {
                    input = Main.in.next("\nPlease select your operation: ");
                    select = Integer.parseInt(input);

                    switch (select) {
                        case 1:
                            // exe when not reserved or reserved and not reserved time
                            if (!isSitDown()) {
                                Commands cmd1 = new CommandCustomerDineIn(customer);
                                customer.setCommand(cmd1);
                                customer.callCommand();
                            } else {
                                System.out.println("Invalid Operation! Please choose from the operation list.");
                            }
                            break;
                        case 2:
                            if (!checkisReserved()) {
                                Commands cmd2 = new CommandCustomerReservation(customer);
                                customer.setCommand(cmd2);
                                customer.callCommand();
                            } else {
                                System.out.println("Invalid Operation! Please choose from the operation list.");
                            }
                            break;
                        case 3:
                            if (checkisReserved()) {
                                Commands cmd3 = new CommandCustomerCancelReservation(customer);
                                customer.setCommand(cmd3);
                                customer.callCommand();
                            } else {
                                System.out.println("Invalid Operation! Please choose from the operation list.");
                            }

                            break;
                        case 4:
                            if (isSitDown()) {
                                String outputString = "You have successfully check out.";
                                Commands cmd4 = new CommandCustomerCheckOut(customer, outputString);
                                customer.setCommand(cmd4);
                                customer.callCommand();
                                select = 0;
                            } else {
                                System.out.println("Invalid Operation! Please choose from the operation list.");
                            }
                            break;
                        case 5:
                            break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error! Wrong input for selection! Please input an integer!");
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public boolean isSitDown() {
        return !customer.getOccupiedTableId().isEmpty();
    }

    public boolean checkisReserved() {
        return customer.checkisReserved();
    }

    public String getReserveReminder() {
        return customer.getReserveReminder();
    }
}