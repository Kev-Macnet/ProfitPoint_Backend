/**
 * Created on 2021/10/14.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.MR_NOTE;
import tw.com.leadtek.nhiwidget.model.rdb.MR_NOTICE;

@ApiModel("病歷資料備註/核刪註記")
public class MrNotePayload implements Serializable {

  private static final long serialVersionUID = -3311990396624265581L;

  @ApiModelProperty(value = "id", required = false)
  private Long id;

  @ApiModelProperty(value = "用戶操作類別，新增、 編輯、刪除", example = "新增", required = true)
  private String actionType;

  @ApiModelProperty(value = "備註內容", example = "請吳醫師補上報告分析資訊", required = true)
  private String note;

  @ApiModelProperty(value = "核刪醫令", example = "0107C", required = false)
  private String code;

  @ApiModelProperty(value = "編輯人員姓名，新增/修改時不需帶入", example = "測試人員", required = false)
  private String editor;

  @ApiModelProperty(value = "更新時間", example = "2021/10/14 14:26:00", required = false)
  @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
  private Date updateAt;

  public MrNotePayload() {}

  public MrNotePayload(MR_NOTE mn) {
    id = mn.getId();
    actionType = actionTypeString(mn.getActionType());
    note = mn.getNote();
    code = mn.getCode();
    editor = mn.getEditor();
    updateAt = mn.getUpdateAt();
  }
  
  public MR_NOTE toDB(long mrId, boolean isMrNote) {
    MR_NOTE result = new MR_NOTE();
    result.setActionType(actionTypeInt(actionType));
    result.setCode(code);
    result.setEditor(editor);
    result.setId(id);
    result.setMrId(mrId);
    result.setNote(note);
    result.setNoteType(isMrNote ? 1 : 2);
    result.setUpdateAt(new Date());
    result.setStatus(1);
    return result;
  }

  public static String actionTypeString(int a) {
    switch (a) {
      case 1:
        return "新增";
      case 2:
        return "修改";
      case 3:
        return "刪除";
    }
    return "Unknow";
  }
  
  public static int actionTypeInt(String s) {
    if (s.equals("新增")) {
      return 1; 
    }
    if (s.equals("修改")) {
      return 2; 
    }
    if (s.equals("刪除")) {
      return 3; 
    }
    return -1;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getActionType() {
    return actionType;
  }

  public void setActionType(String actionType) {
    this.actionType = actionType;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  public Date getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(Date updateAt) {
    this.updateAt = updateAt;
  }

}
