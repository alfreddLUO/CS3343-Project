package GoGoEat;

public class CommandAccountManagementDelete extends CommandAccountManagement {

    CommandAccountManagementDelete() {
        super();
    }

    @Override
    public void exe() {
        super.success = deleteAcc();
    }

    public boolean deleteAcc() {

        String username = "", input = "";

        System.out.print("\nPlease input the username to delete account: ");
        input = Main.in.next("\nPlease input the username to delete account: ");
        username = input;

        return accManager.deleteaccountinUserNameAndAccount(username);
    }
}
