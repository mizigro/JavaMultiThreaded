
public class DeliveryGuy implements Runnable{
	public void run(){
		while(true)
			try {
				synchronized(ServerMain.orders){
					if(ServerMain.orders.size() > 0){
						int i=0;
						int min_index = 0;
						String min = ServerMain.orders.get(0).expectedTime;
						String threshold = ServerMain.getTime();
						boolean found = false;
						System.out.print("("+ServerMain.getTime()+") : " + "Order expected times ");
						for (;i<ServerMain.orders.size();i++){
							System.out.print(ServerMain.orders.get(i).uid + " : "+ ServerMain.orders.get(i).expectedTime+", ");
							if(ServerMain.orders.get(i).expectedTime.compareTo(threshold) <= 0 ){
								if(ServerMain.orders.get(i).expectedTime.compareTo(min) < 0){
									min_index = i;
									min = ServerMain.orders.get(i).expectedTime;
								}
								found = true;
							}
						}
						System.out.println();
						if(found){
							ServerMain.showMsg("Order with token : "+ServerMain.orders.get(min_index).uid+" delivered!");
							ServerMain.orders.remove(min_index);
						}
					}
				}
				
				// deliver order takes time
				Thread.sleep(ServerMain.deliveryTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
}
