/**
 * Created on 2021/5/20.
 */
package tw.com.leadtek.nhiwidget.payload;

import io.swagger.annotations.ApiModelProperty;

public class PointsStatus extends ParameterPayload {

  @ApiModelProperty(value = "西醫是否採用總點數分配", required = true)
  protected Boolean wmStatus;
  
  @ApiModelProperty(value = "西醫是否採用總點數分配", required = true)
  protected Boolean dentistStatus;

  public Boolean getWmStatus() {
    return wmStatus;
  }

  public void setWmStatus(Boolean wmStatus) {
    this.wmStatus = wmStatus;
  }

  public Boolean getDentistStatus() {
    return dentistStatus;
  }

  public void setDentistStatus(Boolean dentistStatus) {
    this.dentistStatus = dentistStatus;
  }
  
}
