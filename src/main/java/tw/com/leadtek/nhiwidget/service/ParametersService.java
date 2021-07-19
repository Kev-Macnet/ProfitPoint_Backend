/**
 * Created on 2020/9/23.
 */
package tw.com.leadtek.nhiwidget.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tw.com.leadtek.nhiwidget.dao.PARAMETERSDao;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.payload.ParameterValue;
import tw.com.leadtek.nhiwidget.payload.PointsStatus;
import tw.com.leadtek.nhiwidget.payload.PointsValue;
import tw.com.leadtek.tools.Utility;

@Service
public class ParametersService {

  public final static int STATUS_ENABLED = 1;

  public final static int STATUS_DISABLED = 0;

  public final static String PAGE_COUNT = "PAGE_COUNT";

  /**
   * 是否計算西醫總點數
   */
  public final static String WM = "WM";

  /**
   * 是否計算牙醫總點數
   */
  public final static String DENTIST = "DENTIST";

  public final static String CAT_TOTAL_POINTS = "TOTAL_POINTS";

  public final static String CAT_TOTAL_POINTS_STATUS = "TOTAL_POINTS_STATUS";

  @Autowired
  private PARAMETERSDao parametersDao;

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
  }

  public Map<String, Object> getPointsStatus(int perPage, int page) {
    Map<String, Object> result = new LinkedHashMap<String, Object>();

    Specification<PARAMETERS> spec = new Specification<PARAMETERS>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<PARAMETERS> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {
        query.where(cb.or(cb.equal(root.get("name"), WM), cb.equal(root.get("name"), DENTIST)));

        List<Order> orderList = new ArrayList<Order>();
        orderList.add(cb.desc(root.get("startDate")));
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
    long total = parametersDao.count(spec);
    int iPerPage = perPage * 2;
    Page<PARAMETERS> pages = parametersDao.findAll(spec, PageRequest.of(page, iPerPage));
    List<PointsStatus> list = new ArrayList<PointsStatus>();
    if (pages != null && pages.getSize() > 0) {
      Date lastStartDate = null;
      PointsStatus lastPS = null;
      for (PARAMETERS p : pages) {
        if (lastStartDate == null) {
          lastStartDate = p.getStartDate();
          lastPS = new PointsStatus();
          lastPS.setStartDate(p.getStartDate());
          lastPS.setEndDate(p.getEndDate());
        }
        if (!p.getStartDate().equals(lastPS.getStartDate())) {
          list.add(lastPS);
          lastPS = new PointsStatus();
          lastPS.setStartDate(p.getStartDate());
          lastPS.setEndDate(p.getEndDate());
        }
        if (p.getName().equals(WM)) {
          lastPS.setWmStatus(p.getValue().equals(String.valueOf(STATUS_ENABLED)));
        } else if (p.getName().equals(DENTIST)) {
          lastPS.setDentistStatus(p.getValue().equals(String.valueOf(STATUS_ENABLED)));
        }
      }
      if (lastPS != null) {
        list.add(lastPS);
      }
    }
    result.put("totalPage", Utility.getTotalPage((int) total, perPage));
    result.put("status", list);
    return result;
  }

  public String updatePointsStatus(Date startDate, Boolean wmStatus, Boolean dentistStatus) {
    List<PARAMETERS> list = parametersDao.findByCatAndStartDate(CAT_TOTAL_POINTS_STATUS, startDate);
    if (list != null && list.size() > 0) {
      // update
      for (PARAMETERS parameters : list) {
        if (parameters.getName().equals(WM)
            && !parameters.getValue().equals(wmStatus.booleanValue() ? "1" : "0")) {
          parameters.setValue(wmStatus.booleanValue() ? "1" : "0");
          parametersDao.save(parameters);
        }
        if (parameters.getName().equals(DENTIST)
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
    result.setStartDate(startDate);
    List<PARAMETERS> list = parametersDao.findByCatAndStartDate(CAT_TOTAL_POINTS, startDate);
    if (list == null || list.size() == 0) {
      return null;
    }
    for (PARAMETERS parameters : list) {
      if (parameters.getValue() == null) {
        continue;
      }
      if (parameters.getEndDate() != null && result.getEndDate() == null) {
        result.setEndDate(parameters.getEndDate());
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
    // pv.getStartDate());
    List<PARAMETERS> list =
        parametersDao.findByCatAndStartDate(CAT_TOTAL_POINTS_STATUS, pv.getStartDate());
    if (list != null && list.size() > 0) {
      // update
      for (PARAMETERS parameters : list) {
        if (!parameters.getEndDate().equals(pv.getEndDate())) {
          parameters.setEndDate(pv.getEndDate());
          parametersDao.save(parameters);
        }
      }
      List<PARAMETERS> pointsList =
          parametersDao.findByCatAndStartDate(CAT_TOTAL_POINTS, pv.getStartDate());
      for (PARAMETERS parameters : pointsList) {
        parameters.setEndDate(pv.getEndDate());
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
        parametersDao.findByCatAndStartDate(CAT_TOTAL_POINTS_STATUS, pv.getStartDate());
    if (list != null && list.size() > 0) {
      return "生效日重複";
    }
    list = parametersDao.findByCatAndStartDateLessThanAndEndDateGreaterThan(CAT_TOTAL_POINTS_STATUS,
        pv.getStartDate(), pv.getStartDate());
    if (list != null && list.size() > 0) {
      // 將有衝突的失效日往前
      changeEndDateByNewStatus(list, pv.getStartDate());
    }

    PARAMETERS p =
        new PARAMETERS("TOTAL_POINTS_STATUS", WM, (pv.getWmOpPoints() == null) ? "0" : "1",
            PARAMETERS.TYPE_INTEGER, "是否計算西醫(Western Medicine)總點數");
    saveParameter(p, pv.getStartDate(), pv.getEndDate());

    p = new PARAMETERS("TOTAL_POINTS_STATUS", DENTIST,
        (pv.getDentistOpPoints() == null || pv.getDentistOpPoints().longValue() == 0) ? "0" : "1",
        PARAMETERS.TYPE_INTEGER, "是否計算牙醫總點數");
    saveParameter(p, pv.getStartDate(), pv.getEndDate());

    p = new PARAMETERS("TOTAL_POINTS_STATUS", DENTIST,
        (pv.getDentistOpPoints() == null) ? "0" : "1", PARAMETERS.TYPE_INTEGER, "是否計算牙醫總點數");
    saveParameter(p, pv.getStartDate(), pv.getEndDate());

    p = new PARAMETERS("TOTAL_POINTS", "WM_IP_POINTS",
        (pv.getWmIpPoints() == null) ? "0" : pv.getWmIpPoints().toString(), PARAMETERS.TYPE_LONG,
        "西醫住院分配總點數");
    saveParameter(p, pv.getStartDate(), pv.getEndDate());

    p = new PARAMETERS("TOTAL_POINTS", "WM_OP_POINTS",
        (pv.getWmOpPoints() == null) ? "0" : pv.getWmOpPoints().toString(), PARAMETERS.TYPE_LONG,
        "西醫門診分配總點數");
    saveParameter(p, pv.getStartDate(), pv.getEndDate());

    p = new PARAMETERS("TOTAL_POINTS", "WM_DRUG_POINTS",
        (pv.getWmDrugPoints() == null) ? "0" : pv.getWmDrugPoints().toString(),
        PARAMETERS.TYPE_LONG, "西醫藥品分配總點數");
    saveParameter(p, pv.getStartDate(), pv.getEndDate());

    p = new PARAMETERS("TOTAL_POINTS", "DENTIST_OP_POINTS",
        (pv.getDentistOpPoints() == null) ? "0" : pv.getDentistOpPoints().toString(),
        PARAMETERS.TYPE_LONG, "牙醫門診分配總點數");
    saveParameter(p, pv.getStartDate(), pv.getEndDate());

    p = new PARAMETERS("TOTAL_POINTS", "DENTIST_DRUG_POINTS",
        (pv.getDentistDrugPoints() == null) ? "0" : pv.getDentistDrugPoints().toString(),
        PARAMETERS.TYPE_LONG, "牙醫藥品分配總點數");
    saveParameter(p, pv.getStartDate(), pv.getEndDate());

    p = new PARAMETERS("TOTAL_POINTS", "DENTIST_FUND_POINTS",
        (pv.getDentistFundPoints() == null) ? "0" : pv.getDentistFundPoints().toString(),
        PARAMETERS.TYPE_LONG, "牙醫專款分配總點數");
    saveParameter(p, pv.getStartDate(), pv.getEndDate());

    p = new PARAMETERS("TOTAL_POINTS", "HEMODIALYSIS_POINTS",
        (pv.getHemodialysisPoints() == null) ? "0" : pv.getHemodialysisPoints().toString(),
        PARAMETERS.TYPE_LONG, "透析總點數");
    saveParameter(p, pv.getStartDate(), pv.getEndDate());

    p = new PARAMETERS("TOTAL_POINTS", "FUND_POINTS",
        (pv.getFundPoints() == null) ? "0" : pv.getFundPoints().toString(), PARAMETERS.TYPE_LONG,
        "專款總點數");
    saveParameter(p, pv.getStartDate(), pv.getEndDate());
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

  public Map<String, Object> getParameterValue(String name, int perPage, int page) {
    Map<String, Object> result = new LinkedHashMap<String, Object>();

    Specification<PARAMETERS> spec = new Specification<PARAMETERS>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<PARAMETERS> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {
        query.where(cb.equal(root.get("name"), name));

        List<Order> orderList = new ArrayList<Order>();
        orderList.add(cb.desc(root.get("startDate")));
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
    long total = parametersDao.count(spec);
    Page<PARAMETERS> pages = parametersDao.findAll(spec, PageRequest.of(page, perPage));
    List<ParameterValue> list = new ArrayList<ParameterValue>();
    if (pages != null && pages.getSize() > 0) {
      for (PARAMETERS p : pages) {
        ParameterValue pv = new ParameterValue();
        pv.setEndDate(p.getEndDate());
        pv.setStartDate(p.getStartDate());
        pv.setValue(Integer.parseInt(p.getValue()));
        list.add(pv);
      }
    }
    result.put("totalPage", Utility.getTotalPage((int) total, perPage));
    result.put("value", list);
    return result;
  }

  public String newValue(String name, String value, Date startDate, Date endDate) {
    List<PARAMETERS> list = parametersDao.findByNameAndStartDate(name, startDate);
    if (list != null && list.size() > 0) {
      return "生效日重複";
    }
    list = parametersDao.findByNameAndStartDateLessThanAndEndDateGreaterThan(name, startDate,
        startDate);
    if (list != null && list.size() > 0) {
      // 將有衝突的失效日往前
      changeEndDateByNewStatus(list, startDate);
    }
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
    System.out.println("cat=" + cat + ", name=" + name + ",value=" + value + ",start=" + startDate + ", end=" + endDate);
    PARAMETERS p = new PARAMETERS(cat, name, value, dataType, note);
    saveParameter(p, startDate, endDate);
    return null;
  }
  
}
