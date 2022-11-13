import java.time.*;
import java.util.ArrayList;
import java.util.Collections;

//singleton pattern
public class TablesManagement implements TimeObserver {
    private static LocalDate currDate;
    private static LocalTime currTime;

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
                // (unsolved) notify customers to check out
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

    // 存放已有桌子的table id
    private ArrayList<Integer> allTableIds;
    // 三个arraylist available（可用的）, reserved（被预定了的但是还没来）, occupied（占用的）,
    // available里面包括 没被预定的桌子且没被占用，和预定了但是在时间范围前没来的桌子
    private ArrayList<Table> availableTables;
    // reserved是被预定的了的且在时间范围内的桌子
    private ArrayList<Table> reservedTables;
    // occupied是现在已经被占用的桌子
    private ArrayList<Table> occupiedTables;
    // 放正在等的桌子的customer； 注意customer里面应该有一个list存放起等待的桌子和occupy的桌子
    private ArrayList<String> waitingCustomers;
    // 存放桌型的列表，按从大到小的顺序排列 e.g. 10, 8, 4,2
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

    public void appendToAvailableTables(Table table) {
        availableTables.add(table);
    }
    // !!!!!!!!!!!!!!!!!!!!!
    // 注意以下的table Arrangement results之类的list全部是按桌型的大小顺序存放的
    // 比如现在有两张八人桌， 七张四人桌， 九张二人桌
    // 则arraylist的数字应该是 2，7，9（index 从0到2）

