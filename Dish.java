
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dish {
    private String dishname;
    private int dishprice;

    public Dish(String dishname){
        this.dishname=dishname;
        
    }

    public Dish(String dishname,int dishprice){
        this.dishname=dishname;
        this.dishprice=dishprice;
    }

    public int getdishPrice(){
        return this.dishprice;
    }
    public String getdishname(){
        return this.dishname;
    }

}    
