/**
 * Created on 2021/3/30.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.util.ArrayList;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.CodeBase;
import tw.com.leadtek.nhiwidget.model.rdb.DEDUCTED_NOTE;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.IP_P;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.nhiwidget.model.rdb.OP_P;
import tw.com.leadtek.nhiwidget.service.CodeTableService;
import tw.com.leadtek.tools.DateTool;

@ApiModel("病歷詳細資料")
public class MRDetail extends MR {

  @ApiModelProperty(value = "案件分類", example = "01-西醫一般", required = true)
  protected String caseType;
  
  @ApiModelProperty(value = "流水編號", example = "1234", required = false)
  protected String seqNo;
  
  @ApiModelProperty(value = "出生日期", example = "1977/03/16", dataType = "String", required = false)
  protected String birthday;
  
  @ApiModelProperty(value = "給付類別，非代碼", example = "普通疾病", dataType = "String", required = false)
  protected String payType;
  
  @ApiModelProperty(value = "特定治療項目", required = false)
  protected List<CodeBase> cureItems;
  
  @ApiModelProperty(value = "診斷代碼及說明", required = false)
  protected List<CodeBase> icdCM;
  
  @ApiModelProperty(value = "手術(處置)代碼及說明", required = false)
  protected List<CodeBase> icdOP;
  
  @ApiModelProperty(value = "補報原因註記，1: 補報整筆案件，2: 補報部分醫令或醫令差額", required = false)
  protected String applCauseMark;
  
  @ApiModelProperty(value = "整合式照護計畫註記，1:全民健康保險急性後期整合照護計畫-腦中風，2: 創傷性神經損傷..", example = "全民健康保險急性後期整合照護計畫-腦中風", required = false)
  protected String careMark;
  
  @ApiModelProperty(value = "轉診、處方調劑或特定檢查資源共享案件註記: 保險對象本次就醫係由他院轉診而來、慢性病連續處方調劑..", example = "慢性病連續處方調劑", required = false)
  protected String shareMark;
  
  @ApiModelProperty(value = "病患是否轉出，Y:是，N:否", example = "N", required = false)
  protected String patTranOut;
  
  @ApiModelProperty(value = "藥師代號", required = false)
  protected String pharID;
  
  @ApiModelProperty(value = "給藥天數", required = false)
  protected Integer drugDay;
  
  @ApiModelProperty(value = "處方調劑方式，自行調劑、交付調劑", required = false)
  protected String medType;
  
  @ApiModelProperty(value = "用藥明細點數小計", required = false)
  protected Integer drugDot;
  
  @ApiModelProperty(value = "診察費項目代號", required = false)
  protected String treatCode;
  
  @ApiModelProperty(value = "藥事服務費項目代號", required = false)
  protected String dsvcNo;
  
  @ApiModelProperty(value = "診療明細點數小計", required = false)
  protected Integer treatDot;
  
  @ApiModelProperty(value = "診察費點數", required = false)
  protected Integer diagDot;
  
  @ApiModelProperty(value = "藥事服務費點數", required = false)
  protected Integer dsvcDot;
  
  @ApiModelProperty(value = "特殊材料明細點數小計", required = false)
  protected Integer metrDot;
  
  @ApiModelProperty(value = "合計點數", required = false)
  protected Integer tDot;
  
  @ApiModelProperty(value = "部分負擔代號", required = false)
  protected String partNo;
  
  @ApiModelProperty(value = "部分負擔點數", required = false)
  protected Integer partDot;
  
  @ApiModelProperty(value = "申請點數", required = false)
  protected Integer tApplDot;
  
  @ApiModelProperty(value = "論病例計酬代碼", required = false)
  protected String casePayCode;
  
  @ApiModelProperty(value = "行政協助項目部分負擔點數", required = false)
  protected Integer assistPartDot;
  
  @ApiModelProperty(value = "慢性病連續處方箋有效期間總處方日份", required = false)
  protected Integer chrDays;
  
  @ApiModelProperty(value = "依附就醫新生兒出生日期", required = false)
  protected String nbBirthday;
  
  @ApiModelProperty(value = "山地離島地區醫療服務計畫代碼", required = false)
  protected String outSvcPlanCode;
  
  @ApiModelProperty(value = "矯正機關代號", required = false)
  protected String agencyId;
  
  @ApiModelProperty(value = "特定地區醫療服務", required = false)
  protected String speAreaSvc;
  
  @ApiModelProperty(value = "依附就醫新生兒胞胎註記", required = false)
  protected String childMark;
  
  @ApiModelProperty(value = "支援區域", required = false)
  protected String supportArea;
  
  @ApiModelProperty(value = "實際提供醫療服務之醫事服務機構代號", required = false)
  protected String hospId;
  
  @ApiModelProperty(value = "轉入服務機構代號", required = false)
  protected String tranInHospId;
  
  @ApiModelProperty(value = "轉往之醫事服務機構代號", required = false)
  protected String tranOutHospId;
  
  @ApiModelProperty(value = "原處方就醫序號", required = false)
  protected String oriCardSeqNo;
  
  @ApiModelProperty(value = "待確認原因", required = false)
  protected String mrCheckReason;
  
  @ApiModelProperty(value = "入院年月日", required = false)
  protected String inDate;
  
  @ApiModelProperty(value = "出院年月日", required = false)
  protected String outDate;
  
  @ApiModelProperty(value = "申報起始時間", required = false)
  protected String applSDate;
  
  @ApiModelProperty(value = "申報結束時間", required = false)
  protected String applEDate;
  
  @ApiModelProperty(value = "急性病床天數", required = false)
  protected Integer eBedDay;
  
  @ApiModelProperty(value = "慢性病床天數", required = false)
  protected Integer sBedDay;
  
  @ApiModelProperty(value = "病患來源", required = false)
  protected String patientSource;
  
  @ApiModelProperty(value = "就醫序號", required = false)
  protected String cardSeqNo;
  
  @ApiModelProperty(value = "TW-DRG碼", required = false)
  protected String twDrgCode;
  
  @ApiModelProperty(value = "TW-DRG支付型態", required = false)
  protected String twDrgPayType;
  
  @ApiModelProperty(value = "DRGS碼", required = false)
  protected String caseDrgCode;
  
  @ApiModelProperty(value = "轉歸代碼", required = false)
  protected String tranCode;
  
  @ApiModelProperty(value = "病房費點數", required = false)
  protected Integer roomDot;
  
  @ApiModelProperty(value = "管灌膳食費點數", required = false)
  protected Integer mealDot;
  
  @ApiModelProperty(value = "檢查費點數", required = false)
  protected Integer aminDot;
  
  @ApiModelProperty(value = "放射線診療費點數", required = false)
  protected Integer radoDot;
  
  @ApiModelProperty(value = "治療處置費點數", required = false)
  protected Integer thrpDot;
  
  @ApiModelProperty(value = "手術費點數", required = false)
  protected Integer sgryDot;
  
  @ApiModelProperty(value = "復健治療費點數", required = false)
  protected Integer phscDot;
  
  @ApiModelProperty(value = "血液血漿費點數", required = false)
  protected Integer blodDot;
  
  @ApiModelProperty(value = "血液透析費點數", required = false)
  protected Integer hdDot;
  
  @ApiModelProperty(value = "麻醉費點數", required = false)
  protected Integer aneDot;
  
  @ApiModelProperty(value = "精神科治療費點數", required = false)
  protected Integer nrtpDot;
  
  @ApiModelProperty(value = "注射技術費點數", required = false)
  protected Integer injtDot;
  
  @ApiModelProperty(value = "嬰兒費點數", required = false)
  protected Integer babyDot;
  
  @ApiModelProperty(value = "醫療費用點數合計", required = false)
  protected Integer medDot;
  
  @ApiModelProperty(value = "醫療費用點數(急性病床1-30日)", required = false)
  protected Integer ebAppl30Dot;
  
  @ApiModelProperty(value = "部分負擔點數(急性病床1-30日)", required = false)
  protected Integer ebPart30Dot;
  
  @ApiModelProperty(value = "醫療費用點數(急性病床31-60日)", required = false)
  protected Integer ebAppl60Dot;
  
  @ApiModelProperty(value = "部分負擔點數(急性病床31-60日)", required = false)
  protected Integer ebPart60Dot;
  
  @ApiModelProperty(value = "醫療費用點數(急性病床61日以上)", required = false)
  protected Integer ebAppl61Dot;
  
  @ApiModelProperty(value = "部分負擔點數(急性病床61日以上)", required = false)
  protected Integer ebPart61Dot;
  
  @ApiModelProperty(value = "醫療費用點數(慢性病床1-30日)", required = false)
  protected Integer sbAppl30Dot;
  
  @ApiModelProperty(value = "部分負擔點數(慢性病床1-30日)", required = false)
  protected Integer sbPart30Dot;
  
  @ApiModelProperty(value = "醫療費用點數(慢性病床31-90日)", required = false)
  protected Integer sbAppl90Dot;
  
  @ApiModelProperty(value = "部分負擔點數(慢性病床31-90日)", required = false)
  protected Integer sbPart90Dot;
  
  @ApiModelProperty(value = "醫療費用點數(慢性病床91-180日)", required = false)
  protected Integer sbAppl180Dot;
  
  @ApiModelProperty(value = "部分負擔點數(慢性病床91-180日)", required = false)
  protected Integer sbPart180Dot;
  
  @ApiModelProperty(value = "醫療費用點數(慢性病床181日以上)", required = false)
  protected Integer sbAppl181Dot;
  
  @ApiModelProperty(value = "部分負擔點數(慢性病床181日以上)", required = false)
  protected Integer sbPart181Dot;
  
  @ApiModelProperty(value = "不適用TW-DRGS案件特殊註記", required = false)
  protected String twDrgsSuitMark;
  
  @ApiModelProperty(value = "不計入醫療費用點數合計欄位項目點數", required = false)
  protected Integer nonApplDot;
  
  @ApiModelProperty(value = "醫療服務計畫", required = false)
  protected String svcPlan;
  
  @ApiModelProperty(value = "試辦計畫", required = false)
  protected String pilotProject;
  
  @ApiModelProperty(value = "就醫日期", required = false)
  protected String funcDate;
  
  @ApiModelProperty(value = "治療結束日期", required = false)
  protected String funcEndDate;
  
  @ApiModelProperty(value = "轉診、處方調劑或特定檢查資源共享案件之服務機構代號", required = false)
  protected String shareHospId;
  
  @ApiModelProperty(value = "醫令list", required = false)
  protected List<MO> mos;
  
  @ApiModelProperty(value = "資料備註", required = false)
  protected List<MrNotePayload> notes;
  
  @ApiModelProperty(value = "核刪註記", required = false)
  protected List<DEDUCTED_NOTE> deducted;
  
  @ApiModelProperty(value = "詳情提示，顯示於病歷詳細資料的右上角", required = false)
  protected List<String> hint;
  
  @ApiModelProperty(value = "是否為上一次調整病歷狀態為優化完成的作者，若無人調整則值為true", example = "true", required = false)
  protected Boolean isLastEditor;
  
  @ApiModelProperty(value = "錯誤訊息", required = false)
  protected String error;
  
  public MRDetail() {
    
  }
  
  public MRDetail(MR mr) {
    applId = mr.getApplId();
    applName = mr.getApplName();
    changeICD = mr.getChangeICD();
    changeOrder = mr.getChangeOrder();
    changeOther = mr.getChangeOther();
    dataFormat = mr.getDataFormat();
    deductedDot = mr.getDeductedDot();
    dId = mr.getdId();
    funcType = mr.getFuncType();
    id = mr.getId();
    infectious = mr.getInfectious();
    inhClinicId = mr.getInhClinicId();
    inhMrId = mr.getInhMrId();
    mrDate = mr.getMrDate();
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
    updateAt = mr.getUpdateAt();
  }
  
  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public String getPayType() {
    return payType;
  }

  public void setPayType(String payType) {
    this.payType = payType;
  }

  public List<CodeBase> getCureItems() {
    return cureItems;
  }

  public void setCureItems(List<CodeBase> cureItems) {
    this.cureItems = cureItems;
  }

  public List<CodeBase> getIcdCM() {
    return icdCM;
  }

  public void setIcdCM(List<CodeBase> icdCM) {
    this.icdCM = icdCM;
  }

  public List<CodeBase> getIcdOP() {
    return icdOP;
  }

  public void setIcdOP(List<CodeBase> icdOP) {
    this.icdOP = icdOP;
  }

  public String getApplCauseMark() {
    return applCauseMark;
  }

  public void setApplCauseMark(String applCauseMark) {
    this.applCauseMark = applCauseMark;
  }

  public String getCareMark() {
    return careMark;
  }

  public void setCareMark(String careMark) {
    this.careMark = careMark;
  }

  public String getShareMark() {
    return shareMark;
  }

  public void setShareMark(String shareMark) {
    this.shareMark = shareMark;
  }

  public String getPatTranOut() {
    return patTranOut;
  }

  public void setPatTranOut(String patTranOut) {
    this.patTranOut = patTranOut;
  }

  public String getPharID() {
    return pharID;
  }

  public void setPharID(String pharID) {
    this.pharID = pharID;
  }

  public Integer getDrugDay() {
    return drugDay;
  }

  public void setDrugDay(Integer drugDay) {
    this.drugDay = drugDay;
  }

  public String getMedType() {
    return medType;
  }

  public void setMedType(String medType) {
    this.medType = medType;
  }

  public Integer getDrugDot() {
    return drugDot;
  }

  public void setDrugDot(Integer drugDot) {
    this.drugDot = drugDot;
  }

  public String getTreatCode() {
    return treatCode;
  }

  public void setTreatCode(String treatCode) {
    this.treatCode = treatCode;
  }

  public String getDsvcNo() {
    return dsvcNo;
  }

  public void setDsvcNo(String dsvcNo) {
    this.dsvcNo = dsvcNo;
  }

  public Integer getTreatDot() {
    return treatDot;
  }

  public void setTreatDot(Integer treatDot) {
    this.treatDot = treatDot;
  }

  public Integer getDiagDot() {
    return diagDot;
  }

  public void setDiagDot(Integer diagDot) {
    this.diagDot = diagDot;
  }

  public Integer getDsvcDot() {
    return dsvcDot;
  }

  public void setDsvcDot(Integer dsvcDot) {
    this.dsvcDot = dsvcDot;
  }

  public Integer getMetrDot() {
    return metrDot;
  }

  public void setMetrDot(Integer metrDot) {
    this.metrDot = metrDot;
  }

  public Integer gettDot() {
    return tDot;
  }

  public void settDot(Integer tDot) {
    this.tDot = tDot;
  }
  
  public String getPartNo() {
    return partNo;
  }

  public void setPartNo(String partNo) {
    this.partNo = partNo;
  }

  public Integer getPartDot() {
    return partDot;
  }

  public void setPartDot(Integer partDot) {
    this.partDot = partDot;
  }

  public Integer gettApplDot() {
    return tApplDot;
  }

  public void settApplDot(Integer tApplDot) {
    this.tApplDot = tApplDot;
  }

  public String getCasePayCode() {
    return casePayCode;
  }

  public void setCasePayCode(String casePayCode) {
    this.casePayCode = casePayCode;
  }

  public Integer getAssistPartDot() {
    return assistPartDot;
  }

  public void setAssistPartDot(Integer assistPartDot) {
    this.assistPartDot = assistPartDot;
  }

  public Integer getChrDays() {
    return chrDays;
  }

  public void setChrDays(Integer chrDays) {
    this.chrDays = chrDays;
  }

  public String getNbBirthday() {
    return nbBirthday;
  }

  public void setNbBirthday(String nbBirthday) {
    this.nbBirthday = nbBirthday;
  }

  public String getOutSvcPlanCode() {
    return outSvcPlanCode;
  }

  public void setOutSvcPlanCode(String outSvcPlanCode) {
    this.outSvcPlanCode = outSvcPlanCode;
  }

  public String getAgencyId() {
    return agencyId;
  }

  public void setAgencyId(String agencyId) {
    this.agencyId = agencyId;
  }

  public String getSpeAreaSvc() {
    return speAreaSvc;
  }

  public void setSpeAreaSvc(String speAreaSvc) {
    this.speAreaSvc = speAreaSvc;
  }

  public String getChildMark() {
    return childMark;
  }

  public void setChildMark(String childMark) {
    this.childMark = childMark;
  }

  public String getSupportArea() {
    return supportArea;
  }

  public void setSupportArea(String supportArea) {
    this.supportArea = supportArea;
  }

  public String getHospId() {
    return hospId;
  }

  public void setHospId(String hospId) {
    this.hospId = hospId;
  }

  public String getTranInHospId() {
    return tranInHospId;
  }

  public void setTranInHospId(String tranInHospId) {
    this.tranInHospId = tranInHospId;
  }

  public String getOriCardSeqNo() {
    return oriCardSeqNo;
  }

  public void setOriCardSeqNo(String oriCardSeqNo) {
    this.oriCardSeqNo = oriCardSeqNo;
  }

  public String getMrCheckReason() {
    return mrCheckReason;
  }

  public void setMrCheckReason(String mrCheckReason) {
    this.mrCheckReason = mrCheckReason;
  }
  
  public String getInDate() {
    return inDate;
  }

  public void setInDate(String inDate) {
    this.inDate = inDate;
  }

  public String getOutDate() {
    return outDate;
  }

  public void setOutDate(String outDate) {
    this.outDate = outDate;
  }

  public String getApplSDate() {
    return applSDate;
  }

  public void setApplSDate(String applSDate) {
    this.applSDate = applSDate;
  }

  public String getApplEDate() {
    return applEDate;
  }

  public void setApplEDate(String applEDate) {
    this.applEDate = applEDate;
  }
  
  public Integer geteBedDay() {
    return eBedDay;
  }

  public void seteBedDay(Integer eBedDay) {
    this.eBedDay = eBedDay;
  }

  public Integer getsBedDay() {
    return sBedDay;
  }

  public void setsBedDay(Integer sBedDay) {
    this.sBedDay = sBedDay;
  }
  
  public String getPatientSource() {
    return patientSource;
  }

  public void setPatientSource(String patientSource) {
    this.patientSource = patientSource;
  }
  
  public String getCardSeqNo() {
    return cardSeqNo;
  }

  public void setCardSeqNo(String cardSeqNo) {
    this.cardSeqNo = cardSeqNo;
  }

  public String getTwDrgCode() {
    return twDrgCode;
  }

  public void setTwDrgCode(String twDrgCode) {
    this.twDrgCode = twDrgCode;
  }

  public String getTwDrgPayType() {
    return twDrgPayType;
  }

  public void setTwDrgPayType(String twDrgPayType) {
    this.twDrgPayType = twDrgPayType;
  }

  public String getCaseDrgCode() {
    return caseDrgCode;
  }

  public void setCaseDrgCode(String caseDrgCode) {
    this.caseDrgCode = caseDrgCode;
  }

  public String getTranCode() {
    return tranCode;
  }

  public void setTranCode(String tranCode) {
    this.tranCode = tranCode;
  }
  
  public Integer getRoomDot() {
    return roomDot;
  }

  public void setRoomDot(Integer roomDot) {
    this.roomDot = roomDot;
  }

  public Integer getMealDot() {
    return mealDot;
  }

  public void setMealDot(Integer mealDot) {
    this.mealDot = mealDot;
  }

  public Integer getAminDot() {
    return aminDot;
  }

  public void setAminDot(Integer aminDot) {
    this.aminDot = aminDot;
  }

  public Integer getRadoDot() {
    return radoDot;
  }

  public void setRadoDot(Integer radoDot) {
    this.radoDot = radoDot;
  }

  public Integer getThrpDot() {
    return thrpDot;
  }

  public void setThrpDot(Integer thrpDot) {
    this.thrpDot = thrpDot;
  }

  public Integer getSgryDot() {
    return sgryDot;
  }

  public void setSgryDot(Integer sgryDot) {
    this.sgryDot = sgryDot;
  }

  public Integer getPhscDot() {
    return phscDot;
  }

  public void setPhscDot(Integer phscDot) {
    this.phscDot = phscDot;
  }

  public Integer getBlodDot() {
    return blodDot;
  }

  public void setBlodDot(Integer blodDot) {
    this.blodDot = blodDot;
  }

  public Integer getHdDot() {
    return hdDot;
  }

  public void setHdDot(Integer hdDot) {
    this.hdDot = hdDot;
  }

  public Integer getAneDot() {
    return aneDot;
  }

  public void setAneDot(Integer aneDot) {
    this.aneDot = aneDot;
  }

  public Integer getNrtpDot() {
    return nrtpDot;
  }

  public void setNrtpDot(Integer nrtpDot) {
    this.nrtpDot = nrtpDot;
  }

  public Integer getInjtDot() {
    return injtDot;
  }

  public void setInjtDot(Integer injtDot) {
    this.injtDot = injtDot;
  }

  public Integer getBabyDot() {
    return babyDot;
  }

  public void setBabyDot(Integer babyDot) {
    this.babyDot = babyDot;
  }

  public Integer getMedDot() {
    return medDot;
  }

  public void setMedDot(Integer medDot) {
    this.medDot = medDot;
  }

  public Integer getEbAppl30Dot() {
    return ebAppl30Dot;
  }

  public void setEbAppl30Dot(Integer ebAppl30Dot) {
    this.ebAppl30Dot = ebAppl30Dot;
  }

  public Integer getEbPart30Dot() {
    return ebPart30Dot;
  }

  public void setEbPart30Dot(Integer ebPart30Dot) {
    this.ebPart30Dot = ebPart30Dot;
  }

  public Integer getEbAppl60Dot() {
    return ebAppl60Dot;
  }

  public void setEbAppl60Dot(Integer ebAppl60Dot) {
    this.ebAppl60Dot = ebAppl60Dot;
  }

  public Integer getEbPart60Dot() {
    return ebPart60Dot;
  }

  public void setEbPart60Dot(Integer ebPart60Dot) {
    this.ebPart60Dot = ebPart60Dot;
  }

  public Integer getEbAppl61Dot() {
    return ebAppl61Dot;
  }

  public void setEbAppl61Dot(Integer ebAppl61Dot) {
    this.ebAppl61Dot = ebAppl61Dot;
  }

  public Integer getEbPart61Dot() {
    return ebPart61Dot;
  }

  public void setEbPart61Dot(Integer ebPart61Dot) {
    this.ebPart61Dot = ebPart61Dot;
  }

  public Integer getSbAppl30Dot() {
    return sbAppl30Dot;
  }

  public void setSbAppl30Dot(Integer sbAppl30Dot) {
    this.sbAppl30Dot = sbAppl30Dot;
  }

  public Integer getSbPart30Dot() {
    return sbPart30Dot;
  }

  public void setSbPart30Dot(Integer sbPart30Dot) {
    this.sbPart30Dot = sbPart30Dot;
  }

  public Integer getSbAppl90Dot() {
    return sbAppl90Dot;
  }

  public void setSbAppl90Dot(Integer sbAppl90Dot) {
    this.sbAppl90Dot = sbAppl90Dot;
  }

  public Integer getSbPart90Dot() {
    return sbPart90Dot;
  }

  public void setSbPart90Dot(Integer sbPart90Dot) {
    this.sbPart90Dot = sbPart90Dot;
  }

  public Integer getSbAppl180Dot() {
    return sbAppl180Dot;
  }

  public void setSbAppl180Dot(Integer sbAppl180Dot) {
    this.sbAppl180Dot = sbAppl180Dot;
  }

  public Integer getSbPart180Dot() {
    return sbPart180Dot;
  }

  public void setSbPart180Dot(Integer sbPart180Dot) {
    this.sbPart180Dot = sbPart180Dot;
  }

  public Integer getSbAppl181Dot() {
    return sbAppl181Dot;
  }

  public void setSbAppl181Dot(Integer sbAppl181Dot) {
    this.sbAppl181Dot = sbAppl181Dot;
  }

  public Integer getSbPart181Dot() {
    return sbPart181Dot;
  }

  public void setSbPart181Dot(Integer sbPart181Dot) {
    this.sbPart181Dot = sbPart181Dot;
  }
  
  public String getTwDrgsSuitMark() {
    return twDrgsSuitMark;
  }

  public void setTwDrgsSuitMark(String twDrgsSuitMark) {
    this.twDrgsSuitMark = twDrgsSuitMark;
  }
  
  public String getTranOutHospId() {
    return tranOutHospId;
  }

  public void setTranOutHospId(String tranOutHospId) {
    this.tranOutHospId = tranOutHospId;
  }
  
  public Integer getNonApplDot() {
    return nonApplDot;
  }

  public void setNonApplDot(Integer nonApplDot) {
    this.nonApplDot = nonApplDot;
  }

  public String getSvcPlan() {
    return svcPlan;
  }

  public void setSvcPlan(String svcPlan) {
    this.svcPlan = svcPlan;
  }

  public String getPilotProject() {
    return pilotProject;
  }

  public void setPilotProject(String pilotProject) {
    this.pilotProject = pilotProject;
  }

  public List<MO> getMos() {
    return mos;
  }

  public void setMos(List<MO> mos) {
    this.mos = mos;
  }
  
  public String getCaseType() {
    return caseType;
  }

  public void setCaseType(String caseType) {
    this.caseType = caseType;
  }

  public String getSeqNo() {
    return seqNo;
  }

  public void setSeqNo(String seqNo) {
    this.seqNo = seqNo;
  }
  
  public String getFuncDate() {
    return funcDate;
  }

  public void setFuncDate(String funcDate) {
    this.funcDate = funcDate;
  }

  public String getFuncEndDate() {
    return funcEndDate;
  }

  public void setFuncEndDate(String funcEndDate) {
    this.funcEndDate = funcEndDate;
  }
  
  public String getShareHospId() {
    return shareHospId;
  }

  public void setShareHospId(String shareHospId) {
    this.shareHospId = shareHospId;
  }
  
  public List<MrNotePayload> getNotes() {
    return notes;
  }

  public void setNotes(List<MrNotePayload> notes) {
    this.notes = notes;
  }

  public List<DEDUCTED_NOTE> getDeducted() {
    return deducted;
  }

  public void setDeducted(List<DEDUCTED_NOTE> deducted) {
    this.deducted = deducted;
  }
  
  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }
  
  public Boolean getIsLastEditor() {
    return isLastEditor;
  }

  public void setIsLastEditor(Boolean isLastEditor) {
    this.isLastEditor = isLastEditor;
  }
  
  public List<String> getHint() {
    return hint;
  }

  public void setHint(List<String> hint) {
    this.hint = hint;
  }

  /**
   * 將table OP_D 的資料寫到MRDetail object
   * @param opd
   * @param cts
   */
  public void setOPDData(OP_D opd, CodeTableService cts) {
    this.name = opd.getName();
    this.birthday = opd.getIdBirthYmd();
    this.payType = CodeTableService.getDesc(cts, "PAY_TYPE", opd.getPayType());
    this.partNo = CodeTableService.getDesc(cts, "PART_NO", opd.getPartNo());
    this.funcType = CodeTableService.getDesc(cts, "FUNC_TYPE", opd.getFuncType());
    this.caseType = CodeTableService.getDesc(cts, "OP_CASE_TYPE", opd.getCaseType());
    if (opd.getSeqNo() != null) {
    this.seqNo = opd.getSeqNo().toString();
    } else {
      this.seqNo = "1";
    }
    cardSeqNo = opd.getCardSeqNo();
    List<CodeBase> listCureItem = new ArrayList<CodeBase>();
    CodeTableService.addToList(cts, listCureItem, "OP_CURE_ITEM", opd.getCureItemNo1());
    CodeTableService.addToList(cts, listCureItem, "OP_CURE_ITEM", opd.getCureItemNo2());
    CodeTableService.addToList(cts, listCureItem, "OP_CURE_ITEM", opd.getCureItemNo3());
    CodeTableService.addToList(cts, listCureItem, "OP_CURE_ITEM", opd.getCureItemNo4());
    if (listCureItem.size() > 0) {
      this.cureItems = listCureItem;
    }
    
    List<CodeBase> listCM = new ArrayList<CodeBase>();
    CodeTableService.addToList(cts, listCM, "ICD10-CM", opd.getIcdCm1());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", opd.getIcdCm2());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", opd.getIcdCm3());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", opd.getIcdCm4());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", opd.getIcdCm5());
    if (listCM.size() > 0) {
      this.icdCM = listCM;
    }
    
    List<CodeBase> listOP = new ArrayList<CodeBase>();
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", opd.getIcdOpCode1());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", opd.getIcdOpCode2());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", opd.getIcdOpCode3());
    if (listOP.size() > 0) {
      this.icdOP = listOP;
    }
    this.shareHospId = opd.getShareHospId();
    this.applCauseMark = CodeTableService.getDesc(cts, "APPL_CAUSE_MARK", opd.getApplCauseMark());
    this.careMark = CodeTableService.getDesc(cts, "CARE_MARK", opd.getCareMark());
    this.shareMark = CodeTableService.getDesc(cts, "SHARE_MARK", opd.getShareMark());
    this.patTranOut = opd.getPatTranOut();
    this.pharID = opd.getPharId();
    this.drugDay = opd.getDrugDay();
    this.medType = CodeTableService.getDesc(cts, "OP_MED_TYPE", opd.getMedType());
    this.drugDot = opd.getDrugDot();
    this.treatCode = CodeTableService.getDesc(cts, "ORDER", opd.getTreatCode());
    this.dsvcNo = CodeTableService.getDesc(cts, "ORDER", opd.getDsvcNo());
    this.dsvcDot = opd.getDsvcDot();
    this.treatDot = opd.getTreatDot();
    this.diagDot = opd.getDiagDot();
    this.metrDot = opd.getMetrDot();
    this.tDot = opd.getTotalDot();
    this.partDot = opd.getPartDot();
    this.tApplDot = opd.getTotalApplDot();
    this.casePayCode = opd.getCasePayCode();
    this.assistPartDot = opd.getAssistPartDot();
    this.chrDays = opd.getChrDays();
    this.nbBirthday = opd.getNbBirthday();
    this.outSvcPlanCode = opd.getOutSvcPlanCode();
    this.agencyId = opd.getAgencyId();
    this.speAreaSvc = CodeTableService.getDesc(cts, "SPE_AREA_SVC", opd.getSpeAreaSvc());
    this.childMark = opd.getChildMark();
    this.supportArea = opd.getSupportArea();
    this.hospId = opd.getHospId();
    this.tranInHospId = opd.getTranInHospId();
    this.oriCardSeqNo = opd.getOriCardSeqNo();
    this.funcDate = opd.getFuncDate();
    this.funcEndDate = opd.getFuncEndDate();
  }
  
  /**
   * 將table IP_D 的資料寫到MRDetail object
   * @param ipd
   * @param cts
   */
  public void setIPDData(IP_D ipd, CodeTableService cts) {
    this.name = ipd.getName();
    this.rocId = ipd.getRocId();
    if (ipd.getSeqNo() != null) {
    this.seqNo = ipd.getSeqNo().toString();
      } else {
        this.seqNo = "1";
      }
    this.caseType = CodeTableService.getDesc(cts, "IP_CASE_TYPE", ipd.getCaseType());
    this.partNo = CodeTableService.getDesc(cts, "PART_NO", ipd.getPartNo());
    this.birthday = ipd.getIdBirthYmd();
    this.payType = CodeTableService.getDesc(cts, "PAY_TYPE", ipd.getPayType());
    this.funcType = CodeTableService.getDesc(cts, "FUNC_TYPE", ipd.getFuncType());
    this.inDate = ipd.getInDate();
    this.outDate = ipd.getOutDate();
    this.applSDate = ipd.getApplStartDate();
    this.applEDate = ipd.getApplEndDate();
    this.sBedDay = ipd.getSBedDay();
    this.eBedDay = ipd.getEBedDay();
    this.patientSource = CodeTableService.getDesc(cts, "IP_PATIENT_SOURCE", ipd.getPatientSource());
    this.cardSeqNo = ipd.getCardSeqNo();
    this.twDrgCode = CodeTableService.getDesc(cts, "DRG",ipd.getTwDrgCode());
    this.twDrgPayType  = CodeTableService.getDesc(cts, "TW_DRG_PAY_TYPE", ipd.getTwDrgPayType());
    this.caseDrgCode = ipd.getCaseDrgCode();
    this.tranCode = CodeTableService.getDesc(cts, "TRAN_CODE", ipd.getTranCode());
    
    List<CodeBase> listCM = new ArrayList<CodeBase>();
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm1());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm2());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm3());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm4());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm5());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm6());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm7());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm8());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm9());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm10());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm11());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm12());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm13());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm14());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm15());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm16());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm17());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm18());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm19());
    CodeTableService.addToList(cts, listCM, "ICD10-CM", ipd.getIcdCm20());
    if (listCM.size() > 0) {
      this.icdCM = listCM;
    }
    
    List<CodeBase> listOP = new ArrayList<CodeBase>();
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode1());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode2());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode3());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode4());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode5());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode6());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode7());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode8());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode9());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode10());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode11());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode12());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode13());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode14());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode15());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode16());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode17());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode18());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode19());
    CodeTableService.addToList(cts, listOP, "ICD10-PCS", ipd.getIcdOpCode20());
    if (listOP.size() > 0) {
      this.icdOP = listOP;
    }
    this.applCauseMark = CodeTableService.getDesc(cts, "APPL_CAUSE_MARK", ipd.getApplCauseMark());
    this.drugDot = ipd.getDrugDot();
    this.dsvcDot = ipd.getDsvcDot();
    this.diagDot = ipd.getDiagDot();
    this.roomDot = ipd.getRoomDot();
    this.mealDot = ipd.getMealDot();
    this.aminDot = ipd.getAminDot();
    this.radoDot = ipd.getRadoDot();
    this.thrpDot = ipd.getThrpDot();
    this.sgryDot = ipd.getSgryDot();
    this.phscDot = ipd.getPhscDot();
    this.blodDot = ipd.getBlodDot();
    this.hdDot = ipd.getHdDot();
    this.aneDot = ipd.getAneDot();
    this.metrDot = ipd.getMetrDot();
    this.drugDot = ipd.getDrugDot();
    this.nrtpDot = ipd.getNrtpDot();
    this.injtDot = ipd.getInjtDot();
    this.babyDot = ipd.getBabyDot();
    this.medDot = ipd.getMedDot();
    this.partDot = ipd.getPartDot();
    this.tApplDot = ipd.getApplDot();
    this.ebAppl30Dot = ipd.getEbAppl30Dot();
    this.ebPart30Dot = ipd.getEbPart30Dot();
    this.ebAppl60Dot = ipd.getEbAppl60Dot();
    this.ebPart60Dot = ipd.getEbPart60Dot();
    this.ebAppl61Dot = ipd.getEbAppl61Dot();
    this.ebPart61Dot = ipd.getEbPart61Dot();
    this.sbAppl30Dot = ipd.getSbAppl30Dot();
    this.sbPart30Dot = ipd.getSbPart30Dot();
    this.sbAppl90Dot = ipd.getSbAppl90Dot();
    this.sbPart90Dot = ipd.getSbPart90Dot();
    this.sbAppl180Dot = ipd.getSbAppl180Dot();
    this.sbPart180Dot = ipd.getSbPart180Dot();
    this.sbAppl181Dot = ipd.getSbPart181Dot();
    this.sbPart181Dot = ipd.getSbPart181Dot();
    this.nbBirthday = ipd.getNbBirthday();
    this.agencyId = ipd.getAgencyId();
    this.childMark = ipd.getChildMark();
    this.twDrgsSuitMark = ipd.getTwDrgsSuitMark();
    this.agencyId = ipd.getAgencyId();
    this.hospId = ipd.getHospId();
    this.tranInHospId = ipd.getTranInHospId();
    this.tranOutHospId = ipd.getTranOutHospId();
    this.svcPlan = ipd.getSvcPlan();
    this.pilotProject = ipd.getPilotProject();
    this.nonApplDot = ipd.getNonApplDot();
  }
  
  public void convertToADYear() {
    if (birthday != null && birthday.length() > 0) {
      birthday = DateTool.convertChineseToADWithSlash(birthday);
    }
    if (applYm != null && applYm.length() > 0 ) {
      applYm = DateTool.convertChineseToADWithSlash(applYm);
    }
    if (funcDate != null && funcDate.length() > 0 ) {
      funcDate = DateTool.convertChineseToADWithSlash(funcDate);
    }
    if (funcEndDate != null && funcEndDate.length() > 0 ) {
      funcEndDate = DateTool.convertChineseToADWithSlash(funcEndDate);
    }
    if (applSDate != null && applSDate.length() > 0) {
      applSDate = DateTool.convertChineseToADWithSlash(applSDate);
    }
    if (applEDate != null && applEDate.length() > 0) {
      applEDate = DateTool.convertChineseToADWithSlash(applEDate);
    }
    if (nbBirthday != null && nbBirthday.length() > 0) {
      nbBirthday = DateTool.convertChineseToADWithSlash(nbBirthday);
    }
    if (inDate != null && inDate.length() > 0) {
      inDate = DateTool.convertChineseToADWithSlash(inDate);
    }
    if (outDate != null && outDate.length() > 0) {
      outDate = DateTool.convertChineseToADWithSlash(outDate);
    }
  }
  
  public static void updateIcdAll(MR mr) {
    StringBuffer sb = new StringBuffer(",");
    if (mr.getIcdcm1() != null) {
      sb.append(mr.getIcdcm1());
      sb.append(",");
    }
    if (mr.getIcdcmOthers() != null) {
      if (sb.charAt(sb.length() - 1) == ',') {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append(mr.getIcdcmOthers());
    }
    if (mr.getIcdpcs() != null) {
      if (sb.charAt(sb.length() - 1) == ',') {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append(mr.getIcdpcs());
    }
    if (sb.length() > 1) {
      mr.setIcdAll(sb.toString());
    } else {
      mr.setIcdAll(null);
    }
  }
  
  public static void updateCodeAllIP(MR mr, List<IP_P> ippList) {
    StringBuffer sb = new StringBuffer(",");
    for(IP_P ipp : ippList) {
      if (ipp.getOrderCode() != null) {
        sb.append(ipp.getOrderCode());
        sb.append(",");
      }
    }
    if (sb.length() > 1) {
      mr.setCodeAll(sb.toString());
    }
  }
  
  public static void updateCodeAllOP(MR mr, List<OP_P> oppList ) {
    StringBuffer sb = new StringBuffer(",");
    for(OP_P opp : oppList) {
      if (opp.getDrugNo() != null) {
        sb.append(opp.getDrugNo());
        sb.append(",");
      }
    }
    if (sb.length() > 1) {
      mr.setCodeAll(sb.toString());
    }
  }
  
  public static void updateIcdcmOtherIP(MR mr, IP_D ipd) {
    StringBuffer sb = new StringBuffer(",");
    appendString(sb, ipd.getIcdCm2());
    appendString(sb, ipd.getIcdCm3());
    appendString(sb, ipd.getIcdCm4());
    appendString(sb, ipd.getIcdCm5());
    appendString(sb, ipd.getIcdCm6());
    appendString(sb, ipd.getIcdCm7());
    appendString(sb, ipd.getIcdCm8());
    appendString(sb, ipd.getIcdCm9());
    appendString(sb, ipd.getIcdCm10());
    appendString(sb, ipd.getIcdCm11());
    appendString(sb, ipd.getIcdCm12());
    appendString(sb, ipd.getIcdCm13());
    appendString(sb, ipd.getIcdCm14());
    appendString(sb, ipd.getIcdCm15());
    appendString(sb, ipd.getIcdCm16());
    appendString(sb, ipd.getIcdCm17());
    appendString(sb, ipd.getIcdCm18());
    appendString(sb, ipd.getIcdCm19());
    appendString(sb, ipd.getIcdCm20());
    if (sb.length() > 1) {
      mr.setIcdcmOthers(sb.toString());
    }
  }
  
  public static void appendString(StringBuffer sb, String s) {
    if (s != null) {
      sb.append(s);
      sb.append(",");
    }
  }
    
  public static void updateIcdpcsIP(MR mr, IP_D ipd) {
    StringBuffer sb = new StringBuffer(",");
    appendString(sb, ipd.getIcdOpCode1());
    appendString(sb, ipd.getIcdOpCode2());
    appendString(sb, ipd.getIcdOpCode3());
    appendString(sb, ipd.getIcdOpCode4());
    appendString(sb, ipd.getIcdOpCode5());
    appendString(sb, ipd.getIcdOpCode6());
    appendString(sb, ipd.getIcdOpCode7());
    appendString(sb, ipd.getIcdOpCode8());
    appendString(sb, ipd.getIcdOpCode9());
    appendString(sb, ipd.getIcdOpCode10());
    appendString(sb, ipd.getIcdOpCode11());
    appendString(sb, ipd.getIcdOpCode12());
    appendString(sb, ipd.getIcdOpCode13());
    appendString(sb, ipd.getIcdOpCode14());
    appendString(sb, ipd.getIcdOpCode15());
    appendString(sb, ipd.getIcdOpCode16());
    appendString(sb, ipd.getIcdOpCode17());
    appendString(sb, ipd.getIcdOpCode18());
    appendString(sb, ipd.getIcdOpCode19());
    appendString(sb, ipd.getIcdOpCode20());
    if (sb.length() > 1) {
      mr.setIcdpcs(sb.toString());
    }
  }
  
  public static void updateIcdcmOtherOP(MR mr, OP_D opd) {
    StringBuffer sb = new StringBuffer(",");
    appendString(sb, opd.getIcdCm2());
    appendString(sb, opd.getIcdCm3());
    appendString(sb, opd.getIcdCm4());
    appendString(sb, opd.getIcdCm5());
    if (sb.length() > 1) {
      mr.setIcdcmOthers(sb.toString());
    }
  }
  
  public static void updateIcdpcsOP(MR mr, OP_D opd) {
    StringBuffer sb = new StringBuffer(",");
    appendString(sb, opd.getIcdOpCode1());
    appendString(sb, opd.getIcdOpCode2());
    appendString(sb, opd.getIcdOpCode3());
    if (sb.length() > 1) {
      mr.setIcdpcs(sb.toString());
    }
  }
}
