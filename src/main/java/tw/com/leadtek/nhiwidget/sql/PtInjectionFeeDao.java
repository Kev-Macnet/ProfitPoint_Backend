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
public class PtInjectionFeeDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;
        sql = "Select PT_ID, INTERVAL_NDAY_ENABLE, INTERVAL_NDAY, EXCLUDE_NHI_NO_ENABLE, MAX_INPATIENT_ENABLE, MAX_INPATIENT\r\n"
                + "From PT_INJECTION_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.debug(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.mapLowerCase(lst.get(0));
        } else {
//            return new java.util.HashMap<String, Object>();
            return Collections.emptyMap();
        }
    }
    
    public int delete(long ptId) {
        String sql;
        sql = "Delete from PT_INJECTION_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.debug(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    //pt_id, interval_nday_enable, interval_nday, exclude_nhi_no_enable, max_inpatient_enable, max_inpatient
    public int add(long ptId, int interval_nday_enable, int interval_nday, int exclude_nhi_no_enable, int max_inpatient_enable, int max_inpatient) {
        String sql;
        sql = "Insert into\r\n"
                + "PT_INJECTION_FEE(PT_ID, INTERVAL_NDAY_ENABLE, INTERVAL_NDAY, EXCLUDE_NHI_NO_ENABLE, MAX_INPATIENT_ENABLE, MAX_INPATIENT)\r\n"
                + "Values(%d, %d, %d, %d, %d, %d)";
        sql = String.format(sql, ptId, interval_nday_enable, interval_nday, exclude_nhi_no_enable, max_inpatient_enable, max_inpatient);
        logger.debug(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int interval_nday_enable, int interval_nday, int exclude_nhi_no_enable, int max_inpatient_enable, int max_inpatient) {
        String sql;
        sql = "Update PT_INJECTION_FEE\r\n"
                + "Set INTERVAL_NDAY_ENABLE=%d, \r\n"
                + "    INTERVAL_NDAY=%d, \r\n"
                + "    EXCLUDE_NHI_NO_ENABLE=%d, \r\n"
                + "    MAX_INPATIENT_ENABLE=%d, \r\n"
                + "    MAX_INPATIENT=%d \r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, interval_nday_enable, interval_nday, exclude_nhi_no_enable, max_inpatient_enable, max_inpatient, ptId);
        logger.debug(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
