package GoGoEat;

public class CustomerVIPstate implements CustomerState {

    private double discount = 1;

    @Override
    public double getdiscount() {
        return discount;
    }

    @Override
    public String toString() {
        return "\nYou are a member. Consume up to $88 to get 80% discount!";
    }

    @Override
    public double priceCount(double originalPrice) {
        // Compute discounted price from original price

        double discounted = originalPrice * discount;
        System.out.printf("\nYour bill is $%.2f.\n", discounted);
        return discounted;
    }
}
