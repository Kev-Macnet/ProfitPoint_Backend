/**
 * Created on 2020/9/23.
 */
package tw.com.leadtek.nhiwidget.service;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import tw.com.leadtek.nhiwidget.constant.DATA_TYPE;
import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.constant.XMLConstant;
import tw.com.leadtek.nhiwidget.dao.ASSIGNED_POINTDao;
import tw.com.leadtek.nhiwidget.dao.ATCDao;
import tw.com.leadtek.nhiwidget.dao.CODE_CONFLICTDao;
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.dao.CODE_THRESHOLDDao;
import tw.com.leadtek.nhiwidget.dao.INTELLIGENTDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.PARAMETERSDao;
import tw.com.leadtek.nhiwidget.dao.PAY_CODEDao;
import tw.com.leadtek.nhiwidget.dao.PLAN_CONDITIONDao;
import tw.com.leadtek.nhiwidget.model.JsonSuggestion;
import tw.com.leadtek.nhiwidget.model.rdb.ASSIGNED_POINT;
import tw.com.leadtek.nhiwidget.model.rdb.ATC;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_CONFLICT;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_THRESHOLD;
import tw.com.leadtek.nhiwidget.model.rdb.DEDUCTED_NOTE;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
import tw.com.leadtek.nhiwidget.model.rdb.PLAN_CONDITION;
import tw.com.leadtek.nhiwidget.payload.AssignedPoints;
import tw.com.leadtek.nhiwidget.payload.AssignedPointsListPayload;
import tw.com.leadtek.nhiwidget.payload.AssignedPointsListResponse;
import tw.com.leadtek.nhiwidget.payload.CodeConflictListPayload;
import tw.com.leadtek.nhiwidget.payload.CodeConflictListResponse;
import tw.com.leadtek.nhiwidget.payload.CodeConflictPayload;
import tw.com.leadtek.nhiwidget.payload.DRGRelatedValues;
import tw.com.leadtek.nhiwidget.payload.HighRatioOrder;
import tw.com.leadtek.nhiwidget.payload.HighRatioOrderListPayload;
import tw.com.leadtek.nhiwidget.payload.HighRatioOrderListResponse;
import tw.com.leadtek.nhiwidget.payload.InfectiousListResponse;
import tw.com.leadtek.nhiwidget.payload.InfectiousPayload;
import tw.com.leadtek.nhiwidget.payload.ParameterListPayload;
import tw.com.leadtek.nhiwidget.payload.ParameterValue;
import tw.com.leadtek.nhiwidget.payload.PointsValue;
import tw.com.leadtek.nhiwidget.payload.RareICDListPayload;
import tw.com.leadtek.nhiwidget.payload.RareICDListResponse;
import tw.com.leadtek.nhiwidget.payload.RareICDPayload;
import tw.com.leadtek.nhiwidget.payload.SameATCListPayload;
import tw.com.leadtek.nhiwidget.payload.SameATCListResponse;
import tw.com.leadtek.nhiwidget.service.pt.ViolatePaymentTermsService;
import tw.com.leadtek.tools.DateTool;
import tw.com.leadtek.tools.Utility;

@Service
public class ParametersService {

  private Logger logger = LogManager.getLogger();

  public final static int STATUS_ENABLED = 1;

  public final static int STATUS_DISABLED = 0;

  public final static String PAGE_COUNT = "PAGE_COUNT";

  public final static String CAT_TOTAL_POINTS = "TOTAL_P";

  public final static String CAT_TOTAL_POINTS_STATUS = "TOTAL_POINTS_STATUS";

  public final static String INFECTIOUS = "INFECTIOUS";

  @Autowired
  private PARAMETERSDao parametersDao;

  @Autowired
  private LogDataService logDataService;

  @Autowired
  private CODE_TABLEDao codeTableDao;

  @Autowired
  private CODE_THRESHOLDDao codeThresholdDao;

  @Autowired
  private CODE_CONFLICTDao codeConflictDao;

  @Autowired
  private PAY_CODEDao payCodeDao;

  @Autowired
  private ASSIGNED_POINTDao assignedPointDao;
  
  @Autowired
  private ATCDao atcDao;
  
  @Autowired
  private ReportService reportService;
  
  @Autowired
  private MRDao mrDao;
  
  @Autowired
  private INTELLIGENTDao intelligentDao;
  
  @Autowired
  private PLAN_CONDITIONDao planConditionDao;
  
  @Autowired
  private IntelligentService is;
  
  @Autowired
  private RedisService redisService;
  
  @Autowired
  private ViolatePaymentTermsService vpts;
  
  private static HashMap<String, String> parameters;
  
  private static final Map<String, String> DRG_SETTING_NOTE;
  
  static {
    Map<String, String> map = new HashMap<String, String>();
    map.put("SPR", "DRG SPR 標準給付額");
    map.put("ADD_HOSP_LEVEL_1", "醫學中心基本診療加成百分比");
    map.put("ADD_HOSP_LEVEL_2", "區域醫院基本診療加成百分比");
    map.put("ADD_HOSP_LEVEL_3", "地區醫院基本診療加成百分比");
    map.put("ADD_CHILD_15_6M", "兒童加成率MDC15小於6個月");
    map.put("ADD_CHILD_15_2Y", "兒童加成率MDC15大於6個月，小於2歲");
    map.put("ADD_CHILD_15_6Y", "兒童加成率MDC15大於2歲，小於6歲");
    map.put("ADD_CHILD_N15M_6M", "兒童加成率非MDC15內科小於6個月");
    map.put("ADD_CHILD_N15M_2Y", "兒童加成率非MDC15內科大於6個月，小於2歲");
    map.put("ADD_CHILD_N15M_6Y", "兒童加成率非MDC15內科大於2歲，小於6歲");
    map.put("ADD_CHILD_N15P_6M", "兒童加成率非MDC15外科小於6個月");
    map.put("ADD_CHILD_N15P_2Y", "兒童加成率非MDC15外科大於6個月，小於2歲");
    map.put("ADD_CHILD_N15P_6Y", "兒童加成率非MDC15外科大於2歲，小於6歲");
    map.put("CMI", "CMI加成率");
    map.put("CMI12", "CMI值大於 1.1 小於等於1.2加成率");
    map.put("CMI13", "CMI值小於等於1.3加成率");
    map.put("CMI14", "CMI值大於1.3加成率");
    map.put("OL", "Outlying Islands 山地離島加成率");
    DRG_SETTING_NOTE = Collections.unmodifiableMap(map);
  }
  public String getParameter(String name) {
    if (parameters == null) {
      reloadParameters();
    }
    return parameters.get(name);
  }

  public int getIntParameter(String name) {
    if (parameters == null) {
      reloadParameters();
    }
    String s = parameters.get(name);
    if (s == null) {
      return 0;
    }
    return Integer.parseInt(s);
  }

