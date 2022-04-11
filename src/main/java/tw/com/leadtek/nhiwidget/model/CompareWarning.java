/**
 * Created on 2022/4/11.
 */
package tw.com.leadtek.nhiwidget.model;

import java.util.List;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.service.CodeTableService;
import tw.com.leadtek.tools.StringUtility;

public class CompareWarning {

  private int compareBy;
  
  private String[] funcType;
  
  private String[] doctors;
  
  private int rollbackHour;
  
  public CompareWarning(List<PARAMETERS> parametersList, CodeTableService codeTableService) {
    for (PARAMETERS p : parametersList) {
      if ("COMPARE_BY".equals(p.getName())) {
        compareBy = Integer.parseInt(p.getValue());
      } else if ("COMPARE_DOCTOR".equals(p.getName())) {
        doctors = StringUtility.splitBySpace(p.getValue());
      } else if ("COMPARE_FUNC_TYPE".equals(p.getName())) {
        funcType = codeTableService.convertFuncTypecToFuncTypeArray(p.getValue());
      } else if ("ROLLBACK_HOUR".equals(p.getName())) {
        rollbackHour = Integer.parseInt(p.getValue());
      }
    }
  }

  public int getCompareBy() {
    return compareBy;
  }

  public void setCompareBy(int compareBy) {
    this.compareBy = compareBy;
  }

  public String[] getFuncType() {
    return funcType;
  }

  public void setFuncType(String[] funcType) {
    this.funcType = funcType;
  }

  public String[] getDoctors() {
    return doctors;
  }

  public void setDoctors(String[] doctors) {
    this.doctors = doctors;
  }

  public int getRollbackHour() {
    return rollbackHour;
  }

  public void setRollbackHour(int rollbackHour) {
    this.rollbackHour = rollbackHour;
  }
  
}
