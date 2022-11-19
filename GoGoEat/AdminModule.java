package GoGoEat;

public class AdminModule implements UserModule {

    private AdminModule() {
    }

    private static final AdminModule instance = new AdminModule();

    public static AdminModule getInstance() {
        return instance;
    }

    private static final Admin admin = Admin.getInstance();

    AdminModulePrompt prompt = new AdminModulePrompt();

    public void run(String Id) throws ExUnableToSetOpenCloseTime, ExTableNotExist, ExTableIdAlreadyInUse,
            ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {

        /*
         * Admin's routine
         * 1. Set food court's opening and closing time
         * 2. Check customer's orders
         * 3. Check customer's reservation status
         * 4. Add restaurant into allRestaurant list
         * 5. Remove restaurant from allRestaurant list
         * 6. Add Table
         * 7. Remove Table
         * 8. Log out
         */

        int select = 0;
        String input = "";

        while (select != 8) {

            prompt.promptOptionStart();

            try {
                System.out.print("\nPlease select your operations: ");
                input = Main.in.next("\nPlease select your operations: ");
                select = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Error! Wrong input for selection! Please input an integer!");
            }

            do {
                switch (select) {
                    case 1:
                        Commands cmd1 = new CommandAdminSetOpenCloseHour();
                        admin.setCommand(cmd1);
                        admin.callCommand();
                        break;
                    case 2:
                        Commands cmd2 = new CommandAdminCheckCustomerOrder();
                        admin.setCommand(cmd2);
                        admin.callCommand();
                        break;
                    case 3:
                        Commands cmd3 = new CommandAdminCheckReservation();
                        admin.setCommand(cmd3);
                        admin.callCommand();
                        break;
                    case 4:
                        Commands cmd4 = new CommandAdminAddRestaurant();
                        admin.setCommand(cmd4);
                        admin.callCommand();
                        break;
                    case 5:
                        Commands cmd5 = new CommandAdminRemoveRestaurant();
                        admin.setCommand(cmd5);
                        admin.callCommand();
                        break;
                    case 6:
                        Commands cmd6 = new CommandAdminAddTable();
                        admin.setCommand(cmd6);
                        admin.callCommand();
                        break;
                    case 7:
                        Commands cmd7 = new CommandAdminRemoveTable();
                        admin.setCommand(cmd7);
                        admin.callCommand();
                        break;
                    case 8:
                        break;
                }
            } while (select != 1 && select != 2 && select != 3 && select != 4 && select != 5 && select != 6
                    && select != 7 && select != 8);

        }
    }
}
