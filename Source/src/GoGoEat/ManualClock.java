package GoGoEat;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ManualClock {
    private static LocalDateTime currDateTime;
    private static ManualClock instance = new ManualClock();
    private ArrayList<TimeObserver> observers = new ArrayList<>();

    private ManualClock() {
        currDateTime = LocalDateTime.now();
        observers.add(TablesManagement.getInstance());
        for (TimeObserver l : observers) {
            l.timeUpdate(getTime());
            l.dateUpdate(getDate());
        }
    }

    public void addObserver(TimeObserver listener) {
        observers.add(listener);
    }

    public static ManualClock getInstance() {
        if (instance == null) {
            instance = new ManualClock();
        }
        return instance;
    }

    // Return time in format yyyy-MM-dd HH:mm
    public String getDateTimeString() {
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

    // Scanner newDay
    public void newDay() {
        System.out.println("Starting new day!");
        currDateTime = currDateTime.plusDays(1);
        currDateTime = LocalDateTime.of(getDate(), LocalTime.of(0, 0));
        for (TimeObserver l : observers) {
            l.dateUpdate(getDate());
        }
        System.out.println("Now is " + getDate().toString());
    }

    // Scanner changeTime
    public void changeTime(String newTime) {
        if (LocalTime.parse(newTime).compareTo(currDateTime.toLocalTime()) < 0) {
            System.out.println("Cannot jump back to past! This is not how time travel works!");
            return;
        }
        currDateTime = LocalDateTime.of(getDate(), LocalTime.parse(newTime));
        for (TimeObserver l : observers) {
            l.timeUpdate(getTime());
        }
        System.out.println("Now is " + ManualClock.getTime().toString());
    }

}