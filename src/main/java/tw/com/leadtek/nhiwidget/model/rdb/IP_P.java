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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * 住院醫令清單
 * 
 * @author Ken Lai
 */
@JsonPropertyOrder({"p1", "p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9", "p10", "p11", "p12",
    "p13", "p14", "p15", "p16", "p17", "p18", "p19", "p20", "p21", "p22", "p23", "p24", "p25",
    "p26", "p27"})
@Table(name = "IP_P")
@Entity
public class IP_P {

  /**
   * 序號
   */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenIP_P")
  @SequenceGenerator(name = "seqGenIP_P", sequenceName = "SEQ_IP_P", allocationSize = 1000)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @JsonIgnore
  private Long id;

  /**
   * 序號
   */
  @Column(name = "IPD_ID", nullable = false)
  @JsonIgnore
  private Long ipdId;

  /**
   * 醫令序
   */
  @Column(name = "ORDER_SEQ_NO")
  @JsonProperty("ORDER_SEQ_NO")
  @JacksonXmlProperty(localName = "p1")
  private Integer orderSeqNo;

  /**
   * 醫令類別
   */
  @Column(name = "ORDER_TYPE")
  @JsonProperty("ORDER_TYPE")
  @JacksonXmlProperty(localName = "p2")
  private String orderType;

  /**
   * 醫令代碼
   */
  @Column(name = "ORDER_CODE")
  @JsonProperty("ORDER_CODE")
  @JacksonXmlProperty(localName = "p3")
  private String orderCode;

  /**
   * 院內碼
   */
  @Column(name = "INH_CODE")
  @JsonIgnore
  private String inhCode;

  /**
   * 支付成數
   */
  @Column(name = "PAY_RATE")
  @JsonProperty("PAY_RATE")
  @JacksonXmlProperty(localName = "p4")
  private String payRate;

  /**
   * 藥品用量
   */
  @Column(name = "DRUG_USE")
  @JsonProperty("DRUG_USE")
  @JacksonXmlProperty(localName = "p5")
  private Double drugUse;

  /**
   * (藥品)使用頻率
   */
  @Column(name = "DRUG_FRE")
  @JsonProperty("DRUG_FRE")
  @JacksonXmlProperty(localName = "p6")
  private String drugFre;

  /**
   * 給藥途徑/作用部位
   */
  @Column(name = "DRUG_PATH")
  @JsonProperty("DRUG_PATH")
  @JacksonXmlProperty(localName = "p7")
  private String drugPath;

  /**
   * 會診科別
   */
  @Column(name = "CON_FUNC_TYPE")
  @JsonProperty("CON_FUNC_TYPE")
  @JacksonXmlProperty(localName = "p8")
  private String conFuncType;

  /**
   * 病床號
   */
  @Column(name = "BED_NO")
  @JsonProperty("BED_NO")
  @JacksonXmlProperty(localName = "p9")
  private String bedNo;

  /**
   * 診療之部位
   */
  @Column(name = "CURE_PATH")
  @JsonProperty("CURE_PATH")
  @JacksonXmlProperty(localName = "p10")
  private String curePath;

  /**
   * TW-DRGS計算
   */
  @Column(name = "TW_DRGS_CALCU")
  @JsonProperty("TW_DRGS_CALCU")
  @JacksonXmlProperty(localName = "p11")
  private Double twDrgsCalcu;

  /**
   * 切帳前筆資料
   */
  @Column(name = "PART_ACCO_DATA")
  @JsonProperty("PART_ACCO_DATA")
  @JacksonXmlProperty(localName = "p12")
  private String partAccoData;

  /**
   * 器官捐贈者資料
   */
  @Column(name = "DONATER")
  @JsonProperty("DONATER")
  @JacksonXmlProperty(localName = "p13")
  private String donater;

  /**
   * 執行時間-起
   */
  @Column(name = "START_TIME")
  @JsonProperty("START_TIME")
  @JacksonXmlProperty(localName = "p14")
  private String startTime;

  /**
   * 執行時間-迄
   */
  @Column(name = "END_TIME")
  @JsonProperty("END_TIME")
  @JacksonXmlProperty(localName = "p15")
  private String endTime;

  /**
   * 總量
   */
  @Column(name = "TOTAL_Q")
  @JsonProperty("TOTAL_Q")
  @JacksonXmlProperty(localName = "p16")
  private Double totalQ;

  /**
   * 單價
   */
  @Column(name = "UNIT_P")
  @JsonProperty("UNIT_P")
  @JacksonXmlProperty(localName = "p17")
  private Float unitP;

