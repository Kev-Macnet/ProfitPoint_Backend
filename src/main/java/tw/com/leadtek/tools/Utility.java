/**
 * Created on 2021/5/14.
 */
package tw.com.leadtek.tools;

public class Utility {

  public static int getTotalPage(int total, int perPage) {
    int result = total / perPage;
    if (total % perPage > 0) {
      result++;
    }
    return result;
  }
}
