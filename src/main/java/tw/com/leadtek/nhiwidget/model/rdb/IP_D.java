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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

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
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "IP_D")
@Entity
public class IP_D {

  /**
   * 序號
   */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenIP_D")
  @SequenceGenerator(name = "seqGenIP_D", sequenceName = "SEQ_IP_D", allocationSize = 1000)
  //@SequenceGenerator(name = "seqGenIP_D", allocationSize = 100)
  @Basic(optional = false)
  @Column(name = "ID")
  @JsonIgnore
  private Long id;

  /**
   * 序號
   */
  @Column(name = "IPT_ID", nullable = false)
  @JsonIgnore
  private Long iptId;

  @Column(name = "CASE_TYPE")
  @JsonIgnore
  private String caseType;

  @Column(name = "SEQ_NO")
  @JsonIgnore
  private Integer seqNo;

  /**
   * 身分證統一編號
   */
  @Column(name = "ROC_ID")
  @JsonProperty("ROC_ID")
  @JacksonXmlProperty(localName = "d3")
  private String rocId;

  /**
   * 部分負擔代號
   */
  @Column(name = "PART_NO")
  @JsonProperty("PART_NO")
  @JacksonXmlProperty(localName = "d4")
  private String partNo;

  /**
   * 補報原因註記
   */
  @Column(name = "APPL_CAUSE_MARK")
  @JsonProperty("APPL_CAUSE_MARK")
  @JacksonXmlProperty(localName = "d5")
  private String applCauseMark;

  /**
   * 出生年月日(民國年)
   */
  @Column(name = "ID_BIRTH_YMD")
  @JsonProperty("ID_BIRTH_YMD")
  @JacksonXmlProperty(localName = "d6")
  private String idBirthYmd;

  /**
   * 給付類別
   */
  @Column(name = "PAY_TYPE")
  @JsonProperty("PAY_TYPE")
  @JacksonXmlProperty(localName = "d7")
  private String payType;
  
  /**
   * 給付類別
   */
  @JsonProperty("d8")
  @JacksonXmlProperty(localName = "d8")
  @Transient
  private String d8;

  /**
   * 就醫科別
   */
  @Column(name = "FUNC_TYPE")
  @JsonProperty("FUNC_TYPE")
  @JacksonXmlProperty(localName = "d9")
  private String funcType;

  /**
   * 入院年月日
   */
  @Column(name = "IN_DATE")
  @JsonProperty("IN_DATE")
  @JacksonXmlProperty(localName = "d10")
  private String inDate;

  /**
   * 出院年月日
   */
  @Column(name = "OUT_DATE")
  @JsonProperty("OUT_DATE")
  @JacksonXmlProperty(localName = "d11")
  private String outDate;

  /**
   * 申報期間-起
   */
  @Column(name = "APPL_START_DATE")
  @JsonProperty("APPL_START_DATE")
  @JacksonXmlProperty(localName = "d12")
  private String applStartDate;

  /**
   * 申報期間-迄
   */
  @Column(name = "APPL_END_DATE")
  @JsonProperty("APPL_END_DATE")
  @JacksonXmlProperty(localName = "d13")
  private String applEndDate;

  /**
   * 急性病床天數
   */
  @Column(name = "E_BED_DAY")
  @JsonProperty("E_BED_DAY")
  @JacksonXmlProperty(localName = "d14")
  private Integer ebedDay;

  /**
   * 慢性病床天數
   */
  @Column(name = "S_BED_DAY")
  @JsonProperty("S_BED_DAY")
  @JacksonXmlProperty(localName = "d15")
  private Integer sbedDay;

  /**
   * 病患來源 
   */
  @Column(name = "PATIENT_SOURCE")
  @JsonProperty("PATIENT_SOURCE")
  @JacksonXmlProperty(localName = "d16")
  private String patientSource;

  /**
   * 就醫序號
   */
  @Column(name = "CARD_SEQ_NO")
  @JsonProperty("CARD_SEQ_NO")
  @JacksonXmlProperty(localName = "d17")
  private String cardSeqNo;

  /**
   * TW-DRG碼
   */
  @Column(name = "TW_DRG_CODE")
  @JsonProperty("TW_DRG_CODE")
  @JacksonXmlProperty(localName = "d18")
  private String twDrgCode;

  /**
   * TW-DRG支付型態
   */
  @Column(name = "TW_DRG_PAY_TYPE")
  @JsonProperty("TW_DRG_PAY_TYPE")
  @JacksonXmlProperty(localName = "d19")
  private String twDrgPayType;

  /**
   * 主治醫師代碼
   */
  @Column(name = "PRSN_ID")
  @JsonProperty("PRSN_ID")
  @JacksonXmlProperty(localName = "d20")
  private String prsnId;

  /**
   * DRGS碼
   */
  @Column(name = "CASE_DRG_CODE")
  @JsonProperty("CASE_DRG_CODE")
  @JacksonXmlProperty(localName = "d21")
  private String caseDrgCode;

  /**
   * 轉歸代碼
   */
  @Column(name = "TRAN_CODE")
  @JsonProperty("TRAN_CODE")
  @JacksonXmlProperty(localName = "d24")
  private String tranCode;

  /**
   * 主診斷
   */
  @Column(name = "ICD_CM_1")
  @JsonProperty("ICD_CM_1")
  @JacksonXmlProperty(localName = "d25")
  private String icdCm1;

  /**
   * 次診斷代碼(一)
   */
  @Column(name = "ICD_CM_2")
  @JsonProperty("ICD_CM_2")
  @JacksonXmlProperty(localName = "d26")
  private String icdCm2;

  /**
   * 次診斷代碼(二)
   */
  @Column(name = "ICD_CM_3")
  @JsonProperty("ICD_CM_3")
  @JacksonXmlProperty(localName = "d27")
  private String icdCm3;

  /**
   * 次診斷代碼(三)
   */
  @Column(name = "ICD_CM_4")
  @JsonProperty("ICD_CM_4")
  @JacksonXmlProperty(localName = "d28")
  private String icdCm4;

  /**
   * 次診斷代碼(四)
   */
  @Column(name = "ICD_CM_5")
  @JsonProperty("ICD_CM_5")
  @JacksonXmlProperty(localName = "d29")
  private String icdCm5;

  /**
   * 次診斷代碼(五)
   */
  @Column(name = "ICD_CM_6")
  @JsonProperty("ICD_CM_6")
  @JacksonXmlProperty(localName = "d30")
  private String icdCm6;

  /**
   * 次診斷代碼(六)
   */
  @Column(name = "ICD_CM_7")
  @JsonProperty("ICD_CM_7")
  @JacksonXmlProperty(localName = "d31")
  private String icdCm7;

  /**
   * 次診斷代碼(七)
   */
  @Column(name = "ICD_CM_8")
  @JsonProperty("ICD_CM_8")
  @JacksonXmlProperty(localName = "d32")
  private String icdCm8;

  /**
   * 次診斷代碼(八)
   */
  @Column(name = "ICD_CM_9")
  @JsonProperty("ICD_CM_9")
  @JacksonXmlProperty(localName = "d33")
  private String icdCm9;

  /**
   * 次診斷代碼(九)
   */
  @Column(name = "ICD_CM_10")
  @JsonProperty("ICD_CM_10")
  @JacksonXmlProperty(localName = "d34")
  private String icdCm10;

  /**
   * 次診斷代碼(十)
   */
  @Column(name = "ICD_CM_11")
  @JsonProperty("ICD_CM_11")
  @JacksonXmlProperty(localName = "d35")
  private String icdCm11;

  /**
   * 次診斷代碼(十一)
   */
  @Column(name = "ICD_CM_12")
  @JsonProperty("ICD_CM_12")
  @JacksonXmlProperty(localName = "d36")
  private String icdCm12;

