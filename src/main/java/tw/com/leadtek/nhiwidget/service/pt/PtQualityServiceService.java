package tw.com.leadtek.nhiwidget.service.pt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.leadtek.nhiwidget.dto.PtInjectionFeePl;
import tw.com.leadtek.nhiwidget.dto.PtQualityServicePl;
import tw.com.leadtek.nhiwidget.dto.ptNhiNoTimes;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.sql.PtQualityServiceDao;
import tw.com.leadtek.tools.Utility;

// swagger: http://127.0.0.1:8081/swagger-ui/index.html
@Service
public class PtQualityServiceService extends BasicPaymentTerms {

    @Autowired
    private PaymentTermsDao paymentTermsDao;
    @Autowired
    private PtQualityServiceDao ptQualityServiceDao;
    
    public static final String Category = "品質支付服務"; 
    
    public java.util.Map<String, Object> findQualityService(long ptId) {
        java.util.Map<String, Object> retMap;
        if (ptId > 0) {
            java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, this.Category);
            if (!master.isEmpty()) {
                java.util.Map<String, Object> detail = ptQualityServiceDao.findOne(ptId);
                for (java.util.Map.Entry<String, Object> entry : detail.entrySet()) {
                    if (!entry.getKey().equals("pt_id")) {
                        master.put(entry.getKey(), entry.getValue());
                    }
                }
                master.put("lst_co_nhi_no", paymentTermsDao.filterCoexistNhiNoTimes(ptId));
            }
            retMap = master;
        } else {
            retMap = new java.util.HashMap<String, Object>();
        }
        return retMap;
    }

    public long addQualityService(PtQualityServicePl params) {
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        params.setCategory(this.Category);
        long ptId = paymentTermsDao.addPaymentTerms(params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                    start_date, end_data, params.getCategory(), 
                                                    params.getHospital_type(), params.getOutpatient_type(), params.getHospitalized_type());
        if (ptId>0) {
            if (params.getLst_co_nhi_no() != null) {
                paymentTermsDao.addCoexistNhiNoTimes(ptId, params.getLst_co_nhi_no());
            }

            ptQualityServiceDao.add(ptId, params.getInterval_nday_enable()|0, params.getInterval_nday()|0, 
                    params.getCoexist_nhi_no_enable()|0,  
                    params.getEvery_nday_enable()|0, params.getEvery_nday_days()|0, params.getEvery_nday_times()|0);
        }
        return ptId;
    }
    
    public int updateQualityService(long ptId, PtQualityServicePl params) {
        int ret = 0;
        java.util.Date start_date = Utility.detectDate(String.valueOf(params.getStart_date()));
        java.util.Date end_data = Utility.detectDate(String.valueOf(params.getEnd_date()));
        if (ptId>0) {
            ret += paymentTermsDao.updatePaymentTerms(ptId, params.getFee_no(), params.getFee_name(), params.getNhi_no(), params.getNhi_name(), 
                                                  start_date, end_data, this.Category, 
                                                  params.getHospital_type(), params.getOutpatient_type(), params.getHospitalized_type());
            if (ret>0) {
                if (params.getLst_co_nhi_no() != null) {
                    paymentTermsDao.deleteCoexistNhiNoTimes(ptId);
                    paymentTermsDao.addCoexistNhiNoTimes(ptId, params.getLst_co_nhi_no());
                }
                ptQualityServiceDao.update(ptId, params.getInterval_nday_enable()|0, params.getInterval_nday()|0, 
                        params.getCoexist_nhi_no_enable()|0, 
                        params.getEvery_nday_enable()|0, params.getEvery_nday_days()|0, params.getEvery_nday_times()|0);
            }
        }
        return ret;
    }

    public int deleteQualityService(long ptId) {
        int ret = 0;
        if (ptId > 0) {
            ret += paymentTermsDao.deletePaymentTerms(ptId, this.Category);
            if (ret>0) {
                ret += paymentTermsDao.deleteCoexistNhiNoTimes(ptId);
                ret += ptQualityServiceDao.delete(ptId);
            }
        }
        return ret;
    }

    public PtQualityServicePl findPtQualityServicePl(long ptId) {
      PtQualityServicePl result = new PtQualityServicePl();
      if (ptId > 0) {
          java.util.Map<String, Object> master = paymentTermsDao.findPaymentTerms(ptId, Category);
          if (!master.isEmpty()) {
              java.util.Map<String, Object> detail = ptQualityServiceDao.findOne(ptId);
              
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
              
              // 限定同患者執行過 ? 支付標準代碼，>= ? 次，方可申報
              result.setCoexist_nhi_no_enable(checkDBColumnType(detail.get("coexist_nhi_no_enable")));
              List<Map<String, Object>> mapList =  paymentTermsDao.filterCoexistNhiNoTimes(ptId);
              List<ptNhiNoTimes> ptNhiNoTimesList = new ArrayList<ptNhiNoTimes>();
              for (Map<String, Object> map : mapList) {
                ptNhiNoTimes pt = new ptNhiNoTimes();
                pt.setNhi_no((String) map.get("nhi_no"));
                pt.setTimes(checkDBColumnType(map.get("times")));
                ptNhiNoTimesList.add(pt);
              }
              result.setLst_co_nhi_no(ptNhiNoTimesList);
              
              // 限定同患者累積申報此支付標準代碼， ? 日內 <= ? 次
              result.setEvery_nday_enable(checkDBColumnType(detail.get("every_nday_enable")));
              result.setEvery_nday_days(checkDBColumnType(detail.get("every_nday_days")));
              result.setEvery_nday_times(checkDBColumnType(detail.get("every_nday_times")));
              
              // 限定同患者前一次應用與當次應用待申報此支付標準代碼，每次申報間隔>= ? 日
              result.setInterval_nday_enable(checkDBColumnType(detail.get("interval_nday_enable")));
              result.setInterval_nday(checkDBColumnType(detail.get("interval_nday")));
          }
      } 
      return result;
    }   
}
