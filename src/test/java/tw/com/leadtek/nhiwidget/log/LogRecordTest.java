package tw.com.leadtek.nhiwidget.log;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.security.jwt.JwtResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogRecordTest {

  private static String accessToken;
  private static final String baseUrl = "http://localhost";
  private static RestTemplate restTemplate = null;
  @Autowired protected JdbcTemplate jdbcTemplate;
  @LocalServerPort private int port;

  @BeforeAll
  static void init() {

    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setOutputStreaming(false);
    restTemplate = new RestTemplate(requestFactory);
    restTemplate.setErrorHandler(
        new DefaultResponseErrorHandler() {
          @Override
          public boolean hasError(HttpStatus statusCode) {
            return false;
          }
        });
  }

  @BeforeEach
  void fetchToken() {
    // 參數
    String username = "leadtek";
    String password = "test";
    String apiUrl = "/auth/login";
    HttpMethod httpMethod = HttpMethod.POST;

    // 調用目標 API
    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    Map<String, String> uriParams = new HashMap<>();

    uriParams.put("username", username);
    uriParams.put("password", password);
    HttpEntity<Map<String, String>> entity = new HttpEntity<>(uriParams, headers);
    ResponseEntity<JwtResponse> result =
        restTemplate.exchange(
            baseUrl.concat(":").concat(port + "").concat(apiUrl),
            httpMethod,
            entity,
            JwtResponse.class);
    accessToken = Objects.requireNonNull(result.getBody()).getToken();
  }

  @Test
  @Order(1)
  void whileApplyForgotPasswordShouldBeRecord() {
    // 參數
    String username = "leadtek2";
    String apiUrl = "/auth/forgetPassword?username=";
    int userId = 7;
    HttpMethod httpMethod = HttpMethod.PUT;

    // 清空資料庫
    String sql_clean = "DELETE FROM log_forgot_password WHERE user_id = %d";
    sql_clean = String.format(sql_clean, userId);
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }

    // 調用目標 API
    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);
    ResponseEntity<BaseResponse> result =
        restTemplate.exchange(
            baseUrl.concat(":").concat(port + "").concat(apiUrl).concat(username),
            httpMethod,
            entity,
            BaseResponse.class);

    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM log_forgot_password WHERE user_id = %d";
    sql = String.format(sql, userId);
    try {
      int cout = jdbcTemplate.queryForObject(sql, Integer.class);
      assertAll(() -> assertEquals(1, cout));
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }
}
