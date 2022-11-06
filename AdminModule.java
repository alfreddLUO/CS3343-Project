public class AdminModule {

    /*
     * Admin的流程操作
     * 1. 增加餐廳
     * 2. 刪除餐廳
     * 3. 修改時間
     * 4. 返回到Main
     */

    public static void run() {
        int select = 0;
        String input = "";

        System.out.println(
                "\nSelect your operation [1 Add Restaurant | 2 Remove Restaurant | 3 Modify Time | 4 Exit]");

        do {
            input = Main.in.next("Input: ");
            select = Integer.parseInt(input);

            Restaurants temp = null;
            showListOfRestaurants();

            if (select == 1) {

                temp = addNewRestaurant();
                Admin.addRestaurant(temp);

                showListOfRestaurants();

            } else if (select == 2) {

                temp = removeRestaurant();
                Admin.deleteRestaurant(temp);

                showListOfRestaurants();

            } else if (select == 3) {

                modifyTime();

            } else if (select == 4) {

                // TODO: Goto Main

            }
        } while (select != 1 && select != 2 && select != 3 && select != 4);
    }

    public static Restaurants addNewRestaurant() {

        System.out.println("\nPlease input the name of the new Restaurant: ");
        String input = Main.in.nextLine("\nInput:");
        String rName = input;

        return new Restaurants(rName);
    }

    public static Restaurants removeRestaurant() {

        String rName = "";
        Restaurants restaurant = null;

        do {
            System.out.println("\nPlease input the name of the Restaurant to remove: ");
            String input = Main.in.nextLine("\nInput: ");
            rName = input;

            restaurant = Main.matchRestaurant(rName);
        } while (restaurant == null);

        return restaurant;
    }

    public static void showListOfRestaurants() {
        System.out.println("\nList of Restaurants: ");

        for (int i = 0; i < Main.listOfRestaurants.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + Main.listOfRestaurants.get(i).toString());
        }
    }

    // TODO: Modify Time
    public static void modifyTime() {

    }
}
