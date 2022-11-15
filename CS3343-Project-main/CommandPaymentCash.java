public class CommandPaymentCash implements Commands {
    private double discountPrice;
    private Payment payment;
    private AccountManagement accountManager = AccountManagement.getInstance();
    private static final Database database = Database.getInstance();
    private Restaurants restaurantChosed;
    private Customers customer;

    public CommandPaymentCash(Payment payment, double discountPrice, Restaurants restaurantChosed, Customers customer) {
        this.payment = payment;
        this.discountPrice = discountPrice;
        this.restaurantChosed = restaurantChosed;
        this.customer = customer;
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
        selectMerchantToPayment();

    }

    public void selectMerchantToPayment() {

        accountManager.printMerchantOfTheRestaurant(restaurantChosed);

        System.out.print("\nInput staff MId: ");
        String staffUserName = Main.in.next("\nInput staff MId: ");

        Merchants merchant = null;
        try {
            merchant = database.matchMId(staffUserName);
            if (merchant.getRestaurantOwned() == restaurantChosed) {
                merchant.checkOutbyMerchant(customer);
            } else {
                System.out.println("No merchant found! Please try again.");
            }
        } catch (NullPointerException ex) {
            System.out.println("No merchant found! Please try again.");
        } finally {

        }
    }
}
