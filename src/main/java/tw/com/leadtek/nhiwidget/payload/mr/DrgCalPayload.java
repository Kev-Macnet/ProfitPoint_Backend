/**
 * Created on 2022/1/11.
 */
package tw.com.leadtek.nhiwidget.payload.mr;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CAL;

@ApiModel("DRG編碼試算結果")
public class DrgCalPayload extends DRG_CAL {

  @ApiModelProperty(value = "是否為目前選項", example = "false", required = true)
  protected Boolean selected;
  
  public DrgCalPayload(DRG_CAL drg) {
    icdCM1 = drg.getIcdCM1();
    icdOPCode1 = drg.getIcdOPCode1();
    this.drg = drg.getDrg();
    medDot = drg.getMedDot();
    cc = drg.getCc();
    drgSection = drg.getDrgSection();
    drgFix = drg.getDrgFix();
    drgDot = drg.getDrgDot();
    rw = drg.getRw();
    mdc = drg.getMdc();
    avgInDay = drg.getAvgInDay();
    ulimit = drg.getUlimit();
    llimit = drg.getLlimit();
  }

  public Boolean getSelected() {
    return selected;
  }

  public void setSelected(Boolean selected) {
    this.selected = selected;
  }
  
}
