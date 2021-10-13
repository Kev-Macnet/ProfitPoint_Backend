/**
 * Created on 2021/5/21.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.constant.DATA_TYPE;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;

@ApiModel("參數值設定")
public class ParameterValue extends StartEndPayload implements Serializable {
  
  private static final long serialVersionUID = -8789132016204883887L;

  @ApiModelProperty(value = "參數值", required = true)
  protected Object value;
  
  @ApiModelProperty(value = "狀態", required = false)
  protected String status;
  
  public ParameterValue() {
    
  }
  
  public ParameterValue(PARAMETERS p) {
    if (p.getDataType().intValue() == PARAMETERS.TYPE_LONG) {
      value = Long.parseLong(p.getValue());
    } else if (p.getDataType().intValue() == PARAMETERS.TYPE_FLOAT) {
      value = Float.parseFloat(p.getValue());
    } else if (p.getDataType().intValue() == PARAMETERS.TYPE_INTEGER) {
      value = Integer.parseInt(p.getValue());
    } else if (p.getDataType().intValue() == PARAMETERS.TYPE_STRING) {
      value = p.getValue();
    }
    sdate = p.getStartDate();
    edate = p.getEndDate();
    id = p.getId();
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
  
}
