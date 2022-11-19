package GoGoEat;

public class PayWechat implements PaymentMethod {

    @Override
    public boolean pay(double price) {
        System.out.println("\nYou will be redirected to WeChat to continue payment.");
        return true;
    }

    @Override
    public String toString() {
        return "WeChat Pay";
    }
}
