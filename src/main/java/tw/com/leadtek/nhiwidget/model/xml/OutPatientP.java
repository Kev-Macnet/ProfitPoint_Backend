/**
 * Created on 2020/12/24.
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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * 門診醫令清單
 * 
 * @author Ken Lai
 */
@JsonPropertyOrder({"p1", "p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9", "p10", 
  "p11", "p12", "p13", "p14", "p15", "p16", "p17", "p18", "p19", "p20", "p21", "p22",
  "p23", "p24", "p25"})
@Table(name = "op_p")
@Entity
public class OutPatientP {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @JsonIgnore
  private Long ID;
  
  @Column(name = "OPD_ID", nullable = false)
  private Long OPD_ID;

  /**
   * 藥品給藥日份
   */
  @Column(length = 2)
  private Integer DRUG_DAY;

  /**
   * 醫令調劑方式
   */
  @Column(length = 1)
  private String MED_TYPE;

  /**
   * 醫令類別
   */
  @Column(length = 1)
  private String ORDER_TYPE;

  /**
   * 藥品(項目)代號
   */
  @Column(length = 12)
  private String DRUG_NO;

  /**
   * 藥品用量
   */
  private Integer DRUG_USE;

  /**
   * 診療之部位
   */
  @Column(length = 18)
  private String CURE_PATH;

  /**
   * 藥品使用頻率
   */
  @Column(length = 18)
  private String DRUG_FRE;

  /**
   * 支付成數
   */
  private Float PAY_RATE;

  /**
   * 給藥途徑/作用部位
   */
  @Column(length = 4)
  private String DRUG_PATH;

  /**
   * 總量
   */
  private Integer TOTAL_Q;

  /**
   * 單價
   */
  private Float UNIT_P;

  /**
   * 點數
   */
  private Integer TOTAL_DOT;

  /**
   * 醫令序
   */
  private Integer ORDER_SEQ_NO;

  /**
   * 執行時間-起
   */
  @Column(length = 11)
  private String START_TIME;

  /**
   * 執行時間-迄
   */
  @Column(length = 11)
  private String END_TIME;

  /**
   * 執行醫事人員代號
   */
  @Column(length = 10)
  private String PRSN_ID;

  /**
   * 慢性病連續處方箋、同一療程及排程檢查案件註記
   */
  @Column(length = 1)
  private String CHR_MARK;

  /**
   * 影像來源
   */
  @Column(length = 1)
  private String IMG_SOURCE;

  /**
   * 事前審查受理編號
   */
  @Column(length = 13)
  private String PRE_NO;

  /**
   * 就醫科別
   */
  @Column(length = 2)
  private String FUNC_TYPE;

  /**
   * 自費特材群組序號/其他特殊註記
   */
  @Column(length = 3)
  private String OWN_EXP_MTR_NO;

  /**
   * 未列項註記
   */
  @Column(length = 1)
  private String NON_LIST_MARK;

  /**
   * 未列項名稱
   */
  @Column(length = 100)
  private String NON_LIST_NAME;

  /**
   * 委託或受託執行轉(代)檢醫事機構代號
   */
  @Column(length = 10)
  private String COMM_HOSP_ID;

  /**
   * 藥品批號
   */
  @Column(length = 20)
  private String DRUG_SERIAL_NO;

  public Integer getDRUG_DAY() {
    return DRUG_DAY;
  }

  @JacksonXmlProperty(localName = "p1")
  public void setDRUG_DAY(Integer dRUG_DAY) {
    DRUG_DAY = dRUG_DAY;
  }

  public String getMED_TYPE() {
    return MED_TYPE;
  }

  @JacksonXmlProperty(localName = "p2")
  public void setMED_TYPE(String mED_TYPE) {
    MED_TYPE = mED_TYPE;
  }

  public String getORDER_TYPE() {
    return ORDER_TYPE;
  }

  @JacksonXmlProperty(localName = "p3")
  public void setORDER_TYPE(String oRDER_TYPE) {
    ORDER_TYPE = oRDER_TYPE;
  }

  public String getDRUG_NO() {
    return DRUG_NO;
  }

  @JacksonXmlProperty(localName = "p4")
  public void setDRUG_NO(String dRUG_NO) {
    DRUG_NO = dRUG_NO;
  }

  public Integer getDRUG_USE() {
    return DRUG_USE;
  }

  @JacksonXmlProperty(localName = "p5")
  public void setDRUG_USE(Integer dRUG_USE) {
    DRUG_USE = dRUG_USE;
  }

  public String getCURE_PATH() {
    return CURE_PATH;
  }

  @JacksonXmlProperty(localName = "p6")
  public void setCURE_PATH(String cURE_PATH) {
    CURE_PATH = cURE_PATH;
  }

  public String getDRUG_FRE() {
    return DRUG_FRE;
  }

  @JacksonXmlProperty(localName = "p7")
  public void setDRUG_FRE(String dRUG_FRE) {
    DRUG_FRE = dRUG_FRE;
  }

