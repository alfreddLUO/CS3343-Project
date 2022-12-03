package GoGoEat;

public class CommandMerchantCheckOrder extends CommandMerchant {

    protected CommandMerchantCheckOrder(Merchants merchant) {
        super(merchant);
    }

    @Override
    public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist, ExCustomersIdNotFound {

        // Check order by passing in customerID
        System.out.print("\nPlease input customer's id: ");
        String CId = Main.in.next("\nPlease input customer's id: ");

        try {
            // Match CID to get instance
            Customers customer = database.matchCId(CId);
            merchant.checkOrder(customer, merchant.getRestaurantOwned());

        } catch (ExCustomersIdNotFound e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException ex) {
            System.out.println("No Customer instance found!");
        }

    }

}
