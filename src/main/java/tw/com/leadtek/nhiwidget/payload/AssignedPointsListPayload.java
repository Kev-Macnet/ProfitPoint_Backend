/**
 * Created on 2021/10/7.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.ASSIGNED_POINT;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;

@ApiModel("分配點數列表")
public class AssignedPointsListPayload extends StartEndPayload implements Serializable {

  private static final long serialVersionUID = -6378588410268637063L;

  /**
   * 西醫總點數
   */
  public final static String WM = "WM_P";

  /**
   * 牙醫總點數
   */
  public final static String DENTIST = "DENTIST_P";


  @ApiModelProperty(value = "西醫總點數", example = "10000000", required = true)
  private Long wmP;

  @ApiModelProperty(value = "牙醫總點數", example = "9000000", required = true)
  private Long dP;

  public AssignedPointsListPayload() {

  }
  
  public AssignedPointsListPayload(ASSIGNED_POINT ap) {
    id = ap.getId();
    wmP = ap.getWmp();
    dP = ap.getDp();
    sdate = ap.getStartDate();
    edate = ap.getEndDate();
  }

  public AssignedPointsListPayload(PARAMETERS p) {
    sdate = p.getStartDate();
    edate = p.getEndDate();
  }

  public void updateValue(PARAMETERS p) {
    if (p.getName().equals(WM)) {
      if (p.getValue() != null && p.getValue().length() > 0) {
        wmP = Long.parseLong(p.getValue());
      } else {
        wmP = 0L;
      }
      id = p.getId();
    } else if (p.getName().equals(DENTIST)) {
      if (p.getValue() != null && p.getValue().length() > 0) {
        dP = Long.parseLong(p.getValue());
      } else {
        dP = 0L;
      }
    }
  }

  public Long getWmP() {
    return wmP;
  }

  public void setWmP(Long wmP) {
    this.wmP = wmP;
  }

  public Long getdP() {
    return dP;
  }

  public void setdP(Long dP) {
    this.dP = dP;
  }


}
