/**
 * Created on 2021/11/3.
 */
package tw.com.leadtek.nhiwidget.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.leadtek.nhiwidget.dao.ASSIGNED_POINTDao;
import tw.com.leadtek.nhiwidget.dao.DRG_MONTHLYDao;
import tw.com.leadtek.nhiwidget.dao.DRG_WEEKLYDao;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.IP_TDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.OP_TDao;
import tw.com.leadtek.nhiwidget.dao.POINT_MONTHLYDao;
import tw.com.leadtek.nhiwidget.dao.POINT_WEEKLYDao;
import tw.com.leadtek.nhiwidget.model.rdb.ASSIGNED_POINT;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_MONTHLY;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_WEEKLY;
import tw.com.leadtek.nhiwidget.model.rdb.IP_T;
import tw.com.leadtek.nhiwidget.model.rdb.OP_T;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_MONTHLY;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_WEEKLY;
import tw.com.leadtek.nhiwidget.payload.report.DRGMonthlyPayload;
import tw.com.leadtek.nhiwidget.payload.report.DRGMonthlySectionPayload;
import tw.com.leadtek.nhiwidget.payload.report.NameCodePoint;
import tw.com.leadtek.nhiwidget.payload.report.NameCodePointQuantity;
import tw.com.leadtek.nhiwidget.payload.report.NameValueList;
import tw.com.leadtek.nhiwidget.payload.report.PeriodPointPayload;
import tw.com.leadtek.nhiwidget.payload.report.PeriodPointWeeklyPayload;
import tw.com.leadtek.nhiwidget.payload.report.PointMRPayload;
import tw.com.leadtek.nhiwidget.payload.report.PointQuantityList;
import tw.com.leadtek.tools.DateTool;

@Service
public class ReportService {

  private Logger logger = LogManager.getLogger();

  /**
   * 全部科別的科別代碼
   */
  public static final String FUNC_TYPE_ALL = "00";

  /**
   * 全部科別的科別代碼
   */
  public static final String FUNC_TYPE_ALL_NAME = "不分科";

  @Autowired
  private OP_DDao opdDao;

  @Autowired
  private OP_TDao optDao;

  @Autowired
  private IP_TDao iptDao;

  @Autowired
  private IP_DDao ipdDao;

  @Autowired
  private IP_PDao ippDao;

  @Autowired
  private OP_PDao oppDao;

  @Autowired
  private MRDao mrDao;

  @Autowired
  private ASSIGNED_POINTDao assignedPointDao;

  @Autowired
  private POINT_MONTHLYDao pointMonthlyDao;

  @Autowired
  private CodeTableService codeTableService;

  @Autowired
  private POINT_WEEKLYDao pointWeeklyDao;

  @Autowired
  private DRG_MONTHLYDao drgMonthlyDao;

  @Autowired
  private DRG_WEEKLYDao drgWeeklyDao;

  public PointMRPayload getMonthlyReport(int year, int month) {
    int lastM = year * 100 + month - 1;
    if (month - 1 <= 0) {
      lastM = (year - 1) * 100 + 12;
    }
    PointMRPayload result = new PointMRPayload();
    result.setCurrent(pointMonthlyDao.findByYm(year * 100 + month));
    result.setLastM(pointMonthlyDao.findByYm(lastM));
    result.setLastY(pointMonthlyDao.findByYm((year - 1) * 100 + month));
    if (result.getCurrent() != null) {
      result.calculateDifference();
    }

    return result;
  }

  /**
   * 取得各年月的 IP_T/OP_T id
   * 
   * @param isOP
   * @return
   */
  public HashMap<String, Long> getYMTID(boolean isOP) {
    HashMap<String, Long> result = new HashMap<String, Long>();
    if (isOP) {
      List<OP_T> list = optDao.findAll();
      for (OP_T op_T : list) {
        result.put(op_T.getFeeYm(), op_T.getId());
      }
    } else {
      List<IP_T> list = iptDao.findAll();
      for (IP_T ip_T : list) {
        result.put(ip_T.getFeeYm(), ip_T.getId());
      }
    }

    return result;
  }

