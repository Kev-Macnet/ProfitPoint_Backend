/**
 * Created on 2021/5/14.
 */
package tw.com.leadtek.tools;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.boot.json.BasicJsonParser;

public class Utility {

  public static int getTotalPage(int total, int perPage) {
    int result = total / perPage;
    if (total % perPage > 0) {
      result++;
    }
    return result;
  }
  
  public static java.util.Map<String, Object> jwtValidate(String jwt) {
      int status;
      java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
      if (jwt.length()>20) {
          jwt = jwt.replace("Bearer", "");
          String arrJwt[] = jwt.split("\\.");
          if (arrJwt.length==3) {
              String jwtBody = "";
              byte[] jwtBytes = java.util.Base64.getDecoder().decode(arrJwt[1]);
              try {
                  jwtBody = new String(jwtBytes, "UTF-8");
              } catch (java.io.UnsupportedEncodingException ex) {
                  jwtBody = new String(jwtBytes);
              }
              
              BasicJsonParser linkJsonParser = new BasicJsonParser();
              try {
                  Map<String, Object> jwtMap = linkJsonParser.parseMap(jwtBody);
                  long exp = (long) jwtMap.get("exp")*1000;
                  //System.out.println("exp="+exp+" / " + new java.util.Date().getTime());
                  if (new java.util.Date().getTime()<exp) {
                      retMap.put("status", 200);
                      retMap.put("userName", jwtMap.get("sub").toString());
                      status = 0;
                  } else {
                      status = -2;
                  }
              } catch (Exception e) {
                  status = -1;
              }
          } else {
              status = -1;
          }
      } else {
          status = -1;
      }
      
      if (status == -1) {
          retMap.put("status", 401);
          retMap.put("message", "token 格式有誤!");
      } else if (status == -2) {
          retMap.put("status", 401);
          retMap.put("message", "token 過期!");
      } else {
          //
      }
      return retMap;
    }
  
  public static String dateFormat(java.util.Date date, String format) {
      if (format.length() == 0)
          format = "yyyy/MM/dd HH:mm:ss";
      if (date == null) {
          return ("");
      } else {
          java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
          return (sdf.format(date));
      }
  }
  
  public static java.util.Date strToDate(String paStr, String format) {
      if (format.length() == 0)
          format = "yyyy/MM/dd";
      java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
      java.util.Date date;

      try {
          date = sdf.parse(paStr);
          return (date);
      } catch (Exception e) {
          return (null);
      }
  }
  
  public static java.util.Date detectDate(String str) {
      java.util.Date enddate = null;
      if (str != null) {
          if ((str.length()==13)||(str.length()==12)||(str.length()==11)) { // 1591113599000
              enddate = new java.util.Date(Long.parseLong(str));
          } else if (str.length()==10) {
              if (str.indexOf("/")>0) {
                  enddate = strToDate(str, "yyyy/MM/dd");
              } else if (str.indexOf("-")>0) {
                  enddate = strToDate(str, "yyyy-MM-dd");
              }
              
          } else if (str.length()==16) {
              if (str.indexOf("/")>0) {
                  enddate = strToDate(str, "yyyy/MM/dd HH:mm");
              } else if (str.indexOf("-")>0) {
                  enddate = strToDate(str, "yyyy-MM-dd HH:mm");
              }
          } else if (str.length()==19) {
              if (str.indexOf("/")>0) {
                  enddate = strToDate(str, "yyyy/MM/dd HH:mm:ss");
              } else if (str.indexOf("-")>0) {
                  enddate = strToDate(str, "yyyy-MM-dd HH:mm:ss");
              }
          }
      }
      return (enddate);
  }
  
  public static java.util.List<Map<String,Object>> listLowerCase(java.util.List<Map<String,Object>> sourceList) {
      java.util.List<Map<String,Object>> retList = new java.util.LinkedList<Map<String,Object>>();
      for (Map<String, Object> item : sourceList) {
          retList.add(mapLowerCase(item));
      }
      return retList;
  }
  