  /**
   * 次診斷代碼(十二)
   */
  @Column(name = "ICD_CM_13")
  @JsonProperty("ICD_CM_13")
  @JacksonXmlProperty(localName = "d37")
  private String icdCm13;

  /**
   * 次診斷代碼(十三)
   */
  @Column(name = "ICD_CM_14")
  @JsonProperty("ICD_CM_14")
  @JacksonXmlProperty(localName = "d38")
  private String icdCm14;

  /**
   * 次診斷代碼(十四)
   */
  @Column(name = "ICD_CM_15")
  @JsonProperty("ICD_CM_15")
  @JacksonXmlProperty(localName = "d39")
  private String icdCm15;

  /**
   * 次診斷代碼(十五)
   */
  @Column(name = "ICD_CM_16")
  @JsonProperty("ICD_CM_16")
  @JacksonXmlProperty(localName = "d40")
  private String icdCm16;

  /**
   * 次診斷代碼(十六)
   */
  @Column(name = "ICD_CM_17")
  @JsonProperty("ICD_CM_17")
  @JacksonXmlProperty(localName = "d41")
  private String icdCm17;

  /**
   * 次診斷代碼(十七)
   */
  @Column(name = "ICD_CM_18")
  @JsonProperty("ICD_CM_18")
  @JacksonXmlProperty(localName = "d42")
  private String icdCm18;

  /**
   * 次診斷代碼(十八)
   */
  @Column(name = "ICD_CM_19")
  @JsonProperty("ICD_CM_19")
  @JacksonXmlProperty(localName = "d43")
  private String icdCm19;

  /**
   * 次診斷代碼(十九)
   */
  @Column(name = "ICD_CM_20")
  @JsonProperty("ICD_CM_20")
  @JacksonXmlProperty(localName = "d44")
  private String icdCm20;

  /**
   * 主手術(處置)代碼
   */
  @Column(name = "ICD_OP_CODE1")
  @JsonProperty("ICD_OP_CODE1")
  @JacksonXmlProperty(localName = "d45")
  private String icdOpCode1;

  /**
   * 次手術(處置)代碼一
   */
  @Column(name = "ICD_OP_CODE2")
  @JsonProperty("ICD_OP_CODE2")
  @JacksonXmlProperty(localName = "d46")
  private String icdOpCode2;

  /**
   * 次手術(處置)代碼二
   */
  @Column(name = "ICD_OP_CODE3")
  @JsonProperty("ICD_OP_CODE3")
  @JacksonXmlProperty(localName = "d47")
  private String icdOpCode3;

  /**
   * 次手術(處置)代碼三
   */
  @Column(name = "ICD_OP_CODE4")
  @JsonProperty("ICD_OP_CODE4")
  @JacksonXmlProperty(localName = "d48")
  private String icdOpCode4;

  /**
   * 次手術(處置)代碼四
   */
  @Column(name = "ICD_OP_CODE5")
  @JsonProperty("ICD_OP_CODE5")
  @JacksonXmlProperty(localName = "d49")
  private String icdOpCode5;

  /**
   * 次手術(處置)代碼五
   */
  @Column(name = "ICD_OP_CODE6")
  @JsonProperty("ICD_OP_CODE6")
  @JacksonXmlProperty(localName = "d50")
  private String icdOpCode6;

  /**
   * 次手術(處置)代碼六
   */
  @Column(name = "ICD_OP_CODE7")
  @JsonProperty("ICD_OP_CODE7")
  @JacksonXmlProperty(localName = "d51")
  private String icdOpCode7;

  /**
   * 次手術(處置)代碼七
   */
  @Column(name = "ICD_OP_CODE8")
  @JsonProperty("ICD_OP_CODE8")
  @JacksonXmlProperty(localName = "d52")
  private String icdOpCode8;

  /**
   * 次手術(處置)代碼八
   */
  @Column(name = "ICD_OP_CODE9")
  @JsonProperty("ICD_OP_CODE9")
  @JacksonXmlProperty(localName = "d53")
  private String icdOpCode9;

  /**
   * 次手術(處置)代碼九
   */
  @Column(name = "ICD_OP_CODE10")
  @JsonProperty("ICD_OP_CODE10")
  @JacksonXmlProperty(localName = "d54")
  private String icdOpCode10;

  /**
   * 次手術(處置)代碼十
   */
  @Column(name = "ICD_OP_CODE11")
  @JsonProperty("ICD_OP_CODE11")
  @JacksonXmlProperty(localName = "d55")
  private String icdOpCode11;

  /**
   * 次手術(處置)代碼十一
   */
  @Column(name = "ICD_OP_CODE12")
  @JsonProperty("ICD_OP_CODE12")
  @JacksonXmlProperty(localName = "d56")
  private String icdOpCode12;

  /**
   * 次手術(處置)代碼十二
   */
  @Column(name = "ICD_OP_CODE13")
  @JsonProperty("ICD_OP_CODE13")
  @JacksonXmlProperty(localName = "d57")
  private String icdOpCode13;

  /**
   * 次手術(處置)代碼十三
   */
  @Column(name = "ICD_OP_CODE14")
  @JsonProperty("ICD_OP_CODE14")
  @JacksonXmlProperty(localName = "d58")
  private String icdOpCode14;

  /**
   * 次手術(處置)代碼十四
   */
  @Column(name = "ICD_OP_CODE15")
  @JsonProperty("ICD_OP_CODE15")
  @JacksonXmlProperty(localName = "d59")
  private String icdOpCode15;

  /**
   * 次手術(處置)代碼十五
   */
  @Column(name = "ICD_OP_CODE16")
  @JsonProperty("ICD_OP_CODE16")
  @JacksonXmlProperty(localName = "d60")
  private String icdOpCode16;

  /**
   * 次手術(處置)代碼十六
   */
  @Column(name = "ICD_OP_CODE17")
  @JsonProperty("ICD_OP_CODE17")
  @JacksonXmlProperty(localName = "d61")
  private String icdOpCode17;

  /**
   * 次手術(處置)代碼十七
   */
  @Column(name = "ICD_OP_CODE18")
  @JsonProperty("ICD_OP_CODE18")
  @JacksonXmlProperty(localName = "d62")
  private String icdOpCode18;

  /**
   * 次手術(處置)代碼十八
   */
  @Column(name = "ICD_OP_CODE19")
  @JsonProperty("ICD_OP_CODE19")
  @JacksonXmlProperty(localName = "d63")
  private String icdOpCode19;

  /**
   * 次手術(處置)代碼十九
   */
  @Column(name = "ICD_OP_CODE20")
  @JsonProperty("ICD_OP_CODE20")
  @JacksonXmlProperty(localName = "d64")
  private String icdOpCode20;

  /**
   * 醫令總數
   */
  @Column(name = "ORDER_QTY")
  @JsonProperty("ORDER_QTY")
  @JacksonXmlProperty(localName = "d65")
  private Integer orderQty;

  /**
   * 診察費點數
   */
  @Column(name = "DIAG_DOT")
  @JsonProperty("DIAG_DOT")
  @JacksonXmlProperty(localName = "d66")
  private Integer diagDot;

  /**
   * 病房費點數
   */
  @Column(name = "ROOM_DOT")
  @JsonProperty("ROOM_DOT")
  @JacksonXmlProperty(localName = "d67")
  private Integer roomDot;

  /**
   * 管灌膳食費點數
   */
  @Column(name = "MEAL_DOT")
  @JsonProperty("MEAL_DOT")
  @JacksonXmlProperty(localName = "d68")
  private Integer mealDot;

