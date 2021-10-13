/**
 * Created on 2021/8/13.
 */
package tw.com.leadtek.nhiwidget.model.rdb;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("DRG代碼")
@Table(name = "DRG_CODE")
@Entity
public class DRG_CODE {

  @Id
  @ApiModelProperty(value = "序號", example = "1", required = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", nullable = false)
  protected Long id;
  
  @ApiModelProperty(value = "DRG代碼", example = "010301", required = true)
  @Column(name = "CODE", length = 6)
  private String code;
  
  @ApiModelProperty(value = "MDC代碼", example = "PRE", required = true)
  @Column(name = "MDC", length = 3)
  private String mdc;
  
  @ApiModelProperty(value = "MDC代碼下的流水號", example = "1", required = true)
  @Column(name = "SERIAL")
  private Integer serial;
  
  @ApiModelProperty(value = "權重RW", example = "19.074", required = true)
  @Column(name = "RW", length = 3)
  private Float rw;
  
  @ApiModelProperty(value = "幾何平均住院日", example = "1", required = true)
  @Column(name = "AVG_IN_DAY")
  private Integer avgInDay;
  
  @ApiModelProperty(value = "科別，M內科，P外科", example = "M", required = false)
  @Column(name = "DEP")
  private String dep;
  
  @ApiModelProperty(value = "下限(Lower Limit)臨界點", example = "833974", required = true)
  @Column(name = "LLIMIT")
  private Integer llimit;
  
  @ApiModelProperty(value = "上限(Upper Limit)臨界點", example = "979217", required = true)
  @Column(name = "ULIMIT")
  private Integer ulimit;
  
  @ApiModelProperty(value = "DRG代碼設定啟用日", example = "2019/07/01", required = true)
  @Column(name = "START_DATE")
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  private Date startDate;
  
  @ApiModelProperty(value = "DRG代碼設定結束日", example = "2019/12/31", required = true)
  @Column(name = "END_DATE")
  @JsonFormat(pattern = "yyyy/MM/dd", timezone = "GMT+8")
  private Date endDate;
  
  @ApiModelProperty(value = "個案數<20註記", example = "1:個案數<20實報實銷，0:否", required = false)
  @Column(name = "CASE20")
  private Integer case20 = 0;
  
  @ApiModelProperty(value = "是否導入，1:有，0:否", example = "1", required = false)
  @Column(name = "STARTED")
  private Integer started;
  
  @ApiModelProperty(value = "是否為論件計酬，1:是，0:否", example = "0", required = false)
  @Column(name = "PR")
  private Integer pr;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMdc() {
    return mdc;
  }

  public void setMdc(String mdc) {
    this.mdc = mdc;
  }

  public Integer getSerial() {
    return serial;
  }

  public void setSerial(Integer serial) {
    this.serial = serial;
  }

  public Float getRw() {
    return rw;
  }

  public void setRw(Float rw) {
    this.rw = rw;
  }

  public Integer getAvgInDay() {
    return avgInDay;
  }

  public void setAvgInDay(Integer avgInDay) {
    this.avgInDay = avgInDay;
  }

  public Integer getLlimit() {
    return llimit;
  }

  public void setLlimit(Integer llimit) {
    this.llimit = llimit;
  }

  public Integer getUlimit() {
    return ulimit;
  }

  public void setUlimit(Integer ulimit) {
    this.ulimit = ulimit;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public Integer getCase20() {
    return case20;
  }

  public void setCase20(Integer case20) {
    this.case20 = case20;
  }

  public String getDep() {
    return dep;
  }

  public void setDep(String dep) {
    this.dep = dep;
  }

  public Integer getStarted() {
    return started;
  }

  public void setStarted(Integer started) {
    this.started = started;
  }

  public Integer getPr() {
    return pr;
  }

  public void setPr(Integer pr) {
    this.pr = pr;
  }

}
