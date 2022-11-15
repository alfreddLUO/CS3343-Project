
public class ExCustomersIdNotFound extends Exception {
    public ExCustomersIdNotFound(String cid) {
        super(String.format("Error! Customer ID not found!", cid));
    }
}
