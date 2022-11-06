public class WechatPay implements PaymentMethod {

    @Override
    public boolean pay(double price) {
        System.out.println("You have completed payment with Wechat");
        return false;
    }

}
