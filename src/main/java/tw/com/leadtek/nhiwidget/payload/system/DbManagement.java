/**
 * Created on 2022/1/13.
 */
package tw.com.leadtek.nhiwidget.payload.system;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;

@ApiModel("資料庫管理功能設定")
public class DbManagement extends BaseResponse {

  private static final long serialVersionUID = -8467328651000687304L;
  
  @ApiModelProperty(value = "使用者清單，0:手動新增，1:從HIS串接AD", required = true)
  protected Integer addUserBy;
  
  @ApiModelProperty(value = "科別與科別代碼、醫護名單與代碼、負責人員名單與代碼，0:手動新增，1:從HIS串接匯入", required = true)
  protected Integer addFuncCodeBy;
  
  @ApiModelProperty(value = "代碼品項資料庫，0:手動新增，1:從HIS串接匯入", required = true)
  protected Integer addPayCodeBy;

  public DbManagement() {
    
  }

  public DbManagement(List<PARAMETERS> list) {
    if (list == null || list.size() == 0) {
      return;
    }
    for (PARAMETERS parameters : list) {
      if (parameters.getName().equals("ADD_USER_BY")) {
        addUserBy = Integer.parseInt(parameters.getValue());
      } else if (parameters.getName().equals("ADD_FUNC_CODE_BY")) {
        addFuncCodeBy = Integer.parseInt(parameters.getValue());
      } else if (parameters.getName().equals("ADD_PAY_CODE_BY")) {
        addPayCodeBy = Integer.parseInt(parameters.getValue());
      }
    }
  }
  
  public Integer getAddUserBy() {
    return addUserBy;
  }

  public void setAddUserBy(Integer addUserBy) {
    this.addUserBy = addUserBy;
  }

  public Integer getAddFuncCodeBy() {
    return addFuncCodeBy;
  }

  public void setAddFuncCodeBy(Integer addFuncCodeBy) {
    this.addFuncCodeBy = addFuncCodeBy;
  }

  public Integer getAddPayCodeBy() {
    return addPayCodeBy;
  }

  public void setAddPayCodeBy(Integer addPayCodeBy) {
    this.addPayCodeBy = addPayCodeBy;
  }
  
}
