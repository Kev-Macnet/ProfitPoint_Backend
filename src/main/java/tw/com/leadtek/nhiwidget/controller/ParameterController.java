/**
 * Created on 2021/5/19.
 */
package tw.com.leadtek.nhiwidget.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import tw.com.leadtek.nhiwidget.annotation.LogDefender;
import tw.com.leadtek.nhiwidget.constant.LogType;
import tw.com.leadtek.nhiwidget.constant.ROLE_TYPE;
import tw.com.leadtek.nhiwidget.payload.AssignedPoints;
import tw.com.leadtek.nhiwidget.payload.AssignedPointsListResponse;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.CodeConflictListResponse;
import tw.com.leadtek.nhiwidget.payload.CodeConflictPayload;
import tw.com.leadtek.nhiwidget.payload.DRGRelatedValues;
import tw.com.leadtek.nhiwidget.payload.HighRatioOrder;
import tw.com.leadtek.nhiwidget.payload.HighRatioOrderListResponse;
import tw.com.leadtek.nhiwidget.payload.InfectiousListResponse;
import tw.com.leadtek.nhiwidget.payload.ParameterListPayload;
import tw.com.leadtek.nhiwidget.payload.ParameterValue;
import tw.com.leadtek.nhiwidget.payload.PointsValue;
import tw.com.leadtek.nhiwidget.payload.RareICDListResponse;
import tw.com.leadtek.nhiwidget.payload.RareICDPayload;
import tw.com.leadtek.nhiwidget.payload.SameATCListResponse;
import tw.com.leadtek.nhiwidget.payload.my.MyTodoListResponse;
import tw.com.leadtek.nhiwidget.payload.my.WarningOrderResponse;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.service.ParametersService;
import tw.com.leadtek.tools.DateTool;

@Api(tags = "參數設定相關API", value = "參數設定相關API")
@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
@RequestMapping(value = "/p", produces = "application/json; charset=utf-8")
public class ParameterController extends BaseController {

  @Autowired
  private ParametersService parameterService;

