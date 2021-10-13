/**
 * Created on 2021/9/6.
 */
package tw.com.leadtek.nhiwidget.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
import tw.com.leadtek.nhiwidget.model.rdb.ATC;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CODE;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
import tw.com.leadtek.nhiwidget.payload.ATCListResponse;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.DrgCodeListResponse;
import tw.com.leadtek.nhiwidget.payload.DrgCodePayload;
import tw.com.leadtek.nhiwidget.payload.PayCode;
import tw.com.leadtek.nhiwidget.payload.PayCodeListResponse;
import tw.com.leadtek.nhiwidget.service.DrgCalService;
import tw.com.leadtek.nhiwidget.service.ParametersService;
import tw.com.leadtek.nhiwidget.service.SystemService;

@Api(tags = "系統設定相關API", value = "系統設定相關API")
@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
@RequestMapping(value = "/sys", produces = "application/json; charset=utf-8")
public class SystemController extends BaseController {

  @Autowired
  private DrgCalService drgCalService;
  
  @Autowired
  private SystemService systemService;
  
  @ApiOperation(value = "取得DRG列表", notes = "取得DRG列表")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/drg")
  public ResponseEntity<DrgCodeListResponse> getDRG(
      @ApiParam(name = "sdate", value = "生效日期，格式 yyyy/MM/dd",
          example = "2021/03/15") @RequestParam(required = false) String sdate,
      @ApiParam(name = "edate", value = "失效日期，格式 yyyy/MM/dd",
          example = "2021/03/18") @RequestParam(required = false) String edate,
      @ApiParam(name = "mdc", value = "MDC分類",
          example = "1") @RequestParam(required = false) String mdc,
      @ApiParam(name = "code", value = "DRG代碼",
          example = "00502") @RequestParam(required = false) String code,
      @ApiParam(name = "orderBy", value = "排序欄位名稱，mdc:MDC分類，serial:流水號，code:DRG代碼，rw:RW，avgInDay:平均住院日，startDay:生效日，endDay:失效日",
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
    return ResponseEntity.ok(drgCalService.getDRGCode(sdate, edate, mdc, code, orderBy, asc, perPageInt, pageInt));
  }
  
  @ApiOperation(value = "新增一組DRG code", notes = "新增一組DRG code")
  @PostMapping("/drg")
  public ResponseEntity<BaseResponse> newDRG(@RequestBody DrgCodePayload request) {
    logger.info("/drg new:" + request.getCode());
    logger.info(request.toString());
    DRG_CODE drgCode = request.toDB();
    if (drgCode == null) {
      return returnAPIResult("日期格式有誤");
    }
    if (drgCode.getEndDate() != null && drgCode.getStartDate() != null && drgCode.getEndDate().getTime() < drgCode.getStartDate().getTime()) {
      return returnAPIResult("失效日不可小於生效日");
    }
    DRG_CODE drg = drgCalService.getDrgCode(drgCode);
    if (drg != null) {
      return returnAPIResult("DRG code " + request.getCode() + " 已存在");
    }

    drgCalService.saveDrgCode(drgCode);
    return returnAPIResult(null);
  }
  
  @ApiOperation(value = "修改DRG code", notes = "更改DRG code")
  @PutMapping("/drg")
  public ResponseEntity<BaseResponse> updateDRG(@RequestBody DrgCodePayload request) {
    if (request.getId() == null || request.getId().intValue() == 0) {
      return returnAPIResult("DRG id 不可為空值或0");
    }
    DRG_CODE drgCode = request.toDB();
    if (drgCode == null) {
      return returnAPIResult("日期格式有誤");
    }
    if (drgCode.getEndDate() != null && drgCode.getStartDate() != null && drgCode.getEndDate().getTime() < drgCode.getStartDate().getTime()) {
      return returnAPIResult("失效日不可小於生效日");
    }
    DRG_CODE drg = drgCalService.getDrgCode(drgCode);
    if (drg == null) {
      return returnAPIResult("DRG code " + request.getCode() + ",id:" + request.getId() + "不存在");
    }
    drgCalService.saveDrgCode(drgCode);
    return returnAPIResult(null);
  }
  
  @ApiOperation(value = "刪除DRG code", notes = "刪除DRG code")
  @DeleteMapping("/drg/{id}")
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
  public ResponseEntity<ATCListResponse> getATC(
      @ApiParam(name = "code", value = "ATC代碼",
          example = "A01") @RequestParam(required = false) String code,
      @ApiParam(name = "leng", value = "代碼碼長",
          example = "3") @RequestParam(required = false) Integer leng,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "第幾頁，第一頁值為0", example = "0") @RequestParam(required = false,
          defaultValue = "0") Integer page) {
    int perPageInt =
        (perPage == null) ? DEFAULT_PAGE_COUNT : perPage.intValue();
    int pageInt = page == null ? 0 : page.intValue();
    return ResponseEntity.ok(systemService.getATC(code, leng, perPageInt, pageInt));
  }
  