  /**
   * 計算指定年月的單月健保點數總表
   * @param ym
   */
  public void calculatePointMR(String ym) {
    String chineseYM = ymToROCYM(ym);
    String adYM = ymToADYM(ym);
    POINT_MONTHLY pm = null;
    POINT_MONTHLY old = pointMonthlyDao.findByYm(Integer.parseInt(adYM));
    if (old == null) {
      pm = new POINT_MONTHLY();
    } else {
      pm = old;
    }
    pm.setYm(Integer.parseInt(adYM));

    long optId = 0;
    long iptId = 0;
    List<OP_T> listOPT = optDao.findByFeeYmOrderById(chineseYM);
    if (listOPT != null && listOPT.size() > 0) {
      optId = listOPT.get(0).getId();
    } else {
      return;
    }

    List<IP_T> listIPT = iptDao.findByFeeYmOrderById(chineseYM);
    if (listIPT != null && listIPT.size() > 0) {
      iptId = listIPT.get(0).getId();
    } else {
      return;
    }
  
     List<Object[]> list = opdDao.findMonthlyPoint(optId, optId, iptId, optId, optId, iptId, optId,
        optId, iptId, optId, chineseYM, chineseYM, chineseYM);
    if (list != null && list.size() > 0) {
      Object[] obj = list.get(0);
      pm.setPartOp(getLongValue(obj[0]));
      pm.setPartEm(getLongValue(obj[1]));
      pm.setPartOpAll(pm.getPartOp() + pm.getPartEm());
      pm.setPartIp(getLongValue(obj[2]));
      pm.setPartAll(pm.getPartOpAll() + pm.getPartIp());

      pm.setApplOp(getLongValue(obj[3]));
      pm.setApplEm(getLongValue(obj[4]));
      pm.setApplOpAll(pm.getApplOp() + pm.getApplEm());
      pm.setApplIp(getLongValue(obj[5]));
      pm.setApplAll(pm.getApplOpAll() + pm.getApplIp());

      pm.setTotalOp(pm.getPartOp() + pm.getApplOp());
      pm.setTotalEm(pm.getPartEm() + pm.getApplEm());
      pm.setTotalOpAll(pm.getTotalOp() + pm.getTotalEm());
      pm.setTotalIp(pm.getPartIp() + pm.getApplIp());
      pm.setTotalAll(pm.getTotalOpAll() + pm.getTotalIp());

      pm.setPatientOp(((BigInteger) obj[6]).longValue());
      pm.setPatientEm(((BigInteger) obj[7]).longValue());
      pm.setPatientIp(((BigInteger) obj[8]).longValue());
      pm.setChronic(getLongValue(obj[9]));

      pm.setIpQuantity(((BigInteger) obj[10]).longValue());
      pm.setDrgQuantity(((BigInteger) obj[11]).longValue());
      pm.setDrgApplPoint(getLongValue(obj[12]));
      pm.setDrgActualPoint(getLongValue(obj[13]));
      pm.setUpdateAt(new Date());

      updateAssignedPoint(pm, adYM);
      if (pm.getAssignedAll() != null) {
        pm.setRemaining(pm.getAssignedAll() - pm.getChronic());
      } else {
        pm.setRemaining(0L);
      }
    }
    pointMonthlyDao.save(pm);
  }

  private void updateAssignedPoint(POINT_MONTHLY pm, String adYM) {
    ASSIGNED_POINT ap = getAssignedPoint(adYM);
    if (ap == null) {
      return;
    }
    pm.setAssignedOpAll(ap.getWmOpPoints());
    pm.setAssignedIp(ap.getWmIpPoints());
    pm.setAssignedAll(ap.getWmp());

    pm.setRateAll(
        cutPointNumber(((double) pm.getTotalAll() * (double) 100) / (double) pm.getAssignedAll()));
    pm.setRateOpAll(
        cutPointNumber(((double) pm.getTotalOp() * (double) 100) / (double) pm.getAssignedOpAll()));
    pm.setRateIp(
        cutPointNumber(((double) pm.getTotalIp() * (double) 100) / (double) pm.getAssignedIp()));
  }

  /**
   * 將double只取小數點後一位
   * 
   * @param d
   * @return
   */
  public static Double cutPointNumber(double d) {
    String s = String.valueOf(d);
    int index = s.indexOf('.');
    if (index > 0 && s.length() - index > 2) {
      return Double.parseDouble(s.substring(0, index + 2));
    }
    return d;
  }

