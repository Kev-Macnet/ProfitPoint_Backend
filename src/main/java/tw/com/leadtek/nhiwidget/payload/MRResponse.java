/**
 * Created on 2021/5/14.
 */
package tw.com.leadtek.nhiwidget.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.CodeTableService;

@ApiModel("病歷")
public class MRResponse extends MR {

  @ApiModelProperty(value = "科別名稱", example = "骨科", required = false)
  protected String funcTypeC;
  
  @ApiModelProperty(value = "病歷檢查狀態", example = "無需變更", required = false)
  protected String sStatus;
  
  @ApiModelProperty(value = "通知是否已讀取", example = "已讀", required = false)
  protected String sReaded;
  
  @ApiModelProperty(value = "通知狀態。-1:標示為有疑問，但尚未通知，0:不需通知，1:已通知", example = "已通知", required = false)
  protected String sNotify;
  
  @ApiModelProperty(value = "診斷碼是否異動。1:有異動，0:無異動。", example = "無異動", required = false)
  protected String sChangeICD;
  
  @ApiModelProperty(value = "其他資訊是否異動。1:有異動，0:無異動。", example = "無異動", required = false)
  protected String sChangeOther;
  
  @ApiModelProperty(value = "醫令是否異動。1:有異動，0:無異動。", example = "無異動", required = false)
  protected String sChangeOrder;
  
  @ApiModelProperty(value = "是否為法定傳染病。1:是，0:否。", example = "否", required = false)
  protected String sInfectious;
  
  public MRResponse() {
    
  }
  
  public MRResponse(MR mr, CodeTableService cts) {
    applId = mr.getApplId();
    applName = mr.getApplName();
    changeICD = mr.getChangeICD();
    changeOrder = mr.getChangeOrder();
    changeOther = mr.getChangeOther();
    dataFormat = cts.getDesc("DATA_FORMAT", mr.getDataFormat()) ;
    deductedDot = mr.getDeductedDot();
    dId = mr.getdId();
    funcType = mr.getFuncType();
    funcTypeC = cts.getDesc("FUNC_TYPE", mr.getFuncType()) ;
    id = mr.getId();
    infectious = mr.getInfectious();
    inhClinicId = mr.getInhClinicId();
    inhMrId = mr.getInhMrId();
    mrDate = mr.getMrDate();
    mrEndDate = mr.getMrEndDate();
    name = mr.getName();
    notify = mr.getNotify();
    objective = mr.getObjective();
    ownExpense = mr.getOwnExpense();
    prsnId = mr.getPrsnId();
    prsnName = mr.getPrsnName();
    readed = mr.getReaded();
    remark = mr.getRemark();
    rocId = mr.getRocId();
    status = mr.getStatus();
    subjective = mr.getSubjective();
    totalDot = mr.getTotalDot();
    if (mr.getApplDot() == null) {
      mr.setApplDot(0);
    }
    if (mr.getReportDot() == null) {
      mr.setReportDot(0L);
    }
    applDot = (mr.getReportDot() == null) ?  mr.getApplDot().intValue() : mr.getReportDot().intValue();
    updateAt = mr.getUpdateAt();
    drgFixed = mr.getDrgFixed();
    drgCode = mr.getDrgCode();
    drgSection = mr.getDrgSection();
    
    sReaded = getReadedString(mr.getReaded());
    sNotify = getNotifyString(mr.getNotify());
    sChangeICD = getChangeICDString(mr.getChangeICD());
    sChangeOther = getChangeICDString(mr.getChangeOther());
    sChangeOrder = getChangeICDString(mr.getChangeOrder());
    sInfectious = getYNString(mr.getInfectious());
    sStatus = (mr.getStatus() == null) ? "無需變更" : MR_STATUS.toStatusString(mr.getStatus().intValue());
  }
  
  public String getsStatus() {
    return sStatus;
  }

  public void setsStatus(String sStatus) {
    this.sStatus = sStatus;
  }
  
  public String getsReaded() {
    return sReaded;
  }

  public void setsReaded(String sReaded) {
    this.sReaded = sReaded;
  }

  public String getsNotify() {
    return sNotify;
  }

  public void setsNotify(String sNotify) {
    this.sNotify = sNotify;
  }

  public String getsChangeICD() {
    return sChangeICD;
  }

  public void setsChangeICD(String sChangeICD) {
    this.sChangeICD = sChangeICD;
  }

  public String getsChangeOther() {
    return sChangeOther;
  }

  public void setsChangeOther(String sChangeOther) {
    this.sChangeOther = sChangeOther;
  }

  public String getsChangeOrder() {
    return sChangeOrder;
  }

  public void setsChangeOrder(String sChangeOrder) {
    this.sChangeOrder = sChangeOrder;
  }

  public String getsInfectious() {
    return sInfectious;
  }

  public void setsInfectious(String sInfectious) {
    this.sInfectious = sInfectious;
  }

  public static String getReadedString(Integer readed) {
    if (readed == null) {
      return "N/A";
    }
    switch(readed.intValue()) {
      case -1 : return "未讀";
      case 0: return "不需讀取";
      case 1: return "已讀";
    }
    return "N/A";
  }
  
  public static String getNotifyString(Integer notify) {
    if (notify == null) {
      return "N/A";
    }
    switch(notify.intValue()) {
      case -1 : return "標示為有疑問，但尚未通知";
      case 0: return "不需通知";
      case 1: return "已通知";
    }
    return "N/A";
  }
  
  public static String getChangeICDString(Integer changeICD) {
    if (changeICD == null) {
      return "無異動";
    }
    switch(changeICD.intValue()) {
      case 0: return "無異動";
      case 1: return "有異動";
    }
    return "無異動";
  }
  
  public static String getYNString(Integer infectious) {
    if (infectious == null) {
      return "否";
    }
    return infectious.intValue() == 1 ? "是" : "否";
  }

  public String getFuncTypeC() {
    return funcTypeC;
  }

  public void setFuncTypeC(String funcTypeC) {
    this.funcTypeC = funcTypeC;
  }
  
}
