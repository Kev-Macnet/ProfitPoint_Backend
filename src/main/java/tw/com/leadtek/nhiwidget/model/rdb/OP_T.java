/**
 * Created on 2021/02/17 by GenerateSqlByClass().
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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@Table(name = "OP_T")
@Entity
public class OP_T {

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
   * 西醫一般案件申請件數
   */
  @Column(name = "MEDIC_GEN_QTY")
  @JsonProperty("MEDIC_GEN_QTY")
  @JacksonXmlProperty(localName = "t7")
  private Integer medicGenQty;

  /**
   * 西醫一般案件申請點數
   */
  @Column(name = "MEDIC_GEN_DOT")
  @JsonProperty("MEDIC_GEN_DOT")
  @JacksonXmlProperty(localName = "t8")
  private Integer medicGenDot;

  /**
   * 西醫專案案件申請件數
   */
  @Column(name = "MEDIC_PRO_QTY")
  @JsonProperty("MEDIC_PRO_QTY")
  @JacksonXmlProperty(localName = "t9")
  private Integer medicProQty;

  /**
   * 西醫專案案件申請點數
   */
  @Column(name = "MEDIC_PRO_DOT")
  @JsonProperty("MEDIC_PRO_DOT")
  @JacksonXmlProperty(localName = "t10")
  private Integer medicProDot;

  /**
   * 洗腎案件申請件數
   */
  @Column(name = "MEDIC_DIA_QTY")
  @JsonProperty("MEDIC_DIA_QTY")
  @JacksonXmlProperty(localName = "t11")
  private Integer medicDiaQty;

  /**
   * 洗腎案件申請點數
   */
  @Column(name = "MEDIC_DIA_DOT")
  @JsonProperty("MEDIC_DIA_DOT")
  @JacksonXmlProperty(localName = "t12")
  private Integer medicDiaDot;

  /**
   * 精神疾病社區復健申請件數
   */
  @Column(name = "PSYC_QTY")
  @JsonProperty("PSYC_QTY")
  @JacksonXmlProperty(localName = "t13")
  private Integer psycQty;

  /**
   * 精神疾病社區復健申請點數
   */
  @Column(name = "PSYC_DOT")
  @JsonProperty("PSYC_DOT")
  @JacksonXmlProperty(localName = "t14")
  private Integer psycDot;

  /**
   * 結核病申請件數
   */
  @Column(name = "MEDIC_TUB_QTY")
  @JsonProperty("MEDIC_TUB_QTY")
  @JacksonXmlProperty(localName = "t15")
  private Integer medicTubQty;

  /**
   * 結核病申請點數
   */
  @Column(name = "MEDIC_TUB_DOT")
  @JsonProperty("MEDIC_TUB_DOT")
  @JacksonXmlProperty(localName = "t16")
  private Integer medicTubDot;

  /**
   * 西醫申請件數小計
   */
  @Column(name = "MEDIC_QTY")
  @JsonProperty("MEDIC_QTY")
  @JacksonXmlProperty(localName = "t17")
  private Integer medicQty;

  /**
   * 西醫申請點數小計
   */
  @Column(name = "MEDIC_DOT")
  @JsonProperty("MEDIC_DOT")
  @JacksonXmlProperty(localName = "t18")
  private Integer medicDot;

  /**
   * 牙醫一般案件申請件數
   */
  @Column(name = "DENT_GEN_QTY")
  @JsonProperty("DENT_GEN_QTY")
  @JacksonXmlProperty(localName = "t19")
  private Integer dentGenQty;

  /**
   * 牙醫一般案件申請點數
   */
  @Column(name = "DENT_GEN_DOT")
  @JsonProperty("DENT_GEN_DOT")
  @JacksonXmlProperty(localName = "t20")
  private Integer dentGenDot;

  /**
   * 牙醫專案案件申請件數
   */
  @Column(name = "DENT_PRO_QTY")
  @JsonProperty("DENT_PRO_QTY")
  @JacksonXmlProperty(localName = "t21")
  private Integer dentProQty;

  /**
   * 牙醫專案案件申請點數
   */
  @Column(name = "DENT_PRO_DOT")
  @JsonProperty("DENT_PRO_DOT")
  @JacksonXmlProperty(localName = "t22")
  private Integer dentProDot;

  /**
   * 牙醫申請件數小計
   */
  @Column(name = "DENT_QTY")
  @JsonProperty("DENT_QTY")
  @JacksonXmlProperty(localName = "t23")
  private Integer dentQty;

  /**
   * 牙醫申請點數小計
   */
  @Column(name = "DENT_DOT")
  @JsonProperty("DENT_DOT")
  @JacksonXmlProperty(localName = "t24")
  private Integer dentDot;

  /**
   * 中醫一般案件申請件數
   */
  @Column(name = "HERB_GEN_QTY")
  @JsonProperty("HERB_GEN_QTY")
  @JacksonXmlProperty(localName = "t25")
  private Integer herbGenQty;

  /**
   * 中醫一般案件申請點數
   */
  @Column(name = "HERB_GEN_DOT")
  @JsonProperty("HERB_GEN_DOT")
  @JacksonXmlProperty(localName = "t26")
  private Integer herbGenDot;

  /**
   * 中醫專案案件申請件數
   */
  @Column(name = "HERB_PRO_QTY")
  @JsonProperty("HERB_PRO_QTY")
  @JacksonXmlProperty(localName = "t27")
  private Integer herbProQty;

  /**
   * 中醫專案案件申請點數
   */
  @Column(name = "HERB_PRO_DOT")
  @JsonProperty("HERB_PRO_DOT")
  @JacksonXmlProperty(localName = "t28")
  private Integer herbProDot;

  /**
   * 中醫申請件數小計
   */
  @Column(name = "HERB_QTY")
  @JsonProperty("HERB_QTY")
  @JacksonXmlProperty(localName = "t29")
  private Integer herbQty;

  /**
   * 中醫申請點數小計
   */
  @Column(name = "HERB_DOT")
  @JsonProperty("HERB_DOT")
  @JacksonXmlProperty(localName = "t30")
  private Integer herbDot;

  /**
   * 預防保健申請件數
   */
  @Column(name = "PRE_CARE_QTY")
  @JsonProperty("PRE_CARE_QTY")
  @JacksonXmlProperty(localName = "t31")
  private Integer preCareQty;

  /**
   * 預防保健申請點數
   */
  @Column(name = "PRE_CARE_DOT")
  @JsonProperty("PRE_CARE_DOT")
  @JacksonXmlProperty(localName = "t32")
  private Integer preCareDot;

  /**
   * 慢性病連續處方調劑申請件數
   */
  @Column(name = "CHR_QTY")
  @JsonProperty("CHR_QTY")
  @JacksonXmlProperty(localName = "t33")
  private Integer chrQty;

  /**
   * 慢性病連續處方調劑申請點數
   */
  @Column(name = "CHR_DOT")
  @JsonProperty("CHR_DOT")
  @JacksonXmlProperty(localName = "t34")
  private Integer chrDot;

  /**
   * 居家照護申請件數
   */
  @Column(name = "HOME_CARE_QTY")
  @JsonProperty("HOME_CARE_QTY")
  @JacksonXmlProperty(localName = "t35")
  private Integer homeCareQty;

  /**
   * 居家照護申請點數
   */
  @Column(name = "HOME_CARE_DOT")
  @JsonProperty("HOME_CARE_DOT")
  @JacksonXmlProperty(localName = "t36")
  private Integer homeCareDot;

  /**
   * 申請件數總計
   */
  @Column(name = "APPL_QTY")
  @JsonProperty("APPL_QTY")
  @JacksonXmlProperty(localName = "t37")
  private Integer applQty;

  /**
   * 申請點數總計
   */
  @Column(name = "APPL_DOT")
  @JsonProperty("APPL_DOT")
  @JacksonXmlProperty(localName = "t38")
  private Integer applDot;

  /**
   * 部分負擔件數總計
   */
  @Column(name = "PART_QTY")
  @JsonProperty("PART_QTY")
  @JacksonXmlProperty(localName = "t39")
  private Integer partQty;

  /**
   * 部分負擔點數總計
   */
  @Column(name = "PART_AMT")
  @JsonProperty("PART_AMT")
  @JacksonXmlProperty(localName = "t40")
  private Integer partAmt;

  /**
   * 本次連線申報起日期
   */
  @Column(name = "APPL_START_DATE")
  @JsonProperty("APPL_START_DATE")
  @JacksonXmlProperty(localName = "t41")
  private String applStartDate;

  /**
   * 本次連線申報迄日期
   */
  @Column(name = "APPL_END_DATE")
  @JsonProperty("APPL_END_DATE")
  @JacksonXmlProperty(localName = "t42")
  private String applEndDate;

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
   * <t7> 西醫一般案件申請件數
   */
  public Integer getMedicGenQty() {
    return medicGenQty;
  }

  /**
   * <t7> 西醫一般案件申請件數
   */
  public void setMedicGenQty(Integer MEDIC_GEN_QTY) {
    medicGenQty = MEDIC_GEN_QTY;
  }

  /**
   * <t8> 西醫一般案件申請點數
   */
  public Integer getMedicGenDot() {
    return medicGenDot;
  }

  /**
   * <t8> 西醫一般案件申請點數
   */
  public void setMedicGenDot(Integer MEDIC_GEN_DOT) {
    medicGenDot = MEDIC_GEN_DOT;
  }

  /**
   * <t9> 西醫專案案件申請件數
   */
  public Integer getMedicProQty() {
    return medicProQty;
  }

  /**
   * <t9> 西醫專案案件申請件數
   */
  public void setMedicProQty(Integer MEDIC_PRO_QTY) {
    medicProQty = MEDIC_PRO_QTY;
  }

  /**
   * <t10> 西醫專案案件申請點數
   */
  public Integer getMedicProDot() {
    return medicProDot;
  }

  /**
   * <t10> 西醫專案案件申請點數
   */
  public void setMedicProDot(Integer MEDIC_PRO_DOT) {
    medicProDot = MEDIC_PRO_DOT;
  }

  /**
   * <t11> 洗腎案件申請件數
   */
  public Integer getMedicDiaQty() {
    return medicDiaQty;
  }

  /**
   * <t11> 洗腎案件申請件數
   */
  public void setMedicDiaQty(Integer MEDIC_DIA_QTY) {
    medicDiaQty = MEDIC_DIA_QTY;
  }

  /**
   * <t12> 洗腎案件申請點數
   */
  public Integer getMedicDiaDot() {
    return medicDiaDot;
  }

  /**
   * <t12> 洗腎案件申請點數
   */
  public void setMedicDiaDot(Integer MEDIC_DIA_DOT) {
    medicDiaDot = MEDIC_DIA_DOT;
  }

  /**
   * <t13> 精神疾病社區復健申請件數
   */
  public Integer getPsycQty() {
    return psycQty;
  }

  /**
   * <t13> 精神疾病社區復健申請件數
   */
  public void setPsycQty(Integer PSYC_QTY) {
    psycQty = PSYC_QTY;
  }

  /**
   * <t14> 精神疾病社區復健申請點數
   */
  public Integer getPsycDot() {
    return psycDot;
  }

  /**
   * <t14> 精神疾病社區復健申請點數
   */
  public void setPsycDot(Integer PSYC_DOT) {
    psycDot = PSYC_DOT;
  }

  /**
   * <t15> 結核病申請件數
   */
  public Integer getMedicTubQty() {
    return medicTubQty;
  }

  /**
   * <t15> 結核病申請件數
   */
  public void setMedicTubQty(Integer MEDIC_TUB_QTY) {
    medicTubQty = MEDIC_TUB_QTY;
  }

  /**
   * <t16> 結核病申請點數
   */
  public Integer getMedicTubDot() {
    return medicTubDot;
  }

  /**
   * <t16> 結核病申請點數
   */
  public void setMedicTubDot(Integer MEDIC_TUB_DOT) {
    medicTubDot = MEDIC_TUB_DOT;
  }

  /**
   * <t17> 西醫申請件數小計
   */
  public Integer getMedicQty() {
    return medicQty;
  }

  /**
   * <t17> 西醫申請件數小計
   */
  public void setMedicQty(Integer MEDIC_QTY) {
    medicQty = MEDIC_QTY;
  }

  /**
   * <t18> 西醫申請點數小計
   */
  public Integer getMedicDot() {
    return medicDot;
  }

  /**
   * <t18> 西醫申請點數小計
   */
  public void setMedicDot(Integer MEDIC_DOT) {
    medicDot = MEDIC_DOT;
  }

  /**
   * <t19> 牙醫一般案件申請件數
   */
  public Integer getDentGenQty() {
    return dentGenQty;
  }

  /**
   * <t19> 牙醫一般案件申請件數
   */
  public void setDentGenQty(Integer DENT_GEN_QTY) {
    dentGenQty = DENT_GEN_QTY;
  }

  /**
   * <t20> 牙醫一般案件申請點數
   */
  public Integer getDentGenDot() {
    return dentGenDot;
  }

  /**
   * <t20> 牙醫一般案件申請點數
   */
  public void setDentGenDot(Integer DENT_GEN_DOT) {
    dentGenDot = DENT_GEN_DOT;
  }

  /**
   * <t21> 牙醫專案案件申請件數
   */
  public Integer getDentProQty() {
    return dentProQty;
  }

  /**
   * <t21> 牙醫專案案件申請件數
   */
  public void setDentProQty(Integer DENT_PRO_QTY) {
    dentProQty = DENT_PRO_QTY;
  }

  /**
   * <t22> 牙醫專案案件申請點數
   */
  public Integer getDentProDot() {
    return dentProDot;
  }

  /**
   * <t22> 牙醫專案案件申請點數
   */
  public void setDentProDot(Integer DENT_PRO_DOT) {
    dentProDot = DENT_PRO_DOT;
  }

  /**
   * <t23> 牙醫申請件數小計
   */
  public Integer getDentQty() {
    return dentQty;
  }

  /**
   * <t23> 牙醫申請件數小計
   */
  public void setDentQty(Integer DENT_QTY) {
    dentQty = DENT_QTY;
  }

  /**
   * <t24> 牙醫申請點數小計
   */
  public Integer getDentDot() {
    return dentDot;
  }

  /**
   * <t24> 牙醫申請點數小計
   */
  public void setDentDot(Integer DENT_DOT) {
    dentDot = DENT_DOT;
  }

  /**
   * <t25> 中醫一般案件申請件數
   */
  public Integer getHerbGenQty() {
    return herbGenQty;
  }

  /**
   * <t25> 中醫一般案件申請件數
   */
  public void setHerbGenQty(Integer HERB_GEN_QTY) {
    herbGenQty = HERB_GEN_QTY;
  }

  /**
   * <t26> 中醫一般案件申請點數
   */
  public Integer getHerbGenDot() {
    return herbGenDot;
  }

  /**
   * <t26> 中醫一般案件申請點數
   */
  public void setHerbGenDot(Integer HERB_GEN_DOT) {
    herbGenDot = HERB_GEN_DOT;
  }

  /**
   * <t27> 中醫專案案件申請件數
   */
  public Integer getHerbProQty() {
    return herbProQty;
  }

  /**
   * <t27> 中醫專案案件申請件數
   */
  public void setHerbProQty(Integer HERB_PRO_QTY) {
    herbProQty = HERB_PRO_QTY;
  }

  /**
   * <t28> 中醫專案案件申請點數
   */
  public Integer getHerbProDot() {
    return herbProDot;
  }

  /**
   * <t28> 中醫專案案件申請點數
   */
  public void setHerbProDot(Integer HERB_PRO_DOT) {
    herbProDot = HERB_PRO_DOT;
  }

  /**
   * <t29> 中醫申請件數小計
   */
  public Integer getHerbQty() {
    return herbQty;
  }

  /**
   * <t29> 中醫申請件數小計
   */
  public void setHerbQty(Integer HERB_QTY) {
    herbQty = HERB_QTY;
  }

  /**
   * <t30> 中醫申請點數小計
   */
  public Integer getHerbDot() {
    return herbDot;
  }

  /**
   * <t30> 中醫申請點數小計
   */
  public void setHerbDot(Integer HERB_DOT) {
    herbDot = HERB_DOT;
  }

  /**
   * <t31> 預防保健申請件數
   */
  public Integer getPreCareQty() {
    return preCareQty;
  }

  /**
   * <t31> 預防保健申請件數
   */
  public void setPreCareQty(Integer PRE_CARE_QTY) {
    preCareQty = PRE_CARE_QTY;
  }

  /**
   * <t32> 預防保健申請點數
   */
  public Integer getPreCareDot() {
    return preCareDot;
  }

  /**
   * <t32> 預防保健申請點數
   */
  public void setPreCareDot(Integer PRE_CARE_DOT) {
    preCareDot = PRE_CARE_DOT;
  }

  /**
   * <t33> 慢性病連續處方調劑申請件數
   */
  public Integer getChrQty() {
    return chrQty;
  }

  /**
   * <t33> 慢性病連續處方調劑申請件數
   */
  public void setChrQty(Integer CHR_QTY) {
    chrQty = CHR_QTY;
  }

  /**
   * <t34> 慢性病連續處方調劑申請點數
   */
  public Integer getChrDot() {
    return chrDot;
  }

  /**
   * <t34> 慢性病連續處方調劑申請點數
   */
  public void setChrDot(Integer CHR_DOT) {
    chrDot = CHR_DOT;
  }

  /**
   * <t35> 居家照護申請件數
   */
  public Integer getHomeCareQty() {
    return homeCareQty;
  }

  /**
   * <t35> 居家照護申請件數
   */
  public void setHomeCareQty(Integer HOME_CARE_QTY) {
    homeCareQty = HOME_CARE_QTY;
  }

  /**
   * <t36> 居家照護申請點數
   */
  public Integer getHomeCareDot() {
    return homeCareDot;
  }

  /**
   * <t36> 居家照護申請點數
   */
  public void setHomeCareDot(Integer HOME_CARE_DOT) {
    homeCareDot = HOME_CARE_DOT;
  }

  /**
   * <t37> 申請件數總計
   */
  public Integer getApplQty() {
    return applQty;
  }

  /**
   * <t37> 申請件數總計
   */
  public void setApplQty(Integer APPL_QTY) {
    applQty = APPL_QTY;
  }

  /**
   * <t38> 申請點數總計
   */
  public Integer getApplDot() {
    return applDot;
  }

  /**
   * <t38> 申請點數總計
   */
  public void setApplDot(Integer APPL_DOT) {
    applDot = APPL_DOT;
  }

  /**
   * <t39> 部分負擔件數總計
   */
  public Integer getPartQty() {
    return partQty;
  }

  /**
   * <t39> 部分負擔件數總計
   */
  public void setPartQty(Integer PART_QTY) {
    partQty = PART_QTY;
  }

  /**
   * <t40> 部分負擔點數總計
   */
  public Integer getPartAmt() {
    return partAmt;
  }

  /**
   * <t40> 部分負擔點數總計
   */
  public void setPartAmt(Integer PART_AMT) {
    partAmt = PART_AMT;
  }

  /**
   * <t41> 本次連線申報起日期
   */
  public String getApplStartDate() {
    return applStartDate;
  }

  /**
   * <t41> 本次連線申報起日期
   */
  public void setApplStartDate(String APPL_START_DATE) {
    applStartDate = APPL_START_DATE;
  }

  /**
   * <t42> 本次連線申報迄日期
   */
  public String getApplEndDate() {
    return applEndDate;
  }

  /**
   * <t42> 本次連線申報迄日期
   */
  public void setApplEndDate(String APPL_END_DATE) {
    applEndDate = APPL_END_DATE;
  }

  /**
   * 更新時間
   */
  public Date getUpdateAt() {
    return updateAt;
  }

  /**
   * 更新時間
   */
  public void setUpdateAt(Date UPDATE_AT) {
    updateAt = UPDATE_AT;
  }

}