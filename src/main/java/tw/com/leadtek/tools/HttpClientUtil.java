package tw.com.leadtek.tools;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 處理Http GET、POST 請求相關共用函數
 * @author Ken Lai
 */
public class HttpClientUtil {

  private Logger log = LogManager.getLogger();
  
  /**
   * setConnectTimeout：設置連接超時時間，單位毫秒。
   * setConnectionRequestTimeout：設置從connect Manager獲取Connection 超時時間，單位毫秒。
   *                              這個屬性是新加的屬性，因為目前版本是可以共享連接池的。
   * setSocketTimeout：請求獲取數據的超時時間，單位毫秒。 如果訪問一個接口，多少時間內無法返回數據，就直接放棄此次調用。
   */
  private RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000)
                                                               .setSocketTimeout(30000)
                                                               .setConnectionRequestTimeout(5000).build();
  
  private String charset = "UTF-8";
  
  public HttpClientUtil() {}
  
  /**
   * 設定connect、請求獲取數據、從connect Manager獲取Connection 連接超時時間
   * @param connectTimeout 設置連接超時時間，單位毫秒。
   * @param socketTimeout 設置請求獲取數據的超時時間，單位毫秒。
   * @param connectionRequestTimeout 設置從connect Manager獲取Connection 超時時間，單位毫秒。
   * @author 
   */
  public void setTimeout(int connectTimeout, int socketTimeout, int connectionRequestTimeout) {
    
    requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
                                          .setSocketTimeout(socketTimeout)
                                          .setConnectionRequestTimeout(connectionRequestTimeout).build();
  }
  
  /**
   * 設定charset，預設為UTF-8
   * @param charset
   */
  public void setCharset(String charset) {
    
    this.charset = charset;
  }
  
  /**
   * 發送 HTTP POST請求
   * @param httpUrl URL位置
   * @param params 參數值(格式:key1=value1&key2=value2)
   * @return String
   */
  public String sendHttpPost(String httpUrl, String params) {

    HttpPost httpPost = new HttpPost(httpUrl);
    try {
      StringEntity stringEntity = new StringEntity(params, charset);
      stringEntity.setContentType("application/x-www-form-urlencoded");
      httpPost.setEntity(stringEntity);
    } catch (Exception e) {
      log.error("run sendHttpPost(httpUrl, params) Exception:", e);
    }
    return sendHttpPost(httpPost);
  }

  /**
   * 發送 HTTP POST請求
   * @param httpUrl URL位置
   * @param maps 參數值(格式:key1=value1，key2=value2)
   * @return String
   */
  public String sendHttpPost(String httpUrl, Map<String, String> maps) {

    HttpPost httpPost = new HttpPost(httpUrl);
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    for (String key:maps.keySet()) {
      nameValuePairs.add(new BasicNameValuePair(key, maps.get(key)));
    }
    try {
      httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, charset));
    } catch (Exception e) {
      log.error("run sendHttpPost(httpUrl, maps) Exception:", e);
    }
    return sendHttpPost(httpPost);
  }
  
  /**
   * 發送 HTTP POST請求
   * @param httpUrl URL位置
   * @param jsonData JSON參數值(格式:{"key":"value","key2":"value"})
   * @return String
   */
  public String sendHttpPostJSON(String httpUrl, String jsonData) {
    
    HttpPost httpPost = new HttpPost(httpUrl);
    httpPost.setEntity(new StringEntity(jsonData, ContentType.APPLICATION_JSON));
    return sendHttpPost(httpPost);
  }
  
  /**
   * 發送 HTTP POST請求 for CatchPlay addheader Oauth_Token
   * @param httpUrl URL位置
   * @param jsonData JSON參數值(格式:{"key":"value","key2":"value"})
   * @return String
   */
  public String sendHttpPostJSONByHeader(String httpUrl, String jsonData,Map<String,String> headerParamMap) {
    
    HttpPost httpPost = new HttpPost(httpUrl);
    httpPost.setHeader("Oauth_Token", headerParamMap.get("Oauth_Token"));
    httpPost.setEntity(new StringEntity(jsonData, ContentType.APPLICATION_JSON));
    return sendHttpPost(httpPost);
  }
  
  /**
   * 發送 HTTP POST請求
   * @param httpUrl URL位置
   * @param xmlData XML參數值
   * @return String
   */
  public String sendHttpPostXML(String httpUrl, String xmlData) {
    
    HttpPost httpPost = new HttpPost(httpUrl);
    StringEntity stringEntity = new StringEntity(xmlData, ContentType.APPLICATION_XML.withCharset(charset));
    stringEntity.setContentEncoding(charset);
    httpPost.setEntity(stringEntity);
    return sendHttpPostExc(httpPost);
  }
  
  /**
   * 發送 HTTP POST SOAP請求
   * @param httpUrl URL位置
   * @param xmlData XML參數值
   * @return String 回應內容
   */
  public String sendHttpPostSOAP(String httpUrl, String xmlData) {
    
    HttpPost httpPost = new HttpPost(httpUrl);
    StringEntity stringEntity = new StringEntity(xmlData, ContentType.TEXT_XML.withCharset(charset));
    stringEntity.setContentEncoding(charset);
    httpPost.setEntity(stringEntity);
    return sendHttpPostExc(httpPost);
  }

  /**
   * 發送 HTTP POST請求
   * @param httpPost
   * @return String
   */
  private String sendHttpPost(HttpPost httpPost) {

    CloseableHttpClient httpClient = null;
    CloseableHttpResponse response = null;
    HttpEntity entity = null;
    String responseContent = null;
    try {
      httpClient = HttpClients.createDefault();
      httpPost.setConfig(requestConfig);
      response = httpClient.execute(httpPost);
      entity = response.getEntity();
      responseContent = EntityUtils.toString(entity, charset);
    } catch (Exception e) {
      log.error("run sendHttpPost(httpPost) Exception: ", e);
    } finally {
      try {
        if (response != null) response.close();
        if (httpClient != null) httpClient.close();
      } catch (IOException e) {
        log.error("run sendHttpPost(httpPost) IOException: ", e);
      }
    }
    return responseContent;
  }
  
  /**
   * 發送 HTTP POST請求，當有Exception時會回傳Exception toString
   * @param httpPost
   * @return String
   */
  private String sendHttpPostExc(HttpPost httpPost) {

    CloseableHttpClient httpClient = null;
    CloseableHttpResponse response = null;
    HttpEntity entity = null;
    String responseContent = null;
    try {
      httpClient = HttpClients.createDefault();
      httpPost.setConfig(requestConfig);
      response = httpClient.execute(httpPost);
      entity = response.getEntity();
      responseContent = EntityUtils.toString(entity, charset);
    } catch (Exception e) {
      log.error("run sendHttpPostExc(httpPost) Exception: ", e);
      responseContent = e.toString();
    } finally {
      try {
        if (response != null) response.close();
        if (httpClient != null) httpClient.close();
      } catch (IOException e) {
        log.error("run sendHttpPostExc(httpPost) IOException: ", e);
        responseContent = e.toString();
      }
    }
    return responseContent;
  }

  /**
   * 發送 HTTP GET請求
   * @param httpUrl URL位置
   * @return String
   */
  public String sendHttpGet(String httpUrl) {

    HttpGet httpGet = new HttpGet(httpUrl);
    return sendHttpGet(httpGet);
  }
  
  /** * 發送 HTTP GET請求
   * @param httpUrl URL位置
   * @param param 額外附帶的參數
   * @return response內容
   * @author Callieo  2016-03-09
   * @throws Exception
   */
  public String sendHttpGet(String httpUrl, Map<String, String> param) throws Exception {

    HttpGet httpGet = new HttpGet();
    URIBuilder newUri = new URIBuilder(httpUrl);
    for (String paramName:param.keySet()) {
      newUri.addParameter(paramName, param.get(paramName));
    }
    httpGet.setURI(newUri.build());
    return sendHttpGet(httpGet);
  }
  
  /**
   * 發送 HTTP GET請求 
   * @param httpUrl URL位置
   * @param param 額外附帶的參數
   * @param encoding 編碼設定
   * @return String
   * @throws Exception
   */
  public String sendHttpGet(String httpUrl, Map<String, String> param, String encoding) throws Exception {

    HttpGet httpGet = new HttpGet();
    URIBuilder newUri = new URIBuilder(httpUrl);
    for (String paramName:param.keySet()) {
      newUri.addParameter(paramName, param.get(paramName));
    }
    newUri.setCharset(Charset.forName(encoding));
    httpGet.setURI(newUri.build());
    return sendHttpGet(httpGet);
  }

  /**
   * 發送 HTTP GET請求
   * @param httpGet
   * @return String
   */
  private String sendHttpGet(HttpGet httpGet) {

    CloseableHttpClient httpClient = null;
    CloseableHttpResponse response = null;
    HttpEntity entity = null;
    String responseContent = null;
    try {
      httpClient = HttpClients.createDefault();
      httpGet.setConfig(requestConfig);
      response = httpClient.execute(httpGet);
      entity = response.getEntity();
      responseContent = EntityUtils.toString(entity, charset);
    } catch (Exception e) {
      log.error("run sendHttpGet(httpGet) Exception: ", e);
    } finally {
      try {
        if (response != null) response.close();
        if (httpClient != null) httpClient.close();
      } catch (IOException e) {
        log.error("run sendHttpGet(httpGet) IOException: ", e);
      }
    }
    return responseContent;
  }
}