  public Float getPAY_RATE() {
    return PAY_RATE;
  }

  @JacksonXmlProperty(localName = "p8")
  public void setPAY_RATE(Float pAY_RATE) {
    PAY_RATE = pAY_RATE;
  }

  public String getDRUG_PATH() {
    return DRUG_PATH;
  }

  @JacksonXmlProperty(localName = "p9")
  public void setDRUG_PATH(String dRUG_PATH) {
    DRUG_PATH = dRUG_PATH;
  }

  public Integer getTOTAL_Q() {
    return TOTAL_Q;
  }

  @JacksonXmlProperty(localName = "p10")
  public void setTOTAL_Q(Integer tOTAL_Q) {
    TOTAL_Q = tOTAL_Q;
  }

  public Float getUNIT_P() {
    return UNIT_P;
  }

  @JacksonXmlProperty(localName = "p11")
  public void setUNIT_P(Float uNIT_P) {
    UNIT_P = uNIT_P;
  }

  public Integer getTOTAL_DOT() {
    return TOTAL_DOT;
  }

  @JacksonXmlProperty(localName = "p12")
  public void setTOTAL_DOT(Integer tOTAL_DOT) {
    TOTAL_DOT = tOTAL_DOT;
  }

  public Integer getORDER_SEQ_NO() {
    return ORDER_SEQ_NO;
  }

  @JacksonXmlProperty(localName = "p13")
  public void setORDER_SEQ_NO(Integer oRDER_SEQ_NO) {
    ORDER_SEQ_NO = oRDER_SEQ_NO;
  }

  public String getSTART_TIME() {
    return START_TIME;
  }

  @JacksonXmlProperty(localName = "p14")
  public void setSTART_TIME(String sTART_TIME) {
    START_TIME = sTART_TIME;
  }

  public String getEND_TIME() {
    return END_TIME;
  }

  @JacksonXmlProperty(localName = "p15")
  public void setEND_TIME(String eND_TIME) {
    END_TIME = eND_TIME;
  }

  public String getPRSN_ID() {
    return PRSN_ID;
  }

  @JacksonXmlProperty(localName = "p16")
  public void setPRSN_ID(String pRSN_ID) {
    PRSN_ID = pRSN_ID;
  }

  public String getCHR_MARK() {
    return CHR_MARK;
  }

  @JacksonXmlProperty(localName = "p17")
  public void setCHR_MARK(String cHR_MARK) {
    CHR_MARK = cHR_MARK;
  }

  public String getIMG_SOURCE() {
    return IMG_SOURCE;
  }

  @JacksonXmlProperty(localName = "p18")
  public void setIMG_SOURCE(String iMG_SOURCE) {
    IMG_SOURCE = iMG_SOURCE;
  }

  public String getPRE_NO() {
    return PRE_NO;
  }

  @JacksonXmlProperty(localName = "p19")
  public void setPRE_NO(String pRE_NO) {
    PRE_NO = pRE_NO;
  }

  public String getFUNC_TYPE() {
    return FUNC_TYPE;
  }

  @JacksonXmlProperty(localName = "p20")
  public void setFUNC_TYPE(String fUNC_TYPE) {
    FUNC_TYPE = fUNC_TYPE;
  }

  public String getOWN_EXP_MTR_NO() {
    return OWN_EXP_MTR_NO;
  }

  @JacksonXmlProperty(localName = "p21")
  public void setOWN_EXP_MTR_NO(String oWN_EXP_MTR_NO) {
    OWN_EXP_MTR_NO = oWN_EXP_MTR_NO;
  }

  public String getNON_LIST_MARK() {
    return NON_LIST_MARK;
  }

  @JacksonXmlProperty(localName = "p22")
  public void setNON_LIST_MARK(String nON_LIST_MARK) {
    NON_LIST_MARK = nON_LIST_MARK;
  }

  public String getNON_LIST_NAME() {
    return NON_LIST_NAME;
  }

  @JacksonXmlProperty(localName = "p23")
  public void setNON_LIST_NAME(String nON_LIST_NAME) {
    NON_LIST_NAME = nON_LIST_NAME;
  }

  public String getCOMM_HOSP_ID() {
    return COMM_HOSP_ID;
  }

  @JacksonXmlProperty(localName = "p24")
  public void setCOMM_HOSP_ID(String cOMM_HOSP_ID) {
    COMM_HOSP_ID = cOMM_HOSP_ID;
  }

  public String getDRUG_SERIAL_NO() {
    return DRUG_SERIAL_NO;
  }

  @JacksonXmlProperty(localName = "p25")
  public void setDRUG_SERIAL_NO(String dRUG_SERIAL_NO) {
    DRUG_SERIAL_NO = dRUG_SERIAL_NO;
  }

  public Long getID() {
    return ID;
  }

  public void setID(Long iD) {
    ID = iD;
  }

  public Long getOPD_ID() {
    return OPD_ID;
  }

  public void setOPD_ID(Long oPD_ID) {
    OPD_ID = oPD_ID;
  }

}
