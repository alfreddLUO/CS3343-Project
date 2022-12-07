package GoGoEat;

public class ExTableNotExist extends Exception {
    public ExTableNotExist(int tableId) {
        super(String.format(
                "Table with ID: %d does not exist!", tableId));
    }
}
