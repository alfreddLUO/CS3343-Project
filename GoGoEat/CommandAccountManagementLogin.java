package GoGoEat;

public class CommandAccountManagementLogin extends CommandAccountManagement {

    UserModule module = null;

    CommandAccountManagementLogin() {
        super();
    }

    @Override
    public void exe() throws ExTableNotExist, ExTimeSlotAlreadyBeReserved, ExTimeSlotNotReservedYet,
            ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExCustomersIdNotFound {

        super.success = login();
    }

    // Login, get userId then return boolean
    public boolean login() throws ExTableNotExist, ExTimeSlotAlreadyBeReserved, ExTimeSlotNotReservedYet,
            ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExCustomersIdNotFound {

        String input = "", username = "", password = "";

        System.out.print("\nPlease input the username: ");
        input = Main.in.next("\nPlease input the username: ");
        username = input;

        System.out.print("Please input the password: ");
        input = Main.in.next("Please input the password: ");
        password = input;

        // Get ID
        String ID = accManager.login(username, password);

        if (ID != null) {

            // Identify which userType belong to the login account, then run Module
            module = accManager.distinguishMerchantandCustomer(ID);

            if (module != null) {
                module.run(ID);
                return true;
            } else {
                System.out.println("There is some error in running corresponding module.");
                return false;
            }
        } else {
            System.out.println("\nLogin failed!");
            return false;
        }
    }

}
