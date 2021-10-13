package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
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
        sql = "Select ID, FEE_NO, FEE_NAME, NHI_NO, NHI_NAME, START_DATE, END_DATE, CATEGORY, OUTPATIENT_TYPE, HOSPITALIZED_TYPE\r\n"
                + "From PT_PAYMENT_TERMS\r\n"
                + "Where (1=1)\r\n"
                + " -- and (FEE_NO like '%s%%')\r\n"
                + " -- and (NHI_NO  like '%s%%')\r\n"
                + " -- and (CATEGORY='%s')\r\n"
                + " -- and (START_DATE='%s')\r\n"
                + " -- and (END_DATE='%s')";
        sql = String.format(sql, feeNo, nhiNo, category, strStart, strEnd);
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
        lst = Utility.listLowerCase(lst);
        for (Map<String, Object> item : lst) {
            item.put("hospital_type", filterHospitalType((long)item.get("id")));
        }

        return lst;
    }
    

    public java.util.List<Map<String, Object>> searchPaymentTermsByDateRange(String category, String feeNo, String nhiNo, 
            java.util.Date startDate, java.util.Date endDate) {
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
        sql = "Select ID, FEE_NO, FEE_NAME, NHI_NO, NHI_NAME, START_DATE, END_DATE, CATEGORY, OUTPATIENT_TYPE, HOSPITALIZED_TYPE\r\n"
                + "From PT_PAYMENT_TERMS\r\n"
                + "Where (1=1)\n"
                + " -- and (CATEGORY='%s')\n"
                + " -- and (FEE_NO='%s')\n"
                + " -- and (NHI_NO='%s')\n"
                + " and (START_DATE BETWEEN '%s' and '%s')\n"
                + " and (END_DATE BETWEEN '%s' and '%s')\n";
        
        sql = String.format(sql, category, feeNo, nhiNo, strStart, strEnd, strStart, strEnd);
        if (category.length()>0) {
            sql = sql.replace("-- and (CATEGORY", " and (CATEGORY");
        }
        if (feeNo.length()>0) {
            sql = sql.replace("-- and (FEE_NO", " and (FEE_NO");
        }
        if (nhiNo.length()>0) {
            sql = sql.replace("-- and (NHI_NO", " and (NHI_NO");
        }
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        lst = Utility.listLowerCase(lst);
        for (Map<String, Object> item : lst) {
            item.put("hospital_type", filterHospitalType((long)item.get("id")));
        }
        return lst;
    }
    
    public java.util.Map<String, Object> findPaymentTerms(long id, String category) {
        String sql;
//        sql= "Insert into\r\n"
//                + "PT_PAYMENT_TERMS(ID, FEE_NO, FEE_NAME, NHI_NO, NHI_NAME, START_DATE, END_DATE, CATEGORY, OUTPATIENT_TYPE, HOSPITALIZED_TYPE)\r\n"
//                + "Values(0, '', '', '', '', CURRENT_DATE, '', '', 0, 0)";
        sql = "Select ID, FEE_NO, FEE_NAME, NHI_NO, NHI_NAME, START_DATE, END_DATE, CATEGORY, OUTPATIENT_TYPE, HOSPITALIZED_TYPE\r\n"
                + "From PT_PAYMENT_TERMS\r\n"
                + "Where (ID=%d) and (CATEGORY='%s')";
        sql = String.format(sql, id, category);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            java.util.Map<String, Object> retMap = Utility.mapLowerCase(lst.get(0));
            retMap.put("hospital_type", filterHospitalType(id));
            return retMap;
        } else {
            return java.util.Collections.emptyMap();
        }
    }
    
    public long addPaymentTerms(String fee_no, String fee_name, String nhi_no, String nhi_name, 
                    java.util.Date start_date, java.util.Date end_date, 
                    String category, java.util.List<String> hospital_type, int outpatient_type, int hospitalized_type) {
        String strStart = Utility.dateFormat(start_date, "yyyy/MM/dd");
        String strEnd = Utility.dateFormat(end_date, "yyyy/MM/dd");
        long newId=0;
        String sql, s1;
        
        sql = "Insert into \r\n"
                + "PT_PAYMENT_TERMS (ID, FEE_NO, FEE_NAME, NHI_NO, NHI_NAME, START_DATE, END_DATE, CATEGORY, OUTPATIENT_TYPE, HOSPITALIZED_TYPE)\r\n"
                + "Values(%d, %s, %s, %s, %s, '%s','%s', '%s', %d, %d)";
        for (int a=0; a<50; a++) {
            newId = newTableId_l("PT_PAYMENT_TERMS", "ID");
            //fee_no, fee_name, nhi_no, nhi_name, start_date, end_date, category, outpatient_type, hospitalized_type
            s1 = String.format(sql, newId, Utility.quotedNotNull(fee_no), Utility.quotedNotNull(fee_name), 
                    Utility.quotedNotNull(nhi_no), Utility.quotedNotNull(nhi_name), strStart, strEnd, 
                    category, outpatient_type, hospitalized_type);
            try {
                int ret = jdbcTemplate.update(s1);
                if (ret > 0) {
                    addHospitalType(newId, hospital_type);
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
            String category, java.util.List<String> hospital_type, int outpatient_type, int hospitalized_type) {
        String strStart = Utility.dateFormat(start_date, "yyyy/MM/dd");
        String strEnd = Utility.dateFormat(end_date, "yyyy/MM/dd");
        String sql;
        /*
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
                */
        sql = "Update PT_PAYMENT_TERMS\r\n"
                + "Set FEE_NO=%s, \r\n"
                + "    FEE_NAME=%s, \r\n"
                + "    NHI_NO=%s, \r\n"
                + "    NHI_NAME=%s, \r\n"
                + "    START_DATE='%s', \r\n"
                + "    END_DATE='%s', \r\n"
                + "    OUTPATIENT_TYPE=%d, \r\n"
                + "    HOSPITALIZED_TYPE=%d\r\n"
                + "Where (ID=%d)and(CATEGORY='%s')";
        sql = String.format(sql, Utility.quotedNotNull(fee_no), Utility.quotedNotNull(fee_name), 
                Utility.quotedNotNull(nhi_no), Utility.quotedNotNull(nhi_name), strStart, strEnd, 
                outpatient_type, hospitalized_type, id, category);
        int ret = jdbcTemplate.update(sql);
        //----
        if (ret > 0) {
            deleteHospitalType(id);
            addHospitalType(id, hospital_type);
        }
        return ret;
    }
    
    public int deletePaymentTerms(long id, String category) {
        String sql;
        sql = "Delete from PT_PAYMENT_TERMS\r\n"
                + "Where (ID=%d)and(CATEGORY='%s')";
        sql = String.format(sql, id, category);
        int ret =  jdbcTemplate.update(sql);
        if (ret >0) {
            deleteHospitalType(id);
        }
        return ret;
    }
    
    //=== ExcludeNhiNo
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
    
    //=== CoexistNhiNo
    public int deleteCoexistNhiNo(long ptId) {
        String sql;
        sql = "Delete from PT_COEXIST_NHI_NO\r\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<String> filterCoexistNhiNo(long ptId) {
        String sql;
        sql = "Select NHI_NO\r\n"
                + "From PT_COEXIST_NHI_NO\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        java.util.List<String> retList = new java.util.ArrayList<String>();
        for (Map<String, Object> item : lst) {
            retList.add(item.get("NHI_NO").toString());
        }
        return retList;
    }
    
    public int addCoexistNhiNo(long ptId, java.util.List<String> lstNhiNo) {
        int ret = 0;
        String sql;
        sql = "Insert into \r\n"
                + "PT_COEXIST_NHI_NO (PT_ID, NHI_NO)\r\n"
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
    
    //=== NotifyNhiNo
    public int deleteNotifyNhiNo(long ptId) {
        String sql;
        sql = "Delete from PT_NOTIFY_NHI_NO\r\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<String> filterNotifyNhiNo(long ptId) {
        String sql;
        sql = "Select NHI_NO\r\n"
                + "From PT_NOTIFY_NHI_NO\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        java.util.List<String> retList = new java.util.ArrayList<String>();
        for (Map<String, Object> item : lst) {
            retList.add(item.get("NHI_NO").toString());
        }
        return retList;
    }
    
    public int addNotifyNhiNo(long ptId, java.util.List<String> lstNhiNo) {
        int ret = 0;
        String sql;
        sql = "Insert into \r\n"
                + "PT_NOTIFY_NHI_NO (PT_ID, NHI_NO)\r\n"
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
    
    
    //=== DrgNo
    /*
    SELECT PT_ID, DRG_NO
    FROM PT_DRG_NO
    */
    public int deleteDrgNo(long ptId) {
        String sql;
        sql = "Delete from PT_DRG_NO\r\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<String> filterDrgNo(long ptId) {
        String sql;
        sql = "Select DRG_NO\r\n"
                + "From PT_DRG_NO\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        java.util.List<String> retList = new java.util.ArrayList<String>();
        for (Map<String, Object> item : lst) {
            retList.add(item.get("DRG_NO").toString());
        }
        return retList;
    }
    
    public int addDrgNo(long ptId, java.util.List<String> lstDrgNo) {
        int ret = 0;
        String sql;
        sql = "Insert into \r\n"
                + "PT_DRG_NO (PT_ID, DRG_NO)\r\n"
                + "Values(%d, '%s')";
        for (String drgNo : lstDrgNo) {
            String s1=String.format(sql, ptId, drgNo);
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
    
  //===
    public int deleteIncludeIcdNo(long ptId) {
        String sql;
        sql = "Delete from PT_INCLUDE_ICD_NO\r\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<String> filterIncludeIcdNo(long ptId) {
        String sql;
        sql = "Select ICD_NO\r\n"
                + "From PT_INCLUDE_ICD_NO\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        java.util.List<String> retList = new java.util.ArrayList<String>();
        for (Map<String, Object> item : lst) {
            retList.add(item.get("ICD_NO").toString());
        }
        return retList;
    }
    
    public int addIncludeIcdNo(long ptId, java.util.List<String> lstIcdNo) {
        int ret = 0;
        String sql;
        sql = "Insert into \r\n"
                + "PT_INCLUDE_ICD_NO (PT_ID, ICD_NO)\r\n"
                + "Values(%d, '%s')";
        for (String icdNo : lstIcdNo) {
            String s1=String.format(sql, ptId, icdNo);
            try {
                ret += jdbcTemplate.update(s1);
            } catch(DataAccessException ex) {
                //
            }
        }
        return ret;
    }
    
    
    
  //=== PT_NOT_ALLOW_PLAN  
    public int deleteNotAllowPlan(long ptId) {
        String sql;
        sql = "Delete from PT_NOT_ALLOW_PLAN\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<String> filterNotAllowPlan(long ptId) {
        String sql;
        sql = "Select PLAN\r\n"
                + "From PT_NOT_ALLOW_PLAN\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        java.util.List<String> retList = new java.util.ArrayList<String>();
        for (Map<String, Object> item : lst) {
            retList.add(item.get("PLAN").toString());
        }
        return retList;
    }
    
    public int addNotAllowPlan(long ptId, java.util.List<String> lstPlan) {
        int ret = 0;
        String sql;
        sql = "Insert into \r\n"
                + "PT_NOT_ALLOW_PLAN (PT_ID, PLAN)\r\n"
                + "Values(%d, '%s')";
        for (String plan : lstPlan) {
            String s1=String.format(sql, ptId, plan);
            try {
                ret += jdbcTemplate.update(s1);
            } catch(DataAccessException ex) {
                //
            }
        }
        return ret;
    }
    
  //=== pt_hospital_type 醫院層級
    public int deleteHospitalType(long ptId) {
        String sql;
        sql = "Delete from PT_HOSPITAL_TYPE\r\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<String> filterHospitalType(long ptId) {
        String sql;
        sql = "Select HOSPITAL_TYPE\r\n"
                + "From PT_HOSPITAL_TYPE\r\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        java.util.List<String> retList = new java.util.ArrayList<String>();
        for (Map<String, Object> item : lst) {
            retList.add(item.get("HOSPITAL_TYPE").toString());
        }
        return retList;
    }
    
    public int addHospitalType(long ptId, java.util.List<String> lstHospitalType) {
        int ret = 0;
        String sql;
        sql = "Insert into \r\n"
                + "PT_HOSPITAL_TYPE(PT_ID, HOSPITAL_TYPE)\r\n"
                + "Values(%d, '%s')";
        for (String hospitalType : lstHospitalType) {
            String s1=String.format(sql, ptId, hospitalType);
            try {
                ret += jdbcTemplate.update(s1);
            } catch(DataAccessException ex) {
                //
            }
        }
        return ret;
    }
    
    //===
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
