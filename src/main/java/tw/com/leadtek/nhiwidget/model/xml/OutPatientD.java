/**
 * Created on 2020/12/24.
 */
package tw.com.leadtek.nhiwidget.model.xml;

import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

/**
 * 門診點數清單
 * 
 * @author Ken Lai
 */
@JsonPropertyOrder({"d3", "d4", "d5", "d6", "d7", "d8", "d9", "d10", "d11", "d12", "d13", "d14",
    "d15", "d16", "d17", "d18", "d19", "d20", "d21", "d22", "d23", "d24", "d25", "d26", "d27",
    "d28", "d29", "d30", "d31", "d32", "d33", "d34", "d35", "d36", "d37", "d38", "d39", "d40",
    "d41", "d42", "d43", "d44", "d45", "d46", "d47", "d48", "d49", "d50", "d51", "d52", "d53",
    "d54", "d55", "d56"})
@Table(name = "op_d")
@Entity
public class OutPatientD {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @JsonIgnore
  private Long ID;

  /**
   * OP_T table 的 ID 序號
   */
  @Column(name = "OPT_ID", nullable = false)
  private Long OPT_ID;

  /**
   * 案件分類, 存DB用. 轉成XML時不產出
   */
  // @JsonIgnore
  private String CASE_TYPE;

  /**
   * 流水編號, 存DB用. 轉成XML時不產出
   */
  // @JsonIgnore
  private Integer SEQ_NO;

  /**
   * 身分證統一編號
   */
  @Column(length = 32)
  private String ROC_ID;

  /**
   * 特定治療項目代號(一)
   */
  @Column(length = 2)
  private String CURE_ITEM_NO1;

  /**
   * 特定治療項目代號(二)
   */
  @Column(length = 2)
  private String CURE_ITEM_NO2;

  /**
   * 特定治療項目代號(三)
   */
  @Column(length = 2)
  private String CURE_ITEM_NO3;

  /**
   * 特定治療項目代號(四)
   */
  @Column(length = 2)
  private String CURE_ITEM_NO4;

  /**
   * 就醫科別
   */
  @Column(length = 2)
  private String FUNC_TYPE;

  /**
   * 就醫日期
   */
  @Column(length = 7)
  private String FUNC_DATE;

  /**
   * 治療結束日期
   */
  @Column(length = 7)
  private String FUNC_END_DATE;

  /**
   * 出生年月日
   */
  @Column(length = 7)
  private String ID_BIRTH_YMD;

  /**
   * 補報原因註記
   */
  @Column(length = 1)
  private String APPL_CAUSE_MARK;

  /**
   * 整合式照護計畫註記
   */
  @Column(length = 1)
  private String CARE_MARK;

  /**
   * 給付類別
   */
  @Column(length = 1)
  private String PAY_TYPE;

  /**
   * 部分負擔代號
   */
  @Column(length = 3)
  private String PART_NO;

  /**
   * 轉診、處方調劑或特定檢查資源共享案件註記
   */
  @Column(length = 2)
  private String SHARE_MARK;

  /**
   * 轉診、處方調劑或特定檢查資源共享案件之服務機構代號
   */
  @Column(length = 10)
  private String SHARE_HOSP_ID;

  /**
   * 病患是否轉出
   */
  @Column(length = 1)
  private String PAT_TRAN_OUT;

  /**
   * 主診斷代碼
   */
  @Column(length = 9)
  private String ICD_CM_1;

  /**
   * 次診斷代碼(一)
   */
  @Column(length = 9)
  private String ICD_CM_2;

  /**
   * 次診斷代碼(二)
   */
  @Column(length = 9)
  private String ICD_CM_3;

  /**
   * 次診斷代碼(三)
   */
  @Column(length = 9)
  private String ICD_CM_4;

  /**
   * 次診斷代碼(四)
   */
  @Column(length = 9)
  private String ICD_CM_5;

  /**
   * 主手術(處置)代碼
   */
  @Column(length = 9)
  private String ICD_OP_CODE1;

  /**
   * 次手術(處置)代碼(一)
   */
  @Column(length = 9)
  private String ICD_OP_CODE2;

  /**
   * 次手術(處置)代碼(二)
   */
  @Column(length = 9)
  private String ICD_OP_CODE3;

