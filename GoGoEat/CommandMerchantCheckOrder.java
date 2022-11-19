package GoGoEat;

public class CommandMerchantCheckOrder implements Commands{
    private static final Database database = Database.getInstance();
    private Merchants merchant;
    public CommandMerchantCheckOrder(Merchants merchant){
        this.merchant=merchant;
    }
    @Override
    public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist, ExCustomersIdNotFound {
        System.out.print("\nPlease input customer's id: ");

        String CId = Main.in.next("\nPlease input customer's id: ");

        try {
            Customers customer = database.matchCId(CId);
            merchant.checkOrder(customer, merchant.getRestaurantOwned());
        } catch (NullPointerException ex) {
            System.out.println("No Customer instance found!");
        }
        
    }
    
}