  @ApiOperation(value = "取得是否使用西醫、牙醫總額度支配點數設定", notes = "取得是否使用西醫、牙醫總額度支配點數設定")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/assignedPoints")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<AssignedPointsListResponse> getAssignedPoints(
      @ApiParam(name = "sdate", value = "起始日期",
          example = "2021/01/01") @RequestParam(required = false) String sdate,
      @ApiParam(name = "edate", value = "結束日期",
          example = "2021/12/31") @RequestParam(required = false) String edate,
      @ApiParam(name = "orderBy", value = "排序欄位名稱，sdate:生效日，edate:生效訖日，wp:西醫分配總點數，dp:牙科分配總點數",
          example = "sdate") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小，空值表示不排序",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "第幾頁，第一頁值為0", example = "0") @RequestParam(required = false,
          defaultValue = "0") Integer page) {
    int perPageInt =
        (perPage == null) ? parameterService.getIntParameter(ParametersService.PAGE_COUNT)
            : perPage.intValue();

    Date startDate = null;
    Date endDate = null;
    if (sdate != null && edate != null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      try {
        startDate = sdf.parse(sdate);
        endDate = sdf.parse(edate);
        
        if (startDate.after(endDate)) {
          AssignedPointsListResponse result = new AssignedPointsListResponse();
          result.setMessage("啟始日不可大於結束日");
          result.setResult("failed");
          return ResponseEntity.badRequest().body(result);
        }
      } catch (ParseException e) {
        AssignedPointsListResponse result = new AssignedPointsListResponse();
        result.setMessage("日期格式有誤");
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    String column = orderBy;
    if (column != null) {
      if (column.equals("sdate")) {
        column = "startDate";
      } else if (column.equals("edate")) {
        column = "endDate";
      } else if (column.equals("wp") || column.equals("wmP")) {
        column = "wmp";
      } else if (column.equals("dp") || column.equals("dP")) {
        column = "dp";
      } else if (column.equals("status")) {
        column = "startDate";
      } else {
        AssignedPointsListResponse result = new AssignedPointsListResponse();
        result.setMessage("orderBy無此欄位：" + column);
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    return ResponseEntity.ok(
        parameterService.getAssignedPoints(startDate, endDate, column, asc, perPageInt, page.intValue()));
  }

  @ApiOperation(value = "新增分配點數", notes = "新增分配點數")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "新增成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PostMapping("/assignedPoints")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增分配點數")
  public ResponseEntity<BaseResponse> newAssignedPoints(@RequestBody AssignedPoints ap) {
    if (ap.getEdate() == null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      try {
        ap.setEdate(sdf.parse(DateTool.MAX_DATE));
      } catch (ParseException e) {
      }
    }
    return returnAPIResult(parameterService.newAssignedPoints(ap));
  }

  @ApiOperation(value = "修改分配總點數設定", notes = "修改分配總點數設定")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/assignedPoints")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改分配總點數設定")
  public ResponseEntity<BaseResponse> updateAssignedPoints(@RequestBody AssignedPoints ap) {
	  
	httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{ap.getId()}));
	  
    return returnAPIResult(parameterService.updateAssignedPoints(ap));
  }

  @ApiOperation(value = "刪除分配總點數", notes = "刪除分配總點數")
  @DeleteMapping("/assignedPoints/{id}")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除分配總點數")
  public ResponseEntity<BaseResponse> deleteAssignedPoints(@PathVariable String id) {
    if (id == null || id.length() == 0) {
      return returnAPIResult("id未帶入");
    }
    long idL;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      return returnAPIResult("id格式有誤");
    }
    
    httpServletReq.setAttribute(LogType.ACTION_D.name()+"_PKS", Arrays.asList(new Long[]{idL}));

    return returnAPIResult(parameterService.deleteAssignedPoints(idL));
  }

  @ApiOperation(value = "取得分配總點數", notes = "取得分配總點數")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/assignedPoints/{id}")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<AssignedPoints> getAssignedPoints(@PathVariable String id) {
    if (id == null || id.length() == 0) {
      return ResponseEntity.badRequest().body(new AssignedPoints());
    }
    long idL = Long.parseLong(id);
    AssignedPoints result = parameterService.getAssignedPoints(idL);
    if (result == null) {
      result = new AssignedPoints();
      return ResponseEntity.badRequest().body(result);
    }
    return ResponseEntity.ok(result);
  }

  // @ApiOperation(value = "取得支配總點數", notes = "取得支配總點數")
  // @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  // @GetMapping("/pointsValue")
  // public ResponseEntity<PointsValue> getPointsValue(@ApiParam(name = "startDate", value = "生效日",
  // example = "2021/05/01") @RequestParam(required = true) String startDate) {
  // SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
  // PointsValue result = null;
  // try {
  // Date sDate = sdf.parse(startDate);
  // result = parameterService.getPointsValue(sDate);
  // if (result == null) {
  // return ResponseEntity.badRequest().body(null);
  // }
  // return ResponseEntity.ok(result);
  // } catch (ParseException e) {
  // return ResponseEntity.badRequest().body(result);
  // }
  // }

  @ApiOperation(value = "修改支配總點數", notes = "修改支配總點數")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/pointsValue")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改支配總點數")
  public ResponseEntity<BaseResponse> updatePointsValue(@RequestBody PointsValue pv) {
	  
	  httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{pv.getId()}));
	  
    return returnAPIResult(parameterService.updatePointsValue(pv));
  }

  @ApiOperation(value = "新增分配總點數", notes = "修改支配總點數")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PostMapping("/pointsValue")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增分配總點數")
  public ResponseEntity<BaseResponse> newPointsValue(@RequestBody PointsValue pv) {
    if (pv.getEdate() == null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      try {
        pv.setEdate(sdf.parse(DateTool.MAX_DATE));
      } catch (ParseException e) {
      }
    }
    return returnAPIResult(parameterService.newPointsValue(pv));
  }

  @ApiOperation(value = "取得參數值", notes = "取得參數值")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/value")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<ParameterListPayload> getValue(
      @ApiParam(name = "name", value = "參數值名稱，如抽件數(SAMPLING),標準給付額(SPR)",
          example = "SPR") @RequestParam(required = true) String name,
      @ApiParam(name = "sdate", value = "生效日",
          example = "2019/07/01") @RequestParam(required = false) String sdate,
      @ApiParam(name = "edate", value = "生效訖日",
          example = "2021/12/31") @RequestParam(required = false) String edate,
      @ApiParam(name = "orderBy", value = "排序欄位名稱，sdate:生效日，edate:失效日，spr:SPR點數，status:啟用狀態",
          example = "sdate") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小，空值表示不排序",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "頁碼，第一頁值為0，第二頁值為1…",
          example = "0") @RequestParam(required = false, defaultValue = "0") Integer page) {
    int perPageInt =
        (perPage == null) ? parameterService.getIntParameter(ParametersService.PAGE_COUNT)
            : perPage.intValue();
    Date startDate = null;
    Date endDate = null;

    if (sdate != null && edate != null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      try {
        startDate = sdf.parse(sdate);
        endDate = sdf.parse(edate);
        
        if (startDate.after(endDate)) {
          ParameterListPayload result = new ParameterListPayload();
          result.setMessage("啟始日不可大於結束日");
          result.setResult("failed");
          return ResponseEntity.badRequest().body(result);
        }
      } catch (ParseException e) {
        ParameterListPayload result = new ParameterListPayload();
        result.setMessage("日期格式有誤");
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    String column = orderBy;
    if (column != null) {
      if (column.equals("sdate")) {
        column = "startDate";
      } else if (column.equals("edate")) {
        column = "endDate";
      } else if (column.equals("spr") || column.equals("sampling")) {
        column = "value";
      } else if (column.equals("status")) {
        column = "startDate";
      } else {
        ParameterListPayload result = new ParameterListPayload();
        result.setMessage("orderBy無此欄位：" + column);
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    return ResponseEntity.ok(parameterService.getParameterValue(name, startDate, endDate, column,
        asc, perPageInt, page.intValue()));
  }

  @ApiOperation(value = "取得指定id參數值", notes = "取得指定id參數值")
  @GetMapping("/value/{id}")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<ParameterValue> getValue(@PathVariable String id) {
    if (id == null || id.length() == 0) {
      ParameterValue result = new ParameterValue();
      result.setValue("id未帶入");
      return ResponseEntity.badRequest().body(result);
    }
    return ResponseEntity.ok(parameterService.getParameterValue(Long.parseLong(id)));
  }

  @ApiOperation(value = "新增參數值", notes = "新增參數值")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PostMapping("/value")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增參數值")
  public ResponseEntity<BaseResponse> newValue(
      @ApiParam(name = "name", value = "參數值名稱，如抽件數(SAMPLING),標準給付額(SPR)",
          example = "SPR") @RequestParam(required = true) String name,
      @ApiParam(name = "value", value = "參數值",
          example = "1234") @RequestParam(required = true) String value,
      @ApiParam(name = "sdate", value = "生效日",
          example = "2021/05/01") @RequestParam(required = true) Date sdate,
      @ApiParam(name = "edate", value = "生效訖日",
          example = "2021/12/31") @RequestParam(required = false) Date edate) {
    if (value == null || value.length() == 0 || "null".equals(value.toLowerCase())) {
      return returnAPIResult("value值不可為空");
    }
    Date endDate = edate;
    if (endDate == null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      try {
        endDate = sdf.parse(DateTool.MAX_DATE);
      } catch (ParseException e) {
      }
    }
    if (endDate.before(sdate)) {
      return returnAPIResult("生效訖日不可小於生效日");
    }
    if (endDate.getTime() == sdate.getTime()) {
      return returnAPIResult("生效訖日不可等於生效日");
    }
    if (value == null || value.length() < 1 || "null".equals(value)) {
      return returnAPIResult("value值不可為空");
    }
    return returnAPIResult(parameterService.newValue(name, value, sdate, endDate));
  }

  @ApiOperation(value = "修改參數值", notes = "修改參數值")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/value")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改參數值")
  public ResponseEntity<BaseResponse> updateValue(@RequestBody ParameterValue pv) {
    if (pv.getValue() == null || pv.getValue().toString().length() == 0
        || "null".equals(pv.getValue())) {
      return returnAPIResult("value值不可為空");
    }
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{pv.getId()}));
    
    return returnAPIResult(parameterService.updateValue(pv));
  }

  @ApiOperation(value = "刪除參數值", notes = "刪除參數值")
  @DeleteMapping("/value/{id}")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除參數值")
  public ResponseEntity<BaseResponse> deleteValue(@PathVariable String id) {
    if (id == null || id.length() == 0) {
      return returnAPIResult("id未帶入");
    }
    
    httpServletReq.setAttribute(LogType.ACTION_D.name()+"_PKS", Arrays.asList(new String[]{id}));
    
    return returnAPIResult(parameterService.deleteParameterValue(id));
  }

  @ApiOperation(value = "取得DRG相關參數值", notes = "取得DRG相關參數值")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/drg")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<DRGRelatedValues> getDRGValues(
      @ApiParam(name = "sdate", value = "生效日",
          example = "2021/07/01") @RequestParam(required = false) String sdate,
      @ApiParam(name = "edate", value = "生效訖日",
          example = "2021/12/31") @RequestParam(required = false) String edate,
      @ApiParam(name = "isMax", value = "最新一筆DRG參數值",
          example = "2021/12/31") @RequestParam(required = false) Boolean isMax) {

    if (isMax != null && isMax.booleanValue()) {
      return ResponseEntity.ok(parameterService.getDRGValues(null, null, null));
    }
    DRGRelatedValues result = new DRGRelatedValues();
    if (sdate == null || sdate.length() == 0 || edate == null || edate.length() == 0) {
      result.setMessage("日期格式有誤");
      result.setResult("failed");
      return ResponseEntity.badRequest().body(result);
    }
    result.setStartDate(sdate);
    result.setEndDate(edate);

    SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
    Date startDate = null;
    Date endDate = null;

    try {
      startDate = sdf.parse(sdate);
      endDate = sdf.parse(edate);
    } catch (ParseException e) {
      result.setMessage("日期格式有誤");
      result.setResult("failed");
      return ResponseEntity.badRequest().body(result);
    }

    return ResponseEntity.ok(parameterService.getDRGValues(startDate, endDate, result));
  }

  @ApiOperation(value = "新增DRG參數值", notes = "新增參數值")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PostMapping("/drg")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增DRG參數值")
  public ResponseEntity<BaseResponse> newDRGValues(@RequestBody DRGRelatedValues request) {

    BaseResponse result = new BaseResponse();
    SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
    Date sDate = null;
    Date eDate = null;

    try {
      sDate = sdf.parse(request.getStartDate());
      eDate = sdf.parse(request.getEndDate());
      if (eDate.before(sDate)) {
        return returnAPIResult("訖日不可小於起日");
      }
    } catch (ParseException e) {
      return returnAPIResult("日期格式有誤");
    }
    return returnAPIResult(parameterService.newDRGValues(sDate, eDate, request));
  }

  @ApiOperation(value = "修改DRG參數值", notes = "修改DRG參數值")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/drg")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改DRG參數值")
  public ResponseEntity<BaseResponse> updateDRGValue(@RequestBody DRGRelatedValues request) {
	  
	  httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{request.getId()}));
	  
    return returnAPIResult(parameterService.updateDRGValue(request));
  }

  @ApiOperation(value = "取得法定傳染病列表", notes = "取得法定傳染病列表")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/infectious")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<InfectiousListResponse> getInfectious(
      @ApiParam(name = "icd", value = "ICD代碼",
          example = "J10.01") @RequestParam(required = false) String icd,
      @ApiParam(name = "cat", value = "傳染病分類，1~5",
          example = "4") @RequestParam(required = false) String cat,
      @ApiParam(name = "orderBy", value = "排序欄位名稱，icd:診斷ICD代碼，name:診斷名稱，cat:傳染病分類",
          example = "sdate") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小，空值表示不排序",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "頁碼，第一頁值為0，第二頁值為1…",
          example = "0") @RequestParam(required = false, defaultValue = "0") Integer page) {

    int perPageInt =
        (perPage == null) ? parameterService.getIntParameter(ParametersService.PAGE_COUNT)
            : perPage.intValue();

    String column = orderBy;
    if (column != null) {
      if (column.equals("icd")) {
        column = "code";
      } else if (column.equals("name")) {
        column = "descChi";
      } else if (column.equals("cat")) {
        column = "parentCode";
      } else {
        InfectiousListResponse response = new InfectiousListResponse();
        response.setMessage("orderBy無此欄位：" + orderBy);
        response.setResult("failed");
        return ResponseEntity.badRequest().body(response);
      }
    }
    return ResponseEntity
        .ok(parameterService.getInfectious(icd, cat, column, asc, perPageInt, page));
  }

  @ApiOperation(value = "修改法定傳染病啟用狀態", notes = "修改法定傳染病啟用狀態")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/infectious")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改法定傳染病啟用狀態")
  public ResponseEntity<BaseResponse> updateInfectiousStatus(
      @ApiParam(name = "icd", value = "ICD代碼",
          example = "J10.01") @RequestParam(required = true) String icd,
      @ApiParam(name = "enable", value = "是否啟用，true/false",
          example = "true") @RequestParam(required = true) Boolean enable) {
	  
    return returnAPIResult(parameterService.updateInfectiousStatus(icd, enable.booleanValue()));
  }

  @ApiOperation(value = "取得罕見ICD代碼列表", notes = "取得罕見ICD代碼列表")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/rareICD")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<RareICDListResponse> getRareICD(
      @ApiParam(name = "icd", value = "ICD代碼",
          example = "J10.01") @RequestParam(required = false) String icd,
      @ApiParam(name = "orderBy", value = "排序欄位名稱，sdate:生效日，edate:失效日，icd:診斷/處置ICD，status:啟用狀態",
          example = "sdate") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小，空值表示不排序",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "頁碼，第一頁值為0，第二頁值為1…",
          example = "0") @RequestParam(required = false, defaultValue = "0") Integer page) {

    String code = (icd == null) ? null : HtmlUtils.htmlEscape(icd);
    int perPageInt =
        (perPage == null) ? parameterService.getIntParameter(ParametersService.PAGE_COUNT)
            : perPage.intValue();
    String column = orderBy;
    if (column != null) {
      if (column.equals("sdate")) {
        column = "startDate";
      } else if (column.equals("edate")) {
        column = "endDate";
      } else if (column.equals("icd")) {
        column = "code";
      } else if (column.equals("status")) {
        column = orderBy;
      } else {
        RareICDListResponse response = new RareICDListResponse();
        response.setMessage("orderBy無此欄位：" + orderBy);
        response.setResult("failed");
        return ResponseEntity.badRequest().body(response);
      }
    }
    return ResponseEntity.ok(parameterService.getRareICD(code, column, asc, perPageInt, page));
  }

  @ApiOperation(value = "取得指定的罕見ICD應用參數", notes = "取得指定的罕見ICD應用參數")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/rareICD/{id}")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<RareICDPayload> getRareICDById(@PathVariable String id) {
    RareICDPayload response = new RareICDPayload();
    if (id == null || id.length() == 0) {
      response.setName("id未帶入");
      return ResponseEntity.badRequest().body(response);
    }
    response = parameterService.getRareICDById(id);
    if (response.getId() == null) {
      return ResponseEntity.badRequest().body(response);
    } else {
      return ResponseEntity.ok(response);
    }
  }

  @ApiOperation(value = "新增罕見ICD代碼", notes = "新增罕見ICD代碼")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PostMapping("/rareICD")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增罕見ICD代碼")
  public ResponseEntity<BaseResponse> newRareICD(@RequestBody RareICDPayload request) {
    if (request.getCode() == null || request.getCode().length() < 1) {
      return returnAPIResult("code值不可為空");
    }
    request.setCode(request.getCode().toUpperCase());
    if (request.getSdate() == null) {
      return returnAPIResult("生效日不可為空");
    }
    if (request.getEdate() == null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      try {
        request.setEdate(sdf.parse(DateTool.MAX_DATE));
      } catch (ParseException e) {
      }
    }
    if (request.getEdate().before(request.getSdate())) {
      return returnAPIResult("生效訖日不可小於生效日");
    }
    return returnAPIResult(parameterService.newRareICD(request));
  }

  @ApiOperation(value = "修改罕見ICD代碼啟用狀態", notes = "修改罕見ICD代碼啟用狀態")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/rareICDStatus")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改罕見ICD代碼啟用狀態")
  public ResponseEntity<BaseResponse> updateRareICDStatus(
      @ApiParam(name = "id", value = "ICD代碼",
          example = "1") @RequestParam(required = true) String id,
      @ApiParam(name = "enable", value = "是否啟用，true/false",
          example = "true") @RequestParam(required = true) Boolean enable) {
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      return returnAPIResult("id值有誤");
    }
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{idL}));
    
    return returnAPIResult(parameterService.updateRareICDStatus(idL, enable.booleanValue()));
  }

  @ApiOperation(value = "修改罕見ICD代碼參數", notes = "修改罕見ICD代碼參數")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/rareICD")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改罕見ICD代碼參數")
  public ResponseEntity<BaseResponse> updateRareICD(@RequestBody RareICDPayload request) {
    if (request == null || request.getId() == null) {
      return returnAPIResult("id未帶入");
    }
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{request.getId()}));

    return returnAPIResult(parameterService.updateRareICD(request));
  }

  @ApiOperation(value = "刪除罕見ICD資料", notes = "刪除罕見ICD資料")
  @DeleteMapping("/rareICD/{id}")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除罕見ICD資料")
  public ResponseEntity<BaseResponse> deleteRareICDById(@PathVariable String id) {
    if (id == null || id.length() == 0) {
      return returnAPIResult("id未帶入");
    }
    
    httpServletReq.setAttribute(LogType.ACTION_D.name()+"_PKS", Arrays.asList(new String[]{id}));
    
    return returnAPIResult(parameterService.deleteCodeThreshold(id));
  }

  @ApiOperation(value = "取得應用比例偏高醫令/特別用量藥品、衛材列表", notes = "取得應用比例偏高醫令/特別用量藥品、衛材列表")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/highRatioOrder")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<HighRatioOrderListResponse> getHighRatioOrder(
      @ApiParam(name = "code", value = "支付標準代碼",
          example = "J10.01") @RequestParam(required = false) String code,
      @ApiParam(name = "inhCode", value = "院內碼",
          example = "J10.01") @RequestParam(required = false) String inhCode,
      @ApiParam(name = "isOrder", value = "是否為應用比例偏高醫令，true:是，false:否，為特別用量藥品、衛材",
          example = "true") @RequestParam(required = false, defaultValue = "true") Boolean isOrder,
      @ApiParam(name = "orderBy",
          value = "排序欄位名稱，sdate:生效日，edate:失效日，code:支付標準代碼，inhCode:院內碼，status:啟用狀態",
          example = "sdate") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小，空值表示不排序",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "頁碼，第一頁值為0，第二頁值為1…",
          example = "0") @RequestParam(required = false, defaultValue = "0") Integer page) {

    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      HighRatioOrderListResponse result = new HighRatioOrderListResponse();
      result.setMessage("無法取得登入狀態");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
    if (ROLE_TYPE.DOCTOR.getRole().equals(user.getRole())) {
      HighRatioOrderListResponse result = new HighRatioOrderListResponse();
      result.setMessage("無權限");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
    }
    String codeS = (code == null) ? null : HtmlUtils.htmlEscape(code);
    int perPageInt =
        (perPage == null) ? parameterService.getIntParameter(ParametersService.PAGE_COUNT)
            : perPage.intValue();
    String column = orderBy;
    if (column != null) {
      if (column.equals("sdate")) {
        column = "startDate";
      } else if (column.equals("edate")) {
        column = "endDate";
      } else if (column.equals("code") || column.equals("inhCode") || column.equals("status")) {
      } else {
        HighRatioOrderListResponse response = new HighRatioOrderListResponse();
        response.setMessage("orderBy無此欄位：" + orderBy);
        response.setResult("failed");
        return ResponseEntity.badRequest().body(response);
      }
    }
    return ResponseEntity.ok(
        parameterService.getHighRatioOrder(codeS, inhCode, isOrder, column, asc, perPageInt, page));
  }

  @ApiOperation(value = "取得應用比例偏高醫令/特別用量藥品、衛材", notes = "取得應用比例偏高醫令/特別用量藥品、衛材")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/highRatioOrder/{id}")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<HighRatioOrder> getHighRatioOrder(@PathVariable String id) {
    HighRatioOrder response = new HighRatioOrder();
    if (id == null || id.length() == 0) {
      response.setName("id未帶入");
      return ResponseEntity.badRequest().body(response);
    }
    response = parameterService.getHighRatioOrderById(id);
    if (response.getId() == null) {
      return ResponseEntity.badRequest().body(response);
    } else {
      return ResponseEntity.ok(response);
    }
  }

  @ApiOperation(value = "新增應用比例偏高醫令/特別用量藥品、衛材", notes = "新增應用比例偏高醫令/特別用量藥品、衛材")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PostMapping("/highRatioOrder")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增應用比例偏高醫令/特別用量藥品、衛材")
  public ResponseEntity<BaseResponse> newHighRatioOrder(
      @ApiParam(name = "isOrder", value = "是否為應用比例偏高醫令，true:是，false:否，為特別用量藥品、衛材",
          example = "true") @RequestParam(required = false, defaultValue = "true") Boolean isOrder,
      @RequestBody HighRatioOrder request) {

    if (request.getEdate() == null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      try {
        request.setEdate(sdf.parse(DateTool.MAX_DATE));
      } catch (ParseException e) {
      }
    }
    return returnAPIResult(parameterService.newHighRatioOrder(request, isOrder));
  }

  @ApiOperation(value = "修改應用比例偏高醫令或特別用量藥材、衛品", notes = "修改應用比例偏高醫令或特別用量藥材、衛品")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/highRatioOrder")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改應用比例偏高醫令或特別用量藥材、衛品")
  public ResponseEntity<BaseResponse> updateHighRatioOrder(@RequestBody HighRatioOrder request) {
    if (request == null || request.getId() == null) {
      return returnAPIResult("id未帶入");
    }
    boolean isOrder = (request.getCodeType() == null) ? true : (request.getCodeType().intValue() == RareICDPayload.CODE_TYPE_ORDER);
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{request.getId()}));
    
    return returnAPIResult(parameterService.updateHighRatioOrder(request, isOrder));
  }
  
  @ApiOperation(value = "修改應用比例偏高醫令狀態", notes = "修改應用比例偏高醫令狀態")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/highRatioOrder/{id}")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改應用比例偏高醫令狀態")
  public ResponseEntity<BaseResponse> updateHighRatioOrderStatus(@PathVariable String id,
      @ApiParam(name = "enable", value = "是否啟用，true/false",
          example = "true") @RequestParam(required = true) Boolean enable) {
    Long idL = null;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      return returnAPIResult("id有誤");
    }
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{idL}));
    
    return returnAPIResult(parameterService.updateHighRatioOrder(idL, enable.booleanValue()));
  }

  /**
   * 刪除表單資料
   * 
   * @param 應用比例偏高醫令 Id
   * @return
   */
  @ApiOperation(value = "刪除應用比例偏高醫令", notes = "刪除應用比例偏高醫令")
  @DeleteMapping("/highRatioOrder/{id}")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除應用比例偏高醫令")
  public ResponseEntity<BaseResponse> deleteHighRatioOrder(@PathVariable String id) {
    if (id == null || id.length() == 0) {
      return returnAPIResult("id未帶入");
    }
    
    httpServletReq.setAttribute(LogType.ACTION_D.name()+"_PKS", Arrays.asList(new String[]{id}));
    
    return returnAPIResult(parameterService.deleteCodeThreshold(id));
  }

  @ApiOperation(value = "取得同性質藥物列表", notes = "取得同性質藥物列表")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/sameATC")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<SameATCListResponse> getSameATC(
      @ApiParam(name = "code", value = "搜尋支付標準代碼",
          example = "") @RequestParam(required = false) String code,
      @ApiParam(name = "inhCode", value = "搜尋院內碼",
          example = "") @RequestParam(required = false) String inhCode,
      @ApiParam(name = "atc", value = "搜尋ATC分類",
          example = "") @RequestParam(required = false) String atc,
      @ApiParam(name = "orderBy",
          value = "排序欄位名稱，atc:ATC分類，inhCode:院內碼，code:支付標準代碼，name:藥品名稱，status:啟用狀態",
          example = "sdate") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小，空值表示不排序",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "頁碼，第一頁值為0，第二頁值為1…",
          example = "0") @RequestParam(required = false, defaultValue = "0") Integer page) {
    int perPageInt =
        (perPage == null) ? parameterService.getIntParameter(ParametersService.PAGE_COUNT)
            : perPage.intValue();

    String column = orderBy;
    if (column != null) {
      if (column.equals("atc") || column.equals("code") || column.equals("inhCode")
          || column.equals("name") || column.equals("stauts")) {

      } else {
        SameATCListResponse result = new SameATCListResponse();
        result.setMessage("orderBy無此欄位：" + column);
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    return ResponseEntity.ok(parameterService.getSameATCFromPayCode(code, inhCode, atc, column, asc,
        perPageInt, page.intValue()));
  }

  @ApiOperation(value = "修改同性質藥物狀態", notes = "修改同性質藥物狀態")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/sameATC")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改同性質藥物狀態")
  public ResponseEntity<BaseResponse> updateSameATCStatus(
      @ApiParam(name = "id", value = "id", example = "1") @RequestParam(required = true) String id,
      @ApiParam(name = "enable", value = "是否啟用，true/false",
          example = "true") @RequestParam(required = true) Boolean enable) {
    Long idL = null;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      return returnAPIResult("id有誤");
    }
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{idL}));
    
    return returnAPIResult(parameterService.updateSameATC(idL, enable.booleanValue()));
  }

  @ApiOperation(value = "取得健保項目對應自費項目並存列表", notes = "取得健保項目對應自費項目並存列表")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/codeConflict")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<CodeConflictListResponse> getCodeConflict(
      @ApiParam(name = "code", value = "搜尋支付標準代碼",
          example = "") @RequestParam(required = false) String code,
      @ApiParam(name = "inhCode", value = "搜尋健保品項院內碼",
          example = "") @RequestParam(required = false) String inhCode,
      @ApiParam(name = "ownCode", value = "搜尋自費品項院內碼",
          example = "") @RequestParam(required = false) String ownCode,
      @ApiParam(name = "orderBy",
          value = "排序欄位名稱，sdate:生效日，edate:生效訖日，ownCode:自費品項院內碼，inhCode:健保品項院內碼，code:支付標準代碼，status:啟用狀態",
          example = "sdate") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小，空值表示不排序",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "頁碼，第一頁值為0，第二頁值為1…",
          example = "0") @RequestParam(required = false, defaultValue = "0") Integer page) {
    int perPageInt =
        (perPage == null) ? parameterService.getIntParameter(ParametersService.PAGE_COUNT)
            : perPage.intValue();

    String column = orderBy;
    if (column != null) {
      if (column.equals("atc") || column.equals("code") || column.equals("inhCode")
          || column.equals("status")) {

      } else if (column.equals("sdate")) {
        column = "startDate";
      } else if (column.equals("edate")) {
        column = "endDate";
      } else if (column.equals("ownCode")) {
        column = "ownExpCode";
      } else {
        CodeConflictListResponse result = new CodeConflictListResponse();
        result.setMessage("orderBy無此欄位：" + column);
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    return ResponseEntity.ok(
        parameterService.getCodeConflict(code, inhCode, ownCode, column, asc, perPageInt, page));
  }

  @ApiOperation(value = "取得健保項目對應自費項目並存詳細資料", notes = "取得健保項目對應自費項目並存詳細資料")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/codeConflict/{id}")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<CodeConflictPayload> getCodeConflict(@PathVariable String id) {
    if (id == null || id.length() == 0) {
      CodeConflictPayload result = new CodeConflictPayload();
      result.setName("id 未帶入");
      return ResponseEntity.badRequest().body(result);
    }

    try {
      return ResponseEntity.ok(parameterService.getCodeConflict(Long.parseLong(id)));
    } catch (NumberFormatException e) {
      CodeConflictPayload result = new CodeConflictPayload();
      result.setName("id 值有誤");
      return ResponseEntity.badRequest().body(result);
    }
  }

  @ApiOperation(value = "新增健保項目對應自費項目並存資料", notes = "新增參數值")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PostMapping("/codeConflict")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增健保項目對應自費項目並存資料")
  public ResponseEntity<BaseResponse> newCodeConflict(@RequestBody CodeConflictPayload request) {
    if (request.getEdate() == null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      try {
        request.setEdate(sdf.parse(DateTool.MAX_DATE));
      } catch (ParseException e) {
      }
    }
    if (request.getEdate().getTime() == request.getSdate().getTime()) {
      return returnAPIResult("訖日不可等於生效日");
    }
    if (request.getEdate().before(request.getSdate())) {
      return returnAPIResult("訖日不可小於生效日");
    }
    return returnAPIResult(parameterService.upsertCodeConflict(request, false));
  }

  @ApiOperation(value = "修改健保項目對應自費項目並存資料", notes = "修改健保項目對應自費項目並存資料")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/codeConflict")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改健保項目對應自費項目並存資料")
  public ResponseEntity<BaseResponse> updateCodeConflict(@RequestBody CodeConflictPayload request) {
    if (request.getId() == null || request.getId().longValue() == 0) {
      return returnAPIResult("id有誤");
    }
    return returnAPIResult(parameterService.upsertCodeConflict(request, true));
  }

  @ApiOperation(value = "修改健保項目對應自費項目並存資料狀態", notes = "修改健保項目對應自費項目並存資料狀態")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/codeConflict/{id}")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改健保項目對應自費項目並存資料狀態")
  public ResponseEntity<BaseResponse> updateCodeConflictStatus(@PathVariable String id,
      @ApiParam(name = "enable", value = "是否啟用，true/false",
          example = "true") @RequestParam(required = true) Boolean enable) {
    Long idL = null;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      return returnAPIResult("id有誤");
    }
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{idL}));
    
    return returnAPIResult(parameterService.updateCodeConflict(idL, enable.booleanValue()));
  }

  @ApiOperation(value = "刪除健保項目對應自費項目並存資料", notes = "刪除健保項目對應自費項目並存資料")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @DeleteMapping("/codeConflict/{id}")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除健保項目對應自費項目並存資料")
  public ResponseEntity<BaseResponse> deleteCodeConflict(@PathVariable String id) {
    if (id == null || id.length() == 0) {
      return returnAPIResult("id 未帶入");
    }

    try {
    	
      httpServletReq.setAttribute(LogType.ACTION_D.name()+"_PKS", Arrays.asList(new String[]{id}));
    	
      return returnAPIResult(parameterService.deleteCodeConflict(Long.parseLong(id)));
    } catch (NumberFormatException e) {
      return returnAPIResult("id 值有誤");
    }
  }
}
