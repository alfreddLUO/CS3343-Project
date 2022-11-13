import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class TimeSlots {
    private ArrayList<TimeSlot> timeSlots;
    private static LocalTime openTime = LocalTime.of(0, 0);// opening time of the table
    private static LocalTime closeTime = LocalTime.of(23, 59);// closing time of the table
    private LocalDate date;

    public TimeSlots(String dateString) {
        timeSlots = new ArrayList<>();
        this.date = LocalDate.parse(dateString);
    }

    public TimeSlots() {
        timeSlots = new ArrayList<>();
    }

    // Changed to tablesManagement.setOpenAndCloseTime()
    public static boolean setOpenAndCloseTime(String open, String close) {
        boolean success = false;

        while (!success) {
            openTime = LocalTime.parse(open);
            closeTime = LocalTime.parse(close);
            success = true;
        }
        return success;
    }

    public Boolean addSlot(TimeSlot ts) { // format example: 12:00 非整点时间
        if (ts.getStart().plusMinutes(30).compareTo(ts.getEnd()) > 0) {
            return false;
        }
        // check if before openTime or after closeTime
        if (ts.getStart().compareTo(openTime) < 0 || ts.getEnd().compareTo(closeTime) > 0) {
            return false;
        } else if (!this.checkAvailable(ts)) {
            return false;
        } else {
            timeSlots.add(ts);
            Collections.sort(timeSlots, (a, b) -> a.getStart().compareTo(b.getStart()));
            return true;
        }
    }

    private boolean checkAvailable(TimeSlot slot) {

        ArrayList<TimeSlot> timeSlotsCopy = new ArrayList<TimeSlot>();

        for (TimeSlot ts : timeSlots) {
            timeSlotsCopy.add(ts.makeDummyCopy());
        }
        timeSlotsCopy.add(slot);
        Collections.sort(timeSlotsCopy, (a, b) -> a.getStart().compareTo(b.getStart()));
        for (int i = 1; i < timeSlotsCopy.size(); i++) {
            if (timeSlotsCopy.get(i - 1).getEnd().compareTo(timeSlotsCopy.get(i).getStart()) > 0) {
                return false;
            }
        }
        return true;
    }

    public LocalDate getDate() {
        return date;
    }

    public String checkReserver(LocalTime time) {
        for (TimeSlot ts : timeSlots) {
            if (time.compareTo(ts.getStart()) > 0 && time.compareTo(ts.getEnd()) < 0) {
                return ts.getCustomerID();
            }
        }
        return null;
    }

    public String getAvailableSlots() {
        ArrayList<LocalTime> temp = new ArrayList<>();
        ArrayList<TimeSlot> available = new ArrayList<>();
        StringBuilder respond = new StringBuilder();
        temp.add(openTime);
        for (TimeSlot ts : timeSlots) {
            temp.add(ts.getStart());
            temp.add(ts.getEnd());
        }
        temp.add(closeTime);
        for (int i = 0; i < temp.size(); i += 2) {
            if (temp.get(i + 1).toSecondOfDay() - temp.get(i).toSecondOfDay() >= 1800) { // if this slot is >= 30 mins
                available.add(new TimeSlot(temp.get(i), temp.get(i + 1), null));
            } else {
                continue;
            }
        }
        int cnt = 0;

        for (TimeSlot a : available) {
            if (cnt > 0) {
                respond.append(", ").append(a.toString());
            } else {
                respond.append(a.toString());
            }
            cnt++;
        }
        temp.clear();
        available.clear();
        return respond.toString();
    }

    public Boolean remove(TimeSlot ts) {

        for (int i = 0; i < timeSlots.size(); i++) {
            if (timeSlots.get(i).equals(ts)) {
                timeSlots.remove(i);
                return true;
            }
        }

        return false;
    }

    public Boolean hasReserved(LocalTime now) {
        for (TimeSlot ts : timeSlots) {
            if (now.compareTo(ts.getStart().minusMinutes(30)) <= 0 || now.compareTo(ts.getEnd()) > 0) {
                continue;
            } else {
                return true;
            }
        }
        return false;
    }

    // 0 = reached reservation time slot
    // 1 = 30 minutes before reservation time slot
    // 2 =
    public int checkReservedStatus(LocalTime now) {
        for (TimeSlot ts : timeSlots) {
            if (now.compareTo(ts.getStart().minusMinutes(30)) >= 0 && now.compareTo(ts.getEnd()) <= 0) {
                if (now.compareTo(ts.getStart().minusMinutes(30)) >= 0 && now.compareTo(ts.getStart()) < 0) {
                    return 1;
                } else if (now.compareTo(ts.getStart()) >= 0 && now.compareTo(ts.getEnd()) <= 0) {
                    return 0;
                }
            } else {
                continue;
            }
        }
        return -1;
    }

    public Boolean checkValidReserver(LocalTime now, String customerID) {
        for (TimeSlot ts : timeSlots) {
            if (now.compareTo(ts.getStart()) >= 0 && now.compareTo(ts.getEnd()) <= 0) {
                return ts.getCustomerID().equals(customerID);
            }
        }
        return false;
    }

    public LocalTime[] getReservationStartEndInDay() {
        LocalTime start = openTime;
        LocalTime end = closeTime;
        if (!timeSlots.isEmpty()) {
            start = timeSlots.get(0).getStart();
            end = timeSlots.get(timeSlots.size() - 1).getEnd();
            return new LocalTime[] { start, end };
        }
        return null;
    }

    public static LocalTime getOpenTime() {
        return openTime;
    }

    public static LocalTime getCloseTime() {
        return closeTime;
    }

    public Boolean isEmpty() {
        return timeSlots.isEmpty();
    }
}