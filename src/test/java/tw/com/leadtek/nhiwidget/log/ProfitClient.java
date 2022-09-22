package tw.com.leadtek.nhiwidget.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class ProfitClient extends ApiClient {

  String name;
  String apiName;
  String accessToken;
  String baseUrl;
  int port;
  String apiUrl = "";
  String pathVariable = "";
  String uriParam = "";
  String usernameNew = "";
  String jsonBody = "";
  MediaType mediaType;
  FileSystemResource file = null;
  HttpMethod httpMethod = HttpMethod.POST;
  Class<?> responseCls = BaseResponse.class;
  String response = "";

  String jsonListBody="";

  public void setJsonListBody(String jsonListBody) {
    this.jsonListBody = jsonListBody;
  }

  public ProfitClient(String name) {
    this.setName(name);
  }

  public String getReponse() {
    return response;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setResponseCls(Class<?> responseCls) {
    this.responseCls = responseCls;
  }

  public void setMediaType(MediaType mediaType) {
    this.mediaType = mediaType;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public void setApiName(String apiName) {
    this.apiName = apiName;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setApiUrl(String apiUrl) {
    this.apiUrl = apiUrl;
  }

  public void setPathVariable(String pathVariable) {
    this.pathVariable = pathVariable;
  }

  public void setUriParam(String uriParam) {
    this.uriParam = uriParam;
  }

  public void setUsernameNew(String usernameNew) {
    this.usernameNew = usernameNew;
  }

  public void setJsonBody(String jsonBody) {
    this.jsonBody = jsonBody;
  }

  public void setHttpMethod(HttpMethod httpMethod) {
    this.httpMethod = httpMethod;
  }

  public void setFile(FileSystemResource file) {
    this.file = file;
  }

  @Override
  public void showResult() {
    System.out.println("response: " + response);
  }

  @Override
  public ResponseEntity<?> call() {

    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setOutputStreaming(false);
    RestTemplate restTemplate = new RestTemplate(requestFactory);
    restTemplate.setErrorHandler(
        new DefaultResponseErrorHandler() {
          @Override
          public boolean hasError(HttpStatus statusCode) {
            return false;
          }
        });

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(mediaType);
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<?> entity;
    if (null == file) {
      try {
        // request 的 body 裝載所需傳送的 json

        HashMap<String, Object> bodayJson =
            jsonBody.isBlank() ? null : new ObjectMapper().readValue(jsonBody, HashMap.class);

        List<Object> bodayJsonList =
                jsonListBody.isBlank() ? null : new ObjectMapper().readValue(jsonListBody, (new ArrayList<Object>()).getClass());

        if (null != bodayJsonList) {
          entity = new HttpEntity<>(bodayJsonList, headers);}
       else{
         entity = new HttpEntity<>(bodayJson, headers);
       }

      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    } else {
      // request 的 body 裝載所需傳送的 file
      MultiValueMap<String, Object> bodayFile = new LinkedMultiValueMap<>();
      bodayFile.add("file", file);
      entity = new HttpEntity<>(bodayFile, headers);
    }

    ResponseEntity<?> result =
        restTemplate.exchange(
            baseUrl
                .concat(":")
                .concat(port + "")
                .concat(apiUrl)
                .concat(pathVariable)
                .concat(uriParam),
            httpMethod,
            entity,
            responseCls);

    // 取得 reaponse
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    try {
      response = ow.writeValueAsString(result);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    return result;
  }
}
