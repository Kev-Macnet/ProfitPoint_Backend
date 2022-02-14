package tw.com.leadtek.nhiwidget.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtRehabilitationFeePl;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtRehabilitationFeeDao;
import tw.com.leadtek.tools.Utility;

// swagger: http://127.0.0.1:8081/swagger-ui/index.html
@Service
public class PtRehabilitationFeeService {

    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtRehabilitationFeeDao ptRehabilitationFeeDao;
    
    private String Category = "復健治療費"; 
    
    public java.util.Map<String, Object> findRehabilitationFee(long ptId) {
        java.util.Map<String, Object> retMap;
        if (ptId > 0) {
            java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, this.Category);
            if (!master.isEmpty()) {
                java.util.Map<String, Object> detail = ptRehabilitationFeeDao.findOne(ptId);
                for (java.util.Map.Entry<String, Object> entry : detail.entrySet()) {
                    if (!entry.getKey().equals("pt_id")) {
                        master.put(entry.getKey(), entry.getValue());
                    }
                }
                master.put("lst_nhi_no", paymentTermsDao.filterExcludeNhiNo(ptId));
                master.put("lst_co_nhi_no", paymentTermsDao.filterCoexistNhiNo(ptId));
                master.put("lst_icd_no", paymentTermsDao.filterIncludeIcdNo(ptId));
                master.put("lst_division", paymentTermsDao.filterLimDivision(ptId));
                //exclude_nhi_no, coexist_nhi_no, include_icd_no, lim_division
            }
            retMap = master;
        } else {
            retMap = new java.util.HashMap<String, Object>();
        }
        return retMap;
    }

    public long addRehabilitationFee(PtRehabilitationFeePl params) {
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        params.setCategory(this.Category);
        long ptId = paymentTermsDao.addPaymentTerms(params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                    start_date, end_data, params.getCategory(), 
                                                    params.getHospital_type(), params.getOutpatient_type(), params.getHospitalized_type());
        if (ptId>0) {
            if (params.getLst_nhi_no() != null) {
//                java.util.List<String> lstNhiNo = new java.util.ArrayList<>();
//                lstNhiNo.add(params.getLst_nhi_no());
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

            ptRehabilitationFeeDao.add(ptId, params.getExclude_nhi_no_enable()|0, 
                    params.getPatient_nday_enable()|0, params.getPatient_nday_days()|0, params.getPatient_nday_times()|0, 
                    params.getInclude_icd_no_enable()|0, params.getCoexist_nhi_no_enable()|0, params.getMin_coexist()|0, 
                    params.getLim_division_enable()|0);
        }
        return ptId;
    }
    
    public int updateRehabilitationFee(long ptId, PtRehabilitationFeePl params) {
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
//                    java.util.List<String> lstNhiNo = new java.util.ArrayList<>();
//                    lstNhiNo.add(params.getLst_nhi_no());
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
                ptRehabilitationFeeDao.update(ptId, params.getExclude_nhi_no_enable()|0, 
                        params.getPatient_nday_enable()|0, params.getPatient_nday_days()|0, params.getPatient_nday_times()|0, 
                        params.getInclude_icd_no_enable()|0, params.getCoexist_nhi_no_enable()|0, params.getMin_coexist()|0, 
                        params.getLim_division_enable()|0);
            }
        }
        return ret;
    }

    public int deleteRehabilitationFee(long ptId) {
        int ret = 0;
        if (ptId > 0) {
            ret += paymentTermsDao.deletePaymentTerms(ptId, this.Category);
            if (ret>0) {
                ret += paymentTermsDao.deleteExcludeNhiNo(ptId);
                ret += paymentTermsDao.deleteCoexistNhiNo(ptId);
                ret += paymentTermsDao.deleteIncludeIcdNo(ptId);
                ret += paymentTermsDao.deleteLimDivision(ptId);
                ret += ptRehabilitationFeeDao.delete(ptId);
            }
        }
        return ret;
    }

    
}
