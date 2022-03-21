/**
 * Created on 2022/3/16.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("健保總額累積達成率季度資料")
public class AchievementQuarter extends BaseResponse implements Serializable {

  private static final long serialVersionUID = 6906815930416042736L;

  @ApiModelProperty(value = "門急診/住院季度資料", required = true)
  private List<QuarterData> all;
  
  @ApiModelProperty(value = "住院季度資料", required = true)
  private List<QuarterData> ip;
  
  @ApiModelProperty(value = "門急診季度資料", required = true)
  private List<QuarterData> op;
  
  public AchievementQuarter() {
    all = new ArrayList<QuarterData>();
    ip = new ArrayList<QuarterData>();
    op = new ArrayList<QuarterData>();
  }

  public List<QuarterData> getAll() {
    return all;
  }

  public void setAll(List<QuarterData> all) {
    this.all = all;
  }

  public List<QuarterData> getIp() {
    return ip;
  }

  public void setIp(List<QuarterData> ip) {
    this.ip = ip;
  }

  public List<QuarterData> getOp() {
    return op;
  }

  public void setOp(List<QuarterData> op) {
    this.op = op;
  }
  
}
