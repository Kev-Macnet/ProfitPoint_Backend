package tw.com.leadtek.nhiwidget.service.pt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtAdjustmentFeePl;
import tw.com.leadtek.nhiwidget.dto.PtNutritionalFeePl;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtAdjustmentFeeDao;
import tw.com.leadtek.tools.Utility;

// swagger: http://127.0.0.1:8081/swagger-ui/index.html
@Service
public class PtAdjustmentFeeService extends BasicPaymentTerms {

    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtAdjustmentFeeDao ptAdjustmentFeeDao;
    
    public final static String Category = "調劑費"; //調劑費 (Adjustment fee)
    
    public java.util.Map<String, Object> findAdjustmentFee(long ptId) {
        java.util.Map<String, Object> retMap;
        if (ptId > 0) {
            java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, this.Category);
            if (!master.isEmpty()) {
                java.util.Map<String, Object> detail = ptAdjustmentFeeDao.findOne(ptId);
                for (java.util.Map.Entry<String, Object> entry : detail.entrySet()) {
                    if (!entry.getKey().equals("pt_id")) {
                        master.put(entry.getKey(), entry.getValue());
                    }
                }
                master.put("lst_nhi_no", paymentTermsDao.filterExcludeNhiNo(ptId));
                master.put("lst_co_nhi_no", paymentTermsDao.filterCoexistNhiNo(ptId));
            }
            retMap = master;
        } else {
            retMap = new java.util.HashMap<String, Object>();
        }
        return retMap;
    }

    
    public long addAdjustmentFee(PtAdjustmentFeePl params) {
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
            ptAdjustmentFeeDao.add(ptId, params.getCoexist_nhi_no_enable()|0, params.getExclude_nhi_no_enable()|0);
        }
        return ptId;
    }
    
    
    public int updateAdjustmentFee(long ptId, PtAdjustmentFeePl params) {
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

                ptAdjustmentFeeDao.update(ptId, params.getCoexist_nhi_no_enable()|0, params.getExclude_nhi_no_enable());
            }
        }
        return ret;
    }

    public int deleteAdjustmentFee(long ptId) {
        int ret = 0;
        if (ptId > 0) {
            ret += paymentTermsDao.deletePaymentTerms(ptId, this.Category);
            if (ret>0) {
                ret += paymentTermsDao.deleteExcludeNhiNo(ptId);
                ret += paymentTermsDao.deleteCoexistNhiNo(ptId);
                ret += ptAdjustmentFeeDao.delete(ptId);
            }
        }
        return ret;
    }

    public PtAdjustmentFeePl findPtAdjustmentFeePl(long ptId) {
      PtAdjustmentFeePl result = new PtAdjustmentFeePl();
      if (ptId > 0) {
          java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, Category);
          if (!master.isEmpty()) {
              java.util.Map<String, Object> detail = ptAdjustmentFeeDao.findOne(ptId);
              
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
              result.setCoexist_nhi_no_enable(checkDBColumnType(detail.get("coexist_nhi_no_enable")));
              result.setLst_co_nhi_no(paymentTermsDao.filterCoexistNhiNo(ptId));
          }
      } 
      return result;
    }   

}
