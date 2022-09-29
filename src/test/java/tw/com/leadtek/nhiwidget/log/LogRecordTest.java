package tw.com.leadtek.nhiwidget.log;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;
import tw.com.leadtek.nhiwidget.model.rdb.USER;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.ResponseId;
import tw.com.leadtek.nhiwidget.payload.mr.EditMRPayload;
import tw.com.leadtek.nhiwidget.security.jwt.JwtResponse;
import tw.com.leadtek.nhiwidget.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("測試寫入log")
public class LogRecordTest {

  private static final String baseUrl = "http://localhost";
  private static String accessToken;

  //  private static String accessToken2;
  private static RestTemplate restTemplate = null;
  @Autowired protected JdbcTemplate jdbcTemplate;
  @LocalServerPort private int port;

  @Autowired private UserService userService;

  @BeforeEach
  void fetchToken() {
    String username = "leadtek";
    String password = "test";

    String username2 = "leadtek2";
    String password2 = "test";

    Builder builder = new ProfitClientBuilder();
    ApiClient profitClient =
        builder
            .setApiName("取得Token")
            .setBaseUrl("http://localhost")
            .setPort(port)
            .setMediaType(MediaType.APPLICATION_JSON)
            .setAccessToken("")
            .addApiUrl("/auth/login")
            .addPathVariable("")
            .addUriParam("")
            .addJsonBody(
                "{\n"
                    + "  \"username\": \""
                    + username
                    + "\",\n"
                    + "  \"password\": \""
                    + password
                    + "\"\n"
                    + "}")
            .httpMethod(HttpMethod.POST)
            .setResponseClass(JwtResponse.class)
            .getProfitApi();
    profitClient.showProperties();
    ResponseEntity<JwtResponse> result = (ResponseEntity<JwtResponse>) profitClient.call();
    profitClient.showResult();
    accessToken = Objects.requireNonNull(result.getBody()).getToken();
  }

  private void code_threshold_rollback(String descChi) {
    String sql_clean = "DELETE FROM code_threshold WHERE desc_chi =\"" + descChi + "\"";
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }
  @AfterEach
  void cleanActionLog() {
    // Recovery 資料庫
    log_action_clean();
  }

