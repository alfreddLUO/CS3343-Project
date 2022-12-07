package GoGoEat;

public class PayCashString implements PaymentString {
    PayCashString() {

    };

    @Override
    public String getPayString() {
        return "Cash";
    }

}
