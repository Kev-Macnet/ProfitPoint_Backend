/**
 * Created on 2021/5/3.
 */
package tw.com.leadtek.nhiwidget.constant;

public enum ROLE_TYPE {

  MIS_ADM("A"), // MIS主管
  APPL_ADM("C"),   // 申報主管
  AM_ADM("B"),    // 行政主管
  APPL("D"), // coding/申報人員
  DOCTOR("E");    // 醫護人員

  private String role;

  ROLE_TYPE(String role) {
    this.role = role;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

//  public static String getRoleString(SInteger type) {
//    switch(type) {
//      case 1: return "user";
//      case 2: return "doctor";
//      case 3: return "supervisor";
//      case 4: return "administrator";
//      case 5: return "superadmin";
//    }
//    return "unknown";
//  }
}
