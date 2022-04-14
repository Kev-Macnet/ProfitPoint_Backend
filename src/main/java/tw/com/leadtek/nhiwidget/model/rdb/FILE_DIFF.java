/**
 * Created on 2022/03/01 by GenerateSqlByClass().
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

@ApiModel("序號")
@Table(name = "FILE_DIFF")
@Entity
public class FILE_DIFF {

  @ApiModelProperty(value = "序號", required = false)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  private Long id;

  @ApiModelProperty(value = "MR TABLE ID", required = false)
  @Column(name = "MR_ID")
  private Long mrId;

  @ApiModelProperty(value = "欄位名稱", required = false)
  @Column(name = "NAME", length = 40)
  private String name;
  
  @ApiModelProperty(value = "陣列位置，若非陣列值為-1", required = false)
  @Column(name = "ARRAY_INDEX")
  private Integer arrayIndex;

  @ApiModelProperty(value = "新的值", required = false)
  @Column(name = "NEW_VALUE", length = 100)
  private String newValue;

  @ApiModelProperty(value = "建立時間", required = false)
  @Column(name = "CREATE_AT")
  private Date createAt;
  
  public FILE_DIFF() {
    
  }
  
  public FILE_DIFF(Long mrId, String name, String newValue) {
    this.mrId = mrId;
    this.name = name;
    this.newValue = newValue;
    this.arrayIndex = -1;
    createAt = new Date();
  }
  
  public FILE_DIFF(Long mrId, String name, int index, String newValue) {
    this.mrId = mrId;
    this.name = name;
    this.newValue = newValue;
    this.arrayIndex = index;
    createAt = new Date();
  }

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
   * MR TABLE ID
   */
  public Long getMrId() {
    return mrId;
  }

  /**
   * MR TABLE ID
   */
  public void setMrId(Long MR_ID) {
    mrId = MR_ID;
  }

  /**
   * 欄位名稱
   */
  public String getName() {
    return name;
  }

  /**
   * 欄位名稱
   */
  public void setName(String NAME) {
    name = NAME;
  }

  /**
   * 新的值
   */
  public String getNewValue() {
    return newValue;
  }

  /**
   * 新的值
   */
  public void setNewValue(String NEW_VALUE) {
    newValue = NEW_VALUE;
  }

  /**
   * 建立時間
   */
  public Date getCreateAt() {
    return createAt;
  }

  /**
   * 建立時間
   */
  public void setCreateAt(Date CREATE_AT) {
    createAt = CREATE_AT;
  }

  public Integer getArrayIndex() {
    return arrayIndex;
  }

  public void setArrayIndex(Integer arrayIndex) {
    this.arrayIndex = arrayIndex;
  }

}