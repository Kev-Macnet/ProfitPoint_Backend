package tw.com.leadtek.nhiwidget.service.pt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtInpatientFeeDao;
import tw.com.leadtek.nhiwidget.dto.PtInpatientFeePl;
import tw.com.leadtek.tools.Utility;

@Service
public class PtInpatientFeeService extends BasicPaymentTerms {

    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtInpatientFeeDao ptInpatientFeeDao;
    
    public final static String Category = "住院診察費"; 
    
    public java.util.Map<String, Object> findInpatientFee(long ptId) {
        java.util.Map<String, Object> retMap;
        if (ptId > 0) {
            java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, Category);
            if (!master.isEmpty()) {
                java.util.Map<String, Object> detail = ptInpatientFeeDao.findOne(ptId);
                for (java.util.Map.Entry<String, Object> entry : detail.entrySet()) {
                    if (!entry.getKey().equals("pt_id")) {
                        master.put(entry.getKey(), entry.getValue());
                    }
                }
                master.put("lst_nhi_no", paymentTermsDao.filterExcludeNhiNo(ptId));
                master.put("lst_co_nhi_no", paymentTermsDao.filterCoexistNhiNo(ptId));
                master.put("lst_allow_plan", paymentTermsDao.filterNotAllowPlan(ptId));
            }
            retMap = master;
        } else {
            retMap = new java.util.HashMap<String, Object>();
        }
        return retMap;
    }

    public long addInpatientFee(PtInpatientFeePl params) {
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
            if (params.getLst_co_nhi_no() != null) {
                paymentTermsDao.addCoexistNhiNo(ptId, params.getLst_co_nhi_no());
            }
            if (params.getLst_allow_plan() != null) {
                paymentTermsDao.addNotAllowPlan(ptId, params.getLst_allow_plan());
            }
            ptInpatientFeeDao.add(ptId, params.getMax_inpatient_enable()|0, params.getMax_inpatient()|0, 
                    params.getMax_emergency_enable()|0, params.getMax_emergency()|0, 
                    params.getMax_patient_no_enable()|0, params.getMax_patient_no()|0, params.getExclude_nhi_no_enable()|0, 
                    params.getNot_allow_plan_enable()|0, params.getCoexist_nhi_no_enable()|0, params.getNo_coexist_enable()|0);
        }
        return ptId;
    }
    
    public int updateInpatientFee(long ptId, PtInpatientFeePl params) {
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
                if (params.getLst_allow_plan() != null) {
                    paymentTermsDao.deleteNotAllowPlan(ptId);
                    paymentTermsDao.addNotAllowPlan(ptId, params.getLst_allow_plan());
                }
                ptInpatientFeeDao.update(ptId, params.getMax_inpatient_enable()|0, params.getMax_inpatient()|0, 
                        params.getMax_emergency_enable()|0, params.getMax_emergency()|0, 
                        params.getMax_patient_no_enable()|0, params.getMax_patient_no()|0, params.getExclude_nhi_no_enable()|0, 
                        params.getNot_allow_plan_enable()|0, params.getCoexist_nhi_no_enable()|0, params.getNo_coexist_enable()|0);
            }
        }
        return ret;
    }

    public int deleteInpatientFee(long ptId) {
        int ret = 0;
        if (ptId > 0) {
            ret += paymentTermsDao.deletePaymentTerms(ptId, this.Category);
            if (ret>0) {
                ret += paymentTermsDao.deleteExcludeNhiNo(ptId);
                ret += paymentTermsDao.deleteCoexistNhiNo(ptId);
                ret += paymentTermsDao.deleteNotAllowPlan(ptId);
                ret += ptInpatientFeeDao.delete(ptId);
            }
        }
        return ret;
    }

    public PtInpatientFeePl findPtInpatientFeePl(long ptId) {
      PtInpatientFeePl result = new PtInpatientFeePl();
      if (ptId > 0) {
          java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, Category);
          if (!master.isEmpty()) {
              java.util.Map<String, Object> detail = ptInpatientFeeDao.findOne(ptId);
              
              result.setFee_no((String) master.get("fee_no"));
              result.setFee_name((String) master.get("fee_name"));
              result.setNhi_no((String) master.get("nhi_no"));
              result.setNhi_name((String) master.get("nhi_name"));
              result.setStart_date((Long) master.get("start_date"));
              result.setEnd_date((Long) master.get("end_date"));
              result.setOutpatient_type((Short) master.get("outpatient_type"));
              result.setHospitalized_type((Short) master.get("hospitalized_type"));
              result.setActive((Short) master.get("active"));
              result.setCategory(Category);
              
              result.setMax_inpatient_enable(checkDBColumnType(detail.get("max_inpatient_enable")));
              result.setMax_inpatient(checkDBColumnType(detail.get("max_inpatient")));
              result.setMax_emergency_enable(checkDBColumnType(detail.get("max_emergency_enable")));
              result.setMax_emergency(checkDBColumnType(detail.get("max_emergency")));
              result.setMax_patient_no_enable(checkDBColumnType(detail.get("max_patient_no_enable")));
              result.setMax_patient_no(checkDBColumnType(detail.get("max_patient_no")));
              result.setExclude_nhi_no_enable(checkDBColumnType(detail.get("exclude_nhi_no_enable")));
              result.setNot_allow_plan_enable(checkDBColumnType(detail.get("not_allow_plan_enable")));
              result.setCoexist_nhi_no_enable(checkDBColumnType(detail.get("coexist_nhi_no_enable")));
              result.setNo_coexist_enable(checkDBColumnType(detail.get("no_coexist_enable")));
              result.setLst_nhi_no(paymentTermsDao.filterExcludeNhiNo(ptId));
              result.setLst_co_nhi_no(paymentTermsDao.filterCoexistNhiNo(ptId));
              result.setLst_allow_plan(paymentTermsDao.filterNotAllowPlan(ptId));
          }
      } 
      return result;
  }

}
