package tw.com.leadtek.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class DateUtil {

	private final static String fmt_yyyy$MM$dd = "yyyy/MM/dd";
	
	
	public static class yyyy$MM$dd{
		
		public static Date convertToDate(String text) throws ParseException {
			
			if(StringUtils.isBlank(text)) {
				return null;
			}
			
			try {
				return new SimpleDateFormat(fmt_yyyy$MM$dd).parse(text);
			} catch (ParseException e) {
				e.printStackTrace();
				throw e;
			}
		}
		
		public static String convertToDate(Date date) {
			
			if(null == date) {
				return null;
			}
			
			return new SimpleDateFormat(fmt_yyyy$MM$dd).format(date);
		}
		
	}
}
