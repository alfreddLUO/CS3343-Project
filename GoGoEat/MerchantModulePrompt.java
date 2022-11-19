package GoGoEat;

class MerchantModulePrompt implements AbstractModulePrompt {
    // constructor
    public MerchantModulePrompt() {
    };

    // merchant module
    public void promptOptionStart() {
        System.out.print("\n--------------------------------------------------");
        System.out.println("\nCommands: ");
        System.out.println("[1] Modify Menu");
        System.out.println("[2] Check Order");
        System.out.println("[3] Logout");
    }

    public static void promptModifyMenu() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Add Dish");
        System.out.println("[2] Delete Dish");
        System.out.println("[3] Edit Dish");
        System.out.println("[4] Cancel");
    }

    public static void promptEditDish() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Edit Dish Name");
        System.out.println("[2] Edit Dish Price");
        System.out.println("[3] Cancel");
    }

}
