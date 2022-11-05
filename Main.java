import java.util.ArrayList;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        // ArrayList<Integer> tableList = new ArrayList<Integer>();
        // System.out.println(tableList.get(0));
        TableManagement tm = TableManagement.getInstance();
        tm.addNewTable(0, 8);
        tm.addNewTable(1, 8);
        tm.addNewTable(2, 8);
        tm.addNewTable(3, 4);
        // System.out.println(tm.getTableCapcityTypList().size());
        tm.showAvailableTables();
        // tm.showReservationTable();
        // tm.canDirectlyDineIn(tm.arrangeTableAccordingToNumOfPeople(25));
        tm.canDirectlyDineIn(tm.recommendedArrangementAccordingToWaitingTime(23));
        tm.setWalkInStatus(tm.recommendedArrangementAccordingToWaitingTime(23));
        tm.showAvailableTables();
        // System.out.println(tm.returnAvailableTableNumWithCapacity(8));
        // Restaurants res = new Restaurants();
        // Dish dish = new Dish("beijingkaoya", 20);
        // Dish dish2 = new Dish("xiaolongbao", 40);
        // res.adddishtoMenu(dish);
        // res.adddishtoMenu(dish2);
        // ArrayList menu = res.showMenutoCustomer();

        // // test
        // Scanner scanner = new Scanner(System.in); // 创建Scanner对象
        // System.out.print("Input your dish: "); // 打印提示
        // String name = scanner.nextLine(); // 读取一行输入并获取字符串

        // Customer customers = new Customer("username", 0);

        // for (int i = 0; i < menu.size(); i++) {
        // if (((Dish) menu.get(i)).getdishname().equals(name)) {
        // customers.orderdish((Dish) (menu.get(i)));
        // }
        // }
        // ArrayList orders = customers.customerOrders();
        // int price = res.countPrice(orders);
        // System.out.println(price);
        // }
    }
}
