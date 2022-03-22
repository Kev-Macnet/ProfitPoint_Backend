/**
 * Created on 2022/3/22.
 */
package tw.com.leadtek.nhiwidget.local;

import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.service.SystemService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class ImportXMLFile {

  private final static String IMPORT_PATH = "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\XML\\";
  
  @Autowired
  private SystemService systemService;
  
  @Test
  public void importXMLFile() {
    File[] files = new File(IMPORT_PATH).listFiles();
    if (files != null && files.length > 0) {
      for (int i = 0; i < files.length; i++) {
        System.out.println(files[i].getAbsolutePath());
        if (!files[i].getAbsolutePath().endsWith("xml")) {
          continue;
        }
        systemService.importFile(files[i]);
      }
    }
  }
}
