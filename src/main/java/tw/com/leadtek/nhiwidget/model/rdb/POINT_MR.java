/**
 * Created on 2021/11/03 by GenerateSqlByClass().
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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Table(name = "POINT_MR")
@Entity
@ApiModel("健保點數月報表")
public class POINT_MR {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @ApiModelProperty(value = "id", example = "1", required = false)
  private Long id;

  @Column(name = "YM")
  @ApiModelProperty(value = "年月(西元年)，如 202111", example = "202111", required = false)
  private Integer ym;

  @Column(name = "PART_OP")
  @ApiModelProperty(value = "門診部分負擔", example = "5000000", required = false)
  private Long partOp;

  @Column(name = "PART_EM")
  @ApiModelProperty(value = "急診部分負擔", example = "3000000", required = false)
  private Long partEm;

 
  @Column(name = "PART_OP_ALL")
  @ApiModelProperty(value = "門急診部分負擔", example = "8000000", required = false)
  private Long partOpAll;

  @Column(name = "PART_IP")
  @ApiModelProperty(value = "住院部分負擔", example = "4000000", required = false)
  private Long partIp;

  @Column(name = "PART_ALL")
  @ApiModelProperty(value = "部分負擔加總", example = "13000000", required = false)
  private Long partAll;

  @Column(name = "APPL_OP")
  @ApiModelProperty(value = "門診申請點數", example = "10000000", required = false)
  private Long applOp;

  @Column(name = "APPL_EM")
  @ApiModelProperty(value = "急診申請點數", example = "6000000", required = false)
  private Long applEm;

  @Column(name = "APPL_OP_ALL")
  @ApiModelProperty(value = "門急診申請點數", example = "16000000", required = false)
  private Long applOpAll;

  @Column(name = "APPL_IP")
  @ApiModelProperty(value = "住院申請點數", example = "8000000", required = false)
  private Long applIp;

  @Column(name = "APPL_ALL")
  @ApiModelProperty(value = "申請點數加總", example = "24000000", required = false)
  private Long applAll;

  @Column(name = "TOTAL_OP")
  @ApiModelProperty(value = "門診部分負擔+申請點數", example = "15000000", required = false)
  private Long totalOp;

  @Column(name = "TOTAL_EM")
  @ApiModelProperty(value = "急診部分負擔+申請點數", example = "9000000", required = false)
  private Long totalEm;

  @Column(name = "TOTAL_OP_ALL")
  @ApiModelProperty(value = "門急診部分負擔+申請點數", example = "24000000", required = false)
  private Long totalOpAll;

  @Column(name = "TOTAL_IP")
  @ApiModelProperty(value = "住院部分負擔+申請點數", example = "12000000", required = false)
  private Long totalIp;

  @Column(name = "TOTAL_ALL")
  @ApiModelProperty(value = "全部部分負擔+申請點數", example = "36000000", required = false)
  private Long totalAll;

  @Column(name = "ASSIGNED_OP")
  @ApiModelProperty(value = "門急診分配額度點數", example = "24000000", required = false)
  private Long assignedOp;

  @Column(name = "ASSIGNED_IP")
  @ApiModelProperty(value = "住院分配額度點數", example = "12000000", required = false)
  private Long assignedIp;

  @Column(name = "ASSIGNED_ALL")
  @ApiModelProperty(value = "全部分配額度點數", example = "36000000", required = false)
  private Long assignedAll;

  @Column(name = "RATE_IP")
  @ApiModelProperty(value = "住院總額達成率", example = "100", required = false)
  private Double rateIp;

  @Column(name = "RATE_OP")
  @ApiModelProperty(value = "門急診總額達成率", example = "100", required = false)
  private Double rateOp;

  @Column(name = "RATE_ALL")
  @ApiModelProperty(value = "全部總額達成率", example = "100", required = false)
  private Double rateAll;
  
  @Column(name = "PATIENT_OP")
  @ApiModelProperty(value = "門診人次", example = "48000", required = false)
  private Long patientOp;

  @Column(name = "PATIENT_EM")
  @ApiModelProperty(value = "急診人次", example = "3000", required = false)
  private Long patientEm;

  @Column(name = "PATIENT_IP")
  @ApiModelProperty(value = "出院人次", example = "4000", required = false)
  private Long patientIp;
  
  @Column(name = "CHRONIC")
  @ApiModelProperty(value = "慢性處方箋額度", example = "50000", required = false)
  private Long chronic;

  @Column(name = "REMAINING")
  @ApiModelProperty(value = "總分配額度扣除申請點數的剩餘額度", example = "50000", required = false)
  private Long remaining;
  
  @Column(name = "IP_QUANTITY")
  @ApiModelProperty(value = "總住院件數", example = "3000", required = false)
  private Long ipQuantity;
  
  @Column(name = "DRG_QUANTITY")
  @ApiModelProperty(value = "DRG總案件數", example = "800", required = false)
  private Long drgQuantity;
  
  @Column(name = "DRG_APPL_POINT")
  @ApiModelProperty(value = "DRG案件申報總點數", example = "800", required = false)
  private Long drgApplPoint;

  @Column(name = "DRG_ACTUAL_POINT")
  @ApiModelProperty(value = "DRG實際總點數", example = "800", required = false)
  private Long drgActualPoint;

  @Column(name = "UPDATE_AT")
  @ApiModelProperty(value = "更新時間", example = "84000", required = false)
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
   * 年月(西元年)，如 201211
   */
  public Integer getYm() {
    return ym;
  }

  /**
   * 年月(西元年)，如 201211
   */
  public void setYm(Integer YM) {
    ym = YM;
  }

  /**
   * 門診部分負擔
   */
  public Long getPartOp() {
    return partOp;
  }

  /**
   * 門診部分負擔
   */
  public void setPartOp(Long PART_OP) {
    partOp = PART_OP;
  }

  /**
   * 急診部分負擔
   */
  public Long getPartEm() {
    return partEm;
  }

  /**
   * 急診部分負擔
   */
  public void setPartEm(Long PART_EM) {
    partEm = PART_EM;
  }

  /**
   * 門急診部分負擔
   */
  public Long getPartOpAll() {
    return partOpAll;
  }

  /**
   * 門急診部分負擔
   */
  public void setPartOpAll(Long PART_OP_ALL) {
    partOpAll = PART_OP_ALL;
  }

  /**
   * 住院部分負擔
   */
  public Long getPartIp() {
    return partIp;
  }

  /**
   * 住院部分負擔
   */
  public void setPartIp(Long PART_IP) {
    partIp = PART_IP;
  }

  /**
   * 部分負擔加總
   */
  public Long getPartAll() {
    return partAll;
  }

  /**
   * 部分負擔加總
   */
  public void setPartAll(Long PART_ALL) {
    partAll = PART_ALL;
  }

  /**
   * 門診申請點數
   */
  public Long getApplOp() {
    return applOp;
  }

  /**
   * 門診申請點數
   */
  public void setApplOp(Long APPL_OP) {
    applOp = APPL_OP;
  }

  /**
   * 急診申請點數
   */
  public Long getApplEm() {
    return applEm;
  }

  /**
   * 急診申請點數
   */
  public void setApplEm(Long APPL_EM) {
    applEm = APPL_EM;
  }

  /**
   * 門急診申請點數
   */
  public Long getApplOpAll() {
    return applOpAll;
  }

  /**
   * 門急診申請點數
   */
  public void setApplOpAll(Long APPL_OP_ALL) {
    applOpAll = APPL_OP_ALL;
  }

  /**
   * 住院申請點數
   */
  public Long getApplIp() {
    return applIp;
  }

  /**
   * 住院申請點數
   */
  public void setApplIp(Long APPL_IP) {
    applIp = APPL_IP;
  }

  /**
   * 申請點數加總
   */
  public Long getApplAll() {
    return applAll;
  }

  /**
   * 申請點數加總
   */
  public void setApplAll(Long APPL_ALL) {
    applAll = APPL_ALL;
  }

  /**
   * 門診部分負擔+申請點數
   */
  public Long getTotalOp() {
    return totalOp;
  }

  /**
   * 門診部分負擔+申請點數
   */
  public void setTotalOp(Long TOTAL_OP) {
    totalOp = TOTAL_OP;
  }

  /**
   * 急診部分負擔+申請點數
   */
  public Long getTotalEm() {
    return totalEm;
  }

  /**
   * 急診部分負擔+申請點數
   */
  public void setTotalEm(Long TOTAL_EM) {
    totalEm = TOTAL_EM;
  }

  /**
   * 門急診部分負擔+申請點數
   */
  public Long getTotalOpAll() {
    return totalOpAll;
  }

  /**
   * 門急診部分負擔+申請點數
   */
  public void setTotalOpAll(Long TOTAL_OP_ALL) {
    totalOpAll = TOTAL_OP_ALL;
  }

  /**
   * 住院部分負擔+申請點數
   */
  public Long getTotalIp() {
    return totalIp;
  }

  /**
   * 住院部分負擔+申請點數
   */
  public void setTotalIp(Long TOTAL_IP) {
    totalIp = TOTAL_IP;
  }

  /**
   * 全部部分負擔+申請點數
   */
  public Long getTotalAll() {
    return totalAll;
  }

  /**
   * 全部部分負擔+申請點數
   */
  public void setTotalAll(Long TOTAL_ALL) {
    totalAll = TOTAL_ALL;
  }

  /**
   * 門急診分配額度點數
   */
  public Long getAssignedOp() {
    return assignedOp;
  }

  /**
   * 門急診分配額度點數
   */
  public void setAssignedOp(Long ASSIGNED_OP) {
    assignedOp = ASSIGNED_OP;
  }

  /**
   * 住院分配額度點數
   */
  public Long getAssignedIp() {
    return assignedIp;
  }

  /**
   * 住院分配額度點數
   */
  public void setAssignedIp(Long ASSIGNED_IP) {
    assignedIp = ASSIGNED_IP;
  }

  /**
   * 全部分配額度點數
   */
  public Long getAssignedAll() {
    return assignedAll;
  }

  /**
   * 全部分配額度點數
   */
  public void setAssignedAll(Long ASSIGNED_ALL) {
    assignedAll = ASSIGNED_ALL;
  }

  /**
   * 門急診總額達成率
   */
  public Double getRateIp() {
    return rateIp;
  }

  /**
   * 門急診總額達成率
   */
  public void setRateIp(Double RATE_IP) {
    rateIp = RATE_IP;
  }

  /**
   * 住院總額達成率
   */
  public Double getRateOp() {
    return rateOp;
  }

  /**
   * 住院總額達成率
   */
  public void setRateOp(Double RATE_OP) {
    rateOp = RATE_OP;
  }

  /**
   * 全部總額達成率
   */
  public Double getRateAll() {
    return rateAll;
  }

  /**
   * 全部總額達成率
   */
  public void setRateAll(Double RATE_ALL) {
    rateAll = RATE_ALL;
  }

  /**
   * 門診人次
   */
  public Long getPatientOp() {
    return patientOp;
  }

  /**
   * 門診人次
   */
  public void setPatientOp(Long PATIENT_OP) {
    patientOp = PATIENT_OP;
  }

  /**
   * 住院人次
   */
  public Long getPatientIp() {
    return patientIp;
  }

  /**
   * 住院人次
   */
  public void setPatientIp(Long PATIENT_IP) {
    patientIp = PATIENT_IP;
  }

  /**
   * 急診人次
   */
  public Long getPatientEm() {
    return patientEm;
  }

  /**
   * 急診人次
   */
  public void setPatientEm(Long PATIENT_EM) {
    patientEm = PATIENT_EM;
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

  public Long getChronic() {
    return chronic;
  }

  public void setChronic(Long chronic) {
    this.chronic = chronic;
  }

  public Long getRemaining() {
    return remaining;
  }

  public void setRemaining(Long remaining) {
    this.remaining = remaining;
  }

  public Long getIpQuantity() {
    return ipQuantity;
  }

  public void setIpQuantity(Long ipQuantity) {
    this.ipQuantity = ipQuantity;
  }

  public Long getDrgQuantity() {
    return drgQuantity;
  }

  public void setDrgQuantity(Long drgQuantity) {
    this.drgQuantity = drgQuantity;
  }

  public Long getDrgApplPoint() {
    return drgApplPoint;
  }

  public void setDrgApplPoint(Long drgApplPoint) {
    this.drgApplPoint = drgApplPoint;
  }

  public Long getDrgActualPoint() {
    return drgActualPoint;
  }

  public void setDrgActualPoint(Long drgActualPoint) {
    this.drgActualPoint = drgActualPoint;
  }

}