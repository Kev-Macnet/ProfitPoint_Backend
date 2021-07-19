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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

/**
 * 住院點數清單
 * 
 * @author Ken Lai
 */
@JsonPropertyOrder({"d3", "d4", "d5", "d6", "d7", "d8", "d9", "d10", "d11", "d12", "d13", "d14",
  "d15", "d16", "d17", "d18", "d19", "d20", "d21", "d22", "d23", "d24", "d25", "d26", "d27",
  "d28", "d29", "d30", "d31", "d32", "d33", "d34", "d35", "d36", "d37", "d38", "d39", "d40",
  "d41", "d42", "d43", "d44", "d45", "d46", "d47", "d48", "d49", "d50", "d51", "d52", "d53",
  "d54", "d55", "d56", "d57", "d58", "d59", "d60", "d61", "d62", "d63", "d64", "d65", "d66", 
  "d67", "d68", "d69", "d70", "d71", "d72", "d73", "d74", "d75", "d76", "d77", "d78", "d79", 
  "d80", "d81", "d82", "d83", "d84", "d85", "d86", "d87", "d88", "d89", "d90", "d91", "d92", 
  "d93", "d94", "d95", "d96", "d97", "d98", "d99", "d100", "d101", "d102", "d103", "d104", "d105",
  "d106", "d107", "d108", "d109", "d110", "d111", "d112"})
@Table(name = "ip_d")
@Entity
public class InPatientD {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  private Long ID;
  
  @Column(name = "IPT_ID", nullable = false)
  private Long IPT_ID;
  
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
  @Column(length = 10)
  private String ROC_ID;

  /**
   * 部分負擔代號
   */
  @Column(length = 3)
  private String PART_CODE;
  
  /**
   * 補報原因註記
   */
  @Column(length = 1)
  private String APPL_CAUSE_MARK;
  
  /**
   * 出生年月日(民國年)
   */
  @Column(length = 7)
  private String ID_BIRTH_YMD;

  /**
   * 給付類別
   */
  @Column(length = 1)
  private String PAY_TYPE;

  /**
   * 就醫科別
   */
  @Column(length = 2)
  private String FUNC_TYPE;

  /**
   * 入院年月日
   */
  @Column(length = 7)
  private String IN_DATE;

  /**
   * 出院年月日
   */
  @Column(length = 7)
  private String OUT_DATE;

  /**
   * 申報期間-起
   */
  @Column(length = 7)
  private String APPL_START_DATE;

  /**
   * 申報期間-迄
   */
  @Column(length = 7)
  private String APPL_END_DATE;

  /**
   * 急性病床天數
   */
  @Column(length = 3)
  private String E_BED_DAY;

  /**
   * 慢性病床天數
   */
  @Column(length = 3)
  private String S_BED_DAY;

  /**
   * 病患來源
   */
  @Column(length = 1)
  private String PATIENT_SOURCE;

  /**
   * 就醫序號
   */
  @Column(length = 4)
  private String CARD_SEQ_NO;

  /**
   * Tw-DRG碼
   */
  @Column(length = 5)
  private String TW_DRG_CODE;

  /**
   * Tw-DRG支付型態
   */
  @Column(length = 1)
  private String TW_DRG_PAY_TYPE;

  /**
   * 主治醫師代碼
   */
  @Column(length = 10)
  private String PRSN_ID;

  /**
   * DRGs碼
   */
  @Column(length = 5)
  private String CASE_DRG_CODE;

  /**
   * 轉歸代碼
   */
  @Column(length = 1)
  private String TRAN_CODE;

  /**
   * 主診斷
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
   * 次診斷代碼(五)
   */
  @Column(length = 9)
  private String ICD_CM_6;

  /**
   * 次診斷代碼(六)
   */
  @Column(length = 9)
  private String ICD_CM_7;

  /**
   * 次診斷代碼(七)
   */
  @Column(length = 9)
  private String ICD_CM_8;

  /**
   * 次診斷代碼(八)
   */
  @Column(length = 9)
  private String ICD_CM_9;

  /**
   * 次診斷代碼(九)
   */
  @Column(length = 9)
  private String ICD_CM_10;

  /**
   * 次診斷代碼(十)
   */
  @Column(length = 9)
  private String ICD_CM_11;

  /**
   * 次診斷代碼(十一)
   */
  @Column(length = 9)
  private String ICD_CM_12;

  /**
   * 次診斷代碼(十二)
   */
  @Column(length = 9)
  private String ICD_CM_13;

  /**
   * 次診斷代碼(十三)
   */
  @Column(length = 9)
  private String ICD_CM_14;

  /**
   * 次診斷代碼(十四)
   */
  @Column(length = 9)
  private String ICD_CM_15;

  /**
   * 次診斷代碼(十五)
   */
  @Column(length = 9)
  private String ICD_CM_16;

  /**
   * 次診斷代碼(十六)
   */
  @Column(length = 9)
  private String ICD_CM_17;

