/**
 * Created on 2021/8/10.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import io.swagger.annotations.ApiModelProperty;

public class MRStatusCount implements Serializable {

  private static final long serialVersionUID = -8376275434049234683L;

  @ApiModelProperty(value = "評估不調整病歷數", required = true)
  protected Integer dontChange;
  
  @ApiModelProperty(value = "疑問標示病歷數", required = true)
  protected Integer questionMark;

  @ApiModelProperty(value = "待確認病歷數", required = true)
  protected Integer waitConfirm;
  
  @ApiModelProperty(value = "待處理病歷數", required = true)
  protected Integer waitProcess;
  
  @ApiModelProperty(value = "無需變更病歷數", required = true)
  protected Integer noChange;

  @ApiModelProperty(value = "優化完成病歷數", required = true)
  protected Integer optimized;
  
  @ApiModelProperty(value = "疾病分類完成病歷數", required = true)
  protected Integer classified;
  
  
  public MRStatusCount() {
    dontChange = 0;
    questionMark = 0;
    waitConfirm = 0;
    waitProcess = 0;
    noChange = 0;
    optimized = 0;
    classified = 0;
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
  
  public Integer getWaitProcess() {
    return waitProcess;
  }

  public void setWaitProcess(Integer waitProcess) {
    this.waitProcess = waitProcess;
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
  
  public void updateStatusCount(int count, String statusName) {
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
