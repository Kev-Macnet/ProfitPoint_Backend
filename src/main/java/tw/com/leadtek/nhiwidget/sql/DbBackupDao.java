package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class DbBackupDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    
    public java.util.List<Map<String, Object>> findAll(String tableName) {
        String sql;
        sql = "Select *\n"
                + "From %s";
        sql = String.format(sql, tableName);
        logger.info(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        return Utility.listLowerCase(lst);
    }
    
    public java.util.List<Map<String, Object>> findData(String tableName, String idName, long minId, long maxId, String updateName, java.util.Date minUpdate) {
        long yesterday = new java.util.Date().getTime()-(86400*1000);
        String stopUpdate = Utility.dateFormat(new java.util.Date(yesterday), "yyyy-MM-dd");
        String startUpdate = Utility.dateFormat(minUpdate, "yyyy-MM-dd");
//        System.out.println("stopUpdate="+stopUpdate);
        String sql;
        sql = "Select *\n"
                + "From %s\n"
                + "Where (%s between %d and %d)\n"
                + " -- UPDATE_AT and (%s >= '%s')\n"
                + "  and (%s <= '%s')\n"
                + "Order By %s";
        sql = String.format(sql, tableName, idName, minId, maxId, updateName, startUpdate, updateName, stopUpdate, idName);
        if (minUpdate.getTime()>0l) {
            sql = sql.replace("-- UPDATE_AT and (", " and (");
        }
//        logger.info(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        return listFormatDate(lst);
    }
    
    public java.util.List<Map<String, Object>> findAll(String tableName, String idName) {
        String sql;
        sql = "Select *\n"
                + "From %s\n"
                + "Order By %s";
        sql = String.format(sql, tableName, idName);
//        logger.info(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        return listFormatDate(lst);
    }

    public java.util.Map<String, Long> getTableIdRange(String tableName, String fieldName) {
        String sql;
        sql = "Select min(%s) AS min_id, max(%s) AS max_id \n"
                + "From %s\n";
        sql = String.format(sql, fieldName, fieldName, tableName);
        logger.info(sql);
        java.util.Map<String, Long> retMap = new java.util.HashMap<String, Long>();
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            java.util.Map<String, Object> map = (Map<String, Object>)lst.get(0);
            retMap.put("min_id", (long)map.get("min_id"));
            retMap.put("max_id", (long)map.get("max_id"));
        } else {
            retMap.put("min_id", 0l);
            retMap.put("max_id", 0l);
        }
        return retMap;
   }
    
    public java.util.List<Map<String,Object>> listFormatDate(java.util.List<Map<String,Object>> sourceList) {
        java.util.List<Map<String,Object>> retList = new java.util.LinkedList<Map<String,Object>>();
        for (Map<String, Object> item : sourceList) {
            retList.add(mapFormatDate(item));
        }
        return retList;
    }
    
    public java.util.Map<String,Object> mapFormatDate(java.util.Map<String,Object> sourceMap) {
        java.util.Map<String, Object> retMap = new java.util.LinkedHashMap<String, Object>();
        for (java.util.Map.Entry<String, Object> entry : sourceMap.entrySet()) {
            String key = entry.getKey();
            key = key.toLowerCase();
            if (entry.getValue()!=null) {
                String sName = "";
                sName = entry.getValue().getClass().getSimpleName();
                if (sName.equals("Timestamp")) {
                    java.sql.Timestamp tm = (java.sql.Timestamp)entry.getValue();
                    retMap.put(key, Utility.dateFormat(tm, "yyyy-MM-dd HH:mm:ss"));
                } else if (sName.equals("Date")) {
                    java.sql.Date tm = (java.sql.Date)entry.getValue();
                    retMap.put(key, Utility.dateFormat(tm, "yyyy-MM-dd"));
                }  else {
                    retMap.put(key, entry.getValue());
                }
            } else {
                retMap.put(key,  null);
            }
        } 
        return (retMap);
    }
    
    public int execSql(String sql) {
        int ret = 0;
        try {
            ret =  jdbcTemplate.update(sql);
        } catch(DataAccessException ex) {
            logger.info("execSql------");
            logger.info(sql);
//            System.out.println("sql-122="+sql);
            ex.printStackTrace();
        }
        return ret;
    }
    
    /*
    public int delete(long ptId) {
        String sql;
        sql = "Delete from PT_MEDICINE_FEE\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public int add(long ptId, int max_nday) {
        String sql;
        sql = "Insert into \n"
                + "PT_MEDICINE_FEE(PT_ID, MAX_NDAY)\n"
                + "Values(%d, %d)";
        sql = String.format(sql, ptId, max_nday);
        logger.info(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int max_nday) {
        String sql;
        sql = "Update PT_MEDICINE_FEE\n"
                + "Set MAX_NDAY=%d\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, max_nday, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

    */
}
