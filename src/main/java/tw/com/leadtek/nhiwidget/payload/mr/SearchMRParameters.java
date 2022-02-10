/**
 * Created on 2021/12/30.
 */
package tw.com.leadtek.nhiwidget.payload.mr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import tw.com.leadtek.tools.DateTool;

public class SearchMRParameters extends HomepageParameters {

  /**
   * 單一分項內容如有空格，須/不須完全符合，Y/N
   */
  private String allMatch;
    
  /**
   * 不包含其他條件
   */
  private Boolean notOthers;
  
  /**
   * 最小申報點數
   */
  private Integer minPoints;
  
  /**
   * 最大申報點數
   */
  private Integer maxPoints;
  
  /**
   * 科別名稱
   */
  private String funcTypec;
  
  /**
   * 醫護代碼
   */
  private String prsnId;
  
  /**
   * 醫護姓名
   */
  private String prsnName;
  
  /**
   * 藥師名
   */
  private String pharName;
  
  /**
   * 藥師代碼
   */
  private String pharId;
  
  /**
   * 病患姓名
   */
  private String patientName;
  
  /**
   * 病患身分證字號
   */
  private String patientId;
  
  /**
   * 標記人員代碼
   */
  private String applId;
  
  /**
   * 標記人員名稱
   */
  private String applName;
  
  /**
   * 病歷號碼
   */
  private String inhMrId;
  
  /**
   * 就醫記錄編號
   */
  private String inhClinicId;
  
  /**
   * 不包含DRG條件
   */
  private Boolean onlyDRG;
  
  /**
   * 不包含DRG條件
   */
  private Boolean notDRG;
  
  /**
   * DRGs代碼
   */
  private String drg;
  
  /**
   * DRG落點區間
   */
  private String drgSection;
  
  /**
   * 不包含醫令條件
   */
  private Boolean notOrderCode;
  
  /**
   * 支付標準代碼
   */
  private String orderCode;
  
  /**
   * 用量
   */
  private String drugUse;
  
  /**
   * 院內碼
   */
  private String inhCode;
  
  /**
   * 院內碼用量
   */
  private String inhCodeDrugUse;
  
  /**
   * 不包含ICD條件
   */
  private Boolean notICD;
  
  /**
   * 不分區ICD碼
   */
  private String icdAll;
  
  /**
   * 主診斷碼ICD
   */
  private String icdCMMajor;
  
  /**
   * 次診斷ICD
   */
  private String icdCMSec;
  
  /**
   * 處置ICD
   */
  private String icdPCS;
  
  /**
   * 品質獎勵計畫收案對象
   */
  private String qrObject;
  
  /**
   * 品質獎勵計畫收案啟始日
   */
  private String qrSdate;
  
  /**
   * 品質獎勵計畫收案結束日
   */
  private String qrEdate;
  
  /**
   * 不包含資料狀態
   */
  private Boolean notStatus;
  
  /**
   * 病歷狀態
   */
  private String status;
  
  /**
   * 不包含被核刪條件
   */
  private Boolean notDeducted;
  
  /**
   * 核刪代碼
   */
  private String deductedCode;
  
  /**
   * 核刪醫令
   */
  private String deductedOrder;
  
  /**
   * 不包含申報狀態
   */
  private Boolean notApplStatus;
  
  /**
   * 本月申報
   */
  private Boolean applThisMonth;
  
  /**
   * 下月申報
   */
  private Boolean applNextMonth;
  
  /**
   * 不申報
   */
  private Boolean NoAppl;
  
  /**
   * 自費項目
   */
  private Boolean ownExpItem;
  
  /**
   * 全站/其他搜尋/關鍵字
   */
  private String all;
  
  /**
   * 排序欄位名稱
   */
  private String orderBy;
  
  /**
   * 排序方式
   */
  private Boolean asc;
  
  /**
   * 每頁顯示筆數
   */
  private Integer perPage;
  