  /**
   * 次診斷代碼(十七)
   */
  @Column(length = 9)
  private String ICD_CM_18;

  /**
   * 次診斷代碼(十八)
   */
  @Column(length = 9)
  private String ICD_CM_19;

  /**
   * 次診斷代碼(十九)
   */
  @Column(length = 9)
  private String ICD_CM_20;

  /**
   * 主手術(處置)代碼
   */
  @Column(length = 9)
  private String ICD_OP_CODE1;

  /**
   * 次手術(處置)代碼一
   */
  @Column(length = 9)
  private String ICD_OP_CODE2;

  /**
   * 次手術(處置)代碼二
   */
  @Column(length = 9)
  private String ICD_OP_CODE3;

  /**
   * 次手術(處置)代碼三
   */
  @Column(length = 9)
  private String ICD_OP_CODE4;

  /**
   * 次手術(處置)代碼四
   */
  @Column(length = 9)
  private String ICD_OP_CODE5;

  /**
   * 次手術(處置)代碼五
   */
  @Column(length = 9)
  private String ICD_OP_CODE6;

  /**
   * 次手術(處置)代碼六
   */
  @Column(length = 9)
  private String ICD_OP_CODE7;

  /**
   * 次手術(處置)代碼七
   */
  @Column(length = 9)
  private String ICD_OP_CODE8;

  /**
   * 次手術(處置)代碼八
   */
  @Column(length = 9)
  private String ICD_OP_CODE9;

  /**
   * 次手術(處置)代碼九
   */
  @Column(length = 9)
  private String ICD_OP_CODE10;

  /**
   * 次手術(處置)代碼十
   */
  @Column(length = 9)
  private String ICD_OP_CODE11;

  /**
   * 次手術(處置)代碼十一
   */
  @Column(length = 9)
  private String ICD_OP_CODE12;

  /**
   * 次手術(處置)代碼十二
   */
  @Column(length = 9)
  private String ICD_OP_CODE13;

  /**
   * 次手術(處置)代碼十三
   */
  @Column(length = 9)
  private String ICD_OP_CODE14;

  /**
   * 次手術(處置)代碼十四
   */
  @Column(length = 9)
  private String ICD_OP_CODE15;

  /**
   * 次手術(處置)代碼十五
   */
  @Column(length = 9)
  private String ICD_OP_CODE16;

  /**
   * 次手術(處置)代碼十六
   */
  @Column(length = 9)
  private String ICD_OP_CODE17;

  /**
   * 次手術(處置)代碼十七
   */
  @Column(length = 9)
  private String ICD_OP_CODE18;

  /**
   * 次手術(處置)代碼十八
   */
  @Column(length = 9)
  private String ICD_OP_CODE19;

  /**
   * 次手術(處置)代碼十九
   */
  @Column(length = 9)
  private String ICD_OP_CODE20;

  /**
   * 醫令總數
   */
  private Integer ORDER_QTY;

  /**
   * 診察費點數
   */
  private Integer DIAG_DOT ;

  /**
   * 病房費點數
   */
  private Integer ROOM_DOT;

  /**
   * 管灌膳食費點數
   */
  private Integer MEAL_DOT;

  /**
   * 檢查費點數
   */
  private Integer AMIN_DOT;

  /**
   * 放射線診療費點數
   */
  private Integer RADO_DOT ;

  /**
   * 治療處置費點數
   */
  private Integer THRP_DOT;

  /**
   * 手術費點數
   */
  private Integer SGRY_DOT;

  /**
   * 復健治療費點數
   */
  private Integer PHSC_DOT;

  /**
   * 血液血漿費點數
   */
  private Integer BLOD_DOT;

  /**
   * 血液透析費點數
   */
  private Integer HD_DOT;

  /**
   * 麻醉費點數
   */
  private Integer ANE_DOT;

  /**
   * 特殊材料費點數
   */
  private Integer METR_DOT;

  /**
   * 藥費點數
   */
  private Integer DRUG_DOT;

  /**
   * 藥事服務費點數
   */
  private Integer DSVC_DOT;

  /**
   * 精神科治療費點數
   */
  private Integer NRTP_DOT;

  /**
   * 注射技術費點數
   */
  private Integer INJT_DOT;

  /**
   * 嬰兒費點數
   */
  private Integer BABY_DOT;

  /**
   * 醫療費用點數合計
   */
  private Integer MED_DOT;

  /**
   * 部分負擔點數 
   */
  private Integer PART_DOT;

  /**
   * 申請費用點數
   */
  private Integer APPL_DOT;
  
  /**
   * 醫療費用點數(急性病床1-30日)
   */
  private Integer EB_APPL30_DOT;

  /**
   * 部分負擔點數(急性病床1-30日)
   */
  private Integer EB_PART30_DOT;

  /**
   * 醫療費用點數(急性病床31-60日)
   */
  private Integer EB_APPL60_DOT;

