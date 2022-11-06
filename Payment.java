public class Payment {

    private Customers customer;
    private PaymentMethod paymentMethod;
    private double originalPrice = 0;
    private double discountPrice = 0;
    private boolean paymentStatus = false;

    public Payment(Customers customer) {
        this.customer = customer;
    }

    // Get the amount to pay from restaurant countPrice with different VIP state
    public double getPrice() {

        originalPrice = customer.getRestaurantChosed().countPrice(customer.customerOrders());

        customer.getMembership().updateState(originalPrice);

        discountPrice = customer.customerState.priceCount(originalPrice);

        customer.setBillAmount(discountPrice);

        return discountPrice;
    }

    // Choose payment method, selected -> paythebill()
    public void payProcess() {
        paymentMethod = null;

        getPrice();

        while (paymentMethod == null && paymentStatus == false) {

            int choice;

            if (discountPrice > 0) {
                do {
                    System.out.printf("\nYour bill number is: %s\n", customer.printBillNo());

                    System.out.println("\nCommands: ");
                    System.out.println("[1] Alipay");
                    System.out.println("[2] WeChat Pay");
                    System.out.println("[3] Cash");

                    System.out.print("\nPlease select your Payment Method: ");
                    String input = Main.in.next("Input: ");
                    choice = Integer.parseInt(input);

                    if (choice == 1) {
                        checkOutbyCustomer(new PayAlipay());
                    } else if (choice == 2) {
                        checkOutbyCustomer(new PayWechat());
                    } else if (choice == 3) {

                        System.out.println("\nPlease go to the counter to proceed payment.");

                        AccountManagement am = AccountManagement.getInstance();
                        am.printAllMerchantActiveAccounts();

                        System.out.print("\nInput staff MId: ");
                        input = Main.in.next("\nInput: ");
                        String staffUserName = input;

                        Merchants merchant = Main.matchMId(staffUserName);
                        merchant.checkOutbyMerchant(this, customer);

                    } else {
                        System.out.println("\nInvalid Payment method, please try again.");
                    }
                } while (choice != 1 && choice != 2 && choice != 3 || this.paymentStatus == false);
            }
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

        if (paymentStatus == true) {
            System.out.println("\nYou have completed payment with " + paymentMethod.toString() + ". Thank you!");
        }
    }
}
