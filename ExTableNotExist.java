
public class ExTableNotExist extends Exception {
    public ExTableNotExist(int tableId) {
        super(String.format(
                "Can't delete the table with id of %d. Maybe due to it's unavailability or it doesn't exist.\n",
                tableId));
    }
}