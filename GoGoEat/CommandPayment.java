package GoGoEat;

public abstract class CommandPayment implements Commands {
    protected double discountPrice;
    protected Payment payment;
    protected Database database;

    public CommandPayment(Payment payment, double discountPrice) {
        this.payment = payment;
        this.discountPrice = discountPrice;
        this.database = Database.getInstance();
    }
}
