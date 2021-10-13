package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.nhiwidget.dto.PlanConditionPl;
import tw.com.leadtek.tools.Utility;


@Repository
public class PlanConditionDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());


    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public java.util.List<Map<String, Object>> findList(String searchName) {
        String sql;
        sql = "Select ID, NAME, DIVISION, ACTIVE\r\n"
                + "From PLAN_CONDITION\r\n"
                + "Where(1=1) \n"
                + "  -- and(NAME like '%%%s%%')";
        sql = String.format(sql, searchName);
        if (searchName.length()>0) {
            sql = sql.replace("-- and(NAME", " and(NAME");
        }
        logger.info(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        return Utility.listLowerCase(lst);
    }

    public java.util.Map<String, Object> findOne(long id) {
        String sql;
        sql = "Select ID, NAME, DIVISION, ACTIVE, EXP_ICD_NO_ENABLE, EXP_ICD_NO, NO_EXP_ICD_NO_ENABLE, NO_EXP_ICD_NO, EXCLUDE_PSYCHIATRIC_ENABLE, MEDICINE_TIMES_ENABLE, MEDICINE_TIMES, MEDICINE_TIMES_DIVISION, EXCLUDE_PLAN_NDAY_ENABLE, EXCLUDE_PLAN_NDAY, EXCLUDE_JOIN_ENABLE, EXCLUDE_JOIN\r\n"
                  + "From PLAN_CONDITION\r\n"
                  + "Where(ID = %d)";
        sql = String.format(sql, id);
        logger.info(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.mapLowerCase(lst.get(0));
        } else {
            return new java.util.HashMap<String, Object>();
        }
        
    }
    
    public java.util.Map<String, Object> findOne(String name, String division) {
        String sql;
        sql = "Select ID, NAME, DIVISION, ACTIVE, EXP_ICD_NO_ENABLE, EXP_ICD_NO, NO_EXP_ICD_NO_ENABLE, NO_EXP_ICD_NO, EXCLUDE_PSYCHIATRIC_ENABLE, MEDICINE_TIMES_ENABLE, MEDICINE_TIMES, MEDICINE_TIMES_DIVISION, EXCLUDE_PLAN_NDAY_ENABLE, EXCLUDE_PLAN_NDAY, EXCLUDE_JOIN_ENABLE, EXCLUDE_JOIN\r\n"
                  + "From PLAN_CONDITION\r\n"
                  + "Where(NAME = '%s')\n"
                  + "  and(DIVISION = '%s')";
        sql = String.format(sql, name, division);
        logger.info(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.mapLowerCase(lst.get(0));
        } else {
            return null;
        }
    }
    
    
    public int addPlanCondition(String name, String division, int active, 
            int exp_icd_no_enable, String exp_icd_no, int no_exp_icd_no_enable, String no_exp_icd_no, 
            int exclude_psychiatric_enable, int medicine_times_enable, int medicine_times, String medicine_times_division, 
            int exclude_plan_nday_enable, int exclude_plan_nday, int exclude_join_enable, String exclude_join) {
        String sql;
        sql = "Insert into \r\n"
                + "PLAN_CONDITION(NAME, DIVISION, ACTIVE, EXP_ICD_NO_ENABLE, EXP_ICD_NO, NO_EXP_ICD_NO_ENABLE, NO_EXP_ICD_NO, EXCLUDE_PSYCHIATRIC_ENABLE, MEDICINE_TIMES_ENABLE, MEDICINE_TIMES, MEDICINE_TIMES_DIVISION, EXCLUDE_PLAN_NDAY_ENABLE, EXCLUDE_PLAN_NDAY, EXCLUDE_JOIN_ENABLE, EXCLUDE_JOIN)\r\n"
                + "Values('%s', '%s', %d, %d, '%s', %d, '%s', %d, %d, %d, '%s', %d, %d, %d, '%s')";
        sql = String.format(sql, name, division, active, exp_icd_no_enable, exp_icd_no, no_exp_icd_no_enable, no_exp_icd_no, exclude_psychiatric_enable, medicine_times_enable, medicine_times, medicine_times_division, exclude_plan_nday_enable, exclude_plan_nday, exclude_join_enable, exclude_join);
        logger.info(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    
    public int updatePlanCondition(long id, String name, String division, int active, 
            int exp_icd_no_enable, String exp_icd_no, int no_exp_icd_no_enable, String no_exp_icd_no, 
            int exclude_psychiatric_enable, int medicine_times_enable, int medicine_times, String medicine_times_division, 
            int exclude_plan_nday_enable, int exclude_plan_nday, int exclude_join_enable, String exclude_join) {
        String sql;
        sql = "Update PLAN_CONDITION\r\n"
                + "Set NAME='%s', \r\n"
                + "    DIVISION='%s', \r\n"
                + "    ACTIVE=%d, \r\n"
                + "    EXP_ICD_NO_ENABLE=%d, \r\n"
                + "    EXP_ICD_NO='%s', \r\n"
                + "    NO_EXP_ICD_NO_ENABLE=%d, \r\n"
                + "    NO_EXP_ICD_NO='%s', \r\n"
                + "    EXCLUDE_PSYCHIATRIC_ENABLE=%d, \r\n"
                + "    MEDICINE_TIMES_ENABLE=%d, \r\n"
                + "    MEDICINE_TIMES=%d, \r\n"
                + "    MEDICINE_TIMES_DIVISION='%s', \r\n"
                + "    EXCLUDE_PLAN_NDAY_ENABLE=%d, \r\n"
                + "    EXCLUDE_PLAN_NDAY=%d, \r\n"
                + "    EXCLUDE_JOIN_ENABLE=%d, \r\n"
                + "    EXCLUDE_JOIN='%s'\r\n"
                + "Where (ID=%d)";
        sql = String.format(sql, name, division, active, exp_icd_no_enable, exp_icd_no, no_exp_icd_no_enable, no_exp_icd_no, 
                            exclude_psychiatric_enable, medicine_times_enable, medicine_times, medicine_times_division, 
                            exclude_plan_nday_enable, exclude_plan_nday, exclude_join_enable, exclude_join, id);
        logger.info(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    public int delPlanCondition(long id) {
        String sql;
        sql = "Delete From PLAN_CONDITION\r\n"
                + "Where (ID=%d)";
        sql = String.format(sql, id);
        logger.info(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    //=== PLAN_icd_no
    public java.util.List<Map<String, Object>> findIcdNo(long id) {
        String sql;
        sql = "Select ENABLE, ICD_NO\r\n"
                + "From PLAN_ICD_NO\r\n"
                + "Where (ID=%d)";
        sql = String.format(sql, id);
        logger.info(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        return Utility.listLowerCase(lst);
    }
  
    public int addIcdNo(long id, int enable, String icdNo) {
        String sql;
        sql = "Insert into \r\n"
              + "PLAN_ICD_NO(ID, ENABLE, ICD_NO)\r\n"
              + "Values(%d, %d, '%s')";
        sql = String.format(sql, id, enable, icdNo);
        logger.info(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }

    public int delIcdNo(long id) {
        String sql;
        sql = "Delete From PLAN_ICD_NO\r\n"
                + "Where (ID=%d)";
        sql = String.format(sql, id);
        logger.info(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    

    //=== PLAN_less_nday 
    public java.util.List<Map<String, Object>> findLessNDay(long id) {
        String sql;
        sql = "Select ENABLE, ICD_NO, NDAY\r\n"
                + "From PLAN_LESS_NDAY\r\n"
                + "Where (ID=%d)";
        sql = String.format(sql, id);
        logger.info(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        return Utility.listLowerCase(lst);
    }
  
    public int addLessNDay(long id, int enable, String icdNo, int nday) {
        String sql;
        sql = "Insert into \r\n"
                + "PLAN_LESS_NDAY (ID, ENABLE, ICD_NO, NDAY)\r\n"
                + "Values(%d, %d, '%s', %d)";
        sql = String.format(sql, id, enable, icdNo, nday);
        logger.info(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }

    public int delLessNDay(long id) {
        String sql;
        sql = "Delete From PLAN_LESS_NDAY\r\n"
                + "Where (ID=%d)";
        sql = String.format(sql, id);
        logger.info(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    
  //=== PLAN_more_times 
    public java.util.List<Map<String, Object>> findMoreTimes(long id) {
        String sql;
        sql = "Select ENABLE, ICD_NO, TIMES\r\n"
                + "From PLAN_MORE_TIMES\r\n"
                + "Where (ID=%d)";
        sql = String.format(sql, id);
        logger.info(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        return Utility.listLowerCase(lst);
    }
  
    public int addMoreTimes(long id, int enable, String icdNo, int times) {
        String sql;
        sql = "Insert into \r\n"
                + "PLAN_MORE_TIMES(ID, ENABLE, ICD_NO, TIMES)\r\n"
                + "Values(%d, %d, '%s', %d)";
        sql = String.format(sql, id, enable, icdNo, times);
        logger.info(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }

    public int delMoreTimes(long id) {
        String sql;
        sql = "Delete From PLAN_MORE_TIMES\r\n"
                + "Where (ID=%d)";
        sql = String.format(sql, id);
        logger.info(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }

}