  public static java.util.Map<String,Object> mapLowerCase(java.util.Map<String,Object> sourceMap) {
      java.util.Map<String, Object> retMap = new java.util.LinkedHashMap<String, Object>();
      for (java.util.Map.Entry<String, Object> entry : sourceMap.entrySet()) {
          String key = entry.getKey();
          key = key.toLowerCase();
          if (entry.getValue()!=null) {
              String sName = "";
              sName = entry.getValue().getClass().getSimpleName();
              if (sName.equals("Timestamp")) {
                  java.sql.Timestamp tm = (java.sql.Timestamp)entry.getValue();
                  retMap.put(key, tm.getTime());
              } else if (sName.equals("Date")) {
                  java.sql.Date tm = (java.sql.Date)entry.getValue();
                  retMap.put(key, tm.getTime());
              }  else {
                  retMap.put(key, entry.getValue());
              }
          } else {
              retMap.put(key,  null);
          }
      } 
      return (retMap);
  }
  
  public static String getMapStr(java.util.Map<String, Object> map, String key) {
      String ret = null;
      if (map.get(key)!=null) {
          ret =  map.get(key).toString();
      }
      return (ret);
  }
  
  public static int getMapInt(java.util.Map<String, Object> map, String key) {
      int ret = 0;
      if (map.get(key)!=null) {
          try {
              ret = Integer.parseInt(map.get(key).toString());
          } catch (Exception e) {
              //
          }
      }
      return (ret);
  }
  
  public static void sleep(int msec) {
      try   {
          Thread.sleep(msec);
      }
      catch(InterruptedException ex)  {
          Thread.currentThread().interrupt();
      }
  }

  public static String quotedNotNull(String str) {
      if (str==null) {
          return "NULL";
      } else {
          
          return "\'"+str.replaceAll("\'", "\'\'")+"\'";
      }
  }
  
  /*
  public static int saveListString(String fileName, java.util.List<String> lstString) {
      try {
          java.io.FileWriter writer = new java.io.FileWriter(fileName);
          for(String str: lstString) {
              writer.write(str);
//              writer.append(str);
          }
          writer.flush();
          writer.close();
      } catch(java.io.IOException e) {
          e.printStackTrace();
      }
      return lstString.size();
  }
  */
  
  public static boolean deleteFile(String fName) {
      java.io.File f = new java.io.File(fName);
      if (f.exists()) {
          f.delete();
          return (true);
      } else {
          return (false);
      } 
  }
  
  public static boolean saveToFile(String fileName, java.util.List<String> lstData, boolean append) {
      try {
          java.io.File fp = new java.io.File(fileName);
//        java.io.BufferedWriter bwr = new java.io.BufferedWriter(new java.io.FileWriter(fp, append));
          java.io.BufferedWriter bwr = new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(fp, append), StandardCharsets.UTF_8));
           for (String str : lstData) {
               bwr.write(str+"\r\n");
           }
           bwr.flush();
           bwr.close();
           return true;
      } catch (java.io.IOException e) {
          e.printStackTrace();
          return false;
      }
  }
  

  public static java.util.List<String> loadFromFile(String fileName, String cartset) {
      if (cartset.length()==0) {
          cartset = "UTF-8"; 
      }
    java.util.List<String> buffer = new java.util.LinkedList<String>();
    try {
        java.io.FileInputStream fis = new java.io.FileInputStream(fileName);
        java.io.BufferedReader isReader = new java.io.BufferedReader(new java.io.InputStreamReader(fis, cartset));
        String str;
        while((str = isReader.readLine()) != null) {
            buffer.add(str);
        }
        isReader.close();
        fis.close();
    } catch(java.io.IOException e) {
        e.printStackTrace();
    }
    return buffer;
  }
  
  public static int dayOfMonth(java.util.Date da) {
      java.util.Calendar calendar=java.util.Calendar.getInstance();
      calendar.setTime(da);
      int date=calendar.get(java.util.Calendar.DAY_OF_MONTH);
      return (date);
  }
  
  public static int dayOfWeek(java.util.Date da) {
      java.util.Calendar calendar=java.util.Calendar.getInstance();
      calendar.setTime(da);
      int week=calendar.get(java.util.Calendar.DAY_OF_WEEK);
      //System.out.println("week="+MiscLib.dateFormatStr(da, "yyyy/MM/dd")+" @ "+week);
      return (week);
  }

}
