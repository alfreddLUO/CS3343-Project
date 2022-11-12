public class GenerateCustomerBillNo implements GenerateId {
    private final String prefix = "B";
    private int currentId = 0;
    private static GenerateCustomerBillNo instance = null;

    public GenerateCustomerBillNo() {

    }

    public static GenerateCustomerBillNo getInstance() {
        if (instance == null)
            instance = new GenerateCustomerBillNo();

        return instance;
    }

    public String getNextId() {
        currentId += 1;
        String temp = String.format("%04d", currentId);
        return (prefix + temp);
    }
}
