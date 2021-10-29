/**
 * Created on 2021/10/12.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;

@ApiModel("同性質藥物開立列表")
public class SameATCListPayload implements Serializable {

  private static final long serialVersionUID = -6755953183273593724L;

  @ApiModelProperty(value = "id", example = "1", required = false)
  protected Long id;

  @ApiModelProperty(value = "ATC分類", example = "", required = false)
  protected String atc;
  
  @ApiModelProperty(value = "院內碼", example = "", required = false)
  protected String inhCode;
  
  @ApiModelProperty(value = "支付標準代碼", example = "", required = false)
  protected String code;
  
  @ApiModelProperty(value = "藥品名稱", example = "", required = false)
  protected String name;
  
  @ApiModelProperty(value = "啟用狀態", example = "", required = false)
  protected Boolean status;
  
  public SameATCListPayload() {
    
  }
  
  public SameATCListPayload(PAY_CODE pc) {
    id = pc.getId();
    atc = pc.getAtc();
    inhCode = pc.getInhCode();
    code = pc.getCode();
    name = pc.getName();
    status = pc.getSameAtc().intValue() == 1;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAtc() {
    return atc;
  }

  public void setAtc(String atc) {
    this.atc = atc;
  }

  public String getInhCode() {
    return inhCode;
  }

  public void setInhCode(String inhCode) {
    this.inhCode = inhCode;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }
  
}
