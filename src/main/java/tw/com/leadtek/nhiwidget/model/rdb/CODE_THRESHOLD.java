/**
 * Created on 2021/10/06 by GenerateSqlByClass().
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

@Table(name = "CODE_THRESHOLD")
@Entity
public class CODE_THRESHOLD {

  /**
   * 序號
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ID", nullable = false)
  @JsonIgnore
  private Long id;

  /**
   * 代碼類別，1:罕見ICD代碼，2:支付代碼應用比例偏高，3:特別用量藥材、衛品
   */
  @Column(name = "CODE_TYPE")
  @JsonIgnore
  private Integer codeType;

  /**
   * ICD代碼
   */
  @Column(name = "CODE", length = 12)
  @JsonIgnore
  private String code;

  /**
   * ICD代碼中文說明
   */
  @Column(name = "DESC_CHI", length = 100)
  @JsonIgnore
  private String descChi;

  /**
   * 院內碼
   */
  @Column(name = "INH_CODE", length = 12)
  @JsonIgnore
  private String inhCode;

  /**
   * 院內碼說明
   */
  @Column(name = "INH_DESC", length = 100)
  @JsonIgnore
  private String inhDesc;

  /**
   * 適用就醫方式，00:門急診及住院均適用，10:門急診，20:住院
   */
  @Column(name = "DATA_FORMAT", length = 2)
  @JsonIgnore
  private String dataFormat;

  /**
   * 是否啟用門診單一就診記錄申報數超過幾次跳提示。0:不啟用，1:啟用
   */
  @Column(name = "OP_TIMES_STATUS")
  @JsonIgnore
  private Integer opTimesStatus;

  /**
   * 門診單一就診記錄申報數超過幾次跳提示
   */
  @Column(name = "OP_TIMES")
  @JsonIgnore
  private Integer opTimes;

  /**
   * 是否啟用門診單一病患幾天內記錄申報數超過幾次跳提示。0:不啟用，1:啟用
   */
  @Column(name = "OP_TIMES_D_STATUS")
  @JsonIgnore
  private Integer opTimesDStatus;

  /**
   * 門診單一病患幾天內記錄申報數超過跳提示
   */
  @Column(name = "OP_TIMES_DAY")
  @JsonIgnore
  private Integer opTimesDay;

  /**
   * 門診單一病患申報數超過次數跳提示
   */
  @Column(name = "OP_TIMES_D")
  @JsonIgnore
  private Integer opTimesD;

  /**
   * 是否啟用門診月份應用超過幾次，0:不啟用，1:啟用
   */
  @Column(name = "OP_TIMES_M_STATUS")
  @JsonIgnore
  private Integer opTimesMStatus;

  /**
   * 門診月份應用超過幾次
   */
  @Column(name = "OP_TIMES_M")
  @JsonIgnore
  private Integer opTimesM;

  /**
   * 是否啟用門診月份應用超過前6個月平均值幾次，0:不啟用，1:啟用
   */
  @Column(name = "OP_TIMES_6M_STATUS")
  @JsonIgnore
  private Integer opTimes6mStatus;

  /**
   * 門診月份應用超過前6個月平均值幾次
   */
  @Column(name = "OP_TIMES_6M")
  @JsonIgnore
  private Integer opTimes6m;

  /**
   * 是否啟用住院單一就診記錄申報數超過幾次跳提示。0:不啟用，1:啟用
   */
  @Column(name = "IP_TIMES_STATUS")
  @JsonIgnore
  private Integer ipTimesStatus;

  /**
   * 住院單一就診記錄申報數超過幾次跳提示
   */
  @Column(name = "IP_TIMES")
  @JsonIgnore
  private Integer ipTimes;

  /**
   * 是否啟用住院單一病患幾天內記錄申報數超過幾次跳提示。0:不啟用，1:啟用
   */
  @Column(name = "IP_TIMES_D_STATUS")
  @JsonIgnore
  private Integer ipTimesDStatus;

  /**
   * 住院單一病患幾天內記錄申報數超過跳提示
   */
  @Column(name = "IP_TIMES_DAY")
  @JsonIgnore
  private Integer ipTimesDay;

  /**
   * 住院單一病患申報數超過次數跳提示
   */
  @Column(name = "IP_TIMES_D")
  @JsonIgnore
  private Integer ipTimesD;

  /**
   * 是否啟用住院月份應用超過幾次，0:不啟用，1:啟用
   */
  @Column(name = "IP_TIMES_M_STATUS")
  @JsonIgnore
  private Integer ipTimesMStatus;

  /**
   * 住院月份應用超過幾次
   */
  @Column(name = "IP_TIMES_M")
  @JsonIgnore
  private Integer ipTimesM;

  /**
   * 是否啟用住院月份應用超過前6個月平均值幾次，0:不啟用，1:啟用
   */
  @Column(name = "IP_TIMES_6M_STATUS")
  @JsonIgnore
  private Integer ipTimes6mStatus;

  /**
   * 住院月份應用超過前6個月平均值幾次
   */
  @Column(name = "IP_TIMES_6M")
  @JsonIgnore
  private Integer ipTimes6m;

  /**
   * 是否啟用此組設定
   */
  @Column(name = "STATUS")
  @JsonIgnore
  private Integer status;

  /**
   * 罕見ICD代碼設定啟用日
   */
  @Column(name = "START_DATE")
  @JsonIgnore
  private Date startDate;

  /**
   * 罕見ICD代碼設定結束日
   */
  @Column(name = "END_DATE")
  @JsonIgnore
  private Date endDate;

  /**
   * 更新日期
   */
  @Column(name = "UPDATE_AT")
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
   * 代碼類別，1:罕見ICD代碼，2:支付代碼應用比例偏高
   */
  public Integer getCodeType() {
    return codeType;
  }

  /**
   * 代碼類別，1:罕見ICD代碼，2:支付代碼應用比例偏高，3:特別用量藥材、衛品
   */
  public void setCodeType(Integer CODE_TYPE) {
    codeType = CODE_TYPE;
  }

  /**
   * ICD代碼
   */
  public String getCode() {
    return code;
  }

  /**
   * ICD代碼
   */
  public void setCode(String CODE) {
    code = CODE;
  }

  /**
   * ICD代碼中文說明
   */
  public String getDescChi() {
    return descChi;
  }

  /**
   * ICD代碼中文說明
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
   * 是否啟用門診單一就診記錄申報數超過幾次跳提示。0:不啟用，1:啟用
   */
  public Integer getOpTimesStatus() {
    return opTimesStatus;
  }

  /**
   * 是否啟用門診單一就診記錄申報數超過幾次跳提示。0:不啟用，1:啟用
   */
  public void setOpTimesStatus(Integer OP_TIMES_STATUS) {
    opTimesStatus = OP_TIMES_STATUS;
  }

  /**
   * 門診單一就診記錄申報數超過幾次跳提示
   */
  public Integer getOpTimes() {
    return opTimes;
  }

  /**
   * 門診單一就診記錄申報數超過幾次跳提示
   */
  public void setOpTimes(Integer OP_TIMES) {
    opTimes = OP_TIMES;
  }

  /**
   * 是否啟用門診單一病患幾天內記錄申報數超過幾次跳提示。0:不啟用，1:啟用
   */
  public Integer getOpTimesDStatus() {
    return opTimesDStatus;
  }

  /**
   * 是否啟用門診單一病患幾天內記錄申報數超過幾次跳提示。0:不啟用，1:啟用
   */
  public void setOpTimesDStatus(Integer OP_TIMES_D_STATUS) {
    opTimesDStatus = OP_TIMES_D_STATUS;
  }

  /**
   * 門診單一病患幾天內記錄申報數超過跳提示
   */
  public Integer getOpTimesDay() {
    return opTimesDay;
  }

  /**
   * 門診單一病患幾天內記錄申報數超過跳提示
   */
  public void setOpTimesDay(Integer OP_TIMES_DAY) {
    opTimesDay = OP_TIMES_DAY;
  }

  /**
   * 門診單一病患申報數超過次數跳提示
   */
  public Integer getOpTimesD() {
    return opTimesD;
  }

  /**
   * 門診單一病患申報數超過次數跳提示
   */
  public void setOpTimesD(Integer OP_TIMES_D) {
    opTimesD = OP_TIMES_D;
  }

  /**
   * 是否啟用門診月份應用超過幾次，0:不啟用，1:啟用
   */
  public Integer getOpTimesMStatus() {
    return opTimesMStatus;
  }

  /**
   * 是否啟用門診月份應用超過幾次，0:不啟用，1:啟用
   */
  public void setOpTimesMStatus(Integer OP_TIMES_M_STATUS) {
    opTimesMStatus = OP_TIMES_M_STATUS;
  }

  /**
   * 門診月份應用超過幾次
   */
  public Integer getOpTimesM() {
    return opTimesM;
  }

  /**
   * 門診月份應用超過幾次
   */
  public void setOpTimesM(Integer OP_TIMES_M) {
    opTimesM = OP_TIMES_M;
  }

  /**
   * 是否啟用門診月份應用超過前6個月平均值幾次，0:不啟用，1:啟用
   */
  public Integer getOpTimes6mStatus() {
    return opTimes6mStatus;
  }

  /**
   * 是否啟用門診月份應用超過前6個月平均值幾次，0:不啟用，1:啟用
   */
  public void setOpTimes6mStatus(Integer OP_TIMES_6M_STATUS) {
    opTimes6mStatus = OP_TIMES_6M_STATUS;
  }

  /**
   * 門診月份應用超過前6個月平均值幾次
   */
  public Integer getOpTimes6m() {
    return opTimes6m;
  }

  /**
   * 門診月份應用超過前6個月平均值幾次
   */
  public void setOpTimes6m(Integer OP_TIMES_6M) {
    opTimes6m = OP_TIMES_6M;
  }

  /**
   * 是否啟用住院單一就診記錄申報數超過幾次跳提示。0:不啟用，1:啟用
   */
  public Integer getIpTimesStatus() {
    return ipTimesStatus;
  }

  /**
   * 是否啟用住院單一就診記錄申報數超過幾次跳提示。0:不啟用，1:啟用
   */
  public void setIpTimesStatus(Integer IP_TIMES_STATUS) {
    ipTimesStatus = IP_TIMES_STATUS;
  }

  /**
   * 住院單一就診記錄申報數超過幾次跳提示
   */
  public Integer getIpTimes() {
    return ipTimes;
  }

  /**
   * 住院單一就診記錄申報數超過幾次跳提示
   */
  public void setIpTimes(Integer IP_TIMES) {
    ipTimes = IP_TIMES;
  }

  /**
   * 是否啟用住院單一病患幾天內記錄申報數超過幾次跳提示。0:不啟用，1:啟用
   */
  public Integer getIpTimesDStatus() {
    return ipTimesDStatus;
  }

  /**
   * 是否啟用住院單一病患幾天內記錄申報數超過幾次跳提示。0:不啟用，1:啟用
   */
  public void setIpTimesDStatus(Integer IP_TIMES_D_STATUS) {
    ipTimesDStatus = IP_TIMES_D_STATUS;
  }

  /**
   * 住院單一病患幾天內記錄申報數超過跳提示
   */
  public Integer getIpTimesDay() {
    return ipTimesDay;
  }

  /**
   * 住院單一病患幾天內記錄申報數超過跳提示
   */
  public void setIpTimesDay(Integer IP_TIMES_DAY) {
    ipTimesDay = IP_TIMES_DAY;
  }

  /**
   * 住院單一病患申報數超過次數跳提示
   */
  public Integer getIpTimesD() {
    return ipTimesD;
  }

  /**
   * 住院單一病患申報數超過次數跳提示
   */
  public void setIpTimesD(Integer IP_TIMES_D) {
    ipTimesD = IP_TIMES_D;
  }

  /**
   * 是否啟用住院月份應用超過幾次，0:不啟用，1:啟用
   */
  public Integer getIpTimesMStatus() {
    return ipTimesMStatus;
  }

  /**
   * 是否啟用住院月份應用超過幾次，0:不啟用，1:啟用
   */
  public void setIpTimesMStatus(Integer IP_TIMES_M_STATUS) {
    ipTimesMStatus = IP_TIMES_M_STATUS;
  }

  /**
   * 住院月份應用超過幾次
   */
  public Integer getIpTimesM() {
    return ipTimesM;
  }

  /**
   * 住院月份應用超過幾次
   */
  public void setIpTimesM(Integer IP_TIMES_M) {
    ipTimesM = IP_TIMES_M;
  }

  /**
   * 是否啟用住院月份應用超過前6個月平均值幾次，0:不啟用，1:啟用
   */
  public Integer getIpTimes6mStatus() {
    return ipTimes6mStatus;
  }

  /**
   * 是否啟用住院月份應用超過前6個月平均值幾次，0:不啟用，1:啟用
   */
  public void setIpTimes6mStatus(Integer IP_TIMES_6M_STATUS) {
    ipTimes6mStatus = IP_TIMES_6M_STATUS;
  }

  /**
   * 住院月份應用超過前6個月平均值幾次
   */
  public Integer getIpTimes6m() {
    return ipTimes6m;
  }

  /**
   * 住院月份應用超過前6個月平均值幾次
   */
  public void setIpTimes6m(Integer IP_TIMES_6M) {
    ipTimes6m = IP_TIMES_6M;
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
   * 罕見ICD代碼設定啟用日
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * 罕見ICD代碼設定啟用日
   */
  public void setStartDate(Date START_DATE) {
    startDate = START_DATE;
  }

  /**
   * 罕見ICD代碼設定結束日
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * 罕見ICD代碼設定結束日
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

}