  /**
   * 檢查費點數
   */
  @Column(name = "AMIN_DOT")
  @JsonProperty("AMIN_DOT")
  @JacksonXmlProperty(localName = "d69")
  private Integer aminDot;

  /**
   * 放射線診療費點數
   */
  @Column(name = "RADO_DOT")
  @JsonProperty("RADO_DOT")
  @JacksonXmlProperty(localName = "d70")
  private Integer radoDot;

  /**
   * 治療處置費點數
   */
  @Column(name = "THRP_DOT")
  @JsonProperty("THRP_DOT")
  @JacksonXmlProperty(localName = "d71")
  private Integer thrpDot;

  /**
   * 手術費點數
   */
  @Column(name = "SGRY_DOT")
  @JsonProperty("SGRY_DOT")
  @JacksonXmlProperty(localName = "d72")
  private Integer sgryDot;

  /**
   * 復健治療費點數
   */
  @Column(name = "PHSC_DOT")
  @JsonProperty("PHSC_DOT")
  @JacksonXmlProperty(localName = "d73")
  private Integer phscDot;

  /**
   * 血液血漿費點數
   */
  @Column(name = "BLOD_DOT")
  @JsonProperty("BLOD_DOT")
  @JacksonXmlProperty(localName = "d74")
  private Integer blodDot;

  /**
   * 血液透析費點數
   */
  @Column(name = "HD_DOT")
  @JsonProperty("HD_DOT")
  @JacksonXmlProperty(localName = "d75")
  private Integer hdDot;

  /**
   * 麻醉費點數
   */
  @Column(name = "ANE_DOT")
  @JsonProperty("ANE_DOT")
  @JacksonXmlProperty(localName = "d76")
  private Integer aneDot;

  /**
   * 特殊材料費點數
   */
  @Column(name = "METR_DOT")
  @JsonProperty("METR_DOT")
  @JacksonXmlProperty(localName = "d77")
  private Integer metrDot;

  /**
   * 藥費點數
   */
  @Column(name = "DRUG_DOT")
  @JsonProperty("DRUG_DOT")
  @JacksonXmlProperty(localName = "d78")
  private Integer drugDot;

  /**
   * 藥事服務費點數
   */
  @Column(name = "DSVC_DOT")
  @JsonProperty("DSVC_DOT")
  @JacksonXmlProperty(localName = "d79")
  private Integer dsvcDot;

  /**
   * 精神科治療費點數
   */
  @Column(name = "NRTP_DOT")
  @JsonProperty("NRTP_DOT")
  @JacksonXmlProperty(localName = "d80")
  private Integer nrtpDot;

  /**
   * 注射技術費點數
   */
  @Column(name = "INJT_DOT")
  @JsonProperty("INJT_DOT")
  @JacksonXmlProperty(localName = "d81")
  private Integer injtDot;

  /**
   * 嬰兒費點數
   */
  @Column(name = "BABY_DOT")
  @JsonProperty("BABY_DOT")
  @JacksonXmlProperty(localName = "d82")
  private Integer babyDot;

  /**
   * 醫療費用點數合計
   */
  @Column(name = "MED_DOT")
  @JsonProperty("MED_DOT")
  @JacksonXmlProperty(localName = "d83")
  private Integer medDot;

  /**
   * 部分負擔點數
   */
  @Column(name = "PART_DOT")
  @JsonProperty("PART_DOT")
  @JacksonXmlProperty(localName = "d84")
  private Integer partDot;

  /**
   * 申請費用點數
   */
  @Column(name = "APPL_DOT")
  @JsonProperty("APPL_DOT")
  @JacksonXmlProperty(localName = "d85")
  private Integer applDot;

  /**
   * 醫療費用點數(急性病床1-30日)
   */
  @Column(name = "EB_APPL30_DOT")
  @JsonProperty("EB_APPL30_DOT")
  @JacksonXmlProperty(localName = "d86")
  private Integer ebAppl30Dot;

  /**
   * 部分負擔點數(急性病床1-30日)
   */
  @Column(name = "EB_PART30_DOT")
  @JsonProperty("EB_PART30_DOT")
  @JacksonXmlProperty(localName = "d87")
  private Integer ebPart30Dot;

  /**
   * 醫療費用點數(急性病床31-60日)
   */
  @Column(name = "EB_APPL60_DOT")
  @JsonProperty("EB_APPL60_DOT")
  @JacksonXmlProperty(localName = "d88")
  private Integer ebAppl60Dot;

  /**
   * 部分負擔點數(急性病床31-60日)
   */
  @Column(name = "EB_PART60_DOT")
  @JsonProperty("EB_PART60_DOT")
  @JacksonXmlProperty(localName = "d89")
  private Integer ebPart60Dot;

  /**
   * 醫療費用點數(急性病床61日以上)
   */
  @Column(name = "EB_APPL61_DOT")
  @JsonProperty("EB_APPL61_DOT")
  @JacksonXmlProperty(localName = "d90")
  private Integer ebAppl61Dot;

  /**
   * 部分負擔點數(急性病床61日以上)
   */
  @Column(name = "EB_PART61_DOT")
  @JsonProperty("EB_PART61_DOT")
  @JacksonXmlProperty(localName = "d91")
  private Integer ebPart61Dot;

  /**
   * 醫療費用點數(慢性病床1-30日)
   */
  @Column(name = "SB_APPL30_DOT")
  @JsonProperty("SB_APPL30_DOT")
  @JacksonXmlProperty(localName = "d92")
  private Integer sbAppl30Dot;

  /**
   * 部分負擔點數(慢性病床1-30日)
   */
  @Column(name = "SB_PART30_DOT")
  @JsonProperty("SB_PART30_DOT")
  @JacksonXmlProperty(localName = "d93")
  private Integer sbPart30Dot;

  /**
   * 醫療費用點數(慢性病床31-90日)
   */
  @Column(name = "SB_APPL90_DOT")
  @JsonProperty("SB_APPL90_DOT")
  @JacksonXmlProperty(localName = "d94")
  private Integer sbAppl90Dot;

  /**
   * 部分負擔點數(慢性病床31-90日)
   */
  @Column(name = "SB_PART90_DOT")
  @JsonProperty("SB_PART90_DOT")
  @JacksonXmlProperty(localName = "d95")
  private Integer sbPart90Dot;

  /**
   * 醫療費用點數(慢性病床91-180日)
   */
  @Column(name = "SB_APPL180_DOT")
  @JsonProperty("SB_APPL180_DOT")
  @JacksonXmlProperty(localName = "d96")
  private Integer sbAppl180Dot;

  /**
   * 部分負擔點數(慢性病床91-180日)
   */
  @Column(name = "SB_PART180_DOT")
  @JsonProperty("SB_PART180_DOT")
  @JacksonXmlProperty(localName = "d97")
  private Integer sbPart180Dot;

  /**
   * 醫療費用點數(慢性病床181日以上)
   */
  @Column(name = "SB_APPL181_DOT")
  @JsonProperty("SB_APPL181_DOT")
  @JacksonXmlProperty(localName = "d98")
  private Integer sbAppl181Dot;

  @Column(name = "SB_PART181_DOT")
  @JsonProperty("SB_PART181_DOT")
  @JacksonXmlProperty(localName = "d99")
  private Integer sbPart181Dot;

  /**
   * 依附就醫新生兒出生年月日
   */
  @Column(name = "NB_BIRTHDAY")
  @JsonProperty("NB_BIRTHDAY")
  @JacksonXmlProperty(localName = "d100")
  private String nbBirthday;

  /**
   * 依附就醫新生兒胞胎註記
   */
  @Column(name = "CHILD_MARK")
  @JsonProperty("CHILD_MARK")
  @JacksonXmlProperty(localName = "d101")
  private String childMark;

