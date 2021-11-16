/**
 * Created on 2021/11/11.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("名稱陣列 + 值陣列 + 值2陣列，名稱與值及值2為1對1")
public class NameValueList2 extends NameValueList implements Serializable {
  
  private static final long serialVersionUID = -3683251650949623453L;
  
  @ApiModelProperty(value = "值陣列", required = true)
  private List<Long> values2;
  
  @ApiModelProperty(value = "顯示名稱", required = false)
  private String displayName;
  
  public NameValueList2() {
    names = new ArrayList<String>();
    values = new ArrayList<Long>();
    values2 = new ArrayList<Long>();
  }
  
  public NameValueList2(String displayName) {
    this();
    this.displayName = displayName;
  }

  public List<Long> getValues2() {
    return values2;
  }

  public void setValues2(List<Long> values2) {
    this.values2 = values2;
  }
  
  public void add(String name, long value, long value2) {
    names.add(0 ,name);
    values.add(0,value);
    values2.add(0, value2);
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
}
