/**
 * Created on 2021/4/15.
 */
package tw.com.leadtek.nhiwidget.service;

import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.model.CodeBase;
import tw.com.leadtek.nhiwidget.model.JsonSuggestion;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;

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
  private RedisService redis;

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
        List<JsonSuggestion> list = redis.query(cat, code);
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
    CODE_TABLE ct = cts.getCodeTable(cat, code);
    if (ct == null) {
      list.add(new CodeBase(code));
    } else {
      list.add(new CodeBase(ct));
    }
  }
  
  public static String getDesc(CodeTableService cts, String cat, String code) {
    if (code == null || code.length() == 0) {
      return null;
    }
    CODE_TABLE ct = cts.getCodeTable(cat, code);
    if (ct == null) {
      return code;
    }
    return code + "-" + ct.getDescChi();
  }
}
