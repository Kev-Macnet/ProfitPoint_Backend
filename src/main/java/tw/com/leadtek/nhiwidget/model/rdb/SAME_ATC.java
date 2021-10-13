/**
 * Created on 2021/10/12 by GenerateSqlByClass().
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

@Table(name = "SAME_ATC")
@Entity
public class SAME_ATC {

  /**
   * 序號
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  private Long id;

  /**
   * ATC分類
   */
  @Column(name = "ATC", length = 12)
  private String atc;

  /**
   * 院內碼
   */
  @Column(name = "INH_CODE", length = 16)
  private String inhCode;

  /**
   * 支付標準代碼
   */
  @Column(name = "CODE", length = 16)
  private String code;

  /**
   * 藥品名稱
   */
  @Column(name = "NAME", length = 180)
  private String name;

  /**
   * 啟用狀態，1:已啟用，0:未啟用
   */
  @Column(name = "STATUS")
  private Integer status;

  /**
   * 更新時間
   */
  @Column(name = "UPDATE_AT", nullable = false)
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
   * ATC分類
   */
  public String getAtc() {
    return atc;
  }

  /**
   * ATC分類
   */
  public void setAtc(String ATC) {
    atc = ATC;
  }

  /**
   * 院內碼
   */
  public String getInhCode() {
    return inhCode;
  }

  /**
   * 院內碼
   */
  public void setInhCode(String INH_CODE) {
    inhCode = INH_CODE;
  }

  /**
   * 支付標準代碼
   */
  public String getCode() {
    return code;
  }

  /**
   * 支付標準代碼
   */
  public void setCode(String CODE) {
    code = CODE;
  }

  /**
   * 藥品名稱
   */
  public String getName() {
    return name;
  }

  /**
   * 藥品名稱
   */
  public void setName(String NAME) {
    name = NAME;
  }

  /**
   * 啟用狀態，1:已啟用，0:未啟用
   */
  public Integer getStatus() {
    return status;
  }

  /**
   * 啟用狀態，1:已啟用，0:未啟用
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

}