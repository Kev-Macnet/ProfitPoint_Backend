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

    // limit 語法的版本
    public java.util.List<Map<String, Object>> findData(String tableName, String idField, long startIdx, long pageSize, String updateField, java.util.Date startDate) {
        String strStartDate = Utility.dateFormat(startDate, "yyyy-MM-dd HH:mm:ss");
        String sql;
        sql= "Select *\r\n"
                + "From %s\r\n"
                + "WHERE (%s>'%s')\r\n"
                + "Order By %s\r\n"
                + "Limit %d offset %d";
        sql = String.format(sql, tableName, updateField, strStartDate, idField, pageSize, startIdx);
//        System.out.println("sql-69="+sql);
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

    public java.util.Map<String, Long> getTableIdRange(String tableName, String fieldName, String updateField, java.util.Date startDate) {
        String strStartDate = Utility.dateFormat(startDate, "yyyy-MM-dd HH:mm:ss");
        String sql;
        sql = "Select min(%s) AS min_id, max(%s) AS max_id, count(ID) AS cnt\n"
                + "From %s\n"
                + "WHERE (%s>'%s')\n";
        sql = String.format(sql, fieldName, fieldName, tableName, updateField, strStartDate);
        logger.info(sql);
        java.util.Map<String, Long> retMap = new java.util.HashMap<String, Long>();
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            java.util.Map<String, Object> map = (Map<String, Object>)lst.get(0);
            retMap.put("min_id", (long)map.get("min_id"));
            retMap.put("max_id", (long)map.get("max_id"));
            retMap.put("count", (long)map.get("cnt"));
        } else {
            retMap.put("min_id", 0l);
            retMap.put("max_id", 0l);
            retMap.put("count", 0l);
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
            logger.info("execSql Error -----");
            logger.info(sql);
            ex.printStackTrace();
        }
        return ret;
    }

}