  /**
   * 給藥日份
   */
  @Column(length = 2)
  private Integer DRUG_DAY;

  /**
   * 處方調劑方式
   */
  @Column(length = 1)
  private String MED_TYPE;

  /**
   * 就醫序號
   */
  @Column(length = 4)
  private String CARD_SEQ_NO;

  /**
   * 診治醫事人員代號
   */
  @Column(length = 10)
  private String PRSN_ID;

  /**
   * 藥師代號
   */
  @Column(length = 10)
  private String PHAR_ID;

  /**
   * 用藥明細點數小計
   */
  private Integer DRUG_DOT;

  /**
   * 診療明細點數小計
   */
  private Integer TREAT_DOT;

  /**
   * 特殊材料明細點數小計
   */
  private Integer METR_DOT;

  /**
   * 診察費項目代號
   */
  @Column(length = 12)
  private String TREAT_CODE;

  /**
   * 診察費點數
   */
  private Integer DIAG_DOT;

  /**
   * 藥事服務費項目代號
   */
  @Column(length = 12)
  private String DSVC_NO;

  /**
   * 藥事服務費點數
   */
  private Integer DSVC_DOT;

  /**
   * 合計點數
   */
  private Integer T_DOT;

  /**
   * 部分負擔點數
   */
  private Integer PART_DOT;

  /**
   * 申請點數
   */
  private Integer T_APPL_DOT;

  /**
   * 論病例計酬代碼
   */
  @Column(length = 2)
  private String CASE_PAY_CODE;

  /**
   * 行政協助項目部分負擔點數
   */
  private Integer ASSIST_PART_DOT;

  /**
   * 慢性病連續處方箋有效期間總處方日份
   */
  private Integer CHR_DAYS;

  /**
   * 依附就醫新生兒出生日期
   */
  @Column(length = 7)
  private String NB_BIRTHDAY;

  /**
   * 山地離島地區醫療服務計畫代碼
   */
  @Column(length = 10)
  private String OUT_SVC_PLAN_CODE;

  /**
   * 姓名
   */
  @Column(length = 20)
  private String NAME;

  /**
   * 矯正機關代號
   */
  @Column(length = 10)
  private String AGENCY_ID;

  /**
   * 依附就醫新生兒胞胎註記
   */
  @Column(length = 1)
  private String CHILD_MARK;

  /**
   * 特定地區醫療服務
   */
  @Column(length = 2)
  private String SPE_AREA_SVC;

  /**
   * 支援區域
   */
  @Column(length = 4)
  private String SUPPORT_AREA;

  /**
   * 實際提供醫療服務之醫事服務機構代號
   */
  @Column(length = 10)
  private String HOSP_ID;

  /**
   * 轉往之醫事服務機構代號
   */
  @Column(length = 10)
  private String TRAN_IN_HOSP_ID;

  /**
   * 原處方就醫序號
   */
  @Column(length = 4)
  private String ORI_CARD_SEQ_NO;

  @JsonProperty("pdata")
  @JacksonXmlElementWrapper(useWrapping = false)
  @Transient
  private List<OutPatientP> pdataList;

  public String getROC_ID() {
    return ROC_ID;
  }

  @JsonProperty("d3")
  public void setROC_ID(String rOC_ID) {
    ROC_ID = rOC_ID;
  }

  public String getCURE_ITEM_NO1() {
    return CURE_ITEM_NO1;
  }

  @JsonProperty("d4")
  public void setCURE_ITEM_NO1(String cURE_ITEM_NO1) {
    CURE_ITEM_NO1 = cURE_ITEM_NO1;
  }

  public String getCURE_ITEM_NO2() {
    return CURE_ITEM_NO2;
  }

  @JsonProperty("d5")
  public void setCURE_ITEM_NO2(String cURE_ITEM_NO2) {
    CURE_ITEM_NO2 = cURE_ITEM_NO2;
  }

  public String getCURE_ITEM_NO3() {
    return CURE_ITEM_NO3;
  }

  @JsonProperty("d6")
  public void setCURE_ITEM_NO3(String cURE_ITEM_NO3) {
    CURE_ITEM_NO3 = cURE_ITEM_NO3;
  }

