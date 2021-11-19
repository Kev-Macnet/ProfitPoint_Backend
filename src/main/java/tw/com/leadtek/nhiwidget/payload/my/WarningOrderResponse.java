/**
 * Created on 2021/11/18.
 */
package tw.com.leadtek.nhiwidget.payload.my;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BasePageResponse;

@ApiModel("我的清單-比對警示")
public class WarningOrderResponse extends BasePageResponse implements Serializable {

  private static final long serialVersionUID = 5238081242884342782L;

  @ApiModelProperty(value = "比對警示陣列", required = false)
  private List<WarningOrder> data;
  
  @ApiModelProperty(value = "已通知數", example = "5", required = false)
  private Integer noticeTimes = 0;
  
  @ApiModelProperty(value = "未通知數", example = "5", required = false)
  private Integer nonNoticeTimes = 0;  
  
  @ApiModelProperty(value = "警示總病歷數", example = "10", required = false)
  private Integer warning = 0;
  
  @ApiModelProperty(value = "ICD碼異動數", example = "2", required = false)
  private Integer changeIcd = 0;
  
  @ApiModelProperty(value = "院內碼異動數", example = "2", required = false)
  private Integer changeInh = 0;
  
  @ApiModelProperty(value = "支付標準碼異動數", example = "2", required = false)
  private Integer changeOrder = 0;
  
  @ApiModelProperty(value = "其他資訊異動數", example = "2", required = false)
  private Integer changeOther = 0;
  
  @ApiModelProperty(value = "SO醫囑異動數", example = "2", required = false)
  private Integer changeSo = 0;
  
  public WarningOrderResponse() {
    
  }

  public List<WarningOrder> getData() {
    return data;
  }

  public void setData(List<WarningOrder> data) {
    this.data = data;
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

  public Integer getWarning() {
    return warning;
  }

  public void setWarning(Integer warning) {
    this.warning = warning;
  }

  public Integer getChangeIcd() {
    return changeIcd;
  }

  public void setChangeIcd(Integer changeIcd) {
    this.changeIcd = changeIcd;
  }

  public Integer getChangeInh() {
    return changeInh;
  }

  public void setChangeInh(Integer changeInh) {
    this.changeInh = changeInh;
  }

  public Integer getChangeOrder() {
    return changeOrder;
  }

  public void setChangeOrder(Integer changeOrder) {
    this.changeOrder = changeOrder;
  }

  public Integer getChangeOther() {
    return changeOther;
  }

  public void setChangeOther(Integer changeOther) {
    this.changeOther = changeOther;
  }

  public Integer getChangeSo() {
    return changeSo;
  }

  public void setChangeSo(Integer changeSo) {
    this.changeSo = changeSo;
  }
  
}
