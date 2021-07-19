/**
 * Created on 2021/1/28.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"dhead", "dbody"})
public class OP_DData {

  @JsonProperty("dhead")
  private DHead dhead;

  @JsonProperty("dbody")
  private OP_D dbody;
  
  public DHead getDhead() {
    return dhead;
  }

  public void setDhead(DHead dhead) {
    this.dhead = dhead;
  }

  public OP_D getDbody() {
    return dbody;
  }

  public void setDbody(OP_D dbody) {
    this.dbody = dbody;
  }
}

