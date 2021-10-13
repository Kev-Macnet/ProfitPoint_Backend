/**
 * Created on 2021/10/12.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_CONFLICT;

@ApiModel("健保項目對應自費項目並存詳細資料")
public class CodeConflictPayload extends CodeConflictListPayload implements Serializable {

  private static final long serialVersionUID = -6859869226469065734L;

  @ApiModelProperty(value = "院內碼名稱", required = false)
  protected String inhName;
  
  @ApiModelProperty(value = "支付標準名稱", required = false)
  protected String name;
  
  @ApiModelProperty(value = "自費品項院內碼名稱", required = false)
  protected String ownName;
  
  @ApiModelProperty(value = "健保項目數量", required = true)
  protected Integer quantityNh;
  
  @ApiModelProperty(value = "自費項目數量", required = true)
  protected Integer quantityOwn;
  
  public CodeConflictPayload() {
    
  }
  
  public CodeConflictPayload(CODE_CONFLICT cc) {
    super(cc);
    inhName = cc.getInhDesc();
    name = cc.getDescChi();
    ownName = cc.getOwnExpDesc();
    quantityNh = cc.getQuantityNh();
    quantityOwn = cc.getQuantityOwn();
  }
  
  public CODE_CONFLICT toDB() {
    CODE_CONFLICT result = new CODE_CONFLICT();
    result.setId(id);
    result.setCode(code);
    if (op && ip) {
      result.setDataFormat("00");
    } else if(op) {
      result.setDataFormat("10");
    } else if (ip) {
      result.setDataFormat("20"); 
    }
    result.setDescChi(name);
    result.setEndDate(edate);
    result.setInhCode(inhCode);
    result.setInhDesc(inhName);
    result.setOwnExpCode(ownCode);
    result.setOwnExpDesc(ownName);
    result.setQuantityNh(quantityNh);
    result.setQuantityOwn(quantityOwn);
    result.setStartDate(sdate);
    result.setStatus(status.booleanValue() ? 1 : 0);
    result.setUpdateAt(new Date());
    return result;
  }

  public String getInhName() {
    return inhName;
  }

  public void setInhName(String inhName) {
    this.inhName = inhName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getQuantityNh() {
    return quantityNh;
  }

  public void setQuantityNh(Integer quantityNh) {
    this.quantityNh = quantityNh;
  }

  public Integer getQuantityOwn() {
    return quantityOwn;
  }

  public void setQuantityOwn(Integer quantityOwn) {
    this.quantityOwn = quantityOwn;
  }

  public String getOwnName() {
    return ownName;
  }

  public void setOwnName(String ownName) {
    this.ownName = ownName;
  }
  
}
