/**
 * Created on 2021/8/24.
 */
package tw.com.leadtek.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.MO;
import tw.com.leadtek.nhiwidget.payload.MRDetail;
import tw.com.leadtek.nhiwidget.security.jwt.JwtResponse;
import tw.com.leadtek.nhiwidget.security.jwt.LoginRequest;

public class SendHTTP {

  private String serverIP;

  private String port;

  private String filename;

  private HttpClientUtil http = new HttpClientUtil();
  
  public SendHTTP(){
    
  }

  public SendHTTP(String... strings) {
    System.out.println("SendHTTP");

    if (!readConf()) {
      return;
    }
    String token = login(serverIP, port);

    if ("post".equals(strings[0])) {
      uploadFile(token);
    } else if ("scandb".equals(strings[0])) {
      getAPI(token, "/dw/scandb");
    } else if ("reload".equals(strings[0])) {
      getAPI(token, "/system/reload");
    }
  }
  
  public String getServerIP() {
    return serverIP;
  }

  public void setServerIP(String serverIP) {
    this.serverIP = serverIP;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  private void uploadFile(String token) {
    if (filename == null) {
      System.out.println("上傳檔案未設定");
      return;
    }
    File file = new File(filename);
    if (!file.exists()) {
      System.out.println("上傳檔案不存在");
      return;
    }
    System.out.println("token=" + token);
    System.out.println(uploadFile(serverIP, port, file, token));
  }

  public static String login(String serverIP, String port) {
    LoginRequest login = new LoginRequest();
    login.setUsername("leadtekadmin");
    login.setPassword("test");

    ObjectMapper objectMapper = new ObjectMapper();
    try {
      String json = objectMapper.writeValueAsString(login);
      HttpClientUtil http = new HttpClientUtil();
      String response = http.sendHttpPostJSON(composeUrl(serverIP, port, "/auth/signin"), json);
      JwtResponse jwt = objectMapper.readValue(response, JwtResponse.class);
      if (jwt != null && jwt.getToken() != null) {
        return jwt.getToken();
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public String loginUser(String username, String password) {
    LoginRequest login = new LoginRequest();
    login.setUsername(username);
    login.setPassword(password);

    ObjectMapper objectMapper = new ObjectMapper();
    try {
      String json = objectMapper.writeValueAsString(login);
      HttpClientUtil http = new HttpClientUtil();
      String response = http.sendHttpPostJSON(composeUrl(serverIP, port, "/auth/login"), json);
      JwtResponse jwt = objectMapper.readValue(response, JwtResponse.class);
      if (jwt != null && jwt.getToken() != null) {
        return jwt.getToken();
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String uploadFile(String serverIP, String port, File file, String token) {
    try {
      CloseableHttpClient client = HttpClients.createDefault();
      HttpPost post = new HttpPost(composeUrl(serverIP, port, "/dictionary/uploadFile"));
      post.addHeader("Accept", "application/json;odata=verbose");
      post.setHeader("Authorization", "Bearer " + token);
      HttpEntity entity = MultipartEntityBuilder.create().addPart("file", new FileBody(file))
          .addTextBody("startLine", "2").addTextBody("tableName", "all").build();
      post.setEntity(entity);
      System.out.println("post=" + post);
      for (Header header : post.getAllHeaders()) {
        System.out.println(header);
      }
      CloseableHttpResponse response = client.execute(post);
      return EntityUtils.toString(response.getEntity(), "UTF-8");
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String composeUrl(String serverIP, String port, String api) {
    StringBuffer sb = new StringBuffer("http://");
    sb.append(serverIP);
    sb.append(":");
    sb.append(port);
    if (api.startsWith("/")) {
      sb.append(api);
    } else {
      sb.append("/");
      sb.append(api);
    }
    return sb.toString();
  }

  private boolean readConf() {
    File file = new File("http.conf");
    if (!file.exists()) {
      System.err.println("設定檔不存在");
      return false;
    }

    try {
      BufferedReader br =
          new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
      String s = null;
      while ((s = br.readLine()) != null) {
        if (s.startsWith("#")) {
          continue;
        }
        String[] ss = s.split("=");
        if (ss.length != 2) {
          continue;
        }
        if ("serverIP".equals(ss[0])) {
          serverIP = ss[1].trim();
        } else if ("port".equals(ss[0])) {
          port = ss[1].trim();
        } else if ("file".equals(ss[0])) {
          filename = ss[1].trim();
        }
      }
      br.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }

  public String getAPI(String token, String apiUrl) {
    try {
      CloseableHttpClient client = HttpClients.createDefault();
      HttpGet get = new HttpGet(composeUrl(serverIP, port, apiUrl));
      //get.addHeader("Accept", "application/json;odata=verbose");
      if (token != null) {
        get.setHeader("Authorization", "Bearer " + token);
      }
      CloseableHttpResponse response = client.execute(get);
      String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
      //System.out.println("responseBody=" + responseBody);
      return responseBody;
      //ObjectMapper objectMapper = new ObjectMapper();
      //BaseResponse baseResponse = objectMapper.readValue(responseBody, BaseResponse.class);
      //System.out.println(baseResponse.getResult());
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public String postAPI(String token, String apiUrl, String requestBody) {
    try {
      CloseableHttpClient client = HttpClients.createDefault();
      HttpPost post = new HttpPost(composeUrl(serverIP, port, apiUrl));
      //get.addHeader("Accept", "application/json;odata=verbose");
      if (token != null) {
        post.setHeader("Authorization", "Bearer " + token);
      }
      StringEntity stringEntity = new StringEntity(requestBody, "UTF-8");
      stringEntity.setContentType("application/json");
      post.setEntity(stringEntity);
      CloseableHttpResponse response = client.execute(post);
      String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
      System.out.println("responseBody=" + responseBody);
      return responseBody;
      //ObjectMapper objectMapper = new ObjectMapper();
      //BaseResponse baseResponse = objectMapper.readValue(responseBody, BaseResponse.class);
      //System.out.println(baseResponse.getResult());
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public static void testGet() {
    SendHTTP send = new SendHTTP();
    send.setServerIP("10.10.5.30");
    send.setPort("8081");
    String token = send.loginUser("applc", "leadtek");
    System.out.println("token=" + token);
    String response = send.getAPI(token, "/nhixml/mr/180003");
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      MRDetail mrDetail = objectMapper.readValue(response, MRDetail.class);
      for(int i=0;i<mrDetail.getMos().size() ; i++) {
        System.out.println(mrDetail.getMos().get(i).getDrugNo());
      }
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
  
  public static void testPost() {
    SendHTTP send = new SendHTTP();
    send.setServerIP("10.10.5.30");
    send.setPort("8081");
    String response = send.postAPI(null, "/auth/login", "{\r\n" + 
        "    \"username\": \"applc\",\r\n" + 
        "    \"password\" : \"leadtek\"\r\n" + 
        "}");
//    ObjectMapper objectMapper = new ObjectMapper();
//    try {
//      MRDetail mrDetail = objectMapper.readValue(response, MRDetail.class);
//      for(int i=0;i<mrDetail.getMos().size() ; i++) {
//        System.out.println(mrDetail.getMos().get(i).getDrugNo());
//      }
//    } catch (JsonMappingException e) {
//      e.printStackTrace();
//    } catch (JsonProcessingException e) {
//      e.printStackTrace();
//    }
    System.out.println("response=" + response);
  }
  
  public static void main(String[] args) {
     SendHTTP.testPost();
  }
}