  /**
   * 點數
   */
  @Column(name = "TOTAL_DOT")
  @JsonProperty("TOTAL_DOT")
  @JacksonXmlProperty(localName = "p18")
  private Integer totalDot;

  /**
   * 事前審查受理編號
   */
  @Column(name = "PRE_NO")
  @JsonProperty("PRE_NO")
  @JacksonXmlProperty(localName = "p19")
  private String preNo;

  /**
   * 執行醫事人員代號
   */
  @Column(name = "PRSN_ID")
  @JsonProperty("PRSN_ID")
  @JacksonXmlProperty(localName = "p20")
  private String prsnId;

  /**
   * 影像來源
   */
  @Column(name = "IMG_SOURCE")
  @JsonProperty("IMG_SOURCE")
  @JacksonXmlProperty(localName = "p21")
  private String imgSource;

  /**
   * 就醫科別
   */
  @Column(name = "FUNC_TYPE")
  @JsonProperty("FUNC_TYPE")
  @JacksonXmlProperty(localName = "p22")
  private String funcType;

  /**
   * 自費特材群組序號
   */
  @Column(name = "OWN_EXP_MTR_NO")
  @JsonProperty("OWN_EXP_MTR_NO")
  @JacksonXmlProperty(localName = "p23")
  private String ownExpMtrNo;

  /**
   * 未列項註記
   */
  @Column(name = "NON_LIST_MARK")
  @JsonProperty("NON_LIST_MARK")
  @JacksonXmlProperty(localName = "p24")
  private String nonListMark;

  /**
   * 未列項名稱
   */
  @Column(name = "NON_LIST_NAME")
  @JsonProperty("NON_LIST_NAME")
  @JacksonXmlProperty(localName = "p25")
  private String nonListName;

  /**
   * 委託或受託執行轉(代)檢醫事機構代號
   */
  @Column(name = "COMM_HOSP_ID")
  @JsonProperty("COMM_HOSP_ID")
  @JacksonXmlProperty(localName = "p26")
  private String commHospId;

  /**
   * 藥品批號
   */
  @Column(name = "DRUG_SERIAL_NO")
  @JsonProperty("DRUG_SERIAL_NO")
  @JacksonXmlProperty(localName = "p27")
  private String drugSerialNo;
  
  /**
   * 是否申報，申報狀態，1:要申報，2:下月申報，3:不申報，4:自費項目
   */
  @Column(name = "APPL_STATUS")
  @JsonIgnore
  private Integer applStatus;
  
  /**
   * 費用狀態，Y:自費計價 
N:健保計價申報 
H:健保不計價申報
h:健保不計價申報 
S:任何身份皆自費 
s:任何身份皆自費
X:不計價不申報 
x:不計價不申報 
Z:自費病人自費,健保病人不申報不計價
V:虛醫令,交付調劑之藥品空針
   */
  @Column(name = "PAY_BY")
  @JsonIgnore
  private String payBy;

  /**
   * 更新時間
   */
  @Column(name = "UPDATE_AT", nullable = false)
  @JsonIgnore
  private Date updateAt;
  
  /**
   * MR table ID
   */
  @Column(name = "MR_ID", nullable = true)
  @JsonIgnore
  private Long mrId;
  
  /**
   * 醫令於標準支付代碼中的類別代碼
   */
  @Column(name = "PAY_CODE_TYPE", nullable = true)
  @JsonIgnore
  private String payCodeType;
  
  /**
   * 醫師姓名
   */
  @Column(name = "PRSN_NAME", nullable = true)
  @JsonIgnore
  private String prsnName;
  
  /**
   * 主診斷
   */
  @Column(name = "ICD_CM_1")
  @JsonIgnore
  private String icdCm1;
  
  /**
   * 醫師姓名
   */
  @Column(name = "ROC_ID", nullable = true)
  @JsonIgnore
  private String rocid;
  
  /**
   * 病患姓名
   */
  @JsonIgnore
  @Transient
  private String name;
  
  /**
   * 病歷號碼
   */
  @JsonIgnore
  @Transient
  private String inhMr;
  
  /**
   * 病患生日
   */
  @JsonIgnore
  @Transient
  private String birthday;
  

  /**
   * 就醫記錄編號
   */
  @JsonIgnore
  @Transient
  private String inhClinicId;


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
  public Long getIpdId() {
    return ipdId;
  }

  /**
   * 序號
   */
  public void setIpdId(Long IPD_ID) {
    ipdId = IPD_ID;
  }

