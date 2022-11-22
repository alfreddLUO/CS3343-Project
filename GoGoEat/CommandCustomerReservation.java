package GoGoEat;

import java.time.LocalDate;
import java.util.ArrayList;

public class CommandCustomerReservation extends CommandCustomer {

    public CommandCustomerReservation(Customers customer) {
        super(customer);
    }

    @Override
    public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
            ExTimeSlotNotReservedYet, ExTimeSlotAlreadyBeReserved {

        // If reserved -> Cannot reserve again
        if (customer.checkisReserved()) {
            System.out.println("\nError! You already has a reservation.");

        } else {
            // Not reserved

            if (!reservationOperation())

                // Reservation not complete
                System.out.println("\nPlease try to reserve again :-)");

            else {
                System.out.println(customer.getReserveSuccessInfo());
            }

        }

    }

    private boolean reservationOperation() throws ExTableNotExist, ExTimeSlotAlreadyBeReserved {

        ArrayList<Integer> chosedTableIds = new ArrayList<>();
        String reserveTime = null, chosedTable = null;
        int chosedTableId = 0;

        try {
            // Show available time slot for booking
            TablesManagement tm = TablesManagement.getInstance();
            tm.showReservationTable();

            // Input the time slot you want to reserve, format: 11:23-12:22
            System.out.print("\nPlease input time slot to reserve (Format: xx:xx-xx:xx): ");
            reserveTime = Main.in.nextLine("\nPlease input time slot to reserve (Format: xx:xx-xx:xx): ");

            // choose table by tableId
            System.out.print("Please input the table ids you want to reserve: (separate by comma): ");
            chosedTable = Main.in.next("Please input the table ids you want to reserve: (separate by comma): ");
            String[] idx = chosedTable.split(",");

            for (String s : idx) {
                chosedTableId = Integer.parseInt(s);
                chosedTableIds.add(chosedTableId);
            }
            Reservation r = setReserve(customer.getID(), reserveTime, chosedTableIds, ManualClock.getDate());
            customer.setReserve(r);

        } catch (Exception e) {
            System.out.println("Error! Please try again!");
        }

        // set reserve in customer.java
        // indicator for successful booking
        return customer.checkisReserved();

    }

    private Reservation setReserve(String CId, String timeslotString, ArrayList<Integer> desiredTableIds,
            LocalDate currDate) {
        try {
            return new Reservation(CId, timeslotString, desiredTableIds, currDate.plusDays(1));
        } catch (ExTimeFormatInvalid e) {
            System.out.println(e.getMessage());
        } catch (ExTimeSlotInvalid e) {
            System.out.println(e.getMessage());
            customer.clearReservation();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid Time Slot!!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}