  public ASSIGNED_POINT getAssignedPoint(String adYM) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
    Date date = null;
    try {
      date = sdf.parse(adYM);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    List<ASSIGNED_POINT> list =
        assignedPointDao.findAllByStartDateLessThanEqualAndEndDateGreaterThanEqual(date, date);
    if (list != null && list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  /**
   * 西元年月轉民國年月
   * 
   * @param ym
   * @return
   */
  private String ymToROCYM(String ym) {
    if (ym == null || ym.length() != 6 || !ym.startsWith("20")) {
      return ym;
    }
    return DateTool.convertToChineseYear(ym);
  }

  /**
   * 民國年月轉西元年月
   * 
   * @param ym
   * @return
   */
  private String ymToADYM(String ym) {
    if (ym == null || ym.length() != 5 || !ym.startsWith("1")) {
      return ym;
    }
    return DateTool.convertChineseToAD(ym);
  }

   public PeriodPointPayload getPeriodPoint(Date sdate, Date edate) {
    PeriodPointPayload result = new PeriodPointPayload();
    java.sql.Date s = new java.sql.Date(sdate.getTime());
    java.sql.Date e = new java.sql.Date(edate.getTime());
    List<Object[]> list = opdDao.findPeriodPoint(s, e, s, e, s, e, s, e, s, e, s, e, s, e, s, e, s,
        e, s, e, s, e, s, e, s, e);
    if (list != null && list.size() > 0) {
      Object[] obj = list.get(0);
      result.setQuantityAll(((BigInteger) obj[0]).longValue());
      result.setQuantityOpAll(((BigInteger) obj[1]).longValue());
      result.setQuantityOp(((BigInteger) obj[2]).longValue());
      result.setQuantityEm(((BigInteger) obj[3]).longValue());
      result.setQuantityIp(((BigInteger) obj[4]).longValue());

      result.setApplPointOpAll(getLongValue(obj[5]));
      result.setApplPointOp(getLongValue(obj[6]));
	  result.setApplPointEm(getLongValue(obj[7]));
	  result.setApplPointIp(getLongValue(obj[8]));
      result.setApplPointAll(result.getApplPointOpAll() + result.getApplPointIp());
      result.setPartPointOpAll(getLongValue(obj[9]));
      result.setPartPointOp(getLongValue(obj[10]));
      result.setPartPointEm(getLongValue(obj[11]));
      result.setPartPointIp(getLongValue(obj[12]));
    
      result.setPartPointAll(result.getPartPointOpAll() + result.getPartPointIp());
      result.setPointAll(result.getApplPointAll() + result.getPartPointAll());
      result.setPointEm(result.getApplPointEm() + result.getPartPointEm());
      result.setPointIp(result.getApplPointIp() + result.getPartPointIp());
      result.setPointOp(result.getApplPointOp() + result.getPartPointOp());
      result.setPointOpAll(result.getApplPointOpAll() + result.getPartPointOpAll());
    }

    result.setApplByFuncType(getApplPointGroupByFuncType(s, e));
    result.setPartByFuncType(getPartPointGroupByFuncType(s, e));
    result.setPayByOrderType(getPointGroupByOrderType(s, e));

    // @TESTDATA
    addTestData(result);
    return result;
  }

  private void addTestData(PeriodPointPayload ppp) {
    ppp.setOwnExpAll(ppp.getPointAll() / 5);
    ppp.setOwnExpEm(ppp.getPointEm() / 5);
    ppp.setOwnExpIp(ppp.getPointIp() / 5);
    ppp.setOwnExpOp(ppp.getPointOp() / 5);
    ppp.setOwnExpOpAll(ppp.getPointOpAll() / 5);

    ppp.setNoApplAll(ppp.getPointAll() / 50);
    ppp.setNoApplEm(ppp.getPointEm() / 50);
    ppp.setNoApplIp(ppp.getPointIp() / 50);
    ppp.setNoApplOp(ppp.getPointOp() / 50);
    ppp.setNoApplOpAll(ppp.getPointOpAll() / 50);

    ppp.setOwnExpByFuncType(ppp.getPartByFuncType());
    ppp.setOwnExpByOrderType(ppp.getPayByOrderType());
  }

  public PointQuantityList getApplPointGroupByFuncType(java.sql.Date s, java.sql.Date e) {
    PointQuantityList result = new PointQuantityList();
    // 門急診各科申報總數
    List<Object[]> list = opdDao.findApplPointGroupByFuncType(s, e);
    if (list != null && list.size() > 0) {
      for (Object[] objects : list) {
        NameCodePointQuantity npq = new NameCodePointQuantity();
        npq.setName(codeTableService.getDesc("FUNC_TYPE", (String) objects[0]));
        npq.setCode((String) objects[0]);
        if (objects[1] == null) {
        	npq.setPoint(0L);
        } else if (objects[1] instanceof BigDecimal) {
			BigDecimal dot = (BigDecimal)objects[1];
			npq.setPoint(dot.longValue());
		} else {
        npq.setPoint(((long) (int) objects[1]));
		}
        npq.setQuantity(((BigInteger) objects[2]).longValue());
        result.addOp(npq);
      }
    }
    list = ipdDao.findApplPointGroupByFuncType(s, e);
    if (list != null && list.size() > 0) {
      for (Object[] objects : list) {
        NameCodePointQuantity npq = new NameCodePointQuantity();
        npq.setName(codeTableService.getDesc("FUNC_TYPE", (String) objects[0]));
        npq.setCode((String) objects[0]);
        if (objects[1] == null) {
        	npq.setPoint(0L);
        } else if (objects[1] instanceof BigDecimal) {
			BigDecimal dot = (BigDecimal)objects[1];
			npq.setPoint(dot.longValue());
		} else {
        npq.setPoint(((long) (int) objects[1]));
		}
        npq.setQuantity(((BigInteger) objects[2]).longValue());
        result.addIp(npq);
      }
    }
    return result;
  }

  public PointQuantityList getPartPointGroupByFuncType(java.sql.Date s, java.sql.Date e) {
    PointQuantityList result = new PointQuantityList();
    // 門急診各科申報總數
    List<Object[]> list = opdDao.findPartPointGroupByFuncType(s, e);
    if (list != null && list.size() > 0) {
      for (Object[] objects : list) {
        NameCodePointQuantity npq = new NameCodePointQuantity();
        npq.setName(codeTableService.getDesc("FUNC_TYPE", (String) objects[0]));
        npq.setCode((String) objects[0]);
        if (objects[1] == null) {
        	npq.setPoint(0L);
        } else if (objects[1] instanceof BigDecimal) {
			BigDecimal dot = (BigDecimal)objects[1];
			npq.setPoint(dot.longValue());
		} else {
        npq.setPoint(((long) (int) objects[1]));
		}
        npq.setQuantity(((BigInteger) objects[2]).longValue());
        result.addOp(npq);
      }
    }
    list = ipdDao.findPartPointGroupByFuncType(s, e);
    if (list != null && list.size() > 0) {
      for (Object[] objects : list) {
        NameCodePointQuantity npq = new NameCodePointQuantity();
        npq.setName(codeTableService.getDesc("FUNC_TYPE", (String) objects[0]));
        npq.setCode((String) objects[0]);
        if (objects[1] == null) {
        	npq.setPoint(0L);
        } else if (objects[1] instanceof BigDecimal) {
			BigDecimal dot = (BigDecimal)objects[1];
			npq.setPoint(dot.longValue());
		} else {
        npq.setPoint(((long) (int) objects[1]));
		}
        npq.setQuantity(((BigInteger) objects[2]).longValue());
        result.addIp(npq);
      }
    }
    return result;
  }

  public PointQuantityList getPointGroupByOrderType(java.sql.Date s, java.sql.Date e) {
    PointQuantityList result = new PointQuantityList();
    // 門急診各科申報總數
    List<Object[]> list = oppDao.findPointGroupByPayCodeType(s, e);
    if (list != null && list.size() > 0) {
      for (Object[] objects : list) {
        NameCodePointQuantity npq = new NameCodePointQuantity();
        npq.setName(codeTableService.getDesc("PAY_CODE_TYPE", (String) objects[0]));
        npq.setCode((String) objects[0]);
        if (objects[1] == null) {
        	npq.setPoint(0L);
        } else if (objects[1] instanceof BigDecimal) {
			BigDecimal dot = (BigDecimal)objects[1];
			npq.setPoint(dot.longValue());
		} else {
        npq.setPoint(((long) (int) objects[1]));
		}
        npq.setQuantity(((BigInteger) objects[2]).longValue());
        result.addOp(npq);
      }
    }
    list = ippDao.findPointGroupByPayCodeType(s, e);
    if (list != null && list.size() > 0) {
      for (Object[] objects : list) {
        NameCodePointQuantity npq = new NameCodePointQuantity();
        npq.setName(codeTableService.getDesc("PAY_CODE_TYPE", (String) objects[0]));
        npq.setCode((String) objects[0]);
        if (objects[1] == null) {
        	npq.setPoint(0L);
        } else if (objects[1] instanceof BigDecimal) {
			BigDecimal dot = (BigDecimal)objects[1];
			npq.setPoint(dot.longValue());
		} else {
        npq.setPoint(((long) (int) objects[1]));
		}
        npq.setQuantity(((BigInteger) objects[2]).longValue());
        result.addIp(npq);
      }
    }
    return result;
  }

  public POINT_WEEKLY calculatePointByWeek(Date sdate, Date edate) {
    if (!checkWeekday(sdate, Calendar.SUNDAY) || !checkWeekday(edate, Calendar.SATURDAY)) {
      logger.error("calculatePointByWeek failed");
      return null;
    }

    java.sql.Date s = new java.sql.Date(sdate.getTime());
    java.sql.Date e = new java.sql.Date(edate.getTime());

    POINT_WEEKLY pw = pointWeeklyDao.findByStartDateAndEndDate(s, e);
    if (pw == null) {
      pw = new POINT_WEEKLY();
      pw.setStartDate(sdate);
      pw.setEndDate(edate);
      Calendar cal = Calendar.getInstance();
      cal.setTime(edate);
      pw.setPyear(cal.get(Calendar.YEAR));
      int week = cal.get(Calendar.WEEK_OF_YEAR);
      // if (isFirstDaySunday(pw.getPyear())) {
      // // 若1/1不是週日，則透過 Calendar.WEEK_OF_YEAR抓出來的週數都要減1，因1/1的值被算在上一年的最後一週
      // week--;
      // }
      pw.setPweek(week);
    }
    List<Object[]> list = opdDao.findAllPoint(s, e, s, e);
    if (list != null && list.size() > 0) {
      Object[] object = list.get(0);
      if (object[0] == null || object[1] == null) {
        return null;
      }
      pw.setOp(((long) (int) object[0]));
      pw.setIp(((long) (int) object[1]));
      // @TESTDATA
      pw.setOwnExpIp(pw.getIp().longValue() / 5);
      pw.setOwnExpOp(pw.getOp().longValue() / 5);
      pw.setUpdateAt(new Date());
      return pointWeeklyDao.save(pw);
    }
    return null;
  }
  
  public void calculatePointWeekly(Calendar startCal) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, startCal.get(Calendar.YEAR));
    cal.set(Calendar.MONTH, startCal.get(Calendar.MONTH));
    cal.set(Calendar.DAY_OF_YEAR, startCal.get(Calendar.DAY_OF_YEAR));

    if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
      cal.add(Calendar.DAY_OF_YEAR, Calendar.SUNDAY - cal.get(Calendar.DAY_OF_WEEK));
    }
    List<Object[]> list = mrDao.findDRGAllFuncType();
    List<String> funcTypes = new ArrayList<String>();
    for (Object[] obj : list) {
      funcTypes.add((String) obj[0]);
    }
    //funcTypes.add(0, ReportService.FUNC_TYPE_ALL);
    
