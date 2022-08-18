package tw.com.leadtek.nhiwidget.service.pt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dto.PaymentTermsPl;
import tw.com.leadtek.nhiwidget.model.rdb.INTELLIGENT;
import tw.com.leadtek.nhiwidget.service.IntelligentService;
import tw.com.leadtek.nhiwidget.service.ParametersService;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.nhiwidget.task.service.PtInpatientFeeServiceTask;
import tw.com.leadtek.nhiwidget.task.service.PtOutpatientFeeServiceTask;
import tw.com.leadtek.tools.Utility;

// swagger: http://127.0.0.1:8081/swagger-ui/index.html
@Service
public class PaymentTermsService extends BasicPaymentTerms {
//    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    protected Logger logger = LogManager.getLogger();
  
    @Autowired
    private PaymentTermsDao paymentTermsDao;
    
    @Autowired
    private PtOutpatientFeeService ptOutpatientFeeService;
    
    @Autowired
    private PtInpatientFeeService ptInpatientFeeService;
    
    @Autowired
    private PtWardFeeService ptWardFeeService;
    
    @Autowired
    private PtPsychiatricWardFeeService ptPsychiatricWardFeeService;
    
    @Autowired
    private PtSurgeryFeeService ptSurgeryFeeService;
    
    @Autowired
    private PtTreatmentFeeService ptTreatmentFeeService;
    
    @Autowired 
    private PtNutritionalFeeService ptNutritionalFeeService;
    
    @Autowired
    private PtAdjustmentFeeService ptAdjustmentFeeService;
    
    @Autowired
    private PtMedicineFeeService ptMedicineFeeService;
    
    @Autowired
    private PtInjectionFeeService ptInjectionFeeService;
    
    @Autowired
    private PtRadiationFeeService ptRadiationFeeService;
    
    @Autowired
    private PtQualityServiceService ptQualityServiceService;
    
    @Autowired
    private PtInpatientCareService ptInpatientCareService;
    
    @Autowired
    private PtRehabilitationFeeService ptRehabilitationFeeService;
    
    @Autowired
    private PtPsychiatricFeeService ptPsychiatricFeeService;
    
    @Autowired
    private PtBoneMarrowTransFeeService ptBoneMarrowTransFeeService;
    
    @Autowired
    private PtPlasterBandageFeeService ptPlasterBandageFeeService;
    
    @Autowired
    private PtAnesthesiaFeeService ptAnesthesiaFeeService;
    
    @Autowired
    private PtSpecificMedicalFeeService ptSpecificMedicalFeeService;
    
    @Autowired
    private PtOthersFeeService ptOthersFeeService;
    
    @Autowired
    private PtOutpatientFeeServiceTask ptOutpatientFeeServiceTask;
    
    @Autowired
    private PtInpatientFeeServiceTask ptInpatientFeeServiceTask;
    
    @Autowired
    private ParametersService parametersService;
    
    @Autowired
    private ViolatePaymentTermsService vpts;
    
    @Autowired
    private IntelligentService is;

    public java.util.Map<String, Object> searchPaymentTerms(String feeNo, String nhiNo, String category, 
            java.util.Date startDate, java.util.Date endDate, int pageSize, int pageIndex,
            String sortField, String sortDirection) {
        long totalCount = paymentTermsDao.searchPaymentTermsCount(feeNo, nhiNo, category, startDate, endDate);
        int start = pageSize*pageIndex;
        if (start>totalCount) {
            start = (int)totalCount;
        } else if (start<0) {
            start = 0;
        }
        java.util.List<Map<String, Object>> lst = paymentTermsDao.searchPaymentTerms(feeNo, nhiNo, category, startDate, endDate, start, pageSize, sortField, sortDirection);
        if (lst.size()==0) {
            totalCount = paymentTermsDao.searchPaymentTermsByDateRangeCount(feeNo, nhiNo, category, startDate, endDate);
            start = pageSize*pageIndex;
            if (start>totalCount) {
                start = (int)totalCount;
            } else if (start<0) {
                start = 0;
            }
            lst = paymentTermsDao.searchPaymentTermsByDateRange(feeNo, nhiNo, category, startDate, endDate, start, pageSize, sortField, sortDirection);
        }
        
        java.util.Map<String, Object> retMap = new java.util.LinkedHashMap<String, Object>();
        retMap.put("total", totalCount);
        retMap.put("pages", (int)totalCount/pageSize + ((totalCount%pageSize)>0 ? 1: 0));
        retMap.put("pageIndex", pageIndex);
        retMap.put("pageSize", pageSize);
        retMap.put("data", lst);
        return retMap;
    }
    