  /**
   * 部分負擔點數(急性病床31-60日)
   */
  private Integer EB_PART60_DOT;

  /**
   * 醫療費用點數(急性病床61日以上)
   */
  private Integer EB_APPL61_DOT;

  /**
   * 部分負擔點數(急性病床61日以上)
   */
  private Integer EB_PART61_DOT;

  /**
   * 醫療費用點數(慢性病床1-30日)
   */
  private Integer SB_APPL30_DOT;

  /**
   * 部分負擔點數(慢性病床1-30日)
   */
  private Integer SB_PART30_DOT;

  /**
   * 醫療費用點數(慢性病床31-90日)
   */
  private Integer SB_APPL90_DOT;

  /**
   * 部分負擔點數(慢性病床31-90日)
   */
  private Integer SB_PART90_DOT;

  /**
   * 醫療費用點數(慢性病床91-180日)
   */
  private Integer SB_APPL180_DOT;

  /**
   * 部分負擔點數(慢性病床91-180日)
   */
  private Integer SB_PART180_DOT;

  /**
   * 醫療費用點數(慢性病床181日以上)
   */
  private Integer SB_APPL181_DOT;

  /**
   * 部分負擔點數(慢性病床 181日以上)
   */
  private Integer SB_PART181_DOT;

  /**
   * 依附就醫新生兒出生年月日
   */
  @Column(length = 7)
  private String NB_BIRTHDAY;

  /**
   * 依附就醫新生兒胞胎註記
   */
  @Column(length = 1)
  private String CHILD_MARK;

  /**
   * 不適用Tw-DRGs案件特殊註記
   */
  @Column(length = 1)
  private String TW_DRGS_SUIT_MARK;

  /**
   * 姓名
   */
  @Column(length = 20)
  private String NAME ;

  /**
   * 矯正機關代號
   */
  @Column(length = 10)
  private String AGENCY_ID;

  /**
   * 轉入服務機構代號
   */
  @Column(length = 10)
  private String TRAN_IN_HOSP_ID;

  /**
   * 轉往之醫事服務機構代號
   */
  @Column(length = 10)
  private String TRAN_OUT_HOSP_ID;

  /**
   * 實際提供醫療服務之醫事服務機構代號
   */
  @Column(length = 10)
  private String HOSP_ID;

  /**
   * 醫療服務計畫
   */
  @Column(length = 1)
  private String SVC_PLAN;

  /**
   * 試辦計畫
   */
  @Column(length = 1)
  private String PILOT_PROJECT;

  /**
   * 不計入醫療費用點數合計欄位項目點數
   */
  private Integer NON_APPL_DOT;

  @JsonProperty("pdata")
  @JacksonXmlElementWrapper(useWrapping=false)
  @Transient // 當做 table 時跳過此欄位
  private List<InPatientP> pdataList;
  
  public String getROC_ID() {
    return ROC_ID;
  }

  @JsonProperty("d3")
  public void setROC_ID(String rOC_ID) {
    ROC_ID = rOC_ID;
  }

  public String getPART_CODE() {
    return PART_CODE;
  }

  @JsonProperty("d4")
  public void setPART_CODE(String pART_CODE) {
    PART_CODE = pART_CODE;
  }

  public String getAPPL_CAUSE_MARK() {
    return APPL_CAUSE_MARK;
  }

  @JsonProperty("d5")
  public void setAPPL_CAUSE_MARK(String aPPL_CAUSE_MARK) {
    APPL_CAUSE_MARK = aPPL_CAUSE_MARK;
  }

  public String getID_BIRTH_YMD() {
    return ID_BIRTH_YMD;
  }

  @JsonProperty("d6")
  public void setID_BIRTH_YMD(String iD_BIRTH_YMD) {
    ID_BIRTH_YMD = iD_BIRTH_YMD;
  }

  public String getPAY_TYPE() {
    return PAY_TYPE;
  }

  @JsonProperty("d7")
  public void setPAY_TYPE(String pAY_TYPE) {
    PAY_TYPE = pAY_TYPE;
  }

  public String getFUNC_TYPE() {
    return FUNC_TYPE;
  }

  @JsonProperty("d9")
  public void setFUNC_TYPE(String fUNC_TYPE) {
    FUNC_TYPE = fUNC_TYPE;
  }

  public String getIN_DATE() {
    return IN_DATE;
  }

  @JsonProperty("d10")
  public void setIN_DATE(String iN_DATE) {
    IN_DATE = iN_DATE;
  }

  public String getOUT_DATE() {
    return OUT_DATE;
  }

  @JsonProperty("d11")
  public void setOUT_DATE(String oUT_DATE) {
    OUT_DATE = oUT_DATE;
  }

  public String getAPPL_START_DATE() {
    return APPL_START_DATE;
  }

  @JsonProperty("d12")
  public void setAPPL_START_DATE(String aPPL_START_DATE) {
    APPL_START_DATE = aPPL_START_DATE;
  }

