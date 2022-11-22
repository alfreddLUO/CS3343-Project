package GoGoEat;

public class Admin {

    private final String adminId = "A0001";
    private final String adminUsername = "Admin";
    private static final Admin instance = new Admin();

    private Commands command;

    public Admin() {
    }

    public static Admin getInstance() {
        return instance;
    }

    // @Override
    public String getUsername() {
        return adminUsername;
    }

    // @Override
    public String getUserId() {
        return adminId;
    }

    public void setCommand(Commands command) {
        this.command = command;
    }

    public void callCommand() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
            ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {
        command.exe();
    }
}
