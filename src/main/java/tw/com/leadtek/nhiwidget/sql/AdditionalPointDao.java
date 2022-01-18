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
public class AdditionalPointDao extends BaseSqlDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    

    public long searchAdditionalPointCount(int syear, java.util.Date startDate, java.util.Date endDate) {
        String strStart, strEnd;
        if (startDate!=null) {
            strStart = Utility.dateFormat(startDate, "yyyy/MM/dd");
        } else {
            strStart = "2000/01/01";
        }
        if (endDate==null) {
            endDate = new java.util.Date();
        }
        strEnd = Utility.dateFormat(endDate, "yyyy/MM/dd");
        String sql;
        sql = "Select Count(*) as CNT\n"
                + "From AP_ADDITIONAL_POINT\n"
                + "Where (1=1)\n"
                + "  -- and (SYEAR=%d)\n"
                + "  and (START_DATE BETWEEN '%s' and '%s')\n"
                + "  and (END_DATE BETWEEN '%s' and '%s')";
        sql = String.format(sql, syear, strStart, strEnd, strStart, strEnd);
        if (syear>0) {
            sql=sql.replace("-- and (SYEAR=", " and (SYEAR=");
        }
//        System.out.println("sql-48="+sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return (long)lst.get(0).get("CNT");
        } else {
            return 0l;
        }

    }

    public java.util.List<Map<String, Object>> searchAdditionalPoint(int syear, java.util.Date startDate, java.util.Date endDate, 
            int start, int pageSize, String sortField, String sortDirection) {
        String strStart, strEnd;
        if (startDate!=null) {
            strStart = Utility.dateFormat(startDate, "yyyy/MM/dd");
        } else {
            strStart = "2000/01/01";
        }
        if (endDate==null) {
            endDate = new java.util.Date();
        }
        strEnd = Utility.dateFormat(endDate, "yyyy/MM/dd");
        
        String sql;
        sql = "Select ID, ACTIVE, SYEAR, START_DATE, END_DATE\n"
                + "From AP_ADDITIONAL_POINT\n"
                + "Where (1=1)\n"
                + "  -- and (SYEAR=%d)\n"
                + "  and (START_DATE BETWEEN '%s' and '%s')\n"
                + "  and (END_DATE BETWEEN '%s' and '%s')\n"
                + "Order By %s %s\n"
                + "limit %d offset %d";
        sql = String.format(sql, syear, strStart, strEnd, strStart, strEnd,
                            noInjection(sortField), noInjection(sortDirection), pageSize, start);
        if (syear>0) {
            sql=sql.replace("-- and (SYEAR=", " and (SYEAR=");
        }
//        System.out.println("sql-86="+sql);
        logger.trace(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        
        return Utility.listLowerCase(lst);
    }

    /*
    public long searchAdditionalPointByDateRangeCount(int syear, java.util.Date startDate, java.util.Date endDate) {
        String strStart, strEnd;
        if (startDate!=null) {
            strStart = Utility.dateFormat(startDate, "yyyy/MM/dd");
        } else {
            strStart = "2000/01/01";
        }
        if (endDate==null) {
            endDate = new java.util.Date();
        }
        strEnd = Utility.dateFormat(endDate, "yyyy/MM/dd");
        String sql;
        sql = "Select Count(*) as CNT\n"
                + "From AP_ADDITIONAL_POINT\n"
                + "Where (1=1)\n"
                + "  -- and (SYEAR=%d)\n"
                + " and (START_DATE BETWEEN '%s' and '%s')\n"
                + " and (END_DATE BETWEEN '%s' and '%s')";
        
        sql = String.format(sql, syear, strStart, strEnd, strStart, strEnd);
        if (syear>0) {
            sql=sql.replace("-- and (SYEAR=", " and (SYEAR=");
        }
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return (long)lst.get(0).get("CNT");
        } else {
            return 0l;
        }
    }

    public java.util.List<Map<String, Object>> searchAdditionalPointByDateRange(int syear, java.util.Date startDate, java.util.Date endDate, 
            int start, int pageSize, String sortField, String sortDirection) {
        String strStart, strEnd;
        if (startDate!=null) {
            strStart = Utility.dateFormat(startDate, "yyyy/MM/dd");
        } else {
            strStart = "2000/01/01";
        }
        if (endDate==null) {
            endDate = new java.util.Date();
        }
        strEnd = Utility.dateFormat(endDate, "yyyy/MM/dd");
        String sql;
        sql = "Select ID, ACTIVE, SYEAR, START_DATE, END_DATE\n"
                + "From AP_ADDITIONAL_POINT\n"
                + "Where (1=1)\n"
                + "  -- and (SYEAR=%d)\n"
                + " and (START_DATE BETWEEN '%s' and '%s')\n"
                + " and (END_DATE BETWEEN '%s' and '%s')\n"
                + "Order By %s %s\n"
                + "limit %d offset %d";
        
        sql = String.format(sql, syear, strStart, strEnd, strStart, strEnd, noInjection(sortField), noInjection(sortDirection), pageSize, start);
        if (syear>0) {
            sql=sql.replace("-- and (SYEAR=", " and (SYEAR=");
        }
//        System.out.println("sql-144="+sql);
        logger.trace(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        return Utility.listLowerCase(lst);
    }
    */
    
    
    public java.util.Map<String, Object> findAdditionalPoint(long ad_id) {
        String sql;
        sql = "Select ID, ACTIVE, SYEAR, START_DATE, END_DATE\n"
                + "From AP_ADDITIONAL_POINT\n"
                + "Where (ID=%d)\n";
        sql = String.format(sql, ad_id);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.mapLowerCase(lst.get(0));
        } else {
            return new java.util.HashMap<String, Object>();
        }
    }
    
    
    //--------------------------------------
    public long addAdditionalPoint(int syear, 
                    java.util.Date start_date, java.util.Date end_date) {
        String strStart = Utility.dateFormat(start_date, "yyyy/MM/dd");
        String strEnd = Utility.dateFormat(end_date, "yyyy/MM/dd");
        long newId=0;
        String sql, s1;
        sql = "Insert into \n"
                + "AP_ADDITIONAL_POINT(ID, SYEAR, START_DATE, END_DATE)\n"
                + "Values(%d, %d, '%s', '%s')";
        for (int a=0; a<50; a++) {
            newId = newTableId_l("AP_ADDITIONAL_POINT", "ID");
            s1 = String.format(sql, newId, syear, strStart, strEnd);
            try {
                int ret = jdbcTemplate.update(s1);
                if (ret > 0) {
                    break;
                }
            } catch(DataAccessException ex) {
                //
            }
            Utility.sleep(10);
        }
        return newId;
    }
    
    public int updateAdditionalPoint(long id, int syear, 
            java.util.Date start_date, java.util.Date end_date) {
        String strStart = Utility.dateFormat(start_date, "yyyy/MM/dd");
        String strEnd = Utility.dateFormat(end_date, "yyyy/MM/dd");
        String sql;
        sql = "Update AP_ADDITIONAL_POINT\n"
                + "Set SYEAR=%d, \n"
                + "    START_DATE='%s', \n"
                + "    END_DATE='%s'\n"
                + "Where (ID=%d)";
        sql = String.format(sql, syear, strStart, strEnd, id);
//        System.out.println("sql-210="+sql);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    public int updateAdditionalPointActive(long id, int state) {
        String sql;
        sql = "Update AP_ADDITIONAL_POINT\n"
                + "Set ACTIVE=%d \n"
                + "Where (ID=%d)";
        sql = String.format(sql, state, id);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    public int deleteAdditionalPoint(long id) {
        String sql;
        sql = "Delete from AP_ADDITIONAL_POINT\n"
                + "Where (ID=%d)";
        sql = String.format(sql, id);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    //=== Outpatient_1
    public long addOutpatient_1(long ap_id, int enable, java.util.List<String> categorys) {
        long newId = 0;
        if ((ap_id>0)&&(categorys.size()>0)) {
            String sql, s1;
            sql = "Insert into \n"
                    + "AP_OUTPATIENT_1(ID, ENABLE, AP_ID)\n"
                    + "Values(%d, %d, %d)";
            for (int a=0; a<50; a++) {
                newId = newTableId_l("AP_OUTPATIENT_1", "ID");
                s1 = String.format(sql, newId, enable, ap_id);
                try {
                    int ret = jdbcTemplate.update(s1);
                    if (ret > 0) {
                        break;
                    }
                } catch(DataAccessException ex) {
                    newId = 0;
                }
                Utility.sleep(10);
            }
        }
        if (newId > 0) {
            for (String category : categorys) {
                addOutpatient_1_category(newId, noInjection(category));
            }
        }
        return newId;
    }
    
    private int addOutpatient_1_category(long id, String category) {
        String sql;
        sql = "Insert into\n"
                + "AP_OUTPATIENT_1_CATEGORY(ID, CATEGORY)\n"
                + "Values(%d, '%s')";
        sql = String.format(sql, id, noInjection(category));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    public int delOutpatient_1(long ap_id) {
        int ret = 0;
        String sql;
        java.util.List<Map<String, Object>> lst = findOutpatient(ap_id, 1);
        for (Map<String, Object> item: lst) {
            long id = (long)item.get("id");
            sql = "Delete from AP_OUTPATIENT_1_CATEGORY\n"
                    + "Where (ID=%d)";
            sql = String.format(sql, id);
            ret += jdbcTemplate.update(sql);
        }
        //----
        sql = "Delete from AP_OUTPATIENT_1\n"
                + "Where (AP_ID=%d)";
        sql = String.format(sql, ap_id);
        ret += jdbcTemplate.update(sql);
        return ret;
    }
    
    //=== Outpatient_2
    public long addOutpatient_2(long ap_id, int enable, String nhi_no, java.util.List<String> cpoes) {
        long newId = 0;
        if ((ap_id>0)&&(cpoes.size()>0)) {
            String sql, s1;
            sql = "Insert into \n"
                    + "AP_OUTPATIENT_2(ID, ENABLE, AP_ID, NHI_NO)\n"
                    + "Values(%d, %d, %d, %s)";
            for (int a=0; a<50; a++) {
                newId = newTableId_l("AP_OUTPATIENT_2", "ID");
                s1 = String.format(sql, newId, enable, ap_id, quotedNotNull(nhi_no));
                try {
                    int ret = jdbcTemplate.update(s1);
                    if (ret > 0) {
                        break;
                    }
                } catch(DataAccessException ex) {
                    newId = 0;
                }
                Utility.sleep(10);
            }
        }
        if (newId > 0) {
            for (String cpoe : cpoes) {
                addOutpatient_2_cpoe(newId, cpoe);
            }
        }
        return newId;
    }
    
    private int addOutpatient_2_cpoe(long id, String cpoe) {
        String sql;
        sql = "Insert into \n"
                + "AP_OUTPATIENT_2_CPOE(ID, CPOE)\n"
                + "Values(%d, '%s')";
        sql = String.format(sql, id, noInjection(cpoe));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    public int delOutpatient_2(long ap_id) {
        int ret = 0;
        String sql;
        java.util.List<Map<String, Object>> lst = findOutpatient(ap_id, 2);
        for (Map<String, Object> item: lst) {
            long id = (long)item.get("id");
            sql = "Delete from AP_OUTPATIENT_2_CPOE\n"
                    + "Where (ID=%d)";
            sql = String.format(sql, id);
            ret += jdbcTemplate.update(sql);
        }
        //----
        sql = "Delete from AP_OUTPATIENT_2\n"
                + "Where (AP_ID=%d)";
        sql = String.format(sql, ap_id);
        ret += jdbcTemplate.update(sql);
        return ret;
    }
    
    //===Outpatient_3
    public long addOutpatient_3(long ap_id, int enable, String nhi_no) {
        long newId = 0;
        if ((ap_id>0)&&(nhi_no.length()>0)) {
            String sql, s1;
            sql = "Insert into \n"
                    + "AP_OUTPATIENT_3(ID, ENABLE, AP_ID, NHI_NO)\n"
                    + "Values(%d, %d, %d, '%s')";
            for (int a=0; a<50; a++) {
                newId = newTableId_l("AP_OUTPATIENT_3", "ID");
                s1 = String.format(sql, newId, enable, ap_id, noInjection(nhi_no));
                try {
                    int ret = jdbcTemplate.update(s1);
                    if (ret > 0) {
                        break;
                    }
                } catch(DataAccessException ex) {
                    newId = 0;
                }
                Utility.sleep(10);
            }
        }
        return newId;
    }

    public int delOutpatient_3(long ap_id) {
        int ret = 0;
        String sql;
        sql = "DELETE FROM AP_OUTPATIENT_3\n"
              + "WHERE (AP_ID=%d)";
        sql = String.format(sql, ap_id);
        ret += jdbcTemplate.update(sql);
        return ret;
    }
    
  //=== Outpatient_4
    public long addOutpatient_4(long ap_id, int enable, String nhi_no, java.util.List<String> categorys, 
            java.util.List<String> cpoes, java.util.List<String> treatments) {
        long newId = 0;
        if ((ap_id>0)&&(categorys.size()>0)||(cpoes.size()>0)||(treatments.size()>0)) {
            String sql, s1;
            sql = "Insert into \n"
                    + "AP_OUTPATIENT_4(ID, ENABLE, AP_ID, NHI_NO)\n"
                    + "Values(%d, %d, %d, %s)";
            for (int a=0; a<50; a++) {
                newId = newTableId_l("AP_OUTPATIENT_4", "ID");
                s1 = String.format(sql, newId, enable, ap_id, quotedNotNull(nhi_no));
                try {
                    int ret = jdbcTemplate.update(s1);
                    if (ret > 0) {
                        break;
                    }
                } catch(DataAccessException ex) {
                    newId = 0;
                }
                Utility.sleep(10);
            }
        }
        if (newId > 0) {
            for (String category : categorys) {
                addOutpatient_4_category(newId, category);
            }
            for (String cpoe : cpoes) {
                addOutpatient_4_cpoe(newId, cpoe);
            }
            for (String treatment : treatments) {
                addOutpatient_4_treatment(newId, treatment);
            }
        }
        return newId;
    }
    
    private int addOutpatient_4_category(long id, String category) {
        String sql;
        sql = "Insert into \n"
                + "AP_OUTPATIENT_4_CATEGORY(ID, CATEGORY)\n"
                + "Values(%d, '%s')";
        sql = String.format(sql, id, noInjection(category));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    private int addOutpatient_4_cpoe(long id, String cpoe) {
        String sql;
        sql = "Insert into \n"
                + "AP_OUTPATIENT_4_CPOE(ID, CPOE)\n"
                + "Values(%d, '%s')";
        sql = String.format(sql, id, noInjection(cpoe));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    private int addOutpatient_4_treatment(long id, String treatment) {
        String sql;
        sql = "Insert into \n"
                + "AP_OUTPATIENT_4_TREATMENT(ID, TREATMENT)\n"
                + "Values(%d, '%s')";
        sql = String.format(sql, id, noInjection(treatment));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    public int delOutpatient_4(long ap_id) {
        int ret = 0;
        String sql;
        java.util.List<Map<String, Object>> lst = findOutpatient(ap_id, 4);
        for (Map<String, Object> item: lst) {
            long id = (long)item.get("id");
            sql = "Delete from AP_OUTPATIENT_4_CATEGORY\n"
                    + "Where (ID=%d)";
            sql = String.format(sql, id);
            ret += jdbcTemplate.update(sql);
            //----
            sql = "Delete from AP_OUTPATIENT_4_CPOE\n"
                    + "Where (ID=%d)";
            sql = String.format(sql, id);
            ret += jdbcTemplate.update(sql);
            //----
            sql = "Delete from AP_OUTPATIENT_4_TREATMENT\n"
                    + "Where (ID=%d)";
            sql = String.format(sql, id);
            ret += jdbcTemplate.update(sql);
        }
        //----
        sql = "Delete from AP_OUTPATIENT_4\n"
                + "Where (AP_ID=%d)";
        sql = String.format(sql, ap_id);
        ret += jdbcTemplate.update(sql);
        return ret;
    }
    
    //=== Outpatient_5
    public long addOutpatient_5(long ap_id, int enable, String icd_no, String nhi_no, java.util.List<String> cpoes) {
        long newId = 0;
        if ((ap_id>0)&&(cpoes.size()>0)) {
            String sql, s1;
            sql = "Insert into \n"
                    + "AP_OUTPATIENT_5(ID, ENABLE, AP_ID, ICD_NO, NHI_NO)\n"
                    + "Values(%d, %d, %d, %s, %s);";
            for (int a=0; a<50; a++) {
                newId = newTableId_l("AP_OUTPATIENT_5", "ID");
                s1 = String.format(sql, newId, enable, ap_id, quotedNotNull(icd_no), quotedNotNull(nhi_no));
                try {
                    int ret = jdbcTemplate.update(s1);
                    if (ret > 0) {
                        break;
                    }
                } catch(DataAccessException ex) {
                    newId = 0;
                }
                Utility.sleep(10);
            }
        }
        if (newId > 0) {
            for (String cpoe : cpoes) {
                addOutpatient_5_cpoe(newId, cpoe);
            }
        }
        return newId;
    }
    
    private int addOutpatient_5_cpoe(long id, String cpoe) {
        String sql;
        sql = "Insert into \n"
                + "AP_OUTPATIENT_5_CPOE(ID, CPOE)\n"
                + "Values(%d, '%s')";
        sql = String.format(sql, id, noInjection(cpoe));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    public int delOutpatient_5(long ap_id) {
        int ret = 0;
        String sql;
        java.util.List<Map<String, Object>> lst = findOutpatient(ap_id, 5);
        for (Map<String, Object> item: lst) {
            long id = (long)item.get("id");
            sql = "Delete from AP_OUTPATIENT_5_CPOE\n"
                    + "Where (ID=%d)";
            sql = String.format(sql, id);
            ret += jdbcTemplate.update(sql);
        }
        //----
        sql = "Delete from AP_OUTPATIENT_5\n"
                + "Where (AP_ID=%d)";
        sql = String.format(sql, ap_id);
        ret += jdbcTemplate.update(sql);
        return ret;
    }

    //=== Outpatient_6
    public long addOutpatient_6(long ap_id, int enable, String nhi_no, java.util.List<String> categorys, 
            java.util.List<String> cpoes, java.util.List<String> plans) {
        long newId = 0;
        if ((ap_id>0)&&(categorys.size()>0)||(cpoes.size()>0)||(plans.size()>0)) {
            String sql, s1;
            sql = "Insert into \n"
                    + "AP_OUTPATIENT_6(ID, ENABLE, AP_ID, NHI_NO)\n"
                    + "Values(%d, %d, %d, %s)";
            for (int a=0; a<50; a++) {
                newId = newTableId_l("AP_OUTPATIENT_6", "ID");
                s1 = String.format(sql, newId, enable, ap_id, quotedNotNull(nhi_no));
                try {
                    int ret = jdbcTemplate.update(s1);
                    if (ret > 0) {
                        break;
                    }
                } catch(DataAccessException ex) {
                    newId = 0;
                }
                Utility.sleep(10);
            }
        }
        if (newId > 0) {
            for (String category : categorys) {
                addOutpatient_6_category(newId, category);
            }
            for (String cpoe : cpoes) {
                addOutpatient_6_cpoe(newId, cpoe);
            }
            for (String plan : plans) {
                addOutpatient_6_plan(newId, plan);
            }
        }
        return newId;
    }
    
    private int addOutpatient_6_category(long id, String category) {
        String sql;
        sql = "Insert into \n"
                + "AP_OUTPATIENT_6_CATEGORY(ID, CATEGORY)\n"
                + "Values(%d, '%s')";
        sql = String.format(sql, id, noInjection(category));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    private int addOutpatient_6_cpoe(long id, String cpoe) {
        String sql;
        sql = "Insert into \n"
                + "AP_OUTPATIENT_6_CPOE(ID, CPOE)\n"
                + "Values(%d, '%s')";
        sql = String.format(sql, id, noInjection(cpoe));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    private int addOutpatient_6_plan(long id, String plan) {
        String sql;
        sql = "Insert into \n"
                + "AP_OUTPATIENT_6_PLAN(ID, PLAN)\n"
                + "Values(%d, '%s')";
        sql = String.format(sql, id, noInjection(plan));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    public int delOutpatient_6(long ap_id) {
        int ret = 0;
        String sql;
        java.util.List<Map<String, Object>> lst = findOutpatient(ap_id, 6);
        for (Map<String, Object> item: lst) {
            long id = (long)item.get("id");
            sql = "Delete from AP_OUTPATIENT_6_CATEGORY\n"
                    + "Where (ID=%d)";
            sql = String.format(sql, id);
            ret += jdbcTemplate.update(sql);
            //----
            sql = "Delete from AP_OUTPATIENT_6_CPOE\n"
                    + "Where (ID=%d)";
            sql = String.format(sql, id);
            ret += jdbcTemplate.update(sql);
            //----
            sql = "Delete from AP_OUTPATIENT_6_PLAN\n"
                    + "Where (ID=%d)";
            sql = String.format(sql, id);
            ret += jdbcTemplate.update(sql);
        }
        //----
        sql = "Delete from AP_OUTPATIENT_6\n"
                + "Where (AP_ID=%d)";
        sql = String.format(sql, ap_id);
        ret += jdbcTemplate.update(sql);
        return ret;
    }
    
    //=== Outpatient7
    public long addOutpatient_7(long ap_id, int enable, String nhi_no, java.util.List<String> plans) {
        long newId = 0;
        if ((ap_id>0)&&(plans.size()>0)) {
            String sql, s1;
            sql = "Insert into \n"
                    + "AP_OUTPATIENT_7(ID, ENABLE, AP_ID, NHI_NO)\n"
                    + "Values(%d, %d, %d, %s)";
            for (int a=0; a<50; a++) {
                newId = newTableId_l("AP_OUTPATIENT_7", "ID");
                s1 = String.format(sql, newId, enable, ap_id, quotedNotNull(nhi_no));
                try {
                    int ret = jdbcTemplate.update(s1);
                    if (ret > 0) {
                        break;
                    }
                } catch(DataAccessException ex) {
                    newId = 0;
                }
                Utility.sleep(10);
            }
        }
        if (newId > 0) {
//            for (String trial : trials) {
//                addOutpatient_7_trial(newId, trial);
//            }
            for (String plan : plans) {
                addOutpatient_7_plan(newId, plan);
            }
        }
        return newId;
    }
    
    /*
    private int addOutpatient_7_trial(long id, String trial) {
        String sql;
        sql = "Insert into \n"
                + "AP_OUTPATIENT_7_TRIAL(ID, TRIAL)\n"
                + "Values(%d, '%s')";
        sql = String.format(sql, id, noInjection(trial));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    */
    
    private int addOutpatient_7_plan(long id, String plan) {
        String sql;
        sql = "Insert into \n"
                + "AP_OUTPATIENT_7_PLAN(ID, PLAN)\n"
                + "Values(%d, '%s')";
        sql = String.format(sql, id, noInjection(plan));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    public int delOutpatient_7(long ap_id) {
        int ret = 0;
        String sql;
        java.util.List<Map<String, Object>> lst = findOutpatient(ap_id, 7);
        for (Map<String, Object> item: lst) {
            long id = (long)item.get("id");
            sql = "Delete from AP_OUTPATIENT_7_TRIAL\n"
                    + "Where (ID=%d)";
            sql = String.format(sql, id);
            ret += jdbcTemplate.update(sql);
            //----
            sql = "Delete from AP_OUTPATIENT_7_PLAN\n"
                    + "Where (ID=%d)";
            sql = String.format(sql, id);
            ret += jdbcTemplate.update(sql);
        }
        //----
        sql = "Delete from AP_OUTPATIENT_7\n"
                + "Where (AP_ID=%d)";
        sql = String.format(sql, ap_id);
        ret += jdbcTemplate.update(sql);
        return ret;
    }
    
  //=== Inpatient_1
    public long addInpatient_1(long ap_id, int enable, java.util.List<String> categorys) {
        long newId = 0;
        if ((ap_id>0)&&(categorys.size()>0)) {
            String sql, s1;
            sql = "Insert into \n"
                    + "AP_INPATIENT_1(ID, ENABLE, AP_ID)\n"
                    + "Values(%d, %d, %d)";
            for (int a=0; a<50; a++) {
                newId = newTableId_l("AP_INPATIENT_1", "ID");
                s1 = String.format(sql, newId, enable, ap_id);
                try {
                    int ret = jdbcTemplate.update(s1);
                    if (ret > 0) {
                        break;
                    }
                } catch(DataAccessException ex) {
                    newId = 0;
                }
                Utility.sleep(10);
            }
        }
        if (newId > 0) {
            for (String category : categorys) {
                addInpatient_1_category(newId, category);
            }
        }
        return newId;
    }
    
    private int addInpatient_1_category(long id, String category) {
        String sql;
        sql = "Insert into\n"
                + "AP_INPATIENT_1_CATEGORY(ID, CATEGORY)\n"
                + "Values(%d, '%s')";
        sql = String.format(sql, id, noInjection(category));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    public int delInpatient_1(long ap_id) {
        int ret = 0;
        String sql;
        java.util.List<Map<String, Object>> lst = findInpatient(ap_id, 1);
        for (Map<String, Object> item: lst) {
            long id = (long)item.get("id");
            sql = "Delete from AP_INPATIENT_1_CATEGORY\n"
                    + "Where (ID=%d)";
            sql = String.format(sql, id);
            ret += jdbcTemplate.update(sql);
        }
        //----
        sql = "Delete from AP_INPATIENT_1\n"
                + "Where (AP_ID=%d)";
        sql = String.format(sql, ap_id);
        ret += jdbcTemplate.update(sql);
        return ret;
    }
    
  //=== Inpatient_2
    public long addInpatient_2(long ap_id, int enable, String nhi_no, java.util.List<String> cpoes) {
        long newId = 0;
        if ((ap_id>0)&&(cpoes.size()>0)) {
            String sql, s1;
            sql = "Insert into \n"
                    + "AP_INPATIENT_2(ID, ENABLE, AP_ID, NHI_NO)\n"
                    + "Values(%d, %d, %d, %s)";
            for (int a=0; a<50; a++) {
                newId = newTableId_l("AP_INPATIENT_2", "ID");
                s1 = String.format(sql, newId, enable, ap_id, quotedNotNull(nhi_no));
                try {
                    int ret = jdbcTemplate.update(s1);
                    if (ret > 0) {
                        break;
                    }
                } catch(DataAccessException ex) {
                    newId = 0;
                }
                Utility.sleep(10);
            }
        }
        if (newId > 0) {
            for (String cpoe : cpoes) {
                addInpatient_2_cpoe(newId, cpoe);
            }
        }
        return newId;
    }
    
    private int addInpatient_2_cpoe(long id, String cpoe) {
        String sql;
        sql = "Insert into \n"
                + "AP_INPATIENT_2_CPOE(ID, CPOE)\n"
                + "Values(%d, '%s')";
        sql = String.format(sql, id, noInjection(cpoe));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    public int delInpatient_2(long ap_id) {
        int ret = 0;
        String sql;
        java.util.List<Map<String, Object>> lst = findInpatient(ap_id, 2);
        for (Map<String, Object> item: lst) {
            long id = (long)item.get("id");
            sql = "Delete from AP_INPATIENT_2_CPOE\n"
                    + "Where (ID=%d)";
            sql = String.format(sql, id);
            ret += jdbcTemplate.update(sql);
        }
        //----
        sql = "Delete from AP_INPATIENT_2\n"
                + "Where (AP_ID=%d)";
        sql = String.format(sql, ap_id);
        ret += jdbcTemplate.update(sql);
        return ret;
    }
    
    //===Inpatient_3
    public long addInpatient_3(long ap_id, int enable, String nhi_no) {
        long newId = 0;
        if ((ap_id>0)&&(nhi_no.length()>0)) {
            String sql, s1;
            sql = "Insert into \n"
                    + "AP_INPATIENT_3(ID, ENABLE, AP_ID, NHI_NO)\n"
                    + "Values(%d, %d, %d, '%s')";
            for (int a=0; a<50; a++) {
                newId = newTableId_l("AP_INPATIENT_3", "ID");
                s1 = String.format(sql, newId, enable, ap_id, noInjection(nhi_no));
                try {
                    int ret = jdbcTemplate.update(s1);
                    if (ret > 0) {
                        break;
                    }
                } catch(DataAccessException ex) {
                    newId = 0;
                }
                Utility.sleep(10);
            }
        }
        return newId;
    }

    public int delInpatient_3(long ap_id) {
        int ret = 0;
        String sql;
        sql = "Delete from AP_INPATIENT_3\n"
              + "Where (AP_ID=%d)";
        sql = String.format(sql, ap_id);
        ret += jdbcTemplate.update(sql);
        return ret;
    }
    
  //=== Inpatient_6
    public long addInpatient_6(long ap_id, int enable, String nhi_no, java.util.List<String> categorys, 
            java.util.List<String> cpoes, java.util.List<String> plans) {
        long newId = 0;
        if ((ap_id>0)&&(categorys.size()>0)||(cpoes.size()>0)||(plans.size()>0)) {
            String sql, s1;
            sql = "Insert into \n"
                    + "AP_INPATIENT_6(ID, ENABLE, AP_ID, NHI_NO)\n"
                    + "Values(%d, %d, %d, %s)";
            for (int a=0; a<50; a++) {
                newId = newTableId_l("AP_INPATIENT_6", "ID");
                s1 = String.format(sql, newId, enable, ap_id, quotedNotNull(nhi_no));
                try {
                    int ret = jdbcTemplate.update(s1);
                    if (ret > 0) {
                        break;
                    }
                } catch(DataAccessException ex) {
                    newId = 0;
                }
                Utility.sleep(10);
            }
        }
        if (newId > 0) {
            for (String category : categorys) {
                addInpatient_6_category(newId, category);
            }
            for (String cpoe : cpoes) {
                addInpatient_6_cpoe(newId, cpoe);
            }
            for (String plan : plans) {
                addInpatient_6_plan(newId, plan);
            }
        }
        return newId;
    }
    
    private int addInpatient_6_category(long id, String category) {
        String sql;
        sql = "Insert into \n"
                + "AP_INPATIENT_6_CATEGORY(ID, CATEGORY)\n"
                + "Values(%d, '%s')";
        sql = String.format(sql, id, noInjection(category));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    private int addInpatient_6_cpoe(long id, String cpoe) {
        String sql;
        sql = "Insert into \n"
                + "AP_INPATIENT_6_CPOE(ID, CPOE)\n"
                + "Values(%d, '%s')";
        sql = String.format(sql, id, noInjection(cpoe));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    private int addInpatient_6_plan(long id, String plan) {
        String sql;
        sql = "Insert into \n"
                + "AP_INPATIENT_6_PLAN(ID, PLAN)\n"
                + "Values(%d, '%s')";
        sql = String.format(sql, id, noInjection(plan));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    public int delInpatient_6(long ap_id) {
        int ret = 0;
        String sql;
        java.util.List<Map<String, Object>> lst = findInpatient(ap_id, 6);
        for (Map<String, Object> item: lst) {
            long id = (long)item.get("id");
            sql = "Delete from AP_INPATIENT_6_CATEGORY\n"
                    + "Where (ID=%d)";
            sql = String.format(sql, id);
            ret += jdbcTemplate.update(sql);
            //----
            sql = "Delete from AP_INPATIENT_6_CPOE\n"
                    + "Where (ID=%d)";
            sql = String.format(sql, id);
            ret += jdbcTemplate.update(sql);
            //----
            sql = "Delete from AP_INPATIENT_6_PLAN\n"
                    + "Where (ID=%d)";
            sql = String.format(sql, id);
            ret += jdbcTemplate.update(sql);
        }
        //----
        sql = "Delete from AP_INPATIENT_6\n"
                + "Where (AP_ID=%d)";
        sql = String.format(sql, ap_id);
        ret += jdbcTemplate.update(sql);
        return ret;
    }
    
    //---------------------
    public java.util.List<Map<String, Object>> findOutpatient(long ap_id, int sn) {
        String sql;
        sql = "Select *\n"
                + "From AP_OUTPATIENT_%d\n"
                + "Where (AP_ID=%d)";
        sql = String.format(sql, sn, ap_id);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.listLowerCase(lst);
        } else {
            return new java.util.ArrayList<Map<String, Object>>();
        }
    }
    
    public java.util.List<String> findOutpatientField(long mid, int sn, String field) {
        field = field.toUpperCase();
        java.util.List<String> retList = new java.util.ArrayList<String>();
        String sql;
        sql = "Select %s\n"
                + "From AP_OUTPATIENT_%d_%s\n"
                + "Where (ID=%d)";
        sql = String.format(sql, field, sn, field, mid);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            for (Map<String, Object> item : lst) {
                retList.add(item.get(field).toString());
            }
        } 
        return retList;
    }
    
    public java.util.List<Map<String, Object>> findInpatient(long ap_id, int sn) {
        String sql;
        sql = "Select *\n"
                + "From AP_INPATIENT_%d\n"
                + "Where (AP_ID=%d)";
        sql = String.format(sql, sn, ap_id);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.listLowerCase(lst);
        } else {
            return new java.util.ArrayList<Map<String, Object>>();
        }
    }
    
    public java.util.List<String> findInpatientField(long mid, int sn, String field) {
        field = field.toUpperCase();
        java.util.List<String> retList = new java.util.ArrayList<String>();
        String sql;
        sql = "Select %s\n"
                + "From AP_INPATIENT_%d_%s\n"
                + "Where (ID=%d)";
        sql = String.format(sql, field, sn, field, mid);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            for (Map<String, Object> item : lst) {
                retList.add(item.get(field).toString());
            }
        } 
        return retList;
    }
    
    //---------
    public java.util.List<Map<String, Object>> findAdditionalPoint(int syear1, int syear2) {
        String sql;
        sql = "Select ID, SYEAR, START_DATE, END_DATE\r\n"
                + "From AP_ADDITIONAL_POINT\r\n"
                + "Where (SYEAR=%d) or (SYEAR=%d)\r\n"
                + "Order By START_DATE";
        
        sql = String.format(sql, syear1, syear2);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        return Utility.listLowerCase(lst);
    }
    
    
    public int updateAdditionalPointStartDate(long ap_id, java.util.Date startDate) {
        String strStart = Utility.dateFormat(startDate, "yyyy/MM/dd");
        String sql;
        sql = "UPDATE AP_ADDITIONAL_POINT\r\n"
                + "SET START_DATE='%s'\r\n"
                + "WHERE (ID=%d)";
        sql = String.format(sql, strStart, ap_id);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    public int updateAdditionalPointEndDate(long ap_id, java.util.Date endDate) {
        String strEnd = Utility.dateFormat(endDate, "yyyy/MM/dd");
        String sql;
        sql = "UPDATE AP_ADDITIONAL_POINT\r\n"
                + "SET END_DATE='%s'\r\n"
                + "WHERE (ID=%d)";
        sql = String.format(sql, strEnd, ap_id);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    


}
