/**
 * Created on 2021/11/19.
 */
package tw.com.leadtek.nhiwidget.service;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.constant.XMLConstant;
import tw.com.leadtek.nhiwidget.dao.CODE_THRESHOLDDao;
import tw.com.leadtek.nhiwidget.dao.INTELLIGENTDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_THRESHOLD;
import tw.com.leadtek.nhiwidget.model.rdb.INTELLIGENT;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.payload.intelligent.IntelligentRecord;
import tw.com.leadtek.nhiwidget.payload.intelligent.IntelligentResponse;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.tools.DateTool;
import tw.com.leadtek.tools.Utility;

@Service
public class IntelligentService {

  private Logger logger = LogManager.getLogger();

  @Autowired
  private INTELLIGENTDao intelligentDao;

  @Autowired
  private CODE_THRESHOLDDao ctDao;

  @Autowired
  private MRDao mrDao;

  @Autowired
  private ParametersService parameterService;

  @Autowired
  private CodeTableService codeTableService;

  @Autowired
  private OP_PDao oppDao;

  @Autowired
  private IP_PDao ippDao;

  public IntelligentResponse getIntelligent(UserDetailsImpl user, Date sDate, Date eDate,
      Integer minPoints, Integer maxPoints, String funcType, String funcTypec, String prsnId,
      String prsnName, String code, String inhCode, String icd, String orderBy, Boolean asc,
      int perPage, int page) {
    IntelligentResponse result = new IntelligentResponse();

    Specification<INTELLIGENT> spec = new Specification<INTELLIGENT>() {

      private static final long serialVersionUID = 1011L;

      public Predicate toPredicate(Root<INTELLIGENT> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (sDate != null && eDate != null) {
          predicate.add(cb.and(cb.between(root.get("startDate"), sDate, eDate),
              cb.between(root.get("endDate"), sDate, eDate)));
        }
        if (minPoints != null) {
          predicate.add(cb.greaterThanOrEqualTo(root.get("applDot"), minPoints));
        }
        if (maxPoints != null) {
          predicate.add(cb.lessThanOrEqualTo(root.get("applDot"), maxPoints));
        }
        addPredicate(root, predicate, cb, "prsnId", prsnId, true, false);
        addPredicate(root, predicate, cb, "funcType", funcType, true, false);
        addPredicate(root, predicate, cb, "funcTypec", funcTypec, true, false);
        addPredicate(root, predicate, cb, "prsnName", prsnName, true, false);
        addPredicate(root, predicate, cb, "code", code, true, false);
        addPredicate(root, predicate, cb, "inhCode", inhCode, true, false);
        addPredicate(root, predicate, cb, "icd", icd, true, false);
        predicate.add(cb.equal(root.get("status"), MR_STATUS.WAIT_CONFIRM.value()));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));

        List<Order> orderList = new ArrayList<Order>();
        if (orderBy != null && asc != null) {
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
        } else {
          orderList.add(cb.desc(root.get("startDate")));
        }
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
    result.setCount((int) intelligentDao.count(spec));
    Page<INTELLIGENT> pages = intelligentDao.findAll(spec, PageRequest.of(page, perPage));
    List<IntelligentRecord> list = new ArrayList<IntelligentRecord>();
    if (pages != null && pages.getSize() > 0) {
      for (INTELLIGENT p : pages) {
        list.add(new IntelligentRecord(p));
      }
    }
    result.setTotalPage(Utility.getTotalPage(result.getCount(), perPage));
    result.setData(list);

    List<Object[]> groupByList = intelligentDao.countGroupByConditionCode();
    for (Object[] obj : groupByList) {
      if (((Integer) obj[0]).intValue() == INTELLIGENT_REASON.HIGH_RATIO.value()) {
        result.setHighRatio(((BigInteger) obj[1]).intValue());
      } else if (((Integer) obj[0]).intValue() == INTELLIGENT_REASON.HIGH_RISK.value()) {
        result.setHighRisk(((BigInteger) obj[1]).intValue());
      } else if (((Integer) obj[0]).intValue() == INTELLIGENT_REASON.INFECTIOUS.value()) {
        result.setInfectious(((BigInteger) obj[1]).intValue());
      } else if (((Integer) obj[0]).intValue() == INTELLIGENT_REASON.INH_OWN_EXIST.value()) {
        result.setInhOwnExist(((BigInteger) obj[1]).intValue());
      } else if (((Integer) obj[0]).intValue() == INTELLIGENT_REASON.MATERIAL.value()) {
        result.setMaterial(((BigInteger) obj[1]).intValue());
      } else if (((Integer) obj[0]).intValue() == INTELLIGENT_REASON.OVER_AMOUNT.value()) {
        result.setOverAmount(((BigInteger) obj[1]).intValue());
      } else if (((Integer) obj[0]).intValue() == INTELLIGENT_REASON.PILOT_PROJECT.value()) {
        result.setPilotProject(((BigInteger) obj[1]).intValue());
      } else if (((Integer) obj[0]).intValue() == INTELLIGENT_REASON.RARE_ICD.value()) {
        result.setRareIcd(((BigInteger) obj[1]).intValue());
      } else if (((Integer) obj[0]).intValue() == INTELLIGENT_REASON.SAME_ATC.value()) {
        result.setSameAtc(((BigInteger) obj[1]).intValue());
      } else if (((Integer) obj[0]).intValue() == INTELLIGENT_REASON.VIOLATE.value()) {
        result.setViolate(((BigInteger) obj[1]).intValue());
      }
    }
    return result;
  }

