package GoGoEat;

public class CommandPaymentAlipay extends CommandPayment {

    public CommandPaymentAlipay(Payment payment, double discountPrice) {
        super(payment, discountPrice);
    }

    @Override
    public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
            ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {

        PayFactory payFactory = new AlipayFactory();

        PaymentMethod paymentMethod = payFactory.getPay();
        PaymentString paymentString =new PayAlipayString();
        boolean result = paymentMethod.pay(discountPrice);

        if (result) {
            payment.setPaymentStatus(result);
            System.out.println("\nYou have completed payment with " + paymentString.getPayString() + ". Thank you!");
        }
    }
}
