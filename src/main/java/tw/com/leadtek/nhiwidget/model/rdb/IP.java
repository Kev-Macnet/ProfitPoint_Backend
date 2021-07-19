/**
 * Created on 2021/1/28.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * in-patient 住院申報資料
 */
@JacksonXmlRootElement(localName = "inpatient")
public class IP {

  @JsonProperty("tdata")
  @JacksonXmlElementWrapper(useWrapping = false)
  private IP_T tdata;

  @JsonProperty("ddata")
  @JacksonXmlElementWrapper(useWrapping = false)
  private List<IP_DData> ddata;

  public IP_T getTdata() {
    return tdata;
  }

  public void setTdata(IP_T tdata) {
    this.tdata = tdata;
  }

  public List<IP_DData> getDdata() {
    return ddata;
  }

  public void setDdata(List<IP_DData> ddata) {
    this.ddata = ddata;
  }
}
