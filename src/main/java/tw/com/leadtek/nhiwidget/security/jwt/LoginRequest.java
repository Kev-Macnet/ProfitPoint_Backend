/**
 * Created on 2020/10/28.
 */
package tw.com.leadtek.nhiwidget.security.jwt;

import javax.validation.constraints.NotBlank;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("登入時的帳號密碼")
public class LoginRequest {

  @ApiModelProperty(value = "帳號", example = "test", required = true)
  @NotBlank
  private String username;
  
  @ApiModelProperty(value = "密碼(更換密碼時為舊密碼)", example = "leadtek", required = true)
  @NotBlank
  private String password;
  
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
