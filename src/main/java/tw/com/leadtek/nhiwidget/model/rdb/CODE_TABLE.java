/**
 * Created on 2021/02/01 by GenerateSqlByClass().
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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("代碼表")
@Table(name = "CODE_TABLE")
@Entity
public class CODE_TABLE {

  /**
   * 序號
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @JsonIgnore
  @ApiModelProperty(hidden = true)
  private Long id;

  /**
   * 代碼類別
   */
  @ApiModelProperty(value = "代碼類別", example = "FUNC_TYPE", required = true)
  @Column(name = "CAT", length = 20)
  private String cat;

  /**
   * 代碼
   */
  @ApiModelProperty(value = "代碼", example = "02", required = true)
  @Column(name = "CODE", length = 12)
  private String code;

  /**
   * 中文說明
   */
  @ApiModelProperty(value = "中文說明", example = "內科", required = true)
  @Column(name = "DESC_CHI", length = 100)
  @JsonProperty("name")
  private String descChi;

  /**
   * 英文說明
   */
  @ApiModelProperty(value = "英文說明", required = false)
  @Column(name = "DESC_EN", length = 100)
  @JsonIgnore
  private String descEn;

  /**
   * 父代碼，隸屬於PARENT_CODE之下。如BC(胸腔外科)父代碼為03(外科)
   */
  @ApiModelProperty(value = "父代碼", required = false)
  @Column(name = "PARENT_CODE", length = 12)
  private String parentCode;

  /**
   * 備註
   */
  @ApiModelProperty(hidden = true)
  @Column(name = "REMARK", length = 50)
  private String remark;

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
   * 代碼類別
   */
  public String getCat() {
    return cat;
  }

  /**
   * 代碼類別
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
   * 父代碼，隸屬於PARENT_CODE之下。如BC(胸腔外科)父代碼為03(外科)
   */
  public String getParentCode() {
    return parentCode;
  }

  /**
   * 父代碼，隸屬於PARENT_CODE之下。如BC(胸腔外科)父代碼為03(外科)
   */
  public void setParentCode(String PARENT_CODE) {
    parentCode = PARENT_CODE;
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

  public static CODE_TABLE initial(String[] ss, int codeIndex) {
    if (codeIndex >= ss.length) {
      return null;
    } else if ("null".equals(ss[codeIndex])) {
      return null;
    }
    CODE_TABLE result = new CODE_TABLE();
    result.setCode(ss[codeIndex]);
    result.setDescChi(ss[codeIndex + 1]);
    if (ss.length > (codeIndex + 2)) {
      result.setRemark(ss[codeIndex + 2]);
    }
    return result;
  }
}
