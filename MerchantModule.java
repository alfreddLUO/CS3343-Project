
public class MerchantModule implements UserModule {

    /*
     * RunDown：
     * 1. Edit Menu
     * 1.1 Add Dish
     * 1.2 Remove Dish
     * 1.3 Edit Dish name / price
     *
     * 2. Check Customer's Order
     * 2.1 Check Order
     * 2.2 payment by Cash
     */
    private MerchantModule() {
    }

    private static final MerchantModule instance = new MerchantModule();

    public static MerchantModule getInstance() {
        return instance;
    }

    public void promptOptionStart() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Modify Menu");
        System.out.println("[2] Check Order");
        System.out.println("[3] Logout");
    }

    @Override
    public void run(String Id) {

        Merchants merchant = database.matchMId(Id);
        int select = 0;
        String input = "";

        while (select != 3) {
            promptOptionStart();

            System.out.print("\nPlease select your operations: ");
            try {
                input = Main.in.next("\nInput: ");
                select = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Error! Please input an integer!");
            }

            String CId = "";
            if (select == 1) {
                merchant.modifyMenu();
            } else if (select == 2) {

                System.out.print("\nPlease input customer's id: ");

                CId = Main.in.next("Input: ");

                try {
                    Customers customer = database.matchCId(CId);
                    merchant.checkOrder(customer, merchant.getRestaurantOwned());
                } catch (NullPointerException ex) {
                    System.out.println("No Customer instance found!");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

    }

}
