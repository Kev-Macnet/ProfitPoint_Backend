/**
 * Created on 2020/12/23.
 */
package tw.com.leadtek.nhiwidget.model.xml;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * 住院總表 
 * @author Ken Lai
 */
@JacksonXmlRootElement(localName = "tdata")
@JsonPropertyOrder({"t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9", "t10", "t11", "t12",
  "t13", "t14", "t15", "t16", "t17", "t18", "t19", "t20", "t21", "t22", "t23", "t24", "t25",
  "t26", "t27", "t28", "t29", "t30", "t31", "t32", "t33"})
@Table(name = "ip_t")
@Entity
public class InPatientT {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @JsonIgnore
  private Long ID;
  
  /**
   * 資料格式
   */
  @Column(length = 2)
  private String DATA_FORMAT;

  /**
   * 服務機構代號
   */
  @Column(length = 10)
  private String HOSP_ID;

  /**
   * 費用年月(民國年)
   */
  @Column(length = 5)
  private String FEE_YM;

  /**
   * 申報方式
   */
  @Column(length = 1)
  private String APPL_MODE;

  /**
   * 申報類別
   */
  @Column(length = 1)
  private String APPL_TYPE;

  /**
   * 申報日期
   */
  @Column(length = 7)
  private String APPL_DATE;
  
  /**
   * 一般案件申請件數
   */
  private Integer CASE_GEN_QTY;
  
  /**
   * 一般案件日數
   */
  private Integer CASE_GEN_DAYS;
  
  /**
   * 一般案件醫療費用點數
   */
  @Column(length = 10)
  private String CASE_GEN_DOT;
  
  /**
   * 論病例計酬案件申請件數
   */
  private Integer CASE_PAY_QTY;
  
  /**
   * 論病例計酬案件日數
   */
  private Integer CASE_PAY_DAYS;
  
  /**
   * 論病例計酬案件醫療費用點數
   */
  private Integer CASE_PAY_DOT;
  
  /**
   * 特定案件申請件數
   */
  private Integer CASE_SPEC_QTY;
  
  /**
   * 特定案件日數
   */
  @Column(length = 8)
  private String CASE_SPEC_DAYS;
  
  /**
   * 特定案件醫療費用點數
   */
  private Integer CASE_SPEC_DOT;
  
  /**
   * 醫療費用件數總計
   */
  private Integer EXP_QTY;
  
  /**
   * 醫療費用日數總計
   */
  private Integer EXP_DAYS;
  
  /**
   * 醫療費用點數總計
   */
  private Integer EXP_DOT;
  
  /**
   * 部分負擔件數總計
   */
  private Integer PART_QTY;
  
  /**
   * 部分負擔日數總計
   */
  private Integer PART_DAYS;
  
  /**
   * 部分負擔點數總計
   */
  private Integer PART_AMT;
  
  /**
   * 支付制度試辦計畫、行政協助案件、安寧療護件數總計
   */
  private Integer PLAN_QTY;
  
  /**
   * 支付制度試辦計畫、行政協助案件、安寧療護日數總計
   */
  private Integer PLAN_DAYS;
  
  /**
   * 支付制度試辦計畫、行政協助案件、安寧療護案件醫療費用點數總計
   */
  private Integer PLAN_DOT;
  
  /**
   * Tw-DRGs件數總計
   */
  private Integer DRG_QTY;
  
  /**
   * Tw-DRGs日數總計
   */
  private Integer DRG_DAYS;
  
  /**
   * Tw-DRGs醫療費用點數總計
   */
  private Integer DRG_DOTS;
  
  /**
   * 申請件數總計
   */
  private Integer APPL_QTY;
  
  /**
   * 申請日數總計
   */
  private Integer APPL_DAYS;
  
  /**
   * 申請點數總計
   */
  private Integer APPL_DOT;
  
  /**
   * 此次連線申報起日期(民國年)
   */
  @Column(length = 7)
  private String APPL_START_DATE;
  
  /**
   * 此次連線申報迄日期(民國年)
   */
  @Column(length = 7)
  private String APPL_END_DATE;
  
  /**
   * 不計入醫療費用點數合計欄位項目點數總計
   */
  private Integer NON_EXP_DOT;
  
