package GoGoEat;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;

//singleton pattern
public class TablesManagement implements TimeObserver {
    private static LocalDate currDate;
    private static LocalTime currTime;
    private TableArrangementAlgorithm algorithm;

    // Singleton Pattern
    private static final TablesManagement instance = new TablesManagement();

    public static TablesManagement getInstance() {

        return instance;
    }

    private TablesManagement() {
        allTableIds = new ArrayList<Integer>();
        reservedTables = new ArrayList<Table>();
        availableTables = new ArrayList<Table>();
        occupiedTables = new ArrayList<Table>();
        tableCapacityTypeList = new ArrayList<Integer>();
        waitingCustomers = new ArrayList<String>();
        Collections.sort(tableCapacityTypeList, Collections.reverseOrder());
        this.algorithm = DefaultTableArrangementAlgorithm.getInstance();
    }

    public void clear() {
        allTableIds.clear();
        reservedTables.clear();
        availableTables.clear();
        occupiedTables.clear();
        tableCapacityTypeList.clear();
    }

    @Override
    public void timeUpdate(LocalTime newTime) {
        currTime = newTime;
        updateStatusAccordingToTime();
    }

    @Override
    public void dateUpdate(LocalDate newDate) {
        currDate = newDate;
        updateStatusAccordingToDate();
    }

    public void updateStatusAccordingToTime() {

        ArrayList<Table> temp = new ArrayList<>();

        for (Table t : availableTables) {
            if (t.toBeReserved(currTime) != -1) {
                temp.add(t);
                reservedTables.add(t);
            }
        }
        availableTables.removeAll(temp);
        temp.clear();

        for (Table t : occupiedTables) {
            if (t.toBeReserved(currTime) == 0) {
                temp.add(t);
                reservedTables.add(t);
            }
        }
        occupiedTables.removeAll(temp);
    }

    public void updateStatusAccordingToDate() {

        for (Table t : reservedTables) {
            setTableFromReservedToAvailable(t.getTableId());
        }
        for (Table t : occupiedTables) {
            setTableFromOccupiedToAvailable(t.getTableId());
        }
        for (Table t : returnAllTablesList()) {
            t.startNewDay();
        }
    }

    // Store all table id
    private ArrayList<Integer> allTableIds;

    /*
     * Three ArrayList:
     * 1. available - Not occupied nor reserved
     * 2. reserved - being reserved but customer hasnt come
     * 3. occupied - Sit down and dining
     */
    private ArrayList<Table> availableTables;
    private ArrayList<Table> reservedTables;
    private ArrayList<Table> occupiedTables;

    /*
     * Store Customers that is waiting for table
     * NOTE: There should also be a list in customer storing waiting table and
     * occupied table
     */
    private ArrayList<String> waitingCustomers;

    // List to store table capacity, order by capacity from large to small
    // 8, 4, 2
    private ArrayList<Integer> tableCapacityTypeList;

    public ArrayList<Table> getReservedTablesfromId(ArrayList<Integer> ids) {
        ArrayList<Table> tables = new ArrayList<>();
        for (Integer id : ids) {
            for (Table t : reservedTables) {
                if (t.getId() == id) {
                    tables.add(t);
                }
            }
        }
        return tables;
    }

    /*
     * For initialization
     */

    public void appendToAllTableIds(int id) {
        allTableIds.add(id);
    }

    /*
     * NOTE: The Below List of table arrangement result arrange by capacity
     * e.g. Now has 2-[8-seat], 7-[4-seat], 9-[2-seat]
     * then integer in arraylist should be 2, 7, 9 (index from 0 to 2)
     */

    public ArrayList<Integer> initializeTableArrangementList() {
        ArrayList<Integer> tableArrangementResults = new ArrayList<Integer>();
        for (int i = 0; i < tableCapacityTypeList.size(); i++) {
            tableArrangementResults.add(i, 0);
        }
        return tableArrangementResults;
    }

