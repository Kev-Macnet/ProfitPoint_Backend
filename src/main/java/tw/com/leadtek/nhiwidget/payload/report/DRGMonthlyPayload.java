/**
 * Created on 2021/11/11.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.constant.XMLConstant;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_MONTHLY;
import tw.com.leadtek.nhiwidget.service.ReportService;

@ApiModel("DRG分配比例(月報)")
public class DRGMonthlyPayload extends DRGReportPayload implements Serializable {

  private static final long serialVersionUID = -1262364590421775271L;
  
  @ApiModelProperty(value = "各科DRG件數/非DRG件數", required = true)
  protected Map<String, NameValueList2> quantityMap;
  
  @ApiModelProperty(value = "各科DRG點數/非DRG點數", required = true)
  protected Map<String, NameValueList2> pointMap;
  
  public DRGMonthlyPayload() {
    
  }

  public DRGMonthlyPayload(POINT_MONTHLY pm) {
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
    quantityMap = new HashedMap<String, NameValueList2>();
    quantityMap.put(XMLConstant.FUNC_TYPE_ALL, new NameValueList2(ReportService.FUNC_TYPE_ALL_NAME));
    pointMap = new HashedMap<String, NameValueList2>();
    pointMap.put(XMLConstant.FUNC_TYPE_ALL, new NameValueList2(ReportService.FUNC_TYPE_ALL_NAME));
  }

  public NameValueList2 getQuantityList(String funcType, String funcTypeName) {
    NameValueList2 result = quantityMap.get(funcType);
    if (result == null) {
      result = new NameValueList2(funcTypeName);
      quantityMap.put(funcType, result);
    }
    return result;
  }

  public NameValueList2 getPointList(String funcType, String funcTypeName) {
    NameValueList2 result = pointMap.get(funcType);
    if (result == null) {
      result = new NameValueList2(funcTypeName);
      pointMap.put(funcType, result);
    }
    return result;
  }

  public Map<String, NameValueList2> getQuantityMap() {
    return quantityMap;
  }

  public void setQuantityMap(Map<String, NameValueList2> quantityMap) {
    this.quantityMap = quantityMap;
  }

  public Map<String, NameValueList2> getPointMap() {
    return pointMap;
  }

  public void setPointMap(Map<String, NameValueList2> pointMap) {
    this.pointMap = pointMap;
  }
  
}
