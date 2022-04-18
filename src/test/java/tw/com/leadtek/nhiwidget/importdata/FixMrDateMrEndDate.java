/**
 * Created on 2022/3/18.
 */
package tw.com.leadtek.nhiwidget.importdata;

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
import tw.com.leadtek.nhiwidget.dao.INTELLIGENTDao;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.model.rdb.INTELLIGENT;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.tools.DateTool;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class FixMrDateMrEndDate {

  @Autowired
  private OP_DDao opdDao;
  
  @Autowired
  private IP_DDao ipdDao;
  
  @Autowired
  private MRDao mrDao;
  
  @Autowired
  private INTELLIGENTDao intelligentDao;
  
  //@Ignore
  @Test
  public void fixDate() {
    // 1. 處理 MR_END_DATE is null但 OP_D.FUNC_END_DATE is null
    // A: 直接將 MR_DATE 值複製到 MR_END_DATE
    // SQL: UPDATE MR SET MR_END_DATE = MR_DATE WHERE MR_END_DATE IS NULL AND DATA_FORMAT ='10' 
    //      AND MR.ID IN (SELECT MR_ID FROM OP_D WHERE FUNC_END_DATE IS NULL) 
    // 2. 處理 MR_END_DATE is null但 OP_D.FUNC_END_DATE is not null且 OP_D.FUNC_DATE = OP_D.FUNC_END_DATE
    // A: 直接將 MR_DATE 值複製到 MR_END_DATE
    // SQL: UPDATE MR SET MR_END_DATE = MR_DATE WHERE MR_END_DATE IS NULL AND DATA_FORMAT ='10' 
    //      AND MR.ID IN (SELECT MR_ID FROM OP_D WHERE FUNC_DATE = FUNC_END_DATE)
    // 3. 處理 MR_END_DATE is null但 OP_D.FUNC_END_DATE is not null且 OP_D.FUNC_DATE <> OP_D.FUNC_END_DATE
    updateMREndDateByOpdFuncDate();
    
    // 4. 處理 MR_END_DATE is null 的住院病歷
    updateMREndDateByIpdApplEndDate();
    
  }
  
  private void updateMREndDateByOpdFuncDate() {
    List<OP_D> list = opdDao.findNoMrEndDateByOpd();
    for (OP_D opd : list) {
      java.sql.Date mrEndDate = new java.sql.Date(DateTool.convertChineseToYear(opd.getFuncEndDate()).getTime());
      mrDao.updateMrEndDate(mrEndDate, opd.getMrId());
    }
  }
  
  private void updateMREndDateByIpdApplEndDate() {
    List<IP_D> list = ipdDao.findNoMrEndDateByIpd();
    for (IP_D ipd : list) {
      java.sql.Date mrEndDate = new java.sql.Date(DateTool.convertChineseToYear(ipd.getApplEndDate()).getTime());
      mrDao.updateMrEndDate(mrEndDate, ipd.getMrId());
    }
  }
  
  @Ignore
  @Test
  public void updateIpdLeaveDate() {
    List<IP_D> list = ipdDao.findOutDateIsNotNull();
    for (IP_D ipd : list) {
      java.sql.Date leaveDate = new java.sql.Date(DateTool.convertChineseToYear(ipd.getOutDate()).getTime());
      ipdDao.updateLeaveDate(leaveDate, ipd.getId());
    }
  }
  
  @Ignore
  @Test
  public void updateIntelligentApplYm() {
    List<INTELLIGENT> list = intelligentDao.findAll();
    for (INTELLIGENT intelligent : list) {
      if (intelligent.getApplYm() == null) {
        Optional<MR> optional = mrDao.findById(intelligent.getMrId());
        if (optional.isPresent()) {
          intelligent.setApplYm(optional.get().getApplYm());
          intelligentDao.save(intelligent);
        }
      }
    }
  }
  
  @Ignore
  @Test
  public void fixOwnExpanse() {
    
  }
}
