/**
 * Created on 2021/03/12 by GenerateSqlByClass().
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.constant.XMLConstant;
import tw.com.leadtek.tools.DateTool;

@ApiModel("病歷")
@Table(name = "MR")
@Entity
public class MR {

  /**
   * 序號
   */
  @ApiModelProperty(value = "病歷id", example = "1234", required = true)
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenMR")
  @SequenceGenerator(name = "seqGenMR", sequenceName = "SEQ_MR", allocationSize = 1000)
  // @SequenceGenerator(name = "seqGenMR", allocationSize = 100)
  @Basic(optional = false)
  @Column(name = "ID")
  protected Long id;

  /**
   * 醫院病歷編號
   */
  @ApiModelProperty(value = "醫院病歷編號，每一位病患的院內編號", example = "11003160001", required = false)
  @Column(name = "INH_MR_ID", length = 16)
  protected String inhMrId;

  /**
   * 就醫科別
   */
  @ApiModelProperty(value = "就醫科別", example = "02", required = false)
  @Column(name = "FUNC_TYPE", length = 2)
  protected String funcType;

  @ApiModelProperty(value = "患者身分證字號", example = "A123456789", required = false)
  @Column(name = "ROC_ID", length = 12)
  protected String rocId;

  @ApiModelProperty(value = "患者姓名", example = "王小明", required = false)
  @Column(name = "NAME", length = 30)
  protected String name;

  @ApiModelProperty(value = "診治醫事人員代號", example = "A123456789", required = false)
  @Column(name = "PRSN_ID", length = 12)
  protected String prsnId;

  @ApiModelProperty(value = "診治醫事人員姓名", example = "王大明", required = false)
  @Column(name = "PRSN_NAME", length = 30)
  protected String prsnName;

  @ApiModelProperty(value = "申報/標記人員代碼", example = "A123456789", required = false)
  @Column(name = "APPL_ID", length = 12)
  protected String applId;

  @ApiModelProperty(value = "申報處理人員姓名", example = "陳小春", required = false)
  @Column(name = "APPL_NAME", length = 30)
  protected String applName;

  @ApiModelProperty(value = "申報民國年月", example = "11008", required = false)
  @Column(name = "APPL_YM", length = 5)
  protected String applYm;

  /**
   * 資料格式，與IP_T, OP_T的DATA_FORMAT值一樣。10:門診，20:住院，30:特約藥局，40:特約物理(職能)治療所...
   */
  @ApiModelProperty(value = "資料格式，與IP_T, OP_T的DATA_FORMAT值一樣。10:門診，20:住院，30:特約藥局，40:特約物理(職能)治療所...",
      example = "10", required = false)
  @Column(name = "DATA_FORMAT", length = 2)
  protected String dataFormat;

  /**
   * 申報點數清單TABLE ID
   */
  @ApiModelProperty(value = "申報點數清單TABLE ID", example = "1234", required = false)
  @Column(name = "D_ID")
  protected Long dId;

  /**
   * 病歷日期
   */
  @ApiModelProperty(value = "病歷日期", example = "2021/03/16", dataType = "String", required = true)
  @Column(name = "MR_DATE")
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  protected Date mrDate;

  /**
   * 病歷日期
   */
  @ApiModelProperty(value = "病歷結束日期", example = "2021/03/26", dataType = "String", required = true)
  @Column(name = "MR_END_DATE")
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  protected Date mrEndDate;

  /**
   * 備註
   */
  @ApiModelProperty(value = "備註", required = true)
  @Column(name = "REMARK", length = 100)
  protected String remark;

  /**
   * 病歷狀態，-2: 待確認，-1: 疑問標示，0: 待處理，1: 無需變更，2: 優化完成，3: 評估不調整
   */
  @ApiModelProperty(value = "病歷狀態，-2: 待確認，-1: 疑問標示，0: 待處理，1: 無需變更，2: 優化完成，3: 評估不調整", example = "1",
      required = true)
  @Column(name = "STATUS")
  protected Integer status;

  /**
   * 讀取狀態。-1: 未讀，0: 不需讀取，1:已讀
   */
  @ApiModelProperty(value = "讀取狀態，-1: 未讀，0: 不需讀取，1:已讀", example = "0", required = false)
  @Column(name = "READED")
  protected Integer readed;

  /**
   * 通知狀態。-1:標示為有疑問，但尚未通知，0:不需通知，1:已通知
   */
  @ApiModelProperty(value = "通知狀態。-1:標示為有疑問，但尚未通知，0:不需通知，1:已通知", example = "0", required = false)
  @Column(name = "NOTIFY")
  protected Integer notify;

  /**
   * 就醫記錄編號
   */
  @ApiModelProperty(value = "就醫記錄編號", example = "11003170001", required = false)
  @Column(name = "INH_CLINIC_ID")
  protected String inhClinicId;

  /**
   * 診斷碼是否異動。1:有異動，0:無異動。
   */
  @ApiModelProperty(value = "診斷碼是否異動。1:有異動，0:無異動。", example = "0", required = false)
  @Column(name = "CHANGE_ICD")
  protected Integer changeICD = 0;

  /**
   * 院內碼(自費醫材)是否異動。1:有異動，0:無異動。
   */
  @ApiModelProperty(value = "院內碼(自費醫材)是否異動。1:有異動，0:無異動。", example = "0", required = false)
  @Column(name = "CHANGE_INH")
  protected Integer changeInh = 0;

  /**
   * 醫囑是否異動。1:有異動，0:無異動。
   */
  @ApiModelProperty(value = "醫囑是否異動。1:有異動，0:無異動。", example = "0", required = false)
  @Column(name = "CHANGE_SO")
  protected Integer changeSo = 0;

  /**
   * 其他資訊是否異動。1:有異動，0:無異動。
   */
  @ApiModelProperty(value = "其他資訊是否異動。1:有異動，0:無異動。", example = "0", required = false)
  @Column(name = "CHANGE_OTHER")
  protected Integer changeOther = 0;

  @ApiModelProperty(value = "醫令是否異動。1:有異動，0:無異動。", example = "0", required = false)
  @Column(name = "CHANGE_ORDER")
  protected Integer changeOrder = 0;

  @ApiModelProperty(value = "是否為法定傳染病。1:是，0:否。", example = "0", required = false)
  @Column(name = "INFECTIOUS")
  protected Integer infectious;

  /**
   * 病歷點數
   */
  @ApiModelProperty(value = "病歷點數", example = "1234", required = false)
  @Column(name = "T_DOT")
  protected Integer totalDot;

  @ApiModelProperty(value = "申請點數", example = "1234", required = false)
  @Column(name = "APPL_DOT")
  protected Integer applDot;

  /**
   * 核刪點數
   */
  @ApiModelProperty(value = "核刪點數", example = "457", required = false)
  @Column(name = "DEDUCTED_DOT")
  protected Integer deductedDot;

  @ApiModelProperty(value = "患者主訴症狀", required = false)
  @Column(name = "SUBJECTIVE")
  protected String subjective;

  @ApiModelProperty(value = "醫療人員的客觀檢查發現", required = false)
  @Column(name = "OBJECTIVE")
  protected String objective;

  @ApiModelProperty(value = "自費金額", required = false)
  @Column(name = "OWN_EXPENSE")
  protected Integer ownExpense;

  @ApiModelProperty(value = "DRG定額", required = false)
  @Column(name = "DRG_FIXED")
  protected Integer drgFixed;

  @ApiModelProperty(value = "DRG代碼", required = false)
  @Column(name = "DRG_CODE")
  protected String drgCode;

  @ApiModelProperty(value = "DRG落點區間", required = false)
  @Column(name = "DRG_SECTION")
  protected String drgSection;

  /**
   * 更新時間
   */
  @ApiModelProperty(value = "更新日期", example = "2021/03/16", dataType = "String", required = true)
  @Column(name = "UPDATE_AT", nullable = false)
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  protected Date updateAt;

  public MR() {

  }

  public MR(IP_D ipd) {
    this.funcType = ipd.getFuncType();
    this.rocId = ipd.getRocId();
    this.name = ipd.getName();
    this.prsnId = ipd.getPrsnId();
    this.dataFormat = XMLConstant.DATA_FORMAT_IP;
    if (ipd.getInDate() != null && ipd.getInDate().length() > 0) {
      this.mrDate = DateTool.convertChineseToYear(ipd.getInDate());
    }
    if (ipd.getOutDate() != null && ipd.getOutDate().length() > 0) {
      this.mrEndDate = DateTool.convertChineseToYear(ipd.getOutDate());
    }
    if (ipd.getApplDot() != null) {
      this.totalDot = ipd.getApplDot();
    }
    if (ipd.getNonApplDot() != null) {
      this.totalDot += ipd.getApplDot();
    }
    this.drgCode = ipd.getTwDrgCode();
    this.applDot = ipd.getApplDot();
    this.ownExpense = ipd.getNonApplDot();
    this.updateAt = new Date();
  }

  public MR(OP_D opd) {
    this.funcType = opd.getFuncType();
    this.rocId = opd.getRocId();
    this.name = opd.getName();
    this.prsnId = opd.getPrsnId();
    this.dataFormat = XMLConstant.DATA_FORMAT_OP;

    if (opd.getFuncDate() != null && opd.getFuncDate().length() > 0) {
      this.mrDate = DateTool.convertChineseToYear(opd.getFuncDate());
    }
    this.totalDot = opd.getTotalDot();
    this.applDot = opd.getTotalApplDot();
    this.ownExpense = totalDot - applDot;
    this.updateAt = new Date();
  }

  /**
   * 序號
   */
  public Long getId() {
    return id;
  }

  /**
   * 序號
   */
  public void setId(Long ID) {
    id = ID;
  }

  public String getRocId() {
    return rocId;
  }

  public void setRocId(String rocId) {
    this.rocId = rocId;
  }

  /**
   * 醫院病歷編號
   */
  public String getInhMrId() {
    return inhMrId;
  }

  /**
   * 醫院病歷編號
   */
  public void setInhMrId(String INH_MR_ID) {
    inhMrId = INH_MR_ID;
  }

  /**
   * 就醫科別
   */
  public String getFuncType() {
    return funcType;
  }

  /**
   * 就醫科別
   */
  public void setFuncType(String FUNC_TYPE) {
    funcType = FUNC_TYPE;
  }

  /**
   * 診治醫事人員代號
   */
  public String getPrsnId() {
    return prsnId;
  }

  /**
   * 診治醫事人員代號
   */
  public void setPrsnId(String PRSN_ID) {
    prsnId = PRSN_ID;
  }

  /**
   * 申報處理人員代碼
   */
  public String getApplId() {
    return applId;
  }

  /**
   * 申報處理人員代碼
   */
  public void setApplId(String APPL_ID) {
    applId = APPL_ID;
  }

  /**
   * 資料格式，與IP_T, OP_T的DATA_FORMAT值一樣。10:門診，20:住院，30:特約藥局，40:特約物理(職能)治療所...
   */
  public String getDataFormat() {
    return dataFormat;
  }

  /**
   * 資料格式，與IP_T, OP_T的DATA_FORMAT值一樣。10:門診，20:住院，30:特約藥局，40:特約物理(職能)治療所...
   */
  public void setDataFormat(String DATA_FORMAT) {
    dataFormat = DATA_FORMAT;
  }

  public Long getdId() {
    return dId;
  }

  public void setdId(Long dId) {
    this.dId = dId;
  }

  /**
   * 病歷日期
   */
  public Date getMrDate() {
    return mrDate;
  }

  /**
   * 病歷日期
   */
  public void setMrDate(Date MR_DATE) {
    mrDate = MR_DATE;
  }

  /**
   * 備註
   */
  public String getRemark() {
    return remark;
  }

  /**
   * 備註
   */
  public void setRemark(String REMARK) {
    remark = REMARK;
  }

  /**
   * 病歷狀態，-2: 待確認，-1: 疑問標示，0: 待處理，1: 無需變更，2: 優化完成，3: 評估不調整
   */
  public Integer getStatus() {
    return status;
  }

  /**
   * 病歷狀態，-2: 待確認，-1: 疑問標示，0: 待處理，1: 無需變更，2: 優化完成，3: 評估不調整
   */
  public void setStatus(Integer STATUS) {
    status = STATUS;
  }

  /**
   * 更新時間
   */
  public Date getUpdateAt() {
    return updateAt;
  }

  /**
   * 更新時間
   */
  public void setUpdateAt(Date UPDATE_AT) {
    updateAt = UPDATE_AT;
  }

  public Integer getReaded() {
    return readed;
  }

  public void setReaded(Integer readed) {
    this.readed = readed;
  }

  public Integer getNotify() {
    return notify;
  }

  public void setNotify(Integer notify) {
    this.notify = notify;
  }

  /**
   * 就醫記錄編號
   */
  public String getInhClinicId() {
    return inhClinicId;
  }

  public void setInhClinicId(String inhClinicId) {
    this.inhClinicId = inhClinicId;
  }

  public Integer getChangeICD() {
    return changeICD;
  }

  public void setChangeICD(Integer changeICD) {
    this.changeICD = changeICD;
  }

  public Integer getChangeOther() {
    return changeOther;
  }

  public void setChangeOther(Integer changeOther) {
    this.changeOther = changeOther;
  }

  public Integer getChangeOrder() {
    return changeOrder;
  }

  public void setChangeOrder(Integer changeOrder) {
    this.changeOrder = changeOrder;
  }

  public Integer getTotalDot() {
    return totalDot;
  }

  public void setTotalDot(Integer totalDot) {
    this.totalDot = totalDot;
  }

  public Integer getDeductedDot() {
    return deductedDot;
  }

  public void setDeductedDot(Integer deductedDot) {
    this.deductedDot = deductedDot;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPrsnName() {
    return prsnName;
  }

  public void setPrsnName(String prsnName) {
    this.prsnName = prsnName;
  }

  public String getApplName() {
    return applName;
  }

  public void setApplName(String applName) {
    this.applName = applName;
  }

  public Integer getInfectious() {
    return infectious;
  }

  public void setInfectious(Integer infectious) {
    this.infectious = infectious;
  }

  public String getSubjective() {
    return subjective;
  }

  public void setSubjective(String subjective) {
    this.subjective = subjective;
  }

  public String getObjective() {
    return objective;
  }

  public void setObjective(String objective) {
    this.objective = objective;
  }

  public Integer getOwnExpense() {
    return ownExpense;
  }

  public void setOwnExpense(Integer ownExpense) {
    this.ownExpense = ownExpense;
  }

  public String getDrgCode() {
    return drgCode;
  }

  public void setDrgCode(String drgCode) {
    this.drgCode = drgCode;
  }

  public String getDrgSection() {
    return drgSection;
  }

  public void setDrgSection(String drgSection) {
    this.drgSection = drgSection;
  }

  public Integer getApplDot() {
    return applDot;
  }

  public void setApplDot(Integer applDot) {
    this.applDot = applDot;
  }

  public Integer getDrgFixed() {
    return drgFixed;
  }

  public void setDrgFixed(Integer drgFixed) {
    this.drgFixed = drgFixed;
  }

  public void updateFuncType(String ft) {
    if (ft != null && ft.indexOf('-') == 2) {
      this.funcType = ft.substring(0, 2);
    } else {
      this.funcType = ft;
    }
  }

  public String getApplYm() {
    return applYm;
  }

  public void setApplYm(String applYm) {
    this.applYm = applYm;
  }

  public Date getMrEndDate() {
    return mrEndDate;
  }

  public void setMrEndDate(Date mrEndDate) {
    this.mrEndDate = mrEndDate;
  }

  public Integer getChangeInh() {
    return changeInh;
  }

  public void setChangeInh(Integer changeInh) {
    this.changeInh = changeInh;
  }

  public Integer getChangeSo() {
    return changeSo;
  }

  public void setChangeSo(Integer changeSo) {
    this.changeSo = changeSo;
  }

  public void updateMR(MR mr) {
    this.inhMrId = mr.getInhMrId();
    updateFuncType(mr.getFuncType());
    this.rocId = mr.getRocId();
    this.name = mr.getName();
    this.prsnId = mr.getPrsnId();
    this.prsnName = mr.getPrsnName();
    this.applId = mr.getApplId();
    this.applName = mr.getApplName();
    this.dataFormat = mr.getDataFormat();
    this.mrDate = mr.getMrDate();
    this.mrEndDate = mr.getMrEndDate();
    this.remark = mr.getRemark();
    this.status = mr.getStatus();
    this.readed = mr.getReaded();
    this.notify = mr.getNotify();
    this.inhClinicId = mr.getInhClinicId();
    this.changeICD = mr.getChangeICD();
    this.changeOther = mr.getChangeOther();
    this.changeOrder = mr.getChangeOrder();
    this.changeInh = mr.getChangeInh();
    this.changeSo = mr.getChangeSo();
    this.infectious = mr.getInfectious();
    this.totalDot = mr.getTotalDot();
    this.applDot = mr.getApplDot();
    this.deductedDot = mr.getDeductedDot();
    this.subjective = mr.getSubjective();
    this.objective = mr.getObjective();
    this.ownExpense = mr.getOwnExpense();
    this.drgFixed = mr.getDrgFixed();
    this.drgCode = mr.getDrgCode();
    this.drgSection = mr.getDrgSection();
    this.applYm = mr.getApplYm();
  }
}
