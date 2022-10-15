import java.util.ArrayList;

//singleton pattern
public class TableManagement {   

   //sigleton pattern
    private static TableManagement instance = new TableManagement();
    private TableManagement(){}
    public static TableManagement getInstance(){
        return instance;
    }
    
    private ArrayList<Tables> allAvailableTables=new ArrayList<>();
    private ArrayList<Tables> allReservedTables=new ArrayList<>();
    private ArrayList<Tables> allOccupiedTables=new ArrayList<>();
    private ArrayList<Tables> alltables=new ArrayList<>();

    public ArrayList showCurrentTables(){
        return this.alltables;
    }
    //三个arraylist available（可用的）, reserved（被预定了的但是还没来）, occupied（占用的）,
    //available里面包括 没被预定的桌子且没被占用，和预定了但是在时间范围前没来的桌子
    //reserved是被预定的了的且在时间范围内的桌子
    //occupied是现在已经被占用的桌子
    //重点在于桌子state的转换，state pattern
    //对应桌子的二人桌子，四人桌子，8人桌子，用subclass实现

}
