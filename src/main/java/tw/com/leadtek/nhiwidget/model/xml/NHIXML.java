/**
 * Created on 2021/1/27.
 */
package tw.com.leadtek.nhiwidget.model.xml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class NHIXML {
  
  @JsonProperty("inpatient")
  @JacksonXmlElementWrapper(useWrapping = false)
  private InPatient inp;

  @JsonProperty("outpatient")
  @JacksonXmlElementWrapper(useWrapping = false)
  private OutPatient oup;

  public InPatient getInp() {
    return inp;
  }

  public void setInp(InPatient inp) {
    this.inp = inp;
  }

  public OutPatient getOup() {
    return oup;
  }

  public void setOup(OutPatient oup) {
    this.oup = oup;
  }

}
