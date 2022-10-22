import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class Table {// 所有的桌子放在一起
    private int tableId;
    private int tableCapacity;// include how many people
    private TableState tableState;
    // 以下这个list存放的是关于第二天的时间点是否被预定情况的存放：1.被reserved了：true 2.没被reserved：false
    // 以下这个list的index代表了时间点：0及代表零点；我们只需要把餐厅营业的时间点初始化为false即可，其余留着为true，让他们定不了就行
    private ArrayList<Boolean> timeslot;
    // 存放各个时间点预定的客户
    private ArrayList<Customer> reservedForCustomer;

    public Table(int tableId, int tableCapacity) {
        this.tableCapacity = tableCapacity;
        this.tableId = tableId;
        // 初始化，在每天更新时将营业时间改为false
        this.timeslot = new ArrayList<Boolean>();
        this.reservedForCustomer = new ArrayList<Customer>();
        // this.tableState=false;
    }

    public int getTableCapacity() {
        return this.tableCapacity;
    }

    public int getTableId() {
        return this.tableId;
    }

    // 用于check该时间点是否被reserved
    public boolean reservedTimeIsAllowed(int reservedTime) {
        if (timeslot.get(reservedTime)) {
            return false;
        }
        return true;
    }

    public void makeReservation(Customer c, int reservedTime) {
        timeslot.add(reservedTime, true);
        reservedForCustomer.add(reservedTime, c);
    }

}