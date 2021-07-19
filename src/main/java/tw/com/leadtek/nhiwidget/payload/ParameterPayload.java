/**
 * Created on 2021/5/20.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("參數設定")
public class ParameterPayload {

  @ApiModelProperty(value = "生效日", required = false)
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  protected Date startDate;
  
  @ApiModelProperty(value = "失效日", required = false)
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  protected Date endDate;

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }
  
}
