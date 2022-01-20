/**
 * Created on 2022/1/13.
 */
package tw.com.leadtek.nhiwidget.payload.system;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("比對警示功能設定")
public class CompareWarningPayload extends BaseResponse {

  private static final long serialVersionUID = -8061918930663221216L;

  @ApiModelProperty(value = "比對條件：0:關閉，1:比對特定時間，2:限定科別", required = true)
  protected Integer compareBy;
  
  @ApiModelProperty(value = "限定科別中文名稱，若多筆用空格隔開", required = false)
  protected String funcType;
  
  @ApiModelProperty(value = "限定醫護中文名稱，若多筆用空格隔開", required = false)
  protected String doctor;
  
  @ApiModelProperty(value = "限定回朔當日資料往前推算幾小時", required = false)
  protected Integer rollbackHour;
  
  public CompareWarningPayload() {
    
  }
  
  public CompareWarningPayload(List<PARAMETERS> list) {
    if (list == null || list.size() == 0) {
      return;
    }
    for (PARAMETERS parameters : list) {
      if (parameters.getName().equals("COMPARE_BY")) {
        compareBy = Integer.parseInt(parameters.getValue());
      } else if (parameters.getName().equals("COMPARE_FUNC_TYPE")) {
        funcType = parameters.getValue();
      } else if (parameters.getName().equals("COMPARE_DOCTOR")) {
        doctor = parameters.getValue();
      } else if (parameters.getName().equals("ROLLBACK_HOUR")) {
        rollbackHour = Integer.parseInt(parameters.getValue());
      }
    }
  }

  public Integer getCompareBy() {
    return compareBy;
  }

  public void setCompareBy(Integer compareBy) {
    this.compareBy = compareBy;
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

  public Integer getRollbackHour() {
    return rollbackHour;
  }

  public void setRollbackHour(Integer rollbackHour) {
    this.rollbackHour = rollbackHour;
  }
  
  
}
