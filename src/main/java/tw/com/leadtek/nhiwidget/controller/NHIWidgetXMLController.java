/**
 * Created on 2021/1/25.
 */
package tw.com.leadtek.nhiwidget.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.constant.XMLConstant;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.DEPARTMENT;
import tw.com.leadtek.nhiwidget.model.rdb.IP;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP;
import tw.com.leadtek.nhiwidget.model.xml.OutPatient;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.MRCountResponse;
import tw.com.leadtek.nhiwidget.payload.MRDetail;
import tw.com.leadtek.nhiwidget.payload.MrNotePayload;
import tw.com.leadtek.nhiwidget.payload.QuickSearchResponse;
import tw.com.leadtek.nhiwidget.payload.SearchReq;
import tw.com.leadtek.nhiwidget.security.jwt.JwtUtils;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.service.LogDataService;
import tw.com.leadtek.nhiwidget.service.NHIWidgetXMLService;
import tw.com.leadtek.nhiwidget.service.ParametersService;
import tw.com.leadtek.nhiwidget.service.UserService;

@Api(tags = "申報檔相關API", value = "申報檔相關API")
@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
// @CrossOrigin(value = "http://localhost:8080")
@RequestMapping(produces = "application/json; charset=utf-8") // reponse 有中文才不會亂碼
public class NHIWidgetXMLController extends BaseController {

  @Autowired
  private NHIWidgetXMLService xmlService;

  @Autowired
  private ParametersService parameters;

  @Autowired
  private LogDataService logService;
  
  @Autowired
  private UserService userService;
  
  @Autowired
  private JwtUtils jwtUtils;

