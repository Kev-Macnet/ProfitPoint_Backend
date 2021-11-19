/**
 * Created on 2021/11/18.
 */
package tw.com.leadtek.nhiwidget.controller;

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
import tw.com.leadtek.nhiwidget.payload.my.DoctorListResponse;
import tw.com.leadtek.nhiwidget.payload.my.MyTodoListResponse;
import tw.com.leadtek.nhiwidget.payload.my.NoticeRecordResponse;
import tw.com.leadtek.nhiwidget.payload.my.QuestionMarkResponse;
import tw.com.leadtek.nhiwidget.payload.my.WarningOrderResponse;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.service.NHIWidgetXMLService;
import tw.com.leadtek.nhiwidget.service.ParametersService;

@Api(tags = "我的清單相關API", value = "我的清單相關API")
@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
@RequestMapping(value = "/my", produces = "application/json; charset=utf-8")
public class MyListController extends BaseController {

  @Autowired
  private ParametersService parameters;
  
  @Autowired
  private NHIWidgetXMLService xmlService;
  
  @ApiOperation(value = "取得我的清單-待辦事項", notes = "取得我的清單-待辦事項")
  @GetMapping("/todo")
  public ResponseEntity<MyTodoListResponse> getMyTodo(
      @ApiParam(name = "orderBy",
      value = "排序欄位名稱，status:資料狀態，sdate:就醫日期-起，edate:就醫日期-訖，inhMrId:病歷號碼，name:患者姓名，"
          + "inhClinicId:就醫記錄編號，funcType:科別代碼，funcTypec:科別，prsnId:醫護代碼，prsnName:醫護姓名，"
          + "totalDot:病歷點數，applId:負責人員代碼，applName:負責人員",
      example = "sdate") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小，空值表示不排序",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "頁碼，第一頁值為0，第二頁值為1…",
          example = "0") @RequestParam(required = false, defaultValue = "0") Integer page) {
    int perPageInt = (perPage == null) ? parameters.getIntParameter(ParametersService.PAGE_COUNT)
        : perPage.intValue();

    String column = orderBy;
    if (column != null) {
      if (column.equals("sdate")) {
        column = "startDate";
      } else if (column.equals("edate")) {
        column = "endDate";
      } else if (column.equals("totalDot")) {
        column = "tDot";
      } else if (!findDeclaredMethod("tw.com.leadtek.nhiwidget.model.rdb.MY_MR", column)) {
        MyTodoListResponse result = new MyTodoListResponse();
        result.setMessage("orderBy無此欄位：" + column);
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      MyTodoListResponse result = new MyTodoListResponse();
      result.setMessage("無法取得登入狀態");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
    if (ROLE_TYPE.DOCTOR.getRole().equals(user.getRole())) {
      MyTodoListResponse result = new MyTodoListResponse();
      result.setMessage("無權限");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
    }
    return ResponseEntity.ok(xmlService.getMyTodoList(user, column, asc, perPageInt, page));
  }
  
  @ApiOperation(value = "取得我的清單-比對警示", notes = "取得我的清單-比對警示")
  @GetMapping("/warning")
  public ResponseEntity<WarningOrderResponse> getWarningOrder(
      @ApiParam(name = "orderBy",
      value = "排序欄位名稱，sdate:就醫日期-起，edate:就醫日期-訖，inhMrId:病歷號碼，name:患者姓名，"
          + "inhClinicId:就醫記錄編號，funcType:科別代碼，funcTypec:科別，prsnId:醫護代碼，prsnName:醫護姓名，"
          + "changeIcd:ICD碼異動，changeOrder:支付標準代碼異動，changeInh:院內碼異動，changeSo:S.O.異動，"
          + "changeOther:其他資訊異動，noticeTimes:通知狀態， applId:負責人員代碼，applName:負責人員，status:資料狀態",
      example = "sdate") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小，空值表示不排序",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "頁碼，第一頁值為0，第二頁值為1…",
          example = "0") @RequestParam(required = false, defaultValue = "0") Integer page) {
    int perPageInt = (perPage == null) ? parameters.getIntParameter(ParametersService.PAGE_COUNT)
        : perPage.intValue();

    String column = orderBy;
    if (column != null) {
      if (column.equals("sdate")) {
        column = "startDate";
      } else if (column.equals("edate")) {
        column = "endDate";
      } else if (column.equals("totalDot")) {
        column = "tDot";
      } else if (!findDeclaredMethod("tw.com.leadtek.nhiwidget.model.rdb.MY_MR", column)) {
        WarningOrderResponse result = new WarningOrderResponse();
        result.setMessage("orderBy無此欄位：" + column);
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      WarningOrderResponse result = new WarningOrderResponse();
      result.setMessage("無法取得登入狀態");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
    if (ROLE_TYPE.DOCTOR.getRole().equals(user.getRole())) {
      WarningOrderResponse result = new WarningOrderResponse();
      result.setMessage("無權限");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
    }
    return ResponseEntity.ok(xmlService.getWarningOrderList(user, column, asc, perPageInt, page));
  }
  
  @ApiOperation(value = "取得我的清單-疑問標示", notes = "取得我的清單-疑問標示")
  @GetMapping("/question")
  public ResponseEntity<QuestionMarkResponse> getQuestionMark(
      @ApiParam(name = "orderBy",
      value = "排序欄位名稱，sdate:就醫日期-起，edate:就醫日期-訖，inhMrId:病歷號碼，name:患者姓名，"
          + "inhClinicId:就醫記錄編號，funcType:科別代碼，funcTypec:科別，prsnId:醫護代碼，prsnName:醫護姓名，"
          + "noticeDate:最新通知日期，applId:負責人員代碼，applName:負責人員，noticeTimes:最新通知狀態，readedPpl:最新讀取狀態",
      example = "sdate") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小，空值表示不排序",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "頁碼，第一頁值為0，第二頁值為1…",
          example = "0") @RequestParam(required = false, defaultValue = "0") Integer page) {
    int perPageInt = (perPage == null) ? parameters.getIntParameter(ParametersService.PAGE_COUNT)
        : perPage.intValue();

    String column = orderBy;
    if (column != null) {
      if (column.equals("sdate")) {
        column = "startDate";
      } else if (column.equals("edate")) {
        column = "endDate";
      } else if (column.equals("totalDot")) {
        column = "tDot";
      } else if (!findDeclaredMethod("tw.com.leadtek.nhiwidget.model.rdb.MY_MR", column)) {
        QuestionMarkResponse result = new QuestionMarkResponse();
        result.setMessage("orderBy無此欄位：" + column);
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      QuestionMarkResponse result = new QuestionMarkResponse();
      result.setMessage("無法取得登入狀態");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
    return ResponseEntity.ok(xmlService.getQuestionMark(user, column, asc, perPageInt, page));
  }
  
  @ApiOperation(value = "取得我的清單-通知記錄", notes = "取得我的清單-比對警示")
  @GetMapping("/notice")
  public ResponseEntity<NoticeRecordResponse> getNoticeRecord(
      @ApiParam(name = "orderBy",
      value = "排序欄位名稱，status:資料狀態，sdate:就醫日期-起，edate:就醫日期-訖，inhMrId:病歷號碼，name:患者姓名，"
          + "inhClinicId:就醫記錄編號，funcType:科別代碼，funcTypec:科別，prsnId:醫護代碼，prsnName:醫護姓名，"
          + "noticeDate:通知日期，applId:負責人員代碼，applName:負責人員，noticeTimes:最新通知狀態，readedPpl:最新讀取狀態",
      example = "sdate") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小，空值表示不排序",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "頁碼，第一頁值為0，第二頁值為1…",
          example = "0") @RequestParam(required = false, defaultValue = "0") Integer page) {
    int perPageInt = (perPage == null) ? parameters.getIntParameter(ParametersService.PAGE_COUNT)
        : perPage.intValue();

    String column = orderBy;
    if (column != null) {
      if (column.equals("sdate")) {
        column = "startDate";
      } else if (column.equals("edate")) {
        column = "endDate";
      } else if (!findDeclaredMethod("tw.com.leadtek.nhiwidget.model.rdb.MR_NOTICE", column)) {
        NoticeRecordResponse result = new NoticeRecordResponse();
        result.setMessage("orderBy無此欄位：" + column);
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      NoticeRecordResponse result = new NoticeRecordResponse();
      result.setMessage("無法取得登入狀態");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
    return ResponseEntity.ok(xmlService.getNoticeRecord(user, column, asc, perPageInt, page));
  }
  
  @ApiOperation(value = "取得我的清單-醫師查看清單", notes = "取得我的清單-醫師查看清單")
  @GetMapping("/doctor")
  public ResponseEntity<DoctorListResponse> getDoctorList(
      @ApiParam(name = "orderBy",
      value = "排序欄位名稱，sdate:就醫日期-起，edate:就醫日期-訖，inhMrId:病歷號碼，name:患者姓名，"
          + "inhClinicId:就醫記錄編號，funcType:科別代碼，funcTypec:科別，prsnId:醫護代碼，prsnName:醫護姓名，"
          + "applId:負責人員代碼，applName:負責人員，readedPpl:最新讀取狀態",
      example = "sdate") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小，空值表示不排序",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "頁碼，第一頁值為0，第二頁值為1…",
          example = "0") @RequestParam(required = false, defaultValue = "0") Integer page) {
    int perPageInt = (perPage == null) ? parameters.getIntParameter(ParametersService.PAGE_COUNT)
        : perPage.intValue();

    String column = orderBy;
    if (column != null) {
      if (column.equals("sdate")) {
        column = "startDate";
      } else if (column.equals("edate")) {
        column = "endDate";
      } else if (column.equals("totalDot")) {
        column = "tDot";
      } else if (!findDeclaredMethod("tw.com.leadtek.nhiwidget.model.rdb.MY_MR", column)) {
        DoctorListResponse result = new DoctorListResponse();
        result.setMessage("orderBy無此欄位：" + column);
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      DoctorListResponse result = new DoctorListResponse();
      result.setMessage("無法取得登入狀態");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
    return ResponseEntity.ok(xmlService.getDoctorList(user, column, asc, perPageInt, page));
  }
  
}
