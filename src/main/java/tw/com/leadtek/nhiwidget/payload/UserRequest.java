/**
 * Created on 2021/5/3.
 */
package tw.com.leadtek.nhiwidget.payload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.USER;

@ApiModel("用戶帳號Response")
public class UserRequest extends USER {
  
  @ApiModelProperty(value = "所屬部門，若有一個以上，用逗號區隔", example ="骨科，急診醫學科", required = true)
  protected String departments;
  
  @ApiModelProperty(value = "所屬部門代碼，若有一個以上，用逗號區隔", example ="88，22", required = true)
  protected String departmentId;
  
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
    inhId = user.getInhId();
    createAt = user.getCreateAt();
    updateAt = user.getUpdateAt();
  }
  
  
  public UserRequest(String message) {
    displayName = message;
  }

  public String getDepartments() {
    return departments;
  }

  public void setDepartments(String departments) {
    this.departments = departments;
  }
  
  public String getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(String departmentId) {
    this.departmentId = departmentId;
  }

  public USER convertToUSER() {
    USER result = new USER();
    result.setUsername(getUsername());
    result.setDisplayName(getDisplayName());
    result.setEmail(getEmail());
    result.setId(getId());
    if (getPassword() != null && getPassword().length() > 0) {
      result.setPassword(getPassword());
    }
    result.setStatus(getStatus());
    result.setCreateAt(getCreateAt());
    result.setUpdateAt(getUpdateAt());
    result.setRole(getRole());
    return result;
  }
  
}
