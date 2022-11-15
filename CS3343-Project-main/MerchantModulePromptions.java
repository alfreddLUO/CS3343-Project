class MerchantModulePromptions implements AbstractModulePromptions{
        //constructor
        public MerchantModulePromptions(){};
        //merchant module
        public void promptOptionStart() {
            System.out.print("\n--------------------------------------------------");
            System.out.println("\nCommands: ");
            System.out.println("[1] Modify Menu");
            System.out.println("[2] Check Order");
            System.out.println("[3] Logout");
        }
}
