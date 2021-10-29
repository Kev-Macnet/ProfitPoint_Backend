/**
 * Created on 2021/10/29.
 */
package tw.com.leadtek.nhiwidget.payload.system;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.DEDUCTED;
import tw.com.leadtek.nhiwidget.payload.BasePageResponse;

@ApiModel("核減代碼列表")
public class DeductedListResponse extends BasePageResponse implements Serializable {

  private static final long serialVersionUID = 3173446493166394821L;

  @ApiModelProperty(value = "核減代碼陣列", required = false)
  public List<DEDUCTED> data;

  public List<DEDUCTED> getData() {
    return data;
  }

  public void setData(List<DEDUCTED> data) {
    this.data = data;
  }
  
}
