import java.util.Scanner;

class InputScanner1 {
    private static Scanner scannerInput = new Scanner(System.in);
    private static InputScanner1 instance;

    private InputScanner1() {

    }

    public static InputScanner1 getInstance() {
        if (instance == null) {
            instance = new InputScanner1();
        }
        return instance;
    }

    public String next(String previousPrintedMsg) {
        String in = scannerInput.next();
        if (in.equals("newDay")) {
            ManualClock1.newDay();
            System.out.print(previousPrintedMsg);
            in = next(previousPrintedMsg);
        } else if (in.equals("changeTime")) {
            System.out.println("Please enter a new system time (hh:mm)");
            ManualClock1.changeTime(scannerInput.nextLine());
            System.out.print(previousPrintedMsg);
            in = next(previousPrintedMsg);
        } else if (in.equals("timeNow")) {
            System.out.println(ManualClock1.getDateTime().toString());
            System.out.print(previousPrintedMsg);
            in = next(previousPrintedMsg);
        }

        return in;
    }

    public String nextLine(String previousPrintedMsg) {
        scannerInput.nextLine();
        String in = scannerInput.nextLine();
        if (in.equals("newDay")) {
            ManualClock1.newDay();
            System.out.print(previousPrintedMsg);
            in = nextLine(previousPrintedMsg);
        } else if (in.equals("changeTime")) {
            System.out.println("Please enter a new system time (hh:mm)");
            ManualClock1.changeTime(scannerInput.nextLine());
            System.out.print(previousPrintedMsg);
            in = nextLine(previousPrintedMsg);
        } else if (in.equals("timeNow")) {
            System.out.println(ManualClock1.getDateTimeString());
            System.out.print(previousPrintedMsg);
            in = nextLine(previousPrintedMsg);
        }
        return in;
    }

    public void close() {
        scannerInput.close();
    }
}