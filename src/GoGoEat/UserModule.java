package GoGoEat;

public interface UserModule {

	/*
	 * 1. AdminModule
	 * 2. CustomerModule
	 * 3. MerchantModule
	 */

	static Database database = Database.getInstance();

	public void run(String Id) throws ExTableNotExist, ExTimeSlotAlreadyBeReserved, ExTimeSlotNotReservedYet,
			ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExCustomersIdNotFound;

}
