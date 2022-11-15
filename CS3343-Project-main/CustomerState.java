public interface CustomerState {

    public void customerstate(Customers customer);

    public void setdiscount(double discount);

    public double getdiscount();

    // public double priceCount(Customers customer);
    public double priceCount(double originalPrice);
}
