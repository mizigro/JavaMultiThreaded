
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerMain {
	
	private static ServerSocket server;
	private static int port = 7999;
	public static String dateformattype = "/dd/ HH:mm:ss";

	public static ArrayList<OrderItem> orders = new ArrayList<OrderItem>();
	
	public static Map<String, Integer> stockLeft = new HashMap<String, Integer> ();
	public static Map<String, Boolean> stockRenewalReqd = new HashMap<String, Boolean>();
	public static Map<String, Integer> itemCost = new HashMap<String, Integer>();
	public static int stockRenewalInterval = 10 * 1000; // 2 seconds
	public static int maxStockValue = 100;
	
	public static int deliveryTime = 4 * 1000; // 2 seconds
	public static int TeaPrepTime = 2; // in seconds 
	public static int CoffeePrepTime = 2; // in seconds 
		
	public static void stockRenew(String item){
		stockRenewalReqd.put(item, true);
	}

	public static void main(String[] args){
		// add initial stocks
		stockLeft.put("Snacks",maxStockValue);
		stockLeft.put("Cookie",maxStockValue);
		stockLeft.put("Juice",maxStockValue);
		stockLeft.put("Peanuts",maxStockValue);
		
		// stock renewals required set to 
		// false for initialization
		stockRenewalReqd.put("Snacks",false);
		stockRenewalReqd.put("Cookie",false);
		stockRenewalReqd.put("Juice",false);
		stockRenewalReqd.put("Peanuts",false);
		
		// set item costs
		itemCost.put("Tea",3);
		itemCost.put("Coffee",5);
		itemCost.put("Snacks",15);
		itemCost.put("Cookie",12);
		itemCost.put("Juice",13);
		itemCost.put("Peanuts",10);
		
		// stock renewal machine
		Runnable sr = new StockRenewer();
		new Thread(sr).start();
		
		// delivery guy
		Runnable deliveryGuy = new DeliveryGuy();
		new Thread(deliveryGuy).start();
		
		// run the server
		runServer();
	}
	
	private static void runServer(){
		try {
			server = new ServerSocket(port, 100);
			Runnable r = new OrderReceiver(server, port);
			new Thread(r).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void showMsg(String msg){
		System.out.println("("+getTime()+") : "+msg);
	}
	
	public static String getTime(){
	    DateFormat dateFormat = new SimpleDateFormat(dateformattype);
	    Date date = new Date();
	    return dateFormat.format(date);
	}
	
	
}