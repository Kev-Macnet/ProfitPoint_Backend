package tw.com.leadtek.nhiwidget.sql;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PtQualityServiceDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;
        sql = "Select PT_ID, INTERVAL_NDAY_ENABLE, INTERVAL_NDAY, COEXIST_NHI_NO_ENABLE, MIN_COEXIST_ENABLE, MIN_COEXIST, EVERY_NDAY_ENABLE, EVERY_NDAY_DAYS, EVERY_NDAY_TIMES\r\n"
                + "From PT_QUALITY_SERVICE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.mapLowerCase(lst.get(0));
        } else {
            return Collections.emptyMap();
        }
    }
    
    public int delete(long ptId) {
        String sql;
        sql = "Delete from PT_QUALITY_SERVICE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    // interval_nday_enable, interval_nday, coexist_nhi_no_enable, min_coexist_enable, min_coexist, every_nday_enable, every_nday_days, every_nday_times
    public int add(long ptId, int interval_nday_enable, int interval_nday, int coexist_nhi_no_enable, 
            int min_coexist_enable, int min_coexist, int every_nday_enable, int every_nday_days, int every_nday_times) {
        String sql;
        sql = "Insert into \r\n"
                + "PT_QUALITY_SERVICE(PT_ID, INTERVAL_NDAY_ENABLE, INTERVAL_NDAY, COEXIST_NHI_NO_ENABLE, MIN_COEXIST_ENABLE, MIN_COEXIST, EVERY_NDAY_ENABLE, EVERY_NDAY_DAYS, EVERY_NDAY_TIMES)\r\n"
                + "Values(%d, %d, %d, %d, %d, %d, %d, %d, %d)";
        sql = String.format(sql, ptId, interval_nday_enable, interval_nday, coexist_nhi_no_enable, min_coexist_enable, min_coexist, every_nday_enable, every_nday_days, every_nday_times);
        logger.info(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int interval_nday_enable, int interval_nday, int coexist_nhi_no_enable, 
            int min_coexist_enable, int min_coexist, int every_nday_enable, int every_nday_days, int every_nday_times) {
        String sql;
        sql = "Update PT_QUALITY_SERVICE\r\n"
                + "Set INTERVAL_NDAY_ENABLE=%d, \r\n"
                + "    INTERVAL_NDAY=%d, \r\n"
                + "    COEXIST_NHI_NO_ENABLE=%d, \r\n"
                + "    MIN_COEXIST_ENABLE=%d, \r\n"
                + "    MIN_COEXIST=%d, \r\n"
                + "    EVERY_NDAY_ENABLE=%d, \r\n"
                + "    EVERY_NDAY_DAYS=%d, \r\n"
                + "    EVERY_NDAY_TIMES=%d\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, interval_nday_enable, interval_nday, coexist_nhi_no_enable, min_coexist_enable, min_coexist, every_nday_enable, every_nday_days, every_nday_times, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
