/**
 * Created on 2021/11/18.
 */
package tw.com.leadtek.nhiwidget.payload.my;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.MY_MR;

@ApiModel("我的清單-疑問標示清單")
public class QuestionMark extends MyListBasePayload implements Serializable {

  private static final long serialVersionUID = 6623111684828368487L;

  @ApiModelProperty(value = "最新通知日期", example = "ν", required = false)
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  protected Date noticeDate;
  
  @ApiModelProperty(value = "通知次數", example = "3", required = false)
  protected Integer noticeTimes;
  
  @ApiModelProperty(value = "通知人數", example = "3", required = false)
  protected Integer noticePpl;
  
  @ApiModelProperty(value = "通知姓名", example = "3", required = false)
  protected String noticeName;
  
  @ApiModelProperty(value = "讀取狀態", example = "未讀取", required = false)
  protected String readedStatus;
  
  @ApiModelProperty(value = "讀取姓名", example = "3", required = false)
  protected String readedName;
  
  @ApiModelProperty(value = "讀取人數", example = "3", required = false)
  protected Integer readedPpl;
  
  public QuestionMark() {
    
  }
  
  public QuestionMark(MY_MR mr) {
    super(mr);
    noticeTimes = mr.getNoticeSeq();
    noticeDate = mr.getNoticeDate();
    noticePpl = mr.getNoticePpl();
    noticeName = removeDot(mr.getNoticeName());
    readedStatus = mr.getReadedPpl().intValue() > 0 ? "已讀取" : "未讀取";
    readedName = removeDot(mr.getReadedName());
    readedPpl = mr.getReadedPpl();
  }

  public Date getNoticeDate() {
    return noticeDate;
  }

  public void setNoticeDate(Date noticeDate) {
    this.noticeDate = noticeDate;
  }

  public Integer getNoticeTimes() {
    return noticeTimes;
  }

  public void setNoticeTimes(Integer noticeTimes) {
    this.noticeTimes = noticeTimes;
  }

  public Integer getNoticePpl() {
    return noticePpl;
  }

  public void setNoticePpl(Integer noticePpl) {
    this.noticePpl = noticePpl;
  }

  public String getNoticeName() {
    return noticeName;
  }

  public void setNoticeName(String noticeName) {
    this.noticeName = noticeName;
  }

  public String getReadedStatus() {
    return readedStatus;
  }

  public void setReadedStatus(String readedStatus) {
    this.readedStatus = readedStatus;
  }

  public String getReadedName() {
    return readedName;
  }

  public void setReadedName(String readedName) {
    this.readedName = readedName;
  }

  public Integer getReadedPpl() {
    return readedPpl;
  }

  public void setReadedPpl(Integer readedPpl) {
    this.readedPpl = readedPpl;
  }
  
}
