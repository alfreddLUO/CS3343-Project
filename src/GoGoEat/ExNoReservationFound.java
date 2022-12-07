package GoGoEat;

public class ExNoReservationFound extends Exception {
    public ExNoReservationFound() {
        super("There is no reservation made by this customer.");
    }
}
