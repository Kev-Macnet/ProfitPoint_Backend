/**
 * Created on 2021/3/16.
 */
package tw.com.leadtek.nhiwidget.constant;

public enum MR_STATUS {
  CLASSIFIED(-3), // 疾病分類完成
  WAIT_CONFIRM(-2), // 待確認
  QUESTION_MARK(-1), // 疑問標示
  WAIT_PROCESS(0), // 待處理
  NO_CHANGE(1), // 無需變更
  OPTIMIZED(2), // 優化完成
  DONT_CHANGE(3); // 評估不調整

  private int value;
  
  public final static int STATUS_ERROR = -100;
  
  public final static String STATUS_ERROR_DESC = "錯誤代碼";

  private MR_STATUS(int value) {
    this.value = value;
  }

  public int value() {
    return value;
  }
  
  public static String toStatusString(int value) {
    switch(value) {
      case -3: return "疾病分類完成";
      case -2: return "待確認";
      case -1: return "疑問標示";
      case 0: return "待處理";
      case 1: return "無需變更";
      case 2: return "優化完成";
      case 3: return "評估不調整";
    }
    return STATUS_ERROR_DESC;
  }
  
  public static int statusStringToInt(String status) {
    if (status.equals("疾病分類完成")) {
      return -3;
    } else if (status.equals("待確認")) {
      return -2;
    } else if (status.equals("疑問標示")) {
      return -1;
    } else if (status.equals("待處理")) {
      return 0;
    } else if (status.equals("無需變更")) {
      return 1;
    } else if (status.equals("優化完成")) {
      return 2;
    } else if (status.equals("評估不調整")) {
      return 3;
    }
    return STATUS_ERROR;
  }

  public static void main(String[] args) {
    for (MR_STATUS status : MR_STATUS.values()) {
      System.out.printf("MR_STATUS(%s, %d)%n", status, status.value());
    }
  }
}
