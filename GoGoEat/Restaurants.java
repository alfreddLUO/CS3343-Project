package GoGoEat;

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
    public boolean adddishtoMenu(Dish dish) {
        this.menu.add(dish);
        return true;
    }

    public boolean deletedishfromMenu(Dish dish) {
        if (this.menu.contains(dish)) {
            this.menu.remove(dish);
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Dish> getMenu() {
        return this.menu;
    }

    // count the price that a customer should pay
    public double countPrice(ArrayList<Dish> orders) {
        double sum = 0;
        for (Dish order : orders) {
            sum += order.getdishPrice();
        }
        return sum;
    }

    // Print all dish from restaurant's menu
    public void printMenu() {
        System.out.println("\nMenu: ");
        for (int i = 0; i < menu.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + menu.get(i).toString());
        }
    }

    public Dish getDishbyName(String dName) {
        for (Dish d : menu) {
            if (d.getdishname().equals(dName)) {
                return d;
            }
        }
        return null;
    }
    
    public void updateMenu(ArrayList<Dish> newMenu) {
    	this.menu = newMenu;
    }
}