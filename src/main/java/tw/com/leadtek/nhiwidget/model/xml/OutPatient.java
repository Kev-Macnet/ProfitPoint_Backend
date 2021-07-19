/**
 * Created on 2020/12/24.
 */
package tw.com.leadtek.nhiwidget.model.xml;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "outpatient")
public class OutPatient {

  @JsonProperty("tdata")
  @JacksonXmlElementWrapper(useWrapping=false)
  private OutPatientT tdata;
  
  @JsonProperty("ddata")
  @JacksonXmlElementWrapper(useWrapping=false)
  private List<OutPatientDData> ddata;

  public OutPatientT getTdata() {
    return tdata;
  }

  public void setTdata(OutPatientT tdata) {
    this.tdata = tdata;
  }

  public List<OutPatientDData> getDdata() {
    return ddata;
  }

  public void setDdata(List<OutPatientDData> ddata) {
    this.ddata = ddata;
  }
  
}
