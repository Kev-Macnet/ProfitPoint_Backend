/**
 * Created on 2021/9/29.
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
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("罕見ICD應用")
@Table(name = "RARE_ICD")
@Entity
public class RARE_ICD {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @JsonProperty("id")
  private Long id;

  @ApiModelProperty(value = "ICD代碼", example = "J10.01", required = true)
  @Column(name = "CODE")
  @JsonProperty("code")
  private String code;
  
  @ApiModelProperty(value = "中文說明", example = "其他確認流感病毒所致流行性感冒併其他呼吸道表徵", required = true)
  @Column(name = "DESC_CHI")
  @JsonProperty("name")
  private String descChi;
  
  @ApiModelProperty(value = "適用就醫方式", example = "00:門急診及住院均適用相同條件，10:門急診，20:住院", required = true)
  @Column(name = "DATA_FORMAT")
  @JsonProperty("dataFormat")
  private String dataFormat;
  
  @ApiModelProperty(value = "是否啟用門診月份應用超過幾次", example = "1", required = false)
  @Column(name = "OP_TIMES_M_STATUS")
  @JsonProperty("opTimesMStatus")
  private Integer opTimesMStatus;
  
  @ApiModelProperty(value = "門診月份應用超過幾次", example = "100", required = false)
  @Column(name = "OP_TIMES_M")
  @JsonProperty("opTimesM")
  private Integer opTimesM;
  
  @ApiModelProperty(value = "是否啟用門診月份應用超過前6個月平均值幾次", example = "1", required = false)
  @Column(name = "OP_TIMES_6M_STATUS")
  @JsonProperty("opTimes6MStatus")
  private Integer opTimes6MStatus;
  
  @ApiModelProperty(value = "門診月份應用超過前6個月平均值幾次", example = "100", required = false)
  @Column(name = "OP_TIMES_6M")
  @JsonProperty("opTimes6M")
  private Integer opTimes6M;
  
  @ApiModelProperty(value = "是否啟用住院月份應用超過幾次", example = "1", required = false)
  @Column(name = "IP_TIMES_M_STATUS")
  @JsonProperty("ipTimesMStatus")
  private Integer ipTimesMStatus;
  
  @ApiModelProperty(value = "住院月份應用超過幾次", example = "100", required = false)
  @Column(name = "IP_TIMES_M")
  @JsonProperty("ipTimesM")
  private Integer ipTimesM;
  
  @ApiModelProperty(value = "是否啟用住院月份應用超過前6個月平均值幾次", example = "1", required = false)
  @Column(name = "IP_TIMES_6M_STATUS")
  @JsonProperty("ipTimesMStatus")
  private Integer ipTimes6MStatus;
  
  @ApiModelProperty(value = "住院月份應用超過前6個月平均值幾次", example = "100", required = false)
  @Column(name = "IP_TIMES_6M")
  @JsonProperty("ipTimesM")
  private Integer ipTimes6M;
  
  @ApiModelProperty(value = "設定啟用狀態", example = "1", required = true)
  @Column(name = "STATUS")
  @JsonProperty("status")
  private Integer status;
  
  @ApiModelProperty(value = "罕見ICD代碼設定啟用日", example = "2019/07/01", required = true)
  @Column(name = "START_DATE")
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  private Date startDate;
  
  @ApiModelProperty(value = "罕見ICD代碼設定結束日", example = "2019/12/31", required = true)
  @Column(name = "END_DATE")
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  private Date endDate;
  
  @ApiModelProperty(value = "更新日期", example = "2021/09/29", required = true)
  @Column(name = "UPDATE_AT")
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  private Date updateAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDescChi() {
    return descChi;
  }

  public void setDescChi(String descChi) {
    this.descChi = descChi;
  }

  public String getDataFormat() {
    return dataFormat;
  }

  public void setDataFormat(String dataFormat) {
    this.dataFormat = dataFormat;
  }

  public Integer getOpTimesMStatus() {
    return opTimesMStatus;
  }

  public void setOpTimesMStatus(Integer opTimesMStatus) {
    this.opTimesMStatus = opTimesMStatus;
  }

  public Integer getOpTimesM() {
    return opTimesM;
  }

  public void setOpTimesM(Integer opTimesM) {
    this.opTimesM = opTimesM;
  }

  public Integer getOpTimes6MStatus() {
    return opTimes6MStatus;
  }

  public void setOpTimes6MStatus(Integer opTimes6MStatus) {
    this.opTimes6MStatus = opTimes6MStatus;
  }

  public Integer getOpTimes6M() {
    return opTimes6M;
  }

  public void setOpTimes6M(Integer opTimes6M) {
    this.opTimes6M = opTimes6M;
  }

  public Integer getIpTimesMStatus() {
    return ipTimesMStatus;
  }

  public void setIpTimesMStatus(Integer ipTimesMStatus) {
    this.ipTimesMStatus = ipTimesMStatus;
  }

  public Integer getIpTimesM() {
    return ipTimesM;
  }

  public void setIpTimesM(Integer ipTimesM) {
    this.ipTimesM = ipTimesM;
  }

  public Integer getIpTimes6MStatus() {
    return ipTimes6MStatus;
  }

  public void setIpTimes6MStatus(Integer ipTimes6MStatus) {
    this.ipTimes6MStatus = ipTimes6MStatus;
  }

  public Integer getIpTimes6M() {
    return ipTimes6M;
  }

  public void setIpTimes6M(Integer ipTimes6M) {
    this.ipTimes6M = ipTimes6M;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public Date getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(Date updateAt) {
    this.updateAt = updateAt;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

}
