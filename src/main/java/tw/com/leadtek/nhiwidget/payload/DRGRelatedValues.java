/**
 * Created on 2021/9/22.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("DRG相關參數設定")
public class DRGRelatedValues extends BaseResponse implements Serializable {

  private static final long serialVersionUID = 7978778847481416834L;
  
  @ApiModelProperty(value = "id", example = "320", required = false)
  private Long id;
  
  @ApiModelProperty(value = "標準給付額", example = "45837", required = true)
  private Integer spr;
  
  @ApiModelProperty(value = "CMI加成率", example = "0", required = false)
  private String cmi;
  
  @ApiModelProperty(value = "CMI值大於 1.1 小於等於1.2加成率", example = "1.1", required = false)
  private String cmi12;
  
  @ApiModelProperty(value = "CMI值大於1.2小於等於1.3加成率", example = "1.2", required = false)
  private String cmi13;
  
  @ApiModelProperty(value = "CMI值大於1.3加成率", example = "1.3", required = false)
  private String cmi14;

  @ApiModelProperty(value = "生效日", example = "2022/01/01", required = true)
  private String startDate;
  
  @ApiModelProperty(value = "生效訖日", example = "2022/06/30", required = true)  
  private String endDate;
  
  @ApiModelProperty(value = "醫學中心基本診療加成百分比", example = "7.1", required = false)
  private String addHospLevel1;
  
  @ApiModelProperty(value = "區域醫院基本診療加成百分比", example = "6.1", required = false)
  private String addHospLevel2;
  
  @ApiModelProperty(value = "地區醫院基本診療加成百分比", example = "5", required = false)
  private String addHospLevel3;
  
  @ApiModelProperty(value = "兒童加成率MDC15小於6個月", example = "23", required = false)
  private String add15Child6m;
  
  @ApiModelProperty(value = "兒童加成率MDC15大於6個月，小於2歲", example = "9", required = false)
  private String add15Child2y;
  
  @ApiModelProperty(value = "兒童加成率MDC15大於2歲，小於6歲", example = "10", required = false)
  private String add15Child6y;
  
  @ApiModelProperty(value = "兒童加成率非MDC15內科小於6個月", example = "91", required = false)
  private String addN15MChild6m;
  
  @ApiModelProperty(value = "兒童加成率非MDC15內科大於6個月，小於2歲", example = "23", required = false)
  private String addN15MChild2y;
  
  @ApiModelProperty(value = "兒童加成率非MDC15內科大於2歲，小於6歲", example = "15", required = false)
  private String addN15MChild6y;
  
  @ApiModelProperty(value = "兒童加成率非MDC15外科小於6個月", example = "66", required = false)
  private String addN15PChild6m;
  
  @ApiModelProperty(value = "兒童加成率非MDC15外科大於6個月，小於2歲", example = "21", required = false)
  private String addN15PChild2y;
  
  @ApiModelProperty(value = "兒童加成率非MDC15外科大於2歲，小於6歲", example = "10", required = false)
  private String addN15PChild6y;
  
  @ApiModelProperty(value = "山地離島加成率", example = "2", required = false)
  private String outlyingIslands;
  
  @ApiModelProperty(value = "狀態", required = false)
  protected String status;
  
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getAddHospLevel1() {
    return addHospLevel1;
  }

  public void setAddHospLevel1(String addHospLevel1) {
    this.addHospLevel1 = addHospLevel1;
  }

  public String getAddHospLevel2() {
    return addHospLevel2;
  }

  public void setAddHospLevel2(String addHospLevel2) {
    this.addHospLevel2 = addHospLevel2;
  }

  public String getAddHospLevel3() {
    return addHospLevel3;
  }

  public void setAddHospLevel3(String addHospLevel3) {
    this.addHospLevel3 = addHospLevel3;
  }

  public String getAdd15Child6m() {
    return add15Child6m;
  }

  public void setAdd15Child6m(String add15Child6m) {
    this.add15Child6m = add15Child6m;
  }

  public String getAdd15Child2y() {
    return add15Child2y;
  }

  public void setAdd15Child2y(String add15Child2y) {
    this.add15Child2y = add15Child2y;
  }

  public String getAdd15Child6y() {
    return add15Child6y;
  }

  public void setAdd15Child6y(String add15Child6y) {
    this.add15Child6y = add15Child6y;
  }

  public String getAddN15MChild6m() {
    return addN15MChild6m;
  }

  public void setAddN15MChild6m(String addN15MChild6m) {
    this.addN15MChild6m = addN15MChild6m;
  }

  public String getAddN15MChild2y() {
    return addN15MChild2y;
  }

  public void setAddN15MChild2y(String addN15MChild2y) {
    this.addN15MChild2y = addN15MChild2y;
  }

  public String getAddN15MChild6y() {
    return addN15MChild6y;
  }

  public void setAddN15MChild6y(String addN15MChild6y) {
    this.addN15MChild6y = addN15MChild6y;
  }

  public String getAddN15PChild6m() {
    return addN15PChild6m;
  }

  public void setAddN15PChild6m(String addN15PChild6m) {
    this.addN15PChild6m = addN15PChild6m;
  }

  public String getAddN15PChild2y() {
    return addN15PChild2y;
  }

  public void setAddN15PChild2y(String addN15PChild2y) {
    this.addN15PChild2y = addN15PChild2y;
  }

  public String getAddN15PChild6y() {
    return addN15PChild6y;
  }

  public void setAddN15PChild6y(String addN15PChild6y) {
    this.addN15PChild6y = addN15PChild6y;
  }

  public String getOutlyingIslands() {
    return outlyingIslands;
  }

  public void setOutlyingIslands(String outlyingIslands) {
    this.outlyingIslands = outlyingIslands;
  }

  public Integer getSpr() {
    return spr;
  }

  public void setSpr(Integer spr) {
    this.spr = spr;
  }

  public String getCmi() {
    return cmi;
  }

  public void setCmi(String cmi) {
    this.cmi = cmi;
  }

  public String getCmi12() {
    return cmi12;
  }

  public void setCmi12(String cmi12) {
    this.cmi12 = cmi12;
  }

  public String getCmi13() {
    return cmi13;
  }

  public void setCmi13(String cmi13) {
    this.cmi13 = cmi13;
  }

  public String getCmi14() {
    return cmi14;
  }

  public void setCmi14(String cmi14) {
    this.cmi14 = cmi14;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

}