    /*
     * Arrange table according to people num
     * Simple logic:
     * - minimize the number of tables but seat all people.
     * - In the meantime, people should sit at the "most appropriate" table
     * 
     * For testing： assume now table types: 15，12， 8，4，2 five table types, then:
     * 1.simplest: 1 people - [1] 2-seats table
     * 2.2 people -> [1] 2-seats table
     * 3.3 people -> [1] 4-seats table
     * 4.5 people -> [1] 8-seats table
     * 5.9 people -> [1] 12-seats table
     * 6.17 people -> [1] 15-seats table + [1] 2-seats table
     * 7.19 people -> [1] 15-seats table + [1] 4-seats table
     * 8.23 people -> [1] 15-seats table + [1] 8-seats table
     * 
     * The tableArrangements stores the num of tables according to table types
     * index=0: stores the num of tables with the max table capacity
     * index=max: stores the num of tables with the minimum table capacity
     */

    public ArrayList<Integer> getTableArrangement(int peopleNum) throws ExPeopleNumExceedTotalCapacity {
        return algorithm.getTableArrangementResult(peopleNum, availableTables, tableCapacityTypeList,
                returnAllTablesList());
    }

    /*
     * Precondition:
     * - The previous table arrangement results is not all available
     * - Arrange the tables acoording to the available table and still based on the
     * purpose of minimizing the table num:
     * 
     * Two situations：
     * 1. All the available tables with capacity less than people num add up to not
     * enough to sit on, that is, return null
     * 
     * Test 1:
     * When available tables: one for two, one for four, one for eight;
     * But eighteen people came; return null
     * 
     * 2. When able to sit in: The returned value will be an ArrayList storing the
     * number of table assigned of different capacity
     * 
     * Test: When available tables: one for two, one for four, one for eight;
     * Fourteen people came; return the corresponding array list
     * 
     * 如果之前自动的安排不能直接入座则启用该算法
     * 这算法：
     * 1. 把available的table list按降序排列，然后从第一个小于当前桌子人数的桌子开始，依次放入，若能放完则output出结果
     * 2. 若不能放完，则output没有优化结果
     */

    /*
     * Determine: whether the table arrangement results is available now
     * 1.Yes: return true；
     * 2.No: return false
     *
     * Testing:
     * - Need to know the current available tables
     * - Then you may create a arraylist as table arrangemnet results,
     * and put it into the argument
     */

    public boolean canDirectlyDineIn(ArrayList<Integer> tableArrangementResults) {
        boolean canDirectlyWalkIn = true;
        StringBuilder waitingTablesListMessage = new StringBuilder(
                "For default arrangements, you still need to wait for: ");
        for (int i = 0; i < tableArrangementResults.size(); i++) {
            int tableCapacityType = tableCapacityTypeList.get(i);
            int availableNumOfThisTableType = returnAvailableTableNumWithCapacity(tableCapacityType);
            int num = tableArrangementResults.get(i);
            if (num > availableNumOfThisTableType) {
                int numOfWaitingTables = num - availableNumOfThisTableType;
                canDirectlyWalkIn = false;
                waitingTablesListMessage
                        .append(String.format("\n[%d] [%d-Seats] Table(s) ", numOfWaitingTables, tableCapacityType));
            }
        }
        if (canDirectlyWalkIn) {
            return true;
        }
        System.out.println(waitingTablesListMessage);
        return false;

    }

    /*
     * Usage: when the 用于当根据人数计算桌子或推荐算法中某一个可行时，且顾客选择入座，则用此函数入座
     * 测试：自己建一个arraylist存放对应桌型的安排数量 作为argument；
     * 1.首先看return的结果是不是对的：可以顺利入座true，不可以顺利入座false
     * 2.当可以顺利入座这种情况发生时，执行完该函数后，去get available tables list看对应的table是否变成了occupied
     */

