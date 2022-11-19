package GoGoEat;

public interface PaymentMethod {
    public boolean pay(double price);

    public String toString();
}