  public void reloadParameters() {
    HashMap<String, String> newParameters = new HashMap<String, String>();
    List<PARAMETERS> list = parametersDao.findAll();
    for (PARAMETERS p : list) {
      if ("SYSTEM".equals(p.getCat())) {
        newParameters.put(p.getName(), p.getValue());
      } else if ("MENU".equals(p.getCat())) {
        newParameters.put(p.getName(), p.getValue());
      }
    }
    parameters = newParameters;

    if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
      File drgFile = new File(parameters.get("DRGSERVICE_PATH") + "\\DRG.BAT");
      if (!drgFile.exists()) {
        String drgPath = parameters.get("DRGSERVICE_PATH");
        if (drgPath == null) {
          drgPath = "C:\\med\\S_DRGService_3412";
        }
        String drgEXE = parameters.get("DRGSERVICE_NAME");
        if (drgEXE == null) {
          drgEXE = "DRGICD10.exe";
        }
        logDataService.createDrgBatchFile(drgPath, drgEXE);
      }
    }
    vpts.updateWordings(true);
  }

  public AssignedPointsListResponse getAssignedPoints(Date sdate, Date edate, String orderBy,
      Boolean asc, int perPage, int page) {
    AssignedPointsListResponse result = new AssignedPointsListResponse();

    Specification<ASSIGNED_POINT> spec = new Specification<ASSIGNED_POINT>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<ASSIGNED_POINT> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (sdate != null && edate != null) {
          // 啟始日 >= sdate,啟始日 <= edate
          predicate.add(cb.and(cb.lessThanOrEqualTo(root.get("startDate"), edate),
              cb.greaterThanOrEqualTo(root.get("startDate"), sdate)));
        }
        if (predicate.size() > 0) {
          Predicate[] pre = new Predicate[predicate.size()];
          query.where(predicate.toArray(pre));
        }
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
    long total = assignedPointDao.count(spec);
    Page<ASSIGNED_POINT> pages = assignedPointDao.findAll(spec, PageRequest.of(page, perPage));
    List<AssignedPointsListPayload> list = new ArrayList<AssignedPointsListPayload>();
    if (pages != null && pages.getSize() > 0) {
      for (ASSIGNED_POINT p : pages) {
        AssignedPointsListPayload ap = new AssignedPointsListPayload(p);
        list.add(ap);
      }
    }
    result.setCount((int)total);
    result.setTotalPage(Utility.getTotalPage((int) total, perPage));
    result.setData(list);
    return result;
  }

  public AssignedPoints getAssignedPoints(long id) {
    Optional<ASSIGNED_POINT> optional = assignedPointDao.findById(id);
    if (optional.isPresent()) {
      return new AssignedPoints(optional.get());
    }
    return null;
  }

  // public AssignedPoints getAssignedPoints(long id) {
  // AssignedPoints result = new AssignedPoints();
  // result.setId(new Long(id));
  // List<PARAMETERS> list = getSameStartDateParameters(id);
  // if (list == null) {
  // return null;
  // }
  // for (PARAMETERS p : list) {
  // result.setSdate(p.getStartDate());
  // result.setEdate(p.getEndDate());
  // if (p.getName().equals("WM_IP_P")) {
  // result.setWmIpPoints(Long.parseLong(p.getValue()));
  // } else if (p.getName().equals("WM_OP_P")) {
  // result.setWmOpPoints(Long.parseLong(p.getValue()));
  // } else if (p.getName().equals("WM_DRUG_P")) {
  // result.setWmDrugPoints(Long.parseLong(p.getValue()));
  // } else if (p.getName().equals("DENTIST_OP_P")) {
  // result.setDentistOpPoints(Long.parseLong(p.getValue()));
  // } else if (p.getName().equals("DENTIST_DRUG_P")) {
  // result.setDentistDrugPoints(Long.parseLong(p.getValue()));
  // } else if (p.getName().equals("DENTIST_FUND_P")) {
  // result.setDentistFundPoints(Long.parseLong(p.getValue()));
  // } else if (p.getName().equals("HEMODIALYSIS_P")) {
  // result.setHemodialysisPoints(Long.parseLong(p.getValue()));
  // } else if (p.getName().equals("FUND_P")) {
  // result.setFundPoints(Long.parseLong(p.getValue()));
  // }
  // }
  // return result;
  // }

  public String newAssignedPoints(AssignedPoints ap) {
    if (ap.getEdate().getTime() <= ap.getSdate().getTime()) {
      return "失效日不可早於或等於生效日！";
    }
    List<ASSIGNED_POINT> list = assignedPointDao.findAll();
    if (checkTimeOverwriteAssignedPoint(list, ap.getSdate().getTime(), ap.getEdate().getTime(),
        0)) {
      return "該時段已有相關設定";
    }
    ASSIGNED_POINT newAP = assignedPointDao.save(ap.toDB());
    updatePointMonthlyTable(newAP);
    return null;
  }
  
  private void updatePointMonthlyTable(ASSIGNED_POINT ap) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(ap.getStartDate());
    cal.set(Calendar.DAY_OF_MONTH, 1);
    
    Integer minPointMonthly = reportService.getMinPointMonthly();
    if (minPointMonthly == null) {
      return;
    }
    Integer maxPointMonthly = reportService.getMaxPointMonthly();
    if (maxPointMonthly == null) {
      return;
    }
    int adYM = cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH) + 1;
    if (adYM < minPointMonthly.intValue()) {
      adYM = minPointMonthly.intValue();
      cal.set(Calendar.YEAR, Integer.parseInt(String.valueOf(adYM).substring(0, 4)));
      cal.set(Calendar.MONTH, Integer.parseInt(String.valueOf(adYM).substring(4, 6)) -1);
    }
    do {
      reportService.refreshPointMonthly(String.valueOf(adYM), ap);
      cal.add(Calendar.MONTH, 1);
      if (ap.getEndDate().before(cal.getTime())) {
        break;
      }
      adYM = cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH) + 1;
      if (adYM > maxPointMonthly.intValue()) {
        break;
      }
    } while (true);
  }

  public String updateAssignedPoints(AssignedPoints ap) {
    if (ap.getId() == null || ap.getId() == 0) {
      return "id不可為空";
    }
    if (ap.getEdate().getTime() <= ap.getSdate().getTime()) {
      return "失效日不可早於或等於生效日！";
    }
    List<ASSIGNED_POINT> list = assignedPointDao.findAll();
    if (checkTimeOverwriteAssignedPoint(list, ap.getSdate().getTime(), ap.getEdate().getTime(),
        ap.getId())) {
      return "該時段已有相關設定";
    }

    ASSIGNED_POINT newAP = assignedPointDao.save(ap.toDB());
    updatePointMonthlyTable(newAP);
    return null;
  }

  private List<PARAMETERS> getSameStartDateParameters(long id) {
    Optional<PARAMETERS> optional = parametersDao.findById(new Long(id));
    if (!optional.isPresent()) {
      return null;
    }
    PARAMETERS p = optional.get();
    return parametersDao.findByCatAndStartDate(CAT_TOTAL_POINTS, p.getStartDate());
  }

  public String deleteAssignedPoints(long id) {
    Optional<ASSIGNED_POINT> optional = assignedPointDao.findById(id);
    if (optional.isPresent()) {
      assignedPointDao.deleteById(id);
      return null;
    } else {
      return "id不存在";
    }
  }

  public String updatePointsStatus(Date startDate, Boolean wmStatus, Boolean dentistStatus) {
    List<PARAMETERS> list = parametersDao.findByCatAndStartDate(CAT_TOTAL_POINTS_STATUS, startDate);
    if (list != null && list.size() > 0) {
      // update
      for (PARAMETERS parameters : list) {
        if (parameters.getName().equals(AssignedPointsListPayload.WM)
            && !parameters.getValue().equals(wmStatus.booleanValue() ? "1" : "0")) {
          parameters.setValue(wmStatus.booleanValue() ? "1" : "0");
          parametersDao.save(parameters);
        }
        if (parameters.getName().equals(AssignedPointsListPayload.DENTIST)
            && !parameters.getValue().equals(dentistStatus.booleanValue() ? "1" : "0")) {
          parameters.setValue(dentistStatus.booleanValue() ? "1" : "0");
          parametersDao.save(parameters);
        }
      }
    } else {
      return "找不到該組設定";
    }
    return null;
  }

  public PointsValue getPointsValue(Date startDate) {
    PointsValue result = new PointsValue();
    result.setSdate(startDate);
    List<PARAMETERS> list = parametersDao.findByCatAndStartDate(CAT_TOTAL_POINTS, startDate);
    if (list == null || list.size() == 0) {
      return null;
    }
    for (PARAMETERS parameters : list) {
      if (parameters.getValue() == null) {
        continue;
      }
      if (parameters.getEndDate() != null && result.getEdate() == null) {
        result.setEdate(parameters.getEndDate());
      }
      if (parameters.getName().equals("WM_OP_POINTS")) {
        result.setWmOpPoints(Long.parseLong(parameters.getValue()));
      } else if (parameters.getName().equals("WM_IP_POINTS")) {
        result.setWmIpPoints(Long.parseLong(parameters.getValue()));
      } else if (parameters.getName().equals("WM_DRUG_POINTS")) {
        result.setWmDrugPoints(Long.parseLong(parameters.getValue()));
      } else if (parameters.getName().equals("DENTIST_OP_POINTS")) {
        result.setDentistOpPoints(Long.parseLong(parameters.getValue()));
      } else if (parameters.getName().equals("DENTIST_DRUG_POINTS")) {
        result.setDentistDrugPoints(Long.parseLong(parameters.getValue()));
      } else if (parameters.getName().equals("DENTIST_FUND_POINTS")) {
        result.setDentistFundPoints(Long.parseLong(parameters.getValue()));
      } else if (parameters.getName().equals("HEMODIALYSIS_POINTS")) {
        result.setHemodialysisPoints(Long.parseLong(parameters.getValue()));
      } else if (parameters.getName().equals("FUND_POINTS")) {
        result.setFundPoints(Long.parseLong(parameters.getValue()));
      }
    }
    return result;
  }

  public String updatePointsValue(PointsValue pv) {
    // List<PARAMETERS> list =
    // parametersDao.findByNameInAndStartDateOrderByStartDateDesc(Arrays.asList(WM, DENTIST),
    // pv.getSdate());
    List<PARAMETERS> list =
        parametersDao.findByCatAndStartDate(CAT_TOTAL_POINTS_STATUS, pv.getSdate());
    if (list != null && list.size() > 0) {
      // update
      for (PARAMETERS parameters : list) {
        if (!parameters.getEndDate().equals(pv.getEdate())) {
          parameters.setEndDate(pv.getEdate());
          parametersDao.save(parameters);
        }
      }
      List<PARAMETERS> pointsList =
          parametersDao.findByCatAndStartDate(CAT_TOTAL_POINTS, pv.getSdate());
      for (PARAMETERS parameters : pointsList) {
        parameters.setEndDate(pv.getEdate());
        updateParameterValue(parameters, "WM_OP_POINTS", pv.getWmOpPoints());
        updateParameterValue(parameters, "WM_IP_POINTS", pv.getWmIpPoints());
        updateParameterValue(parameters, "WM_DRUG_POINTS", pv.getWmDrugPoints());
        updateParameterValue(parameters, "DENTIST_OP_POINTS", pv.getDentistOpPoints());
        updateParameterValue(parameters, "DENTIST_DRUG_POINTS", pv.getDentistDrugPoints());
        updateParameterValue(parameters, "DENTIST_FUND_POINTS", pv.getDentistFundPoints());
        updateParameterValue(parameters, "HEMODIALYSIS_POINTS", pv.getHemodialysisPoints());
        updateParameterValue(parameters, "FUND_POINTS", pv.getFundPoints());
        parametersDao.save(parameters);
      }
    } else {
      return "找不到該組設定";
    }
    return null;
  }

  private void updateParameterValue(PARAMETERS parameters, String name, Long points) {
    if (parameters.getName().equals(name)) {
      if (points != null) {
        parameters.setValue(points.toString());
      } else {
        parameters.setValue(null);
      }
    }
  }

  private void saveParameter(PARAMETERS p, Date startDate, Date endDate) {
    p.setStartDate(startDate);
    p.setEndDate(endDate);
    parametersDao.save(p);
  }

  public String newPointsValue(PointsValue pv) {
    List<PARAMETERS> list =
        parametersDao.findByCatAndStartDate(CAT_TOTAL_POINTS_STATUS, pv.getSdate());
    if (list != null && list.size() > 0) {
      return "生效日重複";
    }
    list = parametersDao.findByCatAndStartDateLessThanAndEndDateGreaterThan(CAT_TOTAL_POINTS_STATUS,
        pv.getSdate(), pv.getSdate());
    if (list != null && list.size() > 0) {
      // 將有衝突的失效日往前
      changeEndDateByNewStatus(list, pv.getSdate());
    }

    PARAMETERS p = new PARAMETERS("TOTAL_POINTS_STATUS", AssignedPointsListPayload.WM,
        (pv.getWmOpPoints() == null) ? "0" : "1", PARAMETERS.TYPE_INTEGER,
        "是否計算西醫(Western Medicine)總點數");
    saveParameter(p, pv.getSdate(), pv.getEdate());

    p = new PARAMETERS("TOTAL_POINTS_STATUS", AssignedPointsListPayload.DENTIST,
        (pv.getDentistOpPoints() == null || pv.getDentistOpPoints().longValue() == 0) ? "0" : "1",
        PARAMETERS.TYPE_INTEGER, "是否計算牙醫總點數");
    saveParameter(p, pv.getSdate(), pv.getEdate());

    p = new PARAMETERS("TOTAL_POINTS_STATUS", AssignedPointsListPayload.DENTIST,
        (pv.getDentistOpPoints() == null) ? "0" : "1", PARAMETERS.TYPE_INTEGER, "是否計算牙醫總點數");
    saveParameter(p, pv.getSdate(), pv.getEdate());

    p = new PARAMETERS("TOTAL_POINTS", "WM_IP_POINTS",
        (pv.getWmIpPoints() == null) ? "0" : pv.getWmIpPoints().toString(), PARAMETERS.TYPE_LONG,
        "西醫住院分配總點數");
    saveParameter(p, pv.getSdate(), pv.getEdate());

    p = new PARAMETERS("TOTAL_POINTS", "WM_OP_POINTS",
        (pv.getWmOpPoints() == null) ? "0" : pv.getWmOpPoints().toString(), PARAMETERS.TYPE_LONG,
        "西醫門診分配總點數");
    saveParameter(p, pv.getSdate(), pv.getEdate());

    p = new PARAMETERS("TOTAL_POINTS", "WM_DRUG_POINTS",
        (pv.getWmDrugPoints() == null) ? "0" : pv.getWmDrugPoints().toString(),
        PARAMETERS.TYPE_LONG, "西醫藥品分配總點數");
    saveParameter(p, pv.getSdate(), pv.getEdate());

    p = new PARAMETERS("TOTAL_POINTS", "DENTIST_OP_POINTS",
        (pv.getDentistOpPoints() == null) ? "0" : pv.getDentistOpPoints().toString(),
        PARAMETERS.TYPE_LONG, "牙醫門診分配總點數");
    saveParameter(p, pv.getSdate(), pv.getEdate());

    p = new PARAMETERS("TOTAL_POINTS", "DENTIST_DRUG_POINTS",
        (pv.getDentistDrugPoints() == null) ? "0" : pv.getDentistDrugPoints().toString(),
        PARAMETERS.TYPE_LONG, "牙醫藥品分配總點數");
    saveParameter(p, pv.getSdate(), pv.getEdate());

    p = new PARAMETERS("TOTAL_POINTS", "DENTIST_FUND_POINTS",
        (pv.getDentistFundPoints() == null) ? "0" : pv.getDentistFundPoints().toString(),
        PARAMETERS.TYPE_LONG, "牙醫專款分配總點數");
    saveParameter(p, pv.getSdate(), pv.getEdate());

    p = new PARAMETERS("TOTAL_POINTS", "HEMODIALYSIS_POINTS",
        (pv.getHemodialysisPoints() == null) ? "0" : pv.getHemodialysisPoints().toString(),
        PARAMETERS.TYPE_LONG, "透析總點數");
    saveParameter(p, pv.getSdate(), pv.getEdate());

    p = new PARAMETERS("TOTAL_POINTS", "FUND_POINTS",
        (pv.getFundPoints() == null) ? "0" : pv.getFundPoints().toString(), PARAMETERS.TYPE_LONG,
        "專款總點數");
    saveParameter(p, pv.getSdate(), pv.getEdate());
    return null;
  }

  private void changeEndDateByNewStatus(List<PARAMETERS> parameters, Date newStartDate) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(newStartDate);
    cal.add(Calendar.DAY_OF_YEAR, -1);
    Date startDate = null;
    for (PARAMETERS p : parameters) {
      if (startDate == null) {
        startDate = p.getStartDate();
      }
      if (startDate != p.getStartDate()) {
        changeEndDateByNewStatusValue(startDate, cal.getTime());
      }
      p.setEndDate(cal.getTime());
      parametersDao.save(p);
    }
    if (startDate != null) {
      changeEndDateByNewStatusValue(startDate, cal.getTime());
    }
  }

  private void changeEndDateByNewStatusValue(Date startDate, Date newEndDate) {
    List<PARAMETERS> pointsList = parametersDao.findByCatAndStartDate(CAT_TOTAL_POINTS, startDate);
    for (PARAMETERS parameters : pointsList) {
      parameters.setEndDate(newEndDate);
      parametersDao.save(parameters);
    }
  }

  public ParameterListPayload getParameterValue(String name, Date sdate, Date edate, String orderBy,
      Boolean asc, int perPage, int page) {
    ParameterListPayload result = new ParameterListPayload();

    Specification<PARAMETERS> spec = new Specification<PARAMETERS>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<PARAMETERS> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (sdate != null && edate != null) {
//          predicate.add(cb.and(cb.lessThanOrEqualTo(root.get("endDate"), edate),
//              cb.greaterThanOrEqualTo(root.get("startDate"), sdate)));
          // 啟始日 >= sdate,啟始日 <= edate
          predicate.add(cb.and(cb.lessThanOrEqualTo(root.get("startDate"), edate),
              cb.greaterThanOrEqualTo(root.get("startDate"), sdate)));
        }
        predicate.add(cb.equal(root.get("name"), name));
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
    long total = parametersDao.count(spec);
    Page<PARAMETERS> pages = parametersDao.findAll(spec, PageRequest.of(page, perPage));
    List<ParameterValue> list = new ArrayList<ParameterValue>();
    if (pages != null && pages.getTotalElements() > 0) {
      for (PARAMETERS p : pages) {
        ParameterValue pv = new ParameterValue();
        pv.setId(p.getId());
        pv.setEdate(p.getEndDate());
        pv.setSdate(p.getStartDate());
        try {
          if (p.getDataType().intValue() == 1 && p.getValue() != null) {
            pv.setValue(Integer.parseInt(p.getValue()));
          } else if (p.getDataType().intValue() == 2 && p.getValue() != null) {
            pv.setValue(Float.parseFloat(p.getValue()));
          }
        } catch (NumberFormatException e) {
          logger.error("getParameterValue:", e);
        }
        pv.setStatus(getStatusString(p.getStartDate(), p.getEndDate()));
        if ("value".equals(orderBy) && asc != null && p.getDataType() != 3) {
          addToSortedArray(pv, list, asc);
        } else {
          list.add(pv);
        }
      }
    }
    result.setCount((int) total);
    result.setTotalPage(Utility.getTotalPage((int) total, perPage));
    result.setData(list);
    return result;
  }

  public ParameterValue getParameterValue(long id) {
    Optional<PARAMETERS> optional = parametersDao.findById(id);
    if (optional.isPresent()) {
      PARAMETERS p = optional.get();
      return new ParameterValue(p);
    }
    ParameterValue result = new ParameterValue();
    result.setValue("id不存在");
    return result;
  }

  private String getStatusString(Date startDate, Date endDate) {
    long now = System.currentTimeMillis();
    if (endDate.getTime() < now) {
      return "鎖定";
    } else if (startDate.getTime() > now) {
      return "未啟動";
    } else {
      return "使用中";
    }
  }

  private void addToSortedArray(ParameterValue pv, List<ParameterValue> list, boolean asc) {
    int i = 0;
    for (i = 0; i < list.size(); i++) {
      ParameterValue p = list.get(i);
      if (pv.getValue() instanceof Float) {
        if (asc) {
          if ((Float) pv.getValue() < (Float) p.getValue()) {
            break;
          }
        } else {
          if ((Float) pv.getValue() > (Float) p.getValue()) {
            break;
          }
        }
      } else if (pv.getValue() instanceof Integer) {
        if (asc) {
          if ((Integer) pv.getValue() < (Integer) p.getValue()) {
            break;
          }
        } else {
          if ((Integer) pv.getValue() > (Integer) p.getValue()) {
            break;
          }
        }
      }
    }
    list.add(i, pv);
  }

  public String newValue(String name, String value, Date startDate, Date endDate) {
    List<PARAMETERS> list = parametersDao.findByNameAndStartDate(name, startDate);
    if (list != null && list.size() > 0) {
      return "生效日重複";
    }
    list = parametersDao.findByNameOrderByStartDateDesc(name);
    if (checkTimeOverlap(list, startDate.getTime(), endDate.getTime(), 0)) {
      return "該時段有相同的參數設定";
    }
    // list = parametersDao.findByNameAndStartDateLessThanAndEndDateGreaterThan(name, startDate,
    // startDate);
    // if (list != null && list.size() > 0) {
    // // 將有衝突的失效日往前
    // changeEndDateByNewStatus(list, startDate);
    // }
    list = parametersDao.findByName(name);
    String cat = null;
    String note = null;
    Integer dataType = null;
    for (PARAMETERS parameters : list) {
      cat = parameters.getCat();
      note = parameters.getNote();
      dataType = parameters.getDataType();
      break;
    }
    if (cat == null) {
      cat = (name.equals("SPR") ? "DRG" : "DEDUCTED");
    }
    if (dataType == null) {
      dataType = PARAMETERS.TYPE_INTEGER;
    }
    if (note == null) {
      note = ("SPR".equals(name) ? "標準給付額" : "核刪抽件數");
    }
    // System.out.println("cat=" + cat + ", name=" + name + ",value=" + value + ",start=" +
    // startDate
    // + ", end=" + endDate);
    PARAMETERS p = new PARAMETERS(cat, name, value, dataType, note);
    saveParameter(p, startDate, endDate);
    return null;
  }

  public String updateValue(ParameterValue pv) {
    if (pv.getId() == null || pv.getId() == 0) {
      return "id不可為空";
    }
    Optional<PARAMETERS> optional = parametersDao.findById(pv.getId());
    if (!optional.isPresent()) {
      return "id不存在";
    }
    PARAMETERS p = optional.get();
    List<PARAMETERS> list = parametersDao.findByNameOrderByStartDateDesc(p.getName());
    if (checkTimeOverlap(list, pv.getSdate().getTime(), pv.getEdate().getTime(), pv.getId())) {
      return "該時段有相同的參數設定";
    }
    p.setEndDate(pv.getEdate());
    p.setStartDate(pv.getSdate());
    p.setValue(String.valueOf(pv.getValue()));
    parametersDao.save(p);
    return null;
  }

  public String deleteParameterValue(String id) {
    Optional<PARAMETERS> optional = parametersDao.findById(Long.parseLong(id));
    if (optional.isPresent()) {
      PARAMETERS p = optional.get();
      parametersDao.deleteById(p.getId());
      return null;
    }
    return "id不存在";
  }

  /**
   * 取得參數名在該時刻的值.
   * 
   * @param key
   * @param date
   * @return
   */
  public Object getParameterValueBetween(String name, Date date) {
    List<PARAMETERS> list =
        parametersDao.findByNameAndStartDateLessThanEqualAndEndDateGreaterThanEqual(name, date, date);
    if (list == null || list.size() == 0) {
      return null;
    }
    PARAMETERS p = list.get(0);
    if (p.getDataType().intValue() == 1) {
      // integer
      return Integer.parseInt(p.getValue());
    } else if (p.getDataType().intValue() == 2) {
      // float
      return Float.parseFloat(p.getValue());
    }
    // string
    return p.getValue();
  }

  public List<String> getHospitalLevel() {
    List<PARAMETERS> list = parametersDao.findByNameStartsWithOrderByName("HOSP_LEVEL_");
    List<String> result = new ArrayList<String>();
    if (list != null && list.size() > 0) {
      for (PARAMETERS p : list) {
        result.add(p.getNote());
      }
    }
    return result;
  }

  public List<String> getPayCodeCategoryOld() {
    List<PARAMETERS> list = parametersDao.findByName("PAY_CODE_CAT");
    List<String> result = new ArrayList<String>();
    if (list != null && list.size() > 0) {
      PARAMETERS p = list.get(0);
      String[] ss = p.getValue().split(" ");
      for (String string : ss) {
        result.add(string);
      }
    }
    return result;
  }
  
  public List<String> getPayCodeCategory() {
    List<CODE_TABLE> list = codeTableDao.findByCatOrderByCode("PAY_CODE_TYPE");
    List<String> result = new ArrayList<String>();
    if (list != null && list.size() > 0) {
      for (CODE_TABLE ct : list) {
        result.add(ct.getDescChi());
      }
    }
    return result;
  }
  
  public String getOneValueByName(String cat, String name) {
    List<PARAMETERS> list = parametersDao.findByCatAndName(cat, name);
    if (list != null && list.size() > 0) {
      PARAMETERS p = list.get(0);
      return p.getValue();
    }
    return null;
  }

  public DRGRelatedValues getDRGValues(Date sDate, Date eDate, DRGRelatedValues values) {
    if (values == null) {
      values = new DRGRelatedValues();
      List<PARAMETERS> list = parametersDao.findByNameOrderByStartDateDesc("SPR");
      if (list != null && list.size() > 0) {
        PARAMETERS p = list.get(0);
        sDate = p.getStartDate();
        eDate = p.getEndDate();
      }
    }
    List<PARAMETERS> list = parametersDao
        .findByCatAndStartDateLessThanEqualAndEndDateGreaterThanEqual("DRG", sDate, eDate);

    values.setStatus(getStatusString(sDate, eDate));
    DecimalFormat df = new DecimalFormat("#.#");
    if (list != null && list.size() > 0) {
      for (PARAMETERS parameters : list) {
        if (parameters.getName().equals("SPR")) {
          values.setSpr(Integer.parseInt(parameters.getValue()));
          values.setId(parameters.getId());
        } else if (parameters.getName().equals("ADD_HOSP_LEVEL_1")) {
          values.setAddHospLevel1(df.format(Float.parseFloat(parameters.getValue()) * 100));
        } else if (parameters.getName().equals("ADD_HOSP_LEVEL_2")) {
          values.setAddHospLevel2(df.format(Float.parseFloat(parameters.getValue()) * 100));
        } else if (parameters.getName().equals("ADD_HOSP_LEVEL_3")) {
          values.setAddHospLevel3(df.format(Float.parseFloat(parameters.getValue()) * 100));
        } else if (parameters.getName().equals("ADD_CHILD_15_6M")) {
          values.setAdd15Child6m(df.format(Float.parseFloat(parameters.getValue()) * 100));
        } else if (parameters.getName().equals("ADD_CHILD_15_2Y")) {
          values.setAdd15Child2y(df.format(Float.parseFloat(parameters.getValue()) * 100));
        } else if (parameters.getName().equals("ADD_CHILD_15_6Y")) {
          values.setAdd15Child6y(df.format(Float.parseFloat(parameters.getValue()) * 100));
        } else if (parameters.getName().equals("ADD_CHILD_N15M_6M")) {
          values.setAddN15MChild6m(df.format(Float.parseFloat(parameters.getValue()) * 100));
        } else if (parameters.getName().equals("ADD_CHILD_N15M_2Y")) {
          values.setAddN15MChild2y(df.format(Float.parseFloat(parameters.getValue()) * 100));
        } else if (parameters.getName().equals("ADD_CHILD_N15M_6Y")) {
          values.setAddN15MChild6y(df.format(Float.parseFloat(parameters.getValue()) * 100));
        } else if (parameters.getName().equals("ADD_CHILD_N15P_6M")) {
          values.setAddN15PChild6m(df.format(Float.parseFloat(parameters.getValue()) * 100));
        } else if (parameters.getName().equals("ADD_CHILD_N15P_2Y")) {
          values.setAddN15PChild2y(df.format(Float.parseFloat(parameters.getValue()) * 100));
        } else if (parameters.getName().equals("ADD_CHILD_N15P_6Y")) {
          values.setAddN15PChild6y(df.format(Float.parseFloat(parameters.getValue()) * 100));
        } else if (parameters.getName().equals("CMI")) {
          values.setCmi(df.format(Float.parseFloat(parameters.getValue()) * 100));
        } else if (parameters.getName().equals("CMI12")) {
          values.setCmi12(df.format(Float.parseFloat(parameters.getValue()) * 100));
        } else if (parameters.getName().equals("CMI13")) {
          values.setCmi13(df.format(Float.parseFloat(parameters.getValue()) * 100));
        } else if (parameters.getName().equals("CMI14")) {
          values.setCmi14(df.format(Float.parseFloat(parameters.getValue()) * 100));
        } else if (parameters.getName().equals("OL")) {
          values.setOutlyingIslands(df.format(Float.parseFloat(parameters.getValue()) * 100));
        }
      }
    }

    return values;
  }

  public String newDRGValues(Date sDate, Date eDate, DRGRelatedValues values) {
    List<PARAMETERS> list = parametersDao.findByNameAndStartDate("SPR", sDate);
    if (list != null && list.size() > 0) {
      return "生效日已有存在的DRG相關參數設定";
    }
    list = parametersDao.findByNameAndStartDateGreaterThanAndEndDateLessThan("SPR", sDate, eDate);
    if (list != null && list.size() > 0) {
      return "生效日內已有存在的DRG相關參數設定";
    }
    list = parametersDao.findByCatAndStartDateLessThanEqualAndEndDateGreaterThanEqual("DRG", sDate,
        eDate);
    if (list != null && list.size() > 0) {
      moveEndDateInAdvance(list, sDate);
    }
    list = parametersDao.findByCatAndStartDateLessThanEqualAndEndDateGreaterThanEqual("DRG", sDate,
        sDate);
    if (list != null && list.size() > 0) {
      moveEndDateInAdvance(list, sDate);
    }
    saveNewParameter("SPR", String.valueOf(values.getSpr()), sDate, eDate,
        DATA_TYPE.INT.ordinal());
    saveNewParameter("ADD_HOSP_LEVEL_1", values.getAddHospLevel1(), sDate, eDate,
        DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_HOSP_LEVEL_2", values.getAddHospLevel2(), sDate, eDate,
        DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_HOSP_LEVEL_3", values.getAddHospLevel3(), sDate, eDate,
        DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_15_6M", values.getAdd15Child6m(), sDate, eDate,
        DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_15_2Y", values.getAdd15Child2y(), sDate,
        eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_15_6Y", values.getAdd15Child6y(), sDate,
        eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_N15M_6M", values.getAddN15MChild6m(), sDate,
        eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_N15M_2Y", values.getAddN15MChild2y(), sDate, eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_N15M_6Y", values.getAddN15MChild6y(), sDate, eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_N15P_6M", values.getAddN15PChild6m(), sDate,
        eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_N15P_2Y", values.getAddN15PChild2y(), sDate, eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_N15P_6Y", values.getAddN15PChild6y(), sDate, eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("CMI", values.getCmi(), sDate, eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("CMI12", values.getCmi12(), sDate, eDate,
        DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("CMI13", values.getCmi13(), sDate, eDate,
        DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("CMI14", values.getCmi14(), sDate, eDate,
        DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("OL", values.getOutlyingIslands(), sDate, eDate,
        DATA_TYPE.FLOAT.ordinal());

    return null;
  }

  private void saveNewParameter(String name, String value, Date sDate, Date eDate,
      int dataType) {
    String newValue = value;
    if (dataType == DATA_TYPE.FLOAT.ordinal()) {
      DecimalFormat df = new DecimalFormat("#.###");
      double d = 0;
      try {
        d = Double.parseDouble(value) / (double) 100;
      } catch (NumberFormatException e) {
        d = 0;
        e.printStackTrace();
      }
      newValue = df.format(d);
    }
    PARAMETERS p = new PARAMETERS("DRG", name, newValue, dataType, DRG_SETTING_NOTE.get(name));
    p.setEndDate(eDate);
    p.setStartDate(sDate);
    p.setUpdateAt(new Date());
    parametersDao.save(p);
  }

  /**
   * 將參數的結束日往前移一天
   * 
   * @param list
   * @param cal
   */
  private void moveEndDateInAdvance(List<PARAMETERS> list, Date endDate) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(endDate);
    cal.add(Calendar.DAY_OF_YEAR, -1);
    for (PARAMETERS parameters : list) {
      if (parameters.getStartDate().before(cal.getTime())) {
        parameters.setEndDate(cal.getTime());
        parametersDao.save(parameters);
      }
    }
  }

  /**
   * 將參數的結束日往前移一天
   * 
   * @param list
   * @param cal
   */
  private void moveEndDateInAdvanceCodeThreshold(List<CODE_THRESHOLD> list, Date endDate) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(endDate);
    cal.add(Calendar.DAY_OF_YEAR, -1);
    for (CODE_THRESHOLD ct : list) {
      if (ct.getStartDate().before(cal.getTime())) {
        ct.setEndDate(cal.getTime());
        codeThresholdDao.save(ct);
      }
    }
  }
  
  /**
   * 將參數的結束日往前移一天
   * 
   * @param list
   * @param cal
   */
  private void moveEndDateInAdvanceAssignedPoint(List<ASSIGNED_POINT> list, Date endDate) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(endDate);
    cal.add(Calendar.DAY_OF_YEAR, -1);
    for (ASSIGNED_POINT ap : list) {
      if (ap.getStartDate().before(cal.getTime())) {
        ap.setEndDate(cal.getTime());
        assignedPointDao.save(ap);
      }
    }
  }
  
  /**
   * 將參數的結束日往前移一天
   * 
   * @param list
   * @param cal
   */
  private void moveEndDateInAdvanceCodeConflict(List<CODE_CONFLICT> list, Date endDate) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(endDate);
    cal.add(Calendar.DAY_OF_YEAR, -1);
    for (CODE_CONFLICT cc : list) {
      if (cc.getStartDate().before(cal.getTime())) {
        cc.setEndDate(cal.getTime());
        codeConflictDao.save(cc);
      }
    }
  }

  public String updateDRGValue(DRGRelatedValues values) {
    if (values == null || values.getId() == null) {
      return "id值不可為空";
    }
    SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
    Date sDate = null;
    Date eDate = null;

    try {
      sDate = sdf.parse(values.getStartDate());
      eDate = sdf.parse(values.getEndDate());
      if (eDate.before(sDate)) {
        return "訖日不可小於起日";
      }
    } catch (ParseException e) {
      return "日期格式有誤";
    }

    List<PARAMETERS> list = parametersDao.findByNameOrderByStartDateDesc("SPR");
    if (list != null && list.size() > 0) {
      for (PARAMETERS p : list) {
        if (p.getId().longValue() == values.getId().longValue()) {
          continue;
        }
        if (p.getStartDate().getTime() <= sDate.getTime()
            && p.getEndDate().getTime() >= sDate.getTime()) {
          return "生效日已有存在的DRG相關參數設定";
        } else if (p.getStartDate().getTime() >= sDate.getTime()
            && p.getEndDate().getTime() <= eDate.getTime()) {
          return "生效日已有存在的DRG相關參數設定";
        } else if (p.getStartDate().getTime() <= eDate.getTime()
            && p.getEndDate().getTime() >= eDate.getTime()) {
          return "生效日已有存在的DRG相關參數設定";
        }
      }
    }
    // List<PARAMETERS> list = parametersDao.findByNameAndStartDate("SPR", sDate);
    // if (list != null && list.size() > 0) {
    // for (PARAMETERS p : list) {
    // if (p.getId().longValue() != values.getId().longValue()) {
    // return "生效日已有存在的DRG相關參數設定";
    // }
    // }
    // }
    // list = parametersDao.findByNameAndStartDateGreaterThanAndEndDateLessThan("SPR", sDate,
    // eDate);
    // if (list != null && list.size() > 0) {
    // for (PARAMETERS p : list) {
    // if (p.getId().longValue() != values.getId().longValue()) {
    // return "生效日內已有存在的DRG相關參數設定";
    // }
    // }
    // }
    //
    // list = parametersDao.findByNameAndStartDateLessThanAndEndDateGreaterThan("SPR", eDate,
    // eDate);
    // if (list != null && list.size() > 0) {
    // for (PARAMETERS p : list) {
    // if (p.getId().longValue() != values.getId().longValue()) {
    // return "生效日內已有存在的DRG相關參數設定";
    // }
    // }
    // }
    //
    // list = parametersDao.findByNameAndStartDateLessThanAndEndDateGreaterThan("SPR", sDate,
    // sDate);
    // if (list != null && list.size() > 0) {
    // for (PARAMETERS p : list) {
    // if (p.getId().longValue() != values.getId().longValue()) {
    // return "生效日內已有存在的DRG相關參數設定";
    // }
    // }
    // }

    Optional<PARAMETERS> optional = parametersDao.findById(values.getId());
    if (optional.isPresent()) {
      PARAMETERS p = optional.get();
      list = parametersDao.findByCatAndStartDateEquals("DRG", p.getStartDate());
      if (list != null && list.size() > 0) {
        DecimalFormat df = new DecimalFormat("#.###");

        updateDRGValues(list, sDate, eDate, "SPR", String.valueOf(values.getSpr()),
            DATA_TYPE.INT.ordinal());
        updateDRGValues(list, sDate, eDate, "ADD_HOSP_LEVEL_1",
            df.format(Double.parseDouble(values.getAddHospLevel1()) / (double) 100),
            DATA_TYPE.INT.ordinal());
        updateDRGValues(list, sDate, eDate, "ADD_HOSP_LEVEL_2",
            df.format(Double.parseDouble(values.getAddHospLevel2()) / (double) 100),
            DATA_TYPE.FLOAT.ordinal());
        updateDRGValues(list, sDate, eDate, "ADD_HOSP_LEVEL_3",
            df.format(Double.parseDouble(values.getAddHospLevel3()) / (double) 100),
            DATA_TYPE.FLOAT.ordinal());
        updateDRGValues(list, sDate, eDate, "ADD_CHILD_15_6M",
            df.format(Double.parseDouble(values.getAdd15Child6m()) / (double) 100),
            DATA_TYPE.FLOAT.ordinal());
        updateDRGValues(list, sDate, eDate, "ADD_CHILD_15_2Y",
            df.format(Double.parseDouble(values.getAdd15Child2y()) / (double) 100),
            DATA_TYPE.FLOAT.ordinal());
        updateDRGValues(list, sDate, eDate, "ADD_CHILD_15_6Y",
            df.format(Double.parseDouble(values.getAdd15Child6y()) / (double) 100),
            DATA_TYPE.FLOAT.ordinal());
        updateDRGValues(list, sDate, eDate, "ADD_CHILD_N15M_6M",
            df.format(Double.parseDouble(values.getAddN15MChild6m()) / (double) 100),
            DATA_TYPE.FLOAT.ordinal());
        updateDRGValues(list, sDate, eDate, "ADD_CHILD_N15M_2Y",
            df.format(Double.parseDouble(values.getAddN15MChild2y()) / (double) 100),
            DATA_TYPE.FLOAT.ordinal());
        updateDRGValues(list, sDate, eDate, "ADD_CHILD_N15M_6Y",
            df.format(Double.parseDouble(values.getAddN15MChild6y()) / (double) 100),
            DATA_TYPE.FLOAT.ordinal());
        updateDRGValues(list, sDate, eDate, "ADD_CHILD_N15P_6M",
            df.format(Double.parseDouble(values.getAddN15PChild6m()) / (double) 100),
            DATA_TYPE.FLOAT.ordinal());
        updateDRGValues(list, sDate, eDate, "ADD_CHILD_N15P_2Y",
            df.format(Double.parseDouble(values.getAddN15PChild2y()) / (double) 100),
            DATA_TYPE.FLOAT.ordinal());
        updateDRGValues(list, sDate, eDate, "ADD_CHILD_N15P_6Y",
            df.format(Double.parseDouble(values.getAddN15PChild6y()) / (double) 100),
            DATA_TYPE.FLOAT.ordinal());
        updateDRGValues(list, sDate, eDate, "CMI",
            df.format(Double.parseDouble(values.getCmi()) / (double) 100),
            DATA_TYPE.FLOAT.ordinal());
        updateDRGValues(list, sDate, eDate, "CMI12",
            df.format(Double.parseDouble(values.getCmi12()) / (double) 100),
            DATA_TYPE.FLOAT.ordinal());
        updateDRGValues(list, sDate, eDate, "CMI13",
            df.format(Double.parseDouble(values.getCmi13()) / (double) 100),
            DATA_TYPE.FLOAT.ordinal());
        updateDRGValues(list, sDate, eDate, "CMI14",
            df.format(Double.parseDouble(values.getCmi14()) / (double) 100),
            DATA_TYPE.FLOAT.ordinal());
        updateDRGValues(list, sDate, eDate, "OL",
            df.format(Double.parseDouble(values.getOutlyingIslands()) / (double) 100),
            DATA_TYPE.FLOAT.ordinal());
      }
    } else {
      return "id 不存在";
    }
    return null;
  }

  private void updateDRGValues(List<PARAMETERS> list, Date sDate, Date eDate, String name,
      String value, int dataType) {
    for (PARAMETERS parameters : list) {
      if (parameters.getName().equals(name)) { 
        parameters.setValue(value);
        parameters.setStartDate(sDate);
        parameters.setEndDate(eDate);
        parametersDao.save(parameters);
        return;
      }
    }
    PARAMETERS p = new PARAMETERS("DRG", name, value, dataType, DRG_SETTING_NOTE.get(name));
    p.setEndDate(eDate);
    p.setStartDate(sDate);
    p.setUpdateAt(new Date());
    parametersDao.save(p);
  }

  public InfectiousListResponse getInfectious(String code, String cat, String orderBy, Boolean asc,
      int perPage, int page) {
    InfectiousListResponse result = new InfectiousListResponse();

    Specification<CODE_TABLE> spec = new Specification<CODE_TABLE>() {

      private static final long serialVersionUID = 2L;

      public Predicate toPredicate(Root<CODE_TABLE> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (code != null && code.length() > 0) {
          predicate.add(cb.like(root.get("code"), code.toUpperCase() + "%"));
        }
        if (cat != null && cat.length() > 0) {
          predicate.add(cb.equal(root.get("parentCode"), cat));
        }
        predicate.add(cb.equal(root.get("cat"), INFECTIOUS));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));

        List<Order> orderList = new ArrayList<Order>();
        if (orderBy != null && asc != null) {
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
          query.orderBy(orderList);
        } else {
          orderList.add(cb.asc(root.get("parentCode")));
          orderList.add(cb.asc(root.get("code")));
          query.orderBy(orderList);
        }
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
    long total = codeTableDao.count(spec);
    Page<CODE_TABLE> pages = codeTableDao.findAll(spec, PageRequest.of(page, perPage));
    List<InfectiousPayload> list = new ArrayList<InfectiousPayload>();
    if (pages != null && pages.getTotalElements() > 0) {
      for (CODE_TABLE ct : pages) {
        list.add(new InfectiousPayload(ct));
      }
    }
    result.setCount((int) total);
    result.setTotalPage(Utility.getTotalPage((int) total, perPage));
    result.setData(list);
    return result;
  }

  public String updateInfectiousStatus(String icd, boolean enable) {
    List<CODE_TABLE> ctList = codeTableDao.findByCodeAndCat(icd.toUpperCase(), INFECTIOUS);
    if (ctList == null || ctList.size() == 0) {
      return "ICD代碼 " + icd + " 不存在";
    }
    CODE_TABLE ct = ctList.get(0);
    if (ct.getRemark() == null && enable) {
      // 都是 enable 狀態，不處理
      return null;
    }
    if (ct.getRemark() == null && !enable) {
      ct.setRemark("inactive");
      codeTableDao.save(ct);
      deleteIntelligent(INTELLIGENT_REASON.INFECTIOUS.value(), ct.getCode(), null);
      return null;
    } else if (ct.getRemark() != null && enable) {
      ct.setRemark(null);
      codeTableDao.save(ct);
    }
    recalculateInfectiousByThread(ct);
    return null;
  }

  public RareICDListResponse getRareICD(String icd, String orderBy, Boolean asc, int perPage,
      int page) {
    RareICDListResponse result = new RareICDListResponse();

    Specification<CODE_THRESHOLD> spec = new Specification<CODE_THRESHOLD>() {

      private static final long serialVersionUID = 3L;

      public Predicate toPredicate(Root<CODE_THRESHOLD> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        predicate.add(cb.equal(root.get("codeType"), new Integer(RareICDPayload.CODE_TYPE_ICD)));
        if (icd != null && icd.length() > 0) {
          predicate.add(cb.like(root.get("code"), icd.toUpperCase() + "%"));
        }

        if (predicate.size() > 0) {
          Predicate[] pre = new Predicate[predicate.size()];
          query.where(predicate.toArray(pre));
        }

        List<Order> orderList = new ArrayList<Order>();
        if (orderBy != null && asc != null) {
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
          query.orderBy(orderList);
        } else {
          orderList.add(cb.desc(root.get("startDate")));
          orderList.add(cb.asc(root.get("code")));
          query.orderBy(orderList);
        }
        return query.getRestriction();
      }
    };
    long total = codeThresholdDao.count(spec);
    Page<CODE_THRESHOLD> pages = codeThresholdDao.findAll(spec, PageRequest.of(page, perPage));
    List<RareICDListPayload> list = new ArrayList<RareICDListPayload>();
    if (pages != null && pages.getTotalElements() > 0) {
      for (CODE_THRESHOLD ct : pages) {
        list.add(new RareICDListPayload(ct));
      }
    }
    result.setCount((int) total);
    result.setTotalPage(Utility.getTotalPage((int) total, perPage));
    result.setData(list);
    return result;
  }

  public RareICDPayload getRareICDById(String id) {
    Optional<CODE_THRESHOLD> optional = codeThresholdDao.findById(Long.parseLong(id));
    if (optional.isPresent()) {
      return new RareICDPayload(optional.get());
    }
    RareICDPayload result = new RareICDPayload();
    result.setName("id不存在");
    return result;
  }

  public String newRareICD(RareICDPayload request) {
    CODE_THRESHOLD ct = request.toDB();
    if (ct.getEndDate().before(ct.getStartDate())) {
      return "失效日不可早於生效日！";
    }
    if (ct.getEndDate().getTime() == ct.getStartDate().getTime()) {
      return "失效日不可等於生效日！";
    }
    List<CODE_THRESHOLD> list = codeThresholdDao.findByCodeTypeAndCodeOrderByStartDateDesc(
        new Integer(RareICDPayload.CODE_TYPE_ICD), request.getCode().toUpperCase());
    if (checkTimeOverwrite(list, ct, false)) {
      return "該時段有相同的罕見ICD代碼！";
    }
    ct.setUpdateAt(new Date());
    ct = codeThresholdDao.save(ct);
    recalculateRareICDByThread(ct);
    return null;
  }

  public String updateRareICDStatus(long id, boolean isEnable) {
    Optional<CODE_THRESHOLD> optional = codeThresholdDao.findById(id);
    if (!optional.isPresent()) {
      return "id:" + id + " 不存在";
    }
    CODE_THRESHOLD ct = optional.get();
    if ((ct.getStatus().intValue() == 1 && isEnable)
        || (ct.getStatus().intValue() == 0 && !isEnable)) {
      // 狀態相同，不處理
      return null;
    }
    ct.setStatus(isEnable ? 1 : 0);
    ct.setUpdateAt(new Date());
    codeThresholdDao.save(ct);
    recalculateRareICDByThread(ct);
    return null;
  }

  public String updateRareICD(RareICDPayload request) {
    if (request.getId() != null) {
      Optional<CODE_THRESHOLD> optional = codeThresholdDao.findById(request.getId());
      if (optional.isPresent()) {
        CODE_THRESHOLD ct = optional.get();
        deleteIntelligent(INTELLIGENT_REASON.RARE_ICD.value(), ct.getCode(), null);
      }
    }
    CODE_THRESHOLD db = request.toDB();
    if (db.getEndDate().before(db.getStartDate())) {
      return "失效日不可早於生效日！";
    }
    List<CODE_THRESHOLD> list = codeThresholdDao
        .findByCodeTypeAndCodeOrderByStartDateDesc(RareICDPayload.CODE_TYPE_ICD, request.getCode());
    if (checkTimeOverwrite(list, db, true)) {
      return "該時段有相同的罕見ICD代碼！";
    }

    db.setUpdateAt(new Date());
    codeThresholdDao.save(db);
    recalculateRareICDByThread(db);
    return null;
  }

  public String deleteCodeThreshold(String id) {
    Optional<CODE_THRESHOLD> optional = codeThresholdDao.findById(Long.parseLong(id));
    if (optional.isPresent()) {
      CODE_THRESHOLD ct = optional.get();
      int conditionCode = 0;
      if (ct.getCodeType().intValue() == 1) {
        conditionCode = INTELLIGENT_REASON.RARE_ICD.value();
      } else if (ct.getCodeType().intValue() == 2) {
        conditionCode = INTELLIGENT_REASON.HIGH_RATIO.value();
      } else if (ct.getCodeType().intValue() == 3) {
        conditionCode = INTELLIGENT_REASON.OVER_AMOUNT.value();
      }
        
      deleteIntelligent(conditionCode, ct.getCode(), null);
      codeThresholdDao.deleteById(ct.getId());
      return null;
    }
    return "id不存在";
  }

  public HighRatioOrderListResponse getHighRatioOrder(String code, String inhCode,
      boolean isHighRatio, String orderBy, Boolean asc, int perPage, int page) {
    HighRatioOrderListResponse result = new HighRatioOrderListResponse();

    Specification<CODE_THRESHOLD> spec = new Specification<CODE_THRESHOLD>() {

      private static final long serialVersionUID = 3L;

      public Predicate toPredicate(Root<CODE_THRESHOLD> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        int codeType = isHighRatio ? RareICDPayload.CODE_TYPE_ORDER : RareICDPayload.CODE_TYPE_DRUG;
        predicate.add(cb.equal(root.get("codeType"), new Integer(codeType)));
        if (code != null && code.length() > 0) {
          predicate.add(cb.like(root.get("code"), code.toUpperCase() + "%"));
        }
        if (inhCode != null && inhCode.length() > 0) {
          predicate.add(cb.like(root.get("inhCode"), inhCode.toUpperCase() + "%"));
        }

        if (predicate.size() > 0) {
          Predicate[] pre = new Predicate[predicate.size()];
          query.where(predicate.toArray(pre));
        }

        List<Order> orderList = new ArrayList<Order>();
        if (orderBy != null && asc != null) {
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
          query.orderBy(orderList);
        } else {
          orderList.add(cb.desc(root.get("startDate")));
          query.orderBy(orderList);
        }
        // 需再加個 order by id，否則一有更新，資料顯示順序會和未更新時不一樣
        orderList.add(cb.desc(root.get("id")));

        return query.getRestriction();
      }
    };
    long total = codeThresholdDao.count(spec);
    if (total < perPage && page > 0) {
      page = 0;
    }
    Page<CODE_THRESHOLD> pages = codeThresholdDao.findAll(spec, PageRequest.of(page, perPage));
    List<HighRatioOrderListPayload> list = new ArrayList<HighRatioOrderListPayload>();
    if (pages != null && pages.getTotalElements() > 0) {
      for (CODE_THRESHOLD ct : pages) {
        list.add(new HighRatioOrderListPayload(ct));
      }
    }
    result.setCount((int) total);
    result.setTotalPage(Utility.getTotalPage((int) total, perPage));
    result.setData(list);
    return result;
  }

  public HighRatioOrder getHighRatioOrderById(String id) {
    Optional<CODE_THRESHOLD> optional = codeThresholdDao.findById(Long.parseLong(id));
    if (optional.isPresent()) {
      return new HighRatioOrder(optional.get());
    }
    HighRatioOrder result = new HighRatioOrder();
    result.setName("id不存在");
    return result;
  }

  public String newHighRatioOrder(HighRatioOrder request, boolean isOrder) {
    int codeType = isOrder ? RareICDPayload.CODE_TYPE_ORDER : RareICDPayload.CODE_TYPE_DRUG;
    if (request.getInhCode() != null) {
      request.setInhCode(request.getInhCode().trim().toUpperCase());
    }
    if (request.getCode() != null) {
      request.setCode(request.getCode().trim().toUpperCase());
    }
    CODE_THRESHOLD ct = request.toDB(codeType);
    if (ct.getEndDate().before(ct.getStartDate())) {
      return "失效日不可早於生效日！";
    }
    if (ct.getEndDate().getTime() == ct.getStartDate().getTime()) {
      return "失效日不可等於生效日！";
    }
    List<CODE_THRESHOLD> list = null;
    if (request.getCode() == null) {
      list = codeThresholdDao.findByCodeTypeAndInhCodeOrderByStartDateDesc(new Integer(codeType), request.getInhCode().toUpperCase());
    } else {
      list = codeThresholdDao.findByCodeTypeAndCodeOrderByStartDateDesc(
        new Integer(codeType), request.getCode().toUpperCase());
    }
    if (checkTimeOverwrite(list, ct, false)) {
      return isOrder ? "該時段有相同的應用比例偏高醫令！" : "該時段有相同的特別用量藥品、衛品！";
    }

    ct.setUpdateAt(new Date());
    ct = codeThresholdDao.save(ct);
    recalculateHighRatioAndOverAmountByThread(ct, isOrder);
    return null;
  }

  /**
   * 
   * @param request
   * @param isOrder true: HIGH_RATIO應用比例偏高，false: OVER_AMOUNT 特別用量
   * @return
   */
  public String updateHighRatioOrder(HighRatioOrder request, boolean isOrder) {
    if (request.getInhCode() != null) {
      request.setInhCode(request.getInhCode().trim().toUpperCase());
    }
    if (request.getCode() != null) {
      request.setCode(request.getCode().trim().toUpperCase());
    }
    Optional<CODE_THRESHOLD> optional = codeThresholdDao.findById(request.getId());
    if (!optional.isPresent()) {
      return "id:" + request.getId() + "不存在";
    }
    CODE_THRESHOLD old = optional.get();
    CODE_THRESHOLD db = request.toDB(old.getCodeType());
    if (db.getEndDate().before(db.getStartDate())) {
      return "失效日不可早於生效日！";
    }
    List<CODE_THRESHOLD> list = null;
    if (request.getCode() == null) {
      list = codeThresholdDao.findByCodeTypeAndInhCodeOrderByStartDateDesc(old.getCodeType(), request.getInhCode());
    } else {
      list = codeThresholdDao.findByCodeTypeAndCodeOrderByStartDateDesc(old.getCodeType(), request.getCode());
    }
    if (checkTimeOverwrite(list, db, true)) {
      return "該時段有相同的應用比例偏高支付代碼";
    }
    if (isOrder) {
      is.removeOldHighRatioMR(old, INTELLIGENT_REASON.HIGH_RATIO.value(), true);
    } else {
      is.removeOldHighRatioMR(old, INTELLIGENT_REASON.OVER_AMOUNT.value(), true);
    }
    db.setUpdateAt(new Date());
    db = codeThresholdDao.save(db);

    if (db.getStatus() != null && db.getStatus().intValue() == 1) {
      recalculateHighRatioAndOverAmountByThread(db, isOrder);
    }
    return null;
  }
  
  public String updateHighRatioOrder(long id, boolean enable) {
    Optional<CODE_THRESHOLD> optional = codeThresholdDao.findById(id);
    if (!optional.isPresent()) {
      return "id不存在";
    }
    CODE_THRESHOLD ct = optional.get();
    ct.setStatus(enable ? 1 : 0);
    ct.setUpdateAt(new Date());
    ct = codeThresholdDao.save(ct);
    if (!enable) {
      if (ct.getCodeType().intValue() == 2) {
        waitIfIntelligentRunning(INTELLIGENT_REASON.HIGH_RATIO.value());
        is.removeOldHighRatioMR(ct, INTELLIGENT_REASON.HIGH_RATIO.value(), true);
      } else if (ct.getCodeType().intValue() == 3) {
        waitIfIntelligentRunning(INTELLIGENT_REASON.OVER_AMOUNT.value());
        is.removeOldHighRatioMR(ct, INTELLIGENT_REASON.OVER_AMOUNT.value(), true);
      }
      return null;
    }
    recalculateHighRatioAndOverAmountByThread(ct, ct.getCodeType().intValue() == RareICDPayload.CODE_TYPE_ORDER);
    return null;
  }

  /**
   * 檢查是否有同時段的設定
   * 
   * @param list
   * @param db
   * @param checkSameId
   * @return
   */
  public boolean checkTimeOverwrite(List<CODE_THRESHOLD> list, CODE_THRESHOLD db,
      boolean checkSameId) {
    List<CODE_THRESHOLD> needProcessList = new ArrayList<CODE_THRESHOLD>();
    for (CODE_THRESHOLD rareIcd : list) {
      if (checkSameId) {
        if (rareIcd.getId().longValue() == db.getId()) {
          continue;
        }
      }
      if (db.getStartDate().getTime() == rareIcd.getStartDate().getTime()) {
        return true;
      }
      if (db.getStartDate().getTime() >= rareIcd.getStartDate().getTime()
          && db.getStartDate().getTime() <= rareIcd.getEndDate().getTime()) {
        needProcessList.add(rareIcd);
        continue;
      }
      if (db.getEndDate().getTime() <= rareIcd.getEndDate().getTime()
          && db.getEndDate().getTime() >= rareIcd.getStartDate().getTime()) {
        return true;
      }
      if (db.getEndDate().getTime() >= rareIcd.getEndDate().getTime()
          && db.getStartDate().getTime() <= rareIcd.getStartDate().getTime()) {
        return true;
      }
    }
    moveEndDateInAdvanceCodeThreshold(needProcessList, db.getStartDate());
    return false;
  }

  /**
   * 檢查該參數設定是否有和其他時段重疊
   * 
   * @param list
   * @param startDate
   * @param endDate
   * @param id
   * @return true:有重疊，false:無
   */
  public boolean checkTimeOverlap(List<PARAMETERS> list, long startDate, long endDate, long id) {
    List<PARAMETERS> needProcessList = new ArrayList<PARAMETERS>();
    for (PARAMETERS p : list) {
      if (id > 0 && p.getId().longValue() == id) {
        continue;
      }
      if (startDate == p.getStartDate().getTime()) {
        return true;
      }
      if (startDate >= p.getStartDate().getTime() && startDate <= p.getEndDate().getTime()) {
        if (id == 0) {
          needProcessList.add(p);
          continue;
        } else {
          return true;
        }
      }
      if (endDate <= p.getEndDate().getTime() && endDate >= p.getStartDate().getTime()) {
        return true;
      }
      if (endDate >= p.getEndDate().getTime() && startDate <= p.getStartDate().getTime()) {
        return true;
      }
    }
    moveEndDateInAdvance(needProcessList, new Date(startDate));
    return false;
  }

  /**
   * 檢查該參數設定是否有和其他時段重疊
   * 
   * @param list
   * @param startDate
   * @param endDate
   * @param id
   * @return true:有重疊，false:無
   */
  public boolean checkTimeOverwriteAssignedPoint(List<ASSIGNED_POINT> list, long startDate,
      long endDate, long id) {
    List<ASSIGNED_POINT> needProcessList = new ArrayList<ASSIGNED_POINT>();
    for (ASSIGNED_POINT p : list) {
      if (id > 0 && p.getId().longValue() == id) {
        continue;
      }
      if (startDate == p.getStartDate().getTime()) {
        return true;
      }
      if (startDate >= p.getStartDate().getTime() && startDate <= p.getEndDate().getTime()) {
        needProcessList.add(p);
        continue;
      }
      if (endDate <= p.getEndDate().getTime() && endDate >= p.getStartDate().getTime()) {
        return true;
      }
      if (endDate >= p.getEndDate().getTime() && startDate <= p.getStartDate().getTime()) {
        return true;
      }
    }
    moveEndDateInAdvanceAssignedPoint(needProcessList, new Date(startDate));
    return false;
  }

  /**
   * 檢查該參數設定是否有和其他時段重疊
   * 
   * @param list
   * @param startDate
   * @param endDate
   * @param id
   * @return true:有重疊，false:無
   */
  public boolean checkTimeOverwriteCodeConfilct(List<CODE_CONFLICT> list, long startDate,
      long endDate, Long id) {
    if (list == null) {
      return false;
    }
    List<CODE_CONFLICT> needProcessList = new ArrayList<CODE_CONFLICT>();
    for (CODE_CONFLICT p : list) {
      if (id != null && id.longValue()> 0 && p.getId().longValue() == id.longValue()) {
        continue;
      }
      if (startDate == p.getStartDate().getTime()) {
        return true;
      }
      if (startDate >= p.getStartDate().getTime() && startDate <= p.getEndDate().getTime()) {
        needProcessList.add(p);
        continue;
      }
      if (endDate <= p.getEndDate().getTime() && endDate >= p.getStartDate().getTime()) {
        return true;
      }
      if (endDate >= p.getEndDate().getTime() && startDate <= p.getStartDate().getTime()) {
        return true;
      }
    }
    moveEndDateInAdvanceCodeConflict(needProcessList, new Date(startDate));
    return false;
  }
  
  public SameATCListResponse getSameATCFromPayCode(String code, String inhCode, String atc,
      String orderBy, Boolean asc, int perPage, int page) {
    SameATCListResponse result = new SameATCListResponse();
    List<SameATCListPayload> data = new ArrayList<SameATCListPayload>();
    // 取得有啟用的同性質藥物開立清單

    Specification<PAY_CODE> spec = new Specification<PAY_CODE>() {

      private static final long serialVersionUID = 5L;

      public Predicate toPredicate(Root<PAY_CODE> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (code != null && code.length() > 0) {
          predicate.add(cb.like(root.get("code"), code.toUpperCase() + "%"));
        }
        if (inhCode != null && inhCode.length() > 0) {
          predicate.add(cb.like(root.get("inhCode"), inhCode.toUpperCase() + "%"));
        }
        if (atc != null && atc.length() > 0) {
          predicate.add(cb.like(root.get("atc"), atc.toUpperCase() + "%"));
        } else {
//          predicate.add(cb.isNotNull(root.get("atc")));
          predicate.add(cb.greaterThanOrEqualTo(cb.length(root.get("atc")), 5));
        }

        if (predicate.size() > 0) {
          Predicate[] pre = new Predicate[predicate.size()];
          query.where(predicate.toArray(pre));
        }

        List<Order> orderList = new ArrayList<Order>();
        if (orderBy != null && asc != null) {
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
        } else {
          orderList.add(cb.asc(root.get("atc")));
          orderList.add(cb.asc(root.get("code")));
        }
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
    long total = payCodeDao.count(spec);
    Page<PAY_CODE> pages = payCodeDao.findAll(spec, PageRequest.of(page, perPage));
    if (pages != null && pages.getTotalElements() > 0) {
      for (PAY_CODE pc : pages) {
        data.add(new SameATCListPayload(pc));
      }
    }
    result.setCount((int) total);
    result.setTotalPage(Utility.getTotalPage((int) total, perPage));
    result.setData(data);

    return result;
  }

  public String updateSameATC(Long id, boolean enable) {
    Optional<PAY_CODE> optional = payCodeDao.findById(id);
    if (optional == null || !optional.isPresent()) {
      return "id: " + id + " 不存在";
    }
    PAY_CODE pc = optional.get();
    if (pc.getSameAtc() == null) {
      pc.setSameAtc(0);
    }
    if (pc.getSameAtc().intValue() == 0 && !enable) {
      // 都是 disable 狀態，不處理
      return null;
    }
    if (pc.getSameAtc().intValue() == 1 && enable) {
      // 都是 enable 狀態，不處理
      return null;
    }
    pc.setSameAtc(enable ? new Integer(1) : new Integer(0));
    pc.setUpdateAt(new Date());
    pc = payCodeDao.save(pc);
    recalculateSameATCByThread(pc, enable);
    return null;
  }

  public CodeConflictListResponse getCodeConflict(String code, String inhCode, String ownCode,
      String orderBy, Boolean asc, int perPage, int page) {
    CodeConflictListResponse result = new CodeConflictListResponse();
    List<CodeConflictListPayload> data = new ArrayList<CodeConflictListPayload>();

    Specification<CODE_CONFLICT> spec = new Specification<CODE_CONFLICT>() {

      private static final long serialVersionUID = 5L;

      public Predicate toPredicate(Root<CODE_CONFLICT> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (code != null && code.length() > 0) {
          predicate.add(cb.like(root.get("code"), code.toUpperCase() + "%"));
        }
        if (inhCode != null && inhCode.length() > 0) {
          predicate.add(cb.like(root.get("inhCode"), inhCode.toUpperCase() + "%"));
        }
        if (ownCode != null && ownCode.length() > 0) {
          predicate.add(cb.like(root.get("ownExpCode"), ownCode.toUpperCase() + "%"));
        }

        if (predicate.size() > 0) {
          Predicate[] pre = new Predicate[predicate.size()];
          query.where(predicate.toArray(pre));
        }

        List<Order> orderList = new ArrayList<Order>();
        if (orderBy != null && asc != null) {
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
        } else {
          orderList.add(cb.desc(root.get("startDate")));
          orderList.add(cb.asc(root.get("inhCode")));
        }
        // 需再加個 order by id，否則一有更新，資料顯示順序會和未更新時不一樣
        orderList.add(cb.desc(root.get("id")));
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
    long total = codeConflictDao.count(spec);
    Page<CODE_CONFLICT> pages = codeConflictDao.findAll(spec, PageRequest.of(page, perPage));
    if (pages != null && pages.getTotalElements() > 0) {
      for (CODE_CONFLICT cc : pages) {
        data.add(new CodeConflictListPayload(cc));
      }
    }
    result.setCount((int) total);
    result.setTotalPage(Utility.getTotalPage((int) total, perPage));
    result.setData(data);
    return result;
  }

  public CodeConflictPayload getCodeConflict(long id) {
    Optional<CODE_CONFLICT> optional = codeConflictDao.findById(new Long(id));
    if (!optional.isPresent()) {
      return null;
    }
    CODE_CONFLICT cc = optional.get();
    return new CodeConflictPayload(cc);
  }

  public String upsertCodeConflict(CodeConflictPayload ccp, boolean isUpdate) {
    List<CODE_CONFLICT> list =
        codeConflictDao.findByCodeAndOwnExpCodeAndCodeType(ccp.getCode(), ccp.getOwnCode(), new Integer(1));
    if (list != null && list.size() > 0) {
      if (checkTimeOverwriteCodeConfilct(list, ccp.getSdate().getTime(),
          ccp.getEdate().getTime(), ccp.getId())) {
        return "該時段有相同的健保項目對應自費項目並存設定！";
      }
    }
    if (isUpdate) {
      Optional<CODE_CONFLICT> optional = codeConflictDao.findById(ccp.getId());
      if (!optional.isPresent()) {
        return "id 不存在";
      }
      CODE_CONFLICT ccDb = optional.get();
      deleteIntelligent(INTELLIGENT_REASON.INH_OWN_EXIST.value(), ccDb.getCode(), null);
    }
    
    CODE_CONFLICT cc = codeConflictDao.save(ccp.toDB());
    recalculateCodeConflictByThread(cc);
    return null;
  }

  public String updateCodeConflict(Long id, boolean enable) {
    Optional<CODE_CONFLICT> optional = codeConflictDao.findById(id);
     
    if (!optional.isPresent()) {
      return "id: " + id + " 不存在";
    }
    CODE_CONFLICT cc = optional.get();
    if (cc.getStatus() == 0 && !enable) {
      // 都是 disable 狀態，不處理
      return null;
    }
    if (cc.getStatus().intValue() == 1 && enable) {
      // 都是 enable 狀態，不處理
      return null;
    }
    deleteIntelligent(INTELLIGENT_REASON.INH_OWN_EXIST.value(), cc.getCode(), null);
    cc.setStatus(enable ? new Integer(1) : new Integer(0));
    cc.setUpdateAt(new Date());
    cc = codeConflictDao.save(cc);
    if (enable) {
      recalculateCodeConflictByThread(cc);  
    }
    return null;
  }

  public String deleteCodeConflict(long id) {
    Optional<CODE_CONFLICT> optional = codeConflictDao.findById(new Long(id));
    if (!optional.isPresent()) {
      return "id不存在";
    }
    CODE_CONFLICT cc = optional.get();
    deleteIntelligent(INTELLIGENT_REASON.INH_OWN_EXIST.value(), cc.getCode(), null);
    codeConflictDao.deleteById(new Long(id));
    return null;
  }

  public String upsertCodeConflictForHighRisk(String code, String ownExpCode, String dataFormat) {
    List<CODE_CONFLICT> list =
        codeConflictDao.findByCodeAndOwnExpCodeAndCodeType(code, ownExpCode, Integer.valueOf(2));
    if (list != null && list.size() > 0) {
      for (CODE_CONFLICT codeConflict : list) {
        if ("00".equals(codeConflict.getDataFormat())) {
          // 不需處理
          return null;
        }
        if (codeConflict.getDataFormat().equals(dataFormat)) {
          // 不需處理
          return null;
        }
        codeConflict.setDataFormat("00");
        codeConflict = codeConflictDao.save(codeConflict);
        recalculateHighRisk(codeConflict, true, dataFormat);
        return null;
      }
    }
    CODE_CONFLICT cc = initialCodeConflictForHighRisk(code, ownExpCode, dataFormat);
    codeConflictDao.save(cc);
    recalculateHighRisk(cc, true, dataFormat);
    return null;
  }
  
  private CODE_CONFLICT initialCodeConflictForHighRisk(String code, String ownExpCode, String dataFormat) {
    CODE_CONFLICT cc = new CODE_CONFLICT();
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
      cc.setStartDate(sdf.parse("2010/01/01"));
      cc.setEndDate(sdf.parse("2099/12/31"));
    } catch (ParseException e) {
    }
    
    cc.setCode(code);
    // 1: 醫令/健保碼，2: ICD 診斷碼
    cc.setCodeType(new Integer(2));
    cc.setDataFormat(dataFormat);
    List<JsonSuggestion> queryList = redisService.query("ICD10-CM", code.toLowerCase(), false);
    //List<JsonSuggestion> queryList = redisService.query(null, code.toLowerCase(), false);
    if (queryList != null) {
      for (JsonSuggestion jsonSuggestion : queryList) {
        if (jsonSuggestion.getId().indexOf(":" + code.toUpperCase()) > 0) {
          cc.setDescChi(jsonSuggestion.getValue());
          break;
        }
      }
    }
    cc.setOwnExpCode(ownExpCode);
    
    queryList = redisService.query(null, ownExpCode.toLowerCase(), false);
    for (JsonSuggestion jsonSuggestion : queryList) {
      if (jsonSuggestion.getLabel().equals(ownExpCode.toLowerCase())) {
        cc.setOwnExpDesc(jsonSuggestion.getValue());
        break;
      }
    }
    cc.setQuantityNh(0);
    cc.setQuantityOwn(0);
    cc.setStatus(1);
    cc.setUpdateAt(new Date());
    return cc;
  }

  public void deleteCodeConflictForHighRisk(DEDUCTED_NOTE dn) {
    Optional<MR> optional = mrDao.findById(dn.getMrId());
    if (!optional.isPresent()) {
      return;
    }
    MR mr = optional.get();
    
    List<MR> sameDeductedOrderMR = mrDao.getSameDeductedOrderMR(dn.getDeductedOrder(), mr.getIcdcm1(), mr.getDataFormat(), mr.getId());
    if (sameDeductedOrderMR != null && sameDeductedOrderMR.size() > 0) {
      // 仍有相同核刪條件的病歷，故不處理
      return;
    }
    List<CODE_CONFLICT> list =
        codeConflictDao.findByCodeAndOwnExpCodeAndCodeType(mr.getIcdcm1(), dn.getDeductedOrder(), new Integer(2));
    if (list != null && list.size() > 0) {
      for (CODE_CONFLICT codeConflict : list) {
        if (XMLConstant.FUNC_TYPE_ALL.equals(codeConflict.getDataFormat())) {
          if (mr.getDataFormat().equals(XMLConstant.DATA_FORMAT_IP)) {
            codeConflict.setDataFormat(XMLConstant.DATA_FORMAT_OP);
          } else if (mr.getDataFormat().equals(XMLConstant.DATA_FORMAT_OP)) {
            codeConflict.setDataFormat(XMLConstant.DATA_FORMAT_IP);
          }
          recalculateHighRisk(codeConflict, false, mr.getDataFormat());
          return;
        }
        if (codeConflict.getDataFormat().equals(mr.getDataFormat())) {
          // 已無相同核刪條件病歷，故將該高風險診斷碼與健保碼組合刪除
          recalculateHighRisk(codeConflict, false, mr.getDataFormat());
          codeConflictDao.deleteById(codeConflict.getId());
          return;
        }
      }
    }
  }
  
  public void recalculateRareICDByThread(CODE_THRESHOLD ct) {
    Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        recalculateRareICD(ct);
        logger.info("recalculateInfectious " + ct.getCode() + " done");
      }
    });
    thread.start();
  }
  
  public int[] getMRMinMaxDate(Date startDate, Date endDate) {
    int[] result  = new int[2];
    Calendar cal = Calendar.getInstance();
    cal.setTime(startDate);
    int adYM = cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH) + 1 ;
    // 目前所有病歷最早的一筆
    String minMR = mrDao.getMinYm();
    if (minMR == null) {
      return new int[0];
    }
    int min = Integer.parseInt(minMR) + 191100;
    result[0] = (min > adYM) ? min : adYM;
    
    cal.setTime(endDate);
    adYM = cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH) + 1 ;
    // 目前所有病歷最新的一筆
    String maxMR = mrDao.getMaxYm();
    int max = Integer.parseInt(maxMR) + 191100;
    result[1] = (max > adYM) ? adYM : max;
    
    return result;
  }
  
  /**
   * 取得DB中符合 date 參數最舊/最新的時間，最舊為當月的1日，最新為當下日期
   * @param date
   * @param isStart
   * @return
   */
  public Calendar getMinMaxCalendar(Date date, boolean isStart) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    if (!isStart) {
      // 取月底
      cal.add(Calendar.MONTH, 1);
      cal.add(Calendar.DAY_OF_YEAR, -1);
    }
    int adYM = cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH) + 1 ;
    // 目前所有病歷最早的一筆
    String mrDate = (isStart) ? mrDao.getMinYm() : mrDao.getMaxYm();
    if (mrDate == null) {
      return null;
    } 
    int mrDateInt = Integer.parseInt(mrDate) + 191100;
    if (isStart) {
      if (mrDateInt > adYM) {
        // 給定date比病歷最舊日期還早，改用病歷最舊日期
        cal.set(Calendar.YEAR, mrDateInt / 100);
        cal.set(Calendar.MONTH, (mrDateInt % 100) - 1);
      }
    } else {
      if (mrDateInt < adYM) {
        // 給定date比病歷最新日期還晚，改用病歷最新日期
        cal.set(Calendar.YEAR, mrDateInt / 100);
        cal.set(Calendar.MONTH, (mrDateInt % 100) - 1);
      }
    }
    return cal;
  }
  
  public void recalculateRareICD(CODE_THRESHOLD ct) {
    waitIfIntelligentRunning(INTELLIGENT_REASON.RARE_ICD.value());
    is.setIntelligentRunning(INTELLIGENT_REASON.RARE_ICD.value(), true);
    
    Calendar calMin = getMinMaxCalendar(ct.getStartDate(), true);
    if (calMin == null) {
      return;
    }
    Calendar calMax =  getMinMaxCalendar(ct.getEndDate(), false);
    String wording1M = getOneValueByName("INTELLIGENT", "RARE_ICD_1M");
    String wording6M = getOneValueByName("INTELLIGENT", "RARE_ICD_6M");
    int chineseYM = calMin.get(Calendar.YEAR) * 100 + calMin.get(Calendar.MONTH) + 1 - 191100;
    do {
      is.calculateRareICD(String.valueOf(chineseYM), ct, wording1M, wording6M);
      calMin.add(Calendar.MONTH, 1);
      if (calMin.after(calMax)) {
        break;
      }
      chineseYM = calMin.get(Calendar.YEAR) * 100 + calMin.get(Calendar.MONTH) + 1 - 191100;
    } while (true);
    is.setIntelligentRunning(INTELLIGENT_REASON.RARE_ICD.value(), false);
  }
  
  public void recalculateInfectiousByThread(CODE_TABLE ct) {
    Thread thread = new Thread(new Runnable() {
      
      @Override
      public void run() {
        recalculateInfectious(ct);
      }
    });
    thread.start();
  }
  
  public void waitIfIntelligentRunning(int conditionCode) {
    while (is.isIntelligentRunning(conditionCode)) {
      try {
        Thread.sleep(1500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void recalculateInfectious(CODE_TABLE ct) {
    waitIfIntelligentRunning(INTELLIGENT_REASON.INFECTIOUS.value());
    is.setIntelligentRunning(INTELLIGENT_REASON.INFECTIOUS.value(), true);
    
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
    String wording = getOneValueByName("INTELLIGENT", "INFECTIOUS");
    do {
      is.calculateInfectious(String.valueOf(adYM - 191100), ct.getCode(), wording, ct.getRemark() == null);
      cal.add(Calendar.MONTH, 1);
      adYM = cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.MONTH) + 1;
      if (adYM > max) {
        break;
      }
    } while (true);
    logger.info("recalculateInfectious " + ct.getCode() + " done");
    is.setIntelligentRunning(INTELLIGENT_REASON.INFECTIOUS.value(), false);
  }
  
  public void recalculateHighRatioAndOverAmountByThread(CODE_THRESHOLD ct, boolean isOrder) {
  
    Thread thread = new Thread(new Runnable() {
      
      @Override
      public void run() {
        recalculateHighRatioAndOverAmount(ct, isOrder);
      }
    });
    thread.start();
  }
  
  /**
   * 重新計算符合特別用量藥材、衛品及應用比例偏高醫令
   */
  public void recalculateHighRatioAndOverAmount(CODE_THRESHOLD ct, boolean isOrder) {
    logger.info("recalculateHighRatio start " + ct.getCode());
    int conditionCode = isOrder ? INTELLIGENT_REASON.HIGH_RATIO.value() : INTELLIGENT_REASON.OVER_AMOUNT.value();
    waitIfIntelligentRunning(conditionCode);
    is.setIntelligentRunning(conditionCode, true);
    
    String wordingHighRatioSingle = getOneValueByName("INTELLIGENT",
        isOrder ? "HIGH_RATIO_SINGLE" : "OVER_AMOUNT_SINGLE");
    String wordingHighRatioTotal = getOneValueByName("INTELLIGENT",
        isOrder ? "HIGH_RATIO_TOTAL" : "OVER_AMOUNT_TOTAL");
    String wordingHighRatio6M = getOneValueByName("INTELLIGENT",
        isOrder ? "HIGH_RATIO_6M" : "OVER_AMOUNT_6M");
    String wordingHighRatio1M = getOneValueByName("INTELLIGENT",
        isOrder ? "HIGH_RATIO_1M" : "OVER_AMOUNT_1M");

    Calendar calMin = getMinMaxCalendar(ct.getStartDate(), true);
    if (calMin == null) {
      return;
    }
    Calendar calMax =  getMinMaxCalendar(ct.getEndDate(), false);
    int chineseYM = calMin.get(Calendar.YEAR) * 100 + calMin.get(Calendar.MONTH) + 1 - 191100;
    do {
      is.calculateHighRatioAndOverAmount(String.valueOf(chineseYM), ct, wordingHighRatioSingle,
          wordingHighRatioTotal, wordingHighRatio6M, wordingHighRatio1M,
          isOrder ? INTELLIGENT_REASON.HIGH_RATIO.value() : INTELLIGENT_REASON.OVER_AMOUNT.value());
      calMin.add(Calendar.MONTH, 1);
      if (calMin.after(calMax)) {
        break;
      }
      chineseYM = calMin.get(Calendar.YEAR) * 100 + calMin.get(Calendar.MONTH) + 1 - 191100;
    } while (true);
    logger.info("recalculateHighRatio " + ct.getCode() + " done");
    is.setIntelligentRunning(conditionCode, false);
  }
  
  public void recalculateCodeConflictByThread(CODE_CONFLICT cc) {
    
    Thread thread = new Thread(new Runnable() {
      
      @Override
      public void run() {
        recalculateCodeConflict(cc, INTELLIGENT_REASON.INH_OWN_EXIST.value());
      }
    });
    thread.start();
  }
  
  /**
   * 重新計算健保項目對應自費項目並存病歷
   * @param cc
   */
  public void recalculateCodeConflict(CODE_CONFLICT cc, int conditionCode) {
    System.out.println("recalculateCodeConflict " + cc.getCode());
    waitIfIntelligentRunning(conditionCode);
    is.setIntelligentRunning(conditionCode, true);
    String wordingName = conditionCode == INTELLIGENT_REASON.INH_OWN_EXIST.value() ? "CODE_CONFLICT" : "HIGH_RISK_WORDING";
    String wording = getOneValueByName("INTELLIGENT", wordingName);
   
    Calendar calMin = getMinMaxCalendar(cc.getStartDate(), true);
    if (calMin == null) {
      return;
    }
    Calendar calMax =  getMinMaxCalendar(cc.getEndDate(), false);
    int chineseYM = calMin.get(Calendar.YEAR) * 100 + calMin.get(Calendar.MONTH) + 1 - 191100;
    do {
      is.calculateCodeConflict(String.valueOf(chineseYM), cc, wording,
          cc.getStatus().intValue() == 1, cc.getDataFormat());
      calMin.add(Calendar.MONTH, 1);
      if (calMin.after(calMax)) {
        break;
      }
      chineseYM = calMin.get(Calendar.YEAR) * 100 + calMin.get(Calendar.MONTH) + 1 - 191100;
    } while (true);
    is.setIntelligentRunning(conditionCode, false);
  }

  public void recalculateSameATCByThread(PAY_CODE payCode, boolean enable) {
    Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        recalculateSameATC(payCode, enable);
      }
    });
    thread.start();
  }
  
  /**
   * 重新計算健保項目對應自費項目並存病歷
   * @param cc
   */
  public void recalculateSameATC(PAY_CODE payCode, boolean enable) {
    if (!"1".equals(getOneValueByName("INTELLIGENT_CONFIG", "SAME_ATC"))) {
      return;
    }
    if (payCode.getAtc() == null) {
      return;
    }
    int atcLen =
        "5".equals(getOneValueByName("INTELLIGENT_CONFIG", "SAME_ATC_LENGTH")) ? 5
            : 7;

    String atc = payCode.getAtc();
    List<PAY_CODE> payCodeList = null;
    if (atcLen == 5 && payCode.getAtc().length() >= 5) {
      atc = payCode.getAtc().substring(0, atcLen);
      payCodeList = payCodeDao.findByATCLen5(atc + "%");
    } else {
      payCodeList = payCodeDao.findByATCLen7(payCode.getAtc());
    }

    if (payCodeList.size() < 2) {
      return;
    }

    List<String> payCodes = new ArrayList<String>();
    for (PAY_CODE p : payCodeList) {
      payCodes.add(p.getCode());
    }

    if (!enable) {
      is.calculateSameATCDisable(payCodes, atc);
      return;
    }
    List<ATC> atcList = atcDao.findByCode(atc);
    String wording = getOneValueByName("INTELLIGENT", "SAME_ATC_WORDING");
    String reason = (wording != null) ? String.format(wording, atc) : null;
    String atcCode = atc;
    
    Calendar calMin = getMinMaxCalendar(payCode.getStartDate(), true);
    if (calMin == null) {
      return;
    }
    Calendar calMax =  getMinMaxCalendar(payCode.getEndDate(), false);
    int chineseYM = calMin.get(Calendar.YEAR) * 100 + calMin.get(Calendar.MONTH) + 1 - 191100;
    do {
      is.calculateSameATC(String.valueOf(chineseYM), payCodes, atcCode, reason);
      calMin.add(Calendar.MONTH, 1);
      if (calMin.after(calMax)) {
        break;
      }
      chineseYM = calMin.get(Calendar.YEAR) * 100 + calMin.get(Calendar.MONTH) + 1 - 191100;
    } while (true);
    logger.info("recalculateSameATC " + payCode.getCode() + " done");
  }

  /**
   * 計算所有病歷是否有因核刪而記錄的高風險ICD碼與醫令組合
   * @param cc
   * @param isEnable
   * @param dataFormat
   */
  public void recalculateHighRisk(CODE_CONFLICT cc, boolean isEnable, String dataFormat) {
    String wording = getOneValueByName("INTELLIGENT", "HIGH_RISK_WORDING");
    
    Thread thread = new Thread(new Runnable() {
      
      @Override
      public void run() {
        is.setIntelligentRunning(INTELLIGENT_REASON.HIGH_RISK.value(), true);
        Calendar calMin = getMinMaxCalendar(cc.getStartDate(), true);
        if (calMin == null) {
          return;
        }
        Calendar calMax =  getMinMaxCalendar(cc.getEndDate(), false);
        int chineseYM = calMin.get(Calendar.YEAR) * 100 + calMin.get(Calendar.MONTH) + 1 - 191100;
        do {
          is.calculateCodeConflict(String.valueOf(chineseYM), cc, wording, isEnable, dataFormat);
          calMin.add(Calendar.MONTH, 1);
          if (calMin.after(calMax)) {
            break;
          }
          chineseYM = calMin.get(Calendar.YEAR) * 100 + calMin.get(Calendar.MONTH) + 1 - 191100;
        } while (true);
        if (cc.getStatus().intValue() == 0 && cc.getCodeType().intValue() == 2) {
          codeConflictDao.deleteById(cc.getId());
        }
        logger.info("recalculateCodeConflict " + cc.getCode() + " done");
        is.setIntelligentRunning(INTELLIGENT_REASON.HIGH_RISK.value(), false);
      }
    });
    thread.start();
  }
  
  public void deleteIntelligent(int conditionCode, String reasonCode, String reason) {
    if (reasonCode == null) {
      mrDao.updateMrStautsForIntelligent(MR_STATUS.NO_CHANGE.value(), conditionCode);
      intelligentDao.deleteIntelligent(conditionCode);
    } else if (reason == null){
      mrDao.updateMrStatusForIntelligent(MR_STATUS.NO_CHANGE.value(), conditionCode, reasonCode);
      intelligentDao.deleteIntelligent(conditionCode, reasonCode);
    } else {
      mrDao.updateMrStatusForIntelligent(MR_STATUS.NO_CHANGE.value(), conditionCode, reasonCode, reason);
      intelligentDao.deleteIntelligent(conditionCode, reasonCode, reason);
    }
  }
  
  /**
   * 開啟或關閉法定傳染病計算功能
   * @param isEnable
   */
  public void switchInfections(boolean isEnable) {
    List<CODE_TABLE> list = codeTableDao.findByCat(ParametersService.INFECTIOUS);
    for (CODE_TABLE ct : list) {
      if (isEnable) {
        recalculateInfectious(ct);
      } else {
        deleteIntelligent(INTELLIGENT_REASON.INFECTIOUS.value(), null, null);       
      }
    }
  }
  
  /**
   * 開啟或關閉應用比例偏高
   * @param isEnable
   */
  public void switchHighRatio(boolean isEnable) {
    List<CODE_THRESHOLD> list = codeThresholdDao.findByCodeTypeOrderByStartDateDesc(2);
    for (CODE_THRESHOLD ct : list) {
      if (ct.getStatus().intValue() == 0) {
        continue;
      }
      if (isEnable) {
        recalculateHighRatioAndOverAmount(ct, true);
      } else {
        deleteIntelligent(INTELLIGENT_REASON.HIGH_RATIO.value(), null, null);       
      }
    }
  }
  
  /**
   * 開啟或關閉特別用量藥材功能
   * @param isEnable
   */
  public void switchOverAmount(boolean isEnable) {
    List<CODE_THRESHOLD> list = codeThresholdDao.findByCodeTypeOrderByStartDateDesc(3);
    for (CODE_THRESHOLD ct : list) {
      if (ct.getStatus().intValue() == 0) {
        continue;
      }
      if (isEnable) {
        recalculateHighRatioAndOverAmount(ct, false);
      } else {
        deleteIntelligent(INTELLIGENT_REASON.OVER_AMOUNT.value(), null, null);       
      }
    }
  }
  
  /**
   * 開啟或關閉健保項目對應自費項目並存功能
   * @param isEnable
   */
  public void switchInhOwnExist(boolean isEnable) {
    List<CODE_CONFLICT> list =
        codeConflictDao.findByCodeType(new Integer(1));
    if (list != null && list.size() > 0) {
      for (CODE_CONFLICT cc : list) {
        if (cc.getStatus().intValue() == 0) {
          continue;
        }
        if (isEnable) {
          recalculateCodeConflict(cc, INTELLIGENT_REASON.INH_OWN_EXIST.value());
        } else {
          deleteIntelligent(INTELLIGENT_REASON.INH_OWN_EXIST.value(), null, null);       
        }
      }
    }
  }
  
  /**
   * 開啟或關閉同性質藥物功能
   * @param isEnable
   */
  public void switchSameATC(boolean isEnable) {
    deleteIntelligent(INTELLIGENT_REASON.SAME_ATC.value(), null, null);
    if (isEnable) {
      List<PAY_CODE> list = payCodeDao.findBySameAtcOrderByAtc(1);
      if (list != null && list.size() > 0) {
        recalculateSameATC(list.get(0), true);
      }
    }
  }
  
  /**
   * 開啟或關閉罕見ICD應用
   * @param isEnable
   */
  public void switchRareICD(boolean isEnable) {
    List<CODE_THRESHOLD> list = codeThresholdDao.findByCodeTypeOrderByStartDateDesc(1);
    for (CODE_THRESHOLD ct : list) {
      if (ct.getStatus().intValue() == 0) {
        continue;
      }
      if (isEnable) {
        recalculateRareICD(ct);
      } else {
        deleteIntelligent(INTELLIGENT_REASON.RARE_ICD.value(), null, null);       
      }
    }
  }

  /**
   * 開啟或關閉試辦計畫
   * @param isEnable
   */
  public void switchPilotProject(boolean isEnable) {
    List<PLAN_CONDITION> list = planConditionDao.findByActive(1);
    for (PLAN_CONDITION pc : list) {
      if (isEnable) {
        is.calculatePilotProject(pc.getId(), true);
      } else {
        deleteIntelligent(INTELLIGENT_REASON.PILOT_PROJECT.value(), null, null);       
      }
    }
  }
  
  /**
   * 開啟或關閉高風險診斷碼與健保碼組合
   * 
   * @param isEnable
   */
  public void switchHighRisk(boolean isEnable) {
    List<CODE_CONFLICT> list = codeConflictDao.findByCodeType(new Integer(2));
    if (list != null && list.size() > 0) {
      for (CODE_CONFLICT cc : list) {
        if (cc.getStatus().intValue() == 0) {
          continue;
        }
        if (isEnable) {
          recalculateCodeConflict(cc, INTELLIGENT_REASON.HIGH_RISK.value());
        } else {
          deleteIntelligent(INTELLIGENT_REASON.HIGH_RISK.value(), null, null);
        }
      }
    }
  }
  
  public List<PARAMETERS> getByCat(String cat) {
    return parametersDao.findByCatOrderByName(cat);
  }
  
  /**
   * 開啟或關閉AI-費用差異
   * @param isEnable
   */
  public void switchCostDiff(boolean isEnable) {
    deleteIntelligent(INTELLIGENT_REASON.COST_DIFF.value(), null, null);
    if (isEnable) {
      is.recalculateAICostThread();
    }
  }
  
  /**
   * 開啟或關閉AI-住院天數差異
   * 
   * @param isEnable
   */
  public void switchIpDays(boolean isEnable) {
    logger.info("switchIpDays " + isEnable );
    deleteIntelligent(INTELLIGENT_REASON.IP_DAYS.value(), null, null);
    if (isEnable) {
      is.recalculateAIIpDaysThread();
    }
  }
  
  public void switchOrderDrug(boolean isEnable) {
    logger.info("switchOrderDrug " + isEnable );
    deleteIntelligent(INTELLIGENT_REASON.ORDER_DRUG.value(), null, null);
    if (isEnable) {
      is.recalculateAIOrderDrugThread();
    }
  }
  
  /**
   * 開啟或關閉罕見ICD應用
   * @param isEnable
   */
  public void switchViolate(boolean isEnable) {
    deleteIntelligent(INTELLIGENT_REASON.VIOLATE.value(), null, null);
    if (isEnable) {
      is.checkAllViolation();
    }
  }
}
