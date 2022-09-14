/**
 * Created on 2021/8/26.
 */
package tw.com.leadtek.nhiwidget.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tw.com.leadtek.nhiwidget.dao.DRG_CALDao;
import tw.com.leadtek.nhiwidget.dao.DRG_CODEDao;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.model.DrgCalculate;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CAL;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CODE;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.payload.DrgCodeListResponse;
import tw.com.leadtek.nhiwidget.payload.DrgCodePayload;
import tw.com.leadtek.tools.DateTool;

/**
 * 計算DRG定額並提供存取DRG_CAL table的service.
 * 
 * @author kenlai
 */
@Service
public class DrgCalService {

  public final static String DRG_DATA_FILE_PATH = "drg_data";
  
  public final static int ADD_CHILD_NONE = 0;

  public final static int ADD_CHILD_6M = 1;

  public final static int ADD_CHILD_2Y = 2;

  public final static int ADD_CHILD_6Y = 3;

  /**
   * 不得加計各項加成或其他另行加計之醫療點數之DRG Code
   */
  public final static List<String> DRG_NO_ADD = new ArrayList<String>() {
    {
      add("513");
    }
  };

  private Logger logger = LogManager.getLogger();

  @Autowired
  private ParametersService parameters;

  @Autowired
  private DRG_CODEDao drgDao;

  @Autowired
  private DRG_CALDao drgCalDao;

  @Autowired
  private IP_PDao ippDao;

  @Autowired
  private IP_DDao ipdDao;
  
  @Autowired
  private LogDataService logDataService;
  
  @Autowired
  private MRDao mrDao;
  
  @Autowired
  private ReportService reportService;
  
  @Autowired
  private IntelligentService intelligentService;
  
  @Value("${project.hospId}")
  private String HOSPITAL_ID;

  /**
   * 取得醫院加成率
   * 
   * @return
   */
  private float getHospAdd(Date date, boolean isM, boolean isMDC15, int childAdd) {
    float result = 1;
    // 山地離島加成率
    if (parameters.getParameter("HOSP_OUT") != null
        && "Y".equals(parameters.getParameter("HOSP_OUT"))) {
      result += Float.parseFloat(parameters.getParameter("OL"));
    }
    // 基本診療加成率
    try {
      String hospLevel = parameters.getParameter("HOSP_LEVEL");
      result += (Float) parameters.getParameterValueBetween("ADD_HOSP_LEVEL_" + hospLevel, date);
    } catch (Exception e) {
      logger.error("getHospAdd" ,e);
    }

    // CMI 加成率
    float cmi = (Float) parameters.getParameterValueBetween("CMI", date);
    if (cmi >= 0.011f && cmi <= 0.012f) {
      cmi = (Float) parameters.getParameterValueBetween("CMI2", date);
    } else if (cmi >= 0.012f && cmi <= 0.013f) {
      cmi = (Float) parameters.getParameterValueBetween("CMI3", date);
    } else if (cmi > 0.013f) {
      cmi = (Float) parameters.getParameterValueBetween("CMI4", date);
    }
    result += cmi;
    if (childAdd == ADD_CHILD_NONE) {
      return result;
    }
    // 兒童加成率
    if (childAdd == ADD_CHILD_6M) {
      if (isMDC15) {
        return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_15_6M", date);
      }
      if (isM) {
        return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_N15M_6M", date);
      }
      return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_N15P_6M", date);
    } else if (childAdd == ADD_CHILD_2Y) {
      if (isMDC15) {
        return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_15_2Y", date);
      }
      if (isM) {
        return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_N15M_2Y", date);
      }
      return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_N15P_2Y", date);
    } else if (childAdd == ADD_CHILD_6Y) {
      if (isMDC15) {
        return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_15_6Y", date);
      }
      if (isM) {
        return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_N15M_6Y", date);
      }
      return result + (Float) parameters.getParameterValueBetween("ADD_CHILD_N15P_6Y", date);
    }
    return result;
  }

  public double getFixedWithoutRW(Date date, boolean isM, boolean isMDC15, int addChild) {
//    System.out.println("SPR=" + (Integer) parameters.getParameterValueBetween("SPR", date) + ","
//        + getHospAdd(date, isM, isMDC15, addChild));
    return ((double) ((Integer) parameters.getParameterValueBetween("SPR", date))
        * (double) getHospAdd(date, isM, isMDC15, addChild));
  }

