/**
 * Created on 2021/11/18.
 */
package tw.com.leadtek.nhiwidget.payload.my;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.model.rdb.MY_MR;

@ApiModel("我的清單-比對警示")
public class WarningOrder extends MyListBasePayload implements Serializable {
  
  private static final long serialVersionUID = 5971151177610105754L;
  
  public static final String CHANGED = "✔";
  
  public static final String NOCHANGE = "ー";

  @ApiModelProperty(value = "資料狀態", example = "待處理", required = true)
  protected String status;
  
  @ApiModelProperty(value = "通知狀態", example = "未通知", required = false)
  protected String noticeStatus;
  
  @ApiModelProperty(value = "ICD診斷碼是否異動", example = "ν", required = false)
  protected String changeIcd;

  @ApiModelProperty(value = "院內碼是否異動", example = "-", required = false)
  private String changeInh;

  @ApiModelProperty(value = "支付標準碼是否異動", example = "-", required = false)
  private String changeOrder;

  @ApiModelProperty(value = "其他資訊是否異動", example = "ν", required = false)
  private String changeOther;

  @ApiModelProperty(value = "SO醫囑是否異動", example = "ν", required = false)
  private String changeSo;
  
  public WarningOrder() {
    
  }
  
  public WarningOrder(MY_MR mr) {
    super(mr);
    status = MR_STATUS.toStatusString(mr.getStatus());
    noticeStatus = (mr.getNoticeDate() == null) ?  "未通知" : "已通知";
    changeIcd = mr.getChangeIcd().intValue() == 1 ? CHANGED : NOCHANGE;
    changeInh = mr.getChangeInh().intValue() == 1 ? CHANGED : NOCHANGE;
    changeOrder = mr.getChangeOrder().intValue() == 1 ? CHANGED : NOCHANGE;
    changeOther = mr.getChangeOther().intValue() == 1 ? CHANGED : NOCHANGE;
    changeSo = mr.getChangeSo().intValue() == 1 ? CHANGED : NOCHANGE;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getNoticeStatus() {
    return noticeStatus;
  }

  public void setNoticeStatus(String noticeStatus) {
    this.noticeStatus = noticeStatus;
  }

  public String getChangeIcd() {
    return changeIcd;
  }

  public void setChangeIcd(String changeIcd) {
    this.changeIcd = changeIcd;
  }

  public String getChangeInh() {
    return changeInh;
  }

  public void setChangeInh(String changeInh) {
    this.changeInh = changeInh;
  }

  public String getChangeOrder() {
    return changeOrder;
  }

  public void setChangeOrder(String changeOrder) {
    this.changeOrder = changeOrder;
  }

  public String getChangeOther() {
    return changeOther;
  }

  public void setChangeOther(String changeOther) {
    this.changeOther = changeOther;
  }

  public String getChangeSo() {
    return changeSo;
  }

  public void setChangeSo(String changeSo) {
    this.changeSo = changeSo;
  }
  
  
}
