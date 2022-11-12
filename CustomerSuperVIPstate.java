public class CustomerSuperVIPstate implements CustomerState {

    private double discount = 0.8;

    @Override
    public double getdiscount() {
        return discount;
    }

    @Override
    public void setdiscount(double discount) {
        this.discount = discount;
    }

    @Override
    public void customerstate(Customers customer) {
        customer.getMembership().setState(this);
    }

    public String toString() {
        return "\nYou are a Super VIP! You get 80% discount.";
    }

    /*
     * @Override
     * public double priceCount(Customers customer) {
     * 
     * double price =
     * customer.getRestaurantChosed().countPrice(customer.customerOrders()) *
     * discount;
     * System.out.println("\nYour bill is $" + price + ".");
     * 
     * return price;
     * }
     */

    @Override
    public double priceCount(double originalPrice) {
        double discounted = originalPrice * discount;
        System.out.printf("\nYour bill is $%.2f.", discounted);
        return discounted;
    }
}
