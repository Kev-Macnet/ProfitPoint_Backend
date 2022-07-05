package tw.com.leadtek.nhiwidget.service;


import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtOutpatientFeePl;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtOutpatientFeeDao;
import tw.com.leadtek.tools.Utility;

// swagger: http://127.0.0.1:8081/swagger-ui/index.html
@Service
public class PtOutpatientFeeService {
    
    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtOutpatientFeeDao ptOutpatientFeeDao;
    @Autowired
    private PaymentTermsService paymentTermsService;
    
    public final static String Category = "門診診察費";
    
    public java.util.Map<String, Object> findOutpatientFee(long ptId) {
        java.util.Map<String, Object> retMap;
        if (ptId > 0) {
            java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, Category);
            if (!master.isEmpty()) {
                java.util.Map<String, Object> detail = ptOutpatientFeeDao.findOne(ptId);
                for (java.util.Map.Entry<String, Object> entry : detail.entrySet()) {
                    if (!entry.getKey().equals("pt_id")) {
                        master.put(entry.getKey(), entry.getValue());
                    }
                }
                master.put("lst_nhi_no", paymentTermsDao.filterExcludeNhiNo(ptId));
                master.put("lst_division", paymentTermsDao.filterLimDivision(ptId));
            }
            retMap = master;
        } else {
            retMap = new java.util.HashMap<String, Object>();
        }
        return retMap;
    }

    public long addOutpatientFee(PtOutpatientFeePl params) {
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
            ptOutpatientFeeDao.add(ptId, params.getNo_dentisit()|0, params.getNo_chi_medicine()|0, params.getNo_service_charge()|0, 
                    params.getLim_out_islands()|0, params.getLim_holiday()|0, 
                    params.getLim_max_enable()|0, params.getLim_max()|0, 
                    params.getLim_age_enable()|0, params.getLim_age_type()|0, params.getLim_age()|0, 
                    params.getLim_division_enable()|0, params.getExclude_nhi_no_enable()|0);
        }
        return ptId;
    }
    
    public int updateOutpatientFee(long ptId, PtOutpatientFeePl params) {
        int ret = 0;
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        if (ptId>0) {
            ret += paymentTermsDao.updatePaymentTerms(ptId, params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                  start_date, end_data, Category, 
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
                ptOutpatientFeeDao.update(ptId, params.getNo_dentisit()|0, params.getNo_chi_medicine()|0, params.getNo_service_charge()|0, 
                        params.getLim_out_islands()|0, params.getLim_holiday()|0, 
                        params.getLim_max_enable()|0, params.getLim_max()|0, 
                        params.getLim_age_enable()|0, params.getLim_age_type()|0, params.getLim_age()|0, 
                        params.getLim_division_enable()|0, params.getExclude_nhi_no_enable()|0);
                paymentTermsService.updateActiveByThread(ptId, Category, params.getActive(), true);
            }
        }
        return ret;
    }

    public int deleteOutpatientFee(long ptId) {
        int ret = 0;
        if (ptId > 0) {
          paymentTermsService.updateActiveByThread(ptId, Category, 0, false);
            ret += paymentTermsDao.deletePaymentTerms(ptId, Category);
            if (ret>0) {
                ret += paymentTermsDao.deleteExcludeNhiNo(ptId);
                ret += paymentTermsDao.deleteLimDivision(ptId);
                ret += ptOutpatientFeeDao.delete(ptId);
            }
        }
        return ret;
    }

    public PtOutpatientFeePl findPtOutpatientFeePl(long ptId) {
      PtOutpatientFeePl result = new PtOutpatientFeePl();
      if (ptId > 0) {
          java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, Category);
          if (!master.isEmpty()) {
              java.util.Map<String, Object> detail = ptOutpatientFeeDao.findOne(ptId);
              for (java.util.Map.Entry<String, Object> entry : detail.entrySet()) {
                  if (!entry.getKey().equals("pt_id")) {
                      master.put(entry.getKey(), entry.getValue());
                  }
              }
              result.setFee_no((String) master.get("fee_no"));
              result.setFee_name((String) master.get("fee_name"));
              result.setNhi_no((String) master.get("nhi_no"));
              result.setNhi_name((String) master.get("nhi_name"));
              result.setStart_date((Long) master.get("start_date"));
              result.setEnd_date((Long) master.get("end_date"));
              result.setOutpatient_type(checkDBColumnType(master.get("outpatient_type")));  
              result.setHospitalized_type(checkDBColumnType(master.get("hospitalized_type")));
              result.setActive(checkDBColumnType(master.get("active")));
              result.setCategory(Category);
              result.setNo_dentisit(checkDBColumnType(detail.get("no_dentisit")));
              result.setNo_chi_medicine(checkDBColumnType(detail.get("no_chi_medicine")));
              result.setNo_service_charge(checkDBColumnType(detail.get("no_service_charge")));
              result.setLim_out_islands(checkDBColumnType(detail.get("lim_out_islands")));
              result.setLim_holiday(checkDBColumnType(detail.get("lim_holiday")));
              result.setLim_max_enable(checkDBColumnType(detail.get("lim_max_enable")));
              result.setLim_max(checkDBColumnType(detail.get("lim_max")));
              result.setLim_age_enable(checkDBColumnType(detail.get("lim_age_enable")));
              result.setLim_age_type(checkDBColumnType(detail.get("lim_age_type")));
              result.setLim_age(checkDBColumnType(detail.get("lim_age")));
              result.setLim_division_enable(checkDBColumnType(detail.get("lim_division_enable")));
              result.setExclude_nhi_no_enable(checkDBColumnType(detail.get("exclude_nhi_no_enable")));
              result.setLst_nhi_no(paymentTermsDao.filterExcludeNhiNo(ptId));
              result.setLst_division(paymentTermsDao.filterLimDivision(ptId));
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
