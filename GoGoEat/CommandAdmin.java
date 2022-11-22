package GoGoEat;

public abstract class CommandAdmin implements Commands {
    protected Database database;
    protected Admin admin;

    public CommandAdmin() {
        this.database = Database.getInstance();
        this.admin = Admin.getInstance();
    }
}
