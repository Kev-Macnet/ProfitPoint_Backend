/**
 * Created on 2021/5/20.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import io.swagger.annotations.ApiModelProperty;

public class PointsValue extends StartEndPayload implements Serializable {

  private static final long serialVersionUID = 6399616008383699336L;

  @ApiModelProperty(value = "西醫門急診分配點數", required = false)
  protected Long wmOpPoints;
  
  @ApiModelProperty(value = "西醫住院分配點數", required = false)
  protected Long wmIpPoints;
  
  @ApiModelProperty(value = "西醫藥品分配點數", required = false)
  protected Long wmDrugPoints;
  
  @ApiModelProperty(value = "透析分配點數", required = false)
  protected Long hemodialysisPoints;
  
  @ApiModelProperty(value = "其他專款分配點數", required = false)
  protected Long fundPoints;
  
  @ApiModelProperty(value = "牙醫門診分配點數", required = false)
  protected Long dentistOpPoints;
  
  @ApiModelProperty(value = "牙醫藥品分配點數", required = false)
  protected Long dentistDrugPoints;
  
  @ApiModelProperty(value = "牙醫專款分配點數", required = false)
  protected Long dentistFundPoints;

  public Long getWmOpPoints() {
    return wmOpPoints;
  }

  public void setWmOpPoints(Long wmOpPoints) {
    this.wmOpPoints = wmOpPoints;
  }

  public Long getWmIpPoints() {
    return wmIpPoints;
  }

  public void setWmIpPoints(Long wmIpPoints) {
    this.wmIpPoints = wmIpPoints;
  }

  public Long getWmDrugPoints() {
    return wmDrugPoints;
  }

  public void setWmDrugPoints(Long wmDrugPoints) {
    this.wmDrugPoints = wmDrugPoints;
  }

  public Long getHemodialysisPoints() {
    return hemodialysisPoints;
  }

  public void setHemodialysisPoints(Long hemodialysisPoints) {
    this.hemodialysisPoints = hemodialysisPoints;
  }

  public Long getFundPoints() {
    return fundPoints;
  }

  public void setFundPoints(Long fundPoints) {
    this.fundPoints = fundPoints;
  }

  public Long getDentistOpPoints() {
    return dentistOpPoints;
  }

  public void setDentistOpPoints(Long dentistOpPoints) {
    this.dentistOpPoints = dentistOpPoints;
  }

  public Long getDentistDrugPoints() {
    return dentistDrugPoints;
  }

  public void setDentistDrugPoints(Long dentistDrugPoints) {
    this.dentistDrugPoints = dentistDrugPoints;
  }

  public Long getDentistFundPoints() {
    return dentistFundPoints;
  }

  public void setDentistFundPoints(Long dentistFundPoints) {
    this.dentistFundPoints = dentistFundPoints;
  }
  
}
