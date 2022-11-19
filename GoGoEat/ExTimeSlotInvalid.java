package GoGoEat;

public class ExTimeSlotInvalid extends Exception {

    public ExTimeSlotInvalid() {
        super(String.format(
                "\nError: Time slot invalid, please set it in range of 30 minutes to 2 hours"));
    }
}
