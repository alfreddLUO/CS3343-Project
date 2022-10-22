import java.util.ArrayList;

//singleton pattern
public class TableManagement {

    private ArrayList<TableforEight> reservedTableForEight;
    private ArrayList<TableforFour> reservedTableForFour;
    private ArrayList<TableforTwo> reservedTableForTwo;
    private ArrayList<TableforEight> availableTableForEight;
    private ArrayList<TableforFour> availableTableForFour;
    private ArrayList<TableforTwo> availableTableForTwo;
    private ArrayList<TableforEight> occupiedTableForEight;
    private ArrayList<TableforFour> occupiedTableForFour;
    private ArrayList<TableforTwo> occupiedTableForTwo;
    private ArrayList<Customer> waitingCustomers;

    // sigleton pattern
    private static TableManagement instance = new TableManagement();

    public static TableManagement getInstance() {
        return instance;
    }

    private TableManagement() {
        reservedTableForEight = new ArrayList<TableforEight>();
        reservedTableForFour = new ArrayList<TableforFour>();
        reservedTableForTwo = new ArrayList<TableforTwo>();
        availableTableForEight = new ArrayList<TableforEight>();
        availableTableForFour = new ArrayList<TableforFour>();
        availableTableForTwo = new ArrayList<TableforTwo>();
        occupiedTableForEight = new ArrayList<TableforEight>();
        occupiedTableForFour = new ArrayList<TableforFour>();
        occupiedTableForTwo = new ArrayList<TableforTwo>();
    }

    public String addNewTable(String tableId, String tableCapacity) {
        if (tableCapacity == "2") {
            TableforTwo table = new TableforTwo(tableId, 2);
            availableTableForTwo.add(table);
        } else if (tableCapacity == "4") {
            TableforFour table = new TableforFour(tableId, 4);
            availableTableForFour.add(table);
        } else if (tableCapacity == "8") {
            TableforEight table = new TableforEight(tableId, 8);
            availableTableForEight.add(table);
        }
        return "Successfully add Table";
    }

    public void arrangeTableAccordingToNumOfPeople(int peopleNum) {
        int tmpPeopleNum = peopleNum;
        int tableNumOfTableForEight = tmpPeopleNum / 8;
        tmpPeopleNum = tmpPeopleNum % 8;
        int tableNumOfTableForFour = tmpPeopleNum / 4;
        tmpPeopleNum = tmpPeopleNum % 4;
        int tableNumOfTableForTwo = tmpPeopleNum % 2 == 0 ? tmpPeopleNum / 2 : (tmpPeopleNum / 2 + 1);
        checkTheTableStatus(tableNumOfTableForTwo, tableNumOfTableForFour,
                tableNumOfTableForEight);
    }

    private void checkTheTableStatus(int tableNumOfTableForTwo, int tableNumOfTableForFour,
            int tableNumOfTableForEight) {
        if (returnTableOfTwoNeedToWait(tableNumOfTableForTwo) == 0
                && returnTableOfFourNeedToWait(tableNumOfTableForFour) == 0
                && returnTableOfEightNeedToWait(tableNumOfTableForEight) == 0) {
            System.out.printf(
                    "Your arranged tables: Two-Seats Table: %d Four-Seats Table: %d Eight-Seats Table: %d. You can directly walk in!",
                    tableNumOfTableForTwo, tableNumOfTableForFour, tableNumOfTableForEight);
            System.out.printf("Please indicate your option: Dine In Or Leave");
        } else {
            System.out.printf(
                    "Your arranged tables: Two-Seats Table: %d Four-Seats Table: %d Eight-Seats Table: %d. You need to wait for: Your arranged tables: Two-Seats Table: %d Four-Seats Table: %d Eight-Seats Table: %d.",
                    tableNumOfTableForTwo, tableNumOfTableForFour, tableNumOfTableForEight,
                    returnTableOfTwoNeedToWait(tableNumOfTableForTwo),
                    returnTableOfFourNeedToWait(tableNumOfTableForFour),
                    returnTableOfEightNeedToWait(tableNumOfTableForEight));
            System.out.printf("Please indicate your option: Wait Or Leave");
        }

    }

