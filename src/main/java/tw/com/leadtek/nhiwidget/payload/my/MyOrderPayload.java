/**
 * Created on 2021/11/16.
 */
package tw.com.leadtek.nhiwidget.payload.my;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.model.rdb.MY_MR;

@ApiModel("我的清單-待辦事項")
public class MyOrderPayload implements Serializable {

  private static final long serialVersionUID = 2869299391406291981L;

  @ApiModelProperty(value = "資料狀態", example = "待處理", required = true)
  protected String status;
  
  @ApiModelProperty(value = "就醫日期-起", example = "2021/03/16", dataType = "String", required = true)
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  protected Date sdate;
  
  @ApiModelProperty(value = "就醫日期-訖", example = "2021/03/16", dataType = "String", required = true)
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  protected Date edate;
  
  @ApiModelProperty(value = "病歷號碼", example = "142060", required = false)
  protected String inhMrId;
  
  @ApiModelProperty(value = "患者姓名", example = "王小明", required = true)
  protected String name;
  
  @ApiModelProperty(value = "就醫記錄編號", example = "11003170001", required = false)
  protected String inhClinicId;
  
  @ApiModelProperty(value = "科別代碼", example = "02", required = false)
  protected String funcType;
  
  @ApiModelProperty(value = "科別", example = "內科", required = false)
  protected String funcTypeC;

  @ApiModelProperty(value = "醫護代碼", example = "A123456789", required = false)
  protected String prsnId;

  @ApiModelProperty(value = "醫護姓名", example = "王大明", required = false)
  protected String prsnName;

  @ApiModelProperty(value = "病歷點數", example = "400", required = false)
  protected Integer totalDot;
  
  @ApiModelProperty(value = "負責人員代碼", example = "A123456789", required = false)
  protected String applId;

  @ApiModelProperty(value = "負責人員姓名", example = "陳小春", required = false)
  protected String applName;
  
  @ApiModelProperty(value = "通知次數", example = "3", required = false)
  private Integer noticeTimes;
  
  @ApiModelProperty(value = "讀取狀態", example = "未讀取", required = false)
  private String readedStatus;
  
  public MyOrderPayload() {
    
  }
  
  public MyOrderPayload(MY_MR mr) {
    status = MR_STATUS.toStatusString(mr.getStatus());
    sdate = mr.getStartDate();
    edate = mr.getEndDate();
    inhMrId = mr.getInhMrId();
    name = mr.getName();
    inhClinicId = mr.getInhClinicId();
    funcType = mr.getFuncType();
    funcTypeC = mr.getFuncTypec();
    prsnId = mr.getPrsnId();
    prsnName = mr.getPrsnName();
    totalDot = mr.getTDot();
    applId = mr.getApplId();
    applName = mr.getApplName();
    noticeTimes = mr.getNoticeTimes();
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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

  public String getInhMrId() {
    return inhMrId;
  }

  public void setInhMrId(String inhMrId) {
    this.inhMrId = inhMrId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public String getPrsnId() {
    return prsnId;
  }

  public void setPrsnId(String prsnId) {
    this.prsnId = prsnId;
  }

  public String getPrsnName() {
    return prsnName;
  }

  public void setPrsnName(String prsnName) {
    this.prsnName = prsnName;
  }

  public Integer getTotalDot() {
    return totalDot;
  }

  public void setTotalDot(Integer totalDot) {
    this.totalDot = totalDot;
  }

  public String getApplId() {
    return applId;
  }

  public void setApplId(String applId) {
    this.applId = applId;
  }

  public String getApplName() {
    return applName;
  }

  public void setApplName(String applName) {
    this.applName = applName;
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