  /**
   * 取得DRG code在當時的定額給付金額
   * 
   * @param drg
   * @return
   */
  public int getDRGFixed(String drg, String applYM, int addChild) {
    Date date = DateTool.getDateByApplYM(applYM);
    List<DRG_CODE> drgList =
        drgDao.findByCodeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(drg, date, date);
    if (drgList == null || drgList.size() == 0) {
      return -1;
    }
    DRG_CODE drgCode = drgList.get(0);
    double value = getFixedWithoutRW(date, "M".equals(drgCode.getDep()),
        "15".equals(drgCode.getMdc()), addChild);
    return (int) Math.round((double) drgCode.getRw() * value);
  }

  /**
   * 取得DRG code資料，並將定額給付金額放在 serial欄位，DRG section放在 dep 欄位
   * 
   * @param drg
   * @return
   */
  public DrgCalculate getDRGSection(String drg, String applYM, int medDot, int childAdd) {
    Date date = DateTool.getDateByApplYM(applYM);
    List<DRG_CODE> drgList =
        drgDao.findByCodeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(drg, date, date);
    if (drgList == null || drgList.size() == 0) {
      return null;
    }
    DRG_CODE drgCode = drgList.get(0);
    DrgCalculate result = new DrgCalculate(drgCode);
    result.setDrgNoAdd(false);
    double value = 0;
    for (String string : DRG_NO_ADD) {
      if (string.equals(drg)) {
        result.setDrgNoAdd(true);
        break;
      }
    }
    if (result.isDrgNoAdd()) {
      // 不得加計各項加成或其他另行加計之醫療點數，直接帶標準給付額
      value = (Integer) parameters.getParameterValueBetween("SPR", date);
    } else {
      value = getFixedWithoutRW(date, "M".equals(drgCode.getDep()), "15".equals(drgCode.getMdc()),
          childAdd);
    }

    result.setFixed((int) (Math.round(drgCode.getRw() * value)));
    if (drgCode.getStarted().intValue() == 1) {
      // 有導入實施的 DRG
      if (result.isDrgNoAdd()) {
        if (medDot > result.getFixed()) {
          result.setSection("B2");
        } else {
          result.setSection("B1");
        }
      } else if (medDot < drgCode.getLlimit().intValue()) {
        result.setSection("A");
      } else if (medDot < result.getFixed()) {
        result.setSection("B1");
      } else if (medDot < drgCode.getUlimit().intValue()) {
        result.setSection("B2");
      } else {
        result.setSection("C");
      }
    } else {
      result.setSection(null);
    }
    return result;
  }

  /**
   * 算出該DRG的申請費用點數
   * 
   * @param drg
   * @param medDot
   * @param partDot
   * @param nonApplDot
   * @param mrId
   * @return
   */
  public int getApplDot(DrgCalculate drg, int medDot, int partDot, int nonApplDot, long mrId,
      int bedDay, String tranCode, boolean isCase20) {
    if (drg.isDrgNoAdd()) {
      return drg.getFixed() - partDot + nonApplDot;
    }
    List<Object[]> values = ippDao.findVirtualCodeByMrId(mrId);
    int h = 0;
    int j = 0;
    int g = 0;
    int x = 0;
    if (values != null && values.size() > 0) {
      for (Object[] obj : values) {
        String orderCode = (String) obj[0];
        if (obj[1] == null && obj[2] != null) {
          int value = (Integer) obj[2];
          x += value;
        } else {
          int value = (int) Math.round((double) obj[1]);
          if (orderCode.startsWith("J")) {
            j += value;
          } else if (orderCode.startsWith("G")) {
            g += value;
          } else if (orderCode.startsWith("H")) {
            h += value;
          }
        }
      }
    }
    int realMedDot = medDot - x;
    // 計算超出上限之邊際成本
    int margin = 0;
    if (realMedDot > drg.getUlimit() && drg.getUlimit() > 0) {
      margin = (int) Math.round((realMedDot - drg.getUlimit()) * 0.8);
    }
    // 論日支付
    boolean payByDay = false;
    if (("5".equals(tranCode) || "6".equals(tranCode)) && bedDay < drg.getAvgInDay()) {
      payByDay = true;
    }

    if (realMedDot < drg.getLlimit()) {
//      System.out.println("drg:" + drg.getCode() + ",realMedDot　＜=" + realMedDot + ",margin="
//          + margin + ",x=" + x + ",j=" + j + ",g=" + g + ",h=" + h + ",partDot=" + partDot
//          + ",nonApplDot=" + nonApplDot);
      return realMedDot + x + h - g - partDot + nonApplDot;
    } else {
//      System.out.println("drg:" + drg.getCode() + ",realMedDot　>=" + realMedDot + ",margin="
//          + margin + ",x=" + x + ",j=" + j + ",g=" + g + ",h=" + h + ",partDot=" + partDot
//          + ",nonApplDot=" + nonApplDot);
      if (payByDay) {
        return (int) Math.round(((double) drg.getFixed() / (double) drg.getAvgInDay()) * bedDay) - g
            - partDot + nonApplDot;
      } else if (drg.getRw() == 0 || isCase20) {
        return realMedDot + x + h - partDot + nonApplDot;
      } else {
        return drg.getFixed() + margin + x + h - j - g - partDot + nonApplDot;
      }
    }
  }

