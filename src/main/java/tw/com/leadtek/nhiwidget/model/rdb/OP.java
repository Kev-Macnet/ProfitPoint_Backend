/**
 * Created on 2021/1/28.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "outpatient")
public class OP {

  @JsonProperty("tdata")
  @JacksonXmlElementWrapper(useWrapping = false)
  private OP_T tdata;

  @JsonProperty("ddata")
  @JacksonXmlElementWrapper(useWrapping = false)
  private List<OP_DData> ddata;

  public OP_T getTdata() {
    return tdata;
  }

  public void setTdata(OP_T tdata) {
    this.tdata = tdata;
  }

  public List<OP_DData> getDdata() {
    return ddata;
  }

  public void setDdata(List<OP_DData> ddata) {
    this.ddata = ddata;
  }

}
