import java.util.Map;


public class OrderItem {
	public String uid;
	public Map<String, Integer> details;
	public String location;
	public String expectedTime;
	
	public OrderItem(String uid, Map<String, Integer> details, String loc, String expectedTime){
		this.uid = uid;
		this.details = details;
		this.location = loc;
		this.expectedTime = expectedTime;
	}
}
