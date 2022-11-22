package GoGoEat;

public abstract class CommandAccountManagement implements Commands {

    protected Database database;
    protected AccountManagement accManager;
    protected boolean success;

    public CommandAccountManagement() {
        this.database = Database.getInstance();
        this.accManager = AccountManagement.getInstance();
    }

    public boolean ifSuccess() {
        return success;
    }
}
