package GoGoEat;

public interface UserModule {

    static TablesManagement tm = TablesManagement.getInstance();
    static Database database = Database.getInstance();

    public void run(String Id) throws ExTableNotExist, ExTimeSlotAlreadyBeReserved, ExTimeSlotNotReservedYet,
            ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExCustomersIdNotFound;

}
