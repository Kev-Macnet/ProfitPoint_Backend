/**
 * Created on 2021/10/7.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("分配點數列表response")
public class AssignedPointsListResponse extends BasePageResponse {

  private static final long serialVersionUID = -2460584758673687979L;

  @ApiModelProperty(value = "分配點數列表陣列", required = false)
  public List<AssignedPointsListPayload> data;

  public List<AssignedPointsListPayload> getData() {
    return data;
  }

  public void setData(List<AssignedPointsListPayload> data) {
    this.data = data;
  }
  
  
}
