package GoGoEat;

public class CommandAdminRemoveRestaurant implements Commands{
    private static final Database database = Database.getInstance();
    private static final Admin admin = Admin.getInstance();
    CommandAdminRemoveRestaurant(){}
    @Override
    public void exe() {
        Restaurants temp = removeRestaurant();
        if (temp == null) {
            System.out.println("No such restaurant. Please check again.");
        } else {
            admin.deleteRestaurant(temp);
        }
        database.showListOfRestaurants();       
    }
    
    public Restaurants removeRestaurant() {

        String rName = "";
        Restaurants restaurant = null;

        System.out.print("\nPlease input the name of the Restaurant to remove: ");
        rName = Main.in.nextLine("\nPlease input the name of the Restaurant to remove: ");

        restaurant = database.matchRestaurant(rName);

        return restaurant;
    }

}
