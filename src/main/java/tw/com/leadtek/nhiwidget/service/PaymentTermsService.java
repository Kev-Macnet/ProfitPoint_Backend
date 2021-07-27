package tw.com.leadtek.nhiwidget.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.sql.LogDataDao;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.tools.Utility;

// swagger: http://127.0.0.1:8081/swagger-ui/index.html
@Service
public class PaymentTermsService {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PaymentTermsDao paymentTermsDao;

    public java.util.List<Map<String, Object>> searchPaymentTerms(String feeNo, String nhiNo, String category, 
            java.util.Date startDate, java.util.Date endDate) {
                
        List<Map<String, Object>> lst = paymentTermsDao.searchPaymentTerms(feeNo, nhiNo, category, startDate, endDate);
    
        return lst;
    }

    
}
