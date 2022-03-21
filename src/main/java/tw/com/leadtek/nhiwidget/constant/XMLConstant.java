/**
 * Created on 2020/12/28.
 */
package tw.com.leadtek.nhiwidget.constant;

import java.util.HashMap;
import java.util.Map;

public class XMLConstant {


  /**
   * 門診
   */
  public final static String DATA_FORMAT_OP = "10";
  
  /**
   * 住院
   */
  public final static String DATA_FORMAT_IP = "20";

  /**
   * 藥局
   */
  public final static String DATA_FORMAT_PHARMACY = "30";
  
  /**
   * 物理(職能)治療所
   */
  public final static String DATA_FORMAT_PHYSICAL = "40";
  
  /**
   * 醫事檢驗(放射)所
   */
  public final static String DATA_FORMAT_RADIOLOGY = "60";
  
  /**
   * 申報類別-送核
   */
  public final static String APPL_TYPE_SEND = "1";
  
  /**
   * 申報類別-補報
   */
  public final static String APPL_TYPE_RESEND = "2";
  
  /**
   * 申報類別-申復
   */
  public final static String APPL_TYPE_APPLY_FOR_REPLY = "3";
  
  /**
   * 申報類別-爭議
   */
  public final static String APPL_TYPE_DISPUTE = "4";
  
  /**
   * 申報類別-訴願
   */
  public final static String APPL_TYPE_PETITION = "5";
  
  /**
   * 申報類別-行政訴訟
   */
  public final static String APPL_TYPE_ADMIN_LITIGATION = "6";
  
  /**
   * 申報方式-書面
   */
  public final static String APPL_MODE_PAPER = "1";
  
  /**
   * 申報方式-媒體
   */
  public final static String APPL_MODE_MEDIA = "2";
  
  /**
   * 申報方式-連線
   */
  public final static String APPL_MODE_CONNECT = "3";
  
  /**
   * 不分科科別代碼
   */
  public final static String FUNC_TYPE_ALL = "00";
  
  /** 紀錄 t1 資料格式 */
  public static Map<String, String> MAP_DATA_FORMAT = new HashMap<String, String>();
  
  /** 紀錄 t4 申報方式 **/
  public static Map<String, String> MAP_APPL_MODE = new HashMap<String, String>();
  
  /** 紀錄 t5 申報類別 **/
  public static Map<String, String> MAP_APPL_TYPE = new HashMap<String, String>();

  static {
    MAP_DATA_FORMAT.put(DATA_FORMAT_OP, "門診");
    MAP_DATA_FORMAT.put(DATA_FORMAT_IP, "住院");

    MAP_APPL_MODE.put(APPL_MODE_PAPER, "書面");
    MAP_APPL_MODE.put(APPL_MODE_MEDIA, "媒體");
    MAP_APPL_MODE.put(APPL_MODE_CONNECT, "連線");
    
    MAP_APPL_TYPE.put(APPL_TYPE_SEND, "送核");
    MAP_APPL_TYPE.put(APPL_TYPE_RESEND, "補報");
    MAP_APPL_TYPE.put(APPL_TYPE_APPLY_FOR_REPLY, "申復");
    
  }
}
