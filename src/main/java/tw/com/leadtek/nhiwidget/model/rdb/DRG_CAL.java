/**
 * Created on 2021/8/27.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("DRG編碼試算")
@Table(name = "DRG_CAL")
@Entity
@IdClass(DRG_CALId.class)
public class DRG_CAL {

  @Id
  @ApiModelProperty(value = "病歷ID", example = "1", required = true)
  @Column(name = "MR_ID", nullable = false)
  @JsonIgnore
  protected Long mrId;
  
  @Id
  @ApiModelProperty(value = "主診斷碼", example = "S82.142.A", required = true)
  @Column(name = "ICD_CM_1", nullable = false)
  protected String icdCM1;
  
  @ApiModelProperty(value = "主手術(處置)碼", example = "0QSH04Z", required = false)
  @Column(name = "ICD_OP_CODE1")
  protected String icdOPCode1;
  
  @ApiModelProperty(value = "DRG代碼", example = "301", required = false)
  @Column(name = "DRG")
  protected String drg;
  
  @ApiModelProperty(value = "醫療費用點數", example = "300", required = false)
  @Column(name = "MED_DOT")
  protected Integer medDot;
  
  @ApiModelProperty(value = "併發症註記，Y/N", example = "N", required = false)
  @Column(name = "CC")
  protected String cc;
  
  @ApiModelProperty(value = "錯誤代碼", example = "Z", required = false)
  @Column(name = "ERROR")
  @JsonIgnore
  protected String error;
  
  @ApiModelProperty(value = "DRG落點區間", example = "B1", required = false)
  @Column(name = "DRG_SECTION")
  protected String drgSection;
  
  @ApiModelProperty(value = "DRG定額給付金額", example = "43021", required = false)
  @Column(name = "DRG_FIX")
  protected Integer drgFix;
  
  @ApiModelProperty(value = "申報點數(金額)", example = "21000", required = false)
  @Column(name = "DRG_DOT")
  protected Integer drgDot;
  
  @ApiModelProperty(value = "權重RW", required = false)
  @Column(name = "RW")
  protected Double rw;

  @ApiModelProperty(value = "MDC代碼", required = false)
  @Column(name = "MDC", length = 3)
  protected String mdc;

  @ApiModelProperty(value = "幾何平均住院日", required = false)
  @Column(name = "AVG_IN_DAY")
  protected Integer avgInDay;

  @ApiModelProperty(value = "下限(LOWER LIMIT)臨界點", required = false)
  @Column(name = "LLIMIT")
  protected Integer llimit;

  @ApiModelProperty(value = "上限(UPPER LIMIT)臨界點", required = false)
  @Column(name = "ULIMIT")
  protected Integer ulimit;

  @ApiModelProperty(value = "更新時間", required = false)
  @Column(name = "UPDATE_AT", nullable = false)
  @JsonIgnore
  protected Date updateAt;

  public Long getMrId() {
    return mrId;
  }

  public void setMrId(Long mrId) {
    this.mrId = mrId;
  }

  public String getIcdCM1() {
    return icdCM1;
  }

  public void setIcdCM1(String icdCM1) {
    this.icdCM1 = icdCM1;
  }

  public String getIcdOPCode1() {
    return icdOPCode1;
  }

  public void setIcdOPCode1(String icdOPCode1) {
    this.icdOPCode1 = icdOPCode1;
  }

  public String getDrg() {
    return drg;
  }

  public void setDrg(String drg) {
    this.drg = drg;
  }

  public Integer getMedDot() {
    return medDot;
  }

  public void setMedDot(Integer medDot) {
    this.medDot = medDot;
  }

  public String getCc() {
    return cc;
  }

  public void setCc(String cc) {
    this.cc = cc;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getDrgSection() {
    return drgSection;
  }

  public void setDrgSection(String drgSection) {
    this.drgSection = drgSection;
  }

  public Integer getDrgFix() {
    return drgFix;
  }

  public void setDrgFix(Integer drgFix) {
    this.drgFix = drgFix;
  }

  public Integer getDrgDot() {
    return drgDot;
  }

  public void setDrgDot(Integer drgDot) {
    this.drgDot = drgDot;
  }

  public Double getRw() {
    return rw;
  }

  public void setRw(Double rw) {
    this.rw = rw;
  }

  public String getMdc() {
    return mdc;
  }

  public void setMdc(String mdc) {
    this.mdc = mdc;
  }

  public Integer getAvgInDay() {
    return avgInDay;
  }

  public void setAvgInDay(Integer avgInDay) {
    this.avgInDay = avgInDay;
  }

  public Integer getLlimit() {
    return llimit;
  }

  public void setLlimit(Integer llimit) {
    this.llimit = llimit;
  }

  public Integer getUlimit() {
    return ulimit;
  }

  public void setUlimit(Integer ulimit) {
    this.ulimit = ulimit;
  }

  public Date getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(Date updateAt) {
    this.updateAt = updateAt;
  }

}
