package GoGoEat;

public class Initialization {

    private static Initialization instance = null;

    public void initialize() throws ExTableIdAlreadyInUse {
        initiateRestaurants();
        initiateDish();
        initializeAdmin();
        initiateMerchants();
        initiateCustomers();
        initiateTables();
    }

    public static Initialization getInstance() {
        if (instance == null) {
            instance = new Initialization();
        }
        return instance;
    }

    private static final AccountManagement accManager = AccountManagement.getInstance();
    private static final Database database = Database.getInstance();

    static Restaurants PepperLunch = new Restaurants("Pepper-Lunch");
    static Restaurants TamJai = new Restaurants("Tam-Jai-Mi-Xian");
    static Restaurants McDonalds = new Restaurants("McDonald's");
    static Restaurants KFC = new Restaurants("KFC");

    static Dish hotChoco = new Dish("Hot-Chocolate", 16.0);
    static Dish latte = new Dish("Latte", 18.0);
    static Dish Americano = new Dish("Americano", 18.0);
    static Dish CrunchyOvaltine = new Dish("Crunchy-Ovaltine", 33.0);
    static Dish FiletOFish = new Dish("Filet-O-Fish", 12.0);
    static Dish BeefnEggBurger = new Dish("Beef-Egg-Burger", 13.0);
    static Dish BeefFries = new Dish("Beef", 59.9);
    static Dish PorkFries = new Dish("Pork", 54.9);
    static Dish TurkeyFries = new Dish("Turkey", 52.9);
    static Dish PorkMixian = new Dish("Pork-Mixian", 34.9);
    static Dish LettuceMixian = new Dish("Lettuce-Mixian", 29.9);

    public void initiateRestaurants() {
        database.addTolistOfRestaurants(PepperLunch);
        database.addTolistOfRestaurants(TamJai);
        database.addTolistOfRestaurants(McDonalds);
        database.addTolistOfRestaurants(KFC);
    }

    public void initiateDish() {
        McDonalds.adddishtoMenu(hotChoco);
        McDonalds.adddishtoMenu(latte);
        McDonalds.adddishtoMenu(Americano);
        McDonalds.adddishtoMenu(CrunchyOvaltine);

        KFC.adddishtoMenu(FiletOFish);
        KFC.adddishtoMenu(BeefnEggBurger);

        PepperLunch.adddishtoMenu(BeefFries);
        PepperLunch.adddishtoMenu(PorkFries);
        PepperLunch.adddishtoMenu(TurkeyFries);

        TamJai.adddishtoMenu(LettuceMixian);
        TamJai.adddishtoMenu(PorkMixian);
    }

    public void initiateMerchants() {
        accManager.registerMerchant("KFCWorker", "t123", KFC);
        accManager.registerMerchant("McDonaldWorker", "t123", McDonalds);
        accManager.registerMerchant("TamJaiWorker", "t123", TamJai);
        accManager.registerMerchant("TamJaiWorker2", "t123", TamJai);
        accManager.registerMerchant("TamJaiWorker1", "t123", TamJai);
        accManager.registerMerchant("PepperLunchWorker", "t123", PepperLunch);
    }

    public void initiateCustomers() {
        accManager.registerCustomer("yinch33", "t123");
        accManager.registerCustomer("ta123", "t123");
        accManager.registerCustomer("wedu2", "t123");
        accManager.registerCustomer("lpy", "t123");
    }

    public void initializeAdmin() {
        accManager.registerAdmin("admin", "t123");
    }

    public void initiateTables() {
        TablesManagement tm = TablesManagement.getInstance();
        try {
            tm.addNewTable(1, 2);
            tm.addNewTable(2, 2);
            tm.addNewTable(3, 2);
            tm.addNewTable(4, 2);
            tm.addNewTable(5, 2);
            tm.addNewTable(6, 4);
            tm.addNewTable(7, 4);
            tm.addNewTable(8, 4);
            tm.addNewTable(9, 8);
            tm.addNewTable(10, 8);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        for (int i = 1; i <= 10; i++) {
            tm.appendToAllTableIds(i);
        }

    }
}
