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
   * 
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
   * 
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

  /**
   * 將民國年10802120000轉為 108/02/12 00:00
   * @param date
   * @return
   */
  public static String convertChineseTimeToFormatTime(String date) {
    if (date == null || date.length() == 0) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < date.length(); i++) {
      sb.append(date.charAt(i));
      if (i == 2 || i == 4) {
        sb.append("/");
      } else if (i == 6) {
        sb.append(" ");
      } else if (i == 8 || i == 10) {
        sb.append(":");
      }
    }
    return sb.toString();
  }
  
  /**
   * 由申報年月取得當月的10號 date，用來查DRG code用.
   * @param applYM
   * @return
   */
  public static Date getDateByApplYM(String applYM) {
    int ym = Integer.parseInt(applYM);
    ym = ym * 100 + 10;
    return convertChineseToYear(String.valueOf(ym));
  }
}
