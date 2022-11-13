import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.time.LocalTime;
import java.util.ArrayList;

public class Table implements Comparable<Table> {// 所有的桌子放在一起
    // -------------------------------------------------------------------------------------------------------------------------------------------------------
    private int tableID;
    private int capacity;
    private TimeSlots reservationsTmr = new TimeSlots(); // for tomorror
    private TimeSlots reservationsTdy = new TimeSlots(); // for today

    private Boolean reserved = false;
    private Boolean seated = false;
    private int customerID;
    public int getTableCapacity;

    @Override
    public int compareTo(Table t) {
        if (this.capacity <= t.capacity) {
            if (this.capacity == t.capacity) {
                if (this.tableID < t.tableID) {
                    return 1;
                } else {
                    return -1;
                }
            }
            return 1;
        }
        return -1;

    }

    public Table(int tableID, int capacity) {
        this.tableID = tableID;
        this.capacity = capacity;
        // reservationsTmr = new TimeSlots();
    }

    public int getId() {
        return this.tableID;
    }

    public void startNewDay() {
        reservationsTdy = reservationsTmr;
        // reservationsTmr = new TimeSlots();
    }

    public int getTableId() {
        return tableID;
    }

    public int getTableCapacity() {
        return capacity;
    }

    public int toBeReserved(LocalTime now) {
        if (reservationsTdy.checkReservedStatus(now) == 1) {
            return 1;
        } else if (reservationsTdy.checkReservedStatus(now) == 0) {
            return 0;
        }
        return -1;
    }

    public boolean reservedTimeIsAllowed(TimeSlot reservedTime) {
        return false;
    }

    public void makeReservation(Customers c, TimeSlot reservedTime) {
    }

    public TimeSlots getTmrReservationTimeSlot() {
        return reservationsTmr;
    }

    public TimeSlots getTodayReservationTimeSlot() {
        return reservationsTdy;
    }

    public void updatetmrTimeslots(TimeSlots tmrTimeSlots) {
        reservationsTmr = tmrTimeSlots;
    }

    public void removeTimeslot(TimeSlot timeslot) {
        reservationsTmr.remove(timeslot);
    }

    public void removeTdayTimeslot(TimeSlot timeslot) {
        reservationsTdy.remove(timeslot);
    }

    public Boolean noReservationForTodayAndTmr() {
        return reservationsTmr.isEmpty() && reservationsTdy.isEmpty();
    }
}