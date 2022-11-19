package GoGoEat;

public class CommandAdminAddTable implements Commands{
    
    private static final Admin admin = Admin.getInstance();
    CommandAdminAddTable(){}
    @Override
    public void exe() throws ExTableIdAlreadyInUse {
        System.out.print("\nPlease input the new tableId: ");
        String input;
        int tableId;
        try {
            input = Main.in.next("\nPlease input the new tableId: ");
            tableId = Integer.parseInt(input);
            System.out.print("\nPlease input the capacity of new table: ");
            input = Main.in.next("\nPlease input the capacity of new table: ");
            int tableCapacity = Integer.parseInt(input);
            admin.forceAddTable(tableId, tableCapacity);
            tableId = 0;     
        } catch (NumberFormatException e) {
            System.out.println("Error! Wrong input for selection! Please input an integer!");
        }
    }


}
