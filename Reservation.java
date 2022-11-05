import java.time.LocalDate;
import java.util.ArrayList;

public class Reservation {
    private Boolean active; // become false when cancel reservation or sit to reserved tables.
    private int customerID;
    private ArrayList<Integer> tableIDs = new ArrayList<>();
    private TableManagement tm = TableManagement.getInstance();
    private TimeSlot timeSlot;
    private LocalDate date;

    public Reservation(int customerID, String dateString, String timeSlotString, int[] desiredTableIDs) {
        this.customerID = customerID;
        date = LocalDate.parse(dateString);
        String tsString[] = timeSlotString.split("-");
        timeSlot = new TimeSlot(tsString[0], tsString[1], customerID);
        this.active = true;
        reserve(desiredTableIDs);
    }

    private void reserve(int[] desiredTableIDs) {
        for (int id : desiredTableIDs) {
            if (tm.reserveTableAccordingToTimeslot(id, timeSlot)) {
                tableIDs.add(id);
            }
        }
    }

    // "this reservation was made by {customerID}, reserved tables: {tableID[]}. +
    // timeSlot.toString()"
    @Override
    public String toString() {
        String s = "This reservation was made by " + customerID + ", reserved tables: ";
        String ids = "";
        for (int id : tableIDs) {
            ids += String.valueOf(id) + ", ";
        }
        s += ids + timeSlot.toString();
        return s;
    }

    public void cancel() {
        for (Integer id : tableIDs) {
            tm.cancelReservationAccordingToTimeslot(id, timeSlot);
            ;
        }
    }
}