  public String getAPPL_END_DATE() {
    return APPL_END_DATE;
  }

  @JsonProperty("d13")
  public void setAPPL_END_DATE(String aPPL_END_DATE) {
    APPL_END_DATE = aPPL_END_DATE;
  }

  public String getE_BED_DAY() {
    return E_BED_DAY;
  }

  @JsonProperty("d14")
  public void setE_BED_DAY(String e_BED_DAY) {
    E_BED_DAY = e_BED_DAY;
  }

  public String getS_BED_DAY() {
    return S_BED_DAY;
  }

  @JsonProperty("d15")
  public void setS_BED_DAY(String s_BED_DAY) {
    S_BED_DAY = s_BED_DAY;
  }

  public String getPATIENT_SOURCE() {
    return PATIENT_SOURCE;
  }

  @JsonProperty("d16")
  public void setPATIENT_SOURCE(String pATIENT_SOURCE) {
    PATIENT_SOURCE = pATIENT_SOURCE;
  }

  public String getCARD_SEQ_NO() {
    return CARD_SEQ_NO;
  }

  @JsonProperty("d17")
  public void setCARD_SEQ_NO(String cARD_SEQ_NO) {
    CARD_SEQ_NO = cARD_SEQ_NO;
  }

  public String getTW_DRG_CODE() {
    return TW_DRG_CODE;
  }

  @JsonProperty("d18")
  public void setTW_DRG_CODE(String tW_DRG_CODE) {
    TW_DRG_CODE = tW_DRG_CODE;
  }

  public String getTW_DRG_PAY_TYPE() {
    return TW_DRG_PAY_TYPE;
  }

  @JsonProperty("d19")
  public void setTW_DRG_PAY_TYPE(String tW_DRG_PAY_TYPE) {
    TW_DRG_PAY_TYPE = tW_DRG_PAY_TYPE;
  }

  public String getPRSN_ID() {
    return PRSN_ID;
  }

  @JsonProperty("d20")
  public void setPRSN_ID(String pRSN_ID) {
    PRSN_ID = pRSN_ID;
  }

  public String getCASE_DRG_CODE() {
    return CASE_DRG_CODE;
  }

  @JsonProperty("d21")
  public void setCASE_DRG_CODE(String cASE_DRG_CODE) {
    CASE_DRG_CODE = cASE_DRG_CODE;
  }

  public String getTRAN_CODE() {
    return TRAN_CODE;
  }

  @JsonProperty("d24")
  public void setTRAN_CODE(String tRAN_CODE) {
    TRAN_CODE = tRAN_CODE;
  }

  public String getICD_CM_1() {
    return ICD_CM_1;
  }

  @JsonProperty("d25")
  public void setICD_CM_1(String iCD_CM_1) {
    ICD_CM_1 = iCD_CM_1;
  }

  public String getICD_CM_2() {
    return ICD_CM_2;
  }

  @JsonProperty("d26")
  public void setICD_CM_2(String iCD_CM_2) {
    ICD_CM_2 = iCD_CM_2;
  }

  public String getICD_CM_3() {
    return ICD_CM_3;
  }

  @JsonProperty("d27")
  public void setICD_CM_3(String iCD_CM_3) {
    ICD_CM_3 = iCD_CM_3;
  }

  public String getICD_CM_4() {
    return ICD_CM_4;
  }

  @JsonProperty("d28")
  public void setICD_CM_4(String iCD_CM_4) {
    ICD_CM_4 = iCD_CM_4;
  }

  public String getICD_CM_5() {
    return ICD_CM_5;
  }

  @JsonProperty("d29")
  public void setICD_CM_5(String iCD_CM_5) {
    ICD_CM_5 = iCD_CM_5;
  }

  public String getICD_CM_6() {
    return ICD_CM_6;
  }

  @JsonProperty("d30")
  public void setICD_CM_6(String iCD_CM_6) {
    ICD_CM_6 = iCD_CM_6;
  }

  public String getICD_CM_7() {
    return ICD_CM_7;
  }

  @JsonProperty("d31")
  public void setICD_CM_7(String iCD_CM_7) {
    ICD_CM_7 = iCD_CM_7;
  }

  public String getICD_CM_8() {
    return ICD_CM_8;
  }

  @JsonProperty("d32")
  public void setICD_CM_8(String iCD_CM_8) {
    ICD_CM_8 = iCD_CM_8;
  }

  public String getICD_CM_9() {
    return ICD_CM_9;
  }

  @JsonProperty("d33")
  public void setICD_CM_9(String iCD_CM_9) {
    ICD_CM_9 = iCD_CM_9;
  }

  public String getICD_CM_10() {
    return ICD_CM_10;
  }

  @JsonProperty("d34")
  public void setICD_CM_10(String iCD_CM_10) {
    ICD_CM_10 = iCD_CM_10;
  }

  public String getICD_CM_11() {
    return ICD_CM_11;
  }

