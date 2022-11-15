public class Payment {

    private Customers customer;
    private static final Database database = Database.getInstance();
    private AccountManagement accountManager = AccountManagement.getInstance();
    private PaymentMethod paymentMethod;
    private boolean paymentStatus = false;
    private Restaurants restaurantChosed = null;
    private PaymentModulePromptions promptions = new PaymentModulePromptions();
    private double originalPrice = 0;
    private double discountPrice = 0;
    private Commands command;

    public Payment(Customers customer, Restaurants restaurant) {
        this.customer = customer;
        this.restaurantChosed = restaurant;
    }

    // Get the amount to pay from restaurant countPrice with different VIP state
    public double getPrice() {

        originalPrice = customer.getRestaurantChosed()
                .countPrice(customer.customerOrdersAccordingToRestaurant(this.restaurantChosed));

        customer.setBillAmount(originalPrice);
        customer.updateState();
        customer.updateStateOutput();
        discountPrice = customer.getState().priceCount(originalPrice);

        customer.setBillAmount(discountPrice);

        return discountPrice;
    }

    // Choose payment method, selected -> paythebill()
    public void payProcess() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
            ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {
        paymentMethod = null;
        getPrice();

        while (paymentMethod == null && !paymentStatus) {

            if (discountPrice > 0) {
                int choice = -1;
                System.out.printf("\nYour bill number is: %s\n", customer.printBillNo());
                do {

                    promptions.promptOptionStart();

                    System.out.print("\nPlease select your Payment Method: ");

                    String input;
                    try {
                        input = Main.in.next("\nPlease select your Payment Method: ");
                        choice = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        System.out.println("Error! Wrong input for selection! Please input an integer!");
                    }

                    if (choice != -1) {
                        if (choice == 1) {
                            Commands cmd1 = new CommandPaymentAlipay(this, discountPrice);
                            this.setCommand(cmd1);
                            this.callCommand();
                            break;
                        } else if (choice == 2) {
                            Commands cmd2 = new CommandPaymentAlipay(this, discountPrice);
                            this.setCommand(cmd2);
                            this.callCommand();
                            break;
                        } else if (choice == 3) {
                            Commands cmd3 = new CommandPaymentCash(this, discountPrice, restaurantChosed, customer);
                            this.setCommand(cmd3);
                            this.callCommand();
                            break;
                        } else {
                            System.out.println("\nInvalid Payment method, please try again.");
                        }
                    }

                } while (choice != 1 && choice != 2 && choice != 3 || this.paymentStatus == false);
            }
        }

    }

    public void setPaymentStatus(boolean status) {
        this.paymentStatus = status;
    }

    public void setCommand(Commands command) {
        this.command = command;
    }

    public void callCommand() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
            ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {
        command.exe();
    }
}
