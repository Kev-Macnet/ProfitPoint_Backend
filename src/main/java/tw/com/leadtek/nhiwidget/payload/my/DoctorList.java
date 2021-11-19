/**
 * Created on 2021/11/18.
 */
package tw.com.leadtek.nhiwidget.payload.my;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.MY_MR;

@ApiModel("我的清單-醫師查看清單")
public class DoctorList extends MyListBasePayload implements Serializable {

  private static final long serialVersionUID = 2776731144116633469L;

  @ApiModelProperty(value = "通知次數", example = "3", required = false)
  protected Integer noticeTimes = 0;
  
  @ApiModelProperty(value = "讀取狀態", example = "未讀取", required = false)
  protected String readedStatus;
  
  public DoctorList() {
    
  }
  
  public DoctorList(MY_MR mr) {
    super(mr);
    
    noticeTimes = mr.getNoticeSeq();
    // todo
    //readedStatus = mr.getReadedPpl().intValue() > 0 ? "已讀取" : "未讀取";
  }

  public Integer getNoticeTimes() {
    return noticeTimes;
  }

  public void setNoticeTimes(Integer noticeTimes) {
    this.noticeTimes = noticeTimes;
  }

  public String getReadedStatus() {
    return readedStatus;
  }

  public void setReadedStatus(String readedStatus) {
    this.readedStatus = readedStatus;
  }
  
}
