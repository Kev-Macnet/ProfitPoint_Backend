package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PtRehabilitationFeeDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;
        sql = "Select PT_ID, EXCLUDE_NHI_NO_ENABLE, PATIENT_NDAY_ENABLE, PATIENT_NDAY_DAYS, PATIENT_NDAY_TIMES, INCLUDE_ICD_NO_ENABLE, COEXIST_NHI_NO_ENABLE, MIN_COEXIST_ENABLE, MIN_COEXIST, LIM_DIVISION_ENABLE\r\n"
                + "From PT_REHABILITATION_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.mapLowerCase(lst.get(0));
        } else {
            return java.util.Collections.emptyMap();
        }
    }
    
    public int delete(long ptId) {
        String sql;
        sql = "Delete from PT_REHABILITATION_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    
    // pt_id, exclude_nhi_no_enable, patient_nday_enable, patient_nday_days, patient_nday_times, include_icd_no_enable, coexist_nhi_no_enable, min_coexist_enable, min_coexist, lim_division_enable
    public int add(long ptId, int exclude_nhi_no_enable, int patient_nday_enable, int patient_nday_days, int patient_nday_times, 
            int include_icd_no_enable, int coexist_nhi_no_enable, int min_coexist_enable, int min_coexist, int lim_division_enable) {
        String sql;
        sql = "Insert into\r\n"
                + "PT_REHABILITATION_FEE(PT_ID, EXCLUDE_NHI_NO_ENABLE, PATIENT_NDAY_ENABLE, PATIENT_NDAY_DAYS, PATIENT_NDAY_TIMES, INCLUDE_ICD_NO_ENABLE, COEXIST_NHI_NO_ENABLE, MIN_COEXIST_ENABLE, MIN_COEXIST, LIM_DIVISION_ENABLE)\r\n"
                + "Values(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d)";
        sql = String.format(sql, ptId, exclude_nhi_no_enable, patient_nday_enable, patient_nday_days, patient_nday_times, include_icd_no_enable, coexist_nhi_no_enable, min_coexist_enable, min_coexist, lim_division_enable);
        logger.info(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int exclude_nhi_no_enable, int patient_nday_enable, int patient_nday_days, int patient_nday_times, 
            int include_icd_no_enable, int coexist_nhi_no_enable, int min_coexist_enable, int min_coexist, int lim_division_enable) {
        String sql;
        sql = "UPDATE PT_REHABILITATION_FEE\r\n"
                + "SET EXCLUDE_NHI_NO_ENABLE=%d, \r\n"
                + "    PATIENT_NDAY_ENABLE=%d, \r\n"
                + "    PATIENT_NDAY_DAYS=%d, \r\n"
                + "    PATIENT_NDAY_TIMES=%d, \r\n"
                + "    INCLUDE_ICD_NO_ENABLE=%d, \r\n"
                + "    COEXIST_NHI_NO_ENABLE=%d, \r\n"
                + "    MIN_COEXIST_ENABLE=%d, \r\n"
                + "    MIN_COEXIST=%d, \r\n"
                + "    LIM_DIVISION_ENABLE=%d\r\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, exclude_nhi_no_enable, patient_nday_enable, patient_nday_days, patient_nday_times, include_icd_no_enable, 
                coexist_nhi_no_enable, min_coexist_enable, min_coexist, lim_division_enable, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
