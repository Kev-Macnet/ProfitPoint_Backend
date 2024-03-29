/**
 * Created on 2021/11/19.
 */
package tw.com.leadtek.nhiwidget.constant;

public enum INTELLIGENT_REASON {
  XML(0),             // 匯完申報檔、病歷檔後的AI條件檢查
  VIOLATE(1),         // 違反支付準則項目-支付準則條件
  RARE_ICD(2),        // 罕見ICD應用
  HIGH_RATIO(3),      // 應用比例偏高醫令
  OVER_AMOUNT(4),     // 特別用量藥材
  MATERIAL(5),        // 衛材
  INH_OWN_EXIST(6),   // 健保項目對應自費項目並存
  INFECTIOUS(7),      // 法定傳染病
  SAME_ATC(8),        // 同性質藥物開立
  PILOT_PROJECT(9),   // 相關計畫疑似可收案病例
  HIGH_RISK(10),      // 高風險診斷碼與健保碼組合(被核刪過的診斷碼與醫令組合)
  COST_DIFF(11),      // 臨床路徑差異–AI提示-費用差異
  ORDER_DIFF(12),     // 臨床路徑差異–AI提示-醫療行為差異
  ORDER_DRUG(13),     // 臨床路徑差異–AI提示-用藥、衛品差異
  IP_DAYS(14),        // 臨床路徑差異–AI提示-住院天數差異
  DRUG_DIFF(15),      // 用藥差異
  SUSPECTED(16),      // 疑似職傷與異常就診記錄判斷
  DRG_SUGGESTION(17); // DRG申報建議

  private int value;
  
  private INTELLIGENT_REASON(int value) {
    this.value = value;
  }

  public int value() {
    return value;
  }
  
  public static String toReasonString(int value) {
    switch(value) {
      case 1: return "違反支付準則項目-支付準則條件";
      case 2: return "罕見ICD應用";
      case 3: return "應用比例偏高醫令";
      case 4: return "特別用量藥材";
      case 5: return "衛材";
      case 6: return "健保項目對應自費項目並存";
      case 7: return "法定傳染病";
      case 8: return "同性質藥物開立";
      case 9: return "相關計畫疑似可收案病例";
      case 10: return "高風險診斷碼與健保碼組合";
      case 11: return "費用差異";
      case 12: return "醫療行為差異";
      case 13: return "用藥、衛品差異";
      case 14: return "住院天數差異";
    }
    return "未知";
  }
  
  public static int ReasonStringToInt(String status) {
    if (status.equals("違反支付準則項目-支付準則條件")) {
      return 1;
    } else if (status.equals("罕見ICD應用")) {
      return 2;
    } else if (status.equals("應用比例偏高醫令")) {
      return 3;
    } else if (status.equals("特別用量藥材")) {
      return 4;
    } else if (status.equals("衛材")) {
      return 5;
    } else if (status.equals("健保項目對應自費項目並存")) {
      return 6;
    } else if (status.equals("法定傳染病")) {
      return 7;
    } else if (status.equals("同性質藥物開立")) {
      return 8;
    } else if (status.equals("相關計畫疑似可收案病例")) {
      return 9;
    } else if (status.equals("高風險診斷碼與健保碼組合")) {
      return 10;
    }
    return 0;
  }

}

