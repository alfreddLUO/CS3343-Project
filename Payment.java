public class Payment {

    private Customers customer;
    private static final Database database = Database.getInstance();
    private PaymentMethod paymentMethod;
    private boolean paymentStatus = false;
    private Restaurants restaurantChosed = null;

    private double originalPrice = 0;
    private double discountPrice = 0;

    public Payment(Customers customer, Restaurants restaurant) {
        this.customer = customer;
        this.restaurantChosed = restaurant;
    }

    public void promptPaymentMethod() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Alipay");
        System.out.println("[2] WeChat Pay");
        System.out.println("[3] Cash");
    }

    // Get the amount to pay from restaurant countPrice with different VIP state
    public double getPrice() {

        originalPrice = customer.getRestaurantChosed()
                .countPrice(customer.customerOrdersAccordingToRestaurant(this.restaurantChosed));

        customer.getMembership().updateState(originalPrice);

        discountPrice = customer.customerState.priceCount(originalPrice);

        customer.setBillAmount(discountPrice);

        return discountPrice;
    }

    // Choose payment method, selected -> paythebill()
    public void payProcess() {
        paymentMethod = null;
        getPrice();

        while (paymentMethod == null && !paymentStatus) {

            if (discountPrice > 0) {
                int choice = -1;
                System.out.printf("\nYour bill number is: %s\n", customer.printBillNo());
                do {

                    promptPaymentMethod();

                    System.out.print("\nPlease select your Payment Method: ");
                    try {
                        String input = Main.in.next("Input: ");
                        choice = Integer.parseInt(input);
                    } catch (NumberFormatException ex) {
                        System.out.println("Error! Please input an integer!");
                    }

                    if (choice != -1) {
                        if (choice == 1) {
                            checkOutbyCustomer(new PayAlipay());
                        } else if (choice == 2) {
                            checkOutbyCustomer(new PayWechat());
                        } else if (choice == 3) {
                            System.out.println("\nPlease go to the counter to proceed payment.");
                            selectMerchantToPayment();
                        } else {
                            System.out.println("\nInvalid Payment method, please try again.");
                        }
                    }

                } while (choice != 1 && choice != 2 && choice != 3 || this.paymentStatus == false);
            }
        }

    }

    public void selectMerchantToPayment() {
        AccountManagement accountManager = AccountManagement.getInstance();
        accountManager.printMerchantOfTheRestaurant(restaurantChosed);

        System.out.print("\nInput staff MId: ");
        String staffUserName = Main.in.next("\nInput: ");

        Merchants merchant = null;
        try {
            merchant = database.matchMId(staffUserName);
            if (merchant.getRestaurantOwned() == restaurantChosed) {
                merchant.checkOutbyMerchant(this, customer);
            } else {
                System.out.println("No merchant found! Please try again.");
            }
        } catch (NullPointerException ex) {
            System.out.println("No merchant found! Please try again.");
        } finally {

        }
    }

    // Customer use their own payment method
    public void checkOutbyCustomer(PaymentMethod paymentMethod) {
        // Choose their payment method
        this.paymentMethod = paymentMethod;
        paythebill(discountPrice, paymentMethod);
    }

    // customer pay the bill
    public void paythebill(double price, PaymentMethod paymentMethod) {

        this.paymentStatus = paymentMethod.pay(price);

        if (paymentStatus) {
            System.out.println("\nYou have completed payment with " + paymentMethod.toString() + ". Thank you!");
        }
    }
}
