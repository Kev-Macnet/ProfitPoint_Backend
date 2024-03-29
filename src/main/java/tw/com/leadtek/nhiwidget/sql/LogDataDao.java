package tw.com.leadtek.nhiwidget.sql;

import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.com.leadtek.nhiwidget.dao.DRG_CALDao;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CAL;
import tw.com.leadtek.tools.Utility;


@Repository
public class LogDataDao extends BaseSqlDao{

  private Logger logger = LogManager.getLogger();

  @Autowired
  protected JdbcTemplate jdbcTemplate;
  
  @Autowired
  protected DRG_CALDao drgCalDao;

  public java.util.List<Map<String, Object>> find_IP_D(String idCard, String in_date) {
    String sql;
    sql =
        "Select CARD_SEQ_NO,ROC_ID,ID_BIRTH_YMD,IN_DATE,OUT_DATE,TRAN_CODE,ICD_CM_1,ICD_CM_2,ICD_CM_3,ICD_CM_4,ICD_CM_5,ICD_OP_CODE1,\r\n"
            + " a.MR_ID AS SN, 1 as QWEIGHT, b.HOSP_ID,b.FEE_YM,a.MED_DOT, a.PART_DOT, a.APPL_DOT, a.NON_APPL_DOT, "
            + " (a.E_BED_DAY + a.S_BED_DAY) AS BED_DAY, a.NB_BIRTHDAY, a.ID_BIRTH_YMD \r\n" // QWEIGHT = 權重
            + "From IP_D a left Join IP_T b on (b.ID= a.IPT_ID)\r\n" + "Where (a.ROC_ID='%s')\r\n"
            + "  and (a.IN_DATE='%s')\r\n" + "Order by OUT_DATE DESC";
    sql = String.format(sql, idCard, in_date);
    logger.debug(sql);
    java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
    return lst;
  }

  public java.util.List<Map<String, Object>> findIPDByMrId(String mrId) {
    String sql;
    sql =
        "Select CARD_SEQ_NO,ROC_ID,ID_BIRTH_YMD,IN_DATE,OUT_DATE,TRAN_CODE,ICD_CM_1,ICD_CM_2,ICD_CM_3,ICD_CM_4,ICD_CM_5,ICD_OP_CODE1,\r\n"
            + " a.MR_ID AS SN, 1 as QWEIGHT, b.HOSP_ID,b.FEE_YM,a.MED_DOT, a.PART_DOT, a.APPL_DOT, a.NON_APPL_DOT\r\n" // QWEIGHT = 權重
            + "From IP_D a left Join IP_T b on (b.ID= a.IPT_ID)\r\n" + "Where (a.MR_ID=%s) "
            + "Order by OUT_DATE DESC";
    sql = String.format(sql, mrId);
    logger.debug(sql);
    java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
    return lst;
  }

  public java.util.Map<String, Object> findOne(String tb, Map<String, Object> condition) {
    int whereCnt = 0;
    String sql;
    sql = "Select * \nFrom " + tb + " \nWhere (1=1)\n";
    for (java.util.Map.Entry<String, Object> entry : condition.entrySet()) {
      if (entry.getValue() != null) {
        sql += String.format("and (%s = '%s')\n", entry.getKey(), entry.getValue());
        whereCnt++;
      }
    }

    java.util.Map<String, Object> retMap = null;
    if (whereCnt > 0) {
      logger.debug(sql);
      java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
      if (lst.size() > 0) {
        retMap = lst.get(0);
      }
    }
    if (retMap == null) {
      retMap = new java.util.HashMap<String, Object>();
    }

    return retMap;
  }

  public int add_DRG_CAL(long mr_id, String icd_cm, String icd_op, long med_dot, String drg,
      String cc, String error, String drg_section, int drg_fix, int drg_dots) {
    String sql;
    sql = "Insert into \r\n"
        + "DRG_CAL(MR_ID, ICD_CM_1, ICD_OP_CODE1, DRG, MED_DOT, CC, ERROR, DRG_SECTION, DRG_FIX, DRG_DOT)\r\n"
        + "Values(%d, '%s', '%s', '%s', %d, '%s', '%s', '%s', %d, %d)";
    sql = String.format(sql, mr_id, icd_cm, icd_op, drg, med_dot, cc, error, drg_section, drg_fix,
        drg_dots);
    logger.debug(sql);
    int ret = jdbcTemplate.update(sql);
    return ret;
  }
  
