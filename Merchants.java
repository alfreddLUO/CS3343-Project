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

            System.out.println("\nCommands: ");
            System.out.println("[1] Add Dish");
            System.out.println("[2] Delete Dish");
            System.out.println("[3] Edit Dish");
            System.out.println("[4] Cancel");

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
    public void editDish() {

        String dishName;
        int temp = 0;

        System.out.print("\nPlease input the name of the dish: ");
        String input = Main.in.nextLine("Input: ");
        dishName = input;

        // TODO: try catch and reinput
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

            if (temp == 4) {
                // nothing
            } else if (temp == 1) {
                editDishName(dishToEdit);
            } else if (temp == 2) {
                editDishPrice(dishToEdit);
            } else {
                // nothing
            }
        }

    }

    // add dish to menu
    public void addDish() {
        String dishName;
        double dishPrice;
        String input = "";

        System.out.println("\nPlease input the name and price of the dish to add: ");

        System.out.print("Dish Name: ");
        dishName = Main.in.nextLine("Input: ");

        System.out.print("Dish Price: ");
        input = Main.in.next("Input: ");
        dishPrice = Double.parseDouble(input);

        restaurantOwned.getMenu().add(new Dish(dishName, dishPrice));

        System.out.println("Add Dish success.");
    }

    // delete dish from menu
    public void removeDish() {
        String dishName;

        System.out.print("\nPlease input the name of the dish to remove: ");
        dishName = Main.in.nextLine("Input: ");

        boolean success = false;

        for (int i = 0; i < restaurantOwned.getMenu().size(); i++) {
            if (restaurantOwned.getMenu().get(i).toString().equals(dishName)) {
                removefromMenu(restaurantOwned.getMenu().get(i));
                success = true;
            }
        }
        if (success) {
            System.out.println("Add Dish success.");
        } else {
            System.out.println("Add dish FAILED!");
        }

    }

    public void addtoMenu(String dishName, double dishPrice) {
        Dish temp = new Dish(dishName, dishPrice);
        restaurantOwned.adddishtoMenu(temp);
    }

    public void removefromMenu(Dish dish) {
        boolean success = restaurantOwned.deletedishfromMenu(dish);
        if (success) {
            System.out.println("Delete dish success");
        } else {
            System.out.println("Delete FAILED");
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
        checkOrder(customer);
        payment.paythebill(customer.getBillAmount(), new PayCash());

    }

    // Before Payment, check order by merchant
    public void checkOrder(Customers customer) {

        System.out.println("\nCustomer Name: " + customer.getUsername());
        System.out.println("Customer ID: " + customer.getID());
        System.out.println("Bill no. is: " + customer.printBillNo());

        System.out.println("\nOrdered Dishes: ");
        ArrayList<Dish> customerOrder = customer.customerOrders();

        if (customerOrder.isEmpty()) {
            System.out.println("This customer has no orders.");
        } else {
            for (int i = 0; i < customerOrder.size(); i++) {
                System.out.println("[" + (i + 1) + "] " + customerOrder.get(i).toString());
            }
        }

    }

}