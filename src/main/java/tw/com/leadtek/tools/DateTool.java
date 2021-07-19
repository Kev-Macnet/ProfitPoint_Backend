/**
 * Created on 2021/2/23.
 */
package tw.com.leadtek.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTool {

  /**
   * 將西元年月轉成民國年月
   * @param year ex:20210223 
   * @return ex:1100223
   */
  public static String convertToChineseYear(String date) {
    String year = date.substring(0, 4);
    int chineseYear = Integer.parseInt(year) - 1911;
    return String.valueOf(chineseYear) + date.substring(4);
  }
  
  /**
   * 將民國年月日轉成西元年月日
   * @param year ex:20210223 
   * @return ex:1100223
   */
  public static Date convertChineseToYear(String date) {
    int minguo = Integer.parseInt(date);
    int dateInt = 19110000 + minguo;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    try {
      return sdf.parse(String.valueOf(dateInt));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }
}
