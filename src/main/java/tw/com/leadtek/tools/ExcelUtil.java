/**
 * Created on 2020/10/13.
 */
package tw.com.leadtek.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.MinguoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DecimalStyle;
import java.util.Date;
import java.util.Locale;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import tw.com.leadtek.nhiwidget.constant.DATA_TYPE;

public class ExcelUtil {

  public final static String DATA_FORMAT_TIME24H = "HH:mm:ss";

  public final static SimpleDateFormat SDF_TIME24H = new SimpleDateFormat(DATA_FORMAT_TIME24H);

  public final static String DATA_FORMAT_DATETIME = "yyyy/MM/dd HH:mm:ss";

  public final static SimpleDateFormat SDF_DATETIME = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

  public final static String DATA_FORMAT_DATE = "yyyy/MM/dd";

  public final static SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy/MM/dd");
   
  public final static String EXCEL_DATE_FORMAT_14 = "yyyy/M/d";

  public final static String MINGUO = "(民國年)";
  
  public static Date getDate(String dataFormat, String cellValue, Logger logger) {
    if (dataFormat.indexOf(ExcelUtil.MINGUO) > -1) {
      String newDatePattern = dataFormat.replace(ExcelUtil.MINGUO, "");
      Chronology chrono = MinguoChronology.INSTANCE;
      try {
        DateTimeFormatter df = new DateTimeFormatterBuilder().parseLenient()
            .appendPattern(newDatePattern).toFormatter().withChronology(chrono)
            .withDecimalStyle(DecimalStyle.of(Locale.getDefault()));
        ChronoLocalDate d1 = chrono.date(df.parse(cellValue));
        return Date.from(LocalDate.from(d1).atStartOfDay(ZoneId.systemDefault()).toInstant());
      } catch (Exception e) {
        logger.error("getDate:" + dataFormat + "," + cellValue, e);
      }
    } else {
      SimpleDateFormatExcel sdf = new SimpleDateFormatExcel(dataFormat);
      try {
        return sdf.parse(cellValue);
      } catch (ParseException e) {
        logger.error("convertStringToDate:" + dataFormat + "," + cellValue, e);
      }
    }
    return null;
  }
  
  /**
   * 從字串找出時間格式，若非時間則回傳空字串
   * 
   * @param s
   * @return
   */
  public String getDateTimeFormat(String s, Logger logger) {
    String[] ss = s.split(" ");
    if (ss.length < 1) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    if (ss.length == 2) {
      // date + time
      String date = getDateFormat(ss[0]);
      if (date != null) {
        String time = getTimeFormat(ss[1]);
        if (time != null) {
          sb.append(date);
          sb.append(" ");
          sb.append(time);
        }
      } else {
        date = getDateFormat(ss[1]);
        if (date != null) {
          String time = getTimeFormat(ss[0]);
          if (time != null) {
            sb.append(time);
            sb.append(" ");
            sb.append(date);
          }
        }
      }
    } else if (ss.length == 3) {
      // date + 上午 + time, ex:2020/11/3 下午 05:44
      String result = getAMPMTimeFormat(ss, "午", logger);
      logger.info("午:" + result);
      if (result == null || result.length() == 0) {
        // AM/PM
        result = getAMPMTimeFormat(ss, "M", logger);
        logger.info("M:" + result);
      }
      if (result == null || result.length() == 0) {
        // am/pm
        result = getAMPMTimeFormat(ss, "m", logger);
        logger.info("m:" + result);
      }
      return result;
    } else {
      String date = getDateFormat(ss[0]);
      if (date != null) {
        sb.append(date);
      } else {
        String time = getTimeFormat(ss[0]);
        if (time != null) {
          sb.append(time);
        }
      }
    }
    return sb.toString();
  }

