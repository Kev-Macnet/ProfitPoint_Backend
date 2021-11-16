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

@Table(name = "DRG_MONTHLY")
@Entity
public class DRG_MONTHLY {

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
   * 年月(西元年)，如 201211
   */
  @Column(name = "YM")
  @JsonIgnore
  private Integer ym;

  /**
   * 科別代碼
   */
  @Column(name = "FUNC_TYPE", length = 2)
  @JsonIgnore
  private String funcType;

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
   * DRG A區申報點數
   */
  @Column(name = "SECTION_A_APPL")
  @JsonIgnore
  private Long sectionAAppl;

  /**
   * DRG B1區申報點數
   */
  @Column(name = "SECTION_B1_APPL")
  @JsonIgnore
  private Long sectionB1Appl;

  /**
   * DRG B2區申報點數
   */
  @Column(name = "SECTION_B2_APPL")
  @JsonIgnore
  private Long sectionB2Appl;

  /**
   * DRG C區申報點數
   */
  @Column(name = "SECTION_C_APPL")
  @JsonIgnore
  private Long sectionCAppl;

  /**
   * DRG A區實際點數
   */
  @Column(name = "SECTION_A_ACTUAL")
  @JsonIgnore
  private Long sectionAActual;

  /**
   * DRG B1區實際點數
   */
  @Column(name = "SECTION_B1_ACTUAL")
  @JsonIgnore
  private Long sectionB1Actual;

  /**
   * DRG B2區實際點數
   */
  @Column(name = "SECTION_B2_ACTUAL")
  @JsonIgnore
  private Long sectionB2Actual;

  /**
   * DRG C區實際點數
   */
  @Column(name = "SECTION_C_ACTUAL")
  @JsonIgnore
  private Long sectionCActual;

  /**
   * 更新時間
   */
  @Column(name = "UPDATE_AT")
  @JsonIgnore
  private Date updateAt;
  
  public DRG_MONTHLY() {
    sectionA = 0L;
    sectionAAppl = 0L;
    sectionAActual = 0L;
    
    sectionB1 = 0L;
    sectionB1Appl = 0L;
    sectionB1Actual = 0L;
    
    sectionB2 = 0L;
    sectionB2Appl = 0L;
    sectionB2Actual = 0L;
    
    sectionC = 0L;
    sectionCAppl = 0L;
    sectionCActual = 0L;
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
   * 年月(西元年)，如 201211
   */
  public Integer getYm() {
    return ym;
  }

  /**
   * 年月(西元年)，如 201211
   */
  public void setYm(Integer YM) {
    ym = YM;
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
   * DRG A區申報點數
   */
  public Long getSectionAAppl() {
    return sectionAAppl;
  }

  /**
   * DRG A區申報點數
   */
  public void setSectionAAppl(Long SECTION_A_APPL) {
    sectionAAppl = SECTION_A_APPL;
  }

  /**
   * DRG B1區申報點數
   */
  public Long getSectionB1Appl() {
    return sectionB1Appl;
  }

  /**
   * DRG B1區申報點數
   */
  public void setSectionB1Appl(Long SECTION_B1_APPL) {
    sectionB1Appl = SECTION_B1_APPL;
  }

  /**
   * DRG B2區申報點數
   */
  public Long getSectionB2Appl() {
    return sectionB2Appl;
  }

  /**
   * DRG B2區申報點數
   */
  public void setSectionB2Appl(Long SECTION_B2_APPL) {
    sectionB2Appl = SECTION_B2_APPL;
  }

  /**
   * DRG C區申報點數
   */
  public Long getSectionCAppl() {
    return sectionCAppl;
  }

  /**
   * DRG C區申報點數
   */
  public void setSectionCAppl(Long SECTION_C_APPL) {
    sectionCAppl = SECTION_C_APPL;
  }

  /**
   * DRG A區實際點數
   */
  public Long getSectionAActual() {
    return sectionAActual;
  }

  /**
   * DRG A區實際點數
   */
  public void setSectionAActual(Long SECTION_A_ACTUAL) {
    sectionAActual = SECTION_A_ACTUAL;
  }

  /**
   * DRG B1區實際點數
   */
  public Long getSectionB1Actual() {
    return sectionB1Actual;
  }

  /**
   * DRG B1區實際點數
   */
  public void setSectionB1Actual(Long SECTION_B1_ACTUAL) {
    sectionB1Actual = SECTION_B1_ACTUAL;
  }

  /**
   * DRG B2區實際點數
   */
  public Long getSectionB2Actual() {
    return sectionB2Actual;
  }

  /**
   * DRG B2區實際點數
   */
  public void setSectionB2Actual(Long SECTION_B2_ACTUAL) {
    sectionB2Actual = SECTION_B2_ACTUAL;
  }

  /**
   * DRG C區實際點數
   */
  public Long getSectionCActual() {
    return sectionCActual;
  }

  /**
   * DRG C區實際點數
   */
  public void setSectionCActual(Long SECTION_C_ACTUAL) {
    sectionCActual = SECTION_C_ACTUAL;
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