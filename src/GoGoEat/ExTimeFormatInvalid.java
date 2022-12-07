package GoGoEat;

public class ExTimeFormatInvalid extends Exception {
    public ExTimeFormatInvalid() {
        super("\nError! Time format invalid. Valid format: xx:xx-xx:xx");
    }   
}
