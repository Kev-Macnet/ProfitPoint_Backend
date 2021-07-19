/**
 * Created on 2021/01/28.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.util.Date;
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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

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
@Table(name = "OP_D")
@Entity
public class OP_D {

  /**
   * 序號
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @JsonIgnore
  private Long id;

  @Column(name = "OPT_ID", nullable = false)
  @JsonIgnore
  private Long optId;

  /**
   * 案件分類，01:西醫一般，02:西醫急診
   */
  @Column(name = "CASE_TYPE", length = 50)
  @JsonIgnore
  private String caseType;

  @Column(name = "SEQ_NO")
  @JsonIgnore
  private Integer seqNo;

  @JsonProperty("pdata")
  @JacksonXmlElementWrapper(useWrapping = false)
  @Transient
  private List<OP_P> pdataList;

  /**
   * 身分證統一編號
   */
  @Column(name = "ROC_ID")
  @JsonProperty("ROC_ID")
  @JacksonXmlProperty(localName = "d3")
  private String rocId;

  /**
   * 特定治療項目代號(一)
   */
  @Column(name = "CURE_ITEM_NO1")
  @JsonProperty("CURE_ITEM_NO1")
  @JacksonXmlProperty(localName = "d4")
  private String cureItemNo1;

  /**
   * 特定治療項目代號(二)
   */
  @Column(name = "CURE_ITEM_NO2")
  @JsonProperty("CURE_ITEM_NO2")
  @JacksonXmlProperty(localName = "d5")
  private String cureItemNo2;

  /**
   * 特定治療項目代號(三)
   */
  @Column(name = "CURE_ITEM_NO3")
  @JsonProperty("CURE_ITEM_NO3")
  @JacksonXmlProperty(localName = "d6")
  private String cureItemNo3;

  /**
   * 特定治療項目代號(四)
   */
  @Column(name = "CURE_ITEM_NO4")
  @JsonProperty("CURE_ITEM_NO4")
  @JacksonXmlProperty(localName = "d7")
  private String cureItemNo4;

  /**
   * 就醫科別
   */
  @Column(name = "FUNC_TYPE")
  @JsonProperty("FUNC_TYPE")
  @JacksonXmlProperty(localName = "d8")
  private String funcType;

  /**
   * 就醫日期
   */
  @Column(name = "FUNC_DATE")
  @JsonProperty("FUNC_DATE")
  @JacksonXmlProperty(localName = "d9")
  private String funcDate;

  /**
   * 治療結束日期
   */
  @Column(name = "FUNC_END_DATE")
  @JsonProperty("FUNC_END_DATE")
  @JacksonXmlProperty(localName = "d10")
  private String funcEndDate;

  /**
   * 出生年月日
   */
  @Column(name = "ID_BIRTH_YMD")
  @JsonProperty("ID_BIRTH_YMD")
  @JacksonXmlProperty(localName = "d11")
  private String idBirthYmd;

  /**
   * 補報原因註記
   */
  @Column(name = "APPL_CAUSE_MARK")
  @JsonProperty("APPL_CAUSE_MARK")
  @JacksonXmlProperty(localName = "d12")
  private String applCauseMark;

  /**
   * 整合式照護計畫註記
   */
  @Column(name = "CARE_MARK")
  @JsonProperty("CARE_MARK")
  @JacksonXmlProperty(localName = "d13")
  private String careMark;

  /**
   * 給付類別
   */
  @Column(name = "PAY_TYPE")
  @JsonProperty("PAY_TYPE")
  @JacksonXmlProperty(localName = "d14")
  private String payType;

  /**
   * 部分負擔代號
   */
  @Column(name = "PART_NO")
  @JsonProperty("PART_NO")
  @JacksonXmlProperty(localName = "d15")
  private String partNo;

  /**
   * 轉診、處方調劑或特定檢查資源共享案件註記
   */
  @Column(name = "SHARE_MARK")
  @JsonProperty("SHARE_MARK")
  @JacksonXmlProperty(localName = "d16")
  private String shareMark;

  /**
   * 轉診、處方調劑或特定檢查資源共享案件之服務機構代號
   */
  @Column(name = "SHARE_HOSP_ID")
  @JsonProperty("SHARE_HOSP_ID")
  @JacksonXmlProperty(localName = "d17")
  private String shareHospId;

  /**
   * 病患是否轉出
   */
  @Column(name = "PAT_TRAN_OUT")
  @JsonProperty("PAT_TRAN_OUT")
  @JacksonXmlProperty(localName = "d18")
  private String patTranOut;

  /**
   * 主診斷代碼
   */
  @Column(name = "ICD_CM_1")
  @JsonProperty("ICD_CM_1")
  @JacksonXmlProperty(localName = "d19")
  private String icdCm1;

  /**
   * 次診斷代碼(一)
   */
  @Column(name = "ICD_CM_2")
  @JsonProperty("ICD_CM_2")
  @JacksonXmlProperty(localName = "d20")
  private String icdCm2;

  /**
   * 次診斷代碼(二)
   */
  @Column(name = "ICD_CM_3")
  @JsonProperty("ICD_CM_3")
  @JacksonXmlProperty(localName = "d21")
  private String icdCm3;

  /**
   * 次診斷代碼(三)
   */
  @Column(name = "ICD_CM_4")
  @JsonProperty("ICD_CM_4")
  @JacksonXmlProperty(localName = "d22")
  private String icdCm4;

  /**
   * 次診斷代碼(四)
   */
  @Column(name = "ICD_CM_5")
  @JsonProperty("ICD_CM_5")
  @JacksonXmlProperty(localName = "d23")
  private String icdCm5;

  /**
   * 主手術(處置)代碼
   */
  @Column(name = "ICD_OP_CODE1")
  @JsonProperty("ICD_OP_CODE1")
  @JacksonXmlProperty(localName = "d24")
  private String icdOpCode1;

  /**
   * 次手術(處置)代碼(一)
   */
  @Column(name = "ICD_OP_CODE2")
  @JsonProperty("ICD_OP_CODE2")
  @JacksonXmlProperty(localName = "d25")
  private String icdOpCode2;

  /**
   * 次手術(處置)代碼(二)
   */
  @Column(name = "ICD_OP_CODE3")
  @JsonProperty("ICD_OP_CODE3")
  @JacksonXmlProperty(localName = "d26")
  private String icdOpCode3;

  /**
   * 給藥日份
   */
  @Column(name = "DRUG_DAY")
  @JsonProperty("DRUG_DAY")
  @JacksonXmlProperty(localName = "d27")
  private Integer drugDay;

  /**
   * 處方調劑方式
   */
  @Column(name = "MED_TYPE")
  @JsonProperty("MED_TYPE")
  @JacksonXmlProperty(localName = "d28")
  private String medType;

  /**
   * 就醫序號
   */
  @Column(name = "CARD_SEQ_NO")
  @JsonProperty("CARD_SEQ_NO")
  @JacksonXmlProperty(localName = "d29")
  private String cardSeqNo;

  /**
   * 診治醫事人員代號
   */
  @Column(name = "PRSN_ID")
  @JsonProperty("PRSN_ID")
  @JacksonXmlProperty(localName = "d30")
  private String prsnId;

  /**
   * 藥師代號
   */
  @Column(name = "PHAR_ID")
  @JsonProperty("PHAR_ID")
  @JacksonXmlProperty(localName = "d31")
  private String pharId;

  /**
   * 用藥明細點數小計
   */
  @Column(name = "DRUG_DOT")
  @JsonProperty("DRUG_DOT")
  @JacksonXmlProperty(localName = "d32")
  private Integer drugDot;

  /**
   * 診療明細點數小計
   */
  @Column(name = "TREAT_DOT")
  @JsonProperty("TREAT_DOT")
  @JacksonXmlProperty(localName = "d33")
  private Integer treatDot;

  /**
   * 特殊材料明細點數小計
   */
  @Column(name = "METR_DOT")
  @JsonProperty("METR_DOT")
  @JacksonXmlProperty(localName = "d34")
  private Integer metrDot;

  /**
   * 診察費項目代號
   */
  @Column(name = "TREAT_CODE")
  @JsonProperty("TREAT_CODE")
  @JacksonXmlProperty(localName = "d35")
  private String treatCode;

  /**
   * 診察費點數
   */
  @Column(name = "DIAG_DOT")
  @JsonProperty("DIAG_DOT")
  @JacksonXmlProperty(localName = "d36")
  private Integer diagDot;

  /**
   * 藥事服務費項目代號
   */
  @Column(name = "DSVC_NO")
  @JsonProperty("DSVC_NO")
  @JacksonXmlProperty(localName = "d37")
  private String dsvcNo;

  /**
   * 藥事服務費點數
   */
  @Column(name = "DSVC_DOT")
  @JsonProperty("DSVC_DOT")
  @JacksonXmlProperty(localName = "d38")
  private Integer dsvcDot;

  /**
   * 合計點數
   */
  @Column(name = "T_DOT")
  @JsonProperty("T_DOT")
  @JacksonXmlProperty(localName = "d39")
  private Integer totalDot;

  /**
   * 部分負擔點數
   */
  @Column(name = "PART_DOT")
  @JsonProperty("PART_DOT")
  @JacksonXmlProperty(localName = "d40")
  private Integer partDot;

  /**
   * 申請點數
   */
  @Column(name = "T_APPL_DOT")
  @JsonProperty("T_APPL_DOT")
  @JacksonXmlProperty(localName = "d41")
  private Integer totalApplDot;

  /**
   * 論病例計酬代碼
   */
  @Column(name = "CASE_PAY_CODE")
  @JsonProperty("CASE_PAY_CODE")
  @JacksonXmlProperty(localName = "d42")
  private String casePayCode;

  /**
   * 行政協助項目部分負擔點數
   */
  @Column(name = "ASSIST_PART_DOT")
  @JsonProperty("ASSIST_PART_DOT")
  @JacksonXmlProperty(localName = "d43")
  private Integer assistPartDot;

  /**
   * 慢性病連續處方箋有效期間總處方日份
   */
  @Column(name = "CHR_DAYS")
  @JsonProperty("CHR_DAYS")
  @JacksonXmlProperty(localName = "d44")
  private Integer chrDays;

  /**
   * 依附就醫新生兒出生日期
   */
  @Column(name = "NB_BIRTHDAY")
  @JsonProperty("NB_BIRTHDAY")
  @JacksonXmlProperty(localName = "d45")
  private String nbBirthday;

  /**
   * 山地離島地區醫療服務計畫代碼
   */
  @Column(name = "OUT_SVC_PLAN_CODE")
  @JsonProperty("OUT_SVC_PLAN_CODE")
  @JacksonXmlProperty(localName = "d48")
  private String outSvcPlanCode;

  /**
   * 姓名
   */
  @Column(name = "NAME")
  @JsonProperty("NAME")
  @JacksonXmlProperty(localName = "d49")
  private String name;

  /**
   * 矯正機關代號
   */
  @Column(name = "AGENCY_ID")
  @JsonProperty("AGENCY_ID")
  @JacksonXmlProperty(localName = "d50")
  private String agencyId;

  /**
   * 依附就醫新生兒胞胎註記
   */
  @Column(name = "CHILD_MARK")
  @JsonProperty("CHILD_MARK")
  @JacksonXmlProperty(localName = "d51")
  private String childMark;

  /**
   * 特定地區醫療服務
   */
  @Column(name = "SPE_AREA_SVC")
  @JsonProperty("SPE_AREA_SVC")
  @JacksonXmlProperty(localName = "d52")
  private String speAreaSvc;

  /**
   * 支援區域
   */
  @Column(name = "SUPPORT_AREA")
  @JsonProperty("SUPPORT_AREA")
  @JacksonXmlProperty(localName = "d53")
  private String supportArea;

  /**
   * 實際提供醫療服務之醫事服務機構代號
   */
  @Column(name = "HOSP_ID")
  @JsonProperty("HOSP_ID")
  @JacksonXmlProperty(localName = "d54")
  private String hospId;

  /**
   * 轉往之醫事服務機構代號
   */
  @Column(name = "TRAN_IN_HOSP_ID")
  @JsonProperty("TRAN_IN_HOSP_ID")
  @JacksonXmlProperty(localName = "d55")
  private String tranInHospId;

  /**
   * 原處方就醫序號
   */
  @Column(name = "ORI_CARD_SEQ_NO")
  @JsonProperty("ORI_CARD_SEQ_NO")
  @JacksonXmlProperty(localName = "d56")
  private String oriCardSeqNo;
  
  /**
   * MR table ID
   */
  @Column(name = "MR_ID", nullable = true)
  @JsonIgnore
  private Long mrId;

  /**
   * 更新時間
   */
  @Column(name = "UPDATE_AT", nullable = false)
  @JsonIgnore
  private Date updateAt;

  public List<OP_P> getPdataList() {
    return pdataList;
  }

  public void setPdataList(List<OP_P> pdataList) {
    this.pdataList = pdataList;
  }

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
   * 序號
   */
  public Long getOptId() {
    return optId;
  }

  /**
   * 序號
   */
  public void setOptId(Long OPT_ID) {
    optId = OPT_ID;
  }

  public String getCaseType() {
    return caseType;
  }

  public void setCaseType(String CASE_TYPE) {
    caseType = CASE_TYPE;
  }

  public Integer getSeqNo() {
    return seqNo;
  }

  public void setSeqNo(Integer SEQ_NO) {
    seqNo = SEQ_NO;
  }

  /**
   * <d3> 身分證統一編號
   */
  public String getRocId() {
    return rocId;
  }

  /**
   * <d3> 身分證統一編號
   */
  public void setRocId(String ROC_ID) {
    rocId = ROC_ID;
  }

  /**
   * <d4> 特定治療項目代號(一)
   */
  public String getCureItemNo1() {
    return cureItemNo1;
  }

  /**
   * <d4> 特定治療項目代號(一)
   */
  public void setCureItemNo1(String CURE_ITEM_NO1) {
    cureItemNo1 = CURE_ITEM_NO1;
  }

  /**
   * <d5> 特定治療項目代號(二)
   */
  public String getCureItemNo2() {
    return cureItemNo2;
  }

  /**
   * <d5> 特定治療項目代號(二)
   */
  public void setCureItemNo2(String CURE_ITEM_NO2) {
    cureItemNo2 = CURE_ITEM_NO2;
  }

  /**
   * <d6> 特定治療項目代號(三)
   */
  public String getCureItemNo3() {
    return cureItemNo3;
  }

  /**
   * <d6> 特定治療項目代號(三)
   */
  public void setCureItemNo3(String CURE_ITEM_NO3) {
    cureItemNo3 = CURE_ITEM_NO3;
  }

  /**
   * <d7> 特定治療項目代號(四)
   */
  public String getCureItemNo4() {
    return cureItemNo4;
  }

  /**
   * <d7> 特定治療項目代號(四)
   */
  public void setCureItemNo4(String CURE_ITEM_NO4) {
    cureItemNo4 = CURE_ITEM_NO4;
  }

  /**
   * <d8> 就醫科別
   */
  public String getFuncType() {
    return funcType;
  }

  /**
   * <d8> 就醫科別
   */
  public void setFuncType(String FUNC_TYPE) {
    funcType = FUNC_TYPE;
  }

  /**
   * <d9> 就醫日期
   */
  public String getFuncDate() {
    return funcDate;
  }

  /**
   * <d9> 就醫日期
   */
  public void setFuncDate(String FUNC_DATE) {
    funcDate = FUNC_DATE;
  }

  /**
   * <d10> 治療結束日期
   */
  public String getFuncEndDate() {
    return funcEndDate;
  }

  /**
   * <d10> 治療結束日期
   */
  public void setFuncEndDate(String FUNC_END_DATE) {
    funcEndDate = FUNC_END_DATE;
  }

  /**
   * <d11> 出生年月日
   */
  public String getIdBirthYmd() {
    return idBirthYmd;
  }

  /**
   * <d11> 出生年月日
   */
  public void setIdBirthYmd(String ID_BIRTH_YMD) {
    idBirthYmd = ID_BIRTH_YMD;
  }

  /**
   * <d12> 補報原因註記
   */
  public String getApplCauseMark() {
    return applCauseMark;
  }

  /**
   * <d12> 補報原因註記
   */
  public void setApplCauseMark(String APPL_CAUSE_MARK) {
    applCauseMark = APPL_CAUSE_MARK;
  }

  /**
   * <d13> 整合式照護計畫註記
   */
  public String getCareMark() {
    return careMark;
  }

  /**
   * <d13> 整合式照護計畫註記
   */
  public void setCareMark(String CARE_MARK) {
    careMark = CARE_MARK;
  }

  /**
   * <d14> 給付類別
   */
  public String getPayType() {
    return payType;
  }

  /**
   * <d14> 給付類別
   */
  public void setPayType(String PAY_TYPE) {
    payType = PAY_TYPE;
  }

  /**
   * <d15> 部分負擔代號
   */
  public String getPartNo() {
    return partNo;
  }

  /**
   * <d15> 部分負擔代號
   */
  public void setPartNo(String PART_NO) {
    partNo = PART_NO;
  }

  /**
   * <d16> 轉診、處方調劑或特定檢查資源共享案件註記
   */
  public String getShareMark() {
    return shareMark;
  }

  /**
   * <d16> 轉診、處方調劑或特定檢查資源共享案件註記
   */
  public void setShareMark(String SHARE_MARK) {
    shareMark = SHARE_MARK;
  }

  /**
   * <d17> 轉診、處方調劑或特定檢查資源共享案件之服務機構代號
   */
  public String getShareHospId() {
    return shareHospId;
  }

  /**
   * <d17> 轉診、處方調劑或特定檢查資源共享案件之服務機構代號
   */
  public void setShareHospId(String SHARE_HOSP_ID) {
    shareHospId = SHARE_HOSP_ID;
  }

  /**
   * <d18> 病患是否轉出
   */
  public String getPatTranOut() {
    return patTranOut;
  }

  /**
   * <d18> 病患是否轉出
   */
  public void setPatTranOut(String PAT_TRAN_OUT) {
    patTranOut = PAT_TRAN_OUT;
  }

  /**
   * <d19> 主診斷代碼
   */
  public String getIcdCm1() {
    return icdCm1;
  }

  /**
   * <d19> 主診斷代碼
   */
  public void setIcdCm1(String ICD_CM_1) {
    icdCm1 = ICD_CM_1;
  }

  /**
   * <d20> 次診斷代碼(一)
   */
  public String getIcdCm2() {
    return icdCm2;
  }

  /**
   * <d20> 次診斷代碼(一)
   */
  public void setIcdCm2(String ICD_CM_2) {
    icdCm2 = ICD_CM_2;
  }

  /**
   * <d21> 次診斷代碼(二)
   */
  public String getIcdCm3() {
    return icdCm3;
  }

  /**
   * <d21> 次診斷代碼(二)
   */
  public void setIcdCm3(String ICD_CM_3) {
    icdCm3 = ICD_CM_3;
  }

  /**
   * <d22> 次診斷代碼(三)
   */
  public String getIcdCm4() {
    return icdCm4;
  }

  /**
   * <d22> 次診斷代碼(三)
   */
  public void setIcdCm4(String ICD_CM_4) {
    icdCm4 = ICD_CM_4;
  }

  /**
   * <d23> 次診斷代碼(四)
   */
  public String getIcdCm5() {
    return icdCm5;
  }

  /**
   * <d23> 次診斷代碼(四)
   */
  public void setIcdCm5(String ICD_CM_5) {
    icdCm5 = ICD_CM_5;
  }

  /**
   * <d24> 主手術(處置)代碼
   */
  public String getIcdOpCode1() {
    return icdOpCode1;
  }

  /**
   * <d24> 主手術(處置)代碼
   */
  public void setIcdOpCode1(String ICD_OP_CODE1) {
    icdOpCode1 = ICD_OP_CODE1;
  }

  /**
   * <d25> 次手術(處置)代碼(一)
   */
  public String getIcdOpCode2() {
    return icdOpCode2;
  }

  /**
   * <d25> 次手術(處置)代碼(一)
   */
  public void setIcdOpCode2(String ICD_OP_CODE2) {
    icdOpCode2 = ICD_OP_CODE2;
  }

  /**
   * <d26> 次手術(處置)代碼(二)
   */
  public String getIcdOpCode3() {
    return icdOpCode3;
  }

  /**
   * <d26> 次手術(處置)代碼(二)
   */
  public void setIcdOpCode3(String ICD_OP_CODE3) {
    icdOpCode3 = ICD_OP_CODE3;
  }

  /**
   * <d27> 給藥日份
   */
  public Integer getDrugDay() {
    return drugDay;
  }

  /**
   * <d27> 給藥日份
   */
  public void setDrugDay(Integer DRUG_DAY) {
    drugDay = DRUG_DAY;
  }

  /**
   * <d28> 處方調劑方式
   */
  public String getMedType() {
    return medType;
  }

  /**
   * <d28> 處方調劑方式
   */
  public void setMedType(String MED_TYPE) {
    medType = MED_TYPE;
  }

  /**
   * <d29> 就醫序號
   */
  public String getCardSeqNo() {
    return cardSeqNo;
  }

  /**
   * <d29> 就醫序號
   */
  public void setCardSeqNo(String CARD_SEQ_NO) {
    cardSeqNo = CARD_SEQ_NO;
  }

  /**
   * <d30> 診治醫事人員代號
   */
  public String getPrsnId() {
    return prsnId;
  }

  /**
   * <d30> 診治醫事人員代號
   */
  public void setPrsnId(String PRSN_ID) {
    prsnId = PRSN_ID;
  }

  /**
   * <d31> 藥師代號
   */
  public String getPharId() {
    return pharId;
  }

  /**
   * <d31> 藥師代號
   */
  public void setPharId(String PHAR_ID) {
    pharId = PHAR_ID;
  }

  /**
   * <d32> 用藥明細點數小計
   */
  public Integer getDrugDot() {
    return drugDot;
  }

  /**
   * <d32> 用藥明細點數小計
   */
  public void setDrugDot(Integer DRUG_DOT) {
    drugDot = DRUG_DOT;
  }

  /**
   * <d33> 診療明細點數小計
   */
  public Integer getTreatDot() {
    return treatDot;
  }

  /**
   * <d33> 診療明細點數小計
   */
  public void setTreatDot(Integer TREAT_DOT) {
    treatDot = TREAT_DOT;
  }

  /**
   * <d34> 特殊材料明細點數小計
   */
  public Integer getMetrDot() {
    return metrDot;
  }

  /**
   * <d34> 特殊材料明細點數小計
   */
  public void setMetrDot(Integer METR_DOT) {
    metrDot = METR_DOT;
  }

  /**
   * <d35> 診察費項目代號
   */
  public String getTreatCode() {
    return treatCode;
  }

  /**
   * <d35> 診察費項目代號
   */
  public void setTreatCode(String TREAT_CODE) {
    treatCode = TREAT_CODE;
  }

  /**
   * <d36> 診察費點數
   */
  public Integer getDiagDot() {
    return diagDot;
  }

  /**
   * <d36> 診察費點數
   */
  public void setDiagDot(Integer DIAG_DOT) {
    diagDot = DIAG_DOT;
  }

  /**
   * <d37> 藥事服務費項目代號
   */
  public String getDsvcNo() {
    return dsvcNo;
  }

  /**
   * <d37> 藥事服務費項目代號
   */
  public void setDsvcNo(String DSVC_NO) {
    dsvcNo = DSVC_NO;
  }

  /**
   * <d38> 藥事服務費點數
   */
  public Integer getDsvcDot() {
    return dsvcDot;
  }

  /**
   * <d38> 藥事服務費點數
   */
  public void setDsvcDot(Integer DSVC_DOT) {
    dsvcDot = DSVC_DOT;
  }

  public Integer getTotalDot() {
    return totalDot;
  }

  public void setTotalDot(Integer totalDot) {
    this.totalDot = totalDot;
  }

  /**
   * <d40> 部分負擔點數
   */
  public Integer getPartDot() {
    return partDot;
  }

  /**
   * <d40> 部分負擔點數
   */
  public void setPartDot(Integer PART_DOT) {
    partDot = PART_DOT;
  }

  public Integer getTotalApplDot() {
    return totalApplDot;
  }

  public void setTotalApplDot(Integer totalApplDot) {
    this.totalApplDot = totalApplDot;
  }

  /**
   * <d42> 論病例計酬代碼
   */
  public String getCasePayCode() {
    return casePayCode;
  }

  /**
   * <d42> 論病例計酬代碼
   */
  public void setCasePayCode(String CASE_PAY_CODE) {
    casePayCode = CASE_PAY_CODE;
  }

  /**
   * <d43> 行政協助項目部分負擔點數
   */
  public Integer getAssistPartDot() {
    return assistPartDot;
  }

  /**
   * <d43> 行政協助項目部分負擔點數
   */
  public void setAssistPartDot(Integer ASSIST_PART_DOT) {
    assistPartDot = ASSIST_PART_DOT;
  }

  /**
   * <d44> 慢性病連續處方箋有效期間總處方日份
   */
  public Integer getChrDays() {
    return chrDays;
  }

  /**
   * <d44> 慢性病連續處方箋有效期間總處方日份
   */
  public void setChrDays(Integer CHR_DAYS) {
    chrDays = CHR_DAYS;
  }

  /**
   * <d45> 依附就醫新生兒出生日期
   */
  public String getNbBirthday() {
    return nbBirthday;
  }

  /**
   * <d45> 依附就醫新生兒出生日期
   */
  public void setNbBirthday(String NB_BIRTHDAY) {
    nbBirthday = NB_BIRTHDAY;
  }

  /**
   * <d48> 山地離島地區醫療服務計畫代碼
   */
  public String getOutSvcPlanCode() {
    return outSvcPlanCode;
  }

  /**
   * <d48> 山地離島地區醫療服務計畫代碼
   */
  public void setOutSvcPlanCode(String OUT_SVC_PLAN_CODE) {
    outSvcPlanCode = OUT_SVC_PLAN_CODE;
  }

  /**
   * <d49> 姓名
   */
  public String getName() {
    return name;
  }

  /**
   * <d49> 姓名
   */
  public void setName(String NAME) {
    name = NAME;
  }

  /**
   * <d50> 矯正機關代號
   */
  public String getAgencyId() {
    return agencyId;
  }

  /**
   * <d50> 矯正機關代號
   */
  public void setAgencyId(String AGENCY_ID) {
    agencyId = AGENCY_ID;
  }

  /**
   * <d51> 依附就醫新生兒胞胎註記
   */
  public String getChildMark() {
    return childMark;
  }

  /**
   * <d51> 依附就醫新生兒胞胎註記
   */
  public void setChildMark(String CHILD_MARK) {
    childMark = CHILD_MARK;
  }

  /**
   * <d52> 特定地區醫療服務
   */
  public String getSpeAreaSvc() {
    return speAreaSvc;
  }

  /**
   * <d52> 特定地區醫療服務
   */
  public void setSpeAreaSvc(String SPE_AREA_SVC) {
    speAreaSvc = SPE_AREA_SVC;
  }

  /**
   * <d53> 支援區域
   */
  public String getSupportArea() {
    return supportArea;
  }

  /**
   * <d53> 支援區域
   */
  public void setSupportArea(String SUPPORT_AREA) {
    supportArea = SUPPORT_AREA;
  }

  /**
   * <d54> 實際提供醫療服務之醫事服務機構代號
   */
  public String getHospId() {
    return hospId;
  }

  /**
   * <d54> 實際提供醫療服務之醫事服務機構代號
   */
  public void setHospId(String HOSP_ID) {
    hospId = HOSP_ID;
  }

  /**
   * <d55> 轉往之醫事服務機構代號
   */
  public String getTranInHospId() {
    return tranInHospId;
  }

  /**
   * <d55> 轉往之醫事服務機構代號
   */
  public void setTranInHospId(String TRAN_IN_HOSP_ID) {
    tranInHospId = TRAN_IN_HOSP_ID;
  }

  /**
   * <d56> 原處方就醫序號
   */
  public String getOriCardSeqNo() {
    return oriCardSeqNo;
  }

  /**
   * <d56> 原處方就醫序號
   */
  public void setOriCardSeqNo(String ORI_CARD_SEQ_NO) {
    oriCardSeqNo = ORI_CARD_SEQ_NO;
  }

  public Date getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(Date updateAt) {
    this.updateAt = updateAt;
  }

  public Long getMrId() {
    return mrId;
  }

  public void setMrId(Long mrId) {
    this.mrId = mrId;
  }

}
