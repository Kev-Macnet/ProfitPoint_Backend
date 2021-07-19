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

/**
 * 門診醫令清單
 * 
 * @author Ken Lai
 */
@JsonPropertyOrder({"p1", "p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9", "p10", "p11", "p12",
    "p13", "p14", "p15", "p16", "p17", "p18", "p19", "p20", "p21", "p22", "p23", "p24", "p25"})
@Table(name = "OP_P")
@Entity
public class OP_P {

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
   * 序號
   */
  @Column(name = "OPD_ID", nullable = false)
  @JsonIgnore
  private Long opdId;

  /**
   * 藥品給藥日份
   */
  @Column(name = "DRUG_DAY")
  @JsonProperty("DRUG_DAY")
  @JacksonXmlProperty(localName = "p1")
  private Integer drugDay;

  /**
   * 醫令調劑方式
   */
  @Column(name = "MED_TYPE")
  @JsonProperty("MED_TYPE")
  @JacksonXmlProperty(localName = "p2")
  private String medType;

  /**
   * 醫令類別
   */
  @Column(name = "ORDER_TYPE")
  @JsonProperty("ORDER_TYPE")
  @JacksonXmlProperty(localName = "p3")
  private String orderType;

  /**
   * 藥品(項目)代號
   */
  @Column(name = "DRUG_NO")
  @JsonProperty("DRUG_NO")
  @JacksonXmlProperty(localName = "p4")
  private String drugNo;

  /**
   * 藥品用量
   */
  @Column(name = "DRUG_USE")
  @JsonProperty("DRUG_USE")
  @JacksonXmlProperty(localName = "p5")
  private String drugUse;

  /**
   * 診療之部位
   */
  @Column(name = "CURE_PATH")
  @JsonProperty("CURE_PATH")
  @JacksonXmlProperty(localName = "p6")
  private String curePath;

  /**
   * 藥品使用頻率
   */
  @Column(name = "DRUG_FRE")
  @JsonProperty("DRUG_FRE")
  @JacksonXmlProperty(localName = "p7")
  private String drugFre;

  /**
   * 支付成數
   */
  @Column(name = "PAY_RATE")
  @JsonProperty("PAY_RATE")
  @JacksonXmlProperty(localName = "p8")
  private String payRate;

  /**
   * 給藥途徑/作用部位
   */
  @Column(name = "DRUG_PATH")
  @JsonProperty("DRUG_PATH")
  @JacksonXmlProperty(localName = "p9")
  private String drugPath;

  /**
   * 總量
   */
  @Column(name = "TOTAL_Q")
  @JsonProperty("TOTAL_Q")
  @JacksonXmlProperty(localName = "p10")
  private Integer totalQ;

  /**
   * 單價
   */
  @Column(name = "UNIT_P")
  @JsonProperty("UNIT_P")
  @JacksonXmlProperty(localName = "p11")
  private Float unitP;

  /**
   * 點數
   */
  @Column(name = "TOTAL_DOT")
  @JsonProperty("TOTAL_DOT")
  @JacksonXmlProperty(localName = "p12")
  private Integer totalDot;

  /**
   * 醫令序
   */
  @Column(name = "ORDER_SEQ_NO")
  @JsonProperty("ORDER_SEQ_NO")
  @JacksonXmlProperty(localName = "p13")
  private Integer orderSeqNo;

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
   * 執行醫事人員代號
   */
  @Column(name = "PRSN_ID")
  @JsonProperty("PRSN_ID")
  @JacksonXmlProperty(localName = "p16")
  private String prsnId;

  /**
   * 慢性病連續處方箋、同一療程及排程檢查案件註記
   */
  @Column(name = "CHR_MARK")
  @JsonProperty("CHR_MARK")
  @JacksonXmlProperty(localName = "p17")
  private String chrMark;

  /**
   * 影像來源
   */
  @Column(name = "IMG_SOURCE")
  @JsonProperty("IMG_SOURCE")
  @JacksonXmlProperty(localName = "p18")
  private String imgSource;

  /**
   * 事前審查受理編號
   */
  @Column(name = "PRE_NO")
  @JsonProperty("PRE_NO")
  @JacksonXmlProperty(localName = "p19")
  private String preNo;

