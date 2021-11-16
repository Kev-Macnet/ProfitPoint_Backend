/**
 * Created on 2021/11/12.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_MONTHLY;
import tw.com.leadtek.nhiwidget.service.ReportService;

@ApiModel("DRG分區分配比例(月報)")
public class DRGMonthlySectionPayload extends DRGReportPayload implements Serializable {

  private static final long serialVersionUID = 3707799700182911466L;

  @ApiModelProperty(value = "A區件數", required = true)
  private Long quantityA;

  @ApiModelProperty(value = "B1區件數", required = true)
  private Long quantityB1;

  @ApiModelProperty(value = "B2區件數", required = true)
  private Long quantityB2;

  @ApiModelProperty(value = "C區件數", required = true)
  private Long quantityC;

  @ApiModelProperty(value = "A區實際點數", required = true)
  private Long actualA;

  @ApiModelProperty(value = "B1區實際點數", required = true)
  private Long actualB1;

  @ApiModelProperty(value = "B2區實際點數", required = true)
  private Long actualB2;

  @ApiModelProperty(value = "C區實際點數", required = true)
  private Long actualC;

  @ApiModelProperty(value = "A區申報總點數", required = true)
  private Long applA;

  @ApiModelProperty(value = "B1區申報總點數", required = true)
  private Long applB1;

  @ApiModelProperty(value = "B2區申報總點數", required = true)
  private Long applB2;

  @ApiModelProperty(value = "C區申報總點數", required = true)
  private Long applC;

  @ApiModelProperty(value = "A區支付差額點數", required = true)
  private Long diffA;

  @ApiModelProperty(value = "B1區支付差額點數", required = true)
  private Long diffB1;

  @ApiModelProperty(value = "B2區支付差額點數", required = true)
  private Long diffB2;

  @ApiModelProperty(value = "C區支付差額點數", required = true)
  private Long diffC;

  @ApiModelProperty(value = "A區各科件數及點數", required = true)
  private List<NameCodePointQuantity> sectionA;

  @ApiModelProperty(value = "B1區各科件數及點數", required = true)
  private List<NameCodePointQuantity> sectionB1;

  @ApiModelProperty(value = "B2區各科件數及點數", required = true)
  private List<NameCodePointQuantity> sectionB2;

  @ApiModelProperty(value = "C區各科件數及點數", required = true)
  private List<NameCodePointQuantity> sectionC;

  @ApiModelProperty(value = "B1區各科差額點數")
  private List<NameCodePoint> diffB1FuncType;

  @ApiModelProperty(value = "B2區各科差額點數")
  private List<NameCodePoint> diffB2FuncType;

  @ApiModelProperty(value = "C區各科差額點數")
  private List<NameCodePoint> diffCFuncType;

  @ApiModelProperty(value = "A區各科案件每周趨勢圖", required = true)
  protected Map<String, NameValueList> weeklyAMap;

  @ApiModelProperty(value = "B1區各科案件每周趨勢圖", required = true)
  protected Map<String, NameValueList> weeklyB1Map;

  @ApiModelProperty(value = "A區各科案件每周趨勢圖", required = true)
  protected Map<String, NameValueList> weeklyB2Map;

  @ApiModelProperty(value = "A區各科案件每周趨勢圖", required = true)
  protected Map<String, NameValueList> weeklyCMap;
  
  public DRGMonthlySectionPayload() {
    
  }
  
  public DRGMonthlySectionPayload(POINT_MONTHLY pm) {
    if (pm == null) {
      return;
    }
    this.quantityIp = pm.getIpQuantity();
    this.quantityDrg = pm.getDrgQuantity();
    this.applPointIp = pm.getApplIp();
    this.applPointDrg = pm.getDrgApplPoint();
    this.pointDrg = pm.getDrgActualPoint();
    this.rateDrg = ReportService.cutPointNumber(((double) quantityDrg * (double) 100) / (double) quantityIp);
    this.ratePointDrg = ReportService.cutPointNumber(((double) applPointDrg * (double) 100) / (double) pm.getTotalIp());
    this.pointIp = pm.getTotalIp();
    this.diffDrg = applPointDrg - pointDrg;
    funcTypes = new ArrayList<String>();

    sectionA = new ArrayList<NameCodePointQuantity>();
    sectionB1 = new ArrayList<NameCodePointQuantity>();
    sectionB2 = new ArrayList<NameCodePointQuantity>();
    sectionC = new ArrayList<NameCodePointQuantity>();
    
    diffB1FuncType = new ArrayList<NameCodePoint>();
    diffB2FuncType = new ArrayList<NameCodePoint>();
    diffCFuncType = new ArrayList<NameCodePoint>();
    
    weeklyAMap = new HashMap<String, NameValueList>();
    weeklyB1Map = new HashMap<String, NameValueList>();
    weeklyB2Map = new HashMap<String, NameValueList>();
    weeklyCMap = new HashMap<String, NameValueList>();
      
    //quantityMap = new HashedMap<String, NameValueList2>();
    //quantityMap.put(ReportService.FUNC_TYPE_ALL_NAME, new NameValueList2());
    //pointMap = new HashedMap<String, NameValueList2>();
    //pointMap.put(ReportService.FUNC_TYPE_ALL_NAME, new NameValueList2());
  }
  
  public Long getQuantityA() {
    return quantityA;
  }

  public void setQuantityA(Long quantityA) {
    this.quantityA = quantityA;
  }

  public Long getQuantityB1() {
    return quantityB1;
  }

  public void setQuantityB1(Long quantityB1) {
    this.quantityB1 = quantityB1;
  }

  public Long getQuantityB2() {
    return quantityB2;
  }

  public void setQuantityB2(Long quantityB2) {
    this.quantityB2 = quantityB2;
  }

  public Long getQuantityC() {
    return quantityC;
  }

  public void setQuantityC(Long quantityC) {
    this.quantityC = quantityC;
  }

  public Long getActualA() {
    return actualA;
  }

  public void setActualA(Long actualA) {
    this.actualA = actualA;
  }

  public Long getActualB1() {
    return actualB1;
  }

  public void setActualB1(Long actualB1) {
    this.actualB1 = actualB1;
  }

  public Long getActualB2() {
    return actualB2;
  }

  public void setActualB2(Long actualB2) {
    this.actualB2 = actualB2;
  }

  public Long getActualC() {
    return actualC;
  }

  public void setActualC(Long actualC) {
    this.actualC = actualC;
  }

  public Long getApplA() {
    return applA;
  }

  public void setApplA(Long applA) {
    this.applA = applA;
  }

  public Long getApplB1() {
    return applB1;
  }

  public void setApplB1(Long applB1) {
    this.applB1 = applB1;
  }

  public Long getApplB2() {
    return applB2;
  }

  public void setApplB2(Long applB2) {
    this.applB2 = applB2;
  }

  public Long getApplC() {
    return applC;
  }

  public void setApplC(Long applC) {
    this.applC = applC;
  }

  public Long getDiffA() {
    return diffA;
  }

  public void setDiffA(Long diffA) {
    this.diffA = diffA;
  }

  public Long getDiffB1() {
    return diffB1;
  }

  public void setDiffB1(Long diffB1) {
    this.diffB1 = diffB1;
  }

  public Long getDiffB2() {
    return diffB2;
  }

  public void setDiffB2(Long diffB2) {
    this.diffB2 = diffB2;
  }

  public Long getDiffC() {
    return diffC;
  }

  public void setDiffC(Long diffC) {
    this.diffC = diffC;
  }

  public List<NameCodePointQuantity> getSectionA() {
    return sectionA;
  }

  public void setSectionA(List<NameCodePointQuantity> sectionA) {
    this.sectionA = sectionA;
  }

  public List<NameCodePointQuantity> getSectionB1() {
    return sectionB1;
  }

  public void setSectionB1(List<NameCodePointQuantity> sectionB1) {
    this.sectionB1 = sectionB1;
  }

  public List<NameCodePointQuantity> getSectionB2() {
    return sectionB2;
  }

  public void setSectionB2(List<NameCodePointQuantity> sectionB2) {
    this.sectionB2 = sectionB2;
  }

  public List<NameCodePointQuantity> getSectionC() {
    return sectionC;
  }

  public void setSectionC(List<NameCodePointQuantity> sectionC) {
    this.sectionC = sectionC;
  }

  public List<NameCodePoint> getDiffB1FuncType() {
    return diffB1FuncType;
  }

  public void setDiffB1FuncType(List<NameCodePoint> diffB1FuncType) {
    this.diffB1FuncType = diffB1FuncType;
  }

  public List<NameCodePoint> getDiffB2FuncType() {
    return diffB2FuncType;
  }

  public void setDiffB2FuncType(List<NameCodePoint> diffB2FuncType) {
    this.diffB2FuncType = diffB2FuncType;
  }

  public List<NameCodePoint> getDiffCFuncType() {
    return diffCFuncType;
  }

  public void setDiffCFuncType(List<NameCodePoint> diffCFuncType) {
    this.diffCFuncType = diffCFuncType;
  }

  public Map<String, NameValueList> getWeeklyAMap() {
    return weeklyAMap;
  }

  public void setWeeklyAMap(Map<String, NameValueList> weeklyAMap) {
    this.weeklyAMap = weeklyAMap;
  }

  public Map<String, NameValueList> getWeeklyB1Map() {
    return weeklyB1Map;
  }

  public void setWeeklyB1Map(Map<String, NameValueList> weeklyB1Map) {
    this.weeklyB1Map = weeklyB1Map;
  }

  public Map<String, NameValueList> getWeeklyB2Map() {
    return weeklyB2Map;
  }

  public void setWeeklyB2Map(Map<String, NameValueList> weeklyB2Map) {
    this.weeklyB2Map = weeklyB2Map;
  }

  public Map<String, NameValueList> getWeeklyCMap() {
    return weeklyCMap;
  }

  public void setWeeklyCMap(Map<String, NameValueList> weeklyCMap) {
    this.weeklyCMap = weeklyCMap;
  }
}
