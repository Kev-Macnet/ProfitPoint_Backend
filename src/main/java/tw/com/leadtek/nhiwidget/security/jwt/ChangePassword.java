/**
 * Created on 2021/5/5.
 */
package tw.com.leadtek.nhiwidget.security.jwt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("更換密碼")
public class ChangePassword {

  @ApiModelProperty(value = "舊密碼", example = "leadtek", required = true)
  private String oldPassword;
  
  @ApiModelProperty(value = "新密碼", example = "leadtek", required = true)
  private String newPassword;

  public String getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(String password) {
    this.oldPassword = password;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
  
  

}
