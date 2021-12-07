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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tw.com.leadtek.nhiwidget.model.rdb.DEPARTMENT;
import tw.com.leadtek.nhiwidget.payload.MRDetail;
import tw.com.leadtek.nhiwidget.payload.UserRequest;
import tw.com.leadtek.nhiwidget.service.IntelligentService;
import tw.com.leadtek.tools.DateTool;
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
      if(!new ImportJsonFile().validateJsonFile(
          file.getAbsolutePath())) {
        break;
      }
    }
  }
  
  public boolean addMrdetailByJsonFile(String filename) {
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
          String response = postAPI("/nhixml/mrdetail", objectMapper.writeValueAsString(mrDetail.get(i)));
//          if (response.indexOf(":500,") > 0) {
//            return false;
//          }
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
  
  public String postAPI(String api, String requestBody) {
    System.out.println("request=" + requestBody);
    SendHTTP send = new SendHTTP();
    send.setServerIP("10.10.5.23");
    send.setPort("8081");
    String response = send.postAPI(null, api, requestBody);
    System.out.println(response);
    return response;
  }
  
  public static void addMrDetail() {
    File dir = new File("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\202111");
    //File dir = new File("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\202111-OP");
    File[] files = dir.listFiles();
    for (File file : files) {
    //File file = files[0];
      if (file.getName().endsWith("_new.json")) {
        continue;
      }
      if (!file.getName().endsWith(".json")) {
        continue;
      }
      System.out.println(file.getName());
      if(!new ImportJsonFile().addMrdetailByJsonFile(
          file.getAbsolutePath())) {
        break;
      }
    }
  }
  
  public void addDepartmentFile() {
    String filename = "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\醫療展\\department.txt";
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
        String response = postAPI("/department", objectMapper.writeValueAsString(department));
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
    //String filename = "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\藥師.txt";
    String filename = "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\醫療展\\user.txt";
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
        String response = postAPI("/auth/user", objectMapper.writeValueAsString(user));
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
//    new ImportJsonFile().readFile(
//        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\202111\\住院-心臟血管內科50筆_11月.json");
  //  ImportJsonFile.validateJsonDir();
   // ImportJsonFile.addMrDetail();
    new ImportJsonFile().addDepartmentFile();
    new ImportJsonFile().addUserFile();
//    String a = ",00156A,18001C,AC373441G0,AC376011G0,BC25350100,BC21914100,BC030771G0,05209A,BC25350100,";
//    String b = "BC25350100";
//    System.out.println(IntelligentService.countStringAppear(a, b));
  }

}
