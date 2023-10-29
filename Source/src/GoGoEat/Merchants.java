package GoGoEat;

import java.util.ArrayList;
import java.util.Collections;

public class Merchants {

    private String username;
    private String staffUserId;
    private Restaurants restaurantOwned;
    private Commands command;

    public Merchants(String username, String merchantId, Restaurants restaurant) {
        this.username = username;
        this.staffUserId = merchantId;
        this.restaurantOwned = restaurant;
    }

    public void setCommand(Commands command) {
        this.command = command;
    }

    public void callCommand() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
            ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {
        command.exe();
    }

    // @Override
    public String getUsername() {
        return username;
    }

    // @Override
    public String getUserId() {
        return staffUserId;
    }

    public Restaurants getRestaurantOwned() {
        return this.restaurantOwned;
    }

    public void getMenu() {
        // Print all dish from restaurant's menu
        restaurantOwned.printMenu();
    }

    public void addDish() {
        // Add dish to menu

        String dishName;
        double dishPrice = -1;
        String input = "";

        System.out.println("\nPlease input the name and price of the dish to add: ");

        System.out.print("Dish Name: ");
        dishName = Main.in.nextLine("Dish Name: ");

        System.out.print("Dish Price: ");

        try {
            input = Main.in.next("Dish Price: ");
            dishPrice = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Please input a double.");
            addDish();
        }

        addtoMenu(dishName, dishPrice);
    }

    public void removeDish() {
        // Delete dish from menu
        if (restaurantOwned.getMenu().isEmpty() == false) {
            String dishName;
            System.out.print("\nPlease input the numbering of the dish to remove: ");

            // input multiple dishes
            dishName = Main.in.nextLine("\nPlease input the numbering of the dish to remove: ");
            String[] tokens = dishName.split(",");
            ArrayList<Integer> idx = new ArrayList<>();

            // String to integer (index)
            try {

                for (int i = 0; i < tokens.length; i++) {
                    idx.add(Integer.parseInt(tokens[i]));
                }

                // Add input to dish list pending to order
                ArrayList<Dish> menuModified = new ArrayList<>(restaurantOwned.getMenu());

                Collections.sort(idx, Collections.reverseOrder());
                for (int i : idx) {
                    menuModified.remove(i - 1);
                }

                restaurantOwned.updateMenu(menuModified);
                System.out.println("Delete Dish success.");

            } catch (NumberFormatException e) {
                System.out.println("Please input integer list!");
            }

        } else {
            System.out.println("Error! Menu is empty!");
        }

    }

    public void addtoMenu(String dishName, double dishPrice) {
        Dish temp = new Dish(dishName, dishPrice);
        boolean success = restaurantOwned.adddishtoMenu(temp);
        if (success) {
            System.out.println("Add dish success.");
        } else {
            System.out.println("Add dish failed!");
        }
    }

    public void editDishName(Dish dishToEdit) {
        System.out.print("\nInput the new name: ");
        String name = Main.in.nextLine("\nInput the new name: ");
        dishToEdit.setDishName(name);
        System.out.println("Edit Dish Name success.");
    }

    public void editDishPrice(Dish dishToEdit) {
        System.out.print("\nInput the new price: ");
        String input = Main.in.next("\nInput the new price: ");
        double newPrice = Double.parseDouble(input);

        dishToEdit.setDishPrice(newPrice);
        System.out.println("Edit Dish Price success.");
    }

    public Dish findDish(String dishName) {
        // Extract menu from restaurant by using dishName
        return restaurantOwned.getDishbyName(dishName);
    }

    public void checkOutbyMerchant(Customers customer) {
        // Payment by merchant
        checkOrder(customer, this.restaurantOwned);

    }

    public void checkOrder(Customers customer, Restaurants restaurant) {
        // Before Payment, check order by merchant

        System.out.println("\nCustomer Name: " + customer.getUsername());
        System.out.println("Customer ID: " + customer.getID());

        // Check if there is bill for this restaurant
        if (!customer.getBillNumberToRestaurant(restaurantOwned).isEmpty()) {
            String number = customer.getBillNumberToRestaurant(restaurantOwned).keySet().toString();
            System.out.println("Bill no. is/are: " + number);

            // Getting customers' orders
            ArrayList<Dish> customerOrder = customer.customerOrdersAccordingToRestaurant(restaurant);

            // Orders made
            if (customerOrder != null) {
                System.out.println("\nOrdered Dishes: ");
                for (int i = 0; i < customerOrder.size(); i++) {
                    System.out.println("[" + (i + 1) + "] " + customerOrder.get(i).toString());
                }
            } else {
                // No orders made
                System.out.println("This customer has no orders.");
            }
        }
    }

    // Edit Dish name or price
    public void editDish() {

        String dishName;
        int temp = 0;

        System.out.print("\nPlease input the name of the dish: ");
        String input = Main.in.nextLine("\nPlease input the name of the dish: ");
        dishName = input;

        Dish dishToEdit = null;

        dishToEdit = findDish(dishName);
        if (dishToEdit == null) {
            System.out.println("Wrong Dish Name! Please try again.");
        } else {
            /*
             * 1. Edit Name
             * 2. Edit Price
             */

            MerchantModulePrompt.promptEditDish();

            System.out.print("\nPlease select your operations: ");
            try {
                input = Main.in.next("\nPlease select your operations: ");
                temp = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Error! Wrong input for selection! Please input an integer!");
            }

            switch (temp) {
                case 1:
                    editDishName(dishToEdit);
                    break;
                case 2:
                    editDishPrice(dishToEdit);
                    break;
            }
        }
    }

}