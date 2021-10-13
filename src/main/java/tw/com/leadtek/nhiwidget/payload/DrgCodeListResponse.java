/**
 * Created on 2021/9/7.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("DRG code list")
public class DrgCodeListResponse extends BasePageResponse implements Serializable {

  private static final long serialVersionUID = 3260538987375054494L;

  @ApiModelProperty(value = "DRG資料陣列", required = false)
  public List<DrgCodePayload> data;

  public List<DrgCodePayload> getData() {
    return data;
  }

  public void setData(List<DrgCodePayload> data) {
    this.data = data;
  }
  
}
