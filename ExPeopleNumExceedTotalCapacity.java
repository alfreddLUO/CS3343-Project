
public class ExPeopleNumExceedTotalCapacity extends Exception {

    public ExPeopleNumExceedTotalCapacity(int total) {
        super(String.format("Exceeds the capacity of %d, please input an valid num", total));
    }

}
