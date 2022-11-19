package GoGoEat;

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

    private MerchantModulePrompt prompt = new MerchantModulePrompt();

    public static MerchantModule getInstance() {
        return instance;
    }

    @Override
    public void run(String Id) throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
            ExTableIdAlreadyInUse, ExTableNotExist,
            ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {

        Merchants merchant = database.matchMId(Id);
        int select = 0;
        String input = "";

        while (select != 3) {
            prompt.promptOptionStart();

            System.out.print("\nPlease select your operations: ");

            try {
                input = Main.in.next("\nPlease select your operations: ");
                select = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Error! Wrong input for selection! Please input an integer!");
            }

            switch (select) {
                case 1:
                    Commands cmd1 = new CommandMerchantModifyMenu(merchant);
                    merchant.setCommand(cmd1);
                    merchant.callCommand();
                    break;
                case 2:
                    Commands cmd2 = new CommandMerchantCheckOrder(merchant);
                    merchant.setCommand(cmd2);
                    merchant.callCommand();
                    break;
                case 3:
                    break;
            }
        }

    }

}
