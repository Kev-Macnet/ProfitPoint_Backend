/**
 * Created on 2021/9/3.
 */
package tw.com.leadtek.nhiwidget.model;

import tw.com.leadtek.nhiwidget.model.rdb.DRG_CODE;

public class DrgCalculate {

  /**
   * DRG代碼
   */
  private String code;
  
  /**
   * 權重
   */
  private float rw;
  
 /**
  * 幾何平均住院日
  */
  private int avgInDay;
  
  /**
   * 給付定額  
   */
  private int fixed;
  
  /**
   * 下限點值
   */
  private int llimit;
  
  /**
   * 上限點值
   */
  private int ulimit;
  
  /**
   * 給付區間
   */
  private String section;
  
  /**
   * 是否為不得加計各項加成或其他另行加計之醫療點數之DRG
   */
  private boolean isDrgNoAdd = false;
  
  /**
   * 是否有導入，1:有，0:尚未導入
   */
  private boolean started;
  
  /**
   * 是否需計算案件數超過20
   */
  private boolean case20 = false;
  
  public DrgCalculate(DRG_CODE drg) {
    code = drg.getCode();
    avgInDay = drg.getAvgInDay();
    llimit = drg.getLlimit();
    ulimit = drg.getUlimit();
    started = drg.getStarted().intValue() == 1;
    if (drg.getCase20() != null) {
      case20 = drg.getCase20().intValue() == 1;
    }
    rw = drg.getRw();
  }

  public int getFixed() {
    return fixed;
  }

  public void setFixed(int fixed) {
    this.fixed = fixed;
  }

  public String getSection() {
    return section;
  }

  public void setSection(String section) {
    this.section = section;
  }

  public boolean isDrgNoAdd() {
    return isDrgNoAdd;
  }

  public void setDrgNoAdd(boolean isDrgNoAdd) {
    this.isDrgNoAdd = isDrgNoAdd;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public int getAvgInDay() {
    return avgInDay;
  }

  public void setAvgInDay(int avgInDay) {
    this.avgInDay = avgInDay;
  }

  public int getLlimit() {
    return llimit;
  }

  public void setLlimit(int llimit) {
    this.llimit = llimit;
  }

  public int getUlimit() {
    return ulimit;
  }

  public void setUlimit(int ulimit) {
    this.ulimit = ulimit;
  }

  public float getRw() {
    return rw;
  }

  public void setRw(float rw) {
    this.rw = rw;
  }

  public boolean isStarted() {
    return started;
  }

  public void setStarted(boolean started) {
    this.started = started;
  }

  public boolean isCase20() {
    return case20;
  }

  public void setCase20(boolean case20) {
    this.case20 = case20;
  }

}
