public class CommandAdminAddRestaurant implements Commands{
    private static final Database database = Database.getInstance();
    private static final Admin admin = Admin.getInstance();
    CommandAdminAddRestaurant(){}
    @Override
    public void exe() {
        Restaurants temp = addNewRestaurant();
        admin.addRestaurant(temp);
        database.showListOfRestaurants();       
    }

    public Restaurants addNewRestaurant() {

        System.out.print("\nPlease input the name of the new Restaurant: ");
        String rName = Main.in.nextLine("\nPlease input the name of the new Restaurant: ");
        return new Restaurants(rName);
    }

}
