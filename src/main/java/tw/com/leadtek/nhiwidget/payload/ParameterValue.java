/**
 * Created on 2021/5/21.
 */
package tw.com.leadtek.nhiwidget.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("參數值設定")
public class ParameterValue extends ParameterPayload {

  @ApiModelProperty(value = "參數值", required = true)
  protected Integer value;

  public Integer getValue() {
    return value;
  }

  public void setValue(Integer value) {
    this.value = value;
  }
  
}
