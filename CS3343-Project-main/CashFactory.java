public class CashFactory implements PayFactory{

    @Override
    public PaymentMethod getPay() {
        System.out.println("\nPlease go to the counter to proceed payment.");
        return new PayCash();
    }
    
}
