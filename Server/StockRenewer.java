
public class StockRenewer implements Runnable{
	public void run(){
		while(true){
			try {
				synchronized(ServerMain.class){
					for(String item : ServerMain.stockLeft.keySet()){
						if(ServerMain.stockRenewalReqd.get(item)){
							ServerMain.stockLeft.put(item, ServerMain.maxStockValue);
							ServerMain.showMsg(item + " : "+ ServerMain.stockLeft.get(item) + " renewed");
							ServerMain.stockRenewalReqd.put(item, false);
						}
					}
//					ServerMain.showMsg("Stock renewal done");
				}
				Thread.sleep(ServerMain.stockRenewalInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
