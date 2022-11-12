
public class GenerateCustomerId implements GenerateId {
    private final String prefix = "C";
    private int currentId = 0;
    private static GenerateCustomerId instance = null;

    public GenerateCustomerId() {

    }

    public static GenerateCustomerId getInstance() {
        if (instance == null)
            instance = new GenerateCustomerId();

        return instance;
    }

    public String getNextId() {
        currentId += 1;
        String temp = String.format("%04d", currentId);
        return (prefix + temp);
    }
}
