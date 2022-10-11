package tw.com.leadtek.nhiwidget.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import tw.com.leadtek.nhiwidget.log.ApiClient;
import tw.com.leadtek.nhiwidget.log.Builder;
import tw.com.leadtek.nhiwidget.log.ProfitClientBuilder;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.report.PeriodPointWeeklyPayload;
import tw.com.leadtek.nhiwidget.security.jwt.JwtResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportControllerTest {

  private static String accessToken;
  @Autowired protected JdbcTemplate jdbcTemplate;
  @LocalServerPort private int port;

  private static List<Arguments> provideStringsForIsBlank() {
    // rollbackM,  rollbackQ,  afrQuantity,  afrAmount,  afrPayQuantity,  afrPayAmount,
    // afrNoPayCode,  afrNoPayDesc,  afrNote
    return Arrays.asList(
        Arguments.of("1", "1", "1", "1", "1", "1", "1", "1", "參考..."),
        Arguments.of(null, "1", "1", "1", "1", "1", "1", "1", "參考..."),
        Arguments.of("1", null, "1", "1", "1", "1", "1", "1", "參考..."),
        Arguments.of("1", "1", null, "1", "1", "1", "1", "1", "參考..."),
        Arguments.of("1", "1", "1", null, "1", "1", "1", "1", "參考..."),
        Arguments.of("1", "1", "1", "1", null, "1", "1", "1", "參考..."),
        Arguments.of("1", "1", "1", "1", "1", null, "1", "1", "參考..."),
        Arguments.of("1", "1", "1", "1", "1", "1", null, "1", "參考..."),
        Arguments.of("1", "1", "1", "1", "1", "1", "1", null, "參考..."),
        Arguments.of("1", "1", "1", "1", "1", "1", "1", "1", null));
  }

  private static List<Arguments> provideParamOfDispute() {
    // disputeQuantity,  disputeAmount,  disputePayQuantity,  disputePayAmount,  disputeNoPayCode,
    // disputeNoPayDesc,  disputeNote
    return Arrays.asList(
        Arguments.of("1", "1", "1", "1", "1", "1", "參考..."),
        Arguments.of(null, "1", "1", "1", "1", "1", "參考..."),
        Arguments.of("1", null, "1", "1", "1", "1", "參考..."),
        Arguments.of("1", "1", null, "1", "1", "1", "參考..."),
        Arguments.of("1", "1", "1", null, "1", "1", "參考..."),
        Arguments.of("1", "1", "1", "1", null, "1", "參考..."),
        Arguments.of("1", "1", "1", "1", "1", null, "參考..."),
        Arguments.of("1", "1", "1", "1", "1", "1", null));
  }

  @BeforeEach
  void fetchToken() {
    String username = "leadtek";
    String password = "test";

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
    //        profitClient.showProperties();
    ResponseEntity<JwtResponse> result = (ResponseEntity<JwtResponse>) profitClient.call();
    //        profitClient.showResult();
    accessToken = Objects.requireNonNull(result.getBody()).getToken();
  }

  @ParameterizedTest
  @MethodSource("provideStringsForIsBlank")
  @DisplayName("")
  void getDeductedNote(
      String rollbackM,
      String rollbackQ,
      String afrQuantity,
      String afrAmount,
      String afrPayQuantity,
      String afrPayAmount,
      String afrNoPayCode,
      String afrNoPayDesc,
      String afrNote) {

    // 查 id
    String sql;
    sql = "SELECT id  FROM deducted_note ORDER BY id DESC limit 1";
    int id = jdbcTemplate.queryForObject(sql, Integer.class);

    // 更新
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
                "[\n"
                    + "  {\n"
                    + "    \"id\": "
                    + id
                    + ",\n"
                    + "    \"cat\": \"立意\",\n"
                    + "    \"item\": \"藥費\",\n"
                    + "    \"l1\": \"程序審查核減代碼\",\n"
                    + "    \"l2\": \"支付標準醫令錯誤代碼\",\n"
                    + "    \"l3\": null,\n"
                    + "    \"code\": \"C11\",\n"
                    + "    \"deductedOrder\": \"00156A\",\n"
                    + "    \"deductedQuantity\": 2,\n"
                    + "    \"deductedAmount\": 1,\n"
                    + "    \"reason\": \"因為..\",\n"
                    + "    \"note\": \"參考...\",\n"
                    + "    \"rollbackM\": "
                    + parseTypeForJson(rollbackM)
                    + ",\n"
                    + "    \"rollbackQ\": "
                    + parseTypeForJson(rollbackQ)
                    + ",\n"
                    + "    \"afrQuantity\": "
                    + parseTypeForJson(afrQuantity)
                    + ",\n"
                    + "    \"afrAmount\": "
                    + parseTypeForJson(afrAmount)
                    + ",\n"
                    + "    \"afrPayQuantity\": "
                    + parseTypeForJson(afrPayQuantity)
                    + ",\n"
                    + "    \"afrPayAmount\": "
                    + parseTypeForJson(afrPayAmount)
                    + ",\n"
                    + "    \"afrNoPayCode\": "
                    + parseTypeForJson(afrNoPayCode)
                    + ",\n"
                    + "    \"afrNoPayDesc\": "
                    + parseTypeForJson(afrNoPayDesc)
                    + ",\n"
                    + "    \"afrNote\": "
                    + parseTypeForJson(afrNote)
                    + ",\n"
                    + "    \"disputeQuantity\": 1,\n"
                    + "    \"disputeAmount\": 1,\n"
                    + "    \"disputePayQuantity\": 1,\n"
                    + "    \"disputePayAmount\": 1,\n"
                    + "    \"disputeNoPayCode\": \"1\",\n"
                    + "    \"disputeNoPayDesc\": \"1\",\n"
                    + "    \"disputeNote\": \"參考...\",\n"
                    + "    \"editor\": \"leadtek\",\n"
                    + "    \"updateAt\": \"2022/10/03 13:15:29\",\n"
                    + "    \"record\": \"修改核刪代碼C11\",\n"
                    + "    \"modifyStatus\": \"修改\",\n"
                    + "    \"deductedDate\": \"2022-09-01\",\n"
                    + "    \"rollbackDate\": \"2022-09-02\",\n"
                    + "    \"disputeDate\": \"2022-09-03\",\n"
                    + "    \"subCat\": null\n"
                    + "  }\n"
                    + "]")
            .httpMethod(HttpMethod.PUT)
            .setResponseClass(BaseResponse.class)
            .getProfitApi();
    profitClient.showProperties();
    ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
    profitClient.showResult();

    // 調用目標 API
    Builder builder2 = new ProfitClientBuilder();
    ApiClient profitClient2 =
        builder2
            .setApiName("取得核刪資訊")
            .setBaseUrl("http://localhost")
            .setPort(port)
            .setMediaType(MediaType.APPLICATION_JSON)
            .setAccessToken(accessToken)
            .addApiUrl("/report/deductedNote")
            .addPathVariable("")
            .addUriParam("/?year=2022&quarter=Q4")
            .addJsonBody("")
            .httpMethod(HttpMethod.GET)
            .setResponseClass(BaseResponse.class)
            .getProfitApi();
    profitClient2.showProperties();
    ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
    profitClient2.showResult();

    // 檢查
    assertAll(() -> assertNotEquals(500, response2.getStatusCodeValue()));
  }

  String parseTypeForJson(Object var) {
    if (null == var) {
      return null;
    } else if (var instanceof String) {
      return "\"" + var + "\"";
    } else if (var instanceof Integer) {
      return String.valueOf(var);
    } else if (var instanceof Double) {
      return String.valueOf(var);
    }
    return null;
  }

  @ParameterizedTest
  @MethodSource("provideParamOfDispute")
  @DisplayName("更新指定病歷的核刪註記內容 - 獨立驗證")
  void getDeductedNote2(
      String disputeQuantity,
      String disputeAmount,
      String disputePayQuantity,
      String disputePayAmount,
      String disputeNoPayCode,
      String disputeNoPayDesc,
      String disputeNote) {

    // 查 id
    String sql;
    sql = "SELECT id  FROM deducted_note ORDER BY id DESC limit 1";
    int id = jdbcTemplate.queryForObject(sql, Integer.class);

    // 更新
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
                "[\n"
                    + "  {\n"
                    + "    \"id\": "
                    + id
                    + "    ,\n"
                    + "    \"cat\": \"立意\",\n"
                    + "    \"item\": \"藥費\",\n"
                    + "    \"l1\": \"程序審查核減代碼\",\n"
                    + "    \"l2\": \"支付標準醫令錯誤代碼\",\n"
                    + "    \"l3\": \"診療品質\",\n"
                    + "    \"code\": \"C11\",\n"
                    + "    \"deductedOrder\": \"00156A\",\n"
                    + "    \"deductedQuantity\": 2,\n"
                    + "    \"deductedAmount\": 1,\n"
                    + "    \"reason\": \"因為..\",\n"
                    + "    \"note\": \"參考...\",\n"
                    + "    \"rollbackM\": 1,\n"
                    + "    \"rollbackQ\": 1,\n"
                    + "    \"afrQuantity\": 1,\n"
                    + "    \"afrAmount\": 1,\n"
                    + "    \"afrNoPayCode\": \"1\",\n"
                    + "    \"afrPayQuantity\": 1,\n"
                    + "    \"afrPayAmount\": 1,\n"
                    + "    \"afrNoPayDesc\": \"1\",\n"
                    + "    \"afrNote\": \"參考...\",\n"
                    + "    \"disputeQuantity\": "
                    + parseTypeForJson(disputeQuantity)
                    + ",\n"
                    + "    \"disputeAmount\": "
                    + parseTypeForJson(disputeAmount)
                    + ",\n"
                    + "    \"disputePayQuantity\": "
                    + parseTypeForJson(disputePayQuantity)
                    + ",\n"
                    + "    \"disputePayAmount\": "
                    + parseTypeForJson(disputePayAmount)
                    + ",\n"
                    + "    \"disputeNoPayCode\": "
                    + parseTypeForJson(disputeNoPayCode)
                    + ",\n"
                    + "    \"disputeNoPayDesc\": "
                    + parseTypeForJson(disputeNoPayDesc)
                    + ",\n"
                    + "    \"disputeNote\": "
                    + parseTypeForJson(disputeNote)
                    + ",\n"
                    + "    \"editor\": \"leadtek\",\n"
                    + "    \"updateAt\": \"2022/09/30 07:32:00\",\n"
                    + "    \"record\": \"修改核刪代碼C11\",\n"
                    + "    \"modifyStatus\": \"修改\",\n"
                    + "    \"deductedDate\": \"2022-10-01\",\n"
                    + "    \"rollbackDate\": \"2022-10-02\",\n"
                    + "    \"disputeDate\": \"2022-10-03\",\n"
                    + "    \"subCat\": null\n"
                    + "  }\n"
                    + "]")
            .httpMethod(HttpMethod.PUT)
            .setResponseClass(BaseResponse.class)
            .getProfitApi();
    profitClient.showProperties();
    ResponseEntity<BaseResponse> response = (ResponseEntity<BaseResponse>) profitClient.call();
    profitClient.showResult();

    // 調用目標 API
    Builder builder2 = new ProfitClientBuilder();
    ApiClient profitClient2 =
        builder2
            .setApiName("取得核刪資訊")
            .setBaseUrl("http://localhost")
            .setPort(port)
            .setMediaType(MediaType.APPLICATION_JSON)
            .setAccessToken(accessToken)
            .addApiUrl("/report/deductedNote")
            .addPathVariable("")
            .addUriParam("/?year=2022&quarter=Q4")
            .addJsonBody("")
            .httpMethod(HttpMethod.GET)
            .setResponseClass(BaseResponse.class)
            .getProfitApi();
    profitClient2.showProperties();
    ResponseEntity<BaseResponse> response2 = (ResponseEntity<BaseResponse>) profitClient2.call();
    profitClient2.showResult();

    // 檢查
    assertAll(() -> assertNotEquals(500, response2.getStatusCodeValue()));
  }

  @Test
  @DisplayName("取得費用業務依照科別-每周趨勢資料 - 驗證資料範圍包含「所選擇的迄日的當週」")
  void getPeriodPointWeekly() {

    // 調用目標 API
    Builder builder = new ProfitClientBuilder();

    ApiClient profitClient =
        builder
            .setApiName("取得費用業務依照科別-每周趨勢資料")
            .setBaseUrl("http://localhost")
            .setPort(port)
            .setMediaType(MediaType.APPLICATION_JSON)
            .setAccessToken(accessToken)
            .addApiUrl("/report/periodPointWeeklyByFunctype")
            .addPathVariable("?edate=2022/01/31&funcType=00")
            .addUriParam("")
            .addJsonListBody("")
            .httpMethod(HttpMethod.GET)
            .setResponseClass(PeriodPointWeeklyPayload.class)
            .getProfitApi();
    profitClient.showProperties();
    ResponseEntity<PeriodPointWeeklyPayload> response =
        (ResponseEntity<PeriodPointWeeklyPayload>) profitClient.call();
    profitClient.showResult();

    // 檢查
    assertAll(
        "2022/01/01 ~ 2022/01/31 應涵蓋到第六週",
        () -> assertEquals("2022 w6", response.getBody().getOp().getNames().get(51)));
  }
}
