package GoGoEat;

public class ExUnableToSetOpenCloseTime extends Exception {
    public ExUnableToSetOpenCloseTime(TimeSlot timeslot) {
        super(String.format("Error: Cannot set Open/Close time, reservation(s) overlap(), %s should be the subset.",
                timeslot.toString()));
    }

}
