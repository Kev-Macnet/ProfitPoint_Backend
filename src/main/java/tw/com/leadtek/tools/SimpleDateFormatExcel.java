/**
 * Created on 2020/11/9.
 */
package tw.com.leadtek.tools;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SimpleDateFormatExcel extends SimpleDateFormat {

  private static final long serialVersionUID = 2967295246575219334L;

  public SimpleDateFormatExcel(String pattern) {
    super(pattern.toUpperCase().indexOf("AM/PM") > -1 ? pattern.substring(0, pattern.toUpperCase().indexOf("AM/PM")) + "a"
        : pattern, pattern.toUpperCase().indexOf("AM/PM") > -1 ? Locale.ENGLISH : Locale.TAIWAN);
  }
}
