public interface UserModule {

    static TablesManagement tm = TablesManagement.getInstance();
    static Database database = Database.getInstance();

    public void run(String Id);

}
