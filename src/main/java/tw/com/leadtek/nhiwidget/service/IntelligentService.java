/**
 * Created on 2021/11/19.
 */
package tw.com.leadtek.nhiwidget.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
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
import tw.com.leadtek.nhiwidget.dao.AIDao;
import tw.com.leadtek.nhiwidget.dao.CODE_CONFLICTDao;
import tw.com.leadtek.nhiwidget.dao.CODE_THRESHOLDDao;
import tw.com.leadtek.nhiwidget.dao.ICDCM_DRUG_ATCDao;
import tw.com.leadtek.nhiwidget.dao.INTELLIGENTDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.PAY_CODEDao;
import tw.com.leadtek.nhiwidget.dto.PaymentTermsPl;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_CONFLICT;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_THRESHOLD;
import tw.com.leadtek.nhiwidget.model.rdb.ICDCM_DRUG_ATC;
import tw.com.leadtek.nhiwidget.model.rdb.INTELLIGENT;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
import tw.com.leadtek.nhiwidget.payload.intelligent.IntelligentRecord;
import tw.com.leadtek.nhiwidget.payload.intelligent.IntelligentResponse;
import tw.com.leadtek.nhiwidget.payload.intelligent.PilotProject;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.service.pt.PaymentTermsService;
import tw.com.leadtek.nhiwidget.service.pt.ViolatePaymentTermsService;
import tw.com.leadtek.nhiwidget.sql.PaymentTermsDao;
import tw.com.leadtek.tools.DateTool;
import tw.com.leadtek.tools.Utility;

@Service
public class IntelligentService {

  private Logger logger = LogManager.getLogger();

  public final static String MENU_CLINCAL = "/intelligent?menu=clincal";

  public final static String MENU_SUSPECTED = "/intelligent?menu=suspected";

  public final static String MENU_DRG_SUGGESTION = "/intelligent?menu=drgSuggestion";

  private final static float DRUG_DIFF_PERCENT = 0.1f;

  @Autowired
  private INTELLIGENTDao intelligentDao;

  @Autowired
  private CODE_THRESHOLDDao ctDao;

  @Autowired
  private MRDao mrDao;

  @Autowired
  private ParametersService parametersService;

  @Autowired
  private CodeTableService codeTableService;

  @Autowired
  private OP_DDao opdDao;

  @Autowired
  private OP_PDao oppDao;

  @Autowired
  private IP_PDao ippDao;

  @Autowired
  private CODE_CONFLICTDao codeConflictDao;

  @Autowired
  private AIDao aiDao;

  @Autowired
  private ICDCM_DRUG_ATCDao idaDao;

  @Autowired
  private PAY_CODEDao payCodeDao;

  @Autowired
  private PlanConditionService planConditionService;

  @Autowired
  private ViolatePaymentTermsService vpts;

  // 存放目前是否有智能提示助理正在處理，及開始跑的時間，避免同時跑造成server loading過高
  private static HashMap<Integer, Long> runningIntelligent = new HashMap<Integer, Long>();

  @Autowired
  private EntityManager em;

  @Autowired
  private PaymentTermsDao paymentTermsDao;

  @Autowired
  private PaymentTermsService paymentTermsService;

  @Autowired
  private ViolatePaymentTermsService violatePaymentTermsService;
  
  @Autowired
  private ReportService reportService;

