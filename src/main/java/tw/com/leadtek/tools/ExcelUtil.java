/**
 * Created on 2020/10/13.
 */
package tw.com.leadtek.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.text.DecimalFormat;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import tw.com.leadtek.nhiwidget.dao.FILE_DOWNLOADDao;
import tw.com.leadtek.nhiwidget.model.rdb.FILE_DOWNLOAD;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.service.SystemService;

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
   * 
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
  
  public static String getCellStringValue(HSSFCell cell) {
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

  public static HashMap<Integer, String> readTitleRow(XSSFRow row) {
    HashMap<Integer, String> result = new HashMap<Integer, String>();
    for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
      if (row.getCell(i) == null) {
        continue;
      }
      String cellValue = row.getCell(i).getStringCellValue();
      if (cellValue != null && cellValue.length() > 1) {
        if (cellValue.indexOf(',') > -1) {
          cellValue = cellValue.split(",")[0];
        }
        result.put(Integer.valueOf(i), cellValue);
      }
    }
    return result;
  }

  public static HashMap<Integer, String> readTitleRow(HSSFRow row) {
    HashMap<Integer, String> result = new HashMap<Integer, String>();
    for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
      if (row.getCell(i) == null) {
        continue;
      }
      String cellValue = row.getCell(i).getStringCellValue();
      if (cellValue != null && cellValue.length() > 1) {
        if (cellValue.indexOf(',') > -1) {
          cellValue = cellValue.split(",")[0];
        }
        result.put(Integer.valueOf(i), cellValue);
      }
    }
    return result;
  }
  
  public static HashMap<Integer, String> readTitleRowHSSFRow(HSSFRow row,
      List<PARAMETERS> parameterList) {
    HashMap<Integer, String> result = new HashMap<Integer, String>();
    for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
      if (row.getCell(i) == null) {
        continue;
      }
      String cellValue = row.getCell(i).getStringCellValue();
      if (cellValue != null && cellValue.length() > 1) {
        if (cellValue.indexOf(',') > -1) {
          cellValue = cellValue.split(",")[0];
        }
        if (cellValue.indexOf(' ') > -1) {
          cellValue = removeSpace(cellValue);
        }
        if (parameterList != null && parameterList.size() > 0) {
          boolean isFound = false;
          for (int j = 0; j < parameterList.size(); j++) {
            if (parameterList.get(j).getValue() == null) {
              continue;
            }
            if (cellValue.equals(parameterList.get(j).getValue().trim())) {
              result.put(Integer.valueOf(i), parameterList.get(j).getName());
              isFound = true;
              break;
            }
          }
          if (!isFound) {
            result.put(Integer.valueOf(i), cellValue);
          }
        } else {
          result.put(Integer.valueOf(i), cellValue);
        }
      }
    }
    return result;
  }
  
  public static HashMap<Integer, String> readTitleRow(Row row) {
	    HashMap<Integer, String> result = new HashMap<Integer, String>();
	    for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
	      if (row.getCell(i) == null) {
	        continue;
	      }
	      String cellValue = row.getCell(i).getStringCellValue();
	      if (cellValue != null && cellValue.length() > 1) {
	        if (cellValue.indexOf(',') > -1) {
	          cellValue = cellValue.split(",")[0];
	        }
	        result.put(Integer.valueOf(i), cellValue);
	      }
	    }
	    return result;
  }
  
  public static HashMap<String, String> readCellValue(HashMap<Integer, String> columnMap,
	      Row row) {
	    HashMap<String, String> result = new HashMap<String, String>();
	    // for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
	    for (int i = 0; i < 100; i++) {
	      String cellValue = null;
	      if (row.getCell(i) == null) {
	        continue;
	      }
	      if (row.getCell(i).getCellType() == CellType.NUMERIC) {
	        cellValue = String.valueOf(row.getCell(i).getNumericCellValue());
	        // System.out.println("cell numeric before:" + cellValue);
	        if (cellValue.endsWith(".0")) {
	          cellValue = cellValue.substring(0, cellValue.length() - 2);
	        }
	        // System.out.println("cell numeric after:" + cellValue);
	      } else {
	        cellValue = row.getCell(i).getStringCellValue().trim();
	      }
	      if (cellValue != null && cellValue.length() > 0) {
	        if (columnMap.get(Integer.valueOf(i)) != null) {
	          result.put(columnMap.get(Integer.valueOf(i)), cellValue);
	        }
	      }
	    }
	    return result;
	  }

  public static HashMap<String, String> readCellValue(HashMap<Integer, String> columnMap,
      XSSFRow row) {
    HashMap<String, String> result = new HashMap<String, String>();
    // for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
    for (int i = 0; i < 130; i++) {
      String cellValue = null;
      if (row.getCell(i) == null) {
        continue;
      }
      if (row.getCell(i).getCellType() == CellType.NUMERIC) {
        cellValue = String.valueOf(row.getCell(i).getNumericCellValue());
        // System.out.println("cell numeric before:" + cellValue);
        if (cellValue.endsWith(".0")) {
          cellValue = cellValue.substring(0, cellValue.length() - 2);
        }
        // System.out.println("cell numeric after:" + cellValue);
      } else {
        cellValue = row.getCell(i).getStringCellValue().trim();
      }
      if (cellValue != null && cellValue.length() > 0) {
        String columnName = columnMap.get(Integer.valueOf(i));
        if (columnName != null) {
          if (columnName.indexOf(',') > 0) {
            String[] columnNames = columnName.split(",");
            for (String string : columnNames) {
              result.put(string, cellValue);
            }
          } else {
            result.put(columnName, cellValue);
          }
        }
      }
    }
    return result;
  }
  
  public static HashMap<String, String> readCellValue(HashMap<Integer, String> columnMap,
      XSSFRow row, DecimalFormat df) {
    HashMap<String, String> result = new HashMap<String, String>();
    // for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
    for (int i = 0; i < 100; i++) {
      String cellValue = null;
      if (row.getCell(i) == null) {
        continue;
      }
      if (row.getCell(i).getCellType() == CellType.NUMERIC) {
        if (DateUtil.isCellDateFormatted(row.getCell(i)) || row.getCell(i).getCellStyle().getDataFormatString().indexOf("yy") > -1
            || row.getCell(i).getCellStyle().getDataFormatString().indexOf("/") > 0) {
          cellValue = ExcelUtil.SDF_DATETIME.format(row.getCell(i).getDateCellValue());
        } else {
          cellValue = String.valueOf(df.format(row.getCell(i).getNumericCellValue()));
          if ("44743".equals(cellValue)) {
            System.out.println(cellValue + " format:" + DateUtil.isCellDateFormatted(row.getCell(i)) + "," + row.getCell(i).getCellStyle().getDataFormatString());
          }
        }
        // System.out.println("cell numeric before:" + cellValue);
        if (cellValue.endsWith(".0")) {
          cellValue = cellValue.substring(0, cellValue.length() - 2);
        }
        // System.out.println("cell numeric after:" + cellValue);
      } else if (row.getCell(i).getCellType() == CellType.FORMULA) {
        cellValue = row.getCell(i).getCellFormula();
        if (cellValue.startsWith("-")) {
          cellValue = cellValue.substring(1).trim();
        }
      } else {
        cellValue = row.getCell(i).getStringCellValue().trim();
      }
      if (cellValue != null && cellValue.length() > 0) {
        if (columnMap.get(Integer.valueOf(i)) != null) {
          result.put(columnMap.get(Integer.valueOf(i)), cellValue);
        }
      }
    }
    return result;
  }

  /**
   * 讀取 excel一列的值，依據 columnMap 的位置與欄位名稱對應關係，放至對應欄位名稱/值的 map
   * @param columnMap
   * @param row
   * @return
   */
  public static HashMap<String, String> readCellValue(HashMap<Integer, String> columnMap,
      HSSFRow row) {
    HashMap<String, String> result = new HashMap<String, String>();
    for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
      String cellValue = null;
      if (row.getCell(i) == null) {
        continue;
      }
      if (row.getCell(i).getCellType() == CellType.NUMERIC) {
        if (DateUtil.isCellDateFormatted(row.getCell(i))
            || row.getCell(i).getCellStyle().getDataFormatString().indexOf("yy") > -1) {
          cellValue = ExcelUtil.SDF_DATETIME.format(row.getCell(i).getDateCellValue());
        } else {
          cellValue = String.valueOf(row.getCell(i).getNumericCellValue());
          // System.out.println("cell numeric before:" + cellValue);
        }
        if (cellValue.endsWith(".0")) {
          cellValue = cellValue.substring(0, cellValue.length() - 2);
        }
      } else {
        cellValue = row.getCell(i).getStringCellValue().trim();
      }
      if (cellValue != null && cellValue.length() > 0 && columnMap.get(Integer.valueOf(i)) != null) {
        result.put(columnMap.get(Integer.valueOf(i)), cellValue);
      }
    }
    return result;
  }
  
  /**
   * 取得標題欄位名稱對應的位置
   * @param row
   * @return
   */
  public static HashMap<Integer, String> readTitleRow(XSSFRow row, List<PARAMETERS> parameterList) {
    HashMap<Integer, String> result = new HashMap<Integer, String>();
    for (int i = 0; i < 130; i++) {
      if (row.getCell(i) == null) {
        continue;
      }
      String cellValue = getCellStringValue(row.getCell(i));
      if (cellValue != null && cellValue.length() > 1) {
        if (cellValue.indexOf(',') > -1) {
          cellValue = cellValue.split(",")[0];
        }
        if (cellValue.indexOf(' ') > -1) {
          cellValue = removeSpace(cellValue);
        }
        if (parameterList != null && parameterList.size() > 0) {
          boolean isFound = false;
          for(int j=0; j<parameterList.size(); j++) {
            if (parameterList.get(j).getValue() == null) {
              continue;
            }
            if (cellValue.equals(parameterList.get(j).getValue().trim())) {
              String value = result.get(Integer.valueOf(i));
              if (value == null) {
                result.put(Integer.valueOf(i), parameterList.get(j).getName());
              } else {
                result.put(Integer.valueOf(i), value + "," + parameterList.get(j).getName());
              }
              isFound = true;
            }
          }
          if (!isFound) {
            result.put(Integer.valueOf(i), cellValue);
          }
        } else {
          result.put(Integer.valueOf(i), cellValue);
        }
      }
    }
    return result;
  }
  
  /**
   * 取得標題欄位名稱對應的位置
   * @param row
   * @return
   */
  public static HashMap<Integer, String> readTitleRow(HSSFRow row, List<PARAMETERS> parameterList) {
    HashMap<Integer, String> result = new HashMap<Integer, String>();
    for (int i = 0; i < 130; i++) {
      if (row.getCell(i) == null) {
        continue;
      }
      String cellValue = getCellStringValue(row.getCell(i));
      if (cellValue != null && cellValue.length() > 1) {
        if (cellValue.indexOf(',') > -1) {
          cellValue = cellValue.split(",")[0];
        }
        if (cellValue.indexOf(' ') > -1) {
          cellValue = removeSpace(cellValue);
        }
        if (parameterList != null && parameterList.size() > 0) {
          boolean isFound = false;
          for(int j=0; j<parameterList.size(); j++) {
            if (parameterList.get(j).getValue() == null) {
              continue;
            }
            if (cellValue.equals(parameterList.get(j).getValue().trim())) {
              result.put(Integer.valueOf(i), parameterList.get(j).getName());
              isFound = true;
              break;
            }
          }
          if (!isFound) {
            result.put(Integer.valueOf(i), cellValue);
          }
        } else {
          result.put(Integer.valueOf(i), cellValue);
        }
      }
    }
    return result;
  }
  
  public static String removeSpace(String s) {
    StringBuffer sb = new StringBuffer();
    for(int i=0;i<s.length();i++) {
      if (s.charAt(i) !=  ' ') {
        sb.append(s.charAt(i));
      }
    }
    return sb.toString();
  }
  

  public static String toCSV(List<Map<String, Object>> list) {
	    List<String> headers = list.stream().flatMap(map -> map.keySet().stream()).distinct().collect(Collectors.toList());
	    final StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < headers.size(); i++) {
	        sb.append(headers.get(i));
	        sb.append(i == headers.size()-1 ? "\n" : ",");
	    }
	    for (Map<String, Object> map : list) {
	        for (int i = 0; i < headers.size(); i++) {
	            sb.append(map.get(headers.get(i)));
	            sb.append(i == headers.size()-1 ? "\n" : ",");
	        }
	    }
	    return sb.toString();
	}
  
  public static String createCSV(List<LinkedHashMap<String, Object>> list, String filePath) throws IOException{
	    List<String> headers = list.stream().flatMap(map -> map.keySet().stream()).distinct().collect(Collectors.toList());
	    System.out.println(headers);
	    try(FileWriter writer= new FileWriter(filePath, true);){
	           for (String string : headers) {
	                 writer.write(string);
	                 writer.write(",");
	           }
	           writer.write("\r\n");

	           for (LinkedHashMap<String, Object> lmap : list) {
	                 for (Entry<String, Object> string2 : lmap.entrySet()) {
	                        writer.write(string2.getValue() == null ? "" : string2.getValue().toString());
	                        writer.write(",");
	                 }
	                 writer.write("\r\n");
	           }
	    }catch (Exception e) {
	        e.printStackTrace();
	    }
	    return filePath;
	}
   /// 處理產生csv並且將進度寫入file_download表
   public static String createCSV(List<LinkedHashMap<String, Object>> list, String filePath, FILE_DOWNLOAD download, FILE_DOWNLOADDao fdDao) throws IOException{
	    List<String> headers = list.stream().flatMap(map -> map.keySet().stream()).distinct().collect(Collectors.toList());
	    System.out.println(headers);
	    int i = 0;
	    double next = 0.1;
	    try{
	    	File file = new File(filePath);
	    	Writer write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
	           for (String string : headers) {
	        	   write.write(string);
	        	   write.write(",");
	           }
	           write.write("\r\n");

	           for (LinkedHashMap<String, Object> lmap : list) {
	                 for (Entry<String, Object> string2 : lmap.entrySet()) {
	                	 String keyName = string2.getKey();
	                	 String value = string2.getValue() == null ? "" : string2.getValue().toString();
	                	 /// 因為此二欄位值都會有換行導致csv欄位顯示錯亂，這邊獨立處理
	                	 if(keyName.contains("SUBJECT") || keyName.contains("OBJECT") ) {
	                		 if(!value.isEmpty()) {
	                			 String thisLine = null;
	                			 String append = "";
	                			 Reader targetReader = new StringReader(value);
	                			 BufferedReader br = new BufferedReader(targetReader);
	                			 while ((thisLine = br.readLine()) != null) {
	                				 append += thisLine;
	                		     } 
	                			 /// 將逗點改為中文輸入逗點，確保csv不會換到下一欄位
	                			 String finalVal = append.replace(",", "，");
	                			 write.write(finalVal);
	                		 }
	                	 }
	                	 else {
	                		 
	                		 write.write(value);
	                	 }
	                	 write.write(",");
	                 }
	                 write.write("\r\n");
	                 if (((double) i / (double) list.size()) > next) {
	                     download.setProgress((int) (next * 100));
	                     download.setUpdateAt(new Date());
	                     fdDao.save(download);
	                     next += 5;
	                   }
	                 i++;
	           }
	           SystemService.updateFileDownloadFinished(download, fdDao);
	           write.flush();
	           write.close();
	    }catch (Exception e) {
	        e.printStackTrace();
	    }
	    return filePath;
	}

}


