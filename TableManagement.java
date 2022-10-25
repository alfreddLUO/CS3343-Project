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

    private ArrayList<Table> reservedTables;
    private ArrayList<Table> availableTables;
    private ArrayList<Table> occupiedTables;
    // 放正在等的桌子的customer； 注意customer里面应该有一个list存放起等待的桌子和occupy的桌子
    private ArrayList<Customer> waitingCustomers;
    // 存放桌型的列表，按从大到小的顺序排列
    private ArrayList<Integer> tableCapacityTypeList;

    // sigleton pattern
    private static TableManagement instance = new TableManagement();

    public static TableManagement getInstance() {
        return instance;
    }

    private TableManagement() {
        reservedTables = new ArrayList<Table>();
        availableTables = new ArrayList<Table>();
        occupiedTables = new ArrayList<Table>();
        // Just set three default table capacity: 2, 4, 8. May add according to
        // restaurant's needs
        tableCapacityTypeList.add(2);
        tableCapacityTypeList.add(4);
        tableCapacityTypeList.add(8);
        Collections.sort(tableCapacityTypeList, Collections.reverseOrder());
    }

    // admin可以添加新的桌子：1.该桌型之前就存在直接加桌子 2.该桌型之前不存在，加桌型和桌子
    public void addNewTable(int tableId, int tableCapacity) {
        if (tableCapacityTypeList.contains(tableCapacity)) {
            availableTables.add(new Table(tableId, tableCapacity));
        } else {
            tableCapacityTypeList.add(tableCapacity);
            Collections.sort(tableCapacityTypeList, Collections.reverseOrder());
            availableTables.add(new Table(tableId, tableCapacity));
        }
        System.out.printf("Successfully  add table with ID of %d, capacity of %d", tableId, tableCapacity);
    }

    // 根据输入的人数判断安排对应桌型的数量
    //
    public ArrayList<Integer> arrangeTableAccordingToNumOfPeople(Customer c, int peopleNum) {
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
                if (tableCapacity > peopleNum
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
        setWalkInStatus(c, tableArrangementResults, canDirectlyDineIn(peopleNum, tableArrangementResults));
        return tableArrangementResults;
    }

    /**
     * @param peopleNum
     */
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

    // 根据安排的桌子结果判断顾客能否直接进店吃： 1.可以 2.不可以， 显示需要等待的桌子数量
    public boolean canDirectlyDineIn(int peopleNum, ArrayList<Integer> tableArrangementResults) {
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
                waitingTablesListMessage += String.format("%d %d-Seats Table ", tableCapacityType, numOfWaitingTables);
            }
        }
        if (canDirectlyWalkIn) {
            dineInWithTableCapacity(tableArrangementResults);
            System.out.printf("You can now directly dine in. Please indicate your option: Dine In Or Leave");
            return true;
        } else {
            ArrayList<Integer> recommendedArrangements = recommendedArrangementAccordingToWaitingTime(peopleNum);
            System.out.println(waitingTablesListMessage);
            if (recommendedArrangements != null) {
                return false;
            } else {
                return true;
            }
        }

    }

    // 会call customer里的checkWhetherThisIsNeeded(int tableCapacity, int tableId)
    public void addWaitingCustomer(Customer c) {
        waitingCustomers.add(c);
    }

    public void removeWaitingCustomer(Customer c) {
        waitingCustomers.remove(c);
    }

    // 当上面两个function给customer展示了安排的桌子的情况后， 以下这个function是用来set 顾客选择walk-in（dine in 和
    // wait)后应该发生的状态
    public void setWalkInStatus(Customer c, ArrayList<Integer> tableArrangement, boolean canDirectlyDineIn) {
        if (canDirectlyDineIn) {
            for (Integer tableCapacity : tableCapacityTypeList) {
                int needOfThisTableCapcity = tableArrangement.get(tableCapacityTypeList.indexOf(tableCapacity));
                int tmpCount = 0;
                for (Table t : availableTables) {
                    if (t.getTableId() == tableCapacity) {
                        tmpCount++;
                        // 把这个table放进customer占用的arraylist里去
                        c.occupyTable(t);
                        availableTables.remove(t);
                        occupiedTables.add(t);
                        if (tmpCount == needOfThisTableCapcity) {
                            break;
                        }
                    }
                }
            }
        } else {
            for (Integer tableCapacity : tableCapacityTypeList) {
                int needOfThisTableCapcity = tableArrangement.get(tableCapacityTypeList.indexOf(tableCapacity));
                // 以下这一个for loop是把能occupt的table先occupy掉
                int tmpCount = 0;
                for (Table t : availableTables) {
                    if (t.getTableId() == tableCapacity) {
                        tmpCount++;
                        // 把这个table放进customer占用的arraylist里去
                        c.occupyTable(t);
                        availableTables.remove(t);
                        occupiedTables.add(t);
                        if (tmpCount == needOfThisTableCapcity) {
                            break;
                        }
                    }
                }
                // 如果该桌型当前的还没够，那么就将待等桌型及等待的数量传递给customer
                if (tmpCount < needOfThisTableCapcity) {
                    //
                    c.addWaitingTable(tableCapacity, needOfThisTableCapcity - tmpCount);
                }
            }
        }

    }

    // 预定桌子
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

    // 把table从available变为reserved
    public void setTableToReservedStatus(int tableId) {
        for (Table t : availableTables) {
            if (t.getTableId() == tableId) {
                availableTables.remove(t);
                reservedTables.add(t);
            }
        }
    }

    // 表示该桌买单离开桌子
    public void checkOutFromTable(int tableId) {
        for (Table t : occupiedTables) {
            if (t.getTableId() == tableId) {
                occupiedTables.remove(t);
                availableTables.add(t);
            }
        }
    }

    public void dineInWithTableCapacity(ArrayList<Integer> tableIdList) {
        for (Table t : availableTables) {
        }

    }

    // 用于对对应reserved的桌子进行check in
    public void reservedCusomerCheckIn(Customer c, int tableId) {
        // 这里会有一个时间的check，是否到了预定的时间，

    }

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
    public void showAvailableTables() {
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
    }
    // 三个arraylist available（可用的）, reserved（被预定了的但是还没来）, occupied（占用的）,
    // available里面包括 没被预定的桌子且没被占用，和预定了但是在时间范围前没来的桌子
    // reserved是被预定的了的且在时间范围内的桌子
    // occupied是现在已经被占用的桌子
    // 重点在于桌子state的转换，state pattern
    // 对应桌子的二人桌子，四人桌子，8人桌子，用subclass实现

}
