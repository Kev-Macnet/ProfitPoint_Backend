/**
 * Created on 2021/1/5.
 */
package tw.com.leadtek.nhiwidget.model;

import java.io.Serializable;

//@Document(collection = "xml_tag")
public class XMLTag extends CodeBaseId implements Serializable {

  private static final long serialVersionUID = -6505337584904156712L;

  /**
   * category :類別
   */
  private String cat;

  public String getCat() {
    return cat;
  }

  public void setCat(String cat) {
    this.cat = cat;
  }
  
}
