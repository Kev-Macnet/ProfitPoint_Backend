/**
 * Created on 2022/03/30.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.annotations.ApiModel;

@ApiModel("診斷碼搭配藥品衛材的出現次數")
@Table(name = "ICDCM_DRUG")
@Entity
@IdClass(ICDCM_DRUG_KEYS.class)
public class ICDCM_DRUG {
  /**
   * 診斷碼
   */
  @Id
  @Column(name = "ICDCM")
  private String icdcm;
  /**
   * 藥品、衛材代碼
   */
  @Id
  @Column(name = "DRUG")
  private String drug;
  /**
   * 資料格式 10: 門急診 20: 住院
   */
  @Id
  @Column(name = "DATA_FORMAT")
  private String dataFormat;

  /**
   * 是否為藥品 1: 藥品 2: 衛材
   */
  @Column(name = "IS_DRUG")
  private int isdurug;

  /**
   * 該診斷碼出現該藥品/衛材的總次數
   */
  @Column(name = "TOTAL")
  private int total;

  /**
   * 該藥品/衛材出現次數百分比，值為0~100
   */
  @Column(name = "PERCENT")
  private int percent;

  /**
   * 建立時間
   */
  @Column(name = "UPDATE_AT")
  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date updateAT;

  public int getIsdurug() {
    return isdurug;
  }

  public void setIsdurug(int isdurug) {
    this.isdurug = isdurug;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getPercent() {
    return percent;
  }

  public void setPercent(int percent) {
    this.percent = percent;
  }

  public Date getUpdateAT() {
    return updateAT;
  }

  public void setUpdateAT(Date updateAT) {
    this.updateAT = updateAT;
  }

  public String getIcdcm() {
    return icdcm;
  }

  public void setIcdcm(String icdcm) {
    this.icdcm = icdcm;
  }

  public String getDrug() {
    return drug;
  }

  public void setDrug(String drug) {
    this.drug = drug;
  }

  public String getDataFormat() {
    return dataFormat;
  }

  public void setDataFormat(String dataFormat) {
    this.dataFormat = dataFormat;
  }

}
