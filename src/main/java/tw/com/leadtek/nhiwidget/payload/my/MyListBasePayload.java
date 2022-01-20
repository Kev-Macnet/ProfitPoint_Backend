/**
 * Created on 2021/11/18.
 */
package tw.com.leadtek.nhiwidget.payload.my;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.MY_MR;

@ApiModel("我的清單")
public class MyListBasePayload implements Serializable {

  private static final long serialVersionUID = 3247347831325206303L;

  @ApiModelProperty(value = "病歷id", example = "991903", required = true)
  protected Long mrId;
   
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

  @ApiModelProperty(value = "負責人員代碼", example = "A123456789", required = false)
  protected String applId;

  @ApiModelProperty(value = "負責人員姓名", example = "陳小春", required = false)
  protected String applName;
  
  @ApiModelProperty(value = "門急診或住院，10:門急診，20:住院", example = "10", required = false)
  protected String dataFormat;
  
  public MyListBasePayload() {
    
  }

  public MyListBasePayload(MY_MR mr) {
    mrId = mr.getMrId();
    sdate = mr.getStartDate();
    edate = mr.getEndDate();
    inhMrId = mr.getInhMrId();
    name = mr.getName();
    inhClinicId = mr.getInhClinicId();
    funcType = mr.getFuncType();
    funcTypeC = mr.getFuncTypec();
    prsnId = mr.getPrsnId();
    prsnName = mr.getPrsnName();
    applId = mr.getApplId();
    applName = mr.getApplName();
    dataFormat = mr.getDataFormat();
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
  
  public String getDataFormat() {
    return dataFormat;
  }

  public void setDataFormat(String dataFormat) {
    this.dataFormat = dataFormat;
  }

  public static String removeDot(String s) {
    if (s == null) {
      return null;
    }
    StringBuffer sb = new StringBuffer(s);
    if (sb.charAt(0) == ',') {
      sb.deleteCharAt(0);
    }
    if (sb.charAt(sb.length() - 1) == ',') {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }
  
}
