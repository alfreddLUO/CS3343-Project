
import java.time.*;

public class TimeSlot {
    private LocalTime start;
    private LocalTime end;
    private String customerID;

    public TimeSlot(LocalTime start, LocalTime end, String customerID) {
        this.start = start;
        this.end = end;
        this.customerID = customerID;
    }

    public TimeSlot(String startString, String endString, String customerID) { // 12:34 - 12:33
        this.start = LocalTime.parse(startString);
        this.end = LocalTime.parse(endString);
        this.customerID = customerID;
    }

    public TimeSlot(String indexString, int customerID) { // format example: 5 -> indicates 4:00-5:00
        int index = Integer.parseInt(indexString);
        this.start = LocalTime.of(index - 1, 0);
        this.end = LocalTime.of(index - 1, 59);
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public String getCustomerID() {
        return customerID;
    }

    @Override
    public String toString() {
        return this.start.toString() + "-" + this.end.toString();
    }

    public int length() {
        return (end.toSecondOfDay() - start.toSecondOfDay()) / 60;
    }

    public Boolean equals(TimeSlot ts) {
        if (this.start.equals(ts.getStart()) && this.end.equals(ts.getEnd())) {
            return true;
        }
        return false;
    }
}
