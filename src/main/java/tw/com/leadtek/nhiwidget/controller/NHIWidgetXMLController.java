/**
 * Created on 2021/1/25.
 */
package tw.com.leadtek.nhiwidget.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import tw.com.leadtek.nhiwidget.constant.ACTION_TYPE;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.DEDUCTED_NOTE;
import tw.com.leadtek.nhiwidget.model.rdb.DEPARTMENT;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.xml.OutPatient;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.DeductedNoteListResponse;
import tw.com.leadtek.nhiwidget.payload.MRCountResponse;
import tw.com.leadtek.nhiwidget.payload.MRDetail;
import tw.com.leadtek.nhiwidget.payload.MrNoteListResponse;
import tw.com.leadtek.nhiwidget.payload.MrNotePayload;
import tw.com.leadtek.nhiwidget.payload.SearchReq;
import tw.com.leadtek.nhiwidget.payload.mr.DrgListPayload;
import tw.com.leadtek.nhiwidget.payload.mr.EditMRPayload;
import tw.com.leadtek.nhiwidget.payload.mr.HomepageParameters;
import tw.com.leadtek.nhiwidget.payload.mr.SearchMRParameters;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.service.CodeTableService;
import tw.com.leadtek.nhiwidget.service.LogDataService;
import tw.com.leadtek.nhiwidget.service.NHIWidgetXMLService;
import tw.com.leadtek.nhiwidget.service.ParametersService;
import tw.com.leadtek.nhiwidget.service.UserService;
import tw.com.leadtek.tools.StringUtility;

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
  private CodeTableService codeTableService;