  @ApiOperation(value = "新增一組ATC分類代碼", notes = "新增一組ATC分類代碼")
  @PostMapping("/atc")
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
    return returnAPIResult(null);
  }
  
  @ApiOperation(value = "修改ATC分類代碼", notes = "修改ATC分類代碼")
  @PutMapping("/atc")
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
    return returnAPIResult(null);
  }
  
  @ApiOperation(value = "刪除ATC code", notes = "刪除ATC code")
  @DeleteMapping("/atc/{code}")
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
  public ResponseEntity<List<String>> getHospitalLevel() {
    return ResponseEntity.ok(parametersService.getHospitalLevel());
  }
  
  @ApiOperation(value = "取得費用分類", notes = "取得費用分類")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/payCodeCat")
  public ResponseEntity<List<String>> getPayCodeCategory() {
    return ResponseEntity.ok(parametersService.getPayCodeCategory());
  }
  
  @ApiOperation(value = "取得代碼品項列表", notes = "取得DRG列表")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/payCode")
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
      @ApiParam(name = "orderBy", value = "排序欄位名稱，inhCode:院內碼，code:支付標準代碼，inhName:院內名稱，codeType:費用分類，atc:ATC分類，startDay:生效日，endDay:終止日",
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
    return ResponseEntity.ok(systemService.getPayCode(startDay, endDay, atc, codeType, 
        code, inhCode, name, inhName, orderBy, asc, perPageInt, pageInt));
  }
  
  @ApiOperation(value = "新增一組代碼品項", notes = "新增一組代碼品項")
  @PostMapping("/payCode")
  public ResponseEntity<BaseResponse> newPayCode(@RequestBody PayCode request) {
    logger.info("/payCode new:" + request.getCode());
    logger.info(request.toString());
    if (request.getCode() == null && request.getInhCode() != null) {
      return returnAPIResult("代碼或院內碼必須有值");
    }
    PAY_CODE pc = request.toDB();
    PAY_CODE oldPayCode = systemService.getPayCode(pc);
    if (oldPayCode != null) {
      return returnAPIResult("代碼品項 code " + request.getCode() + " 已存在且生效日、終止日一致");
    }

    systemService.savePayCode(pc, true);
    return returnAPIResult(null);
  }
  
  @ApiOperation(value = "修改代碼品項資料", notes = "修改代碼品項資料")
  @PutMapping("/payCode")
  public ResponseEntity<BaseResponse> updatePayCode(@RequestBody PayCode request) {
    // @TODO 同一代碼的終止日要往前移
    if (request.getId() == null) {
      return returnAPIResult("id 不可為空值");
    }
    PAY_CODE pc = request.toDB();
    PAY_CODE oldPayCode = systemService.getPayCode(pc);
    if (oldPayCode == null) {
      return returnAPIResult("代碼品項 code " + request.getCode() + " 不存在");
    }
    pc.setRedisId(oldPayCode.getRedisId());
    systemService.savePayCode(pc, false);
    return returnAPIResult(null);
  }
  
  @ApiOperation(value = "刪除代碼品項資料", notes = "刪除代碼品項資料")
  @DeleteMapping("/payCode/{id}")
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
}
