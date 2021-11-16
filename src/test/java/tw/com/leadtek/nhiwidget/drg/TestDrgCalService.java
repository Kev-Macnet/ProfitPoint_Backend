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
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.model.DrgCalculate;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_WEEKLY;
import tw.com.leadtek.nhiwidget.service.DrgCalService;
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
    for (int i = 1; i < 10; i++) {
      reportService.calculatePointMR("1080" + i);
    }
    for (int i = 10; i < 13; i++) {
      reportService.calculatePointMR("108" + i);
    }
    for (int i = 1; i < 10; i++) {
      reportService.calculatePointMR("1090" + i);
    }
    for (int i = 10; i < 13; i++) {
      reportService.calculatePointMR("109" + i);
    }
    for (int i = 1; i < 10; i++) {
      reportService.calculatePointMR("1100" + i);
    }
  }
  
  /**
   * 更新 DRG_MONTHLY table(DRG每月各科各區點數合計))的值 
   */
  //@Ignore
  @Test
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
}
