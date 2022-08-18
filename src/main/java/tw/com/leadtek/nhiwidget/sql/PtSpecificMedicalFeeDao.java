package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PtSpecificMedicalFeeDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;
        sql = "Select PT_ID, EXCLUDE_NHI_NO_ENABLE, INTERVAL_NDAY_ENABLE, INTERVAL_NDAY, MAX_TIMES_ENABLE, MAX_TIMES\r\n"
                + "From PT_SPECIFIC_MEDICAL_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.debug(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.mapLowerCase(lst.get(0));
        } else {
            return new java.util.HashMap<String, Object>();
        }
    }
    //exclude_nhi_no, interval_nday, max_times
    public int delete(long ptId) {
        String sql;
        sql = "Delete from PT_SPECIFIC_MEDICAL_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.debug(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    //pt_id, exclude_nhi_no_enable, interval_nday_enable, interval_nday, max_times_enable, max_times
    public int add(long ptId, int exclude_nhi_no_enable, int interval_nday_enable, int interval_nday, 
            int max_times_enable, int max_times) {
        String sql;
        sql = "Insert into \r\n"
                + "PT_SPECIFIC_MEDICAL_FEE(PT_ID, EXCLUDE_NHI_NO_ENABLE, INTERVAL_NDAY_ENABLE, INTERVAL_NDAY, MAX_TIMES_ENABLE, MAX_TIMES)\r\n"
                + "Values(%d, %d, %d, %d, %d, %d)";
        sql = String.format(sql, ptId, exclude_nhi_no_enable, interval_nday_enable, interval_nday, max_times_enable, max_times);
        logger.debug(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int exclude_nhi_no_enable, int interval_nday_enable, int interval_nday, 
            int max_times_enable, int max_times) {
        String sql;
        sql = "Update PT_SPECIFIC_MEDICAL_FEE\r\n"
                + "Set EXCLUDE_NHI_NO_ENABLE=%d, \r\n"
                + "    INTERVAL_NDAY_ENABLE=%d, \r\n"
                + "    INTERVAL_NDAY=%d, \r\n"
                + "    MAX_TIMES_ENABLE=%d, \r\n"
                + "    MAX_TIMES=%d \r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, exclude_nhi_no_enable, interval_nday_enable, interval_nday, max_times_enable, max_times, ptId);
        logger.debug(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
