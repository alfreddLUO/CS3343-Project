package GoGoEat;

public abstract class CommandCustomer implements Commands {
    protected static Customers customer;
    protected Database database;

    public CommandCustomer(Customers customer) {
        CommandCustomer.customer = customer;
        this.database = Database.getInstance();
    }

}
