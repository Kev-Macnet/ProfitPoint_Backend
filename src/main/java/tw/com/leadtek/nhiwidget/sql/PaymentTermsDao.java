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
public class PaymentTermsDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;


    public java.util.List<Map<String, Object>> searchPaymentTerms(String feeNo, String nhiNo, String category, 
            java.util.Date startDate, java.util.Date endDate) {
        String strStart = Utility.dateFormat(startDate, "yyyy/MM/dd");
        String strEnd = Utility.dateFormat(endDate, "yyyy/MM/dd");
        
        String sql;
        sql = "SELECT ID, FEE_NO, FEE_NAME, NHI_NO, NHI_NAME, START_DATE, END_DATE, CATEGORY, HOSPITAL_TYPE, OUTPATIENT_TYPE, HOSPITALIZED_TYPE\r\n"
                + "FROM PT_PAYMENT_TERMS\r\n"
                + "Where (1=1)\r\n"
                + " -- and (FEE_NO like '%s%%')\r\n"
                + " -- and (NHI_NO  like '%s%%')\r\n"
                + " -- and (CATEGORY='%s')\r\n"
                + " -- and (START_DATE='%s')\r\n"
                + " -- and (END_DATE='%s')";
        sql = String.format(sql, feeNo, nhiNo, category, strStart,strEnd, strStart,strEnd, strStart,strEnd);
        if (feeNo.length()>0) {
            sql=sql.replace("-- and (FEE_NO", " and (FEE_NO");
        }
        if (nhiNo.length()>0) {
            sql=sql.replace("-- and (NHI_NO", " and (NHI_NO");
        }
        if (category.length()>0) {
            sql=sql.replace("-- and (CATEGORY", " and (CATEGORY");
        }
        if (strStart.length()>0) {
          sql=sql.replace("-- and (START_DATE=", " and (START_DATE=");
        }
        if (strEnd.length()>0) {
          sql=sql.replace("-- and (END_DATE=", " and (END_DATE=");
        }
        logger.info(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        
        return Utility.listLowerCase(lst);
    }
    
    public java.util.Map<String, Object> findPaymentTerms(long id, String category) {
        String sql;
        sql = "Select ID, FEE_NO, FEE_NAME, NHI_NO, NHI_NAME, START_DATE, END_DATE, CATEGORY, HOSPITAL_TYPE, OUTPATIENT_TYPE, HOSPITALIZED_TYPE\r\n"
                + "From PT_PAYMENT_TERMS\r\n"
                + "Where (ID=%d)\n"
                + "  and (CATEGORY='%s')";
        sql = String.format(sql, id, category);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.mapLowerCase(lst.get(0));
        } else {
            return new java.util.HashMap<String, Object>();
        }
        
    }
    
    public long addPaymentTerms(String fee_no, String fee_name, String nhi_no, String nhi_name, 
                    java.util.Date start_date, java.util.Date end_date, 
                    String category, int hospital_type, int outpatient_type, int hospitalized_type) {
        String strStart = Utility.dateFormat(start_date, "yyyy/MM/dd");
        String strEnd = Utility.dateFormat(end_date, "yyyy/MM/dd");
        long newId=0;
        String sql, s1;
        sql = "Insert into \r\n"
                + "PT_PAYMENT_TERMS (ID, FEE_NO, FEE_NAME, NHI_NO, NHI_NAME, START_DATE,END_DATE, CATEGORY, HOSPITAL_TYPE, OUTPATIENT_TYPE, HOSPITALIZED_TYPE)\r\n"
                + "Values(%d, %s, %s, %s, %s, '%s','%s', '%s', %d, %d, %d)";
        for (int a=0; a<50; a++) {
            newId = newTableId_l("PT_PAYMENT_TERMS", "ID");
            s1 = String.format(sql, newId, Utility.quotedNotNull(fee_no), Utility.quotedNotNull(fee_name), 
                    Utility.quotedNotNull(nhi_no), Utility.quotedNotNull(nhi_name), strStart, strEnd, 
                    category, hospital_type, outpatient_type, hospitalized_type);
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
    
    public int updatePaymentTerms(long id, String fee_no, String fee_name, String nhi_no, String nhi_name, 
            java.util.Date start_date, java.util.Date end_date, 
            String category, int hospital_type, int outpatient_type, int hospitalized_type) {
        String strStart = Utility.dateFormat(start_date, "yyyy/MM/dd");
        String strEnd = Utility.dateFormat(end_date, "yyyy/MM/dd");
        String sql;
        sql = "Update PT_PAYMENT_TERMS\r\n"
                + "Set FEE_NO=%s, \r\n"
                + "    FEE_NAME=%s, \r\n"
                + "    NHI_NO=%s, \r\n"
                + "    NHI_NAME=%s, \r\n"
                + "    START_DATE='%s', \r\n"
                + "    END_DATE='%s', \r\n"
                + " --   CATEGORY='%s', \r\n"
                + "    HOSPITAL_TYPE=%d, \r\n"
                + "    OUTPATIENT_TYPE=%d, \r\n"
                + "    HOSPITALIZED_TYPE=%d\r\n"
                + "Where (ID=%d)and(CATEGORY='%s')";
        sql = String.format(sql, Utility.quotedNotNull(fee_no), Utility.quotedNotNull(fee_name), 
                Utility.quotedNotNull(nhi_no), Utility.quotedNotNull(nhi_name), strStart, strEnd, 
                category, hospital_type, outpatient_type, hospitalized_type, id, category);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    public int deletePaymentTerms(long id, String category) {
        String sql;
        sql = "Delete from PT_PAYMENT_TERMS\r\n"
                + "Where (ID=%d)and(CATEGORY='%s')";
        sql = String.format(sql, id, category);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    //===
    
    public int deleteExcludeNhiNo(long ptId) {
        String sql;
        sql = "Delete from PT_EXCLUDE_NHI_NO\r\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<String> filterExcludeNhiNo(long ptId) {
        String sql;
        sql = "Select NHI_NO\r\n"
                + "From PT_EXCLUDE_NHI_NO\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        java.util.List<String> retList = new java.util.ArrayList<String>();
        for (Map<String, Object> item : lst) {
            retList.add(item.get("NHI_NO").toString());
        }
        return retList;
    }
    
    public int addExcludeNhiNo(long ptId, java.util.List<String> lstNhiNo) {
        int ret = 0;
        String sql;
        sql = "Insert into \r\n"
                + "PT_EXCLUDE_NHI_NO (PT_ID, NHI_NO)\r\n"
                + "Values(%d, '%s')";
        for (String nhiNo : lstNhiNo) {
            String s1=String.format(sql, ptId, nhiNo);
            try {
                ret += jdbcTemplate.update(s1);
            } catch(DataAccessException ex) {
                //
            }
        }
        return ret;
    }
    
    //===
    public int deleteLimDivision(long ptId) {
        String sql;
        sql = "Delete from PT_LIM_DIVISION\r\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<String> filterLimDivision(long ptId) {
        String sql;
        sql = "Select DIVISION\r\n"
                + "From PT_LIM_DIVISION\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        java.util.List<String> retList = new java.util.ArrayList<String>();
        for (Map<String, Object> item : lst) {
            retList.add(item.get("DIVISION").toString());
        }
        return retList;
    }
    
    public int addLimDivision(long ptId, java.util.List<String> lstDivision) {
        int ret = 0;
        String sql;
        sql = "Insert into \r\n"
                + "PT_LIM_DIVISION (PT_ID, DIVISION)\r\n"
                + "Values(%d, '%s')";
        for (String division : lstDivision) {
            String s1=String.format(sql, ptId, division);
            try {
                ret += jdbcTemplate.update(s1);
            } catch(DataAccessException ex) {
                //
            }
        }
        return ret;
    }
    
    public java.util.Map<String, Object> findUser(String userName) {
        String sql;
        sql = "Select ID, USERNAME, DISPLAY_NAME, EMAIL, STATUS, \"ROLE\"\r\n"
                + "From USER\r\n"
                + "Where (USERNAME='%s')";
        sql = String.format(sql, userName);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.mapLowerCase(lst.get(0));
        } else {
            return null;
        }
    }
    
    public int findUserRole(String userName) {
        int ret = 0;
        java.util.Map<String, Object> user = findUser(userName);
        if (user != null) {
            ret = (int)user.get("role");
        }
        return ret;
    }

    //===
    public long newTableId_l(String tbName, String fdName) {
        long lastID = 0;
        String s1;
        s1 = "Select Max(" + fdName + ") as lastid \n" +
             "From " + tbName;
        try {
            lastID = jdbcTemplate.queryForObject(s1, Long.class);
        } catch (java.lang.NullPointerException e) {
            lastID = 0;
        }
        return (lastID + 1);
    }


}
