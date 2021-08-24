/**
 * Created on 2021/7/30.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.model.rdb.IP;
import tw.com.leadtek.nhiwidget.model.rdb.OP;
import tw.com.leadtek.nhiwidget.service.CodeTableService;
import tw.com.leadtek.nhiwidget.service.NHIWidgetXMLService;

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
    importFile(new File(
        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\敏盛醫院\\麗臺-門急診\\10801_0\\A-補報(A+a)\\10801-0-A.xml"));
  }

  private void importXML(String path) {
    File file = new File(path);
    File[] files = file.listFiles();
    for (File file2 : files) {
      if (file2.isDirectory()) {
        importXML(file2.getAbsolutePath());
      } else {
        if (file2.getName().endsWith(".xml")) {
          importFile(file2);
        }
      }
    }
  }

  private void importFile(File file) {
    System.out.println("import:" + file.getAbsolutePath());
    ObjectMapper xmlMapper = new XmlMapper();
    try {
      if (readFile(file)) {
        IP ip =
            xmlMapper.readValue(new InputStreamReader(new FileInputStream(file), "BIG5"), IP.class);
        System.out.println("IP:" + file.getAbsolutePath());
        xmlService.saveIP(ip);
      } else {
        OP op =
            xmlMapper.readValue(new InputStreamReader(new FileInputStream(file), "BIG5"), OP.class);
        System.out.println("OP:" + file.getAbsolutePath());
        xmlService.saveOPBatch(op);
      }
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean readFile(File file) {
    boolean result = false;
    try {
      BufferedReader br =
          new BufferedReader(new InputStreamReader(new FileInputStream(file), "BIG5"));
      String line = null;
      int count = 0;
      while ((line = br.readLine()) != null) {
        count++;
        if (line.indexOf("inpatient") > 0) {
          result = true;
          break;
        }
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  @Ignore
  @Test
  public void updateMR_DRGCode() {
    List<Object[]> list = ipdDao.findDRGCodeNotNull();

    int count = 0;
    for (Object[] obj : list) {
      count++;
      String drg = (String) obj[0];
      Long mrId = ((BigInteger) obj[1]).longValue();
      mrDao.updateDRG(drg, mrId);
    }
    System.out.println("update " + count + " records.");
  }
}
