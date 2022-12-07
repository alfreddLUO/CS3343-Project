package GoGoEat;

import java.util.*;

public class DefaultTableArrangementAlgorithm implements TableArrangementAlgorithm {
    private static final DefaultTableArrangementAlgorithm instance = new DefaultTableArrangementAlgorithm();

    public static DefaultTableArrangementAlgorithm getInstance() {
        return instance;
    }

    private DefaultTableArrangementAlgorithm() {
    }

    @Override
    public ArrayList<Integer> getTableArrangementResult(int peopleNum, ArrayList<Table> availableTables,
            ArrayList<Integer> tableCapacityTypeList, ArrayList<Table> allTables)
            throws ExPeopleNumExceedTotalCapacity {

        if (peopleNum <= returnTotalCapcityOfTables(allTables)) {
            int tmpPeopleNum = peopleNum;
            StringBuilder arrangementResultMessage = new StringBuilder("\nYour arranged tables are: \n");

            // Store the number of tables of the corresponding table type of that index
            ArrayList<Integer> tableArrangementResults = new ArrayList<Integer>();
            tableArrangementResults.addAll(initializeTableArrangementList(tableCapacityTypeList));
            for (int i = 0; i < tableCapacityTypeList.size(); i++) {
                int tmpResults = 0;
                int tableCapacity = tableCapacityTypeList.get(i);

                // This happens when the last table type comes, it should store all the
                // remaining people
                if (i == tableCapacityTypeList.size() - 1) {
                    tmpResults = (tmpPeopleNum % tableCapacity == 0) ? (tmpPeopleNum / tableCapacity)
                            : ((tmpPeopleNum / tableCapacity) + 1);
                } else {
                    // minimize the table num ， e.g. if exists table type of 2，4，8； 7 people -> [1]
                    // 8-seats tables
                    int addingTableNum = (tmpPeopleNum / tableCapacity <= returnTableNumWithTableCapacity(tableCapacity,
                            allTables))
                                    ? (tmpPeopleNum / tableCapacity)
                                    : returnTableNumWithTableCapacity(tableCapacity, allTables);
                    tmpResults += addingTableNum;
                    tmpPeopleNum = tmpPeopleNum - tableCapacity * addingTableNum;
                    if (tableCapacity >= tmpPeopleNum
                            && tmpPeopleNum > tableCapacityTypeList.get(i + 1)
                            && tmpResults < returnTableNumWithTableCapacity(tableCapacity, allTables)) {
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
        throw new ExPeopleNumExceedTotalCapacity(returnTotalCapcityOfTables(allTables));
    }

    public int returnTotalCapcityOfTables(ArrayList<Table> allTables) {
        int total = 0;
        for (Table t : allTables) {
            total += t.getTableCapacity();
        }
        return total;
    }

    public int returnTableNumWithTableCapacity(int tableCapacity, ArrayList<Table> allTables) {
        ArrayList<Table> allTablesList = allTables;
        int num = 0;
        for (Table t : allTablesList) {
            if (t.getTableCapacity() == tableCapacity) {
                num++;
            }
        }
        return num;
    }

    public ArrayList<Integer> initializeTableArrangementList(ArrayList<Integer> tableCapacityTypeList) {
        ArrayList<Integer> tableArrangementResults = new ArrayList<Integer>();
        for (int i = 0; i < tableCapacityTypeList.size(); i++) {
            tableArrangementResults.add(i, 0);
        }
        return tableArrangementResults;
    }

}
