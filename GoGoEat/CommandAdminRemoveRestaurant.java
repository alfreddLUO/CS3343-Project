package GoGoEat;

public class CommandAdminRemoveRestaurant implements Commands{
    
	private static final Database database = Database.getInstance();
    private static final Admin admin = Admin.getInstance();
    
    CommandAdminRemoveRestaurant(){}
    
    @Override
    public void exe() {
        Restaurants temp = removeRestaurant();
        
        // Check if restaurant instance exist 
        if (temp == null) {
            System.out.println("No such restaurant. Please check again.");
        } else {
        	// exist -> delete instance from Database
            admin.deleteRestaurant(temp);
        }
        
        // Show updated list after deleting
        database.showListOfRestaurants();       
    }
    
    public Restaurants removeRestaurant() {

        String rName = "";
        Restaurants restaurant = null;

        System.out.print("\nPlease input the name of the Restaurant to remove: ");
        rName = Main.in.nextLine("\nPlease input the name of the Restaurant to remove: ");

        // matching by restaurant Name
        restaurant = database.matchRestaurant(rName);

        return restaurant;
    }

}
