/**
 * Created on 2022/3/18.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("門急診-住院-出院人次變化-時間區間點數")
public class PointPeriod implements Serializable {

  private static final long serialVersionUID = 6771918492748375061L;

  @ApiModelProperty(value = "門急診-住院點數", required = true)
  private Long all;

  @ApiModelProperty(value = "門急診點數", required = true)
  private Long opem;

  @ApiModelProperty(value = "門診(早)點數", required = true)
  private Long opMorning;

  @ApiModelProperty(value = "門診(中)點數", required = true)
  private Long opAfternoon;

  @ApiModelProperty(value = "門診(晚)點數", required = true)
  private Long opNight;

  @ApiModelProperty(value = "急診點數", required = true)
  private Long em;

  @ApiModelProperty(value = "住院點數", required = true)
  private Long ip;

  public PointPeriod() {
    all = 0L;
    opem = 0L;
    opMorning = 0L;
    opAfternoon = 0L;
    opNight = 0L;
    em = 0L;
    ip = 0L;
  }

  public Long getAll() {
    return all;
  }

  public void setAll(Long all) {
    this.all = all;
  }

  public Long getOpem() {
    return opem;
  }

  public void setOpem(Long opem) {
    this.opem = opem;
  }

  public Long getOpMorning() {
    return opMorning;
  }

  public void setOpMorning(Long opMorning) {
    this.opMorning = opMorning;
  }

  public Long getOpAfternoon() {
    return opAfternoon;
  }

  public void setOpAfternoon(Long opAfternoon) {
    this.opAfternoon = opAfternoon;
  }

  public Long getOpNight() {
    return opNight;
  }

  public void setOpNight(Long opNight) {
    this.opNight = opNight;
  }

  public Long getEm() {
    return em;
  }

  public void setEm(Long em) {
    this.em = em;
  }

  public Long getIp() {
    return ip;
  }

  public void setIp(Long ip) {
    this.ip = ip;
  }

}
