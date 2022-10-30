import java.time.*;
import java.util.ArrayList;
import java.util.Collections;

//singleton pattern
public class TableManagement implements TimeOvserver {
    private static ManualClock clock = ManualClock.getInstance();
    private static LocalDate currDate = clock.getDate();
    private static LocalTime currTime = clock.getTime();

    @Override
    public void timeUpdate(LocalTime newTime) {
        currTime = newTime;
        updateStatus();
    }

    @Override
    public void dateUpdate(LocalDate newDate) {
        currDate = newDate;
        updateStatus();
    }

    private void updateStatus() {
        // TODO-loop tables and change status
    }

    public void reserve(int customerID, int tableID, LocalDate date, TimeSlot ts) {

    }

    public void cancelReservation(int customerID) {

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
    private ArrayList<Customer> waitingCustomers;
    // 存放桌型的列表，按从大到小的顺序排列 e.g. 10, 8, 4,2
    private ArrayList<Integer> tableCapacityTypeList;

    // !!!!!!!!!!!!!!!!!!!!!
    // 注意以下的table Arrangement results之类的list全部是按桌型的大小顺序存放的
    // 比如现在有两张八人桌， 七张四人桌， 九张二人桌
    // 则arraylist的数字应该是 2，7，9（index 从0到2）

    // sigleton pattern
    private static TableManagement instance = new TableManagement();

    public static TableManagement getInstance() {
        return instance;
    }

    private TableManagement() {
        allTableIds = new ArrayList<Integer>();
        reservedTables = new ArrayList<Table>();
        availableTables = new ArrayList<Table>();
        occupiedTables = new ArrayList<Table>();
        tableCapacityTypeList = new ArrayList<Integer>();
        Collections.sort(tableCapacityTypeList, Collections.reverseOrder());
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
    public ArrayList<Integer> arrangeTableAccordingToNumOfPeople(int peopleNum) {
        int tmpPeopleNum = peopleNum;
        String arrangementResultMessage = "Your arranged tables are: ";
        // 按顺序储存的对应的桌型安排的数量
        ArrayList<Integer> tableArrangementResults = new ArrayList<Integer>();
        for (Integer tableCapacity : tableCapacityTypeList) {
            int tmpResults = 0;
            int capacityIndex = tableCapacityTypeList.indexOf(tableCapacity);
            // 这一步是指当执行到最后一步时，即最小桌型时，若人数为0则0，若大于0则1
            if (capacityIndex == tableCapacityTypeList.size() - 1) {
                tmpResults = (tmpPeopleNum % tableCapacity == 0) ? (tmpPeopleNum / tableCapacity)
                        : ((tmpPeopleNum / tableCapacity) + 1);
            } else {
                // 最佳的是把人放置于能装下他们的最小桌子， e.g. 假设有2，4，8人桌； 7人来放8人桌
                if (tableCapacity >= peopleNum
                        && peopleNum > tableCapacityTypeList.get(capacityIndex)) {
                    tmpResults = 1;
                }
                // 若第一种情况无法满足；即尽可能让他们坐在大的桌子上
                else {
                    tmpResults = tmpPeopleNum / tableCapacity;
                    tmpPeopleNum = tmpPeopleNum % tableCapacity;
                }
            }
            if (tmpResults > 0) {
                arrangementResultMessage += String.format("%d %d-Seats Tables ", tmpResults, tableCapacity);
            }
            tableArrangementResults.add(tmpResults);
        }
        System.out.println(arrangementResultMessage);
        // 通过canDirectlyIn来确定等待/推荐/直接默认入座
        // setWalkInStatus(c, tableArrangementResults,
        // canDirectlyDineIn(peoptableArrangementResults));
        return tableArrangementResults;
    }

    // Sorry，这部分我也没有测试过，所以可能会有不少bug (^-^)见谅^_^
    // （测试不用管后面这一句话）前提上一种结果不可用，即没有比人数更大的桌型available
    // 这部分是指按现有的availableTables里的table按从大到小让顾客坐下
    // 返回有两种情况：
    // 1.所有的比人数更少的available table加在一起都不够坐，即return null
    // 测试：当available tables： 一张二人桌，一张四人桌，一张八人桌； 但来了十八个人；return null
    // 2.可以坐的情况：最后返回的是一个array list存放不同桌型安排的数量
    // 测试：当available tables： 一张二人桌，一张四人桌，一张八人桌； 来了十四个人；return 相应的array list
    public ArrayList<Integer> recommendedArrangementAccordingToWaitingTime(int peopleNum) {
        // 如果之前自动的安排不能直接入座则启用该算法
        // 这算法：
        // 1. 把available的table list按降序排列，然后从第一个小于当前桌子人数的桌子开始，依次放入，若能放完则output出结果
        // 2. 若不能放完，则output没有优化结果
        Collections.sort(availableTables, Collections.reverseOrder());
        int tmpPeopleNum = peopleNum;
        ArrayList<Integer> tableArrangementResults = new ArrayList<Integer>();
        for (int i = 0; i < tableCapacityTypeList.size(); i++) {
            tableArrangementResults.set(i, 0);
        }

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
            String recommendedArrangementMsg = "The Optimized Recommended Arrangements are: ";
            for (Integer i : tableArrangementResults) {
                int index = tableArrangementResults.indexOf(i);
                int tCapacity = tableCapacityTypeList.get(index);
                recommendedArrangementMsg += String.format(" %d %d seats ", i, tCapacity);
            }
            System.out.println(recommendedArrangementMsg);
            return tableArrangementResults;
        }

    }

    // Again，Sorry，这部分我也没有测试过，所以可能会有不少bug (^-^)见谅^_^
    // 根据安排的桌子结果判断顾客能否直接进店吃：
    // 1.可以,则return true； （测试不用管后面这一句话）且print出桌子的安排
    // 2.不可以，则return false
    // 测试：首先你得确保availableTables 有哪些；
    // 然后你自己建一个arraylist存放一个要测试的arraylist作为argument，放进去；
    // 最后看print的结果是否是对的
    public boolean canDirectlyDineIn(ArrayList<Integer> tableArrangementResults) {
        boolean canDirectlyWalkIn = true;
        String waitingTablesListMessage = "For default arrangements, you still need to wait for: ";
        for (Integer num : tableArrangementResults) {
            int availableNumOfThisTableType = 0;
            int tableCapacityType = tableCapacityTypeList.get(tableArrangementResults.indexOf(num));
            for (Table t : availableTables) {
                if (t.getTableCapacity == tableCapacityType) {
                    availableNumOfThisTableType++;
                }
            }
            if (num > availableNumOfThisTableType) {
                int numOfWaitingTables = num - availableNumOfThisTableType;
                canDirectlyWalkIn = false;
                waitingTablesListMessage += String.format(" %d %d-Seats Table ", tableCapacityType, numOfWaitingTables);
            }
        }
        if (canDirectlyWalkIn) {
            System.out.printf("You can now directly dine in. Please indicate your option: Dine In Or Leave");
            return true;
        }
        System.out.println(waitingTablesListMessage);
        return false;
        // else {

        // ArrayList<Integer> recommendedArrangements =
        // recommendedArrangementAccordingToWaitingTime(peopleNum);
        // System.out.println(waitingTablesListMessage);
        // if (recommendedArrangements != null) {
        // return false;
        // } else {
        // return true;
        // }
        // }

    }

    // Again，Sorry，这部分我也没有测试过，所以可能会有不少bug (^-^)见谅^_^
    // （测试不用管后面这一句话）用于当根据人数计算桌子或推荐算法中某一个可行时，且顾客选择入座，则用此函数入座
    // 测试：自己建一个arraylist存放对应桌型的安排数量 作为argument；
    // 1.首先看return的结果是不是对的：可以顺利入座true，不可以顺利入座false
    // 2.当可以顺利入座这种情况发生时，执行完该函数后，去get available tables list看对应的table是否变成了occupied
    public boolean setWalkInStatus(ArrayList<Integer> tableArrangement) {
        boolean canDirectlyDineIn = canDirectlyDineIn(tableArrangement);
        if (canDirectlyDineIn) {
            for (Integer tableCapacity : tableCapacityTypeList) {
                int needOfThisTableCapcity = tableArrangement.get(tableCapacityTypeList.indexOf(tableCapacity));
                int tmpCount = 0;
                for (Table t : availableTables) {
                    if (t.getTableId() == tableCapacity) {
                        tmpCount++;
                        // 把这个table放进customer占用的arraylist里去
                        // c.occupyTable(t);
                        availableTables.remove(t);
                        occupiedTables.add(t);
                        if (tmpCount == needOfThisTableCapcity) {
                            break;
                        }
                    }
                }
            }
            System.out.println("Successfully Dine In!");
            return true;
        }
        return false;

    }

    // Again，Sorry，这部分我也没有测试过，所以可能会有不少bug (^-^)见谅^_^
    // 用于根据安排结果，返回顾客需要等待的对应桌型的数量的list
    public ArrayList<Integer> setWaitingTables(ArrayList<Integer> tableArrangement) {
        ArrayList<Integer> waitingTablesNumList = new ArrayList<Integer>();
        String waitingTablesListMessage = "For selected arrangements, you still need to wait for: ";
        for (Integer tableCapacityType : tableCapacityTypeList) {
            int index = tableCapacityTypeList.indexOf(tableCapacityType);
            int needOfThisTableCapcity = tableArrangement.get(index);
            // 以下这一个for loop是把能occupt的table先occupy掉
            int tmpCount = 0;
            for (Table t : availableTables) {
                if (t.getTableCapacity() == tableCapacityType) {
                    tmpCount++;
                    // 把这个table放进customer占用的arraylist里去
                    // c.occupyTable(t);
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
                waitingTablesListMessage += String.format(" %d %d-Seats Table ", tableCapacityType, waitingCount);
                // c.addWaitingTable(tableCapacity, needOfThisTableCapcity - tmpCount);
            } else {
                waitingTablesNumList.set(index, 0);
            }
        }
        // 15, 8,4,2
        // waitingTablesNumList.(1)=1
        System.out.println(waitingTablesListMessage);
        return waitingTablesNumList;
    }

    // 预定桌子
    // 不用测
    public void reserveTable(Customer c, int tableId, TimeSlot reservedTime) {
        for (Table t : availableTables) {
            if (t.getTableId() == tableId) {
                if (t.reservedTimeIsAllowed(reservedTime) == true) {
                    t.makeReservation(c, reservedTime);
                    System.out.printf("Successfully reserved Two-Seats table of id: %d at time of %d", tableId,
                            reservedTime);
                } else {
                    System.out.printf(
                            "Selected Two-Seats table of id: %d is not available at time of %d. Please input a valid timeslot",
                            tableId, reservedTime);
                }
            }
            // 加一个exception没找到该table
        }
    }

    // 用于对对应reserved的桌子进行check in
    public void reservedCusomerCheckIn(Customer c, int tableId) {
        // 这里会有一个时间的check，是否到了预定的时间，

    }

    // 还没合并，暂时不用测这一个
    // 展示各自table的课预定时间段
    public void showReservationTable() {
        String showReservationTableMsg = "Table for tommorrow reservation and available time slots: \n";
        for (Table t : availableTables) {
            showReservationTableMsg += String.format("%d-Seats Table with ID of %d: ", t.getTableId(),
                    t.getTableId());
            TimeSlots timeslots = t.getTmrReservationTimeSlot();
            System.out.println(timeslots);
            showReservationTableMsg += "\n";
        }
        System.out.println(showReservationTableMsg);

    }

    // 展示所有桌型的available的数量
    public String showAvailableTables() {
        String showAvailableTableMsg = "Below is the available tables: \n";
        for (Integer tableCapacity : tableCapacityTypeList) {
            int numOfAvailableForCurrentTableCapacity = 0;
            for (Table t : availableTables) {
                if (t.getTableId() == tableCapacity) {
                    numOfAvailableForCurrentTableCapacity++;
                }
            }
            showAvailableTableMsg += String.format("Num of Available %d-Seats Table: %d \n", tableCapacity,
                    numOfAvailableForCurrentTableCapacity);
        }
        System.out.println(showAvailableTableMsg);
        return showAvailableTableMsg;
    }

    // admin可以添加新的桌子：1.该桌型之前就存在直接加桌子 2.该桌型之前不存在，加桌型和桌子
    // 测试
    // 15,12,8,4,2
    // 16
    public void addNewTable(int tableId, int tableCapacity) {
        if (checkTableIdIsAreadyInUsed(tableId) == false) {
            allTableIds.add(tableId);
            if (tableCapacityTypeList.contains(tableCapacity)) {
                availableTables.add(new Table(tableId, tableCapacity));
            } else {
                tableCapacityTypeList.add(tableCapacity);
                Collections.sort(tableCapacityTypeList, Collections.reverseOrder());
                availableTables.add(new Table(tableId, tableCapacity));
            }
            System.out.printf("Successfully  add table with ID of %d, capacity of %d", tableId, tableCapacity);
        } else {
            System.out.printf("Can't add such table because Table with ID of %d is already in used", tableId);
        }
    }

    // admin删除旧桌子: 1.桌子是available的直接delete就ok 2.桌子不在available
    // list里可能是因为根本不available或根本没有这张桌子，统一print不能删除
    // 测试：1.桌子本身available 2.桌子不available 3.桌子根本就没有
    public void removeTable(int tableId) {
        for (Table t : availableTables) {
            if (t.getTableId() == tableId) {
                allTableIds.remove(tableId);
                availableTables.remove(t);
                System.out.printf("Successfully delete the table with id of %d", tableId);
                return;
            }
        }
        System.out.printf("Can't delete the table with id of %d. Maybe due to it's unavailable or it doesn't exist.",
                tableId);
    }

    // 把table从available变为reserved
    public void setTableFromAvailableToReservedStatus(int tableId) {
        for (Table t : availableTables) {
            if (t.getTableId() == tableId) {
                availableTables.remove(t);
                reservedTables.add(t);
            }
        }
    }

    // 把table从available变为occupied
    public void setTableFromAvailableToOccupiedStatus(int tableId) {
        for (Table t : availableTables) {
            if (t.getTableId() == tableId) {
                availableTables.remove(t);
                occupiedTables.add(t);
            }
        }
    }

    // 把table从reserved变为occupied
    public void setTableFromReservedToOccupiedStatus(int tableId) {
        for (Table t : reservedTables) {
            if (t.getTableId() == tableId) {
                reservedTables.remove(t);
                occupiedTables.add(t);
            }
        }
    }

    // 把table从occupied变为available
    public void setTableFromOccupiedToAvailable(int tableId) {
        for (Table t : occupiedTables) {
            if (t.getTableId() == tableId) {
                occupiedTables.remove(t);
                availableTables.add(t);
            }
        }
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
        for (Table t : occupiedTables) {
            if (t.getTableId() == tableId) {
                occupiedTables.remove(t);
                reservedTables.add(t);
            }
        }
    }

    // 把table从reserved变成available
    public void setTableFromReservedToAvailable(int tableId) {
        for (Table t : reservedTables) {
            if (t.getTableId() == tableId) {
                reservedTables.remove(t);
                availableTables.add(t);
            }
        }
    }

    // check 该table ID是否已经加进去了
    public boolean checkTableIdIsAreadyInUsed(int tableId) {
        for (int i : allTableIds) {
            if (i == tableId) {
                return true;
            }
        }
        return false;

    }

    // 以下两个不测
    // 会call customer里的checkWhetherThisIsNeeded(int tableCapacity, int tableId)
    public void addWaitingCustomer(Customer c) {
        waitingCustomers.add(c);

    }

    public void removeWaitingCustomer(Customer c) {
        waitingCustomers.remove(c);
    }

}
