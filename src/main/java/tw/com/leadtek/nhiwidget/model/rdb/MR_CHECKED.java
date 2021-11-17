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

@Table(name = "MR_CHECKED")
@Entity
public class MR_CHECKED {

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
   * 申報人員ID(USER.ID)，非證號
   */
  @Column(name = "APPL_ID")
  @JsonIgnore
  private Long applId;

  /**
   * 醫護人員ID(USER.ID)，非證號
   */
  @Column(name = "PRSN_ID")
  @JsonIgnore
  private Long prsnId;

  /**
   * 病歷狀態
   */
  @Column(name = "STATUS")
  @JsonIgnore
  private Integer status;

  /**
   * 上次通知序號
   */
  @Column(name = "LAST_NOTICE_SEQ")
  @JsonIgnore
  private Integer lastNoticeSeq;

  /**
   * 偵測到有衝突的時間點
   */
  @Column(name = "START_AT")
  @JsonIgnore
  private Date startAt;

  /**
   * 調整時間點
   */
  @Column(name = "FIX_AT")
  @JsonIgnore
  private Date fixAt;

  /**
   * 評估不調整時間點
   */
  @Column(name = "DONT_CHANGE_AT")
  @JsonIgnore
  private Date dontChangeAt;

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
   * 申報人員ID(USER.ID)，非證號
   */
  public Long getApplId() {
    return applId;
  }

  /**
   * 申報人員ID(USER.ID)，非證號
   */
  public void setApplId(Long APPL_ID) {
    applId = APPL_ID;
  }

  /**
   * 醫護人員ID(USER.ID)，非證號
   */
  public Long getPrsnId() {
    return prsnId;
  }

  /**
   * 醫護人員ID(USER.ID)，非證號
   */
  public void setPrsnId(Long PRSN_ID) {
    prsnId = PRSN_ID;
  }

  /**
   * 病歷狀態
   */
  public Integer getStatus() {
    return status;
  }

  /**
   * 病歷狀態
   */
  public void setStatus(Integer STATUS) {
    status = STATUS;
  }

  /**
   * 上次通知序號
   */
  public Integer getLastNoticeSeq() {
    return lastNoticeSeq;
  }

  /**
   * 上次通知序號
   */
  public void setLastNoticeSeq(Integer LAST_NOTICE_SEQ) {
    lastNoticeSeq = LAST_NOTICE_SEQ;
  }

  /**
   * 偵測到有衝突的時間點
   */
  public Date getStartAt() {
    return startAt;
  }

  /**
   * 偵測到有衝突的時間點
   */
  public void setStartAt(Date START_AT) {
    startAt = START_AT;
  }

  /**
   * 調整時間點
   */
  public Date getFixAt() {
    return fixAt;
  }

  /**
   * 調整時間點
   */
  public void setFixAt(Date FIX_AT) {
    fixAt = FIX_AT;
  }

  /**
   * 評估不調整時間點
   */
  public Date getDontChangeAt() {
    return dontChangeAt;
  }

  /**
   * 評估不調整時間點
   */
  public void setDontChangeAt(Date DONT_CHANGE_AT) {
    dontChangeAt = DONT_CHANGE_AT;
  }

}