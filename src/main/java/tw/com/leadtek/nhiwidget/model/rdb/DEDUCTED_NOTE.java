/**
 * Created on 2021/12/14 by GenerateSqlByClass().
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("核刪註記")
@Table(name = "DEDUCTED_NOTE")
@Entity
public class DEDUCTED_NOTE implements Serializable{
  
  private static final long serialVersionUID = -8965054955827025111L;

  @ApiModelProperty(value = "序號", required = false)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  private Long id;

  @ApiModelProperty(value = "MR TABLE ID", required = false)
  @Column(name = "MR_ID")
  @JsonIgnore
  private Long mrId;

  @ApiModelProperty(value = "用戶操作類別，1: 新增，2: 編輯/修改，3: 刪除", required = false)
  @Column(name = "ACTION_TYPE")
  @JsonIgnore
  private Integer actionType;

  @ApiModelProperty(value = "核刪類別，有CIS/立意/隨機/行政", example = "隨機", required = false)
  @Column(name = "CAT", length = 6)
  private String cat;

  @ApiModelProperty(value = "核刪項目，有專案/非專案/藥費", example = "專案", required = false)
  @Column(name = "ITEM", length = 6)
  private String item;

  @ApiModelProperty(value = "次分類", example = "醫令", required = false)
  @Column(name = "SUB_CAT", length = 20)
  private String subCat;

  @ApiModelProperty(value = "大分類，有進階人工核減代碼/專業審查不予支付代碼/程序審查核減代碼", example = "專業審查不予支付代碼", required = false)
  @Column(name = "L1", length = 20)
  private String l1;

  @ApiModelProperty(value = "中分類", example = "西醫", required = false)
  @Column(name = "L2", length = 20)
  private String l2;

  @ApiModelProperty(value = "小分類", example = "診療品質", required = false)
  @Column(name = "L3", length = 50)
  private String l3;

  @ApiModelProperty(value = "核減代碼", example = "0004A", required = false)
  @Column(name = "CODE", length = 16)
  private String code;

  @ApiModelProperty(value = "核刪醫令", example = "09043C", required = false)
  @Column(name = "DEDUCTED_ORDER", length = 12)
  private String deductedOrder;

  @ApiModelProperty(value = "核刪數量", example = "1", required = false)
  @Column(name = "DEDUCTED_QUANTITY")
  private Integer deductedQuantity;

  @ApiModelProperty(value = "核刪總點數", example = "200", required = false)
  @Column(name = "DEDUCTED_AMOUNT")
  private Integer deductedAmount;

  @ApiModelProperty(value = "核減理由", example = "治療與病情診斷不符", required = false)
  @Column(name = "REASON", length = 80)
  private String reason;

  @ApiModelProperty(value = "核刪備註", example ="備註....",  required = false)
  @Column(name = "NOTE", length = 120)
  private String note;

  @ApiModelProperty(value = "放大回推金額(月)", example = "0", required = false)
  @Column(name = "ROLLBACK_M")
  private Integer rollbackM;

  @ApiModelProperty(value = "放大回推金額(季)", example = "0", required = false)
  @Column(name = "ROLLBACK_Q")
  private Integer rollbackQ;

  @ApiModelProperty(value = "申復數量", example = "1", required = false)
  @Column(name = "AFR_QUANTITY")
  private Integer afrQuantity;

  @ApiModelProperty(value = "申復金額", example = "200", required = false)
  @Column(name = "AFR_AMOUNT")
  private Integer afrAmount;

  @ApiModelProperty(value = "申復補付數量", example = "0", required = false)
  @Column(name = "AFR_PAY_QUANTITY")
  private Integer afrPayQuantity;

  @ApiModelProperty(value = "申復補付金額", example = "0", required = false)
  @Column(name = "AFR_PAY_AMOUNT")
  private Integer afrPayAmount;

  @ApiModelProperty(value = "申復不補付理由代碼", example = "0011A", required = false)
  @Column(name = "AFR_NO_PAY_CODE", length = 16)
  private String afrNoPayCode;

  @ApiModelProperty(value = "申復不補付理由說明", example = "一般（簡表）案件用藥影響病人安全之處方", required = false)
  @Column(name = "AFR_NO_PAY_DESC", length = 80)
  private String afrNoPayDesc;

  @ApiModelProperty(value = "申復備註", example = "備註",  required = false)
  @Column(name = "AFR_NOTE", length = 120)
  private String afrNote;

  @ApiModelProperty(value = "爭議數量", example = "1", required = false)
  @Column(name = "DISPUTE_QUANTITY")
  private Integer disputeQuantity;

  @ApiModelProperty(value = "爭議金額", example = "200", required = false)
  @Column(name = "DISPUTE_AMOUNT")
  private Integer disputeAmount;

  @ApiModelProperty(value = "爭議補付數量", example = "1", required = false)
  @Column(name = "DISPUTE_PAY_QUANTITY")
  private Integer disputePayQuantity;

  @ApiModelProperty(value = "爭議補付金額", example = "200", required = false)
  @Column(name = "DISPUTE_PAY_AMOUNT")
  private Integer disputePayAmount;

  @ApiModelProperty(value = "爭議不補付理由代碼", example = "", required = false)
  @Column(name = "DISPUTE_NO_PAY_CODE", length = 16)
  private String disputeNoPayCode;

  @ApiModelProperty(value = "爭議不補付理由說明", required = false)
  @Column(name = "DISPUTE_NO_PAY_DESC", length = 80)
  private String disputeNoPayDesc;

  @ApiModelProperty(value = "爭議備註", required = false)
  @Column(name = "DISPUTE_NOTE", length = 120)
  private String disputeNote;

  @ApiModelProperty(value = "編輯人員姓名", required = false)
  @Column(name = "EDITOR", length = 20)
  @JsonIgnore
  private String editor;

  @ApiModelProperty(value = "是否有效，1:有效，0:無效(被刪除)", required = false)
  @Column(name = "STATUS")
  @JsonIgnore
  private Integer status;

  @ApiModelProperty(value = "更新日期", required = false)
  @Column(name = "UPDATE_AT")
  //@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
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
   * 用戶操作類別，1: 新增，2: 編輯/修改，3: 刪除
   */
  public Integer getActionType() {
    return actionType;
  }

  /**
   * 用戶操作類別，1: 新增，2: 編輯/修改，3: 刪除
   */
  public void setActionType(Integer ACTION_TYPE) {
    actionType = ACTION_TYPE;
  }

  /**
   * 核刪類別
   */
  public String getCat() {
    return cat;
  }

  /**
   * 核刪類別
   */
  public void setCat(String CAT) {
    cat = CAT;
  }

  /**
   * 核刪項目
   */
  public String getItem() {
    return item;
  }

  /**
   * 核刪項目
   */
  public void setItem(String ITEM) {
    item = ITEM;
  }

  /**
   * 次分類
   */
  public String getSubCat() {
    return subCat;
  }

  /**
   * 次分類
   */
  public void setSubCat(String SUB_CAT) {
    subCat = SUB_CAT;
  }

  /**
   * 大分類
   */
  public String getL1() {
    return l1;
  }

  /**
   * 大分類
   */
  public void setL1(String L1) {
    l1 = L1;
  }

  /**
   * 中分類
   */
  public String getL2() {
    return l2;
  }

  /**
   * 中分類
   */
  public void setL2(String L2) {
    l2 = L2;
  }

  /**
   * 小分類
   */
  public String getL3() {
    return l3;
  }

  /**
   * 小分類
   */
  public void setL3(String L3) {
    l3 = L3;
  }

  /**
   * 核減代碼
   */
  public String getCode() {
    return code;
  }

  /**
   * 核減代碼
   */
  public void setCode(String CODE) {
    code = CODE;
  }

  /**
   * 核刪醫令
   */
  public String getDeductedOrder() {
    return deductedOrder;
  }

  /**
   * 核刪醫令
   */
  public void setDeductedOrder(String DEDUCTED_ORDER) {
    deductedOrder = DEDUCTED_ORDER;
  }

  /**
   * 核刪數量
   */
  public Integer getDeductedQuantity() {
    return deductedQuantity;
  }

  /**
   * 核刪數量
   */
  public void setDeductedQuantity(Integer DEDUCTED_QUANTITY) {
    deductedQuantity = DEDUCTED_QUANTITY;
  }

  /**
   * 核刪總點數
   */
  public Integer getDeductedAmount() {
    return deductedAmount;
  }

  /**
   * 核刪總點數
   */
  public void setDeductedAmount(Integer DEDUCTED_AMOUNT) {
    deductedAmount = DEDUCTED_AMOUNT;
  }

  /**
   * 核減理由
   */
  public String getReason() {
    return reason;
  }

  /**
   * 核減理由
   */
  public void setReason(String REASON) {
    reason = REASON;
  }

  /**
   * 核刪備註
   */
  public String getNote() {
    return note;
  }

  /**
   * 核刪備註
   */
  public void setNote(String NOTE) {
    note = NOTE;
  }

  /**
   * 放大回推金額(月)
   */
  public Integer getRollbackM() {
    return rollbackM;
  }

  /**
   * 放大回推金額(月)
   */
  public void setRollbackM(Integer ROLLBACK_M) {
    rollbackM = ROLLBACK_M;
  }

  /**
   * 放大回推金額(季)
   */
  public Integer getRollbackQ() {
    return rollbackQ;
  }

  /**
   * 放大回推金額(季)
   */
  public void setRollbackQ(Integer ROLLBACK_Q) {
    rollbackQ = ROLLBACK_Q;
  }

  /**
   * 申復數量
   */
  public Integer getAfrQuantity() {
    return afrQuantity;
  }

  /**
   * 申復數量
   */
  public void setAfrQuantity(Integer AFR_QUANTITY) {
    afrQuantity = AFR_QUANTITY;
  }

  /**
   * 申復金額
   */
  public Integer getAfrAmount() {
    return afrAmount;
  }

  /**
   * 申復金額
   */
  public void setAfrAmount(Integer AFR_AMOUNT) {
    afrAmount = AFR_AMOUNT;
  }

  /**
   * 申復補付數量
   */
  public Integer getAfrPayQuantity() {
    return afrPayQuantity;
  }

  /**
   * 申復補付數量
   */
  public void setAfrPayQuantity(Integer AFR_PAY_QUANTITY) {
    afrPayQuantity = AFR_PAY_QUANTITY;
  }

  /**
   * 申復補付金額
   */
  public Integer getAfrPayAmount() {
    return afrPayAmount;
  }

  /**
   * 申復補付金額
   */
  public void setAfrPayAmount(Integer AFR_PAY_AMOUNT) {
    afrPayAmount = AFR_PAY_AMOUNT;
  }

  /**
   * 申復不補付理由代碼
   */
  public String getAfrNoPayCode() {
    return afrNoPayCode;
  }

  /**
   * 申復不補付理由代碼
   */
  public void setAfrNoPayCode(String AFR_NO_PAY_CODE) {
    afrNoPayCode = AFR_NO_PAY_CODE;
  }

  /**
   * 申復不補付理由說明
   */
  public String getAfrNoPayDesc() {
    return afrNoPayDesc;
  }

  /**
   * 申復不補付理由說明
   */
  public void setAfrNoPayDesc(String AFR_NO_PAY_DESC) {
    afrNoPayDesc = AFR_NO_PAY_DESC;
  }

  /**
   * 申復備註
   */
  public String getAfrNote() {
    return afrNote;
  }

  /**
   * 申復備註
   */
  public void setAfrNote(String AFR_NOTE) {
    afrNote = AFR_NOTE;
  }

  /**
   * 爭議數量
   */
  public Integer getDisputeQuantity() {
    return disputeQuantity;
  }

  /**
   * 爭議數量
   */
  public void setDisputeQuantity(Integer DISPUTE_QUANTITY) {
    disputeQuantity = DISPUTE_QUANTITY;
  }

  /**
   * 爭議金額
   */
  public Integer getDisputeAmount() {
    return disputeAmount;
  }

  /**
   * 爭議金額
   */
  public void setDisputeAmount(Integer DISPUTE_AMOUNT) {
    disputeAmount = DISPUTE_AMOUNT;
  }

  /**
   * 爭議補付數量
   */
  public Integer getDisputePayQuantity() {
    return disputePayQuantity;
  }

  /**
   * 爭議補付數量
   */
  public void setDisputePayQuantity(Integer DISPUTE_PAY_QUANTITY) {
    disputePayQuantity = DISPUTE_PAY_QUANTITY;
  }

  /**
   * 爭議補付金額
   */
  public Integer getDisputePayAmount() {
    return disputePayAmount;
  }

  /**
   * 爭議補付金額
   */
  public void setDisputePayAmount(Integer DISPUTE_PAY_AMOUNT) {
    disputePayAmount = DISPUTE_PAY_AMOUNT;
  }

  /**
   * 爭議不補付理由代碼
   */
  public String getDisputeNoPayCode() {
    return disputeNoPayCode;
  }

  /**
   * 爭議不補付理由代碼
   */
  public void setDisputeNoPayCode(String DISPUTE_NO_PAY_CODE) {
    disputeNoPayCode = DISPUTE_NO_PAY_CODE;
  }

  /**
   * 爭議不補付理由說明
   */
  public String getDisputeNoPayDesc() {
    return disputeNoPayDesc;
  }

  /**
   * 爭議不補付理由說明
   */
  public void setDisputeNoPayDesc(String DISPUTE_NO_PAY_DESC) {
    disputeNoPayDesc = DISPUTE_NO_PAY_DESC;
  }

  /**
   * 爭議備註
   */
  public String getDisputeNote() {
    return disputeNote;
  }

  /**
   * 爭議備註
   */
  public void setDisputeNote(String DISPUTE_NOTE) {
    disputeNote = DISPUTE_NOTE;
  }

  /**
   * 編輯人員姓名
   */
  public String getEditor() {
    return editor;
  }

  /**
   * 編輯人員姓名
   */
  public void setEditor(String EDITOR) {
    editor = EDITOR;
  }

  /**
   * 是否有效，1:有效，0:無效(被刪除)
   */
  public Integer getStatus() {
    return status;
  }

  /**
   * 是否有效，1:有效，0:無效(被刪除)
   */
  public void setStatus(Integer STATUS) {
    status = STATUS;
  }

  /**
   * 更新日期
   */
  public Date getUpdateAt() {
    return updateAt;
  }

  /**
   * 更新日期
   */
  public void setUpdateAt(Date UPDATE_AT) {
    updateAt = UPDATE_AT;
  }

}