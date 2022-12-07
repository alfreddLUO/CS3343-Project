package GoGoEat;

public class WechatPayFactory implements PayFactory{

    @Override
    public PaymentMethod getPay() {
        
        return new PayWechat();
    }
    
}
