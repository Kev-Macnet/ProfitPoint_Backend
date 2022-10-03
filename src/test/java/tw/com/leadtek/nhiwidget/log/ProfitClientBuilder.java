package tw.com.leadtek.nhiwidget.log;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

class ProfitClientBuilder implements Builder {

  ProfitClient profitClient;

  public ProfitClientBuilder() {
    profitClient = new ProfitClient("Profit");
  }

  @Override
  public ApiClient getProfitApi() {
    return profitClient;
  }

  @Override
  public Builder setApiName(String apiName) {
    profitClient.setApiName(apiName);
    profitClient.add("apiName: " + apiName);
    return this;
  }

  @Override
  public Builder setBaseUrl(String baseUrl) {
    profitClient.setBaseUrl(baseUrl);
    profitClient.add("baseUrl: " + baseUrl);
    return this;
  }

  @Override
  public Builder setMediaType(MediaType mediaType) {
    profitClient.setMediaType(mediaType);
    profitClient.add("mediaType: " + mediaType.toString());
    return this;
  }

  @Override
  public Builder setPort(int port) {
    profitClient.setPort(port);
    profitClient.add("port: " + port);
    return this;
  }

  @Override
  public Builder setAccessToken(String accessToken) {
    profitClient.setAccessToken(accessToken);
    profitClient.add("accessToken: " + accessToken);
    return this;
  }

  @Override
  public Builder addApiUrl(String apiUri) {
    profitClient.setApiUrl(apiUri);
    profitClient.add("apiUri: " + apiUri);
    return this;
  }

  @Override
  public Builder addJsonListBody(String jsonListBody) {
    profitClient.setJsonListBody(jsonListBody);
    profitClient.add("jsonListBody: " + jsonListBody);
    return this;
  }

  @Override
  public Builder addPathVariable(String pathVariable) {
    profitClient.setPathVariable(pathVariable);
    profitClient.add("pathVariable: " + pathVariable);
    return this;
  }

  @Override
  public Builder addUriParam(String uriParam) {
    profitClient.setUriParam(uriParam);
    profitClient.add("uriParam: " + uriParam);
    return this;
  }

  @Override
  public Builder addFile(FileSystemResource file) {
    profitClient.setFile(file);
    profitClient.add("file: " + file.toString());
    return this;
  }

  @Override
  public Builder httpMethod(HttpMethod httpMethod) {
    profitClient.setHttpMethod(httpMethod);
    profitClient.add("httpMethod: " + httpMethod.name());
    return this;
  }

  @Override
  public Builder addJsonBody(String jsonBody) {
    profitClient.setJsonBody(jsonBody);
    profitClient.add("jsonBody: " + jsonBody);
    return this;
  }

  @Override
  public Builder setResponseClass(Class<?> resCls) {
    profitClient.setResponseCls(resCls);
    return this;
  }
}
