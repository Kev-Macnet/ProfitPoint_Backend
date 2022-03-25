/**
 * Created on 2021/3/17.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;

@ApiModel("病歷總數及狀態統計回覆訊息")
public class MRCount extends MRStatusCount implements Serializable {

  private static final long serialVersionUID = 4374361249049185183L;

  @ApiModelProperty(value = "申請點數總計", required = true)
  protected Integer applDot;

  @ApiModelProperty(value = "申請件數總計", required = true)
  protected Integer applSum;

  @ApiModelProperty(value = "申請日數總計", required = true)
  protected Integer dayCount;

  @ApiModelProperty(value = "總病歷數", required = true)
  protected Integer totalMr;
  
  @ApiModelProperty(value = "DRG病歷數", required = true)
  protected Integer drg;

  public MRCount() {
    dontChange = 0;
    questionMark = 0;
    waitConfirm = 0;
    applDot = 0;
    applSum = 0;
    dayCount = 0;
    waitProcess = 0;
    totalMr = 0;
    noChange = 0;
    optimized = 0;
    classified = 0;
    drg = 0;
  }

  public Integer getApplDot() {
    return applDot;
  }

  public void setApplDot(Integer applDot) {
    this.applDot = applDot;
  }

  public Integer getApplSum() {
    return applSum;
  }

  public void setApplSum(Integer applSum) {
    this.applSum = applSum;
  }

  public Integer getDayCount() {
    return dayCount;
  }

  public void setDayCount(Integer dayCount) {
    this.dayCount = dayCount;
  }

  public Integer getTotalMr() {
    return totalMr;
  }

  public void setTotalMr(Integer totalMr) {
    this.totalMr = totalMr;
  }
  
  public Integer getDrg() {
    return drg;
  }

  public void setDrg(Integer drg) {
    this.drg = drg;
  }

  public void updateValues(Map<String, Object> map) {
    for (String mapKey : map.keySet()) {
      if ("TOTAL_MR".equals(mapKey)) {
        totalMr = ((BigInteger)map.get(mapKey)).intValue();
      } else if ("APPL_SUM".equals(mapKey)) {
        applSum = ((BigInteger) map.get(mapKey)).intValue();
      } else if ("APPL_DOT".equals(mapKey)) {
        applDot = ((Integer)map.get(mapKey)).intValue();
      }
    }
  }

  public void updateMrStatusCount(List<Map<String, Object>> list) {
    for (MR_STATUS status : MR_STATUS.values()) {
      boolean isFound = false;
      for (Map<String, Object> map : list) {
        if (((Integer)map.get("STATUS")).intValue() == status.value()) {
          updateStatusCount(((BigInteger)map.get("STATUS_SUM")).intValue(), status.name());
          isFound = true;
        }
      }
      if (!isFound) {
        updateStatusCount(0, status.name());
      }
    }
  }

}
