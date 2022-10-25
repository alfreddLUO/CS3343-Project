import java.time.LocalDate;

public class Reservation {
    private Boolean active; //become false when cancel reservation or sit to reserved tables.
    private int customerID;
    private int[] tableIDs;
    private TableManagement tm = TableManagement.getInstance();
    private TimeSlot timeSlot;
    private LocalDate date;

    public Reservation(int customerID, String dateString, String timeSlotString, int[]tableIDs) {
        this.customerID = customerID;
        date = LocalDate.parse(dateString);
        String tsString[] = timeSlotString.split("-");
        this.tableIDs = tableIDs;
        timeSlot = new TimeSlot(tsString[0], tsString[1], customerID);
        this.active = true;
        reserve();
    }

    private void reserve() {
        for (int i : tableIDs) {
            tm.reserve(customerID, i, date, timeSlot);
        }
    }

    //"this reservation was made by {customerID}, reserved tables: {tableID[]}. + timeSlot.toString()"
    @Override
    public String toString() {
        String s = "This reservation was made by " + customerID + ", reserved tables: ";
        String ids = "";
        for (int id: tableIDs) {
            ids += String.valueOf(id) + ", ";
        }
        s += ids + timeSlot.toString();
        return s;
    }

    public void cancel() {
        tm.cancelReservation(this.customerID);
        this.active = false;
    }
}
