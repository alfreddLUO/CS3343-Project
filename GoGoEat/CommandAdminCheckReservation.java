package GoGoEat;

public class CommandAdminCheckReservation extends CommandAdmin {

    CommandAdminCheckReservation() {
        super();
    }

    @Override
    public void exe() throws ExCustomersIdNotFound {

        /*
         * 1. Input Customer ID -> Check and get existing customer instance
         * 2. Get Customers' reservation info
         */

        try {
            System.out.print("\nPlease input the CustomerId to check reservation info: ");
            String customerId = Main.in.next("\nPlease input the CustomerId to check reservation info: ");

            checkReserveInfo(customerId);

        } catch (ExNoReservationFound e) {
            System.out.println(e.getMessage());
        }

    }

    private String checkReserveInfo(String cid) throws ExNoReservationFound {

        /*
         * 1. Pass CID into method to check if there is a matching instance of customer
         * 2. IF customer has a match -> get Reserve Info
         * 3. Print reserve info
         */

        Customers customer;
        String result = null;
        try {
            customer = database.matchCId(cid);
            result = customer.getReserveInfo();
            System.out.println(result);
        } catch (NullPointerException e) {
            throw new ExNoReservationFound();
        } catch (ExCustomersIdNotFound e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

}
