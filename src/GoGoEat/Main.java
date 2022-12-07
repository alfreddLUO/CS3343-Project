package GoGoEat;

class Main {

    public static final InputScanner in = InputScanner.getInstance();

    public static void main(String[] args) throws NumberFormatException, Exception {
        Initialization initialization = Initialization.getInstance();
        initialization.initialize();

        while (true) {
            AccountModule.getInstance().run();
        }
    }

}
