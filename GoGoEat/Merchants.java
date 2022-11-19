package GoGoEat;

import java.util.ArrayList;
import java.util.Collections;

public class Merchants implements UserType {

    private String username;
    private String staffUserId;
    private Restaurants restaurantOwned;
    private Commands command;
    private MerchantModulePromptions promptions = new MerchantModulePromptions();

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

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getUserId() {
        return staffUserId;
    }

    // 增加或刪除菜品
    public void modifyMenu() {

        int select = 0;

        /*
         * 1 Add Dish to Menu
         * 2 Delete Dish from Menu
         * 3 Edit name or price of the Dish in the Menu
         * 4 Cancel Operation (Do nothing)
         */

        while (select != 1 && select != 2 && select != 3) {

            promptions.promptModifyMenu();

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
                getMenu();
                if (select == 1) {
                    addDish();
                } else if (select == 2) {
                    removeDish();
                } else if (select == 3) {
                    editDish();
                }
            } else {
                continue;
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
            System.out.println("\nCommands: ");
            System.out.println("[1] Edit Dish Name");
            System.out.println("[2] Edit Dish Price");
            System.out.println("[3] Cancel");

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

    // add dish to menu
    public void addDish() {
        String dishName;
        double dishPrice = -1;
        String input = "";

        System.out.println("\nPlease input the name and price of the dish to add: ");

        System.out.print("Dish Name: ");
        dishName = Main.in.nextLine("Dish Name: ");

        System.out.print("Dish Price: ");

        input = Main.in.next("Dish Price: ");
        dishPrice = Double.parseDouble(input);
        
        addtoMenu(dishName, dishPrice);
    }

    // delete dish from menu
    // MODIFIED 19 Nov
    // TODO: Delete according to index not name
    public void removeDish() {
        String dishName;
        System.out.print("\nPlease input the name of the dish to remove: ");
        //dishName = Main.in.nextLine("\nPlease input the name of the dish to remove: ");
 
        // TODO: added
        dishName = Main.in.nextLine("\nPlease input the name of the dish to remove: "); // input multiple dishes
        String[] tokens = dishName.split(",");
        ArrayList<Integer> idx = new ArrayList<>();

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
        System.out.println("Delete Dish success");
        
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

    // Extract menu from restaurant by using dishName
    public Dish findDish(String dishName) {
        return restaurantOwned.getDishbyName(dishName);
    }

    // Print all dish from restaurant's menu
    public void getMenu() {
        restaurantOwned.printMenu();
    }

    // Payment by merchant
    public void checkOutbyMerchant(Customers customer) {

        checkOrder(customer, this.restaurantOwned);
        System.out.println("\nYou have completed payment with cash. Thank you!");

    }

    // Before Payment, check order by merchant
    // TODO: modified 18 Nov 00:21
    public void checkOrder(Customers customer, Restaurants restaurant) {

        System.out.println("\nCustomer Name: " + customer.getUsername());
        System.out.println("Customer ID: " + customer.getID());
        if (!customer.getBillNumberToRestaurant(restaurantOwned).isEmpty()) {
            String number = customer.getBillNumberToRestaurant(restaurantOwned).keySet().toString();
            System.out.println("Bill no. is/are: " + number);
        }

        ArrayList<Dish> customerOrder = customer.customerOrdersAccordingToRestaurant(restaurant);
        if (customerOrder != null) {
            System.out.println("\nOrdered Dishes: ");
            for (int i = 0; i < customerOrder.size(); i++) {
                System.out.println("[" + (i + 1) + "] " + customerOrder.get(i).toString());
            }
        } else {
            System.out.println("This customer has no orders.");
        }
    }

    public Restaurants getRestaurantOwned() {
        return this.restaurantOwned;
    }

}