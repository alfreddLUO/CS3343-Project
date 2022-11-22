package GoGoEat;

public class CommandAdminSetOpenCloseHour extends CommandAdmin {

    CommandAdminSetOpenCloseHour() {
        super();
    }

    @Override
    public void exe() throws ExUnableToSetOpenCloseTime {
        setOpenHours();
    }

    private void setOpenHours() throws ExUnableToSetOpenCloseTime {
        String timeString = null;
        boolean success = false;

        System.out.printf("\nPlease input new opening and closing hour (format: xx:xx-xx:xx): ");
        timeString = Main.in.nextLine("\nPlease input new opening and closing hour (format: xx:xx-xx:xx): ");
        success = forceSetOpenAndClosingTime(timeString);

        if (success) {
            System.out.printf("\nChange opening and closing time success!\n");
            System.out.printf("The new opening hour is %s.\n", timeString);
        }
    }

    // Set food court open and close (start of reservation and end of reservation)
    private boolean forceSetOpenAndClosingTime(String timeString) {

        // format: xx:xx-xx:xx
        Boolean setOpenCloseTime = false;
        try {
            TablesManagement tm = TablesManagement.getInstance();
            setOpenCloseTime = tm.setOpenAndCloseTime(timeString);
        } catch (ExTimeFormatInvalid e) {
            System.out.println(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Wrong format of time! Format should be xx:xx-xx:xx!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // return to CommandAdminSetOpenCloseHour.java
        return setOpenCloseTime;
    }
}
