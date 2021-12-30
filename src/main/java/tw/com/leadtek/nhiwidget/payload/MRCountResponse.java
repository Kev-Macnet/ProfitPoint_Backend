/**
 * Created on 2021/7/16.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "首頁用病歷總數及狀態統計回覆訊息", description = "all:不分門急診及住院，ip:住院相關數字，op:門急診相關數字")
public class MRCountResponse implements Serializable {

  private static final long serialVersionUID = -5094371256649393992L;

  @ApiModelProperty(value = "不分門急診及住院", required = true)
  private MRCount all;

  @ApiModelProperty(value = "住院相關數字", required = true)
  private MRCount ip;

  @ApiModelProperty(value = "門急診相關數字", required = true)
  private MRCount op;

  public MRCountResponse() {
    all = new MRCount();
    ip = new MRCount();
    op = new MRCount();
  }

  public MRCount getAll() {
    return all;
  }

  public void setAll(MRCount all) {
    this.all = all;
  }

  public MRCount getIp() {
    return ip;
  }

  public void setIp(MRCount ip) {
    this.ip = ip;
  }

  public MRCount getOp() {
    return op;
  }

  public void setOp(MRCount op) {
    this.op = op;
  }
  
  public void refreshAll() {
    all.setTotalMr(ip.getTotalMr().intValue() + op.getTotalMr().intValue());
    all.setApplSum(ip.getApplSum().intValue() + op.getApplSum().intValue());
    all.setDayCount(
        ip.getDayCount().intValue() > op.getDayCount().intValue() ? ip.getDayCount().intValue()
            : op.getDayCount().intValue());
    all.setApplDot(ip.getApplDot().intValue() + op.getApplDot().intValue());
    all.setClassified(ip.getClassified().intValue() + op.getClassified().intValue());
    all.setNoChange(ip.getNoChange().intValue() + op.getNoChange().intValue());
    all.setWaitConfirm(ip.getWaitConfirm().intValue() + op.getWaitConfirm().intValue());
    all.setWaitProcess(ip.getWaitProcess().intValue() + op.getWaitProcess().intValue());
    all.setQuestionMark(ip.getQuestionMark().intValue() + op.getQuestionMark().intValue());
    all.setOptimized(ip.getOptimized().intValue() + op.getOptimized().intValue());
    
    all.setDrg(ip.getDrg().intValue() + op.getDrg().intValue());
  }

}
