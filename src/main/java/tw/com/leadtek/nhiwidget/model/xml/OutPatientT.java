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

/**
 * 門診總表
 * 
 * @author Ken Lai
 */
@JsonPropertyOrder({"t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9", "t10", "t11", "t12",
    "t13", "t14", "t15", "t16", "t17", "t18", "t19", "t20", "t21", "t22", "t23", "t24", "t25",
    "t26", "t27", "t28", "t29", "t30", "t31", "t32", "t33", "t34", "t35", "t36", "t37", "t38",
    "t39", "t40", "t41", "t42"})
// @Document(collection = "OP_t")
@Table(name = "op_t")
@Entity
public class OutPatientT {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @JsonIgnore
  private Long id;

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
   * 西醫一般案件申請件數
   */
  private Integer MEDIC_GEN_QTY;

  /**
   * 西醫一般案件申請點數
   */
  private Integer MEDIC_GEN_DOT;

  /**
   * 西醫專案案件申請件數
   */
  private Integer MEDIC_PRO_QTY;

  /**
   * 西醫專案案件申請點數
   */
  private Integer MEDIC_PRO_DOT;

  /**
   * 洗腎案件申請件數
   */
  private Integer MEDIC_DIA_QTY;

  /**
   * 洗腎案件申請點數
   */
  private Integer MEDIC_DIA_DOT;

  /**
   * 精神疾病社區復健申請件數
   */
  private Integer PSYC_QTY;

  /**
   * 精神疾病社區復健申請點數
   */
  private Integer PSYC_DOT;

  /**
   * 結核病申請件數
   */
  private Integer MEDIC_TUB_QTY;

  /**
   * 結核病申請點數
   */
  private Integer MEDIC_TUB_DOT;

  /**
   * 西醫申請件數小計
   */
  private Integer MEDIC_QTY;

  /**
   * 西醫申請點數小計
   */
  private Integer MEDIC_DOT;

  /**
   * 牙醫一般案件申請件數
   */
  private Integer DENT_GEN_QTY;

  /**
   * 牙醫一般案件申請點數
   */
  private Integer DENT_GEN_DOT;

  /**
   * 牙醫專案案件申請件數
   */
  private Integer DENT_PRO_QTY;

  /**
   * 牙醫專案案件申請點數
   */
  private Integer DENT_PRO_DOT;

  /**
   * 牙醫申請件數小計
   */
  private Integer DENT_QTY;

  /**
   * 牙醫申請點數小計
   */
  private Integer DENT_DOT;

  /**
   * 中醫一般案件申請件數
   */
  private Integer HERB_GEN_QTY;

  /**
   * 中醫一般案件申請點數
   */
  private Integer HERB_GEN_DOT;

  /**
   * 中醫專案案件申請件數
   */
  private Integer HERB_PRO_QTY;

  /**
   * 中醫專案案件申請點數
   */
  private Integer HERB_PRO_DOT;

  /**
   * 中醫申請件數小計
   */
  private Integer HERB_QTY;

  /**
   * 中醫申請點數小計
   */
  private Integer HERB_DOT;

  /**
   * 預防保健申請件數
   */
  private Integer PRE_CARE_QTY;

  /**
   * 預防保健申請點數
   */
  private Integer PRE_CARE_DOT;

  /**
   * 慢性病連續處方調劑申請件數
   */
  private Integer CHR_QTY;

  /**
   * 慢性病連續處方調劑申請點數
   */
  private Integer CHR_DOT;

  /**
   * 居家照護申請件數
   */
  private Integer HOME_CARE_QTY;

  /**
   * 居家照護申請點數
   */
  private Integer HOME_CARE_DOT;

  /**
   * 申請件數總計
   */
  private Integer APPL_QTY;

  /**
   * 申請點數總計
   */
  private Integer APPL_DOT;

  /**
   * 部分負擔件數總計
   */
  private Integer PART_QTY;

  /**
   * 部分負擔點數總計
   */
  private Integer PART_AMT;

  /**
   * 本次連線申報起日期
   */
  @Column(length = 7)
  private String APPL_START_DATE;

  /**
   * 本次連線申報迄日期
   */
  @Column(length = 7)
  private String APPL_END_DATE;

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

  public Integer getMEDIC_GEN_QTY() {
    return MEDIC_GEN_QTY;
  }

  @JsonProperty("t7")
  public void setMEDIC_GEN_QTY(Integer mEDIC_GEN_QTY) {
    MEDIC_GEN_QTY = mEDIC_GEN_QTY;
  }

  public Integer getMEDIC_GEN_DOT() {
    return MEDIC_GEN_DOT;
  }

