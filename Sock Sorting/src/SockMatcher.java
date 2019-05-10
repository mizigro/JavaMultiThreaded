import java.util.*;

public class SockMatcher implements Runnable{
	public ArrayList<Sock> unmatchedSocks;
	private int idleTime; // milliseconds
	
	public SockMatcher(int idleTime){
		this.idleTime = idleTime;
		this.unmatchedSocks = new ArrayList<Sock>();
	}
	/* pick a matched sock 
	 * from unmatched socks*/
	private Sock[] pickSockPair(){
		Sock[] matchedSocks = null;
		boolean flag = false;
		
		synchronized(this.unmatchedSocks){
			synchronized(System.out){
				System.out.print("\n\nUnmatched Socks -> ");
				SystemProgram.printSocks(this.unmatchedSocks);
			}
			
			for(int i=0;i<this.unmatchedSocks.size();i++){
				for(int j=0;j<this.unmatchedSocks.size();j++){
					
					if(i!= j && this.unmatchedSocks.get(i).color == this.unmatchedSocks.get(j).color){
							
						/* get matched socks 
						 * to be returned */
						matchedSocks = new Sock[2];
						matchedSocks[0] = this.unmatchedSocks.get(i);
						matchedSocks[1] = this.unmatchedSocks.get(j);
						
						/* remove matched socks */
						this.unmatchedSocks.remove(i);
						if(i<j) j = j-1; // as i cannot be equal to j
						this.unmatchedSocks.remove(j);

						synchronized(System.out){
							System.out.print("\n\t");
							SystemProgram.printSock(matchedSocks[0]);
							System.out.print(" Matched with ");
							SystemProgram.printSock(matchedSocks[1]);
							System.out.println();
						}
						
						flag = true;
						break;
					}
				
				}
				if(flag) break;
			}
			if(!flag)System.out.println("\tNo socks matched");
		}
		return matchedSocks;
	}
	
	public void passToShelfManager(Sock[] sockPair){
		synchronized(SystemProgram.shelfManager.sockPairs){
			if(sockPair!=null) SystemProgram.shelfManager.sockPairs.add(sockPair);
		}
	}
	
	public void run(){
		while(true){
			/* match one sock */
			Sock[] sockPair= this.pickSockPair();
			this.passToShelfManager(sockPair);

			/* wait for few seconds */
			try {
				Thread.sleep(this.idleTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
