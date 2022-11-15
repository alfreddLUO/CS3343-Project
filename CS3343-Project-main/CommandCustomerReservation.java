import java.util.ArrayList;

public class CommandCustomerReservation implements Commands {
    private static final TablesManagement tm = TablesManagement.getInstance();
    private Customers customer;

    public CommandCustomerReservation(Customers customer) {
        this.customer = customer;
    }

    @Override
    public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
            ExTimeSlotNotReservedYet, ExTimeSlotAlreadyBeReserved {
        if (customer.checkisReserved()) {
            System.out.println("\nError! You already has a reservation.");

        } else {
            if (!reservationOperation())
                System.out.println("Something is wrong in reservation!");
            else {
                System.out.println(customer.getReserve().toString());
            }

        }

    }

    public boolean reservationOperation() throws ExTableNotExist, ExTimeSlotAlreadyBeReserved {

        ArrayList<Integer> chosedTableIds = new ArrayList<>();
        String reserveTime = null, chosedTable = null;
        int chosedTableId = 0;

        // Show available time slot for booking
        try {
            tm.showReservationTable();

            // Input the time slot you want to reserve, format: 11:23-12:22
            System.out.print("\nPlease input time slot to reserve (Format: xx:xx-xx:xx): ");
            reserveTime = Main.in.next("\nPlease input time slot to reserve (Format: xx:xx-xx:xx): ");

            // choose table by tableId
            System.out.print("Please input the table ids you want to reserve: (separate by comma): ");
            chosedTable = Main.in.next("Please input the table ids you want to reserve: (separate by comma): ");
            String[] idx = chosedTable.split(",");

            for (String s : idx) {
                chosedTableId = Integer.parseInt(s);
                chosedTableIds.add(chosedTableId);
            }
            customer.setReserveChosedTableIds(chosedTableIds);
            customer.setReserve(reserveTime, chosedTableIds);

        } catch (Exception e) {
            System.out.println("Error! Please try again!");
        }

        // set reserve in customer.java

        // indicator for successful booking
        return customer.getReserve() != null;
    }

}
