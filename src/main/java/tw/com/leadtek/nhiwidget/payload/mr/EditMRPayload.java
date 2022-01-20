/**
 * Created on 2022/1/6.
 */
package tw.com.leadtek.nhiwidget.payload.mr;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

/**
 * @author kenlai
 */
@ApiModel("開始/取消編輯病歷資料的 response")
public class EditMRPayload extends BaseResponse implements Serializable {

  private static final long serialVersionUID = -3805041756078582471L;
  
  @ApiModelProperty(value = "編輯動作id，以識別不同分頁同時編輯狀態", example = "1", required = true)
  private Integer actionId;
  
  public EditMRPayload() {
    
  }

  public Integer getActionId() {
    return actionId;
  }

  public void setActionId(Integer actionId) {
    this.actionId = actionId;
  }
  
}
