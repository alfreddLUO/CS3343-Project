package GoGoEat;

import java.time.*;
import java.time.format.DateTimeParseException;

public class TimeSlot {
    private LocalTime start;
    private LocalTime end;
    private String customerID;

    public TimeSlot(LocalTime start, LocalTime end, String customerID) {
        this.start = start;
        this.end = end;
        this.customerID = customerID;
    }

    public TimeSlot(String startString, String endString, String customerID) throws ExTimeSlotInvalid, ExTimeFormatInvalid { // 12:34 - 12:33
        try {
            LocalTime s = LocalTime.parse(startString);
            LocalTime e = LocalTime.parse(endString);
            if (s.plusMinutes(30).compareTo(e) > 0 || s.plusMinutes(120).compareTo(e) < 0) {
                throw new ExTimeSlotInvalid();
            }
            this.start = LocalTime.parse(startString);
            this.end = LocalTime.parse(endString);
            this.customerID = customerID;
        } catch(DateTimeParseException e) {
            throw new ExTimeFormatInvalid();
        }
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

    @Override
    public boolean equals(Object ts) {
        if (ts == this)
            {return true;}
        if (!(ts instanceof TimeSlot))
            {return false;}
        TimeSlot other = (TimeSlot) ts;
        boolean startEquals = (this.getStart() == null && other.getStart() == null)
                || (this.getStart() != null && this.getStart().equals(other.getStart()));
        boolean endEquals = (this.getEnd() == null && other.getEnd() == null)
                || (this.getEnd() != null && this.getEnd().equals(other.getEnd()));
        return startEquals && endEquals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = hash * 31 + (start == null ? 0 : start.hashCode());
        hash = hash * 31 + (end == null ? 0 : end.hashCode());
        hash = hash * 31 + (customerID == null ? 0 : customerID.hashCode());
        return hash;
    }

    public TimeSlot makeDummyCopy() {
        return new TimeSlot(this.getStart(), this.getEnd(), null);
    }

    public static boolean checkValid(String reserveTime) {
        String[] times = reserveTime.split("-");
        LocalTime start = LocalTime.parse(times[0]);
        LocalTime end = LocalTime.parse(times[1]);
        TimeSlot temp = new TimeSlot(start, end, "");
        return temp.length() >= 30;
    }
}
