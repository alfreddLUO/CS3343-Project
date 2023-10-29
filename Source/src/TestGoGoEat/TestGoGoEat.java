package TestGoGoEat;

import static org.junit.jupiter.api.Assertions.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import GoGoEat.*;

public class TestGoGoEat {
	
	Database db = Database.getInstance();
	AccountManagement acMag = AccountManagement.getInstance();
	Initialization initialization = Initialization.getInstance();
	TablesManagement tm = TablesManagement.getInstance();
	Merchants merchant = db.matchMId("M0001");
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	}
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		
	}
	@BeforeEach
	void setUp() throws Exception {
		
	}
	@AfterEach
	void tearDown() throws Exception {
		tm.setOpenAndCloseTime("00:00-23:59");
		initialization.reset();
	}
	
//-----------------------------------------------------------------------------------------------------	
//Login Module Test
	
	@Test
	void registerNewCustomer() throws ExCustomersIdNotFound, ExTableIdAlreadyInUse {
		initialization.initialize();
		String username = "test1";
		String password = "test1";
		acMag.registerCustomer(username, password);
		String cID = acMag.login(username, password);
		assertEquals(true, (db.matchCId(cID) instanceof Customers));
	}

	@Test
	void registerExistCustomer() throws ExCustomersIdNotFound, ExTableIdAlreadyInUse {
		initialization.initialize();
		String username = "yinch33";
		String password = "t123";
		boolean result = acMag.registerCustomer(username, password);
		assertEquals(false, result);
	}

	@Test
	void registerNewMerchant() throws ExCustomersIdNotFound, ExTableIdAlreadyInUse {
		initialization.initialize();
		String username = "testWorker";
		String password = "t123";
		Restaurants r = new Restaurants("test");
		acMag.registerMerchant(username, password, r);
		String mID = acMag.login(username, password);
		assertEquals(true, (db.matchMId(mID) instanceof Merchants));
	}

	@Test
	void registerExistMerchant() throws ExCustomersIdNotFound, ExTableIdAlreadyInUse {
		initialization.initialize();
		String username = "KFCWorker";
		String password = "t123";
		Restaurants r = new Restaurants("KFC");
		boolean result = acMag.registerMerchant(username, password, r);
		assertEquals(false, result);
	}

	// New Added
	@Test
	void LoginAccountNotExist() throws ExCustomersIdNotFound, ExTableIdAlreadyInUse {
		initialization.initialize();
		String username = "y3"; 
		String password = "t123";
		String cID = acMag.login(username, password);
		assertEquals(null, cID);
	}

	@Test
	void LoginExistCustomer() throws ExCustomersIdNotFound, ExTableIdAlreadyInUse {
		initialization.initialize();
		String username = "yinch33";
		String password = "t123";
		String cID = acMag.login(username, password);
		assertEquals("C0001", cID);
	}

	@Test
	void LoginExistMerchant() throws ExCustomersIdNotFound, ExTableIdAlreadyInUse {
		initialization.initialize();
		String username = "KFCWorker";
		String password = "t123";
		String mID = acMag.login(username, password);
		assertEquals("M0001", mID);
	}

	@Test
	void LoginAdmin() throws ExCustomersIdNotFound, ExTableIdAlreadyInUse {
		initialization.initialize();
		String username = "admin";
		String password = "t123";
		String aID = acMag.login(username, password);
		assertEquals("A0001", aID);
	}

	@Test
	void LoginExistCustomerWithWrongPW() throws ExCustomersIdNotFound, ExTableIdAlreadyInUse {
		initialization.initialize();
		String password = "wrongPassword";
		
		String username = "yinch33";
		String cID = acMag.login(username, password);
		assertEquals(null, cID);
		
		username = "KFCWorker";
		String mID = acMag.login(username, password);
		assertEquals(null, mID);
		
		username = "admin";
		String aID = acMag.login(username, password);
		assertEquals(null, aID);
	}

	@Test
	void deleteUser() throws ExCustomersIdNotFound, ExTableIdAlreadyInUse {
		initialization.initialize();
		String username = "yinch33";
		String password = "t123";
		acMag.deleteaccountinUserNameAndAccount(username);
		String cID = acMag.login(username, password);
		assertEquals(null, cID);
	}

// Login Module Test finished
// -----------------------------------------------------------------------------------------------------

// -----------------------------------------------------------------------------------------------------
// Admin Module Test

	@Test
	void adminSetOpenCloseHourWithoutReservationOverlap()
			throws ExTableIdAlreadyInUse, ExUnableToSetOpenCloseTime, ExTimeFormatInvalid {
		initialization.initialize();
		String timeString = "09:00-21:00";
		boolean result = tm.setOpenAndCloseTime(timeString);
		assertEquals(true, result);
		assertEquals("09:00", TimeSlots.getOpenTime().toString());
		assertEquals("21:00", TimeSlots.getCloseTime().toString());
	}

	@Test
	void adminSetOpenCloseHourWithReservationOverlap() throws ExTableIdAlreadyInUse, ExTimeFormatInvalid,
			ExTableNotExist, ExTimeSlotAlreadyBeReserved, ExTimeSlotInvalid {
		initialization.initialize();
		String timeString = "09:00-21:00";
		ArrayList<Integer> table = new ArrayList<Integer>();
		table.add(1);
		Reservation r = new Reservation("C0001", "08:00-10:00", table, ManualClock.getDate());
		try {
			tm.setOpenAndCloseTime(timeString);
		} catch (Exception e) {
			assertEquals(true, e instanceof ExUnableToSetOpenCloseTime);
		} finally {
			assertEquals("00:00", TimeSlots.getOpenTime().toString());
			assertEquals("23:59", TimeSlots.getCloseTime().toString());
			r.cancel();
		}
	}

	@Test
	void AddRemoveRestaurant() {
		//add
		class AddRestaurant_stub extends CommandAdminAddRestaurant {
			AddRestaurant_stub() {
				super();
			}
			public void addRestaurant(Restaurants r) {
				super.addRestaurant(r);
			}
		}
		AddRestaurant_stub cmdAdd = new AddRestaurant_stub();
		Restaurants r = new Restaurants("test");
		cmdAdd.addRestaurant(r);
		assertEquals(true, db.getListofRestaurants().contains(r));
		//remove
		class removeRestaurant_stub extends CommandAdminRemoveRestaurant {
			removeRestaurant_stub() {
				super();
			}
			public void removeRestaurant(Restaurants r) {
				super.deleteRestaurant(r);
			}
		}
		removeRestaurant_stub cmdRemove = new removeRestaurant_stub();
		cmdRemove.removeRestaurant(r);
		assertEquals(false, db.getListofRestaurants().contains(r));
	}
	
	@Test
	void AddTableWithExistID() {
		try {
			initialization.initialize();
			tm.addNewTable(1,999);
		} catch (Exception e) {
			assertEquals(true, e instanceof ExTableIdAlreadyInUse);
			int capacity = tm.returnTableAccordingToTableId(1).getTableCapacity();
			assertEquals(false, capacity == 999);
		}
	}
	
	@Test
	void AddRemoveTable() throws ExTableIdAlreadyInUse, ExTableNotExist, ExUnableToRemoveTable {
		initialization.initialize();
		tm.addNewTable(20,4);
		Object t = tm.returnTableAccordingToTableId(20);
		assertEquals(true, t instanceof Table);
		assertEquals(true, ((Table)t).getTableCapacity() == 4);
		
		tm.removeTable(20);
		Object t1 = tm.returnTableAccordingToTableId(20);
		assertEquals(false, t1 instanceof Table);
	}
	
	@Test
	void removeUnexistTable() throws ExTableIdAlreadyInUse, ExUnableToRemoveTable {
		initialization.initialize();
		try {
			tm.removeTable(20);
		} catch (Exception e) {
			assertEquals(true, e instanceof ExTableNotExist);
		} 
	}
	
	@Test
	void checkReservation() throws ExCustomersIdNotFound, ExTableNotExist, ExTimeSlotAlreadyBeReserved, ExTimeSlotInvalid, ExTimeFormatInvalid, ExTableIdAlreadyInUse {
		initialization.initialize();
		String username = "tester";
		String password = "tester";
		acMag.registerCustomer(username, password);
		String cID = acMag.login(username, password);
		Customers tester = db.matchCId(cID);
		ArrayList<Integer> desiredTable = new ArrayList<Integer>(); 
		desiredTable.add(1);
		Reservation testR = new Reservation(cID, "12:00-13:00", desiredTable,  ManualClock.getDate());
		tester.setReserve(testR);
		
        String expected = "";
        expected += String.format("\nThe upcoming reservation of Customer %s for %s is: ", cID, "tomorrow");
        expected += "\n[1] [Table with ID: 1] [Time Slot: 12:00-13:00]";
        
        assertEquals(expected, tester.getReserveInfo());
        
        tester.getReservation().cancel();
        tester.clearReservation();
        assertEquals(null, tester.getReservation());
	}
	
	
	
	
	
	
	
	