  public void addDrgCal(DRG_CAL drg) {
    drgCalDao.save(drg);
  }

  public int del_DRG_CAL(long mr_id) {
    String sql;
    sql = "Delete from DRG_CAL\r\n" + "Where (MR_ID=%d)";
    sql = String.format(sql, mr_id);
    logger.debug(sql);
    int ret = jdbcTemplate.update(sql);
    return ret;
  }


  public float getDrgRw(String drg_code, String in_date, String out_date) {
    float ret = 1f;
    if (drg_code.length() > 0) {
      String sql;
      sql = "Select RW \r\n" + "From DRG_CODE \r\n" + "Where (CODE='%s')\r\n"
          + "  and ('%s' >= START_DATE)\r\n" + "  and ('%s' <= END_DATE)";
      sql = String.format(sql, drg_code, transDateStr(in_date), transDateStr(out_date));
      logger.debug(sql);
      java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
      if (lst.size() > 0) {
        java.util.Map<String, Object> map = lst.get(0);
        return (float) ((double) map.get("RW"));
      }
    }
    return ret;
  }

  public double strToDouble(String paStr, double defval) {
    if (paStr == null) {
      return (defval);
    } else {
      try {
        return (Double.parseDouble(paStr));
      } catch (Exception e) {
        return (defval);
      }
    }
  }

  public String transDateStr(String dateStr) {
    String ret;
    if (dateStr.length() == 8) {
      ret = String.format("%s-%s-%s", dateStr.substring(0, 4), dateStr.substring(4, 6),
          dateStr.substring(6, 8));
    } else {
      ret = dateStr;
    }
    return ret;
  }

  // ------
  public long newTableId_l(String tbName, String fdName) {
    long lastID = 0;
    String s1;
    s1 = "Select Max(" + fdName + ") as lastid \n" + "From " + tbName;
    try {
      lastID = jdbcTemplate.queryForObject(s1, Long.class);
    } catch (java.lang.NullPointerException e) {
      lastID = 0;
    }
    return (lastID + 1);
  }

  public long addLogData(String tableName, String fields, String values, String user, int mode) {
    long newId = 0;
    String sql, s1;
    sql = "Insert Into LOG_DATA1\r\n"
        + "(ID, TABLE_NAME, FIELD, VALUE, USERNAME, MODE, UPDATE_TM)\r\n"
        + "Values(%d, '%s', '%s', '%s', '%s', %d, CURRENT_TIMESTAMP)";
    for (int a = 0; a < 50; a++) {
      newId = newTableId_l("LOG_DATA1", "ID");
      s1 = String.format(sql, newId, tableName, fields, values, user, mode);
      try {
        int ret = jdbcTemplate.update(s1);
        if (ret > 0) {
          break;
        }
      } catch (DataAccessException ex) {
        //
      }
      Utility.sleep(10);
    }
    return newId;
  }

  /*
   * public long addLogData2(String tableName, String fields, String values, String user, int mode)
   * { String sql; sql = "Insert Into LOG_DATA1\r\n" +
   * "(TABLE_NAME, FIELD, VALUE, USERNAME, MODE, UPDATE_TM)\r\n" +
   * "Values('%s', '%s', '%s', '%s', %d, CURRENT_TIMESTAMP)"; sql = String.format(sql, tableName,
   * fields, values, user, mode); long ret = jdbcTemplate.update(sql); if (ret>0) { sql =
   * "Select ID\r\n" + "From LOG_DATA1\r\n" + "Where (TABLE_NAME='%s')and(USERNAME='%s')\r\n" +
   * "Order By UPDATE_TM DESC\r\n" + "Limit 1"; sql = String.format(sql, tableName, user);
   * java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper()); if
   * (lst.size()>0) { java.util.Map<String, Object> map = lst.get(0); ret = (long)map.get("ID"); } }
   * return ret; }
   */

  public int addLogDataDetail(long mid, String field, String original, String correct, int equal) {
    String sql;
    sql = "Insert into \r\n" + "LOG_DATA2(M_ID, FIELD, ORIGINAL, CORRECT, EQUAL)\r\n"
        + "Values(%d, %s, %s, %s, %d)";
    sql = String.format(sql, mid, Utility.quotedNotNull(field), Utility.quotedNotNull(original),
        Utility.quotedNotNull(correct), equal);
    try {
      int ret = jdbcTemplate.update(sql);
      return ret;
    } catch (DataAccessException ex) {
      return 0;
    }
  }


