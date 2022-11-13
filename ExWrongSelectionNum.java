public class ExWrongSelectionNum extends NumberFormatException {
    public ExWrongSelectionNum() {
        super(String.format(
                "Error! Wrong input for selection! Please input an integer!\n"));
    }
}
