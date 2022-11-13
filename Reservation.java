import java.time.LocalDate;
import java.util.ArrayList;

public class Reservation {
    private Boolean active; // become false when cancel reservation or sit to reserved tables.
    private String customerID;
    private ArrayList<Integer> tableIDs = new ArrayList<>();
    private static final TablesManagement tm = TablesManagement.getInstance();
    private TimeSlot timeSlot;
    // private LocalDate date;
    private String reserveInfo = null;

    /*
     * dateString: 預約第二天的，暫時用不上
     * TimeslotString: 先顯示 後輸入
     * desiredTableIds: 想要預訂的table的ID
     */

    public Reservation(String customerID, String timeSlotString, ArrayList<Integer> desiredTableIDs)
            throws ExTableNotExist, ExTimeSlotAlreadyBeReserved {

        this.customerID = customerID;

        String[] tsString = timeSlotString.split("-");
        timeSlot = new TimeSlot(tsString[0], tsString[1], customerID);
        this.active = true;

        reserve(desiredTableIDs, timeSlot);
    }

    private void reserve(ArrayList<Integer> desiredTableIDs, TimeSlot timeslot) {
        for (int id : desiredTableIDs) {
            try {
                if (tm.reserveTableAccordingToTimeslot(id, timeSlot)) {
                    tableIDs.add(id);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println();
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

    public String getReserveString() {
        computeReserveString();
        return this.reserveInfo;
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
