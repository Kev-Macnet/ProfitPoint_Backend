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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("序號")
@Table(name = "PT_PAYMENT_TERMS")
@Entity
public class PT_PAYMENT_TERMS {

  @ApiModelProperty(value = "序號", required = false)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  private Long id;

  @ApiModelProperty(value = "院內碼", required = false)
  @Column(name = "FEE_NO", length = 80)
  private String feeNo;

  @ApiModelProperty(value = "院內碼名稱", required = false)
  @Column(name = "FEE_NAME", length = 120)
  private String feeName;

  @ApiModelProperty(value = "支付標準代碼", required = false)
  @Column(name = "NHI_NO", length = 80)
  private String nhiNo;

  @ApiModelProperty(value = "支付標準代碼名稱", required = false)
  @Column(name = "NHI_NAME", length = 120)
  private String nhiName;

  @ApiModelProperty(value = "生效日", required = false)
  @Column(name = "START_DATE")
  private Date startDate;

  @ApiModelProperty(value = "失效日", required = false)
  @Column(name = "END_DATE")
  private Date endDate;

  @ApiModelProperty(value = "分類", required = false)
  @Column(name = "CATEGORY", length = 80)
  private String category;

  @ApiModelProperty(value = "醫院層級(1醫學中心/2區域醫院/3地方醫院/4基層診所)", required = false)
  @Column(name = "HOSPITAL_TYPE")
  private Integer hospitalType;

  @ApiModelProperty(value = "就醫方式(門急)", required = false)
  @Column(name = "OUTPATIENT_TYPE")
  private Integer outpatientType;

  @ApiModelProperty(value = "就醫方式(住院)", required = false)
  @Column(name = "HOSPITALIZED_TYPE")
  private Integer hospitalizedType;

  @ApiModelProperty(value = "是否啟用", required = false)
  @Column(name = "ACTIVE")
  private Integer active;

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
   * 院內碼
   */
  public String getFeeNo() {
    return feeNo;
  }

  /**
   * 院內碼
   */
  public void setFeeNo(String FEE_NO) {
    feeNo = FEE_NO;
  }

  /**
   * 院內碼名稱
   */
  public String getFeeName() {
    return feeName;
  }

  /**
   * 院內碼名稱
   */
  public void setFeeName(String FEE_NAME) {
    feeName = FEE_NAME;
  }

  /**
   * 支付標準代碼
   */
  public String getNhiNo() {
    return nhiNo;
  }

  /**
   * 支付標準代碼
   */
  public void setNhiNo(String NHI_NO) {
    nhiNo = NHI_NO;
  }

  /**
   * 支付標準代碼名稱
   */
  public String getNhiName() {
    return nhiName;
  }

  /**
   * 支付標準代碼名稱
   */
  public void setNhiName(String NHI_NAME) {
    nhiName = NHI_NAME;
  }

  /**
   * 生效日
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * 生效日
   */
  public void setStartDate(Date START_DATE) {
    startDate = START_DATE;
  }

  /**
   * 失效日
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * 失效日
   */
  public void setEndDate(Date END_DATE) {
    endDate = END_DATE;
  }

  /**
   * 分類
   */
  public String getCategory() {
    return category;
  }

  /**
   * 分類
   */
  public void setCategory(String CATEGORY) {
    category = CATEGORY;
  }

  /**
   * 醫院層級(1醫學中心/2區域醫院/3地方醫院/4基層診所)
   */
  public Integer getHospitalType() {
    return hospitalType;
  }

  /**
   * 醫院層級(1醫學中心/2區域醫院/3地方醫院/4基層診所)
   */
  public void setHospitalType(Integer HOSPITAL_TYPE) {
    hospitalType = HOSPITAL_TYPE;
  }

  /**
   * 就醫方式(門急)
   */
  public Integer getOutpatientType() {
    return outpatientType;
  }

  /**
   * 就醫方式(門急)
   */
  public void setOutpatientType(Integer OUTPATIENT_TYPE) {
    outpatientType = OUTPATIENT_TYPE;
  }

  /**
   * 就醫方式(住院)
   */
  public Integer getHospitalizedType() {
    return hospitalizedType;
  }

  /**
   * 就醫方式(住院)
   */
  public void setHospitalizedType(Integer HOSPITALIZED_TYPE) {
    hospitalizedType = HOSPITALIZED_TYPE;
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

}