package tw.com.leadtek.tools;

import java.util.Date;

public class VaildateUtil {

	public static boolean isDate(String text) {
		
		try {
			
			Date theDate = DateUtil.yyyy$MM$dd.convertToDate(text);
			
			return DateUtil.yyyy$MM$dd.convertToDate(theDate).equals(text);
		}catch(Exception e) {
			
			return false;
		}
	}
}
