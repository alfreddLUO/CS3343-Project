public class PayAlipay implements PaymentMethod {

    @Override
    public boolean pay(double price) {
        System.out.println("\nYou will be redirected to Alipay to continue payment.");
        // System.out.println("You have completed the payment with alipay.");
        return true;
    }

    @Override
    public String toString() {
        return "Alipay";
    }
}
