/**
 * Created on 2021/9/7.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CODE;

@ApiModel("DRG代碼設定")
public class DrgCodePayload implements Serializable{

  private static final long serialVersionUID = -1961485483648828204L;
  
  @ApiModelProperty(value = "id", example = "1", required = false)
  protected Long id;

  @ApiModelProperty(value = "DRG代碼", example = "00502   ", required = true)
  private String code;
  
  @ApiModelProperty(value = "MDC代碼", example = "1", required = true)
  private String mdc;
  
  @ApiModelProperty(value = "MDC代碼下的流水號", example = "10", required = true)
  private Integer serial;
  
  @ApiModelProperty(value = "權重RW", example = "2.829", required = true)
  private Float rw;
  
  @ApiModelProperty(value = "幾何平均住院日", example = "4", required = true)
  private Integer avgInDay;
  
  @ApiModelProperty(value = "科別，M內科，P外科", example = "pM", required = false)
  private String dep;
  
  @ApiModelProperty(value = "下限(Lower Limit)臨界點", example = "833974", required = true)
  private Integer llimit;
  
  @ApiModelProperty(value = "上限(Upper Limit)臨界點", example = "979217", required = true)
  private Integer ulimit;
  
  @ApiModelProperty(value = "DRG代碼設定啟用日，yyyy/MM/dd", example = "2019/07/01", required = true)
  private String startDay;
  
  @ApiModelProperty(value = "DRG代碼設定結束日，yyyy/MM/dd", example = "2019/12/31", required = true)
  private String endDay;
  
  @ApiModelProperty(value = "個案數<20註記", example = "true:個案數<20實報實銷，false:否", required = false)
  private Boolean case20;
  
  @ApiModelProperty(value = "是否導入，1:有，0:否", example = "1", required = false)
  private Boolean started;
  
  @ApiModelProperty(value = "是否為論件計酬，1:是，0:否", example = "0", required = false)
  private Boolean pr;
  
  public DrgCodePayload() {  
  }
  
  public static DrgCodePayload fromDB(DRG_CODE drg) {
    DrgCodePayload result = new DrgCodePayload();
    result.setId(drg.getId());
    result.setCode(drg.getCode());
    result.setMdc(drg.getMdc());
    result.setSerial(drg.getSerial());
    result.setRw(drg.getRw());
    result.setAvgInDay(drg.getAvgInDay());
    result.setDep(drg.getDep());
    result.setLlimit(drg.getLlimit());
    result.setUlimit(drg.getUlimit());
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    result.setStartDay(sdf.format(drg.getStartDate()));
    result.setEndDay(sdf.format(drg.getEndDate()));
    result.setCase20(drg.getCase20().intValue() == 1);
    result.setStarted(drg.getStarted().intValue() == 1);
    result.setPr(drg.getPr().intValue() == 1);
    return result;
  }
  
  public DRG_CODE toDB() {
    DRG_CODE result = new DRG_CODE();
    result.setId(id);
    result.setCode(code);
    result.setMdc(mdc);
    result.setSerial(serial);
    result.setRw(rw);
    result.setAvgInDay(avgInDay);
    result.setDep(dep);
    result.setLlimit(llimit);
    result.setUlimit(ulimit);
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    try {
      if (startDay != null) {
        result.setStartDate(sdf.parse(startDay));
      }
      if (endDay != null) {
        result.setEndDate(sdf.parse(endDay));
      }
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
    
    result.setCase20(case20 ? 1 : 0);
    result.setStarted(started ? 1 : 0);
    result.setPr(pr ? 1 : 0);
    return result;
  }

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

  public String getDep() {
    return dep;
  }

  public void setDep(String dep) {
    this.dep = dep;
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

  public String getStartDay() {
    return startDay;
  }

  public void setStartDay(String startDay) {
    this.startDay = startDay;
  }

  public String getEndDay() {
    return endDay;
  }

  public void setEndDay(String endDay) {
    this.endDay = endDay;
  }

  public Boolean getCase20() {
    return case20;
  }

  public void setCase20(Boolean case20) {
    this.case20 = case20;
  }

  public Boolean getStarted() {
    return started;
  }

  public void setStarted(Boolean started) {
    this.started = started;
  }

  public Boolean getPr() {
    return pr;
  }

  public void setPr(Boolean pr) {
    this.pr = pr;
  }
  
}
