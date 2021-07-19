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
    return "未知";
  }

  public static void main(String[] args) {
    for (MR_STATUS status : MR_STATUS.values()) {
      System.out.printf("MR_STATUS(%s, %d)%n", status, status.value());
    }
  }
}
