/**
 * Created on 2021/12/14.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.DEDUCTED_NOTE;

@ApiModel("全部病歷核刪註記")
public class DeductedNoteListResponse extends BaseResponse implements Serializable {

  private static final long serialVersionUID = -720418761890922710L;

  @ApiModelProperty(value = "病歷核刪註記陣列", required = false)
  private List<DEDUCTED_NOTE> data;
  
  public DeductedNoteListResponse() {
    
  }
  
  public List<DEDUCTED_NOTE> getData() {
    return data;
  }

  public void setData(List<DEDUCTED_NOTE> data) {
    this.data = data;
  }
}
