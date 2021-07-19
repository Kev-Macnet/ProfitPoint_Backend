/**
 * Created on 2021/1/28.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"dhead", "dbody"})
public class IP_DData {

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

