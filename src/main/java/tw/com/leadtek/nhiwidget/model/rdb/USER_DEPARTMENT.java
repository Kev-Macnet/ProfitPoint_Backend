/**
 * Created on 2021/05/03 by GenerateSqlByClass().
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Table(name = "USER_DEPARTMENT")
@Entity
@IdClass(USER_DEPARTMENT_KEYS.class)
public class USER_DEPARTMENT {

  /**
   * USER TABLE ID
   */
  @Column(name = "USER_ID", nullable = false)
  @Id
  @JsonIgnore
  private Long userId;

  /**
   * DEPARTMENT TABLE ID
   */
  @Column(name = "DEPARTMENT_ID", nullable = false)
  @Id
  @JsonIgnore
  private Long departmentId;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long USER_ID) {
    userId = USER_ID;
  }

  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long DEPARTMENT_ID) {
    departmentId = DEPARTMENT_ID;
  }

}