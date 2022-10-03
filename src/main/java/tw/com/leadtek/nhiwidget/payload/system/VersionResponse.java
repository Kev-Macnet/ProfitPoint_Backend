package tw.com.leadtek.nhiwidget.payload.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("版本編號")
public class VersionResponse extends BaseResponse {

  private static final long serialVersionUID = -4703852777853949254L;
  
  @ApiModelProperty(value = "版本編號", example = "V1A.1.1", required = true)
  protected String vesion;
  
  @ApiModelProperty(value = "顯示AI選單，1:要顯示，其他值則不顯示", example = "1", required = false)
  protected Integer a;
  
  public VersionResponse() {
    
  }
  
  public VersionResponse(String version, Integer a) {
    this.vesion = version;
    this.a = a;
  }

  public String getVesion() {
    return vesion;
  }

  public void setVesion(String vesion) {
    this.vesion = vesion;
  }

  public Integer getA() {
    return a;
  }

  public void setA(Integer a) {
    this.a = a;
  }
}
