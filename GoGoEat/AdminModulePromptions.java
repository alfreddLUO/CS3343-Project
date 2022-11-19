package GoGoEat;

public class AdminModulePromptions implements AbstractModulePromptions {
    private static AdminModulePromptions instance = null;

    public AdminModulePromptions(){};
     //admin module
     public void promptOptionStart() {
        System.out.print("\n--------------------------------------------------");
        System.out.println("\nCommands: ");
        System.out.println("[1] Set Food Court's Opening and Closing Time");
        System.out.println("[2] Check Customer's Orders");
        System.out.println("[3] Check Customer's Reservation");
        System.out.println("[4] Add Restaurant");
        System.out.println("[5] Remove Restaurant");
        System.out.println("[6] Add Table");
        System.out.println("[7] Remove Table");
        System.out.println("[8] Logout");
    }
}