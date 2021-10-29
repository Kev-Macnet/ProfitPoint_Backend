/**
 * Created on 2021/10/27 by GenerateSqlByClass().
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.io.Serializable;
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

@Table(name = "DEDUCTED")
@Entity
@ApiModel("核減代碼")
public class DEDUCTED implements Serializable {

  private static final long serialVersionUID = 3200001731594612865L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @ApiModelProperty(value = "id", example = "1", required = false)
  private Long id;

  @Column(name = "L1", length = 20)
  @ApiModelProperty(value = "大分類", example = "專業審查不予支付代碼", required = true)
  private String l1;

  @Column(name = "L2", length = 20)
  @ApiModelProperty(value = "中分類", example = "西醫", required = true)
  private String l2;

  @Column(name = "L3", length = 50)
  
  private String l3;

  @Column(name = "CODE", length = 16)
  @ApiModelProperty(value = "核檢代碼", example = "0001A", required = true)
  private String code;

  @Column(name = "NAME", length = 80)
  @ApiModelProperty(value = "代碼名稱", example = "診療品質不符專業認定，理由____", required = true)
  private String name;

  /**
   * 啟用狀態，1:已啟用，0:未啟用
   */
  @Column(name = "STATUS")
  @JsonIgnore
  private Integer status;

  /**
   * 更新時間
   */
  @Column(name = "UPDATE_AT", nullable = false)
  @JsonIgnore
  private Date updateAt;
  
  public DEDUCTED() {
    
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
   * 大分類
   */
  public String getL1() {
    return l1;
  }

  /**
   * 大分類
   */
  public void setL1(String L1) {
    l1 = L1;
  }

  /**
   * 中分類
   */
  public String getL2() {
    return l2;
  }

  /**
   * 中分類
   */
  public void setL2(String L2) {
    l2 = L2;
  }

  /**
   * 小分類
   */
  public String getL3() {
    return l3;
  }

  /**
   * 小分類
   */
  public void setL3(String L3) {
    l3 = L3;
  }

  /**
   * 核減代碼
   */
  public String getCode() {
    return code;
  }

  /**
   * 核減代碼
   */
  public void setCode(String CODE) {
    code = CODE;
  }

  /**
   * 中文名稱
   */
  public String getName() {
    return name;
  }

  /**
   * 中文名稱
   */
  public void setName(String NAME) {
    name = NAME;
  }

  /**
   * 啟用狀態，1:已啟用，0:未啟用
   */
  public Integer getStatus() {
    return status;
  }

  /**
   * 啟用狀態，1:已啟用，0:未啟用
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

}