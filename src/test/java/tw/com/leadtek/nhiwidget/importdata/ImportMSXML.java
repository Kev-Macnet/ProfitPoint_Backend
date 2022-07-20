/**
 * Created on 2021/7/30.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.List;
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
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.service.CodeTableService;
import tw.com.leadtek.nhiwidget.service.NHIWidgetXMLService;
import tw.com.leadtek.nhiwidget.service.SystemService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class ImportMSXML {

  private Logger logger = LogManager.getLogger();

  @Autowired
  private NHIWidgetXMLService xmlService;

  @Autowired
  private CodeTableService codeTableService;

  @Autowired
  private IP_DDao ipdDao;
  
  @Autowired
  private MRDao mrDao;
  
  @Autowired
  private SystemService systemService;

  @Ignore
  @Test
  public void importFromPath() {
    codeTableService.refreshCodes();
    // importXML("C:\\nhiwidget\\import\\麗臺-住院");
    // importXML("C:\\nhiwidget\\import\\麗臺-門急診");
    // System.out.println("importFromPath");
    // importXML("D:\\ken\\敏盛醫院\\麗臺-住院");
    // importXML("D:\\Users\\2268\\Desktop\\麗臺-門急診");

    // importFile(new File("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\敏盛醫院\\108021.xml"));
      systemService.importFile(new File(
          "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\敏盛醫院\\麗臺-門急診\\10801_0\\A-補報(A+a)\\10801-0-A.xml"));
  }

}
