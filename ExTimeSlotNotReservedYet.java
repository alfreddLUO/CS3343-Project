
public class ExTimeSlotNotReservedYet extends Exception {

    public ExTimeSlotNotReservedYet(int tableId, TimeSlot timeslot) {
        super(String.format(
                "FAIL to cancel the reservation because the selected table(s) of %d haven't been reserved bewtween %s.",
                tableId, timeslot));
    }

}
