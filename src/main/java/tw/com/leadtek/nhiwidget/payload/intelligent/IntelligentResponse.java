/**
 * Created on 2021/11/19.
 */
package tw.com.leadtek.nhiwidget.payload.intelligent;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BasePageResponse;

@ApiModel("智能提示助理清單")
public class IntelligentResponse extends BasePageResponse implements Serializable {

  private static final long serialVersionUID = -556733660911743334L;

  @ApiModelProperty(value = "智能提示助理陣列", required = false)
  private List<IntelligentRecord> data;

  @ApiModelProperty(value = "違反支付準則項目-支付準則條件病歷數", example = "3", required = false)
  private Integer violate = 0;
  
  @ApiModelProperty(value = "罕見ICD應用病歷數", example = "3", required = false)
  private Integer rareIcd = 0;
  
  @ApiModelProperty(value = "應用比例偏高醫令病歷數", example = "3", required = false)
  private Integer highRatio = 0;
  
  @ApiModelProperty(value = "特別用量藥材病歷數", example = "3", required = false)
  private Integer overAmount = 0;
  
  @ApiModelProperty(value = "衛材病歷數", example = "3", required = false)
  private Integer material = 0;
  
  @ApiModelProperty(value = "健保項目對應自費項目並存病歷數", example = "3", required = false)
  private Integer inhOwnExist = 0;
  
  @ApiModelProperty(value = "法定傳染病病歷數", example = "3", required = false)
  private Integer infectious = 0;
  
  @ApiModelProperty(value = "同性質藥物開立病歷數", example = "3", required = false)
  private Integer sameAtc = 0;
  
  @ApiModelProperty(value = "相關計畫疑似可收案病例病歷數", example = "3", required = false)
  private Integer pilotProject = 0;
  
  @ApiModelProperty(value = "高風險診斷碼與健保碼組合病歷數", example = "3", required = false)
  private Integer highRisk = 0;
  
  @ApiModelProperty(value = "臨床路徑差異-費用差異", example = "3", required = false)
  private Integer costDiff = 0;
  
  @ApiModelProperty(value = "臨床路徑差異-醫療行為差異", example = "3", required = false)
  private Integer orderDiff = 0;
  
  @ApiModelProperty(value = "臨床路徑差異-用藥差異", example = "3", required = false)
  private Integer orderDrug = 0;
  
  @ApiModelProperty(value = "臨床路徑差異-住院天數差異", example = "3", required = false)
  private Integer ipDays = 0;
  
  public IntelligentResponse() {
    
  }

  public List<IntelligentRecord> getData() {
    return data;
  }

  public void setData(List<IntelligentRecord> data) {
    this.data = data;
  }

  public Integer getViolate() {
    return violate;
  }

  public void setViolate(Integer violate) {
    this.violate = violate;
  }

  public Integer getRareIcd() {
    return rareIcd;
  }

  public void setRareIcd(Integer rareIcd) {
    this.rareIcd = rareIcd;
  }

  public Integer getHighRatio() {
    return highRatio;
  }

  public void setHighRatio(Integer highRatio) {
    this.highRatio = highRatio;
  }

  public Integer getOverAmount() {
    return overAmount;
  }

  public void setOverAmount(Integer overAmount) {
    this.overAmount = overAmount;
  }

  public Integer getMaterial() {
    return material;
  }

  public void setMaterial(Integer material) {
    this.material = material;
  }

  public Integer getInhOwnExist() {
    return inhOwnExist;
  }

  public void setInhOwnExist(Integer inhOwnExist) {
    this.inhOwnExist = inhOwnExist;
  }

  public Integer getInfectious() {
    return infectious;
  }

  public void setInfectious(Integer infectious) {
    this.infectious = infectious;
  }

  public Integer getSameAtc() {
    return sameAtc;
  }

  public void setSameAtc(Integer sameAtc) {
    this.sameAtc = sameAtc;
  }

  public Integer getPilotProject() {
    return pilotProject;
  }

  public void setPilotProject(Integer pilotProject) {
    this.pilotProject = pilotProject;
  }

  public Integer getHighRisk() {
    return highRisk;
  }

  public void setHighRisk(Integer highRisk) {
    this.highRisk = highRisk;
  }

  public Integer getCostDiff() {
    return costDiff;
  }

  public void setCostDiff(Integer costDiff) {
    this.costDiff = costDiff;
  }

  public Integer getOrderDiff() {
    return orderDiff;
  }

  public void setOrderDiff(Integer orderDiff) {
    this.orderDiff = orderDiff;
  }

  public Integer getOrderDrug() {
    return orderDrug;
  }

  public void setOrderDrug(Integer orderDrug) {
    this.orderDrug = orderDrug;
  }

  public Integer getIpDays() {
    return ipDays;
  }

  public void setIpDays(Integer ipDays) {
    this.ipDays = ipDays;
  }
  
}
