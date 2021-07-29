package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PtInpatientFeeDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;
        sql = "SELECT PT_ID, MAX_INPATIENT, MAX_EMERGENCY, MAX_PATIENT_NO, EXCLUDE_NHI_NO, NOT_ALLOW_PLAN, COEXIST_NHI_NO, NO_COEXIST\r\n"
                + "FROM PT_INPATIENT_FEE\r\n"
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
        sql = "Delete from PT_INPATIENT_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public int add(long ptId, int max_inpatient, int max_emergency, int max_patient_no, int exclude_nhi_no, 
                   int not_allow_plan, int coexist_nhi_no, int no_coexist) {
        String sql;
        sql = "Insert into \r\n"
                + "PT_INPATIENT_FEE(PT_ID, MAX_INPATIENT, MAX_EMERGENCY, MAX_PATIENT_NO, EXCLUDE_NHI_NO, NOT_ALLOW_PLAN, COEXIST_NHI_NO, NO_COEXIST)\r\n"
                + "Values(%d, %d, %d, %d, %d, %d, %d, %d)";
        sql = String.format(sql, ptId, max_inpatient, max_emergency, max_patient_no, exclude_nhi_no, not_allow_plan, coexist_nhi_no, no_coexist);
        logger.info(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int max_inpatient, int max_emergency, int max_patient_no, int exclude_nhi_no, 
                      int not_allow_plan, int coexist_nhi_no, int no_coexist) {
        String sql;
        sql = "Update PT_INPATIENT_FEE\r\n"
                + "Set MAX_INPATIENT=%d, \r\n"
                + "    MAX_EMERGENCY=%d, \r\n"
                + "    MAX_PATIENT_NO=%d, \r\n"
                + "    EXCLUDE_NHI_NO=%d, \r\n"
                + "    NOT_ALLOW_PLAN=%d, \r\n"
                + "    COEXIST_NHI_NO=%d, \r\n"
                + "    NO_COEXIST=%d\r\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, max_inpatient, max_emergency, max_patient_no, exclude_nhi_no, not_allow_plan, coexist_nhi_no, no_coexist, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