//  @ApiOperation(value = "上傳申報檔XML檔案", notes = "上傳申報檔XML檔案")
//  @ApiImplicitParams({@ApiImplicitParam(name = "file", paramType = "form", value = "申報檔XML檔案",
//      dataType = "file", required = true)})
//  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
//  @PostMapping(value = "/nhixml/uploadFile")
//  public ResponseEntity<BaseResponse> upload(@RequestPart("file") MultipartFile file) {
//    ObjectMapper xmlMapper = new XmlMapper();
//    try {
//      logger.info("start upload:" + file.getOriginalFilename());
//      String xml = new String(file.getBytes(), "BIG5");
//      if (xml.indexOf("inpatient") > 0) {
//        IP ip = xmlMapper.readValue(xml, IP.class);
//        xmlService.saveIP(ip);
//      }
//      if (xml.indexOf("outpatient") > 0) {
//        OP op = xmlMapper.readValue(xml, OP.class);
//        logger.info("start upload:" + file.getOriginalFilename() + " OP op");
//        xmlService.saveOP(op);
//      }
//      return returnAPIResult(null);
//    } catch (JsonMappingException e) {
//      e.printStackTrace();
//    } catch (JsonProcessingException e) {
//      e.printStackTrace();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    return returnAPIResult("system error");
//  }

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
//    if (!XMLConstant.DATA_FORMAT_OP.equals(op.getTdata().getDATA_FORMAT())) {
//      sb.append("t1 資料格式有誤，應為" + XMLConstant.DATA_FORMAT_OP + "," + op.getTdata().getDATA_FORMAT());
//      sb.append("\r\n");
//    }

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
      @ApiParam(name = "indate", value = "住院日期，格式 yyyy/MM/dd",
          example = "2021/03/15") @RequestParam(required = false) String indate,
      @ApiParam(name = "outdate", value = "出院日期，格式 yyyy/MM/dd",
          example = "2021/03/18") @RequestParam(required = false) String outdate,
      @ApiParam(name = "applY", value = "統計月份年，格式西元年 yyyy",
          example = "2021") @RequestParam(required = false) String applY,
      @ApiParam(name = "applM", value = "統計月份月，格式 M",
          example = "8") @RequestParam(required = false) String applM,
      @ApiParam(value = "不包含其他條件", example = "false") @RequestParam(required = false) Boolean notOthers,
      @ApiParam(name = "minPoints", value = "最小申報點數",
          example = "175") @RequestParam(required = false) Integer minPoints,
      @ApiParam(name = "maxPoints", value = "最大申報點數",
          example = "2040") @RequestParam(required = false) Integer maxPoints,
      @ApiParam(name = "dataFormat", value = "資料格式，門急診:10，住院:20",
          example = "10") @RequestParam(required = false) String dataFormat,
      @ApiParam(name = "funcType", value = "科別代碼，如：00(不分科)，01(家醫科)，02(內科)，03(外科)...",
          example = "03") @RequestParam(required = false) String funcType,
      @ApiParam(name = "funcTypec", value = "科別名稱，如：不分科、家醫科、內科、外科...",
          example = "03") @RequestParam(required = false) String funcTypec,
      @ApiParam(name = "prsnName", value = "醫護名",
          example = "王大明") @RequestParam(required = false) String prsnName,
      @ApiParam(name = "prsnId", value = "醫護代碼",
          example = "A123456789") @RequestParam(required = false) String prsnId,
      @ApiParam(name = "pharName", value = "藥師名",
          example = "王小明") @RequestParam(required = false) String pharName,
      @ApiParam(name = "pharId", value = "藥師代碼",
          example = "A123456789") @RequestParam(required = false) String pharId,
      @ApiParam(name = "patientName", value = "病患姓名",
          example = "王小明") @RequestParam(required = false) String patientName,
      @ApiParam(name = "patientId", value = "病患身分證字號",
          example = "A123456789") @RequestParam(required = false) String patientId,
      @ApiParam(name = "applId", value = "標記人員代碼",
          example = "A123456789") @RequestParam(required = false) String applId,
      @ApiParam(name = "applName", value = "標記人員名稱",
          example = "A123456789") @RequestParam(required = false) String applName,
      @ApiParam(name = "inhMrId", value = "病歷號碼",
          example = "11003191") @RequestParam(required = false) String inhMrId,
      @ApiParam(name = "inhClinicId", value = "就醫記錄編號",
          example = "11003190002") @RequestParam(required = false) String inhClinicId,
      @ApiParam(value = "只抓有DRG病歷", example = "false") @RequestParam(required = false) Boolean onlyDRG,
      @ApiParam(value = "不包含DRG條件", example = "false") @RequestParam(required = false) Boolean notDRG,
      @ApiParam(name = "drg", value = "DRGs代碼",
          example = "048201") @RequestParam(required = false) String drg,
      @ApiParam(name = "drgSection", value = "DRG落點區間，多筆請用空格隔開",
          example = "B1 B2") @RequestParam(required = false) String drgSection,
      @ApiParam(value = "不包含醫令條件", example = "false") @RequestParam(required = false) Boolean notOrderCode,
      @ApiParam(name = "orderCode", value = "支付標準代碼，多筆用空格隔開",
          example = "00156A") @RequestParam(required = false) String orderCode,
      @ApiParam(name = "drugUse", value = "用量，多筆用空格隔開，未填請帶 0",
          example = "1") @RequestParam(required = false) String drugUse,
      @ApiParam(name = "inhCode", value = "院內碼",
          example = "C013") @RequestParam(required = false) String inhCode,
      @ApiParam(name = "inhCodeDrugUse", value = "院內碼用量",
          example = "03001K") @RequestParam(required = false) String inhCodeDrugUse,
      @ApiParam(value = "不包含ICD條件", example = "false") @RequestParam(required = false) Boolean notICD,
      @ApiParam(name = "icdAll", value = "不分區ICD碼",
          example = "V20.0") @RequestParam(required = false) String icdAll,
      @ApiParam(name = "icdCMMajor", value = "主診斷ICD",
          example = "V20.0") @RequestParam(required = false) String icdCMMajor,
      @ApiParam(name = "icdCMSec", value = "次診斷ICD",
          example = "V20.0") @RequestParam(required = false) String icdCMSec,
      @ApiParam(name = "icdPCS", value = "處置ICD",
          example = "V20.0") @RequestParam(required = false) String icdPCS,
