/**
 * Created on 2021/9/7.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("含筆數及總頁數的回覆訊息")
public class BasePageResponse extends BaseResponse implements Serializable {

  private static final long serialVersionUID = 3559451295979801766L;
  
  @ApiModelProperty(value = "總筆數", example = "123", required = true)
  protected int count;
  
  @ApiModelProperty(value = "總頁數", example = "20", required = true)
  protected int totalPage;

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getTotalPage() {
    return totalPage;
  }

  public void setTotalPage(int totalPage) {
    this.totalPage = totalPage;
  }
  
}