  /**
   * 取得是否有兒童加成
   * 
   * @param nbBirthday 新生兒生日
   * @param birthday 病患生日
   * @param inDay 住院日
   * @return
   */
  public int getAddChild(String nbBirthday, String birthday, String inDay) {
    if (nbBirthday != null && nbBirthday.length() > 0) {
      return ADD_CHILD_6M;
    }
    Date birth = DateTool.convertChineseToYear(birthday);
    Date in = DateTool.convertChineseToYear(inDay);
    long time = in.getTime() - birth.getTime();
    long day = time / (24 * 60 * 60 * 1000);
    // System.out.println("birth=" + birth + ",in=" + in + " day=" + day);
    if (day < 158) {
      // 因有案件 159 天仍算為 2歲內，所以由 6個月 180 天改為 158 天
      return ADD_CHILD_6M;
    }
    if (day < 730) {
      return ADD_CHILD_2Y;
    }
    if (day < 2190) {
      return ADD_CHILD_6Y;
    }
    return ADD_CHILD_NONE;
  }

  /**
   * 計算當月DRG個案數不超過20件的總數
   * 
   * @param applYM
   * @return
   */
  public HashMap<String, List<Long>> countCase20(String applYM) {
    HashMap<String, List<Long>> result = new HashMap<String, List<Long>>();
    Date date = DateTool.getDateByApplYM(applYM);
    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
    List<Object[]> list = ipdDao.getDRGCase20Id(sqlDate, sqlDate, applYM);
    if (list != null && list.size() > 0) {
      for (Object[] obj : list) {
        String drg = (String) obj[0];
        List<Long> ids = result.get(drg);
        if (ids == null) {
          ids = new ArrayList<Long>();
          result.put(drg, ids);
        }
        ids.add(((BigInteger) obj[1]).longValue());
      }
    }
    return result;
  }

  public boolean checkCase20(DrgCalculate drg, String ym) {
    if (drg.isCase20()) {
      HashMap<String, List<Long>> case20 = countCase20(ym);
      List<Long> ids = case20.get(drg.getCode());
      if (ids == null) {
        return true;
      } else {
        return ids.size() < 20;
      }
    }
    return false;
  }

