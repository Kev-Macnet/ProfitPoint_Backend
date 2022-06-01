/**
 * Created on 2022/5/20.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.io.Serializable;

public class USER_DEPARTMENT_KEYS implements Serializable {

  private static final long serialVersionUID = -3379386576755521059L;

  private Long userId;

  private Long departmentId;
  
  public USER_DEPARTMENT_KEYS() {
    
  }
  
  public USER_DEPARTMENT_KEYS(Long userId, Long departmentId) {
    this.userId = userId;
    this.departmentId = departmentId;
  }
  
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }


  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  @Override
  public String toString() {
    return "USER_DEPARTMENT_KEYS {userId=" + userId + " ,departmentId=" + departmentId + "}";
  }
  
  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof USER_DEPARTMENT_KEYS) {
      USER_DEPARTMENT_KEYS pk = (USER_DEPARTMENT_KEYS) object;
      return userId.longValue() == pk.getUserId().longValue()
          && departmentId.longValue() == pk.getDepartmentId().longValue();
    }
    return false;
  }
}
