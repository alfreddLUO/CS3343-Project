package GoGoEat;

import java.time.format.DateTimeParseException;
import java.util.Scanner;

class InputScanner {
    private static Scanner scannerInput = new Scanner(System.in);
    private static InputScanner instance;
    private static ManualClock clockInstance = ManualClock.getInstance();

    private InputScanner() {

    }

    public void promptNewDay(String previousPrintedMsg) {
        clockInstance.newDay();
        System.out.print(previousPrintedMsg);
    }

    public void promptTimeNow(String previousPrintedMsg) {
        System.out.println(clockInstance.getDateTimeString());
        System.out.print(previousPrintedMsg);
    }

    public static InputScanner getInstance() {
        if (instance == null) {
            instance = new InputScanner();
        }
        return instance;
    }

    // next
    public String next(String previousPrintedMsg) {
        String in = scannerInput.next();
        // scannerInput.nextLine();
        if (in.equals("newDay")) {
            promptNewDay(previousPrintedMsg);
            in = next(previousPrintedMsg);
        } else if (in.equals("changeTime")) {
            System.out.println("Please enter a new system time (hh:mm)");
            try {
                clockInstance.changeTime(scannerInput.next());
            } catch (DateTimeParseException ex) {
                System.out.println("DateTime Error! Please input a valid time!");
            }

            System.out.print(previousPrintedMsg);
            in = next(previousPrintedMsg);
        } else if (in.equals("timeNow")) {
            promptTimeNow(previousPrintedMsg);
            in = next(previousPrintedMsg);
        }
        return in;
    }

    // nextLine
    public String nextLine(String previousPrintedMsg) {
        scannerInput.nextLine();
        String in = scannerInput.nextLine();
        if (in.equals("newDay")) {
            promptNewDay(previousPrintedMsg);
            in = nextLine(previousPrintedMsg);
        } else if (in.equals("changeTime")) {
            System.out.println("Please enter a new system time (hh:mm)");
            try {
                clockInstance.changeTime(scannerInput.nextLine());
            } catch (DateTimeParseException ex) {
                System.out.println("DateTime Error! Please input a valid time!");
            }
            System.out.print(previousPrintedMsg);
            in = nextLine(previousPrintedMsg);
        } else if (in.equals("timeNow")) {
            promptTimeNow(previousPrintedMsg);
            in = nextLine(previousPrintedMsg);
        }
        return in;
    }

    public void close() {
        scannerInput.close();
    }
}