package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.tools.Utility;


@Repository
public class PlanConditionDao extends BaseSqlDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());


    @Autowired
    protected JdbcTemplate jdbcTemplate;
    
    public long findListCount(String searchName) {
        String sql;
        sql = "Select Count(*) as CNT\n"
                + "From PLAN_CONDITION\n"
                + "Where(1=1) \n"
                + "  -- and(NAME like '%%%s%%')";
        sql = String.format(sql, searchName);
        if (searchName.length()>0) {
            sql = sql.replace("-- and(NAME", " and(NAME");
        }
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return (long)lst.get(0).get("CNT");
        } else {
            return 0l;
        }
    }

    public java.util.List<Map<String, Object>> findList(String searchName, int start, int pageSize) {
        String sql;
        sql = "Select ID, NAME, DIVISION, ACTIVE\n"
                + "From PLAN_CONDITION\n"
                + "Where(1=1) \n"
                + "  -- and(NAME like '%%%s%%')\n"
                + "Order By ID\n"
                + "limit %d offset %d";
                
        sql = String.format(sql, searchName, pageSize, start);
        if (searchName.length()>0) {
            sql = sql.replace("-- and(NAME", " and(NAME");
        }
        logger.trace(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        return Utility.listLowerCase(lst);
    }

    public java.util.Map<String, Object> findOne(long id) {
        String sql;
        sql = "Select ID, NAME, DIVISION, ACTIVE, EXP_ICD_NO_ENABLE, EXP_ICD_NO, NO_EXP_ICD_NO_ENABLE, NO_EXP_ICD_NO, EXCLUDE_PSYCHIATRIC_ENABLE, MEDICINE_TIMES_ENABLE, MEDICINE_TIMES, MEDICINE_TIMES_DIVISION, EXCLUDE_PLAN_NDAY_ENABLE, EXCLUDE_PLAN_NDAY, EXCLUDE_JOIN_ENABLE, EXCLUDE_JOIN\n"
                  + "From PLAN_CONDITION\n"
                  + "Where(ID = %d)";
        sql = String.format(sql, id);
        logger.trace(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.mapLowerCase(lst.get(0));
        } else {
            return new java.util.HashMap<String, Object>();
        }
        
    }
    
    public java.util.Map<String, Object> findOne(String name, String division) {
        String sql;
        sql = "Select ID, NAME, DIVISION, ACTIVE, EXP_ICD_NO_ENABLE, EXP_ICD_NO, NO_EXP_ICD_NO_ENABLE, NO_EXP_ICD_NO, EXCLUDE_PSYCHIATRIC_ENABLE, MEDICINE_TIMES_ENABLE, MEDICINE_TIMES, MEDICINE_TIMES_DIVISION, EXCLUDE_PLAN_NDAY_ENABLE, EXCLUDE_PLAN_NDAY, EXCLUDE_JOIN_ENABLE, EXCLUDE_JOIN\n"
                  + "From PLAN_CONDITION\n"
                  + "Where(NAME = '%s')\n"
                  + "  and(DIVISION = '%s')\n"
                  + "Order By ID Desc";
        sql = String.format(sql, name, noInjection(division));
        logger.trace(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.mapLowerCase(lst.get(0));
        } else {
            return null;
        }
    }
    
    
    public long addPlanCondition(String name, String division, int active, 
            int exp_icd_no_enable, String exp_icd_no, int no_exp_icd_no_enable, String no_exp_icd_no, 
            int exclude_psychiatric_enable, int medicine_times_enable, int medicine_times, String medicine_times_division, 
            int exclude_plan_nday_enable, int exclude_plan_nday, int exclude_join_enable, String exclude_join) {
        String sql;
        sql = "Insert into \n"
                + "PLAN_CONDITION(NAME, DIVISION, ACTIVE, EXP_ICD_NO_ENABLE, EXP_ICD_NO, NO_EXP_ICD_NO_ENABLE, NO_EXP_ICD_NO, EXCLUDE_PSYCHIATRIC_ENABLE, MEDICINE_TIMES_ENABLE, MEDICINE_TIMES, MEDICINE_TIMES_DIVISION, EXCLUDE_PLAN_NDAY_ENABLE, EXCLUDE_PLAN_NDAY, EXCLUDE_JOIN_ENABLE, EXCLUDE_JOIN)\n"
                + "Values('%s', '%s', %d, %d, '%s', %d, '%s', %d, %d, %d, '%s', %d, %d, %d, '%s')";
        sql = String.format(sql, noInjection(name), noInjection(division), active, exp_icd_no_enable, 
                exp_icd_no, no_exp_icd_no_enable, noInjection(no_exp_icd_no), 
                exclude_psychiatric_enable, medicine_times_enable, medicine_times, noInjection(medicine_times_division), 
                exclude_plan_nday_enable, exclude_plan_nday, exclude_join_enable, noInjection(exclude_join));
        logger.trace(sql);
        long ret = jdbcTemplate.update(sql);
        if (ret > 0) {
            sql = "Select ID, NAME, DIVISION\n"
                    + "From PLAN_CONDITION\n"
                    + "Where (1=1)\n"
                    + "   and(NAME='%s')\n"
                    + "   and(DIVISION='%s')\n"
                    + "Order By ID DESC\n"
                    + "Limit 1";
            sql = String.format(sql, noInjection(name), noInjection(division));
            logger.trace(sql);
            java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
            if (lst.size()>0) {
                ret = (long)lst.get(0).get("id");
//                System.out.println("plan_condition_id="+ret);
            }
        }
        return ret;
    }
    
    
    public int updatePlanCondition(long id, String name, String division, int active, 
            int exp_icd_no_enable, String exp_icd_no, int no_exp_icd_no_enable, String no_exp_icd_no, 
            int exclude_psychiatric_enable, int medicine_times_enable, int medicine_times, String medicine_times_division, 
            int exclude_plan_nday_enable, int exclude_plan_nday, int exclude_join_enable, String exclude_join) {
        String sql;
        sql = "Update PLAN_CONDITION\n"
                + "Set NAME='%s', \n"
                + "    DIVISION='%s', \n"
                + "    ACTIVE=%d, \n"
                + "    EXP_ICD_NO_ENABLE=%d, \n"
                + "    EXP_ICD_NO='%s', \n"
                + "    NO_EXP_ICD_NO_ENABLE=%d, \n"
                + "    NO_EXP_ICD_NO='%s', \n"
                + "    EXCLUDE_PSYCHIATRIC_ENABLE=%d, \n"
                + "    MEDICINE_TIMES_ENABLE=%d, \n"
                + "    MEDICINE_TIMES=%d, \n"
                + "    MEDICINE_TIMES_DIVISION='%s', \n"
                + "    EXCLUDE_PLAN_NDAY_ENABLE=%d, \n"
                + "    EXCLUDE_PLAN_NDAY=%d, \n"
                + "    EXCLUDE_JOIN_ENABLE=%d, \n"
                + "    EXCLUDE_JOIN='%s'\n"
                + "Where (ID=%d)";
        sql = String.format(sql, noInjection(name), noInjection(division), active, exp_icd_no_enable, noInjection(exp_icd_no), 
                            no_exp_icd_no_enable, noInjection(no_exp_icd_no), 
                            exclude_psychiatric_enable, medicine_times_enable, medicine_times, noInjection(medicine_times_division), 
                            exclude_plan_nday_enable, exclude_plan_nday, exclude_join_enable, noInjection(exclude_join), id);
        logger.trace(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    public int delPlanCondition(long id) {
        String sql;
        sql = "Delete From PLAN_CONDITION\n"
                + "Where (ID=%d)";
        sql = String.format(sql, id);
        logger.trace(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    //=== PLAN_icd_no
    public java.util.List<Map<String, Object>> findIcdNo(long id) {
        String sql;
        sql = "Select ENABLE, ICD_NO\n"
                + "From PLAN_ICD_NO\n"
                + "Where (ID=%d)";
        sql = String.format(sql, id);
        logger.trace(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        return Utility.listLowerCase(lst);
    }
  
    public int addIcdNo(long id, int enable, String icdNo) {
        String sql;
        sql = "Insert into \n"
              + "PLAN_ICD_NO(ID, ENABLE, ICD_NO)\n"
              + "Values(%d, %d, '%s')";
        sql = String.format(sql, id, enable, noInjection(icdNo));
        logger.trace(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }

    public int delIcdNo(long id) {
        String sql;
        sql = "Delete From PLAN_ICD_NO\n"
                + "Where (ID=%d)";
        sql = String.format(sql, id);
        logger.trace(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    

    //=== PLAN_less_nday 
    public java.util.List<Map<String, Object>> findLessNDay(long id) {
        String sql;
        sql = "Select ENABLE, ICD_NO, NDAY\n"
                + "From PLAN_LESS_NDAY\n"
                + "Where (ID=%d)";
        sql = String.format(sql, id);
        logger.trace(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        return Utility.listLowerCase(lst);
    }
  
    public int addLessNDay(long id, int enable, String icdNo, int nday) {
        String sql;
        sql = "Insert into \n"
                + "PLAN_LESS_NDAY (ID, ENABLE, ICD_NO, NDAY)\n"
                + "Values(%d, %d, '%s', %d)";
        sql = String.format(sql, id, enable, noInjection(icdNo), nday);
        logger.trace(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }

    public int delLessNDay(long id) {
        String sql;
        sql = "Delete From PLAN_LESS_NDAY\n"
                + "Where (ID=%d)";
        sql = String.format(sql, id);
        logger.trace(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    
  //=== PLAN_more_times 
    public java.util.List<Map<String, Object>> findMoreTimes(long id) {
        String sql;
        sql = "Select ENABLE, ICD_NO, TIMES\n"
                + "From PLAN_MORE_TIMES\n"
                + "Where (ID=%d)";
        sql = String.format(sql, id);
        logger.trace(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        return Utility.listLowerCase(lst);
    }
  
    public int addMoreTimes(long id, int enable, String icdNo, int times) {
        String sql;
        sql = "Insert into \n"
                + "PLAN_MORE_TIMES(ID, ENABLE, ICD_NO, TIMES)\n"
                + "Values(%d, %d, '%s', %d)";
        sql = String.format(sql, id, enable, noInjection(icdNo), times);
        logger.trace(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }

    public int delMoreTimes(long id) {
        String sql;
        sql = "Delete From PLAN_MORE_TIMES\n"
                + "Where (ID=%d)";
        sql = String.format(sql, id);
        logger.trace(sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }

}
