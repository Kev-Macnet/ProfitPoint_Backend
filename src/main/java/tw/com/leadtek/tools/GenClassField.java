/**
 * Created on 2021/1/27.
 */
package tw.com.leadtek.tools;

/**
 * 由SQL轉成Java model class，給 JPA 用
 * 
 * @author kenlai
 *
 */
public class GenClassField {

  protected String name;

  protected String type;

  protected String comment;

  protected int length = 0;

  protected boolean primaryKey = false;

  protected boolean autoIncrement = false;

  protected boolean notNull = false;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (name.startsWith("\"") && name.endsWith("\"")) {
      this.name = name.substring(1, name.length() - 1);
    } else {
      this.name = name;
    }
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public boolean isPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(boolean primaryKey) {
    this.primaryKey = primaryKey;
  }

  public boolean isAutoIncrement() {
    return autoIncrement;
  }

  public void setAutoIncrement(boolean autoIncrement) {
    this.autoIncrement = autoIncrement;
  }

  public boolean isNotNull() {
    return notNull;
  }

  public void setNotNull(boolean notNull) {
    this.notNull = notNull;
  }

  public static String getJavaType(String type) {
    if ("BIGINT".equals(type)) {
      return "Long";
    }
    if ("VARCHAR".equals(type) || "NVARCHAR".equals(type)) {
      return "String";
    }
    if ("INTEGER".equals(type) || "INT".equals(type)) {
      return "Integer";
    }
    if ("FLOAT".equals(type)) {
      return "Float";
    }
    if ("DATE".equals(type)) {
      return "Date";
    }
    return "Unknown";
  }

  public static String toCamelCase(String s) {
    StringBuffer sb = new StringBuffer();

    boolean nextUpperCase = false;
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == '_') {
        nextUpperCase = true;
      } else if (s.charAt(i) < 'a') {
        // 大寫
        if (nextUpperCase) {
          sb.append(s.charAt(i));
          nextUpperCase = false;
        } else {
          if (s.charAt(i) >= 'A') {
            sb.append((char) (s.charAt(i) + 32));
          } else {
            sb.append(s.charAt(i));
          }
        }
      } else {
        // 小寫
        sb.append(s.charAt(i));
      }
    }
    return sb.toString();
  }
}
