package tw.com.leadtek.nhiwidget.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtMedicineFeePl;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtMedicineFeeDao;
import tw.com.leadtek.tools.Utility;

@Service
public class PtMedicineFeeService {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtMedicineFeeDao ptMedicineFeeDao;
    
    private String Category = "藥費"; 
    
    public java.util.Map<String, Object> findMedicineFee(long ptId) {
        java.util.Map<String, Object> retMap;
        if (ptId > 0) {
            java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, this.Category);
            if (!master.isEmpty()) {
                java.util.Map<String, Object> detail = ptMedicineFeeDao.findOne(ptId);
                for (java.util.Map.Entry<String, Object> entry : detail.entrySet()) {
                    if (!entry.getKey().equals("pt_id")) {
                        master.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            retMap = master;
        } else {
            retMap = new java.util.HashMap<String, Object>();
        }
        return retMap;
    }

    public long addMedicineFee(PtMedicineFeePl params) {
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        params.setCategory(this.Category);
        long ptId = paymentTermsDao.addPaymentTerms(params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                    start_date, end_data, params.getCategory(), 
                                                    params.getHospital_type(), params.getOutpatient_type(), params.getHospitalized_type());
        if (ptId>0) {
            ptMedicineFeeDao.add(ptId, params.getMax_nday()|0);
        }
        return ptId;
    }
    
    public int updateMedicineFee(long ptId, PtMedicineFeePl params) {
        int ret = 0;
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        if (ptId>0) {
            ret += paymentTermsDao.updatePaymentTerms(ptId, params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                  start_date, end_data, this.Category, 
                                                  params.getHospital_type(), params.getOutpatient_type(), params.getHospitalized_type());
            
            if (ret > 0) {
                ret += ptMedicineFeeDao.update(ptId, params.getMax_nday()|0);
            }
        }
        return ret;
    }

    public int deleteMedicineFee(long ptId) {
        int ret = 0;
        if (ptId > 0) {
            ret += paymentTermsDao.deletePaymentTerms(ptId, this.Category);
            if (ret>0) {
                ret += paymentTermsDao.deleteExcludeNhiNo(ptId);
                ret += ptMedicineFeeDao.delete(ptId);
            }
        }
        return ret;
    }

    
}