  /**
   * 不適用TW-DRGS案件特殊註記
   */
  @Column(name = "TW_DRGS_SUIT_MARK")
  @JsonProperty("TW_DRGS_SUIT_MARK")
  @JacksonXmlProperty(localName = "d102")
  private String twDrgsSuitMark;

  /**
   * 姓名
   */
  @Column(name = "NAME")
  @JsonProperty("NAME")
  @JacksonXmlProperty(localName = "d103")
  private String name;

  /**
   * 矯正機關代號
   */
  @Column(name = "AGENCY_ID")
  @JsonProperty("AGENCY_ID")
  @JacksonXmlProperty(localName = "d104")
  private String agencyId;

  /**
   * 轉入服務機構代號
   */
  @Column(name = "TRAN_IN_HOSP_ID")
  @JsonProperty("TRAN_IN_HOSP_ID")
  @JacksonXmlProperty(localName = "d107")
  private String tranInHospId;

  /**
   * 轉往之醫事服務機構代號
   */
  @Column(name = "TRAN_OUT_HOSP_ID")
  @JsonProperty("TRAN_OUT_HOSP_ID")
  @JacksonXmlProperty(localName = "d108")
  private String tranOutHospId;

  /**
   * 實際提供醫療服務之醫事服務機構代號
   */
  @Column(name = "HOSP_ID")
  @JsonProperty("HOSP_ID")
  @JacksonXmlProperty(localName = "d109")
  private String hospId;

  /**
   * 醫療服務計畫
   */
  @Column(name = "SVC_PLAN")
  @JsonProperty("SVC_PLAN")
  @JacksonXmlProperty(localName = "d110")
  private String svcPlan;

  /**
   * 試辦計畫
   */
  @Column(name = "PILOT_PROJECT")
  @JsonProperty("PILOT_PROJECT")
  @JacksonXmlProperty(localName = "d111")
  private String pilotProject;

  /**
   * 不計入醫療費用點數合計欄位項目點數
   */
  @Column(name = "NON_APPL_DOT")
  @JsonProperty("NON_APPL_DOT")
  @JacksonXmlProperty(localName = "d112")
  private Integer nonApplDot;

  /**
   * 更新時間
   */
  @Column(name = "UPDATE_AT", nullable = false)
  @JsonIgnore
  private Date updateAt;
  
  /**
   * 出院日
   */
  @Column(name = "LEAVE_DATE", nullable = true)
  @JsonIgnore
  private Date leaveDate;
  
  /**
   * MR table ID
   */
  @Column(name = "MR_ID", nullable = true)
  @JsonIgnore
  private Long mrId;
  
  /**
   * 自費金額
   */
  @Column(name = "OWN_EXPENSE")
  @JsonIgnore
  private Integer ownExpense;
  
  /**
   * 不申報點數
   */
  @Column(name = "NO_APPL")
  @JsonIgnore
  private Integer noAppl;
  
  /**
   * 試辦計畫
   */
  @Column(name = "PRSN_NAME")
  @JsonIgnore
  private String prsnName;
  
  /**
   * 病床號
   */
  @Column(name = "BED_NO")
  @JsonIgnore
  private String bedNo;
  
  @JsonProperty("pdata")
  @JacksonXmlElementWrapper(useWrapping=false)
  @Transient // 當做 table 時跳過此欄位
  private List<IP_P> pdataList;
  
  public List<IP_P> getPdataList() {
    return pdataList;
  }

