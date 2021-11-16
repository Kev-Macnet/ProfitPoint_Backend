/**
 * Created on 2021/11/4.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_MONTHLY;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_MR;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("單月健保點數總表")
public class PointMRPayload extends BaseResponse implements Serializable {

  private static final long serialVersionUID = -4800113569180059126L;

  @ApiModelProperty(value = "當月健保點數", required = true)
  private POINT_MONTHLY current;
  
  @ApiModelProperty(value = "上個月健保點數", required = true)
  private POINT_MONTHLY lastM;
  
  @ApiModelProperty(value = "去年同期健保點數", required = true)
  private POINT_MONTHLY lastY;
  
  @ApiModelProperty(value = "與上個月總額的差額", required = true)
  private Long diffAllLastM;
  
  @ApiModelProperty(value = "與去年同期總額的差額", required = true)
  private Long diffAllLastY;
  
  @ApiModelProperty(value = "與上個月門急診的差額", required = true)
  private Long diffOpAllLastM;
  
  @ApiModelProperty(value = "與去年同期門急診的差額", required = true)
  private Long diffOpAllLastY;
  
  @ApiModelProperty(value = "與上個月門診的差額", required = true)
  private Long diffOpLastM;
  
  @ApiModelProperty(value = "與去年同期門診的差額", required = true)
  private Long diffOpLastY;
  
  @ApiModelProperty(value = "與上個月急診的差額", required = true)
  private Long diffEmLastM;
  
  @ApiModelProperty(value = "與去年同期急診的差額", required = true)
  private Long diffEmLastY;
  
  @ApiModelProperty(value = "與上個月住院的差額", required = true)
  private Long diffIpLastM;
  
  @ApiModelProperty(value = "與去年同期住院的差額", required = true)
  private Long diffIpLastY;
  
  public PointMRPayload() {
    
  }

  public POINT_MONTHLY getCurrent() {
    return current;
  }

  public void setCurrent(POINT_MONTHLY current) {
    this.current = current;
  }

  public POINT_MONTHLY getLastM() {
    return lastM;
  }

  public void setLastM(POINT_MONTHLY lastM) {
    this.lastM = lastM;
  }

  public POINT_MONTHLY getLastY() {
    return lastY;
  }

  public void setLastY(POINT_MONTHLY lastY) {
    this.lastY = lastY;
  }

  public Long getDiffAllLastM() {
    return diffAllLastM;
  }

  public void setDiffAllLastM(Long diffAllLastM) {
    this.diffAllLastM = diffAllLastM;
  }

  public Long getDiffAllLastY() {
    return diffAllLastY;
  }

  public void setDiffAllLastY(Long diffAllLastY) {
    this.diffAllLastY = diffAllLastY;
  }

  public Long getDiffOpAllLastM() {
    return diffOpAllLastM;
  }

  public void setDiffOpAllLastM(Long diffOpAllLastM) {
    this.diffOpAllLastM = diffOpAllLastM;
  }

  public Long getDiffOpAllLastY() {
    return diffOpAllLastY;
  }

  public void setDiffOpAllLastY(Long diffOpAllLastY) {
    this.diffOpAllLastY = diffOpAllLastY;
  }

  public Long getDiffOpLastM() {
    return diffOpLastM;
  }

  public void setDiffOpLastM(Long diffOpLastM) {
    this.diffOpLastM = diffOpLastM;
  }

  public Long getDiffOpLastY() {
    return diffOpLastY;
  }

  public void setDiffOpLastY(Long diffOpLastY) {
    this.diffOpLastY = diffOpLastY;
  }

  public Long getDiffEmLastM() {
    return diffEmLastM;
  }

  public void setDiffEmLastM(Long diffEmLastM) {
    this.diffEmLastM = diffEmLastM;
  }

  public Long getDiffEmLastY() {
    return diffEmLastY;
  }

  public void setDiffEmLastY(Long diffEmLastY) {
    this.diffEmLastY = diffEmLastY;
  }

  public Long getDiffIpLastM() {
    return diffIpLastM;
  }

  public void setDiffIpLastM(Long diffIpLastM) {
    this.diffIpLastM = diffIpLastM;
  }

  public Long getDiffIpLastY() {
    return diffIpLastY;
  }

  public void setDiffIpLastY(Long diffIpLastY) {
    this.diffIpLastY = diffIpLastY;
  }
  
  public void calculateDifference() {
    diffAllLastM = current.getApplAll()- lastM.getApplAll();
    diffAllLastY = current.getApplAll() - lastY.getApplAll();
    
    diffOpAllLastM = current.getApplOpAll()- lastM.getApplOpAll();
    diffOpAllLastY = current.getApplOpAll()- lastY.getApplOpAll();
    
    diffOpLastM = current.getApplOp() - lastM.getApplOp();
    diffOpLastY = current.getApplOp() - lastY.getApplOp();
    
    diffEmLastM = current.getApplEm() - lastM.getApplEm();
    diffEmLastY = current.getApplEm() - lastY.getApplEm();
    
    diffIpLastM = current.getApplIp() - lastM.getApplIp();
    diffIpLastY = current.getApplIp() - lastY.getApplIp();
  }
}