  @JsonProperty("d35")
  public void setICD_CM_11(String iCD_CM_11) {
    ICD_CM_11 = iCD_CM_11;
  }

  public String getICD_CM_12() {
    return ICD_CM_12;
  }

  @JsonProperty("d36")
  public void setICD_CM_12(String iCD_CM_12) {
    ICD_CM_12 = iCD_CM_12;
  }

  public String getICD_CM_13() {
    return ICD_CM_13;
  }

  @JsonProperty("d37")
  public void setICD_CM_13(String iCD_CM_13) {
    ICD_CM_13 = iCD_CM_13;
  }

  public String getICD_CM_14() {
    return ICD_CM_14;
  }

  @JsonProperty("d38")
  public void setICD_CM_14(String iCD_CM_14) {
    ICD_CM_14 = iCD_CM_14;
  }

  public String getICD_CM_15() {
    return ICD_CM_15;
  }

  @JsonProperty("d39")
  public void setICD_CM_15(String iCD_CM_15) {
    ICD_CM_15 = iCD_CM_15;
  }

  public String getICD_CM_16() {
    return ICD_CM_16;
  }

  @JsonProperty("d40")
  public void setICD_CM_16(String iCD_CM_16) {
    ICD_CM_16 = iCD_CM_16;
  }

  public String getICD_CM_17() {
    return ICD_CM_17;
  }

  @JsonProperty("d41")
  public void setICD_CM_17(String iCD_CM_17) {
    ICD_CM_17 = iCD_CM_17;
  }

  public String getICD_CM_18() {
    return ICD_CM_18;
  }

  @JsonProperty("d42")
  public void setICD_CM_18(String iCD_CM_18) {
    ICD_CM_18 = iCD_CM_18;
  }

  public String getICD_CM_19() {
    return ICD_CM_19;
  }

  @JsonProperty("d43")
  public void setICD_CM_19(String iCD_CM_19) {
    ICD_CM_19 = iCD_CM_19;
  }

  public String getICD_CM_20() {
    return ICD_CM_20;
  }

  @JsonProperty("d44")
  public void setICD_CM_20(String iCD_CM_20) {
    ICD_CM_20 = iCD_CM_20;
  }

  public String getICD_OP_CODE1() {
    return ICD_OP_CODE1;
  }

  @JsonProperty("d45")
  public void setICD_OP_CODE1(String iCD_OP_CODE1) {
    ICD_OP_CODE1 = iCD_OP_CODE1;
  }

  public String getICD_OP_CODE2() {
    return ICD_OP_CODE2;
  }

  @JsonProperty("d46")
  public void setICD_OP_CODE2(String iCD_OP_CODE2) {
    ICD_OP_CODE2 = iCD_OP_CODE2;
  }

  public String getICD_OP_CODE3() {
    return ICD_OP_CODE3;
  }

  @JsonProperty("d47")
  public void setICD_OP_CODE3(String iCD_OP_CODE3) {
    ICD_OP_CODE3 = iCD_OP_CODE3;
  }

  public String getICD_OP_CODE4() {
    return ICD_OP_CODE4;
  }

  @JsonProperty("d48")
  public void setICD_OP_CODE4(String iCD_OP_CODE4) {
    ICD_OP_CODE4 = iCD_OP_CODE4;
  }

  public String getICD_OP_CODE5() {
    return ICD_OP_CODE5;
  }

  @JsonProperty("d49")
  public void setICD_OP_CODE5(String iCD_OP_CODE5) {
    ICD_OP_CODE5 = iCD_OP_CODE5;
  }

  public String getICD_OP_CODE6() {
    return ICD_OP_CODE6;
  }

  @JsonProperty("d50")
  public void setICD_OP_CODE6(String iCD_OP_CODE6) {
    ICD_OP_CODE6 = iCD_OP_CODE6;
  }

  public String getICD_OP_CODE7() {
    return ICD_OP_CODE7;
  }

  @JsonProperty("d51")
  public void setICD_OP_CODE7(String iCD_OP_CODE7) {
    ICD_OP_CODE7 = iCD_OP_CODE7;
  }

  public String getICD_OP_CODE8() {
    return ICD_OP_CODE8;
  }

  @JsonProperty("d52")
  public void setICD_OP_CODE8(String iCD_OP_CODE8) {
    ICD_OP_CODE8 = iCD_OP_CODE8;
  }

  public String getICD_OP_CODE9() {
    return ICD_OP_CODE9;
  }

  @JsonProperty("d53")
  public void setICD_OP_CODE9(String iCD_OP_CODE9) {
    ICD_OP_CODE9 = iCD_OP_CODE9;
  }

  public String getICD_OP_CODE10() {
    return ICD_OP_CODE10;
  }

  @JsonProperty("d54")
  public void setICD_OP_CODE10(String iCD_OP_CODE10) {
    ICD_OP_CODE10 = iCD_OP_CODE10;
  }