    public java.util.Map<String, Object> jwtValidate(String jwt, int roleNo) { //roleNo default=4
        java.util.Map<String, Object> validationMap = Utility.jwtValidate(jwt);
        if ((int)validationMap.get("status") == 200) {
            String role = findUserRole(validationMap.get("userName").toString());
            // "A: MIS主管, B: 行政主管, C: 申報主管, D: coding人員/申報人員, E: 醫護人員, Z: 原廠開發者" 
            java.util.List<String> lstRole = null; // = new java.util.ArrayList<String>();
            if (roleNo==1) {
                String arr[] = {"Z"};
                lstRole = java.util.Arrays.asList(arr);
            } else if (roleNo==2) {
                String arr[] = {"A"};
                lstRole = java.util.Arrays.asList(arr);
            } else if (roleNo==3) {
                String arr[] = {"A","Z"};
                lstRole = java.util.Arrays.asList(arr);
            } else if (roleNo==4) {
                String arr[] = {"A","C","Z"};
                lstRole = java.util.Arrays.asList(arr);
            } else if (roleNo>=5) {
                String arr[] = {"A","B","C","D","E","Z"};
                lstRole = java.util.Arrays.asList(arr);
            } else {
                lstRole = new java.util.ArrayList<String>();
            }
            if (listStrIndexOf(role, lstRole) < 0) {
                validationMap.put("status", 401);
                validationMap.put("message", "權限不足!");
            }
        }
        return validationMap;
    }
    
    
    public int updateActive(long id, String category, int state) {
      if (is.isIntelligentRunning(INTELLIGENT_REASON.VIOLATE.value())){
        return -1;
      }
      Map<String, Object> master = paymentTermsDao.findPaymentTerms(id, category);
      if (!master.isEmpty()) {
        //System.out.println("updateActive id:" + id  + "(" + category + ") state=" + state + ", db:" + master.get("active"));
        if (checkDBColumnType(master.get("active")) == state) {
          // 狀態一樣，不異動
          return -1;
        }
        int result = paymentTermsDao.updatePaymentTermsActive(id, category, state);
        updateActiveByThread(id, category, state, false);
        return result;
      }
      return -1;
    }
    
    private String findUserRole(String userName) {
        return paymentTermsDao.findUserRole(userName);
    }
    
    private int listStrIndexOf(String key, java.util.List<String> lstStr) {
        int ret = -1;
        int idx = 0;
        for (String str : lstStr) {
            if (key.equals(str)) {
                ret = idx;
                break;
             }
            idx++;
        }
        return (ret);
     }
    
    public void correctEndDate(String category) {
        java.util.List<Map<String, Object>> lstNhiNo = paymentTermsDao.findByGroupNhiNo(category, 1);
        for (Map<String, Object> item : lstNhiNo) { //nhi_no, category
//            System.out.println("nhi_no = "+item.get("nhi_no").toString()+", "+item.get("category").toString());
            correctEndDateByNhiNo(item.get("nhi_no").toString(), item.get("category").toString());
        }
    }
    
    public void correctEndDateByNhiNo(String nhiNo, String category) {
        long nextStartDate, endDate;
        java.util.Map<String, Object> nxetMap;
        java.util.List<Map<String, Object>> lstData;
        if (nhiNo.length()>0) {
            lstData = paymentTermsDao.findByNhiNoCategory(nhiNo, category);
            int idx = 1;
            long dataLen = lstData.size();
            for (Map<String, Object> item : lstData) {
                if (idx < dataLen) {
                    nxetMap = lstData.get(idx);
                    nextStartDate = (long)nxetMap.get("start_date");
                    if (item.get("end_date")==null) {
                        endDate = 4102358400000l;
                    } else {
                        endDate = (long)item.get("end_date");
                    }
                    java.util.Date prevDay = new java.util.Date(nextStartDate-(86400*1000));
//                        System.out.println("   end_date="+Utility.dateFormat(new Date(endDate),"yyyy-MM-dd")+", "
//                                +Utility.dateFormat(new Date(nextStartDate),"yyyy-MM-dd")+", "
//                                +Utility.dateFormat(prevDay,"yyyy-MM-dd"));
                    if (nextStartDate<endDate) {
                        paymentTermsDao.updateEndDate((long)item.get("id"), prevDay);
                    }
                }
                idx++;
            }
        }
    }
    
