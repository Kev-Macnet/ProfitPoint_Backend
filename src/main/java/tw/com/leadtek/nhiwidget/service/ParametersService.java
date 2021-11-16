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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import tw.com.leadtek.nhiwidget.dao.ASSIGNED_POINTDao;
import tw.com.leadtek.nhiwidget.dao.CODE_CONFLICTDao;
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.dao.CODE_THRESHOLDDao;
import tw.com.leadtek.nhiwidget.dao.PARAMETERSDao;
import tw.com.leadtek.nhiwidget.dao.PAY_CODEDao;
import tw.com.leadtek.nhiwidget.model.rdb.ASSIGNED_POINT;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_CONFLICT;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_THRESHOLD;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
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

  private static HashMap<String, String> parameters;

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
          predicate.add(cb.and(cb.greaterThanOrEqualTo(root.get("endDate"), edate),
              cb.lessThanOrEqualTo(root.get("startDate"), sdate)));
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
    assignedPointDao.save(ap.toDB());
    return null;
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

    assignedPointDao.save(ap.toDB());
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

  public ParameterListPayload getParameterValue(String name, Date sDate, Date eDate, String orderBy,
      Boolean asc, int perPage, int page) {
    ParameterListPayload result = new ParameterListPayload();

    Specification<PARAMETERS> spec = new Specification<PARAMETERS>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<PARAMETERS> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {

        if (sDate != null && eDate != null) {
          query.where(cb.and(cb.equal(root.get("name"), name),
              cb.between(root.get("startDate"), sDate, eDate),
              cb.between(root.get("endDate"), sDate, eDate)));
        } else {
          query.where(cb.equal(root.get("name"), name));
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
    if (checkTimeOverwrite(list, startDate.getTime(), endDate.getTime(), 0)) {
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
    if (checkTimeOverwrite(list, pv.getSdate().getTime(), pv.getEdate().getTime(), pv.getId())) {
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
        parametersDao.findByNameAndStartDateLessThanAndEndDateGreaterThan(name, date, date);
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

  public List<String> getPayCodeCategory() {
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
    saveNewParameter("SPR", String.valueOf(values.getSpr()), "DRG SPR 標準給付額", sDate, eDate,
        DATA_TYPE.INT.ordinal());
    saveNewParameter("ADD_HOSP_LEVEL_1", values.getAddHospLevel1(), "醫學中心基本診療加成百分比", sDate, eDate,
        DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_HOSP_LEVEL_2", values.getAddHospLevel2(), "區域醫院基本診療加成百分比", sDate, eDate,
        DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_HOSP_LEVEL_3", values.getAddHospLevel3(), "地區醫院基本診療加成百分比", sDate, eDate,
        DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_15_6M", values.getAdd15Child6m(), "兒童加成率MDC15小於6個月", sDate, eDate,
        DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_15_2Y", values.getAdd15Child2y(), "兒童加成率MDC15大於6個月，小於2歲", sDate,
        eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_15_6Y", values.getAdd15Child6y(), "兒童加成率MDC15大於2歲，小於6歲", sDate,
        eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_N15M_6M", values.getAddN15MChild6m(), "兒童加成率非MDC15內科小於6個月", sDate,
        eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_N15M_2Y", values.getAddN15MChild2y(), "兒童加成率非MDC15內科大於6個月，小於2歲",
        sDate, eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_N15M_6Y", values.getAddN15MChild6y(), "兒童加成率非MDC15內科大於2歲，小於6歲",
        sDate, eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_N15P_6M", values.getAddN15PChild6m(), "兒童加成率非MDC15外科小於6個月", sDate,
        eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_N15P_2Y", values.getAddN15PChild2y(), "兒童加成率非MDC15外科大於6個月，小於2歲",
        sDate, eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("ADD_CHILD_N15P_6Y", values.getAddN15PChild6y(), "兒童加成率非MDC15外科大於2歲，小於6歲",
        sDate, eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("CMI", values.getCmi(), "CMI加成率", sDate, eDate, DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("CMI12", values.getCmi12(), "CMI值大於 1.1 小於等於1.2加成率", sDate, eDate,
        DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("CMI13", values.getCmi13(), "CMI值小於等於1.3加成率", sDate, eDate,
        DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("CMI14", values.getCmi14(), "CMI值大於1.3加成率", sDate, eDate,
        DATA_TYPE.FLOAT.ordinal());
    saveNewParameter("OL", values.getOutlyingIslands(), "Outlying Islands 山地離島加成率", sDate, eDate,
        DATA_TYPE.FLOAT.ordinal());

    return null;
  }

  private void saveNewParameter(String name, String value, String note, Date sDate, Date eDate,
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
    PARAMETERS p = new PARAMETERS("DRG", name, newValue, dataType, note);
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

    List<PARAMETERS> list = parametersDao.findByNameAndStartDate("SPR", sDate);
    if (list != null && list.size() > 0) {
      for (PARAMETERS p : list) {
        if (p.getId().longValue() != values.getId().longValue()) {
          return "生效日已有存在的DRG相關參數設定";
        }
      }
    }
    list = parametersDao.findByNameAndStartDateGreaterThanAndEndDateLessThan("SPR", sDate, eDate);
    if (list != null && list.size() > 0) {
      for (PARAMETERS p : list) {
        if (p.getId().longValue() != values.getId().longValue()) {
          return "生效日內已有存在的DRG相關參數設定";
        }
      }
    }

    Optional<PARAMETERS> optional = parametersDao.findById(values.getId());
    if (optional.isPresent()) {
      PARAMETERS p = optional.get();
      list = parametersDao.findByCatAndStartDateEquals("DRG", p.getStartDate());
      if (list != null && list.size() > 0) {
        DecimalFormat df = new DecimalFormat("#.###");
        for (PARAMETERS parameters : list) {
          parameters.setStartDate(sDate);
          parameters.setEndDate(eDate);
          if (parameters.getName().equals("SPR")) {
            parameters.setValue(String.valueOf(values.getSpr()));
          } else if (parameters.getName().equals("ADD_HOSP_LEVEL_1")) {
            parameters
                .setValue(df.format(Double.parseDouble(values.getAddHospLevel1()) / (double) 100));
          } else if (parameters.getName().equals("ADD_HOSP_LEVEL_2")) {
            parameters
                .setValue(df.format(Double.parseDouble(values.getAddHospLevel2()) / (double) 100));
          } else if (parameters.getName().equals("ADD_HOSP_LEVEL_3")) {
            parameters
                .setValue(df.format(Double.parseDouble(values.getAddHospLevel3()) / (double) 100));
          } else if (parameters.getName().equals("ADD_CHILD_15_6M")) {
            parameters
                .setValue(df.format(Double.parseDouble(values.getAdd15Child6m()) / (double) 100));
          } else if (parameters.getName().equals("ADD_CHILD_15_2Y")) {
            parameters
                .setValue(df.format(Double.parseDouble(values.getAdd15Child2y()) / (double) 100));
          } else if (parameters.getName().equals("ADD_CHILD_15_6Y")) {
            parameters
                .setValue(df.format(Double.parseDouble(values.getAdd15Child6y()) / (double) 100));
          } else if (parameters.getName().equals("ADD_CHILD_N15M_6M")) {
            parameters
                .setValue(df.format(Double.parseDouble(values.getAddN15MChild6m()) / (double) 100));
          } else if (parameters.getName().equals("ADD_CHILD_N15M_2Y")) {
            parameters
                .setValue(df.format(Double.parseDouble(values.getAddN15MChild2y()) / (double) 100));
          } else if (parameters.getName().equals("ADD_CHILD_N15M_6Y")) {
            parameters
                .setValue(df.format(Double.parseDouble(values.getAddN15MChild6y()) / (double) 100));
          } else if (parameters.getName().equals("ADD_CHILD_N15P_6M")) {
            parameters
                .setValue(df.format(Double.parseDouble(values.getAddN15PChild6m()) / (double) 100));
          } else if (parameters.getName().equals("ADD_CHILD_N15P_2Y")) {
            parameters
                .setValue(df.format(Double.parseDouble(values.getAddN15PChild2y()) / (double) 100));
          } else if (parameters.getName().equals("ADD_CHILD_N15P_6Y")) {
            parameters
                .setValue(df.format(Double.parseDouble(values.getAddN15PChild6y()) / (double) 100));
          } else if (parameters.getName().equals("CMI")) {
            parameters.setValue(df.format(Double.parseDouble(values.getCmi()) / (double) 100));
          } else if (parameters.getName().equals("CMI12")) {
            parameters.setValue(df.format(Double.parseDouble(values.getCmi12()) / (double) 100));
          } else if (parameters.getName().equals("CMI13")) {
            parameters.setValue(df.format(Double.parseDouble(values.getCmi13()) / (double) 100));
          } else if (parameters.getName().equals("CMI14")) {
            parameters.setValue(df.format(Double.parseDouble(values.getCmi14()) / (double) 100));
          } else if (parameters.getName().equals("OL")) {
            parameters.setValue(
                df.format(Double.parseDouble(values.getOutlyingIslands()) / (double) 100));
          }
          parametersDao.save(parameters);
        }
      }
    } else {
      return "id 不存在";
    }
    return null;
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
    CODE_TABLE ct = codeTableDao.findByCodeAndCat(icd.toUpperCase(), INFECTIOUS);
    if (ct == null) {
      return "ICD代碼 " + icd + " 不存在";
    }
    if (ct.getRemark() == null && enable) {
      // 都是 enable 狀態，不處理
      return null;
    }
    if (ct.getRemark() == null && !enable) {
      ct.setRemark("inactive");
      codeTableDao.save(ct);
    } else if (ct.getRemark() != null && enable) {
      ct.setRemark(null);
      codeTableDao.save(ct);
    }
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
    CODE_THRESHOLD db = request.toDB();
    if (db.getEndDate().before(db.getStartDate())) {
      return "失效日不可早於生效日！";
    }
    if (db.getEndDate().getTime() == db.getStartDate().getTime()) {
      return "失效日不可等於生效日！";
    }
    List<CODE_THRESHOLD> list = codeThresholdDao.findByCodeTypeAndCodeOrderByStartDateDesc(
        new Integer(RareICDPayload.CODE_TYPE_ICD), request.getCode().toUpperCase());
    if (checkTimeOverwrite(list, db, false)) {
      return "該時段有相同的罕見ICD代碼！";
    }
    db.setUpdateAt(new Date());
    codeThresholdDao.save(db);
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
    return null;
  }

  public String updateRareICD(RareICDPayload request) {
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
    return null;
  }

  public String deleteCodeThreshold(String id) {
    Optional<CODE_THRESHOLD> optional = codeThresholdDao.findById(Long.parseLong(id));
    if (optional.isPresent()) {
      CODE_THRESHOLD rareIcd = optional.get();
      codeThresholdDao.deleteById(rareIcd.getId());
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
          predicate.add(cb.equal(root.get("code"), code.toUpperCase()));
        }
        if (inhCode != null && inhCode.length() > 0) {
          predicate.add(cb.equal(root.get("inhCode"), inhCode.toUpperCase()));
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
        return query.getRestriction();
      }
    };
    long total = codeThresholdDao.count(spec);
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
    CODE_THRESHOLD db = request.toDB();
    db.setCodeType(isOrder ? RareICDPayload.CODE_TYPE_ORDER : RareICDPayload.CODE_TYPE_DRUG);
    if (db.getEndDate().before(db.getStartDate())) {
      return "失效日不可早於生效日！";
    }
    if (db.getEndDate().getTime() == db.getStartDate().getTime()) {
      return "失效日不可等於生效日！";
    }
    List<CODE_THRESHOLD> list = codeThresholdDao.findByCodeTypeAndCodeOrderByStartDateDesc(
        new Integer(RareICDPayload.CODE_TYPE_ORDER), request.getCode().toUpperCase());
    if (checkTimeOverwrite(list, db, false)) {
      return isOrder ? "該時段有相同的應用比例偏高醫令！" : "該時段有相同的特別用量藥品、衛品！";
    }

    db.setUpdateAt(new Date());
    codeThresholdDao.save(db);
    return null;
  }

  public String updateHighRatioOrder(HighRatioOrder request) {
    CODE_THRESHOLD db = request.toDB();
    if (db.getEndDate().before(db.getStartDate())) {
      return "失效日不可早於生效日！";
    }
    List<CODE_THRESHOLD> list = codeThresholdDao.findByCodeTypeAndCodeOrderByStartDateDesc(
        RareICDPayload.CODE_TYPE_ORDER, request.getCode());
    if (checkTimeOverwrite(list, db, true)) {
      return "該時段有相同的應用比例偏高支付代碼";
    }
    db.setUpdateAt(new Date());
    codeThresholdDao.save(db);
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
    codeThresholdDao.save(ct);
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
    for (CODE_THRESHOLD rareIcd : list) {
      if (checkSameId) {
        if (rareIcd.getId().longValue() == db.getId()) {
          continue;
        }
      }
      if (db.getStartDate().getTime() >= rareIcd.getStartDate().getTime()
          && db.getStartDate().getTime() <= rareIcd.getEndDate().getTime()) {
        return true;
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
  public boolean checkTimeOverwrite(List<PARAMETERS> list, long startDate, long endDate, long id) {
    List<PARAMETERS> needProcessList = new ArrayList<PARAMETERS>();
    for (PARAMETERS p : list) {
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
          predicate.add(cb.isNotNull(root.get("atc")));
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
    payCodeDao.save(pc);
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

  public String upsertCodeConflict(CodeConflictPayload cc, boolean checkSameId) {
    List<CODE_CONFLICT> list =
        codeConflictDao.findByCodeAndOwnExpCode(cc.getCode(), cc.getOwnCode());
    if (list != null && list.size() > 0) {
      for (CODE_CONFLICT code_CONFLICT : list) {
        if (checkSameId && code_CONFLICT.getId().longValue() == cc.getId().longValue()) {
          continue;
        }
        if (code_CONFLICT.getStartDate().getTime() == cc.getSdate().getTime()) {
          return "生效日已有相同的並存設定";
        }
      }
    }
    codeConflictDao.save(cc.toDB());
    return null;
  }

  public String updateCodeConflict(Long id, boolean enable) {
    Optional<CODE_CONFLICT> optional = codeConflictDao.findById(id);
     
    if (!optional.isPresent()) {
      return "id: " + id + " 不存在";
    }
    CODE_CONFLICT pc = optional.get();
    if (pc.getStatus() == 0 && !enable) {
      // 都是 disable 狀態，不處理
      return null;
    }
    if (pc.getStatus().intValue() == 1 && enable) {
      // 都是 enable 狀態，不處理
      return null;
    }
    pc.setStatus(enable ? new Integer(1) : new Integer(0));
    pc.setUpdateAt(new Date());
    codeConflictDao.save(pc);
    return null;
  }

  public String deleteCodeConflict(long id) {
    Optional<CODE_CONFLICT> optional = codeConflictDao.findById(new Long(id));
    if (!optional.isPresent()) {
      return "id不存在";
    }
    codeConflictDao.deleteById(new Long(id));
    return null;
  }

}
