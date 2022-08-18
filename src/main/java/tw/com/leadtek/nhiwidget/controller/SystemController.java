/**
 * Created on 2021/9/6.
 */
package tw.com.leadtek.nhiwidget.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import tw.com.leadtek.nhiwidget.annotation.LogDefender;
import tw.com.leadtek.nhiwidget.constant.LogType;
import tw.com.leadtek.nhiwidget.model.rdb.ATC;
import tw.com.leadtek.nhiwidget.model.rdb.DEDUCTED;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CODE;
import tw.com.leadtek.nhiwidget.model.rdb.ICD10;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
import tw.com.leadtek.nhiwidget.payload.ATCListResponse;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.DrgCodeListResponse;
import tw.com.leadtek.nhiwidget.payload.DrgCodePayload;
import tw.com.leadtek.nhiwidget.payload.PayCodeListResponse;
import tw.com.leadtek.nhiwidget.payload.PayCodePayload;
import tw.com.leadtek.nhiwidget.payload.system.CompareWarningPayload;
import tw.com.leadtek.nhiwidget.payload.system.DbManagement;
import tw.com.leadtek.nhiwidget.payload.system.DeductedListResponse;
import tw.com.leadtek.nhiwidget.payload.system.FileManagementPayload;
import tw.com.leadtek.nhiwidget.payload.system.ICD10ListResponse;
import tw.com.leadtek.nhiwidget.payload.system.IntelligentConfig;
import tw.com.leadtek.nhiwidget.payload.system.QuestionMarkPayload;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.service.DrgCalService;
import tw.com.leadtek.nhiwidget.service.IntelligentService;
import tw.com.leadtek.nhiwidget.service.NHIWidgetXMLService;
import tw.com.leadtek.nhiwidget.service.ParametersService;
import tw.com.leadtek.nhiwidget.service.ReportService;
import tw.com.leadtek.nhiwidget.service.SystemService;
import tw.com.leadtek.tools.DateTool;

@Api(tags = "系統設定相關API", value = "系統設定相關API")
@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
@RequestMapping(value = "/sys", produces = "application/json; charset=utf-8")
public class SystemController extends BaseController {

  @Autowired
  private DrgCalService drgCalService;

  @Autowired
  private SystemService systemService;

  @Autowired
  private ReportService reportService;
  
  @Autowired
  private NHIWidgetXMLService xmlService;
  
  @Autowired
  private IntelligentService is;
  
  @Autowired
  private ParametersService parametersService;
  
