/**
 * Created on 2022/5/5.
 */
package tw.com.leadtek.nhiwidget.service;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.com.leadtek.nhiwidget.dao.PARAMETERSDao;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.tools.ExcelUtil;

@Service
public class InitialDataService {

  private Logger logger = LogManager.getLogger();
  
  @Autowired
  private PARAMETERSDao parametersDao;

  @Autowired
  private ParametersService parametersService;

  public void importParametersFromExcel(File file, String sheetName, int titleRow) {
    XSSFWorkbook workbook = null;
    try {
      workbook = new XSSFWorkbook(file);
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
          if (row.getCell(5) == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(2000, 1, 1);
            p.setStartDate(cal.getTime());
          } else {
            p.setStartDate(row.getCell(5).getDateCellValue());
          }
          if (row.getCell(6) == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(2099, 12, 31);
            p.setStartDate(cal.getTime());
          } else {
            p.setEndDate(row.getCell(6).getDateCellValue());
          }
          upsertParameters(p);
        }
        parametersService.reloadParameters();
      }
    } catch (InvalidFormatException e) {
      logger.error("importParametersFromExcel", e);
    } catch (IOException e) {
      logger.error("importParametersFromExcel", e);
    } finally {
      if (workbook != null) {
        try {
          workbook.close();
          file.delete();
        } catch (IOException e) {
          logger.error("importParametersFromExcel", e);
        }
      }
    }
  }

  private void upsertParameters(PARAMETERS p) {
    List<PARAMETERS> list = parametersDao.findByName(p.getName());
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
          parametersDao.save(parameters);
          isFound = true;
          break;
        }
      }
      if (!isFound) {
        parametersDao.save(p);
      }
    } else {
      parametersDao.save(p);
    }
  }
}