  public String getCURE_ITEM_NO4() {
    return CURE_ITEM_NO4;
  }

  @JsonProperty("d7")
  public void setCURE_ITEM_NO4(String cURE_ITEM_NO4) {
    CURE_ITEM_NO4 = cURE_ITEM_NO4;
  }

  public String getFUNC_TYPE() {
    return FUNC_TYPE;
  }

  @JsonProperty("d8")
  public void setFUNC_TYPE(String fUNC_TYPE) {
    FUNC_TYPE = fUNC_TYPE;
  }

  public String getFUNC_DATE() {
    return FUNC_DATE;
  }

  @JsonProperty("d9")
  public void setFUNC_DATE(String fUNC_DATE) {
    FUNC_DATE = fUNC_DATE;
  }

  public String getFUNC_END_DATE() {
    return FUNC_END_DATE;
  }

  @JsonProperty("d10")
  public void setFUNC_END_DATE(String fUNC_END_DATE) {
    FUNC_END_DATE = fUNC_END_DATE;
  }

  public String getID_BIRTH_YMD() {
    return ID_BIRTH_YMD;
  }

  @JsonProperty("d11")
  public void setID_BIRTH_YMD(String iD_BIRTH_YMD) {
    ID_BIRTH_YMD = iD_BIRTH_YMD;
  }

  public String getAPPL_CAUSE_MARK() {
    return APPL_CAUSE_MARK;
  }

  @JsonProperty("d12")
  public void setAPPL_CAUSE_MARK(String aPPL_CAUSE_MARK) {
    APPL_CAUSE_MARK = aPPL_CAUSE_MARK;
  }

  public String getCARE_MARK() {
    return CARE_MARK;
  }

  @JsonProperty("d13")
  public void setCARE_MARK(String cARE_MARK) {
    CARE_MARK = cARE_MARK;
  }

  public String getPAY_TYPE() {
    return PAY_TYPE;
  }

  @JsonProperty("d14")
  public void setPAY_TYPE(String pAY_TYPE) {
    PAY_TYPE = pAY_TYPE;
  }

  public String getPART_NO() {
    return PART_NO;
  }

  @JsonProperty("d15")
  public void setPART_NO(String pART_NO) {
    PART_NO = pART_NO;
  }

  public String getSHARE_MARK() {
    return SHARE_MARK;
  }

  @JsonProperty("d16")
  public void setSHARE_MARK(String sHARE_MARK) {
    SHARE_MARK = sHARE_MARK;
  }

  public String getSHARE_HOSP_ID() {
    return SHARE_HOSP_ID;
  }

  @JsonProperty("d17")
  public void setSHARE_HOSP_ID(String sHARE_HOSP_ID) {
    SHARE_HOSP_ID = sHARE_HOSP_ID;
  }

  public String getPAT_TRAN_OUT() {
    return PAT_TRAN_OUT;
  }

  @JsonProperty("d18")
  public void setPAT_TRAN_OUT(String pAT_TRAN_OUT) {
    PAT_TRAN_OUT = pAT_TRAN_OUT;
  }

  public String getICD_CM_1() {
    return ICD_CM_1;
  }

  @JsonProperty("d19")
  public void setICD_CM_1(String iCD_CM_1) {
    ICD_CM_1 = iCD_CM_1;
  }

  public String getICD_CM_2() {
    return ICD_CM_2;
  }

  @JsonProperty("d20")
  public void setICD_CM_2(String iCD_CM_2) {
    ICD_CM_2 = iCD_CM_2;
  }

  public String getICD_CM_3() {
    return ICD_CM_3;
  }

  @JsonProperty("d21")
  public void setICD_CM_3(String iCD_CM_3) {
    ICD_CM_3 = iCD_CM_3;
  }

  public String getICD_CM_4() {
    return ICD_CM_4;
  }

  @JsonProperty("d22")
  public void setICD_CM_4(String iCD_CM_4) {
    ICD_CM_4 = iCD_CM_4;
  }

  public String getICD_CM_5() {
    return ICD_CM_5;
  }

  @JsonProperty("d23")
  public void setICD_CM_5(String iCD_CM_5) {
    ICD_CM_5 = iCD_CM_5;
  }

  public String getICD_OP_CODE1() {
    return ICD_OP_CODE1;
  }

