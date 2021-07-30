package tw.com.leadtek.nhiwidget.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtNutritionalFeePl;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtNutritionalFeeDao;
import tw.com.leadtek.tools.Utility;

@Service
public class PtNutritionalFeeService {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtNutritionalFeeDao ptNutritionalFeeDao;
    
    private String Category = "營養照護費"; 
    
    public java.util.Map<String, Object> findNutritionalFee(long ptId) {
        java.util.Map<String, Object> retMap;
        if (ptId > 0) {
            java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, this.Category);
            if (!master.isEmpty()) {
                java.util.Map<String, Object> detail = ptNutritionalFeeDao.findOne(ptId);
                for (java.util.Map.Entry<String, Object> entry : detail.entrySet()) {
                    if (!entry.getKey().equals("pt_id")) {
                        master.put(entry.getKey(), entry.getValue());
                    }
                }
                master.put("lst_nhi_no", paymentTermsDao.filterExcludeNhiNo(ptId));
            }
            retMap = master;
        } else {
            retMap = new java.util.HashMap<String, Object>();
        }
        return retMap;
    }

    public long addNutritionalFee(PtNutritionalFeePl params) {
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        params.setCategory(this.Category);
        long ptId = paymentTermsDao.addPaymentTerms(params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                    start_date, end_data, params.getCategory(), 
                                                    params.getHospital_type(), params.getOutpatient_type(), params.getHospitalized_type());
        if (ptId>0) {
            if (params.getLst_nhi_no() != null) {
                paymentTermsDao.addExcludeNhiNo(ptId, params.getLst_nhi_no());
            }
            ptNutritionalFeeDao.add(ptId, params.getMax_inpatient()|0, params.getMax_daily()|0, 
                    params.getEvery_nday()|0, params.getEvery_nday_days()|0, params.getEvery_nday_times()|0, 
                    params.getOver_nday()|0, params.getOver_nday_days()|0, params.getOver_nday_times()|0, 
                    params.getExclude_nhi_no()|0);
        }
        return ptId;
    }
    
    public int updateNutritionalFee(long ptId, PtNutritionalFeePl params) {
        int ret = 0;
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        if (ptId>0) {
            ret += paymentTermsDao.updatePaymentTerms(ptId, params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                  start_date, end_data, this.Category, 
                                                  params.getHospital_type(), params.getOutpatient_type(), params.getHospitalized_type());
        
            if (params.getLst_nhi_no() != null) {
                paymentTermsDao.deleteExcludeNhiNo(ptId);
                paymentTermsDao.addExcludeNhiNo(ptId, params.getLst_nhi_no());
            }
            ptNutritionalFeeDao.update(ptId, params.getMax_inpatient()|0, params.getMax_daily()|0, 
                    params.getEvery_nday()|0, params.getEvery_nday_days()|0, params.getEvery_nday_times()|0, 
                    params.getOver_nday()|0, params.getOver_nday_days()|0, params.getOver_nday_times()|0, 
                    params.getExclude_nhi_no()|0);
        }
        return ret;
    }

    public int deleteNutritionalFee(long ptId) {
        int ret = 0;
        if (ptId > 0) {
            ret += paymentTermsDao.deletePaymentTerms(ptId, this.Category);
            if (ret>0) {
                ret += paymentTermsDao.deleteExcludeNhiNo(ptId);
                ret += ptNutritionalFeeDao.delete(ptId);
            }
        }
        return ret;
    }

    
}
