package GoGoEat;

public class PayCash implements PaymentMethod {

    @Override
    public boolean pay(double price) {
        return true;
    }

    @Override
    public String toString() {
        return "Cash";
    }
}
