package GoGoEat;

// WHAT IS THIS FOR?
public class MerchantTypeError implements Commands {
    private static final Database database = Database.getInstance();
    
    public MerchantTypeError(){
        
    }
    @Override
    public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist {
        System.out.print("\nPlease select your operations: ");
        try {
            String input = Main.in.next("\nPlease select your operations: ");
            int select = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Error! Wrong input for selection! Please input an integer!");
        }
    }
}
