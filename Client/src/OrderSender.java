import java.io.*;
import java.net.*;

import com.sun.javafx.collections.MappingChange.Map;


public class OrderSender implements Runnable{
	
	private ObjectOutputStream outstream;
	private ObjectInputStream instream;
	private Socket socket;
	private String serverIP = "127.0.0.1";
	private int port;
	private String details;
	
	public OrderSender(String details, int port){
		this.details = details;
		this.port = port;
	}
	
	public void run(){
		try{
			// setup socket connection
			connectServer();
			ClientMain.logMsg("Connected to server.");
			setupStreams();

			// send initial message
			sendMsg(this.details);
			
			// message receive loop
			String response = "";
			do{
				try {
					response = ( String ) instream.readObject();
					ClientMain.logMsg(response);
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}while ( !response.startsWith ("SERVER - INVOICE" ) );
			
			
			// end connection
			ClientMain.logMsg("Client Closed Connection");
			sendMsg("END");
			closeConnection();
			
		}catch(EOFException exception){
			ClientMain.showMsg("Client terminated connection");
		}catch(IOException exc){
			ClientMain.showMsg("Could not connect to server");
		}
	}

	private void connectServer() throws IOException{
		socket = new Socket(InetAddress.getByName(serverIP), port);
	}
	
	private void setupStreams() throws IOException{
		outstream = new ObjectOutputStream(socket.getOutputStream());
		outstream.flush();
		instream = new ObjectInputStream(socket.getInputStream());
	}
	
	private void sendMsg(String details){
		try{
			outstream.writeObject("CLIENT - " + details);
			outstream.flush();
		}catch(IOException ioException){
			ClientMain.showMsg("Oops! Something went wrong!");
		}
	}
	
	private void closeConnection () throws IOException {
		outstream.close();
		instream.close();
		socket.close();
	}
}