  private void addPredicate(Root<INTELLIGENT> root, List<Predicate> predicate, CriteriaBuilder cb,
      String paramName, String params, boolean isAnd, boolean isInteger) {
    if (params == null || params.length() == 0) {
      return;
    }
    if (params.indexOf(' ') < 0) {
      if (isInteger) {
        predicate.add(cb.equal(root.get(paramName), params));
      } else {
        predicate.add(cb.like(root.get(paramName), params + "%"));
      }
    } else {
      String[] ss = params.split(" ");
      List<Predicate> predicates = new ArrayList<Predicate>();
      for (int i = 0; i < ss.length; i++) {
        if (isInteger) {
          predicates.add(cb.equal(root.get(paramName), ss[i]));
        } else {
          predicates.add(cb.like(root.get(paramName), ss[i] + "%"));
        }
      }
      Predicate[] pre = new Predicate[predicates.size()];
      if (isAnd) {
        predicate.add(cb.and(predicates.toArray(pre)));
      } else {
        predicate.add(cb.or(predicates.toArray(pre)));
      }
    }
  }

  /**
   * 計算指定年月的罕見ICD次數是否超過設定值
   * 
   * @param chineseYm
   */
  public void calculateRareICD(String chineseYm) {
    List<CODE_THRESHOLD> list = ctDao.findByCodeTypeOrderByStartDateDesc(1);
    String wording1M = parameterService.getOneValueByName("RARE_ICD_1M");
    String wording6M = parameterService.getOneValueByName("RARE_ICD_6M");
    for (CODE_THRESHOLD ct : list) {
      // if (ct.getCode().equals("0QSQ04Z")) {
      if (ct.getStatus().intValue() == 0) {
        continue;
      }
      System.out.println("Calculate:" + ct.getCode());
      if (ct.getOpTimesMStatus().intValue() == 1 && ct.getOpTimesM() != null) {
        processRareICDByMonth(chineseYm, "10", ct, ct.getOpTimesM().intValue(), wording1M);
      }
      if (ct.getOpTimes6mStatus().intValue() == 1 && ct.getOpTimes6m() != null) {
        processRareICDBy6Month(chineseYm, "10", ct, ct.getOpTimes6m().intValue(), wording6M);
      }
      if (ct.getIpTimesMStatus().intValue() == 1 && ct.getIpTimesM() != null) {
        processRareICDByMonth(chineseYm, "20", ct, ct.getIpTimesM().intValue(), wording1M);
      }
      if (ct.getIpTimes6mStatus().intValue() == 1 && ct.getIpTimes6m() != null) {
        processRareICDBy6Month(chineseYm, "20", ct, ct.getIpTimes6m().intValue(), wording6M);
      }
    }
  }

