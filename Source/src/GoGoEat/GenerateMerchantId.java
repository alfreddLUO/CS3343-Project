package GoGoEat;

public class GenerateMerchantId implements GenerateId {
    private final String prefix = "M";
    private int currentId = 0;
    private static GenerateMerchantId instance = null;

    public GenerateMerchantId() {

    }

    public static GenerateMerchantId getInstance() {
        if (instance == null) {
            instance = new GenerateMerchantId();
        }

        return instance;
    }

    @Override
    public String getNextId() {
        currentId += 1;
        String temp = String.format("%04d", currentId);
        return (prefix + temp);
    }
}
