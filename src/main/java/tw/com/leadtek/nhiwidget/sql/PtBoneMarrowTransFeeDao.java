package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PtBoneMarrowTransFeeDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;
        sql = "Select PT_ID, COEXIST_NHI_NO, NOT_ALLOW_PLAN, LIM_DIVISION\r\n"
                + "From PT_BONE_MARROW_TRANS_FEE\r\n"
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
        sql = "Delete from PT_BONE_MARROW_TRANS_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public int add(long ptId, int coexist_nhi_no, int not_allow_plan, int lim_division) {
        String sql;
        sql = "Insert into \r\n"
                + "PT_BONE_MARROW_TRANS_FEE(PT_ID, COEXIST_NHI_NO, NOT_ALLOW_PLAN, LIM_DIVISION)\r\n"
                + "Values(%d, %d, %d, %d)";
        sql = String.format(sql, ptId, coexist_nhi_no, not_allow_plan, lim_division);
        logger.info(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int coexist_nhi_no, int not_allow_plan, int lim_division) {
        String sql;
        sql = "Update PT_BONE_MARROW_TRANS_FEE\r\n"
                + "Set COEXIST_NHI_NO=%d, \r\n"
                + "    NOT_ALLOW_PLAN=%d, \r\n"
                + "    LIM_DIVISION=%d\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, coexist_nhi_no, not_allow_plan, lim_division, ptId);
        logger.info(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
