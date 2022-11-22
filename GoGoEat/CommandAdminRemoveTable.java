package GoGoEat;

public class CommandAdminRemoveTable extends CommandAdmin {

    CommandAdminRemoveTable() {
        super();
    }

    @Override
    public void exe() throws ExTableIdAlreadyInUse, ExTableNotExist {
        TablesManagement tm = TablesManagement.getInstance();
        tm.showAllTables();

        // Delete table with TableId
        System.out.print("\nPlease input the TableId to delete table: ");
        String input;
        int tableId;
        try {
            input = Main.in.next("\nPlease input the TableId to delete table: ");
            tableId = Integer.parseInt(input);
            forceDeleteTable(tableId);
            tableId = 0;
        } catch (NumberFormatException e) {
            System.out.println("Error! Wrong input for selection! Please input an integer!");
        }
    }

    // deleteTable -> Abort if Table reserved/occupied or not exist
    private void forceDeleteTable(int tableId) throws ExTableNotExist {
        try {
            TablesManagement tm = TablesManagement.getInstance();
            tm.removeTable(tableId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
