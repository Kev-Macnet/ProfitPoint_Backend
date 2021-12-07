/**
 * Created on 2021/9/1.
 */
package tw.com.leadtek.nhiwidget.drg;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.IP_TDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.OP_TDao;
import tw.com.leadtek.nhiwidget.model.DrgCalculate;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.IP_P;
import tw.com.leadtek.nhiwidget.model.rdb.IP_T;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.nhiwidget.model.rdb.OP_P;
import tw.com.leadtek.nhiwidget.model.rdb.OP_T;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_WEEKLY;
import tw.com.leadtek.nhiwidget.service.DrgCalService;
import tw.com.leadtek.nhiwidget.service.IntelligentService;
import tw.com.leadtek.nhiwidget.service.ReportService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class TestDrgCalService {

  @Autowired
  private IP_DDao ipdDao;

  @Autowired
  private MRDao mrDao;

  @Autowired
  private DrgCalService drgCalService;

  @Autowired
  private ReportService reportService;
  
  @Autowired
  private OP_TDao optDao;
  
  @Autowired
  private IP_TDao iptDao;
  
  @Autowired
  private OP_DDao opdDao;
  
  @Autowired
  private OP_PDao oppDao;
  
  @Autowired
  private IP_PDao ippDao;

  @Autowired
  private IntelligentService is;
  
  /**
   * 計算所有住院病歷的 DRG 代碼、區間、定額
   */
  @Ignore
  @Test
  public void testDrgApplDot() {
    long maxID = 0;
    int count = 1;
    List<IP_D> ipds = ipdDao.findAllWithDRG(maxID);
    // 暫存用，避免重複讀DB
    HashMap<String, HashMap<String, List<Long>>> countCase20 =
        new HashMap<String, HashMap<String, List<Long>>>();
    for (IP_D ipd : ipds) {
      Optional<MR> optional = mrDao.findById(ipd.getMrId());
      if (!optional.isPresent()) {
        continue;
      }
      MR mr = optional.get();
      int addChild =
          drgCalService.getAddChild(ipd.getNbBirthday(), ipd.getIdBirthYmd(), ipd.getInDate());
      
      DrgCalculate drg = drgCalService.getDRGSection(ipd.getTwDrgCode(), mr.getApplYm(),
          ipd.getMedDot(), addChild);
      if (drg == null) {
        System.out.println(ipd.getTwDrgCode() + " not found on " + mr.getApplYm());
        continue;
      }
      boolean isInCase20 = checkCase20(drg, countCase20, mr.getApplYm());

      int applDot =
          drgCalService.getApplDot(drg, ipd.getMedDot(), ipd.getPartDot(), ipd.getNonApplDot(),
              mr.getId(), ipd.getEBedDay() + ipd.getSBedDay(), ipd.getTranCode(), isInCase20);
      if (applDot != ipd.getApplDot().intValue()) {
        System.out.println("id=" + ipd.getId() + "," + applDot + "<>" + ipd.getApplDot() + ",Fix="
            + drg.getFixed() + ",upper=" + drg.getUlimit() + ", lower=" + drg.getLlimit());
        //break;
      } else {
        System.out.println("pass " + (count++));
        mr.setDrgFixed(drg.getFixed());
        mr.setDrgSection(drg.getSection());
        mr.setDrgCode(drg.getCode());
        mrDao.save(mr);
      }
    }
  }

  public boolean checkCase20(DrgCalculate drg,
      HashMap<String, HashMap<String, List<Long>>> countCase20, String ym) {
    if (drg.isCase20()) {
      HashMap<String, List<Long>> case20 = countCase20.get(ym);
      if (case20 == null) {
        case20 = drgCalService.countCase20(ym);
        countCase20.put(ym, case20);
      }
      List<Long> ids = case20.get(drg.getCode());
      if (ids == null) {
        return true;
      } else {
        return ids.size() < 20;
      }
    }
    return false;
  }

  /**
   * 更新 POINT_MONTHLY table(健保點數月報表)的值 
   */
  @Ignore
  @Test
  public void calculatePointMonthly() {
//    for (int i = 1; i < 10; i++) {
//      reportService.calculatePointMR("1080" + i);
//    }
//    for (int i = 10; i < 13; i++) {
//      reportService.calculatePointMR("108" + i);
//    }
//    for (int i = 1; i < 10; i++) {
//      reportService.calculatePointMR("1090" + i);
//    }
//    for (int i = 10; i < 13; i++) {
//      reportService.calculatePointMR("109" + i);
//    }
//    for (int i = 1; i < 10; i++) {
//      reportService.calculatePointMR("1100" + i);
//    }
    for (int i = 11; i < 13; i++) {
      reportService.calculatePointMR("110" + i);
    }
  }
  
  /**
   * 更新 DRG_MONTHLY table(DRG每月各科各區點數合計))的值 
   */
  //@Ignore
  //@Test
  public void calculateDRGMonthly() {
    for (int i = 1; i < 10; i++) {
      reportService.calculateDRGMonthly("1080" + i);
    }
    for (int i = 10; i < 13; i++) {
      reportService.calculateDRGMonthly("108" + i);
    }
    for (int i = 1; i < 10; i++) {
      reportService.calculateDRGMonthly("1090" + i);
    }
    for (int i = 10; i < 13; i++) {
      reportService.calculateDRGMonthly("109" + i);
    }
    for (int i = 1; i < 10; i++) {
      reportService.calculateDRGMonthly("1100" + i);
    }
  }

  @Ignore
  @Test
  public void calculateWeekly() {
    // start date : 2019/01/01
    Calendar cal = Calendar.getInstance();
    int year = 2019;
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, 0);
    cal.set(Calendar.DAY_OF_YEAR, 1);

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

      POINT_WEEKLY pw = reportService.calculatePointByWeek(start, end);
      if (pw == null || pw.getIp().longValue() + pw.getOp().longValue() == 0) {
        break;
      }
      reportService.calculateDRGPointByWeek(start, end, funcTypes);
      System.out.println("year=" + pw.getPyear() + "," + pw.getPweek() + "," + pw.getStartDate() + "," + pw.getEndDate());
      cal.add(Calendar.DAY_OF_YEAR, 1);
    } while (true);
  }
  
  @Ignore
  @Test
  public void initialT() {
    OP_T opt = new OP_T();
    opt.setDataFormat("10");
    opt.setHospId("1532011154");
    opt.setFeeYm("11012");
    opt.setApplMode("2");
    opt.setApplType("2");
    opt.setApplDate("1110101");
    opt.setUpdateAt(new Date());
    optDao.save(opt);
    opt = new OP_T();
    opt.setDataFormat("10");
    opt.setHospId("1532011154");
    opt.setFeeYm("11011");
    opt.setApplMode("2");
    opt.setApplType("2");
    opt.setApplDate("1101201");
    opt.setUpdateAt(new Date());
    optDao.save(opt);
    
    IP_T ipt = new IP_T();
    ipt.setDataFormat("20");
    ipt.setHospId("1532011154");
    ipt.setFeeYm("11012");
    ipt.setApplMode("2");
    ipt.setApplType("1");
    ipt.setApplDate("1110101");
    ipt.setUpdateAt(new Date());
    iptDao.save(ipt);
    
    ipt = new IP_T();
    ipt.setDataFormat("20");
    ipt.setHospId("1532011154");
    ipt.setFeeYm("11011");
    ipt.setApplMode("2");
    ipt.setApplType("1");
    ipt.setApplDate("1101201");
    ipt.setUpdateAt(new Date());
    iptDao.save(ipt);
  }
  
  @Ignore
  @Test
  public void initOPMRICD() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, 1);
    java.sql.Date endDate = new java.sql.Date(cal.getTimeInMillis());
    cal.add(Calendar.MONTH, -14);
    java.sql.Date startDate = new java.sql.Date(cal.getTimeInMillis());
    List<MR> list = mrDao.findByDataFormatAndMrDateBetween("10", startDate, endDate);
    for (MR mr : list) {
      List<OP_D> opdList = opdDao.findByMrId(mr.getId());
      for (OP_D opd : opdList) {
        mr.setIcdcm1(opd.getIcdCm1());
        mr.setIcdcmOthers(getIcdcmOtherOP(opd));
        mr.setIcdpcs(getIcdpcsOP(opd));
        getIcdAll(mr);
      }
      List<OP_P> oppList = oppDao.findByMrId(mr.getId());
      StringBuffer sb = new StringBuffer(",");
      for(OP_P opp : oppList) {
        if (opp.getDrugNo() != null) {
          sb.append(opp.getDrugNo());
          sb.append(",");
        }
      }
      if (sb.length() > 1) {
        mr.setCodeAll(sb.toString());
      }
      mrDao.save(mr);
    }
  }

  @Ignore
  @Test
  public void initIPMRICD() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, 1);
    java.sql.Date endDate = new java.sql.Date(cal.getTimeInMillis());
    cal.add(Calendar.MONTH, -14);
    java.sql.Date startDate = new java.sql.Date(cal.getTimeInMillis());
    List<MR> list = mrDao.findByDataFormatAndMrDateBetween("20", startDate, endDate);
    for (MR mr : list) {
      List<IP_D> ipdList = ipdDao.findByMrId(mr.getId());
      for (IP_D ipd : ipdList) {
        mr.setIcdcm1(ipd.getIcdCm1());
        mr.setIcdcmOthers(getIcdcmOtherIP(ipd));
        mr.setIcdpcs(getIcdpcsIP(ipd));
        getIcdAll(mr);
      }
      List<IP_P> ippList = ippDao.findByMrId(mr.getId());
      StringBuffer sb = new StringBuffer(",");
      for(IP_P ipp : ippList) {
        if (ipp.getOrderCode() != null) {
          sb.append(ipp.getOrderCode());
          sb.append(",");
        }
      }
      if (sb.length() > 1) {
        mr.setCodeAll(sb.toString());
      }
      mrDao.save(mr);
    }
  }
  
  private String getIcdcmOtherOP(OP_D opd) {
    StringBuffer sb = new StringBuffer(",");
    appendString(sb, opd.getIcdCm2());
    appendString(sb, opd.getIcdCm3());
    appendString(sb, opd.getIcdCm4());
    appendString(sb, opd.getIcdCm5());
    if (sb.length() > 1) {
      return sb.toString();
    }
    return null;
  }
  
  private String getIcdcmOtherIP(IP_D ipd) {
    StringBuffer sb = new StringBuffer(",");
    appendString(sb, ipd.getIcdCm2());
    appendString(sb, ipd.getIcdCm3());
    appendString(sb, ipd.getIcdCm4());
    appendString(sb, ipd.getIcdCm5());
    appendString(sb, ipd.getIcdCm6());
    appendString(sb, ipd.getIcdCm7());
    appendString(sb, ipd.getIcdCm8());
    appendString(sb, ipd.getIcdCm9());
    appendString(sb, ipd.getIcdCm10());
    appendString(sb, ipd.getIcdCm11());
    appendString(sb, ipd.getIcdCm12());
    appendString(sb, ipd.getIcdCm13());
    appendString(sb, ipd.getIcdCm14());
    appendString(sb, ipd.getIcdCm15());
    appendString(sb, ipd.getIcdCm16());
    appendString(sb, ipd.getIcdCm17());
    appendString(sb, ipd.getIcdCm18());
    appendString(sb, ipd.getIcdCm19());
    appendString(sb, ipd.getIcdCm20());
    if (sb.length() > 1) {
      return sb.toString();
    }
    return null;
  }
  
  private String getIcdpcsOP(OP_D opd) {
    StringBuffer sb = new StringBuffer(",");
    appendString(sb, opd.getIcdOpCode1());
    appendString(sb, opd.getIcdOpCode2());
    appendString(sb, opd.getIcdOpCode3());
    if (sb.length() > 1) {
      return sb.toString();
    }
    return null;
  }
  
  private String getIcdpcsIP(IP_D opd) {
    StringBuffer sb = new StringBuffer(",");
    appendString(sb, opd.getIcdOpCode1());
    appendString(sb, opd.getIcdOpCode2());
    appendString(sb, opd.getIcdOpCode3());
    appendString(sb, opd.getIcdOpCode4());
    appendString(sb, opd.getIcdOpCode5());
    appendString(sb, opd.getIcdOpCode6());
    appendString(sb, opd.getIcdOpCode7());
    appendString(sb, opd.getIcdOpCode8());
    appendString(sb, opd.getIcdOpCode9());
    appendString(sb, opd.getIcdOpCode10());
    appendString(sb, opd.getIcdOpCode11());
    appendString(sb, opd.getIcdOpCode12());
    appendString(sb, opd.getIcdOpCode13());
    appendString(sb, opd.getIcdOpCode14());
    appendString(sb, opd.getIcdOpCode15());
    appendString(sb, opd.getIcdOpCode16());
    appendString(sb, opd.getIcdOpCode17());
    appendString(sb, opd.getIcdOpCode18());
    appendString(sb, opd.getIcdOpCode19());
    appendString(sb, opd.getIcdOpCode20());
    if (sb.length() > 1) {
      return sb.toString();
    }
    return null;
  }
  
  private void getIcdAll(MR mr) {
    StringBuffer sb = new StringBuffer(",");
    if (mr.getIcdcm1() != null) {
      sb.append(mr.getIcdcm1());
      sb.append(",");
    }
    if (mr.getIcdcmOthers() != null) {
      if (sb.charAt(sb.length() - 1) == ',') {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append(mr.getIcdcmOthers());
    }
    if (mr.getIcdpcs() != null) {
      if (sb.charAt(sb.length() - 1) == ',') {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append(mr.getIcdpcs());
    }
    if (sb.length() > 1) {
      mr.setIcdAll(sb.toString());
    } else {
      mr.setIcdAll(null);
    }
  }
  
  private void appendString(StringBuffer sb, String s) {
    if (s != null) {
      sb.append(s);
      sb.append(",");
    }
  }
  
  //@Ignore
  @Test
  public void calculateRareIcd() {
//    is.calculateRareICD();
//    is.calculateInfectious("11011");
//    is.calculateInfectious("11012");
    
    //is.calculateHighRatio();
    is.testCount();
  }
}
