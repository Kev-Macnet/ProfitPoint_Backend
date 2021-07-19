/**
 * Created on 2020/12/28.
 */
package tw.com.leadtek.nhiwidget.model.xml;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "inpatient")
public class InPatient {

  @JsonProperty("tdata")
  @JacksonXmlElementWrapper(useWrapping = false)
  private InPatientT tdata;

  @JsonProperty("ddata")
  @JacksonXmlElementWrapper(useWrapping = false)
  private List<InPatientDData> ddata;

  public InPatientT getTdata() {
    return tdata;
  }

  public void setTdata(InPatientT tdata) {
    this.tdata = tdata;
  }

  public List<InPatientDData> getDdata() {
    return ddata;
  }

  public void setDdata(List<InPatientDData> ddata) {
    this.ddata = ddata;
  }
}
