package tw.com.leadtek.nhiwidget.service.pt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtNutritionalFeePl;
import tw.com.leadtek.nhiwidget.dto.PtTreatmentFeePl;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtNutritionalFeeDao;
import tw.com.leadtek.tools.Utility;

@Service
public class PtNutritionalFeeService extends BasicPaymentTerms {
    
    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtNutritionalFeeDao ptNutritionalFeeDao;
    
    public final static String Category = "管灌飲食費及營養照護費"; 
    
    public java.util.Map<String, Object> findNutritionalFee(long ptId) {
        java.util.Map<String, Object> retMap;
        if (ptId > 0) {
            java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, this.Category);
            if (!master.isEmpty()) {
                java.util.Map<String, Object> detail = ptNutritionalFeeDao.findOne(ptId);
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

    public long addNutritionalFee(PtNutritionalFeePl params) {
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
//            ptNutritionalFeeDao.add(ptId, params.getMax_inpatient()|0, params.getMax_daily()|0, 
//                    params.getEvery_nday()|0, params.getEvery_nday_days()|0, params.getEvery_nday_times()|0, 
//                    params.getOver_nday()|0, params.getOver_nday_days()|0, params.getOver_nday_times()|0, 
//                    params.getExclude_nhi_no()|0);
            ptNutritionalFeeDao.add(ptId, params.getMax_inpatient_enable()|0, params.getMax_inpatient()|0, 
                    params.getMax_daily_enable()|0, params.getMax_daily()|0, 
                    params.getEvery_nday_enable()|0, params.getEvery_nday_days()|0, params.getEvery_nday_times()|0, 
                    params.getOver_nday_enable()|0, params.getOver_nday_days()|0, params.getOver_nday_times()|0, 
                    params.getExclude_nhi_no_enable()|0);
        }
        return ptId;
    }
    
    public int updateNutritionalFee(long ptId, PtNutritionalFeePl params) {
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
                ptNutritionalFeeDao.update(ptId, params.getMax_inpatient_enable()|0, params.getMax_inpatient()|0, 
                        params.getMax_daily_enable()|0, params.getMax_daily()|0, 
                        params.getEvery_nday_enable()|0, params.getEvery_nday_days()|0, params.getEvery_nday_times()|0, 
                        params.getOver_nday_enable()|0, params.getOver_nday_days()|0, params.getOver_nday_times()|0, 
                        params.getExclude_nhi_no_enable()|0);
            }
        }
        return ret;
    }

    public int deleteNutritionalFee(long ptId) {
        int ret = 0;
        if (ptId > 0) {
            ret += paymentTermsDao.deletePaymentTerms(ptId, this.Category);
            if (ret>0) {
                ret += paymentTermsDao.deleteExcludeNhiNo(ptId);
                ret += ptNutritionalFeeDao.delete(ptId);
            }
        }
        return ret;
    }

    public PtNutritionalFeePl findPtNutritionalFeePl(long ptId) {
      PtNutritionalFeePl result = new PtNutritionalFeePl();
      if (ptId > 0) {
          java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, Category);
          if (!master.isEmpty()) {
              java.util.Map<String, Object> detail = ptNutritionalFeeDao.findOne(ptId);
              
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
              // 單一住院就醫紀錄應用數量,限定<=次數
              result.setMax_inpatient_enable(checkDBColumnType(detail.get("max_inpatient_enable")));
              result.setMax_inpatient(checkDBColumnType(detail.get("max_inpatient")));
              //  單一就醫紀錄上，每日限定應用<= 次             
              result.setMax_daily(checkDBColumnType(detail.get("max_daily")));
              result.setMax_daily_enable(checkDBColumnType(detail.get("max_daily_enable")));
              // 單一就醫紀錄上，每 ? 日內，限定應用<= ? 次
              result.setEvery_nday_enable(checkDBColumnType(detail.get("every_nday_enable")));
              result.setEvery_nday_days(checkDBColumnType(detail.get("every_nday_days")));
              result.setEvery_nday_times(checkDBColumnType(detail.get("every_nday_times")));
              // 單一就醫紀錄上，超過 ? 日後，超出天數部份，限定應用<= ? 次
              result.setOver_nday_enable(checkDBColumnType(detail.get("over_nday_enable")));
              result.setOver_nday_days(checkDBColumnType(detail.get("over_nday_days")));
              result.setOver_nday_times(checkDBColumnType(detail.get("over_nday_times")));
          }
      } 
      return result;
    }   

}
