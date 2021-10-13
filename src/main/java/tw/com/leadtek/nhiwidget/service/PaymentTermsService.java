package tw.com.leadtek.nhiwidget.service;

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

    public java.util.List<Map<String, Object>> searchPaymentTerms(String feeNo, String nhiNo, String category, 
            java.util.Date startDate, java.util.Date endDate) {

        java.util.List<Map<String, Object>> lst = paymentTermsDao.searchPaymentTerms(feeNo, nhiNo, category, startDate, endDate);
        return lst;
    }
    
    public java.util.List<Map<String, Object>> searchPaymentTermsByDateRange(String feeNo, String nhiNo, String category, 
            java.util.Date startDate, java.util.Date endDate) {

        java.util.List<Map<String, Object>> lst = paymentTermsDao.searchPaymentTermsByDateRange(category, feeNo, nhiNo, startDate, endDate);
        return lst;
    }
    
    
    public java.util.Map<String, Object> jwtValidate(String jwt, int roleNo) { //roleNo default=4
        java.util.Map<String, Object> validationMap = Utility.jwtValidate(jwt);
        System.out.println(validationMap);
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

}
