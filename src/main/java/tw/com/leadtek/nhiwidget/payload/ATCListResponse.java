/**
 * Created on 2021/9/9.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.ATC;

@ApiModel("ACT資料陣列")
public class ATCListResponse extends BasePageResponse implements Serializable {

  private static final long serialVersionUID = 3260538987375054495L;

  @ApiModelProperty(value = "ATC資料陣列", required = false)
  public List<ATC> data;

  public List<ATC> getData() {
    return data;
  }

  public void setData(List<ATC> data) {
    this.data = data;
  }
  
}
