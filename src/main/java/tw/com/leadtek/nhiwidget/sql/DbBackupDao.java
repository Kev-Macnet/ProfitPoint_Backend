package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
        sql = "Select *\r\n"
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
        System.out.println("stopUpdate="+stopUpdate);
        String sql;
        sql = "Select *\r\n"
                + "From %s\n"
                + "Where (%s between %d and %d)\n"
                + " -- UPDATE_AT and (%s >= '%s')\n"
                + "  and (%s <= '%s')\n"
                + "Order By %s";
        sql = String.format(sql, tableName, idName, minId, maxId, updateName, startUpdate, updateName, stopUpdate, idName);
        if (minUpdate.getTime()>0l) {
            sql = sql.replace("-- UPDATE_AT and (", " and (");
        }
        logger.info(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        return Utility.listLowerCase(lst);
    }

    public java.util.Map<String, Long> getTableIdRange(String tableName, String fieldName) {
        String sql;
        sql = "Select min(%s) AS min_id, max(%s) AS max_id \r\n"
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
    
    /*
    public int delete(long ptId) {
        String sql;
        sql = "Delete from PT_MEDICINE_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public int add(long ptId, int max_nday) {
        String sql;
        sql = "Insert into \r\n"
                + "PT_MEDICINE_FEE(PT_ID, MAX_NDAY)\r\n"
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
        sql = "Update PT_MEDICINE_FEE\r\n"
                + "Set MAX_NDAY=%d\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, max_nday, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

    */
}
