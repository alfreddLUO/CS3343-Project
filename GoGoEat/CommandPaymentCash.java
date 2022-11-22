package GoGoEat;

public class CommandPaymentCash extends CommandPayment {

    public CommandPaymentCash(Payment payment, double discountPrice, Customers customer) {
        super(payment, discountPrice);
    }

    @Override
    public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
            ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {

        PayFactory payFactory = new CashFactory();
        PaymentMethod paymentMethod = payFactory.getPay();

        boolean result = paymentMethod.pay(discountPrice);

        if (result) {
            payment.setPaymentStatus(result);
        }

        // Input merchantID to proceed payment
        selectMerchantToPayment();
    }

    private void selectMerchantToPayment() {

        // Print list of merchant of the chosen restaurant
        AccountManagement.printMerchantOfTheRestaurant(payment.getRestaurantChosed());

        System.out.print("\nInput staff MId: ");
        String staffUserName = Main.in.next("\nInput staff MId: ");

        Merchants merchant = null;
        try {
            // Match MID to get merchant instance
            merchant = database.matchMId(staffUserName);

            if (merchant.getRestaurantOwned() == payment.getRestaurantChosed()) {

                // Check out by the merchant
                merchant.checkOutbyMerchant(payment.getCustomer());

            } else {
                System.out.println("No merchant found! Please try again.");
            }
        } catch (NullPointerException ex) {
            System.out.println("No merchant found! Please try again.");
            selectMerchantToPayment();
        }
    }
}
