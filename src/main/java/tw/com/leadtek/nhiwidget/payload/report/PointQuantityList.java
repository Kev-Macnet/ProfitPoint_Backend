/**
 * Created on 2021/11/5.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("存放全部、門急診、住院各名稱的點數及件數")
public class PointQuantityList implements Serializable {

  private static final long serialVersionUID = -1503041998522151783L;

  @ApiModelProperty(value = "全部", required = true)
  private List<NameCodePointQuantity> all;
  
  @ApiModelProperty(value = "住院", required = true)
  private List<NameCodePointQuantity> ip;
  
  @ApiModelProperty(value = "門診", required = true)
  private List<NameCodePointQuantity> op;
  
  public PointQuantityList() {
    all = new ArrayList<NameCodePointQuantity>();
    ip = new ArrayList<NameCodePointQuantity>();
    op = new ArrayList<NameCodePointQuantity>();
  }
  
  public void addIp(NameCodePointQuantity npq) {
    ip.add(npq);
    all.add(npq);
  }
  
  public void addOp(NameCodePointQuantity npq) {
    op.add(npq);
    all.add(npq);
  }
  
  public void addAll(NameCodePointQuantity npq) {
    for (NameCodePointQuantity namePointQuantity : all) {
      if (namePointQuantity.getName().equals(npq.getName())) {
        namePointQuantity.setPoint(namePointQuantity.getPoint() + npq.getPoint());
        namePointQuantity.setQuantity(namePointQuantity.getQuantity() + npq.getQuantity());
        return;
      }
    }
    all.add(npq);
  }

  public List<NameCodePointQuantity> getAll() {
    return all;
  }

  public void setAll(List<NameCodePointQuantity> all) {
    this.all = all;
  }

  public List<NameCodePointQuantity> getIp() {
    return ip;
  }

  public void setIp(List<NameCodePointQuantity> ip) {
    this.ip = ip;
  }

  public List<NameCodePointQuantity> getOp() {
    return op;
  }

  public void setOp(List<NameCodePointQuantity> op) {
    this.op = op;
  }
  
}
