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
  
  /**
   * 只針定特定科別做比對警示
   */
  private String[] funcType;

  /**
   * 只針定特定醫師做比對警示
   */
  private String[] doctors;
  
  private int rollbackHour;
  
  /**
   * 離當天超過幾天就不做比對警示及AI計算. 0:表示都要處理
   */
  private int daysIgnore = 0;
  
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
      } else if ("DAYS_IGNORE".equals(p.getName())) {
        daysIgnore = Integer.parseInt(p.getValue());
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

  public int getDaysIgnore() {
    return daysIgnore;
  }

  public void setDaysIgnore(int daysIgnore) {
    this.daysIgnore = daysIgnore;
  }
}
