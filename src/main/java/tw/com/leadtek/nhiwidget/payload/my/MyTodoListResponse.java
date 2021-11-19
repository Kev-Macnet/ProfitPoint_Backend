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
public class MyTodoListResponse extends BasePageResponse implements Serializable {

  private static final long serialVersionUID = -883489690826884051L;

  @ApiModelProperty(value = "待辦事項陣列", required = false)
  private List<MyTodoList> data;
  
  @ApiModelProperty(value = "待處理病歷數", example = "5", required = false)
  private Integer waitProcess = 0;
  
  @ApiModelProperty(value = "疑問標示病歷數", example = "3", required = false)
  private Integer questionMark = 0;
  
  public List<MyTodoList> getData() {
    return data;
  }

  public void setData(List<MyTodoList> data) {
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

}
