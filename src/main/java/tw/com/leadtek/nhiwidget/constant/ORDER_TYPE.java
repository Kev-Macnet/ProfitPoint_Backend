/**
 * Created on 2021/12/24.
 */
package tw.com.leadtek.nhiwidget.constant;

public enum ORDER_TYPE {

  DIAGNOSIS("0"),   // 診察費
  DRUG("1"),    // 用藥
  TREAT("2"),       // 診療
  METERIAL("3"),    // 特殊材料
  NO_PAY("4"),      // 不得另計價之藥品、檢驗、診療
  EPO("5"),         // EPO注射
  HCT("6"),         // HCT檢驗
  ON_BEHALF_OF("7"),// 代檢及轉檢
  DONATION("8"),    //器官捐贈
  DSVC("9"),        // 藥事服務費
  NO_MED("K");      // 不計入醫療費用點數合計欄位項目
  
  private String value;
  
  private ORDER_TYPE(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
