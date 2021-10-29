/**
 * Created on 2021/10/28 by GenerateSqlByClass().
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

@Table(name = "ASSIGNED_POINT")
@Entity
public class ASSIGNED_POINT {

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
   * 西醫總點數
   */
  @Column(name = "WMP")
  @JsonIgnore
  private Long wmp;

  /**
   * 牙醫總點數
   */
  @Column(name = "DP")
  @JsonIgnore
  private Long dp;

  /**
   * 西醫門急診分配點數
   */
  @Column(name = "WM_OP_POINTS")
  @JsonIgnore
  private Long wmOpPoints;

  /**
   * 西醫住院分配點數
   */
  @Column(name = "WM_IP_POINTS")
  @JsonIgnore
  private Long wmIpPoints;

  /**
   * 西醫藥品分配點數
   */
  @Column(name = "WM_DRUG_POINTS")
  @JsonIgnore
  private Long wmDrugPoints;

  /**
   * 透析分配點數
   */
  @Column(name = "HEMODIALYSIS_POINTS")
  @JsonIgnore
  private Long hemodialysisPoints;

  /**
   * 其他專款分配點數
   */
  @Column(name = "FUND_POINTS")
  @JsonIgnore
  private Long fundPoints;

  /**
   * 牙醫門診分配點數
   */
  @Column(name = "DENTIST_OP_POINTS")
  @JsonIgnore
  private Long dentistOpPoints;

  /**
   * 牙醫藥品分配點數
   */
  @Column(name = "DENTIST_DRUG_POINTS")
  @JsonIgnore
  private Long dentistDrugPoints;

  /**
   * 牙醫專款分配點數
   */
  @Column(name = "DENTIST_FUND_POINTS")
  @JsonIgnore
  private Long dentistFundPoints;

  /**
   * 設定啟用日
   */
  @Column(name = "START_DATE")
  @JsonIgnore
  private Date startDate;

  /**
   * 設定結束日
   */
  @Column(name = "END_DATE")
  @JsonIgnore
  private Date endDate;

  /**
   * 更新時間
   */
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
   * 西醫總點數
   */
  public Long getWmp() {
    return wmp;
  }

  /**
   * 西醫總點數
   */
  public void setWmp(Long WMP) {
    wmp = WMP;
  }

  /**
   * 牙醫總點數
   */
  public Long getDp() {
    return dp;
  }

  /**
   * 牙醫總點數
   */
  public void setDp(Long DP) {
    dp = DP;
  }

  /**
   * 西醫門急診分配點數
   */
  public Long getWmOpPoints() {
    return wmOpPoints;
  }

  /**
   * 西醫門急診分配點數
   */
  public void setWmOpPoints(Long WM_OP_POINTS) {
    wmOpPoints = WM_OP_POINTS;
  }

  /**
   * 西醫住院分配點數
   */
  public Long getWmIpPoints() {
    return wmIpPoints;
  }

  /**
   * 西醫住院分配點數
   */
  public void setWmIpPoints(Long WM_IP_POINTS) {
    wmIpPoints = WM_IP_POINTS;
  }

  /**
   * 西醫藥品分配點數
   */
  public Long getWmDrugPoints() {
    return wmDrugPoints;
  }

  /**
   * 西醫藥品分配點數
   */
  public void setWmDrugPoints(Long WM_DRUG_POINTS) {
    wmDrugPoints = WM_DRUG_POINTS;
  }

  /**
   * 透析分配點數
   */
  public Long getHemodialysisPoints() {
    return hemodialysisPoints;
  }

  /**
   * 透析分配點數
   */
  public void setHemodialysisPoints(Long HEMODIALYSIS_POINTS) {
    hemodialysisPoints = HEMODIALYSIS_POINTS;
  }

  /**
   * 其他專款分配點數
   */
  public Long getFundPoints() {
    return fundPoints;
  }

  /**
   * 其他專款分配點數
   */
  public void setFundPoints(Long FUND_POINTS) {
    fundPoints = FUND_POINTS;
  }

  /**
   * 牙醫門診分配點數
   */
  public Long getDentistOpPoints() {
    return dentistOpPoints;
  }

  /**
   * 牙醫門診分配點數
   */
  public void setDentistOpPoints(Long DENTIST_OP_POINTS) {
    dentistOpPoints = DENTIST_OP_POINTS;
  }

  /**
   * 牙醫藥品分配點數
   */
  public Long getDentistDrugPoints() {
    return dentistDrugPoints;
  }

  /**
   * 牙醫藥品分配點數
   */
  public void setDentistDrugPoints(Long DENTIST_DRUG_POINTS) {
    dentistDrugPoints = DENTIST_DRUG_POINTS;
  }

  /**
   * 牙醫專款分配點數
   */
  public Long getDentistFundPoints() {
    return dentistFundPoints;
  }

  /**
   * 牙醫專款分配點數
   */
  public void setDentistFundPoints(Long DENTIST_FUND_POINTS) {
    dentistFundPoints = DENTIST_FUND_POINTS;
  }

  /**
   * 設定啟用日
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * 設定啟用日
   */
  public void setStartDate(Date START_DATE) {
    startDate = START_DATE;
  }

  /**
   * 設定結束日
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * 設定結束日
   */
  public void setEndDate(Date END_DATE) {
    endDate = END_DATE;
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