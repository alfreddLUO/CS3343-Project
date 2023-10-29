package GoGoEat;

public class AccountModule {
    private static final AccountManagement accManager = AccountManagement.getInstance();
    private AccountModulePrompt prompts = new AccountModulePrompt();

    private AccountModule() {
    }

    private static final AccountModule instance = new AccountModule();

    public static AccountModule getInstance() {
        return instance;
    }

    public void run() throws ExTableNotExist, ExTimeSlotAlreadyBeReserved, ExTimeSlotNotReservedYet,
            ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExCustomersIdNotFound {
        int select = 0;
        boolean success = false;

        String input = null;

        do {
            // Print all active accounts
            accManager.printAllActiveAccounts();

            try {

                prompts.promptOptionStart();

                System.out.print("\nPlease select your operation: ");
                input = Main.in.next("\nPlease select your operation: ");
                select = Integer.parseInt(input);

                if (select == 1) {
                    Commands cmd1 = new CommandAccountManagementLogin();
                    accManager.setCommand(cmd1);
                    accManager.callCommand();
                } else if (select == 2) {
                    // register -> Come back here after successful registration
                    Commands cmd2 = new CommandAccountManagementRegister();
                    accManager.setCommand(cmd2);
                    accManager.callCommand();
                    success = ((CommandAccountManagement) cmd2).ifSuccess();

                } else if (select == 3) {
                    Commands cmd3 = new CommandAccountManagementDelete();
                    accManager.setCommand(cmd3);
                    accManager.callCommand();
                    success = ((CommandAccountManagement) cmd3).ifSuccess();
                }
            } catch (NumberFormatException e) {
                System.out.println("Error! Wrong input for selection! Please input an integer!");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } while (select != 1 && select != 3 || !success);
    }
}
