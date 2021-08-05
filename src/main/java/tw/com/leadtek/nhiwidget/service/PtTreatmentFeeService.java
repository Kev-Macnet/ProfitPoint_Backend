package tw.com.leadtek.nhiwidget.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtTreatmentFeePl;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtTreatmentFeeDao;
import tw.com.leadtek.tools.Utility;

@Service
public class PtTreatmentFeeService {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtTreatmentFeeDao ptTreatmentFeeDao;
    
    private String Category = "治療處置費"; 
    
    public java.util.Map<String, Object> findTreatmentFee(long ptId) {
        java.util.Map<String, Object> retMap;
        if (ptId > 0) {
            java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, this.Category);
            if (!master.isEmpty()) {
                java.util.Map<String, Object> detail = ptTreatmentFeeDao.findOne(ptId);
                for (java.util.Map.Entry<String, Object> entry : detail.entrySet()) {
                    if (!entry.getKey().equals("pt_id")) {
                        master.put(entry.getKey(), entry.getValue());
                    }
                }
                master.put("lst_nhi_no", paymentTermsDao.filterExcludeNhiNo(ptId));
                master.put("lst_co_nhi_no", paymentTermsDao.filterCoexistNhiNo(ptId));
                master.put("lst_icd_no", paymentTermsDao.filterIncludeIcdNo(ptId));
                master.put("lst_division", paymentTermsDao.filterLimDivision(ptId));
            }
            retMap = master;
        } else {
            retMap = new java.util.HashMap<String, Object>();
        }
        return retMap;
    }

    public long addTreatmentFee(PtTreatmentFeePl params) {
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        params.setCategory(this.Category);
        long ptId = paymentTermsDao.addPaymentTerms(params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                    start_date, end_data, params.getCategory(), 
                                                    params.getHospital_type(), params.getOutpatient_type(), params.getHospitalized_type());
        if (ptId > 0) {
            if (params.getLst_nhi_no() != null) {
                paymentTermsDao.addExcludeNhiNo(ptId, params.getLst_nhi_no());
            }
            if (params.getLst_co_nhi_no() != null) {
                paymentTermsDao.addCoexistNhiNo(ptId, params.getLst_co_nhi_no());
            }
            if (params.getLst_icd_no() != null) {
                paymentTermsDao.addIncludeIcdNo(ptId, params.getLst_icd_no());
            }
            if (params.getLst_division() != null) {
                paymentTermsDao.addLimDivision(ptId, params.getLst_division());
            }
            ptTreatmentFeeDao.add(ptId, params.getExclude_nhi_no()|0, params.getCoexist_nhi_no()|0, params.getMax_inpatient()|0, params.getMax_daily()|0, 
                    params.getEvery_nday()|0, params.getEvery_nday_days()|0, params.getEvery_nday_times()|0, 
                    params.getPatient_nday()|0, params.getPatient_nday_days()|0, params.getPatient_nday_times()|0, 
                    params.getMax_patient()|0, params.getInclude_icd_no()|0, params.getMax_month_percentage()|0, 
                    params.getMax_age()|0, params.getLim_division()|0);
        }
        return ptId;
    }
    
    public int updateTreatmentFee(long ptId, PtTreatmentFeePl params) {
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
                if (params.getLst_co_nhi_no() != null) {
                    paymentTermsDao.deleteCoexistNhiNo(ptId);
                    paymentTermsDao.addCoexistNhiNo(ptId, params.getLst_co_nhi_no());
                }
                if (params.getLst_icd_no() != null) {
                    paymentTermsDao.deleteIncludeIcdNo(ptId);
                    paymentTermsDao.addIncludeIcdNo(ptId, params.getLst_icd_no());
                }
                if (params.getLst_division() != null) {
                    paymentTermsDao.deleteLimDivision(ptId);
                    paymentTermsDao.addLimDivision(ptId, params.getLst_division());
                }
                ptTreatmentFeeDao.update(ptId, params.getExclude_nhi_no()|0, params.getCoexist_nhi_no()|0, params.getMax_inpatient()|0, params.getMax_daily()|0, 
                        params.getEvery_nday()|0, params.getEvery_nday_days()|0, params.getEvery_nday_times()|0, 
                        params.getPatient_nday()|0, params.getPatient_nday_days()|0, params.getPatient_nday_times()|0, 
                        params.getMax_patient()|0, params.getInclude_icd_no()|0, params.getMax_month_percentage()|0, 
                        params.getMax_age()|0, params.getLim_division()|0);
            }
        }
        return ret;
    }

    public int deleteTreatmentFee(long ptId) {
        int ret = 0;
        if (ptId > 0) {
            ret += paymentTermsDao.deletePaymentTerms(ptId, this.Category);
            if (ret>0) {
                ret += paymentTermsDao.deleteExcludeNhiNo(ptId);
                ret += paymentTermsDao.deleteCoexistNhiNo(ptId);
                ret += paymentTermsDao.deleteIncludeIcdNo(ptId);
                ret += paymentTermsDao.deleteLimDivision(ptId);
                ret += ptTreatmentFeeDao.delete(ptId);
            }
        }
        return ret;
    }

    
}
