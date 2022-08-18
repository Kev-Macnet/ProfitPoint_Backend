package tw.com.leadtek.nhiwidget.service.pt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.leadtek.nhiwidget.dto.PtInjectionFeePl;
import tw.com.leadtek.nhiwidget.dto.PtOthersFeePl;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtOthersFeeDao;
import tw.com.leadtek.tools.Utility;

@Service
public class PtOthersFeeService extends BasicPaymentTerms {

    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtOthersFeeDao ptOthersFeeDao;
    
    public final static String Category = "不分類"; 
    
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
//            ptOthersFeeDao.add(ptId, params.getExclude_nhi_no_enable()|0, params.getMax_inpatient()|0, 
//                    params.getMax_times()|0, params.getInterval_nday()|0,
//                    params.getPatient_nday_enable()|0, params.getPatient_nday_days()|0, params.getPatient_nday_times()|0);
            ptOthersFeeDao.add(ptId, params.getExclude_nhi_no_enable()|0, params.getMax_inpatient_enable()|0, params.getMax_inpatient()|0, 
                    params.getMax_times_enable()|0, params.getMax_times()|0, 
                    params.getInterval_nday_enable()|0, params.getInterval_nday()|0, 
                    params.getPatient_nday_enable()|0, params.getPatient_nday_days()|0, params.getPatient_nday_times()|0);
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
                ptOthersFeeDao.update(ptId, params.getExclude_nhi_no_enable()|0, params.getMax_inpatient_enable()|0, params.getMax_inpatient()|0, 
                        params.getMax_times_enable()|0, params.getMax_times()|0, 
                        params.getInterval_nday_enable()|0, params.getInterval_nday()|0, 
                        params.getPatient_nday_enable()|0, params.getPatient_nday_days()|0, params.getPatient_nday_times()|0);
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

    public PtOthersFeePl findPtOthersFeePl(long ptId) {
      PtOthersFeePl result = new PtOthersFeePl();
      if (ptId > 0) {
          java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, Category);
          if (!master.isEmpty()) {
              java.util.Map<String, Object> detail = ptOthersFeeDao.findOne(ptId);
              
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
              // 每組病歷號碼，每院限申報次數
              result.setMax_inpatient_enable(checkDBColumnType(detail.get("max_inpatient_enable")));
              result.setMax_inpatient(checkDBColumnType(detail.get("max_inpatient")));
              // 限定同患者前一次應用與當次應用待申報此支付標準代碼，每次申報間隔>= ? 日
              result.setInterval_nday_enable(checkDBColumnType(detail.get("interval_nday_enable")));
              result.setInterval_nday(checkDBColumnType(detail.get("interval_nday")));
              // 限定同患者累積申報此支付標準代碼， ? 日內 <= ? 次
              result.setPatient_nday_enable(checkDBColumnType(detail.get("patient_nday_enable")));
              result.setPatient_nday_days(checkDBColumnType(detail.get("patient_nday_days")));
              result.setPatient_nday_times(checkDBColumnType(detail.get("patient_nday_times")));
              // 每組病歷號碼，每院限一年內，限定申報 ? 次
              result.setMax_times_enable(checkDBColumnType(detail.get("max_times_enable")));
              result.setMax_times(checkDBColumnType(detail.get("max_times")));
          }
      } 
      return result;
    }   
 }
