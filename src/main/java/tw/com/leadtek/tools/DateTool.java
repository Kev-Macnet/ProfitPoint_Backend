/**
 * Created on 2021/2/23.
 */
package tw.com.leadtek.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTool {

  /**
   * 日期格式
   */
  public final static String SDF = "yyyy/MM/dd";

  public final static String MAX_DATE = "2099/12/31";

  /**
   * 將西元年月轉成民國年月
   * 
   * @param year ex:20210223
   * @return ex:1100223
   */
  public static String convertToChineseYear(String date) {
    if (date == null) {
      return null;
    }
    if (date.length() == 7) {
      return date;
    }
    String year = date.substring(0, 4);
    int chineseYear = Integer.parseInt(year) - 1911;
    return String.valueOf(chineseYear) + date.substring(4);
  }

  /**
   * 將民國年月日轉成西元年月日, 若只有年月，會自動改為yyyMM01
   * 
   * @param date ex:1110214
   * @return Date
   */
  public static Date convertChineseToYear(String date) {
    if (date == null) {
      return null;
    }
    if (date.length() == 5) {
      date = date + "01";
    }
    try {
      int minguo = Integer.parseInt(date);
      int dateInt = 19110000 + minguo;
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
  
        return sdf.parse(String.valueOf(dateInt));
    } catch (NumberFormatException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
   
    return null;
  }

  /**
   * 將民國年月日轉成西元年月日, 若只有年月，會自動改為yyyMM01
   * 
   * @param date ex:11102140000
   * @param sdf SimpleDateFormat(yyyyMMddHHmm)
   * @return Date yyyyMMddHHmm
   */
  public static Date convertChineseToYears(String date, SimpleDateFormat sdf) {
    if (date == null) {
      return null;
    }
    if (date.length() == 5) {
      date = date + "010000";
    }
    long minguo = Long.valueOf(date);
    long dateInt = Long.valueOf("191100000000") + minguo;
    if (sdf == null) {
      sdf = new SimpleDateFormat("yyyyMMddHHmm");
    }
    try {
      return sdf.parse(String.valueOf(dateInt));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 將民國年月日轉成西元年月日
   * 
   * @param year ex:1100223
   * @return ex:20210223
   */
  public static String convertChineseToAD(String date) {
    int minguo = Integer.parseInt(date);
    int dateInt = 0;
    if (date.length() == 5) {
      dateInt = 191100 + minguo;
    } else if (date.length() == 7) {
      dateInt = 19110000 + minguo;
    }
    return String.valueOf(dateInt);
  }

  /**
   * 將民國年月日轉成西元年月日
   * 
   * @param year ex:1100223
   * @return ex:2021/02/23
   */
  public static String convertChineseToADWithSlash(String date) {
    StringBuffer sb;
    try {
      int minguo = Integer.parseInt(date);
      int dateInt = 0;
      if (date.length() == 5 || date.length() == 6) {
        dateInt = 191100 + minguo;
      } else if (date.length() == 7) {
        dateInt = 19110000 + minguo;
      }
      sb = new StringBuffer(String.valueOf(dateInt));
      sb.insert(4, '/');
      if (sb.length() > 6) {
        sb.insert(7, '/');
      }
    } catch (NumberFormatException e) {
      e.printStackTrace();
      return null;
    }

    return sb.toString();
  }

  /**
   * 將民國年10802120000轉為 108/02/12 00:00
   * 
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
    if (sb.charAt(sb.length() - 1) == ':') {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  /**
   * 由申報年月取得當月的10號 date，用來查DRG code用.
   * 
   * @param applYM
   * @return
   */
  public static Date getDateByApplYM(String applYM) {
    int ym = Integer.parseInt(applYM);
    ym = ym * 100 + 10;
    return convertChineseToYear(String.valueOf(ym));
  }

  /**
   * 將西元年yyyy/MM/dd改為民國年yyyMMdd
   * 
   * @param s
   * @return
   */
  public static String removeSlashForChineseYear(String s) {
    if (s == null || s.length() == 0) {
      return null;
    }
    int firstSlashIndex = s.indexOf('/');
    if (firstSlashIndex == 3 || firstSlashIndex == 4) {
      // 民國年
      StringBuffer sb = new StringBuffer(s);
      sb.deleteCharAt(firstSlashIndex);
      int index = sb.indexOf("/");
      if (index > 0) {
        sb.deleteCharAt(index);
      }
      index = sb.indexOf(" ");
      if (index > 0) {
        sb.deleteCharAt(index);
      }
      index = sb.indexOf(":");
      if (index > 0) {
        sb.deleteCharAt(index);
      }
      if (firstSlashIndex == 4) {
        // 西元年
        int year = Integer.parseInt(sb.substring(0, 4)) - 1911;
        String result = String.valueOf(year) + sb.substring(4);
        if (result.length() == 6) {
          return "0" + result;
        } else {
          return result;
        }
      }
      return sb.toString();
    } else if (firstSlashIndex < 0) {
      return convertToChineseYear(s);
    }
    return s;
  }

  /**
   * 帶入Calendar，回傳該時間的民國年月
   * 
   * @param cal
   * @return 民國年月，i.e. 11011
   */
  public static int getChineseYm(Calendar cal) {
    return (cal.get(Calendar.YEAR) - 1911) * 100 + cal.get(Calendar.MONTH) + 1;
  }
  
  /**
   * 帶入Calendar，回傳該時間的民國年月
   * 
   * @param cal
   * @return 民國年月，i.e. 11011
   */
  public static int getChineseYmDate(Calendar cal) {
    return (cal.get(Calendar.YEAR) - 1911) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * 將民國年月轉成 Calendar object.
   * 
   * @param chineseYm
   * @return
   */
  /**
   * 將民國年月轉成 Calendar object.
   * 
   * @param chineseYm
   * @return
   */
  public static Calendar chineseYmToCalendar(String chineseYm) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, 1911 + Integer.parseInt(chineseYm.substring(0, 3)));
    cal.set(Calendar.MONTH, Integer.parseInt(chineseYm.substring(3)) - 1);
    return cal;
  }
  
  /**
   * 將民國年月轉成 Calendar object.
   * 
   * @param chineseYm yyymm
   * @return
   */
  public static Calendar chineseYmToCalendar2(String chineseYm) {
    Calendar cal = Calendar.getInstance();
    // cal.setTime(ct.getStartDate());
    int y = Integer.parseInt(chineseYm.substring(0, 3));
    int m = Integer.parseInt(chineseYm.substring(3));
    cal.set(Calendar.YEAR, 1911 + y);
    cal.set(Calendar.MONTH, m);
    return cal;
  }

  public static Date stringToDate(String s) {
    if (s == null) {
      return null;
    }
    SimpleDateFormat sdf = null;
    if (s.indexOf('/') > 0) {
      sdf = new SimpleDateFormat(SDF);
    } else if (s.indexOf('-') > 0) {
      sdf = new SimpleDateFormat("yyyy-MM-dd");
      if (s.indexOf('T') > 0) {
        s = s.substring(0, s.indexOf('T'));
      }
    }
    Date result = null;
    try {
      result = sdf.parse(s);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return result;
  }

  public static String convertExcelDateTimeToChinese(String s, boolean includeHourMinute) {
    if (s == null || s.length() < 7) {
      return null;
    }

    StringBuffer sb = new StringBuffer(s.substring(0, 4));
    int len = 0;
    boolean add12Hour = false;
    for (int i = 4; i < s.length(); i++) {
      if (s.charAt(i) == '/' || s.charAt(i) == '-' || s.charAt(i) == ' ' || s.charAt(i) == ':') {
        if (len == 1) {
          // 補0
          sb.insert(sb.length() - 1, '0');
        }
        len = 0;
        continue;
      } else if (s.charAt(i) == 'a' || s.charAt(i) == 'A' || s.charAt(i) == 'M'
          || s.charAt(i) == 'm') {
        continue;
      } else if (s.charAt(i) == 'p' || s.charAt(i) == 'P') {
        add12Hour = true;
        continue;
      }

      sb.append(s.charAt(i));
      len++;
    }
    if (sb.length() == 7) {
      // 補0
      sb.insert(sb.length() - 1, '0');
    }
    if (add12Hour && sb.length() >= 10) {
      int hour = Integer.parseInt(sb.substring(8, 10));
      hour += 12;
      sb.replace(8, 10, String.valueOf(hour));
    }

    SimpleDateFormat sdf = null;
    if (sb.length() == 8) {
      sdf = new SimpleDateFormat("yyyyMMdd");
    } else if (sb.length() == 12) {
      sdf = new SimpleDateFormat("yyyyMMddHHmm");
    } else if (sb.length() == 14) {
      sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    } else {
      System.out.println(
          "error convertExcelDateTimeToChinese() sb=" + sb.toString() + ", len=" + sb.length());
    }
    try {
      Date date = sdf.parse(sb.toString());
      SimpleDateFormat sdf2 = (includeHourMinute) ? new SimpleDateFormat("yyyyMMddHHmm")
          : new SimpleDateFormat("yyyyMMdd");
      String result = sdf2.format(date);
      int yearAD = Integer.parseInt(result.substring(0, 4));
      return String.valueOf(yearAD - 1911) + result.substring(4);
    } catch (ParseException e) {
      System.out.println("error date=" + sb.toString());
      e.printStackTrace();
    }
    return "N/A";
  }
  
  public static String getFirstDayOfMonthsAgo(String startDay, int month, boolean isStartDayOfMonth, String separator) {
    SimpleDateFormat sdfNoSeparator = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat sdfDash = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfSlash = new SimpleDateFormat(SDF);
    SimpleDateFormat sdf = null;
    if (startDay.length() == 8 || startDay.length() == 10) {
      if (startDay.indexOf('-') > 0) {
        sdf = sdfDash;
      } else if (startDay.indexOf('/') > 0) {
        sdf = sdfSlash;
      } else {
        sdf = sdfNoSeparator;
      }
    } else {
      return null;
    }
    try {
      Date startDate = sdf.parse(startDay);
      Calendar cal = Calendar.getInstance();
      cal.setTime(startDate);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      cal.add(Calendar.MONTH, month);
      cal.set(Calendar.DAY_OF_MONTH, 1);
      if (!isStartDayOfMonth) { 
        cal.add(Calendar.MONTH, 1);
      }
      if (separator == null) {
        return sdfNoSeparator.format(cal.getTime());
      } else if ("-".equals(separator)) {
        return sdfDash.format(cal.getTime());
      } else if ("/".equals(separator)) {
        return sdfSlash.format(cal.getTime());
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String formatElapsedTime(long totalSecs) {
	  
	  long hours = totalSecs / 3600;
	  long minutes = (totalSecs % 3600) / 60;
	  long seconds = totalSecs % 60;
	  
	  return String.format("%02d:%02d:%02d", hours, minutes, seconds);
  }
}
