/**
 * Created on 2021/8/27.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.io.Serializable;

/**
 * 因應 DRG_CAL table 複合 key 所需的 class.
 * @author kenlai
 *
 */
public class DRG_CALId implements Serializable {

  private static final long serialVersionUID = -8850461234175584481L;

  private Long mrId;

  private String icdCM1;
  
  public DRG_CALId() {
    
  }

  public DRG_CALId(Long mrId, String icdCM1) {
    this.mrId = mrId;
    this.icdCM1 = icdCM1;
  }

  public Long getMrId() {
    return mrId;
  }

  public void setMrId(Long mrId) {
    this.mrId = mrId;
  }

  public String getIcdCM1() {
    return icdCM1;
  }

  public void setIcdCM1(String icdCM1) {
    this.icdCM1 = icdCM1;
  }

  @Override
  public int hashCode() {
    return mrId.hashCode() + icdCM1.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof DRG_CALId) {
      DRG_CALId pk = (DRG_CALId) object;
      return mrId.equals(pk.mrId) && icdCM1.equals(pk.getIcdCM1());
    } else {
      return false;
    }
  }
}
