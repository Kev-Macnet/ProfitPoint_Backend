/**
 * Created on 2021/4/15.
 */
package tw.com.leadtek.nhiwidget.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.dao.PAY_CODEDao;
import tw.com.leadtek.nhiwidget.model.CodeBase;
import tw.com.leadtek.nhiwidget.model.JsonSuggestion;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.DEPARTMENT;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
import tw.com.leadtek.tools.StringUtility;

/**
 * 存放 DB CODE_TABLE 裡面的內容，減少query DB loading
 * 
 * @author kenlai
 *
 */
@Service
public class CodeTableService {

  private final static String[] REDIS_CAT = {"ORDER","ICD10-CM", "ICD10-PCS", "DEDUCTED"};
  
  @Autowired
  private CODE_TABLEDao ctDao;
  
  @Autowired
  private PAY_CODEDao payCodeDao;
  
  @Autowired
  private RedisService redis;
  
  @Autowired
  private UserService userService;

  private HashMap<String, HashMap<String, CODE_TABLE>> codes;

  public void refreshCodes() {
    HashMap<String, HashMap<String, CODE_TABLE>> newcodes =
        new HashMap<String, HashMap<String, CODE_TABLE>>();
    List<CODE_TABLE> list = ctDao.findAll();
    for (CODE_TABLE codeTable : list) {
      HashMap<String, CODE_TABLE> cat = newcodes.get(codeTable.getCat());
      if (cat == null) {
        cat = new HashMap<String, CODE_TABLE>();
        newcodes.put(codeTable.getCat(), cat);
      }
      cat.put(codeTable.getCode(), codeTable);
    }
    codes = newcodes;
  }

  /**
   * 帶入代碼，取得說明
   * 
   * @param cat
   * @param code
   * @return
   */
  public String getDesc(String cat, String code) {
    CODE_TABLE ct = getCodeTable(cat, code);
    if (ct == null) {
      return "";
    }
    return ct.getDescChi();
  }

  public CODE_TABLE getCodeTable(String cat, String code) {
    if (codes == null) {
      refreshCodes();
    }
    if (code == null || code.length() == 0) {
      return null;
    }
    for (String string : REDIS_CAT) {
      if (string.equals(cat)) {
        List<JsonSuggestion> list = redis.query(cat, code.toLowerCase(), false);
        if (list.size() > 0) {
          CODE_TABLE result = new CODE_TABLE();
          result.setCat(cat);
          result.setCode(code);
          result.setDescChi(list.get(0).getValue());
          result.setDescEn(list.get(0).getLabel());
          return result;
        }
      }
    }
    HashMap<String, CODE_TABLE> codeMap = codes.get(cat);
    if (codeMap == null) {
      return null;
    }
    return codeMap.get(code);
  }
  
  public static void addToList(CodeTableService cts, List<CodeBase> list, String cat, String code) {
    if (code == null || code.length() == 0) {
      return;
    }
    String codes = "ICD10-CM".equals(cat) ? formatICDForICD(code) : code;
   
    CODE_TABLE ct = cts.getCodeTable(cat, codes);
    if (ct == null) {
      list.add(new CodeBase(code));
    } else {
      CodeBase cb = new CodeBase(ct);
      cb.setCode(code);
      list.add(cb);
    }
  }
  
  public static CodeBase getCodeBase(CodeTableService cts, String cat, String code) {
    if (code == null || code.length() == 0) {
      return null;
    }
    String codes = "ICD10-CM".equals(cat) ? StringUtility.formatICD(code) : code;
    CODE_TABLE ct = cts.getCodeTable(cat, codes);
    if (ct == null) {
      return new CodeBase(code);
    } else {
      CodeBase cb = new CodeBase(ct);
      cb.setCode(code);
      return cb;
    }
  }
  
  public static String getDesc(CodeTableService cts, String cat, String code) {
    if (code == null) { 
      return null;
    }
    String c = code.trim();
    if (code.length() == 0) {
      return null; 
    }
    CODE_TABLE ct = cts.getCodeTable(cat, c);
    if (ct == null) {
      if ("ORDER".equals(cat) && code.length() == 10) {
        List<PAY_CODE> list = cts.getPayCodeDao().findByCode(c);
        if (list != null && list.size() > 0) {
          return code + "-" + list.get(0).getName();
        }
      }
      return code;
    }
    return code + "-" + ct.getDescChi();
  }
  