    public ArrayList<Integer> setWalkInStatus(ArrayList<Integer> tableArrangement) {
        ArrayList<Integer> checkedInTableIds = new ArrayList<Integer>();
        for (Integer tableCapacity : tableCapacityTypeList) {
            int needOfThisTableCapcity = tableArrangement.get(tableCapacityTypeList.indexOf(tableCapacity));
            if (needOfThisTableCapcity > 0) {
                int tmpCount = 0;
                ArrayList<Table> copyOfAvailableTables = new ArrayList<Table>();
                copyOfAvailableTables.addAll(availableTables);
                for (Table t : copyOfAvailableTables) {
                    if (t.getTableCapacity() == tableCapacity) {
                        tmpCount++;
                        checkedInTableIds.add(t.getTableId());
                        setTableFromAvailableToOccupiedStatus(t.getTableId());
                        if (tmpCount == needOfThisTableCapcity) {
                            break;
                        }
                    }
                }
            }
        }
        System.out.println("Successfully Dine In!");
        return checkedInTableIds;
    }

    /*
     * According to table arrangements, put the table that are available into
     * customer's occupied, and store the remaining into wairing list
     */
    public ArrayList<Integer> setWaitingTables(String cId, ArrayList<Integer> tableArrangement) {
        ArrayList<Integer> waitingTablesNumList = new ArrayList<Integer>();
        ArrayList<Integer> checkedInTableIds = new ArrayList<Integer>();
        waitingCustomers.add(cId);
        waitingTablesNumList.addAll(initializeTableArrangementList());
        StringBuilder waitingTablesListMessage = new StringBuilder(
                "For selected arrangements, you still need to wait for: \n");
        for (Integer tableCapacityType : tableCapacityTypeList) {
            int index = tableCapacityTypeList.indexOf(tableCapacityType);
            int needOfThisTableCapcity = tableArrangement.get(index);
            if (needOfThisTableCapcity > 0) {
                int tmpCount = 0;
                ArrayList<Table> copyOfAvailableTables = new ArrayList<Table>();
                copyOfAvailableTables.addAll(availableTables);
                for (Table t : copyOfAvailableTables) {
                    if (t.getTableCapacity() == tableCapacityType) {
                        tmpCount++;
                        checkedInTableIds.add(t.getTableId());
                        setTableFromAvailableToOccupiedStatus(t.getTableId());
                        if (tmpCount == needOfThisTableCapcity) {
                            break;
                        }
                    }
                }
                // if available num is not enough, then pass the remaining num to customer
                if (tmpCount < needOfThisTableCapcity) {
                    int waitingCount = needOfThisTableCapcity - tmpCount;
                    waitingTablesNumList.set(index, waitingCount);
                    waitingTablesListMessage.append(String.format("[%d] [%d-Seats] Table\n", waitingCount,
                            tableCapacityType));
                } else {
                    waitingTablesNumList.set(index, 0);
                }
            }
        }
        CommandCustomerDineIn.addCheckInInfo(checkedInTableIds);
        CommandCustomerDineIn.addWaitingInfo(waitingTablesNumList);
        System.out.println(waitingTablesListMessage);
        return waitingTablesNumList;
    }

    public String showReservationTable() {
        StringBuilder showReservationTableMsg = new StringBuilder(
                "\nTable(s) for tomorrow reservation and available time slots: \n");
        ArrayList<Table> all = returnAllTablesList();
        Collections.sort(all, Collections.reverseOrder());
        for (Table t : all) {
            TimeSlots tmrReservationTimeslots = t.getTmrReservationTimeSlot();
            showReservationTableMsg.append(String.format(
                    "%d-Seats Table with ID of %d is available tmr for the timeslots: %s \n", t.getTableCapacity(),
                    t.getTableId(), tmrReservationTimeslots.getAvailableSlots()));
        }
        System.out.println(showReservationTableMsg);
        return showReservationTableMsg.toString();
    }

    // Show list of all tables
    public void showAllTables() {
        StringBuilder showReservationTableMsg = new StringBuilder(
                "\nList of all Tables: \n");
        ArrayList<Table> all = returnAllTablesList();
        Collections.sort(all, Collections.reverseOrder());
        for (Table t : all) {
            showReservationTableMsg.append(String.format(
                    "%d-Seats Table | ID: %d\n", t.getTableCapacity(),
                    t.getTableId()));
        }
        System.out.println(showReservationTableMsg);
    }

