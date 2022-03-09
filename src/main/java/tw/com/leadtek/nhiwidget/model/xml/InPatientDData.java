/**
 * Created on 2020/12/24.
 */
package tw.com.leadtek.nhiwidget.model.xml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tw.com.leadtek.nhiwidget.model.rdb.DHead;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;

@JsonPropertyOrder({"dhead", "dbody"})
//@Document(collection = "IP_d")
public class InPatientDData {

  @JsonProperty("dhead")
  private DHead dhead;
  
  @JsonProperty("dbody")
  private IP_D dbody;

  public DHead getDhead() {
    return dhead;
  }

  public void setDhead(DHead dhead) {
    this.dhead = dhead;
  }

  public IP_D getDbody() {
    return dbody;
  }

  public void setDbody(IP_D dbody) {
    this.dbody = dbody;
  }
  
}
