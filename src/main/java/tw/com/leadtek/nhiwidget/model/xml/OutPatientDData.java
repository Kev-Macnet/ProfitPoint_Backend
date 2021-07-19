/**
 * Created on 2020/12/24.
 */
package tw.com.leadtek.nhiwidget.model.xml;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tw.com.leadtek.nhiwidget.model.rdb.DHead;

@JsonPropertyOrder({"dhead", "dbody"})
//@Document(collection = "OP_d")
public class OutPatientDData implements Serializable {

  private static final long serialVersionUID = 8838519566400898096L;

  @JsonIgnore
  @Id
  public String id;
  
  @JsonProperty("dhead")
  private DHead dhead;

  @JsonProperty("dbody")
  private OutPatientD dbody;
  
  @JsonIgnore
  private String tid;

  public DHead getDhead() {
    return dhead;
  }

  public void setDhead(DHead dhead) {
    this.dhead = dhead;
  }

  public OutPatientD getDbody() {
    return dbody;
  }

  public void setDbody(OutPatientD dbody) {
    this.dbody = dbody;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTid() {
    return tid;
  }

  public void setTid(String tid) {
    this.tid = tid;
  }
  
}
