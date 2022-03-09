package tw.com.leadtek.nhiwidget.service;

import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.tools.Utility;

// swagger: http://127.0.0.1:8081/swagger-ui/index.html
@Service
public class PaymentTermsService {
//    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PaymentTermsDao paymentTermsDao;

    public java.util.Map<String, Object> searchPaymentTerms(String feeNo, String nhiNo, String category, 
            java.util.Date startDate, java.util.Date endDate, int pageSize, int pageIndex,
            String sortField, String sortDirection) {
        long totalCount = paymentTermsDao.searchPaymentTermsCount(feeNo, nhiNo, category, startDate, endDate);
        int start = pageSize*pageIndex;
        if (start>totalCount) {
            start = (int)totalCount;
        } else if (start<0) {
            start = 0;
        }
        java.util.List<Map<String, Object>> lst = paymentTermsDao.searchPaymentTerms(feeNo, nhiNo, category, startDate, endDate, start, pageSize, sortField, sortDirection);
        if (lst.size()==0) {
            totalCount = paymentTermsDao.searchPaymentTermsByDateRangeCount(feeNo, nhiNo, category, startDate, endDate);
            start = pageSize*pageIndex;
            if (start>totalCount) {
                start = (int)totalCount;
            } else if (start<0) {
                start = 0;
            }
            lst = paymentTermsDao.searchPaymentTermsByDateRange(feeNo, nhiNo, category, startDate, endDate, start, pageSize, sortField, sortDirection);
        }
        
        java.util.Map<String, Object> retMap = new java.util.LinkedHashMap<String, Object>();
        retMap.put("total", totalCount);
        retMap.put("pages", (int)totalCount/pageSize + ((totalCount%pageSize)>0 ? 1: 0));
        retMap.put("pageIndex", pageIndex);
        retMap.put("pageSize", pageSize);
        retMap.put("data", lst);
        return retMap;
    }
    
    public java.util.Map<String, Object> jwtValidate(String jwt, int roleNo) { //roleNo default=4
        java.util.Map<String, Object> validationMap = Utility.jwtValidate(jwt);
        if ((int)validationMap.get("status") == 200) {
            String role = findUserRole(validationMap.get("userName").toString());
            // "A: MIS主管, B: 行政主管, C: 申報主管, D: coding人員/申報人員, E: 醫護人員, Z: 原廠開發者" 
            java.util.List<String> lstRole = null; // = new java.util.ArrayList<String>();
            if (roleNo==1) {
                String arr[] = {"Z"};
                lstRole = java.util.Arrays.asList(arr);
            } else if (roleNo==2) {
                String arr[] = {"A"};
                lstRole = java.util.Arrays.asList(arr);
            } else if (roleNo==3) {
                String arr[] = {"A","Z"};
                lstRole = java.util.Arrays.asList(arr);
            } else if (roleNo==4) {
                String arr[] = {"A","C","Z"};
                lstRole = java.util.Arrays.asList(arr);
            } else if (roleNo>=5) {
                String arr[] = {"A","B","C","D","E","Z"};
                lstRole = java.util.Arrays.asList(arr);
            } else {
                lstRole = new java.util.ArrayList<String>();
            }
            if (listStrIndexOf(role, lstRole) < 0) {
                validationMap.put("status", 401);
                validationMap.put("message", "權限不足!");
            }
        }
        return validationMap;
    }
    
    
    public int updateActive(long id, String category, int state) {
        return paymentTermsDao.updatePaymentTermsActive(id, category, state);
    }
    
    private String findUserRole(String userName) {
        return paymentTermsDao.findUserRole(userName);
    }
    
    private int listStrIndexOf(String key, java.util.List<String> lstStr) {
        int ret = -1;
        int idx = 0;
        for (String str : lstStr) {
            if (key.equals(str)) {
                ret = idx;
                break;
             }
            idx++;
        }
        return (ret);
     }
    
    public void correctEndDate(String category) {
        java.util.List<Map<String, Object>> lstNhiNo = paymentTermsDao.findByGroupNhiNo(category, 1);
        for (Map<String, Object> item : lstNhiNo) { //nhi_no, category
//            System.out.println("nhi_no = "+item.get("nhi_no").toString()+", "+item.get("category").toString());
            correctEndDateByNhiNo(item.get("nhi_no").toString(), item.get("category").toString());
        }
    }
    
    public void correctEndDateByNhiNo(String nhiNo, String category) {
        long nextStartDate, endDate;
        java.util.Map<String, Object> nxetMap;
        java.util.List<Map<String, Object>> lstData;
        if (nhiNo.length()>0) {
            lstData = paymentTermsDao.findByNhiNoCategory(nhiNo, category);
            int idx = 1;
            long dataLen = lstData.size();
            for (Map<String, Object> item : lstData) {
                if (idx < dataLen) {
                    nxetMap = lstData.get(idx);
                    nextStartDate = (long)nxetMap.get("start_date");
                    if (item.get("end_date")==null) {
                        endDate = 4102358400000l;
                    } else {
                        endDate = (long)item.get("end_date");
                    }
                    java.util.Date prevDay = new java.util.Date(nextStartDate-(86400*1000));
//                        System.out.println("   end_date="+Utility.dateFormat(new Date(endDate),"yyyy-MM-dd")+", "
//                                +Utility.dateFormat(new Date(nextStartDate),"yyyy-MM-dd")+", "
//                                +Utility.dateFormat(prevDay,"yyyy-MM-dd"));
                    if (nextStartDate<endDate) {
                        paymentTermsDao.updateEndDate((long)item.get("id"), prevDay);
                    }
                }
                idx++;
            }
        }
    }


}
