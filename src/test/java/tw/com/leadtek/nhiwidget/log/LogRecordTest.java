package tw.com.leadtek.nhiwidget.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import tw.com.leadtek.nhiwidget.model.rdb.USER;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.ResponseId;
import tw.com.leadtek.nhiwidget.security.jwt.JwtResponse;
import tw.com.leadtek.nhiwidget.service.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogRecordTest {

  private static final String baseUrl = "http://localhost";
  private static String accessToken;
  private static RestTemplate restTemplate = null;
  @Autowired protected JdbcTemplate jdbcTemplate;
  @LocalServerPort private int port;
  
  @Autowired
  private UserService userService;

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
//    System.out.println("port=" + port + ", token=" + accessToken);
  }

  @Test
  @Order(1)
  void whileApplyForgotPasswordShouldBeRecord() {
    // 參數
    String username = "leadtek2";
    int userId = addUser(username);

    String apiUrl = "/auth/forgetPassword?username=";
    HttpMethod httpMethod = HttpMethod.PUT;

    // 清空資料庫
    cleanLogForgotPassword(userId);
    }
  @AfterEach
  void cleanActionLog(){
    // Recovery 資料庫
    log_action_clean();
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

  private void callProfitPointAPIWithBody(String apiUrl, HttpMethod httpMethod, String jsonBody, String uriParam) {

    Map<String, Object> bodayParams;

    try {
      bodayParams = new ObjectMapper().readValue(jsonBody, HashMap.class);
      //      System.out.println(uriParams);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    //    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(bodayParams, headers);
    ResponseEntity<BaseResponse> result =
        restTemplate.exchange(
            baseUrl.concat(":").concat(port + "").concat(apiUrl).concat(uriParam),
            httpMethod,
            entity,
            BaseResponse.class);
    System.out.println("===========================");
    System.out.println(apiUrl + ": " + result);
    System.out.println("api message"+ ": " + result.getBody().getMessage());
  }

  private void callProfitPointAPIWithParameter(
      String parameter, String apiUrl, HttpMethod httpMethod) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    //    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);
    ResponseEntity<BaseResponse> result =
        restTemplate.exchange(
            baseUrl.concat(":").concat(port + "").concat(apiUrl).concat(parameter),
            httpMethod,
            entity,
            BaseResponse.class);
    System.out.println(apiUrl + ": " + result + "/n");
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

  @Test
  @Order(1)
  void LOG_FORGOT_PASSWORD_UserController_1_忘記密碼() {
    // 參數
    String parameter = "?username=leadtek2";
    String apiUrl = "/auth/forgetPassword";

    int userId = 7;
    HttpMethod httpMethod = HttpMethod.PUT;

    // 清空資料庫
    log_forgot_password_clean(userId);

    // 調用目標 API
    callProfitPointAPIWithParameter(parameter, apiUrl, httpMethod);

    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM log_forgot_password WHERE user_id = %d";
    sql = String.format(sql, userId);
    try {
      int cout = jdbcTemplate.queryForObject(sql, Integer.class);
      log_forgot_password_clean(userId);
      assertAll(() -> assertEquals(1, cout));
      cleanLogForgotPassword(userId);
      userService.deleteUser(Long.valueOf(userId));
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
//    assertAll(() -> assertEquals("success", result.getBody().getResult()));
    return Integer.parseInt(userId);
  }
  
 

  @Test
  @Order(2)
  @DisplayName("新增帳號")
  void LOG_ACTION_1_新增帳號() {

    // 參數
    String apiUrl = "/auth/user";
    String uriParam ="";
    String usernameNew = "leadtek_new";
    HttpMethod httpMethod = HttpMethod.POST;
    String tableName = "log_action";
    String jsonBody =
        "{\n"
            + "  \"departmentId\": \"88,22\",\n"
            + "  \"departments\": \"骨科,急診醫學科\",\n"
            + "  \"displayName\": \"測試帳號\",\n"
            + "  \"email\": \"string\",\n"
            + "  \"id\": 1,\n"
            + "  \"inhId\": \"A01\",\n"
            + "  \"password\": \"test1234\",\n"
            + "  \"rocId\": \"A123456789\",\n"
            + "  \"role\": \"A\",\n"
            + "  \"status\": 1,\n"
            + "  \"username\": \""
            + usernameNew
            + "\"\n"
            + "}";

    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);

    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableName;
    try {
      int cout = jdbcTemplate.queryForObject(sql, Integer.class);

      // recovery 資料庫
      user_rollback(usernameNew);
      assertAll("新增帳號成功",() -> assertEquals(1, cout));
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  @Test
  void LOG_ACTION_2_新增部門() {

    // 參數
    String apiUrl = "/department";
    String uriParam ="";
    String newDepartmentName = "骨科_測試";
    HttpMethod httpMethod = HttpMethod.POST;
    String tableNameToLog = "log_action";
    String jsonBody =
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
            + "}";

    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);
    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;
    try {
      int cout = jdbcTemplate.queryForObject(sql, Integer.class);
      // Recovery 資料庫

      department_rollback(newDepartmentName);

      assertAll(() -> assertEquals(1, cout));
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  @Test
  void LOG_ACTION_SystemController_1_新增ICD10代碼() {

    // 參數
    String apiUrl = "/sys/icd10";
    String uriParam ="";
    HttpMethod httpMethod = HttpMethod.POST;
    String tableNameToLog = "log_action";
    String jsonBody =
        "{\n"
            + "  \"cat\": \"CM\",\n"
            + "  \"code\": \"0SG13ZJ\",\n"
            + "  \"descChi\": \"經皮2節以上腰椎關節由後側進入前柱融合術\",\n"
            + "  \"descEn\": \"Fusion of 2 or more Lumbar Vertebral Joints, Posterior Approach, Anterior Column, Percutaneous Approach\",\n"
            + "  \"id\": 1,\n"
            + "  \"infCat\": 1,\n"
            + "  \"infectious\": 0,\n"
            + "  \"remark\": \"補充說明欄\"\n"
            + "}";

    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);
    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;
    try {
      int cout = jdbcTemplate.queryForObject(sql, Integer.class);

      assertAll(() -> assertEquals(1, cout));
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  @Test
  void LOG_ACTION_SystemController_2_新增一組DRGcode() {

    // 參數
    String apiUrl = "/sys/drg";
    String uriParam ="";
    String code = "00502";
    HttpMethod httpMethod = HttpMethod.POST;
    String tableNameToLog = "log_action";
    String jsonBody =
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
            + "}";

    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);
    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;
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
  void LOG_ACTION_SystemController_3_新增一組ATC分類代碼() {

    // 參數
    String apiUrl = "/sys/atc";
    String uriParam ="";
    String code = "testcode";
    HttpMethod httpMethod = HttpMethod.POST;
    String tableNameToLog = "log_action";
    String jsonBody =
        "{\n"
            + "  \"code\": \""
            + code
            + "\",\n"
            + "  \"leng\": 3,\n"
            + "  \"note\": \"Oxymetazoline\"\n"
            + "}";

    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);
    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;
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
  void LOG_ACTION_SystemController_4_新增一組代碼品項() {

    // 參數
    String apiUrl = "/sys/payCode";
    String uriParam ="";
    String code = "testcode";
    HttpMethod httpMethod = HttpMethod.POST;
    String tableNameToLog = "log_action";
    String jsonBody =
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
            + "}";

    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);
    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;
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
  void LOG_ACTION_SystemController_5_新增核減代碼() {

    // 參數
    String apiUrl = "/sys/deducted";
    String uriParam ="";
    String code = "testcode";
    HttpMethod httpMethod = HttpMethod.POST;
    String tableNameToLog = "log_action";
    String jsonBody =
        "{\n"
            + "  \"code\": \""
            + code
            + "\",\n"
            + "  \"id\": 1,\n"
            + "  \"l1\": \"專業審查不予支付代碼\",\n"
            + "  \"l2\": \"西醫\",\n"
            + "  \"l3\": \"string\",\n"
            + "  \"name\": \"診療品質不符專業認定，理由____\"\n"
            + "}";

    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);
    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;
    try {
      int cout = jdbcTemplate.queryForObject(sql, Integer.class);
      // Recovery 資料庫

      deducted_rollback(code);
      assertAll(() -> assertEquals(1, cout));
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  @Test
  void LOG_ACTION_PlanCOnditionController_1_新增計畫可收案病例條件() {

    // 參數
    String apiUrl = "/plan/add";
    String name = "testname";
    String uriParam ="";
    HttpMethod httpMethod = HttpMethod.POST;
    String tableNameToLog = "log_action";
    String jsonBody =
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
            + "}";

    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);

    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;

    try {
      int cout = jdbcTemplate.queryForObject(sql, Integer.class);
      // Recovery 資料庫

      plan_condition_rollback(name);
      assertAll(() -> assertEquals(1, cout));
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  @Test
  void LOG_ACTION_ParameterController_1_新增分配點數() {

    // 參數
    String apiUrl = "/p/assignedPoints";
    String uriParam ="";
    String fundpoints = "9999999";
    HttpMethod httpMethod = HttpMethod.POST;
    String tableNameToLog = "log_action";
    String jsonBody =
        "{\n"
            + "  \"dentistDrugPoints\": 0,\n"
            + "  \"dentistFundPoints\": 0,\n"
            + "  \"dentistOpPoints\": 0,\n"
            + "  \"edate\": \"2022/06/30\",\n"
            + "  \"fundPoints\": "
            + fundpoints
            + ",\n"
            + "  \"hemodialysisPoints\": 0,\n"
            + "  \"id\": 1,\n"
            + "  \"sdate\": \"2022/01/01\",\n"
            + "  \"wmDrugPoints\": 0,\n"
            + "  \"wmIpPoints\": 0,\n"
            + "  \"wmOpPoints\": 0\n"
            + "}";

    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);

    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;

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

  // TODO 整理位置
  private void assigned_point_rollback(String fundpoints) {
    String sql_clean = "DELETE FROM assigned_point WHERE fund_points =" + fundpoints;
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  @Test
  void LOG_ACTION_ParameterController_2_新增分配總點數() {

    // 隨機生成日期
    Map<String, String> map = getRandomStardAndEndDateMap();

    String formatedDayS = map.get("sDate");
    String formatedDayE = map.get("eDate");

    // 參數
    String apiUrl = "/p/pointsValue";
    String uriParam ="";
    String fundpoints = "9999999";
    HttpMethod httpMethod = HttpMethod.POST;
    String tableNameToLog = "log_action";
    String jsonBody =
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
            + "}";

    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);

    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;

    try {
      int cout = jdbcTemplate.queryForObject(sql, Integer.class);

      assertAll(() -> assertEquals(10, cout));
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  //TODO
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

  @Test
  void LOG_ACTION_ParameterController_3_新增參數值() {
    // 隨機生成日期
    Map<String, String> map = getRandomStardAndEndDateMap();

    // 參數
    String tableNameToLog = "log_action";
    String edate = map.get("eDate");
    String spr = "SPR";
    String sdate = map.get("sDate");
    String s2 = "1234";

    String parameter = "?edate=" + edate + "&name=" + spr + "&sdate=" + sdate + "&value=" + s2;
    String apiUrl = "/p/value".concat(parameter);

    HttpMethod httpMethod = HttpMethod.POST;

    // 調用目標 API
    callProfitPointAPIWithParameter(parameter, apiUrl, httpMethod);

    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;

    try {
      int cout = jdbcTemplate.queryForObject(sql, Integer.class);

      assertAll(() -> assertEquals(1, cout));
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  @Test
  void LOG_ACTION_ParameterController_4_新增DRG參數值() {

    // 隨機生成日期
    Map<String, String> map = getRandomStardAndEndDateMap();


    // 參數
    String apiUrl = "/p/drg";
    String uriParam ="";
    HttpMethod httpMethod = HttpMethod.POST;
    String tableNameToLog = "log_action";
    String jsonBody =
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
            + "  \"endDate\": \""+map.get("eDate")+"\",\n"
            + "  \"id\": 320,\n"
            + "  \"message\": \"錯誤訊息\",\n"
            + "  \"outlyingIslands\": 2,\n"
            + "  \"result\": \"success\",\n"
            + "  \"spr\": 45837,\n"
            + "  \"startDate\": \""+map.get("sDate")+"\",\n"
            + "  \"status\": \"未啟動\"\n"
            + "}";

    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);

    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;

    try {
      int cout = jdbcTemplate.queryForObject(sql, Integer.class);

      assertAll(() -> assertEquals(18, cout));
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  @Test
  void LOG_ACTION_ParameterController_5_新增罕見ICD代碼() {

    // 參數
    String apiUrl = "/p/rareICD";
    String uriParam ="";
    String descChi = "Junit測試-其他確認流感病毒所致流行性感冒併其他呼吸道表徵";
    HttpMethod httpMethod = HttpMethod.POST;
    String tableNameToLog = "log_action";
    String jsonBody =
        "{\n"
            + "  \"both\": true,\n"
            + "  \"code\": \"J10.01\",\n"
            + "  \"edate\": \"2022/06/30\",\n"
            + "  \"id\": 1,\n"
            + "  \"ip\": true,\n"
            + "  \"ipTimes6M\": 100,\n"
            + "  \"ipTimes6MStatus\": true,\n"
            + "  \"ipTimesM\": 100,\n"
            + "  \"ipTimesMStatus\": true,\n"
            + "  \"name\": \""+descChi+"\",\n"
            + "  \"op\": true,\n"
            + "  \"opTimes6M\": 100,\n"
            + "  \"opTimes6MStatus\": true,\n"
            + "  \"opTimesM\": 100,\n"
            + "  \"opTimesMStatus\": true,\n"
            + "  \"sdate\": \"2022/01/01\",\n"
            + "  \"status\": true\n"
            + "}";

    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);

    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;

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

//TODO 等待放置位置
  private void code_threshold_rollback(String descChi) {
    String sql_clean = "DELETE FROM code_threshold WHERE desc_chi =\"" + descChi + "\"";
    try {
      jdbcTemplate.update(sql_clean);
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  @Test
  void LOG_ACTION_ParameterController_6_新增應用比例偏高醫令_特別用量藥品_衛材() {

    // 參數
    String apiUrl = "/p/highRatioOrder";
    String uriParam ="";
    String descChi = "Junit測試-其他確認流感病毒所致流行性感冒併其他呼吸道表徵";
    HttpMethod httpMethod = HttpMethod.POST;
    String tableNameToLog = "log_action";
    String jsonBody = "{\n" +
            "  \"both\": true,\n" +
            "  \"code\": \"J10.01\",\n" +
            "  \"codeType\": 2,\n" +
            "  \"edate\": \"2022/06/30\",\n" +
            "  \"id\": 1,\n" +
            "  \"inhCode\": \"string\",\n" +
            "  \"inhName\": \"string\",\n" +
            "  \"ip\": true,\n" +
            "  \"ipTimes\": 10,\n" +
            "  \"ipTimes6M\": 100,\n" +
            "  \"ipTimes6MStatus\": true,\n" +
            "  \"ipTimesD\": 100,\n" +
            "  \"ipTimesDStatus\": true,\n" +
            "  \"ipTimesDay\": 3,\n" +
            "  \"ipTimesM\": 100,\n" +
            "  \"ipTimesMStatus\": true,\n" +
            "  \"ipTimesStatus\": true,\n" +
            "  \"name\": \""+descChi+"\",\n" +
            "  \"op\": true,\n" +
            "  \"opTimes\": 100,\n" +
            "  \"opTimes6M\": 100,\n" +
            "  \"opTimes6MStatus\": true,\n" +
            "  \"opTimesD\": 100,\n" +
            "  \"opTimesDStatus\": true,\n" +
            "  \"opTimesDay\": 100,\n" +
            "  \"opTimesM\": 100,\n" +
            "  \"opTimesMStatus\": true,\n" +
            "  \"opTimesStatus\": true,\n" +
            "  \"sdate\": \"2022/01/01\",\n" +
            "  \"status\": true\n" +
            "}";

    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);

    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;

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
  void LOG_ACTION_ParameterController_7_新增健保項目對應自費項目並存資料() {

    // 參數
    String apiUrl = "/p/codeConflict";
    String uriParam ="";
    HttpMethod httpMethod = HttpMethod.POST;
    String tableNameToLog = "log_action";
    String jsonBody =
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
            + "}";

    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);

    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;

    try {
      int cout = jdbcTemplate.queryForObject(sql, Integer.class);

      assertAll(() -> assertEquals(1, cout));
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  //TODO 未完成該組的；下一步，先完成其他的，再繼續做。
  @Test
  void LOG_ACTION_NHIWidgetXMLController_1_新增病歷資訊備註() {

    // 參數
    String apiUrl = "/nhixml/note";
    String uriParam ="?id=13220&isNote=true&note=新增病歷資訊備註內容";
    HttpMethod httpMethod = HttpMethod.POST;
    String tableNameToLog = "log_action";
    String jsonBody =
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
                    + "}";

    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);

    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;

    try {
      int cout = jdbcTemplate.queryForObject(sql, Integer.class);

      assertAll(() -> assertEquals(1, cout));
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }



  //TODO 欠缺測試資料；下一步，找 Ken 提供。
  @Test
  void MEDICAL_RECORD_NOTIFYED_NHIWidgetXMLController_1_發送一筆病歷通知() {

    // 參數
    String apiUrl = "/nhixml/sendNotice";
    String uriParam ="？doctorId=";
    String uriVariable ="/"; //id
    HttpMethod httpMethod = HttpMethod.POST;
    String tableNameToLog = "log_action";
    String jsonBody ="";


    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);

    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;

    try {
      int cout = jdbcTemplate.queryForObject(sql, Integer.class);

      assertAll(() -> assertEquals(1, cout));
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }

  @Test
  void MEDICAL_RECORD_STATUS_CHANGE_NHIWidgetXMLController_1_更改指定病歷狀態() {

    // 參數
    String apiUrl = "nhixml/mr/status";
    String uriVariable ="/146020"; //id
    String uriParam ="？status=無需變更&statusCode=1";
    HttpMethod httpMethod = HttpMethod.POST;
    String tableNameToLog = "log_action";
    String jsonBody ="";


    // 調用目標 API
    callProfitPointAPIWithBody(apiUrl, httpMethod, jsonBody, uriParam);

    // 檢查
    String sql;
    sql = "SELECT COUNT(*)  FROM " + tableNameToLog;

    try {
      int cout = jdbcTemplate.queryForObject(sql, Integer.class);

      assertAll(() -> assertEquals(1, cout));
    } catch (DataAccessException ex) {
      ex.printStackTrace();
      assertAll(() -> fail());
    }
  }



}
