/**
 * Created on 2021/10/8.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModelProperty;

public class ParameterListPayload extends BasePageResponse implements Serializable {

  private static final long serialVersionUID = 3260538987375054495L;

  @ApiModelProperty(value = "參數資料陣列", required = false)
  public List<ParameterValue> data;

  public List<ParameterValue> getData() {
    return data;
  }

  public void setData(List<ParameterValue> data) {
    this.data = data;
  }
  
}
