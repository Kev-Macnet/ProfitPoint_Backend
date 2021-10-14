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
        sql = "Select PT_ID, EXCLUDE_NHI_NO_ENABLE, COEXIST_NHI_NO_ENABLE, MAX_INPATIENT_ENABLE, MAX_INPATIENT, MAX_DAILY_ENABLE, MAX_DAILY, EVERY_NDAY_ENABLE, EVERY_NDAY_DAYS, EVERY_NDAY_TIMES, PATIENT_NDAY_ENABLE, PATIENT_NDAY_DAYS, PATIENT_NDAY_TIMES, MAX_PATIENT_ENABLE, MAX_PATIENT, INCLUDE_ICD_NO_ENABLE, MAX_MONTH_ENABLE, MAX_MONTH_PERCENTAGE, MAX_AGE_ENABLE, MAX_AGE, LIM_DIVISION_ENABLE\n"
                + "From PT_TREATMENT_FEE\n"
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
        sql = "Delete from PT_TREATMENT_FEE\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

    // exclude_nhi_no_enable, coexist_nhi_no_enable, max_inpatient_enable, max_inpatient, max_daily_enable, max_daily, every_nday_enable, every_nday_days, every_nday_times, patient_nday_enable, patient_nday_days, patient_nday_times, max_patient_enable, max_patient, include_icd_no_enable, max_month_enable, max_month_percentage, max_age_enable, max_age, lim_division_enable
    public int add(long ptId, int exclude_nhi_no_enable, int coexist_nhi_no_enable, int max_inpatient_enable, int max_inpatient, 
            int max_daily_enable, int max_daily, int every_nday_enable, int every_nday_days, int every_nday_times, 
            int patient_nday_enable, int patient_nday_days, int patient_nday_times, 
            int max_patient_enable, int max_patient, int include_icd_no_enable, 
            int max_month_enable, int max_month_percentage, int max_age_enable, int max_age, int lim_division_enable) {
        String sql;
        sql = "Insert into \n"
                + "PT_TREATMENT_FEE(PT_ID, EXCLUDE_NHI_NO_ENABLE, COEXIST_NHI_NO_ENABLE, MAX_INPATIENT_ENABLE, MAX_INPATIENT, MAX_DAILY_ENABLE, MAX_DAILY, EVERY_NDAY_ENABLE, EVERY_NDAY_DAYS, EVERY_NDAY_TIMES, PATIENT_NDAY_ENABLE, PATIENT_NDAY_DAYS, PATIENT_NDAY_TIMES, MAX_PATIENT_ENABLE, MAX_PATIENT, INCLUDE_ICD_NO_ENABLE, MAX_MONTH_ENABLE, MAX_MONTH_PERCENTAGE, MAX_AGE_ENABLE, MAX_AGE, LIM_DIVISION_ENABLE)\n"
                + "Values(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d)";
        sql = String.format(sql, ptId, exclude_nhi_no_enable, coexist_nhi_no_enable, max_inpatient_enable, max_inpatient, max_daily_enable, max_daily, every_nday_enable, every_nday_days, every_nday_times, patient_nday_enable, patient_nday_days, patient_nday_times, max_patient_enable, max_patient, include_icd_no_enable, max_month_enable, max_month_percentage, max_age_enable, max_age, lim_division_enable);
        logger.info(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int exclude_nhi_no_enable, int coexist_nhi_no_enable, int max_inpatient_enable, int max_inpatient, 
            int max_daily_enable, int max_daily, int every_nday_enable, int every_nday_days, int every_nday_times, 
            int patient_nday_enable, int patient_nday_days, int patient_nday_times, 
            int max_patient_enable, int max_patient, int include_icd_no_enable, 
            int max_month_enable, int max_month_percentage, int max_age_enable, int max_age, int lim_division_enable) {
        String sql;
        sql = "Update PT_TREATMENT_FEE\n"
                + "Set EXCLUDE_NHI_NO_ENABLE=%d, \n"
                + "    COEXIST_NHI_NO_ENABLE=%d, \n"
                + "    MAX_INPATIENT_ENABLE=%d, \n"
                + "    MAX_INPATIENT=%d, \n"
                + "    MAX_DAILY_ENABLE=%d, \n"
                + "    MAX_DAILY=%d, \n"
                + "    EVERY_NDAY_ENABLE=%d, \n"
                + "    EVERY_NDAY_DAYS=%d, \n"
                + "    EVERY_NDAY_TIMES=%d, \n"
                + "    PATIENT_NDAY_ENABLE=%d, \n"
                + "    PATIENT_NDAY_DAYS=%d, \n"
                + "    PATIENT_NDAY_TIMES=%d, \n"
                + "    MAX_PATIENT_ENABLE=%d, \n"
                + "    MAX_PATIENT=%d, \n"
                + "    INCLUDE_ICD_NO_ENABLE=%d, \n"
                + "    MAX_MONTH_ENABLE=%d, \n"
                + "    MAX_MONTH_PERCENTAGE=%d, \n"
                + "    MAX_AGE_ENABLE=%d, \n"
                + "    MAX_AGE=%d, \n"
                + "    LIM_DIVISION_ENABLE=%d\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, exclude_nhi_no_enable, coexist_nhi_no_enable, max_inpatient_enable, max_inpatient, max_daily_enable, max_daily, every_nday_enable, every_nday_days, every_nday_times, patient_nday_enable, patient_nday_days, patient_nday_times, max_patient_enable, max_patient, include_icd_no_enable, max_month_enable, max_month_percentage, max_age_enable, max_age, lim_division_enable, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
