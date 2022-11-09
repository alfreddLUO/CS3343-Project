public class PayCash implements PaymentMethod {

    private String name = "Cash";

    @Override
    public boolean pay(double price) {
        return true;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
