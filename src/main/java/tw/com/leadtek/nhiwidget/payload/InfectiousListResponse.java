/**
 * Created on 2021/9/24.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("法定傳染病列表")
public class InfectiousListResponse extends BasePageResponse implements Serializable {

  private static final long serialVersionUID = 8620362889323481387L;
  
  @ApiModelProperty(value = "法定傳染病陣列", required = false)
  public List<InfectiousPayload> data;

  public List<InfectiousPayload> getData() {
    return data;
  }

  public void setData(List<InfectiousPayload> data) {
    this.data = data;
  }
}
