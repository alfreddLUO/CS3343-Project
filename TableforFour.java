public class TableforFour extends Tables {

    public TableforFour(String tableId, int tableCapacity) {
        super(tableId, tableCapacity);
    }

    public int getTableId() {
        return 0;
    }

    public boolean reservedTimeIsAllowed(int reservedTime) {
        return false;
    }

    public void setReservedStatus(int reservedTime) {
    }

}
