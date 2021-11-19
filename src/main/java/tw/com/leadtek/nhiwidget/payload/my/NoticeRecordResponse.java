/**
 * Created on 2021/11/18.
 */
package tw.com.leadtek.nhiwidget.payload.my;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BasePageResponse;

@ApiModel("我的清單-通知記錄清單")
public class NoticeRecordResponse extends BasePageResponse implements Serializable {

  private static final long serialVersionUID = 110786542057694035L;

  @ApiModelProperty(value = "通知記錄陣列", required = false)
  private List<NoticeRecord> data;
  
  @ApiModelProperty(value = "疑問標示總病歷數", required = false)
  private Integer questionMark = 0;
  
  @ApiModelProperty(value = "已通知數", example = "5", required = false)
  private Integer noticeTimes = 0;
  
  @ApiModelProperty(value = "疑問標示已讀取數", example = "3", required = false)
  private Integer readed = 0;
  
  @ApiModelProperty(value = "疑問標示未讀取數", example = "0", required = false)
  private Integer unread = 0;
  
  public NoticeRecordResponse() {
    
  }

  public List<NoticeRecord> getData() {
    return data;
  }

  public void setData(List<NoticeRecord> data) {
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
