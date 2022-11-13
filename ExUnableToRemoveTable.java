public class ExUnableToRemoveTable extends Exception {
    public ExUnableToRemoveTable(int tableID) {
        super(String.format("Unable to remove table with id of %d, because they may have other duties arranged!",
                tableID));
    }
}