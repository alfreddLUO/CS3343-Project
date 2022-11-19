package GoGoEat;

public class CommandMerchantModifyMenu implements Commands {

    private Merchants merchant = null;

    public CommandMerchantModifyMenu(Merchants merchant2) {
        this.merchant = merchant2;
    }

    @Override
    public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist {

        /*
         * 1 Add Dish to Menu
         * 2 Delete Dish from Menu
         * 3 Edit name or price of the Dish in the Menu
         * 4 Cancel Operation (Do nothing)
         */

        int select = 0;
        while (select != 1 && select != 2 && select != 3) {

            MerchantModulePrompt.promptModifyMenu();

            System.out.print("\nPlease select your operations: ");

            try {
                String input = Main.in.next("\nPlease select your operations: ");
                select = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Error! Wrong input for selection! Please input an integer!");
            }

            if (select == 4) {
                break;
            } else if (select == 1 || select == 2 || select == 3) {
                merchant.getMenu();
                if (select == 1) {
                    merchant.addDish();
                } else if (select == 2) {
                    merchant.removeDish();
                } else if (select == 3) {
                    merchant.editDish();
                }
            } else {
                continue;
            }
        }

    }

}
