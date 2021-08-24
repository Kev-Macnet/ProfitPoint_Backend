package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PtAnesthesiaFeeDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;
        sql = "Select PT_ID, INCLUDE_DRG_NO, COEXIST_NHI_NO, OVER_TIMES, OVER_TIMES_N, OVER_TIMES_FIRST_N, OVER_TIMES_NEXT_N, LIM_DIVISION\r\n"
                + "From PT_ANESTHESIA_FEE\r\n"
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
        sql = "Delete from PT_ANESTHESIA_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public int add(long ptId, int include_drg_no, int coexist_nhi_no, int over_times, int over_times_n, int over_times_first_n, int over_times_next_n, int lim_division) {
        String sql;
        sql = "Insert into \r\n"
                + "PT_ANESTHESIA_FEE(PT_ID, INCLUDE_DRG_NO, COEXIST_NHI_NO, OVER_TIMES, OVER_TIMES_N, OVER_TIMES_FIRST_N, OVER_TIMES_NEXT_N, LIM_DIVISION)\r\n"
                + "Values(%d, %d, %d, %d, %d, %d, %d, %d)";
        sql = String.format(sql, ptId, include_drg_no, coexist_nhi_no, over_times, over_times_n, over_times_first_n, over_times_next_n, lim_division);
        logger.info(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int include_drg_no, int coexist_nhi_no, int over_times, int over_times_n, int over_times_first_n, int over_times_next_n, int lim_division) {
        String sql;
        sql = "Update PT_ANESTHESIA_FEE\r\n"
                + "Set INCLUDE_DRG_NO=%d, \r\n"
                + "    COEXIST_NHI_NO=%d, \r\n"
                + "    OVER_TIMES=%d, \r\n"
                + "    OVER_TIMES_N=%d, \r\n"
                + "    OVER_TIMES_FIRST_N=%d, \r\n"
                + "    OVER_TIMES_NEXT_N=%d, \r\n"
                + "    LIM_DIVISION=%d\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, include_drg_no, coexist_nhi_no, over_times, over_times_n, over_times_first_n, over_times_next_n, lim_division, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
