import java.util.ArrayList;

public class Admin {
    private int adminid;
    private String adminusername;
    
    private ArrayList allrestaurants=new ArrayList<>();

    public Admin(int adminid,String adminusername){
        this.adminid=adminid;
        this.adminusername=adminusername;
    }

    public void addRestaurant(Restaurants res){
        this.allrestaurants.add(res);
    }
    public void deleteRestaurant(Restaurants res){
        this.allrestaurants.remove(res);
    }
    public ArrayList showallRestaurants(){
        return this.allrestaurants;
    }
    //set the discount of the different kinds of customers
    public void setstateanddiscount(CustomerState state,double discount){
        state.setdiscount(discount);
    }
}
