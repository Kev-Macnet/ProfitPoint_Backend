/**
 * Created on 2021/8/27.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("DRG編碼試算結果")
@Table(name = "DRG_CAL")
@Entity
@IdClass(DRG_CALId.class)
public class DRG_CAL {

  @Id
  @ApiModelProperty(value = "病歷ID", example = "1", required = true)
  @Column(name = "MR_ID", nullable = false)
  private String mrId;
  
  @Id
  @ApiModelProperty(value = "主診斷碼", example = "S82.142.A", required = true)
  @Column(name = "ICD_CM_1", nullable = false)
  private String icdCM1;
  
  @ApiModelProperty(value = "主手術(處置)碼", example = "0QSH04Z", required = false)
  @Column(name = "ICD_OP_CODE1")
  private String icdOPCode1;
  
  @ApiModelProperty(value = "DRG代碼", example = "301", required = false)
  @Column(name = "DRG")
  private String drg;
  
  @ApiModelProperty(value = "醫療費用點數", example = "300", required = false)
  @Column(name = "MED_DOT")
  private Integer medDot;
  
  @ApiModelProperty(value = "併發症註記，Y/N", example = "N", required = false)
  @Column(name = "CC")
  private String cc;
  
  @ApiModelProperty(value = "錯誤代碼", example = "Z", required = false)
  @Column(name = "ERROR")
  private String error;
  
  @ApiModelProperty(value = "DRG落點區間", example = "B1", required = false)
  @Column(name = "DRG_SECTION")
  private String drgSection;
  
  @ApiModelProperty(value = "DRG定額給付金額", example = "43021", required = false)
  @Column(name = "DRG_FIX")
  private Integer drgFix;
  
  @ApiModelProperty(value = "申報點數(金額)", example = "21000", required = false)
  @Column(name = "DRG_DOT")
  private Integer drgDot;

  public String getMrId() {
    return mrId;
  }

  public void setMrId(String mrId) {
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
  
}
