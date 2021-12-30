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
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import tw.com.leadtek.nhiwidget.payload.MO;
import tw.com.leadtek.nhiwidget.payload.MRDetail;
import tw.com.leadtek.nhiwidget.service.CodeTableService;
import tw.com.leadtek.nhiwidget.service.DrgCalService;
import tw.com.leadtek.nhiwidget.service.IntelligentService;
import tw.com.leadtek.nhiwidget.service.NHIWidgetXMLService;
import tw.com.leadtek.nhiwidget.service.ReportService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class TestDrgCalService {

  private Logger logger = LogManager.getLogger();
  
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
  
  @Autowired
  private NHIWidgetXMLService nhiService;
  
  @Autowired
  private CodeTableService codeTableService;
  
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
        MRDetail.updateIcdcmOtherOP(mr, opd);
        MRDetail.updateIcdpcsOP(mr, opd);
        MRDetail.updateIcdAll(mr);
      }
      MRDetail.updateCodeAllOP(mr, oppDao.findByMrId(mr.getId()));
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
        MRDetail.updateIcdcmOtherIP(mr, ipd);
        MRDetail.updateIcdpcsIP(mr, ipd);
        MRDetail.updateIcdAll(mr);
      }
      MRDetail.updateCodeAllIP(mr, ippDao.findByMrId(mr.getId()));
      mrDao.save(mr);
    }
  }
  
  @Ignore
  @Test
  public void calculateRareIcd() {
//    is.calculateRareICD();
//    is.calculateInfectious("11011");
    //is.calculateInfectious("11012");
    
    //is.calculateHighRatio();
    //is.calculateOverAmount();
    //is.testCount();
  }
  
  /**
   * 測試門診點數計算邏輯是否有誤
   */
  @Ignore
  @Test
  public void calculateOPApplyPoints() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, 2018);
    
    List<MR> list = mrDao.findByDataFormatAndMrDateBetween("10", new java.sql.Date(cal.getTimeInMillis()), new java.sql.Date(System.currentTimeMillis()));
    //MR mr = mrDao.findById(1285853L).orElse(null);
    for (MR mr : list) {
      //System.out.println("mr id=" + mr.getId() + ", did=" + mr.getdId());
      List<OP_D> opdList = opdDao.findByMrId(mr.getId());
      OP_D opd = opdList.get(0);
   
      NHIWidgetXMLService.initialOP_DDot(opd);
      int drugDot = opd.getDrugDot().intValue();
      int treatDot = opd.getTreatDot().intValue();
      int metrDot = opd.getMetrDot().intValue();
      int diagDot = opd.getDiagDot().intValue();
      int dsvcDot = opd.getDsvcDot().intValue();
      MRDetail mrDetail = new MRDetail(mr);
      
      mrDetail.setOPDData(opd, codeTableService);

      List<OP_P> oppList = oppDao.findByOpdIdOrderByOrderSeqNo(opd.getId());
      List<MO> moList = new ArrayList<MO>();
      for (OP_P op_P : oppList) {
        MO mo = new MO();
        mo.setOPPData(op_P, codeTableService);
        moList.add(mo);
      }
      mrDetail.setMos(moList);
      
      nhiService.updateOPPByMrDetail(mr, opd, mrDetail, false);
      
      if (drugDot != opd.getDrugDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", drugDot " + drugDot + " != " + opd.getDrugDot());
       // break;
      }
      if (treatDot != opd.getTreatDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", treatDot " + treatDot + " != " + opd.getTreatDot());
       // break;
      }
      if (metrDot != opd.getMetrDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", metrDot " + metrDot + " != " + opd.getMetrDot());
       // break;
      }
      if (diagDot != opd.getDiagDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", diagDot " + diagDot + " != " + opd.getDiagDot());
      //  break;
      }
      if (dsvcDot != opd.getDsvcDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", dsvcDot " + dsvcDot + " != " + opd.getDsvcDot());
       // break;
      }
      
    }
  }
  
  /**
   * 測試住院點數計算邏輯是否有誤
   */
  @Ignore
  @Test
  public void calculateIPApplyPoints() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, 2018);
    
    //List<MR> list = mrDao.findByDataFormatAndMrDateBetween("20", new java.sql.Date(cal.getTimeInMillis()), new java.sql.Date(System.currentTimeMillis()));
    MR mr = mrDao.findById(23043L).orElse(null);
    //for (MR mr : list) {
      //System.out.println("mr id=" + mr.getId() + ", did=" + mr.getdId());
      List<IP_D> ipdList = ipdDao.findByMrId(mr.getId());
      IP_D ipd = ipdList.get(0);
   
      int orderQty = ipd.getOrderQty().intValue();
      int totalP = ipd.getApplDot();
      //NHIWidgetXMLService.initialOP_DDot(opd);
      // 診察費點數
      int diagDot = ipd.getDiagDot().intValue();
      // 病房費點數
      int roomDot = ipd.getRoomDot().intValue();
      // 管灌膳食費點數
      int mealDot = ipd.getMealDot().intValue();
      // 檢查費點數
      int aminDot = ipd.getAminDot().intValue();
      // 放射線診療費點數
      int radoDot = ipd.getRadoDot().intValue();
      // 治療處置費點數
      int thrpDot = ipd.getThrpDot().intValue();
      // 手術費點數
      int sgryDot = ipd.getSgryDot().intValue();
      // 復健治療費點數
      int phscDot = ipd.getPhscDot().intValue();
      // 血液血漿費點數
      int blodDot = ipd.getBlodDot().intValue();
      // 血液透析費點數
      int hdDot = ipd.getHdDot().intValue();
      // 麻醉費點數
      int aneDot = ipd.getAneDot().intValue();
      // 特殊材料費點數
      int metrDot = ipd.getMetrDot().intValue();
      // 藥費點數
      int drugDot = ipd.getDrugDot().intValue();
      // 藥事服務費點數
      int dsvcDot = ipd.getDsvcDot().intValue();
      // 精神科治療費點數
      int nrtpDot = ipd.getNrtpDot().intValue();
      // 注射技術費點數
      int injtDot = ipd.getInjtDot().intValue();
      // 嬰兒費點數
      int babyDot = ipd.getBabyDot().intValue();
      MRDetail mrDetail = new MRDetail(mr);
      
      mrDetail.setIPDData(ipd, codeTableService);

      List<IP_P> ippList = ippDao.findByIpdIdOrderByOrderSeqNo(ipd.getId());
      List<MO> moList = new ArrayList<MO>();
      for (IP_P ip_P : ippList) {
        MO mo = new MO();
        mo.setIPPData(ip_P, codeTableService);
        moList.add(mo);
      }
      mrDetail.setMos(moList);
      nhiService.updateIPPByMrDetail(mr, ipd, mrDetail, false);
      
      if (diagDot != ipd.getDiagDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", diagDot " + diagDot + " != " + ipd.getDiagDot());
       // break;
      }
      if (roomDot != ipd.getRoomDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", roomDot " + roomDot + " != " + ipd.getRoomDot());
      //  break;
      }
      if (mealDot != ipd.getMealDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", mealDot " + mealDot + " != " + ipd.getMealDot());
      //  break;
      }
      if (aminDot != ipd.getAminDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", aminDot " + aminDot + " != " + ipd.getAminDot());
      //  break;
      }
      if (radoDot != ipd.getRadoDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", radoDot " + radoDot + " != " + ipd.getRadoDot());
      //  break;
      }
      if (thrpDot != ipd.getThrpDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", thrpDot " + thrpDot + " != " + ipd.getThrpDot());
      //  break;
      }
      if (sgryDot != ipd.getSgryDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", sgryDot " + sgryDot + " != " + ipd.getSgryDot());
      //  break;
      }
      if (phscDot != ipd.getPhscDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", phscDot " + phscDot + " != " + ipd.getPhscDot());
      //  break;
      }
      if (blodDot != ipd.getBlodDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", blodDot " + blodDot + " != " + ipd.getBlodDot());
      //  break;
      }
      if (hdDot != ipd.getHdDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", hdDot " + hdDot + " != " + ipd.getHdDot());
      // break;
      }
      if (aneDot != ipd.getAneDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", aneDot " + aneDot + " != " + ipd.getAneDot());
      //  break;
      }
      if (metrDot != ipd.getMetrDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", metrDot " + metrDot + " != " + ipd.getMetrDot());
      //  break;
      }
      if (drugDot != ipd.getDrugDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", drugDot " + drugDot + " != " + ipd.getDrugDot());
      //  break;
      }
      if (dsvcDot != ipd.getDsvcDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", dsvcDot " + dsvcDot + " != " + ipd.getDsvcDot());
      //  break;
      }
      if (nrtpDot != ipd.getNrtpDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", nrtpDot " + nrtpDot + " != " + ipd.getNrtpDot());
      //  break;
      }
      if (injtDot != ipd.getInjtDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", injtDot " + injtDot + " != " + ipd.getInjtDot());
      //  break;
      }
      if (babyDot != ipd.getBabyDot().intValue()) {
        System.out.println("MR id=" + mr.getId() +", babyDot " + babyDot + " != " + ipd.getBabyDot());
      //  break;
      }
      if (orderQty != ipd.getOrderQty().intValue()) {
        System.out.println("order quantity changed:" + orderQty + "-" + ipd.getOrderQty().intValue());
      }
      if (totalP != ipd.getApplDot().intValue()) {
        System.out.println("MR id=" + mr.getId() + " total points changed:" + totalP + "-" + ipd.getApplDot().intValue());
      }
    //}
    
  }
  
  @Test
  public void testGroupBy() {
//    List<Tuple> list = nhiService.groupByStatus(null, null, "11011", null, null, null);    
//    for (Tuple tuple : list) {
//      System.out.println(tuple.get(0) + "," + tuple.get(1));
//    }
  }
}
