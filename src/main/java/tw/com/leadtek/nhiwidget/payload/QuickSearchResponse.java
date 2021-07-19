/**
 * Created on 2021/4/27.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.model.rdb.MR;

@ApiModel("快速搜尋response")
public class QuickSearchResponse extends BaseResponse implements Serializable {

  private static final long serialVersionUID = 5828960232810556392L;

  @ApiModelProperty(value = "病歷id", example = "1234", required = true)
  protected Long id;
  
  @ApiModelProperty(value = "資料狀態", example = "待確認", required = true)
  protected String status;
  
  @ApiModelProperty(value = "就醫記錄編號", example = "1234", required = false)
  protected String inhClinicId;
  
  @ApiModelProperty(value = "病歷日期", example = "2021/03/16", dataType = "String", required = true)
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")  
  protected Date mrDate;
  
  @ApiModelProperty(value = "就醫科別代碼", example = "02", required = false)
  protected String funcType;
  
  @ApiModelProperty(value = "科別名稱", example = "內科", required = false)
  protected String funcTypeS;
  
  @ApiModelProperty(value = "點數", example = "1234", required = false)
  protected Integer totalDot;
  
  @ApiModelProperty(value = "原由", example = "診斷碼與醫令異常組合", required = false)
  protected String reason;
  
  @ApiModelProperty(value = "詳細資訊", example = "申報17007B時，不得同時申報17003B或17004C", required = false)
  protected String detail;
  
  public QuickSearchResponse() {
    
  }
  
  public QuickSearchResponse(MR mr, String funcTypeS) {
    this.id = mr.getId();
    this.funcType = mr.getFuncType();
    this.funcTypeS = funcTypeS;
    this.mrDate = mr.getMrDate();
    this.totalDot = mr.getTotalDot();
    this.status = MR_STATUS.toStatusString(mr.getStatus());
    this.inhClinicId = mr.getInhClinicId();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Date getMrDate() {
    return mrDate;
  }

  public void setMrDate(Date mrDate) {
    this.mrDate = mrDate;
  }

  public String getFuncType() {
    return funcType;
  }

  public void setFuncType(String funcType) {
    this.funcType = funcType;
  }

  public String getFuncTypeS() {
    return funcTypeS;
  }

  public void setFuncTypeS(String funcTypeS) {
    this.funcTypeS = funcTypeS;
  }

  public Integer getTotalDot() {
    return totalDot;
  }

  public void setTotalDot(Integer totalDot) {
    this.totalDot = totalDot;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public String getInhClinicId() {
    return inhClinicId;
  }

  public void setInhClinicId(String inhClinicId) {
    this.inhClinicId = inhClinicId;
  }
  
}
