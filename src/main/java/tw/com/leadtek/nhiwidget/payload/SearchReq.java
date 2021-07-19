/**
 * Created on 2021/2/3.
 */
package tw.com.leadtek.nhiwidget.payload;

import org.springframework.web.util.HtmlUtils;

public class SearchReq {

  /**
   * 申報年月
   */
  private String ym;

  /**
   * 科別
   */
  private String funcType;

  /**
   * 患者姓名
   */
  private String name;

  /**
   * 案件分類
   */
  private String caseType;

  /**
   * 醫令
   */
  private String order;

  /**
   * 截止日期
   */
  private String funcDate;

  /**
   * 疾病碼
   */
  private String icdCM;

  /**
   * 申報金額
   */
  private Integer applyPoints;

  /**
   * 病患身分證字號
   */
  private String rocID;

  /**
   * 藥費
   */
  private Integer drugFee;

  /**
   * 看診醫師證號
   */
  private String prsnID;

  /**
   * 主要治療
   */
  private String icdOP;

  /**
   * 病歷處理狀態
   */
  private String status;

  /**
   * 全域搜尋
   */
  private String all;

  public SearchReq() {

  }

  public SearchReq(String ym, String funcType, String name, String caseType, String order,
      String funcDate, String icdCM, Integer applyPoints, String rocID, Integer drugFee,
      String prsnID, String icdOP, String status, String all) {
    if (all != null && all.length() > 0) {
      String p =  checkParamStr(all);
      this.all = p;
      this.ym = p;
      this.funcType = p;
      this.name = p;
      this.caseType = p;
      this.prsnID = p;
      this.icdCM = p;
      this.icdOP = p;
      this.order = p;
      this.status = p;
    } else {
      this.ym = checkParamStr(ym);
      this.funcType = checkParamStr(funcType);
      this.name = checkParamStr(name);
      this.caseType = checkParamStr(caseType);
      this.order = checkParamStr(order);
      this.funcDate = checkParamStr(funcDate);
      this.icdCM = checkParamStr(icdCM);
      this.applyPoints = checkParamInt(applyPoints);
      this.rocID = checkParamStr(rocID);
      this.drugFee = checkParamInt(drugFee);
      this.prsnID = checkParamStr(prsnID);
      this.icdOP = checkParamStr(icdOP);
      this.status = checkParamStr(status);
    }
  }

  private static String checkParamStr(Object s) {
    if (s == null) {
      return null;
    }
    return HtmlUtils.htmlEscape((String) s, "UTF-8").toUpperCase();
  }

  private static Integer checkParamInt(Integer s) {
    if (s == null) {
      return null;
    }
    return s;
  }

  public String getYm() {
    return ym;
  }

  public void setYm(String ym) {
    this.ym = ym;
  }

  public String getFuncType() {
    return funcType;
  }

  public void setFuncType(String funcType) {
    this.funcType = funcType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCaseType() {
    return caseType;
  }

  public void setCaseType(String caseType) {
    this.caseType = caseType;
  }

  public String getOrder() {
    return order;
  }

  public void setOrder(String order) {
    this.order = order;
  }

  public String getFuncDate() {
    return funcDate;
  }

  public void setFuncDate(String funcDate) {
    this.funcDate = funcDate;
  }

  public String getIcdCM() {
    return icdCM;
  }

  public void setIcdCM(String icdCM) {
    this.icdCM = icdCM;
  }

  public Integer getApplyPoints() {
    return applyPoints;
  }

  public void setApplyPoints(Integer applyPoints) {
    this.applyPoints = applyPoints;
  }

  public String getRocID() {
    return rocID;
  }

  public void setRocID(String rocID) {
    this.rocID = rocID;
  }

  public Integer getDrugFee() {
    return drugFee;
  }

  public void setDrugFee(Integer drugFee) {
    this.drugFee = drugFee;
  }

  public String getPrsnID() {
    return prsnID;
  }

  public void setPrsnID(String prsnID) {
    this.prsnID = prsnID;
  }

  public String getIcdOP() {
    return icdOP;
  }

  public void setIcdOP(String icdOP) {
    this.icdOP = icdOP;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getAll() {
    return all;
  }

  public void setAll(String all) {
    this.all = all;
  }

}