  private void assigned_point_rollback(String fundpoints) {
    String sql_clean = "DELETE FROM assigned_point WHERE fund_points =" + fundpoints;
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  private Map<String, String> getRandomStardAndEndDateMap() {
    Map<String, String> map = new HashMap<String, String>();
    int hundredYearsRangeS = 52 * 365;
    int hundredYearsRangeE = 100 * 365;
    LocalDate randomDayS =
        LocalDate.ofEpochDay(
            ThreadLocalRandom.current().nextInt(hundredYearsRangeS, hundredYearsRangeE));
    LocalDate randomDayE = randomDayS.plusMonths(1);
    String sdate = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(randomDayS);
    String edate = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(randomDayE);
    map.put("sDate", sdate);
    map.put("eDate", edate);
    return map;
  }

  // 內部 function
  private void log_forgot_password_clean(int userId) {
    String sql_clean = "DELETE FROM log_forgot_password WHERE user_id = %d";
    sql_clean = String.format(sql_clean, userId);
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  private void log_action_clean() {
    String sql_clean = "DELETE FROM log_action ";
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  private void log_medical_record_status_clean() {
    String sql_clean = "DELETE FROM log_medical_record_status ";
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  private void user_rollback(String usernameNew) {
    String sql_clean = "DELETE FROM user WHERE userName =\"" + usernameNew + "\"";
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  private void department_rollback(String name) {

    String sql_clean = "DELETE FROM department WHERE name =\"" + name + "\"";
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  private void drg_code_rollback(String code) {
    String sql_clean = "DELETE FROM drg_code WHERE code =\"" + code + "\"";
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  private void atc_rollback(String code) {
    String sql_clean = "DELETE FROM atc WHERE code =\"" + code + "\"";
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  private void pay_code_rollback(String code) {
    String sql_clean = "DELETE FROM pay_code WHERE code =\"" + code + "\"";
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  private void deducted_rollback(String code) {
    String sql_clean = "DELETE FROM deducted WHERE code =\"" + code + "\"";
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  private void plan_condition_rollback(String name) {
    String sql_clean = "DELETE FROM plan_condition WHERE name =\"" + name + "\"";
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  private void cleanLogForgotPassword(int userId) {
    String sql_clean = "DELETE FROM log_forgot_password WHERE user_id = %d";
    sql_clean = String.format(sql_clean, userId);
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  private void emptyLogTable(String tableName) {
    String sql_clean = "DELETE FROM " + tableName;
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
    }
  }

  private void deleteLogTable(String tableName, String userId) {

    String sql_clean = "DELETE FROM " + tableName + " WHERE user_id = '%s'";
    sql_clean = String.format(sql_clean, userId);
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
    }
  }

  private int addUser(String username) {
    USER user = userService.findUser(username);
    if (user != null) {
      return user.getId().intValue();
    }
    String apiUrl = "/auth/user";

    // 調用目標 API
    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Authorization", "Bearer " + accessToken);
    Map<String, String> uriParams = new HashMap<>();

    uriParams.put("username", username);
    uriParams.put("password", "password");
    uriParams.put("role", "E");
    HttpEntity<Map<String, String>> entity = new HttpEntity<>(uriParams, headers);
    ResponseEntity<ResponseId> result =
        restTemplate.exchange(
            baseUrl.concat(":").concat(port + "").concat(apiUrl),
            HttpMethod.POST,
            entity,
            ResponseId.class);
    String userId = Objects.requireNonNull(result.getBody()).getId();
    return Integer.parseInt(userId);
  }

  // ---

  @Nested
  @DisplayName("測試匯出")
  class EXPORT {
    @Test
    @DisplayName("匯出申報檔")
    void _01() throws InterruptedException {

      String tableNameToLog = "log_export";

      // 調用目標 API
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("匯出申報檔")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/downloadXML")
              .addPathVariable("")
              .addUriParam(
                  "?applCategory=1&applDate=2022-02-24&applM=1&applMedic=門診西醫"
                      + "&applMethod=2&applY=2021&dataFormat=10&dateEnd=2022-02-24&dateStart=2022-02-24&inhClinicId=O21100800257")
              .addJsonBody("")
              .setResponseClass(null)
              .httpMethod(HttpMethod.GET)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      TimeUnit.SECONDS.sleep(10);
      // 檢查

      USER user = userService.findUser("leadtek");

      try {
        String sql;
        sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE user_id='" + user.getId() + "'";
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        deleteLogTable(tableNameToLog, user.getId().toString());
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("匯出csv檔")
    void _02() throws InterruptedException {

      String tableNameToLog = "log_export";

      // 調用目標 API
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("匯出csv檔")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/nhixml/exportCSV")
              .addPathVariable("")
              .addUriParam(
                  "?dataFormat=op&dateType=applyDate"
                      + "&exportType=fileExportTypeOne&fnEdate=2020-01-31"
                      + "&fnSdate=2020-01-01&month=1&outEdate=2020-01-31"
                      + "&outSdate=2020-01-01&year=2020")
              .addJsonBody("")
              .setResponseClass(null)
              .httpMethod(HttpMethod.GET)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      TimeUnit.SECONDS.sleep(5);
      USER user = userService.findUser("leadtek2");

      // 檢查
      try {
        String sql;
        sql = "SELECT COUNT(*)  FROM " + tableNameToLog;
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        deleteLogTable(tableNameToLog, user.getId().toString());
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  // ---

  @Nested
  @DisplayName("Tests for the method LOG_FORGOT_PASSWORD_UserController")
  class LOG_FORGOT_PASSWORD_UserController {
    @Test
    @DisplayName("1 - 忘記密碼")
    void _01() {

      // creating user for testing of forget-password function

      String userName = UUID.randomUUID().toString().substring(0, 6);

      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/auth/user")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"departmentId\": \"\",\n" // + "  \"departmentId\": \"88,22\",\n"
                      + "  \"departments\": \"\",\n" //   + "  \"departments\": \"骨科,急診醫學科\",\n"
                      + "  \"displayName\": \"測試帳號\",\n"
                      + "  \"email\": \"string\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"inhId\": \"A01\",\n"
                      + "  \"password\": \"test\",\n"
                      + "  \"rocId\": \"A123456789\",\n"
                      + "  \"role\": \"A\",\n"
                      + "  \"status\": 1,\n"
                      + "  \"username\": \""
                      + userName
                      + "\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      USER user = userService.findUser(userName);

      // test target

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("1 - 忘記密碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/auth/forgetPassword")
              .addPathVariable("")
              .addUriParam("?username=" + userName)
              .addJsonBody("")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM log_forgot_password WHERE user_id = %s";
      sql = String.format(sql, user.getId());
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
        userService.deleteUser(Long.valueOf(user.getId()));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  // ---

  @Nested
  @DisplayName("Tests for the method MEDICAL_RECORD_NOTIFYED_NHIWidgetXMLController")
  class MEDICAL_RECORD_NOTIFYED_NHIWidgetXMLController {
    @Test
    @DisplayName("發送一筆病歷通知")
    void _01() {

      String id = "2589";
      String tableNameToLog = "log_medical_record_notifyed";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("發送一筆病歷通知")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/nhixml/sendNotice")
              .addPathVariable("/" + id)
              .addUriParam("?doctorId=191")
              .addJsonBody("")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog;

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        String sql_clean = "DELETE FROM "+ tableNameToLog +" limit 1;";
        try {
          jdbcTemplate.update(sql_clean);
        } catch (DataAccessException ex) {
          ex.printStackTrace();
          assertAll(() -> fail());
        }

        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("發送多筆病歷通知")
    void _02() {
      String tableNameToLog = "log_medical_record_notifyed";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("發送多筆病歷通知")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/nhixml/sendNotice")
              .addPathVariable("")
              .addUriParam("?doctorId=191&id=2589")
              .addJsonBody("")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();


      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog;

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);


        String sql_clean = "DELETE FROM "+ tableNameToLog +" limit 1;";
        try {
          jdbcTemplate.update(sql_clean);
        } catch (DataAccessException ex) {
          ex.printStackTrace();
          assertAll(() -> fail());
        }

        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  // ---

  @Nested
  @DisplayName("Tests for the method MEDICAL_RECORD_STATUS_CHANGE_NHIWidgetXMLController")
  class MEDICAL_RECORD_STATUS_CHANGE_NHIWidgetXMLController {
    @Test
    @DisplayName("更改指定病歷狀態")
    void _01() {

      String tableNameToLog = "log_medical_record_status";

      String id = "2837";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("更改指定病歷狀態")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("nhixml/mr/status")
              .addPathVariable("/" + id)
              .addUriParam("?status=待確認&statusCode=-2")
              .addJsonBody("")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog;

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        log_medical_record_status_clean();
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  // ---

  @Nested
  @DisplayName("Tests for IMPORT_SystemController")
  class IMPORT_SystemController {
    @Test
    @DisplayName("上傳XML申報檔")
    void _01() {

      String tableNameToLog = "log_import";
      FileSystemResource file;
      String document =
          "<?xml version='1.0' encoding='Big5'?><outpatient>\n"
              + "  <tdata>\n"
              + "    <t1>10</t1>\n"
              + "    <t2>1532011154</t2>\n"
              + "    <t3>11101</t3>\n"
              + "    <t4>2</t4>\n"
              + "    <t5>2</t5>\n"
              + "    <t6>1110101</t6>\n"
              + "    <t7>0</t7>\n"
              + "    <t8>0</t8>\n"
              + "    <t9>0</t9>\n"
              + "    <t10>0</t10>\n"
              + "    <t11>0</t11>\n"
              + "    <t12>0</t12>\n"
              + "    <t13>0</t13>\n"
              + "    <t14>0</t14>\n"
              + "    <t15>0</t15>\n"
              + "    <t16>0</t16>\n"
              + "    <t17>0</t17>\n"
              + "    <t18>0</t18>\n"
              + "    <t19>0</t19>\n"
              + "    <t20>0</t20>\n"
              + "    <t21>0</t21>\n"
              + "    <t22>0</t22>\n"
              + "    <t23>0</t23>\n"
              + "    <t24>0</t24>\n"
              + "    <t25>0</t25>\n"
              + "    <t26>0</t26>\n"
              + "    <t27>0</t27>\n"
              + "    <t28>0</t28>\n"
              + "    <t29>0</t29>\n"
              + "    <t30>0</t30>\n"
              + "    <t31>0</t31>\n"
              + "    <t32>0</t32>\n"
              + "    <t33>0</t33>\n"
              + "    <t34>0</t34>\n"
              + "    <t35>0</t35>\n"
              + "    <t36>0</t36>\n"
              + "    <t37>0</t37>\n"
              + "    <t38>0</t38>\n"
              + "    <t39>0</t39>\n"
              + "    <t40>0</t40>\n"
              + "  </tdata>\n"
              + "  <ddata>\n"
              + "    <dhead/>\n"
              + "    <dbody>\n"
              + "      <d3>H125******</d3>\n"
              + "      <d4>A6</d4>\n"
              + "      <d8>22</d8>\n"
              + "      <d9>1110117</d9>\n"
              + "      <d11>0890603</d11>\n"
              + "      <d14>4</d14>\n"
              + "      <d19>S96.912.A</d19>\n"
              + "      <d20>X58.XXX.A</d20>\n"
              + "      <d30>D03</d30>\n"
              + "      <d39>1236</d39>\n"
              + "      <d41>936</d41>\n"
              + "      <d49>林*杰</d49>\n"
              + "      <pdata>\n"
              + "        <p4>00203B</p4>\n"
              + "        <p8>120.00</p8>\n"
              + "        <p10>1.0</p10>\n"
              + "        <p11>606.0</p11>\n"
              + "        <p12>727</p12>\n"
              + "        <p13>1</p13>\n"
              + "        <p25>\n"
              + "        </p25>\n"
              + "      </pdata>\n"
              + "      <pdata>\n"
              + "        <p1>3</p1>\n"
              + "        <p4>A020707100</p4>\n"
              + "        <p5>1.0</p5>\n"
              + "        <p7>TIDPC</p7>\n"
              + "        <p8>100.00</p8>\n"
              + "        <p9>PO</p9>\n"
              + "        <p10>9.0</p10>\n"
              + "        <p11>0.43</p11>\n"
              + "        <p12>4</p12>\n"
              + "        <p13>2</p13>\n"
              + "        <p25>\n"
              + "        </p25>\n"
              + "      </pdata>\n"
              + "      <pdata>\n"
              + "        <p1>3</p1>\n"
              + "        <p4>AB323121G0</p4>\n"
              + "        <p5>1.0</p5>\n"
              + "        <p7>TID</p7>\n"
              + "        <p8>100.00</p8>\n"
              + "        <p9>PO</p9>\n"
              + "        <p10>9.0</p10>\n"
              + "        <p11>2.0</p11>\n"
              + "        <p12>18</p12>\n"
              + "        <p13>3</p13>\n"
              + "        <p25>\n"
              + "        </p25>\n"
              + "      </pdata>\n"
              + "      <pdata>\n"
              + "        <p4>32017C</p4>\n"
              + "        <p8>120.00</p8>\n"
              + "        <p10>1.0</p10>\n"
              + "        <p11>200.0</p11>\n"
              + "        <p12>240</p12>\n"
              + "        <p13>4</p13>\n"
              + "        <p14>11001010000</p14>\n"
              + "        <p15>11001010000</p15>\n"
              + "        <p25>\n"
              + "        </p25>\n"
              + "      </pdata>\n"
              + "      <pdata>\n"
              + "        <p4>32018C</p4>\n"
              + "        <p8>120.00</p8>\n"
              + "        <p10>1.0</p10>\n"
              + "        <p11>160.0</p11>\n"
              + "        <p12>192</p12>\n"
              + "        <p13>5</p13>\n"
              + "        <p14>11001010000</p14>\n"
              + "        <p15>11001010000</p15>\n"
              + "        <p25>\n"
              + "        </p25>\n"
              + "      </pdata>\n"
              + "      <pdata>\n"
              + "        <p4>05201A</p4>\n"
              + "        <p8>100.00</p8>\n"
              + "        <p10>1.0</p10>\n"
              + "        <p11>55.0</p11>\n"
              + "        <p12>55</p12>\n"
              + "        <p13>6</p13>\n"
              + "        <p14>11001010000</p14>\n"
              + "        <p15>11001010000</p15>\n"
              + "        <p25>\n"
              + "        </p25>\n"
              + "      </pdata>\n"
              + "      <pdata>\n"
              + "        <p4>ER</p4>\n"
              + "        <p8> </p8>\n"
              + "        <p10>0.0</p10>\n"
              + "        <p11>0.0</p11>\n"
              + "        <p12>0</p12>\n"
              + "        <p13>7</p13>\n"
              + "        <p14>11001011716</p14>\n"
              + "        <p15>11001011749</p15>\n"
              + "      </pdata>\n"
              + "    </dbody>\n"
              + "  </ddata>\n"
              + "</outpatient>\n";

      try {
        Path path = Files.createTempFile("upload", ".xml");
        Files.write(path, document.getBytes());
        file = new FileSystemResource(path.toFile());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      // 調用目標 API
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
                  .setApiName("上傳XML申報檔")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.MULTIPART_FORM_DATA)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/uploadXML")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody("")
              .addFile(file)
              .setResponseClass(BaseResponse.class)
              .httpMethod(HttpMethod.POST)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog;

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        emptyLogTable(tableNameToLog);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  // ---

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_C_UserController")
  class LOG_ACTION_C_UserController {
    @Test
    @DisplayName("1 - 新增帳號")
    void _01() {

      String usernameNew = "test_user_crt";
      String tableName = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增帳號")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/auth/user")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"departmentId\": \"\",\n" // + "  \"departmentId\": \"88,22\",\n"
                      + "  \"departments\": \"\",\n" //   + "  \"departments\": \"骨科,急診醫學科\",\n"
                      + "  \"displayName\": \"測試帳號\",\n"
                      + "  \"email\": \"string\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"inhId\": \"A01\",\n"
                      + "  \"password\": \"test\",\n"
                      + "  \"rocId\": \"A123456789\",\n"
                      + "  \"role\": \"A\",\n"
                      + "  \"status\": 1,\n"
                      + "  \"username\": \""
                      + usernameNew
                      + "\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableName + " WHERE crud ='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        // recovery 資料庫
        user_rollback(usernameNew);
        assertAll("新增帳號成功，會紀錄一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("2 - 新增部門")
    void _02() {
      String newDepartmentName = UUID.randomUUID().toString().substring(0, 6);
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增部門")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/department")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \"06\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"name\": \""
                      + newDepartmentName
                      + "\",\n"
                      + "  \"nhCode\": \"false06\",\n"
                      + "  \"nhName\": \"骨科\",\n"
                      + "  \"note\": \"骨科\",\n"
                      + "  \"status\": 1\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        // Recovery 資料庫

        department_rollback(newDepartmentName);

        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_U_UserController")
  class LOG_ACTION_U_UserController {
    @Test
    @DisplayName("1 - 更新帳號")
    void _01() {

      String tableName = "log_action";

      // creating user for test

      String usernameNew = UUID.randomUUID().toString().substring(0, 6);

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增帳號 - 測試更新帳號")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/auth/user")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"departmentId\": \"\",\n" // + "  \"departmentId\": \"88,22\",\n"
                      + "  \"departments\": \"\",\n" //   + "  \"departments\": \"骨科,急診醫學科\",\n"
                      + "  \"displayName\": \"測試帳號\",\n"
                      + "  \"email\": \"string\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"inhId\": \"A01\",\n"
                      + "  \"password\": \"test\",\n"
                      + "  \"rocId\": \"A123456789\",\n"
                      + "  \"role\": \"A\",\n"
                      + "  \"status\": 1,\n"
                      + "  \"username\": \""
                      + usernameNew
                      + "\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      long id = userService.findUser(usernameNew).getId();

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/user")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"departmentId\": \"88,22\",\n"
                      + "  \"departments\": \"骨科,急診醫學科\",\n"
                      + "  \"displayName\": \"測試帳號\",\n"
                      + "  \"email\": \"string\",\n"
                      + "  \"id\": "
                      + id
                      + ",\n"
                      + "  \"inhId\": \"A01\",\n"
                      + "  \"password\": \"test1234\",\n"
                      + "  \"rocId\": \"A123456789\",\n"
                      + "  \"role\": \"A\",\n"
                      + "  \"status\": 1,\n"
                      + "  \"username\": \"test\"\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 參數
      //      String apiUrl = "/auth/user";
      //      String pathVariable = "";
      //      String uriParam = "";
      //      String usernameNew = "leadtek_new";
      //      HttpMethod httpMethod = HttpMethod.POST;
      //      String tableName = "log_action";
      //      String jsonBody =
      //              "{\n"
      //                      + "  \"departmentId\": \"88,22\",\n"
      //                      + "  \"departments\": \"骨科,急診醫學科\",\n"
      //                      + "  \"displayName\": \"測試帳號\",\n"
      //                      + "  \"email\": \"string\",\n"
      //                      + "  \"id\": 1,\n"
      //                      + "  \"inhId\": \"A01\",\n"
      //                      + "  \"password\": \"test1234\",\n"
      //                      + "  \"rocId\": \"A123456789\",\n"
      //                      + "  \"role\": \"A\",\n"
      //                      + "  \"status\": 1,\n"
      //                      + "  \"username\": \""
      //                      + usernameNew
      //                      + "\"\n"
      //                      + "}";

      // 調用目標 API
      //      callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam, pathVariable);

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableName + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        // recovery 資料庫
        user_rollback(usernameNew);
        assertAll("更新帳號成功，在 log 表裡應新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("2 - 更換密碼")
    void _02() {
      String tableNameToLog = "log_action";

      // === get username & password by creating user ===

      String username = "test_user_chPW";
      String password = "pwFDFDFDFSDFSDFASDF";

      Builder builderCrtUser = new ProfitClientBuilder();
      ApiClient profitClientCrtUser =
          builderCrtUser
              .setApiName("新增一組帳號")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/auth/user")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"departmentId\": \"88,22\",\n"
                      + "  \"departments\": \"\",\n" // 骨科,急診醫學科
                      + "  \"displayName\": \"測試帳號\",\n"
                      + "  \"email\": \"string\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"inhId\": \"A01\",\n"
                      + "  \"password\": \""
                      + password
                      + "\",\n"
                      + "  \"rocId\": \"A123456789\",\n"
                      + "  \"role\": \"A\",\n"
                      + "  \"status\": 1,\n"
                      + "  \"username\": \""
                      + username
                      + "\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClientCrtUser.showProperties();
      ResponseEntity<BaseResponse> responseCrtUser =
          (ResponseEntity<BaseResponse>) profitClientCrtUser.call();
      profitClientCrtUser.showResult();

      // === get current user token ===

      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("登入")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken("")
              .addApiUrl("/auth/login")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"username\": \""
                      + username
                      + "\",\n"
                      + "  \"password\": \""
                      + password
                      + "\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(JwtResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<JwtResponse> result = (ResponseEntity<JwtResponse>) profitClient2.call();
      profitClient2.showResult();
      String accessTokenLocal = Objects.requireNonNull(result.getBody()).getToken();

      // === test target ===

      String newPassword = "111111";
      String oldPassword = password;
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("更換密碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessTokenLocal)
              .addApiUrl("/user/changePassword")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"newPassword\": \""
                      + newPassword
                      + "\",\n"
                      + "  \"oldPassword\": \""
                      + oldPassword
                      + "\"\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        // Recovery 資料庫

        //        department_rollback(newDepartmentName);

        assertAll(
            "在 log 表裡應該會新增一筆資料",
            () -> assertEquals(1, cout),
            () -> assertEquals(1, cout),
            () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("3 - 更新部門 - DepartmentHash 會 null")
    void _03() {
      String tableNameToLog = "log_action";

      // creating  a department for test
      String newDepartmentName = UUID.randomUUID().toString().substring(0, 6);

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增部門")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/department")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \"06\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"name\": \""
                      + newDepartmentName
                      + "\",\n"
                      + "  \"nhCode\": \"false06\",\n"
                      + "  \"nhName\": \"骨科\",\n"
                      + "  \"note\": \"骨科\",\n"
                      + "  \"status\": 1\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      String sqlDep;
      sqlDep = "SELECT id  FROM department  WHERE name='" + newDepartmentName + "'";
      int id = jdbcTemplate.queryForObject(sqlDep, Integer.class);

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("更新部門")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/department")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \"06\",\n"
                      + "  \"id\": \""
                      + id
                      + "\",\n"
                      + "  \"name\": \"骨科2\",\n"
                      + "  \"nhCode\": \"false06\",\n"
                      + "  \"nhName\": \"骨科\",\n"
                      + "  \"note\": \"骨科\",\n"
                      + "  \"status\": 1\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        // Recovery 資料庫

        department_rollback(newDepartmentName);

        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_D_UserController")
  class LOG_ACTION_D_UserController {
    @Test
    @DisplayName("1 - 刪除帳號")
    void _01() {

      String tableName = "log_action";

      // creating user for test

      String username = UUID.randomUUID().toString().substring(0, 6);

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增一組帳號 - 測試用")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/auth/user")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"departmentId\": \"\",\n" // + "  \"departmentId\": \"88,22\",\n"
                      + "  \"departments\": \"\",\n" //   + "  \"departments\": \"骨科,急診醫學科\",\n"
                      + "  \"displayName\": \"測試帳號\",\n"
                      + "  \"email\": \"string\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"inhId\": \"A01\",\n"
                      + "  \"password\": \"test\",\n"
                      + "  \"rocId\": \"A123456789\",\n"
                      + "  \"role\": \"A\",\n"
                      + "  \"status\": 1,\n"
                      + "  \"username\": \""
                      + username
                      + "\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      Long userid = userService.findUser(username).getId();

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/user")
              .addPathVariable("/" + userid)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();


      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableName + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        // recovery 資料庫
        user_rollback(username);
        assertAll("更新帳號成功，在 log 表裡應新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("2 - 刪除部門")
    void _02() {
      String tableNameToLog = "log_action";

      // creating a department for test
      String newDepartmentName = UUID.randomUUID().toString().substring(0, 6);

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增部門 - 測試刪除部門功能")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/department")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \"06\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"name\": \""
                      + newDepartmentName
                      + "\",\n"
                      + "  \"nhCode\": \"false06\",\n"
                      + "  \"nhName\": \"骨科\",\n"
                      + "  \"note\": \"骨科\",\n"
                      + "  \"status\": 1\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // dep id

      String sqlDep;
      sqlDep = "SELECT id  FROM department  WHERE name='" + newDepartmentName + "'";
      int id = jdbcTemplate.queryForObject(sqlDep, Integer.class);

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("刪除部門")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("department")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        // Recovery 資料庫

        department_rollback(newDepartmentName);

        assertAll(
            "在 log 表裡應該會新增一筆資料",
            () -> assertEquals(1, cout),
            () -> assertEquals(1, cout),
            () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  // ---

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_C_PlanCOnditionController")
  class LOG_ACTION_C_PlanCOnditionController {
    @Test
    @DisplayName("新增計畫可收案病例條件")
    void _01() {
      String tableNameToLog = "log_action";
      String name = "testname";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/plan/add")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"division\": \"心臟科\",\n"
                      + "  \"exclude_join\": \"G101***505\",\n"
                      + "  \"exclude_join_enable\": 1,\n"
                      + "  \"exclude_plan_nday\": 10,\n"
                      + "  \"exclude_plan_nday_enable\": 1,\n"
                      + "  \"exclude_psychiatric_enable\": 1,\n"
                      + "  \"exp_icd_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"exp_icd_no_enable\": 1,\n"
                      + "  \"icd_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"icd_no_enable\": 1,\n"
                      + "  \"less_nday\": [\n"
                      + "    {\n"
                      + "      \"icd_no\": \"icd-0001\",\n"
                      + "      \"nday\": 20\n"
                      + "    }\n"
                      + "  ],\n"
                      + "  \"less_nday_enable\": 1,\n"
                      + "  \"medicine_times\": 10,\n"
                      + "  \"medicine_times_division\": \"骨科\",\n"
                      + "  \"medicine_times_enable\": 1,\n"
                      + "  \"more_times\": [\n"
                      + "    {\n"
                      + "      \"icd_no\": \"icd-0001\",\n"
                      + "      \"times\": 10\n"
                      + "    }\n"
                      + "  ],\n"
                      + "  \"more_times_enable\": 1,\n"
                      + "  \"name\": \""
                      + name
                      + "\",\n"
                      + "  \"no_exp_icd_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"no_exp_icd_no_enable\": 1\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        // Recovery 資料庫

        //        plan_condition_rollback(name);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_U_PlanCOnditionController")
  class LOG_ACTION_U_PlanCOnditionController {
    @Test
    @DisplayName("13.03 更新計畫可收案病例條件")
    void _01() {
      String tableNameToLog = "log_action";
      String id = "195";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("13.03 更新計畫可收案病例條件")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/plan")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 2,\n"
                      + "  \"division\": \"心臟科\",\n"
                      + "  \"exclude_join\": \"G101***505\",\n"
                      + "  \"exclude_join_enable\": 1,\n"
                      + "  \"exclude_plan_nday\": 10,\n"
                      + "  \"exclude_plan_nday_enable\": 1,\n"
                      + "  \"exclude_psychiatric_enable\": 1,\n"
                      + "  \"exp_icd_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"exp_icd_no_enable\": 1,\n"
                      + "  \"icd_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"icd_no_enable\": 1,\n"
                      + "  \"less_nday\": [\n"
                      + "    {\n"
                      + "      \"icd_no\": \"icd-0001\",\n"
                      + "      \"nday\": 20\n"
                      + "    }\n"
                      + "  ],\n"
                      + "  \"less_nday_enable\": 1,\n"
                      + "  \"medicine_times\": 10,\n"
                      + "  \"medicine_times_division\": \"骨科\",\n"
                      + "  \"medicine_times_enable\": 1,\n"
                      + "  \"more_times\": [\n"
                      + "    {\n"
                      + "      \"icd_no\": \"icd-0001\",\n"
                      + "      \"times\": 10\n"
                      + "    }\n"
                      + "  ],\n"
                      + "  \"more_times_enable\": 1,\n"
                      + "  \"name\": \"星光計畫\",\n"
                      + "  \"no_exp_icd_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"no_exp_icd_no_enable\": 1\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        // Recovery 資料庫

        //        plan_condition_rollback(name);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("13.06 計畫可收案病例條件狀態設定")
    void _02() {
      String tableNameToLog = "log_action";
      String id = "194";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("13.06 計畫可收案病例條件狀態設定")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/plan/setactive")
              .addPathVariable("/" + id)
              .addUriParam("?state=2")
              .addJsonBody("")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        // Recovery 資料庫

        //        plan_condition_rollback(name);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_D_PlanCOnditionController")
  class LOG_ACTION_D_PlanCOnditionController {
    @Test
    @DisplayName("13.04 刪除計畫可收案病例條件")
    void _01() {
      String tableNameToLog = "log_action";
      String id = "195";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("13.03 更新計畫可收案病例條件")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/plan")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        // Recovery 資料庫

        //        plan_condition_rollback(name);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  // ---

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_C_SystemController")
  class LOG_ACTION_C_SystemController {
    @Test
    @DisplayName("新增ICD10代碼")
    void LOG_ACTION_C_SystemController_1_() {

      String tableNameToLog = "log_action";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增ICD10代碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/icd10")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"cat\": \"CM\",\n"
                      + "  \"code\": \"0SG13ZJ\",\n"
                      + "  \"descChi\": \"經皮2節以上腰椎關節由後側進入前柱融合術\",\n"
                      + "  \"descEn\": \"Fusion of 2 or more Lumbar Vertebral Joints, Posterior Approach, Anterior Column, Percutaneous Approach\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"infCat\": 1,\n"
                      + "  \"infectious\": 0,\n"
                      + "  \"remark\": \"補充說明欄\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("新增一組DRGcode")
    void LOG_ACTION_C_SystemController_2_() {

      String code = "00502";
      String tableNameToLog = "log_action";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增一組DRGcode")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/drg")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"avgInDay\": 4,\n"
                      + "  \"case20\": false,\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"dep\": \"M\",\n"
                      + "  \"endDay\": \"2019/12/31\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"llimit\": 833974,\n"
                      + "  \"mdc\": 1,\n"
                      + "  \"pr\": false,\n"
                      + "  \"rw\": 2.829,\n"
                      + "  \"serial\": 10,\n"
                      + "  \"startDay\": \"2019/07/01\",\n"
                      + "  \"started\": false,\n"
                      + "  \"ulimit\": 979217\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        // Recovery 資料庫

        drg_code_rollback(code);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("新增一組ATC分類代碼")
    void LOG_ACTION_C_SystemController_3_() {
      String tableNameToLog = "log_action";
      String code = "testcode";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增一組ATC分類代碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/atc")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"leng\": 3,\n"
                      + "  \"note\": \"Oxymetazoline3\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        // Recovery 資料庫

        atc_rollback(code);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("新增一組代碼品項")
    void LOG_ACTION_C_SystemController_4_() {

      String code = UUID.randomUUID().toString().substring(0, 6);
      String tableNameToLog = "log_action";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增一組代碼品項")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/payCode")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"atc\": \"A01\",\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"codeType\": \"病房費\",\n"
                      + "  \"eday\": \"2021/06/30\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"inhCode\": \"P56008\",\n"
                      + "  \"inhName\": \"藥品調劑費\",\n"
                      + "  \"level\": [\n"
                      + "    \"基層院所\",\n"
                      + "    \"地區醫院\"\n"
                      + "  ],\n"
                      + "  \"name\": \"藥品調劑費\",\n"
                      + "  \"nameEn\": \"TRANZEPAM TABLETS\",\n"
                      + "  \"ownExpense\": 600,\n"
                      + "  \"point\": 2100,\n"
                      + "  \"sday\": \"2021/01/01\",\n"
                      + "  \"second\": false\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        // Recovery 資料庫

        pay_code_rollback(code);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("新增核減代碼")
    void LOG_ACTION_C_SystemController_5_() {
      String tableNameToLog = "log_action";

      String code = UUID.randomUUID().toString().substring(0, 6);
      ;

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增核減代碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/deducted")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"l1\": \"專業審查不予支付代碼\",\n"
                      + "  \"l2\": \"西醫\",\n"
                      + "  \"l3\": \"string\",\n"
                      + "  \"name\": \"診療品質不符專業認定，理由____\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        // Recovery 資料庫

        //        deducted_rollback(code);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_U_SystemController")
  class LOG_ACTION_U_SystemController {
    @Test
    @DisplayName("修改DRG code")
    void _1() {

      String tableNameToLog = "log_action";

      // creating drgcode for test
      String code = UUID.randomUUID().toString().substring(0, 5);

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增一組DRGcode - 測試修改DRG code ")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/drg")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"avgInDay\": 4,\n"
                      + "  \"case20\": false,\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"dep\": \"M\",\n"
                      + "  \"endDay\": \"2019/12/31\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"llimit\": 833974,\n"
                      + "  \"mdc\": 1,\n"
                      + "  \"pr\": false,\n"
                      + "  \"rw\": 2.829,\n"
                      + "  \"serial\": 10,\n"
                      + "  \"startDay\": \"2019/07/01\",\n"
                      + "  \"started\": false,\n"
                      + "  \"ulimit\": 979217\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      String sqlDrg;
      sqlDrg = "SELECT id  FROM drg_code WHERE code='" + code + "'";
      int id = jdbcTemplate.queryForObject(sqlDrg, Integer.class);

      // test target
      Builder builder2 = new ProfitClientBuilder();

      ApiClient profitClient2 =
          builder2
              .setApiName("修改DRG code")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/drg")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"avgInDay\": 4,\n"
                      + "  \"case20\": false,\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"dep\": \"M\",\n"
                      + "  \"endDay\": \"2019/12/31\",\n"
                      + "  \"id\": "
                      + id
                      + ",\n"
                      + "  \"llimit\": 833975,\n"
                      + "  \"mdc\": 1,\n"
                      + "  \"pr\": false,\n"
                      + "  \"rw\": 2.829,\n"
                      + "  \"serial\": 10,\n"
                      + "  \"startDay\": \"2019/07/01\",\n"
                      + "  \"started\": false,\n"
                      + "  \"ulimit\": 979217\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("修改ATC分類代碼")
    void _2() {

      String code = UUID.randomUUID().toString().substring(0, 7);
      String tableNameToLog = "log_action";

      // creating atc code for test

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增一組ATC分類代碼 - 測試修改ATC分類代碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/atc")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"leng\": 3,\n"
                      + "  \"note\": \"Oxymetazoline3\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // test target
      Builder builder2 = new ProfitClientBuilder();

      ApiClient profitClient2 =
          builder2
              .setApiName("修改ATC分類代碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/atc")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"leng\": 3,\n"
                      + "  \"note\": \"Oxymetazoline2\"\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("修改代碼品項資料")
    void _03() {
      String tableNameToLog = "log_action";
      String code = UUID.randomUUID().toString().substring(0, 7);
      String depTable = "payCode"; // 支付標準代碼(醫令)

      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      // creating paycode for test

      Builder builder = new ProfitClientBuilder();

      ApiClient profitClient =
          builder
              .setApiName("新增一組代碼品項 - 測試修改代碼品項資料")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/payCode")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"atc\": \"A01\",\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"codeType\": \"病房費\",\n"
                      + "  \"eday\": \""
                      + map.get("eDate")
                      + "\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"inhCode\": \"P56008\",\n"
                      + "  \"inhName\": \"藥品調劑費\",\n"
                      + "  \"level\": [\n"
                      + "    \"基層院所\",\n"
                      + "    \"地區醫院\"\n"
                      + "  ],\n"
                      + "  \"name\": \"藥品調劑費\",\n"
                      + "  \"nameEn\": \"TRANZEPAM TABLETS\",\n"
                      + "  \"ownExpense\": 600,\n"
                      + "  \"point\": 2100,\n"
                      + "  \"sday\": \""
                      + map.get("sDate")
                      + "\",\n"
                      + "  \"second\": false\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // test target
      Builder builder2 = new ProfitClientBuilder();
      String s = "2021/06/30";
      String s1 = "2020/01/01";
      ApiClient profitClient2 =
          builder2
              .setApiName("修改代碼品項資料")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/payCode")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"atc\": \"A01\",\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"codeType\": \"病房費\",\n"
                      + "  \"eday\": \""
                      + s
                      + "\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"inhCode\": \"P56008\",\n"
                      + "  \"inhName\": \"藥品調劑費\",\n"
                      + "  \"level\": [\n"
                      + "    \"基層院所\",\n"
                      + "    \"地區醫院\"\n"
                      + "  ],\n"
                      + "  \"name\": \"藥品調劑費\",\n"
                      + "  \"nameEn\": \"TRANZEPAM TABLETS\",\n"
                      + "  \"ownExpense\": 600,\n"
                      + "  \"point\": 2100,\n"
                      + "  \"sday\": \""
                      + s1
                      + "\",\n"
                      + "  \"second\": false\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("修改核減代碼")
    void _04() {

      String code = "testcode";
      String tableNameToLog = "log_action";
      String depTable = "deducted"; // 核減代碼表

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("修改核減代碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/deducted")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \"0001A\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"l1\": \"專業審查不予支付代碼\",\n"
                      + "  \"l2\": \"西醫\",\n"
                      + "  \"l3\": \"string\",\n"
                      + "  \"name\": \"診療品質不符專業認定，理由____\"\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("修改ICD10代碼")
    void _05() {
      String tableNameToLog = "log_action";
      String depTable = "icd10"; // ICD10代碼表

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("修改ICD10代碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/icd10")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"cat\": \"CM\",\n"
                      + "  \"code\": \"0SG13ZJ\",\n"
                      + "  \"descChi\": \"經皮2節以上腰椎關節由後側進入前柱融合術\",\n"
                      + "  \"descEn\": \"Fusion of 2 or more Lumbar Vertebral Joints, Posterior Approach, Anterior Column, Percutaneous Approach\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"infCat\": 1,\n"
                      + "  \"infectious\": 0,\n"
                      + "  \"remark\": \"補充說明欄\"\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("6 - 更新檔案管理功能設定 - 疑問：")
    void _06() {
      String tableNameToLog = "log_action";
      String depTable = "parameters"; // 參數設定

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("更新檔案管理功能設定")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/config/fileManagement")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"dailyInput\": true,\n"
                      + "  \"dailyOutput\": true,\n"
                      + "  \"inputByButton\": true,\n"
                      + "  \"inputByFile\": true,\n"
                      + "  \"inputTime\": \"string\",\n"
                      + "  \"message\": \"錯誤訊息\",\n"
                      + "  \"outputByButton\": true,\n"
                      + "  \"outputByFile\": true,\n"
                      + "  \"outputTime\": \"string\",\n"
                      + "  \"result\": \"success or error\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(8, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("更新比對警示功能設定")
    void _07() {
      String tableNameToLog = "log_action";
      String depTable = "parameters";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("更新比對警示功能設定")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/config/compareWarning")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"compareBy\": 0,\n"
                      + "  \"doctor\": \"string\",\n"
                      + "  \"funcType\": \"string\",\n"
                      + "  \"message\": \"錯誤訊息\",\n"
                      + "  \"result\": \"success or error\",\n"
                      + "  \"rollbackHour\": 0\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(4, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("更新疑問提示通知功能設定")
    void _08() {
      String tableNameToLog = "log_action";
      String depTable = "parameters";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("更新疑問提示通知功能設定")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/config/questionMark")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"doctor\": \"string\",\n"
                      + "  \"funcType\": \"string\",\n"
                      + "  \"markBy\": 0,\n"
                      + "  \"message\": \"錯誤訊息\",\n"
                      + "  \"result\": \"success or error\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(3, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("更新智能提示助理功能設定")
    void _09() {
      String tableNameToLog = "log_action";
      String depTable = "parameters";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("更新智能提示助理功能設定")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/config/intelligent")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"clinicalDiff\": true,\n"
                      + "  \"costDiffLL\": \"string\",\n"
                      + "  \"costDiffUL\": \"string\",\n"
                      + "  \"drgSuggestion\": true,\n"
                      + "  \"highRatio\": true,\n"
                      + "  \"highRisk\": true,\n"
                      + "  \"infectious\": true,\n"
                      + "  \"inhOwnExist\": true,\n"
                      + "  \"ipDays\": 0,\n"
                      + "  \"message\": \"錯誤訊息\",\n"
                      + "  \"orderLL\": \"string\",\n"
                      + "  \"orderUL\": \"string\",\n"
                      + "  \"overAmount\": true,\n"
                      + "  \"pilotProject\": true,\n"
                      + "  \"rareIcd\": true,\n"
                      + "  \"result\": \"success or error\",\n"
                      + "  \"sameAtc\": true,\n"
                      + "  \"sameAtcLen5\": true,\n"
                      + "  \"suspected\": true,\n"
                      + "  \"violate\": true\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("更新資料庫串接管理設定")
    void _10() {
      String tableNameToLog = "log_action";
      String code = "testcode";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("更新資料庫串接管理設定")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/config/dbManagement")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"addFuncCodeBy\": 0,\n"
                      + "  \"addPayCodeBy\": 0,\n"
                      + "  \"addUserBy\": 0,\n"
                      + "  \"message\": \"錯誤訊息\",\n"
                      + "  \"result\": \"success or error\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(3, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_D_SystemController")
  class LOG_ACTION_D_SystemController {
    @Test
    @DisplayName("刪除DRG code")
    void _01() {

      String tableNameToLog = "log_action";
      String depTable = "drg_code";

      // creating drg code for test
      String code = UUID.randomUUID().toString().substring(0, 5);

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增一組DRGcode - 刪除DRG code")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/drg")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"avgInDay\": 4,\n"
                      + "  \"case20\": false,\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"dep\": \"M\",\n"
                      + "  \"endDay\": \"2019/12/31\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"llimit\": 833974,\n"
                      + "  \"mdc\": 1,\n"
                      + "  \"pr\": false,\n"
                      + "  \"rw\": 2.829,\n"
                      + "  \"serial\": 10,\n"
                      + "  \"startDay\": \"2019/07/01\",\n"
                      + "  \"started\": false,\n"
                      + "  \"ulimit\": 979217\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      String sqlDrg = "SELECT id  FROM drg_code WHERE code='" + code + "'";

      int id = jdbcTemplate.queryForObject(sqlDrg, Integer.class);

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("刪除DRG code")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/drg")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("刪除ATC code")
    void _02() {

      String tableNameToLog = "log_action";

      // creating atc code for test
      String code = UUID.randomUUID().toString().substring(0, 7);
      String id = code;

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增一組ATC分類代碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/atc")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"leng\": 3,\n"
                      + "  \"note\": \"Oxymetazoline3\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("刪除ATC code")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/atc")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("刪除代碼品項資料")
    void _03() {
      String tableNameToLog = "log_action";

      // creating paycode for test
      String code = UUID.randomUUID().toString().substring(0, 6);

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增一組代碼品項")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/payCode")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"atc\": \"A01\",\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"codeType\": \"病房費\",\n"
                      + "  \"eday\": \"2021/06/30\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"inhCode\": \"P56008\",\n"
                      + "  \"inhName\": \"藥品調劑費\",\n"
                      + "  \"level\": [\n"
                      + "    \"基層院所\",\n"
                      + "    \"地區醫院\"\n"
                      + "  ],\n"
                      + "  \"name\": \"藥品調劑費\",\n"
                      + "  \"nameEn\": \"TRANZEPAM TABLETS\",\n"
                      + "  \"ownExpense\": 600,\n"
                      + "  \"point\": 2100,\n"
                      + "  \"sday\": \"2021/01/01\",\n"
                      + "  \"second\": false\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      String sqlDrg = "SELECT id  FROM pay_code WHERE code='" + code + "'";

      int id = jdbcTemplate.queryForObject(sqlDrg, Integer.class);

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("刪除代碼品項資料")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/payCode")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("刪除核減代碼")
    void _04() {

      String tableNameToLog = "log_action";

      // creating deducted for test
      String code = UUID.randomUUID().toString().substring(0, 6);
      ;

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增核減代碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/deducted")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"l1\": \"專業審查不予支付代碼\",\n"
                      + "  \"l2\": \"西醫\",\n"
                      + "  \"l3\": \"string\",\n"
                      + "  \"name\": \"診療品質不符專業認定，理由____\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      String sqldeducted = "SELECT id  FROM deducted WHERE code='" + code + "'";

      int id = jdbcTemplate.queryForObject(sqldeducted, Integer.class);

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("刪除核減代碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/deducted")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("刪除ICD10代碼")
    void _05() {
      String tableNameToLog = "log_action";

      // creating icd10 for test
      Builder builder = new ProfitClientBuilder();
      String code = UUID.randomUUID().toString().substring(0, 6);
      ApiClient profitClient =
          builder
              .setApiName("新增ICD10代碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/icd10")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"cat\": \"CM\",\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"descChi\": \"經皮2節以上腰椎關節由後側進入前柱融合術\",\n"
                      + "  \"descEn\": \"Fusion of 2 or more Lumbar Vertebral Joints, Posterior Approach, Anterior Column, Percutaneous Approach\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"infCat\": 1,\n"
                      + "  \"infectious\": 0,\n"
                      + "  \"remark\": \"補充說明欄\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      String sqlDrg = "SELECT id  FROM icd10 WHERE code='" + code + "'";

      int id = jdbcTemplate.queryForObject(sqlDrg, Integer.class);

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("刪除ICD10代碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/sys/icd10")
              .addPathVariable("")
              .addUriParam("/" + id)
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  // ---

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_C_ParameterController")
  class LOG_ACTION_C_ParameterController {
    @RepeatedTest(3)
    @DisplayName("1 - 新增分配點數")
    void _01() {

      String fundpoints = "9999999";
      String tableNameToLog = "log_action";

      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      // test target
      Builder builder = new ProfitClientBuilder();
      String s = "2022/06/30";
      String s1 = "2022/01/01";
      ApiClient profitClient =
          builder
              .setApiName("新增分配點數")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/assignedPoints")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"dentistDrugPoints\": 1000,\n"
                      + "  \"dentistFundPoints\": 1000,\n"
                      + "  \"dentistOpPoints\": 1000,\n"
                      + "  \"edate\": \""
                      + map.get("eDate")
                      + "\",\n"
                      + "  \"fundPoints\": "
                      + fundpoints
                      + ",\n"
                      + "  \"hemodialysisPoints\": 10000,\n"
                      + "  \"id\": 1,\n"
                      + "  \"sdate\": \""
                      + map.get("sDate")
                      + "\",\n"
                      + "  \"wmDrugPoints\": 10000,\n"
                      + "  \"wmIpPoints\": 100000,\n"
                      + "  \"wmOpPoints\": 100000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        // Recovery 資料庫
        assigned_point_rollback(fundpoints);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("2 - 新增分配總點數")
    void _02() {
      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      String formatedDayS = map.get("sDate");
      String formatedDayE = map.get("eDate");
      String fundpoints = "9999999";
      String tableNameToLog = "log_action";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增分配總點數")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/pointsValue")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"dentistDrugPoints\": 0,\n"
                      + "  \"dentistFundPoints\": 0,\n"
                      + "  \"dentistOpPoints\": 0,\n"
                      + "  \"edate\": \""
                      + formatedDayE
                      + "\",\n"
                      + "  \"fundPoints\": 0,\n"
                      + "  \"hemodialysisPoints\": 0,\n"
                      + "  \"id\": 1,\n"
                      + "  \"sdate\": \""
                      + formatedDayS
                      + "\",\n"
                      + "  \"wmDrugPoints\": 0,\n"
                      + "  \"wmIpPoints\": 0,\n"
                      + "  \"wmOpPoints\": 0\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        assertAll(() -> assertEquals(10, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("3 - 新增參數值")
    void LOG_ACTION_C_ParameterController_3_新增參數值() {

      // === 參數 ===

      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      String tableNameToLog = "log_action";
      String edate = map.get("eDate");
      String spr = UUID.randomUUID().toString().substring(0, 4);
      String sdate = map.get("sDate");
      String s2 = "1234";

      String parameter = "?edate=" + edate + "&name=" + spr + "&sdate=" + sdate + "&value=" + s2;

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增參數值")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/value")
              .addPathVariable("")
              .addUriParam(parameter)
              .addJsonBody("")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    void LOG_ACTION_C_ParameterController_4_新增DRG參數值() {

      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      String tableNameToLog = "log_action";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增DRG參數值")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/drg")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"add15Child2y\": 9,\n"
                      + "  \"add15Child6m\": 23,\n"
                      + "  \"add15Child6y\": 10,\n"
                      + "  \"addHospLevel1\": 7.1,\n"
                      + "  \"addHospLevel2\": 6.1,\n"
                      + "  \"addHospLevel3\": 5,\n"
                      + "  \"addN15MChild2y\": 23,\n"
                      + "  \"addN15MChild6m\": 91,\n"
                      + "  \"addN15MChild6y\": 15,\n"
                      + "  \"addN15PChild2y\": 21,\n"
                      + "  \"addN15PChild6m\": 66,\n"
                      + "  \"addN15PChild6y\": 10,\n"
                      + "  \"cmi\": 0,\n"
                      + "  \"cmi12\": 1.1,\n"
                      + "  \"cmi13\": 1.2,\n"
                      + "  \"cmi14\": 1.3,\n"
                      + "  \"endDate\": \""
                      + map.get("eDate")
                      + "\",\n"
                      + "  \"id\": 320,\n"
                      + "  \"message\": \"錯誤訊息\",\n"
                      + "  \"outlyingIslands\": 2,\n"
                      + "  \"result\": \"success\",\n"
                      + "  \"spr\": 45837,\n"
                      + "  \"startDate\": \""
                      + map.get("sDate")
                      + "\",\n"
                      + "  \"status\": \"未啟動\"\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        assertAll(() -> assertEquals(18, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    void LOG_ACTION_C_ParameterController_5_新增罕見ICD代碼() {
      String descChi = "Junit測試-其他確認流感病毒所致流行性感冒併其他呼吸道表徵";
      String tableNameToLog = "log_action";

      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      // test target
      Builder builder = new ProfitClientBuilder();

      ApiClient profitClient =
          builder
              .setApiName("新增罕見ICD代碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/rareICD")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"both\": true,\n"
                      + "  \"code\": \"J10.01\",\n"
                      + "  \"edate\": \""
                      + map.get("eDate")
                      + "\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"ip\": true,\n"
                      + "  \"ipTimes6M\": 100,\n"
                      + "  \"ipTimes6MStatus\": true,\n"
                      + "  \"ipTimesM\": 100,\n"
                      + "  \"ipTimesMStatus\": true,\n"
                      + "  \"name\": \""
                      + descChi
                      + "\",\n"
                      + "  \"op\": true,\n"
                      + "  \"opTimes6M\": 100,\n"
                      + "  \"opTimes6MStatus\": true,\n"
                      + "  \"opTimesM\": 100,\n"
                      + "  \"opTimesMStatus\": true,\n"
                      + "  \"sdate\": \""
                      + map.get("sDate")
                      + "\",\n"
                      + "  \"status\": true\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        // Recovery 資料庫
        code_threshold_rollback(descChi);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    void LOG_ACTION_C_ParameterController_6_新增應用比例偏高醫令_特別用量藥品_衛材() {
      String descChi = "Junit測試-其他確認流感病毒所致流行性感冒併其他呼吸道表徵";
      String tableNameToLog = "log_action";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增應用比例偏高醫令_特別用量藥品_衛材")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/highRatioOrder")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"both\": true,\n"
                      + "  \"code\": \"J10.01\",\n"
                      + "  \"codeType\": 2,\n"
                      + "  \"edate\": \"2022/06/30\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"inhCode\": \"string\",\n"
                      + "  \"inhName\": \"string\",\n"
                      + "  \"ip\": true,\n"
                      + "  \"ipTimes\": 10,\n"
                      + "  \"ipTimes6M\": 100,\n"
                      + "  \"ipTimes6MStatus\": true,\n"
                      + "  \"ipTimesD\": 100,\n"
                      + "  \"ipTimesDStatus\": true,\n"
                      + "  \"ipTimesDay\": 3,\n"
                      + "  \"ipTimesM\": 100,\n"
                      + "  \"ipTimesMStatus\": true,\n"
                      + "  \"ipTimesStatus\": true,\n"
                      + "  \"name\": \""
                      + descChi
                      + "\",\n"
                      + "  \"op\": true,\n"
                      + "  \"opTimes\": 100,\n"
                      + "  \"opTimes6M\": 100,\n"
                      + "  \"opTimes6MStatus\": true,\n"
                      + "  \"opTimesD\": 100,\n"
                      + "  \"opTimesDStatus\": true,\n"
                      + "  \"opTimesDay\": 100,\n"
                      + "  \"opTimesM\": 100,\n"
                      + "  \"opTimesMStatus\": true,\n"
                      + "  \"opTimesStatus\": true,\n"
                      + "  \"sdate\": \"2022/01/01\",\n"
                      + "  \"status\": true\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        // Recovery 資料庫

        code_threshold_rollback(descChi);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @RepeatedTest(3)
    @DisplayName("7 - 新增健保項目對應自費項目並存資料")
    void _07() {

      String tableNameToLog = "log_action";

      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      // test target
      Builder builder = new ProfitClientBuilder();
      String s = "2022/01/01";
      String s1 = "2022/06/30";
      ApiClient profitClient =
          builder
              .setApiName("新增健保項目對應自費項目並存資料")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/codeConflict")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \"J10.01\",\n"
                      + "  \"edate\": \""
                      + map.get("eDate")
                      + "\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"inhCode\": \"string\",\n"
                      + "  \"inhName\": \"string\",\n"
                      + "  \"ip\": true,\n"
                      + "  \"name\": \"string\",\n"
                      + "  \"op\": true,\n"
                      + "  \"ownCode\": true,\n"
                      + "  \"ownName\": \"string\",\n"
                      + "  \"quantityNh\": 0,\n"
                      + "  \"quantityOwn\": 0,\n"
                      + "  \"sdate\": \""
                      + map.get("sDate")
                      + "\",\n"
                      + "  \"status\": true\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_U_ParameterController")
  class LOG_ACTION_U_ParameterController {
    @Test
    @DisplayName("修改分配總點數設定")
    void _01() {

      String tableNameToLog = "log_action";

      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      String formatedDayS = map.get("sDate");
      String formatedDayE = map.get("eDate");

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("修改分配總點數設定")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/assignedPoints")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"dentistDrugPoints\": 0,\n"
                      + "  \"dentistFundPoints\": 0,\n"
                      + "  \"dentistOpPoints\": 0,\n"
                      + "  \"edate\": \""
                      + formatedDayE
                      + "\",\n"
                      + "  \"fundPoints\": 0,\n"
                      + "  \"hemodialysisPoints\": 0,\n"
                      + "  \"id\": 1,\n"
                      + "  \"sdate\": \""
                      + formatedDayS
                      + "\",\n"
                      + "  \"wmDrugPoints\": 0,\n"
                      + "  \"wmIpPoints\": 0,\n"
                      + "  \"wmOpPoints\": 0\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("修改支配總點數")
    void _02() {
      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      //      String formatedDayS = map.get("sDate");
      String formatedDayS = "2063/12/25";
      String formatedDayE = map.get("eDate");
      String tableNameToLog = "log_action";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("修改支配總點數")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/pointsValue")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"dentistDrugPoints\": 0,\n"
                      + "  \"dentistFundPoints\": 0,\n"
                      + "  \"dentistOpPoints\": 0,\n"
                      + "  \"edate\": \""
                      + formatedDayE
                      + "\",\n"
                      + "  \"fundPoints\": 0,\n"
                      + "  \"hemodialysisPoints\": 0,\n"
                      + "  \"id\": 1,\n"
                      + "  \"sdate\": \""
                      + formatedDayS
                      + "\",\n"
                      + "  \"wmDrugPoints\": 0,\n"
                      + "  \"wmIpPoints\": 0,\n"
                      + "  \"wmOpPoints\": 0\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("修改參數值")
    void _03() {

      // === 參數 ===

      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("修改參數值")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/value")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"edate\": \"2022/06/30\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"sdate\": \"2022/01/01\",\n"
                      + "  \"status\": \"string\",\n"
                      + "  \"value\": 10\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @RepeatedTest(3)
    @DisplayName("4 - 修改DRG參數值")
    void _04() {

      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      String tableNameToLog = "log_action";

      // test target
      Builder builder = new ProfitClientBuilder();
      String s = "2024/06/30";
      String s1 = "2024/01/01";
      ApiClient profitClient =
          builder
              .setApiName("修改DRG參數值")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/drg")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"add15Child2y\": 9,\n"
                      + "  \"add15Child6m\": 23,\n"
                      + "  \"add15Child6y\": 10,\n"
                      + "  \"addHospLevel1\": 7.1,\n"
                      + "  \"addHospLevel2\": 6.1,\n"
                      + "  \"addHospLevel3\": 5,\n"
                      + "  \"addN15MChild2y\": 23,\n"
                      + "  \"addN15MChild6m\": 91,\n"
                      + "  \"addN15MChild6y\": 15,\n"
                      + "  \"addN15PChild2y\": 21,\n"
                      + "  \"addN15PChild6m\": 66,\n"
                      + "  \"addN15PChild6y\": 10,\n"
                      + "  \"cmi\": 0,\n"
                      + "  \"cmi12\": 1.1,\n"
                      + "  \"cmi13\": 1.2,\n"
                      + "  \"cmi14\": 1.3,\n"
                      + "  \"endDate\": \""
                      + map.get("eDate")
                      + "\",\n"
                      + "  \"id\": 320,\n"
                      + "  \"message\": \"錯誤訊息\",\n"
                      + "  \"outlyingIslands\": 2,\n"
                      + "  \"result\": \"success or error\",\n"
                      + "  \"spr\": 45837,\n"
                      + "  \"startDate\": \""
                      + map.get("sDate")
                      + "\",\n"
                      + "  \"status\": \"string\"\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("修改法定傳染病啟用狀態")
    void _05() {
      String tableNameToLog = "log_action";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("修改法定傳染病啟用狀態")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/infectious")
              .addPathVariable("")
              .addUriParam("?enable=true&icd=J10.01")
              .addJsonBody("")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("修改罕見ICD代碼啟用狀態")
    void _06() {
      String tableNameToLog = "log_action";
      String descChi = "Junit測試-其他確認流感病毒所致流行性感冒併其他呼吸道表徵";
      //

      Map<String, String> map = getRandomStardAndEndDateMap();

      // test target
      Builder builder = new ProfitClientBuilder();

      String code = UUID.randomUUID().toString().substring(0, 5); // "J10.01";
      ApiClient profitClient =
          builder
              .setApiName("新增罕見ICD代碼 - ")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/rareICD")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"both\": true,\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"edate\": \""
                      + map.get("eDate")
                      + "\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"ip\": true,\n"
                      + "  \"ipTimes6M\": 100,\n"
                      + "  \"ipTimes6MStatus\": true,\n"
                      + "  \"ipTimesM\": 100,\n"
                      + "  \"ipTimesMStatus\": true,\n"
                      + "  \"name\": \""
                      + descChi
                      + "\",\n"
                      + "  \"op\": true,\n"
                      + "  \"opTimes6M\": 100,\n"
                      + "  \"opTimes6MStatus\": true,\n"
                      + "  \"opTimesM\": 100,\n"
                      + "  \"opTimesMStatus\": true,\n"
                      + "  \"sdate\": \""
                      + map.get("sDate")
                      + "\",\n"
                      + "  \"status\": true\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      String sqlDrg = "SELECT id  FROM code_threshold WHERE code='" + code + "'";

      int id = jdbcTemplate.queryForObject(sqlDrg, Integer.class);

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("修改罕見ICD代碼啟用狀態")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/rareICDStatus")
              .addPathVariable("")
              .addUriParam("?enable=true&id=" + id)
              .addJsonBody("")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("修改罕見ICD代碼參數")
    void _07() {

      String tableNameToLog = "log_action";

      String descChi = "Junit測試-其他確認流感病毒所致流行性感冒併其他呼吸道表徵";

      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      // creating threshold for test
      Builder builder = new ProfitClientBuilder();

      String code = UUID.randomUUID().toString().substring(0, 5); // "J10.01";
      ApiClient profitClient =
          builder
              .setApiName("新增罕見ICD代碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/rareICD")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"both\": true,\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"edate\": \""
                      + map.get("eDate")
                      + "\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"ip\": true,\n"
                      + "  \"ipTimes6M\": 100,\n"
                      + "  \"ipTimes6MStatus\": true,\n"
                      + "  \"ipTimesM\": 100,\n"
                      + "  \"ipTimesMStatus\": true,\n"
                      + "  \"name\": \""
                      + descChi
                      + "\",\n"
                      + "  \"op\": true,\n"
                      + "  \"opTimes6M\": 100,\n"
                      + "  \"opTimes6MStatus\": true,\n"
                      + "  \"opTimesM\": 100,\n"
                      + "  \"opTimesMStatus\": true,\n"
                      + "  \"sdate\": \""
                      + map.get("sDate")
                      + "\",\n"
                      + "  \"status\": true\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      String sqlDrg = "SELECT id  FROM  code_threshold WHERE code='" + code + "'";

      int id = jdbcTemplate.queryForObject(sqlDrg, Integer.class);

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("修改罕見ICD代碼參數")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/rareICD")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"both\": true,\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"edate\": \"2022/06/30\",\n"
                      + "  \"id\": "
                      + id
                      + ",\n"
                      + "  \"ip\": true,\n"
                      + "  \"ipTimes6M\": 100,\n"
                      + "  \"ipTimes6MStatus\": true,\n"
                      + "  \"ipTimesM\": 100,\n"
                      + "  \"ipTimesMStatus\": true,\n"
                      + "  \"name\": \"其他確認流感病毒所致流行性感冒併其他呼吸道表徵\",\n"
                      + "  \"op\": true,\n"
                      + "  \"opTimes6M\": 100,\n"
                      + "  \"opTimes6MStatus\": true,\n"
                      + "  \"opTimesM\": 100,\n"
                      + "  \"opTimesMStatus\": true,\n"
                      + "  \"sdate\": \"2022/01/01\",\n"
                      + "  \"status\": true\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("修改應用比例偏高醫令或特別用量藥材、衛品")
    void _08() {

      String depTable = "code_threshold";
      String tableNameToLog = "log_action";

      // test target
      Builder builder = new ProfitClientBuilder();
      String id = "229";
      ApiClient profitClient =
          builder
              .setApiName("修改應用比例偏高醫令或特別用量藥材、衛品")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/highRatioOrder")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"both\": true,\n"
                      + "  \"code\": \"J10.01\",\n"
                      + "  \"codeType\": 2,\n"
                      + "  \"edate\": \"2022/06/30\",\n"
                      + "  \"id\": "
                      + id
                      + ",\n"
                      + "  \"inhCode\": \"string\",\n"
                      + "  \"inhName\": \"string\",\n"
                      + "  \"ip\": true,\n"
                      + "  \"ipTimes\": 10,\n"
                      + "  \"ipTimes6M\": 100,\n"
                      + "  \"ipTimes6MStatus\": true,\n"
                      + "  \"ipTimesD\": 100,\n"
                      + "  \"ipTimesDStatus\": true,\n"
                      + "  \"ipTimesDay\": 3,\n"
                      + "  \"ipTimesM\": 100,\n"
                      + "  \"ipTimesMStatus\": true,\n"
                      + "  \"ipTimesStatus\": true,\n"
                      + "  \"name\": \"其他確認流感病毒所致流行性感冒併其他呼吸道表徵\",\n"
                      + "  \"op\": true,\n"
                      + "  \"opTimes\": 100,\n"
                      + "  \"opTimes6M\": 100,\n"
                      + "  \"opTimes6MStatus\": true,\n"
                      + "  \"opTimesD\": 100,\n"
                      + "  \"opTimesDStatus\": true,\n"
                      + "  \"opTimesDay\": 100,\n"
                      + "  \"opTimesM\": 100,\n"
                      + "  \"opTimesMStatus\": true,\n"
                      + "  \"opTimesStatus\": true,\n"
                      + "  \"sdate\": \"2022/01/01\",\n"
                      + "  \"status\": true\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("修改應用比例偏高醫令狀態")
    void _09() {

      String tableNameToLog = "log_action";
      String id = "229";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("修改應用比例偏高醫令狀態")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/highRatioOrder")
              .addPathVariable("/" + id)
              .addUriParam("?enable=true")
              .addJsonBody("")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("修改同性質藥物狀態")
    void _10() {

      String tableNameToLog = "log_action";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("修改同性質藥物狀態")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/sameATC")
              .addPathVariable("")
              .addUriParam("?enable=true&id=229")
              .addJsonBody("")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @RepeatedTest(3)
    @DisplayName("11 - 修改健保項目對應自費項目並存資料")
    void _11() {

      String tableNameToLog = "log_action";

      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      // creating for test

      String code = UUID.randomUUID().toString().substring(0, 5);

      Builder builder = new ProfitClientBuilder();
      String s3 = "J10.01";
      ApiClient profitClient =
          builder
              .setApiName("新增健保項目對應自費項目並存資料")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/codeConflict")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"edate\": \""
                      + map.get("eDate")
                      + "\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"inhCode\": \"string\",\n"
                      + "  \"inhName\": \"string\",\n"
                      + "  \"ip\": true,\n"
                      + "  \"name\": \"string\",\n"
                      + "  \"op\": true,\n"
                      + "  \"ownCode\": true,\n"
                      + "  \"ownName\": \"string\",\n"
                      + "  \"quantityNh\": 0,\n"
                      + "  \"quantityOwn\": 0,\n"
                      + "  \"sdate\": \""
                      + map.get("sDate")
                      + "\",\n"
                      + "  \"status\": true\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      String sqlDrg = "SELECT id  FROM code_conflict WHERE code='" + code + "'";

      int id = jdbcTemplate.queryForObject(sqlDrg, Integer.class);

      // test target
      Builder builder2 = new ProfitClientBuilder();
      String s = "2022/06/30";
      String s1 = "2022/01/01";
      String s2 = "1";
      ApiClient profitClient2 =
          builder2
              .setApiName("修改健保項目對應自費項目並存資料")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/codeConflict")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \"J10.01\",\n"
                      + "  \"edate\": \""
                      + map.get("eDate")
                      + "\",\n"
                      + "  \"id\": "
                      + id
                      + ",\n"
                      + "  \"inhCode\": \"string\",\n"
                      + "  \"inhName\": \"string\",\n"
                      + "  \"ip\": true,\n"
                      + "  \"name\": \"string\",\n"
                      + "  \"op\": true,\n"
                      + "  \"ownCode\": true,\n"
                      + "  \"ownName\": \"string\",\n"
                      + "  \"quantityNh\": 0,\n"
                      + "  \"quantityOwn\": 0,\n"
                      + "  \"sdate\": \""
                      + map.get("sDate")
                      + "\",\n"
                      + "  \"status\": true\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("12 - 修改健保項目對應自費項目並存資料狀態")
    void _12() {

      String tableNameToLog = "log_action";

      String deptable = "code_confict"; // 健保項目對應自費項目並存設定

      // creating for test
      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      String code = UUID.randomUUID().toString().substring(0, 5);

      // test target
      Builder builder = new ProfitClientBuilder();
      String s = "J10.01";
      ApiClient profitClient =
          builder
              .setApiName("新增健保項目對應自費項目並存資料")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/codeConflict")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"edate\": \""
                      + map.get("eDate")
                      + "\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"inhCode\": \"string\",\n"
                      + "  \"inhName\": \"string\",\n"
                      + "  \"ip\": true,\n"
                      + "  \"name\": \"string\",\n"
                      + "  \"op\": true,\n"
                      + "  \"ownCode\": true,\n"
                      + "  \"ownName\": \"string\",\n"
                      + "  \"quantityNh\": 0,\n"
                      + "  \"quantityOwn\": 0,\n"
                      + "  \"sdate\": \""
                      + map.get("sDate")
                      + "\",\n"
                      + "  \"status\": true\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      String sqlDrg = "SELECT id  FROM code_conflict WHERE code='" + code + "'";

      int id = jdbcTemplate.queryForObject(sqlDrg, Integer.class);

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("修改健保項目對應自費項目並存資料狀態")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/codeConflict")
              .addPathVariable("/" + id)
              .addUriParam("?enable=true")
              .addJsonBody("")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_D_ParameterController")
  class LOG_ACTION_D_ParameterController {
    @RepeatedTest(3)
    @DisplayName("刪除分配總點數")
    void _01() {

      String tableNameToLog = "log_action";

      // assigned_point
      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      // creating assigned——point for test
      int fundpoints = (int) (Math.random() * 100000 + 1);

      // test target
      Builder builder = new ProfitClientBuilder();
      String s = "2022/06/30";
      String s1 = "022/01/01";
      ApiClient profitClient =
          builder
              .setApiName("新增分配點數")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/assignedPoints")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"dentistDrugPoints\": 1000,\n"
                      + "  \"dentistFundPoints\": 1000,\n"
                      + "  \"dentistOpPoints\": 1000,\n"
                      + "  \"edate\": \""
                      + map.get("eDate")
                      + "\",\n"
                      + "  \"fundPoints\": "
                      + fundpoints
                      + ",\n"
                      + "  \"hemodialysisPoints\": 10000,\n"
                      + "  \"id\": 1,\n"
                      + "  \"sdate\": \""
                      + map.get("sDate")
                      + "\",\n"
                      + "  \"wmDrugPoints\": 10000,\n"
                      + "  \"wmIpPoints\": 100000,\n"
                      + "  \"wmOpPoints\": 100000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      String sqlDrg = "SELECT id  FROM  assigned_point WHERE fund_points =" + fundpoints;

      int id = jdbcTemplate.queryForObject(sqlDrg, Integer.class);
      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("刪除分配總點數")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/assignedPoints")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("刪除參數值")
    void _02() {

      String tableNameToLog = "log_action";

      // creating parameter for test
      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      String edate = map.get("eDate");
      String name = UUID.randomUUID().toString().substring(0, 8);
      String sdate = map.get("sDate");
      String s2 = "1234";

      String parameter = "?edate=" + edate + "&name=" + name + "&sdate=" + sdate + "&value=" + s2;

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增參數值")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/value")
              .addPathVariable("")
              .addUriParam(parameter)
              .addJsonBody("")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      String sqlDrg = "SELECT id  FROM parameters WHERE name='" + name + "'";

      int id = jdbcTemplate.queryForObject(sqlDrg, Integer.class);

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("刪除參數值")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/value")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("刪除罕見ICD資料")
    void _03() {

      String tableNameToLog = "log_action";

      // creating ico 罕見 for test
      String descChi = "Junit測試-其他確認流感病毒所致流行性感冒併其他呼吸道表徵";

      // 隨機生成日期
      Map<String, String> map = getRandomStardAndEndDateMap();

      // test target
      Builder builder = new ProfitClientBuilder();

      String code = UUID.randomUUID().toString().substring(0, 6);
      ApiClient profitClient =
          builder
              .setApiName("新增罕見ICD代碼")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/rareICD")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"both\": true,\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"edate\": \""
                      + map.get("eDate")
                      + "\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"ip\": true,\n"
                      + "  \"ipTimes6M\": 100,\n"
                      + "  \"ipTimes6MStatus\": true,\n"
                      + "  \"ipTimesM\": 100,\n"
                      + "  \"ipTimesMStatus\": true,\n"
                      + "  \"name\": \""
                      + descChi
                      + "\",\n"
                      + "  \"op\": true,\n"
                      + "  \"opTimes6M\": 100,\n"
                      + "  \"opTimes6MStatus\": true,\n"
                      + "  \"opTimesM\": 100,\n"
                      + "  \"opTimesMStatus\": true,\n"
                      + "  \"sdate\": \""
                      + map.get("sDate")
                      + "\",\n"
                      + "  \"status\": true\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      String sqlDrg = "SELECT id  FROM code_threshold WHERE code='" + code + "'";

      int id = jdbcTemplate.queryForObject(sqlDrg, Integer.class);

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("刪除罕見ICD資料")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/rareICD")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("刪除應用比例偏高醫令")
    void _04() {

      String tableNameToLog = "log_action";

      // creating for test
      String descChi = "Junit測試-其他確認流感病毒所致流行性感冒併其他呼吸道表徵";

      // test target
      Builder builder = new ProfitClientBuilder();
      String code = UUID.randomUUID().toString().substring(0, 6);
      ApiClient profitClient =
          builder
              .setApiName("新增應用比例偏高醫令_特別用量藥品_衛材")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/highRatioOrder")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"both\": true,\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"codeType\": 2,\n"
                      + "  \"edate\": \"2022/06/30\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"inhCode\": \"string\",\n"
                      + "  \"inhName\": \"string\",\n"
                      + "  \"ip\": true,\n"
                      + "  \"ipTimes\": 10,\n"
                      + "  \"ipTimes6M\": 100,\n"
                      + "  \"ipTimes6MStatus\": true,\n"
                      + "  \"ipTimesD\": 100,\n"
                      + "  \"ipTimesDStatus\": true,\n"
                      + "  \"ipTimesDay\": 3,\n"
                      + "  \"ipTimesM\": 100,\n"
                      + "  \"ipTimesMStatus\": true,\n"
                      + "  \"ipTimesStatus\": true,\n"
                      + "  \"name\": \""
                      + descChi
                      + "\",\n"
                      + "  \"op\": true,\n"
                      + "  \"opTimes\": 100,\n"
                      + "  \"opTimes6M\": 100,\n"
                      + "  \"opTimes6MStatus\": true,\n"
                      + "  \"opTimesD\": 100,\n"
                      + "  \"opTimesDStatus\": true,\n"
                      + "  \"opTimesDay\": 100,\n"
                      + "  \"opTimesM\": 100,\n"
                      + "  \"opTimesMStatus\": true,\n"
                      + "  \"opTimesStatus\": true,\n"
                      + "  \"sdate\": \"2022/01/01\",\n"
                      + "  \"status\": true\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      String sqlDrg = "SELECT id  FROM code_threshold WHERE code='" + code + "'";

      int id = jdbcTemplate.queryForObject(sqlDrg, Integer.class);

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("刪除應用比例偏高醫令")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/highRatioOrder")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);

        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("刪除健保項目對應自費項目並存資料")
    void _05() {
      String tableNameToLog = "log_action";

      // creating for test

      // test target
      Builder builder = new ProfitClientBuilder();
      String code = UUID.randomUUID().toString().substring(0, 6);
      ApiClient profitClient =
          builder
              .setApiName("新增健保項目對應自費項目並存資料")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/codeConflict")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \""
                      + code
                      + "\",\n"
                      + "  \"edate\": \"2022/06/30\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"inhCode\": \"string\",\n"
                      + "  \"inhName\": \"string\",\n"
                      + "  \"ip\": true,\n"
                      + "  \"name\": \"string\",\n"
                      + "  \"op\": true,\n"
                      + "  \"ownCode\": true,\n"
                      + "  \"ownName\": \"string\",\n"
                      + "  \"quantityNh\": 0,\n"
                      + "  \"quantityOwn\": 0,\n"
                      + "  \"sdate\": \"2022/01/01\",\n"
                      + "  \"status\": true\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      String sqlDrg = "SELECT id  FROM code_conflict WHERE code='" + code + "'";

      int id = jdbcTemplate.queryForObject(sqlDrg, Integer.class);

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("刪除健保項目對應自費項目並存資料")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/p/codeConflict")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  // ---

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_C_NHIWidgetXMLController")
  class LOG_ACTION_C_NHIWidgetXMLController {
    @Test
    @DisplayName("新增病歷資訊備註")
    void LOG_ACTION_C_NHIWidgetXMLController_1_新增病歷資訊備註() {

      String tableNameToLog = "log_action";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增病歷資訊備註")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/nhixml/note")
              .addPathVariable("")
              .addUriParam("?id=13220&isNote=true&note=新增病歷資訊備註內容")
              .addJsonBody(
                  "{\n"
                      + "  \"code\": \"J10.01\",\n"
                      + "  \"edate\": \"2022/06/30\",\n"
                      + "  \"id\": 1,\n"
                      + "  \"inhCode\": \"string\",\n"
                      + "  \"inhName\": \"string\",\n"
                      + "  \"ip\": true,\n"
                      + "  \"name\": \"string\",\n"
                      + "  \"op\": true,\n"
                      + "  \"ownCode\": true,\n"
                      + "  \"ownName\": \"string\",\n"
                      + "  \"quantityNh\": 0,\n"
                      + "  \"quantityOwn\": 0,\n"
                      + "  \"sdate\": \"2022/01/01\",\n"
                      + "  \"status\": true\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("新增病歷核刪註記：問題點！ - ")
    void _02() {

      // 參數
      String tableNameToLog = "log_action";

      // 調用目標 API
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("新增病歷核刪註記")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/nhixml/deductedNote")
              .addPathVariable("")
              .addUriParam("?mrId=2837")
              .addJsonBody(
                  "{\"afrAmount\":null,\"afrNoPayCode\":\"\",\"afrNoPayDesc\":\"\",\"afrNote\":\"\",\"afrPayAmount\":null,\"afrPayQuantity\":null,\"afrQuantity\":null,\"cat\":\"有CIS\",\"code\":\"\",\"deductedAmount\":null,\"deductedDate\":\"2022-09-22\",\"deductedOrder\":\"\",\"deductedQuantity\":null,\"disputeAmount\":null,\"disputeNoPayCode\":\"\",\"disputeNoPayDesc\":\"\",\"disputeNote\":\"\",\"disputePayAmount\":null,\"disputePayQuantity\":null,\"disputeQuantity\":null,\"id\":0,\"item\":\"非專案\",\"l1\":\"專業審查不予支付代碼\",\"l2\":\"\",\"l3\":\"\",\"note\":\"\",\"reason\":\"\",\"rollbackM\":null,\"rollbackQ\":null,\"subCat\":\"\"}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName(
        "新增多筆病歷核刪註記")
    void LOG_ACTION_C_NHIWidgetXMLController_3_新增多筆病歷核刪註記() {

      // 參數
      String tableNameToLog = "log_action";
      String mrid = "2755";

      // 調用目標 API
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/nhixml/deductedNotes")
              .addPathVariable("")
              .addUriParam("?mrId=" + mrid)
              .addJsonListBody(
                  "[\n"
                      + "  {\n"
                      + "    \"afrAmount\": null,\n"
                      + "    \"afrNoPayCode\": \"\",\n"
                      + "    \"afrNoPayDesc\": \"\",\n"
                      + "    \"afrNote\": \"\",\n"
                      + "    \"afrPayAmount\": null,\n"
                      + "    \"afrPayQuantity\": null,\n"
                      + "    \"afrQuantity\": null,\n"
                      + "    \"cat\": \"立意\",\n"
                      + "    \"code\": \"0101C\",\n"
                      + "    \"deductedAmount\": \"100\",\n"
                      + "    \"deductedDate\": \"2022-09-27\",\n"
                      + "    \"deductedOrder\": \"117\",\n"
                      + "    \"deductedQuantity\": \"1\",\n"
                      + "    \"disputeAmount\": null,\n"
                      + "    \"disputeNoPayCode\": \"\",\n"
                      + "    \"disputeNoPayDesc\": \"\",\n"
                      + "    \"disputeNote\": \"\",\n"
                      + "    \"disputePayAmount\": null,\n"
                      + "    \"disputePayQuantity\": null,\n"
                      + "    \"disputeQuantity\": null,\n"
                      + "    \"id\": 0,\n"
                      + "    \"item\": \"藥費\",\n"
                      + "    \"l1\": \"專業審查不予支付代碼\",\n"
                      + "    \"l2\": \"中醫\",\n"
                      + "    \"l3\": \"\",\n"
                      + "    \"note\": \"\",\n"
                      + "    \"reason\": \"因為 C ...\",\n"
                      + "    \"rollbackM\": null,\n"
                      + "    \"rollbackQ\": null,\n"
                      + "    \"subCat\": \"\"\n"
                      + "  },\n"
                      + "  {\n"
                      + "    \"afrAmount\": null,\n"
                      + "    \"afrNoPayCode\": \"\",\n"
                      + "    \"afrNoPayDesc\": \"\",\n"
                      + "    \"afrNote\": \"\",\n"
                      + "    \"afrPayAmount\": null,\n"
                      + "    \"afrPayQuantity\": null,\n"
                      + "    \"afrQuantity\": null,\n"
                      + "    \"cat\": \"立意\",\n"
                      + "    \"code\": \"0101C\",\n"
                      + "    \"deductedAmount\": \"100\",\n"
                      + "    \"deductedDate\": \"2022-09-28\",\n"
                      + "    \"deductedOrder\": \"118\",\n"
                      + "    \"deductedQuantity\": \"1\",\n"
                      + "    \"disputeAmount\": null,\n"
                      + "    \"disputeNoPayCode\": \"\",\n"
                      + "    \"disputeNoPayDesc\": \"\",\n"
                      + "    \"disputeNote\": \"\",\n"
                      + "    \"disputePayAmount\": null,\n"
                      + "    \"disputePayQuantity\": null,\n"
                      + "    \"disputeQuantity\": null,\n"
                      + "    \"id\": 0,\n"
                      + "    \"item\": \"藥費\",\n"
                      + "    \"l1\": \"專業審查不予支付代碼\",\n"
                      + "    \"l2\": \"中醫\",\n"
                      + "    \"l3\": \"\",\n"
                      + "    \"note\": \"\",\n"
                      + "    \"reason\": \"因為 D ...\",\n"
                      + "    \"rollbackM\": null,\n"
                      + "    \"rollbackQ\": null,\n"
                      + "    \"subCat\": \"\"\n"
                      + "  }\n"
                      + "]")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_U_NHIWidgetXMLController")
  class LOG_ACTION_U_NHIWidgetXMLController {
    @Test
    @DisplayName(
        "更新指定病歷")
    void _01() {

      String tableNameToLog = "log_action";
      String id = "2837";

      // 2551

      // 取得 action id （開始編輯指定病歷 API）
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("開始編輯指定病歷 - 給測試用")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/nhixml/mr")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(EditMRPayload.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<EditMRPayload> response = (ResponseEntity<EditMRPayload>) profitClient.call();
      profitClient.showResult();

      Integer actionId = response.getBody().getActionId();

      // test target
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("更新指定病歷")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/nhixml/mr")
              .addPathVariable("/" + id)
              .addUriParam("?actionId=" + actionId)
              .addJsonBody(
                  "{\n"
                      + "  \"hint\": [],\n"
                      + "  \"agencyId\": \"\",\n"
                      + "  \"aminDot\": \"\",\n"
                      + "  \"aneDot\": \"\",\n"
                      + "  \"applCauseMark\": \"\",\n"
                      + "  \"applDot\": \"\",\n"
                      + "  \"applEDate\": \"\",\n"
                      + "  \"applId\": \"leadtek\",\n"
                      + "  \"applName\": \"leadtek\",\n"
                      + "  \"applSDate\": \"\",\n"
                      + "  \"assistPartDot\": \"\",\n"
                      + "  \"babyDot\": \"\",\n"
                      + "  \"birthday\": \"1936/06/08\",\n"
                      + "  \"blodDot\": \"\",\n"
                      + "  \"cardSeqNo\": \"\",\n"
                      + "  \"careMark\": \"\",\n"
                      + "  \"caseDrgCode\": \"\",\n"
                      + "  \"casePayCode\": \"\",\n"
                      + "  \"caseType\": \"\",\n"
                      + "  \"changeICD\": 0,\n"
                      + "  \"changeORDER\": 0,\n"
                      + "  \"changeOther\": 1,\n"
                      + "  \"childMark\": \"\",\n"
                      + "  \"chrDays\": \"\",\n"
                      + "  \"cureItems\": [],\n"
                      + "  \"dId\": 144,\n"
                      + "  \"dataFormat\": \"10\",\n"
                      + "  \"deductedDot\": 0,\n"
                      + "  \"diagDot\": 0,\n"
                      + "  \"drgCode\": \"\",\n"
                      + "  \"drgFixed\": \"\",\n"
                      + "  \"drgSection\": \"\",\n"
                      + "  \"drugDay\": 0,\n"
                      + "  \"drugDot\": 0,\n"
                      + "  \"dsvcDot\": 0,\n"
                      + "  \"dsvcNo\": \"\",\n"
                      + "  \"eBedDay\": \"\",\n"
                      + "  \"ebAppl30Dot\": \"\",\n"
                      + "  \"ebAppl60Dot\": \"\",\n"
                      + "  \"ebAppl61Dot\": \"\",\n"
                      + "  \"ebPart30Dot\": \"\",\n"
                      + "  \"ebPart60Dot\": \"\",\n"
                      + "  \"ebPart61Dot\": \"\",\n"
                      + "  \"funcType\": \"22-急診醫學科\",\n"
                      + "  \"funcDate\": \"2022/01/29\",\n"
                      + "  \"funcEndDate\": \"\",\n"
                      + "  \"hdDot\": \"\",\n"
                      + "  \"hospId\": \"\",\n"
                      + "  \"icdCM\": [\n"
                      + "    {\n"
                      + "      \"code\": \"S70.01X.A\",\n"
                      + "      \"updateAt\": null\n"
                      + "    },\n"
                      + "    {\n"
                      + "      \"code\": \"W19.XXX.A\",\n"
                      + "      \"updateAt\": null\n"
                      + "    }\n"
                      + "  ],\n"
                      + "  \"icdOP\": [],\n"
                      + "  \"id\": 2837,\n"
                      + "  \"inDate\": \"\",\n"
                      + "  \"infectious\": 0,\n"
                      + "  \"inhClinicId\": \"\",\n"
                      + "  \"inhMrId\": \"\",\n"
                      + "  \"injtDot\": \"\",\n"
                      + "  \"mealDot\": \"\",\n"
                      + "  \"medDot\": \"\",\n"
                      + "  \"medType\": \"\",\n"
                      + "  \"metrDot\": 0,\n"
                      + "  \"mos\": [\n"
                      + "    {\n"
                      + "      \"orderSeqNo\": 1,\n"
                      + "      \"drugNo\": \"00203B\",\n"
                      + "      \"drugNoCode\": \"00203B\",\n"
                      + "      \"totalQ\": 1,\n"
                      + "      \"unitP\": 606,\n"
                      + "      \"totalDot\": 727,\n"
                      + "      \"payBy\": \"N\",\n"
                      + "      \"applStatus\": 1,\n"
                      + "      \"drugSerialNo\": \"\\n        \",\n"
                      + "      \"payRate\": \"120.00\"\n"
                      + "    },\n"
                      + "    {\n"
                      + "      \"orderSeqNo\": 2,\n"
                      + "      \"drugNo\": \"ER\",\n"
                      + "      \"drugNoCode\": \"ER\",\n"
                      + "      \"totalQ\": null,\n"
                      + "      \"unitP\": null,\n"
                      + "      \"totalDot\": null,\n"
                      + "      \"payBy\": \"N\",\n"
                      + "      \"applStatus\": 1,\n"
                      + "      \"payRate\": \" \",\n"
                      + "      \"startTime\": \"110/01/01 17:07\",\n"
                      + "      \"endTime\": \"110/01/01 18:18\"\n"
                      + "    }\n"
                      + "  ],\n"
                      + "  \"_mosCount\": 2,\n"
                      + "  \"mrCheckReason\": \"\",\n"
                      + "  \"mrDate\": \"2022/01/29\",\n"
                      + "  \"name\": \"陳*玉\",\n"
                      + "  \"nbBirthday\": \"\",\n"
                      + "  \"nonApplDot\": \"\",\n"
                      + "  \"notify\": 0,\n"
                      + "  \"nrtpDot\": \"\",\n"
                      + "  \"objective\": \"\",\n"
                      + "  \"oriCardSeqNo\": \"\",\n"
                      + "  \"outDate\": \"\",\n"
                      + "  \"outSvcPlanCode\": \"\",\n"
                      + "  \"ownExpense\": 0,\n"
                      + "  \"partDot\": 0,\n"
                      + "  \"partNo\": \"\",\n"
                      + "  \"patTranOut\": \"\",\n"
                      + "  \"patientSource\": \"\",\n"
                      + "  \"payType\": \"4-普通疾病\",\n"
                      + "  \"pharID\": \"\",\n"
                      + "  \"phscDot\": \"\",\n"
                      + "  \"pilotProject\": \"\",\n"
                      + "  \"prsnId\": \"D01\",\n"
                      + "  \"prsnName\": \"\",\n"
                      + "  \"radoDot\": \"\",\n"
                      + "  \"readed\": 0,\n"
                      + "  \"remark\": \"\",\n"
                      + "  \"rocId\": \"H210******\",\n"
                      + "  \"roomDot\": \"\",\n"
                      + "  \"sBedDay\": \"\",\n"
                      + "  \"sbAppl30Dot\": \"\",\n"
                      + "  \"sbAppl90Dot\": \"\",\n"
                      + "  \"sbAppl180Dot\": \"\",\n"
                      + "  \"sbAppl181Dot\": \"\",\n"
                      + "  \"sbPart30Dot\": \"\",\n"
                      + "  \"sbPart90Dot\": \"\",\n"
                      + "  \"sbPart180Dot\": \"\",\n"
                      + "  \"sbPart181Dot\": \"\",\n"
                      + "  \"seqNo\": \"1\",\n"
                      + "  \"sgryDot\": \"\",\n"
                      + "  \"shareHospId\": \"\",\n"
                      + "  \"shareMark\": \"\",\n"
                      + "  \"speAreaSvc\": \"\",\n"
                      + "  \"status\": -2,\n"
                      + "  \"subjective\": \"\",\n"
                      + "  \"supportArea\": \"\",\n"
                      + "  \"svcPlan\": \"\",\n"
                      + "  \"tApplDot\": 0,\n"
                      + "  \"tDot\": 427,\n"
                      + "  \"thrpDot\": \"\",\n"
                      + "  \"totalDot\": 427,\n"
                      + "  \"tranCode\": \"\",\n"
                      + "  \"tranInHospId\": \"\",\n"
                      + "  \"tranOutHospId\": \"\",\n"
                      + "  \"treatCode\": \"\",\n"
                      + "  \"treatDot\": 0,\n"
                      + "  \"twDrgCode\": \"\",\n"
                      + "  \"twDrgPayType\": \"\",\n"
                      + "  \"twDrgsSuitMark\": \"\",\n"
                      + "  \"updateAt\": \"2022/09/22\",\n"
                      + "  \"mrEndDate\": \"2022/01/29\",\n"
                      + "  \"changeInh\": 0,\n"
                      + "  \"changeSo\": 0,\n"
                      + "  \"changeOrder\": 0,\n"
                      + "  \"notes\": [\n"
                      + "    {\n"
                      + "      \"id\": 204,\n"
                      + "      \"actionType\": \"新增\",\n"
                      + "      \"note\": \"一切正常\",\n"
                      + "      \"editor\": \"leadtek\",\n"
                      + "      \"updateAt\": \"2022/09/22 11:41:16\"\n"
                      + "    },\n"
                      + "    {\n"
                      + "      \"id\": 205,\n"
                      + "      \"actionType\": \"新增\",\n"
                      + "      \"note\": \"非常好\",\n"
                      + "      \"editor\": \"leadtek\",\n"
                      + "      \"updateAt\": \"2022/09/22 11:47:33\"\n"
                      + "    },\n"
                      + "    {\n"
                      + "      \"id\": 208,\n"
                      + "      \"actionType\": \"新增\",\n"
                      + "      \"note\": \"優秀\",\n"
                      + "      \"editor\": \"leadtek\",\n"
                      + "      \"updateAt\": \"2022/09/22 12:20:39\"\n"
                      + "    }\n"
                      + "  ],\n"
                      + "  \"deducted\": [\n"
                      + "    {\n"
                      + "      \"id\": 2,\n"
                      + "      \"cat\": \"有CIS\",\n"
                      + "      \"item\": \"非專案\",\n"
                      + "      \"l1\": \"程序審查核減代碼\",\n"
                      + "      \"l2\": \"支付標準醫令錯誤代碼\",\n"
                      + "      \"code\": \"C1\",\n"
                      + "      \"deductedOrder\": \"1234\",\n"
                      + "      \"deductedQuantity\": 1,\n"
                      + "      \"deductedAmount\": 10,\n"
                      + "      \"reason\": \"dd\",\n"
                      + "      \"editor\": \"leadtek\",\n"
                      + "      \"updateAt\": \"2022/09/14 14:24:09\",\n"
                      + "      \"record\": \"新增核刪代碼C1\",\n"
                      + "      \"modifyStatus\": \"新增\",\n"
                      + "      \"deductedDate\": \"2022-09-14\"\n"
                      + "    },\n"
                      + "    {\n"
                      + "      \"id\": 34,\n"
                      + "      \"cat\": \"有CIS\",\n"
                      + "      \"item\": \"非專案\",\n"
                      + "      \"l1\": \"專業審查不予支付代碼\",\n"
                      + "      \"code\": \"\",\n"
                      + "      \"deductedOrder\": null,\n"
                      + "      \"editor\": \"leadtek\",\n"
                      + "      \"updateAt\": \"2022/09/22 12:53:02\",\n"
                      + "      \"record\": \"新增核刪代碼\",\n"
                      + "      \"modifyStatus\": \"新增\",\n"
                      + "      \"deductedDate\": \"2022-09-22\"\n"
                      + "    },\n"
                      + "    {\n"
                      + "      \"id\": 36,\n"
                      + "      \"cat\": \"有CIS\",\n"
                      + "      \"item\": \"非專案\",\n"
                      + "      \"l1\": \"專業審查不予支付代碼\",\n"
                      + "      \"code\": \"\",\n"
                      + "      \"deductedOrder\": null,\n"
                      + "      \"editor\": \"leadtek\",\n"
                      + "      \"updateAt\": \"2022/09/22 12:56:11\",\n"
                      + "      \"record\": \"新增核刪代碼\",\n"
                      + "      \"modifyStatus\": \"新增\",\n"
                      + "      \"deductedDate\": \"2022-09-22\"\n"
                      + "    },\n"
                      + "    {\n"
                      + "      \"id\": 38,\n"
                      + "      \"cat\": \"有CIS\",\n"
                      + "      \"item\": \"非專案\",\n"
                      + "      \"l1\": \"專業審查不予支付代碼\",\n"
                      + "      \"code\": \"\",\n"
                      + "      \"deductedOrder\": null,\n"
                      + "      \"editor\": \"leadtek\",\n"
                      + "      \"updateAt\": \"2022/09/22 13:18:52\",\n"
                      + "      \"record\": \"新增核刪代碼\",\n"
                      + "      \"modifyStatus\": \"新增\",\n"
                      + "      \"deductedDate\": \"2022-09-22\"\n"
                      + "    },\n"
                      + "    {\n"
                      + "      \"id\": 40,\n"
                      + "      \"cat\": \"有CIS\",\n"
                      + "      \"item\": \"非專案\",\n"
                      + "      \"l1\": \"專業審查不予支付代碼\",\n"
                      + "      \"code\": \"\",\n"
                      + "      \"deductedOrder\": null,\n"
                      + "      \"editor\": \"leadtek\",\n"
                      + "      \"updateAt\": \"2022/09/22 13:25:00\",\n"
                      + "      \"record\": \"新增核刪代碼\",\n"
                      + "      \"modifyStatus\": \"新增\",\n"
                      + "      \"deductedDate\": \"2022-09-22\"\n"
                      + "    },\n"
                      + "    {\n"
                      + "      \"id\": 63,\n"
                      + "      \"cat\": \"有CIS\",\n"
                      + "      \"item\": \"非專案\",\n"
                      + "      \"l1\": \"專業審查不予支付代碼\",\n"
                      + "      \"code\": \"\",\n"
                      + "      \"deductedOrder\": null,\n"
                      + "      \"editor\": \"leadtek\",\n"
                      + "      \"updateAt\": \"2022/09/27 12:02:49\",\n"
                      + "      \"record\": \"新增核刪代碼\",\n"
                      + "      \"modifyStatus\": \"新增\",\n"
                      + "      \"deductedDate\": \"2022-09-22\"\n"
                      + "    },\n"
                      + "    {\n"
                      + "      \"id\": 65,\n"
                      + "      \"cat\": \"有CIS\",\n"
                      + "      \"item\": \"非專案\",\n"
                      + "      \"l1\": \"專業審查不予支付代碼\",\n"
                      + "      \"code\": \"\",\n"
                      + "      \"deductedOrder\": null,\n"
                      + "      \"editor\": \"leadtek\",\n"
                      + "      \"updateAt\": \"2022/09/27 14:00:26\",\n"
                      + "      \"record\": \"新增核刪代碼\",\n"
                      + "      \"modifyStatus\": \"新增\",\n"
                      + "      \"deductedDate\": \"2022-09-22\"\n"
                      + "    }\n"
                      + "  ],\n"
                      + "  \"isLastEditor\": true,\n"
                      + "  \"diffFields\": [\n"
                      + "    \"totalDot\"\n"
                      + "  ],\n"
                      + "  \"isRaw\": false\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("zzzzz", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("修改病歷資訊備註/核刪註記")
    void _02() {

      // 參數
      String tableNameToLog = "log_action";

      // 調用目標 API
      Builder builder = new ProfitClientBuilder();
      String id = "2383";
      ApiClient profitClient =
          builder
              .setApiName("修改病歷資訊備註/核刪註記")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/nhixml/note")
              .addPathVariable("")
              .addUriParam("?id=" + id + "&isNote=true&note")
              .addJsonBody("")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("更新指定病歷的核刪註記內容")
    void _03() {

      // 參數
      String tableNameToLog = "log_action";

      // 調用目標 API
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("更新指定病歷的核刪註記內容")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/nhixml/deductedNote")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\"id\":2,\"cat\":\"有CIS\",\"item\":\"非專案\",\"l1\":\"程序審查核減代碼\",\"l2\":\"支付標準醫令錯誤代碼\",\"code\":\"C1\",\"deductedOrder\":\"1234\",\"deductedQuantity\":1,\"deductedAmount\":10,\"reason\":\"dd\",\"editor\":\"leadtek\",\"updateAt\":\"2022/09/14 14:24:09\",\"record\":\"新增核刪代碼C1\",\"modifyStatus\":\"新增\",\"deductedDate\":\"2022-09-14\"}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("更新指定病歷的核刪註記內容")
    void _04() {

      // 參數
      String tableNameToLog = "log_action";

      // 調用目標 API
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("更新指定病歷的核刪註記內容")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/nhixml/deductedNotes")
              .addPathVariable("")
              .addUriParam("")
              .addJsonListBody(
                  "[{\"id\":2,\"cat\":\"有CIS\",\"item\":\"非專案\",\"l1\":\"程序審查核減代碼\",\"l2\":\"支付標準醫令錯誤代碼\",\"code\":\"C1\",\"deductedOrder\":\"1234\",\"deductedQuantity\":1,\"deductedAmount\":10,\"reason\":\"dd\",\"editor\":\"leadtek\",\"updateAt\":\"2022/09/14 14:24:09\",\"record\":\"新增核刪代碼C1\",\"modifyStatus\":\"新增\",\"deductedDate\":\"2022-09-14\"}]")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("更新指定病歷的DRG")
    void _05() {

      // 參數
      String tableNameToLog = "log_action";
      String id = "2551";

      // 調用目標 API
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("更新指定病歷的DRG")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/nhixml/drglist")
              .addPathVariable("/" + id)
              .addUriParam("?icd=S72.001.A")
              .addJsonBody("")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(3, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_D_NHIWidgetXMLController")
  class LOG_ACTION_D_NHIWidgetXMLController {
    @Test
    @DisplayName("刪除病歷資訊備註")
    void _01() {

      String tableNameToLog = "log_action";
      String id = "";

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("刪除病歷資訊備註")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/nhixml/note")
              .addPathVariable("")
              .addUriParam("?id=13220&noteId=8")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("zzzzz", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("刪除核刪註記內容")
    void _02() {

      // 參數
      String tableNameToLog = "log_action";

      // 調用目標 API
      Builder builder = new ProfitClientBuilder();
      String id = "1";
      ApiClient profitClient =
          builder
              .setApiName("刪除核刪註記內容")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/nhixml/deductedNote")
              .addPathVariable("")
              .addUriParam("?noteId=1")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll(() -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  // -----

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_C_E2101aControll")
  class LOG_ACTION_C_E2101aControll {
    @Test
    @DisplayName("1 - /payment/outpatientfee/add - 10-1.05 門診診察費設定(add)")
    void _01() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/outpatientfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"住院診察費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_allow_plan\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_emergency\": 3,\n"
                      + "  \"max_emergency_enable\": 1,\n"
                      + "  \"max_inpatient\": 5,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"max_patient_no\": 10,\n"
                      + "  \"max_patient_no_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"no_coexist_enable\": 0,\n"
                      + "  \"not_allow_plan_enable\": 0,\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog;
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("2 - /payment/inpatientfee/add - 10-1.09 住院診察費設定(add)")
    void _02() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("payment/inpatientfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"住院診察費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_allow_plan\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_emergency\": 3,\n"
                      + "  \"max_emergency_enable\": 1,\n"
                      + "  \"max_inpatient\": 5,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"max_patient_no\": 10,\n"
                      + "  \"max_patient_no_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"no_coexist_enable\": 0,\n"
                      + "  \"not_allow_plan_enable\": 0,\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog;
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("3 - /payment/wardfee/add - 10-1.13 病房費設定(add)")
    void _03() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/wardfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"病房費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_stay\": 168,\n"
                      + "  \"max_stay_enable\": 1,\n"
                      + "  \"min_stay\": 48,\n"
                      + "  \"min_stay_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog;
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("4 - /payment/psychiatricwardfee/add - 10-1.17 精神慢性病房費(add)")
    void _04() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/psychiatricwardfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"精神慢性病房費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"need_pass_review_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog;
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("5 - /payment/psychiatricwardfee/add - 10-1.21 手術費設定(add)")
    void _05() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/surgeryfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"手術費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lim_age\": 65,\n"
                      + "  \"lim_age_enable\": 1,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog;
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_U_E2101aControll")
  class LOG_ACTION_U_E2101aControll {
    @ParameterizedTest
    @ValueSource(strings = {"0", "1"})
    @DisplayName("1 - 10-1.03 支付條件設定之狀態設定")
    void _01(String state) {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-1.03 支付條件設定之狀態設定")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/terms/setactive")
              .addPathVariable("/1")
              .addUriParam("?category=門診診察費&state=" + state)
              .addJsonBody("")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會有一筆 CRUD 註記為 U 的資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("2 - /payment/outpatientfee/{pt_id} - 10-1.06 門診診察費設定(update)")
    void _02() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setBaseUrl("http://localhost")
              .setApiName("10-1.06 門診診察費設定(update)")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/outpatientfee")
              .addPathVariable("/1")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"門診診察費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lim_age\": 65,\n"
                      + "  \"lim_age_enable\": 1,\n"
                      + "  \"lim_age_type\": 2,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lim_holiday\": 0,\n"
                      + "  \"lim_max\": 0,\n"
                      + "  \"lim_max_enable\": 1,\n"
                      + "  \"lim_out_islands\": 0,\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"no_chi_medicine\": 1,\n"
                      + "  \"no_dentisit\": 1,\n"
                      + "  \"no_service_charge\": 0,\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      System.out.println("response: " + response);
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName(
        "3 - /payment/inpatientfee/{pt_id} - 10-1.10 住院診察費設定(update) （issues：如果 category 和 id 所對應的 category 會拋 500 ）")
    void _03() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-1.10 住院診察費設定(update) ")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/inpatientfee")
              .addPathVariable("/5")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"住院診察費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_allow_plan\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_emergency\": 4,\n"
                      + "  \"max_emergency_enable\": 1,\n"
                      + "  \"max_inpatient\": 5,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"max_patient_no\": 10,\n"
                      + "  \"max_patient_no_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"no_coexist_enable\": 0,\n"
                      + "  \"not_allow_plan_enable\": 0,\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      System.out.println("response: " + response);
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("4 - 10-1.14 病房費設定(update)")
    void _04() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-1.14 病房費設定(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/wardfee")
              .addPathVariable("/194")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"病房費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_stay\": 168,\n"
                      + "  \"max_stay_enable\": 1,\n"
                      + "  \"min_stay\": 48,\n"
                      + "  \"min_stay_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800001\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("5 - /payment/psychiatricwardfee/{pt_id} - 10-1.18 精神慢性病房費(update)")
    void _05() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-1.18 精神慢性病房費(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/psychiatricwardfee")
              .addPathVariable("/12")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"精神慢性病房費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"need_pass_review_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      System.out.println("response: " + response);
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("6 - /payment/surgeryfee/{pt_id} - 10-1.22 手術費設定(update)")
    void _06() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-1.22 手術費設定(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/surgeryfee")
              .addPathVariable("/17")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"手術費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lim_age\": 65,\n"
                      + "  \"lim_age_enable\": 1,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      System.out.println("response: " + response);
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_D_E2101aControll")
  class LOG_ACTION_D_E2101aControll {
    @Test
    @DisplayName("1 - /payment/terms/setactive/{id} - 10-1.07 門診診察費設定(delete)")
    void _01() {
      String tableNameToLog = "log_action";

      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/outpatientfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"住院診察費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_allow_plan\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_emergency\": 3,\n"
                      + "  \"max_emergency_enable\": 1,\n"
                      + "  \"max_inpatient\": 5,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"max_patient_no\": 10,\n"
                      + "  \"max_patient_no_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"no_coexist_enable\": 0,\n"
                      + "  \"not_allow_plan_enable\": 0,\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();

      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();

      //
      String id = response2.getBody().getMessage().split("/id=")[1];

      // 測試項
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-1.07 門診診察費設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/outpatientfee/")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會有一筆 CRUD 註記為 U 的資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("2 - /payment/inpatientfee/{pt_id} - 10-1.11 住院診察費設定(delete)")
    void _02() {
      String tableNameToLog = "log_action";

      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("payment/inpatientfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"住院診察費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_allow_plan\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_emergency\": 3,\n"
                      + "  \"max_emergency_enable\": 1,\n"
                      + "  \"max_inpatient\": 5,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"max_patient_no\": 10,\n"
                      + "  \"max_patient_no_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"no_coexist_enable\": 0,\n"
                      + "  \"not_allow_plan_enable\": 0,\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      String id = response2.getBody().getMessage().split("/id=")[1];

      // 測試項
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-1.11 住院診察費設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/inpatientfee")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會有一筆 CRUD 註記為 U 的資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("3 - /payment/wardfee/{pt_id} - 10-1.15 病房費設定(delete)")
    void _03() {
      String tableNameToLog = "log_action";

      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/wardfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"病房費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_stay\": 168,\n"
                      + "  \"max_stay_enable\": 1,\n"
                      + "  \"min_stay\": 48,\n"
                      + "  \"min_stay_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      String id = response2.getBody().getMessage().split("/id=")[1];

      //
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-1.15 病房費設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/wardfee")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會有一筆 CRUD 註記為 U 的資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("4 - /payment/psychiatricwardfee/{pt_id}- 10-1.19 精神慢性病房費(delete)")
    void _04() {
      String tableNameToLog = "log_action";
      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/psychiatricwardfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"精神慢性病房費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"need_pass_review_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      String id = response2.getBody().getMessage().split("/id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-1.19 精神慢性病房費(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/psychiatricwardfee")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會有一筆 CRUD 註記為 U 的資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("4 - /payment/surgeryfee/{pt_id} - 10-1.23 手術費設定(delete)")
    void _05() {
      String tableNameToLog = "log_action";
      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/surgeryfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"手術費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lim_age\": 65,\n"
                      + "  \"lim_age_enable\": 1,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();

      String id = response2.getBody().getMessage().split("/id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-1.23 手術費設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/surgeryfee")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會有一筆 CRUD 註記為 U 的資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  // -----

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_C_E2101bControll")
  class LOG_ACTION_C_E2101bControll {
    @Test
    @DisplayName("1 - /payment/treatmentfee/add - 10-2.02 治療處置費設定(add)")
    void _01() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-2.02 治療處置費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/treatmentfee/add")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"治療處置費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"every_nday_days\": 3,\n"
                      + "  \"every_nday_enable\": 0,\n"
                      + "  \"every_nday_times\": 10,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"include_icd_no_enable\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_icd_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_age\": 0,\n"
                      + "  \"max_age_enable\": 1,\n"
                      + "  \"max_daily\": 1,\n"
                      + "  \"max_daily_enable\": 1,\n"
                      + "  \"max_inpatient\": 20,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"max_month_enable\": 1,\n"
                      + "  \"max_month_percentage\": 5,\n"
                      + "  \"max_patient\": 20,\n"
                      + "  \"max_patient_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"patient_nday_days\": 3,\n"
                      + "  \"patient_nday_enable\": 0,\n"
                      + "  \"patient_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("2 - /payment/tubefeedingfee/add - 10-2.06 管灌飲食費設定(add, 棄用!)")
    void _02() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-2.06 管灌飲食費設定(add, 棄用!)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/tubefeedingfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"管灌飲食費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("3 - /payment/nutritionalfee/add - 10-2.10 管灌飲食費及營養照護費設定(add)")
    void _03() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-2.10 管灌飲食費及營養照護費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/nutritionalfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"管灌飲食費及營養照護費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"every_nday_days\": 3,\n"
                      + "  \"every_nday_enable\": 0,\n"
                      + "  \"every_nday_times\": 10,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_daily\": 1,\n"
                      + "  \"max_daily_enable\": 1,\n"
                      + "  \"max_inpatient\": 20,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"over_nday_days\": 3,\n"
                      + "  \"over_nday_enable\": 0,\n"
                      + "  \"over_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("4 - /payment/adjustmentfee/add - 10-2.14 調劑費設定(add)")
    void _04() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-2.14 調劑費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/adjustmentfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"調劑費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("5 - /payment/medicinefee/add - 10-2.18 藥費設定(add)")
    void _05() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-2.18 藥費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/medicinefee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"藥費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"max_nday\": 3,\n"
                      + "  \"max_nday_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_U_E2101bControll")
  class LOG_ACTION_U_E2101bControll {
    @Test
    @DisplayName("1 - /payment/treatmentfee/{pt_id} - 10-2.03 治療處置費設定(update)")
    void _01() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-2.03 治療處置費設定(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/treatmentfee")
              .addPathVariable("/208")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"治療處置費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"every_nday_days\": 3,\n"
                      + "  \"every_nday_enable\": 0,\n"
                      + "  \"every_nday_times\": 10,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"include_icd_no_enable\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_icd_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_age\": 0,\n"
                      + "  \"max_age_enable\": 1,\n"
                      + "  \"max_daily\": 1,\n"
                      + "  \"max_daily_enable\": 1,\n"
                      + "  \"max_inpatient\": 20,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"max_month_enable\": 1,\n"
                      + "  \"max_month_percentage\": 5,\n"
                      + "  \"max_patient\": 20,\n"
                      + "  \"max_patient_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"patient_nday_days\": 3,\n"
                      + "  \"patient_nday_enable\": 0,\n"
                      + "  \"patient_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("2 - /payment/tubefeedingfee/{pt_id} - 10-2.07 管灌飲食費設定(update, 棄用!)")
    void _02() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-2.07 管灌飲食費設定(update, 棄用!)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/tubefeedingfee")
              .addPathVariable("/209")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"管灌飲食費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("3 - payment/nutritionalfee/{pt_id} - 10-2.11 管灌飲食費及營養照護費設定(update)")
    void _03() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-2.11 管灌飲食費及營養照護費設定(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/nutritionalfee")
              .addPathVariable("/210")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"管灌飲食費及營養照護費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"every_nday_days\": 3,\n"
                      + "  \"every_nday_enable\": 0,\n"
                      + "  \"every_nday_times\": 10,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_daily\": 1,\n"
                      + "  \"max_daily_enable\": 1,\n"
                      + "  \"max_inpatient\": 20,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"over_nday_days\": 3,\n"
                      + "  \"over_nday_enable\": 0,\n"
                      + "  \"over_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("4 - /payment/adjustmentfee/{pt_id} - 10-2.15 調劑費設定(update)")
    void _04() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("4 - 10-2.15 調劑費設定(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/adjustmentfee")
              .addPathVariable("/211")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"調劑費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("5 - /payment/medicinefee/{pt_id} - 10-2.19 藥費設定(update)")
    void _05() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-2.19 藥費設定(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/medicinefee")
              .addPathVariable("/212")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"藥費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"max_nday\": 3,\n"
                      + "  \"max_nday_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_U_E2101bControll")
  class LOG_ACTION_D_E2101bControll {
    @Test
    @DisplayName("1 - /payment/treatmentfee/{pt_id} - 10-2.04 治療處置費設定(delete)")
    void _01() {
      String tableNameToLog = "log_action";

      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("10-2.02 治療處置費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/treatmentfee/add")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"治療處置費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"every_nday_days\": 3,\n"
                      + "  \"every_nday_enable\": 0,\n"
                      + "  \"every_nday_times\": 10,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"include_icd_no_enable\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_icd_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_age\": 0,\n"
                      + "  \"max_age_enable\": 1,\n"
                      + "  \"max_daily\": 1,\n"
                      + "  \"max_daily_enable\": 1,\n"
                      + "  \"max_inpatient\": 20,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"max_month_enable\": 1,\n"
                      + "  \"max_month_percentage\": 5,\n"
                      + "  \"max_patient\": 20,\n"
                      + "  \"max_patient_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"patient_nday_days\": 3,\n"
                      + "  \"patient_nday_enable\": 0,\n"
                      + "  \"patient_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      String id = response2.getBody().getMessage().split("/id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-2.04 治療處置費設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/treatmentfee")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("2 - /payment/tubefeedingfee/{pt_id} - 10-2.08 管灌飲食費設定(delete, 棄用!)")
    void _02() {
      String tableNameToLog = "log_action";

      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("10-2.06 管灌飲食費設定(add, 棄用!)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/tubefeedingfee/add")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"管灌飲食費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();
      String id = response2.getBody().getMessage().split("/id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-2.08 管灌飲食費設定(delete, 棄用!)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/tubefeedingfee")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";

      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("3 - payment/nutritionalfee/{pt_id} - 10-2.12 管灌飲食費及營養照護費設定(delete)")
    void _03() {
      String tableNameToLog = "log_action";
      // get if for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("10-2.10 管灌飲食費及營養照護費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/nutritionalfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"管灌飲食費及營養照護費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"every_nday_days\": 3,\n"
                      + "  \"every_nday_enable\": 0,\n"
                      + "  \"every_nday_times\": 10,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_daily\": 1,\n"
                      + "  \"max_daily_enable\": 1,\n"
                      + "  \"max_inpatient\": 20,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"over_nday_days\": 3,\n"
                      + "  \"over_nday_enable\": 0,\n"
                      + "  \"over_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      String id = response2.getBody().getMessage().split("/id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-2.12 管灌飲食費及營養照護費設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("payment/nutritionalfee")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("4 - /payment/adjustmentfee/{pt_id} - 10-2.16 調劑費設定(delete)")
    void _04() {
      String tableNameToLog = "log_action";
      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("10-2.14 調劑費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/adjustmentfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"調劑費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();

      String id = response2.getBody().getMessage().split("/id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-2.16 調劑費設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/adjustmentfee")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("5 - 10-2.20 藥費設定(delete)")
    void _05() {
      String tableNameToLog = "log_action";
      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("10-2.18 藥費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/medicinefee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"藥費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"max_nday\": 3,\n"
                      + "  \"max_nday_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      String id = response2.getBody().getMessage().split("/id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-2.20 藥費設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/medicinefee")
              .addPathVariable("/" + id)
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  // -----

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_C_E2101cControll")
  class LOG_ACTION_C_E2101cControll {
    @Test
    @DisplayName("1 - /payment/radiationfee/add- 10-3.02 放射線診療費設定(add)")
    void _01() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-3.02 放射線診療費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/radiationfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"放射線診療費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_ntf_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_inpatient\": 5,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"notify_nhi_no_enable\": 0,\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("2 - /payment/injectionfee/add - 10-3.06 注射費設定(add)")
    void _02() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-3.06 注射費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/injectionfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"注射\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"interval_nday\": 10,\n"
                      + "  \"interval_nday_enable\": 1,\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_inpatient\": 16,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("3 - /payment/qualityservice/add - 10-3.10 品質支付服務設定(add)")
    void _03() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-3.10 品質支付服務設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/qualityservice/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"品質支付服務\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"every_nday_days\": 3,\n"
                      + "  \"every_nday_enable\": 0,\n"
                      + "  \"every_nday_times\": 10,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"interval_nday\": 10,\n"
                      + "  \"interval_nday_enable\": 1,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    {\n"
                      + "      \"nhi_no\": \"nhi-0001\",\n"
                      + "      \"times\": 3\n"
                      + "    }\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("4 - /payment/inpatientcare/add- 10-3.14 住院安寧療護(add)")
    void _04() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-3.14 住院安寧療護(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/inpatientcare/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"住院安寧療護\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("5 - /payment/rehabilitationfee/add - 10-3.18 復健治療費設定(add)")
    void _05() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-3.18 復健治療費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/rehabilitationfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"復健治療費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"include_icd_no_enable\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_icd_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"min_coexist\": 5,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"patient_nday_days\": 3,\n"
                      + "  \"patient_nday_enable\": 0,\n"
                      + "  \"patient_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_U_E2101cControll")
  class LOG_ACTION_U_E2101cControll {
    @Test
    @DisplayName("1 - /payment/radiationfee/{pt_id} - 10-3.03 放射線診療費設定(update)")
    void _01() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setBaseUrl("http://localhost")
              .setApiName("10-3.03 放射線診療費設定(update)")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/radiationfee")
              .addPathVariable("/203")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"放射線診療費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_ntf_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_inpatient\": 5,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"notify_nhi_no_enable\": 0,\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud ='U' ";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("2 - /payment/injectionfee/{pt_id} - 10-3.07 注射費設定(update)")
    void _02() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-3.07 注射費設定(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/injectionfee")
              .addPathVariable("/204")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"注射\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"interval_nday\": 10,\n"
                      + "  \"interval_nday_enable\": 1,\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_inpatient\": 16,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("3 - /payment/qualityservice/{pt_id} - 10-3.11 品質支付服務設定(update)")
    void _03() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-3.11 品質支付服務設定(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/qualityservice")
              .addPathVariable("/205")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"品質支付服務\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"every_nday_days\": 3,\n"
                      + "  \"every_nday_enable\": 0,\n"
                      + "  \"every_nday_times\": 10,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"interval_nday\": 10,\n"
                      + "  \"interval_nday_enable\": 1,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    {\n"
                      + "      \"nhi_no\": \"nhi-0001\",\n"
                      + "      \"times\": 3\n"
                      + "    }\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("4 - /payment/inpatientcare/{pt_id} - 10-3.19 復健治療費設定(update)")
    void _04() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-3.15 住院安寧療護(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/inpatientcare")
              .addPathVariable("/206")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"住院安寧療護\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("5 - /payment/rehabilitationfee/{pt_id} - 10-3.19 復健治療費設定(update)")
    void _05() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-3.19 復健治療費設定(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/rehabilitationfee")
              .addPathVariable("/207")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"復健治療費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"include_icd_no_enable\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_icd_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"min_coexist\": 5,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"patient_nday_days\": 3,\n"
                      + "  \"patient_nday_enable\": 0,\n"
                      + "  \"patient_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_U_E2101cControll")
  class LOG_ACTION_D_E2101cControll {
    @Test
    @DisplayName("1 - 10-3.04 放射線診療費設定(delete)")
    void _01() {
      String tableNameToLog = "log_action";
      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("10-3.02 放射線診療費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/radiationfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"放射線診療費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_ntf_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_inpatient\": 5,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"notify_nhi_no_enable\": 0,\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      String id = response2.getBody().getMessage().split("id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-3.04 放射線診療費設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/radiationfee")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud ='D' ";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("2 - 10-3.08 注射費設定(delete)")
    void _02() {
      String tableNameToLog = "log_action";

      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("10-3.06 注射費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/injectionfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"注射\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"interval_nday\": 10,\n"
                      + "  \"interval_nday_enable\": 1,\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_inpatient\": 16,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();
      String id = response2.getBody().getMessage().split("/id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-3.08 注射費設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/injectionfee")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("3 - 10-3.12 品質支付服務設定(delete)")
    void _03() {
      String tableNameToLog = "log_action";

      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("10-3.10 品質支付服務設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/qualityservice/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"品質支付服務\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"every_nday_days\": 3,\n"
                      + "  \"every_nday_enable\": 0,\n"
                      + "  \"every_nday_times\": 10,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"interval_nday\": 10,\n"
                      + "  \"interval_nday_enable\": 1,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    {\n"
                      + "      \"nhi_no\": \"nhi-0001\",\n"
                      + "      \"times\": 3\n"
                      + "    }\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      String id = response2.getBody().getMessage().split("/id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-3.11 品質支付服務設定(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/qualityservice")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("4 - 10-3.14 住院安寧療護(add)")
    void _04() {
      String tableNameToLog = "log_action";

      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("10-3.14 住院安寧療護(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/inpatientcare/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"住院安寧療護\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();

      String id = response2.getBody().getMessage().split("/id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-3.14 住院安寧療護(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/inpatientcare")
              .addPathVariable("/" + id)
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("5 - 10-3.20 復健治療費設定(delete)")
    void _05() {
      String tableNameToLog = "log_action";

      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("10-3.18 復健治療費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/rehabilitationfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"復健治療費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"include_icd_no_enable\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_icd_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"min_coexist\": 5,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"patient_nday_days\": 3,\n"
                      + "  \"patient_nday_enable\": 0,\n"
                      + "  \"patient_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      String id = response2.getBody().getMessage().split("id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-3.20 復健治療費設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("payment/rehabilitationfee")
              .addPathVariable("/" + id)
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();
      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  // ------

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_C_E2101dControll")
  class LOG_ACTION_C_E2101dControll {
    @Test
    @DisplayName("1 - /payment/psychiatricfee/add - 10-4.02 精神醫療治療費設定(add)")
    void _01() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.02 精神醫療治療費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/psychiatricfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"精神醫療治療費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_inpatient\": 16,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"patient_nday_days\": 3,\n"
                      + "  \"patient_nday_enable\": 0,\n"
                      + "  \"patient_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("2 - /payment/bonemarrowtransfee/add- 10-4.06 輸血及骨髓移植費設定(add)")
    void _02() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.06 輸血及骨髓移植費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/bonemarrowtransfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"輸血及骨髓移植費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_allow_plan\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"not_allow_plan_enable\": 0,\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("3 - /payment/plasterbandagefee/add - 10-4.10 石膏繃帶費設定(add)")
    void _03() {
      String tableNameToLog = "log_action";
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.10 石膏繃帶費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/plasterbandagefee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"石膏繃帶費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("4 - /payment/anesthesiafee/add - 10-4.14 麻醉費設定(add)")
    void _04() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.14 麻醉費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/anesthesiafee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"麻醉費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"include_drg_no_enable\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_drg_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"over_times_enable\": 0,\n"
                      + "  \"over_times_first_n\": 50,\n"
                      + "  \"over_times_n\": 2,\n"
                      + "  \"over_times_next_n\": 20,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("5 - /payment/specificmedicalfee/add - 10-4.18 特定診療檢查費設定(add)")
    void _05() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.18 特定診療檢查費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/specificmedicalfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"復健治療費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"include_icd_no_enable\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_icd_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"min_coexist\": 5,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"patient_nday_days\": 3,\n"
                      + "  \"patient_nday_enable\": 0,\n"
                      + "  \"patient_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("6 - /payment/othersfee/add\"- 10-4.22 不分類設定(add)")
    void _06() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.22 不分類設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/othersfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"不分類\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"interval_nday\": 10,\n"
                      + "  \"interval_nday_enable\": 1,\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_inpatient\": 10,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"max_times\": 12,\n"
                      + "  \"max_times_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"patient_nday_days\": 3,\n"
                      + "  \"patient_nday_enable\": 0,\n"
                      + "  \"patient_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='C'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_U_E2101dControll")
  class LOG_ACTION_U_E2101dControll {
    @Test
    @DisplayName("1 - /payment/psychiatricfee/{pt_id} - 10-4.03 精神醫療治療費設定(update)")
    void _01() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.03 精神醫療治療費設定(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/psychiatricfee")
              .addPathVariable("/223")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"精神醫療治療費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_inpatient\": 16,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"patient_nday_days\": 3,\n"
                      + "  \"patient_nday_enable\": 0,\n"
                      + "  \"patient_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("2 - /payment/bonemarrowtransfee/{pt_id} - 10-4.07 輸血及骨髓移植費設定(update)")
    void _02() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.07 輸血及骨髓移植費設定(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/bonemarrowtransfee")
              .addPathVariable("/224")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"輸血及骨髓移植費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_allow_plan\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"not_allow_plan_enable\": 0,\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("3 - /payment/plasterbandagefee/{pt_id} - 10-4.11 石膏繃帶費設定(update)")
    void _03() {
      String tableNameToLog = "log_action";
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.11 石膏繃帶費設定(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/plasterbandagefee")
              .addPathVariable("/225")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"石膏繃帶費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("4 - /payment/anesthesiafee/{pt_id} - 10-4.15 麻醉費設定(update)")
    void _04() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.15 麻醉費設定(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/anesthesiafee")
              .addPathVariable("/226")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"麻醉費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"include_drg_no_enable\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_drg_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"over_times_enable\": 0,\n"
                      + "  \"over_times_first_n\": 50,\n"
                      + "  \"over_times_n\": 2,\n"
                      + "  \"over_times_next_n\": 20,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("5 - /payment/specificmedicalfee/{pt_id} - 10-4.19 特定診療檢查費設定(update)")
    void _05() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.19 特定診療檢查費設定(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/specificmedicalfee")
              .addPathVariable("/227")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"特定診療檢查費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"interval_nday\": 0,\n"
                      + "  \"interval_nday_enable\": 1,\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_times\": 0,\n"
                      + "  \"max_times_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("6 - /payment/othersfee/{pt_id} - 10-4.23 不分類設定(update)")
    void _06() {
      String tableNameToLog = "log_action";

      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.23 不分類設定(update)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/othersfee")
              .addPathVariable("/228")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"不分類\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"interval_nday\": 10,\n"
                      + "  \"interval_nday_enable\": 1,\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_inpatient\": 10,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"max_times\": 12,\n"
                      + "  \"max_times_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"patient_nday_days\": 3,\n"
                      + "  \"patient_nday_enable\": 0,\n"
                      + "  \"patient_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.PUT)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='U'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }

  @Nested
  @DisplayName("Tests for the method LOG_ACTION_D_E2101dControll")
  class LOG_ACTION_D_E2101dControll {
    @Test
    @DisplayName("1 - 10-4.04 精神醫療治療費設定(delete)")
    void _01() {
      String tableNameToLog = "log_action";

      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("10-4.02 精神醫療治療費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/psychiatricfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"精神醫療治療費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_inpatient\": 16,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"patient_nday_days\": 3,\n"
                      + "  \"patient_nday_enable\": 0,\n"
                      + "  \"patient_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      String id = response2.getBody().getMessage().split("/id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.04 精神醫療治療費設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/psychiatricfee")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("2 - 10-4.08 輸血及骨髓移植費設定(delete)")
    void _02() {
      String tableNameToLog = "log_action";

      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("10-4.06 輸血及骨髓移植費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/bonemarrowtransfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"輸血及骨髓移植費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_allow_plan\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"not_allow_plan_enable\": 0,\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      String id = response2.getBody().getMessage().split("/id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.08 輸血及骨髓移植費設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/bonemarrowtransfee")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("3 - 10-4.12 石膏繃帶費設定(delete)")
    void _03() {
      String tableNameToLog = "log_action";

      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("10-4.10 石膏繃帶費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/plasterbandagefee/add")
              .addPathVariable("")
              .addUriParam("")
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"石膏繃帶費\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      String id = response2.getBody().getMessage().split("/id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.12 石膏繃帶費設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/plasterbandagefee")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("4 - 10-4.16 麻醉費設定(delete)")
    void _04() {
      String tableNameToLog = "log_action";

      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("10-4.14 麻醉費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/anesthesiafee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"麻醉費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"include_drg_no_enable\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_drg_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"over_times_enable\": 0,\n"
                      + "  \"over_times_first_n\": 50,\n"
                      + "  \"over_times_n\": 2,\n"
                      + "  \"over_times_next_n\": 20,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();
      String id = response2.getBody().getMessage().split("/id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.16 麻醉費設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/anesthesiafee")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("5 - 10-4.20 特定診療檢查費設定(delete)")
    void _05() {
      String tableNameToLog = "log_action";

      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("10-4.18 特定診療檢查費設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/specificmedicalfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"復健治療費\",\n"
                      + "  \"coexist_nhi_no_enable\": 0,\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"include_icd_no_enable\": 0,\n"
                      + "  \"lim_division_enable\": 0,\n"
                      + "  \"lst_co_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_division\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_icd_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"min_coexist\": 5,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"patient_nday_days\": 3,\n"
                      + "  \"patient_nday_enable\": 0,\n"
                      + "  \"patient_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();

      String id = response2.getBody().getMessage().split("/id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.20 特定診療檢查費設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/specificmedicalfee")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }

    @Test
    @DisplayName("6 - 10-4.24 不分類設定(delete)")
    void _06() {
      String tableNameToLog = "log_action";

      // get id for deleting
      Builder builder2 = new ProfitClientBuilder();
      ApiClient profitClient2 =
          builder2
              .setApiName("10-4.22 不分類設定(add)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/othersfee/add")
              .addPathVariable("")
              .addUriParam("") // 省略 &inhCode=
              .addJsonBody(
                  "{\n"
                      + "  \"active\": 1,\n"
                      + "  \"category\": \"不分類\",\n"
                      + "  \"end_date\": 1627488000000,\n"
                      + "  \"exclude_nhi_no_enable\": 0,\n"
                      + "  \"fee_name\": \"雜支\",\n"
                      + "  \"fee_no\": \"zz001\",\n"
                      + "  \"hospital_type\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"hospitalized_type\": 0,\n"
                      + "  \"interval_nday\": 10,\n"
                      + "  \"interval_nday_enable\": 1,\n"
                      + "  \"lst_nhi_no\": [\n"
                      + "    \"string\"\n"
                      + "  ],\n"
                      + "  \"max_inpatient\": 10,\n"
                      + "  \"max_inpatient_enable\": 1,\n"
                      + "  \"max_times\": 12,\n"
                      + "  \"max_times_enable\": 1,\n"
                      + "  \"nhi_name\": \"雜支\",\n"
                      + "  \"nhi_no\": \"axow-001\",\n"
                      + "  \"outpatient_type\": 1,\n"
                      + "  \"patient_nday_days\": 3,\n"
                      + "  \"patient_nday_enable\": 0,\n"
                      + "  \"patient_nday_times\": 10,\n"
                      + "  \"start_date\": 1625932800000\n"
                      + "}")
              .httpMethod(HttpMethod.POST)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient2.showProperties();
      ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
      profitClient2.showResult();
      String id = response2.getBody().getMessage().split("/id=")[1];

      // test target
      Builder builder = new ProfitClientBuilder();
      ApiClient profitClient =
          builder
              .setApiName("10-4.24 不分類設定(delete)")
              .setBaseUrl("http://localhost")
              .setPort(port)
              .setMediaType(MediaType.APPLICATION_JSON)
              .setAccessToken(accessToken)
              .addApiUrl("/payment/othersfee")
              .addPathVariable("/" + id)
              .addUriParam("")
              .addJsonBody("")
              .httpMethod(HttpMethod.DELETE)
              .setResponseClass(BaseResponse.class)
              .getProfitApi();
      profitClient.showProperties();
      ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
      profitClient.showResult();

      // 檢查
      String sql;
      sql = "SELECT COUNT(*)  FROM " + tableNameToLog + " WHERE crud='D'";
      try {
        int cout = jdbcTemplate.queryForObject(sql, Integer.class);
        assertAll("在 log 表裡應該會新增一筆資料", () -> assertEquals(1, cout));
      } catch (DataAccessException ex) {
        ex.printStackTrace();
        assertAll(() -> fail());
      }
    }
  }
}
