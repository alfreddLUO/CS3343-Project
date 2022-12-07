package GoGoEat;

public class ExCustomersIdNotFound extends Exception {
    public ExCustomersIdNotFound(String cid) {
        super(String.format("Error! Customer ID: %s not found!", cid));
    }   
}