//      @ApiParam(name = "qrObject",
//          value = "品質獎勵計畫收案對象") @RequestParam(required = false) String qrObject,
//      @ApiParam(name = "qrSdate",
//          value = "品質獎勵計畫收案啟始日") @RequestParam(required = false) String qrSdate,
//      @ApiParam(name = "qrEdate",
//          value = "品質獎勵計畫收案結束日") @RequestParam(required = false) String qrEdate,
      @ApiParam(value = "不包含資料狀態", example = "false") @RequestParam(required = false) Boolean notStatus,
      @ApiParam(name = "status",
          value = "病歷狀態：-3:疾病分類完成, -2:待確認，-1:疑問標示，0:待處理，1:無需變更，2:優化完成，3:評估不調整",
          example = "1") @RequestParam(required = false) String status,
      @ApiParam(value = "不包含被核刪條件", example = "false") @RequestParam(required = false) Boolean notDeducted,
      @ApiParam(name = "deductedCode",
          value = "核刪代碼") @RequestParam(required = false) String deductedCode,
      @ApiParam(name = "deductedOrder",
          value = "核刪醫令") @RequestParam(required = false) String deductedOrder,
      @ApiParam(value = "不包含申報狀態") @RequestParam(required = false) Boolean notApplStatus,
      @ApiParam(name = "applThisMonth",
          value = "本月申報") @RequestParam(required = false) Boolean applThisMonth,
      @ApiParam(name = "applNextMonth",
          value = "下月申報") @RequestParam(required = false) Boolean applNextMonth,
      @ApiParam(name = "NoAppl", value = "不申報") @RequestParam(required = false) Boolean NoAppl,
      @ApiParam(name = "ownExpItem",
          value = "自費項目") @RequestParam(required = false) Boolean ownExpItem,
      @ApiParam(name = "all", value = "全站/其他搜尋/關鍵字",
          example = "0") @RequestParam(required = false) String all,
      @ApiParam(name = "orderBy",
          value = "排序欄位名稱，status:資料狀態，sdate:就醫日期-起，edate:就醫日期-迄，inhMrId:病歷號碼，inhClinicId:就醫記錄編號，patientName:病患姓名，funcType:科別代碼，funcTypec:科別，prsnId:醫護代碼，prsnName:醫護姓名，totalDot:病歷點數，drgFixed:DRG定額，drgSection:DRG落點，applId:負員人員代碼，applName:負責人員",
          example = "code") @RequestParam(required = false) String orderBy,
      @ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小",
          example = "true") @RequestParam(required = false) Boolean asc,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "第幾頁，第一頁值為0", example = "0") @RequestParam(required = false,
          defaultValue = "0") Integer page) {

    if (minPoints != null) {
      if (maxPoints == null) {
        return ResponseEntity.badRequest().body(returnMRError("最大申報點數未帶入"));
      }
    }
    funcType = addAllFuncType(funcType, funcTypec);
    
    SearchMRParameters smrp = new SearchMRParameters(userService);
    smrp.setBasic(applY, applM, sdate, edate, indate, outdate, inhMrId, inhClinicId, dataFormat);
    smrp.setOthers(notOthers, minPoints, maxPoints, funcType, funcTypec, prsnId, prsnName, pharName, pharId, patientName, patientId, applId, applName);
    smrp.setICD(notICD, icdAll, icdCMMajor, icdCMSec, icdPCS);
    smrp.setOrder(notOrderCode, orderCode, drugUse, inhCode, inhCodeDrugUse);
    smrp.setApplStatus(notApplStatus, applThisMonth, applNextMonth, NoAppl, ownExpItem);
    smrp.setDRG(notDRG, drg, drgSection, onlyDRG);
    smrp.setDeducted(notDeducted, deductedCode, deductedOrder);
    smrp.setStatus(notStatus, status);
    smrp.setAll(all, orderBy, asc, perPage, page);
    if (smrp.getFuncTypec() != null) {
      smrp.setFuncType(codeTableService.convertFuncTypecToFuncType(smrp.getFuncTypec()));
    }
    
    int iPerPage = (perPage == null) ? parameters.getIntParameter(ParametersService.PAGE_COUNT)
        : perPage.intValue();
    int iPage = (page == null) ? 0 : page.intValue();

    if (funcType != null && funcType.startsWith("error:")) {
      String error = funcType.substring("error:".length());
      HashMap<String, String> result = new HashMap<String, String>();
      result.put("result", BaseResponse.ERROR);
      result.put("message", error);
      return ResponseEntity.badRequest().body(returnMRError(error));
    }
    logService.updateLogSearch("system", allMatch, sdate, edate, minPoints, maxPoints, dataFormat,
        funcType, prsnId, prsnName, applId, applName, inhMrId, inhClinicId, drg, drgSection,
        orderCode, inhCode, drugUse, inhCodeDrugUse, icdAll, icdCMMajor, icdCMSec, icdPCS, null,
        null, null, status, deductedCode, deductedOrder);

    String startDate = sdate;
    String endDate = edate;
    String applYM = applY + applM;
//    Map<String, Object> list = xmlService.getMR(allMatch, startDate, endDate, applYM, minPoints,
//        maxPoints, dataFormat, funcType, prsnId, prsnName, applId, applName, inhMrId, inhClinicId,
//        drg, drgSection, orderCode, inhCode, drugUse, inhCodeDrugUse, icdAll, icdCMMajor, icdCMSec,
//        icdPCS, null, null, null, status, deductedCode, deductedOrder, all, patientName,
//        patientId, pharId, iPerPage, iPage);
    Map<String, Object> list = xmlService.getMR(smrp);
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
  // List<MR> list = xmlService.dwSearch(sdate, edate, dataFormat, funcType,
  // prsnId, prsnName, applId, applName, status, perPage, page);
  // if (list.size() == 0) {
  // return ResponseEntity.badRequest().body(returnMRError("無符合條件資料"));
  // }
  // return ResponseEntity.ok(list);
  // }

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
      @ApiParam(name = "id", value = "病歷id", example = "146019") @PathVariable String id,
      @ApiParam(name = "isRaw", value = "是否取得最新資料", example = "true") 
        @RequestParam(defaultValue = "false", required = false) Boolean isRaw) {
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      MRDetail result = new MRDetail();
      result.setError("id格式不正確");
      return ResponseEntity.badRequest().body(result);
    }
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      MRDetail result = new MRDetail();
      result.setError("無法取得登入狀態");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
    return ResponseEntity.ok(xmlService.getMRDetail(idL, user, isRaw));
  }
  
  @ApiOperation(value = "取得指定病歷的全部資訊備註/核刪註記", notes = "取得指定病歷的全部資訊備註/核刪註記")
  @GetMapping("/nhixml/mrNote/{id}")
  public ResponseEntity<MrNoteListResponse> getMRNote(
      @ApiParam(name = "id", value = "病歷id", example = "146019") @PathVariable String id,
      @ApiParam(name = "isNote", value = "病歷id", example = "true") @RequestParam(required = true) Boolean isNote) {
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      MrNoteListResponse result = new MrNoteListResponse();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("id格式不正確");
      return ResponseEntity.badRequest().body(result);
    }
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      MrNoteListResponse result = new MrNoteListResponse();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("無法取得登入狀態");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
    return ResponseEntity.ok(xmlService.getMRNote(idL, user, isNote));
  }
  
  @ApiOperation(value = "取得指定病歷的全部資訊備註", notes = "取得指定病歷的全部資訊備註")
  @GetMapping("/nhixml/note/{id}")
  public ResponseEntity<MrNoteListResponse> getAllMRNote(
      @ApiParam(name = "id", value = "病歷id", example = "146019") @PathVariable String id,
      @ApiParam(name = "isNote", value = "true:資料備註", example = "true") @RequestParam(defaultValue = "true", required = false) Boolean isNote) {
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      MrNoteListResponse result = new MrNoteListResponse();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("id格式不正確");
      return ResponseEntity.badRequest().body(result);
    }
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      MrNoteListResponse result = new MrNoteListResponse();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("無法取得登入狀態");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
    return ResponseEntity.ok(xmlService.getMRNote(idL, user, isNote));
  }

  @ApiOperation(value = "開始編輯指定病歷", notes = "開始編輯指定病歷")
  @PostMapping("/nhixml/mr/{id}")
  public ResponseEntity<EditMRPayload> editMRDetail(@RequestHeader("Authorization") String token,
      @ApiParam(name = "id", value = "病歷id", example = "146019") @PathVariable String id) {
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      EditMRPayload result = new EditMRPayload();
      result.setMessage("id有誤");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.badRequest().body(result);
    }
    if (token == null || token.indexOf(' ') < 0) {
      EditMRPayload result = new EditMRPayload();
      result.setMessage("JWT有誤");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.badRequest().body(result);
    }
    String jwt = token.split(" ")[1];
    return ResponseEntity.ok(xmlService.editMRDetail(idL, jwt, true, 0));
  }

  @ApiOperation(value = "取消編輯指定病歷", notes = "取消編輯指定病歷")
  @PostMapping("/nhixml/mr/cancel/{id}")
  public ResponseEntity<EditMRPayload> editMRDetailCancel(
      @RequestHeader("Authorization") String token,
      @ApiParam(name = "id", value = "病歷id", example = "146019") @PathVariable String id,
      @ApiParam(value = "開始編輯時取得的actionId", example = "1") @RequestParam(required = false) Integer actionId) {
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      EditMRPayload result = new EditMRPayload();
      result.setMessage("id有誤");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.badRequest().body(result);
    }
    if (token == null || token.indexOf(' ') < 0) {
      EditMRPayload result = new EditMRPayload();
      result.setMessage("JWT有誤");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.badRequest().body(result);
    }
    String jwt = token.split(" ")[1];
    return ResponseEntity.ok(xmlService.editMRDetail(idL, jwt, false, actionId));
  }

  @ApiOperation(value = "更新指定病歷", notes = "更新指定病歷")
  @PutMapping("/nhixml/mr/{id}")
  public ResponseEntity<MRDetail> updateMRDetail(@RequestHeader("Authorization") String jwt,
      @ApiParam(name = "id", value = "病歷id", example = "146020") @PathVariable String id,
      @ApiParam(name = "mrDetail",
          value = "病歷詳細資訊") @RequestBody(required = true) MRDetail mrDetail, 
      @ApiParam(value = "開始編輯時取得的actionId", example = "1") @RequestParam(required = false) Integer actionId) {

    mrDetail.setId(Long.parseLong(id));
    String token = jwt.split(" ")[1];
    MRDetail result = xmlService.updateMRDetail(mrDetail, token, actionId);
    if (result.getError() == null) {
      return ResponseEntity.ok(result);
    } else {
      return ResponseEntity.badRequest().body(result);
    }
  }

  @ApiOperation(value = "更改指定病歷狀態", notes = "更改指定病歷狀態，參數 status 或 statusCode 二擇一帶入")
  @PutMapping("/nhixml/mr/status/{id}")
  public ResponseEntity<BaseResponse> editMRStatus(
      @ApiParam(name = "id", value = "病歷id", example = "146020") @PathVariable String id,
      @ApiParam(name = "status", value = "病歷狀態, 有：疾病分類完成、待確認、疑問標示、待處理、無需變更、優化完成、評估不調整",
          example = "無需變更") @RequestParam(required = false) String status,
      @ApiParam(name = "statusCode",
          value = "病歷狀態代碼，-3:疾病分類完成，-2:待確認，-1:疑問標示，0:待處理，1:無需變更，2:優化完成，3:評估不調整",
          example = "1") @RequestParam(required = false) Integer statusCode) {
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      return returnAPIResult("id有誤");
    }
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(BaseResponse.ERROR, "無法取得登入狀態"));
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
      @ApiParam(name = "applY", value = "申報年，格式西元年 yyyy",
        example = "2021") @RequestParam(required = false) String applY,
      @ApiParam(name = "applM", value = "申報月，格式 M",
        example = "8") @RequestParam(required = false) String applM,
      @ApiParam(name = "sdate", value = "起始日期，格式 yyyy/MM/dd",
          example = "2021/03/15") @RequestParam(required = false) String sdate,
      @ApiParam(name = "edate", value = "結束日期，格式 yyyy/MM/dd",
          example = "2021/03/18") @RequestParam(required = false) String edate,
      @ApiParam(name = "dataFormat", value = "就醫類別，門急診:10，住院:20，不分: 00",
          example = "10") @RequestParam(required = false) String dataFormat,
      @ApiParam(name = "funcType", value = "科別，00:不分科，01:家醫科，02:內科，03:外科...",
          example = "03") @RequestParam(required = false) String funcType,
      @ApiParam(name = "prsnName", value = "醫護姓名",
          example = "A123456789") @RequestParam(required = false) String prsnName) {
    HomepageParameters hp = new HomepageParameters(applY, applM, sdate, edate, dataFormat, funcType, prsnName);
    MRCountResponse response =
        xmlService.getHomePageMRCount(hp);
    return ResponseEntity.ok(response);
  }

  @ApiOperation(value = "新增病歷資訊備註", notes = "新增病歷資訊備註")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "新增成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PostMapping("/nhixml/note")
  public ResponseEntity<BaseResponse> newMrNote(
      @ApiParam(name = "id", value = "病歷id",
          example = "13220") @RequestParam(required = false) String id,
      @ApiParam(name = "isNote", value = "是否為病歷資訊備註，true:是，false:否",
          example = "13220") @RequestParam(required = false) Boolean isNote,
      @ApiParam(name = "note", value = "備註內容",
          example = "新增病歷資訊備註內容") @RequestParam(required = false) String note,
      @ApiParam(value = "備註內容，id:帶病歷id，note:備註內容") @RequestBody(required = false) MrNotePayload notePayload) {
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      BaseResponse br = new BaseResponse();
      br.setResult("failed");
      br.setMessage("未登入，無法執行");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(br);
    }
    MrNotePayload request = new MrNotePayload();
    request.setEditor(user.getUsername());
    request.setActionType("新增");
    if (notePayload != null) {
      request.setNote(notePayload.getNote());
      return returnAPIResult(xmlService.newMrNote(request, notePayload.getId().toString(), false));
    } else {
      request.setNote(note);
      return returnAPIResult(xmlService.newMrNote(request, id, false));
    }
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
    return returnAPIResult(xmlService.newMrNote(request, id, false));
  }

  @ApiOperation(value = "刪除病歷資訊備註", notes = "刪除病歷資訊備註")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @DeleteMapping("/nhixml/note")
  public ResponseEntity<BaseResponse> deleteMrNote(
      @ApiParam(name = "id", value = "病歷id",
          example = "13220") @RequestParam(required = true) String id,
      @ApiParam(name = "noteId", value = "註記id",
      example = "8") @RequestParam(required = true) String noteId) {
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      BaseResponse br = new BaseResponse();
      br.setResult("failed");
      br.setMessage("未登入，無法執行");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(br);
    }
    MrNotePayload request = new MrNotePayload();
    request.setId(Long.parseLong(noteId));
    request.setEditor(user.getUsername());
    request.setActionType("刪除");
    return returnAPIResult(xmlService.newMrNote(request, id, true));
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
      List<DEPARTMENT> departments = userService.getAllDepartment(null, null, 0);
      String[] ss = funcTypeC.split(" ");
      for (String string : ss) {
        boolean isFound = false;
        for (DEPARTMENT department : departments) {
          if (string.equals(department.getName()) || string.equals(department.getNhName())) {
            sb.append(department.getCode());
            sb.append(" ");
            isFound = true;
            break;
          }
        }
        if (!isFound) {
          return "error:錯誤的科別名稱：" + string;
        }
      }
      if (sb.charAt(sb.length() - 1) == ' ') {
        sb.deleteCharAt(sb.length() - 1);
      }
    }
    return sb.toString();
  }
  
  @ApiOperation(value = "發送一筆病歷通知", notes = "發送通知")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @PostMapping(value = "/nhixml/sendNotice/{id}")
  public ResponseEntity<BaseResponse> sendNotice(@PathVariable String id, 
      @ApiParam(name = "doctorId", value = "接收對象id，若多筆用空格隔開",
      example = "425 426 427") @RequestParam(required = true) String doctorId) {
    
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(BaseResponse.ERROR, "無法取得登入狀態"));
    }
    
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      return returnAPIResult("病歷id格式不正確");
    }
    return returnAPIResult(xmlService.sendNotice(id, user, doctorId));
  }
  
  @ApiOperation(value = "發送多筆病歷通知", notes = "發送通知")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @PostMapping(value = "/nhixml/sendNotice")
  public ResponseEntity<BaseResponse> sendManyNotice( 
      @ApiParam(value = "病歷id，若多筆用空格隔開", example = "1306412 1306413 1306414") 
      @RequestParam(required = true) String id,
      @ApiParam(name = "doctorId", value = "接收對象id，若多筆用空格隔開",
      example = "425 426 427") @RequestParam(required = true) String doctorId) {
    
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(BaseResponse.ERROR, "無法取得登入狀態"));
    }
    
    String[] ids = StringUtility.splitBySpace(id);
    if (ids == null || ids.length == 0) {
      return returnAPIResult("病歷id有誤");
    }
    String result = xmlService.sendNotice(id, user, doctorId);
    if (result != null) {
      return returnAPIResult(result);
    }
    return returnAPIResult(null);
  }
  
