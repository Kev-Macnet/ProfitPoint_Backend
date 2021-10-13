/**
 * Created on 2020/10/28.
 */
package tw.com.leadtek.nhiwidget.security.jwt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("login成功後回傳的JWT")
public class JwtResponse {

  @ApiModelProperty(value = "JWT", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwidWlkIjoiNWZiY2JmNmYxMjdkZWIwOGM1OGZjMjlmIiwiZXhwIjoxNjE1MjcwNTU0fQ.g6dfOms4tK5hhnDbJyYoAaXSSGH4QPopl1sLK2559EQ", required = true)
  private String token;

//  @ApiModelProperty(value = "用戶帳號id", example = "5fbcbf6f127deb08c58fc29f", required = true)
//  private Long id;
  
  @ApiModelProperty(value = "用戶帳號角色", example = "A", required = true)
  private String role;

  @ApiModelProperty(value = "用戶登入帳號", example = "test", required = true)
  private String username;

  @ApiModelProperty(value = "用戶帳號顯示名稱", example = "test", required = true)
  private String displayName;

  public JwtResponse(String token, String role, String username, String displayName) {
    this.token = token;
    this.role = role;
    this.username = username;
    this.displayName = displayName;
  }

  public String getRole() {
    return role;
  }

  public void setId(String role) {
    this.role = role;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

}

