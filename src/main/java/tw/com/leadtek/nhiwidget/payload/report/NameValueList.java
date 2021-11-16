/**
 * Created on 2021/11/5.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("名稱陣列 + 值陣列，名稱與值為1對1")
public class NameValueList implements Serializable {

  private static final long serialVersionUID = 6134412943825576967L;

  @ApiModelProperty(value = "名稱陣列", required = true)
  protected List<String> names;
  
  @ApiModelProperty(value = "值陣列", required = true)
  protected List<Long> values;
  
  public NameValueList() {
    names = new ArrayList<String>();
    values = new ArrayList<Long>();
  }

  public List<String> getNames() {
    return names;
  }

  public void setNames(List<String> names) {
    this.names = names;
  }

  public List<Long> getValues() {
    return values;
  }

  public void setValues(List<Long> values) {
    this.values = values;
  }
  
  public void append(String name, long value) {
    names.add(names.size(), name);
    values.add(values.size(), value);
  }
  
  public void add(String name, long value) {
    names.add(0, name);
    values.add(0, value);
  }
  
}
