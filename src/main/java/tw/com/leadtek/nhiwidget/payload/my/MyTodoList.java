/**
 * Created on 2021/11/18.
 */
package tw.com.leadtek.nhiwidget.payload.my;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.model.rdb.MY_MR;

@ApiModel("我的清單-待辦事項")
public class MyTodoList extends MyListBasePayload implements Serializable {

  private static final long serialVersionUID = -1743762001535065967L;
  
  @ApiModelProperty(value = "病歷點數", example = "400", required = false)
  protected Integer totalDot;
  
  @ApiModelProperty(value = "資料狀態", example = "待處理", required = true)
  protected String status;
  
  public MyTodoList() {
    
  }
  
  public MyTodoList(MY_MR mr) {
    super(mr);
    totalDot = mr.getTDot();
    status = MR_STATUS.toStatusString(mr.getStatus());
  }

  public Integer getTotalDot() {
    return totalDot;
  }

  public void setTotalDot(Integer totalDot) {
    this.totalDot = totalDot;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

}
