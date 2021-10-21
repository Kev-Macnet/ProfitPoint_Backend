package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PtRadiationFeeDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;
        sql = "Select PT_ID, NOTIFY_NHI_NO_ENABLE, EXCLUDE_NHI_NO_ENABLE, COEXIST_NHI_NO_ENABLE, MAX_INPATIENT_ENABLE, MAX_INPATIENT\r\n"
                + "From PT_RADIATION_FEE\r\n"
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
        sql = "Delete from PT_RADIATION_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.trace(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

    //PT_ID, notify_nhi_no_enable, exclude_nhi_no_enable, coexist_nhi_no_enable, max_inpatient_enable, max_inpatient
    public int add(long ptId, int notify_nhi_no_enable, int exclude_nhi_no_enable, int coexist_nhi_no_enable, 
            int max_inpatient_enable, int max_inpatient) {
        String sql;
        sql = "Insert into \r\n"
                + "PT_RADIATION_FEE(PT_ID, NOTIFY_NHI_NO_ENABLE, EXCLUDE_NHI_NO_ENABLE, COEXIST_NHI_NO_ENABLE, MAX_INPATIENT_ENABLE, MAX_INPATIENT)\r\n"
                + "Values(%d, %d, %d, %d, %d, %d)";
        sql = String.format(sql, ptId, notify_nhi_no_enable, exclude_nhi_no_enable, coexist_nhi_no_enable, max_inpatient_enable, max_inpatient);
        logger.trace(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int notify_nhi_no_enable, int exclude_nhi_no_enable, int coexist_nhi_no_enable, 
            int max_inpatient_enable, int max_inpatient) {
        String sql;
        sql = "Update PT_RADIATION_FEE\r\n"
                + "Set NOTIFY_NHI_NO_ENABLE=%d, \r\n"
                + "    EXCLUDE_NHI_NO_ENABLE=%d, \r\n"
                + "    COEXIST_NHI_NO_ENABLE=%d, \r\n"
                + "    MAX_INPATIENT_ENABLE=%d, \r\n"
                + "    MAX_INPATIENT=%d \r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, notify_nhi_no_enable, exclude_nhi_no_enable, coexist_nhi_no_enable, max_inpatient_enable, max_inpatient, ptId);
        logger.trace(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
