package tw.com.leadtek.nhiwidget.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.tools.Utility;

// swagger: http://127.0.0.1:8081/swagger-ui/index.html
@Service
public class PaymentTermsService {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PaymentTermsDao paymentTermsDao;
//    @Autowired
//    private  PtOutpatientFeeDao ptOutpatientFeeDao;

    public java.util.List<Map<String, Object>> searchPaymentTerms(String feeNo, String nhiNo, String category, 
            java.util.Date startDate, java.util.Date endDate) {

        java.util.List<Map<String, Object>> lst = paymentTermsDao.searchPaymentTerms(feeNo, nhiNo, category, startDate, endDate);
        return lst;
    }
    
    public java.util.Map<String, Object> jwtValidate(String jwt) {
        java.util.Map<String, Object> validationMap = Utility.jwtValidate(jwt);
        if ((int)validationMap.get("status") == 200) {
            if (findUserRole(validationMap.get("userName").toString())<4) {
                validationMap.put("status", 401);
                validationMap.put("message", "權限不足!");
            }
        }
        return validationMap;
    }
    
    private int findUserRole(String userName) {
        return paymentTermsDao.findUserRole(userName);
    }
}
