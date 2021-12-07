/**
 * Created on 2021/11/19.
 */
package tw.com.leadtek.nhiwidget.payload.intelligent;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.model.rdb.INTELLIGENT;

@ApiModel("智能提示助理")
public class IntelligentRecord implements Serializable {

  private static final long serialVersionUID = 3935157516250372246L;

  @ApiModelProperty(value = "病歷id", example = "991903", required = true)
  protected Long mrId;

  @ApiModelProperty(value = "就醫日期-起", example = "2021/03/16", dataType = "String", required = true)
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  protected Date sdate;

  @ApiModelProperty(value = "就醫日期-訖", example = "2021/03/16", dataType = "String", required = true)
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  protected Date edate;

  @ApiModelProperty(value = "就醫記錄編號", example = "11003170001", required = false)
  protected String inhClinicId;

  @ApiModelProperty(value = "科別代碼", example = "02", required = false)
  protected String funcType;

  @ApiModelProperty(value = "科別", example = "內科", required = false)
  protected String funcTypeC;

  @ApiModelProperty(value = "病歷點數", example = "400", required = false)
  protected Integer totalDot;

  @ApiModelProperty(value = "資料狀態", example = "待處理", required = false)
  protected String status;

  @ApiModelProperty(value = "原由", example = "應用比例偏高醫令", required = false)
  protected String reason;

  @ApiModelProperty(value = "詳細資訊", example = "詳細資訊", required = false)
  protected String detail;
  
  @ApiModelProperty(value = "病歷格式，10:門急診，20:住院，30:特約藥局，40:特約物理(職能)治療所..", example = "10", required = false)
  protected String dataFormat;

  public IntelligentRecord() {

  }
  
  public IntelligentRecord(INTELLIGENT in) {
    mrId = in.getMrId();
    dataFormat = in.getDataFormat();
    status = MR_STATUS.toStatusString(in.getStatus());
    sdate = in.getStartDate();
    edate = in.getEndDate();
    inhClinicId = in.getInhClinicId();
    funcType = in.getFuncType();
    funcTypeC = in.getFuncTypec();
    totalDot = in.getApplDot();
    detail = in.getReason();
    reason = INTELLIGENT_REASON.toReasonString(in.getConditionCode());
  }

  public Long getMrId() {
    return mrId;
  }

  public void setMrId(Long mrId) {
    this.mrId = mrId;
  }

  public Date getSdate() {
    return sdate;
  }

  public void setSdate(Date sdate) {
    this.sdate = sdate;
  }

  public Date getEdate() {
    return edate;
  }

  public void setEdate(Date edate) {
    this.edate = edate;
  }

  public String getInhClinicId() {
    return inhClinicId;
  }

  public void setInhClinicId(String inhClinicId) {
    this.inhClinicId = inhClinicId;
  }

  public String getFuncType() {
    return funcType;
  }

  public void setFuncType(String funcType) {
    this.funcType = funcType;
  }

  public String getFuncTypeC() {
    return funcTypeC;
  }

  public void setFuncTypeC(String funcTypeC) {
    this.funcTypeC = funcTypeC;
  }

  public Integer getTotalDot() {
    return totalDot;
  }

  public void setTotalDot(Integer totalDot) {
    this.totalDot = totalDot;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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

  public String getDataFormat() {
    return dataFormat;
  }

  public void setDataFormat(String dataFormat) {
    this.dataFormat = dataFormat;
  }
  
}
