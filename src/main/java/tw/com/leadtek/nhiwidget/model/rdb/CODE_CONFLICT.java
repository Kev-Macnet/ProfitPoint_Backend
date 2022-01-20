/**
 * Created on 2021/10/12 by GenerateSqlByClass().
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

@Table(name = "CODE_CONFLICT")
@Entity
public class CODE_CONFLICT {

  /**
   * 序號
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  private Long id;

  /**
   * 標準支付代碼
   */
  @Column(name = "CODE", length = 12)
  private String code;

  /**
   * 代碼中文說明
   */
  @Column(name = "DESC_CHI", length = 100)
  private String descChi;

  /**
   * 院內碼
   */
  @Column(name = "INH_CODE", length = 12)
  private String inhCode;

  /**
   * 院內碼說明
   */
  @Column(name = "INH_DESC", length = 100)
  private String inhDesc;

  /**
   * 健保項目數量
   */
  @Column(name = "QUANTITY_NH")
  private Integer quantityNh;

  /**
   * 自費院內碼
   */
  @Column(name = "OWN_EXP_CODE", length = 12)
  private String ownExpCode;

  /**
   * 自費項目說明
   */
  @Column(name = "OWN_EXP_DESC", length = 12)
  private String ownExpDesc;

  /**
   * 自費項目數量
   */
  @Column(name = "QUANTITY_OWN")
  private Integer quantityOwn;

  /**
   * 適用就醫方式，00:門急診及住院均適用，10:門急診，20:住院
   */
  @Column(name = "DATA_FORMAT", length = 2)
  private String dataFormat;

  /**
   * 是否啟用此組設定
   */
  @Column(name = "STATUS")
  private Integer status;

  /**
   * 並存設定啟用日
   */
  @Column(name = "START_DATE")
  private Date startDate;

  /**
   * 並存碼設定結束日
   */
  @Column(name = "END_DATE")
  @JsonIgnore
  private Date endDate;
  
  /**
   * CODE類別，1: 醫令/健保碼，2: ICD 診斷碼
   */
  @Column(name = "CODE_TYPE")
  @JsonIgnore
  private Integer codeType;

  /**
   * 更新日期
   */
  @Column(name = "UPDATE_AT")
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
   * 標準支付代碼
   */
  public String getCode() {
    return code;
  }

  /**
   * 標準支付代碼
   */
  public void setCode(String CODE) {
    code = CODE;
  }

  /**
   * 代碼中文說明
   */
  public String getDescChi() {
    return descChi;
  }

  /**
   * 代碼中文說明
   */
  public void setDescChi(String DESC_CHI) {
    descChi = DESC_CHI;
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
   * 院內碼說明
   */
  public String getInhDesc() {
    return inhDesc;
  }

  /**
   * 院內碼說明
   */
  public void setInhDesc(String INH_DESC) {
    inhDesc = INH_DESC;
  }

  /**
   * 健保項目數量
   */
  public Integer getQuantityNh() {
    return quantityNh;
  }

  /**
   * 健保項目數量
   */
  public void setQuantityNh(Integer QUANTITY_NH) {
    quantityNh = QUANTITY_NH;
  }

  /**
   * 自費院內碼
   */
  public String getOwnExpCode() {
    return ownExpCode;
  }

  /**
   * 自費院內碼
   */
  public void setOwnExpCode(String OWN_EXP_CODE) {
    ownExpCode = OWN_EXP_CODE;
  }

  /**
   * 自費項目說明
   */
  public String getOwnExpDesc() {
    return ownExpDesc;
  }

  /**
   * 自費項目說明
   */
  public void setOwnExpDesc(String OWN_EXP_DESC) {
    ownExpDesc = OWN_EXP_DESC;
  }

  /**
   * 自費項目數量
   */
  public Integer getQuantityOwn() {
    return quantityOwn;
  }

  /**
   * 自費項目數量
   */
  public void setQuantityOwn(Integer QUANTITY_OWN) {
    quantityOwn = QUANTITY_OWN;
  }

  /**
   * 適用就醫方式，00:門急診及住院均適用，10:門急診，20:住院
   */
  public String getDataFormat() {
    return dataFormat;
  }

  /**
   * 適用就醫方式，00:門急診及住院均適用，10:門急診，20:住院
   */
  public void setDataFormat(String DATA_FORMAT) {
    dataFormat = DATA_FORMAT;
  }

  /**
   * 是否啟用此組設定
   */
  public Integer getStatus() {
    return status;
  }

  /**
   * 是否啟用此組設定
   */
  public void setStatus(Integer STATUS) {
    status = STATUS;
  }

  /**
   * 並存設定啟用日
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * 並存設定啟用日
   */
  public void setStartDate(Date START_DATE) {
    startDate = START_DATE;
  }

  /**
   * 並存碼設定結束日
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * 並存碼設定結束日
   */
  public void setEndDate(Date END_DATE) {
    endDate = END_DATE;
  }

  /**
   * 更新日期
   */
  public Date getUpdateAt() {
    return updateAt;
  }

  /**
   * 更新日期
   */
  public void setUpdateAt(Date UPDATE_AT) {
    updateAt = UPDATE_AT;
  }

  public Integer getCodeType() {
    return codeType;
  }

  public void setCodeType(Integer codeType) {
    this.codeType = codeType;
  }
  
}