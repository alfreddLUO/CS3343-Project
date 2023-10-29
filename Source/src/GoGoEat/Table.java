package GoGoEat;

import java.time.LocalTime;

public class Table implements Comparable<Table> {
	// All Tables put together
	
    private int tableID;
    private int capacity;
    private TimeSlots reservationsTmr = new TimeSlots(); // for tomorrorw
    private TimeSlots reservationsTdy = new TimeSlots(); // for today

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
    }

    public int getId() {
        return this.tableID;
    }

    public void startNewDay() {
        reservationsTdy = reservationsTmr;
        reservationsTmr = new TimeSlots();
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

    // Get tomorrow's all reservation timeslot
    public TimeSlots getTmrReservationTimeSlot() {
        return reservationsTmr;
    }

    // Get Today's all reservation timeslot
    public TimeSlots getTodayReservationTimeSlot() {
        return reservationsTdy;
    }

    // Update timeslot of tomorrow
    public void updatetmrTimeslots(TimeSlots tmrTimeSlots) {
        reservationsTmr = tmrTimeSlots;
    }

    // Remove timeslot for tomorrow
    public void removeTimeslot(TimeSlot timeslot) {
        reservationsTmr.remove(timeslot);
    }

    // Remove today Timeslot
    public void removeTdayTimeslot(TimeSlot timeslot) {
        reservationsTdy.remove(timeslot);
    }

    // Check if today and tomorrow has no reservation
    public Boolean noReservationForTodayAndTmr() {
        return reservationsTmr.isEmpty() && reservationsTdy.isEmpty();
    }
}
