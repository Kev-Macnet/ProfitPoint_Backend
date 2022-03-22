/**
 * Created on 2022/3/22.
 */
package tw.com.leadtek.nhiwidget.local;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
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
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.dao.PARAMETERSDao;
import tw.com.leadtek.nhiwidget.dao.USERDao;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.DEPARTMENT;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.payload.UserRequest;
import tw.com.leadtek.nhiwidget.service.UserService;
import tw.com.leadtek.tools.DateTool;
import tw.com.leadtek.tools.ExcelUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class InitialEnvironment {

  public final static String SERVER_IP = "localhost";
  
  private final static String FILE_PATH = "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\Job\\";
  
  @Autowired
  private PARAMETERSDao pDao;
  
  @Autowired
  private CODE_TABLEDao ctDao;
  
  @Autowired
  private USERDao userDao;
  
  @Autowired
  private UserService userService;
  
  @Test
  public void importData() {
    if(userDao.count() ==0) {
      initialLeadtek();
    }
    importFromExcel(FILE_PATH + "PARAMETERS.xlsx", "參數設定", 1);
    importCODE_TABLEToRDB(FILE_PATH + "CODE_TABLE.xlsx");
    addDepartmentFile();
    addUserFile();
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
  
  private void importCODE_TABLEToRDB(String filename) {
    System.out.println("importCODE_TABLEToRDB " + filename);
    File file = new File(filename);

    try {
      XSSFWorkbook workbook = new XSSFWorkbook(file);

      processCODE_TABLE(workbook);
      workbook.close();
    } catch (InvalidFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * 處理儲放代碼的sheet
   * 
   * @param workbook
   * @param codes
   * @param groupName
   * @param sheetIndex
   */
  private void processCODE_TABLE(XSSFWorkbook workbook) {
    String sheetName = "CODE_TABLE";
    XSSFSheet sheet = getSheetByName(workbook, sheetName);
    if (sheet == null) {
      System.out.println(sheetName + " not exist.");
      return;
    }
    String groupName = null;
    int codeIndex = 2;
    for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
      XSSFRow row = sheet.getRow(i);
      String[] ss = getAllCellStringValue(row);
      if (ss == null) {
        break;
      }
      if (ss[codeIndex] != null && ss[codeIndex].length() == 0) {
        continue;
      }
      groupName = ss[1];
      // CODE_TABLE ct = CODE_TABLE.initial(ss, codeIndex);
      CODE_TABLE ct = new CODE_TABLE();
      ct.setCat(row.getCell(1).getStringCellValue());
      if (row.getCell(2).getCellType() == CellType.NUMERIC) {
        String value = String.valueOf(row.getCell(2).getNumericCellValue());
        if (value.endsWith(".0")) {
          value = value.substring(0, value.length() - 2);
        }
        ct.setCode(value); 
      } else {
        ct.setCode(row.getCell(2).getStringCellValue()); 
      }
      if (row.getCell(3) == null) {
        System.err.println(ct.getCat() + "," + ct.getCode() + " no DESC_CHI");
        ct.setDescChi("");
        //break;
      } else if (row.getCell(3).getCellType() == CellType.NUMERIC) {
        String value = String.valueOf(row.getCell(3).getNumericCellValue());
        if (value.endsWith(".0")) {
          value = value.substring(0, value.length() - 2);
        }
        ct.setDescChi(value); 
      } else {
        ct.setDescChi(row.getCell(3).getStringCellValue());
      }
      if (row.getCell(5) != null && row.getCell(5).getCellType() != CellType.BLANK) {
        ct.setParentCode(row.getCell(5).getRawValue());
      }
      if (row.getCell(6) != null && row.getCell(6).getCellType() != CellType.BLANK) {
        ct.setRemark(row.getCell(6).getRawValue());
      }
      if (ct == null || ct.getCode() == null) {
        continue;
      }
      ct.setCat(groupName);
      if (groupName.equals("FUNC_TYPE") && (ct.getRemark() != null)) {
        CODE_TABLE parent = ctDao.findByDescChiAndCat(ct.getRemark(), groupName);
        if (parent != null) {
          ct.setParentCode(parent.getCode());
        }
      }
      System.out.println(ct.getCat() + "," + ct.getCode() + "," + ct.getDescChi());
      CODE_TABLE ctInDB = ctDao.findByCodeAndCat(ct.getCode(), groupName);
      if (ctInDB == null || ctInDB.getDescChi() == null) {
        ctDao.save(ct);
      }
    }

    // System.out.println(
    // "================================" + groupName + "===================================");
    // printCodeBook(codes, groupName);
  }
  
  private XSSFSheet getSheetByName(XSSFWorkbook workbook, String name) {
    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
      XSSFSheet result = workbook.getSheetAt(i);
      if (result.getSheetName().contains(name)) {
        return result;
      }
    }
    return null;
  }
  
  /**
   * 判斷整列是否都是空的，並回傳以String型態的值
   * 
   * @param row
   * @param fieldList
   * @return
   */
  private String[] getAllCellStringValue(XSSFRow row) {
    if (row == null) {
      return null;
    }
    String[] result = new String[100];
    int i = 0;

    for (; i < row.getPhysicalNumberOfCells(); i++) {
      result[i] = getCellStringValue(row.getCell(i));
      if (result[i] == null && getCellStringValue(row.getCell(i + 1)) == null) {
        break;
      }
    }
    String[] ss = new String[i + 1];
    System.arraycopy(result, 0, ss, 0, i + 1);
    return ss;
  }
  
  private String getCellStringValue(XSSFCell cell) {
    if (cell == null) {
      return null;
    }
    if (cell.getCellType() == CellType.BLANK) {
      return "";
    } else if (cell.getCellType() == CellType.BOOLEAN) {
      return String.valueOf(cell.getBooleanCellValue());
    } else if (cell.getCellType() == CellType.ERROR) {
      return cell.getErrorCellString();
    } else if (cell.getCellType() == CellType.FORMULA) {
      return cell.getCellFormula();
    } else if (cell.getCellType() == CellType.STRING) {
      return cell.getStringCellValue().trim();
    } else if (cell.getCellType() == CellType.NUMERIC) {
      String number = String.valueOf(cell.getNumericCellValue());
      if (number.endsWith(".0")) {
        return number.substring(0, number.length() - 2);
      }
      return number;
    } else if (cell.getCellType() == CellType.STRING) {
      return cell.getStringCellValue().trim();
    }
    return null;
  }
  
  public void addUserFile() {
    String filename = FILE_PATH + "user.txt";
    try {
      BufferedReader br = new BufferedReader(
          new InputStreamReader(new FileInputStream(new File(filename)), "UTF-8"));
      String line = null;

      while ((line = br.readLine()) != null) {
        String[] ss = line.split(",");
        UserRequest user = new UserRequest();
        user.setDepartments(ss[3]);
        user.setPassword("leadtek");
        user.setRocId(ss[0]);
        user.setDisplayName(ss[1]);
        user.setUsername(ss[0]);
        user.setRole(ss[2]);
        user.setStatus(1);
        userService.newUser(user);
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void addDepartmentFile() {
    String filename = FILE_PATH + "department.txt";
    try {
      BufferedReader br = new BufferedReader(
          new InputStreamReader(new FileInputStream(new File(filename)), "UTF-8"));
      String line = null;

      while ((line = br.readLine()) != null) {
        String[] ss = line.split(",");
        DEPARTMENT department = new DEPARTMENT();
        department.setName(ss[0]);
        department.setNhName(ss[0]);
        department.setNhCode(ss[1]);
        department.setStatus(1);
        department.setCode(ss[1]);
        department.setNote("test");
        userService.newDepartment(department);
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void initialLeadtek() {
    UserRequest user = new UserRequest();
    user.setDepartments("ADM");
    user.setPassword("test");
    user.setRocId("leadtek");
    user.setDisplayName("leadtek");
    user.setUsername("leadtek");
    user.setRole("Z");
    user.setStatus(1);
    userService.newUser(user);
  }
}