  /**
   * 就醫科別
   */
  @Column(name = "FUNC_TYPE")
  @JsonProperty("FUNC_TYPE")
  @JacksonXmlProperty(localName = "p20")
  private String funcType;

  /**
   * 自費特材群組序號/其他特殊註記
   */
  @Column(name = "OWN_EXP_MTR_NO")
  @JsonProperty("OWN_EXP_MTR_NO")
  @JacksonXmlProperty(localName = "p21")
  private String ownExpMtrNo;

  /**
   * 未列項註記
   */
  @Column(name = "NON_LIST_MARK")
  @JsonProperty("NON_LIST_MARK")
  @JacksonXmlProperty(localName = "p22")
  private String nonListMark;

  /**
   * 未列項名稱
   */
  @Column(name = "NON_LIST_NAME")
  @JsonProperty("NON_LIST_NAME")
  @JacksonXmlProperty(localName = "p23")
  private String nonListName;

  /**
   * 委託或受託執行轉(代)檢醫事機構代號
   */
  @Column(name = "COMM_HOSP_ID")
  @JsonProperty("COMM_HOSP_ID")
  @JacksonXmlProperty(localName = "p24")
  private String commHospId;

  /**
   * 藥品批號
   */
  @Column(name = "DRUG_SERIAL_NO")
  @JsonProperty("DRUG_SERIAL_NO")
  @JacksonXmlProperty(localName = "p25")
  private String drugSerialNo;

  /**
   * 是否申報，0:不申報，1:要申報
   */
  @Column(name = "APPL_STATUS")
  @JsonIgnore
  private Integer applStatus;
  
  /**
   * 費用狀態，1:健保給付，2:自費，3:勞保，4:其他
   */
  @Column(name = "PAY_BY")
  @JsonIgnore
  private Integer payBy;
  
  /**
   * 院內碼
   */
  @Column(name = "INH_CODE")
  @JsonIgnore
  private String inhCode;
  
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
  public Long getOpdId() {
    return opdId;
  }

  /**
   * 序號
   */
  public void setOpdId(Long OPD_ID) {
    opdId = OPD_ID;
  }

  /**
   * <p1> 藥品給藥日份
   */
  public Integer getDrugDay() {
    return drugDay;
  }

  /**
   * <p1> 藥品給藥日份
   */
  public void setDrugDay(Integer DRUG_DAY) {
    drugDay = DRUG_DAY;
  }

  /**
   * <p2> 醫令調劑方式
   */
  public String getMedType() {
    return medType;
  }

  /**
   * <p2> 醫令調劑方式
   */
  public void setMedType(String MED_TYPE) {
    medType = MED_TYPE;
  }

  /**
   * <p3> 醫令類別
   */
  public String getOrderType() {
    return orderType;
  }

  /**
   * <p3> 醫令類別
   */
  public void setOrderType(String ORDER_TYPE) {
    orderType = ORDER_TYPE;
  }

  /**
   * <p4> 藥品(項目)代號
   */
  public String getDrugNo() {
    return drugNo;
  }

  /**
   * <p4> 藥品(項目)代號
   */
  public void setDrugNo(String DRUG_NO) {
    drugNo = DRUG_NO;
  }

  /**
   * <p5> 藥品用量
   */
  public String getDrugUse() {
    return drugUse;
  }

  /**
   * <p5> 藥品用量
   */
  public void setDrugUse(String DRUG_USE) {
    drugUse = DRUG_USE;
  }

  /**
   * <p6> 診療之部位
   */
  public String getCurePath() {
    return curePath;
  }

  /**
   * <p6> 診療之部位
   */
  public void setCurePath(String CURE_PATH) {
    curePath = CURE_PATH;
  }

  /**
   * <p7> 藥品使用頻率
   */
  public String getDrugFre() {
    return drugFre;
  }

  /**
   * <p7> 藥品使用頻率
   */
  public void setDrugFre(String DRUG_FRE) {
    drugFre = DRUG_FRE;
  }

  /**
   * <p8> 支付成數
   */
  public String getPayRate() {
    return payRate;
  }

  /**
   * <p8> 支付成數
   */
  public void setPayRate(String PAY_RATE) {
    payRate = PAY_RATE;
  }

  /**
   * <p9> 給藥途徑/作用部位
   */
  public String getDrugPath() {
    return drugPath;
  }

