package net.sourceforge.squirrel_sql.plugins.dataimport.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;


public class DateUtils {
	private static Vector<DateFormat> formats = new Vector<DateFormat>();
	
	static {
		formats.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
		formats.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		formats.add(new SimpleDateFormat("yyyy-MM-dd"));
		formats.add(new SimpleDateFormat("HH:mm:ss"));
		formats.add(new SimpleDateFormat("dd.MM.yyyy"));
	
	}
	
	public static Date parseSQLFormats(String value) {
	
		Date parsedDate = null;
		for (DateFormat f : formats) {
			parsedDate = parse(f, value);
			if (parsedDate != null)
				break;
		}
		return parsedDate;
	}
	
	private static Date parse(DateFormat format, String value) {
		Date d = null;
		try {
			d = format.parse(value);
		} catch (ParseException pe) {
			
		}
		return d;
	}

}
