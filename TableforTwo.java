public class TableforTwo extends Tables {

    public String getTableId;

    public TableforTwo(String tableId, int tableCapacity) {
        super(tableId, tableCapacity);
    }

    public boolean getReservedTimeStatus(int reservedTime) {
        return false;
    }

    public void setReservedStatus(int reservedTime) {
    }

    public int getTableId() {
        return 0;
    }

    public boolean reservedTimeIsAllowed(int reservedTime) {
        return false;
    }

}
