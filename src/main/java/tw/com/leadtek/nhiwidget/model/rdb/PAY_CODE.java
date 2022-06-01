/**
 * Created on 2021/09/10 by GenerateSqlByClass().
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
import tw.com.leadtek.nhiwidget.model.redis.OrderCode;

@ApiModel("支付標準代碼")
@Table(name = "PAY_CODE")
@Entity
public class PAY_CODE implements Serializable {

  
  private static final long serialVersionUID = -4138986327884237230L;

  @ApiModelProperty(value = "存在DB的id", example = "1", required = false)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  protected Long id;

  @ApiModelProperty(value = "代碼", example = "P56008", required = false)
  @Column(name = "CODE")
  protected String code;

  @ApiModelProperty(value = "支付標準名稱", example = "藥品調劑費", required = false)
  @Column(name = "NAME")
  protected String name;
  
  @ApiModelProperty(value = "英文名稱", example = "TRANZEPAM TABLETS", required = false)
  @Column(name = "NAME_EN")
  protected String nameEn;

  @ApiModelProperty(value = "健保支付點數", example = "2100", required = false)
  @Column(name = "POINT")
  protected Integer point;

  @ApiModelProperty(value = "院內碼", example = "P56008", required = false)
  @Column(name = "INH_CODE")
  protected String inhCode;

  @ApiModelProperty(value = "院內名稱", example = "藥品調劑費", required = false)
  @Column(name = "INH_NAME")
  protected String inhName;

  @ApiModelProperty(value = "自費金額", example = "600", required = false)
  @Column(name = "OWN_EXPENSE")
  protected Double ownExpense;

  @Column(name = "CODE_TYPE")
  @ApiModelProperty(value = "費用分類，如不分類、病房費、藥費…", example = "病房費", required = true)
  protected String codeType;

  @ApiModelProperty(value = "ATC分類代碼", example = "A01", required = false)
  @Column(name = "ATC")
  protected String atc;

  /**
   * 適用醫院層級，若有多組以空格區隔。0:基層院所,1:醫學中心,2:區域醫院,3:地區醫院
   */
  @Column(name = "HOSP_LEVEL", length = 12)
  @JsonIgnore
  protected String hospLevel;

  /**
   * 是否為二類特材(SECOND SPECIAL MATERIAL)，1:是，0:否
   */
  @Column(name = "SEC_SM")
  @JsonIgnore
  protected Integer secSm;

  @ApiModelProperty(value = "生效日", example = "A01", required = false)
  @Column(name = "START_DATE")
  //@JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  @JsonIgnore
  protected Date startDate;

  @ApiModelProperty(value = "終止日", example = "A01", required = false)
  @Column(name = "END_DATE")
  //@JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  @JsonIgnore
  protected Date endDate;

  /**
   * 存放在REDIS中HASH的FIELD值
   */
  @Column(name = "REDIS_ID")
  @JsonIgnore
  protected Integer redisId;
  
  /**
   * 存放在REDIS中HASH的FIELD值
   */
  @Column(name = "SAME_ATC")
  @JsonIgnore
  protected Integer sameAtc;

  /**
   * 更新時間
   */
  @Column(name = "UPDATE_AT", nullable = false)
  @JsonIgnore
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
   * 支付標準代碼
   */
  public String getCode() {
    return code;
  }

  /**
   * 支付標準代碼
   */
  public void setCode(String CODE) {
    code = CODE;
  }

  /**
   * 支付標準名稱
   */
  public String getName() {
    return name;
  }

  /**
   * 支付標準名稱
   */
  public void setName(String NAME) {
    name = NAME;
  }

  /**
   * 健保支付點數
   */
  public Integer getPoint() {
    return point;
  }

  /**
   * 健保支付點數
   */
  public void setPoint(Integer POINT) {
    point = POINT;
  }

  /**
   * 院內碼
   */
  public String getInhCode() {
    return inhCode;
  }

  /**
   * 院內碼
   */
  public void setInhCode(String INH_CODE) {
    inhCode = INH_CODE;
  }

  /**
   * 院內名稱
   */
  public String getInhName() {
    return inhName;
  }

  /**
   * 院內名稱
   */
  public void setInhName(String INH_NAME) {
    inhName = INH_NAME;
  }

  /**
   * 自費金額
   */
  public Double getOwnExpense() {
    return ownExpense;
  }

  /**
   * 自費金額
   */
  public void setOwnExpense(Double OWN_EXPENSE) {
    ownExpense = OWN_EXPENSE;
  }

  /**
   * 費用分類，如不分類、病房費、藥費…
   */
  public String getCodeType() {
    return codeType;
  }

  /**
   * 費用分類，如不分類、病房費、藥費…
   */
  public void setCodeType(String CODE_TYPE) {
    codeType = CODE_TYPE;
  }

  /**
   * ATC分類代碼
   */
  public String getAtc() {
    return atc;
  }

  /**
   * ATC分類代碼
   */
  public void setAtc(String ATC) {
    atc = ATC;
  }

  /**
   * 適用醫院層級，若有多組以空格區隔。0:基層院所,1:醫學中心,2:區域醫院,3:地區醫院
   */
  public String getHospLevel() {
    return hospLevel;
  }

  /**
   * 適用醫院層級，若有多組以空格區隔。0:基層院所,1:醫學中心,2:區域醫院,3:地區醫院
   */
  public void setHospLevel(String HOSP_LEVEL) {
    hospLevel = HOSP_LEVEL;
  }

  /**
   * 是否為二類特材(SECOND SPECIAL MATERIAL)，1:是，0:否
   */
  public Integer getSecSm() {
    return secSm;
  }

  /**
   * 是否為二類特材(SECOND SPECIAL MATERIAL)，1:是，0:否
   */
  public void setSecSm(Integer SEC_SM) {
    secSm = SEC_SM;
  }

  /**
   * 生效日
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * 生效日
   */
  public void setStartDate(Date START_DATE) {
    startDate = START_DATE;
  }

  /**
   * 失效日
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * 失效日
   */
  public void setEndDate(Date END_DATE) {
    endDate = END_DATE;
  }

  /**
   * 存放在REDIS中HASH的FIELD值
   */
  public Integer getRedisId() {
    return redisId;
  }

  /**
   * 存放在REDIS中HASH的FIELD值
   */
  public void setRedisId(Integer REDIS_ID) {
    redisId = REDIS_ID;
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
  
  public String getNameEn() {
    return nameEn;
  }

  public void setNameEn(String nameEn) {
    this.nameEn = nameEn;
  }
  
  public Integer getSameAtc() {
    return sameAtc;
  }

  public void setSameAtc(Integer sameAtc) {
    this.sameAtc = sameAtc;
  }

  public static PAY_CODE convertFromOrderCode(OrderCode oc) {
    PAY_CODE result = new PAY_CODE();
    result.setCode(oc.getCode().toUpperCase());
    result.setInhCode(result.getCode());
    result.setCodeType(oc.getDetail());
    result.setEndDate(oc.geteDate());
    result.setStartDate(oc.getsDate());
    result.setHospLevel(oc.getLevel());
    result.setName(oc.getDesc());
    result.setInhName(result.getName());
    result.setPoint(oc.getP());
    result.setUpdateAt(new Date());
    return result;
  }
  
  public OrderCode toOrderCode() {
    OrderCode result = new OrderCode();
    result.setCode(code.toLowerCase());
    result.setDetail(codeType);
    result.setsDate(startDate);
    result.seteDate(endDate);
    result.setLevel(hospLevel);
    result.setDesc(name);
    if (point != null) {
      result.setP(point);
    }
    result.setUpdateAt(updateAt);
    return result;
  }

}