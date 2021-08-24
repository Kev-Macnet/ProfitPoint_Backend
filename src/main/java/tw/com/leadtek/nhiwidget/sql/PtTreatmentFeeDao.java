package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PtTreatmentFeeDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;
        sql = "Select PT_ID, EXCLUDE_NHI_NO, COEXIST_NHI_NO, MAX_INPATIENT, MAX_DAILY, EVERY_NDAY, EVERY_NDAY_DAYS, EVERY_NDAY_TIMES, PATIENT_NDAY, PATIENT_NDAY_DAYS, PATIENT_NDAY_TIMES, MAX_PATIENT, INCLUDE_ICD_NO, MAX_MONTH_PERCENTAGE, MAX_AGE, LIM_DIVISION\r\n"
                + "From PT_TREATMENT_FEE\r\n"
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
        sql = "Delete from PT_TREATMENT_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public int add(long ptId, int exclude_nhi_no, int coexist_nhi_no, int max_inpatient, int max_daily, 
                   int every_nday, int every_nday_days, int every_nday_times, 
                   int patient_nday, int patient_nday_days, int patient_nday_times, 
                   int max_patient, int include_icd_no, int max_month_percentage, int max_age, int lim_division) {
        String sql;
        sql = "Insert into \r\n"
                + "PT_TREATMENT_FEE(PT_ID, EXCLUDE_NHI_NO, COEXIST_NHI_NO, MAX_INPATIENT, MAX_DAILY, EVERY_NDAY, EVERY_NDAY_DAYS, EVERY_NDAY_TIMES, PATIENT_NDAY, PATIENT_NDAY_DAYS, PATIENT_NDAY_TIMES, MAX_PATIENT, INCLUDE_ICD_NO, MAX_MONTH_PERCENTAGE, MAX_AGE, LIM_DIVISION)\r\n"
                + "Values(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d)";
        sql = String.format(sql, ptId, exclude_nhi_no, coexist_nhi_no, max_inpatient, max_daily, every_nday, every_nday_days, every_nday_times, patient_nday, patient_nday_days, patient_nday_times, max_patient, include_icd_no, max_month_percentage, max_age, lim_division);
        logger.info(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int exclude_nhi_no, int coexist_nhi_no, int max_inpatient, int max_daily, 
            int every_nday, int every_nday_days, int every_nday_times, 
            int patient_nday, int patient_nday_days, int patient_nday_times, 
            int max_patient, int include_icd_no, int max_month_percentage, int max_age, int lim_division) {
        String sql;
        sql = "UPDATE PT_TREATMENT_FEE\r\n"
                + "SET EXCLUDE_NHI_NO=%d, \r\n"
                + "    COEXIST_NHI_NO=%d, \r\n"
                + "    MAX_INPATIENT=%d, \r\n"
                + "    MAX_DAILY=%d, \r\n"
                + "    EVERY_NDAY=%d, \r\n"
                + "    EVERY_NDAY_DAYS=%d, \r\n"
                + "    EVERY_NDAY_TIMES=%d, \r\n"
                + "    PATIENT_NDAY=%d, \r\n"
                + "    PATIENT_NDAY_DAYS=%d, \r\n"
                + "    PATIENT_NDAY_TIMES=%d, \r\n"
                + "    MAX_PATIENT=%d, \r\n"
                + "    INCLUDE_ICD_NO=%d, \r\n"
                + "    MAX_MONTH_PERCENTAGE=%d, \r\n"
                + "    MAX_AGE=%d, \r\n"
                + "    LIM_DIVISION=%d\r\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, exclude_nhi_no, coexist_nhi_no, max_inpatient, max_daily, every_nday, every_nday_days, every_nday_times, patient_nday, patient_nday_days, patient_nday_times, max_patient, include_icd_no, max_month_percentage, max_age, lim_division, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
