/**
 * Created on 2021/5/3.
 */
package tw.com.leadtek.nhiwidget.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.USER;

@ApiModel("用戶帳號")
public class UserRequest extends USER {
  
  @ApiModelProperty(value = "所屬部門，若有一個以上，用,區隔", required = true)
  protected String departments;
  
  public UserRequest() {
    
  }
  
  public UserRequest(USER user) {
    id = user.getId();
    username = user.getUsername();
    displayName = user.getDisplayName();
    password = user.getPassword();
    email = user.getEmail();
    role = user.getRole();
    status = user.getStatus();
    createAt = user.getCreateAt();
    updateAt = user.getUpdateAt();
  }

  public String getDepartments() {
    return departments;
  }

  public void setDepartments(String departments) {
    this.departments = departments;
  }
  
  public USER convertToUSER() {
    USER result = new USER();
    result.setUsername(getUsername());
    result.setDisplayName(getDisplayName());
    result.setEmail(getEmail());
    result.setId(getId());
    result.setPassword(getPassword());
    result.setStatus(getStatus());
    result.setCreateAt(getCreateAt());
    result.setUpdateAt(getUpdateAt());
    result.setRole(getRole());
    return result;
  }
  
}