    // sigleton pattern
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
    }

    public ArrayList<Integer> initializeTableArrangementList() {
        ArrayList<Integer> tableArrangementResults = new ArrayList<Integer>();
        for (int i = 0; i < tableCapacityTypeList.size(); i++) {
            tableArrangementResults.add(i, 0);
        }
        return tableArrangementResults;
    }

    // 这部分我没有测试过，所以可能会有不少bug (^-^)见谅^_^
    // 根据输入的人数判断安排对应桌型的数量
    // 简单来说就是用最少的桌子让顾客坐下，但同时也得满足桌子不能浪费，即顾客必须得坐最适合自己的桌子
    // 测试：打比方： 假设现在只有15，12， 8，4，2这五种类型的桌子， 那么即有以下几种情况
    // 这一部分是纯理论，不用考虑相应桌型数量不够的情况
    // 1.最简单的：一个人来让坐两人桌
    // 2.二人来：两人桌
    // 3.三人来：四人桌
    // 4.五人来：八人桌
    // 5.九人来：十二人桌
    // 6.十七人来： 十五人桌+二人桌
    // 7.十九人来： 十五人桌+四人桌
    // 8.二十三人来： 十五人桌+八人桌
    // 最后返回的是一个array list存放不同桌型安排的数量
    // index为0时，那个返回数字代表的是最大的桌子的安排数量
    // index最大的那一个，返回的数字代表的是最小桌子的安排数量
    public ArrayList<Integer> arrangeTableAccordingToNumOfPeople(int peopleNum) throws ExPeopleNumExceedTotalCapacity {
        if (peopleNum <= returnTotalCapcityOfTables()) {
            int tmpPeopleNum = peopleNum;
            StringBuilder arrangementResultMessage = new StringBuilder("\nYour arranged tables are: \n");
            // 按顺序储存的对应的桌型安排的数量
            ArrayList<Integer> tableArrangementResults = new ArrayList<Integer>();
            tableArrangementResults.addAll(initializeTableArrangementList());
            for (int i = 0; i < tableCapacityTypeList.size(); i++) {
                int tmpResults = 0;
                int tableCapacity = tableCapacityTypeList.get(i);
                // 这一步是指当执行到最后一步时，即最小桌型时，若人数为0则0，若大于0则1
                if (i == tableCapacityTypeList.size() - 1) {
                    tmpResults = (tmpPeopleNum % tableCapacity == 0) ? (tmpPeopleNum / tableCapacity)
                            : ((tmpPeopleNum / tableCapacity) + 1);
                } else {
                    // 最佳的是把人放置于能装下他们的最小桌子， e.g. 假设有2，4，8人桌； 7人来放8人桌
                    int addingTableNum = (tmpPeopleNum
                            / tableCapacity <= returnTableNumWithTableCapacity(tableCapacity))
                                    ? (tmpPeopleNum / tableCapacity)
                                    : returnTableNumWithTableCapacity(tableCapacity);
                    tmpResults += addingTableNum;
                    tmpPeopleNum = tmpPeopleNum - tableCapacity * addingTableNum;
                    if (tableCapacity >= tmpPeopleNum
                            && tmpPeopleNum > tableCapacityTypeList.get(i + 1)) {
                        tmpResults += 1;
                        tmpPeopleNum = 0;
                    }
                }
                if (tmpResults > 0) {
                    arrangementResultMessage
                            .append(String.format("[%d] [%d-Seats] Tables \n", tmpResults, tableCapacity));
                }
                tableArrangementResults.set(i, tmpResults);
                if (tmpPeopleNum == 0) {
                    break;
                }
            }
            System.out.println(arrangementResultMessage);
            return tableArrangementResults;
        }
        throw new ExPeopleNumExceedTotalCapacity(returnTotalCapcityOfTables());
    }

    // （测试不用管后面这一句话）前提上一种结果不可用，即没有比人数更大的桌型available
    // 这部分是指按现有的availableTables里的table按从大到小让顾客坐下
    // 返回有两种情况：
    // 1.所有的比人数更少的available table加在一起都不够坐，即return null
    // 测试：当available tables： 一张二人桌，一张四人桌，一张八人桌； 但来了十八个人；return null
    // 2.可以坐的情况：最后返回的是一个array list存放不同桌型安排的数量
    // 测试：当available tables： 一张二人桌，一张四人桌，一张八人桌； 来了十四个人；return 相应的array list
    // 如果之前自动的安排不能直接入座则启用该算法
    // 这算法：
    // 1. 把available的table list按降序排列，然后从第一个小于当前桌子人数的桌子开始，依次放入，若能放完则output出结果
    // 2. 若不能放完，则output没有优化结果
    public ArrayList<Integer> recommendedArrangementAccordingToWaitingTime(int peopleNum) {
        Collections.sort(availableTables, Collections.reverseOrder());
        int tmpPeopleNum = peopleNum;
        ArrayList<Integer> tableArrangementResults = new ArrayList<Integer>();
        tableArrangementResults.addAll(initializeTableArrangementList());
        for (Table t : availableTables) {
            if (t.getTableCapacity() < peopleNum) {
                int tCapacity = t.getTableCapacity();
                int capacityIndex = tableCapacityTypeList.indexOf(tCapacity);
                int num = tableArrangementResults.get(capacityIndex);
                tableArrangementResults.set(tableCapacityTypeList.indexOf(tCapacity), num + 1);
                tmpPeopleNum = tmpPeopleNum - tCapacity;
                if (tmpPeopleNum <= 0) {
                    break;
                }
            }
        }
        if (tmpPeopleNum > 0) {
            System.out.println("No Optimized Recommended Arrangements!");
            return null;
        } else {
            StringBuilder recommendedArrangementMsg = new StringBuilder("The Optimized Recommended Arrangements are: ");
            for (int i = 0; i < tableArrangementResults.size(); i++) {
                if (tableArrangementResults.get(i) > 0) {
                    int tCapacity = tableCapacityTypeList.get(i);
                    recommendedArrangementMsg
                            .append(String.format("\n[%d] [%d-seats] ", tableArrangementResults.get(i), tCapacity));
                }
            }
            System.out.println(recommendedArrangementMsg);
            return tableArrangementResults;
        }

    }

    // 根据安排的桌子结果判断顾客能否直接进店吃：
    // 1.可以,则return true； （测试不用管后面这一句话）且print出桌子的安排
    // 2.不可以，则return false
    // 测试：首先你得确保availableTables 有哪些；
    // 然后你自己建一个arraylist存放一个要测试的arraylist作为argument，放进去；
    // 最后看print的结果是否是对的
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

    // （测试不用管后面这一句话）用于当根据人数计算桌子或推荐算法中某一个可行时，且顾客选择入座，则用此函数入座
    // 测试：自己建一个arraylist存放对应桌型的安排数量 作为argument；
    // 1.首先看return的结果是不是对的：可以顺利入座true，不可以顺利入座false
    // 2.当可以顺利入座这种情况发生时，执行完该函数后，去get available tables list看对应的table是否变成了occupied
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
                        // 把这个table放进customer占用的arraylist里去
                        // c.occupyTable(t);
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

    // Again，Sorry，这部分我也没有测试过，所以可能会有不少bug (^-^)见谅^_^
    // 用于根据安排结果，返回顾客需要等待的对应桌型的数量的list
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
                // 如果该桌型当前的还没够，那么就将待等桌型及等待的数量传递给customer
                if (tmpCount < needOfThisTableCapcity) {
                    int waitingCount = needOfThisTableCapcity - tmpCount;
                    waitingTablesNumList.set(index, waitingCount);
                    waitingTablesListMessage.append(String.format(" [%d] [%d-Seats] Table ", waitingCount,
                            tableCapacityType));
                } else {
                    waitingTablesNumList.set(index, 0);
                }
            }
        }
        CustomerModule.addCheckInAndWaitingInfo(cId, checkedInTableIds, waitingTablesNumList);
        System.out.println(waitingTablesListMessage);
        return waitingTablesNumList;
    }

    // 展示各自table的课预定时间段
    public String showReservationTable() {
        StringBuilder showReservationTableMsg = new StringBuilder(
                "\nTable(s) for tomorrow reservation and available time slots: \n");
        ArrayList<Table> copyOfAvailableTables = new ArrayList<Table>();
        copyOfAvailableTables.addAll(availableTables);
        Collections.sort(copyOfAvailableTables);
        for (Table t : availableTables) {
            TimeSlots tmrReservationTimeslots = t.getTmrReservationTimeSlot();
            showReservationTableMsg.append(String.format(
                    "%d-Seats Table with ID of %d is available tmr for the timeslots: %s \n", t.getTableCapacity(),
                    t.getTableId(), tmrReservationTimeslots.getAvailableSlots()));
        }
        System.out.println(showReservationTableMsg);
        return new String(showReservationTableMsg);
    }

    // 展示所有桌型的available的数量
    public String showAvailableTables() {
        StringBuilder showAvailableTableMsg = new StringBuilder("\nBelow is the available tables: \n");
        for (Integer tableCapacity : tableCapacityTypeList) {
            showAvailableTableMsg.append(String.format("Num of Available %d-Seats Table: %d \n", tableCapacity,
                    returnAvailableTableNumWithCapacity(tableCapacity)));
        }
        System.out.println(showAvailableTableMsg);
        return showAvailableTableMsg.toString();
    }

    // admin可以添加新的桌子：1.该桌型之前就存在直接加桌子 2.该桌型之前不存在，加桌型和桌子
    // 测试
    // 15,12,8,4,2
    // 16
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
        tableCapacityTypeList.remove(tableCapacity);
        Collections.sort(tableCapacityTypeList, Collections.reverseOrder());
    }

    // admin删除旧桌子: 1.桌子是available的直接delete就ok 2.桌子不在available
    // list里可能是因为根本不available或根本没有这张桌子，统一print不能删除
    // 测试：1.桌子本身available 2.桌子不available 3.桌子根本就没有
    public void removeTable(int tableId) throws ExTableNotExist {
        Table t = returnTableAccordingToTableId(tableId);
        if (t != null) {
            allTableIds.remove(tableId);
            availableTables.remove(t);
            if (returnTableNumWithTableCapacity(t.getTableCapacity()) == 0) {
                removeTableCapacity(t.getTableCapacity());
            }
            System.out.printf("Successfully delete the table with id of %d \n", tableId);
            return;
        }
        throw new ExTableNotExist(tableId);

    }

    // 把table从available变为reserved
    public void setTableFromAvailableToReservedStatus(int tableId) {
        Table t = returnTableAccordingToTableId(tableId);
        availableTables.remove(t);
        reservedTables.add(t);
    }

    // 把table从available变为occupied
    public void setTableFromAvailableToOccupiedStatus(int tableId) {
        Table t = returnTableAccordingToTableId(tableId);
        availableTables.remove(t);
        occupiedTables.add(t);
    }

    // 把table从reserved变为occupied
    public void setTableFromReservedToOccupiedStatus(int tableId) {
        Table t = returnTableAccordingToTableId(tableId);
        reservedTables.remove(t);
        occupiedTables.add(t);
    }

    // 把table从occupied变为available
    public void setTableFromOccupiedToAvailable(int tableId) {
        Table t = returnTableAccordingToTableId(tableId);
        occupiedTables.remove(t);
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

    public ArrayList<Integer> getAllTableIds() {
        return allTableIds;
    }

    public ArrayList<Integer> getTableCapcityTypList() {
        return tableCapacityTypeList;
    }

    // 把table从occupied变为reserved
    public void setTableFromOccupiedToReserved(int tableId) {
        Table t = returnTableAccordingToTableId(tableId);
        occupiedTables.remove(t);
        reservedTables.add(t);
    }

    // 把table从reserved变成available
    public void setTableFromReservedToAvailable(int tableId) {
        Table t = returnTableAccordingToTableId(tableId);
        reservedTables.remove(t);
        availableTables.add(t);
    }

    // check 该table ID是否已经加进去了
    public boolean checkTableIdIsAreadyInUsed(int tableId) {
        for (int i : allTableIds) {
            if (i == tableId) {
                return false;
            }
        }
        return false;
    }

    // 会call customer里的checkWhetherThisIsNeeded(int tableCapacity, int tableId)

    public void removeWaitingCustomer(Customers c) {
        waitingCustomers.remove(c);
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

    // 返回对应capacity的table数量
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

    // 预定桌子；把预定时间添加到对应桌子的明日timeslots里
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
        System.out.printf("\nTable with id of %d is succesfully reserved between %s!", tableId,
                timeslot.toString());
        return true;
    }

    // 如名字所写,取消对应桌子的预定时间段子
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

        System.out.println(t.getTmrReservationTimeSlot().getAvailableSlots());
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
                // get waitingNumList中的第i個
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

    public LocalTime[] getReservationStartEndInDayOfTables(ArrayList<Table> tables, LocalTime start, LocalTime end) {
        for (Table tb : tables) {
            LocalTime[] temp = tb.getTmrReservationTimeSlot().getReservationStartEndInDay();
            if (temp[0].compareTo(start) < 0) {
                start = temp[0];
            }
            if (temp[1].compareTo(end) > 0) {
                end = temp[0];
            }
        }
        return new LocalTime[] { start, end };
    }

    public boolean setOpenAndCloseTime(String open, String close) throws ExUnableToSetOpenCloseTime {
        LocalTime start = LocalTime.of(23, 59);
        LocalTime end = LocalTime.of(0, 0);
        LocalTime[] temp;

        temp = getReservationStartEndInDayOfTables(availableTables, start, end);
        temp = getReservationStartEndInDayOfTables(reservedTables, temp[0], temp[1]);
        temp = getReservationStartEndInDayOfTables(occupiedTables, temp[0], temp[1]);
        start = temp[0];
        end = temp[1];

        System.out.println("#debug: " + start.toString() + " - " + end.toString());

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
    }

}