  public void setPdataList(List<IP_P> pdataList) {
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
  public Long getIptId() {
    return iptId;
  }

  /**
   * 序號
   */
  public void setIptId(Long IPT_ID) {
    iptId = IPT_ID;
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
   * <d4> 部分負擔代號
   */
  public String getPartNo() {
    return partNo;
  }

  /**
   * <d4> 部分負擔代號
   */
  public void setPartNo(String PART_NO) {
    partNo = PART_NO;
  }

  /**
   * <d5> 補報原因註記
   */
  public String getApplCauseMark() {
    return applCauseMark;
  }

  /**
   * <d5> 補報原因註記
   */
  public void setApplCauseMark(String APPL_CAUSE_MARK) {
    applCauseMark = APPL_CAUSE_MARK;
  }

  /**
   * <d6> 出生年月日(民國年)
   */
  public String getIdBirthYmd() {
    return idBirthYmd;
  }

  /**
   * <d6> 出生年月日(民國年)
   */
  public void setIdBirthYmd(String ID_BIRTH_YMD) {
    idBirthYmd = ID_BIRTH_YMD;
  }

  /**
   * <d7> 給付類別
   */
  public String getPayType() {
    return payType;
  }

  /**
   * <d7> 給付類別
   */
  public void setPayType(String PAY_TYPE) {
    payType = PAY_TYPE;
  }

  /**
   * <d9> 就醫科別
   */
  public String getFuncType() {
    return funcType;
  }

  /**
   * <d9> 就醫科別
   */
  public void setFuncType(String FUNC_TYPE) {
    funcType = FUNC_TYPE;
  }

  /**
   * <d10> 入院年月日
   */
  public String getInDate() {
    return inDate;
  }

  /**
   * <d10> 入院年月日
   */
  public void setInDate(String IN_DATE) {
    inDate = IN_DATE;
  }

  /**
   * <d11> 出院年月日
   */
  public String getOutDate() {
    return outDate;
  }

  /**
   * <d11> 出院年月日
   */
  public void setOutDate(String OUT_DATE) {
    outDate = OUT_DATE;
  }

  /**
   * <d12> 申報期間-起
   */
  public String getApplStartDate() {
    return applStartDate;
  }

  /**
   * <d12> 申報期間-起
   */
  public void setApplStartDate(String APPL_START_DATE) {
    applStartDate = APPL_START_DATE;
  }

  /**
   * <d13> 申報期間-迄
   */
  public String getApplEndDate() {
    return applEndDate;
  }

  /**
   * <d13> 申報期間-迄
   */
  public void setApplEndDate(String APPL_END_DATE) {
    applEndDate = APPL_END_DATE;
  }

  /**
   * <d14> 急性病床天數
   */
  public Integer getEbedDay() {
    return ebedDay;
  }

  /**
   * <d14> 急性病床天數
   */
  public void setEbedDay(Integer E_BED_DAY) {
    ebedDay = E_BED_DAY;
  }

  /**
   * <d15> 慢性病床天數
   */
  public Integer getSbedDay() {
    return sbedDay;
  }

  /**
   * <d15> 慢性病床天數
   */
  public void setSbedDay(Integer S_BED_DAY) {
    sbedDay = S_BED_DAY;
  }

  /**
   * <d16> 病患來源
   */
  public String getPatientSource() {
    return patientSource;
  }

  /**
   * <d16> 病患來源
   */
  public void setPatientSource(String PATIENT_SOURCE) {
    patientSource = PATIENT_SOURCE;
  }

  /**
   * <d17> 就醫序號
   */
  public String getCardSeqNo() {
    return cardSeqNo;
  }

  /**
   * <d17> 就醫序號
   */
  public void setCardSeqNo(String CARD_SEQ_NO) {
    cardSeqNo = CARD_SEQ_NO;
  }

  /**
   * <d18> TW-DRG碼
   */
  public String getTwDrgCode() {
    return twDrgCode;
  }

  /**
   * <d18> TW-DRG碼
   */
  public void setTwDrgCode(String TW_DRG_CODE) {
    twDrgCode = TW_DRG_CODE;
  }

  /**
   * <d19> TW-DRG支付型態
   */
  public String getTwDrgPayType() {
    return twDrgPayType;
  }

  /**
   * <d19> TW-DRG支付型態
   */
  public void setTwDrgPayType(String TW_DRG_PAY_TYPE) {
    twDrgPayType = TW_DRG_PAY_TYPE;
  }

  /**
   * <d20> 主治醫師代碼
   */
  public String getPrsnId() {
    return prsnId;
  }

  /**
   * <d20> 主治醫師代碼
   */
  public void setPrsnId(String PRSN_ID) {
    prsnId = PRSN_ID;
  }

  /**
   * <d21> DRGS碼
   */
  public String getCaseDrgCode() {
    return caseDrgCode;
  }

  /**
   * <d21> DRGS碼
   */
  public void setCaseDrgCode(String CASE_DRG_CODE) {
    caseDrgCode = CASE_DRG_CODE;
  }

  /**
   * <d24> 轉歸代碼
   */
  public String getTranCode() {
    return tranCode;
  }

  /**
   * <d24> 轉歸代碼
   */
  public void setTranCode(String TRAN_CODE) {
    tranCode = TRAN_CODE;
  }

  /**
   * <d25> 主診斷
   */
  public String getIcdCm1() {
    return icdCm1;
  }

  /**
   * <d25> 主診斷
   */
  public void setIcdCm1(String ICD_CM_1) {
    icdCm1 = ICD_CM_1;
  }

  /**
   * <d26> 次診斷代碼(一)
   */
  public String getIcdCm2() {
    return icdCm2;
  }

  /**
   * <d26> 次診斷代碼(一)
   */
  public void setIcdCm2(String ICD_CM_2) {
    icdCm2 = ICD_CM_2;
  }

  /**
   * <d27> 次診斷代碼(二)
   */
  public String getIcdCm3() {
    return icdCm3;
  }

  /**
   * <d27> 次診斷代碼(二)
   */
  public void setIcdCm3(String ICD_CM_3) {
    icdCm3 = ICD_CM_3;
  }

  /**
   * <d28> 次診斷代碼(三)
   */
  public String getIcdCm4() {
    return icdCm4;
  }

  /**
   * <d28> 次診斷代碼(三)
   */
  public void setIcdCm4(String ICD_CM_4) {
    icdCm4 = ICD_CM_4;
  }

  /**
   * <d29> 次診斷代碼(四)
   */
  public String getIcdCm5() {
    return icdCm5;
  }

  /**
   * <d29> 次診斷代碼(四)
   */
  public void setIcdCm5(String ICD_CM_5) {
    icdCm5 = ICD_CM_5;
  }

  /**
   * <d30> 次診斷代碼(五)
   */
  public String getIcdCm6() {
    return icdCm6;
  }

  /**
   * <d30> 次診斷代碼(五)
   */
  public void setIcdCm6(String ICD_CM_6) {
    icdCm6 = ICD_CM_6;
  }

  /**
   * <d31> 次診斷代碼(六)
   */
  public String getIcdCm7() {
    return icdCm7;
  }

  /**
   * <d31> 次診斷代碼(六)
   */
  public void setIcdCm7(String ICD_CM_7) {
    icdCm7 = ICD_CM_7;
  }

  /**
   * <d32> 次診斷代碼(七)
   */
  public String getIcdCm8() {
    return icdCm8;
  }

  /**
   * <d32> 次診斷代碼(七)
   */
  public void setIcdCm8(String ICD_CM_8) {
    icdCm8 = ICD_CM_8;
  }

  /**
   * <d33> 次診斷代碼(八)
   */
  public String getIcdCm9() {
    return icdCm9;
  }

  /**
   * <d33> 次診斷代碼(八)
   */
  public void setIcdCm9(String ICD_CM_9) {
    icdCm9 = ICD_CM_9;
  }

  /**
   * <d34> 次診斷代碼(九)
   */
  public String getIcdCm10() {
    return icdCm10;
  }

  /**
   * <d34> 次診斷代碼(九)
   */
  public void setIcdCm10(String ICD_CM_10) {
    icdCm10 = ICD_CM_10;
  }

  /**
   * <d35> 次診斷代碼(十)
   */
  public String getIcdCm11() {
    return icdCm11;
  }

  /**
   * <d35> 次診斷代碼(十)
   */
  public void setIcdCm11(String ICD_CM_11) {
    icdCm11 = ICD_CM_11;
  }

  /**
   * <d36> 次診斷代碼(十一)
   */
  public String getIcdCm12() {
    return icdCm12;
  }

  /**
   * <d36> 次診斷代碼(十一)
   */
  public void setIcdCm12(String ICD_CM_12) {
    icdCm12 = ICD_CM_12;
  }

  /**
   * <d37> 次診斷代碼(十二)
   */
  public String getIcdCm13() {
    return icdCm13;
  }

  /**
   * <d37> 次診斷代碼(十二)
   */
  public void setIcdCm13(String ICD_CM_13) {
    icdCm13 = ICD_CM_13;
  }

  /**
   * <d38> 次診斷代碼(十三)
   */
  public String getIcdCm14() {
    return icdCm14;
  }

  /**
   * <d38> 次診斷代碼(十三)
   */
  public void setIcdCm14(String ICD_CM_14) {
    icdCm14 = ICD_CM_14;
  }

  /**
   * <d39> 次診斷代碼(十四)
   */
  public String getIcdCm15() {
    return icdCm15;
  }

  /**
   * <d39> 次診斷代碼(十四)
   */
  public void setIcdCm15(String ICD_CM_15) {
    icdCm15 = ICD_CM_15;
  }

  /**
   * <d40> 次診斷代碼(十五)
   */
  public String getIcdCm16() {
    return icdCm16;
  }

  /**
   * <d40> 次診斷代碼(十五)
   */
  public void setIcdCm16(String ICD_CM_16) {
    icdCm16 = ICD_CM_16;
  }

  /**
   * <d41> 次診斷代碼(十六)
   */
  public String getIcdCm17() {
    return icdCm17;
  }

  /**
   * <d41> 次診斷代碼(十六)
   */
  public void setIcdCm17(String ICD_CM_17) {
    icdCm17 = ICD_CM_17;
  }

  /**
   * <d42> 次診斷代碼(十七)
   */
  public String getIcdCm18() {
    return icdCm18;
  }

  /**
   * <d42> 次診斷代碼(十七)
   */
  public void setIcdCm18(String ICD_CM_18) {
    icdCm18 = ICD_CM_18;
  }

  /**
   * <d43> 次診斷代碼(十八)
   */
  public String getIcdCm19() {
    return icdCm19;
  }

  /**
   * <d43> 次診斷代碼(十八)
   */
  public void setIcdCm19(String ICD_CM_19) {
    icdCm19 = ICD_CM_19;
  }

  /**
   * <d44> 次診斷代碼(十九)
   */
  public String getIcdCm20() {
    return icdCm20;
  }

  /**
   * <d44> 次診斷代碼(十九)
   */
  public void setIcdCm20(String ICD_CM_20) {
    icdCm20 = ICD_CM_20;
  }

  /**
   * <d45> 主手術(處置)代碼
   */
  public String getIcdOpCode1() {
    return icdOpCode1;
  }

  /**
   * <d45> 主手術(處置)代碼
   */
  public void setIcdOpCode1(String ICD_OP_CODE1) {
    icdOpCode1 = ICD_OP_CODE1;
  }

  /**
   * <d46> 次手術(處置)代碼一
   */
  public String getIcdOpCode2() {
    return icdOpCode2;
  }

  /**
   * <d46> 次手術(處置)代碼一
   */
  public void setIcdOpCode2(String ICD_OP_CODE2) {
    icdOpCode2 = ICD_OP_CODE2;
  }

  /**
   * <d47> 次手術(處置)代碼二
   */
  public String getIcdOpCode3() {
    return icdOpCode3;
  }

  /**
   * <d47> 次手術(處置)代碼二
   */
  public void setIcdOpCode3(String ICD_OP_CODE3) {
    icdOpCode3 = ICD_OP_CODE3;
  }

  /**
   * <d48> 次手術(處置)代碼三
   */
  public String getIcdOpCode4() {
    return icdOpCode4;
  }

  /**
   * <d48> 次手術(處置)代碼三
   */
  public void setIcdOpCode4(String ICD_OP_CODE4) {
    icdOpCode4 = ICD_OP_CODE4;
  }

  /**
   * <d49> 次手術(處置)代碼四
   */
  public String getIcdOpCode5() {
    return icdOpCode5;
  }

  /**
   * <d49> 次手術(處置)代碼四
   */
  public void setIcdOpCode5(String ICD_OP_CODE5) {
    icdOpCode5 = ICD_OP_CODE5;
  }

  /**
   * <d50> 次手術(處置)代碼五
   */
  public String getIcdOpCode6() {
    return icdOpCode6;
  }

  /**
   * <d50> 次手術(處置)代碼五
   */
  public void setIcdOpCode6(String ICD_OP_CODE6) {
    icdOpCode6 = ICD_OP_CODE6;
  }

  /**
   * <d51> 次手術(處置)代碼六
   */
  public String getIcdOpCode7() {
    return icdOpCode7;
  }

  /**
   * <d51> 次手術(處置)代碼六
   */
  public void setIcdOpCode7(String ICD_OP_CODE7) {
    icdOpCode7 = ICD_OP_CODE7;
  }

  /**
   * <d52> 次手術(處置)代碼七
   */
  public String getIcdOpCode8() {
    return icdOpCode8;
  }

  /**
   * <d52> 次手術(處置)代碼七
   */
  public void setIcdOpCode8(String ICD_OP_CODE8) {
    icdOpCode8 = ICD_OP_CODE8;
  }

  /**
   * <d53> 次手術(處置)代碼八
   */
  public String getIcdOpCode9() {
    return icdOpCode9;
  }

  /**
   * <d53> 次手術(處置)代碼八
   */
  public void setIcdOpCode9(String ICD_OP_CODE9) {
    icdOpCode9 = ICD_OP_CODE9;
  }

  /**
   * <d54> 次手術(處置)代碼九
   */
  public String getIcdOpCode10() {
    return icdOpCode10;
  }

  /**
   * <d54> 次手術(處置)代碼九
   */
  public void setIcdOpCode10(String ICD_OP_CODE10) {
    icdOpCode10 = ICD_OP_CODE10;
  }

  /**
   * <d55> 次手術(處置)代碼十
   */
  public String getIcdOpCode11() {
    return icdOpCode11;
  }

  /**
   * <d55> 次手術(處置)代碼十
   */
  public void setIcdOpCode11(String ICD_OP_CODE11) {
    icdOpCode11 = ICD_OP_CODE11;
  }

  /**
   * <d56> 次手術(處置)代碼十一
   */
  public String getIcdOpCode12() {
    return icdOpCode12;
  }

  /**
   * <d56> 次手術(處置)代碼十一
   */
  public void setIcdOpCode12(String ICD_OP_CODE12) {
    icdOpCode12 = ICD_OP_CODE12;
  }

  /**
   * <d57> 次手術(處置)代碼十二
   */
  public String getIcdOpCode13() {
    return icdOpCode13;
  }

  /**
   * <d57> 次手術(處置)代碼十二
   */
  public void setIcdOpCode13(String ICD_OP_CODE13) {
    icdOpCode13 = ICD_OP_CODE13;
  }

  /**
   * <d58> 次手術(處置)代碼十三
   */
  public String getIcdOpCode14() {
    return icdOpCode14;
  }

  /**
   * <d58> 次手術(處置)代碼十三
   */
  public void setIcdOpCode14(String ICD_OP_CODE14) {
    icdOpCode14 = ICD_OP_CODE14;
  }

  /**
   * <d59> 次手術(處置)代碼十四
   */
  public String getIcdOpCode15() {
    return icdOpCode15;
  }

  /**
   * <d59> 次手術(處置)代碼十四
   */
  public void setIcdOpCode15(String ICD_OP_CODE15) {
    icdOpCode15 = ICD_OP_CODE15;
  }

  /**
   * <d60> 次手術(處置)代碼十五
   */
  public String getIcdOpCode16() {
    return icdOpCode16;
  }

  /**
   * <d60> 次手術(處置)代碼十五
   */
  public void setIcdOpCode16(String ICD_OP_CODE16) {
    icdOpCode16 = ICD_OP_CODE16;
  }

  /**
   * <d61> 次手術(處置)代碼十六
   */
  public String getIcdOpCode17() {
    return icdOpCode17;
  }

  /**
   * <d61> 次手術(處置)代碼十六
   */
  public void setIcdOpCode17(String ICD_OP_CODE17) {
    icdOpCode17 = ICD_OP_CODE17;
  }

  /**
   * <d62> 次手術(處置)代碼十七
   */
  public String getIcdOpCode18() {
    return icdOpCode18;
  }

  /**
   * <d62> 次手術(處置)代碼十七
   */
  public void setIcdOpCode18(String ICD_OP_CODE18) {
    icdOpCode18 = ICD_OP_CODE18;
  }

  /**
   * <d63> 次手術(處置)代碼十八
   */
  public String getIcdOpCode19() {
    return icdOpCode19;
  }

  /**
   * <d63> 次手術(處置)代碼十八
   */
  public void setIcdOpCode19(String ICD_OP_CODE19) {
    icdOpCode19 = ICD_OP_CODE19;
  }

  /**
   * <d64> 次手術(處置)代碼十九
   */
  public String getIcdOpCode20() {
    return icdOpCode20;
  }

  /**
   * <d64> 次手術(處置)代碼十九
   */
  public void setIcdOpCode20(String ICD_OP_CODE20) {
    icdOpCode20 = ICD_OP_CODE20;
  }

  /**
   * <d65> 醫令總數
   */
  public Integer getOrderQty() {
    return orderQty;
  }

  /**
   * <d65> 醫令總數
   */
  public void setOrderQty(Integer ORDER_QTY) {
    orderQty = ORDER_QTY;
  }

  /**
   * <d66> 診察費點數
   */
  public Integer getDiagDot() {
    return diagDot;
  }

  /**
   * <d66> 診察費點數
   */
  public void setDiagDot(Integer DIAG_DOT) {
    diagDot = DIAG_DOT;
  }

  /**
   * <d67> 病房費點數
   */
  public Integer getRoomDot() {
    return roomDot;
  }

  /**
   * <d67> 病房費點數
   */
  public void setRoomDot(Integer ROOM_DOT) {
    roomDot = ROOM_DOT;
  }

  /**
   * <d68> 管灌膳食費點數
   */
  public Integer getMealDot() {
    return mealDot;
  }

  /**
   * <d68> 管灌膳食費點數
   */
  public void setMealDot(Integer MEAL_DOT) {
    mealDot = MEAL_DOT;
  }

  /**
   * <d69> 檢查費點數
   */
  public Integer getAminDot() {
    return aminDot;
  }

  /**
   * <d69> 檢查費點數
   */
  public void setAminDot(Integer AMIN_DOT) {
    aminDot = AMIN_DOT;
  }

  /**
   * <d70> 放射線診療費點數
   */
  public Integer getRadoDot() {
    return radoDot;
  }

  /**
   * <d70> 放射線診療費點數
   */
  public void setRadoDot(Integer RADO_DOT) {
    radoDot = RADO_DOT;
  }

  /**
   * <d71> 治療處置費點數
   */
  public Integer getThrpDot() {
    return thrpDot;
  }

  /**
   * <d71> 治療處置費點數
   */
  public void setThrpDot(Integer THRP_DOT) {
    thrpDot = THRP_DOT;
  }

  /**
   * <d72> 手術費點數
   */
  public Integer getSgryDot() {
    return sgryDot;
  }

  /**
   * <d72> 手術費點數
   */
  public void setSgryDot(Integer SGRY_DOT) {
    sgryDot = SGRY_DOT;
  }

  /**
   * <d73> 復健治療費點數
   */
  public Integer getPhscDot() {
    return phscDot;
  }

  /**
   * <d73> 復健治療費點數
   */
  public void setPhscDot(Integer PHSC_DOT) {
    phscDot = PHSC_DOT;
  }

  /**
   * <d74> 血液血漿費點數
   */
  public Integer getBlodDot() {
    return blodDot;
  }

  /**
   * <d74> 血液血漿費點數
   */
  public void setBlodDot(Integer BLOD_DOT) {
    blodDot = BLOD_DOT;
  }

  /**
   * <d75> 血液透析費點數
   */
  public Integer getHdDot() {
    return hdDot;
  }

  /**
   * <d75> 血液透析費點數
   */
  public void setHdDot(Integer HD_DOT) {
    hdDot = HD_DOT;
  }

  /**
   * <d76> 麻醉費點數
   */
  public Integer getAneDot() {
    return aneDot;
  }

  /**
   * <d76> 麻醉費點數
   */
  public void setAneDot(Integer ANE_DOT) {
    aneDot = ANE_DOT;
  }

  /**
   * <d77> 特殊材料費點數，由醫令類別為 3 的點數合計
   */
  public Integer getMetrDot() {
    return metrDot;
  }

  /**
   * <d77> 特殊材料費點數，由醫令類別為 3 的點數合計
   */
  public void setMetrDot(Integer METR_DOT) {
    metrDot = METR_DOT;
  }

  /**
   * <d78> 藥費點數
   */
  public Integer getDrugDot() {
    return drugDot;
  }

  /**
   * <d78> 藥費點數
   */
  public void setDrugDot(Integer DRUG_DOT) {
    drugDot = DRUG_DOT;
  }

  /**
   * <d79> 藥事服務費點數
   */
  public Integer getDsvcDot() {
    return dsvcDot;
  }

  /**
   * <d79> 藥事服務費點數
   */
  public void setDsvcDot(Integer DSVC_DOT) {
    dsvcDot = DSVC_DOT;
  }

  /**
   * <d80> 精神科治療費點數
   */
  public Integer getNrtpDot() {
    return nrtpDot;
  }

  /**
   * <d80> 精神科治療費點數
   */
  public void setNrtpDot(Integer NRTP_DOT) {
    nrtpDot = NRTP_DOT;
  }

  /**
   * <d81> 注射技術費點數
   */
  public Integer getInjtDot() {
    return injtDot;
  }

  /**
   * <d81> 注射技術費點數
   */
  public void setInjtDot(Integer INJT_DOT) {
    injtDot = INJT_DOT;
  }

  /**
   * <d82> 嬰兒費點數
   */
  public Integer getBabyDot() {
    return babyDot;
  }

  /**
   * <d82> 嬰兒費點數
   */
  public void setBabyDot(Integer BABY_DOT) {
    babyDot = BABY_DOT;
  }

  /**
   * <d83> 醫療費用點數合計
   */
  public Integer getMedDot() {
    return medDot;
  }

  /**
   * <d83> 醫療費用點數合計
   */
  public void setMedDot(Integer MED_DOT) {
    medDot = MED_DOT;
  }

  /**
   * <d84> 部分負擔點數
   */
  public Integer getPartDot() {
    return partDot;
  }

  /**
   * <d84> 部分負擔點數
   */
  public void setPartDot(Integer PART_DOT) {
    partDot = PART_DOT;
  }

  /**
   * <d85> 申請費用點數
   */
  public Integer getApplDot() {
    return applDot;
  }

  /**
   * <d85> 申請費用點數
   */
  public void setApplDot(Integer APPL_DOT) {
    applDot = APPL_DOT;
  }

  /**
   * <d86> 醫療費用點數(急性病床1-30日)
   */
  public Integer getEbAppl30Dot() {
    return ebAppl30Dot;
  }

  /**
   * <d86> 醫療費用點數(急性病床1-30日)
   */
  public void setEbAppl30Dot(Integer EB_APPL30_DOT) {
    ebAppl30Dot = EB_APPL30_DOT;
  }

  /**
   * <d87> 部分負擔點數(急性病床1-30日)
   */
  public Integer getEbPart30Dot() {
    return ebPart30Dot;
  }

  /**
   * <d87> 部分負擔點數(急性病床1-30日)
   */
  public void setEbPart30Dot(Integer EB_PART30_DOT) {
    ebPart30Dot = EB_PART30_DOT;
  }

  /**
   * <d88> 醫療費用點數(急性病床31-60日)
   */
  public Integer getEbAppl60Dot() {
    return ebAppl60Dot;
  }

  /**
   * <d88> 醫療費用點數(急性病床31-60日)
   */
  public void setEbAppl60Dot(Integer EB_APPL60_DOT) {
    ebAppl60Dot = EB_APPL60_DOT;
  }

  /**
   * <d89> 部分負擔點數(急性病床31-60日)
   */
  public Integer getEbPart60Dot() {
    return ebPart60Dot;
  }

  /**
   * <d89> 部分負擔點數(急性病床31-60日)
   */
  public void setEbPart60Dot(Integer EB_PART60_DOT) {
    ebPart60Dot = EB_PART60_DOT;
  }

  /**
   * <d90> 醫療費用點數(急性病床61日以上)
   */
  public Integer getEbAppl61Dot() {
    return ebAppl61Dot;
  }

  /**
   * <d90> 醫療費用點數(急性病床61日以上)
   */
  public void setEbAppl61Dot(Integer EB_APPL61_DOT) {
    ebAppl61Dot = EB_APPL61_DOT;
  }

  /**
   * <d91> 部分負擔點數(急性病床61日以上)
   */
  public Integer getEbPart61Dot() {
    return ebPart61Dot;
  }

  /**
   * <d91> 部分負擔點數(急性病床61日以上)
   */
  public void setEbPart61Dot(Integer EB_PART61_DOT) {
    ebPart61Dot = EB_PART61_DOT;
  }

  /**
   * <d92> 醫療費用點數(慢性病床1-30日)
   */
  public Integer getSbAppl30Dot() {
    return sbAppl30Dot;
  }

  /**
   * <d92> 醫療費用點數(慢性病床1-30日)
   */
  public void setSbAppl30Dot(Integer SB_APPL30_DOT) {
    sbAppl30Dot = SB_APPL30_DOT;
  }

  /**
   * <d93> 部分負擔點數(慢性病床1-30日)
   */
  public Integer getSbPart30Dot() {
    return sbPart30Dot;
  }

  /**
   * <d93> 部分負擔點數(慢性病床1-30日)
   */
  public void setSbPart30Dot(Integer SB_PART30_DOT) {
    sbPart30Dot = SB_PART30_DOT;
  }

  /**
   * <d94> 醫療費用點數(慢性病床31-90日)
   */
  public Integer getSbAppl90Dot() {
    return sbAppl90Dot;
  }

  /**
   * <d94> 醫療費用點數(慢性病床31-90日)
   */
  public void setSbAppl90Dot(Integer SB_APPL90_DOT) {
    sbAppl90Dot = SB_APPL90_DOT;
  }

  /**
   * <d95> 部分負擔點數(慢性病床31-90日)
   */
  public Integer getSbPart90Dot() {
    return sbPart90Dot;
  }

  /**
   * <d95> 部分負擔點數(慢性病床31-90日)
   */
  public void setSbPart90Dot(Integer SB_PART90_DOT) {
    sbPart90Dot = SB_PART90_DOT;
  }

  /**
   * <d96> 醫療費用點數(慢性病床91-180日)
   */
  public Integer getSbAppl180Dot() {
    return sbAppl180Dot;
  }

  /**
   * <d96> 醫療費用點數(慢性病床91-180日)
   */
  public void setSbAppl180Dot(Integer SB_APPL180_DOT) {
    sbAppl180Dot = SB_APPL180_DOT;
  }

  /**
   * <d97> 部分負擔點數(慢性病床91-180日)
   */
  public Integer getSbPart180Dot() {
    return sbPart180Dot;
  }

  /**
   * <d97> 部分負擔點數(慢性病床91-180日)
   */
  public void setSbPart180Dot(Integer SB_PART180_DOT) {
    sbPart180Dot = SB_PART180_DOT;
  }

  /**
   * <d98> 醫療費用點數(慢性病床181日以上)
   */
  public Integer getSbAppl181Dot() {
    return sbAppl181Dot;
  }

  /**
   * <d98> 醫療費用點數(慢性病床181日以上)
   */
  public void setSbAppl181Dot(Integer SB_APPL181_DOT) {
    sbAppl181Dot = SB_APPL181_DOT;
  }

  public Integer getSbPart181Dot() {
    return sbPart181Dot;
  }

  public void setSbPart181Dot(Integer SB_PART181_DOT) {
    sbPart181Dot = SB_PART181_DOT;
  }

  /**
   * <d100> 依附就醫新生兒出生年月日
   */
  public String getNbBirthday() {
    return nbBirthday;
  }

  /**
   * <d100> 依附就醫新生兒出生年月日
   */
  public void setNbBirthday(String NB_BIRTHDAY) {
    nbBirthday = NB_BIRTHDAY;
  }

  /**
   * <d101> 依附就醫新生兒胞胎註記
   */
  public String getChildMark() {
    return childMark;
  }

  /**
   * <d101> 依附就醫新生兒胞胎註記
   */
  public void setChildMark(String CHILD_MARK) {
    childMark = CHILD_MARK;
  }

  /**
   * <d102> 不適用TW-DRGS案件特殊註記
   */
  public String getTwDrgsSuitMark() {
    return twDrgsSuitMark;
  }

  /**
   * <d102> 不適用TW-DRGS案件特殊註記
   */
  public void setTwDrgsSuitMark(String TW_DRGS_SUIT_MARK) {
    twDrgsSuitMark = TW_DRGS_SUIT_MARK;
  }

  /**
   * <d103> 姓名
   */
  public String getName() {
    return name;
  }

  /**
   * <d103> 姓名
   */
  public void setName(String NAME) {
    name = NAME;
  }

  /**
   * <d104> 矯正機關代號
   */
  public String getAgencyId() {
    return agencyId;
  }

  /**
   * <d104> 矯正機關代號
   */
  public void setAgencyId(String AGENCY_ID) {
    agencyId = AGENCY_ID;
  }

  /**
   * <d107> 轉入服務機構代號
   */
  public String getTranInHospId() {
    return tranInHospId;
  }

  /**
   * <d107> 轉入服務機構代號
   */
  public void setTranInHospId(String TRAN_IN_HOSP_ID) {
    tranInHospId = TRAN_IN_HOSP_ID;
  }

  /**
   * <d108> 轉往之醫事服務機構代號
   */
  public String getTranOutHospId() {
    return tranOutHospId;
  }

  /**
   * <d108> 轉往之醫事服務機構代號
   */
  public void setTranOutHospId(String TRAN_OUT_HOSP_ID) {
    tranOutHospId = TRAN_OUT_HOSP_ID;
  }

  /**
   * <d109> 實際提供醫療服務之醫事服務機構代號
   */
  public String getHospId() {
    return hospId;
  }

  /**
   * <d109> 實際提供醫療服務之醫事服務機構代號
   */
  public void setHospId(String HOSP_ID) {
    hospId = HOSP_ID;
  }

  /**
   * <d110> 醫療服務計畫
   */
  public String getSvcPlan() {
    return svcPlan;
  }

  /**
   * <d110> 醫療服務計畫
   */
  public void setSvcPlan(String SVC_PLAN) {
    svcPlan = SVC_PLAN;
  }

  /**
   * <d111> 試辦計畫
   */
  public String getPilotProject() {
    return pilotProject;
  }

  /**
   * <d111> 試辦計畫
   */
  public void setPilotProject(String PILOT_PROJECT) {
    pilotProject = PILOT_PROJECT;
  }

  /**
   * <d112> 不計入醫療費用點數合計欄位項目點數
   */
  public Integer getNonApplDot() {
    return nonApplDot;
  }

  /**
   * <d112> 不計入醫療費用點數合計欄位項目點數
   */
  public void setNonApplDot(Integer NON_APPL_DOT) {
    nonApplDot = NON_APPL_DOT;
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
  
  public void calculateTotalDot() {
    medDot = diagDot.intValue() + roomDot.intValue() + mealDot.intValue() + aminDot.intValue() + radoDot.intValue() + 
        thrpDot.intValue() + sgryDot.intValue() + phscDot.intValue() + blodDot.intValue() + hdDot.intValue() + 
        aneDot.intValue() + metrDot.intValue() + drugDot.intValue() + dsvcDot.intValue() + nrtpDot.intValue() + 
        injtDot.intValue() + babyDot.intValue();
  }
  
  public void calculateTotalApplDot(int g00001) {
    //亞急性呼吸照護病房定額申報費用<亞急性呼吸照護病房合計點數
    //則：[申請點數]欄位=(定額申報費用-部分負擔點數)+1/3*(合計點數-定額申報費用)
    
    
    if ("A".equals(twDrgsSuitMark)) {
      //欄位IDd102(不適用Tw-DRGs案件特殊註記代碼)為「A:因住院30日內切帳申報(如部分負擔代碼，且出院(或未出院)之非DRG案件或>30天未出院之非DRG案件)」者，
      //申請費用點數＝點數清單段欄位IDd83－醫令代碼G00001-點數清單段欄位IDd84(部分負擔點數) +欄位IDd112點數。 
      applDot = medDot.intValue() - partDot.intValue() + nonApplDot.intValue() - g00001;
    //} else if ("6".equals(twDrgsSuitMark) && !"4".equals(payType)) {
      // 試辦計畫且非一般疾病
    } else if ("A".equals(twDrgsSuitMark) || ("6".equals(twDrgsSuitMark) && "9".equals(payType))) {
      // A: 因住院 30 日內切帳申報 ，試辦計畫、 安寧療護 案件且為呼吸照護案件
      applDot = g00001;
    } else {
      applDot = medDot.intValue() - partDot.intValue() + nonApplDot.intValue();
    }
  }

  public Integer getOwnExpense() {
    return ownExpense;
  }

  public void setOwnExpense(Integer ownExpense) {
    this.ownExpense = ownExpense;
  }

  public Date getLeaveDate() {
    return leaveDate;
  }

  public void setLeaveDate(Date leaveDate) {
    this.leaveDate = leaveDate;
  }

  public Integer getNoAppl() {
    return noAppl;
  }

  public void setNoAppl(Integer noAppl) {
    this.noAppl = noAppl;
  }

  public String getPrsnName() {
    return prsnName;
  }

  public void setPrsnName(String prsnName) {
    this.prsnName = prsnName;
  }

  public String getBedNo() {
    return bedNo;
  }

  public void setBedNo(String bedNo) {
    this.bedNo = bedNo;
  }

  public String getD8() {
    return d8;
  }

  public void setD8(String d8) {
    this.d8 = d8;
  }
 
}