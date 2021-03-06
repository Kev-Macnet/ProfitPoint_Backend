package tw.com.leadtek.nhiwidget.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtBoneMarrowTransFeePl;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtBoneMarrowTransFeeDao;
import tw.com.leadtek.tools.Utility;

@Service
public class PtBoneMarrowTransFeeService {

    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtBoneMarrowTransFeeDao ptBoneMarrowTransFeeDao;
    
    private String Category = "輸血及骨髓移植費"; 
    
    public java.util.Map<String, Object> findBoneMarrowTransFee(long ptId) {
        java.util.Map<String, Object> retMap;
        if (ptId > 0) {
            java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, this.Category);
            if (!master.isEmpty()) {
                java.util.Map<String, Object> detail = ptBoneMarrowTransFeeDao.findOne(ptId);
                for (java.util.Map.Entry<String, Object> entry : detail.entrySet()) {
                    if (!entry.getKey().equals("pt_id")) {
                        master.put(entry.getKey(), entry.getValue());
                    }
                }
                master.put("lst_co_nhi_no", paymentTermsDao.filterCoexistNhiNo(ptId));
                master.put("lst_allow_plan", paymentTermsDao.filterNotAllowPlan(ptId));
                master.put("lst_division", paymentTermsDao.filterLimDivision(ptId));
            }
            retMap = master;
        } else {
            retMap = new java.util.HashMap<String, Object>();
        }
        return retMap;
    }

    public long addBoneMarrowTransFee(PtBoneMarrowTransFeePl params) {
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
            if (params.getLst_allow_plan() != null) {
                paymentTermsDao.addNotAllowPlan(ptId, params.getLst_allow_plan());
            }
            if (params.getLst_division() != null) {
                paymentTermsDao.addLimDivision(ptId, params.getLst_division());
            }
            ptBoneMarrowTransFeeDao.add(ptId, params.getCoexist_nhi_no_enable()|0, params.getNot_allow_plan_enable()|0, params.getLim_division_enable()|0);
//            coexist_nhi_no_enable, not_allow_plan_enable, lim_division_enable
        }
        return ptId;
    }
    
    public int updateBoneMarrowTransFee(long ptId, PtBoneMarrowTransFeePl params) {
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
                if (params.getLst_allow_plan() != null) {
                    paymentTermsDao.deleteNotAllowPlan(ptId);
                    paymentTermsDao.addNotAllowPlan(ptId, params.getLst_allow_plan());
                }
                if (params.getLst_division() != null) {
                    paymentTermsDao.deleteLimDivision(ptId);
                    paymentTermsDao.addLimDivision(ptId, params.getLst_division());
                }
                ptBoneMarrowTransFeeDao.update(ptId, params.getCoexist_nhi_no_enable()|0, params.getNot_allow_plan_enable()|0, params.getLim_division_enable()|0);
            }
        }
        return ret;
    }

    public int deleteBoneMarrowTransFee(long ptId) {
        int ret = 0;
        if (ptId > 0) {
            ret += paymentTermsDao.deletePaymentTerms(ptId, this.Category);
            if (ret>0) {
                ret += paymentTermsDao.deleteCoexistNhiNo(ptId);
                ret += paymentTermsDao.deleteNotAllowPlan(ptId);
                ret += paymentTermsDao.deleteLimDivision(ptId);
                ret += ptBoneMarrowTransFeeDao.delete(ptId);
            }
        }
        return ret;
    }

    
}