  @ApiOperation(value = "上傳申報檔XML檔案", notes = "上傳申報檔XML檔案")
  @ApiImplicitParams({@ApiImplicitParam(name = "file", paramType = "form", value = "申報檔XML檔案",
      dataType = "file", required = true)})
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @PostMapping(value = "/nhixml/uploadFile")
  public ResponseEntity<BaseResponse> upload(@RequestPart("file") MultipartFile file) {
    ObjectMapper xmlMapper = new XmlMapper();
    try {
      logger.info("start upload:" + file.getOriginalFilename());
      String xml = new String(file.getBytes(), "BIG5");
      if (xml.indexOf("inpatient") > 0) {
        IP ip = xmlMapper.readValue(xml, IP.class);
        xmlService.saveIP(ip);
      }
      if (xml.indexOf("outpatient") > 0) {
        OP op = xmlMapper.readValue(xml, OP.class);
        logger.info("start upload:" + file.getOriginalFilename() + " OP op");
        xmlService.saveOP(op);
      }
      return returnAPIResult(null);
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return returnAPIResult("system error");
  }

  @ApiOperation(value = "下載整個月份或指定區間的申報檔XML檔案", notes = "下載申報檔XML檔案")
  @GetMapping(value = "/nhixml/download", produces = "application/xml; charset=big5")
  public ResponseEntity<?> downloadFile(
      @ApiParam(name = "ym", value = "西元年月，如202103",
          example = "202103") @RequestParam(required = false) String ym,
      @ApiParam(name = "dataFormat", value = "資料格式，10:門診，20:住院，30:特約藥局，40:特約物理(職能)治療所...",
          example = "10") @RequestParam(required = true) String dataFormat,
      @ApiParam(name = "sdate", value = "起始日期，格式 yyyy/MM/dd",
          example = "2021/03/15") @RequestParam(required = false) String sdate,
      @ApiParam(name = "edate", value = "結束日期，格式 yyyy/MM/dd",
          example = "2021/03/18") @RequestParam(required = false) String edate,
      @ApiParam(name = "applType", value = "申報類別，1:送核，2:補報",
          example = "1") @RequestParam(required = false) String applType,
      HttpServletResponse response) throws UnsupportedEncodingException {

    try {
      String filename = (ym == null) ? sdate + ".xml" : ym + ".xml";
      int rows = 0;
      if (ym != null && ym.length() > 4) {
        String op = ("10".equals(dataFormat)) ? "OP" : "IP";
        String yearMonth = String.valueOf(Integer.parseInt(ym) - 191100);
        xmlService.getNHIXMLFile(yearMonth, op, response.getOutputStream());
      } else {
        rows =
            xmlService.getNHIXMLFileBetween(sdate, edate, dataFormat, response.getOutputStream());
      }
      logger.info("downloadFile rows = " + rows);
      if (rows > 0) {
        response.setContentType("application/xml; charset=big5");
        response.setCharacterEncoding("BIG5");
        // 下載檔案能正常顯示中文
        response.setHeader("Content-Disposition",
            "attachment; filename=" + URLEncoder.encode(filename, "BIG5"));
      }
      // 配置檔案下載
      logger.info("download/" + filename + ":" + rows);
    } catch (UnsupportedEncodingException e) {
      logger.error("downloadFile", e);
    } catch (IOException e) {
      logger.error("downloadFile", e);
    }
    return null;
  }

  /**
   * 取得代碼表內容
   * 
   * @param name
   * @param model
   * @return
   */
  @ApiOperation(value = "下載代碼表", notes = "下載代碼表")
  @GetMapping("/nhixml/code/{cat}")
  public ResponseEntity<List<CODE_TABLE>> getCT(@ApiParam(name = "cat",
      value = "代碼表類型，FUNC_TYPE:就診科別，PAY_TYPE:給付類別，OP_CASE_TYPE:門診案件分類，IP_CASE_TYPE:住院案件分類, PAY_STATUS:費用狀態, IP_TW_DRGS_SUIT_MARK:不適用Tw-DRGs案件特殊註記",
      example = "FUNC_TYPE") @PathVariable String cat) {
    logger.info("=========/nhixml/code/{" + cat + "} =============================");
    String catReal = HtmlUtils.htmlEscape(cat, "UTF-8").toUpperCase();
    List<CODE_TABLE> list = xmlService.getCodeTable(catReal);
    if (list.size() == 0) {
      return ResponseEntity.badRequest().body(new ArrayList<CODE_TABLE>());
    }
    return ResponseEntity.ok(list);
  }

  /**
   * 搜尋病歷
   * 
   * @param name
   * @param model
   * @return
   */
  @ApiOperation(value = "搜尋病歷", notes = "搜尋病歷")
  @GetMapping("/nhixml/search")
  public ResponseEntity<?> search(@RequestParam(required = false) String ym,
      @ApiParam(name = "funcType", value = "就診科別",
          example = "02") @RequestParam(required = false) String funcType,
      @ApiParam(name = "name", value = "病患姓名",
          example = "王小明") @RequestParam(required = false) String name,
      @RequestParam(required = false) String caseType, @RequestParam(required = false) String order,
      @RequestParam(required = false) String funcDate, @RequestParam(required = false) String icdCM,
      @RequestParam(required = false) Integer applyPoints,
      @RequestParam(required = false) String rocID, @RequestParam(required = false) Integer drugFee,
      @RequestParam(required = false) String prsnID, @RequestParam(required = false) String icdOP,
      @RequestParam(required = false) String status, @RequestParam(required = false) String all) {
    logger.info("=========/nhixml/search/{" + funcType + "} =============================");

    SearchReq req = new SearchReq(ym, funcType, name, caseType, order, funcDate, icdCM, applyPoints,
        rocID, drugFee, prsnID, icdOP, status, all);
    String result = xmlService.search(req);
    return ResponseEntity.ok(result);
  }

  /**
   * 檢查門診申報檔案內容是否有問題
   * 
   * @param op
   */
  private String checkOP(OutPatient op) {
    StringBuffer sb = new StringBuffer();
    if (!XMLConstant.DATA_FORMAT_OP.equals(op.getTdata().getDATA_FORMAT())) {
      sb.append("t1 資料格式有誤，應為" + XMLConstant.DATA_FORMAT_OP + "," + op.getTdata().getDATA_FORMAT());
      sb.append("\r\n");
    }

    return sb.toString();
  }

  /**
   * 取得指定日期區間的病歷
   * 
   * @param name
   * @param model
   * @return
   */
  @ApiOperation(value = "取得指定日期區間的病歷", notes = "取得指定日期區間的病歷")
  @GetMapping("/nhixml/mr_old")
  public ResponseEntity<List<MR>> selectMR(
      @ApiParam(name = "sdate", value = "起始日期，格式 yyyy/MM/dd",
          example = "2021/03/15") @RequestParam String sdate,
      @ApiParam(name = "edate", value = "結束日期，格式 yyyy/MM/dd",
          example = "2021/03/18") @RequestParam String edate,
      @ApiParam(name = "status", value = "病歷狀態：-2:待確認，-1:疑問標示，0:無需變更，1:優化完成，2:評估不調整",
          example = "0") @RequestParam(required = false) Integer status) {
    logger.info("=========/nhixml/mr/{" + sdate + "} =============================");
    List<MR> list = xmlService.getMRByMrDateBetween(sdate, edate, status);
    if (list.size() == 0) {
      return ResponseEntity.badRequest().body(new ArrayList<MR>());
    }
    return ResponseEntity.ok(list);
  }

  /**
   * 取得指定日期區間的病歷
   * 
   * @param name
   * @param model
   * @return
   */
  @ApiOperation(value = "取得指定條件的病歷", notes = "取得指定條件的病歷")
  @GetMapping("/nhixml/mr")
  public ResponseEntity<Map<String, Object>> getMR(
      @ApiParam(name = "allMatch", value = "單一分項內容如有空格 須/不須完全符合，Y/N",
          example = "N") @RequestParam(required = false) String allMatch,
      @ApiParam(name = "sdate", value = "起始日期，格式 yyyy/MM/dd",
          example = "2021/03/15") @RequestParam(required = false) String sdate,
      @ApiParam(name = "edate", value = "結束日期，格式 yyyy/MM/dd",
          example = "2021/03/18") @RequestParam(required = false) String edate,
      @ApiParam(name = "applY", value = "申報年，格式西元年 yyyy",
          example = "2021") @RequestParam(required = false) String applY,
      @ApiParam(name = "applM", value = "申報月，格式 M",
          example = "8") @RequestParam(required = false) String applM,
      @ApiParam(name = "minPoints", value = "最小申報點數",
          example = "175") @RequestParam(required = false) Integer minPoints,
      @ApiParam(name = "maxPoints", value = "最大申報點數",
          example = "2040") @RequestParam(required = false) Integer maxPoints,
      @ApiParam(name = "dataFormat", value = "資料格式，門急診:10，住院:20",
          example = "10") @RequestParam(required = false) String dataFormat,
      @ApiParam(name = "funcType", value = "科別代碼，如：00(不分科)，01(家醫科)，02(內科)，03(外科)...",
          example = "03") @RequestParam(required = false) String funcType,
      @ApiParam(name = "funcTypeC", value = "科別名稱，如：不分科、家醫科、內科、外科...",
      example = "03") @RequestParam(required = false) String funcTypeC,
      @ApiParam(name = "prsnName", value = "醫護名",
          example = "王大明") @RequestParam(required = false) String prsnName,
      @ApiParam(name = "prsnId", value = "醫護代碼",
          example = "A123456789") @RequestParam(required = false) String prsnId,
      @ApiParam(name = "pharName", value = "藥師名",
          example = "王小明") @RequestParam(required = false) String pharName,
      @ApiParam(name = "pharId", value = "藥師代碼",
          example = "A123456789") @RequestParam(required = false) String pharId,
      @ApiParam(name = "patientName", value = "病患名稱",
          example = "王小明") @RequestParam(required = false) String patientName,
      @ApiParam(name = "patientId", value = "病患身分證字號",
          example = "A123456789") @RequestParam(required = false) String patientId,
      @ApiParam(name = "applId", value = "標記人員代碼",
          example = "A123456789") @RequestParam(required = false) String applId,
      @ApiParam(name = "applName", value = "標記人員名稱",
          example = "A123456789") @RequestParam(required = false) String applName,
      @ApiParam(name = "inhMrId", value = "病歷編號",
          example = "11003191") @RequestParam(required = false) String inhMrId,
      @ApiParam(name = "inhClinicId", value = "就醫記錄編號",
          example = "11003190002") @RequestParam(required = false) String inhClinicId,
      @ApiParam(name = "drg", value = "DRGs代碼",
          example = "048201") @RequestParam(required = false) String drg,
      @ApiParam(name = "drgSection", value = "DRG落點區間",
          example = "B1") @RequestParam(required = false) String drgSection,
      @ApiParam(name = "orderCode", value = "支付標準代碼",
          example = "00156A") @RequestParam(required = false) String orderCode,
      @ApiParam(name = "drugUse", value = "用量",
          example = "1") @RequestParam(required = false) String drugUse,
      @ApiParam(name = "inhCode", value = "院內碼",
          example = "03001K") @RequestParam(required = false) String inhCode,
      @ApiParam(name = "inhCodeDrugUse", value = "院內碼用量",
          example = "03001K") @RequestParam(required = false) String inhCodeDrugUse,
      @ApiParam(name = "icdAll", value = "不分區ICD碼",
          example = "V20.0") @RequestParam(required = false) String icdAll,
      @ApiParam(name = "icdCMMajor", value = "主診斷ICD",
          example = "V20.0") @RequestParam(required = false) String icdCMMajor,
      @ApiParam(name = "icdCMSec", value = "次診斷ICD",
          example = "V20.0") @RequestParam(required = false) String icdCMSec,
      @ApiParam(name = "icdPCS", value = "處置ICD",
          example = "V20.0") @RequestParam(required = false) String icdPCS,
      @ApiParam(name = "qrObject",
          value = "品質獎勵計畫收案對象") @RequestParam(required = false) String qrObject,
      @ApiParam(name = "qrSdate",
          value = "品質獎勵計畫收案啟始日") @RequestParam(required = false) String qrSdate,
      @ApiParam(name = "qrEdate",
          value = "品質獎勵計畫收案結束日") @RequestParam(required = false) String qrEdate,
      @ApiParam(name = "status",
          value = "病歷狀態：-3:疾病分類完成, -2:待確認，-1:疑問標示，0:待處理，1:無需變更，2:優化完成，3:評估不調整",
          example = "1") @RequestParam(required = false) String status,
      @ApiParam(name = "deductedCode",
          value = "核刪代碼") @RequestParam(required = false) String deductedCode,
      @ApiParam(name = "deductedOrder",
          value = "核刪醫令") @RequestParam(required = false) String deductedOrder,
      @ApiParam(name = "notApplStatus",
          value = "不包含申報狀態") @RequestParam(required = false) Boolean notApplStatus,
      @ApiParam(name = "applThisMonth",
          value = "本月申報") @RequestParam(required = false) Boolean applThisMonth,
      @ApiParam(name = "applNextMonth",
          value = "下月申報") @RequestParam(required = false) Boolean applNextMonth,
      @ApiParam(name = "NoAppl",
          value = "不申報") @RequestParam(required = false) Boolean NoAppl,
      @ApiParam(name = "ownExpItem",
          value = "自費項目") @RequestParam(required = false) Boolean ownExpItem,
      @ApiParam(name = "all", value = "全站/其他搜尋/關鍵字",
          example = "0") @RequestParam(required = false) String all,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "第幾頁，第一頁值為0", example = "0") @RequestParam(required = false,
          defaultValue = "0") Integer page) {
    logger.info("=========/nhixml/mr/{" + sdate + "} =============================");
    if (minPoints != null) {
      if (maxPoints == null) {
        return ResponseEntity.badRequest().body(returnMRError("最大申報點數未帶入"));
      }
    }
    int iPerPage = (perPage == null) ? parameters.getIntParameter(ParametersService.PAGE_COUNT)
        : perPage.intValue();
    int iPage = (page == null) ? 0 : page.intValue();

    String startDate = (sdate == null) ? "2010/01/01" : sdate;
    String endDate = edate;
    if (endDate == null) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
      endDate = sdf.format(new Date());
    }
    String applYM = null;
    if (applY != null && applY.length() == 4) {
      int applYMInteger = 0;
      int year = Integer.parseInt(applY) - 1911;
      if (applM != null) {
        applYMInteger = year * 100 + Integer.parseInt(applM);
        applYM = String.valueOf(applYMInteger);
      }
    }
  
