import java.util.*;

public class SockPicker implements Runnable{
	/* sock picker id */
	public int pickerId;
	private Sock currSock;
	private int idleTime; // milliseconds
	
	/* create unique sock ID's */
	public SockPicker(int picker_id, int idleTime){
		this.idleTime = idleTime;
		this.pickerId = picker_id;
	}
	
	/* pick a random sock
	 * and remove it from 
	 * socks array
	 * */
	public Sock pickRandomSock(){
		synchronized(SystemProgram.socks){
			try{
				if(SystemProgram.socks.size() > 0){
					/* get random index
					 * from socks list */
					int random_index = new Random().nextInt(SystemProgram.socks.size());
					
					/* get sock id of
					 * random index */
					Sock sock = SystemProgram.socks.get(random_index);
					
					/* remove sock of
					 * given index */
					SystemProgram.socks.remove(random_index);
					
					/* return sock id */
					return sock;
				}else{
					/* no more socks left */
					return null;
				}
			}catch(Exception e){
				throw new java.lang.Error("Exception at popRandomSock(): "+e);
			}
		}
	}
	
	public void passSockToMatcher(Sock sock){
		synchronized(SystemProgram.sockMatcher.unmatchedSocks){
			SystemProgram.sockMatcher.unmatchedSocks.add(sock);
			
			synchronized(System.out){
				System.out.print("\n\t\t\tPicker "+this.pickerId+" passed sock ");
				SystemProgram.printSock(sock);
				System.out.print(" to Matcher");
			}
		}
	}
	
	public void run(){
		/* pick first sock */
		this.currSock = this.pickRandomSock();
		
		/* keep picking socks 
		 * till no socks left 
		 * */
		while(this.currSock!=null){
			// do something with picked sock 
			synchronized (System.out) {
				System.out.print("\n\t\tPicker "+this.pickerId+" picks sock ");
				SystemProgram.printSock(this.currSock);
			}
			
			// pass sock to matcher 
			this.passSockToMatcher(this.currSock);
			
			// pick a sock 
			this.currSock = this.pickRandomSock();
			
			
			// wait for few seconds 
			try {
				Thread.sleep(this.idleTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		/* no more 
		 * socks left */
		System.out.println("\n-- Picker "+this.pickerId+" : no more socks left!");
	}
}
