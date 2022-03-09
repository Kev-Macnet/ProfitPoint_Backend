package tw.com.leadtek.nhiwidget.sql;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import tw.com.leadtek.nhiwidget.dto.ptNhiNoTimes;
import tw.com.leadtek.tools.Utility;


@Repository
public class PaymentTermsDao extends BaseSqlDao {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    
    public long searchPaymentTermsCount(String feeNo, String nhiNo, String category, 
            java.util.Date startDate, java.util.Date endDate) {
        String strStart = Utility.dateFormat(startDate, "yyyy/MM/dd");
        String strEnd = Utility.dateFormat(endDate, "yyyy/MM/dd");
        
        String sql;
        sql = "Select Count(*) as CNT\n"
                + "From PT_PAYMENT_TERMS\n"
                + "Where (1=1)\n"
                + " -- and (FEE_NO like '%s%%')\n"
                + " -- and (NHI_NO  like '%s%%')\n"
                + " -- and (CATEGORY='%s')\n"
                + " -- and (START_DATE='%s')\n"
                + " -- and (END_DATE='%s')";
        sql = String.format(sql, noInjection(feeNo), noInjection(nhiNo), noInjection(category), strStart, strEnd);
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
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return (long)lst.get(0).get("CNT");
        } else {
            return 0l;
        }
    }

    public java.util.List<Map<String, Object>> searchPaymentTerms(String feeNo, String nhiNo, String category, 
            java.util.Date startDate, java.util.Date endDate, int start, int pageSize,
            String sortField, String sortDirection) {
        String strStart = Utility.dateFormat(startDate, "yyyy/MM/dd");
        String strEnd = Utility.dateFormat(endDate, "yyyy/MM/dd");
        
        String sql;
        sql = "Select ID, ACTIVE, FEE_NO, FEE_NAME, NHI_NO, NHI_NAME, START_DATE, END_DATE, CATEGORY, OUTPATIENT_TYPE, HOSPITALIZED_TYPE\n"
                + "From PT_PAYMENT_TERMS\n"
                + "Where (1=1)\n"
                + " -- and (FEE_NO like '%s%%')\n"
                + " -- and (NHI_NO  like '%s%%')\n"
                + " -- and (CATEGORY='%s')\n"
                + " -- and (START_DATE='%s')\n"
                + " -- and (END_DATE='%s')\n"
//                + "Order By ID\n"
                + "Order By %s %s\n"
                + "limit %d offset %d";
        sql = String.format(sql, noInjection(feeNo), noInjection(nhiNo), noInjection(category), strStart, strEnd, 
                sortField, sortDirection, pageSize, start);
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
//        System.out.println("sql-95="+sql);
        logger.trace(sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        lst = Utility.listLowerCase(lst);
        for (Map<String, Object> item : lst) {
            item.put("hospital_type", filterHospitalType((long)item.get("id")));
        }

        return lst;
    }
    
    
    public long searchPaymentTermsByDateRangeCount(String feeNo, String nhiNo, String category, 
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
        sql = "Select Count(*) as CNT\n"
                + "From PT_PAYMENT_TERMS\n"
                + "Where (1=1)\n"
                + " -- and (CATEGORY='%s')\n"
                + " -- and (FEE_NO='%s')\n"
                + " -- and (NHI_NO='%s')\n"
                + " and (START_DATE BETWEEN '%s' and '%s')\n"
                + " and (END_DATE BETWEEN '%s' and '%s')";
        sql = String.format(sql, noInjection(category), noInjection(feeNo), noInjection(nhiNo), strStart, strEnd, strStart, strEnd);
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
        if (lst.size()>0) {
            return (long)lst.get(0).get("CNT");
        } else {
            return 0l;
        }
    }

    public java.util.List<Map<String, Object>> searchPaymentTermsByDateRange(String feeNo, String nhiNo, String category,
            java.util.Date startDate, java.util.Date endDate, int start, int pageSize,
            String sortField, String sortDirection) {
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
        sql = "Select ID, ACTIVE, FEE_NO, FEE_NAME, NHI_NO, NHI_NAME, START_DATE, END_DATE, CATEGORY, OUTPATIENT_TYPE, HOSPITALIZED_TYPE\n"
                + "From PT_PAYMENT_TERMS\n"
                + "Where (1=1)\n"
                + " -- and (CATEGORY='%s')\n"
                + " -- and (FEE_NO='%s')\n"
                + " -- and (NHI_NO='%s')\n"
                + " and (START_DATE BETWEEN '%s' and '%s')\n"
                + " and (END_DATE BETWEEN '%s' and '%s')\n"
//                + "Order By ID\n"
                + "Order By %s %s\n"
                + "limit %d offset %d";
        
        sql = String.format(sql, noInjection(category), noInjection(feeNo), noInjection(nhiNo), strStart, strEnd, strStart, strEnd, 
                sortField, sortDirection, pageSize, start);
        if (category.length()>0) {
            sql = sql.replace("-- and (CATEGORY", " and (CATEGORY");
        }
        if (feeNo.length()>0) {
            sql = sql.replace("-- and (FEE_NO", " and (FEE_NO");
        }
        if (nhiNo.length()>0) {
            sql = sql.replace("-- and (NHI_NO", " and (NHI_NO");
        }
//        System.out.println("sql-183="+sql);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        lst = Utility.listLowerCase(lst);
        for (Map<String, Object> item : lst) {
            item.put("hospital_type", filterHospitalType((long)item.get("id")));
        }
        return lst;
    }
    
    public java.util.Map<String, Object> findPaymentTerms(long id, String category) {
        String sql;
        sql = "Select ID, ACTIVE, FEE_NO, FEE_NAME, NHI_NO, NHI_NAME, START_DATE, END_DATE, CATEGORY, OUTPATIENT_TYPE, HOSPITALIZED_TYPE\n"
                + "From PT_PAYMENT_TERMS\n"
                + "Where (ID=%d) and (CATEGORY='%s')";
        sql = String.format(sql, id, noInjection(category));
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
//        System.out.println("category="+category+", "+id);
//        System.out.println(lst);
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
        
        sql = "Insert into \n"
                + "PT_PAYMENT_TERMS (ID, FEE_NO, FEE_NAME, NHI_NO, NHI_NAME, START_DATE, END_DATE, CATEGORY, OUTPATIENT_TYPE, HOSPITALIZED_TYPE)\n"
                + "Values(%d, %s, %s, %s, %s, '%s','%s', '%s', %d, %d)";
        for (int a=0; a<50; a++) {
            newId = newTableId_l("PT_PAYMENT_TERMS", "ID");
            //fee_no, fee_name, nhi_no, nhi_name, start_date, end_date, category, outpatient_type, hospitalized_type
            s1 = String.format(sql, newId, quotedNotNull(fee_no), quotedNotNull(fee_name), 
                    quotedNotNull(nhi_no), quotedNotNull(nhi_name), strStart, strEnd, 
                    noInjection(category), outpatient_type, hospitalized_type);
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
        sql = "Update PT_PAYMENT_TERMS\n"
                + "Set FEE_NO=%s, \n"
                + "    FEE_NAME=%s, \n"
                + "    NHI_NO=%s, \n"
                + "    NHI_NAME=%s, \n"
                + "    START_DATE='%s', \n"
                + "    END_DATE='%s', \n"
                + "    OUTPATIENT_TYPE=%d, \n"
                + "    HOSPITALIZED_TYPE=%d\n"
                + "Where (ID=%d)and(CATEGORY='%s')";
        sql = String.format(sql, quotedNotNull(fee_no), quotedNotNull(fee_name), 
                quotedNotNull(nhi_no), quotedNotNull(nhi_name), strStart, strEnd, 
                outpatient_type, hospitalized_type, id, noInjection(category));
        int ret = jdbcTemplate.update(sql);
        //----
        if (ret > 0) {
            deleteHospitalType(id);
            addHospitalType(id, hospital_type);
        }
        return ret;
    }
    
    public int updatePaymentTermsActive(long id, String category, int state) {
        String sql;
        sql = "Update PT_PAYMENT_TERMS\n"
                + "Set ACTIVE=%d\n"
                + "Where (ID=%d)and(CATEGORY='%s')";
        sql = String.format(sql, state, id, noInjection(category));
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    public int deletePaymentTerms(long id, String category) {
        String sql;
        sql = "Delete from PT_PAYMENT_TERMS\n"
                + "Where (ID=%d)and(CATEGORY='%s')";
        sql = String.format(sql, id, noInjection(category));
        int ret =  jdbcTemplate.update(sql);
        if (ret >0) {
            deleteHospitalType(id);
        }
        return ret;
    }
    
    //=== ExcludeNhiNo
    public int deleteExcludeNhiNo(long ptId) {
        String sql;
        sql = "Delete from PT_EXCLUDE_NHI_NO\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<String> filterExcludeNhiNo(long ptId) {
        String sql;
        sql = "Select NHI_NO\n"
                + "From PT_EXCLUDE_NHI_NO\n"
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
        sql = "Insert into \n"
                + "PT_EXCLUDE_NHI_NO (PT_ID, NHI_NO)\n"
                + "Values(%d, '%s')";
        for (String nhiNo : lstNhiNo) {
            String s1=String.format(sql, ptId, noInjection(nhiNo));
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
        sql = "Delete from PT_COEXIST_NHI_NO\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<String> filterCoexistNhiNo(long ptId) {
        String sql;
        sql = "Select NHI_NO\n"
                + "From PT_COEXIST_NHI_NO\n"
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
        sql = "Insert into \n"
                + "PT_COEXIST_NHI_NO (PT_ID, NHI_NO)\n"
                + "Values(%d, '%s')";
        for (String nhiNo : lstNhiNo) {
            String s1=String.format(sql, ptId, noInjection(nhiNo));
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
        sql = "Delete from PT_NOTIFY_NHI_NO\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<String> filterNotifyNhiNo(long ptId) {
        String sql;
        sql = "Select NHI_NO\n"
                + "From PT_NOTIFY_NHI_NO\n"
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
        sql = "Insert into \n"
                + "PT_NOTIFY_NHI_NO (PT_ID, NHI_NO)\n"
                + "Values(%d, '%s')";
        for (String nhiNo : lstNhiNo) {
            String s1=String.format(sql, ptId, noInjection(nhiNo));
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
        sql = "Delete from PT_DRG_NO\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<String> filterDrgNo(long ptId) {
        String sql;
        sql = "Select DRG_NO\n"
                + "From PT_DRG_NO\n"
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
        sql = "Insert into \n"
                + "PT_DRG_NO (PT_ID, DRG_NO)\n"
                + "Values(%d, '%s')";
        for (String drgNo : lstDrgNo) {
            String s1=String.format(sql, ptId, noInjection(drgNo));
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
        sql = "Delete from PT_LIM_DIVISION\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<String> filterLimDivision(long ptId) {
        String sql;
        sql = "Select DIVISION\n"
                + "From PT_LIM_DIVISION\n"
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
        sql = "Insert into \n"
                + "PT_LIM_DIVISION (PT_ID, DIVISION)\n"
                + "Values(%d, '%s')";
        for (String division : lstDivision) {
            String s1=String.format(sql, ptId, noInjection(division));
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
        sql = "Delete from PT_INCLUDE_ICD_NO\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<String> filterIncludeIcdNo(long ptId) {
        String sql;
        sql = "Select ICD_NO\n"
                + "From PT_INCLUDE_ICD_NO\n"
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
        sql = "Insert into \n"
                + "PT_INCLUDE_ICD_NO (PT_ID, ICD_NO)\n"
                + "Values(%d, '%s')";
        for (String icdNo : lstIcdNo) {
            String s1=String.format(sql, ptId, noInjection(icdNo));
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
        sql = "Delete from PT_NOT_ALLOW_PLAN\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<String> filterNotAllowPlan(long ptId) {
        String sql;
        sql = "Select PLAN\n"
                + "From PT_NOT_ALLOW_PLAN\n"
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
        sql = "Insert into \n"
                + "PT_NOT_ALLOW_PLAN (PT_ID, PLAN)\n"
                + "Values(%d, '%s')";
        for (String plan : lstPlan) {
            String s1=String.format(sql, ptId, noInjection(plan));
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
        sql = "Delete from PT_HOSPITAL_TYPE\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<String> filterHospitalType(long ptId) {
        String sql;
        sql = "Select HOSPITAL_TYPE\n"
                + "From PT_HOSPITAL_TYPE\n"
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
        sql = "Insert into \n"
                + "PT_HOSPITAL_TYPE(PT_ID, HOSPITAL_TYPE)\n"
                + "Values(%d, '%s')";
        for (String hospitalType : lstHospitalType) {
            String s1=String.format(sql, ptId, noInjection(hospitalType));
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
        sql = "Select ID, USERNAME, DISPLAY_NAME, EMAIL, STATUS, ROLE \n"
                + "From USER\n"
                + "Where (USERNAME='%s')";
        sql = String.format(sql, noInjection(userName));
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            // A: MIS主管, B: 行政主管, C: 申報主管, D: coding人員/申報人員, E: 醫護人員, Z: 原廠開發者
            return Utility.mapLowerCase(lst.get(0));
        } else {
            return null;
        }
    }
    
    public String findUserRole(String userName) {
        String ret = "x";
        java.util.Map<String, Object> user = findUser(userName);
        if (user != null) {
            ret = user.get("role").toString();
        }
        return ret;
    }

    //===
    public java.util.List<Map<String, Object>> findByGroupNhiNo(String category, int lim) {
        String sql;
        sql = "Select *\r\n"
                + "FROM (Select NHI_NO, Count(*) as CNT\r\n"
                + "      From PT_PAYMENT_TERMS\r\n"
                + "      Where (NHI_NO IS NOT null)\r\n"
                + "       -- and (CATEGORY='%s')"
                + "      Group By NHI_NO)\r\n"
                + "Where (CNT>%d)\n"
                + "Order By 2";
        sql = String.format(sql, noInjection(category), lim);
        if (category.length()>0) {
            sql = sql.replace("-- and (CATEGORY", " and (CATEGORY");
        }
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.listLowerCase(lst);
        } else {
            return java.util.Collections.emptyList();
        }
    }
    
    public java.util.List<Map<String, Object>> findByNhiNoCategory(String nhiNo, String category) {
        String sql;
        sql = "Select ID, START_DATE, END_DATE, FEE_NO, NHI_NO, CATEGORY\r\n"
                + "From PT_PAYMENT_TERMS\r\n"
                + "Where (NHI_NO='%s')\n"
                + "   -- and(CATEGORY='%s')\r\n"
                + "Order BY START_DATE";
        sql = String.format(sql, noInjection(nhiNo), noInjection(category));
        if (category.length()>0) {
            sql = sql.replace("-- and(CATEGORY=", " and(CATEGORY=");
        }
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
        if (lst.size()>0) {
            return Utility.listLowerCase(lst);
        } else {
            return java.util.Collections.emptyList();
        }
    }
    
    public int updateEndDate(long ap_id, java.util.Date endDate) {
        String strEnd = Utility.dateFormat(endDate, "yyyy/MM/dd");
        String sql;
        sql = "Update PT_PAYMENT_TERMS\r\n"
                + "Set END_DATE='%s'\r\n"
                + "Where (ID=%d)";
        sql = String.format(sql, strEnd, ap_id);
        int ret = jdbcTemplate.update(sql);
        return ret;
    }
    
    //=== CoexistNhiNoTimes
    public int deleteCoexistNhiNoTimes(long ptId) {
        String sql;
        sql = "Delete from PT_COEXIST_NHI_NO_TIMES\n"
                + "WHERE (PT_ID=%d)";
        sql = String.format(sql, ptId);
        int ret =  jdbcTemplate.update(sql);
        return ret;
    }
    
    public java.util.List<Map<String, Object>> filterCoexistNhiNoTimes(long ptId) {
        String sql;
        sql = "Select NHI_NO, TIMES\n"
                + "From PT_COEXIST_NHI_NO_TIMES\n"
                + "Where (PT_ID=%d)";
        sql = String.format(sql, ptId);
        java.util.List<Map<String, Object>> lst = jdbcTemplate.query(sql, new ColumnMapRowMapper());
//        java.util.List<String> retList = new java.util.ArrayList<String>();
//        for (Map<String, Object> item : lst) {
//            retList.add(item.get("NHI_NO").toString());
//        }
        return Utility.listLowerCase(lst);
    }
    
    public int addCoexistNhiNoTimes(long ptId, java.util.List<ptNhiNoTimes> lstNhiNo) {
        int ret = 0;
        if (lstNhiNo!=null) {
            String sql;
            sql = "Insert into \n"
                    + "PT_COEXIST_NHI_NO_TIMES (PT_ID, NHI_NO, TIMES)\n"
                    + "Values(%d, '%s', %d)";
            for (ptNhiNoTimes nhiNo : lstNhiNo) {
                String s1=String.format(sql, ptId, noInjection(nhiNo.getNhi_no()), nhiNo.getTimes());
                try {
                    ret += jdbcTemplate.update(s1);
                } catch(DataAccessException ex) {
                    //
                }
            }
        }
        return ret;
    }

}