  public String getICD_OP_CODE11() {
    return ICD_OP_CODE11;
  }

  @JsonProperty("d55")
  public void setICD_OP_CODE11(String iCD_OP_CODE11) {
    ICD_OP_CODE11 = iCD_OP_CODE11;
  }

  public String getICD_OP_CODE12() {
    return ICD_OP_CODE12;
  }

  @JsonProperty("d56")
  public void setICD_OP_CODE12(String iCD_OP_CODE12) {
    ICD_OP_CODE12 = iCD_OP_CODE12;
  }

  public String getICD_OP_CODE13() {
    return ICD_OP_CODE13;
  }

  @JsonProperty("d57")
  public void setICD_OP_CODE13(String iCD_OP_CODE13) {
    ICD_OP_CODE13 = iCD_OP_CODE13;
  }

  public String getICD_OP_CODE14() {
    return ICD_OP_CODE14;
  }

  @JsonProperty("d58")
  public void setICD_OP_CODE14(String iCD_OP_CODE14) {
    ICD_OP_CODE14 = iCD_OP_CODE14;
  }

  public String getICD_OP_CODE15() {
    return ICD_OP_CODE15;
  }

  @JsonProperty("d59")
  public void setICD_OP_CODE15(String iCD_OP_CODE15) {
    ICD_OP_CODE15 = iCD_OP_CODE15;
  }

  public String getICD_OP_CODE16() {
    return ICD_OP_CODE16;
  }

  @JsonProperty("d60")
  public void setICD_OP_CODE16(String iCD_OP_CODE16) {
    ICD_OP_CODE16 = iCD_OP_CODE16;
  }

  public String getICD_OP_CODE17() {
    return ICD_OP_CODE17;
  }

  @JsonProperty("d61")
  public void setICD_OP_CODE17(String iCD_OP_CODE17) {
    ICD_OP_CODE17 = iCD_OP_CODE17;
  }

  public String getICD_OP_CODE18() {
    return ICD_OP_CODE18;
  }

  @JsonProperty("d62")
  public void setICD_OP_CODE18(String iCD_OP_CODE18) {
    ICD_OP_CODE18 = iCD_OP_CODE18;
  }

  public String getICD_OP_CODE19() {
    return ICD_OP_CODE19;
  }

  @JsonProperty("d63")
  public void setICD_OP_CODE19(String iCD_OP_CODE19) {
    ICD_OP_CODE19 = iCD_OP_CODE19;
  }

  public String getICD_OP_CODE20() {
    return ICD_OP_CODE20;
  }

  @JsonProperty("d64")
  public void setICD_OP_CODE20(String iCD_OP_CODE20) {
    ICD_OP_CODE20 = iCD_OP_CODE20;
  }

  public Integer getORDER_QTY() {
    return ORDER_QTY;
  }

  @JsonProperty("d65")
  public void setORDER_QTY(Integer oRDER_QTY) {
    ORDER_QTY = oRDER_QTY;
  }

  public Integer getDIAG_DOT() {
    return DIAG_DOT;
  }

  @JsonProperty("d66")
  public void setDIAG_DOT(Integer dIAG_DOT) {
    DIAG_DOT = dIAG_DOT;
  }

  public Integer getROOM_DOT() {
    return ROOM_DOT;
  }


  @JsonProperty("d67")
  public void setROOM_DOT(Integer rOOM_DOT) {
    ROOM_DOT = rOOM_DOT;
  }

  public Integer getMEAL_DOT() {
    return MEAL_DOT;
  }

  @JsonProperty("d68")
  public void setMEAL_DOT(Integer mEAL_DOT) {
    MEAL_DOT = mEAL_DOT;
  }

  public Integer getAMIN_DOT() {
    return AMIN_DOT;
  }

  @JsonProperty("d69")
  public void setAMIN_DOT(Integer aMIN_DOT) {
    AMIN_DOT = aMIN_DOT;
  }

  public Integer getRADO_DOT() {
    return RADO_DOT;
  }

  @JsonProperty("d70")
  public void setRADO_DOT(Integer rADO_DOT) {
    RADO_DOT = rADO_DOT;
  }

  public Integer getTHRP_DOT() {
    return THRP_DOT;
  }

  @JsonProperty("d71")
  public void setTHRP_DOT(Integer tHRP_DOT) {
    THRP_DOT = tHRP_DOT;
  }

  public Integer getSGRY_DOT() {
    return SGRY_DOT;
  }

  @JsonProperty("d72")
  public void setSGRY_DOT(Integer sGRY_DOT) {
    SGRY_DOT = sGRY_DOT;
  }

  public Integer getPHSC_DOT() {
    return PHSC_DOT;
  }

  @JsonProperty("d73")
  public void setPHSC_DOT(Integer pHSC_DOT) {
    PHSC_DOT = pHSC_DOT;
  }

  public Integer getBLOD_DOT() {
    return BLOD_DOT;
  }

