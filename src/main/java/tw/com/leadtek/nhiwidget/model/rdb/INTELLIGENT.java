/**
 * Created on 2021/11/19 by GenerateSqlByClass().
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
import io.swagger.annotations.ApiModelProperty;

@Table(name = "INTELLIGENT")
@Entity
public class INTELLIGENT {

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
   * MR TABLE ID
   */
  @Column(name = "MR_ID")
  @JsonIgnore
  private Long mrId;

  /**
   * 病歷狀態，-2: 待確認，-1: 疑問標示，0: 待處理，1: 無需變更，2: 優化完成，3: 評估不調整
   */
  @Column(name = "STATUS")
  @JsonIgnore
  private Integer status;

  /**
   * 就醫日期-起
   */
  @Column(name = "START_DATE")
  @JsonIgnore
  private Date startDate;

  /**
   * 就醫日期-訖
   */
  @Column(name = "END_DATE")
  @JsonIgnore
  private Date endDate;

  /**
   * 就醫記錄編號
   */
  @Column(name = "INH_CLINIC_ID", length = 16)
  @JsonIgnore
  private String inhClinicId;

  /**
   * 就醫科別代碼
   */
  @Column(name = "FUNC_TYPE", length = 2)
  @JsonIgnore
  private String funcType;

  /**
   * 就醫科別名稱
   */
  @Column(name = "FUNC_TYPEC", length = 20)
  @JsonIgnore
  private String funcTypec;

  /**
   * 申請點數總計
   */
  @Column(name = "APPL_DOT")
  @JsonIgnore
  private Integer applDot;

  /**
   * 錯誤條件代碼
   */
  @Column(name = "CONDITION_CODE")
  @JsonIgnore
  private Integer conditionCode;

  /**
   * 錯誤代碼
   */
  @Column(name = "REASON_CODE", length = 12)
  @JsonIgnore
  private String reasonCode;

  /**
   * 錯誤原因
   */
  @Column(name = "REASON", length = 100)
  @JsonIgnore
  private String reason;
  
  @Column(name = "PRSN_NAME", length = 30)
  private String prsnName;

  /**
   * ICD診斷碼和處置碼，用,分隔
   */
  @Column(name = "ICD", length = 600)
  @JsonIgnore
  private String icd;

  /**
   * 健保碼/醫令/支付標準代碼
   */
  @Column(name = "CODE", length = 3200)
  @JsonIgnore
  private String code;

  /**
   * 院內碼
   */
  @Column(name = "INH_CODE", length = 1600)
  @JsonIgnore
  private String inhCode;
  
  /**
   * 資料格式，與IP_T, OP_T的DATA_FORMAT值一樣。10:門診，20:住院，30:特約藥局，40:特約物理(職能)治療所...
   */
  @ApiModelProperty(value = "資料格式，與IP_T, OP_T的DATA_FORMAT值一樣。10:門診，20:住院，30:特約藥局，40:特約物理(職能)治療所...",
      example = "10", required = false)
  @Column(name = "DATA_FORMAT", length = 2)
  @JsonIgnore
  private String dataFormat;
  
  /**
   * 院內碼
   */
  @Column(name = "ROC_ID", length = 12)
  @JsonIgnore
  private String rocId;
  
  /**
   * 院內碼
   */
  @Column(name = "APPL_YM", length = 5)
  @JsonIgnore
  private String applYm;

  /**
   * 更新日期
   */
  @Column(name = "UPDATE_AT")
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
   * MR TABLE ID
   */
  public Long getMrId() {
    return mrId;
  }

  /**
   * MR TABLE ID
   */
  public void setMrId(Long MR_ID) {
    mrId = MR_ID;
  }

  /**
   * 病歷狀態，-2: 待確認，-1: 疑問標示，0: 待處理，1: 無需變更，2: 優化完成，3: 評估不調整
   */
  public Integer getStatus() {
    return status;
  }

  /**
   * 病歷狀態，-2: 待確認，-1: 疑問標示，0: 待處理，1: 無需變更，2: 優化完成，3: 評估不調整
   */
  public void setStatus(Integer STATUS) {
    status = STATUS;
  }

  /**
   * 就醫日期-起
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * 就醫日期-起
   */
  public void setStartDate(Date START_DATE) {
    startDate = START_DATE;
  }

  /**
   * 就醫日期-訖
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * 就醫日期-訖
   */
  public void setEndDate(Date END_DATE) {
    endDate = END_DATE;
  }

  /**
   * 就醫記錄編號
   */
  public String getInhClinicId() {
    return inhClinicId;
  }

  /**
   * 就醫記錄編號
   */
  public void setInhClinicId(String INH_CLINIC_ID) {
    inhClinicId = INH_CLINIC_ID;
  }

  /**
   * 就醫科別代碼
   */
  public String getFuncType() {
    return funcType;
  }

  /**
   * 就醫科別代碼
   */
  public void setFuncType(String FUNC_TYPE) {
    funcType = FUNC_TYPE;
  }

  /**
   * 就醫科別名稱
   */
  public String getFuncTypec() {
    return funcTypec;
  }

  /**
   * 就醫科別名稱
   */
  public void setFuncTypec(String FUNC_TYPEC) {
    funcTypec = FUNC_TYPEC;
  }

  /**
   * 申請點數總計
   */
  public Integer getApplDot() {
    return applDot;
  }

  /**
   * 申請點數總計
   */
  public void setApplDot(Integer APPL_DOT) {
    applDot = APPL_DOT;
  }

  /**
   * 錯誤條件代碼
   */
  public Integer getConditionCode() {
    return conditionCode;
  }

  /**
   * 錯誤條件代碼
   */
  public void setConditionCode(Integer CONDITION_CODE) {
    conditionCode = CONDITION_CODE;
  }

  /**
   * 錯誤代碼
   */
  public String getReasonCode() {
    return reasonCode;
  }

  /**
   * 錯誤代碼
   */
  public void setReasonCode(String REASON_CODE) {
    reasonCode = REASON_CODE;
  }

  /**
   * 錯誤原因
   */
  public String getReason() {
    return reason;
  }

  /**
   * 錯誤原因
   */
  public void setReason(String REASON) {
    reason = REASON;
  }

  /**
   * ICD診斷碼和處置碼，用,分隔
   */
  public String getIcd() {
    return icd;
  }

  /**
   * ICD診斷碼和處置碼，用,分隔
   */
  public void setIcd(String ICD) {
    icd = ICD;
  }

  /**
   * 健保碼/醫令/支付標準代碼
   */
  public String getCode() {
    return code;
  }

  /**
   * 健保碼/醫令/支付標準代碼
   */
  public void setCode(String CODE) {
    code = CODE;
  }

  /**
   * 院內碼
   */
  public String getInhCode() {
    return inhCode;
  }

  /**
   * 院內碼
   */
  public void setInhCode(String INH_CODE) {
    inhCode = INH_CODE;
  }

  /**
   * 更新日期
   */
  public Date getUpdateAt() {
    return updateAt;
  }

  /**
   * 更新日期
   */
  public void setUpdateAt(Date UPDATE_AT) {
    updateAt = UPDATE_AT;
  }

  public String getPrsnName() {
    return prsnName;
  }

  public void setPrsnName(String prsnName) {
    this.prsnName = prsnName;
  }

  public String getDataFormat() {
    return dataFormat;
  }

  public void setDataFormat(String dataFormat) {
    this.dataFormat = dataFormat;
  }

  public String getRocId() {
    return rocId;
  }

  public void setRocId(String rocId) {
    this.rocId = rocId;
  }

  public String getApplYm() {
    return applYm;
  }

  public void setApplYm(String applYm) {
    this.applYm = applYm;
  }
  
}