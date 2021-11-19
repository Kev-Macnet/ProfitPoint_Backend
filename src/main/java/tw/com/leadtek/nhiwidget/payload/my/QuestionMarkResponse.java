/**
 * Created on 2021/11/18.
 */
package tw.com.leadtek.nhiwidget.payload.my;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BasePageResponse;

@ApiModel("我的清單-疑問標示報告")
public class QuestionMarkResponse extends BasePageResponse implements Serializable {

  private static final long serialVersionUID = 2573322298654017075L;

  @ApiModelProperty(value = "疑問標示陣列", required = false)
  private List<QuestionMark> data;
  
  @ApiModelProperty(value = "疑問標示總病歷數", required = false)
  private Integer questionMark = 0;
  
  @ApiModelProperty(value = "已通知數", example = "5", required = false)
  private Integer noticeTimes = 0;
  
  @ApiModelProperty(value = "未通知數", example = "5", required = false)
  private Integer nonNoticeTimes = 0;
  
  @ApiModelProperty(value = "疑問標示已讀取數", example = "3", required = false)
  private Integer readed = 0;
  
  @ApiModelProperty(value = "疑問標示未讀取數", example = "0", required = false)
  private Integer unread = 0;
  
  public QuestionMarkResponse() {
    
  }

  public List<QuestionMark> getData() {
    return data;
  }

  public void setData(List<QuestionMark> data) {
    this.data = data;
  }

  public Integer getQuestionMark() {
    return questionMark;
  }

  public void setQuestionMark(Integer questionMark) {
    this.questionMark = questionMark;
  }

  public Integer getNoticeTimes() {
    return noticeTimes;
  }

  public void setNoticeTimes(Integer noticeTimes) {
    this.noticeTimes = noticeTimes;
  }

  public Integer getNonNoticeTimes() {
    return nonNoticeTimes;
  }

  public void setNonNoticeTimes(Integer nonNoticeTimes) {
    this.nonNoticeTimes = nonNoticeTimes;
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
