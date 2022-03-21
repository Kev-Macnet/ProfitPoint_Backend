/**
 * Created on 2022/3/18.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("門急診和住院及出院人次變化-時間區間人次清單")
public class VisitsPeriod implements Serializable {

  private static final long serialVersionUID = -181881587937552541L;

  @ApiModelProperty(value = "總人次(含手術)", required = true)
  private VisitsPeriodDetail total;
  
  @ApiModelProperty(value = "手術人次", required = true)
  private VisitsPeriodDetail surgery;
  
  @ApiModelProperty(value = "上月同區間總人次相比差額", required = true)
  private VisitsPeriodDetail diff;
  
  @ApiModelProperty(value = "上月同區間總差額率-門急診和住院", required = true)
  private Float percentAll;
  
  @ApiModelProperty(value = "上月同區間總差額率-門急診", required = true)
  private Float percentOpem;
  
  @ApiModelProperty(value = "上月同區間總差額率-門診(早)", required = true)
  private Float percentOpMorning;
  
  @ApiModelProperty(value = "上月同區間總差額率-門診(中)", required = true)
  private Float percentOpAfternoon;
  
  @ApiModelProperty(value = "上月同區間總差額率-門診(晚)", required = true)
  private Float percentOpNight;
  
  @ApiModelProperty(value = "上月同區間總差額率-急診", required = true)
  private Float percentEm;
  
  @ApiModelProperty(value = "上月同區間總差額率-住院", required = true)
  private Float percentIp;
  
  @ApiModelProperty(value = "上月同區間總差額率-出院", required = true)
  private Float percentLeave;
  
  public VisitsPeriod() {
    percentAll = 0f;
    percentOpem = 0f;
    percentOpMorning = 0f;
    percentOpAfternoon = 0f;
    percentOpNight = 0f;
    percentEm = 0f;
    percentIp = 0f;
    percentLeave = 0f;
  }

  public VisitsPeriodDetail getTotal() {
    return total;
  }

  public void setTotal(VisitsPeriodDetail total) {
    this.total = total;
  }

  public VisitsPeriodDetail getSurgery() {
    return surgery;
  }

  public void setSurgery(VisitsPeriodDetail surgery) {
    this.surgery = surgery;
  }

  public VisitsPeriodDetail getDiff() {
    return diff;
  }

  public void setDiff(VisitsPeriodDetail diff) {
    this.diff = diff;
  }

  public Float getPercentAll() {
    return percentAll;
  }

  public void setPercentAll(Float percentAll) {
    this.percentAll = percentAll;
  }

  public Float getPercentOpem() {
    return percentOpem;
  }

  public void setPercentOpem(Float percentOpem) {
    this.percentOpem = percentOpem;
  }

  public Float getPercentOpMorning() {
    return percentOpMorning;
  }

  public void setPercentOpMorning(Float percentOpMorning) {
    this.percentOpMorning = percentOpMorning;
  }

  public Float getPercentOpAfternoon() {
    return percentOpAfternoon;
  }

  public void setPercentOpAfternoon(Float percentOpAfternoon) {
    this.percentOpAfternoon = percentOpAfternoon;
  }

  public Float getPercentOpNight() {
    return percentOpNight;
  }

  public void setPercentOpNight(Float percentOpNight) {
    this.percentOpNight = percentOpNight;
  }

  public Float getPercentEm() {
    return percentEm;
  }

  public void setPercentEm(Float percentEm) {
    this.percentEm = percentEm;
  }

  public Float getPercentIp() {
    return percentIp;
  }

  public void setPercentIp(Float percentIp) {
    this.percentIp = percentIp;
  }

  public Float getPercentLeave() {
    return percentLeave;
  }

  public void setPercentLeave(Float percentLeave) {
    this.percentLeave = percentLeave;
  }
  
}