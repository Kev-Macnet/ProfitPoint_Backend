/**
 * Created on 2021/01/28.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.util.Date;
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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * 住院總表
 * 
 * @author Ken Lai
 */
@JacksonXmlRootElement(localName = "tdata")
@JsonPropertyOrder({"t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9", "t10", "t11", "t12",
    "t13", "t14", "t15", "t16", "t17", "t18", "t19", "t20", "t21", "t22", "t23", "t24", "t25",
    "t26", "t27", "t28", "t29", "t30", "t31", "t32", "t33"})
@Table(name = "IP_T")
@Entity
public class IP_T {

  /**
   * 序號
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @JsonIgnore
  private Long id;

  /**
   * 資料格式
   */
  @Column(name = "DATA_FORMAT")
  @JsonProperty("DATA_FORMAT")
  @JacksonXmlProperty(localName = "t1")
  private String dataFormat;

  /**
   * 服務機構代號
   */
  @Column(name = "HOSP_ID")
  @JsonProperty("HOSP_ID")
  @JacksonXmlProperty(localName = "t2")
  private String hospId;

  /**
   * 費用年月(民國年)
   */
  @Column(name = "FEE_YM")
  @JsonProperty("FEE_YM")
  @JacksonXmlProperty(localName = "t3")
  private String feeYm;

  /**
   * 申報方式
   */
  @Column(name = "APPL_MODE")
  @JsonProperty("APPL_MODE")
  @JacksonXmlProperty(localName = "t4")
  private String applMode;

  /**
   * 申報類別
   */
  @Column(name = "APPL_TYPE")
  @JsonProperty("APPL_TYPE")
  @JacksonXmlProperty(localName = "t5")
  private String applType;

  /**
   * 申報日期
   */
  @Column(name = "APPL_DATE")
  @JsonProperty("APPL_DATE")
  @JacksonXmlProperty(localName = "t6")
  private String applDate;

  /**
   * 一般案件申請件數
   */
  @Column(name = "CASE_GEN_QTY")
  @JsonProperty("CASE_GEN_QTY")
  @JacksonXmlProperty(localName = "t7")
  private Integer caseGenQty;

  /**
   * 一般案件日數
   */
  @Column(name = "CASE_GEN_DAYS")
  @JsonProperty("CASE_GEN_DAYS")
  @JacksonXmlProperty(localName = "t8")
  private Integer caseGenDays;

  /**
   * 一般案件醫療費用點數
   */
  @Column(name = "CASE_GEN_DOT")
  @JsonProperty("CASE_GEN_DOT")
  @JacksonXmlProperty(localName = "t9")
  private String caseGenDot;

  /**
   * 論病例計酬案件申請件數
   */
  @Column(name = "CASE_PAY_QTY")
  @JsonProperty("CASE_PAY_QTY")
  @JacksonXmlProperty(localName = "t10")
  private Integer casePayQty;

  /**
   * 論病例計酬案件日數
   */
  @Column(name = "CASE_PAY_DAYS")
  @JsonProperty("CASE_PAY_DAYS")
  @JacksonXmlProperty(localName = "t11")
  private Integer casePayDays;

  /**
   * 論病例計酬案件醫療費用點數
   */
  @Column(name = "CASE_PAY_DOT")
  @JsonProperty("CASE_PAY_DOT")
  @JacksonXmlProperty(localName = "t12")
  private Integer casePayDot;

  /**
   * 特定案件申請件數
   */
  @Column(name = "CASE_SPEC_QTY")
  @JsonProperty("CASE_SPEC_QTY")
  @JacksonXmlProperty(localName = "t13")
  private Integer caseSpecQty;

  /**
   * 特定案件日數
   */
  @Column(name = "CASE_SPEC_DAYS")
  @JsonProperty("CASE_SPEC_DAYS")
  @JacksonXmlProperty(localName = "t14")
  private String caseSpecDays;

  /**
   * 特定案件醫療費用點數
   */
  @Column(name = "CASE_SPEC_DOT")
  @JsonProperty("CASE_SPEC_DOT")
  @JacksonXmlProperty(localName = "t15")
  private Integer caseSpecDot;

  /**
   * 醫療費用件數總計
   */
  @Column(name = "EXP_QTY")
  @JsonProperty("EXP_QTY")
  @JacksonXmlProperty(localName = "t16")
  private Integer expQty;

  /**
   * 醫療費用日數總計
   */
  @Column(name = "EXP_DAYS")
  @JsonProperty("EXP_DAYS")
  @JacksonXmlProperty(localName = "t17")
  private Integer expDays;

  /**
   * 醫療費用點數總計
   */
  @Column(name = "EXP_DOT")
  @JsonProperty("EXP_DOT")
  @JacksonXmlProperty(localName = "t18")
  private Integer expDot;

