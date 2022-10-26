import java.util.Scanner;

class InputScanner {
    private static Scanner scannerInput = new Scanner(System.in);
    private static InputScanner instance;

    private InputScanner() {

    }

    public static InputScanner getInstance() {
        if (instance == null) {
            instance = new InputScanner();
        }
        return instance;
    }

    public String next(String previousPrintedMsg) {
        String in = scannerInput.next();
        if (in.equals("newDay")) {
            ManualClock.newDay();
            System.out.print(previousPrintedMsg);
            in = next(previousPrintedMsg);
        }
        else if (in.equals("changeTime")) {
            System.out.println("Please enter a new system time (hh:mm)");
            ManualClock.changeTime(scannerInput.nextLine());
            System.out.print(previousPrintedMsg);
            in = next(previousPrintedMsg);
        }
        else if (in.equals("timeNow")) {
            System.out.println(ManualClock.getDateTime().toString());
            System.out.print(previousPrintedMsg);
            in = next(previousPrintedMsg);
        }
        return in;
    }

    public String nextLine(String previousPrintedMsg) {
        String in = scannerInput.nextLine();
        if (in.equals("newDay")) {
            ManualClock.newDay();
            System.out.print(previousPrintedMsg);
            in = nextLine(previousPrintedMsg);
        }
        else if (in.equals("changeTime")) {
            System.out.println("Please enter a new system time (hh:mm)");
            ManualClock.changeTime(scannerInput.nextLine());
            System.out.print(previousPrintedMsg);
            in = nextLine(previousPrintedMsg);
        }
        else if (in.equals("timeNow")) {
            System.out.println(ManualClock.getDateTimeString());
            System.out.print(previousPrintedMsg);
            in = nextLine(previousPrintedMsg);
        }
        return in;
    }

    public void close() {
        scannerInput.close();
    }
}