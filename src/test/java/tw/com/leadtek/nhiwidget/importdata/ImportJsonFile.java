/**
 * Created on 2021/11/25.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tw.com.leadtek.nhiwidget.model.rdb.DEPARTMENT;
import tw.com.leadtek.nhiwidget.payload.MO;
import tw.com.leadtek.nhiwidget.payload.MRDetail;
import tw.com.leadtek.nhiwidget.payload.UserRequest;
import tw.com.leadtek.nhiwidget.security.jwt.JwtResponse;
import tw.com.leadtek.nhiwidget.security.jwt.LoginRequest;
import tw.com.leadtek.tools.SendHTTP;

public class ImportJsonFile {

  public final static String[] IGNORE = new String[] {"sStatus", "sReaded", "sNotify", "sChangeICD",
      "sChangeOther", "sChangeOrder", "sInfectious", "sNotify"};

  public ImportJsonFile() {

  }

  public boolean validateJsonFile(String filename) {
    try {
      BufferedReader br = new BufferedReader(
          new InputStreamReader(new FileInputStream(new File(filename)), "UTF-8"));
      String line = null;
      StringBuffer sb = new StringBuffer();
      while ((line = br.readLine()) != null) {
        boolean isIgnore = false;
        for (String string : IGNORE) {
          if (line.indexOf(string) > -1) {
            isIgnore = true;
            break;
          }
        }
        if (isIgnore) {
          continue;
        }
        if (line.indexOf('}') > -1 && sb.charAt(sb.length() - 3) == ',') {
          sb.deleteCharAt(sb.length() - 3);
        }
        sb.append(line);
        sb.append("\r\n");
      }
      br.close();

      String newFileName = filename.substring(0, filename.length() - 5) + "_new.json";
      try {
        BufferedWriter bw =
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFileName), "UTF-8"));
        bw.write(sb.toString());
        bw.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

      ObjectMapper objectMapper = new ObjectMapper();

      try {
        List<MRDetail> mrDetail =
            objectMapper.readValue(sb.toString(), new TypeReference<List<MRDetail>>() {});
        for (int i = 0; i < mrDetail.size(); i++) {
          if (mrDetail.get(i).getMos() == null || mrDetail.get(i).getMos().size() == 0) {

          }
        }
      } catch (JsonMappingException e) {
        e.printStackTrace();
        return false;
      } catch (JsonProcessingException e) {
        e.printStackTrace();
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static void validateJsonDir() {
    File dir = new File("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\202111");
    File[] files = dir.listFiles();
    for (File file : files) {
      if (file.getName().endsWith("_new.json")) {
        continue;
      }
      if (!file.getName().endsWith(".json")) {
        continue;
      }
      System.out.println(file.getName());
      if (!new ImportJsonFile().validateJsonFile(file.getAbsolutePath())) {
        break;
      }
    }
  }

  /**
   * 
   * @param filename
   * @param addMonth 往後加幾個月
   * @return
   */
  public boolean addMrdetailByJsonFile(String filename, int addMonth, String jwt) {
    try {
      BufferedReader br = new BufferedReader(
          new InputStreamReader(new FileInputStream(new File(filename)), "UTF-8"));
      String line = null;
      StringBuffer sb = new StringBuffer();
      while ((line = br.readLine()) != null) {
        boolean isIgnore = false;
        for (String string : IGNORE) {
          if (line.indexOf(string) > -1) {
            isIgnore = true;
            break;
          }
        }
        if (isIgnore) {
          continue;
        }
        if (line.indexOf('}') > -1 && sb.charAt(sb.length() - 3) == ',') {
          sb.deleteCharAt(sb.length() - 3);
        }
        sb.append(line);
        sb.append("\r\n");
      }
      br.close();

      ObjectMapper objectMapper = new ObjectMapper();

      try {
        List<MRDetail> mrDetail =
            objectMapper.readValue(sb.toString(), new TypeReference<List<MRDetail>>() {});
        System.out.println("mrDetail size=" + mrDetail.size());
        for (int i = 0; i < mrDetail.size(); i++) {
          MRDetail mrd = mrDetail.get(i);
          addMonth(mrd, addMonth);
          String response = postAPI("/nhixml/mrdetail", objectMapper.writeValueAsString(mrd), jwt);
          if (response.indexOf("Internal Server Error") > 0) {
            return false;
          }
        }
      } catch (JsonMappingException e) {
        e.printStackTrace();
        return false;
      } catch (JsonProcessingException e) {
        e.printStackTrace();
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public void addMonth(MRDetail mr, int addMonth) {
    mr.setMrDate(addMonth(mr.getMrDate(), addMonth));
    mr.setMrEndDate(addMonth(mr.getMrDate(), addMonth));
    if (mr.getMos() == null) {
      return;
    }
    for (int i = 0; i < mr.getMos().size(); i++) {
      MO mo = mr.getMos().get(i);
      mo.setStartTime(addMonth(mo.getStartTime(), addMonth));
      mo.setEndTime(addMonth(mo.getEndTime(), addMonth));
    }
  }

  public Date addMonth(Date date, int addMonth) {
    if (date == null || addMonth == 0) {
      return date;
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.MONTH, addMonth);
    return cal.getTime();
  }

  public String addMonth(String date, int addMonth) {
    if (date == null || addMonth == 0) {
      return date;
    }
    String[] ss = date.split(" ");
    String[] sss = ss[0].split("/");
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, 1911 + Integer.parseInt(sss[0]));
    cal.set(Calendar.MONTH, Integer.parseInt(sss[1]) - 1);
    cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(sss[2]) - 1);
    cal.add(Calendar.MONTH, addMonth);

    StringBuffer sb = new StringBuffer();
    sb.append(cal.get(Calendar.YEAR) - 1911);
    sb.append("/");
    DecimalFormat df = new DecimalFormat("00");
    sb.append(df.format(cal.get(Calendar.MONTH) + 1));
    sb.append("/");
    sb.append(df.format(cal.get(Calendar.DAY_OF_MONTH)));
    return sb.toString();
  }

  public String postAPI(String api, String requestBody, String jwt) {
    System.out.println("request=" + requestBody);
    SendHTTP send = new SendHTTP();
    send.setServerIP("10.10.5.23");
    send.setPort("8081");
    String response = send.postAPI(jwt, api, requestBody);
    System.out.println(response);
    return response;
  }

  public static String signin(String serverIP, String port) {
    // /auth/login
    SendHTTP send = new SendHTTP();
    send.setServerIP(serverIP);
    send.setPort(port);
    LoginRequest lr = new LoginRequest();
    lr.setPassword("leadtek");
    lr.setUsername("kenlai");

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    String response = null;
    try {
      requestBody = objectMapper.writeValueAsString(lr);
      response = send.postAPI(null, "/auth/login", requestBody);
      JwtResponse jwt = objectMapper.readValue(response, JwtResponse.class);
      //System.out.println(response);
      return jwt.getToken();
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void addMrDetail() {
    String jwt = signin("10.10.5.30", "8081");
    if (jwt == null) {
      System.out.println("token is null");
      return;
    }
    File dir = new File("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\202111");
    // File dir = new File("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\202111-OP");
    
    File[] files = dir.listFiles();
    for (File file : files) {
      // File file = files[0];
      if (file.getName().endsWith("_new.json")) {
        continue;
      }
      if (!file.getName().endsWith(".json")) {
        continue;
      }
      System.out.println(file.getName());
      if (!new ImportJsonFile().addMrdetailByJsonFile(file.getAbsolutePath(), 2, jwt)) {
        break;
      }
    }
  }

  public void addDepartmentFile() {
    String filename = "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\醫療展\\department.txt";
    String jwt = signin("10.10.5.30", "8081");
    if (jwt == null) {
      System.out.println("token is null");
      return;
    }
    try {
      BufferedReader br = new BufferedReader(
          new InputStreamReader(new FileInputStream(new File(filename)), "UTF-8"));
      String line = null;

      ObjectMapper objectMapper = new ObjectMapper();

      while ((line = br.readLine()) != null) {
        String[] ss = line.split(",");
        DEPARTMENT department = new DEPARTMENT();
        department.setName(ss[0]);
        department.setNhName(ss[0]);
        department.setNhCode(ss[1]);
        department.setStatus(1);
        department.setCode(ss[1]);
        department.setNote("醫療展demo用");
        String response = postAPI("/department", jwt, objectMapper.writeValueAsString(department));
        if (response.indexOf(":500") > 0) {
          break;
        }
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void addUserFile() {
    // String filename = "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\藥師.txt";
    String filename = "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\醫療展\\user.txt";
    String jwt = signin("10.10.5.30", "8081");
    if (jwt == null) {
      System.out.println("token is null");
      return;
    }
    try {
      BufferedReader br = new BufferedReader(
          new InputStreamReader(new FileInputStream(new File(filename)), "UTF-8"));
      String line = null;

      ObjectMapper objectMapper = new ObjectMapper();

      while ((line = br.readLine()) != null) {
        String[] ss = line.split(",");
        UserRequest user = new UserRequest();
        user.setDepartments(ss[3]);
        user.setPassword("leadtek");
        user.setRocId(ss[0]);
        user.setDisplayName(ss[1]);
        user.setUsername(ss[0]);
        user.setRole(ss[2]);
        user.setStatus(1);
        String response = postAPI("/auth/user", jwt, objectMapper.writeValueAsString(user));
        if (response.indexOf(":500") > 0) {
          break;
        }
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    ImportJsonFile ijf = new ImportJsonFile();
    //System.out.println(ijf.signin());

    // new ImportJsonFile().readFile(
    // "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\202111\\住院-心臟血管內科50筆_11月.json");
    // ImportJsonFile.validateJsonDir();

    //ImportJsonFile.addMrDetail();
    String values = "1641520372446,1,2,3,4";
    String[] ss = values.split(",");
    for(int i=0;i<ss.length;i++) {
      System.out.println("i=" + i + ",value=" + ss[i]);
    }

    // new ImportJsonFile().addDepartmentFile();
    // new ImportJsonFile().addUserFile();
    // String a =
    // ",00156A,18001C,AC373441G0,AC376011G0,BC25350100,BC21914100,BC030771G0,05209A,BC25350100,";
    // String b = "BC25350100";
    // System.out.println(IntelligentService.countStringAppear(a, b));
  }

}
