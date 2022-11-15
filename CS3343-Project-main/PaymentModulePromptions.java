class PaymentModulePromptions implements AbstractModulePromptions{

    @Override
    public void promptOptionStart() {
        System.out.println("\nCommands: ");
        System.out.println("[1] Alipay");
        System.out.println("[2] WeChat Pay");
        System.out.println("[3] Cash");
    }
        

        
    
}
