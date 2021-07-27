package tw.com.leadtek.nhiwidget.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtOutpatientFeePl;
import tw.com.leadtek.nhiwidget.sql.LogDataDao;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtOutpatientFeeDao;
import tw.com.leadtek.tools.Utility;

// swagger: http://127.0.0.1:8081/swagger-ui/index.html
@Service
public class PtOutpatientFeeService {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PaymentTermsDao paymentTermsDao;
    
    @Autowired
    private PtOutpatientFeeDao ptOutpatientFeeDao;
    
    public java.util.Map<String, Object> findOutpatientFee(long ptId) {
        java.util.Map<String, Object> retMap;
        if (ptId > 0) {
            java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId);
            java.util.Map<String, Object> detail = ptOutpatientFeeDao.findOne(ptId);
            for (java.util.Map.Entry<String, Object> entry : detail.entrySet()) {
                if (!entry.getKey().equals("pt_id")) {
                    master.put(entry.getKey(), entry.getValue());
                }
            }
            master.put("lst_nhi_no", paymentTermsDao.filterExcludeNhiNo(ptId));
            master.put("lst_division", paymentTermsDao.filterLimDivision(ptId));
            retMap = master;
        } else {
            retMap = new java.util.HashMap<String, Object>();
        }
        return retMap;
    }

    public long addOutpatientFee(PtOutpatientFeePl params) {
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        long ptId = paymentTermsDao.addPaymentTerms(params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                    start_date, end_data, params.getCategory(), 
                                                    params.getHospital_type(), params.getOutpatient_type(), params.getHospitalized_type());
        if (ptId>0) {
            if (params.getLst_division() != null) {
                paymentTermsDao.addLimDivision(ptId, params.getLst_division());
            }
            if (params.getLst_nhi_no() != null) {
                paymentTermsDao.addExcludeNhiNo(ptId, params.getLst_nhi_no());
            }
            ptOutpatientFeeDao.add(ptId, params.getNo_dentisit(), params.getNo_chi_medicine(), params.getNo_service_charge()|0, 
                    params.getLim_out_islands()|0, params.getLim_holiday()|0, params.getLim_max()|0, 
                    params.getLim_age()|0, params.getLim_age_type()|0,
                    params.getLim_division()|0, params.getLim_holiday()|0);
        }
//        add(long ptId, int no_dentisit, int no_chi_medicine, int no_service_charge, int lim_out_islands, int lim_holiday, 
//                int lim_max, int lim_age, int lim_age_type, int lim_division, int exclude_nhi_no)
        
        return ptId;
    }
    
    public int updateOutpatientFee(long ptId, PtOutpatientFeePl params) {
        int ret = 0;
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        if (ptId>0) {
            ret += paymentTermsDao.updatePaymentTerms(ptId, params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                  start_date, end_data, params.getCategory(), 
                                                  params.getHospital_type(), params.getOutpatient_type(), params.getHospitalized_type());
        
            if (params.getLst_division() != null) {
                paymentTermsDao.deleteLimDivision(ptId);
                paymentTermsDao.addLimDivision(ptId, params.getLst_division());
            }
            if (params.getLst_nhi_no() != null) {
                paymentTermsDao.deleteExcludeNhiNo(ptId);
                paymentTermsDao.addExcludeNhiNo(ptId, params.getLst_nhi_no());
            }
            ptOutpatientFeeDao.update(ptId, params.getNo_dentisit(), params.getNo_chi_medicine(), params.getNo_service_charge()|0, 
                    params.getLim_out_islands()|0, params.getLim_holiday()|0, params.getLim_max()|0, 
                    params.getLim_age()|0, params.getLim_age_type()|0,
                    params.getLim_division()|0, params.getLim_holiday()|0);
        }
//        add(long ptId, int no_dentisit, int no_chi_medicine, int no_service_charge, int lim_out_islands, int lim_holiday, 
//                int lim_max, int lim_age, int lim_age_type, int lim_division, int exclude_nhi_no)
        
        return ret;
    }

    public int deleteOutpatientFee(long ptId) {
        int ret = 0;
        if (ptId > 0) {
            ret += paymentTermsDao.deleteExcludeNhiNo(ptId);
            ret += paymentTermsDao.deleteLimDivision(ptId);
            ret += ptOutpatientFeeDao.delete(ptId);
            ret += paymentTermsDao.deletePaymentTerms(ptId);
        }
        return ret;
    }

    
}
