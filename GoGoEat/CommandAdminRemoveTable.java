package GoGoEat;

public class CommandAdminRemoveTable implements Commands {

    private static final Admin admin = Admin.getInstance();

    CommandAdminRemoveTable() {
    }

    @Override
    public void exe() throws ExTableIdAlreadyInUse, ExTableNotExist {

    	// Delete table with TableId
    	
        System.out.print("\nPlease input the TableId to delete table: ");
        String input;
        int tableId;
        try {
            input = Main.in.next("\nPlease input the TableId to delete table: ");
            tableId = Integer.parseInt(input);
            admin.forceDeleteTable(tableId);
            tableId = 0;
        } catch (NumberFormatException e) {
            System.out.println("Error! Wrong input for selection! Please input an integer!");
        }
    }

}
