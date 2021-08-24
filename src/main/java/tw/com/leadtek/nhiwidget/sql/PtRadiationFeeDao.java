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
        sql = "Select PT_ID, NOTIFY_NHI_NO, EXCLUDE_NHI_NO, COEXIST_NHI_NO, MAX_INPATIENT\r\n"
                + "From PT_RADIATION_FEE\r\n"
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
        sql = "Delete from PT_RADIATION_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public int add(long ptId, int notify_nhi_no, int exclude_nhi_no, int coexist_nhi_no, int max_inpatient) {
        String sql;
        sql = "Insert into \r\n"
                + "PT_RADIATION_FEE(PT_ID, NOTIFY_NHI_NO, EXCLUDE_NHI_NO, COEXIST_NHI_NO, MAX_INPATIENT)\r\n"
                + "Values(%d, %d, %d, %d, %d)";
        sql = String.format(sql, ptId, notify_nhi_no, exclude_nhi_no, coexist_nhi_no, max_inpatient);
        logger.info(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int notify_nhi_no, int exclude_nhi_no, int coexist_nhi_no, int max_inpatient) {
        String sql;
        sql = "Update PT_RADIATION_FEE\r\n"
                + "Set NOTIFY_NHI_NO=%d, \r\n"
                + "    EXCLUDE_NHI_NO=%d, \r\n"
                + "    COEXIST_NHI_NO=%d, \r\n"
                + "    MAX_INPATIENT=%d\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, notify_nhi_no, exclude_nhi_no, coexist_nhi_no, max_inpatient, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
