
public class ExTableIdAlreadyInUse extends Exception {
    public ExTableIdAlreadyInUse(int tableId) {
        super(String.format("Can't add such table because Table with ID of %d is already in use!", tableId));
    }
}
