import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

public class ClientMain extends JFrame {
	public static String dateformattype = "/dd/ HH:mm:ss";
	
	private static JTextArea log = new JTextArea(10,20);
	private static JLabel status_output = new JLabel("Order something!");
	private int port = 7999;
	
	JLabel TeaLabel = new JLabel("Tea");
	JLabel CoffeeLabel = new JLabel("Coffee");
	JLabel CookieLabel = new JLabel("Cookie");
	JLabel SnacksLabel = new JLabel("Snacks");
	JLabel JuiceLabel = new JLabel("Juice");
	JLabel PeanutLabel = new JLabel("Peanuts");
	JLabel LocationLabel = new JLabel("Location");
	
	JTextField TeaCount = new JTextField();
	JTextField CoffeeCount = new JTextField();
	JTextField CookieCount = new JTextField();
	JTextField SnacksCount = new JTextField();
	JTextField JuiceCount = new JTextField();
	JTextField PeanutCount = new JTextField();
	JTextField Location = new JTextField();
	JTextField[] TextFields = {TeaCount, CoffeeCount, CookieCount,SnacksCount,JuiceCount,PeanutCount,Location};
	
	JLabel[] Labels = {TeaLabel, CoffeeLabel, CookieLabel,SnacksLabel,JuiceLabel,PeanutLabel, LocationLabel};
	
	public static void main(String[] args){
		new ClientMain();
	}
	
	public static String getTime(){
	    DateFormat dateFormat = new SimpleDateFormat(dateformattype);
	    Date date = new Date();
	    return dateFormat.format(date);
	}
	
	public static void showMsg(String msg){
		status_output.setText(msg);
	}	
	
	public static void logMsg(String msg){
		log.setText("("+getTime()+")\n"+msg+"\n"+log.getText());
	}
	
	private String getDetails(){
		if(!Location.getText().equals("")){
			String details = "";
			for(int i=0;i<Labels.length;i++){
				details += Labels[i].getText()+":"+TextFields[i].getText()+";";
			}
			return "Order "+details;
		}else{
			showMsg("Location Cannot be empty!");
			return "";
		}
	}
	
	public ClientMain(){
		
		this.setSize(300,400);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		for (int i=0;i<Labels.length;i++){
			
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = i;
			
			panel.add(Labels[i], c);

			c.gridx = 1;
			panel.add(TextFields[i], c);
			
			TextFields[i].setPreferredSize(new Dimension(200, 20));
		}
		
		JLabel OrderLabel = new JLabel("Order");
		JButton OrderButton = new JButton("Send");
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = Labels.length;
		panel.add(OrderLabel, c);
		
		c.gridx = 1;
		panel.add(OrderButton, c);
		

		OrderButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String loc = LocationLabel.getText();
				
				if(!loc.equals("")){
					// output some text about order initiation
					String msg = ((JButton) e.getSource()).getText();
					showMsg (  msg + " initiated" );
					
					String details = getDetails();
					if(!details.equals("")){
						Runnable ordr = new OrderSender(getDetails(), port);
						new Thread(ordr).start();
					}
				}else{
					showMsg ("Location cannot be empty!");
				}
			}
		});
		

		JScrollPane scroll = new JScrollPane (log,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		c.ipady = 40;
		c.gridx = 0;
		c.gridy = Labels.length+1;
		c.gridwidth = 2;
		panel.add(status_output, c);
		c.ipady = 1;
		c.gridy = Labels.length+2;
		panel.add(scroll, c);
		
		this.add(panel);
		this.setVisible(true);
	}
}