  /**
   * <p1> 醫令序
   */
  public Integer getOrderSeqNo() {
    return orderSeqNo;
  }

  /**
   * <p1> 醫令序
   */
  public void setOrderSeqNo(Integer ORDER_SEQ_NO) {
    orderSeqNo = ORDER_SEQ_NO;
  }

  /**
   * <p2> 醫令類別
   */
  public String getOrderType() {
    return orderType;
  }

  /**
   * <p2> 醫令類別
   */
  public void setOrderType(String ORDER_TYPE) {
    orderType = ORDER_TYPE;
  }

  /**
   * <p3> 醫令代碼
   */
  public String getOrderCode() {
    return orderCode;
  }

  /**
   * <p3> 醫令代碼
   */
  public void setOrderCode(String ORDER_CODE) {
    orderCode = ORDER_CODE;
  }

  /**
   * <p4> 支付成數
   */
  public String getPayRate() {
    return payRate;
  }

  /**
   * <p4> 支付成數
   */
  public void setPayRate(String PayRate) {
    payRate = PayRate;
  }

  /**
   * <p5> 藥品用量
   */
  public Double getDrugUse() {
    return drugUse;
  }

  /**
   * <p5> 藥品用量
   */
  public void setDrugUse(Double DRUG_USE) {
    drugUse = DRUG_USE;
  }

  /**
   * <p6> (藥品)使用頻率
   */
  public String getDrugFre() {
    return drugFre;
  }

  /**
   * <p6> (藥品)使用頻率
   */
  public void setDrugFre(String DRUG_FRE) {
    drugFre = DRUG_FRE;
  }

  /**
   * <p7> 給藥途徑/作用部位
   */
  public String getDrugPath() {
    return drugPath;
  }

  /**
   * <p7> 給藥途徑/作用部位
   */
  public void setDrugPath(String DRUG_PATH) {
    drugPath = DRUG_PATH;
  }

  /**
   * <p8> 會診科別
   */
  public String getConFuncType() {
    return conFuncType;
  }

  /**
   * <p8> 會診科別
   */
  public void setConFuncType(String CON_FUNC_TYPE) {
    conFuncType = CON_FUNC_TYPE;
  }

  /**
   * <p9> 病床號
   */
  public String getBedNo() {
    return bedNo;
  }

  /**
   * <p9> 病床號
   */
  public void setBedNo(String BED_NO) {
    bedNo = BED_NO;
  }

  /**
   * <p10> 診療之部位
   */
  public String getCurePath() {
    return curePath;
  }

  /**
   * <p10> 診療之部位
   */
  public void setCurePath(String CURE_PATH) {
    curePath = CURE_PATH;
  }

  /**
   * <p11> TW-DRGS計算
   */
  public Double getTwDrgsCalcu() {
    return twDrgsCalcu;
  }

  /**
   * <p11> TW-DRGS計算
   */
  public void setTwDrgsCalcu(Double TW_DRGS_CALCU) {
    twDrgsCalcu = TW_DRGS_CALCU;
  }

  /**
   * <p12> 切帳前筆資料
   */
  public String getPartAccoData() {
    return partAccoData;
  }

  /**
   * <p12> 切帳前筆資料
   */
  public void setPartAccoData(String PART_ACCO_DATA) {
    partAccoData = PART_ACCO_DATA;
  }

  /**
   * <p13> 器官捐贈者資料
   */
  public String getDonater() {
    return donater;
  }

  /**
   * <p13> 器官捐贈者資料
   */
  public void setDonater(String DONATER) {
    donater = DONATER;
  }

  /**
   * <p14> 執行時間-起
   */
  public String getStartTime() {
    return startTime;
  }

  /**
   * <p14> 執行時間-起
   */
  public void setStartTime(String START_TIME) {
    startTime = START_TIME;
  }

  /**
   * <p15> 執行時間-迄
   */
  public String getEndTime() {
    return endTime;
  }

  /**
   * <p15> 執行時間-迄
   */
  public void setEndTime(String END_TIME) {
    endTime = END_TIME;
  }

  /**
   * <p16> 總量
   */
  public Double getTotalQ() {
    return totalQ;
  }

  /**
   * <p16> 總量
   */
  public void setTotalQ(Double TOTAL_Q) {
    totalQ = TOTAL_Q;
  }

  /**
   * <p17> 單價
   */
  public Float getUnitP() {
    return unitP;
  }

