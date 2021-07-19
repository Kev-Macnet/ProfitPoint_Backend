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

@Table(name = "ORDER_CHECK")
@Entity
public class ORDER_CHECK {

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
   * 醫令/藥品代碼
   */
  @Column(name = "ITEM", length = 20)
  @JsonIgnore
  private String item;

  /**
   * 1: 醫令，2: 藥物、衛材，3: 科別，4: 小時，5: 天數，6: 年齡(歲數)
   */
  @Column(name = "ITEM_UNIT")
  @JsonIgnore
  private Integer itemUnit;

  /**
   * 診別，0:不分，1:門診，2:急診，3:住院
   */
  @Column(name = "DIAGNOSIS")
  @JsonIgnore
  private Integer diagnosis;

  /**
   * 第一組條件運算子
   */
  @Column(name = "OP1", length = 10)
  @JsonIgnore
  private String opQ;

  /**
   * 值參考ITEM_UNIT
   */
  @Column(name = "UNIT1")
  @JsonIgnore
  private Integer unitQ;

  /**
   * 第一組比對值，若有多筆，以,分隔
   */
  @Column(name = "VALUE1", length = 120)
  @JsonIgnore
  private String valueQ;

  /**
   * AND : OP1 和 OP2 需同時存在；OR : OP1 或OP2 擇一
   */
  @Column(name = "OP_1A2", length = 2)
  @JsonIgnore
  private String op1aR;

  /**
   * 第二組條件運算子
   */
  @Column(name = "OP2", length = 10)
  @JsonIgnore
  private String opR;

  /**
   * 值參考ITEM_UNIT
   */
  @Column(name = "UNIT2")
  @JsonIgnore
  private Integer unitR;

  /**
   * 第二組比對值，若有多筆，以,分隔
   */
  @Column(name = "VALUE2", length = 120)
  @JsonIgnore
  private String valueR;

  /**
   * 1:警告，2:禁止
   */
  @Column(name = "ALERT")
  @JsonIgnore
  private Integer alert;

  /**
   * 原由
   */
  @Column(name = "REASON", length = 100)
  @JsonIgnore
  private String reason;
  
  /**
   * 詳細資訊
   */
  @Column(name = "DETAIL", length = 200)
  @JsonIgnore
  private String detail;

  /**
   * 0:失效，1:生效
   */
  @Column(name = "STATUS")
  @JsonIgnore
  private Integer status;

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
   * 醫令/藥品代碼
   */
  public String getItem() {
    return item;
  }

  /**
   * 醫令/藥品代碼
   */
  public void setItem(String ITEM) {
    item = ITEM;
  }

  /**
   * 1: 醫令，2: 藥物、衛材，3: 科別，4: 小時，5: 天數，6: 年齡(歲數)
   */
  public Integer getItemUnit() {
    return itemUnit;
  }

  /**
   * 1: 醫令，2: 藥物、衛材，3: 科別，4: 小時，5: 天數，6: 年齡(歲數)
   */
  public void setItemUnit(Integer ITEM_UNIT) {
    itemUnit = ITEM_UNIT;
  }

  /**
   * 診別，0:不分，1:門診，2:急診，3:住院
   */
  public Integer getDiagnosis() {
    return diagnosis;
  }

  /**
   * 診別，0:不分，1:門診，2:急診，3:住院
   */
  public void setDiagnosis(Integer DIAGNOSIS) {
    diagnosis = DIAGNOSIS;
  }

  /**
   * 第一組條件運算子
   */
  public String getOpQ() {
    return opQ;
  }

  /**
   * 第一組條件運算子
   */
  public void setOpQ(String OP1) {
    opQ = OP1;
  }

  /**
   * 值參考ITEM_UNIT
   */
  public Integer getUnitQ() {
    return unitQ;
  }

  /**
   * 值參考ITEM_UNIT
   */
  public void setUnitQ(Integer UNIT1) {
    unitQ = UNIT1;
  }

  /**
   * 第一組比對值，若有多筆，以,分隔
   */
  public String getValueQ() {
    return valueQ;
  }

  /**
   * 第一組比對值，若有多筆，以,分隔
   */
  public void setValueQ(String VALUE1) {
    valueQ = VALUE1;
  }

  /**
   * AND : OP1 和 OP2 需同時存在；OR : OP1 或OP2 擇一
   */
  public String getOp1aR() {
    return op1aR;
  }

  /**
   * AND : OP1 和 OP2 需同時存在；OR : OP1 或OP2 擇一
   */
  public void setOp1aR(String OP_1A2) {
    op1aR = OP_1A2;
  }

  /**
   * 第二組條件運算子
   */
  public String getOpR() {
    return opR;
  }

  /**
   * 第二組條件運算子
   */
  public void setOpR(String OP2) {
    opR = OP2;
  }

  /**
   * 值參考ITEM_UNIT
   */
  public Integer getUnitR() {
    return unitR;
  }

  /**
   * 值參考ITEM_UNIT
   */
  public void setUnitR(Integer UNIT2) {
    unitR = UNIT2;
  }

  /**
   * 第二組比對值，若有多筆，以,分隔
   */
  public String getValueR() {
    return valueR;
  }

  /**
   * 第二組比對值，若有多筆，以,分隔
   */
  public void setValueR(String VALUE2) {
    valueR = VALUE2;
  }

  /**
   * 1:警告，2:禁止
   */
  public Integer getAlert() {
    return alert;
  }

  /**
   * 1:警告，2:禁止
   */
  public void setAlert(Integer ALERT) {
    alert = ALERT;
  }

  /**
   * 備註
   */
  public String getReason() {
    return reason;
  }

  /**
   * 備註
   */
  public void setReason(String REASON) {
    reason = REASON;
  }

  /**
   * 0:失效，1:生效
   */
  public Integer getStatus() {
    return status;
  }

  /**
   * 0:失效，1:生效
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

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

}