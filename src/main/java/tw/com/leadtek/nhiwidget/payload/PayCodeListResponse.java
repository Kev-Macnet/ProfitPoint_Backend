/**
 * Created on 2021/9/11.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("代碼品項資料")
public class PayCodeListResponse extends BasePageResponse implements Serializable{

  private static final long serialVersionUID = -1547296877696270321L;
  
  @ApiModelProperty(value = "代碼品項陣列", required = false)
  protected List<PayCode> data;

  public List<PayCode> getData() {
    return data;
  }

  public void setData(List<PayCode> data) {
    this.data = data;
  }

}
