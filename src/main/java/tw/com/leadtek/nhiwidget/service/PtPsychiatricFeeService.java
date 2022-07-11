package tw.com.leadtek.nhiwidget.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtPsychiatricFeePl;
import tw.com.leadtek.nhiwidget.dto.PtTreatmentFeePl;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtPsychiatricFeeDao;
import tw.com.leadtek.tools.Utility;

@Service
public class PtPsychiatricFeeService {

    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtPsychiatricFeeDao ptPsychiatricFeeDao;
    
    public final static String Category = "精神醫療治療費"; 
    
    public java.util.Map<String, Object> findPsychiatricFee(long ptId) {
        java.util.Map<String, Object> retMap;
        if (ptId > 0) {
            java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, this.Category);
            if (!master.isEmpty()) {
                java.util.Map<String, Object> detail = ptPsychiatricFeeDao.findOne(ptId);
                for (java.util.Map.Entry<String, Object> entry : detail.entrySet()) {
                    if (!entry.getKey().equals("pt_id")) {
                        master.put(entry.getKey(), entry.getValue());
                    }
                }
                master.put("lst_nhi_no", paymentTermsDao.filterExcludeNhiNo(ptId));
                master.put("lst_division", paymentTermsDao.filterLimDivision(ptId));
                //exclude_nhi_no, lim_division
            }
            retMap = master;
        } else {
            retMap = new java.util.HashMap<String, Object>();
        }
        return retMap;
    }

    public long addPsychiatricFee(PtPsychiatricFeePl params) {
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        params.setCategory(this.Category);
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

//            ptPsychiatricFeeDao.add(ptId, params.getExclude_nhi_no()|0, params.getPatient_nday()|0, params.getPatient_nday_days()|0, 
//                    params.getPatient_nday_times()|0, params.getMax_inpatient()|0, params.getLim_division()|0);
            ptPsychiatricFeeDao.add(ptId, params.getExclude_nhi_no_enable()|0, params.getPatient_nday_enable()|0, params.getPatient_nday_days()|0, params.getPatient_nday_times()|0, 
                    params.getMax_inpatient_enable()|0, params.getMax_inpatient()|0, params.getLim_division_enable()|0);
        }
        return ptId;
    }
    
    public int updatePsychiatricFee(long ptId, PtPsychiatricFeePl params) {
        int ret = 0;
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        if (ptId>0) {
            ret += paymentTermsDao.updatePaymentTerms(ptId, params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                  start_date, end_data, this.Category, 
                                                  params.getHospital_type(), params.getOutpatient_type(), params.getHospitalized_type());
            if (ret>0) {
                if (params.getLst_division() != null) {
                    paymentTermsDao.deleteLimDivision(ptId);
                    paymentTermsDao.addLimDivision(ptId, params.getLst_division());
                }
                if (params.getLst_nhi_no() != null) {
                    paymentTermsDao.deleteExcludeNhiNo(ptId);
                    paymentTermsDao.addExcludeNhiNo(ptId, params.getLst_nhi_no());
                }
                ptPsychiatricFeeDao.update(ptId, params.getExclude_nhi_no_enable()|0, params.getPatient_nday_enable()|0, params.getPatient_nday_days()|0, params.getPatient_nday_times()|0, 
                        params.getMax_inpatient_enable()|0, params.getMax_inpatient()|0, params.getLim_division_enable()|0);
            }
        }
        return ret;
    }

    public int deletePsychiatricFee(long ptId) {
        int ret = 0;
        if (ptId > 0) {
            ret += paymentTermsDao.deletePaymentTerms(ptId, this.Category);
            if (ret>0) {
                ret += paymentTermsDao.deleteExcludeNhiNo(ptId);
                ret += paymentTermsDao.deleteLimDivision(ptId);
                ret += ptPsychiatricFeeDao.delete(ptId);
            }
        }
        return ret;
    }

    public PtPsychiatricFeePl findPtPsychiatricFeePl(long ptId) {
      PtPsychiatricFeePl result = new PtPsychiatricFeePl();
      if (ptId > 0) {
          java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, Category);
          if (!master.isEmpty()) {
              java.util.Map<String, Object> detail = ptPsychiatricFeeDao.findOne(ptId);
              
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
              
              //不可與此支付標準代碼並存單一就醫紀錄一併申報(開關)
              result.setExclude_nhi_no_enable(checkDBColumnType(detail.get("exclude_nhi_no_enable")));
              result.setLst_nhi_no(paymentTermsDao.filterExcludeNhiNo(ptId));
              // 科別限制
              result.setLim_division_enable(checkDBColumnType(detail.get("lim_division_enable")));
              result.setLst_division(paymentTermsDao.filterLimDivision(ptId));
              // 同患者限定每<= ? 日，總申報次數<= ? 次
              result.setPatient_nday_enable(checkDBColumnType(detail.get("patient_nday_enable")));
              result.setPatient_nday_days(checkDBColumnType(detail.get("patient_nday_days")));
              result.setPatient_nday_times(checkDBColumnType(detail.get("patient_nday_times")));
              // 每組病歷號碼，每院限申報次數
              result.setMax_inpatient_enable((Short) detail.get("max_inpatient_enable"));
              result.setMax_inpatient((Short) detail.get("max_inpatient"));
          }
      } 
      return result;
    }   
    
    private int checkDBColumnType(Object obj) {
      if (obj instanceof Integer) {
        return (Integer) obj;
      } else {
        return (Short) obj;
      }
    }
}
