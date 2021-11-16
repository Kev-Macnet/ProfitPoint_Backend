/**
 * Created on 2021/11/5.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("存放每周點數趨勢資料")
public class PeriodPointWeeklyPayload extends BaseResponse implements Serializable {

  private static final long serialVersionUID = -7414309481942947130L;
  
  @ApiModelProperty(value = "門診點數趨勢", required = true)
  private NameValueList op;
  
  @ApiModelProperty(value = "住院點數趨勢", required = true)
  private NameValueList ip;
  
  @ApiModelProperty(value = "門診自費點數趨勢", required = true)
  private NameValueList ownExpOp;
  
  @ApiModelProperty(value = "住院自費點數趨勢", required = true)
  private NameValueList ownExpIp;
  
  public PeriodPointWeeklyPayload() {
    op = new NameValueList();
    ip = new NameValueList();
    ownExpOp = new NameValueList();
    ownExpIp = new NameValueList();
  }

  public NameValueList getOp() {
    return op;
  }

  public void setOp(NameValueList op) {
    this.op = op;
  }

  public NameValueList getIp() {
    return ip;
  }

  public void setIp(NameValueList ip) {
    this.ip = ip;
  }

  public NameValueList getOwnExpOp() {
    return ownExpOp;
  }

  public void setOwnExpOp(NameValueList ownExpOp) {
    this.ownExpOp = ownExpOp;
  }

  public NameValueList getOwnExpIp() {
    return ownExpIp;
  }

  public void setOwnExpIp(NameValueList ownExpIp) {
    this.ownExpIp = ownExpIp;
  }
  
}
