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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 住院醫令清單
 * 
 * @author Ken Lai
 */
@JsonPropertyOrder({"p1", "p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9", "p10", 
  "p11", "p12", "p13", "p14", "p15", "p16", "p17", "p18", "p19", "p20", "p21", "p22",
  "p23", "p24", "p25", "p26", "p27"})
@Table(name = "ip_p")
@Entity
public class InPatientP {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @JsonIgnore
  private Long ID;
  
  @Column(name = "IPD_ID", nullable = false)
  private Long IPD_ID;
  
  /**
   * 醫令序
   */
  private Integer ORDER_SEQ_NO;
  
  /**
   * 醫令類別
   */
  @Column(length = 2)
  private String ORDER_TYPE;
  
  /**
   * 醫令代碼
   */
  @Column(length = 12)
  private String ORDER_CODE;
  
  /**
   * 支付成數
   */
  @Column(length = 6)
  private String RATE_TYPE;
  
  /**
   * 藥品用量
   */
  private Integer DRUG_USE;
  
  /**
   * (藥品)使用頻率
   */
  @Column(length = 18)
  private String DRUG_FRE;
  
  /**
   * 給藥途徑/作用部位
   */
  @Column(length = 4)
  private String DRUG_PATH;
  
  /**
   * 會診科別
   */
  @Column(length = 2)
  private String CON_FUNC_TYPE;
  
  /**
   * 病床號
   */
  @Column(length = 10)
  private String BED_NO;
  
  /**
   * 診療之部位
   */
  @Column(length = 18)
  private String CURE_PATH;
  
  /**
   * Tw-DRGs計算
   */
  private Integer TW_DRGS_CALCU;
  
  /**
   * 切帳前筆資料
   */
  @Column(length = 21)
  private String PART_ACCO_DATA;
  
  /**
   * 器官捐贈者資料
   */
  @Column(length = 28)
  private String DONATER;
  
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
   * 事前審查受理編號
   */
  @Column(length = 13)
  private String PRE_NO;
  
  /**
   * 執行醫事人員代號
   */
  @Column(length = 10)
  private String PRSN_ID;
  
  /**
   * 影像來源
   */
  @Column(length = 1)
  private String IMG_SOURCE;
  
  /**
   * 就醫科別
   */
  @Column(length = 2)
  private String FUNC_TYPE;
  
  /**
   * 自費特材群組序號
   */
  @Column(length = 3)
  private String OWN_EXP_MTR_NO ;
  
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

  public Integer getORDER_SEQ_NO() {
    return ORDER_SEQ_NO;
  }

  @JsonProperty("p1")
  public void setORDER_SEQ_NO(Integer oRDER_SEQ_NO) {
    ORDER_SEQ_NO = oRDER_SEQ_NO;
  }

  public String getORDER_TYPE() {
    return ORDER_TYPE;
  }

  @JsonProperty("p2")
  public void setORDER_TYPE(String oRDER_TYPE) {
    ORDER_TYPE = oRDER_TYPE;
  }

  public String getORDER_CODE() {
    return ORDER_CODE;
  }

  @JsonProperty("p3")
  public void setORDER_CODE(String oRDER_CODE) {
    ORDER_CODE = oRDER_CODE;
  }

  public String getRATE_TYPE() {
    return RATE_TYPE;
  }

  @JsonProperty("p4")
  public void setRATE_TYPE(String rATE_TYPE) {
    RATE_TYPE = rATE_TYPE;
  }

  public Integer getDRUG_USE() {
    return DRUG_USE;
  }

  @JsonProperty("p5")
  public void setDRUG_USE(Integer dRUG_USE) {
    DRUG_USE = dRUG_USE;
  }

  public String getDRUG_FRE() {
    return DRUG_FRE;
  }

  @JsonProperty("p6")
  public void setDRUG_FRE(String dRUG_FRE) {
    DRUG_FRE = dRUG_FRE;
  }

  public String getDRUG_PATH() {
    return DRUG_PATH;
  }

  @JsonProperty("p7")
  public void setDRUG_PATH(String dRUG_PATH) {
    DRUG_PATH = dRUG_PATH;
  }

  public String getCON_FUNC_TYPE() {
    return CON_FUNC_TYPE;
  }

  @JsonProperty("p8")
  public void setCON_FUNC_TYPE(String cON_FUNC_TYPE) {
    CON_FUNC_TYPE = cON_FUNC_TYPE;
  }

  public String getBED_NO() {
    return BED_NO;
  }

  @JsonProperty("p9")
  public void setBED_NO(String bED_NO) {
    BED_NO = bED_NO;
  }

