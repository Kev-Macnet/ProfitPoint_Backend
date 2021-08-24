/**
 * Created on 2021/8/13.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.dao.DRG_CODEDao;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CODE;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class ImportDRG {

  @Autowired
  private DRG_CODEDao drgDao;

  @Test
  public void importDRG() {
//    importDRGExcel("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\Tw-DRG_公式與排除109年7至12月.xlsx",
//        "109年7至12月 3.4版 TW-DRGs權重表", "2020/07/01", "2020/12/31");
//    importDRGExcel("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\Tw-DRG_公式與排除110年1月至6月.xlsx",
//        "110年1至6月 3.4版 TW-DRGs權重表", "2021/01/01", "2021/06/30");
    importDRGExcel("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\Tw-DRG_公式與排除110年7月至12月.xlsx",
        "110年7至12月 3.4版 TW-DRGs權重表", "2021/07/01", "2021/12/31");
  }

  public void importDRGExcel(String filename, String sheetName, String startDay, String endDay) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    File file = new File(filename);
    try {
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        XSSFSheet sheet = workbook.getSheetAt(i);
        if (sheet.getSheetName().equals(sheetName)) {
          importDRGFromSheet(sheet, sdf.parse(startDay), sdf.parse(endDay));
          break;
        }
      }
    } catch (ParseException e) {
      e.printStackTrace();
    } catch (InvalidFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void importDRGFromSheet(XSSFSheet sheet, Date startDate, Date endDate) {
    for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
      XSSFRow row = sheet.getRow(i);
      if (row == null || row.getCell(2) == null) {
        // System.out.println("sheet:" + i + ", row=" + j + " is null");
        continue;
      }
      String code = row.getCell(2).getStringCellValue().trim().toLowerCase();
      if (code.length() == 0) {
        break;
      }
      DRG_CODE drg = new DRG_CODE();
      drg.setCode(code);
      List<DRG_CODE> list = drgDao.findByCodeAndStartDayAndEndDay(code, startDate, endDate);
      if (list != null && list.size() > 0) {
        // 之前有匯過
        System.out.println("duplicate:" + code);
        continue;
      }

      if (row.getCell(0).getCellType() == CellType.NUMERIC) {
        drg.setMdc(row.getCell(0).getRawValue());
      } else {
        drg.setMdc(row.getCell(0).getStringCellValue());
      }

//      System.out.println("code=" + drg.getCode() + ", mdc=" + drg.getMdc() + ", case20:"
//          + row.getCell(4).getRawValue());
      drg.setSerial(Integer.parseInt(row.getCell(1).getRawValue()));

      if (row.getCell(3).getCellType() == CellType.STRING && "-".equals(row.getCell(3).getStringCellValue())) {
        drg.setRw((float) 0);
      } else if (row.getCell(3) == null || row.getCell(3).getRawValue() == null) {
        drg.setRw((float) 0);
      } else {
        drg.setRw(Float.parseFloat(row.getCell(3).getRawValue()));
      }
      if (row.getCell(4).getCellType() == CellType.STRING
          && ("＊".equals(row.getCell(4).getStringCellValue())
          || "*".equals(row.getCell(4).getStringCellValue()))) {
        drg.setCase20(1);
      } else {
        drg.setCase20(0);
      }

      drg.setAvgInDay(getNumericCell(row.getCell(6)));
      drg.setLlimit(getNumericCell(row.getCell(7)));
      drg.setUlimit(getNumericCell(row.getCell(8)));
      drg.setStartDay(startDate);
      drg.setEndDay(endDate);
      drgDao.save(drg);
    }
  }

  private int getNumericCell(XSSFCell cell) {
    if (cell.getCellType() == CellType.NUMERIC) {
      return Integer.parseInt(cell.getRawValue());
    } else {
      String avgInDay = cell.getStringCellValue();
      if (avgInDay.equals("-")) {
        return 0;
      }
    }
    return -1;
  }
}
