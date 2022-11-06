public class CustomerMembership {

    /*
     * Manage operations related to Customer's VIP / SuperVIP State
     * 1. Set customerState (For Admin)
     * 2. Update customerState -> 每次付款用實際價錢更新customerState再決定有沒有折扣
     * 3. Clear state // 暫時不知道要幹嘛
     */

    private Customers customer;

    public CustomerMembership(Customers customers) {
        this.customer = customers;
        this.customer.customerState = new CustomerVIPstate();
    }

    // Customer State Settings
    public void setState(CustomerState customerState) {
        customer.customerState = customerState;
    }

    // Update to check state everytime when checkout
    public void updateState(double price) {
        if (price >= 88) {
            customer.customerState = new CustomerSuperVIPstate();
        }
        System.out.println(customer.customerState.toString());
    }

    // Admin update the state of customer force
    public void adminUpdateState(CustomerState state) {
        setState(state);
    }

    public void clearState() {
        this.customer.customerState = new CustomerVIPstate();
    }
}
