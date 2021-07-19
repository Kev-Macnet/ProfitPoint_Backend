/**
 * Created on 2021/02/20 by GenerateSqlByClass().
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

@Table(name = "XML_TRANS")
@Entity
public class XML_TRANS {

  /**
   * 序號
   */
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @JsonIgnore
  @Id
  private Long id;

  /**
   * 異動的表格名稱
   */
  @Column(name = "TABLE_NAME", length = 20)
  @JsonIgnore
  private String tableName;

  /**
   * 表格 ID
   */
  @Column(name = "TABLE_ID")
  @JsonIgnore
  private Long tableId;

  /**
   * 表格欄位名稱
   */
  @Column(name = "FIELD", length = 12)
  @JsonIgnore
  private String field;

  /**
   * 異動前
   */
  @Column(name = "TRAN_BEFORE", length = 250)
  @JsonIgnore
  private String tranBefore;

  /**
   * 異動後
   */
  @Column(name = "TRAN_AFTER", length = 250)
  @JsonIgnore
  private String tranAfter;

  /**
   * ADD, UPDATE, DELETE
   */
  @Column(name = "TRAN_ACTION", length = 12)
  @JsonIgnore
  private String tranAction;

  /**
   * 備註
   */
  @Column(name = "REMARK", length = 100)
  @JsonIgnore
  private String remark;

  /**
   * 更新時間
   */
  @Column(name = "UPDATE_AT", nullable = false)
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
   * 異動的表格名稱
   */
  public String getTableName() {
    return tableName;
  }

  /**
   * 異動的表格名稱
   */
  public void setTableName(String TABLE_NAME) {
    tableName = TABLE_NAME;
  }

  /**
   * 表格 ID
   */
  public Long getTableId() {
    return tableId;
  }

  /**
   * 表格 ID
   */
  public void setTableId(Long TABLE_ID) {
    tableId = TABLE_ID;
  }

  /**
   * 表格欄位名稱
   */
  public String getField() {
    return field;
  }

  /**
   * 表格欄位名稱
   */
  public void setField(String FIELD) {
    field = FIELD;
  }

  /**
   * 異動前
   */
  public String getTranBefore() {
    return tranBefore;
  }

  /**
   * 異動前
   */
  public void setTranBefore(String TRAN_BEFORE) {
    tranBefore = TRAN_BEFORE;
  }

  /**
   * 異動後
   */
  public String getTranAfter() {
    return tranAfter;
  }

  /**
   * 異動後
   */
  public void setTranAfter(String TRAN_AFTER) {
    tranAfter = TRAN_AFTER;
  }

  /**
   * ADD, UPDATE, DELETE
   */
  public String getTranAction() {
    return tranAction;
  }

  /**
   * ADD, UPDATE, DELETE
   */
  public void setTranAction(String TRAN_ACTION) {
    tranAction = TRAN_ACTION;
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