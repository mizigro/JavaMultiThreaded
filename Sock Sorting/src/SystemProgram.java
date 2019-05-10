import java.util.*;

public class SystemProgram {
	// idle time
	public static int shelfIdleTime = 1000;
	public static int matcherIdleTime = 1000;
	public static int pickerIdleTime = 500;
	
	// 
	public static ArrayList<Sock> socks = new ArrayList<Sock>();
	public static SockMatcher sockMatcher = new SockMatcher(matcherIdleTime);
	public static ShelfManager shelfManager = new ShelfManager(shelfIdleTime);
	public static Map<SockColor, ArrayList<Sock[]>> shelf = new HashMap<SockColor, ArrayList<Sock[]>>();
	
	/* print shelf sock
	 * pairs for each color
	 * */
	public static void printShelf(){
		synchronized(System.out){
			System.out.println("\n\nShelf Status:");
			for(Object key : shelf.keySet()){
				System.out.print("\t"+key.toString()+" : ");
				for(int i=0;i<shelf.get(key).size();i++){
					Sock[] pair = shelf.get(key).get(i);
					System.out.print("("+pair[0].sid);
					System.out.print(", "+pair[1].sid);
					System.out.print(") ");
				}
				System.out.println();
			}
		}
	}
	
	/* print list 
	 * of socks 
	 * */
	public static void printSocks(ArrayList<Sock> socks){
		synchronized(System.out){
			for(int k=0;k<socks.size();k++)
				printSock(socks.get(k));
			System.out.println();
		}
	}
	
	public static void printSock(Sock sock){
		System.out.print("("+sock.sid+", "+sock.color+") ");
	}
	
	public static void main(String[] args){
		// get number of socks
		Scanner io = new Scanner(System.in);
		int n_socks = Integer.parseInt(io.next());
		io.close();
		if(n_socks <= 0) throw new java.lang.Error("Number of socks cannot be less than 1");
		if(n_socks > 100) throw new java.lang.Error("Number of socks too large");
		
		// add n_socks sock with random color
		for(int i=1;i<=n_socks;i++)
			socks.add(new Sock(i, SockColor.values()[new Random().nextInt(SockColor.values().length)]));
		
		// print socks
		for(int i=0;i<n_socks;i++)
			printSock(socks.get(i));
		
		// initialize sock list for each shelf color
		for(int i=0;i<SockColor.values().length;i++)
			shelf.put(SockColor.values()[i], new ArrayList<Sock[]>());
		
		// sock pickers
		Runnable sp1 = new SockPicker(1, pickerIdleTime);
		Runnable sp2 = new SockPicker(2, pickerIdleTime);
		
		// other machines
		Runnable sm = sockMatcher;
		Runnable shm = shelfManager;
		
		// start all machines
		new Thread(sp1).start();
		new Thread(sp2).start();
		new Thread(sm).start();
		new Thread(shm).start();
	}
}
