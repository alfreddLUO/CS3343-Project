
public class ExTimeSlotAlreadyBeReserved extends Exception {

    public ExTimeSlotAlreadyBeReserved(int tableId, TimeSlot timeslot) {
        super(String.format("Table with id of %d can't be reserved between %s!", tableId,
                timeslot.toString()));
    }

}
