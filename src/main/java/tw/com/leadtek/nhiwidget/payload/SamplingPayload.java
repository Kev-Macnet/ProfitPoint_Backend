/**
 * Created on 2021/10/8.
 */
package tw.com.leadtek.nhiwidget.payload;

import java.io.Serializable;

public class SamplingPayload extends StartEndPayload implements Serializable {

  private static final long serialVersionUID = 6440880593962641556L;

  private int sampling;

  public int getSampling() {
    return sampling;
  }

  public void setSampling(int sampling) {
    this.sampling = sampling;
  }
  
}
