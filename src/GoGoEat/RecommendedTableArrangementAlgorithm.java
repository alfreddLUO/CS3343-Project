package GoGoEat;

import java.util.ArrayList;
import java.util.Collections;

public class RecommendedTableArrangementAlgorithm implements TableArrangementAlgorithm {
    private static final RecommendedTableArrangementAlgorithm instance = new RecommendedTableArrangementAlgorithm();

    public static RecommendedTableArrangementAlgorithm getInstance() {
        return instance;
    }

    private RecommendedTableArrangementAlgorithm() {
    }

    @Override
    public ArrayList<Integer> getTableArrangementResult(int peopleNum, ArrayList<Table> availableTables,
            ArrayList<Integer> tableCapacityTypeList, ArrayList<Table> allTables) {
        Collections.sort(availableTables);
        int tmpPeopleNum = peopleNum;
        ArrayList<Integer> tableArrangementResults = new ArrayList<Integer>();
        tableArrangementResults.addAll(initializeTableArrangementList(tableCapacityTypeList));
        for (Table t : availableTables) {
            if (t.getTableCapacity() < peopleNum) {
                int tCapacity = t.getTableCapacity();
                int capacityIndex = tableCapacityTypeList.indexOf(tCapacity);
                int num = tableArrangementResults.get(capacityIndex);
                tableArrangementResults.set(capacityIndex, num + 1);
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

    public ArrayList<Integer> initializeTableArrangementList(ArrayList<Integer> tableCapacityTypeList) {
        ArrayList<Integer> tableArrangementResults = new ArrayList<Integer>();
        for (int i = 0; i < tableCapacityTypeList.size(); i++) {
            tableArrangementResults.add(i, 0);
        }
        return tableArrangementResults;
    }

}
