/**
 * Created on 2021/1/18.
 */
package tw.com.leadtek.nhiwidget.model;

import org.springframework.data.annotation.Id;

public class CodeBaseId extends CodeBase {

  @Id
  private String id;
  
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
