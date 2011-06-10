package awesome.persistence.manager;

import java.util.Date;

public class DateFormatter {

	public static String dateToString(Date d){
		return Long.toString(d.getTime());
	}
	
	public static Date dateFromString(String dateString) {
		return new Date(Long.decode(dateString));
	}
}
