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
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("部門")
@Table(name = "DEPARTMENT")
@Entity
public class DEPARTMENT {

  @ApiModelProperty(value = "序號", example = "1" , required = false)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  private Long id;

  @ApiModelProperty(value = "部門名稱", example = "骨科", required = true)
  @Column(name = "NAME", length = 20)
  private String name;

  @ApiModelProperty(value = "部門代碼", example = "06", required = false)
  @Column(name = "CODE", length = 12)
  private String code;
  
  @ApiModelProperty(value = "對應至健保科別名稱", example = "骨科", required = false)
  @Column(name = "NH_NAME", length = 20)
  private String nhName;

  @ApiModelProperty(value = "對應至健保科別代碼", example = "false06", required = false)
  @Column(name = "NH_CODE", length = 12)
  private String nhCode;

  @ApiModelProperty(value = "說明", example = "骨科", required = false)
  @Column(name = "NOTE", length = 30)
  private String note;

  /**
   * 上一層部門ID
   */
  @ApiModelProperty(hidden = true)
  @Column(name = "PARENT_ID")
  private Long parentId;

  @ApiModelProperty(value = "狀態，1: 有效，0: 無效", example = "1", required = false)
  @Column(name = "STATUS")
  private Integer status;

  /**
   * 更新時間
   */
  @ApiModelProperty(hidden = true)
  @Column(name = "UPDATE_AT", nullable = false)
  @JsonIgnore
  private Date updateAt;

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
   * 部門名稱
   */
  public String getName() {
    return name;
  }

  /**
   * 部門名稱
   */
  public void setName(String NAME) {
    name = NAME;
  }

  /**
   * 代碼
   */
  public String getCode() {
    return code;
  }

  /**
   * 代碼
   */
  public void setCode(String CODE) {
    code = CODE;
  }

  /**
   * 說明
   */
  public String getNote() {
    return note;
  }

  /**
   * 說明
   */
  public void setNote(String NOTE) {
    note = NOTE;
  }

  /**
   * 上一層部門ID
   */
  public Long getParentId() {
    return parentId;
  }

  /**
   * 上一層部門ID
   */
  public void setParentId(Long PARENT_ID) {
    parentId = PARENT_ID;
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

  public String getNhName() {
    return nhName;
  }

  public void setNhName(String nhName) {
    this.nhName = nhName;
  }

  public String getNhCode() {
    return nhCode;
  }

  public void setNhCode(String nhCode) {
    this.nhCode = nhCode;
  }
  
}