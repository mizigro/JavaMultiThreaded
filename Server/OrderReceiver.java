import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;


public class OrderReceiver implements Runnable{
	private ObjectOutputStream outstream;
	private ObjectInputStream instream;
	private ServerSocket server;
	private Socket socket;
	private int port;
	public static int curToken;
	
	public OrderReceiver(ServerSocket s, int port){
		this.server = s;
		this.port = port;
	}
	
	public void run(){
		while ( true ) {
			try{
				waitForConnection();
				setupStreams();
				whileOrder();
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				closeConnections();
			}
		}
	}
	
	private boolean isInt(String str){
		return str.matches("-?\\d+");
	}
	
	private String processOrder(String details){
		String uid = Integer.toString(curToken++);
		// initializations
		String response = "";
		Map<String, Integer> orderDetails = new HashMap<String, Integer> ();
		String[] stockedItems = {"Cookie", "Snacks", "Juice", "Peanuts"};
		String loc = "", expectedTime;
		String[] eachDetail = details.split(";");
		int prepTime = 0, totalCost = 0, cur_cost;
		
		// get details
		for(int i=0;i<eachDetail.length;i++){
			String[] split = eachDetail[i].split(":");
			if(split.length == 2){
				String label = split[0].trim();
				String value = split[1].trim();
				if(label.equals("Location")){
					loc = value;
				}else{
					if(isInt(value))
						orderDetails.put(label, Integer.parseInt(value));
					else
						return "Invalid Value for key : "+label;
				}
			}else if(split.length == 1){
				orderDetails.put(split[0].trim(), 0);
			}
		}
		
		// calculate preparation time
		prepTime += orderDetails.get("Tea") * ServerMain.TeaPrepTime;
		prepTime += orderDetails.get("Coffee") * ServerMain.CoffeePrepTime;

		// print primary invoice details
		expectedTime = getExpectedTime(prepTime);
		response += "\nExpected Delivery :"+getExpectedTime(prepTime);
		response += "\nTOKEN :\t"+uid;
		response += "\nLocation :\t"+loc;
		response += "\nItem\tAmount\tCost";
		
		// calculate cost of tea and coffee and append to invoice
		cur_cost =  orderDetails.get("Tea")*ServerMain.itemCost.get("Tea");
		totalCost += cur_cost;
		response += "\nTea\t" + orderDetails.get("Tea")+"\t"+cur_cost;
		cur_cost = orderDetails.get("Coffee")*ServerMain.itemCost.get("Coffee");
		totalCost += cur_cost;
		response += "\nCoffee\t" + orderDetails.get("Coffee")+"\t"+cur_cost;
		
		// check stock item availability;
		synchronized(ServerMain.class){
			for(String item : stockedItems){
				int itemsLeft = ServerMain.stockLeft.get(item);
				
				// if stock empty
				if( itemsLeft == 0 )
					ServerMain.stockRenew(item);
				
				// if reqd items available
				if(orderDetails.get(item) > itemsLeft){
					return "Only "+itemsLeft+" "+item+" left! Please Order Again after some time.";
				}else{
					ServerMain.stockLeft.put(item, itemsLeft - orderDetails.get(item));
					
					// calculate cost and append item amt. and cost to invoice
					cur_cost = ServerMain.itemCost.get(item)*orderDetails.get(item);
					if(orderDetails.get(item)>0)
						response += "\n"+item+"\t"+orderDetails.get(item)+"\t"+cur_cost;
					
					totalCost += cur_cost;
				}
				
				// if no more stock left then re-spawn stocks
				if(itemsLeft - orderDetails.get(item) <= 0)
					ServerMain.stockRenew(item);
			}

			// print current stock left
			for(String item : stockedItems){
				ServerMain.showMsg(item+" : " +ServerMain.stockLeft.get(item)+" left");
			}
		}
		
		response += "\nTotal Cost \t\t"+totalCost;
		
		synchronized(ServerMain.orders){
			ServerMain.orders.add(new OrderItem(uid, orderDetails, loc, expectedTime));
		}
		return response;
	}
	
	// get time after current
	public String getExpectedTime(int prepTime){
		DateFormat dateFormat = new SimpleDateFormat(ServerMain.dateformattype);
		Date cur_date = new Date();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(cur_date);
		System.out.println(dateFormat.format(calendar.getTime()));
		calendar.add(Calendar.SECOND, prepTime);
		System.out.println(prepTime);
		System.out.println(dateFormat.format(calendar.getTime()));
		
		return dateFormat.format(calendar.getTime());
	}

	private void whileOrder() throws IOException{
		
		String msg = "";
		
		do{
			try{
				msg = ( String ) instream.readObject();
				ServerMain.showMsg("Client msg received : "+msg);
				
				// if end request then break loop
				if(msg.equals("CLIENT - END")){ 
					break;
				}
				
				// client wants to order something
				else if(msg.startsWith("CLIENT - Order")){
					String order = msg.substring(14,msg.length());
					String invoice = processOrder(order.trim());
					sendMsg("INVOICE : "+invoice);
				}
				
				// any other message
				else{
					ServerMain.showMsg("Invalid Client Msg  \'"+msg+"\'");
					break;
				}
			} catch (ClassNotFoundException cnf) {
				cnf.printStackTrace();
			}
		}while(true);

		sendMsg("END");
		closeConnections();
		ServerMain.showMsg("SERVER DISCONNECTED socket");
	}
	
	private void closeConnections(){
		try {
			instream.close();
			outstream.close();
			socket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void waitForConnection () throws IOException{
		ServerMain.showMsg("WAITING port : "+port);
		socket = server.accept();
		ServerMain.showMsg("Connected : "+socket.getInetAddress().getHostName());
	}

	private void sendMsg(String msg){
		try{
			outstream.writeObject("SERVER - " + msg);
			outstream.flush();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	private void setupStreams() throws IOException{
		outstream = new ObjectOutputStream(socket.getOutputStream());
		outstream.flush();
		instream = new ObjectInputStream(socket.getInputStream());
		ServerMain.showMsg("Streams setup!");
	}
}
