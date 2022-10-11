package tw.com.leadtek.nhiwidget.log;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public interface Builder {

  ApiClient getProfitApi();

  Builder setApiName(String apiName);

  Builder setBaseUrl(String baseUrl);

  Builder setMediaType(MediaType mediaType);

  Builder setPort(int port);

  Builder setAccessToken(String accessToken);

  Builder addApiUrl(String apiUrl);

  Builder addPathVariable(String pathVariable);

  Builder addUriParam(String uriParam);

  /**
   * 使用 addFile 時會忽略 addJsonBody 和 addJsonListBody
   *
   * @param file
   * @return
   */
  Builder addFile(FileSystemResource file);

  Builder httpMethod(HttpMethod httpMethod);

  Builder addJsonBody(String jsonBody);

  Builder addJsonListBody(String jsonListBody);

  Builder setResponseClass(Class<?> resCls);
}
