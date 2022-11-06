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

            System.out.print("\nAdd or Delete Dish from Menu? [1 Add | 2 Delete | 3 Edit | 4 Cancel]: ");
            String input = Main.in.next("Input: ");
            select = Integer.parseInt(input);

            if (select == 1 || select == 2 || select == 3) {
                getMenu();
            }

            if (select == 1) {
                addDish();
            } else if (select == 2) {
                removeDish();
            } else if (select == 3) {
                editDish();
            } else if (select == 4) {
                MerchantModule.run(this);
            } else {
                continue;
            }
        }
    }

    // 修改菜品名字或價錢
    public void editDish() {

        String dishName;
        int temp = 0;

        System.out.print("\nPlease input the name of the dish: ");
        String input = Main.in.nextLine("Input: ");
        dishName = input;

        // TODO: try catch and reinput
        Dish dishToEdit = null;

        try {
            dishToEdit = findDish(dishName);
        } catch (NullPointerException e) {
            System.out.println("Dish Name not found! Please try again.");

        }

        System.out.print("\nEdit dish name or dish price? [1 Name | 2 Price | 3 Cancel]: ");
        input = Main.in.next("Input: ");
        temp = Integer.parseInt(input);

        if (temp == 1) {
            editDishName(dishToEdit);
        } else if (temp == 2) {
            editDishPrice(dishToEdit);
        } else {
        }
    }

    // 增加新菜品到餐廳餐單
    public void addDish() {
        String dishName;
        double dishPrice;
        String input = "";

        System.out.println("\nPlease input the name and price of the dish to add: ");

        System.out.print("\nDish Name: ");
        dishName = Main.in.nextLine("Input: ");

        System.out.print("\nDish Price: ");
        input = Main.in.next("Input: ");
        dishPrice = Double.parseDouble(input);

        restaurantOwned.getMenu().add(new Dish(dishName, dishPrice));
    }

    // 從餐單刪除菜品
    public void removeDish() {
        String dishName;

        System.out.print("\nPlease input the name of the dish to remove: ");
        dishName = Main.in.nextLine("Input: ");

        for (int i = 0; i < restaurantOwned.getMenu().size(); i++) {
            if (restaurantOwned.getMenu().get(i).toString().equals(dishName)) {
                removefromMenu(restaurantOwned.getMenu().get(i));
            }
        }
    }

    public void addtoMenu(String dishName, double dishPrice) {
        Dish temp = new Dish(dishName, dishPrice);
        restaurantOwned.adddishtoMenu(temp);
    }

    public void removefromMenu(Dish dish) {
        restaurantOwned.deletedishfromMenu(dish);
    }

    public void editDishName(Dish dishToEdit) {
        System.out.print("\nInput the new name: ");
        String name = Main.in.nextLine("\nInput the new name: ");
        dishToEdit.setDishName(name);
    }

    public void editDishPrice(Dish dishToEdit) {
        System.out.print("\nInput the new price: ");
        String input = Main.in.next("\nInput the new price: ");
        double newPrice = Double.parseDouble(input);

        dishToEdit.setDishPrice(newPrice);
    }

    // 從餐廳中提取菜單（用菜名尋找）
    public Dish findDish(String dishName) {
        for (int i = 0; i < restaurantOwned.getMenu().size(); i++) {
            if (restaurantOwned.getMenu().get(i).toString().equals(dishName)) {
                return restaurantOwned.getMenu().get(i);
            }
        }
        return null;
    }

    public void getMenu() {
        for (int i = 0; i < restaurantOwned.getMenu().size(); i++) {
            System.out.println((i + 1) + " " + restaurantOwned.getMenu().get(i).toString());
        }
    }

    // Payment
    public void checkOutbyMerchant(Payment payment, Customers customer) {

        // Show customers' official order
        checkOrder(customer);
        payment.paythebill(customer.getBillAmount(), new PayCash());

    }

    // Payment
    public void checkOrder(Customers customer) {

        System.out.println("\nCustomer Name: " + customer.getUsername());
        System.out.println("Customer ID: " + customer.getID());
        System.out.println("Bill no. is: " + customer.printBillNo());

        System.out.println("\nOrdered Dishes: ");
        for (int i = 0; i < customer.customerOrders().size(); i++) {
            System.out.println("[" + (i + 1) + "] " + customer.customerOrders().get(i).toString());
        }

    }

}