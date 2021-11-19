/**
 * Created on 2021/11/18.
 */
package tw.com.leadtek.nhiwidget.payload.my;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.model.rdb.MR_NOTICE;

@ApiModel("我的清單-待辦事項")
public class NoticeRecord extends QuestionMark {

  private static final long serialVersionUID = 3429653215537999026L;

  @ApiModelProperty(value = "資料狀態", example = "待處理", required = true)
  protected String status;
  
  public NoticeRecord() {
    
  }
  
  public NoticeRecord(MR_NOTICE mr) {
    mrId = mr.getMrId();
    sdate = mr.getStartDate();
    edate = mr.getEndDate();
    inhMrId = mr.getInhMrId();
    name = mr.getName();
    inhClinicId = mr.getInhClinicId();
    funcType = mr.getFuncType();
    funcTypeC = mr.getFuncTypec();
    prsnId = mr.getPrsnId();
    prsnName = mr.getPrsnName();
    applId = mr.getApplId();
    applName = mr.getApplName();
    status = MR_STATUS.toStatusString(mr.getStatus());
    noticeDate = mr.getNoticeDate();
    noticeName = removeDot(mr.getNoticeName());
    noticeTimes = mr.getSeq();
    noticePpl = mr.getNoticePpl();
    readedStatus = mr.getReadedPpl().intValue() > 0 ? "已讀取" : "未讀取";
    readedName = removeDot(mr.getReadedName());
    readedPpl = mr.getReadedPpl();
  }
  
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
