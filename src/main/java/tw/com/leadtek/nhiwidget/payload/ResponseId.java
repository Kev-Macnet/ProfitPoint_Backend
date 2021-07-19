/**
 * Created on 2020/10/21.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("加了 id 的回覆訊息")
public class ResponseId extends BaseResponse implements Serializable{

  private static final long serialVersionUID = -2183099958324555614L;
  
  @ApiModelProperty(value = "object id", example = "5fae0fb0cc451074a045fa69", required = true)
  private String id;
  
  public ResponseId(String result, String message, String id) {
    setResult(result);
    setMessage(message);
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
  
}
