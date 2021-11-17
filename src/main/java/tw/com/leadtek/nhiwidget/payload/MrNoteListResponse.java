/**
 * Created on 2021/11/17.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("全部病歷資料備註/核刪註記陣列")
public class MrNoteListResponse extends BaseResponse implements Serializable {

  private static final long serialVersionUID = -956339985447822812L;
  
  @ApiModelProperty(value = "全部病歷資料備註/核刪註記陣列", required = false)
  private List<MrNotePayload> data;
  
  public MrNoteListResponse() {
    
  }
  
  public List<MrNotePayload> getData() {
    return data;
  }

  public void setData(List<MrNotePayload> data) {
    this.data = data;
  }
}
