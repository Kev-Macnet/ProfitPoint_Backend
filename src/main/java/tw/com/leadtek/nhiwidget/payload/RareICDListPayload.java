/**
 * Created on 2021/9/29.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_THRESHOLD;

@ApiModel("罕見ICD列表資料")
public class RareICDListPayload extends StartEndPayload implements Serializable {

  private static final long serialVersionUID = 747203358608875831L;

  @ApiModelProperty(value = "診斷/處置代碼/支付標準代碼", example = "J10.01", required = true)
  protected String code;

  @ApiModelProperty(value = "平均/單月平均用量提示", example = "true", required = false)
  protected Boolean average;

  @ApiModelProperty(value = "定量/每月總量提示", example = "true", required = false)
  protected Boolean amount;

  @ApiModelProperty(value = "門急診", example = "true", required = false)
  protected Boolean op;

  @ApiModelProperty(value = "住院", example = "true", required = false)
  protected Boolean ip;

  @ApiModelProperty(value = "啟用狀態", example = "true", required = false)
  protected Boolean status;
  
  public RareICDListPayload() {
    
  }

  public RareICDListPayload(CODE_THRESHOLD ct) {
    id = ct.getId();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    code = ct.getCode();
    sdate = ct.getStartDate();
    edate = ct.getEndDate();
    ip = false;
    op = false;
 
    if ("00".equals(ct.getDataFormat())) {
      ip = true;
      op = true;
    } else if ("10".equals(ct.getDataFormat())) {
      op = true;
    }  else if ("20".equals(ct.getDataFormat())) {
      ip = true;
    }
    
    if ((ct.getIpTimesM() != null && ct.getIpTimesMStatus().intValue() == 1 && ip)
        || ct.getOpTimesM() != null && ct.getOpTimesMStatus().intValue() == 1 && op) {
      amount = true;
    } else {
      amount = false;
    }
    if ((ct.getIpTimes6m() != null && ct.getIpTimes6mStatus().intValue() == 1  && ip)
        || (ct.getOpTimes6m() != null && ct.getOpTimes6mStatus().intValue() == 1)  && op) {
      average = true;
    } else {
      average = false;
    }
    status = ct.getStatus() != null && ct.getStatus().intValue() == 1;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Boolean getAverage() {
    return average;
  }

  public void setAverage(Boolean average) {
    this.average = average;
  }

  public Boolean getAmount() {
    return amount;
  }

  public void setAmount(Boolean amount) {
    this.amount = amount;
  }

  public Boolean getIp() {
    return ip;
  }

  public void setIp(Boolean ip) {
    this.ip = ip;
  }

  public Boolean getOp() {
    return op;
  }

  public void setOp(Boolean op) {
    this.op = op;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

}
