/**
 * Created on 2022/6/15.
 */
package tw.com.leadtek.nhiwidget.importdata;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class GetOrderTypeFromOppIpp {

  @Autowired
  private IP_PDao ippDao;
  
  @Autowired
  private OP_PDao oppDao;
  
  @Test
  public void getOrderType() {
    
  }
  
}