    private int returnTableOfEightNeedToWait(int tableNumOfTableForEight) {
        int availableNumOfEightTable = availableTableForEight.size();
        return (tableNumOfTableForEight - availableNumOfEightTable) <= 0 ? 0
                : (tableNumOfTableForEight - availableNumOfEightTable);
    }

    private int returnTableOfFourNeedToWait(int tableNumOfTableForFour) {
        int availableNumOfFourTable = availableTableForFour.size();
        return (tableNumOfTableForFour - availableNumOfFourTable) <= 0 ? 0
                : (tableNumOfTableForFour - availableNumOfFourTable);
    }

    private int returnTableOfTwoNeedToWait(int tableNumOfTableForTwo) {
        int availableNumOfTwoTable = availableTableForTwo.size();
        return (tableNumOfTableForTwo - availableNumOfTwoTable) <= 0 ? 0
                : (tableNumOfTableForTwo - availableNumOfTwoTable);
    }

    public void reserveTwoSeatsTable(int tableId, int reservedTime) {
        for (TableforTwo t : availableTableForTwo) {
            if (t.getTableId() == tableId) {
                if (t.reservedTimeIsAllowed(reservedTime) == true) {
                    t.setReservedStatus(reservedTime);
                    System.out.printf("Successfully reserved Two-Seats table of id: %d at time of %d", tableId,
                            reservedTime);
                } else {
                    System.out.printf(
                            "Selected Two-Seats table of id: %d is not available at time of %d. Please input a valid timeslot",
                            tableId, reservedTime);
                }
            } else {
                System.out.printf(
                        "Didn't find the Two-Seats Table of id: %d, Please check out your input and input again!",
                        tableId, reservedTime);
            }

        }
    }

    public void reserveFourSeatsTable(int tableId, int reservedTime) {
        for (TableforFour t : availableTableForFour) {
            if (t.getTableId() == tableId) {
                if (t.reservedTimeIsAllowed(reservedTime) == true) {
                    t.setReservedStatus(reservedTime);
                    System.out.printf("Successfully reserved Two-Seats table of id: %d at time of %d", tableId,
                            reservedTime);
                } else {
                    System.out.printf(
                            "Selected Two-Seats table of id: %d is not available at time of %d. Please input a valid timeslot",
                            tableId, reservedTime);
                }
            } else {
                System.out.printf(
                        "Didn't find the Two-Seats Table of id: %d, Please check out your input and input again!",
                        tableId, reservedTime);
            }

        }
    }

    public void reserveTable(int tableCapacity, int tableId, int reservedTime) {
        if (tableCapacity == 2) {
            reserveTwoSeatsTable(tableId, reservedTime);
        }
        else if (tableCapacity == 4) {
            reserveFourSeatsTable(tableId, reservedTime);
        }
        else if
    }

    public void showReservationTable() {

    }

    private String checkTheTableStdfatus(int tableNumOfTableForTwo, int tableNumOfTableForFour,
            int tableNumOfTableForEight) {
        int availableNumOfTwoTable = availableTableForTwo.size();
        int availableNumOfFourTable = availableTableForFour.size();
        int availableNumOfEightTable = availableTableForEight.size();

        return "ssfs";
    }

    public ArrayList showCurrentTables() {
        return this.alltables;
    }
    // 三个arraylist available（可用的）, reserved（被预定了的但是还没来）, occupied（占用的）,
    // available里面包括 没被预定的桌子且没被占用，和预定了但是在时间范围前没来的桌子
    // reserved是被预定的了的且在时间范围内的桌子
    // occupied是现在已经被占用的桌子
    // 重点在于桌子state的转换，state pattern
    // 对应桌子的二人桌子，四人桌子，8人桌子，用subclass实现

}
