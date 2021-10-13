package tw.com.leadtek.nhiwidget.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtSpecificMedicalFeePl;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtSpecificMedicalFeeDao;
import tw.com.leadtek.tools.Utility;

@Service
public class PtSpecificMedicalFeeService {

    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtSpecificMedicalFeeDao ptSpecificMedicalFeeDao;
    
    private String Category = "特定診療檢查費"; 
    
    public java.util.Map<String, Object> findSpecificMedicalFee(long ptId) {
        java.util.Map<String, Object> retMap;
        if (ptId > 0) {
            java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, this.Category);
            if (!master.isEmpty()) {
                java.util.Map<String, Object> detail = ptSpecificMedicalFeeDao.findOne(ptId);
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

    public long addSpecificMedicalFee(PtSpecificMedicalFeePl params) {
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
//            ptSpecificMedicalFeeDao.add(ptId, params.getExclude_nhi_no()|0, params.getInterval_nday()|0, params.getMax_times()|0);
            ptSpecificMedicalFeeDao.add(ptId, params.getExclude_nhi_no_enable()|0, params.getInterval_nday_enable()|0, params.getInterval_nday()|0, 
                    params.getMax_times_enable()|0, params.getMax_times()|0);
        }
        return ptId;
    }
    
    public int updateSpecificMedicalFee(long ptId, PtSpecificMedicalFeePl params) {
        int ret = 0;
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        if (ptId>0) {
            ret += paymentTermsDao.updatePaymentTerms(ptId, params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                  start_date, end_data, this.Category, 
                                                  params.getHospital_type(), params.getOutpatient_type(), params.getHospitalized_type());
            if (ret>0) {
                if (params.getLst_nhi_no() != null) {
                    paymentTermsDao.deleteExcludeNhiNo(ptId);
                    paymentTermsDao.addExcludeNhiNo(ptId, params.getLst_nhi_no());
                }
                ptSpecificMedicalFeeDao.update(ptId, params.getExclude_nhi_no_enable()|0, params.getInterval_nday_enable()|0, params.getInterval_nday()|0, 
                        params.getMax_times_enable()|0, params.getMax_times()|0);
            }
        }
        return ret;
    }

    public int deleteSpecificMedicalFee(long ptId) {
        int ret = 0;
        if (ptId > 0) {
            ret += paymentTermsDao.deletePaymentTerms(ptId, this.Category);
            if (ret>0) {
                ret += paymentTermsDao.deleteExcludeNhiNo(ptId);
                ret += ptSpecificMedicalFeeDao.delete(ptId);
            }
        }
        return ret;
    }

    
}
