package GoGoEat;

public class PayWeChatString implements PaymentString{
    PayWeChatString(){};
    @Override
    public String getPayString() {
       
        return "WeChat Pay";
    }
    
}
