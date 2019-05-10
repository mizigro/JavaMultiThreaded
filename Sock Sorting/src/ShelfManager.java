import java.util.*;

public class ShelfManager implements Runnable{
	public ArrayList<Sock[]> sockPairs = new ArrayList<Sock[]>();
	public int idleTime; //milliseconds
	
	public ShelfManager(int idleTime){
		this.idleTime = idleTime;
	}
	
	public void putPairToShelf(){
		synchronized(this.sockPairs){
			Map<SockColor, ArrayList<Sock[]>> shelf = SystemProgram.shelf;
			if(this.sockPairs.size()>0){
				// move sock pair to shelf 
				Sock[] sockPair = this.sockPairs.remove(0);
				shelf.get(sockPair[0].color).add(sockPair);
				
				SystemProgram.printShelf();
			}
		}
	}
	
	public void run(){
		while(true){
			this.putPairToShelf();
			
			/* wait for few seconds */
			try {
				Thread.sleep(this.idleTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
