/**
 * Created on 2021/7/28.
 */
package tw.com.leadtek.tools;

public class StringUtility {

  public final static char MASK = '*';
  
  /**
   * 身分證字號後4碼遮蔽
   */
  public final static String MASK_ID = "id";
  
  /**
   * number:遮蔽學號，保留前2後2
   */
  public final static String MASK_NUMBER = "number";

  /**
   * name:保留前後一字
   */
  public final static String MASK_NAME = "name";

  /**
   * birthday:遮蔽生日的月日
   */
  public final static String MASK_BIRTHDAY = "birthday";
  
  /**
   * mobile:遮蔽手機門號，只顯示前3碼、後3碼
   */
  public final static String MASK_MOBILE = "mobile";
  
  public static String maskString(String s, String maskType) {
    if (s == null || s.length() < 2 || maskType == null || "".equals(maskType)) {
      return s;
    }
    if (maskType.equals(MASK_BIRTHDAY)) {
      String birthday =  (s.indexOf(' ') > 0) ? s.split(" ")[0] : s;
      if (birthday.indexOf("/") > 0) {
        return maskBirthday(birthday, "/");
      } else if (birthday.indexOf("-") > 0) {
        return maskBirthday(birthday, "-");
      }
    } else if (maskType.equals(MASK_ID)) {
      if (s.length() > 6) {
        StringBuffer sb = new StringBuffer();
        for(int i=s.length()-1; i>=0; i--) {
          if (i > s.length() - 5) {
            sb.append(MASK);
          } else {
            sb.insert(0, s.charAt(i));
          }
        }
        return sb.toString();
      }
    } else if (maskType.equals(MASK_MOBILE)) {
      if (s.length() > 7) {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<s.length(); i++) {
          if (i > s.length() - 4 || i < s.length() - 6) {
            sb.append(s.charAt(i));
          } else {
            sb.append(MASK);
          }
        }
        return sb.toString();
      }
    } else if (maskType.equals(MASK_NAME)) {
      if (s.length() == 2) {
        StringBuffer sb = new StringBuffer();
        sb.append(s.charAt(0));
        sb.append(MASK);
        return sb.toString();
      } else {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<s.length(); i++) {
          if (i == s.length() - 2) {
            sb.append(MASK);
          } else {
            sb.append(s.charAt(i));
          }
        }
        return sb.toString();
      }
    } else if (maskType.equals(MASK_NUMBER)) {
      if (s.length() > 5) {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<s.length(); i++) {
          if (i < 2 || i > s.length() - 3) {
            sb.append(s.charAt(i));
          } else {
            sb.append(MASK);
          }
        }
        return sb.toString();
      }
    }
    return s;
  }
  
  public static String maskBirthday(String birthday, String split) {
    StringBuffer sb = new StringBuffer();
    if (birthday.indexOf(split) > 0) {
      String[] ss = birthday.split(split);
      sb.append(ss[0]);
      sb.append(split);
      for (int i=1; i<ss.length; i++) {
        sb.append(MASK);
        sb.append(MASK);
        sb.append(split);
      }
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }
  
  public static String formatICD(String code) {
    if (code == null) {
      return null;
    }
    if (code.length() > 7 && code.charAt(code.length() - 2) == '.') {
      return code.substring(0, code.length() - 2) + code.charAt(code.length() - 1);
    }
    if (code.indexOf('.') > 0) {
      return code.toLowerCase();
    }
    StringBuffer sb = new StringBuffer(code);
//    if (sb.length() > 6) {
//      sb.insert(6, '.');
//    }
    if (sb.length() > 3) {
      sb.insert(3, '.');
    }
    return sb.toString().toLowerCase();
  }
  
  public static String formatICDtoUpperCase(String code) {
    if (code == null) {
      return null;
    }
    String c = code.trim();
    if (c.length() == 0 || "\r\n".equals(c)) {
      return null;
    }
    if (c.indexOf('.') > 0) {
      return c.toUpperCase();
    }
    StringBuffer sb = new StringBuffer(c);
    if (sb.length() > 6) {
      sb.insert(6, '.');
    }
    if (sb.length() > 3) {
      sb.insert(3, '.');
    }
    return sb.toString().toUpperCase();
  }
  
  public static String removeDotFromICD(String code) {
    if (code.indexOf('.') < 0) {
      return code;
    }
    StringBuffer sb = new StringBuffer(code);
    for (int i=0; i<code.length(); i++) {
      if (code.charAt(i) != '.') {
        sb.append(code.charAt(i));
      }
    }
    return sb.toString();
  }
  
  public static String[] splitBySpace(String s) {
    if (s == null || s.length() < 1) {
      return new String[0];
    }
    if (s.indexOf(' ') < 0) {
      return new String[] {s};
    }
    return s.split(" ");
  }
}
