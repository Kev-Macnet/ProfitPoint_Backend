/**
 * Created on 2021/7/25.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;

public class MemoryStatus implements Serializable {

  private static final long serialVersionUID = 8175808604534591802L;

  private String heapSize;
  
  private String heapMaxSize;
  
  private String heapFreeSize;

  public String getHeapSize() {
    return heapSize;
  }

  public void setHeapSize(String heapSize) {
    this.heapSize = heapSize;
  }

  public String getHeapMaxSize() {
    return heapMaxSize;
  }

  public void setHeapMaxSize(String heapMaxSize) {
    this.heapMaxSize = heapMaxSize;
  }

  public String getHeapFreeSize() {
    return heapFreeSize;
  }

  public void setHeapFreeSize(String heapFreeSize) {
    this.heapFreeSize = heapFreeSize;
  }
  
}