  public String getDATA_FORMAT() {
    return DATA_FORMAT;
  }

  @JsonProperty("t1")
  public void setDATA_FORMAT(String dATA_FORMAT) {
    DATA_FORMAT = dATA_FORMAT;
  }

  public String getHOSP_ID() {
    return HOSP_ID;
  }

  @JsonProperty("t2")
  public void setHOSP_ID(String hOSP_ID) {
    HOSP_ID = hOSP_ID;
  }

  public String getFEE_YM() {
    return FEE_YM;
  }

  @JsonProperty("t3")
  public void setFEE_YM(String fEE_YM) {
    FEE_YM = fEE_YM;
  }

  public String getAPPL_MODE() {
    return APPL_MODE;
  }

  @JsonProperty("t4")
  public void setAPPL_MODE(String aPPL_MODE) {
    APPL_MODE = aPPL_MODE;
  }

  public String getAPPL_TYPE() {
    return APPL_TYPE;
  }

  @JsonProperty("t5")
  public void setAPPL_TYPE(String aPPL_TYPE) {
    APPL_TYPE = aPPL_TYPE;
  }

  public String getAPPL_DATE() {
    return APPL_DATE;
  }

  @JsonProperty("t6")
  public void setAPPL_DATE(String aPPL_DATE) {
    APPL_DATE = aPPL_DATE;
  }
  
  public Integer getCASE_GEN_QTY() {
    return CASE_GEN_QTY;
  }

  @JsonProperty("t7")
  public void setCASE_GEN_QTY(Integer cASE_GEN_QTY) {
    CASE_GEN_QTY = cASE_GEN_QTY;
  }

  public Integer getCASE_GEN_DAYS() {
    return CASE_GEN_DAYS;
  }

  @JsonProperty("t8")
  public void setCASE_GEN_DAYS(Integer cASE_GEN_DAYS) {
    CASE_GEN_DAYS = cASE_GEN_DAYS;
  }

  public String getCASE_GEN_DOT() {
    return CASE_GEN_DOT;
  }

  @JsonProperty("t9")
  public void setCASE_GEN_DOT(String cASE_GEN_DOT) {
    CASE_GEN_DOT = cASE_GEN_DOT;
  }

  public Integer getCASE_PAY_QTY() {
    return CASE_PAY_QTY;
  }

  @JsonProperty("t10")
  public void setCASE_PAY_QTY(Integer cASE_PAY_QTY) {
    CASE_PAY_QTY = cASE_PAY_QTY;
  }

  public Integer getCASE_PAY_DAYS() {
    return CASE_PAY_DAYS;
  }

  @JsonProperty("t11")
  public void setCASE_PAY_DAYS(Integer cASE_PAY_DAYS) {
    CASE_PAY_DAYS = cASE_PAY_DAYS;
  }

  public Integer getCASE_PAY_DOT() {
    return CASE_PAY_DOT;
  }

  @JsonProperty("t12")
  public void setCASE_PAY_DOT(Integer cASE_PAY_DOT) {
    CASE_PAY_DOT = cASE_PAY_DOT;
  }

  public Integer getCASE_SPEC_QTY() {
    return CASE_SPEC_QTY;
  }

  @JsonProperty("t13")
  public void setCASE_SPEC_QTY(Integer cASE_SPEC_QTY) {
    CASE_SPEC_QTY = cASE_SPEC_QTY;
  }

  public String getCASE_SPEC_DAYS() {
    return CASE_SPEC_DAYS;
  }

  @JsonProperty("t14")
  public void setCASE_SPEC_DAYS(String cASE_SPEC_DAYS) {
    CASE_SPEC_DAYS = cASE_SPEC_DAYS;
  }

  public Integer getCASE_SPEC_DOT() {
    return CASE_SPEC_DOT;
  }

  @JsonProperty("t15")
  public void setCASE_SPEC_DOT(Integer cASE_SPEC_DOT) {
    CASE_SPEC_DOT = cASE_SPEC_DOT;
  }

  public Integer getEXP_QTY() {
    return EXP_QTY;
  }

  @JsonProperty("t16")
  public void setEXP_QTY(Integer eXP_QTY) {
    EXP_QTY = eXP_QTY;
  }

