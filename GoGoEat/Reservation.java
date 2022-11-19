package GoGoEat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Reservation {
    private Boolean active; // become false when cancel reservation or sit to reserved tables.
    private String customerID;
    private ArrayList<Integer> tableIDs = new ArrayList<>();
    private static final TablesManagement tm = TablesManagement.getInstance();
    private TimeSlot timeSlot;
    // private LocalDate date;
    private String reserveInfo = null;
    private LocalDate date;

    /*
     * dateString: 預約第二天的，暫時用不上
     * TimeslotString: 先顯示 後輸入
     * desiredTableIds: 想要預訂的table的ID
     */

    public Reservation(String customerID, String timeSlotString, ArrayList<Integer> desiredTableIDs, LocalDate date)
            throws ExTableNotExist, ExTimeSlotAlreadyBeReserved, ExTimeSlotInvalid, ExTimeFormatInvalid {

        this.customerID = customerID;

        String[] tsString = timeSlotString.split("-");
        timeSlot = new TimeSlot(tsString[0], tsString[1], customerID);
        this.active = true;
        this.date = date;

        reserve(desiredTableIDs, timeSlot);
    }

    private void reserve(ArrayList<Integer> desiredTableIDs, TimeSlot timeslot) {
        for (int id : desiredTableIDs) {
            try {
                if (tm.reserveTableAccordingToTimeslot(id, timeSlot)) {
                    this.tableIDs.add(id);
                }
            } catch (ExTableNotExist e) {
                System.out.println(e.getMessage());
                continue;
            } catch (ExTimeSlotAlreadyBeReserved e) {
                System.out.println(e.getMessage());
                continue;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                continue;
            }
        }
        System.out.println();
    }

    public ArrayList<Integer> getReservedTableIDs() {
        return tableIDs;
    }

    @Override
    public String toString() {
        if (tableIDs.isEmpty()) {
            return "\n[" + customerID + "] Error: Reservation not made.\n";
        }
        String s = "\n[" + customerID + "] Reservation made, Reserved tables: ";

        computeReserveString();
        s += reserveInfo;
        return s;
    }

    // Compute Reservation timeslot & TableId String
    public void computeReserveString() {
        String str = "";
        StringBuilder ids = new StringBuilder();

        int num = 1;
        for (int id : tableIDs) {
            ids.append("\n[" + num + "] " + "[Table with ID: " + String.valueOf(id) + "] " + "[Time Slot: "
                    + timeSlot.toString() + "]");
            num++;
        }
        str += ids;
        this.reserveInfo = str;
    }

    // Return only reserved timeslot
    public String getReserveString() {
        computeReserveString();
        return this.reserveInfo;
    }

    // Cancel reservation
    public void cancel() {
        try {
            for (Integer id : tableIDs) {
                tm.cancelReservationAccordingToTimeslot(id, timeSlot);
            }
            active = false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public TimeSlot getReservedTimeSlot() {
        return timeSlot;
    }

    public int checkValid(LocalDate currDate) {
        if (currDate.compareTo(date) == 0) {
            return 0;
        } 
        else if (currDate.plusDays(1).compareTo(date) == 0) {
            return 1;
        }
        else {
            return -1;
        }
    }
}