  @JsonProperty("d74")
  public void setBLOD_DOT(Integer bLOD_DOT) {
    BLOD_DOT = bLOD_DOT;
  }

  public Integer getHD_DOT() {
    return HD_DOT;
  }

  @JsonProperty("d75")
  public void setHD_DOT(Integer hD_DOT) {
    HD_DOT = hD_DOT;
  }

  public Integer getANE_DOT() {
    return ANE_DOT;
  }

  @JsonProperty("d76")
  public void setANE_DOT(Integer aNE_DOT) {
    ANE_DOT = aNE_DOT;
  }

  public Integer getMETR_DOT() {
    return METR_DOT;
  }

  @JsonProperty("d77")
  public void setMETR_DOT(Integer mETR_DOT) {
    METR_DOT = mETR_DOT;
  }

  public Integer getDRUG_DOT() {
    return DRUG_DOT;
  }

  @JsonProperty("d78")
  public void setDRUG_DOT(Integer dRUG_DOT) {
    DRUG_DOT = dRUG_DOT;
  }

  public Integer getDSVC_DOT() {
    return DSVC_DOT;
  }

  @JsonProperty("d79")
  public void setDSVC_DOT(Integer dSVC_DOT) {
    DSVC_DOT = dSVC_DOT;
  }

  public Integer getNRTP_DOT() {
    return NRTP_DOT;
  }

  @JsonProperty("d80")
  public void setNRTP_DOT(Integer nRTP_DOT) {
    NRTP_DOT = nRTP_DOT;
  }

  public Integer getINJT_DOT() {
    return INJT_DOT;
  }

  @JsonProperty("d81")
  public void setINJT_DOT(Integer iNJT_DOT) {
    INJT_DOT = iNJT_DOT;
  }

  public Integer getBABY_DOT() {
    return BABY_DOT;
  }

  @JsonProperty("d82")
  public void setBABY_DOT(Integer bABY_DOT) {
    BABY_DOT = bABY_DOT;
  }

  public Integer getMED_DOT() {
    return MED_DOT;
  }

  @JsonProperty("d83")
  public void setMED_DOT(Integer mED_DOT) {
    MED_DOT = mED_DOT;
  }

  public Integer getPART_DOT() {
    return PART_DOT;
  }

  @JsonProperty("d84")
  public void setPART_DOT(Integer pART_DOT) {
    PART_DOT = pART_DOT;
  }

  public Integer getAPPL_DOT() {
    return APPL_DOT;
  }

  @JsonProperty("d85")
  public void setAPPL_DOT(Integer aPPL_DOT) {
    APPL_DOT = aPPL_DOT;
  }

  public Integer getEB_APPL30_DOT() {
    return EB_APPL30_DOT;
  }

  @JsonProperty("d86")
  public void setEB_APPL30_DOT(Integer eB_APPL30_DOT) {
    EB_APPL30_DOT = eB_APPL30_DOT;
  }

  public Integer getEB_PART30_DOT() {
    return EB_PART30_DOT;
  }

  @JsonProperty("d87")
  public void setEB_PART30_DOT(Integer eB_PART30_DOT) {
    EB_PART30_DOT = eB_PART30_DOT;
  }

  public Integer getEB_APPL60_DOT() {
    return EB_APPL60_DOT;
  }

  @JsonProperty("d88")
  public void setEB_APPL60_DOT(Integer eB_APPL60_DOT) {
    EB_APPL60_DOT = eB_APPL60_DOT;
  }

  public Integer getEB_PART60_DOT() {
    return EB_PART60_DOT;
  }

  @JsonProperty("d89")
  public void setEB_PART60_DOT(Integer eB_PART60_DOT) {
    EB_PART60_DOT = eB_PART60_DOT;
  }

  public Integer getEB_APPL61_DOT() {
    return EB_APPL61_DOT;
  }

  @JsonProperty("d90")
  public void setEB_APPL61_DOT(Integer eB_APPL61_DOT) {
    EB_APPL61_DOT = eB_APPL61_DOT;
  }

  public Integer getEB_PART61_DOT() {
    return EB_PART61_DOT;
  }

  @JsonProperty("d91")
  public void setEB_PART61_DOT(Integer eB_PART61_DOT) {
    EB_PART61_DOT = eB_PART61_DOT;
  }

  public Integer getSB_APPL30_DOT() {
    return SB_APPL30_DOT;
  }

  @JsonProperty("d92")
  public void setSB_APPL30_DOT(Integer sB_APPL30_DOT) {
    SB_APPL30_DOT = sB_APPL30_DOT;
  }

  public Integer getSB_PART30_DOT() {
    return SB_PART30_DOT;
  }

  @JsonProperty("d93")
  public void setSB_PART30_DOT(Integer sB_PART30_DOT) {
    SB_PART30_DOT = sB_PART30_DOT;
  }

  public Integer getSB_APPL90_DOT() {
    return SB_APPL90_DOT;
  }

