/**
 * Created on 2021/11/01 by GenerateSqlByClass().
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.io.Serializable;
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

@Table(name = "ICD10")
@Entity
@ApiModel("ICD10代碼")
public class ICD10 implements Serializable {

  private static final long serialVersionUID = 5078009954032081470L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @ApiModelProperty(value = "id", example = "1", required = false)
  private Long id;

  @Column(name = "CAT", length = 20)
  @ApiModelProperty(value = "代碼類別，CM:診斷碼，PCS:處置碼", example = "CM", required = false)
  private String cat;

  @Column(name = "CODE", length = 12)
  @ApiModelProperty(value = "代碼", example = "0SG13ZJ", required = false)
  private String code;

  @Column(name = "DESC_CHI", length = 250)
  @ApiModelProperty(value = "中文說明", example = "經皮2節以上腰椎關節由後側進入前柱融合術", required = false)
  private String descChi;

  @Column(name = "DESC_EN", length = 100)
  @ApiModelProperty(value = "英文說明",
      example = "Fusion of 2 or more Lumbar Vertebral Joints, Posterior Approach, Anterior Column, Percutaneous Approach",
      required = false)
  private String descEn;

  @Column(name = "INFECTIOUS")
  @ApiModelProperty(value = "是否為法定傳染病。1: 是，0: 否", example = "0", required = false)
  private Integer infectious;

  @Column(name = "INF_CAT", length = 1)
  @ApiModelProperty(value = "法定傳染病類別", example = "1", required = false)
  private String infCat;

  @Column(name = "REMARK", length = 100)
  @ApiModelProperty(value = "備註", example = "補充說明欄", required = false)
  private String remark;
  
  @ApiModelProperty(value = "存放在Redis中HASH的field值", example = "1", required = false)
  @Column(name = "REDIS_ID")
  @JsonIgnore
  private Long redisId;

  public ICD10() {

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
   * 代碼類別，CM:診斷碼，PCS:處置碼
   */
  public String getCat() {
    return cat;
  }

  /**
   * 代碼類別，CM:診斷碼，PCS:處置碼
   */
  public void setCat(String CAT) {
    cat = CAT;
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
   * 中文說明
   */
  public String getDescChi() {
    return descChi;
  }

  /**
   * 中文說明
   */
  public void setDescChi(String DESC_CHI) {
    descChi = DESC_CHI;
  }

  /**
   * 英文說明
   */
  public String getDescEn() {
    return descEn;
  }

  /**
   * 英文說明
   */
  public void setDescEn(String DESC_EN) {
    descEn = DESC_EN;
  }

  /**
   * 是否為法定傳染病。1: 是，0: 否
   */
  public Integer getInfectious() {
    return infectious;
  }

  /**
   * 是否為法定傳染病。1: 是，0: 否
   */
  public void setInfectious(Integer INFECTIOUS) {
    infectious = INFECTIOUS;
  }

  /**
   * 法定傳染病類別
   */
  public String getInfCat() {
    return infCat;
  }

  /**
   * 法定傳染病類別
   */
  public void setInfCat(String INF_CAT) {
    infCat = INF_CAT;
  }

  /**
   * 備註
   */
  public String getRemark() {
    return remark;
  }

  /**
   * 備註
   */
  public void setRemark(String REMARK) {
    remark = REMARK;
  }

  public Long getRedisId() {
    return redisId;
  }

  public void setRedisId(Long redisId) {
    this.redisId = redisId;
  }

}
