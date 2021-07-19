/**
 * Created on 2021/05/03 by GenerateSqlByClass().
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Table(name = "USER_DEPARTMENT")
@Entity
public class USER_DEPARTMENT {

  /**
   * USER TABLE ID
   */
  @Id
  @Column(name = "USER_ID", nullable = false)
  @JsonIgnore
  private Long userId;

  /**
   * DEPARTMENT TABLE ID
   */
  @Column(name = "DEPARTMENT_ID", nullable = false)
  @JsonIgnore
  private Long departmentId;

  /**
   * USER TABLE ID
   */
  public Long getUserId() {
    return userId;
  }

  /**
   * USER TABLE ID
   */
  public void setUserId(Long USER_ID) {
    userId = USER_ID;
  }

  /**
   * DEPARTMENT TABLE ID
   */
  public Long getDepartmentId() {
    return departmentId;
  }

  /**
   * DEPARTMENT TABLE ID
   */
  public void setDepartmentId(Long DEPARTMENT_ID) {
    departmentId = DEPARTMENT_ID;
  }

}