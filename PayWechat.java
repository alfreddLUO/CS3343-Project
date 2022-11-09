public class PayWechat implements PaymentMethod {

    private String name = "WeChat Pay";

    @Override
    public boolean pay(double price) {
        System.out.println("\nYou will be redirected to WeChat to continue payment.");
        // System.out.println("You have completed payment with Wechat");
        return true;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