  private void addPredicate(Root<DRG_CODE> root, List<Predicate> predicate, CriteriaBuilder cb,
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

  private Date parseDateString(String s, SimpleDateFormat sdf) throws ParseException {
    if (s != null && s.length() >= 10) {
      return sdf.parse(s);
    }
    return null;
  }

  public LinkedHashMap<String, Object> getDRGCode2(String sdate, String edate, String mdc,
      String code, String orderBy, Boolean asc, int perPage, int page) {
    LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
    List<DrgCodePayload> codes = new ArrayList<DrgCodePayload>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    Specification<DRG_CODE> spec = null;
    try {
      Date sd = parseDateString(sdate, sdf);
      Date ed = parseDateString(edate, sdf);
      spec = new Specification<DRG_CODE>() {

        private static final long serialVersionUID = 1L;

        public Predicate toPredicate(Root<DRG_CODE> root, CriteriaQuery<?> query,
            CriteriaBuilder cb) {
          List<Predicate> predicate = new ArrayList<Predicate>();
          if (sd != null) {
            predicate.add(cb.lessThanOrEqualTo(root.get("startDate"), sd));
          }
          if (ed != null) {
            predicate.add(cb.greaterThan(root.get("endDate"), ed));
          }
          if (mdc != null) {
            predicate.add(cb.equal(root.get("mdc"), mdc));
          }
          if (code != null && code.length() > 1) {
            predicate.add(cb.like(root.get("code"), code + "%"));
          }
          Predicate[] pre = new Predicate[predicate.size()];
          query.where(predicate.toArray(pre));

          if (orderBy != null && asc != null) {
            List<Order> orderList = new ArrayList<Order>();
            if (asc.booleanValue()) {
              orderList.add(cb.asc(root.get(orderBy)));
            } else {
              orderList.add(cb.desc(root.get(orderBy)));
            }
            query.orderBy(orderList);
          }
          return query.getRestriction();
        }
      };
    } catch (ParseException e) {
      e.printStackTrace();
    }
    int count = (int) drgDao.count(spec);
    result.put("count", count);
    int totalPage = count / perPage;
    if (count % perPage > 0) {
      totalPage++;
    }
    result.put("totalPage", totalPage);
    Page<DRG_CODE> pages = drgDao.findAll(spec, PageRequest.of(page, perPage));
    if (pages != null && pages.getSize() > 0) {
      for (DRG_CODE drg : pages) {
        codes.add(DrgCodePayload.fromDB(drg));
      }
    }
    result.put("drg", codes);
    return result;
  }

  public DrgCodeListResponse getDRGCode(Date sd, Date ed, String mdc, String code,
      String orderBy, Boolean asc, int perPage, int page) {
    List<DrgCodePayload> codes = new ArrayList<DrgCodePayload>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    Specification<DRG_CODE> spec = null;
    spec = new Specification<DRG_CODE>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<DRG_CODE> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {
        List<Predicate> predicate = new ArrayList<Predicate>();
        if (sd != null) {
          predicate.add(cb.greaterThanOrEqualTo(root.get("startDate"), sd));
        }
        if (ed != null) {
          predicate.add(cb.lessThanOrEqualTo(root.get("endDate"), ed));
        }
        if (mdc != null) {
          predicate.add(cb.equal(root.get("mdc"), mdc));
        }
        if (code != null && code.length() > 1) {
          predicate.add(cb.like(root.get("code"), code + "%"));
        }
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));

        if (orderBy != null && asc != null) {
          List<Order> orderList = new ArrayList<Order>();
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
          query.orderBy(orderList);
        }
        return query.getRestriction();
      }
    };

    DrgCodeListResponse result = new DrgCodeListResponse();
    result.setCount((int) drgDao.count(spec));
    int totalPage = result.getCount() / perPage;
    if (result.getCount() % perPage > 0) {
      totalPage++;
    }
    result.setTotalPage(totalPage);
    Page<DRG_CODE> pages = drgDao.findAll(spec, PageRequest.of(page, perPage));
    if (pages != null && pages.getSize() > 0) {
      for (DRG_CODE drg : pages) {
        codes.add(DrgCodePayload.fromDB(drg));
      }
    }
    result.setData(codes);
    return result;
  }

  public DRG_CODE getDrgCode(DRG_CODE code) {
    if (code.getId() != null) {
      return drgDao.findById(code.getId()).orElse(null);
    }
    if (isTimeOverlapDrgCode(code)) {
      return code;
    }
    return null;
  }

  public DRG_CODE getDrgCode(Long id) {
    if (id != null) {
      Optional<DRG_CODE> optional = drgDao.findById(id);
      if (optional.isPresent()) {
        return optional.get();
      }
    }
    return null;
  }

  public void saveDrgCode(DRG_CODE code) {
    List<DRG_CODE> list = drgDao.findByCode(code.getCode());
    drgDao.save(code);
    if (list == null || list.size() == 0) {
      return;
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(code.getStartDate());
    cal.add(Calendar.DAY_OF_YEAR, -1);
    for (DRG_CODE db : list) {
      if (db != null && db.getId().longValue() == code.getId().longValue()) {
        continue;
      }
      if (db.getStartDate().getTime() < code.getEndDate().getTime()
          && db.getEndDate().getTime() >= code.getStartDate().getTime()) {
        db.setEndDate(cal.getTime());
        drgDao.save(db);
      }
    }
  }

  public void deleteDrgCode(DRG_CODE code) {
    drgDao.deleteById(code.getId());
  }

  public boolean isTimeOverlapDrgCode(DRG_CODE dc) {
    List<DRG_CODE> list = drgDao.findByCode(dc.getCode());
    for (DRG_CODE drgCode : list) {
      if (dc.getId() != null && drgCode.getId().longValue() == dc.getId().longValue()) {
        continue;
      }
      if (drgCode.getStartDate().getTime() <= dc.getStartDate().getTime()
          && drgCode.getEndDate().getTime() >= dc.getStartDate().getTime()) {
        return true;
      } else if (drgCode.getStartDate().getTime() >= dc.getStartDate().getTime()
          && drgCode.getEndDate().getTime() <= dc.getEndDate().getTime()) {
        return true;
      } else if (drgCode.getStartDate().getTime() <= dc.getEndDate().getTime()
          && drgCode.getEndDate().getTime() >= dc.getEndDate().getTime()) {
        return true;
      }
    }
    return false;
  }
  
  public File generateDrgCalFile(List<MR> mrList, List<Long> mrIdList, HashMap<Long, IP_D> ipdMap) {
    if (mrList == null || mrList.size() == 0) {
      return null;
    }
    DecimalFormat df = new DecimalFormat("0000000");
    File file = new File(DRG_DATA_FILE_PATH + "/" + System.currentTimeMillis() + ".txt");
    //System.out.println("drg file:" + file.getAbsolutePath());
    List<Map<String, Object>> data = ipdDao.getDrgCalField(mrIdList);
    try {
      BufferedWriter bw =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
      for (Map<String, Object> map : data) {
        if (map.get("IN_DATE") == null) {
          continue;
        }
        List<String> icd = getIcdListFromMap(map);
        writeMrRecordForDrgCal(bw, map, icd, df);
        changeIcdOrder(bw, map, df, icd, 2);
        changeIcdOrder(bw, map, df, icd, 3);
        changeIcdOrder(bw, map, df, icd, 4);
        changeIcdOrder(bw, map, df, icd, 5);
        MR mr = getMrById(mrList, ((BigInteger) map.get("ID")).longValue());
        // 將是否有小孩加成放在 changeIcd 欄位
        mr.setChangeICD(getAddChild((String) map.get("NB_BIRTHDAY"),
            (String) map.get("ID_BIRTH_YMD"), (String) map.get("IN_DATE")));
        if (!ipdMap.containsKey(mr.getId())) {
          IP_D ipd = new IP_D();
          ipd.setPartDot(map.get("PART_DOT") != null ? (Integer) map.get("PART_DOT") : 0);
          ipd.setApplDot(map.get("APPL_DOT") != null ? (Integer) map.get("APPL_DOT") : 0);
          ipd.setNonApplDot(map.get("NON_APPL_DOT") != null ? (Integer) map.get("NON_APPL_DOT") : 0);
          ipd.setMedDot(map.get("MED_DOT") != null ? (Integer) map.get("MED_DOT") : 0);
          if (map.get("BED_DAY") != null) {
            ipd.setEbedDay((map.get("BED_DAY") instanceof Integer) ? (Integer) map.get("BED_DAY") : ((Double) map.get("BED_DAY")).intValue());
          } else {
            ipd.setEbedDay(0);
          }
          ipd.setTranCode((String) map.get("TRAN_CODE"));
          ipdMap.put(mr.getId(), ipd);
        }
      }
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return file;
  }
  
  private void writeMrRecordForDrgCal(BufferedWriter bw, Map<String, Object> map, List<String> icd, DecimalFormat df) throws IOException {
    if (map.get("IN_DATE") == null) {
      return;
    }
    bw.write(HOSPITAL_ID);
    bw.write(",");
    bw.write(String.valueOf(Integer.parseInt((String) map.get("APPL_YM")) + 191100));
    bw.write(",");
    bw.write((String) map.get("ROC_ID"));
    bw.write(",");
    bw.write(((BigInteger) map.get("ID")).toString());
    bw.write(",");
    if ('1' == ((String) map.get("ROC_ID")).charAt(1)) {
      bw.write("M");  
    } else {
      bw.write("F");
    }
    bw.write(",");
    bw.write(String.valueOf(Integer.parseInt((String) map.get("IN_DATE")) + 19110000));
    bw.write(",");
    bw.write(String.valueOf(Integer.parseInt((String) map.get("ID_BIRTH_YMD")) + 19110000));
    bw.write(",");
    writeString(bw, icd, 1);
    bw.write(",");
    writeString(bw, icd, 2);
    bw.write(",");
    writeString(bw, icd, 3);
    bw.write(",");
    writeString(bw, icd, 4);
    bw.write(",");
    writeString(bw, icd, 5);
    bw.write(",");
    writeString(bw, map.get("ICD_OP_CODE1"));
    bw.write(",");
    writeString(bw, map.get("ICD_OP_CODE2"));
    bw.write(",");
    writeString(bw, map.get("ICD_OP_CODE3"));
    bw.write(",");
    writeString(bw, map.get("ICD_OP_CODE4"));
    bw.write(",");
    writeString(bw, map.get("ICD_OP_CODE5"));
    bw.write(",");
    writeString(bw, map.get("TRAN_CODE"));
    bw.write(",");
    if (map.get("OUT_DATE") != null) {
    bw.write(String.valueOf(Integer.parseInt((String) map.get("OUT_DATE")) + 19110000));
    }
    bw.write(",+");
    bw.write((df.format((Integer) map.get("MED_DOT"))));
    bw.write(",1,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,");
    bw.newLine();
  }
  private List<String> getIcdListFromMap(Map<String, Object> map){
    List<String> result = new ArrayList<String>();
    addIcdToList(result, map, "ICD_CM_1");
    addIcdToList(result, map, "ICD_CM_2");
    addIcdToList(result, map, "ICD_CM_3");
    addIcdToList(result, map, "ICD_CM_4");
    addIcdToList(result, map, "ICD_CM_5");
    return result;
  }
  
  private void addIcdToList(List<String> list, Map<String, Object> map, String key) {
    if (map.get(key) != null) {
      list.add(((String) map.get(key)).replaceAll("\\.", ""));
    }
  }
  
  private void changeIcdOrder(BufferedWriter bw, Map<String, Object> map, DecimalFormat df, List<String> list, int count) throws IOException {
    if (list.size() >= count) {
      String first = list.get(0);
      String firstNew = list.get(count - 1);
      list.set(0, firstNew);
      list.set(count - 1, first);
      writeMrRecordForDrgCal(bw, map, list, df);
    }
  }
  
  private void writeString(BufferedWriter bw, Object obj) throws IOException {
    if (obj == null) {
      return;
    }
    bw.write(((String) obj).replaceAll("\\.", ""));
  }
  
  private void writeString(BufferedWriter bw, List<String> list, int count) throws IOException {
    if (list.size() < count) {
      return;
    }
    bw.write(list.get(count - 1));
  }
  
  public List<Long> getMrIdByDataFormat(List<MR> mrList, String dataFormat) {
    List<Long> result = new ArrayList<Long>();
    for (MR mr : mrList) {
      if (dataFormat != null) {
        if (!mr.getDataFormat().equals(dataFormat)) {
          continue;
        }
      }
      result.add(mr.getId());
    }
    return result;
  }
  
  /**
   * 產生call DRG編審程式 .bat 檔案
   * @param mrList
   * @return true:成功，false:失敗
   */
  private boolean generateDRGBAT(List<MR> mrList) {
    Date maxDate = new Date(0);
    for (MR mr : mrList) {
      if (mr.getMrEndDate().after(maxDate)) {
        maxDate = mr.getMrEndDate();
      }
    }
    String drgPath = (String) parameters.getParameterValueBetween("DRGSERVICE_PATH", maxDate);
    if (drgPath == null) {
      return false;
    }
    String drgEXE = (String) parameters.getParameterValueBetween("DRGSERVICE_NAME", maxDate);
    if (drgEXE == null) {
      drgEXE = "DRGICD10.exe";
    }
    logDataService.createDrgBatchFile(drgPath, drgEXE);
    return true;
  }
  
  public void callDrgCalProgram(File file, List<MR> mrList, List<Long> mrIdList, HashMap<Long, IP_D> ipdMap) {
    if (!generateDRGBAT(mrList)) {
      return;
    }
    String targetName = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 4) + "B.txt";
    targetName = targetName.substring(targetName.lastIndexOf('\\') + 1);
    String pyCommand = DRG_DATA_FILE_PATH + "/DRG.BAT " + file.getName() + " " + targetName + " y";
    execBatch(pyCommand);
    processDrgCalResultFile(new File(DRG_DATA_FILE_PATH + "\\" + targetName), mrList, mrIdList, ipdMap);
  }
  
  public void processDrgCalResultFile(File file, List<MR> mrList, List<Long> mrIdList, HashMap<Long, IP_D> ipdMap) {
    logger.info("processDrgCalResultFile:" + file.getAbsolutePath());
    HashMap<String, Integer> drgApplDot = new HashMap<String, Integer>();
    int count = 0;
    try {
      if (mrIdList != null && mrIdList.size() > 0) {
        drgCalDao.deleteByMrId(mrIdList);
      } else {
        deleteDrgCalInResultFile(file, mrList, ipdMap);
      }
      FileInputStream fis = new FileInputStream(file);
      BufferedReader isReader =
          new java.io.BufferedReader(new InputStreamReader(fis, "big5"));
      // 跳過檔頭
      isReader.readLine();
      String str;
      while ((str = isReader.readLine()) != null) {
        count++;
        String[] ss = str.split(",");
        DRG_CAL dc = new DRG_CAL();
        
        if (ss.length > 55 && ss[55].trim().length() > 0) {
          //dc.setError(logDataService.getErrorMessage(ss[55].trim()));
          dc.setError(ss[55].trim());
        } else if (ss[24] != null && '0' != ss[24].charAt(0)) {
          dc.setError(String.valueOf(ss[24].charAt(0)));
        }
        if (dc.getError() != null && dc.getError().length() > 10) {
          dc.setError(dc.getError().substring(0, 9));
        }
        dc.setMrId(Long.parseLong(ss[3]));
        dc.setIcdCM1(NHIWidgetXMLService.addICDCMDot(ss[7]));
        dc.setIcdOPCode1(NHIWidgetXMLService.addICDCMDot(ss[12]));
        dc.setMedDot(Integer.parseInt(ss[19].substring(1)));
        dc.setCc(ss[22]);
        dc.setDrg(ss[21]);
        dc.setMdc(ss[23]);
        
        MR mr = getMrById(mrList, dc.getMrId().longValue());
        IP_D ipd = ipdMap.get(mr.getId());
        // 西元年
        int adYM = Integer.parseInt(ss[1]);
        int newApplDot = 0;
        DrgCalculate drgCodeDetail = getDRGSection(dc.getDrg(), String.valueOf(adYM - 191100),
              dc.getMedDot(), mr.getChangeICD().intValue());
        if (drgCodeDetail != null && !drgCodeDetail.isStarted()) {
          // DRG代碼尚未導入
          dc.setError("C");
        }
        if (drgCodeDetail != null) {
          // 有Error且不是 C (未導入) 就不處理以下code
          DecimalFormat df = new DecimalFormat("#.###");
          dc.setRw(Double.parseDouble(df.format(drgCodeDetail.getRw())));
          dc.setAvgInDay(drgCodeDetail.getAvgInDay());
          dc.setUlimit(drgCodeDetail.getUlimit());
          dc.setLlimit(drgCodeDetail.getLlimit());
          dc.setDrgFix(drgCodeDetail.getFixed());
          dc.setDrgSection(drgCodeDetail.getSection());

          // 2022/8/12 每筆都計算，不抓上次相同DRG的結果
//          if (drgApplDot.get(dc.getDrg()) != null) {
//            newApplDot = drgApplDot.get(dc.getDrg()).intValue();
//          } else {
            boolean isInCase20 = checkCase20(drgCodeDetail, ss[1]);
            newApplDot = getApplDot(drgCodeDetail, ipd.getMedDot(), ipd.getPartDot(),
                ipd.getNonApplDot(), mr.getId(), ipd.getEbedDay(), ipd.getTranCode(), isInCase20);
          //  drgApplDot.put(dc.getDrg(), newApplDot);
          //}
          dc.setDrgDot(newApplDot);

          if (dc.getIcdCM1().equals(mr.getIcdcm1())) {
            mr.setDrgCode(dc.getDrg());
            mr.setDrgFixed(dc.getDrgFix());
            if ("0".equals(ipd.getTwDrgsSuitMark())) {
              mr.setDrgSection(dc.getDrgSection());
            }
            mrDao.updateDRG(dc.getDrg(), dc.getDrgFix(), dc.getDrgSection(), mr.getId());
          }
        }

        dc.setUpdateAt(new Date());
        drgCalDao.save(dc);
      }
      isReader.close();
      fis.close();
    } catch (java.io.IOException e) {
      e.printStackTrace();
    } finally {
      File fileSource = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 5) + ".txt");
      if (fileSource.exists()) {
        fileSource.delete();
      }
      if (file.exists()) {
        //file.delete();
      }
    }
    logger.info("DrgCalculate result count:" + count);
    calculateDRGReport(mrList);
  }
  
  private void calculateDRGReport(List<MR> mrList) {
    List<String> applYm = intelligentService.getDistinctApplYm(mrList);
    // 月報表資料
    for (String ym : applYm) {
      reportService.calculatePointMR(ym);
      reportService.calculateDRGMonthly(ym);
    }
    
    Date firstDate = new Date();
    for (MR mr : mrList) {
      if (mr.getMrDate().before(firstDate)) {
        firstDate = mr.getMrDate();
      }
    }
    Calendar startCal = Calendar.getInstance();
    startCal.setTime(firstDate);
    reportService.calculatePointWeekly(startCal, true);
  }
  
  private void deleteDrgCalInResultFile(File file, List<MR> mrList, HashMap<Long, IP_D> ipdMap) {
    List<Long> mrIdList = new ArrayList<>();
    
    try {
      FileInputStream fis = new FileInputStream(file);
      BufferedReader isReader =
          new java.io.BufferedReader(new InputStreamReader(fis, "big5"));
      // 跳過檔頭
      isReader.readLine();
      String str;
      while ((str = isReader.readLine()) != null) {
        String[] ss = str.split(",");
        mrIdList.add(Long.parseLong(ss[3]));
      }
      isReader.close();
      fis.close();
    } catch (java.io.IOException e) {
      logger.error("deleteDrgCalInResultFile", e);
    }
    drgCalDao.deleteByMrId(mrIdList);
    if (mrList.size() == 0) {
      List<MR> list = mrDao.getMrByIdList(mrIdList);
      mrList.addAll(list);
    }
    if (ipdMap.size() == 0) {
      List<IP_D> ipdList = ipdDao.getIpdListByMrId(mrIdList);
      for (IP_D ipd : ipdList) {
        ipdMap.put(ipd.getMrId(), ipd);
      }
    }
  }
  
  public String execBatch(String pyCommand) {
    String[] arrCommand = pyCommand.split(" ");
    String[] arguments = new String[arrCommand.length];
    for (int a = 0; a < arrCommand.length; a++) {
      arguments[a] = arrCommand[a];
    }
    StringBuffer sBuffer = new StringBuffer();
    try {
      Process process = Runtime.getRuntime().exec(arguments);
      int exitCode = process.waitFor();
      logger.info("execBatch status=" + exitCode);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return sBuffer.toString();
  }
  
  private MR getMrById(List<MR> mrList, long id) {
    for (MR mr : mrList) {
      if (mr.getId().longValue() == id) {
        return mr;
      }
    }
    return null;
  }
}