    // show the available num of each table type
    public String showAvailableTables() {
        StringBuilder showAvailableTableMsg = new StringBuilder("\nBelow is the available tables: \n");
        for (int index = tableCapacityTypeList.size() - 1; index >= 0; index--) {
            int tableCapacity = tableCapacityTypeList.get(index);
            showAvailableTableMsg.append(String.format("Num of Available %d-Seats Table: %d \n", tableCapacity,
                    returnAvailableTableNumWithCapacity(tableCapacity)));
        }
        System.out.println(showAvailableTableMsg);
        return showAvailableTableMsg.toString();
    }

    /*
     * Admin can add new Table：
     * 1.If the capacity of the new table already exist -> Add directly
     * 2.If the capacity did not exist -> Add capacity then Table
     */
    public void addNewTable(int tableId, int tableCapacity) throws ExTableIdAlreadyInUse {
        if (checkTableIdIsAreadyInUsed(tableId) == false) {
            allTableIds.add(tableId);
            if (tableCapacityTypeList.contains(tableCapacity)) {
                availableTables.add(new Table(tableId, tableCapacity));
            } else {
                tableCapacityTypeList.add(tableCapacity);
                Collections.sort(tableCapacityTypeList, Collections.reverseOrder());
                availableTables.add(new Table(tableId, tableCapacity));
            }
            System.out.printf("Successfully add table with ID of %d, capacity of %d \n", tableId, tableCapacity);
        } else {
            throw new ExTableIdAlreadyInUse(tableId);
        }
    }

    public void removeTableCapacity(int tableCapacity) {
        ArrayList<Integer> copyOfTablCapacityTypeList = new ArrayList<Integer>();
        copyOfTablCapacityTypeList.addAll(tableCapacityTypeList);
        for (Integer tCapacity : copyOfTablCapacityTypeList) {
            if (tCapacity == tableCapacity) {
                tableCapacityTypeList.remove(tCapacity);
            }
        }
        Collections.sort(tableCapacityTypeList, Collections.reverseOrder());
    }

    /*
     * Admin Remove table:
     * 1. If the status of the table is available -> Delete directly
     * 2. Table is not in availableTable list -> Cannot delete
     * (Could be table not exist or table unavailable)
     */
    public void removeTable(int tableId) throws ExTableNotExist, ExUnableToRemoveTable {
        Table t = returnTableAccordingToTableId(tableId);
        if (t != null) {
            if (availableTables.contains(t) && t.noReservationForTodayAndTmr() == true) {
                ArrayList<Integer> copyOfAllTableIds = new ArrayList<Integer>();
                copyOfAllTableIds.addAll(allTableIds);
                for (Integer tId : copyOfAllTableIds) {
                    if (tId == tableId) {
                        allTableIds.remove(tId);
                    }
                }
                availableTables.remove(t);
                if (returnTableNumWithTableCapacity(t.getTableCapacity()) == 0) {
                    removeTableCapacity(t.getTableCapacity());
                }
                System.out.printf("Successfully delete the table with id of %d \n", tableId);
                return;
            } else {
                throw new ExUnableToRemoveTable(tableId);
            }
        }
        throw new ExTableNotExist(tableId);

    }

    // Change table from available to reserved
    public void setTableFromAvailableToReservedStatus(int tableId) {
        Table t = returnTableAccordingToTableId(tableId);
        availableTables.remove(t);
        reservedTables.add(t);
    }

    // Change table from occupied to reserved
    public void setTableFromOccupiedToReserved(int tableId) {
        Table t = returnTableAccordingToTableId(tableId);
        occupiedTables.remove(t);
        reservedTables.add(t);
    }

    // Change table from available to occupied
    public void setTableFromAvailableToOccupiedStatus(int tableId) {
        Table t = returnTableAccordingToTableId(tableId);
        availableTables.remove(t);
        occupiedTables.add(t);
    }