  /**
   * <p17> 單價
   */
  public void setUnitP(Float UNIT_P) {
    unitP = UNIT_P;
  }

  /**
   * <p18> 點數
   */
  public Integer getTotalDot() {
    return totalDot;
  }

  /**
   * <p18> 點數
   */
  public void setTotalDot(Integer TOTAL_DOT) {
    totalDot = TOTAL_DOT;
  }

  /**
   * <p19> 事前審查受理編號
   */
  public String getPreNo() {
    return preNo;
  }

  /**
   * <p19> 事前審查受理編號
   */
  public void setPreNo(String PRE_NO) {
    preNo = PRE_NO;
  }

  /**
   * <p20> 執行醫事人員代號
   */
  public String getPrsnId() {
    return prsnId;
  }

  /**
   * <p20> 執行醫事人員代號
   */
  public void setPrsnId(String PRSN_ID) {
    prsnId = PRSN_ID;
  }

  /**
   * <p21> 影像來源
   */
  public String getImgSource() {
    return imgSource;
  }

  /**
   * <p21> 影像來源
   */
  public void setImgSource(String IMG_SOURCE) {
    imgSource = IMG_SOURCE;
  }

  /**
   * <p22> 就醫科別
   */
  public String getFuncType() {
    return funcType;
  }

  /**
   * <p22> 就醫科別
   */
  public void setFuncType(String FUNC_TYPE) {
    funcType = FUNC_TYPE;
  }

  /**
   * <p23> 自費特材群組序號
   */
  public String getOwnExpMtrNo() {
    return ownExpMtrNo;
  }

  /**
   * <p23> 自費特材群組序號
   */
  public void setOwnExpMtrNo(String OWN_EXP_MTR_NO) {
    ownExpMtrNo = OWN_EXP_MTR_NO;
  }

  /**
   * <p24> 未列項註記
   */
  public String getNonListMark() {
    return nonListMark;
  }

  /**
   * <p24> 未列項註記
   */
  public void setNonListMark(String NON_LIST_MARK) {
    nonListMark = NON_LIST_MARK;
  }

  /**
   * <p25> 未列項名稱
   */
  public String getNonListName() {
    return nonListName;
  }

  /**
   * <p25> 未列項名稱
   */
  public void setNonListName(String NON_LIST_NAME) {
    nonListName = NON_LIST_NAME;
  }

  /**
   * <p26> 委託或受託執行轉(代)檢醫事機構代號
   */
  public String getCommHospId() {
    return commHospId;
  }

  /**
   * <p26> 委託或受託執行轉(代)檢醫事機構代號
   */
  public void setCommHospId(String COMM_HOSP_ID) {
    commHospId = COMM_HOSP_ID;
  }

  /**
   * <p27> 藥品批號
   */
  public String getDrugSerialNo() {
    return drugSerialNo;
  }

  /**
   * <p27> 藥品批號
   */
  public void setDrugSerialNo(String DRUG_SERIAL_NO) {
    drugSerialNo = DRUG_SERIAL_NO;
  }

  public String getInhCode() {
    return inhCode;
  }

  public void setInhCode(String inhCode) {
    this.inhCode = inhCode;
  }

  public Date getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(Date updateAt) {
    this.updateAt = updateAt;
  }

  public Integer getApplStatus() {
    return applStatus;
  }

  public void setApplStatus(Integer applStatus) {
    this.applStatus = applStatus;
  }

  public String getPayBy() {
    return payBy;
  }

  public void setPayBy(String payBy) {
    this.payBy = payBy;
  }

  public Long getMrId() {
    return mrId;
  }

  public void setMrId(Long mrId) {
    this.mrId = mrId;
  }

  public String getPayCodeType() {
    return payCodeType;
  }

  public void setPayCodeType(String payCodeType) {
    this.payCodeType = payCodeType;
  }

  public String getPrsnName() {
    return prsnName;
  }

  public void setPrsnName(String prsnName) {
    this.prsnName = prsnName;
  }

  public String getIcdCm1() {
    return icdCm1;
  }

  public void setIcdCm1(String icdCm1) {
    this.icdCm1 = icdCm1;
  }

  public String getRocid() {
    return rocid;
  }

  public void setRocid(String rocid) {
    this.rocid = rocid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getInhMr() {
    return inhMr;
  }

  public void setInhMr(String inhMr) {
    this.inhMr = inhMr;
  }

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public String getInhClinicId() {
    return inhClinicId;
  }

  public void setInhClinicId(String inhClinicId) {
    this.inhClinicId = inhClinicId;
  }

}
