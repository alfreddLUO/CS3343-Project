package GoGoEat;

public class CommandAdminRemoveRestaurant extends CommandAdmin {

    public CommandAdminRemoveRestaurant() {
        super();
    }

    @Override
    public void exe() {
        Restaurants temp = findRestaurantToRemove();

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

    private Restaurants findRestaurantToRemove() {
        database.outputRestaurant();

        String rName = "";
        Restaurants restaurant = null;

        System.out.print("\nPlease input the name of the Restaurant to remove: ");
        rName = Main.in.nextLine("\nPlease input the name of the Restaurant to remove: ");

        // matching by restaurant Name
        restaurant = database.matchRestaurant(rName);

        return restaurant;
    }

    protected void deleteRestaurant(Restaurants res) {
        database.removeFromlistOfRestaurants(res);
        System.out.println("Delete restaurant success.");
    }

}
