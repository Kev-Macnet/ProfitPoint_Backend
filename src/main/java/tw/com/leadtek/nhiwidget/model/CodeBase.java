/**
 * Created on 2020/12/29.
 */
package tw.com.leadtek.nhiwidget.model;

import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;

@ApiModel("代碼及說明")
public class CodeBase {
  
  @ApiModelProperty(value = "代碼", example = "A2", required = true)
  protected String code;

  @ApiModelProperty(value = "中文說明", example = "耳鼻喉科檢查", required = false)
  protected String desc;

  @ApiModelProperty(value = "英文名稱", example = "耳鼻喉科檢查", required = false)
  protected String descEn;
  
  @ApiModelProperty(value = "更新時間", required = false)
  protected Date updateAt;
  
  public CodeBase() {
    
  }
  
  public CodeBase(String code) {
    this.code = code;
  }
  
  public CodeBase(CODE_TABLE ct) {
    this.code = ct.getCode();
    this.desc = ct.getDescChi();
    this.descEn = ct.getDescEn();
  }
  
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getDescEn() {
    return descEn;
  }

  public void setDescEn(String descEn) {
    this.descEn = descEn;
  }
  
  public Date getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(Date updateAt) {
    this.updateAt = updateAt;
  }

  public static CodeBase initial(String[] ss, int codeIndex) {
    if (codeIndex >= ss.length) {
      return null;
    } else if ("null".equals(ss[codeIndex])) {
      return null;
    }
    CodeBase result = new CodeBase();
    result.setCode(ss[codeIndex]);
    result.setDesc(ss[codeIndex + 1]);
    if (ss.length > (codeIndex + 2)) {
      result.setDescEn(ss[codeIndex + 2]);
    }
    return result;
  }
  
}
