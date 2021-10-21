package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PtOthersFeeDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;

        sql = "Select PT_ID, EXCLUDE_NHI_NO_ENABLE, MAX_INPATIENT_ENABLE, MAX_INPATIENT, MAX_TIMES_ENABLE, MAX_TIMES, INTERVAL_NDAY_ENABLE, INTERVAL_NDAY, PATIENT_NDAY_ENABLE, PATIENT_NDAY_DAYS, PATIENT_NDAY_TIMES\r\n"
                + "From PT_OTHERS_FEE\r\n"
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
        sql = "Delete from PT_OTHERS_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.trace(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    //pt_id, exclude_nhi_no_enable, max_inpatient_enable, max_inpatient, max_times_enable, max_times, interval_nday_enable, interval_nday, patient_nday_enable, patient_nday_days, patient_nday_times
    public int add(long ptId, int exclude_nhi_no_enable, int max_inpatient_enable, int max_inpatient, int max_times_enable, int max_times, 
            int interval_nday_enable, int interval_nday, int patient_nday_enable, int patient_nday_days, int patient_nday_times) {
        String sql;
        sql = "Insert into \r\n"
                + "PT_OTHERS_FEE(PT_ID, EXCLUDE_NHI_NO_ENABLE, MAX_INPATIENT_ENABLE, MAX_INPATIENT, MAX_TIMES_ENABLE, MAX_TIMES, INTERVAL_NDAY_ENABLE, INTERVAL_NDAY, PATIENT_NDAY_ENABLE, PATIENT_NDAY_DAYS, PATIENT_NDAY_TIMES)\r\n"
                + "VALUES(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d)";
        sql = String.format(sql, ptId, exclude_nhi_no_enable, max_inpatient_enable, max_inpatient, max_times_enable, max_times, 
                            interval_nday_enable, interval_nday, patient_nday_enable, patient_nday_days, patient_nday_times);
        logger.trace(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int exclude_nhi_no_enable, int max_inpatient_enable, int max_inpatient, int max_times_enable, int max_times, 
            int interval_nday_enable, int interval_nday, int patient_nday_enable, int patient_nday_days, int patient_nday_times) {
        String sql;
        sql = "Update PT_OTHERS_FEE\r\n"
                + "Set EXCLUDE_NHI_NO_ENABLE=%d, \r\n"
                + "    MAX_INPATIENT_ENABLE=%d, \r\n"
                + "    MAX_INPATIENT=%d, \r\n"
                + "    MAX_TIMES_ENABLE=%d, \r\n"
                + "    MAX_TIMES=%d, \r\n"
                + "    INTERVAL_NDAY_ENABLE=%d, \r\n"
                + "    INTERVAL_NDAY=%d, \r\n"
                + "    PATIENT_NDAY_ENABLE=%d, \r\n"
                + "    PATIENT_NDAY_DAYS=%d, \r\n"
                + "    PATIENT_NDAY_TIMES=%d\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, exclude_nhi_no_enable, max_inpatient_enable, max_inpatient, max_times_enable, max_times, 
                interval_nday_enable, interval_nday, patient_nday_enable, patient_nday_days, patient_nday_times, ptId);
        logger.trace(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
