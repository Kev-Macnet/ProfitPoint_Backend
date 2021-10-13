/**
 * Created on 2020/9/25.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("回覆訊息")
public class BaseResponse implements Serializable {

  private static final long serialVersionUID = -7241762696516780045L;

  public final static String SUCCESS = "success";

  public final static String ERROR = "error";

  @ApiModelProperty(value = "結果", example = SUCCESS, required = true)
  protected String result = SUCCESS;

  @ApiModelProperty(value = "訊息", example = "錯誤訊息", required = false)
  protected String message;

  public BaseResponse() {}

  public BaseResponse(String result) {
    this.result = result;
  }

  public BaseResponse(String result, String message) {
    this.result = result;
    this.message = message;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}
