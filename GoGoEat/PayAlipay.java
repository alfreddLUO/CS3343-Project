package GoGoEat;

public class PayAlipay implements PaymentMethod {

    @Override
    public boolean pay(double price) {
        System.out.println("\nYou will be redirected to Alipay to continue payment.");
        return true;
    }

    @Override
    public String toString() {
        return "Alipay";
    }
}
