package GoGoEat;

public class AlipayFactory implements PayFactory {

    @Override
    public PaymentMethod getPay() {
        return new PayAlipay();
    }

}