  public static String getInhCodeDesc(CodeTableService cts, String cat, String inhCode) {
    if (inhCode == null) { 
      return null;
    }
    String c = inhCode.trim();
    if (c.length() == 0) {
      return null; 
    }
    List<PAY_CODE> list = cts.getPayCodeDao().findByInhCode(c);
    if (list != null && list.size() > 0) {
      return list.get(0).getName();
    }
    return null;
  }
  
  public String getCodeByDesc(String cat, String desc) {
	if (codes == null) {
	  refreshCodes();
    }
    HashMap<String, CODE_TABLE> codeMap = codes.get(cat);
    if (codeMap == null) {
      return "unknown";
    }
    for (CODE_TABLE ct : codeMap.values()) {
      if (ct.getDescChi() != null && ct.getDescChi().equals(desc)) {
        return ct.getCode();
      }
    }
    return "unknown";
  }
  
  public String getFuncTypeCodeByName(String desc) {
    if (codes == null) {
      refreshCodes();
    }
    HashMap<String, CODE_TABLE> codeMap = codes.get("FUNC_TYPE");
    if (codeMap == null) {
      return getFuncTypeCodeByDepartment(desc);
    }
    for (CODE_TABLE ct : codeMap.values()) {
      if (ct.getDescChi() != null && ct.getDescChi().equals(desc)) {
        return ct.getCode();
      }
    }
    return getFuncTypeCodeByDepartment(desc);
  }
  
  private String getFuncTypeCodeByDepartment(String funcName) {
    DEPARTMENT department =  userService.findDepartmentByName(funcName);
    if (department == null || department.getNhCode() == null) {
      return "unknown";
    } else {
      return department.getNhCode();
    }
  }
  
  public List<CODE_TABLE> getInfectious(){
    return ctDao.findByCatOrderByCode("INFECTIOUS");
  }
  
  public List<String> convertFuncTypeToNameList(List<String> funcTypes) {
    List<String> result = new ArrayList<String>();
    for (int i = 0; i < funcTypes.size(); i++) {
      result.add(getDesc("FUNC_TYPE", funcTypes.get(i)));
    }
    return result;
  }
  
  public String convertFuncTypeToName(List<String> funcTypes) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < funcTypes.size(); i++) {
      sb.append(getDesc("FUNC_TYPE", funcTypes.get(i)));
      sb.append("、");
    }
    if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '、') {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }
  
  public String convertFuncTypecToFuncType(String funcTypec) {
    if (funcTypec == null || funcTypec.length() == 0) {
      return null;
    }
    if (funcTypec.indexOf(' ') > 0) {
      String[] ss = funcTypec.split(" ");
      StringBuffer sb = new StringBuffer();
      for (String s : ss) {
        sb.append(getFuncTypeCodeByName(s));
        sb.append(' ');
      }
      if (sb.charAt(sb.length() - 1) == ' ') {
        sb.deleteCharAt(sb.length() - 1);
      }
      return sb.toString();
    }
    return getFuncTypeCodeByName(funcTypec);
  }
  
  public String[] convertFuncTypecToFuncTypeArray(String funcTypec) {
    if (funcTypec == null || funcTypec.length() == 0) {
      return null;
    }
    if (funcTypec.indexOf(' ') > 0) {
      String[] ss = funcTypec.split(" ");
      String[] result = new String[ss.length];
      for (int i=0; i< ss.length; i++) {
        result[i] = getFuncTypeCodeByName(ss[i]);
      }
      return result;
    }
    String[] result = new String[1];
    result[0] = getFuncTypeCodeByName(funcTypec);
    return result;
  }
  
  public PAY_CODEDao getPayCodeDao() {
    return payCodeDao;
  }
  
  public static String formatICDForICD(String code) {
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
    if (sb.length() > 3) {
      sb.insert(3, '.');
    }
    return sb.toString().toLowerCase();
  }
}
