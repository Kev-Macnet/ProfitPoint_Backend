package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PtWardFeeDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;
        sql = "Select PT_ID, MIN_STAY_ENABLE, MIN_STAY, MAX_STAY_ENABLE, MAX_STAY, EXCLUDE_NHI_NO_ENABLE\r\n"
                + "From PT_WARD_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.debug(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.mapLowerCase(lst.get(0));
        } else {
            return new java.util.HashMap<String, Object>();
        }
    }
    
    public int delete(long ptId) {
        String sql;
        sql = "Delete from PT_WARD_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.debug(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    // pt_id, min_stay_enable, min_stay, max_stay_enable, max_stay, exclude_nhi_no_enable
    public int add(long ptId, int min_stay_enable, int min_stay, int max_stay_enable, int max_stay, int exclude_nhi_no_enable) {
        String sql;
        sql = "Insert into \r\n"
                + "PT_WARD_FEE(PT_ID, MIN_STAY_ENABLE, MIN_STAY, MAX_STAY_ENABLE, MAX_STAY, EXCLUDE_NHI_NO_ENABLE)\r\n"
                + "Values(%d, %d, %d, %d, %d, %d)";
        sql = String.format(sql, ptId, min_stay_enable, min_stay, max_stay_enable, max_stay, exclude_nhi_no_enable);
        logger.debug(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int min_stay_enable, int min_stay, int max_stay_enable, int max_stay, int exclude_nhi_no_enable) {
        String sql;
        sql = "Update PT_WARD_FEE\r\n"
                + "Set MIN_STAY_ENABLE=%d, \r\n"
                + "    MIN_STAY=%d, \r\n"
                + "    MAX_STAY_ENABLE=%d, \r\n"
                + "    MAX_STAY=%d, \r\n"
                + "    EXCLUDE_NHI_NO_ENABLE=%d\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, min_stay_enable, min_stay, max_stay_enable, max_stay, exclude_nhi_no_enable, ptId);
        logger.debug(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
