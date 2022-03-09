/**
 * Created on 2021/10/6.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_THRESHOLD;

@ApiModel("應用比例偏高醫令列表")
public class HighRatioOrderListPayload extends RareICDListPayload implements Serializable {

  private static final long serialVersionUID = 1637348344742006083L;

  @ApiModelProperty(value = "院內碼", example = "J10.01", required = false)
  protected String inhCode;
  
  @ApiModelProperty(value = "單筆總量提示", example = "true", required = false)
  protected Boolean singleAmount;

  @ApiModelProperty(value = "區間用量提示", example = "true", required = false)
  protected Boolean period;
  
  public HighRatioOrderListPayload() {
    
  }
  
  public HighRatioOrderListPayload(CODE_THRESHOLD ct) {
    id = ct.getId();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    code = ct.getCode();
    sdate = ct.getStartDate();
    edate = ct.getEndDate();
    ip = false;
    op = false;

    if ((ct.getIpTimesStatus() != null && ct.getIpTimesStatus().intValue() == 1)
        || (ct.getOpTimesStatus() != null && ct.getOpTimesStatus().intValue() == 1)) {
      singleAmount = true;
    } else {
      singleAmount = false;
    }
    
    if ((ct.getIpTimesDStatus() != null && ct.getIpTimesDStatus().intValue() == 1) 
        || (ct.getOpTimesDStatus() != null && ct.getOpTimesDStatus().intValue() == 1)) {
      period = true;
    } else {
      period = false;
    }
    
    if ((ct.getIpTimesM() != null && ct.getIpTimesMStatus().intValue() == 1)
        || ct.getOpTimesM() != null && ct.getOpTimesMStatus().intValue() == 1) {
      amount = true;
    } else {
      amount = false;
    }
    if ((ct.getIpTimes6m() != null && ct.getIpTimes6mStatus().intValue() == 1)
        || (ct.getOpTimes6m() != null && ct.getOpTimes6mStatus().intValue() == 1)) {
      average = true;
    } else {
      average = false;
    }
    if ("00".equals(ct.getDataFormat()) || "11".equals(ct.getDataFormat())) {
      ip = true;
      op = true;
    } else if ("10".equals(ct.getDataFormat())) {
      op = true;
    }  else if ("20".equals(ct.getDataFormat())) {
      ip = true;
    }
    inhCode = ct.getInhCode();
    status = ct.getStatus() != null && ct.getStatus().intValue() == 1;
  }

  public String getInhCode() {
    return inhCode;
  }

  public void setInhCode(String inhCode) {
    this.inhCode = inhCode;
  }

  public Boolean getSingleAmount() {
    return singleAmount;
  }

  public void setSingleAmount(Boolean singleAmount) {
    this.singleAmount = singleAmount;
  }

  public Boolean getPeriod() {
    return period;
  }

  public void setPeriod(Boolean period) {
    this.period = period;
  }
  
}
