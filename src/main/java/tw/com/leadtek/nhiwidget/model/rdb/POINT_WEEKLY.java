/**
 * Created on 2021/11/05 by GenerateSqlByClass().
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

@Table(name = "POINT_WEEKLY")
@Entity
public class POINT_WEEKLY {

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
   * 西元年
   */
  @Column(name = "P_YEAR")
  @JsonIgnore
  private Integer pyear;

  /**
   * 週數
   */
  @Column(name = "P_WEEK")
  @JsonIgnore
  private Integer pweek;

  /**
   * 啟始日
   */
  @Column(name = "START_DATE")
  @JsonIgnore
  private Date startDate;

  /**
   * 結束日
   */
  @Column(name = "END_DATE")
  @JsonIgnore
  private Date endDate;
  
  /**
   * 科別代碼00為不分科(全部)
   */
  @Column(name = "FUNC_TYPE")
  @JsonIgnore
  private String funcType;

  /**
   * 門急診點數
   */
  @Column(name = "OP")
  @JsonIgnore
  private Long op;
  
  /**
   * 門急診點數
   */
  @Column(name = "EM")
  @JsonIgnore
  private Long em;

  /**
   * 住院點數
   */
  @Column(name = "IP")
  @JsonIgnore
  private Long ip;

  /**
   * 自費門急診點數
   */
  @Column(name = "OWN_EXP_OP")
  @JsonIgnore
  private Long ownExpOp;

  /**
   * 自費住院點數
   */
  @Column(name = "OWN_EXP_IP")
  @JsonIgnore
  private Long ownExpIp;
  
  /**
   * 門急診人次
   */
  @Column(name = "VISITS_OP")
  @JsonIgnore
  private Long visitsOp;
  
  /**
   * 住院人次
   */
  @Column(name = "VISITS_IP")
  @JsonIgnore
  private Long visitsIp;
  
  /**
   * 出院人次
   */
  @Column(name = "VISITS_LEAVE")
  @JsonIgnore
  private Long visitsLeave;

  /**
   * 更新時間
   */
  @Column(name = "UPDATE_AT")
  @JsonIgnore
  private Date updateAt;

  public POINT_WEEKLY() {
    op = 0L;
    ip = 0L;
    em = 0L;
    ownExpOp = 0L;
    ownExpIp = 0L;
    visitsOp = 0L;
    visitsIp = 0L;
    visitsLeave = 0L;
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
   * 西元年
   */
  public Integer getPyear() {
    return pyear;
  }

  /**
   * 西元年
   */
  public void setPyear(Integer P_YEAR) {
    pyear = P_YEAR;
  }

  /**
   * 週數
   */
  public Integer getPweek() {
    return pweek;
  }

  /**
   * 週數
   */
  public void setPweek(Integer P_WEEK) {
    pweek = P_WEEK;
  }

  /**
   * 啟始日
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * 啟始日
   */
  public void setStartDate(Date START_DATE) {
    startDate = START_DATE;
  }

  /**
   * 結束日
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * 結束日
   */
  public void setEndDate(Date END_DATE) {
    endDate = END_DATE;
  }

  /**
   * 門急診點數
   */
  public Long getOp() {
    return op;
  }

  /**
   * 門急診點數
   */
  public void setOp(Long OP) {
    op = OP;
  }

  /**
   * 住院點數
   */
  public Long getIp() {
    return ip;
  }

  /**
   * 住院點數
   */
  public void setIp(Long IP) {
    ip = IP;
  }

  /**
   * 自費門急診點數
   */
  public Long getOwnExpOp() {
    return ownExpOp;
  }

  /**
   * 自費門急診點數
   */
  public void setOwnExpOp(Long OWN_EXP_OP) {
    ownExpOp = OWN_EXP_OP;
  }

  /**
   * 自費住院點數
   */
  public Long getOwnExpIp() {
    return ownExpIp;
  }

  /**
   * 自費住院點數
   */
  public void setOwnExpIp(Long OWN_EXP_IP) {
    ownExpIp = OWN_EXP_IP;
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

  public Long getEm() {
    return em;
  }

  public void setEm(Long em) {
    this.em = em;
  }

  public String getFuncType() {
    return funcType;
  }

  public void setFuncType(String funcType) {
    this.funcType = funcType;
  }

  public Long getVisitsOp() {
    return visitsOp;
  }

  public void setVisitsOp(Long visitsOp) {
    this.visitsOp = visitsOp;
  }

  public Long getVisitsIp() {
    return visitsIp;
  }

  public void setVisitsIp(Long visitsIp) {
    this.visitsIp = visitsIp;
  }

  public Long getVisitsLeave() {
    return visitsLeave;
  }

  public void setVisitsLeave(Long visitsLeave) {
    this.visitsLeave = visitsLeave;
  }

}