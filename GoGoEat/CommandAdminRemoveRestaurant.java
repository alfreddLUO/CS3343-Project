package GoGoEat;

public class CommandAdminRemoveRestaurant extends CommandAdmin {

    CommandAdminRemoveRestaurant() {
        super();
    }

    @Override
    public void exe() {
        Restaurants temp = removeRestaurant();

        // Check if restaurant instance exist
        if (temp == null) {
            System.out.println("No such restaurant. Please check again.");
        } else {
            // exist -> delete instance from Database
            deleteRestaurant(temp);

            // Show updated list after deleting
            database.showListOfRestaurants();
        }
    }

    private Restaurants removeRestaurant() {
        database.outputRestaurant();

        String rName = "";
        Restaurants restaurant = null;

        System.out.print("\nPlease input the name of the Restaurant to remove: ");
        rName = Main.in.nextLine("\nPlease input the name of the Restaurant to remove: ");

        // matching by restaurant Name
        restaurant = database.matchRestaurant(rName);

        return restaurant;
    }

    private void deleteRestaurant(Restaurants res) {
        database.removeFromlistOfRestaurants(res);
        System.out.println("Delete restaurant success.");
    }

}