  public String getAMPMTimeFormat(String[] ss, String character, Logger logger) {
    StringBuffer sb = new StringBuffer();
    String date = getDateFormat(ss[0]);
    logger.info("getAMPMTimeFormat date=" + date);
    if (date != null) {
      sb.append(date);
      sb.append(" ");
      if (ss[1].indexOf(character) > 0) {
        // am 代表 new SimpleDateFormat 時要加 Locale.ENGLISH
        sb.append(character.equals("午") ? "a" : "AM/PM");
        sb.append(" ");
        String time = getTimeFormat(ss[2]);
        logger.info("getAMPMTimeFormat time=" + time);
        if (time != null) {
          String timeh = time.replaceAll("H", "h");
          sb.append(timeh);
          return sb.toString();
        }
      }
      return "";
    }
    if (ss[0].indexOf(character) > 0) {
      // 下午 05:44 2020/11/3
      sb.append(character.equals("午") ? "a" : "AM/PM");
      sb.append(" ");
      String time = getTimeFormat(ss[0]);
      if (time != null) {
        String timeh = time.replaceAll("H", "h");
        sb.append(timeh);
        sb.append(" ");
        date = getDateFormat(ss[1]);
        if (date != null) {
          sb.append(date);
          return sb.toString();
        }
      }
      return "";
    }
    return "";
  }

  public String getDateFormat(String s) {
    if (s.indexOf("://") > -1) {
      // http url
      return null;
    }
    String date = checkDateQuote(s, "/");
    if (date != null && date.length() > 0) {
      return date;
    }
    date = checkDateQuote(s, "-");
    if (date != null && date.length() > 0) {
      return date;
    }
    return null;
  }
  
  /**
   * 檢查是否為日期格式，若非日期，回傳 null
   * @param s
   * @param quote
   * @return
   */
  public static String checkDateQuote(String s, String quote) {
    StringBuffer sb = new StringBuffer();
    // 月日長度為 2
    int mdLen = 2;
    if (s.indexOf(quote) > -1) {
      String[] ss = s.split(quote);
      if (ss.length == 3) {
        int year = 0;
        try {
          year = Integer.parseInt(ss[0]);
        } catch (NumberFormatException e) {
          // 非數字
          return null;
        }
        // 日期
        if (year < 200) {
          // 民國年
          sb.append("yyy");
        } else if (year > 1900) {
          // 西元年
          sb.append("yyyy");
        }
        sb.append(quote);

        if (ss[1].length() == 2) {
          sb.append("MM");
        } else if (ss[1].length() == 1) {
          mdLen = 1;
          sb.append("M");
        }
        sb.append(quote);
        if (ss[2].length() == 2) {
          if (mdLen == 1) {
            sb.append("d");
          } else {
            sb.append("dd");
          }
        } else if (ss[2].length() == 1) {
          if (sb.indexOf("MM") > 0) {
            sb.deleteCharAt(sb.length() - 2);
          }
          sb.append("d");
        }
      }
    }
    return sb.toString();
  }
  
  public String getTimeFormat(String s) {
    StringBuffer sb = new StringBuffer();
    String[] ss = s.split(":");
    if (ss.length == 3) {
      if (ss[0].length() == 2) {
        sb.append("HH");
      } else if (ss[0].length() == 1) {
        sb.append("H");
      }
      sb.append(":");
      if (ss[1].length() == 2) {
        sb.append("mm");
      } else if (ss[1].length() == 1) {
        sb.append("m");
      }
      sb.append(":");
      if (ss[2].length() == 2) {
        sb.append("ss");
      } else if (ss[0].length() == 1) {
        sb.append("s");
      }
    } else if (ss.length == 2) {
      if (ss[0].length() == 2) {
        sb.append("HH");
      } else if (ss[0].length() == 1) {
        sb.append("H");
      }
      sb.append(":");
      if (ss[1].length() == 2) {
        sb.append("mm");
      } else if (ss[1].length() == 1) {
        sb.append("m");
      }
    }
    return sb.toString();
  }
  
  public static String getCellStringValue(XSSFCell cell) {
    if (cell.getCellType() == CellType.NUMERIC) {
      String number = String.valueOf(cell.getNumericCellValue());
      if (number.endsWith(".0")) {
        return number.substring(0, number.length() - 2);
      }
      return number;
    } else {
      return cell.getStringCellValue().trim();
    }
  }

}