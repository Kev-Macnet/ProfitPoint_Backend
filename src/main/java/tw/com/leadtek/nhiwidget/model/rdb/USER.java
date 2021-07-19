/**
 * Created on 2021/05/03 by GenerateSqlByClass().
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("用戶帳號")
@Table(name = "USER")
@Entity
public class USER {

  @ApiModelProperty(value = "序號", required = false)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  protected Long id;

  @ApiModelProperty(value = "帳號", required = true)
  @Column(name = "USERNAME", length = 20)
  protected String username;

  @ApiModelProperty(value = "顯示名稱", required = false)
  @Column(name = "DISPLAY_NAME", length = 20)
  protected String displayName;

  @ApiModelProperty(value = "密碼", required = false)
  @Column(name = "PASSWORD", length = 64)
  protected String password;

  @ApiModelProperty(value = "email", required = false)
  @Column(name = "EMAIL", length = 30)
  protected String email;

  @ApiModelProperty(value = "1: 一般user, 2: 醫師, 3: 主管, 4: 系統管理員, 5: 原廠開發者", required = false)
  @Column(name = "ROLE")
  protected Integer role;
  
  @ApiModelProperty(value = "狀態，1: 有效，0: 無效", required = false)
  @Column(name = "STATUS")
  protected Integer status;

  /**
   * 帳號建立時間
   */
  @ApiModelProperty(hidden = true)
  @Column(name = "CREATE_AT", nullable = false)
  protected Date createAt;

  /**
   * 更新時間
   */
  @ApiModelProperty(hidden = true)
  @Column(name = "UPDATE_AT", nullable = false)
  protected Date updateAt;

  /**
   * 序號
   */
  public Long getId() {
    return id;
  }

  /**
   * 序號
   */
  public void setId(Long ID) {
    id = ID;
  }

  /**
   * 登入帳號
   */
  public String getUsername() {
    return username;
  }

  /**
   * 登入帳號
   */
  public void setUsername(String USERNAME) {
    username = USERNAME;
  }

  /**
   * 顯示名稱
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * 顯示名稱
   */
  public void setDisplayName(String DISPLAY_NAME) {
    displayName = DISPLAY_NAME;
  }

  /**
   * 密碼
   */
  public String getPassword() {
    return password;
  }

  /**
   * 密碼
   */
  public void setPassword(String PASSWORD) {
    password = PASSWORD;
  }

  /**
   * EMAIL
   */
  public String getEmail() {
    return email;
  }

  /**
   * EMAIL
   */
  public void setEmail(String EMAIL) {
    email = EMAIL;
  }

  /**
   * 狀態，1: 有效，0: 無效
   */
  public Integer getStatus() {
    return status;
  }

  /**
   * 狀態，1: 有效，0: 無效
   */
  public void setStatus(Integer STATUS) {
    status = STATUS;
  }

  /**
   * 帳號建立時間
   */
  public Date getCreateAt() {
    return createAt;
  }

  /**
   * 帳號建立時間
   */
  public void setCreateAt(Date CREATE_AT) {
    createAt = CREATE_AT;
  }

  /**
   * 更新時間
   */
  public Date getUpdateAt() {
    return updateAt;
  }

  /**
   * 更新時間
   */
  public void setUpdateAt(Date UPDATE_AT) {
    updateAt = UPDATE_AT;
  }

  public Integer getRole() {
    return role;
  }

  public void setRole(Integer role) {
    this.role = role;
  }
  
}