  /**
   * 第幾頁，第一頁值為0
   */
  private Integer page;
  
  /**
   * 住院日期，格式 yyyy/MM/dd
   */
  private String indate;

  /**
   * 出院日期，格式 yyyy/MM/dd
   */
  private String outdate;
  
  public SearchMRParameters() {
    
  }

  public String getAllMatch() {
    return allMatch;
  }

  public void setAllMatch(String allMatch) {
    this.allMatch = allMatch;
  }

  public Integer getMinPoints() {
    return minPoints;
  }

  public void setMinPoints(Integer minPoints) {
    this.minPoints = minPoints;
  }

  public Integer getMaxPoints() {
    return maxPoints;
  }

  public void setMaxPoints(Integer maxPoints) {
    this.maxPoints = maxPoints;
  }

  public String getFuncTypec() {
    return funcTypec;
  }

  public void setFuncTypec(String funcTypec) {
    this.funcTypec = funcTypec;
  }

  public String getPrsnId() {
    return prsnId;
  }

  public void setPrsnId(String prsnId) {
    this.prsnId = prsnId;
  }

  public String getPharName() {
    return pharName;
  }

  public void setPharName(String pharName) {
    this.pharName = pharName;
  }

  public String getPharId() {
    return pharId;
  }

  public void setPharId(String pharId) {
    this.pharId = pharId;
  }

  public String getPatientName() {
    return patientName;
  }

  public void setPatientName(String patientName) {
    this.patientName = patientName;
  }

  public String getPatientId() {
    return patientId;
  }

  public void setPatientId(String patientId) {
    this.patientId = patientId;
  }

  public String getApplId() {
    return applId;
  }

  public void setApplId(String applId) {
    this.applId = applId;
  }

  public String getApplName() {
    return applName;
  }

  public void setApplName(String applName) {
    this.applName = applName;
  }

  public String getInhMrId() {
    return inhMrId;
  }

  public void setInhMrId(String inhMrId) {
    this.inhMrId = inhMrId;
  }

  public String getInhClinicId() {
    return inhClinicId;
  }

  public void setInhClinicId(String inhClinicId) {
    this.inhClinicId = inhClinicId;
  }

  public String getDrg() {
    return drg;
  }

  public void setDrg(String drg) {
    this.drg = drg;
  }

  public String getDrgSection() {
    return drgSection;
  }

  public void setDrgSection(String drgSection) {
    this.drgSection = drgSection;
  }

  public String getOrderCode() {
    return orderCode;
  }

  public void setOrderCode(String orderCode) {
    this.orderCode = orderCode;
  }

  public String getDrugUse() {
    return drugUse;
  }

  public void setDrugUse(String drugUse) {
    this.drugUse = drugUse;
  }

  public String getInhCode() {
    return inhCode;
  }

  public void setInhCode(String inhCode) {
    this.inhCode = inhCode;
  }

  public String getInhCodeDrugUse() {
    return inhCodeDrugUse;
  }

  public void setInhCodeDrugUse(String inhCodeDrugUse) {
    this.inhCodeDrugUse = inhCodeDrugUse;
  }

  public String getIcdAll() {
    return icdAll;
  }

  public void setIcdAll(String icdAll) {
    this.icdAll = icdAll;
  }

  public String getIcdCMMajor() {
    return icdCMMajor;
  }

  public void setIcdCMMajor(String icdCMMajor) {
    this.icdCMMajor = icdCMMajor;
  }

  public String getIcdCMSec() {
    return icdCMSec;
  }

  public void setIcdCMSec(String icdCMSec) {
    this.icdCMSec = icdCMSec;
  }

  public String getIcdPCS() {
    return icdPCS;
  }

  public void setIcdPCS(String icdPCS) {
    this.icdPCS = icdPCS;
  }

  public String getQrObject() {
    return qrObject;
  }

  public void setQrObject(String qrObject) {
    this.qrObject = qrObject;
  }

