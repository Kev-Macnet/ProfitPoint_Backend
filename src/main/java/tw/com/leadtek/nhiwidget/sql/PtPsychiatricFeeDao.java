package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PtPsychiatricFeeDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;
        sql = "Select PT_ID, EXCLUDE_NHI_NO, PATIENT_NDAY, PATIENT_NDAY_DAYS, PATIENT_NDAY_TIMES, MAX_INPATIENT, LIM_DIVISION\r\n"
                + "From PT_PSYCHIATRIC_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.mapLowerCase(lst.get(0));
        } else {
            return new java.util.HashMap<String, Object>();
        }
    }
    
    public int delete(long ptId) {
        String sql;
        sql = "Delete from PT_PSYCHIATRIC_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public int add(long ptId, int exclude_nhi_no, int patient_nday, int patient_nday_days, int patient_nday_times, int max_inpatient, int lim_division) {
        String sql;
        sql = "Insert into \r\n"
                + "PT_PSYCHIATRIC_FEE(PT_ID, EXCLUDE_NHI_NO, PATIENT_NDAY, PATIENT_NDAY_DAYS, PATIENT_NDAY_TIMES, MAX_INPATIENT, LIM_DIVISION)\r\n"
                + "Values(%d, %d, %d, %d, %d, %d, %d)";
        sql = String.format(sql, ptId, exclude_nhi_no, patient_nday, patient_nday_days, patient_nday_times, max_inpatient, lim_division);
        logger.info(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int exclude_nhi_no, int patient_nday, int patient_nday_days, int patient_nday_times, int max_inpatient, int lim_division) {
        String sql;
        sql = "Update PT_PSYCHIATRIC_FEE\r\n"
                + "Set EXCLUDE_NHI_NO=%d, \r\n"
                + "    PATIENT_NDAY=%d, \r\n"
                + "    PATIENT_NDAY_DAYS=%d, \r\n"
                + "    PATIENT_NDAY_TIMES=%d, \r\n"
                + "    MAX_INPATIENT=%d, \r\n"
                + "    LIM_DIVISION=%d\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, exclude_nhi_no, patient_nday, patient_nday_days, patient_nday_times, max_inpatient, lim_division, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
