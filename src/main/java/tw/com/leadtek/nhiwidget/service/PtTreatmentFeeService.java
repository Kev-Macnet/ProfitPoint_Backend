package tw.com.leadtek.nhiwidget.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.dto.PtTreatmentFeePl;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtTreatmentFeeDao;
import tw.com.leadtek.tools.Utility;

@Service
public class PtTreatmentFeeService {
    
    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtTreatmentFeeDao ptTreatmentFeeDao;
    
    public final static String Category = "治療處置費"; 
    
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

    //exclude_nhi_no_enable, coexist_nhi_no_enable, max_inpatient_enable, max_inpatient, max_daily_enable, max_daily, every_nday_enable, every_nday_days, every_nday_times, patient_nday_enable, patient_nday_days, patient_nday_times, max_patient_enable, max_patient, include_icd_no_enable, max_month_enable, max_month_percentage, max_age_enable, max_age, lim_division_enable
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
            // ptTreatmentFeeDao.add(ptId, params.getExclude_nhi_no()|0
            ptTreatmentFeeDao.add(ptId, params.getExclude_nhi_no_enable()|0, params.getCoexist_nhi_no_enable()|0, 
                    params.getMax_inpatient_enable()|0, params.getMax_inpatient()|0, 
                    params.getMax_daily_enable()|0, params.getMax_daily()|0, 
                    params.getEvery_nday_enable()|0, params.getEvery_nday_days()|0, params.getEvery_nday_times()|0, 
                    params.getPatient_nday_enable()|0, params.getPatient_nday_days()|0, params.getPatient_nday_times()|0, 
                    params.getMax_patient_enable()|0, params.getMax_patient()|0, 
                    params.getInclude_icd_no_enable()|0, params.getMax_month_enable()|0, params.getMax_month_percentage()|0, 
                    params.getMax_age_enable()|0, params.getMax_age()|0, params.getLim_division_enable()|0);
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
                ptTreatmentFeeDao.update(ptId, params.getExclude_nhi_no_enable()|0, params.getCoexist_nhi_no_enable()|0, 
                        params.getMax_inpatient_enable()|0, params.getMax_inpatient()|0, 
                        params.getMax_daily_enable()|0, params.getMax_daily()|0, 
                        params.getEvery_nday_enable()|0, params.getEvery_nday_days()|0, params.getEvery_nday_times()|0, 
                        params.getPatient_nday_enable()|0, params.getPatient_nday_days()|0, params.getPatient_nday_times()|0, 
                        params.getMax_patient_enable()|0, params.getMax_patient()|0, 
                        params.getInclude_icd_no_enable()|0, params.getMax_month_enable()|0, params.getMax_month_percentage()|0, 
                        params.getMax_age_enable()|0, params.getMax_age()|0, params.getLim_division_enable()|0);
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

    public PtTreatmentFeePl findPtTreatmentFeePl(long ptId) {
      PtTreatmentFeePl result = new PtTreatmentFeePl();
      if (ptId > 0) {
          java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, Category);
          if (!master.isEmpty()) {
              java.util.Map<String, Object> detail = ptTreatmentFeeDao.findOne(ptId);
              
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
              // 需與以下任一支付標準代碼並存(開關)
              result.setCoexist_nhi_no_enable((Short) detail.get("coexist_nhi_no_enable"));
              result.setLst_co_nhi_no(paymentTermsDao.filterCoexistNhiNo(ptId));
              // 年齡限制
              result.setMax_age_enable(checkDBColumnType(detail.get("max_age_enable")));
              result.setMax_age(checkDBColumnType(detail.get("max_age")));
              // 科別限制
              result.setLim_division_enable(checkDBColumnType(detail.get("lim_division_enable")));
              result.setLst_division(paymentTermsDao.filterLimDivision(ptId));
              // 單一就醫紀錄應用數量,限定<=次數
              result.setMax_patient_enable(checkDBColumnType(detail.get("max_patient_enable")));
              result.setMax_patient(checkDBColumnType(detail.get("max_patient")));
              //  單一就醫紀錄上，每日限定應用<= 次             
              result.setMax_daily(checkDBColumnType(detail.get("max_daily")));
              result.setMax_daily_enable(checkDBColumnType(detail.get("max_daily_enable")));
              // 單一就醫紀錄上，每 ? 日內，限定應用<= ? 次
              result.setEvery_nday_enable(checkDBColumnType(detail.get("every_nday_enable")));
              result.setEvery_nday_days(checkDBColumnType(detail.get("every_nday_days")));
              result.setEvery_nday_times(checkDBColumnType(detail.get("every_nday_times")));
              // 同患者限定每<= ? 日，總申報次數<= ? 次
              result.setPatient_nday_enable(checkDBColumnType(detail.get("patient_nday_enable")));
              result.setPatient_nday_days(checkDBColumnType(detail.get("patient_nday_days")));
              result.setPatient_nday_times(checkDBColumnType(detail.get("patient_nday_times")));
              // 單一就醫紀錄上，須包含以下任一ICD診斷碼
              result.setInclude_icd_no_enable(checkDBColumnType(detail.get("include_icd_no_enable")));
              result.setLst_icd_no(paymentTermsDao.filterIncludeIcdNo(ptId));
              // 每組病歷號碼，每院限申報次數
              result.setMax_inpatient_enable((Short) detail.get("max_inpatient_enable"));
              result.setMax_inpatient((Short) detail.get("max_inpatient"));
              // 每月申報數量，不可超過門診就診人次之百分之 ?
              result.setMax_month_enable(checkDBColumnType(detail.get("max_month_enable")));
              result.setMax_month_percentage(checkDBColumnType(detail.get("max_month_percentage")));
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
