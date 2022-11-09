
public class GenerateMerchantId {
    private String prefix = "M";
    private int currentId = 0;
    private static GenerateMerchantId instance = null;

    public GenerateMerchantId() {

    }

    public static GenerateMerchantId getInstance() {
        if (instance == null)
            instance = new GenerateMerchantId();

        return instance;
    }

    public String getNextId() {
        currentId += 1;
        String temp = String.format("%04d", currentId);
        return (prefix + temp);
    }
}
