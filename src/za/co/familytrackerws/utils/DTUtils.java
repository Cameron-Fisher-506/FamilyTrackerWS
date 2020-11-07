package za.co.familytrackerws.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DTUtils 
{
	public static String getCurrentDateTime() { 
		String toReturn = "";
		
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date now = new Date();
	    
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(now);
	    calendar.add(Calendar.HOUR_OF_DAY,2); // this will add two hours
	    now = calendar.getTime();
	    toReturn = sdfDate.format(now);
	    
	    return toReturn;
	}
}
