/**
 * Created on 2020/9/23.
 */
package tw.com.leadtek.nhiwidget;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import tw.com.leadtek.nhiwidget.dao.PARAMETERSDao;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.service.ParametersService;
import tw.com.leadtek.tools.DateTool;
import tw.com.leadtek.tools.ExcelUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class TestParameterService {

  private Logger logger = LogManager.getLogger();
  
  // 最後要有 \\
  public final static String FILE_PATH = "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Install\\";

  // private final static String FRONT_END_DATETIME = "yyyy-MM-dd HH:mm:ss";

  private final static String FRONT_END_TIME = "HH:mm:ss";

  @Autowired
  private PARAMETERSDao pDao;

  // 初始化系統參數
  @Ignore
  @Test
  public void testInitParameters() {
    PARAMETERS p = new PARAMETERS("SYSTEM", ParametersService.PAGE_COUNT, "20",
        PARAMETERS.TYPE_INTEGER, "預設每頁顯示筆數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);

    p = new PARAMETERS("TOTAL_POINTS_STATUS", "WM", "1", PARAMETERS.TYPE_INTEGER,
        "是否計算西醫(Western Medicine)總點數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);

    p = new PARAMETERS("TOTAL_POINTS_STATUS", "DENTIST", "0", PARAMETERS.TYPE_INTEGER, "是否計算牙醫總點數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);

    p = new PARAMETERS("TOTAL_POINTS", "WM", "1", PARAMETERS.TYPE_INTEGER,
        "是否計算西醫(Western Medicine)總點數");
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

    p = new PARAMETERS("TOTAL_POINTS", "DENTIST_OP_POINTS", "0", PARAMETERS.TYPE_INTEGER,
        "牙醫門診分配總點數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);

    p = new PARAMETERS("TOTAL_POINTS", "DENTIST_DRUG_POINTS", "0", PARAMETERS.TYPE_INTEGER,
        "牙醫藥品分配總點數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);

    p = new PARAMETERS("TOTAL_POINTS", "DENTIST_FUND_POINTS", "0", PARAMETERS.TYPE_INTEGER,
        "牙醫專款分配總點數");
    p.setStartEndDate("2021/05/01", "2910/12/31");
    upsert(p);

    p = new PARAMETERS("TOTAL_POINTS", "HEMODIALYSIS_POINTS", "0", PARAMETERS.TYPE_INTEGER,
        "透析總點數");
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
    SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
    List<PARAMETERS> list = pDao.findByName(p.getName());
    if (list != null) {
      boolean isFound = false;
      for (PARAMETERS parameters : list) {
        if (p.getStartDate() != null && p.getStartDate().equals(parameters.getStartDate())) {
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
        System.out.println("list found, but not found " + p.getName());
        pDao.save(p);
      }
    } else {
      System.out.println("list not found " + p.getName());
      pDao.save(p);
    }
  }

  @Test
  public void importFromExcel() {
    importFromExcel("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\Job\\PARAMETERS.xlsx", "參數設定", 1);
//    System.out.println(DATA_TYPE.NULL + "=" + DATA_TYPE.NULL.ordinal());
//    System.out.println(DATA_TYPE.INT + "=" + DATA_TYPE.INT.ordinal());
//    System.out.println(DATA_TYPE.FLOAT + "=" + DATA_TYPE.FLOAT.ordinal());
//    System.out.println(DATA_TYPE.STRING + "=" + DATA_TYPE.STRING.ordinal());
  }

  public void importFromExcel(String filename, String sheetName, int titleRow) {
    File file = new File(filename);
    try {
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      // DataFormatter formatter = new DataFormatter();
      DecimalFormat df = new DecimalFormat("#.######");
      int total = 0;
      // HashMap<Integer, String> columnMap = readTitleRow(sheet.getRow(0));
      int count = 0;

      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        XSSFSheet sheet = workbook.getSheetAt(i);
        if (!sheet.getSheetName().equals(sheetName)) {
          continue;
        }
        for (int j = titleRow; j < sheet.getPhysicalNumberOfRows(); j++) {
          XSSFRow row = sheet.getRow(j);
          if (row == null) {
            // System.out.println("sheet:" + i + ", row=" + j + " is null");
            continue;
          }
          // CAT (類別名稱) NAME (參數名稱) VAL (值) DATA_TYPE (資料型態) NOTE (說明) START_DATE (參數生效日) END_DATE
          // (參數失效日)
          PARAMETERS p = new PARAMETERS();
          p.setCat(row.getCell(0).getStringCellValue());
          p.setName(ExcelUtil.getCellStringValue(row.getCell(1)));
          if (row.getCell(2) != null && row.getCell(2).getCellType() == CellType.NUMERIC) {
            p.setValue(df.format(row.getCell(2).getNumericCellValue()));
          } else {
            p.setValue(row.getCell(2).getStringCellValue());
          }
          p.setDataType((int) row.getCell(3).getNumericCellValue());
          if (row.getCell(4) != null) {
            p.setNote(row.getCell(4).getStringCellValue());
          }
          p.setUpdateAt(new Date());
          p.setStartDate(row.getCell(5).getDateCellValue());
          p.setEndDate(row.getCell(6).getDateCellValue());

          System.out.println(p.getCat() + "," + p.getName() + "," + p.getValue() + ","
              + p.getDataType() + ",startDate:" + row.getCell(5).getDateCellValue());
          upsert(p);
        }
      }
    } catch (InvalidFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
