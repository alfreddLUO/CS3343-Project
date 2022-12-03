package GoGoEat;

import java.util.InputMismatchException;

public class CommandAccountManagementRegister extends CommandAccountManagement {
    protected CommandAccountManagementRegister() {
        super();
    }

    @Override
    public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
            ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {
        super.success = register();
    }

    protected boolean registerCustomer() {
        String username = null, password = null;
        try {
            System.out.print("\nPlease input the username: ");
            username = Main.in.next("\nPlease input the username: ");

            System.out.print("Please input the password: ");
            password = Main.in.next("Please input the password: ");
        } catch (InputMismatchException ex) {
            System.out.println("\nError! Wrong input type!");
        }

        if (username != null && password != null) {
            if (confirmToRegister(username, password)) {
                return accManager.registerCustomer(username, password);
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    protected boolean confirmToRegister(String username, String password) {
        boolean select = false;
        String input = null;

        System.out.println("Your username will be: " + username);
        System.out.println("Your password will be: " + password);

        try {
            System.out.println("\nDo you wish to confirm and proceed registration? [true / false]");
            input = Main.in.next("\nDo you wish to confirm and proceed registration? [true / false]");
            select = Boolean.parseBoolean(input);
        } catch (InputMismatchException ex) {
            System.out.println("\nError! Wrong input type!");
        }
        return select;
    }

    protected boolean registerMerchant() {
        String username, password, rName;

        System.out.print("\nPlease input the username: ");
        username = Main.in.next("\nPlease input the username: ");

        System.out.print("Please input the password: ");
        password = Main.in.next("Please input the password: ");

        System.out.print("\nPlease input the name of the Restaurant: ");
        rName = Main.in.next("\nPlease input the name of the Restaurant: ");

        if (username != null && password != null) {
            if (confirmToRegister(username, password)) {
                if (rName != null) {
                    database.registerRestaurant(rName);
                    return accManager.registerMerchant(username, password, database.matchRestaurant(rName));
                }
            }
        }
        return false;
    }

    protected boolean register() {

        String input = "";
        int select = 0;
        boolean registerFinished = false;

        // 1: Register Customer Account
        // 2: Register Merchant Account (And register Restaurant at the same time)
        do {
            try {
                System.out.print(
                        "\nPlease choose the type of account to register [1 Customer | 2 Merchant | 3 Cancel]: ");
                input = Main.in
                        .next("\nPlease choose the type of account to register [1 Customer | 2 Merchant | 3 Cancel]: ");
                select = Integer.parseInt(input);
                if (select == 1) {
                    registerFinished = registerCustomer();
                } else if (select == 2) {
                    registerFinished = registerMerchant();
                } else if (select == 3) {
                    break;
                }
            } catch (InputMismatchException ex) {
                System.out.println("\nError! Please input an integer of choice!");
            } catch (NumberFormatException e) {
                System.out.println("Error! Wrong input for selection! Please input an integer!");
            }

        } while (select != 1 && select != 2 || !registerFinished);

        if (registerFinished) {
            System.out.println("\nRegistration Completed. Please return to login.");
        }
        return registerFinished;
    }

}
