/**
 * Created on 2020/9/23.
 */
package tw.com.leadtek.nhiwidget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import tw.com.leadtek.nhiwidget.dao.PARAMETERSDao;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.service.ParametersService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class TestParameterService {

  private Logger logger = LogManager.getLogger();

  private final static String DATE = "yyyy/MM/dd";

  //private final static String FRONT_END_DATETIME = "yyyy-MM-dd HH:mm:ss";

  private final static String FRONT_END_TIME = "HH:mm:ss";

  @Autowired
  private PARAMETERSDao pDao;


  // 初始化系統參數
  // @Ignore
  @Test
  public void testInitParameters() {
    PARAMETERS p = new PARAMETERS("SYSTEM", ParametersService.PAGE_COUNT, "20", PARAMETERS.TYPE_INTEGER, "預設每頁顯示筆數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);
    
    p = new PARAMETERS("TOTAL_POINTS_STATUS", "WM", "1", PARAMETERS.TYPE_INTEGER, "是否計算西醫(Western Medicine)總點數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);
    
    p = new PARAMETERS("TOTAL_POINTS_STATUS", "DENTIST", "0", PARAMETERS.TYPE_INTEGER, "是否計算牙醫總點數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);
    
    p = new PARAMETERS("TOTAL_POINTS", "WM", "1", PARAMETERS.TYPE_INTEGER, "是否計算西醫(Western Medicine)總點數");
    p.setStartEndDate("2021/03/01", "2021/04/30");
    upsert(p);
    
    p = new PARAMETERS("TOTAL_POINTS", "DENTIST", "0", PARAMETERS.TYPE_INTEGER, "是否計算牙醫總點數");
    p.setStartEndDate("2021/03/01", "2021/04/30");
    upsert(p);
    
    p = new PARAMETERS("TOTAL_POINTS", "WM_OP_POINTS", "0", PARAMETERS.TYPE_INTEGER, "西醫門診分配總點數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);
    
    p = new PARAMETERS("TOTAL_POINTS", "WM_IP_POINTS", "0", PARAMETERS.TYPE_INTEGER, "西醫住院分配總點數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);
    
    p = new PARAMETERS("TOTAL_POINTS", "WM_DRUG_POINTS", "0", PARAMETERS.TYPE_INTEGER, "西醫藥品分配總點數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);
    
    p = new PARAMETERS("TOTAL_POINTS", "DENTIST_OP_POINTS", "0", PARAMETERS.TYPE_INTEGER, "牙醫門診分配總點數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);
    
    p = new PARAMETERS("TOTAL_POINTS", "DENTIST_DRUG_POINTS", "0", PARAMETERS.TYPE_INTEGER, "牙醫藥品分配總點數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);
    
    p = new PARAMETERS("TOTAL_POINTS", "DENTIST_FUND_POINTS", "0", PARAMETERS.TYPE_INTEGER, "牙醫專款分配總點數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);
    
    p = new PARAMETERS("TOTAL_POINTS", "HEMODIALYSIS_POINTS", "0", PARAMETERS.TYPE_INTEGER, "透析總點數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);
    
    p = new PARAMETERS("TOTAL_POINTS", "FUND_POINTS", "0", PARAMETERS.TYPE_INTEGER, "專款總點數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);
    
    p = new PARAMETERS("DEDUCTED", "SAMPLING", "0", PARAMETERS.TYPE_INTEGER, "核刪抽件數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);
    
  }
  
  private void upsert(PARAMETERS p) {
    SimpleDateFormat sdf = new SimpleDateFormat(DATE);
    List<PARAMETERS> list = pDao.findByName(p.getName());
    if (list != null ) {
      boolean isFound = false;
      for (PARAMETERS parameters : list) {
        if (p.getStartDate().equals(parameters.getStartDate())) {
          parameters.setValue(p.getValue());
          parameters.setNote(p.getNote());
          parameters.setCat(p.getCat());
          parameters.setDataType(p.getDataType());
          parameters.setEndDate(p.getEndDate());
          parameters.setUpdateAt(new Date());
          pDao.save(parameters);
          isFound = true;
          break;
        }
      }
      if (!isFound) {
        pDao.save(p);
      }
    } else {
      pDao.save(p);
    }
  }
}
