package GoGoEat;

public class CommandPaymentWeChatPay extends CommandPayment {

    public CommandPaymentWeChatPay(Payment payment, double discountPrice) {
        super(payment, discountPrice);
    }

    @Override
    public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
            ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {

        PayFactory payFactory = new WechatPayFactory();
        PaymentMethod paymentMethod = payFactory.getPay();
        PaymentString paymentString=new PayWeChatString();
        boolean result = paymentMethod.pay(discountPrice);

        if (result) {
            payment.setPaymentStatus(result);
            System.out.println("\nYou have completed payment with " + paymentString.getPayString() + ". Thank you!");
        }

    }

}