  /**
   * 部分負擔件數總計
   */
  @Column(name = "PART_QTY")
  @JsonProperty("PART_QTY")
  @JacksonXmlProperty(localName = "t19")
  private Integer partQty;

  /**
   * 部分負擔日數總計
   */
  @Column(name = "PART_DAYS")
  @JsonProperty("PART_DAYS")
  @JacksonXmlProperty(localName = "t20")
  private Integer partDays;

  /**
   * 部分負擔點數總計
   */
  @Column(name = "PART_AMT")
  @JsonProperty("PART_AMT")
  @JacksonXmlProperty(localName = "t21")
  private Integer partAmt;

  /**
   * 支付制度試辦計畫、行政協助案件、安寧療護件數總計
   */
  @Column(name = "PLAN_QTY")
  @JsonProperty("PLAN_QTY")
  @JacksonXmlProperty(localName = "t22")
  private Integer planQty;

  /**
   * 支付制度試辦計畫、行政協助案件、安寧療護日數總計
   */
  @Column(name = "PLAN_DAYS")
  @JsonProperty("PLAN_DAYS")
  @JacksonXmlProperty(localName = "t23")
  private Integer planDays;

  /**
   * 支付制度試辦計畫、行政協助案件、安寧療護案件醫療費用點數總計
   */
  @Column(name = "PLAN_DOT")
  @JsonProperty("PLAN_DOT")
  @JacksonXmlProperty(localName = "t24")
  private Integer planDot;

  /**
   * TW-DRGS件數總計
   */
  @Column(name = "DRG_QTY")
  @JsonProperty("DRG_QTY")
  @JacksonXmlProperty(localName = "t25")
  private Integer drgQty;

  /**
   * TW-DRGS日數總計
   */
  @Column(name = "DRG_DAYS")
  @JsonProperty("DRG_DAYS")
  @JacksonXmlProperty(localName = "t26")
  private Integer drgDays;

  /**
   * TW-DRGS醫療費用點數總計
   */
  @Column(name = "DRG_DOTS")
  @JsonProperty("DRG_DOTS")
  @JacksonXmlProperty(localName = "t27")
  private Integer drgDots;

  /**
   * 申請件數總計
   */
  @Column(name = "APPL_QTY")
  @JsonProperty("APPL_QTY")
  @JacksonXmlProperty(localName = "t28")
  private Integer applQty;

  /**
   * 申請日數總計
   */
  @Column(name = "APPL_DAYS")
  @JsonProperty("APPL_DAYS")
  @JacksonXmlProperty(localName = "t29")
  private Integer applDays;

  /**
   * 申請點數總計
   */
  @Column(name = "APPL_DOT")
  @JsonProperty("APPL_DOT")
  @JacksonXmlProperty(localName = "t30")
  private Integer applDot;

  /**
   * 此次連線申報起日期(民國年)
   */
  @Column(name = "APPL_START_DATE")
  @JsonProperty("APPL_START_DATE")
  @JacksonXmlProperty(localName = "t31")
  private String applStartDate;

  /**
   * 此次連線申報迄日期(民國年)
   */
  @Column(name = "APPL_END_DATE")
  @JsonProperty("APPL_END_DATE")
  @JacksonXmlProperty(localName = "t32")
  private String applEndDate;

  /**
   * 不計入醫療費用點數合計欄位項目點數總計
   */
  @Column(name = "NON_EXP_DOT")
  @JsonProperty("NON_EXP_DOT")
  @JacksonXmlProperty(localName = "t33")
  private Integer nonExpDot;

  /**
   * 更新時間
   */
  @Column(name = "UPDATE_AT", nullable = false)
  @JsonIgnore
  private Date updateAt;

  /**
   * 序號
   */
  public Long getId() {
    return id;
  }

  /**
   * 序號
   */
  public void setId(Long ID) {
    id = ID;
  }

  /**
   * <t1> 資料格式
   */
  public String getDataFormat() {
    return dataFormat;
  }

  /**
   * <t1> 資料格式
   */
  public void setDataFormat(String DATA_FORMAT) {
    dataFormat = DATA_FORMAT;
  }

  /**
   * <t2> 服務機構代號
   */
  public String getHospId() {
    return hospId;
  }

  /**
   * <t2> 服務機構代號
   */
  public void setHospId(String HOSP_ID) {
    hospId = HOSP_ID;
  }

  /**
   * <t3> 費用年月(民國年)
   */
  public String getFeeYm() {
    return feeYm;
  }

  /**
   * <t3> 費用年月(民國年)
   */
  public void setFeeYm(String FEE_YM) {
    feeYm = FEE_YM;
  }

