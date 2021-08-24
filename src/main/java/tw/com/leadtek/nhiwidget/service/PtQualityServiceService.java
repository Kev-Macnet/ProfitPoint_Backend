package tw.com.leadtek.nhiwidget.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtQualityServicePl;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtQualityServiceDao;
import tw.com.leadtek.tools.Utility;

// swagger: http://127.0.0.1:8081/swagger-ui/index.html
@Service
public class PtQualityServiceService {

    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtQualityServiceDao ptQualityServiceDao;
    
    private String Category = "品質支付服務"; 
    
    public java.util.Map<String, Object> findQualityService(long ptId) {
        java.util.Map<String, Object> retMap;
        if (ptId > 0) {
            java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, this.Category);
            if (!master.isEmpty()) {
                java.util.Map<String, Object> detail = ptQualityServiceDao.findOne(ptId);
                for (java.util.Map.Entry<String, Object> entry : detail.entrySet()) {
                    if (!entry.getKey().equals("pt_id")) {
                        master.put(entry.getKey(), entry.getValue());
                    }
                }
                master.put("lst_co_nhi_no", paymentTermsDao.filterCoexistNhiNo(ptId));
            }
            retMap = master;
        } else {
            retMap = new java.util.HashMap<String, Object>();
        }
        return retMap;
    }

    public long addQualityService(PtQualityServicePl params) {
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        params.setCategory(this.Category);
        long ptId = paymentTermsDao.addPaymentTerms(params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                    start_date, end_data, params.getCategory(), 
                                                    params.getHospital_type(), params.getOutpatient_type(), params.getHospitalized_type());
        if (ptId>0) {
            if (params.getLst_co_nhi_no() != null) {
                paymentTermsDao.addCoexistNhiNo(ptId, params.getLst_co_nhi_no());
            }
            ptQualityServiceDao.add(ptId, params.getInterval_nday()|0, params.getCoexist_nhi_no()|0, params.getMin_coexist()|0,
                    params.getEvery_nday()|0, params.getEvery_nday_days()|0, params.getEvery_nday_times()|0);
//            interval_nday, coexist_nhi_no, min_coexist, every_nday, every_nday_days, every_nday_times
        }
        return ptId;
    }
    
    public int updateQualityService(long ptId, PtQualityServicePl params) {
        int ret = 0;
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        if (ptId>0) {
            ret += paymentTermsDao.updatePaymentTerms(ptId, params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                  start_date, end_data, this.Category, 
                                                  params.getHospital_type(), params.getOutpatient_type(), params.getHospitalized_type());
            if (ret>0) {
                if (params.getLst_co_nhi_no() != null) {
                    paymentTermsDao.deleteCoexistNhiNo(ptId);
                    paymentTermsDao.addCoexistNhiNo(ptId, params.getLst_co_nhi_no());
                }
                ptQualityServiceDao.update(ptId, params.getInterval_nday()|0, params.getCoexist_nhi_no()|0, params.getMin_coexist()|0,
                        params.getEvery_nday()|0, params.getEvery_nday_days()|0, params.getEvery_nday_times()|0);
            }
        }
        return ret;
    }

    public int deleteQualityService(long ptId) {
        int ret = 0;
        if (ptId > 0) {
            ret += paymentTermsDao.deletePaymentTerms(ptId, this.Category);
            if (ret>0) {
                ret += paymentTermsDao.deleteCoexistNhiNo(ptId);
                ret += ptQualityServiceDao.delete(ptId);
            }
        }
        return ret;
    }

    
}
