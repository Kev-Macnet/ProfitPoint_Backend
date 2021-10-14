package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PtInpatientFeeDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    // pt_id, max_inpatient_enable, max_inpatient, max_emergency_enable, max_emergency, max_patient_no_enable, max_patient_no, exclude_nhi_no_enable, not_allow_plan_enable, coexist_nhi_no_enable, no_coexist_enable 
    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;
        sql = "Select PT_ID, MAX_INPATIENT_ENABLE, MAX_INPATIENT, MAX_EMERGENCY_ENABLE, MAX_EMERGENCY, MAX_PATIENT_NO_ENABLE, MAX_PATIENT_NO, EXCLUDE_NHI_NO_ENABLE, NOT_ALLOW_PLAN_ENABLE, COEXIST_NHI_NO_ENABLE, NO_COEXIST_ENABLE\n"
                + "From PT_INPATIENT_FEE\n"
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
        sql = "Delete from PT_INPATIENT_FEE\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    // max_inpatient_enable, max_inpatient, max_emergency_enable, max_emergency, max_patient_no_enable, max_patient_no, exclude_nhi_no_enable, not_allow_plan_enable, coexist_nhi_no_enable, no_coexist_enable
    public int add(long ptId, int max_inpatient_enable, int max_inpatient, int max_emergency_enable, int max_emergency, 
            int max_patient_no_enable, int max_patient_no, int exclude_nhi_no_enable, int not_allow_plan_enable, 
            int coexist_nhi_no_enable, int no_coexist_enable) {
        String sql;
        sql = "Insert into \n"
                + "PT_INPATIENT_FEE(PT_ID, MAX_INPATIENT_ENABLE, MAX_INPATIENT, MAX_EMERGENCY_ENABLE, MAX_EMERGENCY, MAX_PATIENT_NO_ENABLE, MAX_PATIENT_NO, EXCLUDE_NHI_NO_ENABLE, NOT_ALLOW_PLAN_ENABLE, COEXIST_NHI_NO_ENABLE, NO_COEXIST_ENABLE)\n"
                + "Values(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d)";
        sql = String.format(sql, ptId, max_inpatient_enable, max_inpatient, max_emergency_enable, max_emergency, max_patient_no_enable, max_patient_no, exclude_nhi_no_enable, not_allow_plan_enable, coexist_nhi_no_enable, no_coexist_enable);
        logger.info(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int max_inpatient_enable, int max_inpatient, int max_emergency_enable, int max_emergency, 
            int max_patient_no_enable, int max_patient_no, int exclude_nhi_no_enable, int not_allow_plan_enable, 
            int coexist_nhi_no_enable, int no_coexist_enable) {
        String sql;
        sql = "UPDATE PT_INPATIENT_FEE \n"
                + "SET MAX_INPATIENT_ENABLE=%d, \n"
                + "    MAX_INPATIENT=%d, \n"
                + "    MAX_EMERGENCY_ENABLE=%d, \n"
                + "    MAX_EMERGENCY=%d, \n"
                + "    MAX_PATIENT_NO_ENABLE=%d, \n"
                + "    MAX_PATIENT_NO=%d, \n"
                + "    EXCLUDE_NHI_NO_ENABLE=%d, \n"
                + "    NOT_ALLOW_PLAN_ENABLE=%d, \n"
                + "    COEXIST_NHI_NO_ENABLE=%d, \n"
                + "    NO_COEXIST_ENABLE=%d \n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, max_inpatient_enable, max_inpatient, max_emergency_enable, max_emergency, max_patient_no_enable, max_patient_no, exclude_nhi_no_enable, not_allow_plan_enable, coexist_nhi_no_enable, no_coexist_enable, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