    do {
      Date start = cal.getTime();
      cal.add(Calendar.DAY_OF_YEAR, 6);
      Date end = cal.getTime();

      POINT_WEEKLY pw = calculatePointByWeek(start, end);
      if (pw == null || pw.getIp().longValue() + pw.getOp().longValue() == 0) {
        break;
      }
      calculateDRGPointByWeek(start, end, funcTypes);
      System.out.println("year=" + pw.getPyear() + "," + pw.getPweek() + "," + pw.getStartDate() + "," + pw.getEndDate());
      cal.add(Calendar.DAY_OF_YEAR, 1);
    } while (true);
  }

  public void calculateDRGPointByWeek(Date sdate, Date edate, List<String> funcTypes) {
    if (!checkWeekday(sdate, Calendar.SUNDAY) || !checkWeekday(edate, Calendar.SATURDAY)) {
      logger.error("calculatePointByWeek failed");
      return;
    }

    java.sql.Date s = new java.sql.Date(sdate.getTime());
    java.sql.Date e = new java.sql.Date(edate.getTime());

    HashMap<String, String> elapseFuncType = new HashMap<String, String>();
    for (String string : funcTypes) {
      elapseFuncType.put(string, "");
    }

    DRG_WEEKLY drgWeeklyAll = selectOrCreateDrgWeekly(s, e, FUNC_TYPE_ALL);
    List<Object[]> list = mrDao.countDRGPointByStartDateAndEndDate(s, e, s, e, s, e);
    for (Object[] obj : list) {
      String funcType = (String) obj[0];
      elapseFuncType.remove(funcType);
      DRG_WEEKLY drgWeekly = selectOrCreateDrgWeekly(s, e, funcType);
      drgWeekly.setDrgQuantity(((BigInteger) obj[1]).longValue());
      drgWeekly.setDrgPoint(((long) (int) obj[2]));
      drgWeekly.setNondrgQuantity(((BigInteger) obj[4]).longValue());
      drgWeekly.setNondrgPoint(((long) (int) obj[5]));
      drgWeeklyAll.setDrgQuantity(drgWeeklyAll.getDrgQuantity() + drgWeekly.getDrgQuantity());
      drgWeeklyAll.setDrgPoint(drgWeeklyAll.getDrgPoint() + drgWeekly.getDrgPoint());
      drgWeeklyAll
          .setNondrgQuantity(drgWeeklyAll.getNondrgQuantity() + drgWeekly.getNondrgQuantity());
      drgWeeklyAll.setNondrgPoint(drgWeeklyAll.getNondrgPoint() + drgWeekly.getNondrgPoint());

      List<Object[]> sectionList = mrDao.countDRGPointByFuncTypeGroupByDRGSection(s, e, funcType);
      for (Object[] obj2 : sectionList) {
        if ("A".equals((String) obj2[0])) {
          drgWeekly.setSectionA(((BigInteger) obj[1]).longValue());
          drgWeeklyAll.setSectionA(drgWeeklyAll.getSectionA() + drgWeekly.getSectionA());
        } else if ("B1".equals((String) obj2[0])) {
          drgWeekly.setSectionB1(((BigInteger) obj[1]).longValue());
          drgWeeklyAll.setSectionB1(drgWeeklyAll.getSectionB1() + drgWeekly.getSectionB1());
        } else if ("B2".equals((String) obj2[0])) {
          drgWeekly.setSectionB2(((BigInteger) obj[1]).longValue());
          drgWeeklyAll.setSectionB2(drgWeeklyAll.getSectionB2() + drgWeekly.getSectionB2());
        } else if ("C".equals((String) obj2[0])) {
          drgWeekly.setSectionC(((BigInteger) obj[1]).longValue());
          drgWeeklyAll.setSectionC(drgWeeklyAll.getSectionC() + drgWeekly.getSectionC());
        }
      }
      drgWeeklyDao.save(drgWeekly);
    }
    drgWeeklyDao.save(drgWeeklyAll);
    processElapseFuncTypeWeekly(s, e, elapseFuncType.keySet());
  }

  private void processElapseFuncTypeWeekly(java.sql.Date startDate, java.sql.Date endDate,
      Set<String> elapseFuncTypes) {
    List<Object[]> list = mrDao.countNonDRGPointByStartDateAndEndDate(startDate, endDate);
    for (Object[] obj : list) {
      String funcType = (String) obj[0];
      if (!elapseFuncTypes.contains(funcType)) {
        continue;
      }
      DRG_WEEKLY drgWeekly = selectOrCreateDrgWeekly(startDate, endDate, funcType);
      drgWeekly.setDrgQuantity(0L);
      drgWeekly.setDrgPoint(0L);
      drgWeekly.setNondrgQuantity(((BigInteger) obj[1]).longValue());
      drgWeekly.setNondrgPoint(((long) (int) obj[2]));
      drgWeekly.setSectionA(0L);
      drgWeekly.setSectionB1(0L);
      drgWeekly.setSectionB2(0L);
      drgWeekly.setSectionC(0L);
      drgWeeklyDao.save(drgWeekly);
    }
  }

  private DRG_WEEKLY selectOrCreateDrgWeekly(java.sql.Date startDate, java.sql.Date endDate,
      String funcType) {
    DRG_WEEKLY result =
        drgWeeklyDao.findByFuncTypeAndStartDateAndEndDate(funcType, startDate, endDate);
    if (result == null) {
      result = new DRG_WEEKLY();
      result.setFuncType(funcType);
      result.setStartDate(startDate);
      result.setEndDate(endDate);
      Calendar cal = Calendar.getInstance();
      cal.setTime(endDate);
      result.setPyear(cal.get(Calendar.YEAR));
      int week = cal.get(Calendar.WEEK_OF_YEAR);
      result.setPweek(week);
    }
    return result;
  }

  private boolean checkWeekday(Date date, int weekday) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.DAY_OF_WEEK) == weekday;
  }

  public static boolean isFirstDaySunday(int year) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.MONTH, Calendar.JANUARY);
    return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
  }


  public PeriodPointWeeklyPayload getPeroidPointWeekly(Date edate) {
    PeriodPointWeeklyPayload result = new PeriodPointWeeklyPayload();
    java.sql.Date e = new java.sql.Date(edate.getTime());
    List<POINT_WEEKLY> list = pointWeeklyDao.findByEndDateLessThanEqualOrderByEndDateDesc(e);
    int count = 0;
    for (POINT_WEEKLY pw : list) {
      String name = pw.getPyear() + " w" + pw.getPweek();
      result.getIp().add(name, pw.getIp());
      result.getOp().add(name, pw.getOp());
      result.getOwnExpIp().add(name, pw.getOwnExpIp());
      result.getOwnExpOp().add(name, pw.getOwnExpOp());
      count++;
      if (count >= 52) {
        break;
      }
    }
    return result;
  }

  public List<String> getAllDRGFuncTypes(java.sql.Date startDate, java.sql.Date endDate) {
    List<String> result = new ArrayList<String>();
    List<Object[]> list = mrDao.findDRGDistinctFuncTypeByDate(startDate, endDate);
    for (Object[] objects : list) {
      result.add((String) objects[0]);
    }
    return result;
  }

  public List<String> getAllDRGFuncTypes(String ym) {
    List<String> result = new ArrayList<String>();
    List<Object[]> list = mrDao.findDRGDistinctFuncTypeByApplYm(ym);
    for (Object[] objects : list) {
      result.add((String) objects[0]);
    }
    return result;
  }

  public void calculateDRGMonthly(String ym) {
    String chineseYM = ymToROCYM(ym);
    String adYM = ymToADYM(ym);

    DRG_MONTHLY drgMonthlyAll =
        drgMonthlyDao.findByYmAndFuncType(Integer.parseInt(adYM), FUNC_TYPE_ALL);
    if (drgMonthlyAll == null) {
      drgMonthlyAll = new DRG_MONTHLY();
    }
    drgMonthlyAll.setYm(Integer.parseInt(adYM));
    drgMonthlyAll.setFuncType(FUNC_TYPE_ALL);

    List<String> funcTypes = getAllDRGFuncTypes(chineseYM);
    for (String funcType : funcTypes) {
      DRG_MONTHLY pm = null;
      DRG_MONTHLY old = drgMonthlyDao.findByYmAndFuncType(Integer.parseInt(adYM), funcType);
      if (old == null) {
        pm = new DRG_MONTHLY();
      } else {
        pm = old;
      }
      pm.setYm(Integer.parseInt(adYM));
      pm.setFuncType(funcType);
      List<Object[]> list = mrDao.findDRGCountAndDotByApplYmGroupByDrgSection(chineseYM, funcType);
      if (list != null && list.size() > 0) {
        for (Object[] obj : list) {
          String section = (String) obj[0];
          if ("A".equals(section)) {
            pm.setSectionA(((BigInteger) obj[1]).longValue());
            pm.setSectionAAppl(((long) (int) obj[2]));
            pm.setSectionAActual(((long) (int) obj[3]));
          } else if ("B1".equals(section)) {
            pm.setSectionB1(((BigInteger) obj[1]).longValue());
            pm.setSectionB1Appl(((long) (int) obj[2]));
            pm.setSectionB1Actual(((long) (int) obj[3]));
          } else if ("B2".equals(section)) {
            pm.setSectionB2(((BigInteger) obj[1]).longValue());
            pm.setSectionB2Appl(((long) (int) obj[2]));
            pm.setSectionB2Actual(((long) (int) obj[3]));
          } else if ("C".equals(section)) {
            pm.setSectionC(((BigInteger) obj[1]).longValue());
            pm.setSectionCAppl(((long) (int) obj[2]));
            pm.setSectionCActual(((long) (int) obj[3]));
          }
        }

        drgMonthlyAll.setSectionA(drgMonthlyAll.getSectionA() + pm.getSectionA());
        drgMonthlyAll.setSectionB1(drgMonthlyAll.getSectionB1() + pm.getSectionB1());
        drgMonthlyAll.setSectionB2(drgMonthlyAll.getSectionB2() + pm.getSectionB2());
        drgMonthlyAll.setSectionC(drgMonthlyAll.getSectionC() + pm.getSectionC());

        drgMonthlyAll.setSectionAAppl(drgMonthlyAll.getSectionAAppl() + pm.getSectionAAppl());
        drgMonthlyAll.setSectionB1Appl(drgMonthlyAll.getSectionB1Appl() + pm.getSectionB1Appl());
        drgMonthlyAll.setSectionB2Appl(drgMonthlyAll.getSectionB2Appl() + pm.getSectionB2Appl());
        drgMonthlyAll.setSectionCAppl(drgMonthlyAll.getSectionCAppl() + pm.getSectionCAppl());

        drgMonthlyAll.setSectionAActual(drgMonthlyAll.getSectionAActual() + pm.getSectionAActual());
        drgMonthlyAll
            .setSectionB1Actual(drgMonthlyAll.getSectionB1Actual() + pm.getSectionB1Actual());
        drgMonthlyAll
            .setSectionB2Actual(drgMonthlyAll.getSectionB2Actual() + pm.getSectionB2Actual());
        drgMonthlyAll.setSectionCActual(drgMonthlyAll.getSectionCActual() + pm.getSectionCActual());
      }
      pm.setUpdateAt(new Date());
      drgMonthlyDao.save(pm);
    }
    drgMonthlyAll.setUpdateAt(new Date());
    drgMonthlyDao.save(drgMonthlyAll);
  }

  private java.sql.Date getLastDayOfMonth(int year, int month) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTime(sdf.parse(String.valueOf(year * 100 + month)));
      cal.set(Calendar.DAY_OF_MONTH, 1);
      cal.add(Calendar.MONTH, 1);
      cal.add(Calendar.DAY_OF_YEAR, -1);
      return new java.sql.Date(cal.getTimeInMillis());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  private List<String> convertFuncTypeToName(List<String> funcTypes) {
    List<String> result = new ArrayList<String>();
    for (int i = 0; i < funcTypes.size(); i++) {
      result.add(codeTableService.getDesc("FUNC_TYPE", funcTypes.get(i)));
    }
    return result;
  }

  public DRGMonthlyPayload getDrgMonthly(int year, int month) {
    DRGMonthlyPayload result = new DRGMonthlyPayload(pointMonthlyDao.findByYm(year * 100 + month));
    result.getFuncTypes().add(FUNC_TYPE_ALL_NAME);
    java.sql.Date lastDay = getLastDayOfMonth(year, month);
    addQuantityAndPoint(result, FUNC_TYPE_ALL, FUNC_TYPE_ALL_NAME, lastDay);
    return result;
  }

  public DRGMonthlyPayload getDrgMonthlyAllFuncType(int year, int month) {
    DRGMonthlyPayload result = new DRGMonthlyPayload(pointMonthlyDao.findByYm(year * 100 + month));
    List<String> funcTypes = getAllDRGFuncTypes(String.valueOf((year - 1911) * 100 + month));
    funcTypes.add(0, FUNC_TYPE_ALL);
    List<String> funcTypeName = convertFuncTypeToName(funcTypes);
    result.setFuncTypes(funcTypeName);
    java.sql.Date lastDay = getLastDayOfMonth(year, month);

    for (int i = 0; i < funcTypes.size(); i++) {
      addQuantityAndPoint(result, funcTypes.get(i), funcTypeName.get(i), lastDay);
    }

    return result;
  }

  private void addQuantityAndPoint(DRGMonthlyPayload payload, String funcType, String funcTypeName,
      java.sql.Date lastDay) {

    List<DRG_WEEKLY> list =
        drgWeeklyDao.findByFuncTypeAndEndDateLessThanEqualOrderByEndDateDesc(funcType, lastDay);
    if (list != null && list.size() > 0) {
      int count = 1;
      for (DRG_WEEKLY dw : list) {
        String name = dw.getPyear() + " w" + dw.getPweek();
        payload.getQuantityList(funcType, funcTypeName).add(name, dw.getDrgQuantity(),
            dw.getNondrgQuantity());
        payload.getPointList(funcType, funcTypeName).add(name, dw.getDrgPoint(), dw.getNondrgPoint());
        count++;
        if (count >= 52) {
          break;
        }
      }
    }
  }

  public DRGMonthlySectionPayload getDrgMonthlySection(int year, int month) {
    DRGMonthlySectionPayload result =
        new DRGMonthlySectionPayload(pointMonthlyDao.findByYm(year * 100 + month));
    List<String> funcTypes = getAllDRGFuncTypes(String.valueOf((year - 1911) * 100 + month));
    funcTypes.add(0, FUNC_TYPE_ALL);
    List<String> funcTypeNames = convertFuncTypeToName(funcTypes);
    result.setFuncTypes(funcTypeNames);

    java.sql.Date lastDay = getLastDayOfMonth(year, month);

    for (int i = 0; i < funcTypes.size(); i++) {
      String funcTypeName = codeTableService.getDesc("FUNC_TYPE", funcTypes.get(i));
      DRG_MONTHLY dm = drgMonthlyDao.findByYmAndFuncType(year * 100 + month, funcTypes.get(i));
      if (funcTypes.get(i).equals(FUNC_TYPE_ALL)) {
        result.setActualA(dm.getSectionAActual());
        result.setActualB1(dm.getSectionB1Actual());
        result.setActualB2(dm.getSectionB2Actual());
        result.setActualC(dm.getSectionCActual());

        result.setApplA(dm.getSectionAAppl());
        result.setApplB1(dm.getSectionB1Appl());
        result.setApplB2(dm.getSectionB2Appl());
        result.setApplC(dm.getSectionCAppl());

        result.setDiffA(result.getApplA() - result.getActualA());
        result.setDiffB1(result.getApplB1() - result.getActualB1());
        result.setDiffB2(result.getApplB2() - result.getActualB2());
        result.setDiffC(result.getApplC() - result.getActualC());

        result.setQuantityA(dm.getSectionA());
        result.setQuantityB1(dm.getSectionB1());
        result.setQuantityB2(dm.getSectionB2());
        result.setQuantityC(dm.getSectionC());
      } else {
        NameCodePointQuantity ncpqA = new NameCodePointQuantity();
        ncpqA.setCode(funcTypes.get(i));
        ncpqA.setName(funcTypeName);
        ncpqA.setPoint(dm.getSectionAAppl());
        ncpqA.setQuantity(dm.getSectionA());
        result.getSectionA().add(ncpqA);

        NameCodePointQuantity ncpqB1 = new NameCodePointQuantity();
        ncpqB1.setCode(funcTypes.get(i));
        ncpqB1.setName(funcTypeName);
        ncpqB1.setPoint(dm.getSectionB1Appl());
        ncpqB1.setQuantity(dm.getSectionB1());
        result.getSectionB1().add(ncpqB1);

        NameCodePointQuantity ncpqB2 = new NameCodePointQuantity();
        ncpqB2.setCode(funcTypes.get(i));
        ncpqB2.setName(funcTypeName);
        ncpqB2.setPoint(dm.getSectionB2Appl());
        ncpqB2.setQuantity(dm.getSectionB2());
        result.getSectionB2().add(ncpqB2);

        NameCodePointQuantity ncpqC = new NameCodePointQuantity();
        ncpqC.setCode(funcTypes.get(i));
        ncpqC.setName(funcTypeName);
        ncpqC.setPoint(dm.getSectionCAppl());
        ncpqC.setQuantity(dm.getSectionC());
        result.getSectionC().add(ncpqC);

        NameCodePoint ncpB1 = new NameCodePoint();
        ncpB1.setCode(funcTypes.get(i));
        ncpB1.setName(funcTypeName);
        ncpB1.setPoint(dm.getSectionB1Appl() - dm.getSectionB1Actual());
        result.getDiffB1FuncType().add(ncpB1);

        NameCodePoint ncpB2 = new NameCodePoint();
        ncpB2.setCode(funcTypes.get(i));
        ncpB2.setName(funcTypeName);
        ncpB2.setPoint(dm.getSectionB2Appl() - dm.getSectionB2Actual());
        result.getDiffB2FuncType().add(ncpB2);

        NameCodePoint ncpC = new NameCodePoint();
        ncpC.setCode(funcTypes.get(i));
        ncpC.setName(funcTypeName);
        ncpC.setPoint(dm.getSectionCAppl() - dm.getSectionCActual());
        result.getDiffCFuncType().add(ncpC);
      }
      getDrgSectionWeekly(result, funcTypes.get(i), funcTypeName, lastDay);
    }
  

    return result;
  }

  private void getDrgSectionWeekly(DRGMonthlySectionPayload payload, String funcType,
      String funcTypeName, java.sql.Date lastDay) {
    List<DRG_WEEKLY> list =
        drgWeeklyDao.findByFuncTypeAndEndDateLessThanEqualOrderByEndDateDesc(funcType, lastDay);
    if (list != null && list.size() > 0) {
      int count = 1;
      NameValueList nvlA = new NameValueList();
      NameValueList nvlB1 = new NameValueList();
      NameValueList nvlB2 = new NameValueList();
      NameValueList nvlC = new NameValueList();
      for (DRG_WEEKLY dw : list) {
        String name = dw.getPyear() + " w" + dw.getPweek();
        nvlA.add(name, dw.getSectionA());
        nvlB1.add(name, dw.getSectionB1());
        nvlB2.add(name, dw.getSectionB2());
        nvlC.add(name, dw.getSectionC());
        count++;
        if (count >= 52) {
          break;
        }
      }
      payload.getWeeklyAMap().put(funcTypeName, nvlA);
      payload.getWeeklyB1Map().put(funcTypeName, nvlB1);
      payload.getWeeklyB2Map().put(funcTypeName, nvlB2);
      payload.getWeeklyCMap().put(funcTypeName, nvlC);
    }
  }
  
  public static long getLongValue(Object obj) {
	  if (obj == null) {
		  return 0L;
	  }
	  if (obj instanceof BigDecimal) {
		  BigDecimal dot = (BigDecimal) obj;
		  return dot.longValue();
	  }
	  return (long) (int) obj;
  }
}
