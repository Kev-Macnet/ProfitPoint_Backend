/**
 * Created on 2022/7/12.
 */
package tw.com.leadtek.nhiwidget.service.pt;

public class BasicPaymentTerms {

  public int checkDBColumnType(Object obj) {
    if (obj == null) {
      return 0;
    }
    if (obj instanceof Integer) {
      return (Integer) obj;  
    } else {
      return (Short) obj;
    }
  }
}
