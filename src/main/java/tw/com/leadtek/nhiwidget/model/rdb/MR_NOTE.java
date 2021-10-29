/**
 * Created on 2021/10/14 by GenerateSqlByClass().
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
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Table(name = "MR_NOTE")
@Entity
public class MR_NOTE {

  /**
   * 序號
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  private Long id;

  /**
   * MR TABLE ID
   */
  @Column(name = "MR_ID")
  @JsonIgnore
  private Long mrId;

  /**
   * 註記類別，1: 資訊備註，2: 核刪註記
   */
  @Column(name = "NOTE_TYPE")
  @JsonIgnore
  private Integer noteType;

  /**
   * 用戶操作類別，1新增、 2編輯、3刪除
   */
  @Column(name = "ACTION_TYPE")
  private Integer actionType;

  /**
   * 註記內容
   */
  @Column(name = "NOTE", length = 120)
  private String note;

  /**
   * 核刪醫令
   */
  @Column(name = "CODE", length = 12)
  private String code;

  /**
   * 編輯人員姓名
   */
  @Column(name = "EDITOR", length = 20)
  private String editor;

  /**
   * 輸入日期
   */
  @Column(name = "UPDATE_AT")
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
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
   * 註記類別，1: 資訊備註，2: 核刪註記
   */
  public Integer getNoteType() {
    return noteType;
  }

  /**
   * 註記類別，1: 資訊備註，2: 核刪註記
   */
  public void setNoteType(Integer NOTE_TYPE) {
    noteType = NOTE_TYPE;
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
   * 註記內容
   */
  public String getNote() {
    return note;
  }

  /**
   * 註記內容
   */
  public void setNote(String NOTE) {
    note = NOTE;
  }

  /**
   * 核刪醫令
   */
  public String getCode() {
    return code;
  }

  /**
   * 核刪醫令
   */
  public void setCode(String CODE) {
    code = CODE;
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
   * 輸入日期
   */
  public Date getUpdateAt() {
    return updateAt;
  }

  /**
   * 輸入日期
   */
  public void setUpdateAt(Date UPDATE_AT) {
    updateAt = UPDATE_AT;
  }

}