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
public class MRCount implements Serializable {

  private static final long serialVersionUID = 4374361249049185183L;

  @ApiModelProperty(value = "評估不調整病歷數", required = true)
  private Integer dontChange;

  @ApiModelProperty(value = "疑問標示病歷數", required = true)
  private Integer questionMark;

  @ApiModelProperty(value = "待確認病歷數", required = true)
  private Integer waitConfirm;

  @ApiModelProperty(value = "申請點數總計", required = true)
  private Integer applDot;

  @ApiModelProperty(value = "申請件數總計", required = true)
  private Integer applSum;

  @ApiModelProperty(value = "申請日數總計", required = true)
  private Integer dayCount;

  @ApiModelProperty(value = "待處理病歷數", required = true)
  private Integer waitProcess;

  @ApiModelProperty(value = "總病歷數", required = true)
  private Integer totalMr;

  @ApiModelProperty(value = "無需變更病歷數", required = true)
  private Integer noChange;

  @ApiModelProperty(value = "優化完成病歷數", required = true)
  private Integer optimized;
  
  @ApiModelProperty(value = "疾病分類完成病歷數", required = true)
  private Integer classified;
  
  @ApiModelProperty(value = "DRG病歷數", required = true)
  private Integer drg;
  
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

  public Integer getDontChange() {
    return dontChange;
  }

  public void setDontChange(Integer dontChange) {
    this.dontChange = dontChange;
  }

  public Integer getQuestionMark() {
    return questionMark;
  }

  public void setQuestionMark(Integer questionMark) {
    this.questionMark = questionMark;
  }

  public Integer getWaitConfirm() {
    return waitConfirm;
  }

  public void setWaitConfirm(Integer waitConfirm) {
    this.waitConfirm = waitConfirm;
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

  public Integer getWaitProcess() {
    return waitProcess;
  }

  public void setWaitProcess(Integer waitProcess) {
    this.waitProcess = waitProcess;
  }

  public Integer getTotalMr() {
    return totalMr;
  }

  public void setTotalMr(Integer totalMr) {
    this.totalMr = totalMr;
  }

  public Integer getNoChange() {
    return noChange;
  }

  public void setNoChange(Integer noChange) {
    this.noChange = noChange;
  }

  public Integer getOptimized() {
    return optimized;
  }

  public void setOptimized(Integer optimized) {
    this.optimized = optimized;
  }
  
  public Integer getClassified() {
    return classified;
  }

  public void setClassified(Integer classified) {
    this.classified = classified;
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

  private void updateStatusCount(int count, String statusName) {
    if ("WAIT_CONFIRM".equals(statusName)) {
      waitConfirm = count;
    } else if ("QUESTION_MARK".equals(statusName)) {
      questionMark = count;
    } else if ("WAIT_PROCESS".equals(statusName)) {
      waitProcess = count;
    } else if ("NO_CHANGE".equals(statusName)) {
      noChange = count;
    } else if ("OPTIMIZED".equals(statusName)) {
      optimized = count;
    } else if ("DONT_CHANGE".equals(statusName)) {
      dontChange = count;
    } else if ("CLASSIFIED".equals(statusName)) {
      classified = count;
    }
  }
}
