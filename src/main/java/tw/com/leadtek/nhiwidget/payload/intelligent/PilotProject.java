/**
 * Created on 2022/2/11.
 */
package tw.com.leadtek.nhiwidget.payload.intelligent;

import java.util.List;

/**
 * 試辦計畫
 * @author kenlai
 *
 */
public class PilotProject {

  private String name;
  
  // table PLAN_CONDITION id
  private Long id;
  
  // 診斷碼
  private List<String> icd;
  
  // 就醫次數限制
  private Integer times;

  // 就醫天數限制
  private Integer days;
  
  public PilotProject() {
    
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<String> getIcd() {
    return icd;
  }

  public void setIcd(List<String> icd) {
    this.icd = icd;
  }

  public Integer getTimes() {
    return times;
  }

  public void setTimes(Integer times) {
    this.times = times;
  }

  public Integer getDays() {
    return days;
  }

  public void setDays(Integer days) {
    this.days = days;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
}
