/**
 * Created on 2021/5/14.
 */
package tw.com.leadtek.tools;

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


}
