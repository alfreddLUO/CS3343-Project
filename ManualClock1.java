
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ManualClock1 {
    private static LocalDateTime currDateTime;
    private static ManualClock1 instance = new ManualClock1();
    private static ArrayList<TimeObserver> observers = new ArrayList<>();

    private ManualClock1() {
        currDateTime = LocalDateTime.now();
    }

    public static void addObserver(TimeObserver listener) {
        observers.add(listener);
    }

    public static ManualClock1 getInstance() {
        if (instance == null) {
            instance = new ManualClock1();
        }
        return instance;
    }

    public static String getDateTimeString() {
        return getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public static LocalDateTime getDateTime() {
        return currDateTime;
    }

    public static LocalTime getTime() {
        return getDateTime().toLocalTime();
    }

    public static LocalDate getDate() {
        return getDateTime().toLocalDate();
    }

    public static void newDay() {
        System.out.println("Starting new day!");
        currDateTime = currDateTime.plusDays(1);
        currDateTime = LocalDateTime.of(getDate(), LocalTime.of(0, 0));
        for (TimeObserver l : observers) {
            l.dateUpdate(getDate());
        }
        System.out.println("Now is " + getDate().toString());
    }

    public static void changeTime(String newTime) {
        if (LocalTime.parse(newTime).compareTo(currDateTime.toLocalTime()) < 0) {
            System.out.println("Can not jump back to past! This is not how time travel works!");
            return;
        }
        currDateTime = LocalDateTime.of(getDate(), LocalTime.parse(newTime));
        for (TimeObserver l : observers) {
            l.timeUpdate(getTime());
        }
        System.out.println("Now is " + ManualClock1.getTime().toString());
    }

}