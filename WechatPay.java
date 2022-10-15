public class WechatPay implements PaymentMethod{

    @Override
    public void pay() {
        System.out.println("You have completed payment with Wechat");      
    }
    
}