  /**
   * <t4> 申報方式
   */
  public String getApplMode() {
    return applMode;
  }

  /**
   * <t4> 申報方式
   */
  public void setApplMode(String APPL_MODE) {
    applMode = APPL_MODE;
  }

  /**
   * <t5> 申報類別
   */
  public String getApplType() {
    return applType;
  }

  /**
   * <t5> 申報類別
   */
  public void setApplType(String APPL_TYPE) {
    applType = APPL_TYPE;
  }

  /**
   * <t6> 申報日期
   */
  public String getApplDate() {
    return applDate;
  }

  /**
   * <t6> 申報日期
   */
  public void setApplDate(String APPL_DATE) {
    applDate = APPL_DATE;
  }

  /**
   * <t7> 一般案件申請件數
   */
  public Integer getCaseGenQty() {
    return caseGenQty;
  }

  /**
   * <t7> 一般案件申請件數
   */
  public void setCaseGenQty(Integer CASE_GEN_QTY) {
    caseGenQty = CASE_GEN_QTY;
  }

  /**
   * <t8> 一般案件日數
   */
  public Integer getCaseGenDays() {
    return caseGenDays;
  }

  /**
   * <t8> 一般案件日數
   */
  public void setCaseGenDays(Integer CASE_GEN_DAYS) {
    caseGenDays = CASE_GEN_DAYS;
  }

  /**
   * <t9> 一般案件醫療費用點數
   */
  public String getCaseGenDot() {
    return caseGenDot;
  }

  /**
   * <t9> 一般案件醫療費用點數
   */
  public void setCaseGenDot(String CASE_GEN_DOT) {
    caseGenDot = CASE_GEN_DOT;
  }

  /**
   * <t10> 論病例計酬案件申請件數
   */
  public Integer getCasePayQty() {
    return casePayQty;
  }

  /**
   * <t10> 論病例計酬案件申請件數
   */
  public void setCasePayQty(Integer CASE_PAY_QTY) {
    casePayQty = CASE_PAY_QTY;
  }

  /**
   * <t11> 論病例計酬案件日數
   */
  public Integer getCasePayDays() {
    return casePayDays;
  }

  /**
   * <t11> 論病例計酬案件日數
   */
  public void setCasePayDays(Integer CASE_PAY_DAYS) {
    casePayDays = CASE_PAY_DAYS;
  }

  /**
   * <t12> 論病例計酬案件醫療費用點數
   */
  public Integer getCasePayDot() {
    return casePayDot;
  }

  /**
   * <t12> 論病例計酬案件醫療費用點數
   */
  public void setCasePayDot(Integer CASE_PAY_DOT) {
    casePayDot = CASE_PAY_DOT;
  }

  /**
   * <t13> 特定案件申請件數
   */
  public Integer getCaseSpecQty() {
    return caseSpecQty;
  }

  /**
   * <t13> 特定案件申請件數
   */
  public void setCaseSpecQty(Integer CASE_SPEC_QTY) {
    caseSpecQty = CASE_SPEC_QTY;
  }

  /**
   * <t14> 特定案件日數
   */
  public String getCaseSpecDays() {
    return caseSpecDays;
  }

  /**
   * <t14> 特定案件日數
   */
  public void setCaseSpecDays(String CASE_SPEC_DAYS) {
    caseSpecDays = CASE_SPEC_DAYS;
  }

  /**
   * <t15> 特定案件醫療費用點數
   */
  public Integer getCaseSpecDot() {
    return caseSpecDot;
  }

  /**
   * <t15> 特定案件醫療費用點數
   */
  public void setCaseSpecDot(Integer CASE_SPEC_DOT) {
    caseSpecDot = CASE_SPEC_DOT;
  }

  /**
   * <t16> 醫療費用件數總計
   */
  public Integer getExpQty() {
    return expQty;
  }

  /**
   * <t16> 醫療費用件數總計
   */
  public void setExpQty(Integer EXP_QTY) {
    expQty = EXP_QTY;
  }

  /**
   * <t17> 醫療費用日數總計
   */
  public Integer getExpDays() {
    return expDays;
  }

  /**
   * <t17> 醫療費用日數總計
   */
  public void setExpDays(Integer EXP_DAYS) {
    expDays = EXP_DAYS;
  }

  /**
   * <t18> 醫療費用點數總計
   */
  public Integer getExpDot() {
    return expDot;
  }

  /**
   * <t18> 醫療費用點數總計
   */
  public void setExpDot(Integer EXP_DOT) {
    expDot = EXP_DOT;
  }

  /**
   * <t19> 部分負擔件數總計
   */
  public Integer getPartQty() {
    return partQty;
  }

