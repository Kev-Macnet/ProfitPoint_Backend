/**
 * Created on 2021/11/10 by GenerateSqlByClass().
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

@ApiModel("月報表資料")
@Table(name = "POINT_MONTHLY")
@Entity
public class POINT_MONTHLY {

  /**
   * 序號
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @JsonIgnore
  private Long id;

  @Column(name = "YM")
  @ApiModelProperty(value = "年月(西元年)", example = "202101", required = true)
  private Integer ym;

  @Column(name = "PART_OP")
  @ApiModelProperty(value = "門診部分負擔", example = "1000000", required = true)
  private Long partOp;

  @ApiModelProperty(value = "急診部分負擔", example = "700000", required = true)
  @Column(name = "PART_EM")
  private Long partEm;

  @ApiModelProperty(value = "門急診部分負擔", example = "1700000", required = true)
  @Column(name = "PART_OP_ALL")
  private Long partOpAll;

  @ApiModelProperty(value = "住院部分負擔", example = "1200000", required = true)
  @Column(name = "PART_IP")
  private Long partIp;

  @ApiModelProperty(value = "部分負擔加總", example = "2900000", required = true)
  @Column(name = "PART_ALL")
  private Long partAll;

  @ApiModelProperty(value = "門診申請點數", example = "5000000", required = true)
  @Column(name = "APPL_OP")
  private Long applOp;

  @ApiModelProperty(value = "急診申請點數", example = "6000000", required = true)
  @Column(name = "APPL_EM")
  private Long applEm;

  @ApiModelProperty(value = "門急診申請點數", example = "11000000", required = true)
  @Column(name = "APPL_OP_ALL")
  private Long applOpAll;

  @ApiModelProperty(value = "住院申請點數", example = "9000000", required = true)
  @Column(name = "APPL_IP")
  private Long applIp;

  @ApiModelProperty(value = "申請點數加總", example = "20000000", required = true)
  @Column(name = "APPL_ALL")
  private Long applAll;

  @ApiModelProperty(value = "門診部分負擔+申請點數", example = "13900000", required = true)
  @Column(name = "TOTAL_OP")
  private Long totalOp;

  @ApiModelProperty(value = "急診部分負擔+申請點數", example = "8900000", required = true)
  @Column(name = "TOTAL_EM")
  private Long totalEm;

  @ApiModelProperty(value = "門急診部分負擔+申請點數", example = "28900000", required = true)
  @Column(name = "TOTAL_OP_ALL")
  private Long totalOpAll;

  @ApiModelProperty(value = "住院部分負擔+申請點數", example = "18900000", required = true)
  @Column(name = "TOTAL_IP")
  private Long totalIp;

  @ApiModelProperty(value = "全部部分負擔+申請點數", example = "47800000", required = true)
  @Column(name = "TOTAL_ALL")
  private Long totalAll;

  @ApiModelProperty(value = "門急診分配額度點數", example = "20000000", required = true)
  @Column(name = "ASSIGNED_OP")
  private Long assignedOpAll;

  @ApiModelProperty(value = "住院分配額度點數", example = "20000000", required = true)
  @Column(name = "ASSIGNED_IP")
  private Long assignedIp;

  @ApiModelProperty(value = "全部分配額度點數", example = "40000000", required = true)
  @Column(name = "ASSIGNED_ALL")
  private Long assignedAll;

  @ApiModelProperty(value = "住院總額達成率", example = "100", required = true)
  @Column(name = "RATE_IP")
  private Double rateIp;

  @ApiModelProperty(value = "門急診總額達成率", example = "100", required = true)
  @Column(name = "RATE_OP")
  private Double rateOpAll;

  @ApiModelProperty(value = "全部總額達成率", example = "100", required = true)
  @Column(name = "RATE_ALL")
  private Double rateAll;

  @ApiModelProperty(value = "門診人次", example = "100", required = true)
  @Column(name = "PATIENT_OP")
  private Long patientOp;

  @ApiModelProperty(value = "出院人次", example = "100", required = true)
  @Column(name = "PATIENT_IP")
  private Long patientIp;

  @ApiModelProperty(value = "急診人次", example = "100", required = true)
  @Column(name = "PATIENT_EM")
  private Long patientEm;

  @ApiModelProperty(value = "慢性處方箋額度", example = "100", required = true)
  @Column(name = "CHRONIC")
  private Long chronic;
  
  @ApiModelProperty(value = "總分配額度扣除申請點數的剩餘額度", example = "100", required = true)
  @Column(name = "REMAINING")
  private Long remaining;

  @ApiModelProperty(value = "總住院件數", example = "10000", required = true)
  @Column(name = "IP_QUANTITY")
  @JsonIgnore
  private Long ipQuantity;

  /**
   * DRG總案件數
   */
  @Column(name = "DRG_QUANTITY")
  @JsonIgnore
  private Long drgQuantity;

  /**
   * DRG案件申報總點數
   */
  @Column(name = "DRG_APPL_POINT")
  @JsonIgnore
  private Long drgApplPoint;

  /**
   * DRG實際總點數
   */
  @Column(name = "DRG_ACTUAL_POINT")
  @JsonIgnore
  private Long drgActualPoint;
  
  /**
   * 不申報門急診/住院合計點數，報表健保總額累積達成率用到

   */
  @Column(name = "NO_APPL_ALL")
  @JsonIgnore
  private Long noApplAll;
  
  /**
   * 不申報住院點數，報表健保總額累積達成率用到
   */
  @Column(name = "NO_APPL_IP")
  @JsonIgnore
  private Long noApplIp;
  
  /**
   * 不申報門急診點數，報表健保總額累積達成率用到
   */
  @Column(name = "NO_APPL_OP")
  @JsonIgnore
  private Long noApplOp;

  /**
   * 更新時間
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
  public Long getAssignedOpAll() {
    return assignedOpAll;
  }

  /**
   * 門急診分配額度點數
   */
  public void setAssignedOpAll(Long ASSIGNED_OP) {
    assignedOpAll = ASSIGNED_OP;
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
   * 住院總額達成率
   */
  public Double getRateIp() {
    return rateIp;
  }

  /**
   * 住院總額達成率
   */
  public void setRateIp(Double RATE_IP) {
    rateIp = RATE_IP;
  }

  /**
   * 門急診總額達成率
   */
  public Double getRateOpAll() {
    return rateOpAll;
  }

  /**
   * 門急診總額達成率
   */
  public void setRateOpAll(Double RATE_OP) {
    rateOpAll = RATE_OP;
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
   * 出院人次
   */
  public Long getPatientIp() {
    return patientIp;
  }

  /**
   * 出院人次
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
   * 慢性處方箋額度
   */
  public Long getChronic() {
    return chronic;
  }

  /**
   * 慢性處方箋額度
   */
  public void setChronic(Long CHRONIC) {
    chronic = CHRONIC;
  }

  /**
   * 總分配額度扣除申請點數的剩餘額度
   */
  public Long getRemaining() {
    return remaining;
  }

  /**
   * 總分配額度扣除申請點數的剩餘額度
   */
  public void setRemaining(Long REMAINING) {
    remaining = REMAINING;
  }

  /**
   * 總住院件數
   */
  public Long getIpQuantity() {
    return ipQuantity;
  }

  /**
   * 總住院件數
   */
  public void setIpQuantity(Long IP_QUANTITY) {
    ipQuantity = IP_QUANTITY;
  }

  /**
   * DRG總案件數
   */
  public Long getDrgQuantity() {
    return drgQuantity;
  }

  /**
   * DRG總案件數
   */
  public void setDrgQuantity(Long DRG_QUANTITY) {
    drgQuantity = DRG_QUANTITY;
  }

  /**
   * DRG案件申報總點數
   */
  public Long getDrgApplPoint() {
    return drgApplPoint;
  }

  /**
   * DRG案件申報總點數
   */
  public void setDrgApplPoint(Long DRG_APPL_POINT) {
    drgApplPoint = DRG_APPL_POINT;
  }

  /**
   * DRG實際總點數
   */
  public Long getDrgActualPoint() {
    return drgActualPoint;
  }

  /**
   * DRG實際總點數
   */
  public void setDrgActualPoint(Long DRG_ACTUAL_POINT) {
    drgActualPoint = DRG_ACTUAL_POINT;
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

  public Long getNoApplAll() {
    return noApplAll;
  }

  public void setNoApplAll(Long noApplAll) {
    this.noApplAll = noApplAll;
  }

  public Long getNoApplIp() {
    return noApplIp;
  }

  public void setNoApplIp(Long noApplIp) {
    this.noApplIp = noApplIp;
  }

  public Long getNoApplOp() {
    return noApplOp;
  }

  public void setNoApplOp(Long noApplOp) {
    this.noApplOp = noApplOp;
  }

}