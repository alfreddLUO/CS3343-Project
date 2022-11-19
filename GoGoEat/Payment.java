package GoGoEat;

import java.util.ArrayList;

public class Payment {

    private Customers customer;
    private PaymentMethod paymentMethod;
    private Commands command;
    private Restaurants restaurantChosed = null;
    private PaymentModulePrompt prompt = new PaymentModulePrompt();

    private boolean paymentStatus = false;
    private boolean isNoOrder = false;

    private double originalPrice = 0;
    private double discountPrice = 0;

    public Payment(Customers customer, Restaurants restaurant) {
        this.customer = customer;
        this.restaurantChosed = restaurant;
    }

    // UPDATE: Modified on 16 Nov added parameter + originalPrice modified
    public double getPrice(ArrayList<Dish> orders) {

        /*
         * 1. Calculate Original Sum
         * 2. Update Customers' bill amount -> updateState
         * 3. Use updated state to calculate discount price
         * 4. Update customers' bill amount
         */

        // Calculate original price of the orders
        originalPrice = customer.getRestaurantChosed().countPrice(orders);

        customer.setBillAmount(originalPrice);
        customer.updateState();

        if (originalPrice > 0) {
            customer.updateStateOutput();
        }

        // Updated discountprice from different VIP state
        discountPrice = customer.getState().priceCount(originalPrice);
        customer.setBillAmount(discountPrice);

        return discountPrice;
    }

    // Choose payment method, selected -> paythebill()
    public void payProcess(ArrayList<Dish> orders)
            throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
            ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {

        paymentMethod = null;
        getPrice(orders);

        if (discountPrice > 0) {
            int choice = -1;

            // Print Bill no
            System.out.printf("\nYour bill number is: %s\n", customer.printBillNo());

            do {
                prompt.promptOptionStart();

                System.out.print("\nPlease select your Payment Method: ");

                // Choose Payment method
                String input;
                try {
                    input = Main.in.next("\nPlease select your Payment Method: ");
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Error! Wrong input for selection! Please input an integer!");
                }

                // 1. Alipay 2. Wechat Pay 3. Cash
                if (choice != -1) {
                    if (choice == 1) {
                        Commands cmd1 = new CommandPaymentAlipay(this, discountPrice);
                        this.setCommand(cmd1);
                        this.callCommand();
                        break;
                    } else if (choice == 2) {
                        Commands cmd2 = new CommandPaymentWeChatPay(this, discountPrice);
                        this.setCommand(cmd2);
                        this.callCommand();
                        break;
                    } else if (choice == 3) {
                        Commands cmd3 = new CommandPaymentCash(this, discountPrice, restaurantChosed, customer);
                        this.setCommand(cmd3);
                        this.callCommand();
                        break;
                    } else {
                        System.out.println("\nInvalid Payment method, please try again.");
                    }
                }

            } while (choice != 1 && choice != 2 && choice != 3 || this.paymentStatus == false && !this.isNoOrder
                    || paymentMethod == null);

        } else {
            // UPDATE: Modified on 16 Nov
            String outputString = "\nThere is no order made. Thank you.\nYou have successfully check out.\n";
            isNoOrder = true;

            // CHECKOUT
            CommandCustomerCheckOut cmdCheckout = new CommandCustomerCheckOut(customer, outputString);
            customer.setCommand(cmdCheckout);
            customer.callCommand();
        }
    }

    public void setPaymentStatus(boolean status) {
        this.paymentStatus = status;
    }

    public void setCommand(Commands command) {
        this.command = command;
    }

    public void callCommand() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
            ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {
        command.exe();
    }
}
