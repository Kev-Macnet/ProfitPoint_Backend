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

  public final static int STATUS_ACTIVE = 1;
  
  public final static int STATUS_INACTIVE = 0;
  
  public final static int STATUS_CHANGE_PASSWORD = -1;
  
  @ApiModelProperty(value = "序號", example = "1", required = false)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  protected Long id;
  
  @ApiModelProperty(value = "證號", example = "A123456789", required = true)
  @Column(name = "ROC_ID", length = 20)
  protected String rocId;
  
  @ApiModelProperty(value = "在HIS中的醫護代碼", example = "A01", required = false)
  @Column(name = "INH_ID", length = 20)
  protected String inhId;

  @ApiModelProperty(value = "帳號", example = "test", required = true)
  @Column(name = "USERNAME", length = 20)
  protected String username;

  @ApiModelProperty(value = "顯示名稱", example = "測試帳號", required = false)
  @Column(name = "DISPLAY_NAME", length = 20)
  protected String displayName;

  @ApiModelProperty(value = "密碼", example = "test1234", required = false)
  @Column(name = "PASSWORD", length = 64)
  protected String password;

  @ApiModelProperty(value = "email address", required = false)
  @Column(name = "EMAIL", length = 30)
  protected String email;

  @ApiModelProperty(value = "A: MIS主管, B: 行政主管, C: 申報主管, D: coding人員或申報人員, E: 醫護人員, Z: 原廠開發者", example = "A", required = false)
  @Column(name = "ROLE")
  protected String role;
  
  @ApiModelProperty(value = "狀態，1: 有效，0: 無效", example = "1", required = false)
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
  
  public USER() {}

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

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getRocId() {
    return rocId;
  }

  public void setRocId(String rocId) {
    this.rocId = rocId;
  }

  public String getInhId() {
    return inhId;
  }

  public void setInhId(String inhId) {
    this.inhId = inhId;
  }
  
}