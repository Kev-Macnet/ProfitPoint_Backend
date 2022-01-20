/**
 * Created on 2022/1/11.
 */
package tw.com.leadtek.nhiwidget.payload.mr;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("DRG編審清單response")
public class DrgListPayload extends BaseResponse {

  private static final long serialVersionUID = -3354313051153371133L;

  @ApiModelProperty(value = "DRG編審清單", required = false)
  private List<DrgCalPayload> data;

  public List<DrgCalPayload> getData() {
    return data;
  }

  public void setData(List<DrgCalPayload> data) {
    this.data = data;
  }

}