  public Integer getEXP_DAYS() {
    return EXP_DAYS;
  }

  @JsonProperty("t17")
  public void setEXP_DAYS(Integer eXP_DAYS) {
    EXP_DAYS = eXP_DAYS;
  }

  public Integer getEXP_DOT() {
    return EXP_DOT;
  }

  @JsonProperty("t18")
  public void setEXP_DOT(Integer eXP_DOT) {
    EXP_DOT = eXP_DOT;
  }

  public Integer getPART_QTY() {
    return PART_QTY;
  }

  @JsonProperty("t19")
  public void setPART_QTY(Integer pART_QTY) {
    PART_QTY = pART_QTY;
  }

  public Integer getPART_DAYS() {
    return PART_DAYS;
  }

  @JsonProperty("t20")
  public void setPART_DAYS(Integer pART_DAYS) {
    PART_DAYS = pART_DAYS;
  }

  public Integer getPART_AMT() {
    return PART_AMT;
  }

  @JsonProperty("t21")
  public void setPART_AMT(Integer pART_AMT) {
    PART_AMT = pART_AMT;
  }

  public Integer getPLAN_QTY() {
    return PLAN_QTY;
  }

  @JsonProperty("t22")
  public void setPLAN_QTY(Integer pLAN_QTY) {
    PLAN_QTY = pLAN_QTY;
  }

  public Integer getPLAN_DAYS() {
    return PLAN_DAYS;
  }

  @JsonProperty("t23")
  public void setPLAN_DAYS(Integer pLAN_DAYS) {
    PLAN_DAYS = pLAN_DAYS;
  }

  public Integer getPLAN_DOT() {
    return PLAN_DOT;
  }

  @JsonProperty("t24")
  public void setPLAN_DOT(Integer pLAN_DOT) {
    PLAN_DOT = pLAN_DOT;
  }

  public Integer getDRG_QTY() {
    return DRG_QTY;
  }

  @JsonProperty("t25")
  public void setDRG_QTY(Integer dRG_QTY) {
    DRG_QTY = dRG_QTY;
  }

  public Integer getDRG_DAYS() {
    return DRG_DAYS;
  }

  @JsonProperty("t26")
  public void setDRG_DAYS(Integer dRG_DAYS) {
    DRG_DAYS = dRG_DAYS;
  }

  public Integer getDRG_DOTS() {
    return DRG_DOTS;
  }

  @JsonProperty("t27")
  public void setDRG_DOTS(Integer dRG_DOTS) {
    DRG_DOTS = dRG_DOTS;
  }

  public Integer getAPPL_QTY() {
    return APPL_QTY;
  }

  @JsonProperty("t28")
  public void setAPPL_QTY(Integer aPPL_QTY) {
    APPL_QTY = aPPL_QTY;
  }

  public Integer getAPPL_DAYS() {
    return APPL_DAYS;
  }

  @JsonProperty("t29")
  public void setAPPL_DAYS(Integer aPPL_DAYS) {
    APPL_DAYS = aPPL_DAYS;
  }

  public Integer getAPPL_DOT() {
    return APPL_DOT;
  }

  @JsonProperty("t30")
  public void setAPPL_DOT(Integer aPPL_DOT) {
    APPL_DOT = aPPL_DOT;
  }

  public String getAPPL_START_DATE() {
    return APPL_START_DATE;
  }

  @JsonProperty("t31")
  public void setAPPL_START_DATE(String aPPL_START_DATE) {
    APPL_START_DATE = aPPL_START_DATE;
  }

  public String getAPPL_END_DATE() {
    return APPL_END_DATE;
  }

  @JsonProperty("t32")
  public void setAPPL_END_DATE(String aPPL_END_DATE) {
    APPL_END_DATE = aPPL_END_DATE;
  }

  public Integer getNON_EXP_DOT() {
    return NON_EXP_DOT;
  }

  @JsonProperty("t33")
  public void setNON_EXP_DOT(Integer nON_EXP_DOT) {
    NON_EXP_DOT = nON_EXP_DOT;
  }

  public Long getID() {
    return ID;
  }

  public void setID(Long iD) {
    ID = iD;
  }

}
