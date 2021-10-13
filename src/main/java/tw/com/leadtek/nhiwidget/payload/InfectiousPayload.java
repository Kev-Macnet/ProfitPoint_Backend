/**
 * Created on 2021/9/24.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;

@ApiModel("法定傳染病")
public class InfectiousPayload implements Serializable {

  private static final long serialVersionUID = -3661066928009814227L;

  @ApiModelProperty(value = "ICD診斷碼", required = true)
  private String icd;
  
  @ApiModelProperty(value = "診斷名稱", required = true)
  private String name;
  
  @ApiModelProperty(value = "分類", required = true)
  private String cat;
  
  @ApiModelProperty(value = "是否啟用", required = true)
  private Boolean status;
  
  public InfectiousPayload() {
    
  }
  
  public InfectiousPayload(CODE_TABLE ct) {
    icd = ct.getCode();
    name = ct.getDescChi();
    cat = ct.getParentCode();
    status = ct.getRemark() == null;
  }

  public String getIcd() {
    return icd;
  }

  public void setIcd(String icd) {
    this.icd = icd;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCat() {
    return cat;
  }

  public void setCat(String cat) {
    this.cat = cat;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }
  
  
}