  public int addSignin(String uid, Long userId,String jwt) {
    String sql;
    sql = "Insert into \r\n" + "LOG_SIGNIN(USERNAME, JWT)\r\n" + "Values ('%s', '%s')";
    sql = String.format(sql, uid, jwt);
    try {
      int ret = jdbcTemplate.update(sql);
      return ret;
    } catch (DataAccessException ex) {
      return 0;
    }
  }

  public int updateSignout(String jwt) {
    String sql;
    sql = "Update LOG_SIGNIN\r\n" + "Set LOGOUT_TM=CURRENT_TIMESTAMP\r\n"
        + "Where (JWT='%s')";
    sql = String.format(sql, jwt);
    //System.out.println("updateSignout:" + sql);
    try {
      int ret = jdbcTemplate.update(sql);
      return ret;
    } catch (DataAccessException ex) {
      return 0;
    }
  }
  
  public int updateSignout(String jwt, String logoutTime) {
    String sql;
    sql = "Update LOG_SIGNIN\r\n" + "Set LOGOUT_TM='%s'\r\n"
        + "Where (JWT='%s')";
    sql = String.format(sql, logoutTime, jwt);
    try {
      int ret = jdbcTemplate.update(sql);
      return ret;
    } catch (DataAccessException ex) {
      return 0;
    }
  }


  // ===
  public long addLogSearch(String user) {
    long newId = 0;
    String sql, s1;
    sql = "Insert into \r\n" + "log_search1(USERNAME, UPDATE_TM)\r\n"
        + "Values('%s', CURRENT_TIMESTAMP)";
    for (int a = 0; a < 50; a++) {
      newId = newTableId_l("LOG_SEARCH1", "ID");
      s1 = String.format(sql, newId, user);
      try {
        int ret = jdbcTemplate.update(s1);
        if (ret > 0) {
          break;
        }
        Utility.sleep(10);
      } catch (DataAccessException ex) {
        //
      }
    }
    return newId;
  }

  public int addLogSearchDetail(long mid, String field, String keyword) {
    int ret = -1;
    String sql;
    if (keyword != null && keyword.length() > 0) {
      sql = "Insert into " + "log_search2(M_ID, FIELD, KEYWORD) " + "Values(%d, '%s', '%s')";
      sql = String.format(sql, mid, field, keyword);
      try {
        ret = jdbcTemplate.update(sql);
      } catch (DataAccessException ex) {
        //
      }
    }
    return ret;
  }

  /* HANA 不支援一次寫多筆 --------- */
  /*
   * public int addLogDataDetailAll(long mid, java.util.Set<Map<String, Object>> lstData) { String
   * field, original, correct, str; int idx=0; int equal; StringBuffer strBuf = new StringBuffer();
   * strBuf.append("Insert into\n");
   * strBuf.append("LOG_DATA2(M_ID, FIELD, ORIGINAL, CORRECT, EQUAL)\n"); strBuf.append("Values ");
   * for (Map<String, Object> item : lstData) { field = getMapStr(item, "field"); original =
   * getMapStr(item, "source"); correct = getMapStr(item, "modify"); equal = (int)item.get("equal");
   * str = String.format("(%d, %s, %s, %s, %d)\n", mid, quotedNotNull(field),
   * quotedNotNull(original), quotedNotNull(correct), equal); if (idx==0) { strBuf.append(str); }
   * else { strBuf.append(","+str); } idx++; } System.out.println("--------------------------");
   * System.out.println(strBuf.toString()); try { int ret = jdbcTemplate.update(strBuf.toString());
   * return ret; } catch(DataAccessException ex) { return 0; } }
   */
  /**
   * 取得IP_D 的申報點數、不申報點數及部份負擔點數，修正MR table 的 T_DOT 欄位值
   * 
   * @param idCard
   * @param in_date
   * @return
   */
  public List<Map<String, Object>> find_IPD_Dot() {
    String sql = "SELECT APPL_DOT, NON_APPL_DOT, PART_DOT, MR_ID FROM IP_D ORDER BY MR_ID";
    // logger.debug(sql);
    java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
    return lst;
  }

  /**
   * 更新MR T_DOT 點數.
   * 
   * @param tDot
   * @param mrId
   * @return
   */
  public int updateMRTDot(Integer tDot, Long mrId) {
    String sql = "UPDATE MR SET T_DOT = %d WHERE ID = %d";
    sql = String.format(sql, tDot, mrId);
    int result = jdbcTemplate.update(sql);
    return result;
  }

}
