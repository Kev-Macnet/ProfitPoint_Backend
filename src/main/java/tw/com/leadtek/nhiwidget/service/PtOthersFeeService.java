package tw.com.leadtek.nhiwidget.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtOthersFeePl;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtOthersFeeDao;
import tw.com.leadtek.tools.Utility;

@Service
public class PtOthersFeeService {

    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtOthersFeeDao ptOthersFeeDao;
    
    private String Category = "其他"; 
    
    public java.util.Map<String, Object> findOthersFee(long ptId) {
        java.util.Map<String, Object> retMap;
        if (ptId > 0) {
            java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, this.Category);
            if (!master.isEmpty()) {
                java.util.Map<String, Object> detail = ptOthersFeeDao.findOne(ptId);
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

    public long addOthersFee(PtOthersFeePl params) {
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
            ptOthersFeeDao.add(ptId, params.getExclude_nhi_no()|0, params.getMax_inpatient()|0, 
                    params.getMax_times()|0, params.getInterval_nday()|0,
                    params.getPatient_nday()|0, params.getPatient_nday_days()|0, params.getPatient_nday_times()|0);
//            exclude_nhi_no, max_inpatient, max_times, interval_nday
        }
        return ptId;
    }
    
    public int updateOthersFee(long ptId, PtOthersFeePl params) {
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
                ptOthersFeeDao.update(ptId, params.getExclude_nhi_no()|0, params.getMax_inpatient()|0, 
                        params.getMax_times()|0, params.getInterval_nday()|0,
                        params.getPatient_nday()|0, params.getPatient_nday_days()|0, params.getPatient_nday_times()|0);
            }
        }
        return ret;
    }

    public int deleteOthersFee(long ptId) {
        int ret = 0;
        if (ptId > 0) {
            ret += paymentTermsDao.deletePaymentTerms(ptId, this.Category);
            if (ret>0) {
                ret += paymentTermsDao.deleteExcludeNhiNo(ptId);
                ret += ptOthersFeeDao.delete(ptId);
            }
        }
        return ret;
    }

    
}