  public String getQrSdate() {
    return qrSdate;
  }

  public void setQrSdate(String qrSdate) {
    this.qrSdate = qrSdate;
  }

  public String getQrEdate() {
    return qrEdate;
  }

  public void setQrEdate(String qrEdate) {
    this.qrEdate = qrEdate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getDeductedCode() {
    return deductedCode;
  }

  public void setDeductedCode(String deductedCode) {
    this.deductedCode = deductedCode;
  }

  public String getDeductedOrder() {
    return deductedOrder;
  }

  public void setDeductedOrder(String deductedOrder) {
    this.deductedOrder = deductedOrder;
  }

  public Boolean getNotApplStatus() {
    return notApplStatus;
  }

  public void setNotApplStatus(Boolean notApplStatus) {
    this.notApplStatus = notApplStatus;
  }

  public Boolean getApplThisMonth() {
    return applThisMonth;
  }

  public void setApplThisMonth(Boolean applThisMonth) {
    this.applThisMonth = applThisMonth;
  }

  public Boolean getApplNextMonth() {
    return applNextMonth;
  }

  public void setApplNextMonth(Boolean applNextMonth) {
    this.applNextMonth = applNextMonth;
  }

  public Boolean getNoAppl() {
    return NoAppl;
  }

  public void setNoAppl(Boolean noAppl) {
    NoAppl = noAppl;
  }

  public Boolean getOwnExpItem() {
    return ownExpItem;
  }

  public void setOwnExpItem(Boolean ownExpItem) {
    this.ownExpItem = ownExpItem;
  }

  public String getAll() {
    return all;
  }

  public void setAll(String all) {
    this.all = all;
  }

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  public Boolean getAsc() {
    return asc;
  }

  public void setAsc(Boolean asc) {
    this.asc = asc;
  }

  public Integer getPerPage() {
    return perPage;
  }

  public void setPerPage(Integer perPage) {
    this.perPage = perPage;
  }

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public Boolean getNotOthers() {
    return notOthers;
  }

  public void setNotOthers(Boolean notOthers) {
    this.notOthers = notOthers;
  }

  public Boolean getNotOrderCode() {
    return notOrderCode;
  }

  public void setNotOrderCode(Boolean notOrderCode) {
    this.notOrderCode = notOrderCode;
  }
  
  public Boolean getNotDRG() {
    return notDRG;
  }

  public void setNotDRG(Boolean notDRG) {
    this.notDRG = notDRG;
  }

  public Boolean getNotICD() {
    return notICD;
  }

  public void setNotICD(Boolean notICD) {
    this.notICD = notICD;
  }

  public Boolean getNotStatus() {
    return notStatus;
  }

  public void setNotStatus(Boolean notStatus) {
    this.notStatus = notStatus;
  }

  public Boolean getNotDeducted() {
    return notDeducted;
  }

  public void setNotDeducted(Boolean notDeducted) {
    this.notDeducted = notDeducted;
  }

  public String getIndate() {
    return indate;
  }

  public void setIndate(String indate) {
    this.indate = indate;
  }

  public String getOutdate() {
    return outdate;
  }

  public void setOutdate(String outdate) {
    this.outdate = outdate;
  }
  
  public String getPrsnName() {
    return prsnName;
  }

  public void setPrsnName(String prsnName) {
    this.prsnName = prsnName;
  }
  
  public Boolean getOnlyDRG() {
    return onlyDRG;
  }

  public void setOnlyDRG(Boolean onlyDRG) {
    this.onlyDRG = onlyDRG;
  }

  public void setBasic(String applY, String applM, String sdate, String edate, String indate, String outdate,
      String inhMrId, String inhClinicId, String dataFormat) {
     
    initialApplYM(applY, applM);

    SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
    this.edate = edate;
    this.sdate = (sdate == null) ? "2010/01/01" : sdate;
    try {
      sDate = new java.sql.Date(sdf.parse(this.sdate).getTime());
      if (edate == null) {
        this.edate  = sdf.format(new Date());
      }
      eDate = new java.sql.Date(sdf.parse(this.edate).getTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    this.indate = indate;
    this.outdate = outdate;
    this.inhMrId = inhMrId;
    this.inhClinicId = inhClinicId;
    if (dataFormat == null || "00".equals(dataFormat) || dataFormat.indexOf(',') > 0 || dataFormat.indexOf(' ') > 0) {
      this.dataFormat = null; 
    } else {
      this.dataFormat = dataFormat;
    }
  }
  
  public void setOthers(Boolean notOthers, Integer minPoints, Integer maxPoints, String funcType,
      String funcTypec, String prsnId, String prsnName, String pharName, String pharId, 
      String patientName, String patientId) {
    this.notOthers = notOthers;
    this.minPoints = minPoints;
    this.maxPoints = maxPoints;
    if ("00".equals(funcType) || "0".equals(funcType)) {
      funcType = null;
    } else {
      this.funcType = funcType;
    }
    
    this.funcTypec = funcTypec;
    this.prsnId = prsnId;
    this.prsnName = prsnName;
    this.pharId = pharId;
    this.pharName = pharName;
    this.patientName = patientName;
    this.patientId = patientId;
  }
  
  public void setICD(Boolean notICD, String icdAll, String icdCMMajor, String icdCMSec, String icdPCS) {
    this.notICD = notICD;
    if (icdAll != null) {
      this.icdAll = "%," + icdAll;
    }
    if (icdCMMajor != null) {
      this.icdCMMajor = icdCMMajor;
    }
    if (icdCMSec != null) {
      this.icdCMSec = "%," + icdCMSec;  
    }
    if (icdPCS != null) {
      this.icdPCS = "%," + icdPCS;
    }
  }
  
  public void setOrder(Boolean notOrderCode, String orderCode, String drugUse, String inhCode,
      String inhCodeDrugUse) {
    this.notOrderCode = notOrderCode;
    if (orderCode != null) {
      this.orderCode = "%," + orderCode;
    }
    this.drugUse = drugUse;
    this.inhCode = inhCode;
    this.inhCodeDrugUse = inhCodeDrugUse;
  }
  
  public void setApplStatus(Boolean notApplStatus, Boolean applThisMonth, Boolean applNextMonth,
      Boolean NoAppl, Boolean ownExpItem) {
    this.notApplStatus = notApplStatus;
    this.applThisMonth = applThisMonth;
    this.applNextMonth = applNextMonth;
    this.NoAppl = NoAppl;
    this.ownExpItem = ownExpItem;
  }
  
  public void setDRG(Boolean notDRG, String drg, String drgSection, Boolean onlyDRG) {
    this.notDRG = notDRG;
    this.drg = drg;
    this.drgSection = drgSection;
    this.onlyDRG = onlyDRG;
  }
  
  public void setDeducted(Boolean notDeducted, String deductedCode, String deductedOrder) {
    this.notDeducted = notDeducted;
    this.deductedCode = deductedCode;
    this.deductedOrder = deductedOrder;
  }
  
  public void setStatus(Boolean notStatus, String status) {
    this.notStatus = notStatus;
    this.status = status;
  }
  
  public void setAll(String all, String orderBy, Boolean asc, Integer perPage, Integer page) {
    this.all = all;
    if ("sdate".equals(orderBy)) {
      this.orderBy = "mrDate";
    } else if ("edate".equals(orderBy)) {
      this.orderBy = "mrEndDate";
    } else if ("patientName".equals(orderBy)) {
      this.orderBy = "name";
    } else if ("funcTypec".equals(orderBy)) {
      this.orderBy = "funcType";
    } else {
      this.orderBy = orderBy;
    }
    this.asc = asc;
    this.perPage = (perPage == null || perPage < 1) ? 20 : perPage;
    this.page = page;
  }
  
}