  private void processRareICDByMonth(String chineseYm, String dataFormat, CODE_THRESHOLD ct,
      int max, String wording) {
    List<MR> list = getMRByCode(dataFormat, chineseYm, ct.getCode(), max, true, false);
    if (list != null) {
      for (MR mr : list) {
        String reason = (wording != null) ? String.format(wording, ct.getCode(), max) : null;
        insertIntelligent(mr, INTELLIGENT_REASON.RARE_ICD.value(), null, ct.getCode(), reason);
      }
    }
  }

  /**
   * 計算罕見ICD出現次數是否超過前6個月平均值 overLimit次.
   * 
   * @param dataFormat
   * @param ct
   * @param overLimit 超過前6個月平均值次數才列入智慧提示
   * @param wording
   */
  private void processRareICDBy6Month(String chineseYm, String dataFormat, CODE_THRESHOLD ct,
      int overLimit, String wording) {
    Calendar cal = DateTool.chineseYmToCalendar(chineseYm);
    System.out.println("processRareICDBy6Month chineseYm=" + chineseYm + "," + cal);
    int avg = get6MAvgByCode(dataFormat, cal, ct.getCode(), true);
    List<MR> list = getMRByCode(dataFormat, String.valueOf(chineseYm), ct.getCode(),
        avg + overLimit, true, false);
    if (list != null) {
      int over = list.size() - avg;
      for (MR mr : list) {
        String reason = (wording != null) ? String.format(wording, ct.getCode(), over) : null;
        insertIntelligent(mr, INTELLIGENT_REASON.RARE_ICD.value(), null, ct.getCode(), reason);
      }
    }
  }

  /**
   * 取得符合ICD或支付標準代碼的病歷
   * 
   * @param dataFormat 10:門診，20:住院
   * @param applYm 申報民國年月
   * @param code 代碼
   * @param max 數值門檻
   * @param isICD true:ICD代碼，false:支付標準代碼/藥品/衛材/醫令
   * @param countEvery true:每一筆病歷的醫令
   * @return
   */
  private List<MR> getMRByCode(String dataFormat, String applYm, String code, int max,
      boolean isICD, boolean countEvery) {
    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 1012L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (dataFormat != null) {
          predicate.add(cb.equal(root.get("dataFormat"), dataFormat));
        }
        if (applYm != null) {
          predicate.add(cb.equal(root.get("applYm"), applYm));
        }
        String parameterName = isICD ? "icdAll" : "codeAll";
        predicate.add(cb.like(root.get(parameterName), "%," + code + ",%"));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    if (countEvery) {
      List<MR> result = new ArrayList<MR>();
      List<MR> list = mrDao.findAll(spec);
      for (MR mr : list) {
        String compare = (isICD) ? mr.getIcdAll() : mr.getCodeAll();
        if (countStringAppear(compare, code) > max) {
          result.add(mr);
        }
      }
      return result;
    } else if (mrDao.count(spec) > max) {
      return mrDao.findAll(spec);
    }
    return null;
  }

