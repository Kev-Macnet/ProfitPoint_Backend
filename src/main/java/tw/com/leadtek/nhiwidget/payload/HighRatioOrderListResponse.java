/**
 * Created on 2021/10/6.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("應用比例偏高醫令列表")
public class HighRatioOrderListResponse extends BasePageResponse {

  private static final long serialVersionUID = -1007917380811298809L;

  @ApiModelProperty(value = "應用比例偏高醫令陣列", required = false)
  public List<HighRatioOrderListPayload> data;

  public List<HighRatioOrderListPayload> getData() {
    return data;
  }

  public void setData(List<HighRatioOrderListPayload> data) {
    this.data = data;
  }
  
}
