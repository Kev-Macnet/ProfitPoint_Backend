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
   * 門急診點數
   */
  @Column(name = "OP")
  @JsonIgnore
  private Long op;

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
   * 更新時間
   */
  @Column(name = "UPDATE_AT")
  @JsonIgnore
  private Date updateAt;

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

}