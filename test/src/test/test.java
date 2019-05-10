package test;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class test {
	public static void main(String[] args){
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss::SSS");
		int prepTime = 30;
		Date cur_date = new Date();
		
		int numberOfseconds = 30;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(cur_date);
		calendar.add(Calendar.SECOND, prepTime);

		System.out.println("\nExpected Delivery Time : "+dateFormat.format(cur_date));
		System.out.println("\nExpected Delivery Time : "+dateFormat.format(calendar.getTime()));

	}
}