//  @ApiOperation(value = "測試用：新增病歷", notes = "新增病歷")
//  @PostMapping("/nhixml/mrdetail")
//  public ResponseEntity<MRDetail> addMRDetail(
//      @ApiParam(name = "mrDetail", value = "病歷詳細資訊") @RequestBody(required = true) MRDetail mrDetail) {
//    MRDetail result = xmlService.addMRDetail(mrDetail);
//    if (result == null) {
//      return ResponseEntity.ok(result);
//    } else {
//      return ResponseEntity.badRequest().body(result);
//    }
//  }
  
  @ApiOperation(value = "新增病歷核刪註記", notes = "新增病歷核刪註記")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "新增成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PostMapping("/nhixml/deductedNote")
  public ResponseEntity<BaseResponse> newDeductedNote(
      @ApiParam(value = "病歷id",
          example = "161633") @RequestParam(required = true) String mrId,
      @ApiParam(value = "核刪註記內容") @RequestBody(required = true) DEDUCTED_NOTE note) {
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      BaseResponse br = new BaseResponse();
      br.setResult("failed");
      br.setMessage("未登入，無法執行");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(br);
    }
    if (note.getCode() == null) {
      BaseResponse br = new BaseResponse();
      br.setResult("failed");
      br.setMessage("無核刪代碼，無法新增");
    }

    note.setEditor(user.getUsername());
    note.setActionType(ACTION_TYPE.ADD.value());
    return returnAPIResult(xmlService.newDeductedNote(mrId, note));
  }
  
  @ApiOperation(value = "新增多筆病歷核刪註記", notes = "新增多筆病歷核刪註記")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "新增成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PostMapping("/nhixml/deductedNotes")
  public ResponseEntity<BaseResponse> newDeductedNotes(
      @ApiParam(value = "病歷id",
          example = "161633") @RequestParam(required = true) String mrId,
      @ApiParam(value = "核刪註記內容") @RequestBody(required = true) List<DEDUCTED_NOTE> note) {
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      BaseResponse br = new BaseResponse();
      br.setResult("failed");
      br.setMessage("未登入，無法執行");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(br);
    }
    String result = null;
    for (DEDUCTED_NOTE deducted_NOTE : note) {
      if (deducted_NOTE.getCode() == null || deducted_NOTE.getCode().length() == 0) {
        continue;
      }
      deducted_NOTE.setEditor(user.getUsername());
      deducted_NOTE.setActionType(ACTION_TYPE.ADD.value());
      result = xmlService.newDeductedNote(mrId, deducted_NOTE);
    }
   
    return returnAPIResult(result);
  }
  
  @ApiOperation(value = "取得指定病歷的全部核刪註記(含已被刪除的註記)", notes = "取得指定病歷的全部核刪註記，用於檢視編輯記錄")
  @GetMapping("/nhixml/deductedNote")
  public ResponseEntity<DeductedNoteListResponse> getAllDeductedNote(
      @ApiParam(value = "病歷id", example = "161633") @RequestParam String mrId) {
    long idL = 0;
    try {
      idL = Long.parseLong(mrId);
    } catch (NumberFormatException e) {
      DeductedNoteListResponse result = new DeductedNoteListResponse();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("id格式不正確");
      return ResponseEntity.badRequest().body(result);
    }
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      DeductedNoteListResponse result = new DeductedNoteListResponse();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("無法取得登入狀態");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
    return ResponseEntity.ok(xmlService.getDeductedNoteResponse(idL, false));
  }
  
  @ApiOperation(value = "更新指定病歷的核刪註記內容", notes = "更新指定病歷的核刪註記內容")
  @PutMapping("/nhixml/deductedNote")
  public ResponseEntity<BaseResponse> updateDeductedNote(
      @ApiParam(value = "核刪註記內容") @RequestBody(required = true) DEDUCTED_NOTE note) {
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      BaseResponse br = new BaseResponse();
      br.setMessage("無法取得登入狀態");
      br.setResult(BaseResponse.ERROR);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(br);
    }
    note.setEditor(user.getUsername());
    return returnAPIResult(xmlService.updateDeductedNote(note, user.getDisplayName()));
  }
  
  @ApiOperation(value = "更新指定病歷的核刪註記內容", notes = "更新指定病歷的核刪註記內容")
  @PutMapping("/nhixml/deductedNotes")
  public ResponseEntity<BaseResponse> updateDeductedNotes(
      @ApiParam(value = "核刪註記內容") @RequestBody(required = true) List<DEDUCTED_NOTE> notes) {
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      BaseResponse br = new BaseResponse();
      br.setMessage("無法取得登入狀態");
      br.setResult(BaseResponse.ERROR);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(br);
    }
    String response = null;
    for (DEDUCTED_NOTE note : notes) {
      note.setEditor(user.getDisplayName());
      response = xmlService.updateDeductedNote(note, user.getDisplayName());
    }
    return returnAPIResult(response);
  }
  
  @ApiOperation(value = "刪除核刪註記內容", notes = "刪除核刪註記內容")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @DeleteMapping("/nhixml/deductedNote")
  public ResponseEntity<BaseResponse> deleteDeductedNote(
      @ApiParam(value = "核刪註記id",
      example = "8") @RequestParam(required = true) String noteId) {
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      BaseResponse br = new BaseResponse();
      br.setResult("failed");
      br.setMessage("未登入，無法執行");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(br);
    }
    long noteIdL = 0;
    try {
      noteIdL = Long.parseLong(noteId);
    } catch (NumberFormatException e) {
      return returnAPIResult("noteId 格式有誤");
    }
    
    return returnAPIResult(xmlService.deleteDeductedNote(user.getUsername(), noteIdL));
  }
  
  /**
   * 取得指定日期區間的病歷
   * 
   * @param name
   * @param model
   * @return
   */
  @ApiOperation(value = "取得指定病歷的DRG編審結果", notes = "取得指定病歷的DRG編審結果")
  @GetMapping("/nhixml/drglist/{id}")
  public ResponseEntity<DrgListPayload> getDrgList(
      @ApiParam(name = "id", value = "病歷id", example = "31986") @PathVariable String id) {
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
      DrgListPayload result = new DrgListPayload();
      result.setMessage("id格式不正確");
      return ResponseEntity.badRequest().body(result);
    }
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      DrgListPayload result = new DrgListPayload();
      result.setMessage("無法取得登入狀態");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
    return ResponseEntity.ok(xmlService.getDrgList(idL));
  }
  
  /**
   * 取得指定日期區間的病歷
   * 
   * @param name
   * @param model
   * @return
   */
  @ApiOperation(value = "更新指定病歷的DRG", notes = "更新指定病歷的DRG")
  @PostMapping("/nhixml/drglist/{id}")
  public ResponseEntity<BaseResponse> setDrgList(
      @ApiParam(name = "id", value = "病歷id", example = "31986") @PathVariable String id,
      @ApiParam(value = "選擇的主診斷碼", example = "S72.001.A") @RequestParam(required = true) String icd) {
    long idL = 0;
    try {
      idL = Long.parseLong(id);
    } catch (NumberFormatException e) {
     return returnAPIResult("id格式不正確");
    }
    UserDetailsImpl user = getUserDetails();
    if (user == null) {
      return returnAPIResult("無法取得登入狀態");
    }
    return returnAPIResult(xmlService.updateDrgList(idL, icd));
  }
  
  @CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
  @ApiOperation(value = "上傳excel核刪檔案", notes = "上傳excel核刪檔案")
  @PostMapping(value = "/nhixml/uploadExcel",consumes = {"multipart/form-data"})
  public ResponseEntity<Map<String,Object>> importDeductedNoteExcel(@ApiParam(name = "id", value = "病歷id", example = "31986") @RequestParam String id,
		  @ApiParam(name = "inputFile") @RequestParam("file") MultipartFile file
		  ) throws FileNotFoundException, IOException, ParseException {
	  Map<String,Object> result = new HashMap<String,Object>();
	  List<DEDUCTED_NOTE> dataList =  new ArrayList<DEDUCTED_NOTE>();
	  UserDetailsImpl user = getUserDetails();
	  
	  Workbook workbook = null;
	  workbook = WorkbookFactory.create(file.getInputStream());
//	  Sheet sheet = workbook.getSheet("測試資料");
	  ///取第一個sheet資料
	  Sheet sheet = workbook.getSheetAt(0);
	  dataList = xmlService.readDeductedNoteHSSFSheet(sheet, id, user.getUsername());

	  result.put("result", "success");
	  result.put("msg", "");
	  result.put("data", dataList);
	  return ResponseEntity.ok(result);
  }
  @CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
  @ApiOperation(value = "匯出csv檔", notes = "匯出csv檔")
  @ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
  @GetMapping("/nhixml/exportCSV")
  public ResponseEntity<BaseResponse> exportCSV(@ApiParam(name = "exportType", value = "匯出類型，fileExportTypeOne、fileExportTypeTwo", example = "fileExportTypeOne") @RequestParam(required = false) String exportType,
			@ApiParam(name = "dataFormat", value = "就醫類型，op=門急診、ip=住院，若為多筆資料，用空格隔開", example = "op") @RequestParam(required = false) String dataFormat,
			@ApiParam(name = "dateType", value = "日期區間類型	，applyDate、cureFinishRange、lpRange", example = "applyDate") @RequestParam(required = false) String dateType,
			@ApiParam(name = "year", value = "日期年，dateType=applyDate，必填", example = "2020") @RequestParam(required = false) String year,
			@ApiParam(name = "month", value = "日期月，dateType=applyDate，必填", example = "1") @RequestParam(required = false) String month,
			@ApiParam(name = "fnSdate", value = "治療結束起日，dateType=cureFinishRange，必填", example = "2020-01-01") @RequestParam(required = false) String fnSdate,
			@ApiParam(name = "fnEdate", value = "治療結束迄日，dateType=cureFinishRange，必填", example = "2020-01-31") @RequestParam(required = false) String fnEdate,
			@ApiParam(name = "outSdate", value = "出院起日，dateType=lpRange，必填", example = "2020-01-01") @RequestParam(required = false) String outSdate,
			@ApiParam(name = "outEdate", value = "出院迄日，dateType=lpRange，必填", example = "2020-01-31") @RequestParam(required = false) String outEdate,
			@ApiParam(name = "inhCode", value = "就醫紀錄編號，若為多筆資料，用空格隔開，exportType=fileExportTypeTwo，必填", example = "") @RequestParam(required = false) String inhCode,
			
			HttpServletResponse response
		  ) throws FileNotFoundException, IOException, ParseException {
	  BaseResponse result = new BaseResponse();
	  if(exportType.equals("fileExportTypeOne")) {
		  if(dataFormat == null || dataFormat.isEmpty()) {
			  result.setMessage("就醫類型不得為空");
			  result.setResult(BaseResponse.ERROR);
			  return ResponseEntity.badRequest().body(result);
		  }
		  switch(dateType) {
		  case "applyDate":
			  if(year == null || month == null) {
				  result.setMessage("年月不得為空");
				  result.setResult(BaseResponse.ERROR);
				  return ResponseEntity.badRequest().body(result);
			  }
			  break;
		  case "cureFinishRange":
			  if(fnSdate == null || fnEdate == null) {
				  result.setMessage("治療結束日不得為空");
				  result.setResult(BaseResponse.ERROR);
				  return ResponseEntity.badRequest().body(result);
			  }
			  break;
		  case "lpRange":
			  if(outSdate == null || outEdate == null) {
				  result.setMessage("出院日不得為空");
				  result.setResult(BaseResponse.ERROR);
				  return ResponseEntity.badRequest().body(result);
			  }
			  break;
		  }
	  }
	  else {
		  if(inhCode == null || inhCode.isEmpty()) {
			  result.setMessage("就醫紀錄編號不得為空");
			  result.setResult(BaseResponse.ERROR);
			  return ResponseEntity.badRequest().body(result);
		  }
	  }
	  xmlService.exportCSV(exportType, dataFormat, dateType, year, month, fnSdate, fnEdate, outSdate, outEdate, inhCode, response);
	  return ResponseEntity.ok(result);
  }
}
