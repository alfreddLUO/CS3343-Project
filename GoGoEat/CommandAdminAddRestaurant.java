package GoGoEat;

public class CommandAdminAddRestaurant extends CommandAdmin {

    CommandAdminAddRestaurant() {
        super();
    }

    @Override
    public void exe() {
        Restaurants temp = addNewRestaurant();
        addRestaurant(temp);
        database.showListOfRestaurants();
    }

    private Restaurants addNewRestaurant() {
        System.out.print("\nPlease input the name of the new Restaurant: ");
        String rName = Main.in.nextLine("\nPlease input the name of the new Restaurant: ");
        return new Restaurants(rName);
    }

    // Add Restaurants by passing instance
    private void addRestaurant(Restaurants res) {
        database.addTolistOfRestaurants(res);
        System.out.println("Add new restaurant success.");
    }
}
