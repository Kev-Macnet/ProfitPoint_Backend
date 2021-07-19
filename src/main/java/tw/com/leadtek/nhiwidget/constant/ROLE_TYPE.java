/**
 * Created on 2021/5/3.
 */
package tw.com.leadtek.nhiwidget.constant;

public enum ROLE_TYPE {

  user(1), // 一般user
  doctor(2),        // 醫師
  supervisor(3),    // 主管
  administrator(4), // 系統管理員
  superadmin(5);    // 系統開發商

  private Integer type;

  ROLE_TYPE(Integer type) {
    this.type = type;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }
  
  public static String getRoleString(Integer type) {
    switch(type) {
      case 1: return "user";
      case 2: return "doctor";
      case 3: return "supervisor";
      case 4: return "administrator";
      case 5: return "superadmin";
    }
    return "unknown";
  }
}
