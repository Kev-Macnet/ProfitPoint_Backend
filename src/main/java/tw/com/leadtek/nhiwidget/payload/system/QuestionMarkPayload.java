/**
 * Created on 2022/1/13.
 */
package tw.com.leadtek.nhiwidget.payload.system;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("疑問標示通知功能設定")
public class QuestionMarkPayload extends BaseResponse{

  private static final long serialVersionUID = 8450807003456215889L;

  @ApiModelProperty(value = "疑問提示通知功能設定：0:關閉，1:系統所有疑問標示狀態就醫記錄皆可通知，2:限定科別", required = true)
  protected Integer markBy;
  
  @ApiModelProperty(value = "限定科別中文名稱，若多筆用空格隔開", required = false)
  protected String funcType;
  
  @ApiModelProperty(value = "限定醫護中文名稱，若多筆用空格隔開", required = false)
  protected String doctor;
  
  public QuestionMarkPayload() {
    
  }
  
  public QuestionMarkPayload(List<PARAMETERS> list) {
    if (list == null || list.size() == 0) {
      return;
    }
    for (PARAMETERS parameters : list) {
      if (parameters.getName().equals("MARK_BY")) {
        markBy = Integer.parseInt(parameters.getValue());
      } else if (parameters.getName().equals("MARK_FUNC_TYPE")) {
        funcType = parameters.getValue();
      } else if (parameters.getName().equals("MARK_DOCTOR")) {
        doctor = parameters.getValue();
      }
    }
  }

  public Integer getMarkBy() {
    return markBy;
  }

  public void setMarkBy(Integer markBy) {
    this.markBy = markBy;
  }

  public String getFuncType() {
    return funcType;
  }

  public void setFuncType(String funcType) {
    this.funcType = funcType;
  }

  public String getDoctor() {
    return doctor;
  }

  public void setDoctor(String doctor) {
    this.doctor = doctor;
  }
  
}
