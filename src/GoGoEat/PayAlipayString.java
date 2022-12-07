package GoGoEat;

public class PayAlipayString implements PaymentString {
    PayAlipayString() {

    };

    @Override
    public String getPayString() {
        return "Alipay";
    }

}
