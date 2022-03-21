/**
 * Created on 2022/03/09 by GenerateSqlByClass().
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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("序號")
@Table(name = "PLAN_CONDITION")
@Entity
public class PLAN_CONDITION {

  @ApiModelProperty(value = "序號", required = false)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  private Long id;

  @ApiModelProperty(value = "計畫名稱", required = false)
  @Column(name = "NAME", length = 80)
  private String name;

  @ApiModelProperty(value = "就醫科別", required = false)
  @Column(name = "DIVISION", length = 80)
  private String division;

  @ApiModelProperty(value = "是否啟用", required = false)
  @Column(name = "ACTIVE")
  private Integer active;

  @ApiModelProperty(value = "開關", required = false)
  @Column(name = "EXP_ICD_NO_ENABLE")
  private Integer expIcdNoEnable;

  @ApiModelProperty(value = "曾申報過ICD診斷碼", required = false)
  @Column(name = "EXP_ICD_NO", length = 80)
  private String expIcdNo;

  @ApiModelProperty(value = "開關", required = false)
  @Column(name = "NO_EXP_ICD_NO_ENABLE")
  private Integer noExpIcdNoEnable;

  @ApiModelProperty(value = "不曾申報過ICD診斷碼", required = false)
  @Column(name = "NO_EXP_ICD_NO", length = 80)
  private String noExpIcdNo;

  @ApiModelProperty(value = "開關(排除就醫申報中含有精神科慢性病房住院照護類費用)", required = false)
  @Column(name = "EXCLUDE_PSYCHIATRIC_ENABLE")
  private Integer excludePsychiatricEnable;

  @ApiModelProperty(value = "開關", required = false)
  @Column(name = "MEDICINE_TIMES_ENABLE")
  private Integer medicineTimesEnable;

  @ApiModelProperty(value = "拿藥次數>=", required = false)
  @Column(name = "MEDICINE_TIMES")
  private Integer medicineTimes;

  @ApiModelProperty(value = "拿藥科別", required = false)
  @Column(name = "MEDICINE_TIMES_DIVISION", length = 80)
  private String medicineTimesDivision;

  @ApiModelProperty(value = "開關", required = false)
  @Column(name = "EXCLUDE_PLAN_NDAY_ENABLE")
  private Integer excludePlanNdayEnable;

  @ApiModelProperty(value = "同院計畫N天結案", required = false)
  @Column(name = "EXCLUDE_PLAN_NDAY")
  private Integer excludePlanNday;

  @ApiModelProperty(value = "開關", required = false)
  @Column(name = "EXCLUDE_JOIN_ENABLE")
  private Integer excludeJoinEnable;

  @ApiModelProperty(value = "排除同院所參與者", required = false)
  @Column(name = "EXCLUDE_JOIN", length = 80)
  private String excludeJoin;

  @ApiModelProperty(value = "時間", required = false)
  @Column(name = "UPDATE_TM")
  private Date updateTm;

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
   * 計畫名稱
   */
  public String getName() {
    return name;
  }

  /**
   * 計畫名稱
   */
  public void setName(String NAME) {
    name = NAME;
  }

  /**
   * 就醫科別
   */
  public String getDivision() {
    return division;
  }

  /**
   * 就醫科別
   */
  public void setDivision(String DIVISION) {
    division = DIVISION;
  }

  /**
   * 是否啟用
   */
  public Integer getActive() {
    return active;
  }

  /**
   * 是否啟用
   */
  public void setActive(Integer ACTIVE) {
    active = ACTIVE;
  }

  /**
   * 開關
   */
  public Integer getExpIcdNoEnable() {
    return expIcdNoEnable;
  }

  /**
   * 開關
   */
  public void setExpIcdNoEnable(Integer EXP_ICD_NO_ENABLE) {
    expIcdNoEnable = EXP_ICD_NO_ENABLE;
  }

  /**
   * 曾申報過ICD診斷碼
   */
  public String getExpIcdNo() {
    return expIcdNo;
  }

  /**
   * 曾申報過ICD診斷碼
   */
  public void setExpIcdNo(String EXP_ICD_NO) {
    expIcdNo = EXP_ICD_NO;
  }

  /**
   * 開關
   */
  public Integer getNoExpIcdNoEnable() {
    return noExpIcdNoEnable;
  }

  /**
   * 開關
   */
  public void setNoExpIcdNoEnable(Integer NO_EXP_ICD_NO_ENABLE) {
    noExpIcdNoEnable = NO_EXP_ICD_NO_ENABLE;
  }

  /**
   * 不曾申報過ICD診斷碼
   */
  public String getNoExpIcdNo() {
    return noExpIcdNo;
  }

  /**
   * 不曾申報過ICD診斷碼
   */
  public void setNoExpIcdNo(String NO_EXP_ICD_NO) {
    noExpIcdNo = NO_EXP_ICD_NO;
  }

  /**
   * 開關(排除就醫申報中含有精神科慢性病房住院照護類費用)
   */
  public Integer getExcludePsychiatricEnable() {
    return excludePsychiatricEnable;
  }

  /**
   * 開關(排除就醫申報中含有精神科慢性病房住院照護類費用)
   */
  public void setExcludePsychiatricEnable(Integer EXCLUDE_PSYCHIATRIC_ENABLE) {
    excludePsychiatricEnable = EXCLUDE_PSYCHIATRIC_ENABLE;
  }

  /**
   * 開關
   */
  public Integer getMedicineTimesEnable() {
    return medicineTimesEnable;
  }

  /**
   * 開關
   */
  public void setMedicineTimesEnable(Integer MEDICINE_TIMES_ENABLE) {
    medicineTimesEnable = MEDICINE_TIMES_ENABLE;
  }

  /**
   * 拿藥次數>=
   */
  public Integer getMedicineTimes() {
    return medicineTimes;
  }

  /**
   * 拿藥次數>=
   */
  public void setMedicineTimes(Integer MEDICINE_TIMES) {
    medicineTimes = MEDICINE_TIMES;
  }

  /**
   * 拿藥科別
   */
  public String getMedicineTimesDivision() {
    return medicineTimesDivision;
  }

  /**
   * 拿藥科別
   */
  public void setMedicineTimesDivision(String MEDICINE_TIMES_DIVISION) {
    medicineTimesDivision = MEDICINE_TIMES_DIVISION;
  }

  /**
   * 開關
   */
  public Integer getExcludePlanNdayEnable() {
    return excludePlanNdayEnable;
  }

  /**
   * 開關
   */
  public void setExcludePlanNdayEnable(Integer EXCLUDE_PLAN_NDAY_ENABLE) {
    excludePlanNdayEnable = EXCLUDE_PLAN_NDAY_ENABLE;
  }

  /**
   * 同院計畫N天結案
   */
  public Integer getExcludePlanNday() {
    return excludePlanNday;
  }

  /**
   * 同院計畫N天結案
   */
  public void setExcludePlanNday(Integer EXCLUDE_PLAN_NDAY) {
    excludePlanNday = EXCLUDE_PLAN_NDAY;
  }

  /**
   * 開關
   */
  public Integer getExcludeJoinEnable() {
    return excludeJoinEnable;
  }

  /**
   * 開關
   */
  public void setExcludeJoinEnable(Integer EXCLUDE_JOIN_ENABLE) {
    excludeJoinEnable = EXCLUDE_JOIN_ENABLE;
  }

  /**
   * 排除同院所參與者
   */
  public String getExcludeJoin() {
    return excludeJoin;
  }

  /**
   * 排除同院所參與者
   */
  public void setExcludeJoin(String EXCLUDE_JOIN) {
    excludeJoin = EXCLUDE_JOIN;
  }

  /**
   * 時間
   */
  public Date getUpdateTm() {
    return updateTm;
  }

  /**
   * 時間
   */
  public void setUpdateTm(Date UPDATE_TM) {
    updateTm = UPDATE_TM;
  }

}