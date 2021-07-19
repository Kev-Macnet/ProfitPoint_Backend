/**
 * Created on 2021/1/5.
 */
package tw.com.leadtek.nhiwidget.model;

import java.io.Serializable;
import java.util.List;

/**
 * 存放申報檔XML中用到的代碼
 * 
 * @author 2268
 *
 */
//@Document(collection = "code_map")
public class CodeMap extends XMLTag implements Serializable {

  private static final long serialVersionUID = -7735982250090937027L;

  private List<CodeMap> children;

  public List<CodeMap> getChildren() {
    return children;
  }

  public void setChildren(List<CodeMap> children) {
    this.children = children;
  }

  public static CodeMap initial(String[] ss, int codeIndex) {
    if (codeIndex >= ss.length) {
      return null;
    } else if ("null".equals(ss[codeIndex])) {
      return null;
    }
    CodeMap result = new CodeMap();
    result.setCode(ss[codeIndex]);
    result.setDesc(ss[codeIndex + 1]);
    if (ss.length > (codeIndex + 2)) {
      result.setDescEn(ss[codeIndex + 2]);
    }
    return result;
  }
}