  @JsonProperty("d24")
  public void setICD_OP_CODE1(String iCD_OP_CODE1) {
    ICD_OP_CODE1 = iCD_OP_CODE1;
  }

  public String getICD_OP_CODE2() {
    return ICD_OP_CODE2;
  }

  @JsonProperty("d25")
  public void setICD_OP_CODE2(String iCD_OP_CODE2) {
    ICD_OP_CODE2 = iCD_OP_CODE2;
  }

  public String getICD_OP_CODE3() {
    return ICD_OP_CODE3;
  }

  @JsonProperty("d26")
  public void setICD_OP_CODE3(String iCD_OP_CODE3) {
    ICD_OP_CODE3 = iCD_OP_CODE3;
  }

  public Integer getDRUG_DAY() {
    return DRUG_DAY;
  }

  @JsonProperty("d27")
  public void setDRUG_DAY(Integer dRUG_DAY) {
    DRUG_DAY = dRUG_DAY;
  }

  public String getMED_TYPE() {
    return MED_TYPE;
  }

  @JsonProperty("d28")
  public void setMED_TYPE(String mED_TYPE) {
    MED_TYPE = mED_TYPE;
  }

  public String getCARD_SEQ_NO() {
    return CARD_SEQ_NO;
  }

  @JsonProperty("d29")
  public void setCARD_SEQ_NO(String cARD_SEQ_NO) {
    CARD_SEQ_NO = cARD_SEQ_NO;
  }

  public String getPRSN_ID() {
    return PRSN_ID;
  }

  @JsonProperty("d30")
  public void setPRSN_ID(String pRSN_ID) {
    PRSN_ID = pRSN_ID;
  }

  public String getPHAR_ID() {
    return PHAR_ID;
  }

  @JsonProperty("d31")
  public void setPHAR_ID(String pHAR_ID) {
    PHAR_ID = pHAR_ID;
  }

  public Integer getDRUG_DOT() {
    return DRUG_DOT;
  }

  @JsonProperty("d32")
  public void setDRUG_DOT(Integer dRUG_DOT) {
    DRUG_DOT = dRUG_DOT;
  }

  public Integer getTREAT_DOT() {
    return TREAT_DOT;
  }

  @JsonProperty("d33")
  public void setTREAT_DOT(Integer tREAT_DOT) {
    TREAT_DOT = tREAT_DOT;
  }

  public Integer getMETR_DOT() {
    return METR_DOT;
  }

  @JsonProperty("d34")
  public void setMETR_DOT(Integer mETR_DOT) {
    METR_DOT = mETR_DOT;
  }

  public String getTREAT_CODE() {
    return TREAT_CODE;
  }

  @JsonProperty("d35")
  public void setTREAT_CODE(String tREAT_CODE) {
    TREAT_CODE = tREAT_CODE;
  }

  public Integer getDIAG_DOT() {
    return DIAG_DOT;
  }

  @JsonProperty("d36")
  public void setDIAG_DOT(Integer dIAG_DOT) {
    DIAG_DOT = dIAG_DOT;
  }

  public String getDSVC_NO() {
    return DSVC_NO;
  }

  @JsonProperty("d37")
  public void setDSVC_NO(String dSVC_NO) {
    DSVC_NO = dSVC_NO;
  }

  public Integer getDSVC_DOT() {
    return DSVC_DOT;
  }

  @JsonProperty("d38")
  public void setDSVC_DOT(Integer dSVC_DOT) {
    DSVC_DOT = dSVC_DOT;
  }

  public Integer getT_DOT() {
    return T_DOT;
  }

  @JsonProperty("d39")
  public void setT_DOT(Integer t_DOT) {
    T_DOT = t_DOT;
  }

  public Integer getPART_DOT() {
    return PART_DOT;
  }

  @JsonProperty("d40")
  public void setPART_DOT(Integer pART_DOT) {
    PART_DOT = pART_DOT;
  }

  public Integer getT_APPL_DOT() {
    return T_APPL_DOT;
  }

  @JsonProperty("d41")
  public void setT_APPL_DOT(Integer t_APPL_DOT) {
    T_APPL_DOT = t_APPL_DOT;
  }

  public String getCASE_PAY_CODE() {
    return CASE_PAY_CODE;
  }

