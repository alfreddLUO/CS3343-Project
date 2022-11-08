import java.util.Scanner;

class InputScanner {
    private static Scanner scannerInput = new Scanner(System.in);
    private static InputScanner instance;
    private static ManualClock clockInstance = ManualClock.getInstance();

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
        // scannerInput.nextLine();
        if (in.equals("newDay")) {
            clockInstance.newDay();
            System.out.print(previousPrintedMsg);
            in = next(previousPrintedMsg);
        } else if (in.equals("changeTime")) {
            System.out.println("Please enter a new system time (hh:mm)");
            clockInstance.changeTime(scannerInput.next());
            System.out.print(previousPrintedMsg);
            in = next(previousPrintedMsg);
        } else if (in.equals("timeNow")) {
            System.out.println(clockInstance.getDateTimeString());
            System.out.print(previousPrintedMsg);
            in = next(previousPrintedMsg);
        }
        return in;
    }

    public String nextLine(String previousPrintedMsg) {
        scannerInput.nextLine();
        String in = scannerInput.nextLine();
        if (in.equals("newDay")) {
            clockInstance.newDay();
            System.out.print(previousPrintedMsg);
            in = nextLine(previousPrintedMsg);
        } else if (in.equals("changeTime")) {
            System.out.println("Please enter a new system time (hh:mm)");
            clockInstance.changeTime(scannerInput.nextLine());
            System.out.print(previousPrintedMsg);
            in = nextLine(previousPrintedMsg);
        } else if (in.equals("timeNow")) {
            System.out.println(clockInstance.getDateTimeString());
            System.out.print(previousPrintedMsg);
            in = nextLine(previousPrintedMsg);
        }
        return in;
    }

    public void close() {
        scannerInput.close();
    }
}