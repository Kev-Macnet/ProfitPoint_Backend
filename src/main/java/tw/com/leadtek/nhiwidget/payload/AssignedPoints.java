/**
 * Created on 2021/10/7.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.ASSIGNED_POINT;

@ApiModel("分配點數詳細資料")
public class AssignedPoints extends StartEndPayload implements Serializable {

  private static final long serialVersionUID = -4694306579598385624L;

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
  
  public AssignedPoints() {
    
  }
  
  public AssignedPoints(ASSIGNED_POINT ap) {
    wmOpPoints = ap.getWmOpPoints();
    wmIpPoints = ap.getWmIpPoints();
    wmDrugPoints = ap.getWmDrugPoints();
    hemodialysisPoints = ap.getHemodialysisPoints();
    fundPoints = ap.getFundPoints();
    dentistOpPoints = ap.getDentistOpPoints();
    dentistDrugPoints = ap.getDentistDrugPoints();
    dentistFundPoints = ap.getDentistFundPoints();
    sdate = ap.getStartDate();
    edate = ap.getEndDate();
    id = ap.getId();
  }
  
  public ASSIGNED_POINT toDB() {
    ASSIGNED_POINT result = new ASSIGNED_POINT();
    result.setId(id);
    result.setDentistDrugPoints(dentistDrugPoints);
    result.setDentistFundPoints(dentistFundPoints);
    result.setDentistOpPoints(dentistOpPoints);
    result.setDp(result.getDentistDrugPoints().longValue() + result.getDentistFundPoints().longValue() + result.getDentistOpPoints().longValue());
    result.setEndDate(edate);
    result.setStartDate(sdate);
    result.setFundPoints(fundPoints);
    result.setHemodialysisPoints(hemodialysisPoints);
    result.setUpdateAt(new Date());
    result.setWmDrugPoints(wmDrugPoints);
    result.setWmIpPoints(wmIpPoints);
    result.setWmOpPoints(wmOpPoints);
    result.setWmp(result.getWmDrugPoints().longValue() + result.getWmIpPoints().longValue() + result.getWmOpPoints().longValue());
    return result;
  }

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
  
  public void refreshValues() {
    if (wmOpPoints == null) {
      wmOpPoints = new Long(0);
    }
    if (wmIpPoints == null) {
      wmOpPoints = new Long(0);
    }
    if (wmDrugPoints == null) {
      wmDrugPoints = new Long(0);
    }
    if (hemodialysisPoints == null) {
      hemodialysisPoints = new Long(0);
    }
    if (fundPoints == null) {
      fundPoints = new Long(0);
    }
    if (dentistOpPoints == null) {
      dentistOpPoints = new Long(0);
    }
    if (dentistDrugPoints == null) {
      dentistDrugPoints = new Long(0);
    }
    if (dentistFundPoints == null) {
      dentistFundPoints = new Long(0);
    }
  }
}
