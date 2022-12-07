package GoGoEat;

public abstract class CommandMerchant implements Commands {
    protected Database database = Database.getInstance();
    protected Merchants merchant;

    public CommandMerchant(Merchants merchant) {
        this.merchant = merchant;
    }
}
