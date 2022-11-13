import java.time.LocalDate;
import java.util.ArrayList;

public class Reservation {
    private Boolean active; // become false when cancel reservation or sit to reserved tables.
    private String customerID;
    private ArrayList<Integer> tableIDs = new ArrayList<>();
    private static final TablesManagement tm = TablesManagement.getInstance();
    private TimeSlot timeSlot;
    // private LocalDate date;

    /*
     * dateString: 預約第二天的，暫時用不上
     * TimeslotString: 先顯示 後輸入
     * desiredTableIds: 想要預訂的table的ID
     */

    public Reservation(String customerID, String timeSlotString, ArrayList<Integer> desiredTableIDs)
            throws ExTableNotExist, ExTimeSlotAlreadyBeReserved {
        this.customerID = customerID;
        // date = LocalDate.parse(dateString);
        String[] tsString = timeSlotString.split("-");
        timeSlot = new TimeSlot(tsString[0], tsString[1], customerID);
        this.active = true;
        reserve(desiredTableIDs, timeSlot);
    }

    private void reserve(ArrayList<Integer> desiredTableIDs, TimeSlot timeslot) {
        try {
            for (int id : desiredTableIDs) {
                if (tm.reserveTableAccordingToTimeslot(id, timeSlot)) {
                    tableIDs.add(id);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // "this reservation was made by {customerID}, reserved tables: {tableID[]}. +
    // timeSlot.toString()"
    @Override
    public String toString() {
        if (tableIDs.isEmpty()) {
            return "\n\n[" + customerID + "] Error: Reservation not made.";
        }
        String s = "\n\n[" + customerID + "] Reservation made.";
        s += "\nReserved tables: ";

        StringBuilder ids = new StringBuilder();
        for (int id : tableIDs) {
            ids.append(String.valueOf(id)).append(", ");
        }
        s += ids + timeSlot.toString();
        return s;
    }

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
}