    /**
     * 避免執行違反支付準則條件時間讓user等待過久，且防止user短時間內重複按多次，用thread執行
     * @param id
     * @param category
     */
    public void updateActiveByThread(long id, String category, int state, boolean useThread) {
      PaymentTermsPl pt = findRealPaymentTerms(id, category);
      if (pt == null) {
        return;
      }
      long time = is.getIntelligentRunningTime(INTELLIGENT_REASON.VIOLATE.value());
      if (state == 1 && time == id) {
        // 表示正在執行同一組支付代碼是否違反條件判斷
        return;
      }
        Thread thread = new Thread(new Runnable() {

          @Override
          public void run() {
            logger.info("start run " + id + " checkFee:" + pt.getNhi_no());
            int waitTimes = 0;
            do {
              try {
                Thread.sleep(500);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              waitTimes++;
              if ((waitTimes % 60) == 0) {
                logger.info("updateActiveByThread wait " + id + ":" + pt.getNhi_no() + " for " + waitTimes + " times");
              }
            } while (is.isIntelligentRunning(INTELLIGENT_REASON.VIOLATE.value()));
            if (state == 0) {
              is.setIntelligentRunning(INTELLIGENT_REASON.VIOLATE.value(), true);
            } else {
              is.setIntelligentRunningTime(INTELLIGENT_REASON.VIOLATE.value(), id);
            }
            
            parametersService.deleteIntelligent(INTELLIGENT_REASON.VIOLATE.value(), pt.getNhi_no(),
                null);
            if (state == 1 && !"0".equals(parametersService.getParameter("VIOLATE"))) {
              List<INTELLIGENT> batch = new ArrayList<INTELLIGENT>();
              try {
                logger.info("start run " + id + " checkFee wait " + waitTimes + " times.");
                vpts.checkFee(pt, batch);
                //System.out.println("finish checkFee");
              } catch (Exception e) {
                e.printStackTrace();
              }
              is.saveIntelligentBatch(batch);
            }
            is.setIntelligentRunning(INTELLIGENT_REASON.VIOLATE.value(), false);
            logger.info("start run " + id + " checkFee finished:" + pt.getNhi_no());
          }
        });
        if (useThread) {
          thread.start();
        } else {
          thread.run();
        }
    }
    
    public PaymentTermsPl findRealPaymentTerms(long id, String category) {
      PaymentTermsPl result = null;
      if (PtOutpatientFeeService.Category.equals(category)) {
        result = ptOutpatientFeeService.findPtOutpatientFeePl(id);
      } else if (PtInpatientFeeService.Category.equals(category)) {
        result = ptInpatientFeeService.findPtInpatientFeePl(id);
      } else if (PtWardFeeService.Category.equals(category)) {
        result = ptWardFeeService.findPtWardFeePl(id);
      } else if (PtPsychiatricWardFeeService.Category.equals(category)) {
        result = ptPsychiatricWardFeeService.findPtPsychiatricWardFeePl(id);
      } else if (PtSurgeryFeeService.Category.equals(category)) {
        result = ptSurgeryFeeService.findSurgeryFeePl(id);
      } else if (PtTreatmentFeeService.Category.equals(category)) {
        result = ptTreatmentFeeService.findPtTreatmentFeePl(id);
      } else if (PtNutritionalFeeService.Category.equals(category)) {
        result = ptNutritionalFeeService.findPtNutritionalFeePl(id);
      } else if (PtAdjustmentFeeService.Category.equals(category)) {
        result = ptAdjustmentFeeService.findPtAdjustmentFeePl(id);
      } else if (PtMedicineFeeService.Category.equals(category)) {
        result = ptMedicineFeeService.findPtMedicineFeePl(id);
      } else if (PtRadiationFeeService.Category.equals(category)) {
        result = ptRadiationFeeService.findPtRadiationFeePl(id);
      } else if (PtInjectionFeeService.Category.equals(category)) {
        result = ptInjectionFeeService.findPtInjectionFeePl(id);
      } else if (PtQualityServiceService.Category.equals(category)) {
        result = ptQualityServiceService.findPtQualityServicePl(id);
      } else if (PtRehabilitationFeeService.Category.equals(category)) {
        result = ptRehabilitationFeeService.findPtRehabilitationFeePl(id);
      } else if (PtPsychiatricFeeService.Category.equals(category)) {
        result = ptPsychiatricFeeService.findPtPsychiatricFeePl(id); 
      } else if (PtBoneMarrowTransFeeService.Category.equals(category)) {
        result = ptBoneMarrowTransFeeService.findPtBoneMarrowTransFeePl(id);
      } else if (PtAnesthesiaFeeService.Category.equals(category)) {
        result = ptAnesthesiaFeeService.findPtAnesthesiaFeePl(id);
      } else if (PtSpecificMedicalFeeService.Category.equals(category)) {
        result = ptSpecificMedicalFeeService.findPtSpecificMedicalFeePl(id);
      } else if (PtOthersFeeService.Category.equals(category)) {
        result = ptOthersFeeService.findPtOthersFeePl(id);
      }
   
      if (result == null || result.getNhi_no() == null) {
        return null;
      }
      return result;
    }
}
