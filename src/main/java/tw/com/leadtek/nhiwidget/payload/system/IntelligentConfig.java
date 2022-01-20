/**
 * Created on 2022/1/13.
 */
package tw.com.leadtek.nhiwidget.payload.system;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("智能提示助理功能設定")
public class IntelligentConfig extends BaseResponse {

  private static final long serialVersionUID = -8395316115594410039L;

  @ApiModelProperty(value = "違反支付準則項目-支付準則條件", required = true)
  protected Boolean violate;
  
  @ApiModelProperty(value = "罕見ICD應用", required = true)
  protected Boolean rareIcd;
  
  @ApiModelProperty(value = "應用比例偏高醫材", required = true)
  protected Boolean highRatio;
  
  @ApiModelProperty(value = "特別用量藥材、衛材", required = true)
  protected Boolean overAmount;
  
  @ApiModelProperty(value = "健保項目對應自費項目並存", required = true)
  protected Boolean inhOwnExist;
  
  @ApiModelProperty(value = "法定傳染病", required = true)
  protected Boolean infectious;
  
  @ApiModelProperty(value = "同性質藥物開立", required = true)
  protected Boolean sameAtc;
  
  @ApiModelProperty(value = "同性質藥物開立比對前5碼或前7碼，true:5碼，false:7碼", required = true)
  protected Boolean sameAtcLen5;
  
  @ApiModelProperty(value = "相關計畫疑似可收案病例", required = true)
  protected Boolean pilotProject;
  
  @ApiModelProperty(value = "高風險診斷碼與健保碼組合", required = true)
  protected Boolean highRisk;
  
  public IntelligentConfig() {
    
  }

  public IntelligentConfig(List<PARAMETERS> list) {
    if (list == null || list.size() == 0) {
      return;
    }
    for (PARAMETERS parameters : list) {
      if (parameters.getName().equals("VIOLATE")) {
        violate = "1".equals(parameters.getValue());
      } else if (parameters.getName().equals("RARE_ICD")) {
        rareIcd = "1".equals(parameters.getValue());
      } else if (parameters.getName().equals("HIGH_RATIO")) {
        highRatio = "1".equals(parameters.getValue());
      } else if (parameters.getName().equals("OVER_AMOUNT")) {
        overAmount = "1".equals(parameters.getValue());
      } else if (parameters.getName().equals("INH_OWN_EXIST")) {
        inhOwnExist = "1".equals(parameters.getValue());
      } else if (parameters.getName().equals("INFECTIOUS")) {
        infectious = "1".equals(parameters.getValue());
      } else if (parameters.getName().equals("SAME_ATC")) {
        sameAtc = "1".equals(parameters.getValue());
      } else if (parameters.getName().equals("SAME_ATC_LENGTH")) {
        sameAtcLen5 = Integer.parseInt(parameters.getValue()) == 5;
      } else if (parameters.getName().equals("PILOT_PROJECT")) {
        pilotProject = "1".equals(parameters.getValue());
      } else if (parameters.getName().equals("HIGH_RISK")) {
        highRisk = "1".equals(parameters.getValue());
      }
    }
  }

  public Boolean getViolate() {
    return violate;
  }

  public void setViolate(Boolean violate) {
    this.violate = violate;
  }

  public Boolean getRareIcd() {
    return rareIcd;
  }

  public void setRareIcd(Boolean rareIcd) {
    this.rareIcd = rareIcd;
  }

  public Boolean getHighRatio() {
    return highRatio;
  }

  public void setHighRatio(Boolean highRatio) {
    this.highRatio = highRatio;
  }

  public Boolean getOverAmount() {
    return overAmount;
  }

  public void setOverAmount(Boolean overAmount) {
    this.overAmount = overAmount;
  }

  public Boolean getInhOwnExist() {
    return inhOwnExist;
  }

  public void setInhOwnExist(Boolean inhOwnExist) {
    this.inhOwnExist = inhOwnExist;
  }

  public Boolean getInfectious() {
    return infectious;
  }

  public void setInfectious(Boolean infectious) {
    this.infectious = infectious;
  }

  public Boolean getSameAtc() {
    return sameAtc;
  }

  public void setSameAtc(Boolean sameAtc) {
    this.sameAtc = sameAtc;
  }

  public Boolean getSameAtcLen5() {
    return sameAtcLen5;
  }

  public void setSameAtcLen5(Boolean sameAtcLen5) {
    this.sameAtcLen5 = sameAtcLen5;
  }

  public Boolean getPilotProject() {
    return pilotProject;
  }

  public void setPilotProject(Boolean pilotProject) {
    this.pilotProject = pilotProject;
  }

  public Boolean getHighRisk() {
    return highRisk;
  }

  public void setHighRisk(Boolean highRisk) {
    this.highRisk = highRisk;
  }
  
}
