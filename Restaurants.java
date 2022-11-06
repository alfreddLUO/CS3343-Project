import java.util.ArrayList;

public class Restaurants {
    private String name;
    private ArrayList<Dish> menu = new ArrayList<>();

    // Constructor
    public Restaurants(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // function
    public void adddishtoMenu(Dish dish) {
        this.menu.add(dish);
    }

    public void deletedishfromMenu(Dish dish) {
        this.menu.remove(dish);
    }

    public ArrayList<Dish> getMenu() {
        return this.menu;
    }

    // count the price that a customer should pay
    public double countPrice(ArrayList<Dish> orders) {
        double sum = 0;
        for (int i = 0; i < orders.size(); i++) {
            sum += orders.get(i).getdishPrice();
        }
        return sum;
    }
}