/**
 * Created on 2021/10/12.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("同性質藥物開立列表")
public class SameATCListResponse extends BasePageResponse implements Serializable {

  private static final long serialVersionUID = 2626549418656876550L;

  @ApiModelProperty(value = "同性質藥物陣列", required = false)
  public List<SameATCListPayload> data;

  public List<SameATCListPayload> getData() {
    return data;
  }

  public void setData(List<SameATCListPayload> data) {
    this.data = data;
  }
  
}
