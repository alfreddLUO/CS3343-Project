public class CommandAdminSetOpenCloseHour implements Commands{
    
    private static final Admin admin = Admin.getInstance();
    CommandAdminSetOpenCloseHour(){}
    @Override
    public void exe() throws ExUnableToSetOpenCloseTime{
        setOpenHours();
    }

    private void setOpenHours() throws ExUnableToSetOpenCloseTime {
        String sHour = null, fHour = null;
        boolean success = false;

        System.out.print("\nPlease input new opening hour: ");
        sHour = Main.in.next("\nPlease input new opening hour: ");
        System.out.print("Please input new closing hour: ");
        fHour = Main.in.next("Please input new closing hour: ");
        success = admin.forceSetOpenAndClosingTime(sHour, fHour);

        if (success) {
            System.out.println("\nChange opening and closing time success!");
            System.out.printf("The new opening time is %s.\nThe new closing time is %s.\n", sHour, fHour);
        }
    }
}