    funcType = addAllFuncType(funcType, funcTypeC);
    if (funcType != null && funcType.startsWith("error:")) {
      String error = funcType.substring("error:".length());
      HashMap<String, String> result = new HashMap<String, String>();
      result.put("result", BaseResponse.ERROR);
      result.put("message",error);
      return ResponseEntity.badRequest().body(returnMRError(error));
    }
    logService.updateLogSearch("system", allMatch, sdate, edate, minPoints, maxPoints, dataFormat,
        funcType, prsnId, prsnName, applId, applName, inhMrId, inhClinicId, drg, drgSection,
        orderCode, inhCode, drugUse, inhCodeDrugUse, icdAll, icdCMMajor, icdCMSec, icdPCS, qrObject,
        qrSdate, qrEdate, status, deductedCode, deductedOrder);

    Map<String, Object> list =
        xmlService.getMR(allMatch, startDate, endDate, applYM, minPoints, maxPoints, dataFormat,
            funcType, prsnId, prsnName, applId, applName, inhMrId, inhClinicId, drg, drgSection,
            orderCode, inhCode, drugUse, inhCodeDrugUse, icdAll, icdCMMajor, icdCMSec, icdPCS,
            qrObject, qrSdate, qrEdate, status, deductedCode, deductedOrder, all, patientName,
            patientId, pharId, iPerPage, iPage);
    if (list.size() == 0) {
      return ResponseEntity.badRequest().body(returnMRError("無符合條件資料"));
    }
    return ResponseEntity.ok(list);
  }

  // /**
  // * 取得指定日期區間的病歷
  // *
  // * @param name
  // * @param model
  // * @return
  // */
  // @ApiOperation(value = "資料倉儲搜尋病歷", notes = "資料倉儲搜尋病歷")
  // @GetMapping("/nhixml/dwSearch")
  // public ResponseEntity<List<MR>> dwSearch(
  // @ApiParam(name = "sdate", value = "起始日期，格式 yyyy/MM/dd", example = "2021/03/15")
  // @RequestParam(required = true) String sdate,
  // @ApiParam(name = "edate", value = "結束日期，格式 yyyy/MM/dd", example = "2021/03/18")
  // @RequestParam(required = true) String edate,
  // @ApiParam(name = "dataFormat", value = "資料格式，門急診:10，住院:20", example = "10")
  // @RequestParam(required = false) String dataFormat,
  // @ApiParam(name = "funcType", value = "科別代碼，00:不分科，01:家醫科，02:內科，03:外科...", example = "03")
  // @RequestParam(required = false) String funcType,
  // @ApiParam(name = "prsnName", value = "醫護名", example = "王大明") @RequestParam(required = false)
  // String prsnName,
  // @ApiParam(name = "prsnId", value = "醫護代碼", example = "A123456789") @RequestParam(required =
  // false) String prsnId,
  // @ApiParam(name = "applName", value = "標記人員名稱", example = "王小明") @RequestParam(required = false)
  // String applName,
  // @ApiParam(name = "applId", value = "標記人員代碼", example = "A123456789") @RequestParam(required =
  // false) String applId,
  // @ApiParam(name = "status", value = "病歷狀態：-3:疾病分類完成,
  // -2:待確認，-1:疑問標示，0:待處理，1:無需變更，2:優化完成，3:評估不調整", example = "1") @RequestParam(required = false)
  // String status,
  // @ApiParam(name = "perPage", value = "每頁顯示筆數", example = "20") @RequestParam(required = false,
  // defaultValue = "20") Integer perPage,
  // @ApiParam(name = "page", value = "第幾頁，第一頁值為0", example = "00") @RequestParam(required = false,
  // defaultValue = "0") Integer page)
  // {
  // logger.info("=========/nhixml/mr/{" + sdate + "} =============================");
  // List<MR> list = xmlService.dwSearch(sdate, edate, dataFormat, funcType,
  // prsnId, prsnName, applId, applName, status, perPage, page);
  // if (list.size() == 0) {
  // return ResponseEntity.badRequest().body(returnMRError("無符合條件資料"));
  // }
  // return ResponseEntity.ok(list);
  // }

  /**
   * 取得指定日期區間的病歷
   * 
   * @param name
   * @param model
   * @return
   */
  @ApiOperation(value = "快速搜尋", notes = "快速搜尋")
  @GetMapping("/nhixml/qs")
  public ResponseEntity<Map<String, Object>> quickSearch(
      @ApiParam(name = "sdate", value = "起始日期，格式 yyyy/MM/dd",
          example = "2021/03/15") @RequestParam(required = true) String sdate,
      @ApiParam(name = "edate", value = "結束日期，格式 yyyy/MM/dd",
          example = "2021/03/18") @RequestParam(required = true) String edate,
      @ApiParam(name = "minPoints", value = "最小申報點數",
          example = "175") @RequestParam(required = false) Integer minPoints,
      @ApiParam(name = "maxPoints", value = "最大申報點數",
          example = "2040") @RequestParam(required = false) Integer maxPoints,
      @ApiParam(name = "dataFormat", value = "資料格式，門急診:10，住院:20",
          example = "10") @RequestParam(required = false) String dataFormat,
      @ApiParam(name = "funcType", value = "科別，00:不分科，01:家醫科，02:內科，03:外科...",
          example = "03") @RequestParam(required = false) String funcType,
      @ApiParam(name = "orderCode", value = "健保碼",
          example = "03001K") @RequestParam(required = false) String orderCode,
      @ApiParam(name = "inhCode", value = "院內碼",
          example = "03001K") @RequestParam(required = false) String inhCode,
      @ApiParam(name = "icdCMMajor", value = "診斷碼",
          example = "V20.0") @RequestParam(required = false) String icdCMMajor,
      @ApiParam(name = "perPage", value = "每頁顯示筆數", example = "20") @RequestParam(required = false,
          defaultValue = "20") Integer perPage,
      @ApiParam(name = "page", value = "第幾頁，第一頁值為0", example = "00") @RequestParam(required = false,
          defaultValue = "0") Integer page) {
    logger.info("=========/nhixml/mr/{" + sdate + "} =============================");
    Map<String, Object> result = new HashMap<String, Object>();
    if (minPoints != null) {
      if (maxPoints == null) {
        List<QuickSearchResponse> list = new ArrayList<QuickSearchResponse>();
        QuickSearchResponse qsr = new QuickSearchResponse();
        qsr.setMessage("最大申報點數未帶入");
        list.add(qsr);
        result.put("totalPage", 0);
        result.put("qs", list);
        return ResponseEntity.badRequest().body(result);
      }
    }
    int iPerPage = (perPage == null) ? parameters.getIntParameter(ParametersService.PAGE_COUNT)
        : perPage.intValue();
    int iPage = (page == null) ? 0 : page.intValue();
    // List<QuickSearchResponse> list
    result = xmlService.quickSearch(iPerPage, iPage, sdate, edate, minPoints, maxPoints, dataFormat,
        funcType, orderCode, inhCode, icdCMMajor);
    if (result.get("qs") == null || ((List<QuickSearchResponse>) result.get("qs")).size() == 0) {
      List<QuickSearchResponse> list = new ArrayList<QuickSearchResponse>();
      QuickSearchResponse qsr = new QuickSearchResponse();
      qsr.setMessage("無符合條件資料");
      list.add(qsr);
      result.put("totalPage", 0);
      result.put("qs", list);
      return ResponseEntity.badRequest().body(result);
    }
    return ResponseEntity.ok(result);
  }

  private Map<String, Object> returnMRError(String message) {
    Map<String, Object> result = new HashMap<String, Object>();
    List<MR> mrs = new ArrayList<MR>();
    MR mr = new MR();
    mr.setRemark(message);
    mrs.add(mr);
    result.put("totalPage", new Integer(0));
    result.put("mr", mrs);
    return result;
  }

  /**
   * 取得指定日期區間的病歷
   * 
   * @param name
   * @param model
   * @return
   */
  @ApiOperation(value = "取得指定病歷", notes = "取得指定病歷")
  @GetMapping("/nhixml/mr/{id}")
  public ResponseEntity<MRDetail> getMRDetail(
      @ApiParam(name = "id", value = "病歷id", example = "146019") @PathVariable String id) {
    logger.info("=========/nhixml/mr/{" + id + "} =============================");
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      MRDetail result = new MRDetail();
      result.setError( "id格式不正確");
      return ResponseEntity.badRequest().body(result);
    }
    return ResponseEntity.ok(xmlService.getMRDetail(id));
  }

  @ApiOperation(value = "開始編輯指定病歷", notes = "開始編輯指定病歷")
  @PostMapping("/nhixml/mr/{id}")
  public ResponseEntity<BaseResponse> editMRDetail(@RequestHeader("Authorization") String token,
      @ApiParam(name = "id", value = "病歷id", example = "146019") @PathVariable String id) {
    logger.info("=========/nhixml/mr/{" + id + "} =============================");
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      return returnAPIResult("id有誤");
    }
    if (token == null || token.indexOf(' ') <0) {
      return returnAPIResult("JWT有誤");
    }
    String jwt = token.split(" ")[1];
    return returnAPIResult(xmlService.editMRDetail(idL, jwt, true));
  }

  @ApiOperation(value = "取消編輯指定病歷", notes = "取消編輯指定病歷")
  @PostMapping("/nhixml/mr/cancel/{id}")
  public ResponseEntity<BaseResponse> editMRDetailCancel(@RequestHeader("Authorization") String token,
      @ApiParam(name = "id", value = "病歷id", example = "146019") @PathVariable String id) {
    logger.info("=========/nhixml/mr/{" + id + "} =============================");
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      return returnAPIResult("id有誤");
    }
    if (token == null || token.indexOf(' ') <0) {
      return returnAPIResult("JWT有誤");
    }
    String jwt = token.split(" ")[1];
    return returnAPIResult(xmlService.editMRDetail(idL, jwt, false));
  }

  @ApiOperation(value = "更新指定病歷", notes = "更新指定病歷")
  @PutMapping("/nhixml/mr/{id}")
  public ResponseEntity<MRDetail> updateMRDetail(@RequestHeader("Authorization") String jwt,
      @ApiParam(name = "id", value = "病歷id", example = "146019") @PathVariable String id,
      @ApiParam(name = "mrDetail",
          value = "病歷詳細資訊") @RequestBody(required = true) MRDetail mrDetail) {
    logger.info("=========/nhixml/mr/{" + id + "} =============================");
    mrDetail.setId(Long.parseLong(id));
    MRDetail result = xmlService.updateMRDetail(mrDetail, jwt);
    if (result == null) {
      return ResponseEntity.ok(result);
    } else {
      return ResponseEntity.badRequest().body(result);
    }
  }
  
  @ApiOperation(value = "更改指定病歷狀態", notes = "更改指定病歷狀態，參數 status 或 statusCode 二擇一帶入")
  @PutMapping("/nhixml/mr/status/{id}")
  public ResponseEntity<BaseResponse> editMRStatus(
      @ApiParam(name = "id", value = "病歷id", example = "146020") @PathVariable String id, 
      @ApiParam(name = "status",
      value = "病歷狀態, 有：疾病分類完成、待確認、疑問標示、待處理、無需變更、優化完成、評估不調整", example = "無需變更") @RequestParam(required = false) String status,
      @ApiParam(name = "statusCode",
      value = "病歷狀態代碼，-3:疾病分類完成，-2:待確認，-1:疑問標示，0:待處理，1:無需變更，2:優化完成，3:評估不調整", example = "1") @RequestParam(required = false) Integer statusCode) {
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      return returnAPIResult("id有誤");
    }
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      return returnAPIResult("無法取得登入狀態");
    }
    int statusInt = MR_STATUS.STATUS_ERROR;
    if (status != null && status.length() > 0) {
      statusInt = MR_STATUS.statusStringToInt(status);
    } else if (statusCode != null) {
      statusInt = statusCode.intValue();
      if (MR_STATUS.STATUS_ERROR_DESC.equals(MR_STATUS.toStatusString(statusInt))) {
        statusInt = MR_STATUS.STATUS_ERROR;
      }
    }
    
    if (statusInt == MR_STATUS.STATUS_ERROR) {
      return returnAPIResult("病歷狀態有誤");
    }
    return returnAPIResult(xmlService.updateMRStatus(idL, user, statusInt));
  }

  /**
   * 取得指定日期區間的病歷
   */
  @ApiOperation(value = "取得指定日期區間的病歷總數", notes = "取得指定日期區間的病歷總數")
  @GetMapping("/nhixml/mrCount")
  public ResponseEntity<MRCountResponse> selectMRCountByMrDate(
      @ApiParam(name = "sdate", value = "起始日期，格式 yyyy/MM/dd",
          example = "2021/03/15") @RequestParam String sdate,
      @ApiParam(name = "edate", value = "結束日期，格式 yyyy/MM/dd",
          example = "2021/03/18") @RequestParam String edate,
      @ApiParam(name = "dataFormat", value = "資料格式，門急診:10，住院:20，不分: 00",
          example = "10") @RequestParam(required = false) String dataFormat,
      @ApiParam(name = "funcType", value = "科別，00:不分科，01:家醫科，02:內科，03:外科...",
          example = "03") @RequestParam(required = false) String funcType,
      @ApiParam(name = "prsnId", value = "醫護代碼",
          example = "A123456789") @RequestParam(required = false) String prsnId,
      @ApiParam(name = "applId", value = "標記人員代碼",
          example = "A123456789") @RequestParam(required = false) String applId) {
    logger.info("=========/nhixml/mr/{" + sdate + "} =============================");
    if (dataFormat == null) {
      dataFormat = "00";
    }
    MRCountResponse response =
        xmlService.getMRCount(sdate, edate, dataFormat, funcType, prsnId, applId);
    return ResponseEntity.ok(response);
  }

  @ApiOperation(value = "新增病歷資訊備註/核刪註記", notes = "新增病歷資訊備註/核刪註記")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "新增成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PostMapping("/nhixml/note")
  public ResponseEntity<BaseResponse> newMrNote(
      @ApiParam(name = "id", value = "病歷id",
          example = "13220") @RequestParam(required = true) String id,
      @ApiParam(name = "isNote", value = "是否為病歷資訊備註，true:是，false:否",
          example = "13220") @RequestParam(required = true) Boolean isNote,
      @ApiParam(name = "note", value = "備註內容",
          example = "新增病歷資訊備註內容") @RequestParam(required = true) String note) {
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      BaseResponse br = new BaseResponse();
      br.setResult("failed");
      br.setMessage("未登入，無法執行");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(br);
    }
    MrNotePayload request = new MrNotePayload();
    request.setNote(note);
    request.setEditor(user.getUsername());
    request.setActionType("新增");
    return returnAPIResult(xmlService.newMrNote(request, id, isNote));
  }

  @ApiOperation(value = "修改病歷資訊備註/核刪註記", notes = "修改病歷資訊備註/核刪註記")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/nhixml/note")
  public ResponseEntity<BaseResponse> updateMrNote(
      @ApiParam(name = "id", value = "病歷id",
          example = "13220") @RequestParam(required = true) String id,
      @ApiParam(name = "isNote", value = "是否為病歷資訊備註，true:是，false:否",
          example = "13220") @RequestParam(required = true) Boolean isNote,
      @ApiParam(name = "note", value = "備註內容",
          example = "修改病歷資訊備註測試") @RequestParam(required = true) String note) {
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      BaseResponse br = new BaseResponse();
      br.setResult("failed");
      br.setMessage("未登入，無法執行");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(br);
    }
    MrNotePayload request = new MrNotePayload();
    request.setNote(note);
    request.setEditor(user.getUsername());
    request.setActionType("修改");
    return returnAPIResult(xmlService.newMrNote(request, id, isNote));
  }

  @ApiOperation(value = "刪除病歷資訊備註/核刪註記", notes = "刪除病歷資訊備註/核刪註記")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @DeleteMapping("/nhixml/note")
  public ResponseEntity<BaseResponse> deleteMrNote(
      @ApiParam(name = "id", value = "病歷id",
          example = "13220") @RequestParam(required = true) String id,
      @ApiParam(name = "isNote", value = "是否為病歷資訊備註，true:是，false:否",
          example = "13220") @RequestParam(required = true) Boolean isNote,
      @ApiParam(name = "note", value = "備註內容",
          example = "刪除測試") @RequestParam(required = false) String note) {
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      BaseResponse br = new BaseResponse();
      br.setResult("failed");
      br.setMessage("未登入，無法執行");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(br);
    }
    MrNotePayload request = new MrNotePayload();
    request.setNote(note);
    request.setEditor(user.getUsername());
    request.setActionType("刪除");
    return returnAPIResult(xmlService.newMrNote(request, id, isNote));
  }
  
  public String addAllFuncType(String funcType, String funcTypeC) {
    if (funcType == null && funcTypeC == null) {
      return null;
    }
    StringBuffer sb = new StringBuffer();
    if (funcType != null && funcType.length() > 0) {
      String[] ss = funcType.split(" ");
      for (String string : ss) {
        sb.append(string);
        sb.append(" ");
      }
      if (sb.charAt(sb.length() - 1) == ' ') {
        sb.deleteCharAt(sb.length() - 1);
      }
    }
    if (funcTypeC != null && funcTypeC.length() > 0) {
      List<DEPARTMENT> departments = userService.getAllDepartment(null ,null);
      String[] ss = funcTypeC.split(" ");
      for (String string : ss) {
        boolean isFound = false;
        for (DEPARTMENT department : departments) {
          if (department.getName().equals(string)) {
            sb.append(department.getCode());
            sb.append(" ");
            isFound = true;
            break;
          }
        }
        if(!isFound) {
          return "error:錯誤的科別名稱：" + string; 
        }
      }
      if (sb.charAt(sb.length() - 1) == ' ') {
        sb.deleteCharAt(sb.length() - 1);
      }
    }
    return sb.toString();
  }
}