    // Change table from reserved to occupied
    public void setTableFromReservedToOccupiedStatus(int tableId) {
        Table t = returnTableAccordingToTableId(tableId);
        reservedTables.remove(t);
        occupiedTables.add(t);
    }

    // Change table from occupied to available
    public void setTableFromOccupiedToAvailable(int tableId) {
        Table t = returnTableAccordingToTableId(tableId);
        occupiedTables.remove(t);
        availableTables.add(t);
    }

    // Change table from reserved to available
    public void setTableFromReservedToAvailable(int tableId) {
        Table t = returnTableAccordingToTableId(tableId);
        reservedTables.remove(t);
        availableTables.add(t);
    }

    public ArrayList<Table> getAvailableTables() {
        return availableTables;
    }

    public ArrayList<Table> getReservedTables() {
        return reservedTables;
    }

    public ArrayList<Table> getOccupiedTables() {
        return occupiedTables;
    }

    // Check if the tableID has been added
    public boolean checkTableIdIsAreadyInUsed(int tableId) {
        for (int i : allTableIds) {
            if (i == tableId) {
                return true;
            }
        }
        return false;
    }

    public Table returnTableAccordingToTableId(int tableId) {
        ArrayList<Table> allTablesList = new ArrayList<>();
        allTablesList.addAll(availableTables);
        allTablesList.addAll(reservedTables);
        allTablesList.addAll(occupiedTables);
        for (Table t : allTablesList) {
            if (t.getTableId() == tableId) {
                return t;
            }
        }
        return null;
    }

    public ArrayList<Table> returnAllTablesList() {
        ArrayList<Table> allTablesList = new ArrayList<>();
        allTablesList.addAll(availableTables);
        allTablesList.addAll(reservedTables);
        allTablesList.addAll(occupiedTables);
        return allTablesList;
    }

    // Return number of table corresponding to the capacity
    public int returnTableNumWithTableCapacity(int tableCapacity) {
        ArrayList<Table> allTablesList = returnAllTablesList();
        int num = 0;
        for (Table t : allTablesList) {
            if (t.getTableCapacity() == tableCapacity) {
                num++;
            }
        }
        return num;
    }

    /*
     * Reserve Table
     * Add the reservation timeslot to the tmrReservationTimeslot
     * of each of the table
     */
    public Boolean reserveTableAccordingToTimeslot(int tableId, TimeSlot timeslot)
            throws ExTableNotExist, ExTimeSlotAlreadyBeReserved {
        Table table = returnTableAccordingToTableId(tableId);
        if (table == null) {
            throw new ExTableNotExist(tableId);
        }
        TimeSlots tmrReservationTimeslots = table.getTmrReservationTimeSlot();
        Boolean reserved = tmrReservationTimeslots.addSlot(timeslot);
        if (reserved == false) {
            throw new ExTimeSlotAlreadyBeReserved(tableId, timeslot);
        }
        System.out.printf("\nTable with id of %d is succesfully reserved between %s.", tableId,
                timeslot.toString());
        return true;
    }

    // Cancel the reservation of the corresponding table
    public void cancelReservationAccordingToTimeslot(int tableId, TimeSlot timeslot)
            throws ExTableNotExist, ExTimeSlotNotReservedYet {
        Table t = returnTableAccordingToTableId(tableId);
        if (t == null) {
            throw new ExTableNotExist(tableId);
        }
        TimeSlots tmrTimeSlots = t.getTmrReservationTimeSlot();
        if (tmrTimeSlots.remove(timeslot) == false) {
            throw new ExTimeSlotNotReservedYet(tableId, timeslot);
        }
        t.updatetmrTimeslots(tmrTimeSlots);

        // System.out.println(t.getTmrReservationTimeSlot().getAvailableSlots());
    }

    public int returnAvailableTableNumWithCapacity(int tableCapacityType) {
        int numOfAvailableForCurrentTableCapacity = 0;
        for (Table t : availableTables) {
            if (t.getTableCapacity() == tableCapacityType) {
                numOfAvailableForCurrentTableCapacity++;
            }
        }
        return numOfAvailableForCurrentTableCapacity++;
    }

