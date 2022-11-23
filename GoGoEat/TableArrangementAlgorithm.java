package GoGoEat;

import java.util.*;

public interface TableArrangementAlgorithm {
    public ArrayList<Integer> getTableArrangementResult(int peopleNum, ArrayList<Table> availableTables,
            ArrayList<Integer> tableCapacityTypeList, ArrayList<Table> allTables) throws ExPeopleNumExceedTotalCapacity;
}