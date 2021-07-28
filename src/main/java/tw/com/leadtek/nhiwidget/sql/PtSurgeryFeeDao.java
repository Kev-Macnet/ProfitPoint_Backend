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
public class PtSurgeryFeeDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;
        sql = "Select PT_ID, LIM_DIVISION, EXCLUDE_NHI_NO, LIM_AGE, LIM_AGE_TYPE\r\n"
                + "From PT_SURGERY_FEE\r\n"
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
        sql = "Delete from PT_SURGERY_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public int add(long ptId, int lim_division, int exclude_nhi_no, int lim_age) {
        String sql;
        sql = "Insert into \r\n"
                + "PT_SURGERY_FEE(PT_ID, LIM_DIVISION, EXCLUDE_NHI_NO, LIM_AGE, LIM_AGE_TYPE)\r\n"
                + "Values(%d, %d, %d, %d, 3)";
        sql = String.format(sql, ptId, lim_division, exclude_nhi_no, lim_age);
        logger.info(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int lim_division, int exclude_nhi_no, int lim_age) {
        String sql;
        sql = "Update PT_SURGERY_FEE\r\n"
                + "SET LIM_DIVISION=%d, \r\n"
                + "    EXCLUDE_NHI_NO=%d, \r\n"
                + "    LIM_AGE=%d\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, lim_division, exclude_nhi_no, lim_age, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
