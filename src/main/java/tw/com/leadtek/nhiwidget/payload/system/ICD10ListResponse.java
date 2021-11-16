/**
 * Created on 2021/11/1.
 */
package tw.com.leadtek.nhiwidget.payload.system;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.ICD10;
import tw.com.leadtek.nhiwidget.payload.BasePageResponse;

public class ICD10ListResponse extends BasePageResponse implements Serializable {

  private static final long serialVersionUID = -6833536754792443369L;

  @ApiModelProperty(value = "ICD10代碼陣列", required = false)
  public List<ICD10> data;

  public List<ICD10> getData() {
    return data;
  }

  public void setData(List<ICD10> data) {
    this.data = data;
  }

}