  @JsonProperty("t8")
  public void setMEDIC_GEN_DOT(Integer mEDIC_GEN_DOT) {
    MEDIC_GEN_DOT = mEDIC_GEN_DOT;
  }

  public Integer getMEDIC_PRO_QTY() {
    return MEDIC_PRO_QTY;
  }

  @JsonProperty("t9")
  public void setMEDIC_PRO_QTY(Integer mEDIC_PRO_QTY) {
    MEDIC_PRO_QTY = mEDIC_PRO_QTY;
  }

  public Integer getMEDIC_PRO_DOT() {
    return MEDIC_PRO_DOT;
  }

  @JsonProperty("t10")
  public void setMEDIC_PRO_DOT(Integer mEDIC_PRO_DOT) {
    MEDIC_PRO_DOT = mEDIC_PRO_DOT;
  }

  public Integer getMEDIC_DIA_QTY() {
    return MEDIC_DIA_QTY;
  }

  @JsonProperty("t11")
  public void setMEDIC_DIA_QTY(Integer mEDIC_DIA_QTY) {
    MEDIC_DIA_QTY = mEDIC_DIA_QTY;
  }

  public Integer getMEDIC_DIA_DOT() {
    return MEDIC_DIA_DOT;
  }

  @JsonProperty("t12")
  public void setMEDIC_DIA_DOT(Integer mEDIC_DIA_DOT) {
    MEDIC_DIA_DOT = mEDIC_DIA_DOT;
  }

  public Integer getPSYC_QTY() {
    return PSYC_QTY;
  }

  @JsonProperty("t13")
  public void setPSYC_QTY(Integer pSYC_QTY) {
    PSYC_QTY = pSYC_QTY;
  }

  public Integer getPSYC_DOT() {
    return PSYC_DOT;
  }

  @JsonProperty("t14")
  public void setPSYC_DOT(Integer pSYC_DOT) {
    PSYC_DOT = pSYC_DOT;
  }

  public Integer getMEDIC_TUB_QTY() {
    return MEDIC_TUB_QTY;
  }

  @JsonProperty("t15")
  public void setMEDIC_TUB_QTY(Integer mEDIC_TUB_QTY) {
    MEDIC_TUB_QTY = mEDIC_TUB_QTY;
  }

  public Integer getMEDIC_TUB_DOT() {
    return MEDIC_TUB_DOT;
  }

  @JsonProperty("t16")
  public void setMEDIC_TUB_DOT(Integer mEDIC_TUB_DOT) {
    MEDIC_TUB_DOT = mEDIC_TUB_DOT;
  }

  public Integer getMEDIC_QTY() {
    return MEDIC_QTY;
  }

  @JsonProperty("t17")
  public void setMEDIC_QTY(Integer mEDIC_QTY) {
    MEDIC_QTY = mEDIC_QTY;
  }

  public Integer getMEDIC_DOT() {
    return MEDIC_DOT;
  }

  @JsonProperty("t18")
  public void setMEDIC_DOT(Integer mEDIC_DOT) {
    MEDIC_DOT = mEDIC_DOT;
  }

  public Integer getDENT_GEN_QTY() {
    return DENT_GEN_QTY;
  }

  @JsonProperty("t19")
  public void setDENT_GEN_QTY(Integer dENT_GEN_QTY) {
    DENT_GEN_QTY = dENT_GEN_QTY;
  }

  public Integer getDENT_GEN_DOT() {
    return DENT_GEN_DOT;
  }

  @JsonProperty("t20")
  public void setDENT_GEN_DOT(Integer dENT_GEN_DOT) {
    DENT_GEN_DOT = dENT_GEN_DOT;
  }

  public Integer getDENT_PRO_QTY() {
    return DENT_PRO_QTY;
  }

  @JsonProperty("t21")
  public void setDENT_PRO_QTY(Integer dENT_PRO_QTY) {
    DENT_PRO_QTY = dENT_PRO_QTY;
  }

  public Integer getDENT_PRO_DOT() {
    return DENT_PRO_DOT;
  }

  @JsonProperty("t22")
  public void setDENT_PRO_DOT(Integer dENT_PRO_DOT) {
    DENT_PRO_DOT = dENT_PRO_DOT;
  }

  public Integer getDENT_QTY() {
    return DENT_QTY;
  }

  @JsonProperty("t23")
  public void setDENT_QTY(Integer dENT_QTY) {
    DENT_QTY = dENT_QTY;
  }

  public Integer getDENT_DOT() {
    return DENT_DOT;
  }

  @JsonProperty("t24")
  public void setDENT_DOT(Integer dENT_DOT) {
    DENT_DOT = dENT_DOT;
  }

