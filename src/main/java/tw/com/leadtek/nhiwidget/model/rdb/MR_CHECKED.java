/**
 * Created on 2021/4/29.
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

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @JsonIgnore
  private Long id;

  /**
   * MR table ID
   */
  @Column(name = "MR_ID")
  private Long mrId;
  
  /**
   * ORDER_CHECK table ID
   */
  @Column(name = "OC_ID")
  private Long ocId;
  
  /**
   * 偵測有衝突時間
   */
  @Column(name = "START_AT")
  private Date startAt;
  
  /**
   * 調整時間
   */
  @Column(name = "FIX_AT")
  private Date fixAt;
  
  /**
   * 評估不調整時間
   */
  @Column(name = "DONT_CHANGE__AT")
  private Date dontChangeAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getMrId() {
    return mrId;
  }

  public void setMrId(Long mrId) {
    this.mrId = mrId;
  }

  public Long getOcId() {
    return ocId;
  }

  public void setOcId(Long ocId) {
    this.ocId = ocId;
  }

  public Date getStartAt() {
    return startAt;
  }

  public void setStartAt(Date startAt) {
    this.startAt = startAt;
  }

  public Date getFixAt() {
    return fixAt;
  }

  public void setFixAt(Date fixAt) {
    this.fixAt = fixAt;
  }

  public Date getDontChangeAt() {
    return dontChangeAt;
  }

  public void setDontChangeAt(Date dontChangeAt) {
    this.dontChangeAt = dontChangeAt;
  }
  
}