  /**
   * 取得符合ICD或支付標準代碼的病歷
   * 
   * @param dataFormat 10:門診，20:住院
   * @param applYm 申報民國年月
   * @param code 代碼
   * @param max 數值門檻
   * @param isICD true:ICD代碼，false:支付標準代碼/藥品/衛材/醫令
   * @param countEvery true:每一筆病歷的醫令
   * @return
   */
  private List<MR> getMRByCode(String dataFormat, String[] applYm, String code, int max,
      boolean isICD, boolean isAsc) {
    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 1012L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (dataFormat != null) {
          predicate.add(cb.equal(root.get("dataFormat"), dataFormat));
        }
        if (applYm != null) {
          Predicate[] pre = new Predicate[applYm.length];
          for (int i=0; i<applYm.length; i++) {
            String ym = applYm[i];
            pre[i] = cb.equal(root.get("applYm"), ym);
          }          
           predicate.add(cb.or(pre));
        }
        String parameterName = isICD ? "icdAll" : "codeAll";
        predicate.add(cb.like(root.get(parameterName), "%," + code + ",%"));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        if (isAsc) {
          query.orderBy(cb.asc(root.get("mrDate")));
        } else {
          query.orderBy(cb.desc(root.get("mrDate")));
        }
        return query.getRestriction();
      }
    };
    return mrDao.findAll(spec);
  }

  public boolean insertIntelligent(MR mr, int conditionCode, String reasonCode, String code,
      String reason) {
    List<INTELLIGENT> list = intelligentDao.findByMrIdAndConditionCode(mr.getId(), conditionCode);
    if (list == null || list.size() == 0) {
      INTELLIGENT ig = new INTELLIGENT();
      ig.setApplDot(mr.getApplDot());
      ig.setDataFormat(mr.getDataFormat());
      ig.setCode(mr.getCodeAll());
      ig.setConditionCode(conditionCode);
      ig.setEndDate(mr.getMrEndDate());
      ig.setFuncType(mr.getFuncType());
      ig.setFuncTypec(codeTableService.getDesc("FUNC_TYPE", mr.getFuncType()));
      ig.setIcd(mr.getIcdAll());
      ig.setInhClinicId(mr.getInhClinicId());
      ig.setInhCode(mr.getInhMrId());
      ig.setMrId(mr.getId());
      ig.setPrsnName(mr.getPrsnName());
      ig.setReason(reason);
      ig.setReasonCode(reasonCode);
      ig.setStartDate(mr.getMrDate());
      mr.setStatus(MR_STATUS.WAIT_CONFIRM.value());
      ig.setStatus(mr.getStatus());
      ig.setUpdateAt(new Date());
      intelligentDao.save(ig);
      return true;
    }
    return false;
  }

  /**
   * 計算指定年月的法定傳染病病歷.
   * 
   * @param applYm
   */
  public void calculateInfectious(String applYm) {
    String wording = parameterService.getOneValueByName("INFECTIOUS");
    List<String> infectious = codeTableService.getInfectious();
    for (String inf : infectious) {
      List<MR> list = getMRByCode(null, applYm, inf, 0, true, false);
      if (list != null) {
        for (MR mr : list) {
          String reason = (wording != null) ? String.format(wording, inf) : null;
          insertIntelligent(mr, INTELLIGENT_REASON.INFECTIOUS.value(), null, inf, reason);
        }
      }
    }
  }

  /**
   * 計算指定年月應用比例偏高的病歷
   * @param chineseYm
   */
  public void calculateHighRatio(String chineseYm) {
    List<CODE_THRESHOLD> list = ctDao.findByCodeTypeOrderByStartDateDesc(2);
    String wordingHighRatioSingle = parameterService.getOneValueByName("HIGH_RATIO_SINGLE");
    String wordingHighRatioTotal = parameterService.getOneValueByName("HIGH_RATIO_TOTAL");
    String wordingHighRatio6M = parameterService.getOneValueByName("HIGH_RATIO_6M");
    String wordingHighRatio1M = parameterService.getOneValueByName("HIGH_RATIO_1M");

    calculateHighRatioAndOverAmount(chineseYm, list, wordingHighRatioSingle, wordingHighRatioTotal,
        wordingHighRatio6M, wordingHighRatio1M, INTELLIGENT_REASON.HIGH_RATIO.value());
  }

  /**
   * 單一病患幾天內申報數超過 max 次
   * 
   * @param dataFormat
   * @param ct
   * @param max
   * @param wording
   */
  private void processPatient(String chineseYm, String dataFormat, CODE_THRESHOLD ct,
      int max, String wording, int conditionCode) {
    Calendar cal = DateTool.chineseYmToCalendar(chineseYm);
    int day = "10".equals(dataFormat) ? ct.getOpTimesDay() : ct.getIpTimesDay();
    cal.set(Calendar.DAY_OF_MONTH, 1);
    
    Calendar firstCal = DateTool.chineseYmToCalendar(chineseYm);
    firstCal.set(Calendar.DAY_OF_MONTH, 1);
    firstCal.add(Calendar.DAY_OF_YEAR, - day);
    int diffMonth = cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH) - (firstCal.get(Calendar.YEAR) * 12 +  firstCal.get(Calendar.MONTH));
    if (diffMonth == 0) {
      diffMonth = 1;
    }
    String[] lastNMonth = getLastNMonth(cal, diffMonth);
    List<MR> list = getMRByCode(dataFormat, lastNMonth, ct.getCode(), 0, false, false);
    HashMap<String, Integer> patientCount = new HashMap<String, Integer>();
    HashMap<String, Long> patientLastTime = new HashMap<String, Long>();
    HashMap<String, MR> patientMR = new HashMap<String, MR>();
    long maxDay = day * 86400000;
    if (list != null) {
      for (MR mr : list) {
        Integer count = patientCount.get(mr.getRocId());
        String compare = mr.getCodeAll();
        int appearCount = countStringAppear(compare, ct.getCode());
        if (count == null) {
          patientCount.put(mr.getRocId(), new Integer(appearCount));
          patientLastTime.put(mr.getRocId(),mr.getMrDate().getTime());
          patientMR.put(mr.getRocId(), mr);
        } else {
          Long lastTime = patientLastTime.get(mr.getRocId());
          if (lastTime.longValue() - mr.getMrDate().getTime() <  maxDay) {
            int total = count.intValue() + appearCount;
            patientCount.put(mr.getRocId(), new Integer(total));
            if (total > max) {
              String reason = (wording != null) ? String.format(wording, ct.getCode(), max) : null;
              System.out.println(mr.getId() + ":" + reason);
              insertIntelligent(mr, conditionCode, null, ct.getCode(), reason);  
            }
          }
        }
      }
    }
  }
  
  /**
   * 單一就診紀錄使用數量超過 max 次
   * 
   * @param dataFormat
   * @param ct
   * @param max
   * @param wording
   */
  private void processHighRatioSingle(String chineseYm, String dataFormat, CODE_THRESHOLD ct,
      int max, String wording, int conditionCode) {
    List<MR> list = getMRByCode(dataFormat, chineseYm, ct.getCode(), max, false, true);
    if (list != null) {
      for (MR mr : list) {
        String reason = (wording != null) ? String.format(wording, ct.getCode(), max) : null;
        System.out.println(mr.getId() + ":" + reason);
        insertIntelligent(mr, conditionCode, null, ct.getCode(), reason);
      }
    }
  }

  /**
   * 單月申報總數量是否高於上限
   * 
   * @param dataFormat 10:門急診，20:住院
   * @param ct
   * @param max 上限
   * @param wording 提示訊息
   */
  private void processHighRatio1M(String chineseYm, String dataFormat, CODE_THRESHOLD ct, int max,
      String wording, int conditionCode) {
    int count = 0;
    if (XMLConstant.DATA_FORMAT_IP.equals(dataFormat)) {
      count = ippDao.countOrderByDrugNoAndApplYm(chineseYm, ct.getCode()).intValue();
    } else {
      count = oppDao.countOrderByDrugNoAndApplYm(chineseYm, ct.getCode()).intValue();
    }
    if (count > max) {
      List<MR> list = getMRByCode(dataFormat, chineseYm, ct.getCode(), 0, false, false);
      if (list != null) {
        for (MR mr : list) {
          String reason = (wording != null) ? String.format(wording, ct.getCode(), max) : null;
          System.out.println(mr.getId() + ":" + reason);
          insertIntelligent(mr, conditionCode, null, ct.getCode(), reason);
        }
      }
    }
  }

  /**
   * 單月總量高於前六個月平均用量值後提示
   * 
   * @param dataFormat 10:門急診，20:住院
   * @param ct
   * @param max 上限
   * @param wording 提示訊息
   */
  private void processHighRatio6M(String chineseYm, String dataFormat, CODE_THRESHOLD ct, int max, String wording,
      int conditionCode) {
      Calendar cal = DateTool.chineseYmToCalendar(chineseYm);
      String[] m6 = getLast6M(cal);
      int count6M = 0;
      int count1M = 0;
      if (XMLConstant.DATA_FORMAT_IP.equals(dataFormat)) {
        count6M = ippDao
            .countOrderByDrugNoAnd6ApplYm(m6[0], m6[1], m6[2], m6[3], m6[4], m6[5], ct.getCode())
            .intValue();
        count1M =
            ippDao.countOrderByDrugNoAndApplYm(chineseYm, ct.getCode()).intValue();
      } else {
        count6M = oppDao
            .countOrderByDrugNoAnd6ApplYm(m6[0], m6[1], m6[2], m6[3], m6[4], m6[5], ct.getCode())
            .intValue();
        count1M =
            oppDao.countOrderByDrugNoAndApplYm(chineseYm, ct.getCode()).intValue();
      }
      int avg6m = (int) (count6M / 6);

      if (count1M > (avg6m + max)) {
        List<MR> list =
            getMRByCode(dataFormat, chineseYm, ct.getCode(), 0, false, false);
        if (list != null) {
          for (MR mr : list) {
            String reason = (wording != null) ? String.format(wording, ct.getCode(), max) : null;
            System.out.println(mr.getId() + ":" + reason);
            insertIntelligent(mr, conditionCode, null, ct.getCode(), reason);
          }
        }
      }
  }


  /**
   * 取得code在就醫月份前六個月出現的平均值
   * 
   * @param dataFormat
   * @param cal
   * @param code
   * @param max
   * @param isICD
   * @return
   */
  private int get6MAvgByCode(String dataFormat, Calendar cal, String code, boolean isICD) {
    String[] m6 = getLast6M(cal);
    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 1013L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (dataFormat != null) {
          predicate.add(cb.equal(root.get("dataFormat"), dataFormat));
        }
        predicate
            .add(cb.or(cb.equal(root.get("applYm"), m6[0]), cb.equal(root.get("applYm"), m6[1]),
                cb.equal(root.get("applYm"), m6[2]), cb.equal(root.get("applYm"), m6[3]),
                cb.equal(root.get("applYm"), m6[4]), cb.equal(root.get("applYm"), m6[5])));

        String parameterName = isICD ? "icdAll" : "codeAll";
        predicate.add(cb.like(root.get(parameterName), "%," + code + ",%"));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    int total = (int) mrDao.count(spec);
    return (int) (total / 6);
  }

  public void testCount() {
    long count = oppDao.countOrderByDrugNoAndApplYm("11011", "09099C");
    System.out.println("count=" + count);
  }

  /**
   * 取得字串 ,b, 在 a字串中出現的次數
   * 
   * @param a
   * @param b
   * @return
   */
  public static int countStringAppear(String a, String b) {
    int result = 0;
    String c = "," + b + ",";
    String a1 = a;
    int index = 0;
    do {
      index = a1.indexOf(c);
      if (index > -1) {
        result++;
        a1 = a1.substring(index + b.length());
      }
    } while (index > -1);
    return result;
  }

  /**
   * 計算應用比例偏高及醫令
   * 
   * @param list
   * @param wordingSingle
   * @param wordingTotal
   * @param wording6M
   * @param wording1M
   * @param conditonCode
   */
  public void calculateHighRatioAndOverAmount(String chineseYm, List<CODE_THRESHOLD> list, String wordingSingle,
      String wordingTotal, String wording6M, String wording1M, int conditonCode) {
    for (CODE_THRESHOLD ct : list) {
      // if (ct.getCode().equals("0QSQ04Z")) {
      if (ct.getStatus().intValue() == 0) {
        continue;
      }
      if (ct.getOpTimesStatus().intValue() == 1) {
        // 單一就診紀錄使用數量超過 n 次
        processHighRatioSingle(chineseYm, "10", ct, ct.getOpTimes().intValue(), wordingSingle, conditonCode);
      }
      if (ct.getIpTimesStatus().intValue() == 1) {
        processHighRatioSingle(chineseYm, "20", ct, ct.getIpTimes().intValue(), wordingSingle, conditonCode);
      }
      if (ct.getOpTimesMStatus().intValue() == 1) {
        // 單月申報總數量是否高於上限
        processHighRatio1M(chineseYm, XMLConstant.DATA_FORMAT_OP, ct, ct.getOpTimesM().intValue(), wording1M,
            conditonCode);
      }
      if (ct.getIpTimesMStatus().intValue() == 1) {
        // 單月申報總數量是否高於上限
        processHighRatio1M(chineseYm, XMLConstant.DATA_FORMAT_IP, ct, ct.getIpTimesM().intValue(), wording1M,
            conditonCode);
      }
      if (ct.getIpTimes6mStatus().intValue() == 1) {
        processHighRatio6M(chineseYm, XMLConstant.DATA_FORMAT_IP, ct, ct.getIpTimes6m().intValue(), wording6M,
            conditonCode);
      }
      if (ct.getOpTimes6mStatus().intValue() == 1) {
        processHighRatio6M(chineseYm, XMLConstant.DATA_FORMAT_OP, ct, ct.getIpTimes6m().intValue(), wording6M,
            conditonCode);
      }
      if (ct.getOpTimesDStatus().intValue() == 1) {
        processPatient(chineseYm, "10", ct, ct.getOpTimesD(), wordingTotal, conditonCode);
      }
      if (ct.getIpTimesDStatus().intValue() == 1) {
        processPatient(chineseYm, "20", ct, ct.getIpTimesD(), wordingTotal, conditonCode);
      }
    }
  }

  /**
   * 計算 特別用量藥品、衛材
   */
  public void calculateOverAmount(String chineseYm) {
    List<CODE_THRESHOLD> list = ctDao.findByCodeTypeOrderByStartDateDesc(3);
    String wordingOverAmountSingle = parameterService.getOneValueByName("OVER_AMOUNT_SINGLE");
    String wordingOverAmountTotal = parameterService.getOneValueByName("OVER_AMOUNT_TOTAL");
    String wordingOverAmount6M = parameterService.getOneValueByName("OVER_AMOUNT_6M");
    String wordingOverAmount1M = parameterService.getOneValueByName("OVER_AMOUNT_1M");
    calculateHighRatioAndOverAmount(chineseYm, list, wordingOverAmountSingle, wordingOverAmountTotal,
        wordingOverAmount6M, wordingOverAmount1M, INTELLIGENT_REASON.OVER_AMOUNT.value());
  }

  /**
   * 取得 cal 前六個月的民國年月字串
   * 
   * @param cal
   * @return
   */
  public static String[] getLast6M(Calendar cal) {
    Calendar temp = Calendar.getInstance();
    temp.setTime(cal.getTime());
    String[] result = new String[6];
    for (int i = 0; i < 6; i++) {
      temp.add(Calendar.MONTH, -1);
      result[i] = String.valueOf(DateTool.getChineseYm(temp));
    }
    return result;
  }
  
  /**
   * 取得 cal 前n個月的民國年月字串，含當月
   * 
   * @param cal
   * @return
   */
  public static String[] getLastNMonth(Calendar cal, int n) {
    Calendar temp = Calendar.getInstance();
    temp.setTime(cal.getTime());
    String[] result = new String[n+1];
    result[0] = String.valueOf(DateTool.getChineseYm(temp));
    for (int i = 0; i < n; i++) {
      temp.add(Calendar.MONTH, -1);
      result[i+1] = String.valueOf(DateTool.getChineseYm(temp));
    }
    return result;
  }
  
  public List<String> getMRHint(Long mrId) {
    List<String> result = new ArrayList<String>();
    List<INTELLIGENT> list =  intelligentDao.findByMrId(mrId);
    for (INTELLIGENT intelligent : list) {
      result.add(intelligent.getReason());
    }
    return result;
  }
}
