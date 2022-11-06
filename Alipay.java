public class Alipay implements PaymentMethod {

    @Override
    public boolean pay(double price) {
        // TODO Auto-generated method stub
        System.out.println("You have completed the payment with alipay.");
        return false;
    }

}