  /**
   * <t19> 部分負擔件數總計
   */
  public void setPartQty(Integer PART_QTY) {
    partQty = PART_QTY;
  }

  /**
   * <t20> 部分負擔日數總計
   */
  public Integer getPartDays() {
    return partDays;
  }

  /**
   * <t20> 部分負擔日數總計
   */
  public void setPartDays(Integer PART_DAYS) {
    partDays = PART_DAYS;
  }

  /**
   * <t21> 部分負擔點數總計
   */
  public Integer getPartAmt() {
    return partAmt;
  }

  /**
   * <t21> 部分負擔點數總計
   */
  public void setPartAmt(Integer PART_AMT) {
    partAmt = PART_AMT;
  }

  /**
   * <t22> 支付制度試辦計畫、行政協助案件、安寧療護件數總計
   */
  public Integer getPlanQty() {
    return planQty;
  }

  /**
   * <t22> 支付制度試辦計畫、行政協助案件、安寧療護件數總計
   */
  public void setPlanQty(Integer PLAN_QTY) {
    planQty = PLAN_QTY;
  }

  /**
   * <t23> 支付制度試辦計畫、行政協助案件、安寧療護日數總計
   */
  public Integer getPlanDays() {
    return planDays;
  }

  /**
   * <t23> 支付制度試辦計畫、行政協助案件、安寧療護日數總計
   */
  public void setPlanDays(Integer PLAN_DAYS) {
    planDays = PLAN_DAYS;
  }

  /**
   * <t24> 支付制度試辦計畫、行政協助案件、安寧療護案件醫療費用點數總計
   */
  public Integer getPlanDot() {
    return planDot;
  }

  /**
   * <t24> 支付制度試辦計畫、行政協助案件、安寧療護案件醫療費用點數總計
   */
  public void setPlanDot(Integer PLAN_DOT) {
    planDot = PLAN_DOT;
  }

  /**
   * <t25> TW-DRGS件數總計
   */
  public Integer getDrgQty() {
    return drgQty;
  }

  /**
   * <t25> TW-DRGS件數總計
   */
  public void setDrgQty(Integer DRG_QTY) {
    drgQty = DRG_QTY;
  }

  /**
   * <t26> TW-DRGS日數總計
   */
  public Integer getDrgDays() {
    return drgDays;
  }

  /**
   * <t26> TW-DRGS日數總計
   */
  public void setDrgDays(Integer DRG_DAYS) {
    drgDays = DRG_DAYS;
  }

  /**
   * <t27> TW-DRGS醫療費用點數總計
   */
  public Integer getDrgDots() {
    return drgDots;
  }

  /**
   * <t27> TW-DRGS醫療費用點數總計
   */
  public void setDrgDots(Integer DRG_DOTS) {
    drgDots = DRG_DOTS;
  }

  /**
   * <t28> 申請件數總計
   */
  public Integer getApplQty() {
    return applQty;
  }

  /**
   * <t28> 申請件數總計
   */
  public void setApplQty(Integer APPL_QTY) {
    applQty = APPL_QTY;
  }

  /**
   * <t29> 申請日數總計
   */
  public Integer getApplDays() {
    return applDays;
  }

  /**
   * <t29> 申請日數總計
   */
  public void setApplDays(Integer APPL_DAYS) {
    applDays = APPL_DAYS;
  }

  /**
   * <t30> 申請點數總計
   */
  public Integer getApplDot() {
    return applDot;
  }

  /**
   * <t30> 申請點數總計
   */
  public void setApplDot(Integer APPL_DOT) {
    applDot = APPL_DOT;
  }

  /**
   * <t31> 此次連線申報起日期(民國年)
   */
  public String getApplStartDate() {
    return applStartDate;
  }

  /**
   * <t31> 此次連線申報起日期(民國年)
   */
  public void setApplStartDate(String APPL_START_DATE) {
    applStartDate = APPL_START_DATE;
  }

  /**
   * <t32> 此次連線申報迄日期(民國年)
   */
  public String getApplEndDate() {
    return applEndDate;
  }

  /**
   * <t32> 此次連線申報迄日期(民國年)
   */
  public void setApplEndDate(String APPL_END_DATE) {
    applEndDate = APPL_END_DATE;
  }

  /**
   * <t33> 不計入醫療費用點數合計欄位項目點數總計
   */
  public Integer getNonExpDot() {
    return nonExpDot;
  }

  /**
   * <t33> 不計入醫療費用點數合計欄位項目點數總計
   */
  public void setNonExpDot(Integer NON_EXP_DOT) {
    nonExpDot = NON_EXP_DOT;
  }

  public Date getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(Date updateAt) {
    this.updateAt = updateAt;
  }

}