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
            // TODO-call new day
            System.out.println("Starting new day!");

            // End of TODO
            System.out.print(previousPrintedMsg);
            in = next(previousPrintedMsg);
        } else if (in.equals("changeTime")) {
            // TODO-call changeTime
            System.out.println("TimeChanged!");

            // End of TODO
            System.out.print(previousPrintedMsg);
            in = next(previousPrintedMsg);
        }
        return in;
    }

    public String nextLine(String previousPrintedMsg) {
        scannerInput.nextLine();
        String in = scannerInput.nextLine();
        if (in.equals("newDay")) {
            // TODO-call new day
            System.out.println("Starting new day!");

            // End of TODO
            System.out.print(previousPrintedMsg);
            in = nextLine(previousPrintedMsg);
        } else if (in.equals("changeTime")) {
            // TODO-call changeTime
            System.out.println("TimeChanged!");

            // End of TODO
            System.out.print(previousPrintedMsg);
            in = nextLine(previousPrintedMsg);
        }
        return in;
    }

    public void close() {
        scannerInput.close();
    }
}