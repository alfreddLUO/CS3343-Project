
public class ExCustomersIdNotFound extends Exception {
    public ExCustomersIdNotFound(String cid) {
        super(String.format("Can't add such table because Table with ID of %d is already in used \n", cid));
    }
}
