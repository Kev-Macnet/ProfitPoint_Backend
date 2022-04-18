/**
 * Created on 2021/11/5.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("名稱, 代碼, 點數, 件數")
public class NameCodePointQuantity extends NameCodePoint implements Serializable {

  private static final long serialVersionUID = 3907511005876173981L;

  @ApiModelProperty(value = "件數", required = true)
  private Long quantity;
  
  public NameCodePointQuantity() {
    
  }

  public Long getQuantity() {
    return quantity;
  }

  public void setQuantity(Long quantity) {
    this.quantity = quantity;
  }
  
  public NameCodePointQuantity clone() {
    NameCodePointQuantity result = new NameCodePointQuantity();
    result.setCode(this.code);
    result.setName(this.name);
    result.setPoint(this.point);
    result.setQuantity(this.quantity);
    return result;
  }
  
}
