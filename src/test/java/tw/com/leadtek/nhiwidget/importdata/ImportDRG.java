/**
 * Created on 2021/8/13.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
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

import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.dao.DRG_CODEDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.local.InitialEnvironment;
import tw.com.leadtek.nhiwidget.model.rdb.DRG_CODE;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.payload.ParameterValue;
import tw.com.leadtek.nhiwidget.service.ParametersService;
import tw.com.leadtek.nhiwidget.service.UserService;
import tw.com.leadtek.nhiwidget.sql.LogDataDao;
import tw.com.leadtek.tools.DateTool;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class ImportDRG {

  @Autowired
  private DRG_CODEDao drgDao;

  @Autowired
  private MRDao mrDao;

  @Autowired
  private ParametersService parameters;

  @Autowired
  private LogDataDao logDataDao;

  @Autowired
  private UserService userService;


  private HashMap<String, String> drgDep = new HashMap<String, String>();

  private List<String> started = null;

  //@Ignore
  @Test
  public void importDRG() {
    importDRGDep(InitialEnvironment.FILE_PATH + "Tw-DRG_???????????????110???7??????12???.xlsx",
        "??????7_2");
    String[] sheetNames = new String[2];
    sheetNames[0] = "??????????????????";
    sheetNames[1] = "??????????????????";
    
//    started = new ArrayList<String>();
//    importDRGStarted(
//    		InitialEnvironment.FILE_PATH + "Tw-DRG_???????????????(?????????2021???07??????12???).xlsx",
//        sheetNames, "20200101", "20200630");
//    importDRGExcel(InitialEnvironment.FILE_PATH + "Tw-DRG_???????????????(?????????2021???07??????12???).xlsx",
//        "109???1???6??? 3.4??? TW-DRGs?????????", "20200101", "20200630");
    // ====================================
//    started = new ArrayList<String>();
//    importDRGStarted(
//        "D:\\Users\\2268\\2020\\??????????????????\\docs_??????????????????\\???????????????\\Tw-DRG_???????????????(?????????2021???07??????12???).xlsx",
//        sheetNames, "20200701", "20201231");
//    importDRGExcel("D:\\Users\\2268\\2020\\??????????????????\\docs_??????????????????\\???????????????\\Tw-DRG_???????????????109???7???12???.xlsx",
//        "109???7???12??? 3.4??? TW-DRGs?????????", "20200701", "20201231");

    // ====================================
//    started = new ArrayList<String>();
//    importDRGStarted(
//        "D:\\Users\\2268\\2020\\??????????????????\\docs_??????????????????\\???????????????\\Tw-DRG_???????????????(?????????2021???07??????12???).xlsx",
//        sheetNames, "20210101", "20210630");
//    importDRGExcel("D:\\Users\\2268\\2020\\??????????????????\\docs_??????????????????\\???????????????\\Tw-DRG_???????????????110???1??????6???.xlsx",
//        "110???1???6??? 3.4??? TW-DRGs?????????", "20210101", "20210630");

    // ====================================
//    started = new ArrayList<String>();
//    importDRGStarted(
//        "D:\\Users\\2268\\2020\\??????????????????\\docs_??????????????????\\???????????????\\Tw-DRG_???????????????(?????????2021???07??????12???).xlsx",
//        sheetNames, "20210701", "20211231");
//    importDRGExcel("D:\\Users\\2268\\2020\\??????????????????\\docs_??????????????????\\???????????????\\Tw-DRG_???????????????110???7??????12???.xlsx",
//        "110???7???12??? 3.4??? TW-DRGs?????????", "20210701", "20211231");
    // ==========================
  started = new ArrayList<String>();
  importDRGStarted(
      InitialEnvironment.FILE_PATH + "Tw-DRG_???????????????(?????????2021???07??????12???).xlsx",
      sheetNames);
  importDRGExcel("D:\\Users\\2268\\2020\\??????????????????\\docs_??????????????????\\Install\\111???1-6???3.4???1,068???Tw-DRGs???????????????.xlsx",
      "??????7.3", "20220101", "20220630");
    // 
  }

  /**
   * ?????????DRG????????????(M)?????????(P)
   * 
   * @param filename
   * @param sheetName
   */
  private void importDRGDep(String filename, String sheetName) {
    // ??????7_2
    File file = new File(filename);
    try {
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        XSSFSheet sheet = workbook.getSheetAt(i);
        if (sheet.getSheetName().equals(sheetName)) {
          importDRGDep(sheet);
          break;
        }
      }
    } catch (InvalidFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * ???????????????(??????)??? DRG code.
   * 
   * @param filename
   * @param sheetNames
   * @param startDay
   * @param endDay
   */
  private void importDRGStarted(String filename, String[] sheetNames) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    File file = new File(filename);
    try {
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        XSSFSheet sheet = workbook.getSheetAt(i);
        for (int j = 0; j < sheetNames.length; j++) {
          if (sheet.getSheetName().equals(sheetNames[j])) {
            System.out.println("importDRGNotStart sheet:" + sheet.getSheetName());
            importDRGStarted(sheet);
            break;
          }
        }
      }
    } catch (InvalidFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void importDRGExcel(String filename, String sheetName, String startDay, String endDay) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
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
    System.out.println("importDRGFromSheet:" + startDate + "," + endDate);
    int titleRow = 0;
    for (int i = titleRow ; i < sheet.getPhysicalNumberOfRows(); i++) {
      XSSFRow row = sheet.getRow(i);
      if (row.getCell(0).getStringCellValue().startsWith("MDC")) {
        break;
      }
      titleRow++;
    }
    for (int i = titleRow + 1; i < sheet.getPhysicalNumberOfRows(); i++) {
      XSSFRow row = sheet.getRow(i);
      
      if (row == null || row.getCell(0) == null) {
        // System.out.println("sheet:" + i + ", row=" + j + " is null");
        continue;
      }
      if (row.getCell(0).getStringCellValue().startsWith("??????")) {
        break;
      }
      String code = row.getCell(2).getStringCellValue().trim().toLowerCase();
      if (code.length() == 0) {
        break;
      }
      DRG_CODE drg = new DRG_CODE();
      drg.setCode(code);
      if (drgDep.get(code) != null) {
        drg.setDep(drgDep.get(code));
      } else {
        System.out.println("DRG DEP not found:" + code);
        drg.setDep("M");
      }
      List<DRG_CODE> list = drgDao.findByCodeAndStartDateAndEndDate(code, startDate, endDate);
      if (list != null && list.size() > 0) {
        // ???????????????
        System.out.println("duplicate:" + code);
        continue;
      }

      if (row.getCell(0).getCellType() == CellType.NUMERIC) {
        drg.setMdc(row.getCell(0).getRawValue());
      } else {
        drg.setMdc(row.getCell(0).getStringCellValue());
      }

      // System.out.println("code=" + drg.getCode() + ", mdc=" + drg.getMdc() + ", case20:"
      // + row.getCell(4).getRawValue());
      drg.setSerial(Integer.parseInt(row.getCell(1).getRawValue()));

      if (row.getCell(3).getCellType() == CellType.STRING
          && "-".equals(row.getCell(3).getStringCellValue())) {
        drg.setRw((float) 0);
      } else if (row.getCell(3) == null || row.getCell(3).getRawValue() == null) {
        drg.setRw((float) 0);
      } else if (row.getCell(3).getCellType() == CellType.NUMERIC) {
        drg.setRw((float) row.getCell(3).getNumericCellValue());
      } else {
        drg.setRw(Float.parseFloat(row.getCell(3).getStringCellValue()));
      }
      // System.out.println("code=" + drg.getCode() + ", rw=" + drg.getRw()+ ", mdc=" + drg.getMdc()
      // + ", case20:"
      // + row.getCell(4).getRawValue());
      if (row.getCell(4) != null && row.getCell(4).getCellType() == CellType.STRING
          && ("???".equals(row.getCell(4).getStringCellValue())
              || "*".equals(row.getCell(4).getStringCellValue()))) {
        drg.setCase20(1);
      } else {
        drg.setCase20(0);
      }

      drg.setAvgInDay(getNumericCell(row.getCell(6)));
      drg.setLlimit(getNumericCell(row.getCell(7)));
      drg.setUlimit(getNumericCell(row.getCell(8)));
      drg.setStartDate(startDate);
      drg.setEndDate(endDate);
      if (isDRGFound(code, started)) {
        drg.setStarted(1);
      } else {
        drg.setStarted(0);
      }
      drgDao.save(drg);
      System.out.println("save:" + drg.getCode());
    }
  }

  private int getNumericCell(XSSFCell cell) {
    if (cell.getCellType() == CellType.NUMERIC) {
      return (int) cell.getNumericCellValue();
    } else {
      return 0;
    }
  }

  private void importDRGDep(XSSFSheet sheet) {
    boolean start = false;
    for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
      XSSFRow row = sheet.getRow(i);
      if (row == null || row.getCell(0) == null) {
        // System.out.println("sheet:" + i + ", row=" + j + " is null");
        continue;
      }
      String code = row.getCell(0).getStringCellValue().trim().toUpperCase();
      if (code.equals("DRG")) {
        start = true;
        continue;
      }
      if (!start) {
        continue;
      }
      for (int j = 0; j < row.getPhysicalNumberOfCells(); j += 2) {
        if (row.getCell(j) != null && row.getCell(j + 1) != null) {
          if (row.getCell(j).getCellType() == CellType.NUMERIC) {
            drgDep.put(String.valueOf((int) row.getCell(j).getNumericCellValue()),
                row.getCell(j + 1).getStringCellValue());
            System.out.println(i + ":" + j + "=num:" + row.getCell(j).getNumericCellValue()
                + ", raw=" + row.getCell(j).getRawValue() + ","
                + String.valueOf((int) row.getCell(j).getNumericCellValue()));
          } else {
            drgDep.put(row.getCell(j).getStringCellValue(),
                row.getCell(j + 1).getStringCellValue());
          }
        }
      }
    }
    System.out.println("total drg:" + drgDep.size());
  }

  private void importDRGStarted(XSSFSheet sheet) {
    boolean start = false;
    for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
      XSSFRow row = sheet.getRow(i);
      if (row == null || row.getCell(0) == null) {
        continue;
      }
      if (!start) {
        String code = row.getCell(0).getStringCellValue().trim().toUpperCase();
        if (code.equals("MDC")) {
          start = true;
        }
        continue;
      }
      // started
      if (row.getCell(0) != null && row.getCell(1) != null) {
        if (row.getCell(1).getCellType() == CellType.NUMERIC) {
          String value = String.valueOf(row.getCell(1).getNumericCellValue());
          if (value.endsWith(".0")) {
            value = value.substring(0, value.length() - 2);
          }
          started.add(value);
          // System.out.println("add " + value);
          // drgDep.put(String.valueOf((int) row.getCell(j).getNumericCellValue()),
          // row.getCell(j + 1).getStringCellValue());
          // System.out.println(i + ":" + j + "=num:" + row.getCell(j).getNumericCellValue() + ",
          // raw="
          // + row.getCell(j).getRawValue() + ","
          // + String.valueOf((int) row.getCell(j).getNumericCellValue()));
        } else if (row.getCell(1).getCellType() == CellType.STRING) {
          // drgDep.put(row.getCell(j).getStringCellValue(), row.getCell(j +
          // 1).getStringCellValue());
          started.add(String.valueOf(row.getCell(1).getStringCellValue()));
          // System.out.println("add " + String.valueOf(row.getCell(1).getStringCellValue()));
        }
      }
    }
    // System.out.println("started count:" + started.size());
  }

  /**
   * ??????DRG??????
   */
  @Ignore
  @Test
  public void calDRGFixed() {
    long mrId = 26943;
    Optional<MR> optional = mrDao.findById(mrId);
    if (optional == null) {
      System.out.println("MR ID(" + mrId + ") not found.");
      return;
    }
    MR mr = optional.get();
    Date date = DateTool.getDateByApplYM(mr.getApplYm());
    float value = getFixedWithoutRW(date);
    System.out.println(mr.getApplYm() + ":" + value);

    List<DRG_CODE> drgList = drgDao
        .findByCodeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(mr.getDrgCode(), date, date);
    if (drgList == null || drgList.size() == 0) {
      System.out.println("DRG " + mr.getDrgCode() + " not found in " + date);
      return;
    }
    DRG_CODE drg = drgList.get(0);
    float fixed = drg.getRw() * value;
    System.out.println("MR ID:" + mrId + ":" + mr.getApplDot() + "," + drg.getLlimit() + "-" + fixed
        + "-" + drg.getUlimit());
  }

  private float getFixedWithoutRW(Date date) {
    return ((Integer) getParameterValue("SPR", date)) * getHospAdd(date);
  }

  /**
   * ?????????????????????
   * 
   * @return
   */
  private float getHospAdd(Date date) {
    float result = 1;
    // ?????????????????????
    if (parameters.getParameter("HOSP_OUT") != null
        && "Y".equals(parameters.getParameter("HOSP_OUT"))) {
      result += Float.parseFloat(parameters.getParameter("OL"));
    }
    // ?????????????????????
    String hospLevel = parameters.getParameter("HOSP_LEVEL");
    result += (Float) getParameterValue("ADD_" + hospLevel, date);

    // CMI ?????????
    float cmi = (Float) getParameterValue("CMI", date);
    if (cmi >= 0.011f && cmi <= 0.012f) {
      cmi = (Float) getParameterValue("CMI2", date);
    } else if (cmi >= 0.012f && cmi <= 0.013f) {
      cmi = (Float) getParameterValue("CMI3", date);
    } else if (cmi > 0.013f) {
      cmi = (Float) getParameterValue("CMI4", date);
    }
    result += cmi;
    return result;
  }

  /**
   * ?????????????????????
   * 
   * @return
   */
  private float getHospAdd(String applYM) {
    int ym = Integer.parseInt(applYM);
    ym = ym * 100 + 10;
    Date date = DateTool.convertChineseToYear(String.valueOf(ym));
    return getHospAdd(date);
  }

  @SuppressWarnings("unchecked")
  private Object getParameterValue(String key, Date date) {
    List<ParameterValue> list =
        (List<ParameterValue>) (parameters.getParameterValue(key, null, null, null, null, 10000, 0).getData());

    for (ParameterValue p : list) {
      if (date.getTime() >= p.getSdate().getTime()
          && date.getTime() <= p.getEdate().getTime()) {
        return p.getValue();
      }
    }
    return null;
  }

  private boolean isDRGFound(String drg, List<String> list) {
    for (String string : list) {
      if (drg.equals(string)) {
        return true;
      }
    }
    return false;
  }

  @Ignore
  @Test
  public void updateMRTDotData() {
    List<Map<String, Object>> ipds = logDataDao.find_IPD_Dot();
    for (Map<String, Object> ipd : ipds) {
      int total = 0;
      if (ipd.get("APPL_DOT") != null) {
        total += ((Integer) ipd.get("APPL_DOT")).intValue();
      }
      if (ipd.get("NON_APPL_DOT") != null) {
        total += ((Integer) ipd.get("NON_APPL_DOT")).intValue();
      }
      if (ipd.get("PART_DOT") != null) {
        total += ((Integer) ipd.get("PART_DOT")).intValue();
      }
      long mrId = (Long) ipd.get("MR_ID");
      // System.out.println("update " + mrId);
      logDataDao.updateMRTDot(total, mrId);
    }
  }
  
  @Ignore
  @Test
  public void testEncrypt() {
    String day = "90";
    String encode = userService.encrypt(day);
    String decode = userService.decrypt(encode);
    
    System.out.println(day + " ==> " + encode + " <== " + decode);
    String test = "bYDmdrF7CxhyBsa1n7hu+Q==";
    System.out.println("test = " + userService.decrypt(test));
        
  }
}
