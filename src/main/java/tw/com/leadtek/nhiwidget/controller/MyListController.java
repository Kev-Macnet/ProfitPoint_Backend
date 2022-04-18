/**
 * Created on 2021/11/18.
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
import tw.com.leadtek.nhiwidget.payload.my.DoctorListResponse;
import tw.com.leadtek.nhiwidget.payload.my.MyTodoListResponse;
import tw.com.leadtek.nhiwidget.payload.my.NoticeRecordResponse;
import tw.com.leadtek.nhiwidget.payload.my.QuestionMarkResponse;
import tw.com.leadtek.nhiwidget.payload.my.SecondFilterParameter;
import tw.com.leadtek.nhiwidget.payload.my.WarningOrderResponse;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.service.NHIWidgetXMLService;
import tw.com.leadtek.nhiwidget.service.ParametersService;
import tw.com.leadtek.tools.DateTool;

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
      @ApiParam(name = "sdate", value = "起始日期，格式 yyyy/MM/dd",
      example = "2021/03/15") @RequestParam(required = false) String sdate,
    @ApiParam(name = "edate", value = "結束日期，格式 yyyy/MM/dd",
      example = "2021/03/18") @RequestParam(required = false) String edate,
    @ApiParam(name = "isOp", value = "就醫類別為門急診",
      example = "true") @RequestParam(required = false) Boolean isOp,
    @ApiParam(name = "isIp", value = "就醫類別為住院",
      example = "true") @RequestParam(required = false) Boolean isIp,
    @ApiParam(name = "funcType", value = "科別代碼，如：00(不分科)，01(家醫科)，02(內科)，03(外科)...",
      example = "03") @RequestParam(required = false) String funcType,
    @ApiParam(name = "funcTypec", value = "科別名稱，如：不分科、家醫科、內科、外科...",
      example = "家醫科") @RequestParam(required = false) String funcTypec,
    @ApiParam(name = "prsnId", value = "醫護代碼",
      example = "A123456789") @RequestParam(required = false) String prsnId,
    @ApiParam(name = "prsnName", value = "醫護姓名",
      example = "王小明") @RequestParam(required = false) String prsnName,
    @ApiParam(name = "applId", value = "負責人員代碼",
      example = "A123456789") @RequestParam(required = false) String applId,
    @ApiParam(name = "applName", value = "負責人員姓名",
      example = "王小明") @RequestParam(required = false) String applName,
    @ApiParam(value = "病歷狀態，0:待處理，-1:疑問標示",
      example = "0") @RequestParam(required = false) Integer status,
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
    String dataFormat = null;
    if (isOp != null && isOp.booleanValue()) {
      if (isIp != null && isIp) {
        // dataFormat = null;
      } else {
        dataFormat = "10";
      }
    } else if (isIp != null && isIp.booleanValue()) {
      dataFormat = "20";
    }
   
    Date startDate = null;
    Date endDate = null;

    if (sdate != null && edate != null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      try {
        startDate = sdf.parse(sdate);
        endDate = sdf.parse(edate);
        if (startDate.after(endDate)) {
          MyTodoListResponse result = new MyTodoListResponse();
          result.setMessage("啟始日不可大於結束日");
          result.setResult("failed");
          return ResponseEntity.badRequest().body(result);
        }
      } catch (ParseException e) {
        MyTodoListResponse result = new MyTodoListResponse();
        result.setMessage("日期格式有誤");
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    return ResponseEntity.ok(xmlService.getMyTodoList(user, startDate, endDate, dataFormat, 
        funcType, funcTypec, prsnId, prsnName, applId, applName, status, column, asc, perPageInt, page));
  }
  
  @ApiOperation(value = "取得我的清單-比對警示", notes = "取得我的清單-比對警示")
  @GetMapping("/warning")
  public ResponseEntity<WarningOrderResponse> getWarningOrder(
    @ApiParam(name = "sdate", value = "起始日期，格式 yyyy/MM/dd",
      example = "2021/03/15") @RequestParam(required = false) String sdate,
    @ApiParam(name = "edate", value = "結束日期，格式 yyyy/MM/dd",
      example = "2021/03/18") @RequestParam(required = false) String edate,
    @ApiParam(name = "isOp", value = "就醫類別為門急診",
      example = "true") @RequestParam(required = false) Boolean isOp,
    @ApiParam(name = "isIp", value = "就醫類別為住院",
      example = "true") @RequestParam(required = false) Boolean isIp,
    @ApiParam(name = "funcType", value = "科別代碼，如：00(不分科)，01(家醫科)，02(內科)，03(外科)...",
      example = "03") @RequestParam(required = false) String funcType,
    @ApiParam(name = "funcTypec", value = "科別名稱，如：不分科、家醫科、內科、外科...",
      example = "家醫科") @RequestParam(required = false) String funcTypec,
    @ApiParam(name = "prsnId", value = "醫護代碼",
      example = "A123456789") @RequestParam(required = false) String prsnId,
    @ApiParam(name = "prsnName", value = "醫護姓名",
      example = "王小明") @RequestParam(required = false) String prsnName,
    @ApiParam(name = "applId", value = "負責人員代碼",
      example = "A123456789") @RequestParam(required = false) String applId,
    @ApiParam(name = "applName", value = "負責人員姓名",
      example = "王小明") @RequestParam(required = false) String applName,
    @ApiParam(value = "取得異動病歷，icd:取得ICD碼有異動的病歷，order:取得支付標準代碼有異動的病歷，inh:取得院內碼有異動的病歷，so:取得S.O.異動病歷，other:取得其他資訊有異動病歷，notify:取得已通知病歷，notnotify:取得未通知病歷",
      example = "icd") @RequestParam(required = false) String block,
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
    
    SecondFilterParameter sfp = new SecondFilterParameter(getUserDetails(), isOp, isIp, sdate, edate, block, page);
    if (sfp.getMessage() != null) {
      WarningOrderResponse result = new WarningOrderResponse();
      result.setMessage(sfp.getMessage());
      result.setResult("failed");
      return ResponseEntity.badRequest().body(result);
    }
    
    sfp.setPerPage((perPage == null) ? parameters.getIntParameter(ParametersService.PAGE_COUNT)
        : perPage.intValue());
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
    sfp.setOrderBy(column);
    
    if (sfp.getUser() == null) {
      WarningOrderResponse result = new WarningOrderResponse();
      result.setMessage("無法取得登入狀態");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
    if (ROLE_TYPE.DOCTOR.getRole().equals(sfp.getUser().getRole())) {
      WarningOrderResponse result = new WarningOrderResponse();
      result.setMessage("無權限");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
    }
   
    return ResponseEntity.ok(xmlService.getWarningOrderList(sfp));
  }
  
  @ApiOperation(value = "取得我的清單-疑問標示", notes = "取得我的清單-疑問標示")
  @GetMapping("/question")
  public ResponseEntity<QuestionMarkResponse> getQuestionMark(
    @ApiParam(name = "applYm", value = "申報西元年月，格式 yyyyMM",
      example = "202103") @RequestParam(required = false) String applYm,
    @ApiParam(name = "sdate", value = "起始日期，格式 yyyy/MM/dd",
      example = "2021/03/15") @RequestParam(required = false) String sdate,
    @ApiParam(name = "edate", value = "結束日期，格式 yyyy/MM/dd",
      example = "2021/03/18") @RequestParam(required = false) String edate,
    @ApiParam(name = "isOp", value = "就醫類別為門急診",
      example = "true") @RequestParam(required = false) Boolean isOp,
    @ApiParam(name = "isIp", value = "就醫類別為住院",
      example = "true") @RequestParam(required = false) Boolean isIp,
    @ApiParam(name = "funcType", value = "科別代碼，如：00(不分科)，01(家醫科)，02(內科)，03(外科)...",
      example = "03") @RequestParam(required = false) String funcType,
    @ApiParam(name = "funcTypec", value = "科別名稱，如：不分科、家醫科、內科、外科...",
      example = "家醫科") @RequestParam(required = false) String funcTypec,
    @ApiParam(name = "prsnId", value = "醫護代碼",
      example = "A123456789") @RequestParam(required = false) String prsnId,
    @ApiParam(name = "prsnName", value = "醫護姓名",
      example = "王小明") @RequestParam(required = false) String prsnName,
    @ApiParam(name = "applId", value = "負責人員代碼",
      example = "A123456789") @RequestParam(required = false) String applId,
    @ApiParam(name = "applName", value = "負責人員姓名",
      example = "王小明") @RequestParam(required = false) String applName,
    @ApiParam(value = "取得疑問標示上方區塊病歷，notify:已通知，notnotify:未通知，read:已讀取，unread:未讀取",
      example = "notify") @RequestParam(required = false) String block,
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

    if ("undefined".equals(applYm)) {
    	applYm = null;
    }
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
    String dataFormat = null;
    if (isOp != null && isOp.booleanValue()) {
      if (isIp != null && isIp) {
        // null
      } else {
        dataFormat = "10";
      }
    } else if (isIp != null && isIp.booleanValue()) {
      dataFormat = "20";
    }
   
    java.sql.Date startDate = null;
    java.sql.Date endDate = null;

    if (sdate != null && edate != null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      try {
        startDate = new java.sql.Date(sdf.parse(sdate).getTime());
        endDate = new java.sql.Date(sdf.parse(edate).getTime());
        if (startDate.after(endDate)) {
          QuestionMarkResponse result = new QuestionMarkResponse();
          result.setMessage("啟始日不可大於結束日");
          result.setResult("failed");
          return ResponseEntity.badRequest().body(result);
        }
      } catch (ParseException e) {
        QuestionMarkResponse result = new QuestionMarkResponse();
        result.setMessage("日期格式有誤");
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    return ResponseEntity.ok(xmlService.getQuestionMark(user, applYm,
        startDate, endDate, dataFormat, funcType, funcTypec, prsnId, prsnName, 
        applId, applName, block, column, asc, perPageInt, page));
  }
  
  @ApiOperation(value = "取得我的清單-通知記錄", notes = "取得我的清單-通知記錄")
  @GetMapping("/notice")
  public ResponseEntity<NoticeRecordResponse> getNoticeRecord(
    @ApiParam(name = "applYm", value = "申報西元年月，格式 yyyyMM",
      example = "202103") @RequestParam(required = false) String applYm,
    @ApiParam(name = "sdate", value = "起始日期，格式 yyyy/MM/dd",
      example = "2021/03/15") @RequestParam(required = false) String sdate,
    @ApiParam(name = "edate", value = "結束日期，格式 yyyy/MM/dd",
      example = "2021/03/18") @RequestParam(required = false) String edate,
    @ApiParam(name = "isOp", value = "就醫類別為門急診",
      example = "true") @RequestParam(required = false) Boolean isOp,
    @ApiParam(name = "isIp", value = "就醫類別為住院",
      example = "true") @RequestParam(required = false) Boolean isIp,
    @ApiParam(name = "funcType", value = "科別代碼，如：00(不分科)，01(家醫科)，02(內科)，03(外科)...",
      example = "03") @RequestParam(required = false) String funcType,
    @ApiParam(name = "funcTypec", value = "科別名稱，如：不分科、家醫科、內科、外科...",
      example = "家醫科") @RequestParam(required = false) String funcTypec,
    @ApiParam(name = "prsnId", value = "醫護代碼",
      example = "A123456789") @RequestParam(required = false) String prsnId,
    @ApiParam(name = "prsnName", value = "醫護姓名",
      example = "王小明") @RequestParam(required = false) String prsnName,
    @ApiParam(name = "applId", value = "負責人員代碼",
      example = "A123456789") @RequestParam(required = false) String applId,
    @ApiParam(name = "applName", value = "負責人員姓名",
      example = "王小明") @RequestParam(required = false) String applName,
    @ApiParam(value = "通知記錄清單上方區塊病歷，notify:已通知，read:已讀取，unread:未讀取",
      example = "notify") @RequestParam(required = false) String block,
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
      } else if (column.equals("noticeTimes")) {
        column = "seq";
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
    String dataFormat = null;
    if (isOp != null && isOp.booleanValue()) {
      if (isIp != null && isIp) {
        // null
      } else {
        dataFormat = "10";
      }
    } else if (isIp != null && isIp.booleanValue()) {
      dataFormat = "20";
    }
   
    Date startDate = null;
    Date endDate = null;

    if (sdate != null && edate != null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      try {
        startDate = sdf.parse(sdate);
        endDate = sdf.parse(edate);
        if (startDate.after(endDate)) {
          NoticeRecordResponse result = new NoticeRecordResponse();
          result.setMessage("啟始日不可大於結束日");
          result.setResult("failed");
          return ResponseEntity.badRequest().body(result);
        }
      } catch (ParseException e) {
        NoticeRecordResponse result = new NoticeRecordResponse();
        result.setMessage("日期格式有誤");
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    return ResponseEntity.ok(xmlService.getNoticeRecord(user, applYm,
        startDate, endDate, dataFormat, funcType, funcTypec, prsnId, prsnName, 
        applId, applName, block, column, asc, perPageInt, page));
  }
  
  @ApiOperation(value = "取得我的清單-醫師查看清單", notes = "取得我的清單-醫師查看清單")
  @GetMapping("/doctor")
  public ResponseEntity<DoctorListResponse> getDoctorList(
      @ApiParam(value = "取得已讀取或未讀取的病歷，read:取得已讀取的病歷，unread:取得未讀取的病歷",
         example = "read") @RequestParam(required = false) String block,
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
    return ResponseEntity.ok(xmlService.getDoctorList(user, block, column, asc, perPageInt, page));
  }
  
}
