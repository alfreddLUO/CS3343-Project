package GoGoEat;

public class CommandPaymentWeChatPay implements Commands {
    
	private double discountPrice;
    private Payment payment;

    public CommandPaymentWeChatPay(Payment payment, double discountPrice) {
        this.payment = payment;
        this.discountPrice = discountPrice;
    }

    @Override
    public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
            ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {
    
    	PayFactory payFactory = new WechatPayFactory();
        PaymentMethod paymentMethod = payFactory.getPay();
        
        boolean result = paymentMethod.pay(discountPrice);
        
        if (result) {
            payment.setPaymentStatus(result);
            System.out.println("\nYou have completed payment with " + paymentMethod.toString() + ". Thank you!");
        }

    }

}