  /**
   * <p9> 給藥途徑/作用部位
   */
  public void setDrugPath(String DRUG_PATH) {
    drugPath = DRUG_PATH;
  }

  /**
   * <p10> 總量
   */
  public Integer getTotalQ() {
    return totalQ;
  }

  /**
   * <p10> 總量
   */
  public void setTotalQ(Integer TOTAL_Q) {
    totalQ = TOTAL_Q;
  }

  /**
   * <p11> 單價
   */
  public Float getUnitP() {
    return unitP;
  }

  /**
   * <p11> 單價
   */
  public void setUnitP(Float UNIT_P) {
    unitP = UNIT_P;
  }

  /**
   * <p12> 點數
   */
  public Integer getTotalDot() {
    return totalDot;
  }

  /**
   * <p12> 點數
   */
  public void setTotalDot(Integer TOTAL_DOT) {
    totalDot = TOTAL_DOT;
  }

  /**
   * <p13> 醫令序
   */
  public Integer getOrderSeqNo() {
    return orderSeqNo;
  }

  /**
   * <p13> 醫令序
   */
  public void setOrderSeqNo(Integer ORDER_SEQ_NO) {
    orderSeqNo = ORDER_SEQ_NO;
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
   * <p16> 執行醫事人員代號
   */
  public String getPrsnId() {
    return prsnId;
  }

  /**
   * <p16> 執行醫事人員代號
   */
  public void setPrsnId(String PRSN_ID) {
    prsnId = PRSN_ID;
  }

  /**
   * <p17> 慢性病連續處方箋、同一療程及排程檢查案件註記
   */
  public String getChrMark() {
    return chrMark;
  }

  /**
   * <p17> 慢性病連續處方箋、同一療程及排程檢查案件註記
   */
  public void setChrMark(String CHR_MARK) {
    chrMark = CHR_MARK;
  }

  /**
   * <p18> 影像來源
   */
  public String getImgSource() {
    return imgSource;
  }

  /**
   * <p18> 影像來源
   */
  public void setImgSource(String IMG_SOURCE) {
    imgSource = IMG_SOURCE;
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
   * <p20> 就醫科別
   */
  public String getFuncType() {
    return funcType;
  }

  /**
   * <p20> 就醫科別
   */
  public void setFuncType(String FUNC_TYPE) {
    funcType = FUNC_TYPE;
  }

  /**
   * <p21> 自費特材群組序號/其他特殊註記
   */
  public String getOwnExpMtrNo() {
    return ownExpMtrNo;
  }

  /**
   * <p21> 自費特材群組序號/其他特殊註記
   */
  public void setOwnExpMtrNo(String OWN_EXP_MTR_NO) {
    ownExpMtrNo = OWN_EXP_MTR_NO;
  }

  /**
   * <p22> 未列項註記
   */
  public String getNonListMark() {
    return nonListMark;
  }

  /**
   * <p22> 未列項註記
   */
  public void setNonListMark(String NON_LIST_MARK) {
    nonListMark = NON_LIST_MARK;
  }

  /**
   * <p23> 未列項名稱
   */
  public String getNonListName() {
    return nonListName;
  }

  /**
   * <p23> 未列項名稱
   */
  public void setNonListName(String NON_LIST_NAME) {
    nonListName = NON_LIST_NAME;
  }

  /**
   * <p24> 委託或受託執行轉(代)檢醫事機構代號
   */
  public String getCommHospId() {
    return commHospId;
  }

  /**
   * <p24> 委託或受託執行轉(代)檢醫事機構代號
   */
  public void setCommHospId(String COMM_HOSP_ID) {
    commHospId = COMM_HOSP_ID;
  }

  /**
   * <p25> 藥品批號
   */
  public String getDrugSerialNo() {
    return drugSerialNo;
  }

  /**
   * <p25> 藥品批號
   */
  public void setDrugSerialNo(String DRUG_SERIAL_NO) {
    drugSerialNo = DRUG_SERIAL_NO;
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

  public Integer getPayBy() {
    return payBy;
  }

  public void setPayBy(Integer payBy) {
    this.payBy = payBy;
  }

  public String getInhCode() {
    return inhCode;
  }

  public void setInhCode(String inhCode) {
    this.inhCode = inhCode;
  }

  public Long getMrId() {
    return mrId;
  }

  public void setMrId(Long mrId) {
    this.mrId = mrId;
  }

}