  @ApiOperation(value = "取得DRG列表", notes = "取得DRG列表")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/drg")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<DrgCodeListResponse> getDRG(
      @ApiParam(name = "startDay", value = "生效日期，格式 yyyy/MM/dd",
          example = "2021/03/15") @RequestParam(required = false) String startDay,
      @ApiParam(name = "endDay", value = "失效日期，格式 yyyy/MM/dd",
          example = "2021/03/18") @RequestParam(required = false) String endDay,
      @ApiParam(name = "mdc", value = "MDC分類",
          example = "1") @RequestParam(required = false) String mdc,
      @ApiParam(name = "code", value = "DRG代碼",
          example = "00502") @RequestParam(required = false) String code,
      @ApiParam(name = "orderBy",
          value = "排序欄位名稱，mdc:MDC分類，serial:流水號，code:DRG代碼，rw:RW，avgInDay:平均住院日，"
              + "llimit:下限臨界值，ulimit:上限臨界值， startDay:生效日，endDay:失效日，dep:科別",
          example = "code") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "第幾頁，第一頁值為0", example = "0") @RequestParam(required = false,
          defaultValue = "0") Integer page) {
    int perPageInt =
        (perPage == null) ? parametersService.getIntParameter(ParametersService.PAGE_COUNT)
            : perPage.intValue();
    int pageInt = page == null ? 0 : page.intValue();
    
    Date startDate = null;
    Date endDate = null;
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      if (startDay != null) {
        startDate = sdf.parse(startDay);
      }
      if (endDay != null) {
        endDate = sdf.parse(endDay);
      }
      if (startDate != null && endDate != null && startDate.after(endDate)) {
        DrgCodeListResponse result = new DrgCodeListResponse();
        result.setMessage("啟始日不可大於結束日");
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    } catch (ParseException e) {
      DrgCodeListResponse result = new DrgCodeListResponse();
      result.setMessage("日期格式有誤");
      result.setResult("failed");
      return ResponseEntity.badRequest().body(result);
    }
    
    String column = orderBy;
    if (column != null) {
      if (column.equals("inhCode") || column.equals("code") || column.equals("inhName")
          || column.equals("codeType") || column.equals("statcauts") || column.equals("serial") 
          || column.equals("rw") || column.equals("avgInDay") || column.equals("mdc")
          || column.equals("llimit") || column.equals("ulimit") || column.equals("dep")) {

      } else if (column.equals("startDay")) {
        column = "startDate";
      } else if (column.equals("endDay")) {
        column = "endDate";
      } else {
        DrgCodeListResponse result = new DrgCodeListResponse();
        result.setMessage("orderBy無此欄位：" + column);
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    return ResponseEntity
        .ok(drgCalService.getDRGCode(startDate, endDate, mdc, code, column, asc, perPageInt, pageInt));
  }

  @ApiOperation(value = "新增一組DRG code", notes = "新增一組DRG code")
  @PostMapping("/drg")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增一組DRG code")
  public ResponseEntity<BaseResponse> newDRG(@RequestBody DrgCodePayload request) {
    if (request.getStartDay() == null || request.getStartDay().length() == 0) {
      
    }
    request.setId(null);
    DRG_CODE drgCode = request.toDB();
    if (drgCode == null) {
      return returnAPIResult("日期格式有誤");
    }
    if (drgCode.getEndDate() != null && drgCode.getStartDate() != null
        && drgCode.getEndDate().getTime() < drgCode.getStartDate().getTime()) {
      return returnAPIResult("失效日不可小於生效日");
    }
    if (drgCode.getEndDate() == null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      try {
        drgCode.setEndDate(sdf.parse(DateTool.MAX_DATE));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    DRG_CODE drg = drgCalService.getDrgCode(drgCode);
    if (drg != null) {
      return returnAPIResult("DRG code " + request.getCode() + " 已存在");
    }

    drgCalService.saveDrgCode(drgCode);
    
    httpServletReq.setAttribute(LogType.ACTION_C.name()+"_PKS", Arrays.asList(new Long[]{drgCode.getId()}));
    
    return returnAPIResult(null);
  }

  @ApiOperation(value = "修改DRG code", notes = "更改DRG code")
  @PutMapping("/drg")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改DRG code")
  public ResponseEntity<BaseResponse> updateDRG(@RequestBody DrgCodePayload request) {
    if (request.getId() == null || request.getId().intValue() == 0) {
      return returnAPIResult("DRG id 不可為空值或0");
    }
    DRG_CODE drgCode = request.toDB();
    if (drgCode == null) {
      return returnAPIResult("日期格式有誤");
    }
    if (drgCode.getEndDate() != null && drgCode.getStartDate() != null
        && drgCode.getEndDate().getTime() < drgCode.getStartDate().getTime()) {
      return returnAPIResult("失效日不可小於生效日");
    }
    DRG_CODE drg = drgCalService.getDrgCode(drgCode);
    if (drg == null) {
      return returnAPIResult("DRG code " + request.getCode() + ",id:" + request.getId() + "不存在");
    }
    drgCalService.saveDrgCode(drgCode);
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{request.getId()}));
    
    return returnAPIResult(null);
  }

  @ApiOperation(value = "刪除DRG code", notes = "刪除DRG code")
  @DeleteMapping("/drg/{id}")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除DRG code")
  public ResponseEntity<?> deleteDRG(@PathVariable String id) {
    logger.info("/drg/{id}: delete:" + id);
    DRG_CODE drg = drgCalService.getDrgCode(Long.parseLong(id));
    if (drg == null) {
      return returnAPIResult("DRG id:" + id + "不存在");
    }
    drgCalService.deleteDrgCode(drg);
    return returnAPIResult(null);
  }

  @ApiOperation(value = "取得ATC代碼", notes = "取得ATC代碼")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/atc")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<ATCListResponse> getATC(
      @ApiParam(name = "code", value = "ATC代碼",
          example = "A01") @RequestParam(required = false) String code,
      @ApiParam(value = "ATC分類名稱",
          example = "氟化亞錫") @RequestParam(required = false) String note,
      @ApiParam(value = "排序欄位名稱，code:ATC分類代碼，note:ATC分類名稱",
          example = "code") @RequestParam(required = false) String orderBy,
      @ApiParam(value = "排序方式，true:由小至大，false:由大至小", example = "true") 
          @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "第幾頁，第一頁值為0", example = "0") @RequestParam(required = false,
          defaultValue = "0") Integer page) {
    
    String column = orderBy;
    if (column != null) {
      if (column.equals("code") || column.equals("note")) {
      } else {
        ATCListResponse result = new ATCListResponse();
        result.setMessage("orderBy無此欄位：" + column);
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    
    int perPageInt = (perPage == null) ? DEFAULT_PAGE_COUNT : perPage.intValue();
    int pageInt = page == null ? 0 : page.intValue();
    return ResponseEntity.ok(systemService.getATC(code, note, column, asc, perPageInt, pageInt));
  }

  @ApiOperation(value = "新增一組ATC分類代碼", notes = "新增一組ATC分類代碼")
  @PostMapping("/atc")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增一組ATC分類代碼")
  public ResponseEntity<BaseResponse> newATC(@RequestBody ATC request) {
    logger.info("/atc new:" + request.getCode());
    request.setCode(HtmlUtils.htmlEscape(request.getCode()));
    request.setNote(HtmlUtils.htmlEscape(request.getNote()));
    logger.info(request.toString());
    if (request.getCode() == null) {
      return returnAPIResult("ATC代碼不存在");
    }
    ATC atc = systemService.getATC(request.getCode());
    if (atc != null) {
      return returnAPIResult("ATC code " + request.getCode() + " 已存在");
    }

    systemService.saveATC(request, true);
    
    httpServletReq.setAttribute(LogType.ACTION_C.name()+"_PKS", Arrays.asList(new String[]{request.getCode()}));
    
    return returnAPIResult(null);
  }

  @ApiOperation(value = "修改ATC分類代碼", notes = "修改ATC分類代碼")
  @PutMapping("/atc")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改ATC分類代碼")
  public ResponseEntity<BaseResponse> updateATC(@RequestBody ATC request) {
    if (request.getCode() == null) {
      return returnAPIResult("ATC code 不可為空值");
    }
    request.setCode(HtmlUtils.htmlEscape(request.getCode()));
    request.setNote(HtmlUtils.htmlEscape(request.getNote()));
    ATC atc = systemService.getATC(request.getCode());
    if (atc == null) {
      return returnAPIResult("ATC code " + request.getCode() + "不存在");
    }
    request.setRedisId(atc.getRedisId());
    systemService.saveATC(request, false);
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new String[]{request.getCode()}));
    
    return returnAPIResult(null);
  }

  @ApiOperation(value = "刪除ATC code", notes = "刪除ATC code")
  @DeleteMapping("/atc/{code}")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除ATC code")
  public ResponseEntity<?> deleteATC(@PathVariable String code) {
    logger.info("/atc/{code}: delete:" + code);
    ATC atc = systemService.getATC(code);
    if (atc == null) {
      return returnAPIResult("ATC code " + code + "不存在");
    }
    systemService.deleteATC(atc);
    return returnAPIResult(null);
  }

  @ApiOperation(value = "取得醫院層級", notes = "取得醫院層級")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/hospLevel")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<List<String>> getHospitalLevel() {
    return ResponseEntity.ok(parametersService.getHospitalLevel());
  }

  @ApiOperation(value = "取得費用分類", notes = "取得費用分類")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/payCodeCat")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<List<String>> getPayCodeCategory() {
    return ResponseEntity.ok(parametersService.getPayCodeCategory());
  }

  @ApiOperation(value = "取得代碼品項列表", notes = "取得代碼品項列表")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/payCode")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<PayCodeListResponse> getPayCode(
      @ApiParam(name = "startDay", value = "生效日，格式 yyyy/MM/dd",
          example = "2021/03/15") @RequestParam(required = false) String startDay,
      @ApiParam(name = "endDay", value = "終止日，格式 yyyy/MM/dd",
          example = "2021/03/18") @RequestParam(required = false) String endDay,
      @ApiParam(name = "atc", value = "ATC分類",
          example = "A01") @RequestParam(required = false) String atc,
      @ApiParam(name = "codeType", value = "費用分類",
          example = "藥費") @RequestParam(required = false) String codeType,
      @ApiParam(name = "code", value = "代碼",
          example = "87203C") @RequestParam(required = false) String code,
      @ApiParam(name = "inhCode", value = "院內碼",
          example = "87203C") @RequestParam(required = false) String inhCode,
      @ApiParam(name = "name", value = "支付標準名稱",
          example = "結膜切片") @RequestParam(required = false) String name,
      @ApiParam(name = "inhName", value = "院內名稱",
          example = "結膜切片") @RequestParam(required = false) String inhName,
      @ApiParam(name = "orderBy",
          value = "排序欄位名稱，inhCode:院內碼，code:支付標準代碼，inhName:院內名稱，codeType:費用分類，atc:ATC分類，startDay:生效日，endDay:終止日",
          example = "code") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "第幾頁，第一頁值為0", example = "0") @RequestParam(required = false,
          defaultValue = "0") Integer page) {
    int perPageInt =
        (perPage == null) ? parametersService.getIntParameter(ParametersService.PAGE_COUNT)
            : perPage.intValue();
    int pageInt = page == null ? 0 : page.intValue();
    
    String column = orderBy;
    if (column != null) {
      if (column.length() == 0) {
        column = null;
      } else if (column.equals("inhCode") || column.equals("code") || column.equals("inhName")
          || column.equals("codeType") || column.equals("statcauts") || column.equals("atc")) {

      } else if (column.equals("startDay")) {
        column = "startDate";
      } else if (column.equals("endDay")) {
        column = "endDate";
      } else {
        PayCodeListResponse result = new PayCodeListResponse();
        result.setMessage("orderBy無此欄位：" + column);
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    String codeUC = (code == null) ? null : code.toUpperCase();
    String inhCodeUC = (inhCode == null) ? null : inhCode.toUpperCase();
    return ResponseEntity.ok(systemService.getPayCode(startDay, endDay, atc, codeType, codeUC,
        inhCodeUC, name, inhName, column, asc, perPageInt, pageInt));
  }

  @ApiOperation(value = "新增一組代碼品項", notes = "新增一組代碼品項")
  @PostMapping("/payCode")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增一組代碼品項")
  public ResponseEntity<BaseResponse> newPayCode(@RequestBody PayCodePayload request) {
    logger.info("/payCode new:" + request.getCode());
    logger.info(request.toString());
    if (request.getCode() == null && request.getInhCode() != null) {
      return returnAPIResult("代碼或院內碼必須有值");
    }
    if (request.getId() != null) {
      request.setId(null);
    }
    PAY_CODE pc = request.toDB();
    PAY_CODE oldPayCode = systemService.getPayCode(pc);
    if (oldPayCode != null) {
      return returnAPIResult("代碼品項 code " + request.getCode() + " 已存在且生效日、終止日一致");
    }
    systemService.savePayCode(pc, true);
    
    httpServletReq.setAttribute(LogType.ACTION_C.name()+"_PKS", Arrays.asList(new Long[]{pc.getId()}));
    
    return returnAPIResult(null);
  }

  @ApiOperation(value = "修改代碼品項資料", notes = "修改代碼品項資料")
  @PutMapping("/payCode")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改代碼品項資料")
  public ResponseEntity<BaseResponse> updatePayCode(@RequestBody PayCodePayload request) {
    if (request.getId() == null) {
      return returnAPIResult("id 不可為空值");
    }
    if (request.getEndDate() == null) {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      try {
        request.setEndDate(sdf.parse(DateTool.MAX_DATE));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    PAY_CODE pc = request.toDB();
    if (systemService.isTimeOverlapPayCode(pc)) {
      return returnAPIResult("該時段有相同的代碼品項資料");
    }
    PAY_CODE oldPayCode = systemService.getPayCode(pc);
    if (oldPayCode == null) {
      return returnAPIResult("代碼品項 code " + request.getCode() + " 不存在");
    }
    pc.setRedisId(oldPayCode.getRedisId());
    systemService.savePayCode(pc, false);
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{request.getId()}));
    
    return returnAPIResult(null);
  }

  @ApiOperation(value = "刪除代碼品項資料", notes = "刪除代碼品項資料")
  @DeleteMapping("/payCode/{id}")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除代碼品項資料")
  public ResponseEntity<?> deletePayCode(@PathVariable String id) {
    logger.info("/payCode/{id}: delete:" + id);
    PAY_CODE pc = new PAY_CODE();
    try {
      pc.setId(Long.parseLong(id));
    } catch (NumberFormatException e) {
      return returnAPIResult("代碼品項 id 有誤");
    }
    PAY_CODE oldPayCode = systemService.getPayCode(pc);
    if (oldPayCode == null) {
      return returnAPIResult("代碼品項 id:" + id + "不存在");
    }
    systemService.deletePayCode(oldPayCode);
    return returnAPIResult(null);
  }

  @ApiOperation(value = "取得核刪代碼大分類", notes = "取得減核代碼大分類")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/deductedCat")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<List<String>> getDeductedCat() {
    return ResponseEntity.ok(systemService.getDeductedCat());
  }

  @ApiOperation(value = "取得核刪代碼中分類", notes = "取得減核代碼中分類")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/deductedCat/{l1}")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<List<String>> getDeductedCat(@PathVariable String l1) {
    return ResponseEntity.ok(systemService.getDeductedCat(l1));
  }

  @ApiOperation(value = "取得核刪代碼小分類", notes = "取得減核代碼小分類")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/deductedCat/{l1}/{l2}")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<List<String>> getDeductedCat(@PathVariable String l1,
      @PathVariable String l2) {
    return ResponseEntity.ok(systemService.getDeductedCat(l1, l2));
  }
  
  @ApiOperation(value = "取得核刪代碼列表", notes = "取得核減代碼列表")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/deducted")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<DeductedListResponse> getDuductedList(
      @ApiParam(name = "l1", value = "核減代碼大分類",
          example = "專業審查不予支付代碼") @RequestParam(required = false) String l1,
      @ApiParam(name = "l2", value = "核減代碼中分類",
          example = "西醫") @RequestParam(required = false) String l2,
      @ApiParam(name = "l3", value = "核減代碼小分類",
          example = "診療品質") @RequestParam(required = false) String l3,
      @ApiParam(name = "code", value = "核減代碼",
          example = "0001A") @RequestParam(required = false) String code,
      @ApiParam(name = "name", value = "不予支付理由",
          example = "治療與病情診斷不符") @RequestParam(required = false) String name,
      @ApiParam(name = "orderBy",
          value = "排序欄位名稱，l1:核減代碼大分類，l2:核減代碼中分類，l3:核減代碼小分類，code:核減代碼，name:不予支付理由",
          example = "code") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "第幾頁，第一頁值為0", example = "0") @RequestParam(required = false,
          defaultValue = "0") Integer page) {
    int perPageInt =
        (perPage == null) ? parametersService.getIntParameter(ParametersService.PAGE_COUNT)
            : perPage.intValue();
    int pageInt = page == null ? 0 : page.intValue();
    String layer3 = l3;
    if ("-".equals(layer3)) {
      layer3 = null;
    }
    return ResponseEntity.ok(
        systemService.getDuductedList(l1, l2, layer3, code, name, orderBy, asc, perPageInt, pageInt));
  }

  @ApiOperation(value = "新增核減代碼", notes = "新增核減代碼")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PostMapping("/deducted")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增核減代碼")
  public ResponseEntity<BaseResponse> newDeducted(@RequestBody DEDUCTED request) {
    if (request.getCode() == null || request.getCode().length() < 1) {
      return returnAPIResult("code值不可為空");
    }
    
    String result = systemService.newDeducted(request);
    
    httpServletReq.setAttribute(LogType.ACTION_C.name()+"_PKS", Arrays.asList(new Long[]{request.getId()}));
    
    return returnAPIResult(result);
  }

  @ApiOperation(value = "取得指定id核減代碼", notes = "取得指定id核減代碼")
  @GetMapping("/deducted/{id}")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<DEDUCTED> getDeducted(@PathVariable String id) {
    if (id == null || id.length() == 0) {
      return ResponseEntity.badRequest().body(new DEDUCTED());
    }
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest().body(new DEDUCTED());
    }
    return ResponseEntity.ok(systemService.getDeducted(idL));
  }

  @ApiOperation(value = "修改核減代碼", notes = "修改核減代碼")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/deducted")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改核減代碼")
  public ResponseEntity<BaseResponse> updateRareICD(@RequestBody DEDUCTED request) {
    if (request == null || request.getId() == null) {
      return returnAPIResult("id未帶入");
    }
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{request.getId()}));
    
    return returnAPIResult(systemService.updateDeducted(request));
  }

  @ApiOperation(value = "刪除核減代碼", notes = "刪除核減代碼")
  @DeleteMapping("/deducted/{id}")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除核減代碼")
  public ResponseEntity<BaseResponse> deleteRareICDById(@PathVariable String id) {
    if (id == null || id.length() == 0) {
      return returnAPIResult("id未帶入");
    }
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      return returnAPIResult("id不正確");
    }
    return returnAPIResult(systemService.deleteDeducted(idL));
  }
  
  @ApiOperation(value = "取得ICD代碼列表", notes = "取得ICD代碼列表")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/icd10")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<ICD10ListResponse> getICD10List(
      @ApiParam(value = "是否為法定傳染病",
          example = "true") @RequestParam(required = false) Boolean infectious,
      @ApiParam(name = "infCat", value = "法定傳染病分類層級",
          example = "第一類") @RequestParam(required = false) String infCat,
      @ApiParam(name = "code", value = "ICD10代碼",
          example = "0001A") @RequestParam(required = false) String code,
      @ApiParam(name = "name", value = "ICD10項目名稱",
          example = "維生素B1 (硫胺素)缺乏") @RequestParam(required = false) String name,
      @ApiParam(name = "orderBy",
          value = "排序欄位名稱，cat:代碼類別，code:ICD10代碼，name:ICD10項目名稱(中文)，enName:ICD10項目名稱(英文)，isInfectious:是否為法定傳染病，infCat:分類層級",
          example = "code") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "第幾頁，第一頁值為0", example = "0") @RequestParam(required = false,
          defaultValue = "0") Integer page) {
    int perPageInt =
        (perPage == null) ? parametersService.getIntParameter(ParametersService.PAGE_COUNT)
            : perPage.intValue();
    int pageInt = page == null ? 0 : page.intValue();
    
    String column = orderBy;
    if (column != null) {
      if ("name".equals(column)) {
        column = "descChi";
      } else if ("enName".equals(column)) {
        column = "descEn";
      } else if ("isInfectious".equals(column)) {
        column = "infectious";
      } else if (column.equals("code") || column.equals("infCat") || column.equals("cat")) {
        
      } else {
        ICD10ListResponse result = new ICD10ListResponse();
        result.setMessage("orderBy無此欄位：" + column);
        result.setResult("failed");
        return ResponseEntity.badRequest().body(result);
      }
    }
    return ResponseEntity.ok(
        systemService.getIcd10(code, name, infectious, infCat, perPageInt, pageInt, column, asc));
  }

  @ApiOperation(value = "新增ICD10代碼", notes = "新增ICD10代碼")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PostMapping("/icd10")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_C}, name = "新增ICD10代碼")
  public ResponseEntity<BaseResponse> newICD10(@RequestBody ICD10 request) {
    if (request.getCode() == null || request.getCode().length() < 1) {
      return returnAPIResult("code值不可為空");
    }
    
    String result = systemService.saveIcd10(request, true);
    
    httpServletReq.setAttribute(LogType.ACTION_C.name()+"_PKS", Arrays.asList(new Long[]{request.getId()}));
    
    return returnAPIResult(result);
  }

  @ApiOperation(value = "取得指定ICD10代碼", notes = "取得指定ICD10代碼")
  @GetMapping("/icd10/{id}")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<ICD10> getICD10(@PathVariable String id) {
    if (id == null || id.length() == 0) {
      return ResponseEntity.badRequest().body(new ICD10());
    }
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest().body(new ICD10());
    }
    return ResponseEntity.ok(systemService.getIcd10(idL));
  }

  @ApiOperation(value = "修改ICD10代碼", notes = "修改ICD10代碼")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/icd10")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "修改ICD10代碼")
  public ResponseEntity<BaseResponse> updateIcd10(@RequestBody ICD10 request) {
    if (request == null || request.getId() == null) {
      return returnAPIResult("id未帶入");
    }
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{request.getId()}));
    
    return returnAPIResult(systemService.saveIcd10(request, false));
  }

  @ApiOperation(value = "刪除ICD10代碼", notes = "刪除ICD10代碼")
  @DeleteMapping("/icd10/{id}")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_D}, name = "刪除ICD10代碼")
  public ResponseEntity<BaseResponse> deleteICD10(@PathVariable String id) {
    if (id == null || id.length() == 0) {
      return returnAPIResult("id未帶入");
    }
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      return returnAPIResult("id不正確");
    }
    return returnAPIResult(systemService.deleteIcd10(idL));
  }
  
  @ApiOperation(value = "取得檔案管理功能設定", notes = "取得檔案管理功能設定")
  @GetMapping("/config/fileManagement")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<FileManagementPayload> getFileManagement() {
    return ResponseEntity.ok(systemService.getFileManagementPaylod());
  }
  
  @ApiOperation(value = "更新檔案管理功能設定", notes = "更新檔案管理功能設定")
  @PostMapping("/config/fileManagement")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "更新檔案管理功能設定")
  public ResponseEntity<BaseResponse> updateFileManagement(
      @RequestBody FileManagementPayload request) {
	  
    return returnAPIResult(systemService.updateFileManagementPaylod(request));
  }
  
  @ApiOperation(value = "取得比對警示功能設定", notes = "取得比對警示功能設定")
  @GetMapping("/config/compareWarning")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<CompareWarningPayload> getCompareWarning() {
    return ResponseEntity.ok(systemService.getCompareWarningPayload());
  }
  
  @ApiOperation(value = "更新比對警示功能設定", notes = "更新比對警示功能設定")
  @PostMapping("/config/compareWarning")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "更新比對警示功能設定")
  public ResponseEntity<BaseResponse> updateCompareWarning(
      @RequestBody CompareWarningPayload request) {
	  
    return returnAPIResult(systemService.updateCompareWarningPayload(request));
  }
  
  @ApiOperation(value = "取得疑問提示通知功能設定", notes = "取得疑問提示通知功能設定")
  @GetMapping("/config/questionMark")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<QuestionMarkPayload> getQuestionMarkPayload() {
    return ResponseEntity.ok(systemService.getQuestionMarkPayload());
  }
  
  @ApiOperation(value = "更新疑問提示通知功能設定", notes = "更新疑問提示通知功能設定")
  @PostMapping("/config/questionMark")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "更新疑問提示通知功能設定")
  public ResponseEntity<BaseResponse> updateQuestionMarkPayload(
      @RequestBody QuestionMarkPayload request) {
	  
    return returnAPIResult(systemService.updateQuestionMarkPayload(request));
  }
  
  @ApiOperation(value = "取得智能提示助理功能設定", notes = "取得智能提示助理功能設定")
  @GetMapping("/config/intelligent")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<IntelligentConfig> getIntelligentConfig() {
    return ResponseEntity.ok(systemService.getIntelligentConfig());
  }
  
  @ApiOperation(value = "更新智能提示助理功能設定", notes = "更新智能提示助理功能設定")
  @PostMapping("/config/intelligent")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "更新智能提示助理功能設定")
  public ResponseEntity<BaseResponse> updateIntelligentConfig(
      @RequestBody IntelligentConfig request) {
	  
    return returnAPIResult(systemService.updateIntelligentConfig(request));
  }
  
  @ApiOperation(value = "取得資料庫串接管理設定", notes = "取得資料庫串接管理設定")
  @GetMapping("/config/dbManagement")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<DbManagement> getDbManagement() {
    return ResponseEntity.ok(systemService.getDbManagement());
  }
  
  @ApiOperation(value = "更新資料庫串接管理設定", notes = "更新資料庫串接管理設定")
  @PostMapping("/config/dbManagement")
  @LogDefender(value = {LogType.SIGNIN, LogType.ACTION_U}, name = "更新資料庫串接管理設定")
  public ResponseEntity<BaseResponse> updateDbManagement(
      @RequestBody DbManagement request) {
	  
    return returnAPIResult(systemService.updateDbManagement(request));
  }
  
  @ApiOperation(value = "重跑job", notes = "重跑job")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/run")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<BaseResponse> runReport(
      @ApiParam(value = "job name", example = "PointMR") 
      @RequestParam(required = false) String name, 
      @ApiParam(value = "job 參數", example = "11012") 
      @RequestParam(required = false) String param){
    
    if ("PointMR".equals(name)) {
      reportService.calculatePointMR(param);
    } else if ("RareIcd".equals(name)) {
      is.calculateRareICD(param);
    } else if ("Infectious".equals(name)) {
      is.calculateInfectious(param);
    } else if ("HighRatio".equals(name)) {
      is.calculateHighRatio(param);
    } else if ("OverAmount".equals(name)) {
      is.calculateOverAmount(param);
    } else if ("DRG".equals(name)) {
      reportService.calculateDRGMonthly(param);
    } else if ("Weekly".equals(name)) {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.YEAR, -4);
      cal = parametersService.getMinMaxCalendar(cal.getTime(), true);
      reportService.calculatePointWeekly(cal, true);
    } else if ("CostDiff".equals(name)) {
      is.recalculateAICostThread();
    } else if ("IpDays".equals(name)) {
      is.recalculateAIIpDaysThread();
    } else if ("DrugDiff".equals(name)) {
      is.calculateAIOrderDrug(param);
    }
    return returnAPIResult(null);
  }
  
  @ApiOperation(value = "取得申報檔匯出進度", notes = "取得申報檔匯出進度")
  @GetMapping("/filesStatus")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<BaseResponse> downloadFiles(){
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      BaseResponse result = new BaseResponse();
      result.setMessage("無法取得登入狀態");
      result.setResult("error");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }

    BaseResponse response = new BaseResponse();
    response.setResult("success");
    response.setMessage(systemService.getDownloadFiles(user.getId()));
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
  
  @ApiOperation(value = "匯出申報檔", notes = "匯出申報檔")
  @GetMapping("/downloadXML")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<BaseResponse> downloadXML(@ApiParam(value = "資料格式，10:門急診，20:住院",
        example = "10") @RequestParam(required = false) String dataFormat,
      @ApiParam(value = "申報年，格式西元年 yyyy",
        example = "2021") @RequestParam(required = false) String applY,
      @ApiParam(value = "統計月份月，格式 M",
        example = "1") @RequestParam(required = false) String applM,
      @ApiParam(value = "申報日期，格式yyyy-MM-dd 或 yyyy/MM/dd",
        example = "2022-02-24") @RequestParam(required = false) String applDate,
      @ApiParam(value = "申報類別，1:送核，2:補報",
        example = "1") @RequestParam(required = false) String applCategory,
      @ApiParam(value = "申報方式，1:書面 2: 媒體 3: 連線",
        example = "2") @RequestParam(required = false) String applMethod,
      @ApiParam(value = "醫事類別:門診西醫、門診牙醫、門診中醫、住診西醫、門診洗腎",
        example = "門診西醫") @RequestParam(required = false) String applMedic,
      @ApiParam(value = "起始日期，格式yyyy-MM-dd 或 yyyy/MM/dd",
        example = "2022-02-24") @RequestParam(required = false) String dateStart,
      @ApiParam(value = "結束日期，格式yyyy-MM-dd 或 yyyy/MM/dd",
        example = "2022-02-24") @RequestParam(required = false) String dateEnd,
      HttpServletResponse response) throws UnsupportedEncodingException {

    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      BaseResponse result = new BaseResponse();
      result.setMessage("無法取得登入狀態");
      result.setResult("error");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
    
    if (dateStart != null && dateEnd != null || "0".equals(parametersService.getParameter("/sys/downloadXML"))) {
      applY = "2000";
      applM = "1";
    }
    String filename = systemService.getDownloadXMLFilename(dataFormat, applY, applM);
    try {
      int progress = systemService.downloadXML(dataFormat, applY, applM, applDate, applCategory,
          applMethod, applMedic, user.getId(), response);
     
      // 配置檔案下載
      logger.info("downloadXML/" + filename + ":" + progress);
      response.setCharacterEncoding("BIG5");
    } catch (UnsupportedEncodingException e) {
      logger.error("downloadSampleFile-", e);
    } catch (IOException e) {
      logger.error("downloadSampleFile-", e);
    }

    return null;
  }
  
  @ApiOperation(value = "上傳XML申報檔", notes = "上傳XML申報檔")
  @ApiImplicitParams({@ApiImplicitParam(name = "file", paramType = "form", value = "自定義表單檔案",
      dataType = "file", required = true, example = "111-0.xml")})
  @PostMapping(value = "/uploadXML")
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<BaseResponse> uploadXML(
      @ApiParam(name = "file", value = "自定義表單檔案", example = "111-0.xml") @RequestPart("file") MultipartFile file) {
    logger.info("/uploadXML:" + file.getOriginalFilename() + "," + file.getSize());
    if (file.isEmpty()) {
      return ResponseEntity.ok(new BaseResponse("error", "error file"));
    }

    String result = null;
    try {
      String dirPath =
          systemService.checkDownloadDir((parametersService.getParameter("MR_PATH") != null)
              ? parametersService.getParameter("MR_PATH")
              : SystemService.FILE_PATH);
      String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
          ? dirPath + "\\" + file.getOriginalFilename()
          : dirPath + "/" + file.getOriginalFilename();
      File saveFile = new File(filepath);
      if (saveFile.exists() && saveFile.length() > 0) {
        try {
          saveFile.delete();
        } catch (Exception e) {
          logger.error("delete exist file", e);
          return returnAPIResult("檔案已存在");
        }
      } else if (file != null && file.getSize() == 0) {
        return returnAPIResult(file.getOriginalFilename()  + " 檔案資料為0筆！");
      }
      try {
        systemService.deleteInFileDownload(file.getOriginalFilename());
        file.transferTo(saveFile);
        //systemService.processUploadFile(saveFile);
      } catch (IllegalStateException e) {
        e.printStackTrace();
      }

    } catch (IOException e) {
      logger.error("uploadXML-", e);
    }
//    if (result.get("success") != null) {
//      return ResponseEntity.ok(result.get("success"));
//    } else {
//      return returnAPIResult(result.get("error"));
//    }
    return returnAPIResult(result);
  }

}
