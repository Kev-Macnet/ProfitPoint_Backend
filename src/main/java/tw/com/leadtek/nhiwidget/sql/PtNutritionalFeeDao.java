package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PtNutritionalFeeDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;
        sql = "Select PT_ID, MAX_INPATIENT_ENABLE, MAX_INPATIENT, MAX_DAILY_ENABLE, MAX_DAILY, EVERY_NDAY_ENABLE, EVERY_NDAY_DAYS, EVERY_NDAY_TIMES, OVER_NDAY_ENABLE, OVER_NDAY_DAYS, OVER_NDAY_TIMES, EXCLUDE_NHI_NO_ENABLE\r\n"
                + "From PT_NUTRITIONAL_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.trace(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.mapLowerCase(lst.get(0));
        } else {
            return new java.util.HashMap<String, Object>();
        }
    }
    
    public int delete(long ptId) {
        String sql;
        sql = "Delete from PT_NUTRITIONAL_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.trace(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    
    //pt_id, max_inpatient_enable, max_inpatient, max_daily_enable, max_daily, every_nday_enable, every_nday_days, every_nday_times, over_nday_enable, over_nday_days, over_nday_times, exclude_nhi_no_enable
    public int add(long ptId, int max_inpatient_enable, int max_inpatient, int max_daily_enable, int max_daily, 
            int every_nday_enable, int every_nday_days, int every_nday_times, 
            int over_nday_enable, int over_nday_days, int over_nday_times, int exclude_nhi_no_enable) {
        String sql;
        sql = "Insert into \r\n"
                + "PT_NUTRITIONAL_FEE(PT_ID, MAX_INPATIENT_ENABLE, MAX_INPATIENT, MAX_DAILY_ENABLE, MAX_DAILY, EVERY_NDAY_ENABLE, EVERY_NDAY_DAYS, EVERY_NDAY_TIMES, OVER_NDAY_ENABLE, OVER_NDAY_DAYS, OVER_NDAY_TIMES, EXCLUDE_NHI_NO_ENABLE)\r\n"
                + "Values(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d)";
        sql = String.format(sql, ptId, max_inpatient_enable, max_inpatient, max_daily_enable, max_daily, every_nday_enable, every_nday_days, every_nday_times, over_nday_enable, over_nday_days, over_nday_times, exclude_nhi_no_enable);
        logger.trace(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int max_inpatient_enable, int max_inpatient, int max_daily_enable, int max_daily, 
            int every_nday_enable, int every_nday_days, int every_nday_times, 
            int over_nday_enable, int over_nday_days, int over_nday_times, int exclude_nhi_no_enable) {
        String sql;
        sql = "Update PT_NUTRITIONAL_FEE\r\n"
                + "Set MAX_INPATIENT_ENABLE=%d, \r\n"
                + "    MAX_INPATIENT=%d, \r\n"
                + "    MAX_DAILY_ENABLE=%d, \r\n"
                + "    MAX_DAILY=%d, \r\n"
                + "    EVERY_NDAY_ENABLE=%d, \r\n"
                + "    EVERY_NDAY_DAYS=%d, \r\n"
                + "    EVERY_NDAY_TIMES=%d, \r\n"
                + "    OVER_NDAY_ENABLE=%d, \r\n"
                + "    OVER_NDAY_DAYS=%d, \r\n"
                + "    OVER_NDAY_TIMES=%d, \r\n"
                + "    EXCLUDE_NHI_NO_ENABLE=%d \r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, max_inpatient_enable, max_inpatient, max_daily_enable, max_daily, every_nday_enable, every_nday_days, every_nday_times, over_nday_enable, over_nday_days, over_nday_times, exclude_nhi_no_enable, ptId);
        logger.trace(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