  public IntelligentResponse getIntelligent(UserDetailsImpl user, String menu, java.sql.Date sDate,
      java.sql.Date eDate, Integer minPoints, Integer maxPoints, String funcType, String funcTypec,
      String prsnId, String prsnName, String code, String inhCode, String icd, Integer reason,
      String applYm, String dataFormat, String orderBy, Boolean asc, int perPage, int page) {
    IntelligentResponse result = new IntelligentResponse();

    Specification<INTELLIGENT> spec = new Specification<INTELLIGENT>() {

      private static final long serialVersionUID = 1011L;

      public Predicate toPredicate(Root<INTELLIGENT> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {

        List<Predicate> predicate = getIntelligentPredicate(cb, root, query, menu, sDate, eDate,
            minPoints, maxPoints, funcType, funcTypec, prsnId, prsnName, code, inhCode, icd, reason,
            applYm, dataFormat);
        if (reason != null) {
          addPredicate(root, predicate, cb, "conditionCode", reason.toString(), true, true, false);
        }
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

    List<Tuple> tuples = groupByConditionCode(menu, sDate, eDate, minPoints, maxPoints, funcType,
        funcTypec, prsnId, prsnName, code, inhCode, icd, reason, applYm, dataFormat);
    for (Tuple tuple : tuples) {
      int value = (tuple.get(1) instanceof Long) ? ((Long) tuple.get(1)).intValue()
          : ((Integer) tuple.get(1)).intValue();
      if (((Integer) tuple.get(0)).intValue() == INTELLIGENT_REASON.VIOLATE.value()) {
        result.setViolate(value);
      } else if (((Integer) tuple.get(0)).intValue() == INTELLIGENT_REASON.HIGH_RATIO.value()) {
        result.setHighRatio(value);
      } else if (((Integer) tuple.get(0)).intValue() == INTELLIGENT_REASON.HIGH_RISK.value()) {
        result.setHighRisk(value);
      } else if (((Integer) tuple.get(0)).intValue() == INTELLIGENT_REASON.INFECTIOUS.value()) {
        result.setInfectious(value);
      } else if (((Integer) tuple.get(0)).intValue() == INTELLIGENT_REASON.INH_OWN_EXIST.value()) {
        result.setInhOwnExist(value);
      } else if (((Integer) tuple.get(0)).intValue() == INTELLIGENT_REASON.MATERIAL.value()) {
        result.setMaterial(value);
      } else if (((Integer) tuple.get(0)).intValue() == INTELLIGENT_REASON.OVER_AMOUNT.value()) {
        result.setOverAmount(value);
      } else if (((Integer) tuple.get(0)).intValue() == INTELLIGENT_REASON.PILOT_PROJECT.value()) {
        result.setPilotProject(value);
      } else if (((Integer) tuple.get(0)).intValue() == INTELLIGENT_REASON.RARE_ICD.value()) {
        result.setRareIcd(value);
      } else if (((Integer) tuple.get(0)).intValue() == INTELLIGENT_REASON.SAME_ATC.value()) {
        result.setSameAtc(value);
      } else if (((Integer) tuple.get(0)).intValue() == INTELLIGENT_REASON.COST_DIFF.value()) {
        result.setCostDiff(value);
      } else if (((Integer) tuple.get(0)).intValue() == INTELLIGENT_REASON.ORDER_DIFF.value()) {
        result.setOrderDiff(value);
      } else if (((Integer) tuple.get(0)).intValue() == INTELLIGENT_REASON.ORDER_DRUG.value()) {
        result.setOrderDrug(value);
      } else if (((Integer) tuple.get(0)).intValue() == INTELLIGENT_REASON.IP_DAYS.value()) {
        result.setIpDays(value);
      }
    }
    return result;
  }

  private List<Predicate> getIntelligentPredicate(CriteriaBuilder cb, Root<INTELLIGENT> root,
      CriteriaQuery<?> query, String menu, java.sql.Date sDate, java.sql.Date eDate,
      Integer minPoints, Integer maxPoints, String funcType, String funcTypec, String prsnId,
      String prsnName, String code, String inhCode, String icd, Integer reason, String applYm,
      String dataFormat) {
    List<Predicate> result = new ArrayList<Predicate>();
    if (sDate != null && eDate != null) {
      addSearchDateParameter(result, cb, root, sDate, eDate);
    }
    if (minPoints != null) {
      result.add(cb.greaterThanOrEqualTo(root.get("applDot"), minPoints));
    }
    if (maxPoints != null) {
      result.add(cb.lessThanOrEqualTo(root.get("applDot"), maxPoints));
    }

    addPredicate(root, result, cb, "prsnId", prsnId, true, false, false);
    addPredicate(root, result, cb, "funcType", funcType, true, false, false);
    addPredicate(root, result, cb, "funcTypec", funcTypec, true, false, false);
    addPredicate(root, result, cb, "prsnName", prsnName, true, false, false);
    addPredicate(root, result, cb, "code", code, true, false, true);
    addPredicate(root, result, cb, "inhCode", inhCode, true, false, true);
    addPredicate(root, result, cb, "icd", icd, true, false, true);
    addPredicate(root, result, cb, "applYm", applYm, true, false, false);
    if (dataFormat != null && dataFormat.indexOf(XMLConstant.DATA_FORMAT_IP) > -1
        && dataFormat.indexOf(XMLConstant.DATA_FORMAT_OP) > -1) {
      dataFormat = null;
    }
    addPredicate(root, result, cb, "dataFormat", dataFormat, true, false, false);
    // baseRule(固定條件判斷)，clincal(臨床路徑差異)，suspected(疑似職傷與異常就診記錄判斷)，drgSuggestion(DRG申報建議)
    if ("baseRule".equals(menu)) {
      result.add(cb.between(root.get("conditionCode"), INTELLIGENT_REASON.VIOLATE.value(),
          INTELLIGENT_REASON.HIGH_RISK.value()));
    } else if ("clincal".equals(menu)) {
      if ("0".equals(parametersService.getParameter(MENU_CLINCAL))) {
        result.add(cb.equal(root.get("conditionCode"), Integer.MAX_VALUE));
      } else {
        result.add(cb.between(root.get("conditionCode"), INTELLIGENT_REASON.COST_DIFF.value(),
            INTELLIGENT_REASON.IP_DAYS.value()));
      }
    } else {
      result.add(cb.equal(root.get("conditionCode"), Integer.MAX_VALUE));
    }
    result.add(cb.equal(root.get("status"), MR_STATUS.WAIT_CONFIRM.value()));
    return result;
  }

  private void addSearchDateParameter(List<Predicate> predicate, CriteriaBuilder cb,
      Root<INTELLIGENT> root, Date sdate, Date edate) {
    predicate.add(cb.or(
        cb.and(cb.equal(root.get("dataFormat"), XMLConstant.DATA_FORMAT_IP),
            cb.or(
                cb.and(cb.greaterThanOrEqualTo(root.get("startDate"), sdate),
                    cb.lessThanOrEqualTo(root.get("startDate"), edate)),
                cb.and(cb.greaterThanOrEqualTo(root.get("endDate"), sdate),
                    cb.lessThanOrEqualTo(root.get("endDate"), edate)))),
        cb.and(cb.equal(root.get("dataFormat"), XMLConstant.DATA_FORMAT_OP),
            cb.greaterThanOrEqualTo(root.get("endDate"), sdate),
            cb.lessThanOrEqualTo(root.get("endDate"), edate))));
  }

  private void addPredicate(Root<?> root, List<Predicate> predicate, CriteriaBuilder cb,
      String paramName, String params, boolean isAnd, boolean isInteger, boolean prefixPercent) {
    if (params == null || params.length() == 0) {
      return;
    }
    if (params.indexOf(' ') < 0) {
      if (isInteger) {
        predicate.add(cb.equal(root.get(paramName), params));
      } else {
        if (prefixPercent) {
          predicate.add(cb.like(root.get(paramName), "%," + params + "%"));
        } else {
          predicate.add(cb.like(root.get(paramName), params + "%"));
        }
      }
    } else {
      String[] ss = params.split(" ");
      List<Predicate> predicates = new ArrayList<Predicate>();
      for (int i = 0; i < ss.length; i++) {
        if (isInteger) {
          predicates.add(cb.equal(root.get(paramName), ss[i]));
        } else {
          if (prefixPercent) {
            predicates.add(cb.like(root.get(paramName), "%," + ss[i] + "%"));
          } else {
            predicates.add(cb.like(root.get(paramName), ss[i] + "%"));
          }
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

  public List<Tuple> groupByConditionCode(String menu, java.sql.Date sDate, java.sql.Date eDate,
      Integer minPoints, Integer maxPoints, String funcType, String funcTypec, String prsnId,
      String prsnName, String code, String inhCode, String icd, Integer reason, String applYm,
      String dataFormat) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Tuple> query = cb.createTupleQuery();
    Root<INTELLIGENT> root = query.from(INTELLIGENT.class);

    query.select(cb.tuple(root.get("conditionCode"), cb.count(root)));
    List<Predicate> predicate =
        getIntelligentPredicate(cb, root, query, menu, sDate, eDate, minPoints, maxPoints, funcType,
            funcTypec, prsnId, prsnName, code, inhCode, icd, reason, applYm, dataFormat);

    Predicate[] pre = new Predicate[predicate.size()];
    query.where(predicate.toArray(pre)).groupBy(root.get("conditionCode"));
    TypedQuery<Tuple> typedQuery = em.createQuery(query);
    return typedQuery.getResultList();
  }

  /**
   * 計算指定年月的罕見ICD次數是否超過設定值
   * 
   * @param chineseYm 民國年月
   */
  public void calculateRareICD(String chineseYm) {
    List<CODE_THRESHOLD> list = ctDao.findByCodeTypeOrderByStartDateDesc(1);
    String wording1M = parametersService.getOneValueByName("INTELLIGENT", "RARE_ICD_1M");
    String wording6M = parametersService.getOneValueByName("INTELLIGENT", "RARE_ICD_6M");
    for (CODE_THRESHOLD ct : list) {
      if (ct.getStatus().intValue() == 0) {
        continue;
      }
      calculateRareICD(chineseYm, ct, wording1M, wording6M);
    }
  }

  public void calculateRareICD(String chineseYm, CODE_THRESHOLD ct, String wording1M,
      String wording6M) {
    if (XMLConstant.FUNC_TYPE_ALL.equals(ct.getDataFormat())
        || XMLConstant.DATA_FORMAT_IP.equals(ct.getDataFormat())
        || CODE_THRESHOLD.DATA_FORMAT_OP_IP_OWNS.equals(ct.getDataFormat())) {
      if (ct.getIpTimesMStatus().intValue() == 1 && ct.getIpTimesM() != null) {
        processRareICDByMonth(chineseYm, XMLConstant.DATA_FORMAT_IP, ct,
            ct.getIpTimesM().intValue(), wording1M);
      }
      if (ct.getIpTimes6mStatus().intValue() == 1 && ct.getIpTimes6m() != null) {
        processRareICDBy6Month(chineseYm, XMLConstant.DATA_FORMAT_IP, ct,
            ct.getIpTimes6m().intValue(), wording6M);
      }
    }
    if (XMLConstant.FUNC_TYPE_ALL.equals(ct.getDataFormat())
        || XMLConstant.DATA_FORMAT_OP.equals(ct.getDataFormat())
        || CODE_THRESHOLD.DATA_FORMAT_OP_IP_OWNS.equals(ct.getDataFormat())) {
      if (ct.getOpTimesMStatus().intValue() == 1 && ct.getOpTimesM() != null) {
        processRareICDByMonth(chineseYm, XMLConstant.DATA_FORMAT_OP, ct,
            ct.getOpTimesM().intValue(), wording1M);
      }
      if (ct.getOpTimes6mStatus().intValue() == 1 && ct.getOpTimes6m() != null) {
        processRareICDBy6Month(chineseYm, XMLConstant.DATA_FORMAT_OP, ct,
            ct.getOpTimes6m().intValue(), wording6M);
      }
    }
  }

  private void processRareICDByMonth(String chineseYm, String dataFormat, CODE_THRESHOLD ct,
      int max, String wording) {
    List<MR> list = getMRByCode(dataFormat, chineseYm, "icdAll", ct.getCode(), max, false,
        ct.getStartDate(), ct.getEndDate());
    if (list != null) {
      String reason = (wording != null) ? String.format(wording, ct.getCode(), max) : null;
      for (MR mr : list) {
        insertIntelligent(mr, INTELLIGENT_REASON.RARE_ICD.value(), ct.getCode(), reason,
            ct.getStatus() == 1, null);
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
      int max, String wording) {
    Calendar cal = DateTool.chineseYmToCalendar(chineseYm);
    int avg = get6MAvgByCode(dataFormat, cal, ct.getCode(), true);
    List<MR> list = getMRByCode(dataFormat, String.valueOf(chineseYm), "icdAll", ct.getCode(),
        avg + max, false, ct.getStartDate(), ct.getEndDate());
    if (list != null) {
      int over = list.size() - avg;
      for (MR mr : list) {
        String reason = (wording != null) ? String.format(wording, ct.getCode(), max) : null;
        insertIntelligent(mr, INTELLIGENT_REASON.RARE_ICD.value(), ct.getCode(), reason,
            ct.getStatus().intValue() == 1, null);
      }
    }
  }

  /**
   * 取得符合ICD或支付標準代碼的病歷
   * 
   * @param dataFormat 10:門診，20:住院
   * @param applYm 申報民國年月
   * @param fieldName MR class 欄位名稱，有 icdAll, codeAll, inhCode
   * @param code 代碼
   * @param max 數值門檻
   * @param countEvery true:每一筆病歷的醫令
   * @return
   */
  private List<MR> getMRByCode(String dataFormat, String applYm, String fieldName, String code,
      int max, boolean countEvery, Date startDate, Date endDate) {

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
        if (startDate != null && endDate != null) {
          predicate.add(cb.between(root.get("mrEndDate"), startDate, endDate));
        }
        predicate.add(cb.like(root.get(fieldName), "%," + code + ",%"));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    if (countEvery) {
      List<MR> result = new ArrayList<MR>();
      List<MR> list = mrDao.findAll(spec);
      List<MR> useCodeMR = new ArrayList<MR>();
      List<Long> mrIdList = new ArrayList<Long>();
      for (MR mr : list) {
        if ("icdAll".equals(fieldName)) {
          // compare = mr.getIcdAll();
          result.add(mr);
        } else {
          if (max == 0) {
            // if (countStringAppear(compare, code) > max) {
            result.add(mr);
          } else {
            // 查該筆病歷的指定醫令用量
            useCodeMR.add(mr);
            mrIdList.add(mr.getId());
          }
        }
      }
      if (useCodeMR.size() > 0) {
        return getOrderCodeCountOver(list, mrIdList, fieldName, code, max);
      }
      return result;
    } else if (mrDao.count(spec) > max) {
      return mrDao.findAll(spec);
    }
    return null;
  }

  private List<MR> getOrderCodeCountOver(List<MR> mrList, List<Long> mrIdList, String fieldName,
      String code, int max) {
    List<MR> result = new ArrayList<MR>();
    List<Long> matchedMrIdList = new ArrayList<Long>();
    // "inhCode" : "codeAll";
    if (fieldName.equals("codeAll")) {
      matchedMrIdList = opdDao.getMrIdByOrderCodeCount(code, max, mrIdList);
    } else if (fieldName.equals("inhCode")) {
      matchedMrIdList = opdDao.getMrIdByInhCodeCount(code, max, mrIdList);
    }
    for (MR mr : mrList) {
      int index = -1;
      for (int i = matchedMrIdList.size() - 1; i >= 0; i--) {
        Long matchId = matchedMrIdList.get(i);
        if (mr.getId().longValue() == matchId.longValue()) {
          result.add(mr);
          index = i;
          break;
        }
      }
      if (index > -1) {
        matchedMrIdList.remove(index);
      }
    }
    return result;
  }

  /**
   * 取得符合ICD或支付標準代碼的病歷
   * 
   * @param dataFormat 10:門診，20:住院
   * @param applYm 申報民國年月
   * @param fieldName MR class 欄位名稱，有 icdAll,codeAll,inhCode
   * @param code 代碼
   * @param max 數值門檻
   * @param isICD true:ICD代碼，false:支付標準代碼/藥品/衛材/醫令
   * @param countEvery true:每一筆病歷的醫令
   * @return
   */
  private List<MR> getMRByCode(String dataFormat, String[] applYm, String fieldName, String code,
      int max, boolean isAsc) {
    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 1014L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (dataFormat != null) {
          predicate.add(cb.equal(root.get("dataFormat"), dataFormat));
        }
        if (applYm != null) {
          Predicate[] pre = new Predicate[applYm.length];
          for (int i = 0; i < applYm.length; i++) {
            String ym = applYm[i];
            pre[i] = cb.equal(root.get("applYm"), ym);
          }
          predicate.add(cb.or(pre));
        }
        predicate.add(cb.like(root.get(fieldName), "%," + code + ",%"));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        if (isAsc) {
          query.orderBy(cb.asc(root.get("mrEndDate")));
        } else {
          query.orderBy(cb.desc(root.get("mrEndDate")));
        }
        return query.getRestriction();
      }
    };
    return mrDao.findAll(spec);
  }

  /**
   * 取得健保項目對應自費項目並存設定的病歷
   * 
   * @param dataFormat 10:門診，20:住院
   * @param applYm 申報民國年月
   * @param code1 代碼1
   * @param max1 代碼1的數值門檻
   * @param code2 代碼2
   * @param max2 代碼2的數值門檻
   * @return
   */
  private List<MR> getMRBy2PayCode(String dataFormat, String applYm, String code1, int max1,
      String code2, int max2) {
    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 1015L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (dataFormat != null) {
          predicate.add(cb.equal(root.get("dataFormat"), dataFormat));
        }
        if (applYm != null) {
          predicate.add(cb.equal(root.get("applYm"), applYm));
        }
        predicate.add(cb.like(root.get("codeAll"), "%," + code1 + ",%"));
        predicate.add(cb.like(root.get("inhCode"), "%," + code2 + ",%"));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    List<MR> result = new ArrayList<MR>();
    List<MR> list = mrDao.findAll(spec);
    for (MR mr : list) {
      if (countStringAppear(mr.getCodeAll(), code1) >= max1
          && countStringAppear(mr.getCodeAll(), code2) >= max2) {
        result.add(mr);
      }
    }
    return result;
  }

  /**
   * 取得出現同性質藥物的病歷
   * 
   * @param dataFormat 10:門診，20:住院
   * @param applYm 申報民國年月
   * @param payCode 同性質藥健保碼
   * @return
   */
  private List<MR> getMRByPayCodes(String applYm, List<String> payCode) {
    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 1016L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (applYm != null) {
          predicate.add(cb.equal(root.get("applYm"), applYm));
        }
        List<Predicate> predicatePayCode = new ArrayList<Predicate>();
        for (String string : payCode) {
          predicatePayCode.add(cb.like(root.get("codeAll"), "%," + string + ",%"));
        }
        predicate.add(cb.or(predicatePayCode.toArray(new Predicate[predicate.size()])));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    List<MR> list = mrDao.findAll(spec);
    List<MR> result = new ArrayList<MR>();
    for (MR mr : list) {
      int count = 0;
      for (String str : payCode) {
        if (mr.getCodeAll().indexOf("," + str + ",") > -1) {
          count++;
        }
        if (count > 1) {
          result.add(mr);
          break;
        }
      }
    }
    return result;
  }

  /**
   * 取得高風險診斷碼與健保碼設定的病歷
   * 
   * @param dataFormat 10:門診，20:住院
   * @param applYm 申報民國年月
   * @param icd ICD診斷碼
   * @param code 醫令代碼/健保碼
   * @return
   */
  private List<MR> getMRByICDAndPayCode(String dataFormat, String applYm, String icd, String code) {
    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 1015L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (dataFormat != null) {
          predicate.add(cb.equal(root.get("dataFormat"), dataFormat));
        }
        if (applYm != null) {
          predicate.add(cb.equal(root.get("applYm"), applYm));
        }
        predicate.add(cb.like(root.get("codeAll"), "%," + code + ",%"));
        predicate.add(cb.equal(root.get("icdcm1"), icd));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    return mrDao.findAll(spec);
  }

  /**
   * 寫入/刪除智能提示案件
   * 
   * @param mr MR table 的病歷
   * @param conditionCode 智能提示代碼
   * @param reasonCode ICD診斷碼/醫令
   * @param reason 提示文字
   * @param enable true:新增，false:移除
   * @return
   */
  public boolean insertIntelligent(MR mr, int conditionCode, String reasonCode, String reason,
      boolean enable, List<INTELLIGENT> batch) {
    List<INTELLIGENT> list = null;
    if (conditionCode == INTELLIGENT_REASON.PILOT_PROJECT.value()) {
      list = intelligentDao.findByRocIdAndConditionCodeAndReasonCode(mr.getRocId(),
          INTELLIGENT_REASON.PILOT_PROJECT.value(), reasonCode);
    } else {
      list = intelligentDao.findByMrIdAndConditionCodeAndReasonCode(mr.getId(), conditionCode,
          reasonCode);
    }

    if (enable && (list == null || list.size() == 0)) {
      INTELLIGENT ig = new INTELLIGENT();
      ig.setApplDot(mr.getTotalDot());
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
      ig.setPrsnId(mr.getPrsnId());
      ig.setReason(reason);
      ig.setReasonCode(reasonCode);
      ig.setStartDate(mr.getMrDate());
      mr.setStatus(MR_STATUS.WAIT_CONFIRM.value());
      mrDao.updateMrStauts(MR_STATUS.WAIT_CONFIRM.value(), mr.getId());
      ig.setStatus(mr.getStatus());
      ig.setRocId(mr.getRocId());
      ig.setApplYm(mr.getApplYm());
      ig.setUpdateAt(new Date());
      if (batch != null) {
        batch.add(ig);
        if (batch.size() % XMLConstant.BATCH == 0) {
          saveIntelligentBatch(batch);
        }
      } else {
        intelligentDao.save(ig);
      }
      return true;
    } else if (list != null) {
      for (INTELLIGENT intelligent : list) {
        if (enable) {
          if (intelligent.getReason() != null && !intelligent.getReason().equals(reason)) {
            mr.setStatus(MR_STATUS.WAIT_CONFIRM.value());
            mrDao.updateMrStauts(MR_STATUS.WAIT_CONFIRM.value(), mr.getId());
            intelligent.setReason(reason);
            intelligent.setReasonCode(reasonCode);
            intelligent.setUpdateAt(new Date());
            if (batch != null) {
              batch.add(intelligent);
              if (batch.size() % XMLConstant.BATCH == 0) {
                saveIntelligentBatch(batch);
              }
            } else {
              intelligentDao.save(intelligent);
            }
          }
        } else {
          if (intelligent.getReason() == null || (intelligent.getReason().equals(reason)
              || reasonCode.equals(intelligent.getReasonCode()))) {
            if (mr.getStatus().intValue() == MR_STATUS.WAIT_CONFIRM.value()) {
              mr.setStatus(MR_STATUS.NO_CHANGE.value());
              mrDao.updateMrStauts(MR_STATUS.NO_CHANGE.value(), mr.getId());
            }
            intelligentDao.deleteById(intelligent.getId());
          }
        }
      }
    }
    return false;
  }

  /**
   * 寫入/刪除智能提示案件
   * 
   * @param mr MR table 的病歷
   * @param conditionCode 智能提示代碼
   * @param reasonCode ICD診斷碼/醫令
   * @param reason 提示文字
   * @param enable true:新增，false:移除
   * @return
   */
  public boolean insertIntelligentNoUpdateMrStatus(MR mr, int conditionCode, String reasonCode,
      String reason, boolean enable, List<INTELLIGENT> batch) {
    List<INTELLIGENT> list = null;
    if (conditionCode == INTELLIGENT_REASON.PILOT_PROJECT.value()) {
      list = intelligentDao.findByRocIdAndConditionCodeAndReasonCode(mr.getRocId(),
          INTELLIGENT_REASON.PILOT_PROJECT.value(), reasonCode);
    } else {
      list = intelligentDao.findByMrIdAndConditionCodeAndReasonCode(mr.getId(), conditionCode,
          reasonCode);
    }

    if (enable && (list == null || list.size() == 0)) {
      INTELLIGENT ig = new INTELLIGENT();
      ig.setApplDot(mr.getTotalDot());
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
      ig.setPrsnId(mr.getPrsnId());
      ig.setReason(reason);
      ig.setReasonCode(reasonCode);
      ig.setStartDate(mr.getMrDate());
      ig.setStatus(MR_STATUS.WAIT_CONFIRM.value());
      ig.setRocId(mr.getRocId());
      ig.setApplYm(mr.getApplYm());
      ig.setUpdateAt(new Date());
      if (batch != null) {
        batch.add(ig);
        if (batch.size() % XMLConstant.BATCH == 0) {
          saveIntelligentBatch(batch);
        }
      } else {
        intelligentDao.save(ig);
      }
      return true;
    } else if (list != null) {
      for (INTELLIGENT intelligent : list) {
        if (enable) {
          if (intelligent.getReason() != null && !intelligent.getReason().equals(reason)) {
            intelligent.setReason(reason);
            intelligent.setReasonCode(reasonCode);
            intelligent.setUpdateAt(new Date());
            if (batch != null) {
              batch.add(intelligent);
              if (batch.size() % XMLConstant.BATCH == 0) {
                saveIntelligentBatch(batch);
              }
            } else {
              intelligentDao.save(intelligent);
            }
          }
        } else {
          if (intelligent.getReason() == null || (intelligent.getReason().equals(reason)
              || reasonCode.equals(intelligent.getReasonCode()))) {
            if (mr.getStatus().intValue() == MR_STATUS.WAIT_CONFIRM.value()) {
              mr.setStatus(MR_STATUS.NO_CHANGE.value());
              mrDao.updateMrStauts(MR_STATUS.NO_CHANGE.value(), mr.getId());
            }
            intelligentDao.deleteById(intelligent.getId());
          }
        }
      }
    }
    return false;
  }

  /**
   * 寫入/刪除智能提示案件
   * 
   * @param mr MR table 的病歷
   * @param conditionCode 智能提示代碼
   * @param reasonCode ICD診斷碼/醫令
   * @param reason 提示文字
   * @param enable true:新增，false:移除
   * @return
   */
  public boolean insertIntelligent(MR mr, int conditionCode, String reasonCode, String reason,
      boolean enable) {
    List<INTELLIGENT> list = null;
    if (conditionCode == INTELLIGENT_REASON.PILOT_PROJECT.value()) {
      list = intelligentDao.findByRocIdAndConditionCodeAndReasonCode(mr.getRocId(),
          INTELLIGENT_REASON.PILOT_PROJECT.value(), reasonCode);
    } else {
      list = intelligentDao.findByMrIdAndConditionCodeAndReasonCode(mr.getId(), conditionCode,
          reasonCode);
    }

    if (enable && (list == null || list.size() == 0)) {
      INTELLIGENT ig = new INTELLIGENT();
      ig.setApplDot(mr.getTotalDot());
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
      ig.setPrsnId(mr.getPrsnId());
      ig.setReason(reason);
      ig.setReasonCode(reasonCode);
      ig.setStartDate(mr.getMrDate());
      mr.setStatus(MR_STATUS.WAIT_CONFIRM.value());
      mrDao.updateMrStauts(MR_STATUS.WAIT_CONFIRM.value(), mr.getId());
      ig.setStatus(mr.getStatus());
      ig.setRocId(mr.getRocId());
      ig.setApplYm(mr.getApplYm());
      ig.setUpdateAt(new Date());
      intelligentDao.save(ig);
      return true;
    } else if (list != null) {
      for (INTELLIGENT intelligent : list) {
        if (enable) {
          if (intelligent.getReason() != null && !intelligent.getReason().equals(reason)) {
            mr.setStatus(MR_STATUS.WAIT_CONFIRM.value());
            mrDao.updateMrStauts(MR_STATUS.WAIT_CONFIRM.value(), mr.getId());
            intelligent.setReason(reason);
            intelligent.setReasonCode(reasonCode);
            intelligent.setUpdateAt(new Date());
            intelligentDao.save(intelligent);
          }
        } else {
          if (intelligent.getReason() == null || (intelligent.getReason().equals(reason)
              || reasonCode.equals(intelligent.getReasonCode()))) {
            if (mr.getStatus().intValue() == MR_STATUS.WAIT_CONFIRM.value()) {
              mr.setStatus(MR_STATUS.NO_CHANGE.value());
              mrDao.updateMrStauts(MR_STATUS.NO_CHANGE.value(), mr.getId());
            }
            intelligentDao.deleteById(intelligent.getId());
          }
        }
      }
    }
    return false;
  }

  public boolean insertIntelligent(MR mr, INTELLIGENT intelligent, int conditionCode,
      String reasonCode, String reason, boolean enable) {

    if (enable && intelligent == null) {
      // 新的智能提示病歷
      INTELLIGENT ig = new INTELLIGENT();
      ig.setApplDot(mr.getTotalDot());
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
      mrDao.updateMrStauts(MR_STATUS.WAIT_CONFIRM.value(), mr.getId());
      ig.setStatus(mr.getStatus());
      ig.setRocId(mr.getRocId());
      ig.setApplYm(mr.getApplYm());
      ig.setUpdateAt(new Date());
      intelligentDao.save(ig);
      return true;
    } else if (intelligent != null) {
      if (enable) {
        if (!intelligent.getReason().equals(reason)) {
          // 更新智能提示內容
          mr.setStatus(MR_STATUS.WAIT_CONFIRM.value());
          mrDao.updateMrStauts(MR_STATUS.WAIT_CONFIRM.value(), mr.getId());
          intelligent.setReason(reason);
          intelligent.setReasonCode(reasonCode);
          intelligent.setUpdateAt(new Date());
          intelligentDao.save(intelligent);
        }
      } else {
        // 刪除
        if (mr.getStatus().intValue() == MR_STATUS.WAIT_CONFIRM.value()) {
          mr.setStatus(MR_STATUS.NO_CHANGE.value());
          mrDao.updateMrStauts(MR_STATUS.NO_CHANGE.value(), mr.getId());
        }
        intelligentDao.deleteById(intelligent.getId());
      }
    }
    return false;
  }

  /**
   * 計算指定年月的法定傳染病病歷.
   * 
   * @param applYm
   */
  public void calculateInfectious(String applYm) {
    String wording = parametersService.getOneValueByName("INTELLIGENT", "INFECTIOUS");
    List<CODE_TABLE> ctList = codeTableService.getInfectious();
    for (CODE_TABLE ct : ctList) {
      calculateInfectious(applYm, ct.getCode(), wording, true);
    }
  }

  public void calculateInfectious(String applYm, String code, String wording, boolean isEnable) {
    String reason = (wording != null) ? String.format(wording, code) : null;
    List<MR> list = getMRByCode(null, applYm, "icdAll", code, 0, false, null, null);
    if (list != null) {
      for (MR mr : list) {
        insertIntelligent(mr, INTELLIGENT_REASON.INFECTIOUS.value(), code, reason, isEnable, null);
      }
    }
  }

  /**
   * 計算指定年月的法定傳染病病歷.
   * 
   * @param applYm
   */
  public void calculateInfectious(List<MR> mrList, List<INTELLIGENT> batch) {
    String wording = parametersService.getOneValueByName("INTELLIGENT", "INFECTIOUS");
    List<Long> mrIdList = new ArrayList<Long>();
    List<MR> violateMrList = new ArrayList<MR>();
    List<CODE_TABLE> ctList = codeTableService.getInfectious();
    for (CODE_TABLE ct : ctList) {
      if (ct.getRemark() != null) {
        // inactive
        continue;
      }
      for (MR mr : mrList) {
        if (mr.getIcdAll() == null) {
          continue;
        }
        if (mr.getIcdAll().indexOf("," + ct.getCode() + ",") < 0) {
          continue;
        }
        mrIdList.add(mr.getId());
        violateMrList.add(mr);
      }
      if (mrIdList.size() == 0) {
        continue;
      }
      String reason = (wording != null) ? String.format(wording, ct.getCode()) : null;
      for (MR mr : violateMrList) {
        insertIntelligentNoUpdateMrStatus(mr, INTELLIGENT_REASON.INFECTIOUS.value(), ct.getCode(), reason, true, batch);
      }
      mrDao.updateMultiMrStauts(MR_STATUS.WAIT_CONFIRM.value(), mrIdList);
      mrIdList.clear();
      violateMrList.clear();
    }
    saveIntelligentBatch(batch);
  }
  
  public void calculateSameATC(List<MR> mrList, List<INTELLIGENT> batch) {
    List<PAY_CODE> list = payCodeDao.findBySameAtcOrderByAtc(1);
    if (list == null && list.size() == 0) {
      return;
    }
    String wording = parametersService.getOneValueByName("INTELLIGENT", "SAME_ATC_WORDING");
    int atcLen =
        "5".equals(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "SAME_ATC_LENGTH")) ? 5
            : 7;
   
    List<String> payCodes = null;
    String atc = "";
    for (PAY_CODE pc : list) {
      if (pc.getAtc() == null || pc.getAtc().length() < 5 || pc.getCode() == null) {
        continue;
      }
      String newAtc = null;
      if (atcLen == 5) {
        newAtc = pc.getAtc().substring(0, atcLen);
      } else {
        newAtc = pc.getAtc();
      }
      if (!atc.equals(newAtc)) {
        if (atc.length() > 0 && payCodes != null && payCodes.size() > 0) {
          calculateSameATC(mrList, batch, atc, payCodes, wording);
        }
        atc = newAtc;
        payCodes = new ArrayList<String>();
      }
      payCodes.add(pc.getCode());
    }
    calculateSameATC(mrList, batch, atc, payCodes, wording);
    saveIntelligentBatch(batch);
  }

  public void calculateSameATC(List<MR> mrList, List<INTELLIGENT> batch, String atc,
      List<String> payCodes, String wording) {
    if (payCodes == null || payCodes.size() == 0) {
      return;
    }
    String reason = (wording != null) ? String.format(wording, atc) : null;
    List<Long> mrIdList = new ArrayList<Long>();
    for (MR mr : mrList) {
      if (mr.getCodeAll() == null) {
        continue;
      }
      int match = 0;
      for (String payCode : payCodes) {
        if (mr.getCodeAll().indexOf("," + payCode + ",") > -1) {
          match++;
        }
      }
      if (match > 1) {
        insertIntelligentNoUpdateMrStatus(mr, INTELLIGENT_REASON.INFECTIOUS.value(), atc, reason, true, batch);
        mrIdList.add(mr.getId());
      }
    }
    mrDao.updateMultiMrStauts(MR_STATUS.WAIT_CONFIRM.value(), mrIdList);
  }
  /**
   * 計算指定年月應用比例偏高的病歷
   * 
   * @param chineseYm
   */
  public void calculateHighRatio(String chineseYm) {
    List<CODE_THRESHOLD> list = ctDao.findByCodeTypeOrderByStartDateDesc(2);
    String wordingHighRatioSingle =
        parametersService.getOneValueByName("INTELLIGENT", "HIGH_RATIO_SINGLE");
    String wordingHighRatioTotal =
        parametersService.getOneValueByName("INTELLIGENT", "HIGH_RATIO_TOTAL");
    String wordingHighRatio6M = parametersService.getOneValueByName("INTELLIGENT", "HIGH_RATIO_6M");
    String wordingHighRatio1M = parametersService.getOneValueByName("INTELLIGENT", "HIGH_RATIO_1M");

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
  private void processPatient(String chineseYm, String dataFormat, CODE_THRESHOLD ct, int max,
      String wording, int conditionCode, List<INTELLIGENT> intelligentList) {
    String fieldName = ct.getCode() == null ? "inhCode" : "codeAll";
    String code = ct.getCode() == null ? ct.getInhCode() : ct.getCode();

    Calendar cal = DateTool.chineseYmToCalendar(chineseYm);
    int day =
        XMLConstant.DATA_FORMAT_OP.equals(dataFormat) ? ct.getOpTimesDay() : ct.getIpTimesDay();
    cal.set(Calendar.DAY_OF_MONTH, 1);

    Calendar firstCal = DateTool.chineseYmToCalendar(chineseYm);
    firstCal.set(Calendar.DAY_OF_MONTH, 1);
    firstCal.add(Calendar.DAY_OF_YEAR, -day);
    int diffMonth = cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH)
        - (firstCal.get(Calendar.YEAR) * 12 + firstCal.get(Calendar.MONTH));
    if (diffMonth == 0) {
      diffMonth = 1;
    }
    String[] lastNMonth = getLastNMonth(cal, diffMonth);
    List<MR> list = getMRByCode(dataFormat, lastNMonth, fieldName, code, 0, false);
    HashMap<String, Integer> patientCount = new HashMap<String, Integer>();
    HashMap<String, Long> patientLastTime = new HashMap<String, Long>();
    HashMap<String, MR> patientMR = new HashMap<String, MR>();
    long maxDay = day * 86400000;
    if (list != null) {
      String reason = (wording != null) ? String.format(wording, code, max) : null;
      for (MR mr : list) {
        Integer count = patientCount.get(mr.getRocId() + mr.getName());
        String compare = ct.getCode() == null ? mr.getInhCode() : mr.getCodeAll();
        int appearCount = countStringAppear(compare, ct.getCode());
        if (count == null) {
          patientCount.put(mr.getRocId() + mr.getName(), new Integer(appearCount));
          patientLastTime.put(mr.getRocId() + mr.getName(), mr.getMrEndDate().getTime());
          patientMR.put(mr.getRocId() + mr.getName(), mr);
        } else {
          Long lastTime = patientLastTime.get(mr.getRocId() + mr.getName());
          long diff = lastTime.longValue() - mr.getMrEndDate().getTime();
          int diffDays = (int) (diff / 86400000);

          if (lastTime.longValue() - mr.getMrEndDate().getTime() < maxDay) {
            int total = count.intValue() + appearCount;
            patientCount.put(mr.getRocId() + mr.getName(), new Integer(total));
            if (total > max) {
              INTELLIGENT intelligent = findIntelligentByMrId(mr.getId(), intelligentList);
              insertIntelligent(mr, intelligent, conditionCode, code, reason,
                  ct.getStatus().intValue() == 1);
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
      int max, String wording, int conditionCode, List<INTELLIGENT> intelligentList) {
    String fieldName = ct.getCode() == null ? "inhCode" : "codeAll";
    String code = ct.getCode() == null ? ct.getInhCode() : ct.getCode();
    List<MR> list = getMRByCode(dataFormat, chineseYm, fieldName, code, max, true,
        ct.getStartDate(), ct.getEndDate());
    if (list != null) {
      String reason = (wording != null) ? String.format(wording, code, max) : null;
      for (MR mr : list) {
        INTELLIGENT intelligent = findIntelligentByMrId(mr.getId(), intelligentList);
        insertIntelligent(mr, intelligent, conditionCode, code, reason,
            ct.getStatus().intValue() == 1);
      }
    }
  }

  private INTELLIGENT findIntelligentByMrId(long mrId, List<INTELLIGENT> intelligentList) {
    if (intelligentList != null) {
      for (INTELLIGENT intelligent : intelligentList) {
        if (intelligent.getMrId().longValue() == mrId) {
          return intelligent;
        }
      }
    }
    return null;
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
      String wording, int conditionCode, List<INTELLIGENT> intelligentList) {
    int count = 0;
    String fieldName = ct.getCode() == null ? "inhCode" : "codeAll";
    String code = ct.getCode() == null ? ct.getInhCode() : ct.getCode();
    if (ct.getCode() == null) {
      count = mrDao.countByApplYmAndDataFormatAndInhCode(chineseYm, dataFormat,
          "%," + ct.getInhCode() + ",%").intValue();
    } else {
      // List<MR> list = getMRByCode(dataFormat, chineseYm, fieldName, ct.getCode(), max, true);
      if (XMLConstant.DATA_FORMAT_IP.equals(dataFormat)) {
        count = ippDao.countOrderByDrugNoAndApplYm(chineseYm, ct.getCode()).intValue();
      } else {
        count = oppDao.countOrderByDrugNoAndApplYm(chineseYm, ct.getCode()).intValue();
      }
    }
    if (count > max) {
      List<MR> list = getMRByCode(dataFormat, chineseYm, fieldName, code, 0, false,
          ct.getStartDate(), ct.getEndDate());
      if (list != null) {
        String reason = (wording != null) ? String.format(wording, ct.getCode(), max) : null;
        for (MR mr : list) {
          INTELLIGENT intelligent = findIntelligentByMrId(mr.getId(), intelligentList);
          insertIntelligent(mr, intelligent, conditionCode, code, reason,
              ct.getStatus().intValue() == 1);
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
  private void processHighRatio6M(String chineseYm, String dataFormat, CODE_THRESHOLD ct, int max,
      String wording, int conditionCode, List<INTELLIGENT> intelligentList) {
    String fieldName = ct.getCode() == null ? "inhCode" : "codeAll";
    String code = ct.getCode() == null ? ct.getInhCode() : ct.getCode();
    Calendar cal = DateTool.chineseYmToCalendar(chineseYm);
    String[] m6 = getLast6M(cal);
    int count6M = 0;
    int count1M = 0;
    if (ct.getCode() == null) {
      count1M = mrDao.countByApplYmAndDataFormatAndInhCode(chineseYm, dataFormat,
          "%," + ct.getInhCode() + ",%").intValue();
      count6M = mrDao.countBy6ApplYmAndDataFormatAndInhCode(m6[0], m6[1], m6[2], m6[3], m6[4],
          m6[5], dataFormat, "%," + ct.getInhCode() + ",%").intValue();
    } else {
      if (XMLConstant.DATA_FORMAT_IP.equals(dataFormat)) {
        count6M = ippDao
            .countOrderByDrugNoAnd6ApplYm(m6[0], m6[1], m6[2], m6[3], m6[4], m6[5], ct.getCode())
            .intValue();
        count1M = ippDao.countOrderByDrugNoAndApplYm(chineseYm, ct.getCode()).intValue();
      } else {
        count6M = oppDao
            .countOrderByDrugNoAnd6ApplYm(m6[0], m6[1], m6[2], m6[3], m6[4], m6[5], ct.getCode())
            .intValue();
        count1M = oppDao.countOrderByDrugNoAndApplYm(chineseYm, ct.getCode()).intValue();
      }
    }
    int avg6m = (int) (count6M / 6);

    if (count1M > (avg6m + max)) {
      List<MR> list = getMRByCode(dataFormat, chineseYm, fieldName, code, 0, false,
          ct.getStartDate(), ct.getEndDate());
      if (list != null) {
        String reason = (wording != null) ? String.format(wording, code, max) : null;
        for (MR mr : list) {
          INTELLIGENT intelligent = findIntelligentByMrId(mr.getId(), intelligentList);
          insertIntelligent(mr, intelligent, conditionCode, code, reason,
              ct.getStatus().intValue() == 1);
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
  public void calculateHighRatioAndOverAmount(String chineseYm, List<CODE_THRESHOLD> list,
      String wordingSingle, String wordingTotal, String wording6M, String wording1M,
      int conditonCode) {
    for (CODE_THRESHOLD ct : list) {
      calculateHighRatioAndOverAmount(chineseYm, ct, wordingSingle, wordingTotal, wording6M,
          wording1M, conditonCode);
    }
  }

  public void calculateHighRatioAndOverAmount(String chineseYm, CODE_THRESHOLD ct,
      String wordingSingle, String wordingTotal, String wording6M, String wording1M,
      int conditionCode) {

    String reasonCode = ct.getCode() == null ? ct.getInhCode() : ct.getCode();

    List<INTELLIGENT> list =
        intelligentDao.findByConditionCodeAndReasonCode(conditionCode, reasonCode);
    if (XMLConstant.FUNC_TYPE_ALL.equals(ct.getDataFormat())
        || XMLConstant.DATA_FORMAT_OP.equals(ct.getDataFormat())
        || CODE_THRESHOLD.DATA_FORMAT_OP_IP_OWNS.equals(ct.getDataFormat())) {
      if (ct.getOpTimesStatus().intValue() == 1) {
        // 單一就診紀錄使用數量超過 n 次
        processHighRatioSingle(chineseYm, XMLConstant.DATA_FORMAT_OP, ct,
            ct.getOpTimes().intValue(), wordingSingle, conditionCode, list);
      }
      if (ct.getOpTimesMStatus().intValue() == 1) {
        // 單月申報總數量是否高於上限
        processHighRatio1M(chineseYm, XMLConstant.DATA_FORMAT_OP, ct, ct.getOpTimesM().intValue(),
            wording1M, conditionCode, list);
      }
      if (ct.getOpTimes6mStatus().intValue() == 1) {
        processHighRatio6M(chineseYm, XMLConstant.DATA_FORMAT_OP, ct, ct.getOpTimes6m().intValue(),
            wording6M, conditionCode, list);
      }
      if (ct.getOpTimesDStatus().intValue() == 1) {
        processPatient(chineseYm, XMLConstant.DATA_FORMAT_OP, ct, ct.getOpTimesD(), wordingTotal,
            conditionCode, list);
      }
    }

    if (XMLConstant.FUNC_TYPE_ALL.equals(ct.getDataFormat())
        || XMLConstant.DATA_FORMAT_IP.equals(ct.getDataFormat())
        || CODE_THRESHOLD.DATA_FORMAT_OP_IP_OWNS.equals(ct.getDataFormat())) {
      if (ct.getIpTimesStatus().intValue() == 1) {
        processHighRatioSingle(chineseYm, XMLConstant.DATA_FORMAT_IP, ct,
            ct.getIpTimes().intValue(), wordingSingle, conditionCode, list);
      }
      if (ct.getIpTimesMStatus().intValue() == 1) {
        // 單月申報總數量是否高於上限
        processHighRatio1M(chineseYm, XMLConstant.DATA_FORMAT_IP, ct, ct.getIpTimesM().intValue(),
            wording1M, conditionCode, list);
      }
      if (ct.getIpTimes6mStatus().intValue() == 1) {
        processHighRatio6M(chineseYm, XMLConstant.DATA_FORMAT_IP, ct, ct.getIpTimes6m().intValue(),
            wording6M, conditionCode, list);
      }
      if (ct.getIpTimesDStatus().intValue() == 1) {
        processPatient(chineseYm, XMLConstant.DATA_FORMAT_IP, ct, ct.getIpTimesD(), wordingTotal,
            conditionCode, list);
      }
    }
  }

  public void removeOldHighRatioMR(CODE_THRESHOLD old, int conditionCode, boolean isOrder) {
    String wordingHighRatioSingle = parametersService.getOneValueByName("INTELLIGENT",
        isOrder ? "HIGH_RATIO_SINGLE" : "OVER_AMOUNT_SINGLE");
    String wordingHighRatioTotal = parametersService.getOneValueByName("INTELLIGENT",
        isOrder ? "HIGH_RATIO_TOTAL" : "OVER_AMOUNT_TOTAL");
    String wordingHighRatio6M = parametersService.getOneValueByName("INTELLIGENT",
        isOrder ? "HIGH_RATIO_6M" : "OVER_AMOUNT_6M");
    String wordingHighRatio1M = parametersService.getOneValueByName("INTELLIGENT",
        isOrder ? "HIGH_RATIO_1M" : "OVER_AMOUNT_1M");


    String code = old.getCode() == null ? old.getInhCode() : old.getCode();
    if (old.getOpTimesStatus() != null && old.getOpTimesStatus().intValue() == 1) {
      // 單一就診紀錄使用數量超過 n 次
      String reason = (wordingHighRatioSingle != null)
          ? String.format(wordingHighRatioSingle, code, old.getOpTimes().intValue())
          : null;
      parametersService.deleteIntelligent(conditionCode, code, reason);
    }
    if (old.getIpTimesStatus() != null && old.getIpTimesStatus().intValue() == 1) {
      String reason = (wordingHighRatioSingle != null)
          ? String.format(wordingHighRatioSingle, code, old.getIpTimes().intValue())
          : null;
      parametersService.deleteIntelligent(conditionCode, code, reason);
    }

    if (old.getOpTimesDStatus() != null && old.getOpTimesDStatus().intValue() == 1) {
      String reason = (wordingHighRatioTotal != null)
          ? String.format(wordingHighRatioTotal, code, old.getOpTimesD().intValue())
          : null;
      parametersService.deleteIntelligent(conditionCode, code, reason);
    }
    if (old.getIpTimesDStatus() != null && old.getIpTimesDStatus().intValue() == 1) {
      String reason = (wordingHighRatioTotal != null)
          ? String.format(wordingHighRatioTotal, code, old.getIpTimesD().intValue())
          : null;
      parametersService.deleteIntelligent(conditionCode, code, reason);
    }

    if (old.getOpTimesMStatus() != null && old.getOpTimesMStatus().intValue() == 1) {
      // 單月申報總數量是否高於上限
      String reason = (wordingHighRatio1M != null)
          ? String.format(wordingHighRatio1M, code, old.getOpTimesM().intValue())
          : null;
      parametersService.deleteIntelligent(conditionCode, code, reason);
    }
    if (old.getIpTimesMStatus() != null && old.getIpTimesMStatus().intValue() == 1) {
      // 單月申報總數量是否高於上限
      String reason = (wordingHighRatio1M != null)
          ? String.format(wordingHighRatio1M, code, old.getIpTimesM().intValue())
          : null;
      parametersService.deleteIntelligent(conditionCode, code, reason);
    }
    if (old.getIpTimes6mStatus() != null && old.getIpTimes6mStatus().intValue() == 1) {
      String reason = (wordingHighRatio6M != null)
          ? String.format(wordingHighRatio6M, code, old.getIpTimes6m().intValue())
          : null;
      parametersService.deleteIntelligent(conditionCode, code, reason);
    }
    if (old.getOpTimes6mStatus() != null && old.getOpTimes6mStatus().intValue() == 1) {
      String reason = (wordingHighRatio6M != null)
          ? String.format(wordingHighRatio6M, code, old.getOpTimes6m().intValue())
          : null;
      parametersService.deleteIntelligent(conditionCode, code, reason);
    }
  }

  /**
   * 計算 特別用量藥品、衛材
   */
  public void calculateOverAmount(String chineseYm) {
    List<CODE_THRESHOLD> list = ctDao.findByCodeTypeOrderByStartDateDesc(3);
    String wordingOverAmountSingle =
        parametersService.getOneValueByName("INTELLIGENT", "OVER_AMOUNT_SINGLE");
    String wordingOverAmountTotal =
        parametersService.getOneValueByName("INTELLIGENT", "OVER_AMOUNT_TOTAL");
    String wordingOverAmount6M =
        parametersService.getOneValueByName("INTELLIGENT", "OVER_AMOUNT_6M");
    String wordingOverAmount1M =
        parametersService.getOneValueByName("INTELLIGENT", "OVER_AMOUNT_1M");
    calculateHighRatioAndOverAmount(chineseYm, list, wordingOverAmountSingle,
        wordingOverAmountTotal, wordingOverAmount6M, wordingOverAmount1M,
        INTELLIGENT_REASON.OVER_AMOUNT.value());
  }

  /**
   * 計算健保項目對應自費項目並存及高風險診斷碼醫令組合
   */
  public void calculateInhExistAndHighRisk(List<MR> mrList, List<INTELLIGENT> batch, boolean isHighRisk) {
    List<CODE_CONFLICT> list = codeConflictDao.findByCodeType(isHighRisk ? Integer.valueOf(2) : Integer.valueOf(1));
    String wording = parametersService.getOneValueByName("INTELLIGENT", isHighRisk ? "HIGH_RISK_WORDING" : "CODE_CONFLICT");

    if (list != null && list.size() > 0) {
      for (CODE_CONFLICT cc : list) {
        if (cc.getStatus().intValue() == 0) {
          continue;
        }
        processCodeConflict(mrList, XMLConstant.DATA_FORMAT_OP, cc, wording, batch);
        processCodeConflict(mrList, XMLConstant.DATA_FORMAT_IP, cc, wording, batch);
      }
    }
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
    String[] result = new String[n + 1];
    result[0] = String.valueOf(DateTool.getChineseYm(temp));
    for (int i = 0; i < n; i++) {
      temp.add(Calendar.MONTH, -1);
      result[i + 1] = String.valueOf(DateTool.getChineseYm(temp));
    }
    return result;
  }

  public List<String> getMRHint(Long mrId) {
    List<String> result = new ArrayList<String>();
    List<INTELLIGENT> list = intelligentDao.findByMrId(mrId);
    for (INTELLIGENT intelligent : list) {
      result.add(intelligent.getReason());
    }
    return result;
  }

  public void calculateCodeConflict(String chineseYm, CODE_CONFLICT cc, String wording,
      boolean isEnable, String dataFormat) {
    if (XMLConstant.FUNC_TYPE_ALL.equals(dataFormat)) {
      processCodeConflict(chineseYm, XMLConstant.DATA_FORMAT_OP, cc, wording, isEnable);
      processCodeConflict(chineseYm, XMLConstant.DATA_FORMAT_IP, cc, wording, isEnable);
    } else {
      processCodeConflict(chineseYm, dataFormat, cc, wording, isEnable);
    }
  }

  /**
   * 
   * @param chineseYm
   * @param dataFormat
   * @param cc
   * @param wording
   * @param isEnable 給高風險組合用
   */
  private void processCodeConflict(String chineseYm, String dataFormat, CODE_CONFLICT cc,
      String wording, boolean isEnable) {
    if (cc.getCodeType().intValue() == 1) {
      // CODE_TYPE: 1: 醫令/健保碼，2: ICD 診斷碼
      List<MR> list = getMRBy2PayCode(dataFormat, chineseYm, cc.getCode(), 0, cc.getOwnExpCode(), 0);
      processCodeConflict(list, dataFormat, cc, wording, null);
    } else {
      List<MR> list = getMRByICDAndPayCode(dataFormat, chineseYm, cc.getCode(), cc.getOwnExpCode());
      String reason =
          (wording != null) ? String.format(wording, cc.getCode(), cc.getOwnExpCode()) : null;
      if (list != null) {
        for (MR mr : list) {
          insertIntelligent(mr, INTELLIGENT_REASON.HIGH_RISK.value(), cc.getCode(), reason,
              isEnable, null);
        }
      }
    }
  }

  /**
   * 找出違反健保項目對應自費項目並存設定的病歷
   * @param mrList
   * @param dataFormat
   * @param cc
   * @param wording
   * @param batch
   */
  private void processCodeConflict(List<MR> mrList, String dataFormat, CODE_CONFLICT cc,
      String wording, List<INTELLIGENT> batch) {
    // CODE_TYPE: 1: 醫令/健保碼，2: ICD 診斷碼
    List<Long> mrIdList = new ArrayList<Long>();
    // getMRBy2PayCode(dataFormat, chineseYm, cc.getCode(),
    // cc.getQuantityNh().intValue(), cc.getOwnExpCode(), cc.getQuantityOwn().intValue());
    for (MR mr : mrList) {
      if (mr.getCodeAll() == null) {
        continue;
      }
      if (!dataFormat.equals(mr.getDataFormat())) {
        continue;
      }
      if (cc.getCodeType().intValue() == 1) {
        if (mr.getCodeAll().indexOf("," + cc.getCode() + ",") < 0) {
          continue;
        }
        if (mr.getInhCode() == null || mr.getInhCode().indexOf(cc.getOwnExpCode()) < 0) {
          continue;
        }
        if (mr.getMrEndDate().before(cc.getStartDate())) {
          continue;
        }
        if (mr.getMrEndDate().after(cc.getEndDate())) {
          continue;
        }
      } else {
        if (mr.getIcdAll().indexOf("," + cc.getCode() + ",") < 0) {
          continue;
        }
        if (mr.getCodeAll() == null || mr.getCodeAll().indexOf(cc.getOwnExpCode()) < 0) {
          continue;
        }
      }
      mrIdList.add(mr.getId());
    }
    if (mrIdList.size() == 0) {
      return;
    }
    if (cc.getCodeType().intValue() == 2) {
      String reason =
          (wording != null) ? String.format(wording, cc.getCode(), cc.getOwnExpCode()) : null;

      for (MR mr : mrList) {
        for (Long mrId : mrIdList) {
          if (mr.getId().longValue() == mrId.longValue()) {
            insertIntelligentNoUpdateMrStatus(mr, INTELLIGENT_REASON.HIGH_RISK.value(), cc.getCode(),
                reason, true, batch);
            break;
          }
        }
      }
      mrDao.updateMultiMrStauts(MR_STATUS.WAIT_CONFIRM.value(), mrIdList);
      return;
    }
    
    List<Object[]> data = null;
    if (XMLConstant.DATA_FORMAT_OP.equals(dataFormat)) {
      data = oppDao.getMrIdAndDrugNoAndTotalQByMrIdList(cc.getCode(), cc.getOwnExpCode(), mrIdList);
    } else {
      data =
          ippDao.getMrIdAndOrderCodeAndTotalQByMrIdList(cc.getCode(), cc.getOwnExpCode(), mrIdList);
    }
    Map<Long, String> violateMrMap = new HashMap<Long, String>();
    long mrId = 0;
    int orderCodeCount = 0;
    int ownExpCodeCount = 0;
    for (Object[] obj : data) {
      long newMrId = ((BigInteger) obj[0]).longValue();
      if (violateMrMap.containsKey((new Long(newMrId)))) {
        continue;
      }
      if (mrId != newMrId) {
        orderCodeCount = 0;
        ownExpCodeCount = 0;
        mrId = newMrId;
      }
      if (cc.getCode().equals((String) obj[1])) {
        orderCodeCount = ((Double) obj[3]).intValue();
      } else if (cc.getOwnExpCode().equals((String) obj[2])) {
        ownExpCodeCount = ((Double) obj[3]).intValue();
      }
      if (orderCodeCount >= cc.getQuantityNh() && ownExpCodeCount >= cc.getQuantityOwn()) {
        violateMrMap.put(mrId, "");
      }
    }
    String reason =
        (wording != null) ? String.format(wording, cc.getCode(), cc.getOwnExpCode()) : null;
    if (violateMrMap.size() == 0) {
      return;
    }

    for (MR mr : mrList) {
      if (violateMrMap.get(mr.getId()) == null) {
        continue;
      }
      insertIntelligentNoUpdateMrStatus(mr, INTELLIGENT_REASON.INH_OWN_EXIST.value(), cc.getCode(),
          reason, true, batch);
    }
    mrDao.updateMultiMrStauts(MR_STATUS.WAIT_CONFIRM.value(),
        new ArrayList<Long>(violateMrMap.keySet()));
  }

  public void calculateSameATC(String chineseYm, List<String> payCodelist, String atcCode,
      String wording) {
    List<MR> list = getMRByPayCodes(chineseYm, payCodelist);
    if (list != null) {
      for (MR mr : list) {
        StringBuffer sb = new StringBuffer(wording);
        sb.append("(");
        for (String payCode : payCodelist) {
          if (mr.getCodeAll().indexOf("," + payCode + ",") > -1) {
            sb.append(payCode);
            sb.append(",");
          }
        }
        if (sb.charAt(sb.length() - 1) == ',') {
          sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");
        insertIntelligent(mr, INTELLIGENT_REASON.SAME_ATC.value(), atcCode, sb.toString(), true,
            null);
      }
    }
  }

  public void calculateSameATCDisable(List<String> payCodeList, String atcCode) {
    parametersService.deleteIntelligent(INTELLIGENT_REASON.SAME_ATC.value(), atcCode, null);
    // List<MR> list = mrDao.getIntelligentMR(INTELLIGENT_REASON.SAME_ATC.value(), atcCode + "%");
    // if (list != null) {
    // for (MR mr : list) {
    // int count = 0;
    // for (String str : payCodeList) {
    // if (mr.getCodeAll().indexOf("," + str + ",") > -1) {
    // count++;
    // }
    // if (count > 1) {
    // break;
    // }
    // }
    // if (count < 2) {
    // // 不符合同性質藥物條件，因此移除
    // insertIntelligent(mr, INTELLIGENT_REASON.SAME_ATC.value(), atcCode, null, false);
    // }
    // }
    // }
  }

  /**
   * 找出符合收案條件的病歷或移除收案條件的病歷
   */
  public void calculatePilotProjectByThread(Long planConditionId, boolean isEnable) {
    Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        calculatePilotProject(planConditionId, isEnable);
      }
    });
    thread.start();
  }

  /**
   * 找出符合收案條件的病歷或移除收案條件的病歷
   */
  public void calculatePilotProject(Long planConditionId, boolean isEnable) {
    PilotProject pp = getPilotProject(planConditionId);
    if (pp.getIcd() == null || pp.getIcd().size() == 0) {
      return;
    }
    if (!isEnable) {
      parametersService.deleteIntelligent(INTELLIGENT_REASON.PILOT_PROJECT.value(),
          planConditionId.toString(), null);
      return;
    }
    parametersService.waitIfIntelligentRunning(INTELLIGENT_REASON.PILOT_PROJECT.value());
    setIntelligentRunning(INTELLIGENT_REASON.PILOT_PROJECT.value(), true);
    String wording = parametersService.getOneValueByName("INTELLIGENT", "PILOT_PROJECT_TIPS");
    // 抓最近三個月
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, 1);

    for (int i = 1; i < 24; i++) {
      processPilotProject(String.valueOf(DateTool.getChineseYm(cal)), pp, wording, isEnable);
      cal.add(Calendar.MONTH, -i);
    }
    setIntelligentRunning(INTELLIGENT_REASON.PILOT_PROJECT.value(), false);
  }

  public PilotProject getPilotProject(Long planConditionId) {
    PilotProject pp = new PilotProject();
    pp.setId(planConditionId);
    Map<String, Object> map = planConditionService.findOne(planConditionId);
    pp.setName((String) map.get("name"));

    List<String> icds = (List<String>) map.get("icd_no");
    if (icds != null && icds.size() > 0) {
      int enable = (map.get("icd_no_enable") instanceof Short) ? (Short) map.get("icd_no_enable")
          : (Integer) map.get("icd_no_enable");
      if (enable == 1) {
        pp.setIcd(icds);
      }
    }

    if (pp.getIcd() == null) {
      pp.setIcd(new ArrayList<String>());
    }
    List<Map<String, Object>> daysList = (List<Map<String, Object>>) map.get("less_nday");
    if (daysList != null && daysList.size() > 0) {
      int days = 0;
      int enable =
          (map.get("less_nday_enable") instanceof Short) ? (Short) map.get("less_nday_enable")
              : (Integer) map.get("less_nday_enable");
      if (enable == 1) {
        for (Map<String, Object> map2 : daysList) {
          if (pp.getIcd().indexOf((String) map2.get("icd_no")) < 0) {
            pp.getIcd().add((String) map2.get("icd_no"));
          }
          days = (Short) map2.get("nday");
        }
        pp.setDays(days);
      }
    }

    List<Map<String, Object>> timesList = (List<Map<String, Object>>) map.get("more_times");
    if (timesList != null && timesList.size() > 0) {
      int times = 0;
      int enable =
          (map.get("more_times_enable") instanceof Short) ? (Short) map.get("more_times_enable")
              : (Integer) map.get("more_times_enable");
      if (enable == 1) {
        for (Map<String, Object> map2 : timesList) {
          if (enable == 0) {
            break;
          }
          if (pp.getIcd().indexOf((String) map2.get("icd_no")) < 0) {
            pp.getIcd().add((String) map2.get("icd_no"));
          }
          times = (Short) map2.get("times");
        }
        pp.setTimes(times);
      }
    }
    return pp;
  }

  /**
   * 試辦計畫：處理只需ICD碼判斷的病歷
   * 
   * @param dataFormat
   * @param ct
   * @param max
   * @param wording
   */
  private void processPilotProject(String chineseYm, PilotProject pp, String wording,
      boolean isEnable) {
    String reason = (wording != null) ? String.format(wording, pp.getName()) : null;

    logger.info("processPilotProject " + pp.getName());
    if (pp.getDays() == null && pp.getTimes() == null) {
      // 有指定的診斷碼
      List<MR> list = getMRByICD(chineseYm, pp.getIcd());
      if (list != null) {
        for (MR mr : list) {
          List<INTELLIGENT> alreadyInList = intelligentDao.findByRocIdAndConditionCodeAndReasonCode(
              mr.getRocId(), INTELLIGENT_REASON.PILOT_PROJECT.value(), String.valueOf(pp.getId()));
          if (alreadyInList != null && alreadyInList.size() > 0) {
            continue;
          }
          insertIntelligent(mr, INTELLIGENT_REASON.PILOT_PROJECT.value(),
              String.valueOf(pp.getId()), reason, isEnable, null);
        }
      }
    } else if (pp.getDays() != null && pp.getTimes() != null) {
      List<MR> list =
          getMRByCodeAndDaysAndCount(chineseYm, pp.getIcd(), pp.getTimes(), pp.getDays());
      if (list != null) {
        for (MR mr : list) {
          List<INTELLIGENT> alreadyInList = intelligentDao.findByRocIdAndConditionCodeAndReasonCode(
              mr.getRocId(), INTELLIGENT_REASON.PILOT_PROJECT.value(), String.valueOf(pp.getId()));
          if (alreadyInList != null && alreadyInList.size() > 0) {
            continue;
          }
          insertIntelligent(mr, INTELLIGENT_REASON.PILOT_PROJECT.value(),
              String.valueOf(pp.getId()), reason, isEnable, null);
        }
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
  private List<MR> getMRByICD(String applYm, List<String> code) {

    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 1017L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (applYm != null) {
          predicate.add(cb.equal(root.get("applYm"), applYm));
        }
        String parameterName = "icdAll";
        List<Predicate> predicateOr = new ArrayList<Predicate>();
        for (String icd : code) {
          System.out.println("like %," + icd);
          predicateOr.add(cb.like(root.get(parameterName), "%," + icd + "%"));
        }
        Predicate[] preOr = new Predicate[predicateOr.size()];
        predicate.add(cb.or(predicateOr.toArray(preOr)));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    // if (countEvery) {
    // List<MR> result = new ArrayList<MR>();
    // List<MR> list = mrDao.findAll(spec);
    // for (MR mr : list) {
    // String compare = (isICD) ? mr.getIcdAll() : mr.getCodeAll();
    // if (countStringAppear(compare, code) > max) {
    // result.add(mr);
    // }
    // }
    // return result;
    // } else if (mrDao.count(spec) > max) {
    return mrDao.findAll(spec);
  }

  /**
   * 試辦計畫：處理符合ICD碼、天數及次數限制的病歷
   */
  private List<MR> getMRByCodeAndDaysAndCount(String applYm, List<String> code, int max, int days) {
    Date date = DateTool.convertChineseToYear(applYm);
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DAY_OF_MONTH, -1);
    Date findDateAfter = cal.getTime();
    cal.add(Calendar.DAY_OF_MONTH, -days + 1);
    Date firstDay = cal.getTime();
    cal.add(Calendar.DAY_OF_MONTH, days);
    cal.add(Calendar.MONTH, 1);
    cal.add(Calendar.DAY_OF_MONTH, -1);
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    Date lastDay = cal.getTime();

    StringBuffer sb = new StringBuffer();
    for (String string : code) {
      sb.append(string);
      sb.append(" ");
    }
    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 1018L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        predicate.add(cb.between(root.get("mrDate"), firstDay, lastDay));
        predicate.add(cb.equal(root.get("dataFormat"), XMLConstant.DATA_FORMAT_OP));
        String parameterName = "icdcm1";
        addPredicate(root, predicate, cb, parameterName, sb.toString(), false, false, false);
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));

        List<Order> orderList = new ArrayList<Order>();
        orderList.add(cb.desc(root.get("mrDate")));
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
    List<MR> result = new ArrayList<MR>();
    List<MR> list = mrDao.findAll(spec);
    // 存放<姓名+證號, 次數>
    HashMap<String, Integer> map = new HashMap<String, Integer>();
    HashMap<String, MR> mapMR = new HashMap<String, MR>();
    // 存放<姓名+證號, 就醫日期> 避免同一天二張病歷
    HashMap<String, Long> mapMrDate = new HashMap<String, Long>();
    int MATCH = -1;
    for (MR mr : list) {
      String key = mr.getName() + mr.getRocId();
      Integer count = map.get(key);
      if (count != null) {
        if (mapMrDate.get(key).longValue() == mr.getMrDate().getTime()) {
          continue;
        }
        if (count.intValue() != MATCH) {
          // 表示已符合條件並加到result內
          int newCount = count.intValue() + 1;
          if (newCount >= max) {
            result.add(mapMR.get(key));
            map.put(key, MATCH);
          } else {
            map.put(key, newCount);
          }
        }
      } else {
        if (mr.getMrDate().after(findDateAfter)) {
          // 只有在要找到年月病歷才要計算
          map.put(key, 1);
          mapMR.put(key, mr);
          mapMrDate.put(key, mr.getMrDate().getTime());
        }
      }
    }
    // logger.info("before exclude:" + result.size());
    result = excludeCaseTypeE1(applYm, result);
    // System.out.println("after exclude:" + result.size());
    return result;

  }

  /**
   * 將已是試辦計畫的門急診病歷排除掉
   * 
   * @param applYm
   * @param mrList
   * @return
   */
  private List<MR> excludeCaseTypeE1(String applYm, List<MR> mrList) {
    List<Long> mrId = new ArrayList<Long>();
    String caseTypePilotProject = "E1";
    Specification<OP_D> spec = new Specification<OP_D>() {

      private static final long serialVersionUID = 1019L;

      public Predicate toPredicate(Root<OP_D> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        In<Long> inClause = cb.in(root.get("mrId"));

        for (MR mr : mrList) {
          inClause.value(mr.getId());
        }
        predicate.add(inClause);
        predicate.add(cb.equal(root.get("caseType"), caseTypePilotProject));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    List<MR> result = new ArrayList<MR>();
    List<OP_D> list = opdDao.findAll(spec);
    for (MR mr : mrList) {
      boolean isFound = false;
      for (OP_D op_D : list) {
        if (op_D.getMrId().longValue() == mr.getId().longValue()) {
          isFound = true;
          break;
        }
      }
      if (!isFound) {
        result.add(mr);
        if (mr.getDataFormat().equals(XMLConstant.DATA_FORMAT_IP)) {
          System.out.println("IP:" + mr.getId());
        }
      }
    }

    return result;
  }

  public void removeIntelligentWaitConfirm(long mrId) {
    List<INTELLIGENT> list = intelligentDao.findByMrId(mrId);
    if (list == null || list.size() == 0) {
      return;
    }
    for (INTELLIGENT intelligent : list) {
      intelligent.setStatus(MR_STATUS.NO_CHANGE.value());
    }
  }

  public synchronized boolean isIntelligentRunning(int intelligentCode) {
    Long runningTime = runningIntelligent.get(new Integer(intelligentCode));
    if (runningTime == null) {
      return false;
    }
    return runningTime > 0;
  }

  public synchronized void setIntelligentRunning(int intelligentCode, boolean isRunning) {
    if (isRunning) {
      runningIntelligent.put(new Integer(intelligentCode), new Long(System.currentTimeMillis()));
    } else {
      runningIntelligent.put(new Integer(intelligentCode), new Long(-1));
    }
  }

  public synchronized void extendIntelligentRunning(int intelligentCode, long second) {
    Long runningTime = runningIntelligent.get(new Integer(intelligentCode));
    if (runningTime == null || runningTime < 0) {
      runningTime = System.currentTimeMillis();
    }
    if (runningTime < Long.MAX_VALUE) {
      runningIntelligent.put(new Integer(intelligentCode), new Long(runningTime + second * 1000));
    }
  }

  public synchronized void setIntelligentRunningTime(int intelligentCode, long time) {
    runningIntelligent.put(new Integer(intelligentCode), time);
  }

  public synchronized long getIntelligentRunningTime(int intelligentCode) {
    Long runningTime = runningIntelligent.get(new Integer(intelligentCode));
    if (runningTime == null) {
      runningTime = -1L;
    }
    return runningTime.longValue();
  }

  public void recalculateAICostThread() {
    Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        recalculateAICost();
      }
    });
    thread.start();
  }

  /**
   * 計算費用差異
   */
  public void recalculateAICost() {
    parametersService.waitIfIntelligentRunning(INTELLIGENT_REASON.INFECTIOUS.value());
    setIntelligentRunning(INTELLIGENT_REASON.COST_DIFF.value(), true);

    Calendar cal = Calendar.getInstance();

    cal.set(Calendar.DAY_OF_MONTH, 1);

    String minYm = mrDao.getMinYm();
    if (minYm == null) {
      return;
    }
    int min = Integer.parseInt(minYm) + 191100;
    String maxYm = mrDao.getMaxYm();
    if (maxYm == null) {
      return;
    }
    int max = Integer.parseInt(maxYm) + 191100;
    int adYM = cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH) + 1;
    if (adYM < min || adYM > min) {
      adYM = min;
      cal.set(Calendar.YEAR, Integer.parseInt(String.valueOf(adYM).substring(0, 4)));
      cal.set(Calendar.MONTH, Integer.parseInt(String.valueOf(adYM).substring(4, 6)) - 1);
    }
    do {
      cal.add(Calendar.MONTH, 1);
      adYM = cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH) + 1;
      if (adYM > max) {
        break;
      }
      calculateAICost(String.valueOf(adYM - 191100));
    } while (true);
    logger.info("recalculateAICost done");
    setIntelligentRunning(INTELLIGENT_REASON.COST_DIFF.value(), false);
  }

  /**
   * 計算AI功能費用差異
   * 
   * @param applYm 申報年月
   */
  public void calculateAICost(String applYm) {
    // 設定檔上限
    float costDiffUl =
        Float.valueOf(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "COST_DIFF_UL"))
            / 100 + (float) 1;
    // 設定檔下限
    float costDiffll = (float) 1
        - Float.valueOf(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "COST_DIFF_LL"))
            / 100;
    /// 上限字樣
    String wordUl = parametersService.getOneValueByName("INTELLIGENT", "COST_DIFF_UL_WORDING");
    /// 下限字樣
    String wordLl = parametersService.getOneValueByName("INTELLIGENT", "COST_DIFF_LL_WORDING");

    Calendar cal = Calendar.getInstance();
    cal.setTime(DateTool.convertChineseToYear(applYm));
    cal.add(Calendar.YEAR, -1);
    java.sql.Date startDate = new java.sql.Date(cal.getTimeInMillis());
    cal.add(Calendar.YEAR, 1);
    cal.add(Calendar.DAY_OF_YEAR, -1);
    java.sql.Date endDate = new java.sql.Date(cal.getTimeInMillis());

    List<Map<String, Object>> list = aiDao.costDiffOP(startDate, endDate,
        XMLConstant.DATA_FORMAT_OP, applYm, costDiffUl, costDiffll);

    insertIntelligentForAICost(list, wordUl, wordLl);

    list = aiDao.costDiffOP(startDate, endDate, XMLConstant.DATA_FORMAT_IP, applYm, costDiffUl,
        costDiffll);

    insertIntelligentForAICost(list, wordUl, wordLl);
  }

  private void insertIntelligentForAICost(List<Map<String, Object>> list, String wordUl,
      String wordLl) {
    for (Map<String, Object> map : list) {
      Optional<MR> optional = mrDao.findById(Long.parseLong(map.get("ID").toString()));
      if (!optional.isPresent()) {
        continue;
      }
      MR mr = optional.get();

      int t_dot = (int) map.get("T_DOT");
      float up = Float.valueOf(map.get("UP").toString());
      float down = Float.valueOf(map.get("DOWN").toString());

      DecimalFormat df = new DecimalFormat("#.##");
      String reason = null;
      if (t_dot > up) {
        float per = ((t_dot - up) * 100) / up;
        reason = String.format(wordUl + "%", mr.getId().toString(), mr.getIcdcm1(),
            (int) (Math.floor(up)), df.format(per));
      } else if (t_dot < down) {
        float per = ((down - t_dot) * 100) / down;
        reason = String.format(wordLl + "%", mr.getId(), mr.getIcdcm1(), (int) (Math.floor(down)),
            df.format(per));
      }
      insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), mr.getIcdcm1().toString(), reason,
          true, null);
    }
  }

  public void recalculateAIIpDaysThread() {
    Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        recalculateAIIpDays();
      }
    });
    thread.start();
  }

  /**
   * 計算AI功能住院天數差異
   */
  public void recalculateAIIpDays() {
    parametersService.waitIfIntelligentRunning(INTELLIGENT_REASON.COST_DIFF.value());
    setIntelligentRunning(INTELLIGENT_REASON.COST_DIFF.value(), true);

    Calendar cal = Calendar.getInstance();

    cal.set(Calendar.DAY_OF_MONTH, 1);

    String minYm = mrDao.getMinYm();
    if (minYm == null) {
      return;
    }
    int min = Integer.parseInt(minYm) + 191100;
    String maxYm = mrDao.getMaxYm();
    if (maxYm == null) {
      return;
    }
    int max = Integer.parseInt(maxYm) + 191100;
    int adYM = cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH) + 1;
    if (adYM < min || adYM > min) {
      adYM = min;
      // 要抓一年前的資料當比較，所以往後一年開始算
      cal.set(Calendar.YEAR, Integer.parseInt(String.valueOf(adYM).substring(0, 4)) + 1);
      cal.set(Calendar.MONTH, Integer.parseInt(String.valueOf(adYM).substring(4, 6)) - 1);
    }
    do {
      cal.add(Calendar.MONTH, 1);
      adYM = cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH) + 1;
      if (adYM > max) {
        break;
      }
      calculateAIIpDays(String.valueOf(adYM - 191100));
    } while (true);
    logger.info("recalculateAIIpDays done");
    setIntelligentRunning(INTELLIGENT_REASON.COST_DIFF.value(), false);
  }

  /**
   * 計算AI功能住院天數差異
   * 
   * @param applYm 申報年月
   */
  public void calculateAIIpDays(String applYm) {
    // 設定檔差異天數
    int days =
        Integer.valueOf(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "IP_DAYS"));
    /// 上限字樣
    String wording = parametersService.getOneValueByName("INTELLIGENT", "IP_DAYS_WORDING");

    Calendar cal = Calendar.getInstance();
    cal.setTime(DateTool.convertChineseToYear(applYm));
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    java.sql.Date sDate = new java.sql.Date(cal.getTimeInMillis());
    cal.add(Calendar.YEAR, -1);
    java.sql.Date startDate = new java.sql.Date(cal.getTimeInMillis());

    cal.add(Calendar.YEAR, 1);
    cal.add(Calendar.DAY_OF_YEAR, -1);
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    java.sql.Date endDate = new java.sql.Date(cal.getTimeInMillis());

    cal.add(Calendar.DAY_OF_YEAR, 1);
    cal.add(Calendar.MONTH, 1);
    cal.add(Calendar.DAY_OF_YEAR, -1);
    java.sql.Date eDate = new java.sql.Date(cal.getTimeInMillis());

    List<Map<String, Object>> list = aiDao.ipDays(startDate, endDate, sDate, eDate, days);
    // (startDate, endDate, XMLConstant.DATA_FORMAT_OP, applYm, costDiffUl, costDiffll);
    insertIntelligentForIpDays(list, wording);
  }

  private void insertIntelligentForIpDays(List<Map<String, Object>> list, String wording) {
    for (Map<String, Object> map : list) {
      Optional<MR> optional = mrDao.findById(Long.parseLong(map.get("MR_ID").toString()));
      if (!optional.isPresent()) {
        continue;
      }
      MR mr = optional.get();

      int up = 0;
      if (map.get("UP") instanceof BigDecimal) {
        BigDecimal bd = (BigDecimal) map.get("UP");
        up = bd.intValue();
      } else {
        Double d = (Double) map.get("UP");
        up = d.intValue();
      }
      int count = (Integer) map.get("COUNT");
      int diff = count - up;
      String prsn = mr.getPrsnName() == null ? mr.getPrsnId() : mr.getPrsnName();
      String reason =
          String.format(wording, mr.getId(), mr.getIcdcm1(), mr.getPrsnName(), up, diff);
      insertIntelligent(mr, INTELLIGENT_REASON.IP_DAYS.value(), mr.getIcdcm1().toString(), reason,
          true, null);
    }
  }

  public void recalculateAIOrderDrugThread() {
    Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        recalculateAIOrderDrug();
      }
    });
    thread.start();
  }

  /**
   * 重新計算所有病歷的用藥差異
   */
  public void recalculateAIOrderDrug() {
    parametersService.waitIfIntelligentRunning(INTELLIGENT_REASON.COST_DIFF.value());
    setIntelligentRunning(INTELLIGENT_REASON.COST_DIFF.value(), true);

    Calendar cal = Calendar.getInstance();

    cal.set(Calendar.DAY_OF_MONTH, 1);

    String minYm = mrDao.getMinYm();
    if (minYm == null) {
      return;
    }
    int min = Integer.parseInt(minYm) + 191100;
    String maxYm = mrDao.getMaxYm();
    if (maxYm == null) {
      return;
    }
    int max = Integer.parseInt(maxYm) + 191100;
    int adYM = cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH) + 1;
    if (adYM < min || adYM > min) {
      adYM = min;
      // 要抓一年前的資料當比較，所以往後一年開始算
      cal.set(Calendar.YEAR, Integer.parseInt(String.valueOf(adYM).substring(0, 4)) + 1);
      cal.set(Calendar.MONTH, Integer.parseInt(String.valueOf(adYM).substring(4, 6)) - 1);
    }
    do {
      cal.add(Calendar.MONTH, 1);
      adYM = cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH) + 1;
      if (adYM > max) {
        break;
      }
      calculateAIOrderDrug(String.valueOf(adYM - 191100));
    } while (true);
    logger.info("recalculateAIOrderDrug done");
    setIntelligentRunning(INTELLIGENT_REASON.COST_DIFF.value(), false);
  }

  /**
   * 計算AI功能用藥差異
   * 
   * @param applYm 申報年月
   */
  public void calculateAIOrderDrug(String applYm) {
    initialAIOrderDrug();
    /// 提示訊息
    String wording = parametersService.getOneValueByName("INTELLIGENT", "DRUG_DIFF_WORDING");

    Calendar cal = Calendar.getInstance();
    cal.setTime(DateTool.convertChineseToYear(applYm));
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    java.sql.Date startDate = new java.sql.Date(cal.getTimeInMillis());
    cal.add(Calendar.MONTH, 1);
    java.sql.Date endDate = new java.sql.Date(cal.getTimeInMillis());

    getDrugDiff(wording, XMLConstant.DATA_FORMAT_OP, startDate, endDate);
    getDrugDiff(wording, XMLConstant.DATA_FORMAT_IP, startDate, endDate);
    // List<Map<String, Object>> list = aiDao.icdcmDrugCountOP(endDate);
    // (startDate, endDate, XMLConstant.DATA_FORMAT_OP, applYm, costDiffUl, costDiffll);
    // insertIntelligentForIpDays(list, wording);
  }

  private void getDrugDiff(String wording, String dataFormat, java.sql.Date sdate,
      java.sql.Date edate) {
    List<ICDCM_DRUG_ATC> diff = idaDao.getDiffList(dataFormat, DRUG_DIFF_PERCENT);
    // System.out.println("dataFormat=" + dataFormat + " diff size=" + diff.size() + "," + sdate +
    // "," + edate);
    HashMap<String, String> icdAtcDrug = getIcdDiffTopMap(diff);
    HashMap<String, List<ICDCM_DRUG_ATC>> icdMap = getIcdDiffMap(diff);
    List<MR> mrList =
        mrDao.getMrByIcdcm(new ArrayList<String>(icdMap.keySet()), dataFormat, sdate, edate);
    List<INTELLIGENT> intelligentBatch = new ArrayList<INTELLIGENT>();
    for (MR mr : mrList) {
      List<ICDCM_DRUG_ATC> list = icdMap.get(mr.getIcdcm1());
      for (ICDCM_DRUG_ATC ida : list) {
        if (mr.getCodeAll().indexOf("," + ida.getDrug() + ",") > -1) {
          // 符合用藥比例偏低
          // 病歷編號%s主診斷%s，醫師%s使用藥品%s與常態(%s)選擇有差異
          String reason = String.format(wording,
              mr.getInhMrId() == null ? mr.getId().toString() : mr.getInhMrId().toString(),
              mr.getIcdcm1(), mr.getPrsnName() == null ? mr.getPrsnId() : mr.getPrsnName(),
              ida.getDrug(), icdAtcDrug.get(mr.getIcdcm1() + ida.getAtc()));
          insertIntelligent(mr, INTELLIGENT_REASON.ORDER_DRUG.value(), ida.getDrug(), reason, true,
              intelligentBatch);
          if (intelligentBatch.size() > XMLConstant.BATCH) {
            intelligentDao.saveAll(intelligentBatch);
            intelligentBatch.clear();
          }
        }
      }
    }
    if (intelligentBatch.size() > 0) {
      intelligentDao.saveAll(intelligentBatch);
      intelligentBatch.clear();
    }
  }

  private void initialAIOrderDrug() {
    java.sql.Date endDate = getIcdcmDrugAtcDataEndDate(XMLConstant.DATA_FORMAT_OP);
    if (endDate != null) {
      updateIcdcmDrugAtc(XMLConstant.DATA_FORMAT_OP, endDate);
    }
    endDate = getIcdcmDrugAtcDataEndDate(XMLConstant.DATA_FORMAT_IP);
    if (endDate != null) {
      updateIcdcmDrugAtc(XMLConstant.DATA_FORMAT_IP, endDate);
    }
  }

  private java.sql.Date getIcdcmDrugAtcDataEndDate(String dataFormat) {
    List<Map<String, Object>> list = aiDao.getMaxMrEndDateAndIcdcmDrugAtcDate(dataFormat);
    if (list != null && list.size() > 0) {
      Map<String, Object> map = list.get(0);
      java.sql.Date mrDate = (java.sql.Date) map.get("MAX_MR_DATE");
      java.sql.Date icdDate = (java.sql.Date) map.get("MAX_DATE");
      if (icdDate == null || (mrDate.getTime() > icdDate.getTime())) {
        return mrDate;
      }
    }
    return null;
  }

  private void updateIcdcmDrugAtc(String dataFormat, java.sql.Date endDate) {
    idaDao.deleteByDataFormat(dataFormat);
    List<Map<String, Object>> list = null;
    if (XMLConstant.DATA_FORMAT_OP.equals(dataFormat)) {
      list = aiDao.icdcmDrugCountOP(endDate);
    } else if (XMLConstant.DATA_FORMAT_IP.equals(dataFormat)) {
      list = aiDao.icdcmDrugCountIP(endDate);
    }
    List<ICDCM_DRUG_ATC> needSave = new ArrayList<ICDCM_DRUG_ATC>();
    List<ICDCM_DRUG_ATC> saveBatch = new ArrayList<ICDCM_DRUG_ATC>();

    String lastATC = null;
    int lastATCCount = 0;
    for (Map<String, Object> map : list) {
      // ICDCM1, temp2.COUNT as ICDCM_COUNT, temp1.DRUG_NO as DRUGNO, temp1.DRUG_COUNT,temp1.atc
      String icdAtc = (String) map.get("ICDCM1") + (String) map.get("ATC");
      if (lastATC == null) {
        lastATC = icdAtc;
      } else if (!lastATC.equals(icdAtc)) {
        for (ICDCM_DRUG_ATC icdcm_DRUG_ATC : needSave) {
          icdcm_DRUG_ATC.setAtcCount(lastATCCount);
          saveBatch.add(icdcm_DRUG_ATC);
          if (saveBatch.size() > XMLConstant.BATCH) {
            idaDao.saveAll(saveBatch);
            saveBatch.clear();
          }
        }
        needSave.clear();
        lastATCCount = 0;
        lastATC = icdAtc;
      }
      ICDCM_DRUG_ATC ida = new ICDCM_DRUG_ATC(dataFormat, map, endDate);
      needSave.add(ida);
      lastATCCount += ida.getDrugCount();
    }
    if (saveBatch.size() > 0) {
      idaDao.saveAll(saveBatch);
      saveBatch.clear();
    }
  }

  /**
   * 取得ICD+ATC使用率較低的藥物
   * 
   * @param list
   * @return
   */
  private HashMap<String, List<ICDCM_DRUG_ATC>> getIcdDiffMap(List<ICDCM_DRUG_ATC> list) {
    HashMap<String, List<ICDCM_DRUG_ATC>> result = new HashMap<String, List<ICDCM_DRUG_ATC>>();
    for (ICDCM_DRUG_ATC ida : list) {
      if (((float) ida.getDrugCount() / (float) ida.getAtcCount()) > DRUG_DIFF_PERCENT) {
        continue;
      }
      if (result.get(ida.getIcdcm()) == null) {
        List<ICDCM_DRUG_ATC> idaList = new ArrayList<ICDCM_DRUG_ATC>();
        idaList.add(ida);
        result.put(ida.getIcdcm(), idaList);
      } else {
        result.get(ida.getIcdcm()).add(ida);
      }
    }
    return result;
  }

  /**
   * 取得ICD+ATC最常用的前二組藥物名
   * 
   * @param list
   * @return
   */
  private HashMap<String, String> getIcdDiffTopMap(List<ICDCM_DRUG_ATC> list) {
    HashMap<String, String> result = new HashMap<String, String>();
    String lastIcdATC = null;
    for (ICDCM_DRUG_ATC ida : list) {
      String icdATC = ida.getIcdcm() + ida.getAtc();
      if (((float) ida.getDrugCount() / (float) ida.getAtcCount()) <= DRUG_DIFF_PERCENT) {
        continue;
      }
      if (lastIcdATC == null || !lastIcdATC.equals(icdATC)) {
        lastIcdATC = icdATC;
        result.put(icdATC, ida.getDrug());
      } else {
        String drugno = result.get(icdATC);
        if (drugno == null) {
          result.put(icdATC, ida.getDrug());
        } else if (drugno.indexOf("、") < 0) {
          // 已有一組
          result.put(icdATC, drugno + "、" + ida.getDrug());
        }
      }
    }
    return result;
  }

  /**
   * 匯完申報檔、病歷檔後，做智能提示掃描
   */
  public void checkAllIntelligentCondition() {
    List<MR> mrList = mrDao.getTodayUpdatedMR();
    List<String> applYm = getDistinctApplYm(mrList);
    List<INTELLIGENT> batch = new ArrayList<INTELLIGENT>();
    logger.info("start checkAllIntelligentCondition");
    
    Date firstDate = new Date();
    for (MR mr : mrList) {
      if (mr.getMrDate().before(firstDate)) {
        firstDate = mr.getMrDate();
      }
    }
    Calendar calStart = Calendar.getInstance();
    calStart.setTime(firstDate);
    // 週報表資料
    reportService.calculatePointWeekly(calStart);
    // 月報表資料
    for (String ym : applYm) {
      reportService.calculatePointMR(ym);
      reportService.calculateDRGMonthly(ym);
    }
    logger.info("start checkAllIntelligentCondition report finished");

    //智能提示助理 - 固定條件判斷
    checkIntelligentFixCondition(mrList, applYm, batch);
    //違反支付準則
    checkAllViolation(mrList, batch);
    saveIntelligentBatch(batch);
    logger.info("start checkAllIntelligentCondition checkAllViolation finished");
    
    logger.info("start check AI");
    // 臨床路徑差異
    for (String ym : applYm) {
      calculateAICost(ym);
      calculateAIIpDays(ym);
      calculateAIOrderDrug(ym);
    }
    logger.info("start check AI finished.");
    logger.info("start checkAllIntelligentCondition finished");
    setIntelligentRunning(INTELLIGENT_REASON.XML.value(), false);
  }
  
  private void checkIntelligentFixCondition(List<MR> mrList, List<String> applYm, List<INTELLIGENT> batch) {
    String config = parametersService.getOneValueByName("INTELLIGENT_CONFIG", "RARE_ICD");
    if (config != null && "1".equals(config)) {
      for (String ym : applYm) {
        calculateRareICD(ym);
      }
    }
    logger.info("check RARE_ICD finished");

    config = parametersService.getOneValueByName("INTELLIGENT_CONFIG", "HIGH_RATIO");
    if (config != null && "1".equals(config)) {
      for (String ym : applYm) {
        calculateHighRatio(ym);
      }
    }
    logger.info("check HIGH_RATIO finished");

    config = parametersService.getOneValueByName("INTELLIGENT_CONFIG", "OVER_AMOUNT");
    if (config != null && "1".equals(config)) {
      for (String ym : applYm) {
        calculateOverAmount(ym);
      }
    }
    logger.info("check OVER_AMOUNT finished");

    config = parametersService.getOneValueByName("INTELLIGENT_CONFIG", "INH_OWN_EXIST");
    if (config != null && "1".equals(config)) {
      calculateInhExistAndHighRisk(mrList, batch, false);
    }
    logger.info("check INH_OWN_EXIST finished");
    
    config = parametersService.getOneValueByName("INTELLIGENT_CONFIG", "INFECTIOUS");
    if (config != null && "1".equals(config)) {
      calculateInfectious(mrList, batch);
    }
    logger.info("check INFECTIOUS finished");
    
    config = parametersService.getOneValueByName("INTELLIGENT_CONFIG", "SAME_ATC");
    if (config != null && "1".equals(config)) {
      calculateSameATC(mrList, batch);
    }
    logger.info("check SAME_ATC finished");
    
    config = parametersService.getOneValueByName("INTELLIGENT_CONFIG", "HIGH_RISK");
    if (config != null && "1".equals(config)) {
      calculateInhExistAndHighRisk(mrList, batch, true);
    } logger.info("check HIGH_RISK finished");
  }

  public void checkAllViolation(List<MR> mrList, List<INTELLIGENT> batch) {
    List<Map<String, Object>> lst =
        paymentTermsDao.searchPaymentTerms("", "", "", null, null, 0, 1000000, "id", "asc");
    for (Map<String, Object> map2 : lst) {
      if ((Short) map2.get("active") == 0) {
        continue;
      }
      PaymentTermsPl pt = paymentTermsService
          .findRealPaymentTerms(((Long) map2.get("id")).longValue(), (String) map2.get("category"));
      if (pt == null) {
        continue;
      }
      try {
        List<MR> mrListUseOrderCode = getUseOrderCodeMR(mrList, pt.getNhi_no());
        if (mrListUseOrderCode.size() == 0) {
          continue;
        }
        violatePaymentTermsService.checkFee(pt, mrListUseOrderCode, batch);
        saveIntelligentBatch(batch);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private List<MR> getUseOrderCodeMR(List<MR> mrList, String orderCode) {
    List<MR> result = new ArrayList<MR>();
    for (MR mr : mrList) {
      if (mr.getCodeAll() == null) {
        continue;
      }
      if (mr.getCodeAll().indexOf("," + orderCode + ",") > -1) {
        result.add(mr);
      }
    }
    return result;
  }

  public void saveIntelligentBatch(List<INTELLIGENT> batch) {
    if (batch == null) {
      return;
    }
    if (batch.size() > 0) {
      intelligentDao.saveAll(batch);
      intelligentDao.flush();
      batch.clear();
    }
  }

  private List<String> getDistinctApplYm(List<MR> mrList) {
    Map<String, String> map = new HashMap<String, String>();
    for (MR mr : mrList) {
      if (mr.getApplYm() == null) {
        continue;
      }
      if (map.containsKey(mr.getApplYm())) {
        continue;
      }
      map.put(mr.getApplYm(), "");
    }
    return new ArrayList<String>(map.keySet());
  }

}
