/**
 * Created on 2021/10/12.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("健保項目對應自費項目並存列表")
public class CodeConflictListResponse extends BasePageResponse {

  private static final long serialVersionUID = 2791013669032055090L;


  @ApiModelProperty(value = "應用比例偏高醫令陣列", required = false)
  public List<CodeConflictListPayload> data;


  public List<CodeConflictListPayload> getData() {
    return data;
  }


  public void setData(List<CodeConflictListPayload> data) {
    this.data = data;
  }
  
}
