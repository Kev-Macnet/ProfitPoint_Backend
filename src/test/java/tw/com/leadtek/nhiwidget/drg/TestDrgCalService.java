/**
 * Created on 2021/9/1.
 */
package tw.com.leadtek.nhiwidget.drg;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CODE;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.DrgCalService;

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
  
  @Test
  public void testDrgApplDot() {
    long maxID = 0;
    int count = 0;
    List<IP_D> ipds = ipdDao.findAllWithDRG(maxID);
    // 暫存用，避免重複讀DB
    HashMap<String, HashMap<String, List<Long>>> countCase20 = new HashMap<String, HashMap<String,List<Long>>>();
    for (IP_D ipd : ipds) {
     Optional<MR> optional = mrDao.findById(ipd.getMrId());
      if (!optional.isPresent()) {
        continue;
      }
      MR mr = optional.get();
      int addChild = drgCalService.getAddChild(ipd.getNbBirthday(), ipd.getIdBirthYmd(), ipd.getInDate());
      DrgCalculate drg = drgCalService.getDRGSection(ipd.getTwDrgCode(), mr.getApplYm(), ipd.getMedDot(), addChild);
      if (drg == null) {
        System.out.println(ipd.getTwDrgCode() + " not found on " + mr.getApplYm());
        continue;
      }
      System.out.println("id=" + ipd.getId());
      boolean isInCase20 = checkCase20(drg, countCase20, mr.getApplYm());
      
      int applDot = drgCalService.getApplDot(drg, ipd.getMedDot(), ipd.getPartDot(), ipd.getNonApplDot(), mr.getId(), 
          ipd.getEBedDay() + ipd.getSBedDay(), ipd.getTranCode(), isInCase20);
      if (applDot != ipd.getApplDot().intValue()) {
        System.out.println("id=" + ipd.getId() + "," + applDot + "<>" + ipd.getApplDot() + ",Fix=" + drg.getFixed() +
            ",upper=" + drg.getUlimit() + ", lower=" + drg.getLlimit());
        break;
      } else {
        System.out.println("pass " +(count++));
        mr.setDrgFixed(drg.getFixed());
        mr.setDrgSection(drg.getSection());
        mr.setDrgCode(drg.getCode());
        mrDao.save(mr);
      }
    }
  }
  
  public boolean checkCase20(DrgCalculate drg,  HashMap<String, HashMap<String, List<Long>>> countCase20, String ym) {
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
}
