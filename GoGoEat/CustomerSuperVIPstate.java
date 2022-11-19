package GoGoEat;

public class CustomerSuperVIPstate implements CustomerState {

    private double discount = 0.8;

    @Override
    public double getdiscount() {
        return discount;
    }

    @Override
    public String toString() {
        return "\nYou are a Super VIP! You get 80% discount.";
    }

    @Override
    public double priceCount(double originalPrice) {
        // Compute discounted price from original price
        double discounted = originalPrice * discount;
        System.out.printf("\nYour bill is $%.2f.", discounted);
        return discounted;
    }
}