/*----------------------------------------------------------------------*/
	@Test
	public void testDefaultInitializeTableArrangementList() {
		DefaultTableArrangementAlgorithm dtaa = DefaultTableArrangementAlgorithm.getInstance();
		ArrayList<Integer> tableCapacityTypeList = new ArrayList<>();
		tableCapacityTypeList.add(2);
		tableCapacityTypeList.add(4);
		tableCapacityTypeList.add(8);
		
		ArrayList<Integer> tableArrangementResults = dtaa.initializeTableArrangementList(tableCapacityTypeList);
		
		ArrayList<Integer> ExpectedTableArrangementResults = new ArrayList<>();
		ExpectedTableArrangementResults.add(0);
		ExpectedTableArrangementResults.add(0);
		ExpectedTableArrangementResults.add(0);
		
		assertEquals(ExpectedTableArrangementResults, tableArrangementResults);
	}
	
	@Test
	public void testDefaultReturnTableNumWithTableCapacity() {
		DefaultTableArrangementAlgorithm dtaa = DefaultTableArrangementAlgorithm.getInstance();
		ArrayList<Table> allTables = new ArrayList<>();
		
		Table t1 = new Table(11, 2);
		Table t2 = new Table(12, 4);
		Table t3 = new Table(13, 8);
		Table t4 = new Table(14, 8);
		
		allTables.add(t1);
		allTables.add(t2);
		allTables.add(t3);
		allTables.add(t4);
		
		assertEquals(2, dtaa.returnTableNumWithTableCapacity(8, allTables));
	}
	
	@Test
	public void testDefaultReturnTotalCapcityOfTables() {
		DefaultTableArrangementAlgorithm dtaa = DefaultTableArrangementAlgorithm.getInstance();
		ArrayList<Table> allTables = new ArrayList<>();
		
		Table t1 = new Table(11, 2);
		Table t2 = new Table(12, 4);
		Table t3 = new Table(13, 8);
		Table t4 = new Table(14, 8);
		
		allTables.add(t1);
		allTables.add(t2);
		allTables.add(t3);
		allTables.add(t4);
		
		assertEquals(22, dtaa.returnTotalCapcityOfTables(allTables));
	}
	
	@Test
	public void testDefaultGetTableArrangementResult() throws ExPeopleNumExceedTotalCapacity {
		DefaultTableArrangementAlgorithm dtaa = DefaultTableArrangementAlgorithm.getInstance();
		
		int peopleNum = 11;
		
		ArrayList<Integer> tableCapacityTypeList = new ArrayList<>();
		
		tableCapacityTypeList.add(8);
		tableCapacityTypeList.add(4);
		tableCapacityTypeList.add(2);
		
		ArrayList<Table> allTables = new ArrayList<>();
		
		Table t1 = new Table(11, 2);
		Table t2 = new Table(12, 4);
		Table t3 = new Table(13, 8);
		Table t4 = new Table(14, 8);
		Table t5 = new Table(15, 4);
		
		allTables.add(t1);
		allTables.add(t2);
		allTables.add(t3);
		allTables.add(t4);
		allTables.add(t5);
		
		ArrayList<Table> availableTables = new ArrayList<>();
		
		availableTables.add(t1);
		availableTables.add(t3);
		availableTables.add(t4);
		availableTables.add(t5);
		
		ArrayList<Integer> tableArrangementResults = new ArrayList<>();
		
		
		tableArrangementResults = dtaa.getTableArrangementResult(peopleNum, availableTables, tableCapacityTypeList, allTables);
		
		ArrayList<Integer> ExpectedTableArrangementResults = new ArrayList<>();
		ExpectedTableArrangementResults.add(1);
		ExpectedTableArrangementResults.add(1);
		ExpectedTableArrangementResults.add(0);
		
		assertEquals(ExpectedTableArrangementResults, tableArrangementResults);
	}
	
	@Test
	public void testDefaultGetTableArrangementResultPPLNumEqualCapacityLastTable() throws ExPeopleNumExceedTotalCapacity {
		DefaultTableArrangementAlgorithm dtaa = DefaultTableArrangementAlgorithm.getInstance();
		
		int peopleNum = 2;
		
		ArrayList<Integer> tableCapacityTypeList = new ArrayList<>();
		
		tableCapacityTypeList.add(8);
		tableCapacityTypeList.add(4);
		tableCapacityTypeList.add(2);
		
		ArrayList<Table> allTables = new ArrayList<>();
		
		Table t1 = new Table(11, 8);
		Table t2 = new Table(12, 8);
		Table t3 = new Table(13, 2);
		
		allTables.add(t1);
		allTables.add(t2);
		allTables.add(t3);
		
		ArrayList<Table> availableTables = new ArrayList<>();
	
		availableTables.add(t3);
		
		ArrayList<Integer> tableArrangementResults = new ArrayList<>();
		
		
		tableArrangementResults = dtaa.getTableArrangementResult(peopleNum, availableTables, tableCapacityTypeList, allTables);
		
		ArrayList<Integer> ExpectedTableArrangementResults = new ArrayList<>();
		ExpectedTableArrangementResults.add(0);
		ExpectedTableArrangementResults.add(0);
		ExpectedTableArrangementResults.add(1);
		
		assertEquals(ExpectedTableArrangementResults, tableArrangementResults);
	}
	
	@Test
	public void testDefaultGetTableArrangementResultPPLNumEqualCapacityNotLastTable() throws ExPeopleNumExceedTotalCapacity {
		DefaultTableArrangementAlgorithm dtaa = DefaultTableArrangementAlgorithm.getInstance();
		
		int peopleNum = 4;
		
		ArrayList<Integer> tableCapacityTypeList = new ArrayList<>();
		
		tableCapacityTypeList.add(8);
		tableCapacityTypeList.add(4);
		tableCapacityTypeList.add(2);
		
		ArrayList<Table> allTables = new ArrayList<>();
		
		Table t1 = new Table(11, 8);
		Table t2 = new Table(12, 8);
		Table t3 = new Table(13, 2);
		
		allTables.add(t1);
		allTables.add(t2);
		allTables.add(t3);
		
		ArrayList<Table> availableTables = new ArrayList<>();
	
		availableTables.add(t3);
		
		ArrayList<Integer> tableArrangementResults = new ArrayList<>();
		
		
		tableArrangementResults = dtaa.getTableArrangementResult(peopleNum, availableTables, tableCapacityTypeList, allTables);
		
		ArrayList<Integer> ExpectedTableArrangementResults = new ArrayList<>();
		ExpectedTableArrangementResults.add(0);
		ExpectedTableArrangementResults.add(0);
		ExpectedTableArrangementResults.add(2);
		
		assertEquals(ExpectedTableArrangementResults, tableArrangementResults);
	}
	
	@Test
	public void testDefaultGetTableArrangementResultPPLNumExceedCapacity() throws ExPeopleNumExceedTotalCapacity {
		DefaultTableArrangementAlgorithm dtaa = DefaultTableArrangementAlgorithm.getInstance();
		
		int peopleNum = 9;
		
		ArrayList<Integer> tableCapacityTypeList = new ArrayList<>();
		
		tableCapacityTypeList.add(8);
		tableCapacityTypeList.add(4);
		tableCapacityTypeList.add(2);
		
		ArrayList<Table> allTables = new ArrayList<>();
		
		Table t1 = new Table(11, 2);
		Table t2 = new Table(12, 2);
		Table t3 = new Table(13, 2);
		Table t4 = new Table(14, 4);
		Table t5 = new Table(15, 8);
		
		allTables.add(t1);
		allTables.add(t2);
		allTables.add(t3);
		allTables.add(t4);
		allTables.add(t5);
		
		ArrayList<Table> availableTables = new ArrayList<>();
	
		availableTables.add(t5);
		
		ArrayList<Integer> tableArrangementResults = new ArrayList<>();
		
		
		tableArrangementResults = dtaa.getTableArrangementResult(peopleNum, availableTables, tableCapacityTypeList, allTables);
		
		ArrayList<Integer> ExpectedTableArrangementResults = new ArrayList<>();
		ExpectedTableArrangementResults.add(1);
		ExpectedTableArrangementResults.add(0);
		ExpectedTableArrangementResults.add(1);
		
		assertEquals(ExpectedTableArrangementResults, tableArrangementResults);
	}
	
	// ------------------------------------RecommendedTableArrangementAlgorithm--------------------------------------
	@Test
	public void testRecommendedInitializeTableArrangementList() {
		RecommendedTableArrangementAlgorithm rtaa = RecommendedTableArrangementAlgorithm.getInstance();
		ArrayList<Integer> tableCapacityTypeList = new ArrayList<>();
		tableCapacityTypeList.add(2);
		tableCapacityTypeList.add(4);
		tableCapacityTypeList.add(8);
		
		ArrayList<Integer> tableArrangementResults = rtaa.initializeTableArrangementList(tableCapacityTypeList);
		
		ArrayList<Integer> ExpectedTableArrangementResults = new ArrayList<>();
		ExpectedTableArrangementResults.add(0);
		ExpectedTableArrangementResults.add(0);
		ExpectedTableArrangementResults.add(0);
		
		assertEquals(ExpectedTableArrangementResults, tableArrangementResults);
	}
	
	
	@Test
	public void testRecommendedGetTableArrangementResult() {
		// Has optimised Recommended Arrangement
		RecommendedTableArrangementAlgorithm dtaa = RecommendedTableArrangementAlgorithm.getInstance();
		
		int peopleNum = 11;
		
		ArrayList<Integer> tableCapacityTypeList = new ArrayList<>();
		
		tableCapacityTypeList.add(8);
		tableCapacityTypeList.add(4);
		tableCapacityTypeList.add(2);
		
		ArrayList<Table> allTables = new ArrayList<>();
		
		Table t1 = new Table(11, 2);
		Table t2 = new Table(12, 4);
		Table t3 = new Table(13, 8);
		Table t4 = new Table(14, 8);
		Table t5 = new Table(15, 4);
		
		allTables.add(t1);
		allTables.add(t2);
		allTables.add(t3);
		allTables.add(t4);
		allTables.add(t5);
		
		ArrayList<Table> availableTables = new ArrayList<>();
		
		availableTables.add(t1);
		availableTables.add(t3);
		availableTables.add(t4);
		availableTables.add(t5);
		
		ArrayList<Integer> tableArrangementResults = new ArrayList<>();
		
		tableArrangementResults = dtaa.getTableArrangementResult(peopleNum, availableTables, tableCapacityTypeList, allTables);
		
		ArrayList<Integer> ExpectedTableArrangementResults = new ArrayList<>();
		ExpectedTableArrangementResults.add(2);
		ExpectedTableArrangementResults.add(0);
		ExpectedTableArrangementResults.add(0);
		
		assertEquals(ExpectedTableArrangementResults, tableArrangementResults);
	}
	
	@Test
	public void testRecommendedGetTableArrangementResultPPLNumExceedCapacity() {
		// No optimised Recommended Arrangement 
		RecommendedTableArrangementAlgorithm dtaa = RecommendedTableArrangementAlgorithm.getInstance();
		
		int peopleNum = 31;
		
		ArrayList<Integer> tableCapacityTypeList = new ArrayList<>();
		
		tableCapacityTypeList.add(8);
		tableCapacityTypeList.add(4);
		tableCapacityTypeList.add(2);
		
		ArrayList<Table> allTables = new ArrayList<>();
		
		Table t1 = new Table(11, 2);
		Table t2 = new Table(12, 4);
		Table t3 = new Table(13, 8);
		Table t4 = new Table(14, 8);
		Table t5 = new Table(15, 4);
		
		allTables.add(t1);
		allTables.add(t2);
		allTables.add(t3);
		allTables.add(t4);
		allTables.add(t5);
		
		ArrayList<Table> availableTables = new ArrayList<>();
		
		availableTables.add(t1);
		availableTables.add(t3);
		availableTables.add(t4);
		availableTables.add(t5);
		
		ArrayList<Integer> tableArrangementResults = new ArrayList<>();
		
		tableArrangementResults = dtaa.getTableArrangementResult(peopleNum, availableTables, tableCapacityTypeList, allTables);
		
		assertEquals(null, tableArrangementResults);
	}
	
	
	@Test
	void testgetUsername() {
		Customers cus=new Customers("yinch33","C0001");
		String result=cus.getUsername();
		assertEquals("yinch33",result);
	}
	@Test
	void testgetUserId() {
		Customers cus=new Customers("yinch33","C0001");
		String result=cus.getUserId();
		assertEquals("C0001",result);
	}
	@Test
	void testcheckisReserved1() {
		class customerstub extends Customers{
			private Reservation reserve = null;
			public customerstub(String username, String userid) {
				super(username, userid);
				
			}
			
		}
		customerstub cus=new customerstub("yinch33","C0001");
		boolean result=cus.checkisReserved();
		assertEquals(false,result);
	}
	
	@Test
	public void testGetRestaurantOwned() {
		assertEquals("KFC", merchant.getRestaurantOwned().toString());
	}
	
	@Test
	public void testAddDish() {
		String dishName = "ABC Dish";
		double dishPrice = 199.9;
		merchant.addtoMenu(dishName, dishPrice);
		ArrayList<Dish> menu = merchant.getRestaurantOwned().getMenu();
		Dish dish = merchant.getRestaurantOwned().getDishbyName(dishName);
		assertEquals(true, menu.contains(dish));
	}
	
	
	@Test
	public void testFindDish() {
		String dishName;
		double dishPrice;
		dishName = "ABC Dish";
		dishPrice = 199.9;
		merchant.addtoMenu(dishName, dishPrice);;
	
		assertEquals("ABC Dish", merchant.findDish(dishName).toString());
	}
	
	// ----- Dish Operation -----
	@Test
	public void testEditDishName() {
		Dish dishA = new Dish("A Dish", 12.9);
		dishA.setDishName("Dish B");
		assertEquals("Dish B", dishA.getdishname());
	}
	
	@Test
	public void testEditDishPrice() {
		Dish dishA = new Dish("A Dish", 12.9);
		dishA.setDishPrice(129.9);
		assertEquals(129.9, dishA.getdishPrice());
	}
	
	// ----- Restaurant Operation -----
	@Test
	public void testAddDishtoMenu() {
		Dish dishA = new Dish("A Dish", 12.9);
		Restaurants r = merchant.getRestaurantOwned();
		r.adddishtoMenu(dishA);
		assertEquals(true, merchant.getRestaurantOwned().getMenu().contains(dishA));
	}
	
	@Test
	public void testAddRemoveDishfromMenu() {
		Dish dishA = new Dish("A Dish", 12.9);
		Restaurants r = merchant.getRestaurantOwned();
		r.adddishtoMenu(dishA);
		assertEquals(true, merchant.getRestaurantOwned().getMenu().contains(dishA));
		
		r.deletedishfromMenu(dishA);
		assertEquals(false, merchant.getRestaurantOwned().getMenu().contains(dishA));
	}
	
	@Test
	public void testRemoveNotExistDishfromMenu() {
		Dish dishA = new Dish("A Dish", 12.9);
		Restaurants r = merchant.getRestaurantOwned();
		r.deletedishfromMenu(dishA);
		
		assertEquals(false, merchant.getRestaurantOwned().getMenu().contains(dishA));
	}
	
	@Test
	public void testCountPrice() {
		Restaurants r = merchant.getRestaurantOwned();
		
		Dish dishA = new Dish("A Dish", 100);
		Dish dishB = new Dish("B Dish", 200.5);
		Dish dishC = new Dish("C Dish", 199.5);
		
		r.adddishtoMenu(dishA);
		r.adddishtoMenu(dishB);
		r.adddishtoMenu(dishC);
		
		ArrayList<Dish> orders = new ArrayList<>();
		orders.add(dishA);
		orders.add(dishB);
		orders.add(dishC);
		
		assertEquals(500, r.countPrice(orders));
	}
	
	@Test
	public void testGetDishByNameNotExist() {
		Restaurants r = merchant.getRestaurantOwned();
		assertEquals(null, r.getDishbyName("X Dish"));
	}
	
	@Test
	public void testUpdateMenu() {
		Restaurants r = merchant.getRestaurantOwned();
		ArrayList<Dish> newMenu = new ArrayList<>();
		
		Dish dishA = new Dish("A Dish", 100);
		Dish dishB = new Dish("B Dish", 200.5);
		Dish dishC = new Dish("C Dish", 199.5);
		
		newMenu.add(dishA);
		newMenu.add(dishB);
		newMenu.add(dishC);
		
		r.updateMenu(newMenu);
		assertEquals(newMenu, r.getMenu());
		
		ArrayList<Dish> menuModified = new ArrayList<>(r.getMenu());
		ArrayList<Integer> idx = new ArrayList<>();
		idx.add(1);
		idx.add(2);
		idx.add(3);
        Collections.sort(idx, Collections.reverseOrder());
        for (int i : idx) {
            menuModified.remove(i - 1);
        }
        r.updateMenu(menuModified);		
		assertEquals(true, r.getMenu().isEmpty());
	}

	
	@Test
	public void timeSlotTest1() throws ExTimeFormatInvalid {
		TimeSlots.setOpenAndCloseTime("10:00:00", "19:00:00");
		TimeSlot ts1;
		try {
			ts1 = new TimeSlot("09:00:00","21:00:00","tester1");
		} catch (Exception e) {
			assertEquals(true, e instanceof ExTimeSlotInvalid);
		}
	}
	
	@Test
	public void timeSlotTest2() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots tss = new TimeSlots();
		TimeSlots.setOpenAndCloseTime("10:00:00", "19:00:00");
		TimeSlot ts1 = new TimeSlot("12:00:00","13:00:00","tester1");
		assertEquals(true, tss.addSlot(ts1));	
	}
	
	@Test
	public void timeSlotTest3() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots tss = new TimeSlots();
		TimeSlots.setOpenAndCloseTime("10:00:00", "19:00:00");
		TimeSlot ts1 = new TimeSlot("12:00:00","13:00:00","tester1");
		TimeSlot ts2 = new TimeSlot("12:00:00","13:00:00","tester2");
		tss.addSlot(ts1);	
		assertEquals(false, tss.addSlot(ts2));	
	}
	
	@Test
	public void timeSlotTest4() {
		TimeSlots tss = new TimeSlots("2022-10-27");
		LocalDate thisday = LocalDate.parse("2022-10-27");
		assertEquals(thisday, tss.getDate());	
	}
	
	@Test
	public void timeSlotTest5() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots tss = new TimeSlots("2022-10-27");
		TimeSlot ts1 = new TimeSlot("12:00:00","13:00:00","tester1");
		tss.addSlot(ts1);
		 LocalTime time = LocalTime.parse("14:00:00");
		assertEquals(null, tss.checkReserver(time));	
	}
	
	@Test
	public void timeSlotTest6() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots tss = new TimeSlots("2022-10-27");
		TimeSlot ts1 = new TimeSlot("12:00:00","13:00:00","tester1");
		tss.addSlot(ts1);
		 LocalTime time = LocalTime.parse("12:30:00");
		assertEquals("tester1", tss.checkReserver(time));	
	}
	
	@Test
	public void timeSlotTest7() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots tss = new TimeSlots("2022-10-27");
		TimeSlots.setOpenAndCloseTime("10:00:00", "19:00:00");
		TimeSlot ts1 = new TimeSlot("10:00:00","11:00:00","tester1");
		TimeSlot ts2 = new TimeSlot("11:25:00","13:00:00","tester2");
		tss.addSlot(ts1);
		tss.addSlot(ts2);
		
		assertEquals("13:00-19:00", tss.getAvailableSlots());	
	}
	
	@Test
	public void timeSlotTest8() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots ts = new TimeSlots();
		TimeSlots.setOpenAndCloseTime("10:00:00", "19:00:00");
		TimeSlot timeslot = new TimeSlot("12:00:00","13:00:00","0");
		assertEquals(true, ts.addSlot(timeslot));	
	}
	
	
	@Test
	public void timeSlotTest9() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots ts = new TimeSlots();
		TimeSlots.setOpenAndCloseTime("10:00:00", "19:00:00");
		TimeSlot timeslot1 = new TimeSlot("12:00:00","13:00:00","0");
		ts.addSlot(timeslot1);
		TimeSlot timeslot2 = new TimeSlot("12:00:00","13:00:00","0");
		assertEquals(false, ts.addSlot(timeslot2));
	}
	
	@Test
	public void timeSlotTest10() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots ts = new TimeSlots("2022-10-27");
		TimeSlot timeslot1 = new TimeSlot("12:00:00","13:00:00","0");
		ts.addSlot(timeslot1);
		LocalTime time = LocalTime.parse("12:30:00");
		assertEquals("0", ts.checkReserver(time));	
	}
	
	@Test
	public void timeSlotTest11() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots ts = new TimeSlots("2022-10-27");
		TimeSlot timeslot1 = new TimeSlot("12:00:00","13:00:00","0");
		ts.addSlot(timeslot1);
		LocalTime time = LocalTime.parse("13:30:00");
		assertEquals(null, ts.checkReserver(time));	
	}
	
	@Test
	public void timeSlotTest12() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots ts = new TimeSlots("2022-10-27");
		TimeSlots.setOpenAndCloseTime("10:00:00", "19:00:00");
		TimeSlot timeslot1 = new TimeSlot("10:00:00","11:00:00","5");
		TimeSlot timeslot2 = new TimeSlot("11:35:00","13:00:00","4");
		ts.addSlot(timeslot1);
		ts.addSlot(timeslot2);
		assertEquals("11:00-11:35, 13:00-19:00", ts.getAvailableSlots());	
	}
	
	@Test
	public void timeSlotTest13() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots ts = new TimeSlots("2022-10-27");
		TimeSlots.setOpenAndCloseTime("10:00:00", "19:00:00");
		TimeSlot timeslot1 = new TimeSlot("10:00:00","11:00:00","5");
		ts.addSlot(timeslot1);
		LocalTime time = LocalTime.parse("10:30:00");
		ts.remove(timeslot1);
		assertEquals(false, ts.hasReserved(time));	
	}
	
	@Test
	public void timeSlotTest14() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots ts = new TimeSlots("2022-10-27");
		TimeSlots.setOpenAndCloseTime("10:00:00", "19:00:00");
		TimeSlot timeslot1 = new TimeSlot("10:00:00","11:00:00","5");
		TimeSlot timeslot2 = new TimeSlot("11:35:00","13:00:00","4");
		ts.addSlot(timeslot1);
		ts.addSlot(timeslot2);
		LocalTime time = LocalTime.parse("12:30:00");
		assertEquals(true, ts.hasReserved(time));	
	}
	
	@Test
	public void timeSlotTest15() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots ts = new TimeSlots("2022-10-27");
		TimeSlots.setOpenAndCloseTime("10:00:00", "19:00:00");
		TimeSlot timeslot = new TimeSlot("11:35:00","13:00:00","4");
		ts.addSlot(timeslot);
		LocalTime time = LocalTime.parse("15:30:00");
		assertEquals(-1, ts.checkReservedStatus(time));	
	}
	
	@Test
	public void timeSlotTest16() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots ts = new TimeSlots("2022-10-27");
		TimeSlots.setOpenAndCloseTime("10:00:00", "19:00:00");
		TimeSlot timeslot = new TimeSlot("11:35:00","13:00:00","4");
		ts.addSlot(timeslot);
		LocalTime time = LocalTime.parse("11:35:00");
		assertEquals(0, ts.checkReservedStatus(time));	
	}

	@Test
	public void timeSlotTest17() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots ts = new TimeSlots("2022-10-27");
		TimeSlots.setOpenAndCloseTime("10:00:00", "19:00:00");
		TimeSlot timeslot1 = new TimeSlot("10:00:00","11:00:00","5");
		TimeSlot timeslot = new TimeSlot("11:35:00","13:00:00","4");
		ts.addSlot(timeslot1);
		ts.addSlot(timeslot);
		LocalTime time = LocalTime.parse("11:10:00");
		assertEquals(1, ts.checkReservedStatus(time));	
	}
	
	@Test
	public void timeSlotTest18() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots ts = new TimeSlots("2022-10-27");
		TimeSlots.setOpenAndCloseTime("10:00:00", "19:00:00");
		TimeSlot timeslot = new TimeSlot("11:35:00","13:00:00","4");
		ts.addSlot(timeslot);
		LocalTime time = LocalTime.parse("11:35:00");
		assertEquals(true, ts.checkValidReserver(time, "4"));	
	}
	

	@Test
	public void timeSlotTest19() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
		TimeSlots ts = new TimeSlots("2022-10-27");
		TimeSlots.setOpenAndCloseTime("10:00:00", "19:00:00");
		TimeSlot timeslot = new TimeSlot("11:35:00","13:00:00","4");
		ts.addSlot(timeslot);
		LocalTime time = LocalTime.parse("15:35:00");
		assertEquals(false, ts.checkValidReserver(time, "4"));	
	}
	
	
	
	@Test
	public void manualClockTest1() {	
		ManualClock mc = ManualClock.getInstance();
		mc.changeTime("23:59");
		assertEquals(LocalTime.of(23, 59), ManualClock.getTime());
	}
	
	@Test
	public void manualClockTest2() {
		ManualClock mc = ManualClock.getInstance(); 
		LocalDate first = LocalDate.now();
		mc.newDay();
		assertEquals(first.plusDays(1), ManualClock.getDate());
	}
	
	
	
	
	@Test
	public void tableManagementTest1() { 
		try {
			tm.addNewTable(0, 4);
			tm.addNewTable(0, 4);
		} catch (Exception e) {
			assertEquals(true, e instanceof ExTableIdAlreadyInUse);
		} 
	}
	
	@Test
	public void tableManagementTest2() throws ExTableIdAlreadyInUse, ExTableNotExist, ExUnableToRemoveTable {
		tm.addNewTable(20, 8);
		tm.removeTable(20);
		tm.addNewTable(1, 4);
		String result = "\nBelow is the available tables: \n"+ "Num of Available 4-Seats Table: 1 \n";
		assertEquals(result, tm.showAvailableTables());
	}
	
	@Test
	public void tableManagementTest3() throws ExTableIdAlreadyInUse, 
			ExTimeSlotInvalid, ExTimeFormatInvalid, ExTableNotExist, ExTimeSlotAlreadyBeReserved {
		tm.addNewTable(0, 4);
		TimeSlot ts = new TimeSlot("12:00:00","13:00:00","0");
		boolean result = tm.reserveTableAccordingToTimeslot(0, ts);
		assertEquals(true, result);
		
	}
	
	@Test
	public void tableManagementTest4() throws ExTimeSlotInvalid, ExTimeFormatInvalid, ExTableNotExist, ExTableIdAlreadyInUse {
		initialization.initialize();
		TimeSlot ts = new TimeSlot("12:30:00","13:00:00","1");
		try {
			tm.reserveTableAccordingToTimeslot(1, ts);
			tm.reserveTableAccordingToTimeslot(1, ts);
		} catch (Exception e) {
			assertEquals(true, e instanceof ExTimeSlotAlreadyBeReserved);
		}
	}
	@Test
	public void tableManagementTest5() throws ExTimeSlotInvalid, ExTimeFormatInvalid, ExTableNotExist, 
			ExTimeSlotAlreadyBeReserved, ExTableIdAlreadyInUse {
		initialization.initialize();
		TimeSlot ts = new TimeSlot("12:00:00","13:00:00","1");
		try {
			tm.cancelReservationAccordingToTimeslot(1, ts);
		} catch (Exception e) {
			assertEquals(true, e instanceof ExTimeSlotNotReservedYet);
		}
		
	}
	@Test
	public void tableManagementTest6() throws ExTableIdAlreadyInUse {
		tm.addNewTable(1, 4);
		tm.addNewTable(2, 5);
		assertEquals(1, tm.returnAvailableTableNumWithCapacity(4));
	}

	@Test
	public void tableManagementTest7() throws ExTableIdAlreadyInUse {
		tm.addNewTable(0, 4);
		String correct = "\nBelow is the available tables: \n" + "Num of Available 4-Seats Table: 1 \n";
		String result = tm.showAvailableTables();
		assertEquals(correct, result);
	}

	@Test
	public void tableManagementTest8() throws ExTableIdAlreadyInUse {
		tm.addNewTable(1, 4);
		String correct = "\nBelow is the available tables: \n" + "Num of Available 4-Seats Table: 1 \n";
		assertEquals(correct, tm.showAvailableTables());
	}

	@Test
	public void tableManagementTest9_reservedDineIn() throws ExTableIdAlreadyInUse, ExCustomersIdNotFound, 
			ExTableNotExist, ExTimeSlotAlreadyBeReserved, ExTimeSlotInvalid, ExTimeFormatInvalid, ExUnableToSetOpenCloseTime, 
			ExTimeSlotNotReservedYet {
		initialization.initialize();
		ManualClock mc = ManualClock.getInstance();
		String username = "tester";
		String password = "tester";
		acMag.registerCustomer(username, password);
		String cID = acMag.login(username, password);
		Customers tester = db.matchCId(cID);
		ArrayList<Integer> desiredTable = new ArrayList<Integer>();
		desiredTable.add(1);
		desiredTable.add(2);
		desiredTable.add(3);
		Reservation testR = new Reservation(cID, "12:00-13:00", desiredTable,  ManualClock.getDate().plusDays(1));
		tester.setReserve(testR);
		mc.newDay();
		mc.changeTime("11:30");
		assertEquals(true, tester.checkisReserved());
		assertEquals(false, tester.isReserveTime());
		mc.changeTime("12:15");
		assertEquals(true, tester.checkisReserved());
		assertEquals(true, tester.isReserveTime());
		for (Integer i : tester.getReservedTableIDs()) {
			tm.reserverCheckIn(i, tester.getReservationTimeSlot(), cID);
		}
		ArrayList<Integer> occupiedTableId = tester.getOccupiedTableId();
		assertEquals(true, occupiedTableId.contains(1));
		assertEquals(true, occupiedTableId.contains(2));
		assertEquals(true, occupiedTableId.contains(3));
		assertEquals(false, occupiedTableId.contains(4));
	}
	
	@Test
	public void tableManagementTest10_queueingDineIn() {
		
	}

	//----------------------------------------------Customer-----------------------------------------------------
	
		@Test
		public void testGetUsername() throws ExTableIdAlreadyInUse, ExCustomersIdNotFound {
			initialization.initialize();
			Customers c = db.matchCId("C0001");
			
			assertEquals("yinch33", c.getUsername());
		}
		
		@Test
		public void testGetUserId() throws ExTableIdAlreadyInUse, ExCustomersIdNotFound {
			initialization.initialize();
			Customers c = db.matchCId("C0001");
			
			assertEquals("C0001", c.getID());
		}
		
		@Test
		public void testCustomerSetStateUpdate() throws ExTableIdAlreadyInUse, ExCustomersIdNotFound {
			initialization.initialize();
			Customers c = db.matchCId("C0001");
			assertEquals(true, c.getState() instanceof CustomerVIPstate);
			
			c.setState(new CustomerSuperVIPstate());
			assertEquals(true, c.getState() instanceof CustomerSuperVIPstate);
		}
		
		@Test
		public void testCheckIsReserved() throws ExCustomersIdNotFound, ExTableIdAlreadyInUse {
			initialization.initialize();

			Customers c = db.matchCId("C0001");
			
			
			
		}
		
		
	//----------------------------------------------Test Reservation-----------------------------------------------------
		@Test
		public void testcheckValid1() throws ExCustomersIdNotFound, ExTableIdAlreadyInUse, ExTableNotExist, ExTimeSlotAlreadyBeReserved, ExTimeSlotInvalid, ExTimeFormatInvalid {
			initialization.initialize();
			
			Customers c = db.matchCId("C0001");
			LocalDate currDate = LocalDate.of(2022, 12, 4);
			String customerID = "C0001";
			String timeSlotString = "12:00-13:00";
			LocalDate date = LocalDate.of(2022, 12, 1);
			
			ArrayList<Integer> desiredTableIDs = new ArrayList<>();
			desiredTableIDs.add(1);
			desiredTableIDs.add(3);
			desiredTableIDs.add(7);
			
			Reservation r = new Reservation(customerID, timeSlotString, desiredTableIDs, date);
			
			assertEquals(-1, r.checkValid(currDate));
		}
		
		@Test
		public void testcheckValid2() throws ExCustomersIdNotFound, ExTableIdAlreadyInUse, ExTableNotExist, ExTimeSlotAlreadyBeReserved, ExTimeSlotInvalid, ExTimeFormatInvalid {
			initialization.initialize();
			
			Customers c = db.matchCId("C0001");
			LocalDate currDate = LocalDate.of(2022, 12, 1);
			String customerID = "C0001";
			String timeSlotString = "12:00-13:00";
			LocalDate date = LocalDate.of(2022, 12, 1);
			
			ArrayList<Integer> desiredTableIDs = new ArrayList<>();
			desiredTableIDs.add(1);
			desiredTableIDs.add(3);
			desiredTableIDs.add(7);
			
			Reservation r = new Reservation(customerID, timeSlotString, desiredTableIDs, date);
			
			assertEquals(0, r.checkValid(currDate));
		}
		
		@Test
		public void testcheckValid3() throws ExCustomersIdNotFound, ExTableIdAlreadyInUse, ExTableNotExist, ExTimeSlotAlreadyBeReserved, ExTimeSlotInvalid, ExTimeFormatInvalid {
			initialization.initialize();
			
			LocalDate currDate = LocalDate.of(2022, 11, 30);
			String customerID = "C0001";
			String timeSlotString = "12:00-13:00";
			LocalDate date = LocalDate.of(2022, 12, 1);
			
			ArrayList<Integer> desiredTableIDs = new ArrayList<>();
			desiredTableIDs.add(1);
			desiredTableIDs.add(3);
			desiredTableIDs.add(7);
			
			Reservation r = new Reservation(customerID, timeSlotString, desiredTableIDs, date);
			
			assertEquals(1, r.checkValid(currDate));
		}
		
	//----------------------------------------------Test CommandCustomerReservation-----------------------------------------------------

		
		@Test
		public void testReserveCreated() throws ExCustomersIdNotFound, ExTableIdAlreadyInUse {
			initialization.initialize();
			
			Customers c = db.matchCId("C0001");
			CommandCustomerReservation cmdCustomerReserve = new CommandCustomerReservation(c);
			
			
			String customerID = "C0001";
			String timeSlotString = "12:00-13:00";
			LocalDate date = ManualClock.getDate();
			
			ArrayList<Integer> desiredTableIDs = new ArrayList<>();
			desiredTableIDs.add(1);
			desiredTableIDs.add(3);
			desiredTableIDs.add(7);
			
			
			Object r = cmdCustomerReserve.setReserve(customerID, timeSlotString, desiredTableIDs, date);

			assertEquals(true, r instanceof Reservation);
				
		}
		@Test
		public void testReserveTimeFormatInvalid() throws ExTableIdAlreadyInUse, ExCustomersIdNotFound {
			initialization.initialize();
			
			Customers c = db.matchCId("C0001");
			CommandCustomerReservation cmdCustomerReserve = new CommandCustomerReservation(c);
			
			String customerID = "C0001";
			String timeSlotString = "12:00 - 13:00";
			LocalDate date = ManualClock.getDate();
			
			ArrayList<Integer> desiredTableIDs = new ArrayList<>();
			desiredTableIDs.add(1);
			desiredTableIDs.add(3);
			desiredTableIDs.add(7);
			
			Object r = cmdCustomerReserve.setReserve(customerID, timeSlotString, desiredTableIDs, date);

			assertEquals(null, r);
		}
		
		@Test
		public void testReserveTimeSlotInvalid() throws ExTableIdAlreadyInUse, ExCustomersIdNotFound {
			initialization.initialize();

			Customers c = db.matchCId("C0001");
			CommandCustomerReservation cmdCustomerReserve = new CommandCustomerReservation(c);
			
			String customerID = "C0001";
			String timeSlotString = "12:00-17:00";
			LocalDate date = ManualClock.getDate();
			
			ArrayList<Integer> desiredTableIDs = new ArrayList<>();
			desiredTableIDs.add(1);
			desiredTableIDs.add(3);
			desiredTableIDs.add(7);
			
			Object r = cmdCustomerReserve.setReserve(customerID, timeSlotString, desiredTableIDs, date);
			assertEquals(null, r);
		}
		
		@Test
		public void testReserve() throws ExTableNotExist, ExTimeSlotAlreadyBeReserved, ExTimeSlotInvalid, ExTimeFormatInvalid, ExCustomersIdNotFound, ExTableIdAlreadyInUse {
			initialization.initialize();

			Customers c = db.matchCId("C0001");
			
			String customerID = "C0001";
			String timeSlotString = "12:00-13:00";
			LocalDate date = ManualClock.getDate();
			
			ArrayList<Integer> desiredTableIDs = new ArrayList<>();
			desiredTableIDs.add(1);
			desiredTableIDs.add(3);
			desiredTableIDs.add(7);
			
			Reservation r = new Reservation(customerID, timeSlotString, desiredTableIDs, date);
			c.setReserve(r);
			
			assertEquals(desiredTableIDs, r.getReservedTableIDs());
			
			assertEquals("12:00" ,c.getReservation().getReservedTimeSlot().getStart().toString());
			assertEquals("13:00" ,c.getReservation().getReservedTimeSlot().getEnd().toString());
			assertEquals("C0001" ,c.getReservation().getReservedTimeSlot().getCustomerID());

		}
		
	// ----------------------------------------------Cancel Reservation------------------------------------------------------
		
		@Test
		public void testCancelReservation() throws ExTableIdAlreadyInUse, ExCustomersIdNotFound, ExUnableToSetOpenCloseTime, ExTableNotExist, ExTimeSlotNotReservedYet, ExTimeSlotAlreadyBeReserved, ExTimeSlotInvalid, ExTimeFormatInvalid {
			initialization.initialize();

			Customers c = db.matchCId("C0001");
			CommandCustomerCancelReservation cmdCancelReserve = new CommandCustomerCancelReservation(c);
			
			String customerID = "C0001";
			String timeSlotString = "12:00-13:00";
			LocalDate date = ManualClock.getDate();
			
			ArrayList<Integer> desiredTableIDs = new ArrayList<>();
			desiredTableIDs.add(1);
			desiredTableIDs.add(3);
			desiredTableIDs.add(7);
			
			Reservation r = new Reservation(customerID, timeSlotString, desiredTableIDs, date);
			c.setReserve(r);
			
			assertEquals(true, c.getReservation() instanceof Reservation);
			
			cmdCancelReserve.exe();
			
			assertEquals(null, c.getReservation());
		}
		

		@Test
		public void testCancelReservationNotReserved() throws ExCustomersIdNotFound, ExTableIdAlreadyInUse, ExTableNotExist, ExTimeSlotNotReservedYet, ExUnableToSetOpenCloseTime {
			initialization.initialize();
			Customers c = db.matchCId("C0001");
			c.clearReservation();
			assertEquals(null, c.getReservation());
			CommandCustomerCancelReservation cmdCancelReserve = new CommandCustomerCancelReservation(c);
			cmdCancelReserve.exe();
		}
		
		@Test
		void testCheckoutbyCustomer() throws ExTableIdAlreadyInUse {
			initialization.initialize();
			TablesManagement tm=TablesManagement.getInstance();
			ArrayList<Integer> array=new ArrayList<Integer>();		
			array.add(1);
			tm.checkOutByCustomer(array);
			assertEquals(true, tm.getAvailableTables().contains(tm.returnTableAccordingToTableId(1)));
			assertEquals(false, tm.getOccupiedTables().contains(tm.returnTableAccordingToTableId(1)));
		}
		
		@Test
		void testDineinOperation() {
			class customerstub extends Customers{
				public customerstub(String username, String userid) {
					super(username, userid);
				}
				
				public boolean checkisReserved() {
					return false;
				}
			}
			
			class cmd_stub extends CommandCustomerDineIn {
				public cmd_stub(Customers customer) {
					super(customer);
				}
				
				public boolean dineInOperation() {
					return super.dineInOperation();
				}
		  	}
			Customers cus = new customerstub("yinch33","C0001");
			cmd_stub customerdinein = new cmd_stub(cus);
			boolean result=customerdinein.dineInOperation();
			assertEquals(false,result);
		}
		
		@Test
		void testDirectWalkin() {
			Customers cus=new Customers("yinch33","C0001");
			class cmd_stub extends CommandCustomerDineIn {
				public cmd_stub(Customers customer) {
					super(customer);
				}
				
				public boolean directWalkIn(ArrayList<Integer> array) {
					return super.directWalkIn(array);
				}
		  	}
			cmd_stub customerdinein = new cmd_stub(cus);
			ArrayList<Integer> array=new ArrayList<Integer>();
			array.add(2);
			boolean result=customerdinein.directWalkIn(array);
			assertEquals(true,result);	
		}
		
		@Test
		void testaddCheckInInfo() {
			Customers cus=new Customers("yinch33","C0001");
			class cmd_stub extends CommandCustomer {
				public cmd_stub(Customers customer) {
					super(customer);
				}
				
				@Override
				public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
						ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {
				}
		  	}
		  	Commands cmd = new cmd_stub(cus);
			ArrayList<Integer> array=new ArrayList<Integer>();
			array.add(1);
			CommandCustomerDineIn.addCheckInInfo(array);
			assertEquals(array,cus.getOccupiedTableId());
		}
		
		@Test
		void equalsInTimeSlotTest() throws ExTimeSlotInvalid, ExTimeFormatInvalid {
			TimeSlot ts = new TimeSlot("11:00", "12:00", "tester");
			TimeSlot ts1 = ts;
			TimeSlot ts2 = new TimeSlot("11:00", "12:00", "tester1");
			Table ts3 = new Table(99,99);
			assertEquals(true, ts.equals(ts1));
			assertEquals(true, ts.equals(ts2));
			assertEquals(false, ts.equals(ts3));
		}
		
		@Test
		 public void testSetWaitingTableInTableManagement() throws ExPeopleNumExceedTotalCapacity, ExTableIdAlreadyInUse, ExCustomersIdNotFound{
			initialization.initialize();
			String username = "tester";
			String password = "tester";
			acMag.registerCustomer(username, password);
			String cID = acMag.login(username, password);
			ArrayList<Integer> defaultArrangment1=new ArrayList<>();
			tm.toDefaultAlgo();
			defaultArrangment1=tm.getTableArrangement(8);
		  	tm.setWalkInStatus(defaultArrangment1);
		  	ArrayList<Integer> defaultArrangment2=new ArrayList<>();
		  	tm.toDefaultAlgo();
		  	defaultArrangment2=tm.getTableArrangement(16);
		  	Customers c = db.matchCId(cID);
		  	class cmd_stub extends CommandCustomer {
				public cmd_stub(Customers customer) {
					super(customer);
				}
				
				@Override
				public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
						ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {
				}
		  	}
		  	Commands cmd = new cmd_stub(c);
		  	tm.setWaitingTables(cID,defaultArrangment2);
		  	boolean containsC = tm.waitingCustomersContains(cID);
		  	assertEquals(true, containsC);
		 }
		
		@Test
	 	public void testSetTableFromAvailableToOccupied() throws ExTableIdAlreadyInUse {
	  		tm.addNewTable(11, 2);
	  		tm.addNewTable(12, 4);
	  		Table t1=tm.returnTableAccordingToTableId(11);
	  		Table t2=tm.returnTableAccordingToTableId(12);
	  		tm.setTableFromAvailableToOccupiedStatus(11);
	  		tm.setTableFromAvailableToOccupiedStatus(12);
	  		ArrayList<Table> occupiedTableList=tm.getOccupiedTables();
	  		boolean testRes1=occupiedTableList.contains(t1);
	  		boolean testRes2=occupiedTableList.contains(t2);
	  		assertEquals(true, testRes1);
	  		assertEquals(true, testRes2);
	 	}

	 	@Test
	 	public void testSetTableFromOccupiedToAvailable() throws ExTableIdAlreadyInUse{
	  		tm.addNewTable(11, 2);
	  		tm.addNewTable(12, 4);
	  		tm.setTableFromAvailableToOccupiedStatus(11);
	  		tm.setTableFromAvailableToOccupiedStatus(12);
	  		Table t1=tm.returnTableAccordingToTableId(11);
	  		Table t2=tm.returnTableAccordingToTableId(12);
	  		tm.setTableFromOccupiedToAvailable(11);
	  		tm.setTableFromOccupiedToAvailable(12);
	  		ArrayList<Table> availableTableList=tm.getAvailableTables();
	  		boolean testRes1=availableTableList.contains(t1);
	  		boolean testRes2=availableTableList.contains(t2);
	  		assertEquals(true, testRes1);
	  		assertEquals(true, testRes2);
	  	}

	 	@Test
	 	public void testSetTableFromAvailableToReserved() throws ExTableIdAlreadyInUse{
	  		tm.addNewTable(11, 2);
	  		tm.addNewTable(12, 4);
	  		Table t1=tm.returnTableAccordingToTableId(11);
	  		Table t2=tm.returnTableAccordingToTableId(12);
	  		tm.setTableFromAvailableToReservedStatus(11);
	  		tm.setTableFromAvailableToReservedStatus(12);
	  		ArrayList<Table> reservedTableList=tm.getReservedTables();
	  		boolean testRes1=reservedTableList.contains(t1);
	  		boolean testRes2=reservedTableList.contains(t2);
	  		assertEquals(true, testRes1);
	  		assertEquals(true, testRes2); 
	 	}

	 	@Test
	 	public void testSetTableFromReservedToAvailable() throws ExTableIdAlreadyInUse{
	  		tm.addNewTable(11, 2);
	  		tm.addNewTable(12, 4);
	  		tm.setTableFromAvailableToReservedStatus(11);
	  		tm.setTableFromAvailableToReservedStatus(12);
	  		Table t1=tm.returnTableAccordingToTableId(11);
	  		Table t2=tm.returnTableAccordingToTableId(12);
	  		tm.setTableFromReservedToAvailable(11);
	  		tm.setTableFromReservedToAvailable(12);
	  		ArrayList<Table> availableTableList=tm.getAvailableTables();
	  		boolean testRes1=availableTableList.contains(t1);
	  		boolean testRes2=availableTableList.contains(t2);
	  		assertEquals(true, testRes1);
	  		assertEquals(true, testRes2);
	  	}

	 	@Test
	 	public void testSetTableFromReservedToOccupied() throws ExTableIdAlreadyInUse{
	  		tm.addNewTable(11, 2);
	  		tm.addNewTable(12, 4);
	  		Table t1=tm.returnTableAccordingToTableId(11);
	  		Table t2=tm.returnTableAccordingToTableId(12);
	  		tm.setTableFromAvailableToReservedStatus(11);
	  		tm.setTableFromAvailableToReservedStatus(12);
	  		ArrayList<Table> reservedTableList=tm.getReservedTables();
	  		boolean testRes1=reservedTableList.contains(t1);
	  		boolean testRes2=reservedTableList.contains(t2);
	  		assertEquals(true, testRes1);
	  		assertEquals(true, testRes2);
	  		tm.setTableFromReservedToOccupiedStatus(11);
	  		tm.setTableFromReservedToOccupiedStatus(12);
	  		ArrayList<Table> occupiedTableList=tm.getOccupiedTables();
	  		boolean testRes3=occupiedTableList.contains(t1);
	  		boolean testRes4=occupiedTableList.contains(t2);
	  		assertEquals(true, testRes3);
	  		assertEquals(true, testRes4);
	  	}

	 	@Test
	 	public void testSetTableFromOccupiedToReserved() throws ExTableIdAlreadyInUse{
	  		tm.addNewTable(11, 2);
	  		tm.addNewTable(12, 4);
	  		tm.setTableFromAvailableToOccupiedStatus(11);
	  		tm.setTableFromAvailableToOccupiedStatus(12);
	  		Table t1=tm.returnTableAccordingToTableId(11);
	  		Table t2=tm.returnTableAccordingToTableId(12);
	  		ArrayList<Table> occupiedTableList=tm.getOccupiedTables();
	  		boolean testRes3=occupiedTableList.contains(t1);
	  		boolean testRes4=occupiedTableList.contains(t2);
	  		assertEquals(true, testRes3);
	  		assertEquals(true, testRes4);
	  		tm.setTableFromOccupiedToReserved(11);
	  		tm.setTableFromOccupiedToReserved(12);
	  		ArrayList<Table> reservedTableList=tm.getReservedTables();
	  		boolean testRes1=reservedTableList.contains(t1);
	  		boolean testRes2=reservedTableList.contains(t2);
	  		assertEquals(true, testRes1);
	  		assertEquals(true, testRes2);
	 	}
	 	
	 	@Test
	 	public void testcanDirectlyWalkIn() throws ExTableIdAlreadyInUse, ExPeopleNumExceedTotalCapacity{
	 		tm.addNewTable(11, 2);
	 		tm.addNewTable(12, 4);
	 		ArrayList<Integer> defaultArrangment1=new ArrayList<>();
	 		tm.toDefaultAlgo();
	 	    defaultArrangment1=tm.getTableArrangement(3);
	 	    boolean res1=tm.canDirectlyDineIn(defaultArrangment1);
	 	    assertEquals(true,res1);
	 	    tm.setWalkInStatus(defaultArrangment1);
	 	    ArrayList<Integer> defaultArrangment2=new ArrayList<>();
	 	    tm.toDefaultAlgo();
	 	    defaultArrangment2=tm.getTableArrangement(3);
	 	    boolean res2=tm.canDirectlyDineIn(defaultArrangment2);
	 	    assertEquals(false,res2);
	 	}
	 	
	 	@Test
	 	public void testGetTotalCapacity() throws ExTableIdAlreadyInUse, ExPeopleNumExceedTotalCapacity, ExCustomersIdNotFound{
	 		tm.addNewTable(11, 2);
	 		tm.addNewTable(12, 4);
	 	  
	 		assertEquals(6,tm.returnTotalCapcityOfTables());
	 	     
	 	}
	 	
	 	@Test
	 	public void testshowReservationTables() throws ExTableIdAlreadyInUse, ExPeopleNumExceedTotalCapacity, ExCustomersIdNotFound{
	 		initialization.initialize();
	 		TablesManagement.getInstance().clear();
	 		tm.addNewTable(11, 2);
	 		tm.addNewTable(12, 4);
	 		String Msg="\nTable(s) for tomorrow reservation and available time slots: \n"
	 				+"2-Seats Table with ID of 11 is available tmr for the timeslots: 00:00-23:59 \n"
	 				+"4-Seats Table with ID of 12 is available tmr for the timeslots: 00:00-23:59 \n";
	 		String actualMsg=tm.showReservationTable();
	 		assertEquals(Msg,actualMsg);
	 	     
	 	}
	 	
	 	@Test
	 	public void testCheckOutByCustomer() throws ExTableIdAlreadyInUse, ExPeopleNumExceedTotalCapacity, ExCustomersIdNotFound{
	 		tm.addNewTable(11, 2);
	 		tm.addNewTable(12, 4);
	 	    String username = "tester1";
	 	    String password = "tester1";
	 	    acMag.registerCustomer(username, password);
	 	    String cID = acMag.login(username, password);
	 	    ArrayList<Integer> defaultArrangment1=new ArrayList<>();
	 	    tm.toDefaultAlgo();
	 	    defaultArrangment1=tm.getTableArrangement(6);
	 	    tm.setWalkInStatus(defaultArrangment1);
	 	    ArrayList<Integer> defaultArrangment2=new ArrayList<>();
	 	    tm.toDefaultAlgo();
	 	    defaultArrangment2=tm.getTableArrangement(6);
	 	    Customers c = db.matchCId(cID);
	 	    class cmd_stub extends CommandCustomer {
	 	    	public cmd_stub(Customers customer) {
	 	    		super(customer);
	 	    	}
	 	      
	 	    	@Override
	 	    	public void exe() throws ExUnableToSetOpenCloseTime, ExTableIdAlreadyInUse, ExTableNotExist,
	 	        		ExTimeSlotNotReservedYet, ExCustomersIdNotFound, ExTimeSlotAlreadyBeReserved {
	 	    	}
	 	    }
	 	   Commands cmd = new cmd_stub(c);
	 	   tm.setWaitingTables(cID,defaultArrangment2);
	 	   ArrayList<Integer> allId=new ArrayList<Integer>();
	 	   allId.add(11);
	 	   allId.add(12);
	 	   tm.checkOutByCustomer(allId);
	 	   boolean containsC = tm.waitingCustomersContains(cID);
	 	   assertEquals(true, containsC);
	 	}
	 	
	 	
	 	@Test
		void testsortByValue() {
			HashMap<String, String> hashmap =new HashMap<String, String>();
			hashmap.put("Custom","C0001");
			hashmap.put("Admin","A0001");
			Set<Entry<String, String>> result=AccountManagement.sortByValue(hashmap);
			LinkedHashMap<String, String> sortedByValue = new LinkedHashMap<String, String>();
			sortedByValue.put("Admin", "A0001");
			sortedByValue.put("Custom", "C0001");
			Set<Entry<String, String>> smallset=sortedByValue.entrySet();
			AccountManagement.sortByValue(hashmap);
			assertEquals(smallset,result);
		}
	 	
		@Test
		void testdistinguishMerchantandCustomer1() {
			String userid="A0001";
			AccountManagement ac=AccountManagement.getInstance();
			AdminModule am=AdminModule.getInstance();
			assertEquals(am,ac.distinguishMerchantandCustomer(userid));
		}
		
		@Test
		void testdistinguishMerchantandCustomer2() {
			String userid="C0001";
			AccountManagement ac=AccountManagement.getInstance();
			CustomerModule cm=CustomerModule.getInstance();
			assertEquals(cm,ac.distinguishMerchantandCustomer(userid));
		}
		
		@Test
		void testdistinguishMerchantandCustomer3() {
			String userid="M0001";
			AccountManagement ac=AccountManagement.getInstance();
			MerchantModule cm=MerchantModule.getInstance();
			assertEquals(cm,ac.distinguishMerchantandCustomer(userid));
		}
		
		@Test
		void testdistinguishMerchantandCustomer4() {
			String userid="S0001";
			AccountManagement ac=AccountManagement.getInstance();
			assertEquals(null,ac.distinguishMerchantandCustomer(userid));
		}
		
		@Test
		void testReservationToString1() throws ExTableNotExist, ExTimeSlotAlreadyBeReserved, 
			ExTimeSlotInvalid, ExTimeFormatInvalid {
			
			ArrayList<Integer> table = new ArrayList<Integer>();
			Reservation r=new Reservation("C0001", "08:00-10:00", table, ManualClock.getDate());
			String result=r.toString();
			String s="\n[C0001] Error: Reservation not made.\n";
			assertEquals(s,result);
		}
		
		@Test
		void testReservationToString2() throws ExTableNotExist, ExTimeSlotAlreadyBeReserved, 
			ExTimeSlotInvalid, ExTimeFormatInvalid, ExTableIdAlreadyInUse {
			
			initialization.initialize();
			ArrayList<Integer> table = new ArrayList<Integer>();
			table.add(1);
			Reservation r = new Reservation("C0001", "08:00-10:00", table, ManualClock.getDate());
			String result = r.toString();
			String s = "\n[C0001] Reservation made, Reserved tables: "
					+"\n[1] [Table with ID: 1] [Time Slot: 08:00-10:00]";
			assertEquals(s,result);
		}
		
		@Test
		public void testCustomerGetReservationInfo() throws ExTableIdAlreadyInUse, ExTableNotExist, 
			ExTimeSlotAlreadyBeReserved, ExTimeSlotInvalid, ExTimeFormatInvalid {
			
			initialization.initialize();
			TablesManagement.getInstance().clear();
			tm.addNewTable(11, 2);
			tm.addNewTable(12, 4);
			Customers c= new Customers("tester","C0008");
			ArrayList<Integer> reserveTd=new ArrayList<Integer>();
			reserveTd.add(11);
			reserveTd.add(12);
			LocalDate thisday = LocalDate.parse("2022-10-27");
			Reservation r=new Reservation("C0008","11:00-12:00",reserveTd,thisday);
			c.setReserve(r);
			String Msg= String.format("\nThe upcoming reservation of Customer %s for %s is: ", "C0008","tomorrow");
			Msg += "\n[1] [Table with ID: 11] [Time Slot: 11:00-12:00]";
			Msg += "\n[2] [Table with ID: 12] [Time Slot: 11:00-12:00]";
		  
		    String actualMsg=c.getReserveInfo();
		    assertEquals(Msg,actualMsg); 
		}
		
		@Test
		public void testCustomerGetReservationReminder() throws ExTableIdAlreadyInUse, ExTableNotExist,
			ExTimeSlotAlreadyBeReserved, ExTimeSlotInvalid, ExTimeFormatInvalid {
			
			initialization.initialize();
			TablesManagement.getInstance().clear();
			
			tm.addNewTable(11, 2);
			tm.addNewTable(12, 4);
			Customers c= new Customers("tester","C0008");
			ArrayList<Integer> reserveTd=new ArrayList<Integer>();
			reserveTd.add(11);
		  	reserveTd.add(12);
		  	LocalDate thisday = LocalDate.parse("2022-10-27");
		  	Reservation r=new Reservation("C0008","11:00-12:00",reserveTd,thisday);
		  	c.setReserve(r);
		  	String Msg= String.format("\nReminder: You have a reservation for %s: ","tomorrow");
		  	Msg += "\n[1] [Table with ID: 11] [Time Slot: 11:00-12:00]";
		    Msg += "\n[2] [Table with ID: 12] [Time Slot: 11:00-12:00]";
		        
		  
		    String actualMsg=c.getReserveReminder();
		    assertEquals(Msg,actualMsg);  
		}
		
		@Test
		public void testCustomerUpdateOrders() throws ExTableIdAlreadyInUse, ExTableNotExist, 
			ExTimeSlotAlreadyBeReserved, ExTimeSlotInvalid, ExTimeFormatInvalid {
		
			initialization.initialize();
			TablesManagement.getInstance().clear();
			tm.addNewTable(11, 2);
			tm.addNewTable(12, 4);
			
			Customers c= new Customers("tester","C0008");
			Dish d=new Dish("H",35);
			Restaurants r= new Restaurants("H");
			
			c.addPendingOrder(d);
			c.updateOrder(r);
			
			Multimap<Dish, Restaurants> DishToRestaurant =ArrayListMultimap.create();
			DishToRestaurant.put(d, r);
			boolean res=c.isDishToRestaurant(DishToRestaurant);
			
			assertEquals(true,res);
			
			Dish d1=new Dish("M",35);
			Restaurants r1= new Restaurants("M");
			c.addPendingOrder(d1);
			c.updateOrder(r1);
			DishToRestaurant.put(d1, r1);    
		}
		
		
		@Test
		public void testCustomerUpdateState() throws ExTableIdAlreadyInUse, ExTableNotExist, 
			ExTimeSlotAlreadyBeReserved, ExTimeSlotInvalid, ExTimeFormatInvalid {
			
			Customers c= new Customers("tester","C0008");
			c.setBillAmount(89);
			c.updateState();
		  	CustomerState cs=new CustomerSuperVIPstate();
		  	assertEquals(cs.getdiscount(),c.getState().getdiscount());
		  	Customers c1= new Customers("tester1","C0009");
		  	c1.setBillAmount(85);
		  	c1.updateState();
		  	CustomerState cs1=new CustomerVIPstate();
		  	assertEquals(cs1.getdiscount(),c1.getState().getdiscount());   
		}
}