  public Integer getHERB_GEN_QTY() {
    return HERB_GEN_QTY;
  }

  @JsonProperty("t25")
  public void setHERB_GEN_QTY(Integer hERB_GEN_QTY) {
    HERB_GEN_QTY = hERB_GEN_QTY;
  }

  public Integer getHERB_GEN_DOT() {
    return HERB_GEN_DOT;
  }

  @JsonProperty("t26")
  public void setHERB_GEN_DOT(Integer hERB_GEN_DOT) {
    HERB_GEN_DOT = hERB_GEN_DOT;
  }

  public Integer getHERB_PRO_QTY() {
    return HERB_PRO_QTY;
  }

  @JsonProperty("t27")
  public void setHERB_PRO_QTY(Integer hERB_PRO_QTY) {
    HERB_PRO_QTY = hERB_PRO_QTY;
  }

  public Integer getHERB_PRO_DOT() {
    return HERB_PRO_DOT;
  }

  @JsonProperty("t28")
  public void setHERB_PRO_DOT(Integer hERB_PRO_DOT) {
    HERB_PRO_DOT = hERB_PRO_DOT;
  }

  public Integer getHERB_QTY() {
    return HERB_QTY;
  }

  @JsonProperty("t29")
  public void setHERB_QTY(Integer hERB_QTY) {
    HERB_QTY = hERB_QTY;
  }

  public Integer getHERB_DOT() {
    return HERB_DOT;
  }

  @JsonProperty("t30")
  public void setHERB_DOT(Integer hERB_DOT) {
    HERB_DOT = hERB_DOT;
  }

  public Integer getPRE_CARE_QTY() {
    return PRE_CARE_QTY;
  }

  @JsonProperty("t31")
  public void setPRE_CARE_QTY(Integer pRE_CARE_QTY) {
    PRE_CARE_QTY = pRE_CARE_QTY;
  }

  public Integer getPRE_CARE_DOT() {
    return PRE_CARE_DOT;
  }

  @JsonProperty("t32")
  public void setPRE_CARE_DOT(Integer pRE_CARE_DOT) {
    PRE_CARE_DOT = pRE_CARE_DOT;
  }

  public Integer getCHR_QTY() {
    return CHR_QTY;
  }

  @JsonProperty("t33")
  public void setCHR_QTY(Integer cHR_QTY) {
    CHR_QTY = cHR_QTY;
  }

  public Integer getCHR_DOT() {
    return CHR_DOT;
  }

  @JsonProperty("t34")
  public void setCHR_DOT(Integer cHR_DOT) {
    CHR_DOT = cHR_DOT;
  }

  public Integer getHOME_CARE_QTY() {
    return HOME_CARE_QTY;
  }

  @JsonProperty("t35")
  public void setHOME_CARE_QTY(Integer hOME_CARE_QTY) {
    HOME_CARE_QTY = hOME_CARE_QTY;
  }

  public Integer getHOME_CARE_DOT() {
    return HOME_CARE_DOT;
  }

  @JsonProperty("t36")
  public void setHOME_CARE_DOT(Integer hOME_CARE_DOT) {
    HOME_CARE_DOT = hOME_CARE_DOT;
  }

  public Integer getAPPL_QTY() {
    return APPL_QTY;
  }

  @JsonProperty("t37")
  public void setAPPL_QTY(Integer aPPL_QTY) {
    APPL_QTY = aPPL_QTY;
  }

  public Integer getAPPL_DOT() {
    return APPL_DOT;
  }

  @JsonProperty("t38")
  public void setAPPL_DOT(Integer aPPL_DOT) {
    APPL_DOT = aPPL_DOT;
  }

  public Integer getPART_QTY() {
    return PART_QTY;
  }

  @JsonProperty("t39")
  public void setPART_QTY(Integer pART_QTY) {
    PART_QTY = pART_QTY;
  }

  public Integer getPART_AMT() {
    return PART_AMT;
  }

  @JsonProperty("t40")
  public void setPART_AMT(Integer pART_AMT) {
    PART_AMT = pART_AMT;
  }

  public String getAPPL_START_DATE() {
    return APPL_START_DATE;
  }

  @JsonProperty("t41")
  public void setAPPL_START_DATE(String aPPL_START_DATE) {
    APPL_START_DATE = aPPL_START_DATE;
  }

  public String getAPPL_END_DATE() {
    return APPL_END_DATE;
  }

  @JsonProperty("t42")
  public void setAPPL_END_DATE(String aPPL_END_DATE) {
    APPL_END_DATE = aPPL_END_DATE;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

}