  @JsonProperty("d42")
  public void setCASE_PAY_CODE(String cASE_PAY_CODE) {
    CASE_PAY_CODE = cASE_PAY_CODE;
  }

  public Integer getASSIST_PART_DOT() {
    return ASSIST_PART_DOT;
  }

  @JsonProperty("d43")
  public void setASSIST_PART_DOT(Integer aSSIST_PART_DOT) {
    ASSIST_PART_DOT = aSSIST_PART_DOT;
  }

  public Integer getCHR_DAYS() {
    return CHR_DAYS;
  }

  @JsonProperty("d44")
  public void setCHR_DAYS(Integer cHR_DAYS) {
    CHR_DAYS = cHR_DAYS;
  }

  public String getNB_BIRTHDAY() {
    return NB_BIRTHDAY;
  }

  @JsonProperty("d45")
  public void setNB_BIRTHDAY(String nB_BIRTHDAY) {
    NB_BIRTHDAY = nB_BIRTHDAY;
  }

  public String getOUT_SVC_PLAN_CODE() {
    return OUT_SVC_PLAN_CODE;
  }

  @JsonProperty("d48")
  public void setOUT_SVC_PLAN_CODE(String oUT_SVC_PLAN_CODE) {
    OUT_SVC_PLAN_CODE = oUT_SVC_PLAN_CODE;
  }

  public String getNAME() {
    return NAME;
  }

  @JsonProperty("d49")
  public void setNAME(String nAME) {
    NAME = nAME;
  }

  public String getAGENCY_ID() {
    return AGENCY_ID;
  }

  @JsonProperty("d50")
  public void setAGENCY_ID(String aGENCY_ID) {
    AGENCY_ID = aGENCY_ID;
  }

  public String getCHILD_MARK() {
    return CHILD_MARK;
  }

  @JsonProperty("d51")
  public void setCHILD_MARK(String cHILD_MARK) {
    CHILD_MARK = cHILD_MARK;
  }

  public String getSPE_AREA_SVC() {
    return SPE_AREA_SVC;
  }

  @JsonProperty("d52")
  public void setSPE_AREA_SVC(String sPE_AREA_SVC) {
    SPE_AREA_SVC = sPE_AREA_SVC;
  }

  public String getSUPPORT_AREA() {
    return SUPPORT_AREA;
  }

  @JsonProperty("d53")
  public void setSUPPORT_AREA(String sUPPORT_AREA) {
    SUPPORT_AREA = sUPPORT_AREA;
  }

  public String getHOSP_ID() {
    return HOSP_ID;
  }

  @JsonProperty("d54")
  public void setHOSP_ID(String hOSP_ID) {
    HOSP_ID = hOSP_ID;
  }

  public String getTRAN_IN_HOSP_ID() {
    return TRAN_IN_HOSP_ID;
  }

  @JsonProperty("d55")
  public void setTRAN_IN_HOSP_ID(String tRAN_IN_HOSP_ID) {
    TRAN_IN_HOSP_ID = tRAN_IN_HOSP_ID;
  }

  public String getORI_CARD_SEQ_NO() {
    return ORI_CARD_SEQ_NO;
  }

  @JsonProperty("d56")
  public void setORI_CARD_SEQ_NO(String oRI_CARD_SEQ_NO) {
    ORI_CARD_SEQ_NO = oRI_CARD_SEQ_NO;
  }

  public List<OutPatientP> getPdataList() {
    return pdataList;
  }

  public void setPdataList(List<OutPatientP> pdataList) {
    this.pdataList = pdataList;
  }

  public Long getID() {
    return ID;
  }

  public void setID(Long iD) {
    ID = iD;
  }

  public String getCASE_TYPE() {
    return CASE_TYPE;
  }

  public void setCASE_TYPE(String cASE_TYPE) {
    CASE_TYPE = cASE_TYPE;
  }

  public Integer getSEQ_NO() {
    return SEQ_NO;
  }

  public void setSEQ_NO(Integer sEQ_NO) {
    SEQ_NO = sEQ_NO;
  }

  public Long getOPT_ID() {
    return OPT_ID;
  }

  public void setOPT_ID(Long oPT_ID) {
    OPT_ID = oPT_ID;
  }

}
