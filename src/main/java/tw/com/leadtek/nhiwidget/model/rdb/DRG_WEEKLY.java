/**
 * Created on 2021/11/10 by GenerateSqlByClass().
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
import com.fasterxml.jackson.annotation.JsonProperty;

@Table(name = "DRG_WEEKLY")
@Entity
public class DRG_WEEKLY {

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
   * 西元年
   */
  @Column(name = "P_YEAR")
  @JsonIgnore
  private Integer pyear;

  /**
   * 週數
   */
  @Column(name = "P_WEEK")
  @JsonIgnore
  private Integer pweek;

  /**
   * 啟始日
   */
  @Column(name = "START_DATE")
  @JsonIgnore
  private Date startDate;

  /**
   * 結束日
   */
  @Column(name = "END_DATE")
  @JsonIgnore
  private Date endDate;

  /**
   * 科別代碼
   */
  @Column(name = "FUNC_TYPE", length = 2)
  @JsonIgnore
  private String funcType;

  /**
   * DRG件數
   */
  @Column(name = "DRG_QUANTITY")
  @JsonIgnore
  private Long drgQuantity;

  /**
   * 非DRG件數
   */
  @Column(name = "NONDRG_QUANTITY")
  @JsonIgnore
  private Long nondrgQuantity;

  /**
   * DRG點數
   */
  @Column(name = "DRG_POINT")
  @JsonIgnore
  private Long drgPoint;

  /**
   * 非DRG點數
   */
  @Column(name = "NONDRG_POINT")
  @JsonIgnore
  private Long nondrgPoint;

  /**
   * DRG A區件數
   */
  @Column(name = "SECTION_A")
  @JsonIgnore
  private Long sectionA;
  
  /**
   * DRG B1區件數
   */
  @Column(name = "SECTION_B1")
  @JsonIgnore
  private Long sectionB1;

  /**
   * DRG B2區件數
   */
  @Column(name = "SECTION_B2")
  @JsonIgnore
  private Long sectionB2;

  /**
   * DRG C區件數
   */
  @Column(name = "SECTION_C")
  @JsonIgnore
  private Long sectionC;

  /**
   * DRG A區點數
   */
  @Column(name = "POINT_A")
  private Long pointA;
  
  /**
   * DRG B1區點數
   */
  @Column(name = "POINT_B1")
  private Long pointB1;
  
  /**
   * DRG B2區點數
   */
  @Column(name = "POINT_B2")
  private Long pointB2;
  
  /**
   * DRG C區點數
   */
  @Column(name = "POINT_C")
  private Long pointC;
  
  /**
   * 更新時間
   */
  @Column(name = "UPDATE_AT")
  @JsonIgnore
  private Date updateAt;

  public DRG_WEEKLY() {
    this.drgPoint = 0L;
    this.drgQuantity = 0L;
    this.nondrgPoint = 0L;
    this.nondrgQuantity = 0L;
    this.sectionA = 0L;
    this.sectionB1 = 0L;
    this.sectionB2 = 0L;
    this.sectionC = 0L;
    this.pointA = 0L;
    this.pointB1 = 0L;
    this.pointB2 = 0L;
    this.pointC = 0L;
    this.updateAt = new Date();
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
   * 西元年
   */
  public Integer getPyear() {
    return pyear;
  }

  /**
   * 西元年
   */
  public void setPyear(Integer P_YEAR) {
    pyear = P_YEAR;
  }

  /**
   * 週數
   */
  public Integer getPweek() {
    return pweek;
  }

  /**
   * 週數
   */
  public void setPweek(Integer P_WEEK) {
    pweek = P_WEEK;
  }

  /**
   * 啟始日
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * 啟始日
   */
  public void setStartDate(Date START_DATE) {
    startDate = START_DATE;
  }

  /**
   * 結束日
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * 結束日
   */
  public void setEndDate(Date END_DATE) {
    endDate = END_DATE;
  }

  /**
   * 科別代碼
   */
  public String getFuncType() {
    return funcType;
  }

  /**
   * 科別代碼
   */
  public void setFuncType(String FUNC_TYPE) {
    funcType = FUNC_TYPE;
  }

  /**
   * DRG件數
   */
  public Long getDrgQuantity() {
    return drgQuantity;
  }

  /**
   * DRG件數
   */
  public void setDrgQuantity(Long DRG_QUANTITY) {
    drgQuantity = DRG_QUANTITY;
  }

  /**
   * 非DRG件數
   */
  public Long getNondrgQuantity() {
    return nondrgQuantity;
  }

  /**
   * 非DRG件數
   */
  public void setNondrgQuantity(Long NONDRG_QUANTITY) {
    nondrgQuantity = NONDRG_QUANTITY;
  }

  /**
   * DRG點數
   */
  public Long getDrgPoint() {
    return drgPoint;
  }

  /**
   * DRG點數
   */
  public void setDrgPoint(Long DRG_POINT) {
    drgPoint = DRG_POINT;
  }

  /**
   * 非DRG點數
   */
  public Long getNondrgPoint() {
    return nondrgPoint;
  }

  /**
   * 非DRG點數
   */
  public void setNondrgPoint(Long NONDRG_POINT) {
    nondrgPoint = NONDRG_POINT;
  }

  /**
   * DRG A區件數
   */
  public Long getSectionA() {
    return sectionA;
  }

  /**
   * DRG A區件數
   */
  public void setSectionA(Long SECTION_A) {
    sectionA = SECTION_A;
  }

  /**
   * DRG B1區件數
   */
  public Long getSectionB1() {
    return sectionB1;
  }

  /**
   * DRG B1區件數
   */
  public void setSectionB1(Long SECTION_B1) {
    sectionB1 = SECTION_B1;
  }

  /**
   * DRG B2區件數
   */
  public Long getSectionB2() {
    return sectionB2;
  }

  /**
   * DRG B2區件數
   */
  public void setSectionB2(Long SECTION_B2) {
    sectionB2 = SECTION_B2;
  }

  /**
   * DRG C區件數
   */
  public Long getSectionC() {
    return sectionC;
  }

  /**
   * DRG C區件數
   */
  public void setSectionC(Long SECTION_C) {
    sectionC = SECTION_C;
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

  public Long getPointA() {
    return pointA;
  }

  public void setPointA(Long pointA) {
    this.pointA = pointA;
  }

  public Long getPointB1() {
    return pointB1;
  }

  public void setPointB1(Long pointB1) {
    this.pointB1 = pointB1;
  }

  public Long getPointB2() {
    return pointB2;
  }

  public void setPointB2(Long pointB2) {
    this.pointB2 = pointB2;
  }

  public Long getPointC() {
    return pointC;
  }

  public void setPointC(Long pointC) {
    this.pointC = pointC;
  }

}