    public void checkOutByCustomer(ArrayList<Integer> tableId) {
        final Database database = Database.getInstance();
        for (Integer tId : tableId) {
            setTableFromOccupiedToAvailable(tId);
            Table t = returnTableAccordingToTableId(tId);
            int capacityIndex = tableCapacityTypeList.indexOf(t.getTableCapacity());
            for (String id : waitingCustomers) {
                // get ith in waitingNumList
                Customers customer;
                try {
                    customer = database.matchCId(id);
                    if (customer.getithTableNumList(capacityIndex) > 0) {
                        customer.minusOneTableNumList(capacityIndex);
                        customer.addOccupiedTable(t.getTableId());
                        setTableFromAvailableToOccupiedStatus(tId);
                        System.out.printf("\nCustomer with Id of %s now occupied a new table with id of %d", id,
                                t.getTableId());
                        break;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public int returnTotalCapcityOfTables() {
        int total = 0;
        for (Table t : returnAllTablesList()) {
            total += t.getTableCapacity();
        }
        return total;
    }

    public void reserverCheckIn(int tableId, TimeSlot ts, String cId) throws ExCustomersIdNotFound {
        final Database database = Database.getInstance();
        Table t = returnTableAccordingToTableId(tableId);
        t.removeTdayTimeslot(ts);
        Customers customer = database.matchCId(cId);
        customer.addOccupiedTable(t.getTableId());
        // setTableFromAvailableToOccupiedStatus(tableId); - bug
        setTableFromReservedToOccupiedStatus(tableId);
    }

    public LocalTime[] getReservationStartEndInDayOfTables(ArrayList<Table> tables, LocalTime start, LocalTime end) {
        for (Table tb : tables) {
            LocalTime[] temp = tb.getTmrReservationTimeSlot().getReservationStartEndInDay();
            if (temp == null) {
                continue;
            }
            if (temp[0].compareTo(start) < 0) {
                start = temp[0];
            }
            if (temp[1].compareTo(end) > 0) {
                end = temp[1];
            }
        }
        return new LocalTime[] { start, end };
    }

    public boolean setOpenAndCloseTime(String timeString) throws ExUnableToSetOpenCloseTime, ExTimeFormatInvalid {
        LocalTime start = LocalTime.of(23, 59);
        LocalTime end = LocalTime.of(0, 0);
        LocalTime[] temp;

        temp = getReservationStartEndInDayOfTables(availableTables, start, end);
        temp = getReservationStartEndInDayOfTables(reservedTables, temp[0], temp[1]);
        temp = getReservationStartEndInDayOfTables(occupiedTables, temp[0], temp[1]);
        start = temp[0];
        end = temp[1];

        String input[] = timeString.split("-");
        String open = input[0];
        String close = input[1];

        try {
            LocalTime inputOpen = LocalTime.parse(open);
            LocalTime inputClose = LocalTime.parse(close);
            if (start.compareTo(TimeSlots.getOpenTime()) == 0 && end.compareTo(TimeSlots.getCloseTime()) == 0) {
                TimeSlots.setOpenAndCloseTime(open, close);
                return true;
            }
            if (inputOpen.compareTo(start) > 0 || inputClose.compareTo(end) < 0) {
                TimeSlot ts = new TimeSlot(start, end, null);
                throw new ExUnableToSetOpenCloseTime(ts);
            }

            TimeSlots.setOpenAndCloseTime(open, close);
            return true;
        } catch (DateTimeParseException e) {
            throw new ExTimeFormatInvalid();
        }

    }

    public void toRecommendAlgo() {
        algorithm = RecommendedTableArrangementAlgorithm.getInstance();
    }

    public void toDefaultAlgo() {
        algorithm = DefaultTableArrangementAlgorithm.getInstance();
    }

    public boolean waitingCustomersContains(String c) {
        return waitingCustomers.contains(c);
    }
}