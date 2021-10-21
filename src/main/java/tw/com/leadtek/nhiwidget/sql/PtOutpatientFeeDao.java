package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PtOutpatientFeeDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.Map<String, Object> findOne(long ptId) {
        String sql;
        sql = "Select PT_ID, NO_DENTISIT, NO_CHI_MEDICINE, NO_SERVICE_CHARGE, LIM_OUT_ISLANDS, LIM_HOLIDAY, LIM_MAX_ENABLE, LIM_MAX, LIM_AGE_ENABLE, LIM_AGE_TYPE, LIM_AGE, LIM_DIVISION_ENABLE, EXCLUDE_NHI_NO_ENABLE\r\n"
                + "From PT_OUTPATIENT_FEE\r\n"
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
        sql = "Delete from PT_OUTPATIENT_FEE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        logger.trace(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    // pt_id, no_dentisit, no_chi_medicine, no_service_charge, lim_out_islands, lim_holiday, lim_max_enable, lim_max, lim_age_enable, lim_age_type, lim_age, lim_division_enable, exclude_nhi_no_enable
    public int add(long ptId, int no_dentisit, int no_chi_medicine, int no_service_charge, int lim_out_islands, int lim_holiday, 
            int lim_max_enable, int lim_max, int lim_age_enable, int lim_age_type, int lim_age, 
            int lim_division_enable, int exclude_nhi_no_enable) {

        String sql;
        sql = "Insert into \r\n"
                + "PT_OUTPATIENT_FEE(PT_ID, NO_DENTISIT, NO_CHI_MEDICINE, NO_SERVICE_CHARGE, LIM_OUT_ISLANDS, LIM_HOLIDAY, LIM_MAX_ENABLE, LIM_MAX, LIM_AGE_ENABLE, LIM_AGE_TYPE, LIM_AGE, LIM_DIVISION_ENABLE, EXCLUDE_NHI_NO_ENABLE)\r\n"
                + "Values(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d)";
        sql = String.format(sql, ptId, no_dentisit, no_chi_medicine, no_service_charge, lim_out_islands, lim_holiday, 
                lim_max_enable, lim_max, lim_age_enable, lim_age_type, lim_age, 
                lim_division_enable, exclude_nhi_no_enable);
        logger.trace(sql);
        try {
            int ret =  jdbcTemplate.update(sql);
            return ret;
        } catch(DataAccessException ex) {
            return -1;
        }
    }
    
    public int update(long ptId, int no_dentisit, int no_chi_medicine, int no_service_charge, int lim_out_islands, int lim_holiday, 
            int lim_max_enable, int lim_max, int lim_age_enable, int lim_age_type, int lim_age, 
            int lim_division_enable, int exclude_nhi_no_enable) {

        String sql;
        sql = "Update PT_OUTPATIENT_FEE\r\n"
                + "Set NO_DENTISIT=%d, \r\n"
                + "    NO_CHI_MEDICINE=%d, \r\n"
                + "    NO_SERVICE_CHARGE=%d, \r\n"
                + "    LIM_OUT_ISLANDS=%d, \r\n"
                + "    LIM_HOLIDAY=%d, \r\n"
                + "    LIM_MAX_ENABLE=%d, \r\n"
                + "    LIM_MAX=%d, \r\n"
                + "    LIM_AGE_ENABLE=%d, \r\n"
                + "    LIM_AGE_TYPE=%d, \r\n"
                + "    LIM_AGE=%d, \r\n"
                + "    LIM_DIVISION_ENABLE=%d, \r\n"
                + "    EXCLUDE_NHI_NO_ENABLE=%d \r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, no_dentisit, no_chi_medicine, no_service_charge, lim_out_islands, lim_holiday, 
                lim_max_enable, lim_max, lim_age_enable, lim_age_type, lim_age, 
                lim_division_enable, exclude_nhi_no_enable, ptId);
        logger.trace(sql);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }

}
