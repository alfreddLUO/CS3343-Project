import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public abstract class Tables {// 所有的桌子放在一起
    private String tableId;
    private int tableCapacity;// include how many people
    private TableState tableState;
    private ArrayList alltimes = new ArrayList<>();

    public Tables(String tableId, int tableCapacity) {
        this.tableCapacity = tableCapacity;
        this.tableId = tableId;
        // this.tableState=false;
    }

    public boolean getwhetherisReserved() {
        return this.tableState.getState();
    }

    public void setReserved() {
        this.tableState.setTableState();
    }

    public void cancelReserved() {
        this.reserved = false;
    }

    public char getTableCapacity() {
        return 0;
    }

}
