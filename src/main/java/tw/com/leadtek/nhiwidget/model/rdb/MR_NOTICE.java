/**
 * Created on 2021/11/16 by GenerateSqlByClass().
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Table(name = "MR_NOTICE")
@Entity
public class MR_NOTICE {

  /**
   * 序號
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @JsonIgnore
  private Long id;

  /**
   * MR TABLE ID
   */
  @Column(name = "MR_ID")
  @JsonIgnore
  private Long mrId;

  /**
   * 該筆病歷通知序號
   */
  @Column(name = "SEQ")
  @JsonIgnore
  private Integer seq;

  /**
   * 發送人員 ID(USER.ID)
   */
  @Column(name = "SEND_USER_ID")
  @JsonIgnore
  private Long sendUserId;

  /**
   * 接收人員ID(USER.ID)，以,區隔
   */
  @Column(name = "RECEIVE_USER_ID", length = 100)
  @JsonIgnore
  private String receiveUserId;

  /**
   * 就醫日期-起
   */
  @Column(name = "START_DATE")
  @JsonIgnore
  private Date startDate;

  /**
   * 就醫日期-訖
   */
  @Column(name = "END_DATE")
  @JsonIgnore
  private Date endDate;

  /**
   * 醫院病歷編號(病患ID)
   */
  @Column(name = "INH_MR_ID", length = 16)
  @JsonIgnore
  private String inhMrId;

  /**
   * 就醫記錄編號
   */
  @Column(name = "INH_CLINIC_ID", length = 16)
  @JsonIgnore
  private String inhClinicId;

  /**
   * 患者姓名
   */
  @Column(name = "NAME", length = 30)
  @JsonIgnore
  private String name;

  /**
   * 就醫科別代碼
   */
  @Column(name = "FUNC_TYPE", length = 2)
  @JsonIgnore
  private String funcType;

  /**
   * 就醫科別名稱
   */
  @Column(name = "FUNC_TYPEC", length = 20)
  @JsonIgnore
  private String funcTypec;

  /**
   * 診治醫事人員代號
   */
  @Column(name = "PRSN_ID", length = 12)
  @JsonIgnore
  private String prsnId;

  /**
   * 診治醫事人員ID(USER.ID)
   */
  @Column(name = "PRSN_USER_ID")
  @JsonIgnore
  private Long prsnUserId;

  /**
   * 診治醫事人員姓名
   */
  @Column(name = "PRSN_NAME", length = 30)
  @JsonIgnore
  private String prsnName;

  /**
   * 申報人員代碼
   */
  @Column(name = "APPL_ID", length = 16)
  @JsonIgnore
  private String applId;

  /**
   * 申報人員ID(USER.ID)
   */
  @Column(name = "APPL_USER_ID")
  @JsonIgnore
  private Long applUserId;

  /**
   * 申報人員姓名
   */
  @Column(name = "APPL_NAME", length = 30)
  @JsonIgnore
  private String applName;

  /**
   * 通知日期
   */
  @Column(name = "NOTICE_DATE")
  @JsonIgnore
  private Date noticeDate;

  /**
   * 最新通知人數
   */
  @Column(name = "NOTICE_PPL")
  @JsonIgnore
  private Integer noticePpl;

  /**
   * 已通知人員姓名，以,區隔
   */
  @Column(name = "NOTICE_NAME", length = 100)
  @JsonIgnore
  private String noticeName;

  /**
   * 最新讀取狀態人數
   */
  @Column(name = "READED_PPL")
  @JsonIgnore
  private Integer readedPpl;

  /**
   * 已讀取人員姓名，以,區隔
   */
  @Column(name = "READED_NAME", length = 100)
  @JsonIgnore
  private String readedName;

  /**
   * 0:已全部通知，1:已全部讀取
   */
  @Column(name = "STATUS")
  @JsonIgnore
  private Integer status;
  
  /**
   * 申報年月(西元年)，如 202111
   */
  @Column(name = "YM")
  private Integer ym;
  
  /**
   * 資料格式，與IP_T, OP_T的DATA_FORMAT值一樣。10:門診，20:住院
   */
  @Column(name = "DATA_FORMAT")
  private String dataFormat;

  /**
   * 更新時間
   */
  @Column(name = "UPDATE_AT", nullable = false)
  @JsonIgnore
  private Date updateAt;

  public MR_NOTICE() {

  }

  public MR_NOTICE(MY_MR myMr) {
    mrId = myMr.getMrId();
    seq = myMr.getNoticeSeq();
    sendUserId = myMr.getApplUserId();
    startDate = myMr.getStartDate();
    endDate = myMr.getEndDate();
    inhMrId = myMr.getInhMrId();
    inhClinicId = myMr.getInhClinicId();
    name = myMr.getName();
    funcType = myMr.getFuncType();
    funcTypec = myMr.getFuncTypec();
    prsnId = myMr.getPrsnId();
    prsnUserId = myMr.getPrsnUserId();
    prsnName = myMr.getPrsnName();
    applId = myMr.getApplId();
    applUserId = myMr.getApplUserId();
    applName = myMr.getApplName();
    noticeDate = new Date();
    noticePpl = myMr.getNoticePpl();
    noticeName = myMr.getNoticeName();
    status = 0;
    readedPpl = 0;
    ym = myMr.getYm();
    dataFormat = myMr.getDataFormat();
    updateAt = new Date();
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

  /**
   * MR TABLE ID
   */
  public Long getMrId() {
    return mrId;
  }

  /**
   * MR TABLE ID
   */
  public void setMrId(Long MR_ID) {
    mrId = MR_ID;
  }

  /**
   * 該筆病歷通知序號
   */
  public Integer getSeq() {
    return seq;
  }

  /**
   * 該筆病歷通知序號
   */
  public void setSeq(Integer SEQ) {
    seq = SEQ;
  }

  /**
   * 發送人員 ID(USER.ID)
   */
  public Long getSendUserId() {
    return sendUserId;
  }

  /**
   * 發送人員 ID(USER.ID)
   */
  public void setSendUserId(Long SEND_USER_ID) {
    sendUserId = SEND_USER_ID;
  }

  /**
   * 接收人員ID(USER.ID)，以,區隔
   */
  public String getReceiveUserId() {
    return receiveUserId;
  }

  /**
   * 接收人員ID(USER.ID)，以,區隔
   */
  public void setReceiveUserId(String RECEIVE_USER_ID) {
    receiveUserId = RECEIVE_USER_ID;
  }

  /**
   * 就醫日期-起
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * 就醫日期-起
   */
  public void setStartDate(Date START_DATE) {
    startDate = START_DATE;
  }

  /**
   * 就醫日期-訖
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * 就醫日期-訖
   */
  public void setEndDate(Date END_DATE) {
    endDate = END_DATE;
  }

  /**
   * 醫院病歷編號(病患ID)
   */
  public String getInhMrId() {
    return inhMrId;
  }

  /**
   * 醫院病歷編號(病患ID)
   */
  public void setInhMrId(String INH_MR_ID) {
    inhMrId = INH_MR_ID;
  }

  /**
   * 就醫記錄編號
   */
  public String getInhClinicId() {
    return inhClinicId;
  }

  /**
   * 就醫記錄編號
   */
  public void setInhClinicId(String INH_CLINIC_ID) {
    inhClinicId = INH_CLINIC_ID;
  }

  /**
   * 患者姓名
   */
  public String getName() {
    return name;
  }

  /**
   * 患者姓名
   */
  public void setName(String NAME) {
    name = NAME;
  }

  /**
   * 就醫科別代碼
   */
  public String getFuncType() {
    return funcType;
  }

  /**
   * 就醫科別代碼
   */
  public void setFuncType(String FUNC_TYPE) {
    funcType = FUNC_TYPE;
  }

  /**
   * 就醫科別名稱
   */
  public String getFuncTypec() {
    return funcTypec;
  }

  /**
   * 就醫科別名稱
   */
  public void setFuncTypec(String FUNC_TYPEC) {
    funcTypec = FUNC_TYPEC;
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
   * 診治醫事人員ID(USER.ID)
   */
  public Long getPrsnUserId() {
    return prsnUserId;
  }

  /**
   * 診治醫事人員ID(USER.ID)
   */
  public void setPrsnUserId(Long PRSN_USER_ID) {
    prsnUserId = PRSN_USER_ID;
  }

  /**
   * 診治醫事人員姓名
   */
  public String getPrsnName() {
    return prsnName;
  }

  /**
   * 診治醫事人員姓名
   */
  public void setPrsnName(String PRSN_NAME) {
    prsnName = PRSN_NAME;
  }

  /**
   * 申報人員代碼
   */
  public String getApplId() {
    return applId;
  }

  /**
   * 申報人員代碼
   */
  public void setApplId(String APPL_ID) {
    applId = APPL_ID;
  }

  /**
   * 申報人員ID(USER.ID)
   */
  public Long getApplUserId() {
    return applUserId;
  }

  /**
   * 申報人員ID(USER.ID)
   */
  public void setApplUserId(Long APPL_USER_ID) {
    applUserId = APPL_USER_ID;
  }

  /**
   * 申報人員姓名
   */
  public String getApplName() {
    return applName;
  }

  /**
   * 申報人員姓名
   */
  public void setApplName(String APPL_NAME) {
    applName = APPL_NAME;
  }

  /**
   * 通知日期
   */
  public Date getNoticeDate() {
    return noticeDate;
  }

  /**
   * 通知日期
   */
  public void setNoticeDate(Date NOTICE_DATE) {
    noticeDate = NOTICE_DATE;
  }

  /**
   * 最新通知人數
   */
  public Integer getNoticePpl() {
    return noticePpl;
  }

  /**
   * 最新通知人數
   */
  public void setNoticePpl(Integer NOTICE_PPL) {
    noticePpl = NOTICE_PPL;
  }

  /**
   * 已通知人員姓名，以,區隔
   */
  public String getNoticeName() {
    return noticeName;
  }

  /**
   * 已通知人員姓名，以,區隔
   */
  public void setNoticeName(String NOTICE_NAME) {
    noticeName = NOTICE_NAME;
  }

  /**
   * 最新讀取狀態人數
   */
  public Integer getReadedPpl() {
    return readedPpl;
  }

  /**
   * 最新讀取狀態人數
   */
  public void setReadedPpl(Integer READED_PPL) {
    readedPpl = READED_PPL;
  }

  /**
   * 已讀取人員姓名，以,區隔
   */
  public String getReadedName() {
    return readedName;
  }

  /**
   * 已讀取人員姓名，以,區隔
   */
  public void setReadedName(String READED_NAME) {
    readedName = READED_NAME;
  }

  /**
   * 0:已全部通知，1:已全部讀取
   */
  public Integer getStatus() {
    return status;
  }

  /**
   * 0:已全部通知，1:已全部讀取
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

  public Integer getYm() {
    return ym;
  }

  public void setYm(Integer ym) {
    this.ym = ym;
  }

  public String getDataFormat() {
    return dataFormat;
  }

  public void setDataFormat(String dataFormat) {
    this.dataFormat = dataFormat;
  }

}