  public String getCURE_PATH() {
    return CURE_PATH;
  }

  @JsonProperty("p10")
  public void setCURE_PATH(String cURE_PATH) {
    CURE_PATH = cURE_PATH;
  }

  public Integer getTW_DRGS_CALCU() {
    return TW_DRGS_CALCU;
  }

  @JsonProperty("p11")
  public void setTW_DRGS_CALCU(Integer tW_DRGS_CALCU) {
    TW_DRGS_CALCU = tW_DRGS_CALCU;
  }

  public String getPART_ACCO_DATA() {
    return PART_ACCO_DATA;
  }

  @JsonProperty("p12")
  public void setPART_ACCO_DATA(String pART_ACCO_DATA) {
    PART_ACCO_DATA = pART_ACCO_DATA;
  }

  public String getDONATER() {
    return DONATER;
  }

  @JsonProperty("p13")
  public void setDONATER(String dONATER) {
    DONATER = dONATER;
  }

  public String getSTART_TIME() {
    return START_TIME;
  }

  @JsonProperty("p14")
  public void setSTART_TIME(String sTART_TIME) {
    START_TIME = sTART_TIME;
  }

  public String getEND_TIME() {
    return END_TIME;
  }

  @JsonProperty("p15")
  public void setEND_TIME(String eND_TIME) {
    END_TIME = eND_TIME;
  }

  public Integer getTOTAL_Q() {
    return TOTAL_Q;
  }

  @JsonProperty("p16")
  public void setTOTAL_Q(Integer tOTAL_Q) {
    TOTAL_Q = tOTAL_Q;
  }

  public Float getUNIT_P() {
    return UNIT_P;
  }

  @JsonProperty("p17")
  public void setUNIT_P(Float uNIT_P) {
    UNIT_P = uNIT_P;
  }

  public Integer getTOTAL_DOT() {
    return TOTAL_DOT;
  }

  @JsonProperty("p18")
  public void setTOTAL_DOT(Integer tOTAL_DOT) {
    TOTAL_DOT = tOTAL_DOT;
  }

  public String getPRE_NO() {
    return PRE_NO;
  }

  @JsonProperty("p19")
  public void setPRE_NO(String pRE_NO) {
    PRE_NO = pRE_NO;
  }

  public String getPRSN_ID() {
    return PRSN_ID;
  }

  @JsonProperty("p20")
  public void setPRSN_ID(String pRSN_ID) {
    PRSN_ID = pRSN_ID;
  }

  public String getIMG_SOURCE() {
    return IMG_SOURCE;
  }

  @JsonProperty("p21")
  public void setIMG_SOURCE(String iMG_SOURCE) {
    IMG_SOURCE = iMG_SOURCE;
  }

  public String getFUNC_TYPE() {
    return FUNC_TYPE;
  }

  @JsonProperty("p22")
  public void setFUNC_TYPE(String fUNC_TYPE) {
    FUNC_TYPE = fUNC_TYPE;
  }

  public String getOWN_EXP_MTR_NO() {
    return OWN_EXP_MTR_NO;
  }

  @JsonProperty("p23")
  public void setOWN_EXP_MTR_NO(String oWN_EXP_MTR_NO) {
    OWN_EXP_MTR_NO = oWN_EXP_MTR_NO;
  }

  public String getNON_LIST_MARK() {
    return NON_LIST_MARK;
  }

  @JsonProperty("p24")
  public void setNON_LIST_MARK(String nON_LIST_MARK) {
    NON_LIST_MARK = nON_LIST_MARK;
  }

  public String getNON_LIST_NAME() {
    return NON_LIST_NAME;
  }

  @JsonProperty("p25")
  public void setNON_LIST_NAME(String nON_LIST_NAME) {
    NON_LIST_NAME = nON_LIST_NAME;
  }

  public String getCOMM_HOSP_ID() {
    return COMM_HOSP_ID;
  }

  @JsonProperty("p26")
  public void setCOMM_HOSP_ID(String cOMM_HOSP_ID) {
    COMM_HOSP_ID = cOMM_HOSP_ID;
  }

  public String getDRUG_SERIAL_NO() {
    return DRUG_SERIAL_NO;
  }

  @JsonProperty("p27")
  public void setDRUG_SERIAL_NO(String dRUG_SERIAL_NO) {
    DRUG_SERIAL_NO = dRUG_SERIAL_NO;
  }

  public Long getID() {
    return ID;
  }

  public void setID(Long iD) {
    ID = iD;
  }

  public Long getIPD_ID() {
    return IPD_ID;
  }

  public void setIPD_ID(Long iPD_ID) {
    IPD_ID = iPD_ID;
  }
  
}
