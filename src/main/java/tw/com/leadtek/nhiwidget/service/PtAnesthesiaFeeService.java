package tw.com.leadtek.nhiwidget.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtAnesthesiaFeePl;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtAnesthesiaFeeDao;
import tw.com.leadtek.tools.Utility;

@Service
public class PtAnesthesiaFeeService {

    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtAnesthesiaFeeDao ptAnesthesiaFeeDao;
    
    private String Category = "麻醉費"; 
    
    public java.util.Map<String, Object> findAnesthesiaFee(long ptId) {
        java.util.Map<String, Object> retMap;
        if (ptId > 0) {
            java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, this.Category);
            if (!master.isEmpty()) {
                java.util.Map<String, Object> detail = ptAnesthesiaFeeDao.findOne(ptId);
                for (java.util.Map.Entry<String, Object> entry : detail.entrySet()) {
                    if (!entry.getKey().equals("pt_id")) {
                        master.put(entry.getKey(), entry.getValue());
                    }
                }
                master.put("lst_drg_no", paymentTermsDao.filterDrgNo(ptId));
                master.put("lst_division", paymentTermsDao.filterLimDivision(ptId));
                master.put("lst_co_nhi_no", paymentTermsDao.filterCoexistNhiNo(ptId));
            }
            retMap = master;
        } else {
            retMap = new java.util.HashMap<String, Object>();
        }
        return retMap;
    }

    public long addAnesthesiaFee(PtAnesthesiaFeePl params) {
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        params.setCategory(this.Category);
        long ptId = paymentTermsDao.addPaymentTerms(params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                    start_date, end_data, params.getCategory(), 
                                                    params.getHospital_type(), params.getOutpatient_type(), params.getHospitalized_type());
        if (ptId>0) {
            if (params.getLst_drg_no() != null) {
                paymentTermsDao.addDrgNo(ptId, params.getLst_drg_no());
            }
            if (params.getLst_co_nhi_no() != null) {
                paymentTermsDao.addCoexistNhiNo(ptId, params.getLst_co_nhi_no());
            }
            if (params.getLst_division() != null) {
                paymentTermsDao.addLimDivision(ptId, params.getLst_division());
            }
            ptAnesthesiaFeeDao.add(ptId, params.getInclude_drg_no()|0, params.getCoexist_nhi_no()|0, 
                    params.getOver_times()|0, params.getOver_times_n()|0, params.getOver_times_first_n()|0,
                    params.getOver_times_next_n()|0, params.getLim_division()|0);
//            include_drg_no, coexist_nhi_no, over_times, over_times_n, over_times_first_n, over_times_next_n, lim_division
        }
        return ptId;
    }
    
    public int updateAnesthesiaFee(long ptId, PtAnesthesiaFeePl params) {
        int ret = 0;
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        if (ptId>0) {
            ret += paymentTermsDao.updatePaymentTerms(ptId, params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                  start_date, end_data, this.Category, 
                                                  params.getHospital_type(), params.getOutpatient_type(), params.getHospitalized_type());
            if (ret>0) {
                if (params.getLst_drg_no() != null) {
                    paymentTermsDao.deleteDrgNo(ptId);
                    paymentTermsDao.addDrgNo(ptId, params.getLst_drg_no());
                }
                if (params.getLst_co_nhi_no() != null) {
                    paymentTermsDao.deleteCoexistNhiNo(ptId);
                    paymentTermsDao.addCoexistNhiNo(ptId, params.getLst_co_nhi_no());
                }
                if (params.getLst_division() != null) {
                    paymentTermsDao.deleteLimDivision(ptId);
                    paymentTermsDao.addLimDivision(ptId, params.getLst_division());
                }
                
                ptAnesthesiaFeeDao.update(ptId, params.getInclude_drg_no()|0, params.getCoexist_nhi_no()|0, 
                        params.getOver_times()|0, params.getOver_times_n()|0, params.getOver_times_first_n()|0,
                        params.getOver_times_next_n()|0, params.getLim_division()|0);
            }
        }
        return ret;
    }

    public int deleteAnesthesiaFee(long ptId) {
        int ret = 0;
        if (ptId > 0) {
            ret += paymentTermsDao.deletePaymentTerms(ptId, this.Category);
            if (ret>0) {
                ret += paymentTermsDao.deleteDrgNo(ptId);
                ret += paymentTermsDao.deleteCoexistNhiNo(ptId);
                ret += paymentTermsDao.deleteLimDivision(ptId);
                ret += ptAnesthesiaFeeDao.delete(ptId);
            }
        }
        return ret;
    }

    
}
