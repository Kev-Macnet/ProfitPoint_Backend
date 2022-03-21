/**
 * Created on 2022/3/15.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("健保申報總額達成趨勢資料")
public class AchievementWeekly extends BaseResponse implements Serializable {

  private static final long serialVersionUID = -4247510669297020761L;

  @ApiModelProperty(value = "當月累計總額點數", required = true)
  private Long monthTotal;
  
  @ApiModelProperty(value = "當月結算分配額度點數", required = true)
  private Long monthAssigned;
  
  @ApiModelProperty(value = "總額達成率，如98.58%", required = true)
  private String achievementRate;
  
  @ApiModelProperty(value = "門急診/住院總額點數趨勢", required = true)
  private NameValueList all;
  
  @ApiModelProperty(value = "門急診點數趨勢", required = true)
  private NameValueList opAll;
  
  @ApiModelProperty(value = "門診點數趨勢", required = true)
  private NameValueList op;
  
  @ApiModelProperty(value = "急診點數趨勢", required = true)
  private NameValueList em;
  
  @ApiModelProperty(value = "住院點數趨勢", required = true)
  private NameValueList ip;
  
  @ApiModelProperty(value = "門急診/住院分配額度", required = true)
  private NameValueList assignedAll;
  
  @ApiModelProperty(value = "門急診分配額度", required = true)
  private NameValueList assignedOpAll;
  
  @ApiModelProperty(value = "住院分配額度", required = true)
  private NameValueList assignedIp;
  
  @ApiModelProperty(value = "門急診/住院實際點數及達成率", required = true)
  private NameValueList2 actualAll;
  
  @ApiModelProperty(value = "門急診實際點數及達成率", required = true)
  private NameValueList2 actualOpAll;
  
  @ApiModelProperty(value = "住院實際點數及達成率", required = true)
  private NameValueList2 actualIp;

  public AchievementWeekly() {
    all = new NameValueList();
    ip = new NameValueList();
    op = new NameValueList();
    opAll = new NameValueList();
    em = new NameValueList();
    
    assignedAll = new NameValueList();
    assignedIp = new NameValueList();
    assignedOpAll = new NameValueList();
    actualAll = new NameValueList2();
    actualOpAll = new NameValueList2();
    actualIp = new NameValueList2();
  }
  
  public Long getMonthTotal() {
    return monthTotal;
  }

  public void setMonthTotal(Long monthTotal) {
    this.monthTotal = monthTotal;
  }

  public Long getMonthAssigned() {
    return monthAssigned;
  }

  public void setMonthAssigned(Long monthAssigned) {
    this.monthAssigned = monthAssigned;
  }

  public String getAchievementRate() {
    return achievementRate;
  }

  public void setAchievementRate(String achievementRate) {
    this.achievementRate = achievementRate;
  }

  public NameValueList getAll() {
    return all;
  }

  public void setAll(NameValueList all) {
    this.all = all;
  }

  public NameValueList getOp() {
    return op;
  }

  public void setOp(NameValueList op) {
    this.op = op;
  }

  public NameValueList getEm() {
    return em;
  }

  public void setEm(NameValueList em) {
    this.em = em;
  }

  public NameValueList getIp() {
    return ip;
  }

  public void setIp(NameValueList ip) {
    this.ip = ip;
  }

  public NameValueList getAssignedAll() {
    return assignedAll;
  }

  public void setAssignedAll(NameValueList assignedAll) {
    this.assignedAll = assignedAll;
  }

  public NameValueList getAssignedIp() {
    return assignedIp;
  }

  public void setAssignedIp(NameValueList assignedIp) {
    this.assignedIp = assignedIp;
  }

  public NameValueList2 getActualAll() {
    return actualAll;
  }

  public void setActualAll(NameValueList2 actualAll) {
    this.actualAll = actualAll;
  }

  public NameValueList2 getActualIp() {
    return actualIp;
  }

  public void setActualIp(NameValueList2 actualIp) {
    this.actualIp = actualIp;
  }

  public NameValueList getOpAll() {
    return opAll;
  }

  public void setOpAll(NameValueList opAll) {
    this.opAll = opAll;
  }

  public NameValueList getAssignedOpAll() {
    return assignedOpAll;
  }

  public void setAssignedOpAll(NameValueList assignedOpAll) {
    this.assignedOpAll = assignedOpAll;
  }

  public NameValueList2 getActualOpAll() {
    return actualOpAll;
  }

  public void setActualOpAll(NameValueList2 actualOpAll) {
    this.actualOpAll = actualOpAll;
  }
  
}
