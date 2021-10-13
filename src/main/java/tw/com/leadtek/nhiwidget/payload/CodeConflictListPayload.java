/**
 * Created on 2021/10/12.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_CONFLICT;

@ApiModel("健保項目對應自費項目並存列表")
public class CodeConflictListPayload extends StartEndPayload implements Serializable {

  private static final long serialVersionUID = -5874402837383364344L;

  @ApiModelProperty(value = "支付標準代碼", example = "J10.01", required = true)
  protected String code;

  @ApiModelProperty(value = "健保品項院內碼", example = "", required = false)
  protected String inhCode;

  @ApiModelProperty(value = "自費品項院內碼", example = "true", required = false)
  protected String ownCode;

  @ApiModelProperty(value = "門急診", example = "true", required = false)
  protected Boolean op;

  @ApiModelProperty(value = "住院", example = "true", required = false)
  protected Boolean ip;

  @ApiModelProperty(value = "啟用狀態", example = "true", required = false)
  protected Boolean status;
  
  public CodeConflictListPayload() {
    
  }
  
  public CodeConflictListPayload(CODE_CONFLICT cc) {
    id = cc.getId();
    sdate = cc.getStartDate();
    edate = cc.getEndDate();
    inhCode = cc.getInhCode();
    code = cc.getCode();
    ownCode = cc.getOwnExpCode();
    status = cc.getStatus().intValue() == 1;
    if ("00".equals(cc.getDataFormat())) {
      op = true;
      ip = true;
    } else if ("10".equals(cc.getDataFormat())) {
      op = true;
      ip = false;
    } else if ("20".equals(cc.getDataFormat())) {
      op = false;
      ip = true;
    }  
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getInhCode() {
    return inhCode;
  }

  public void setInhCode(String inhCode) {
    this.inhCode = inhCode;
  }

  public String getOwnCode() {
    return ownCode;
  }

  public void setOwnCode(String ownCode) {
    this.ownCode = ownCode;
  }

  public Boolean getOp() {
    return op;
  }

  public void setOp(Boolean op) {
    this.op = op;
  }

  public Boolean getIp() {
    return ip;
  }

  public void setIp(Boolean ip) {
    this.ip = ip;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }
  
}
