public class CustomerSuperVIPstate implements CustomerState {
    private double discount;

    public double getdiscount() {
        return this.discount;
    }

    @Override
    public void customerstate(Customers customer) {

        customer.setState(this);

    }

    public String toString() {
        return "SuperVIP State";
    }

    @Override
    public void setdiscount(double discount) {
        this.discount = discount;
    }
}
