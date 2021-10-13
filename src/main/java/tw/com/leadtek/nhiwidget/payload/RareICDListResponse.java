/**
 * Created on 2021/9/29.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("取得罕見ICD列表")
public class RareICDListResponse extends BasePageResponse implements Serializable {

  private static final long serialVersionUID = 4455916037271940335L;
  
  @ApiModelProperty(value = "罕見ICD資料陣列", required = false)
  private List<RareICDListPayload> data;
    
  public RareICDListResponse() {
    
  }

  public List<RareICDListPayload> getData() {
    return data;
  }

  public void setData(List<RareICDListPayload> data) {
    this.data = data;
  }
  
}
