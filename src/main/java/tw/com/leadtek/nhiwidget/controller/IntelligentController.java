/**
 * Created on 2021/11/19.
 */
package tw.com.leadtek.nhiwidget.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import tw.com.leadtek.nhiwidget.constant.ROLE_TYPE;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.ParameterListPayload;
import tw.com.leadtek.nhiwidget.payload.intelligent.IntelligentResponse;
import tw.com.leadtek.nhiwidget.payload.my.MyTodoListResponse;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.service.IntelligentService;
import tw.com.leadtek.nhiwidget.service.ParametersService;
import tw.com.leadtek.tools.DateTool;

@Api(tags = "智能提示助理API", value = "智能提示助理API")
@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
@RequestMapping(produces = "application/json; charset=utf-8")
public class IntelligentController extends BaseController {

  @Autowired
  private ParametersService parameters;
  
  @Autowired
  private IntelligentService intelligentService;
  
  @ApiOperation(value = "取得智能提示助理清單", notes = "取得智能提示助理清單")
  @GetMapping("/intelligent")
  public ResponseEntity<IntelligentResponse> getIntelligent(
      @ApiParam(value = "選單種類，有以下四種：baseRule(固定條件判斷)，clincal(臨床路徑差異)，suspected(疑似職傷與異常就診記錄判斷)，drgSuggestion(DRG申報建議)",
        example = "baseRule") @RequestParam(required = false, defaultValue = "baseRule") String menu,
      @ApiParam(name = "sdate", value = "起始日期，格式 yyyy/MM/dd",
        example = "2021/03/15") @RequestParam(required = false) String sdate,
      @ApiParam(name = "edate", value = "結束日期，格式 yyyy/MM/dd",
        example = "2021/03/18") @RequestParam(required = false) String edate,
      @ApiParam(value = "申報西元年", example = "2021") @RequestParam(required = false) String applY,
      @ApiParam(value = "申報月份", example = "1") @RequestParam(required = false) String applM,
      @ApiParam(value = "資料格式，門急診:10，住院:20", example = "10") @RequestParam(required = false) String dataFormat,
      @ApiParam(name = "minPoints", value = "最小申報點數",
        example = "175") @RequestParam(required = false) Integer minPoints,
      @ApiParam(name = "maxPoints", value = "最大申報點數",
        example = "2040") @RequestParam(required = false) Integer maxPoints,
      @ApiParam(name = "funcType", value = "科別代碼，如：00(不分科)，01(家醫科)，02(內科)，03(外科)...",
        example = "03") @RequestParam(required = false) String funcType,
      @ApiParam(name = "funcTypec", value = "科別名稱，如：不分科、家醫科、內科、外科...",
        example = "家醫科") @RequestParam(required = false) String funcTypec,
      @ApiParam(name = "prsnId", value = "醫護代碼",
        example = "A123456789") @RequestParam(required = false) String prsnId,
      @ApiParam(name = "prsnName", value = "醫護姓名",
        example = "王小明") @RequestParam(required = false) String prsnName,
      @ApiParam(name = "orderCode", value = "搜尋支付標準代碼",
        example = "") @RequestParam(required = false) String orderCode,
      @ApiParam(name = "inhCode", value = "搜尋院內碼",
        example = "") @RequestParam(required = false) String inhCode,
      @ApiParam(name = "icd", value = "搜尋診斷碼",
        example = "") @RequestParam(required = false) String icd,
      @ApiParam(name = "reason", value = "智能提示原由，1:違反支付準則項目-支付準則條件，2:罕見ICD應用，3:應用比例偏高醫令，"
          + "4:特別用量藥材、衛材，6:健保項目對應自費項目並存，7:法定傳染病，8:同性質藥物開立，9:相關計畫疑似可收案病例，10:高風險診斷碼與健保碼組合，"
          + "11:臨床路徑差異–AI提示-費用差異，12:醫療行為差異，13:用藥差異，14:住院天數差異，16:疑似職傷與異常就診記錄，17:DRG申報建議",
        example = "1") @RequestParam(required = false) Integer reason,
      @ApiParam(name = "orderBy",
      value = "排序欄位名稱，status:資料狀態，sdate:就醫日期-起，edate:就醫日期-訖，inhMrId:病歷號碼，name:患者姓名，"
          + "inhClinicId:就醫記錄編號，funcType:科別代碼，funcTypec:科別，prsnId:醫護代碼，prsnName:醫護姓名，"
          + "totalDot:病歷點數，applId:負責人員代碼，applName:負責人員，reason:原由，detail:詳細資訊",
      example = "sdate") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小，空值表示不排序",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "頁碼，第一頁值為0，第二頁值為1…",
          example = "0") @RequestParam(required = false, defaultValue = "0") Integer page) {
    int perPageInt = (perPage == null) ? parameters.getIntParameter(ParametersService.PAGE_COUNT)
        : perPage.intValue();

    java.sql.Date startDate = null;
    java.sql.Date endDate = null;

    if (sdate != null && edate != null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      try {
        startDate = new java.sql.Date(sdf.parse(sdate).getTime());
        endDate = new java.sql.Date(sdf.parse(edate).getTime());
      } catch (ParseException e) {
        IntelligentResponse result = new IntelligentResponse();
        result.setMessage("日期格式有誤");
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    
    String applYm = getChineseYearMonth(applY, applM);
    
    String column = orderBy;
    if (column != null && orderBy.length() > 0) {
      if (column.equals("sdate")) {
        column = "startDate";
      } else if (column.equals("edate")) {
        column = "endDate";
      } else if (column.equals("totalDot")) {
        column = "applDot";
      } else if (column.equals("funcTypeC")) {
        column = "funcType";
      } else if (column.equals("detail")) {
        column = "reason";
      } else if (column.equals("reason")) {
        column = "conditionCode";
      } else if (!findDeclaredMethod("tw.com.leadtek.nhiwidget.model.rdb.INTELLIGENT", column)) {
        IntelligentResponse result = new IntelligentResponse();
        result.setMessage("orderBy無此欄位：" + column);
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    } else {
      column = null;
    }
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      IntelligentResponse result = new IntelligentResponse();
      result.setMessage("無法取得登入狀態");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
    if (ROLE_TYPE.DOCTOR.getRole().equals(user.getRole())) {
      IntelligentResponse result = new IntelligentResponse();
      result.setMessage("無權限");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
    }
    if ("clincal".equals(menu) && reason != null && reason.intValue() < 10) {
      reason = new Integer(reason.intValue() + 10);
    }
    return ResponseEntity.ok(
        intelligentService.getIntelligent(user, menu, startDate, endDate, minPoints, maxPoints, funcType,
            funcTypec, prsnId, prsnName, orderCode, inhCode, icd, reason, applYm, dataFormat, column, asc, perPageInt, page));
  }
  
  private String getChineseYearMonth(String applY, String applM) {
    if (applY == null || applY.length() == 0 || applM == null || applM.length() == 0) {
      return null;
    }
    return String.valueOf((Integer.parseInt(applY) - 1911) * 100 + Integer.parseInt(applM));
  }
}
