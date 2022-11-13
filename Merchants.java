import java.util.ArrayList;

public class Merchants implements UserType {

    private String username;
    private String staffUserId;
    private Restaurants restaurantOwned;

    public Merchants(String username, String merchantId, Restaurants restaurant) {
        this.username = username;
        this.staffUserId = merchantId;
        this.restaurantOwned = restaurant;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getUserId() {
        return staffUserId;
    }

    public void promptModifyMenu() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Add Dish");
        System.out.println("[2] Delete Dish");
        System.out.println("[3] Edit Dish");
        System.out.println("[4] Cancel");
    }

    // 增加或刪除菜品
    public void modifyMenu() throws ExWrongSelectionNum {

        int select = 0;

        /*
         * 1 Add Dish to Menu
         * 2 Delete Dish from Menu
         * 3 Edit name or price of the Dish in the Menu
         * 4 Cancel Operation (Do nothing)
         */

        while (select != 1 && select != 2 && select != 3) {

            promptModifyMenu();

            System.out.print("\nPlease select your operations: ");

            String input = Main.in.next("Input: ");
            select = Integer.parseInt(input);

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
    public void editDish() throws ExWrongSelectionNum {

        String dishName;
        int temp = 0;

        System.out.print("\nPlease input the name of the dish: ");
        String input = Main.in.nextLine("Input: ");
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
            input = Main.in.next("Input: ");
            temp = Integer.parseInt(input);

            switch (temp) {
                case 1 -> editDishName(dishToEdit);
                case 2 -> editDishPrice(dishToEdit);
            }

        }

    }

    // add dish to menu
    public void addDish() throws ExWrongSelectionNum {
        String dishName;
        double dishPrice = -1;
        String input = "";

        System.out.println("\nPlease input the name and price of the dish to add: ");

        System.out.print("Dish Name: ");
        dishName = Main.in.nextLine("Input: ");

        System.out.print("Dish Price: ");

        input = Main.in.next("Input: ");
        dishPrice = Double.parseDouble(input);

        if (dishPrice != -1) {
            restaurantOwned.getMenu().add(new Dish(dishName, dishPrice));

            System.out.println("Add Dish success.");
        } else {
            System.out.println("Add Dish FAILED.");
        }

    }

    // delete dish from menu
    public void removeDish() {
        String dishName;
        boolean removeSuccess = false;

        System.out.print("\nPlease input the name of the dish to remove: ");
        dishName = Main.in.nextLine("Input: ");

        for (int i = 0; i < restaurantOwned.getMenu().size(); i++) {
            if (restaurantOwned.getMenu().get(i).toString().equals(dishName)) {
                removefromMenu(restaurantOwned.getMenu().get(i));
                removeSuccess = true;
            }
        }
        if (removeSuccess) {
            System.out.println("Remove dish success.");
        } else {
            System.out.println("Remove dish FAILED!");
        }
    }

    public void addtoMenu(String dishName, double dishPrice) {
        Dish temp = new Dish(dishName, dishPrice);
        restaurantOwned.adddishtoMenu(temp);
    }

    public void removefromMenu(Dish dish) {
        boolean success = restaurantOwned.deletedishfromMenu(dish);
        if (success) {
            System.out.println("Delete dish success.");
        } else {
            System.out.println("Delete FAILED!");
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
    public void checkOutbyMerchant(Payment payment, Customers customer) {

        // Show customers' official order
        checkOrder(customer, this.restaurantOwned);
        payment.paythebill(customer.getBillAmount(), new PayCash());

    }

    // Before Payment, check order by merchant
    public void checkOrder(Customers customer, Restaurants restaurant) {

        System.out.println("\nCustomer Name: " + customer.getUsername());
        System.out.println("Customer ID: " + customer.getID());
        System.out.println("Bill no. is: " + customer.printBillNo());

        System.out.println("\nOrdered Dishes: ");
        ArrayList<Dish> customerOrder = customer.customerOrdersAccordingToRestaurant(restaurant);

        if (customerOrder.isEmpty()) {
            System.out.println("This customer has no orders.");
        } else {
            for (int i = 0; i < customerOrder.size(); i++) {
                System.out.println("[" + (i + 1) + "] " + customerOrder.get(i).toString());
            }
        }

    }

    public Restaurants getRestaurantOwned() {
        return this.restaurantOwned;
    }

}