  @JsonProperty("d94")
  public void setSB_APPL90_DOT(Integer sB_APPL90_DOT) {
    SB_APPL90_DOT = sB_APPL90_DOT;
  }

  public Integer getSB_PART90_DOT() {
    return SB_PART90_DOT;
  }

  @JsonProperty("d95")
  public void setSB_PART90_DOT(Integer sB_PART90_DOT) {
    SB_PART90_DOT = sB_PART90_DOT;
  }

  public Integer getSB_APPL180_DOT() {
    return SB_APPL180_DOT;
  }

  @JsonProperty("d96")
  public void setSB_APPL180_DOT(Integer sB_APPL180_DOT) {
    SB_APPL180_DOT = sB_APPL180_DOT;
  }

  public Integer getSB_PART180_DOT() {
    return SB_PART180_DOT;
  }

  @JsonProperty("d97")
  public void setSB_PART180_DOT(Integer sB_PART180_DOT) {
    SB_PART180_DOT = sB_PART180_DOT;
  }

  public Integer getSB_APPL181_DOT() {
    return SB_APPL181_DOT;
  }

  @JsonProperty("d98")
  public void setSB_APPL181_DOT(Integer sB_APPL181_DOT) {
    SB_APPL181_DOT = sB_APPL181_DOT;
  }

  public Integer getSB_PART181_DOT() {
    return SB_PART181_DOT;
  }

  @JsonProperty("d99")
  public void setSB_PART181_DOT(Integer sB_PART181_DOT) {
    SB_PART181_DOT = sB_PART181_DOT;
  }

  public String getNB_BIRTHDAY() {
    return NB_BIRTHDAY;
  }

  @JsonProperty("d100")
  public void setNB_BIRTHDAY(String nB_BIRTHDAY) {
    NB_BIRTHDAY = nB_BIRTHDAY;
  }

  public String getCHILD_MARK() {
    return CHILD_MARK;
  }

  @JsonProperty("d101")
  public void setCHILD_MARK(String cHILD_MARK) {
    CHILD_MARK = cHILD_MARK;
  }

  public String getTW_DRGS_SUIT_MARK() {
    return TW_DRGS_SUIT_MARK;
  }

  @JsonProperty("d102")
  public void setTW_DRGS_SUIT_MARK(String tW_DRGS_SUIT_MARK) {
    TW_DRGS_SUIT_MARK = tW_DRGS_SUIT_MARK;
  }

  public String getNAME() {
    return NAME;
  }

  @JsonProperty("d103")
  public void setNAME(String nAME) {
    NAME = nAME;
  }

  public String getAGENCY_ID() {
    return AGENCY_ID;
  }

  @JsonProperty("d104")
  public void setAGENCY_ID(String aGENCY_ID) {
    AGENCY_ID = aGENCY_ID;
  }

  public String getTRAN_IN_HOSP_ID() {
    return TRAN_IN_HOSP_ID;
  }

  @JsonProperty("d107")
  public void setTRAN_IN_HOSP_ID(String tRAN_IN_HOSP_ID) {
    TRAN_IN_HOSP_ID = tRAN_IN_HOSP_ID;
  }

  public String getTRAN_OUT_HOSP_ID() {
    return TRAN_OUT_HOSP_ID;
  }

  @JsonProperty("d108")
  public void setTRAN_OUT_HOSP_ID(String tRAN_OUT_HOSP_ID) {
    TRAN_OUT_HOSP_ID = tRAN_OUT_HOSP_ID;
  }

  public String getHOSP_ID() {
    return HOSP_ID;
  }

  @JsonProperty("d109")
  public void setHOSP_ID(String hOSP_ID) {
    HOSP_ID = hOSP_ID;
  }

  public String getSVC_PLAN() {
    return SVC_PLAN;
  }

  @JsonProperty("d110")
  public void setSVC_PLAN(String sVC_PLAN) {
    SVC_PLAN = sVC_PLAN;
  }

  public String getPILOT_PROJECT() {
    return PILOT_PROJECT;
  }

  @JsonProperty("d111")
  public void setPILOT_PROJECT(String pILOT_PROJECT) {
    PILOT_PROJECT = pILOT_PROJECT;
  }

  public Integer getNON_APPL_DOT() {
    return NON_APPL_DOT;
  }

  @JsonProperty("d112")
  public void setNON_APPL_DOT(Integer nON_APPL_DOT) {
    NON_APPL_DOT = nON_APPL_DOT;
  }

  public List<InPatientP> getPdataList() {
    return pdataList;
  }

  public void setPdataList(List<InPatientP> pdataList) {
    this.pdataList = pdataList;
  }

  public Long getID() {
    return ID;
  }

  public void setID(Long iD) {
    ID = iD;
  }

  public Long getIPT_ID() {
    return IPT_ID;
  }

  public void setIPT_ID(Long iPT_ID) {
    IPT_ID = iPT_ID;
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
  
}
