
public class MerchantModule {

    private static Customers customer;

    /*
     * 流程：
     * 1. 修改菜單
     * 1.1 增加菜品
     * 1.2 刪除菜品
     * 1.3 修改名稱/價錢
     * 1.4 返回MerchantModule.run
     * 
     * 2. Check Customer's Order
     * 2.1 查看顧客的Order
     * 2.2 payment by Cash
     * 
     * 3. Exit -> 登出
     */

    public static void run(Merchants merchant) {

        int select = 0;
        String input = "";

        while (select != 3) {
            System.out.println("\nCommands: ");
            System.out.println("[1] Modify Menu");
            System.out.println("[2] Check Order");
            System.out.println("[3] Logout");

            System.out.print("\nPlease select your operations: ");
            input = Main.in.next("\nInput: ");
            select = Integer.parseInt(input);

            String CId = "";
            if (select == 1) {

                merchant.modifyMenu();

            } else if (select == 2) {

                System.out.print("\nPlease input customer's id: ");
                input = Main.in.next("Input: ");
                CId = input;

                // loop through the listOfCustomers in Main to find a match
                customer = Main.matchCId(CId);

                merchant.checkOrder(customer);
            } else {

            }

        }

    }

}
