/**
 * Created on 2021/11/16.
 */
package tw.com.leadtek.nhiwidget.payload.my;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BasePageResponse;

@ApiModel("我的清單-待辦事項陣列")
public class MyOrderListResponse extends BasePageResponse implements Serializable {

  private static final long serialVersionUID = -883489690826884051L;

  @ApiModelProperty(value = "待辦事項陣列", required = false)
  private List<MyOrderPayload> data;
  
  @ApiModelProperty(value = "待處理病歷數", example = "5", required = false)
  private Integer waitProcess;
  
  @ApiModelProperty(value = "疑問標示病歷數", example = "3", required = false)
  private Integer questionMark;
  
  @ApiModelProperty(value = "疑問標示已讀取數", example = "3", required = false)
  private Integer readed;
  
  @ApiModelProperty(value = "疑問標示未讀取數", example = "0", required = false)
  private Integer unread;

  public List<MyOrderPayload> getData() {
    return data;
  }

  public void setData(List<MyOrderPayload> data) {
    this.data = data;
  }

  public Integer getWaitProcess() {
    return waitProcess;
  }

  public void setWaitProcess(Integer waitProcess) {
    this.waitProcess = waitProcess;
  }

  public Integer getQuestionMark() {
    return questionMark;
  }

  public void setQuestionMark(Integer questionMark) {
    this.questionMark = questionMark;
  }

  public Integer getReaded() {
    return readed;
  }

  public void setReaded(Integer readed) {
    this.readed = readed;
  }

  public Integer getUnread() {
    return unread;
  }

  public void setUnread(Integer unread) {
    this.unread = unread;
  }

}
