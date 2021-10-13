package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PtMedicineFeeDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;
        sql = "Select PT_ID, MAX_NDAY_ENABLE, MAX_NDAY\r\n"
                + "From PT_MEDICINE_FEE\r\n"
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
        sql = "Delete from PT_MEDICINE_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    // pt_id, max_nday_enable, max_nday
    public int add(long ptId, int max_nday_enable, int max_nday) {
        String sql;
        sql = "Insert into \r\n"
                + "PT_MEDICINE_FEE(PT_ID, MAX_NDAY_ENABLE, MAX_NDAY)\r\n"
                + "Values(%d, %d, %d)";
        sql = String.format(sql, ptId, max_nday_enable, max_nday);
        logger.info(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int max_nday_enable, int max_nday) {
        String sql;
        sql = "Update PT_MEDICINE_FEE\r\n"
                + "Set MAX_NDAY_ENABLE=%d, \r\n"
                + "    MAX_NDAY=%d\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, max_nday_enable, max_nday, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
