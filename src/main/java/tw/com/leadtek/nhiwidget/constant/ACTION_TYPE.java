/**
 * Created on 2021/12/20.
 */
package tw.com.leadtek.nhiwidget.constant;

public enum ACTION_TYPE {

  ADD(1), // 無需變更
  MODIFIED(2), // 修改
  DELETED(3); // 評估不調整
  
  private int value;
  
  private ACTION_TYPE(int value) {
    this.value = value;
  }

  public int value() {
    return value;
  }
}
