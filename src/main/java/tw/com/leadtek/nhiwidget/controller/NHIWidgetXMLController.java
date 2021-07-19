/**
 * Created on 2021/1/25.
 */
package tw.com.leadtek.nhiwidget.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import tw.com.leadtek.nhiwidget.constant.XMLConstant;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.IP;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP;
import tw.com.leadtek.nhiwidget.model.xml.OutPatient;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.MRCount;
import tw.com.leadtek.nhiwidget.payload.MRCountResponse;
import tw.com.leadtek.nhiwidget.payload.MRDetail;
import tw.com.leadtek.nhiwidget.payload.QuickSearchResponse;
import tw.com.leadtek.nhiwidget.payload.SearchReq;
import tw.com.leadtek.nhiwidget.service.NHIWidgetXMLService;
import tw.com.leadtek.nhiwidget.service.ParametersService;

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

  @ApiOperation(value = "上傳申報檔XML檔案", notes = "上傳申報檔XML檔案")
  @ApiImplicitParams({@ApiImplicitParam(name = "file", paramType = "form", value = "申報檔XML檔案",
      dataType = "file", required = true)})
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @PostMapping(value = "/nhixml/uploadFile")
  public ResponseEntity<BaseResponse> upload(@RequestPart("file") MultipartFile file) {
    System.out.println("upload file:" + file.getName());
    ObjectMapper xmlMapper = new XmlMapper();
    try {
      String xml = new String(file.getBytes(), "BIG5");
      if (xml.indexOf("inpatient") > 0) {
        IP ip = xmlMapper.readValue(xml, IP.class);
        xmlService.saveIP(ip);
      }
      if (xml.indexOf("outpatient") > 0) {
        OP op = xmlMapper.readValue(xml, OP.class);
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
      System.out.println("dataFormat=" + dataFormat);
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
      value = "代碼表類型，FUNC_TYPE:就診科別，PAY_TYPE:給付類別，OP_CASE_TYPE:門診案件分類，IP_CASE_TYPE:住院案件分類",
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
  @ApiOperation(value = "取得指定日期區間的病歷", notes = "取得指定日期區間的病歷")
  @GetMapping("/nhixml/mr")
  public ResponseEntity<Map<String, Object>> getMR(
      @ApiParam(name = "allMatch", value = "單一分項內容如有逗號，須/不須完全符合，Y/N",
          example = "N") @RequestParam(required = false) String allMatch,
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
      @ApiParam(name = "prsnName", value = "醫護名",
          example = "王大明") @RequestParam(required = false) String prsnName,
      @ApiParam(name = "prsnId", value = "醫護代碼",
          example = "A123456789") @RequestParam(required = false) String prsnId,
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
      @ApiParam(name = "orderCode", value = "健保碼",
          example = "03001K") @RequestParam(required = false) String orderCode,
      @ApiParam(name = "drugUse", value = "健保碼用量",
          example = "03001K") @RequestParam(required = false) String drugUse,
      @ApiParam(name = "inhCode", value = "院內碼",
          example = "03001K") @RequestParam(required = false) String inhCode,
      @ApiParam(name = "inhCodeDrugUse", value = "院內碼用量",
          example = "03001K") @RequestParam(required = false) String inhCodeDrugUse,
      @ApiParam(name = "icdAll", value = "不分區ICD碼",
          example = "V20.0") @RequestParam(required = false) String icdAll,
      @ApiParam(name = "icdCMMajor", value = "主診斷ICD",
          example = "V20.0") @RequestParam(required = false) String icdCMMajor,
      @ApiParam(name = "icdCMSecondary", value = "次診斷ICD",
          example = "V20.0") @RequestParam(required = false) String icdCMSecondary,
      @ApiParam(name = "icdPCS", value = "處置ICD",
          example = "V20.0") @RequestParam(required = false) String icdPCS,
      @ApiParam(name = "qrObject",
          value = "品質獎勵計畫收案對象") @RequestParam(required = false) String qrObject,
      @ApiParam(name = "qrSdate",
          value = "品質獎勵計畫收案對象") @RequestParam(required = false) String qrSdate,
      @ApiParam(name = "qrEdate",
          value = "品質獎勵計畫收案對象") @RequestParam(required = false) String qrEdate,
      @ApiParam(name = "status",
          value = "病歷狀態：-3:疾病分類完成, -2:待確認，-1:疑問標示，0:待處理，1:無需變更，2:優化完成，3:評估不調整",
          example = "1") @RequestParam(required = false) String status,
      @ApiParam(name = "deductedCode",
          value = "核刪代碼") @RequestParam(required = false) String deductedCode,
      @ApiParam(name = "deductedOrder",
          value = "核刪醫令") @RequestParam(required = false) String deductedOrder,
      @ApiParam(name = "all", value = "全站搜尋/關鍵字",
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

    Map<String, Object> list =
        xmlService.getMR(allMatch, sdate, edate, minPoints, maxPoints, dataFormat, funcType, prsnId,
            prsnName, applId, applName, inhMrId, inhClinicId, drg, drgSection, orderCode, inhCode,
            drugUse, inhCodeDrugUse, icdAll, icdCMMajor, icdCMSecondary, icdPCS, qrObject, qrSdate,
            qrEdate, status, deductedCode, deductedOrder, all, iPerPage, iPage);
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
      @ApiParam(name = "id", value = "病歷id", example = "87890") @PathVariable String id) {
    logger.info("=========/nhixml/mr/{" + id + "} =============================");
    // List<MR> list = xmlService.getMR(sdate, edate, minPoints, maxPoints, dataFormat, funcType,
    // prsnName, prsnId, inhMrId, inhClinicId, drg, orderCode, icdCM, status, all);
    // if (list.size() == 0) {
    // return ResponseEntity.badRequest().body(new ArrayList<MR>());
    // }
    return ResponseEntity.ok(xmlService.getMRDetail(id));
  }

  @ApiOperation(value = "更新指定病歷", notes = "更新指定病歷")
  @PutMapping("/nhixml/mr/{id}")
  public ResponseEntity<MRDetail> updateMRDetail(
      @ApiParam(name = "id", value = "病歷id", example = "87890") @PathVariable String id,
      @ApiParam(name = "mrDetail",
          value = "病歷詳細資訊") @RequestBody(required = true) MRDetail mrDetail) {
    logger.info("=========/nhixml/mr/{" + id + "} =============================");
    mrDetail.setId(Long.parseLong(id));
    return ResponseEntity.ok(xmlService.updateMRDetail(mrDetail));
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

}
