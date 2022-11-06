
import java.time.*;
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

    public static void setOpenAndCloseTime(String open, String close) {
        openTime = LocalTime.parse(open);
        closeTime = LocalTime.parse(close);
    }

    // public Boolean addSlot(String startString, String endString, String
    // customerID) { // format example: 12:00 非整点时间
    // LocalTime start = LocalTime.parse(startString);
    // LocalTime end = LocalTime.parse(endString);
    // // check if before openTime or after closeTime
    // if (start.compareTo(openTime) < 0 || end.compareTo(closeTime) > 0) {
    // return false;
    // }
    // TimeSlot slot = new TimeSlot(start, end, customerID);
    // if (!checkAvailable(slot)) {
    // return false;
    // }
    // timeSlots.add(slot);
    // Collections.sort(timeSlots, (a, b) -> a.getStart().compareTo(b.getStart()));
    // return true;
    // }

    public Boolean addSlot(TimeSlot ts) { // format example: 12:00 非整点时间
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

    // public Boolean add(String index, int customerID) { // 整点时间
    // TimeSlot slot = new TimeSlot(index, customerID);
    // if (slot.getStart().compareTo(openTime) < 0 ||
    // slot.getEnd().compareTo(closeTime) > 0) {
    // return false;
    // }
    // if (!checkAvaliable(slot)) {
    // return false;
    // }
    // timeSlots.add(slot);
    // Collections.sort(timeSlots, (a, b) -> a.getStart().compareTo(b.getStart()));
    // return true;
    // }

    // check if the timeslot is able to be added in the timeSlots. true -> can,
    // false -> cannot
    private boolean checkAvailable(TimeSlot slot) {

        ArrayList<TimeSlot> timeSlotsCopy = new ArrayList<TimeSlot>();

        for (TimeSlot ts : timeSlots) {
            timeSlotsCopy.add(ts.makeDummyCopy());
        }
        // ArrayList<TimeSlot> timeSlotsCopy = (ArrayList<TimeSlot>)
        // this.timeSlots.clone();
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
        ArrayList<TimeSlot> avaliable = new ArrayList<>();
        String respond = "";
        temp.add(openTime);
        for (TimeSlot ts : timeSlots) {
            temp.add(ts.getStart());
            temp.add(ts.getEnd());
        }
        temp.add(closeTime);
        for (int i = 0; i < temp.size(); i += 2) {
            if (temp.get(i + 1).toSecondOfDay() - temp.get(i).toSecondOfDay() >= 1800) { // if this slot is >= 30 mins
                avaliable.add(new TimeSlot(temp.get(i), temp.get(i + 1), null));
            } else {
                continue;
            }
        }
        int cnt = 0;

        for (TimeSlot a : avaliable) {
            if (cnt > 0) {
                respond += ", " + a.toString();
            } else {
                respond += a.toString();
            }
            cnt++;
        }
        temp.clear();
        avaliable.clear();
        return respond;
    }

    public void remove(TimeSlot ts) {
        // for (TimeSlot t : timeSlots) {
        // if (ts.equals(t)) {
        // timeSlots.remove(t);
        // break;
        // }
        // }
        System.out.println("before remove:");
        for (TimeSlot t : timeSlots) {
            System.out.printf(t.toString() + ", ");
        }

        System.out.println();

        for (int i = 0; i < timeSlots.size(); i++) {
            if (timeSlots.get(i).equals(ts)) {
                timeSlots.remove(i);
                break;
            }
        }
        System.out.println("after remove:");
        for (TimeSlot t : timeSlots) {
            System.out.printf(t.toString() + ", ");
        }
        System.out.println();
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

    public int checkReservedStatus(LocalTime now) {
        for (TimeSlot ts : timeSlots) {
            if (now.compareTo(ts.getStart().minusMinutes(30)) <= 0 && now.compareTo(ts.getStart()) < 0) {
                return 1;
            } else if (now.compareTo(ts.getStart()) <= 0 && now.compareTo(ts.getEnd()) >= 0) {
                return 0;
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
}
