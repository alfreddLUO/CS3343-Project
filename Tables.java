import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public abstract class Tables {//所有的桌子放在一起
    private int tableid;
    private int num;//include how many people
    private boolean reserved;
    private boolean available;
    private ArrayList alltimes=new ArrayList<>();

    public Tables(int tableid,int num){
        this.num=num;
        this.tableid=tableid;
        this.reserved=false;
        this.available=true;
    }
    public boolean getwhetherisReserved(){
        return this.reserved;
    }
    public void setReserved(){
        this.reserved=true;
    }
    public void cancelReserved(){
        this.reserved=false;
    }

}
