/**
 * Created on 2021/11/12.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("名稱, 代碼, 點數")
public class NameCodePoint implements Serializable {

  private static final long serialVersionUID = -5356308081879530908L;

  @ApiModelProperty(value = "名稱", required = true)
  protected String name;
  
  @ApiModelProperty(value = "代碼", required = true)
  protected String code;
  
  @ApiModelProperty(value = "點數", required = true)
  protected Long point;
  
  public NameCodePoint() {
    
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Long getPoint() {
    return point;
  }

  public void setPoint(Long point) {
    this.point = point;
  }
}
