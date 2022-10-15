import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class Restaurants {
    private String name;
    private ArrayList<Dish> menu = new ArrayList<>();

    // Constructor
    public Restaurants(String name) {
        this.name = name;
    }

    // function
    public void adddishtoMenu(Dish dish) {
        this.menu.add(dish);
    }

    public void deletedishfromMenu(Dish dish) {
        this.menu.remove(dish);
    }

    public ArrayList<Dish> showMenutoCustomer() {
        return this.menu;
    }

    // count the price that a customer should pay
    public int countPrice(ArrayList<Dish> orders) {
        int sum = 0;
        for (int i = 0; i < orders.size(); i++) {
            sum += orders.get(i).getdishPrice();
        }
        return sum;
    }

}
