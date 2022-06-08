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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.PARAMETERSDao;
import tw.com.leadtek.nhiwidget.dao.PAY_CODEDao;
import tw.com.leadtek.nhiwidget.dao.USERDao;
import tw.com.leadtek.nhiwidget.importdata.WriteToRedisThreadPool;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.DEPARTMENT;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
import tw.com.leadtek.nhiwidget.model.redis.CodeBaseLongId;
import tw.com.leadtek.nhiwidget.model.redis.OrderCode;
import tw.com.leadtek.nhiwidget.payload.UserRequest;
import tw.com.leadtek.nhiwidget.service.RedisService;
import tw.com.leadtek.nhiwidget.service.SystemService;
import tw.com.leadtek.nhiwidget.service.UserService;
import tw.com.leadtek.tools.DateTool;
import tw.com.leadtek.tools.ExcelUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class InitialEnvironment {

  private Logger logger = LogManager.getLogger();
  
  public final static String SERVER_IP = "localhost";
  
  // 最後要有 \\
  //public final static String FILE_PATH = "D:\\Documents\\Projects\\Leadtek\\健保點數申報\\docs_健保點數申報\\資料匯入用\\Job\\";
  public final static String FILE_PATH = "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\Job\\";
  
  @Autowired
  private PARAMETERSDao pDao;
  
  @Autowired
  private CODE_TABLEDao ctDao;
  
  @Autowired
  private USERDao userDao;
  
  @Autowired
  private UserService userService;
  
  @Autowired
  private SystemService systemService;
  
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private RedisService redisService;

  @Autowired
  private PAY_CODEDao payCodeDao;

  @Autowired
  private IP_PDao ippDao;
  
  @Autowired
  private OP_PDao oppDao;

  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d");

  private final static String[] HOSP_LEVEL = new String[] {"基層院所", "醫學中心", "區域醫院", "地區醫院"};

  /**
   * 存放在 HashSet 的 id
   */
  private long maxId = 0;

  //@Ignore
  @Test
  public void importData() {
    if(userDao.count() ==0) {
      initialLeadtek();
    }
    importFromExcel(FILE_PATH + "PARAMETERS.xlsx", "參數設定", 1);
    //importCODE_TABLEToRDB(FILE_PATH + "CODE_TABLE_霖園醫院.xlsx");
    //addDepartmentFile();
    //addUserFile();
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
    user.setRole("A");
    user.setStatus(1);
    userService.newUser(user);
  }
  
  @Ignore
  @Test
  public void importPayCode() {
    maxId = getMaxId() + 1;
    System.out.println("maxid=" + maxId);
    importExcelToRedisNew("ICD10", FILE_PATH + "醫療服務給付項目(1110518生效).xlsx", "ORDER");
    //updateCodeTypeCode();
  }
  
  /**
   * 更新IP_P, OP_P 醫令的標準代碼類別代碼
   */
  public void updateCodeTypeCode() {
    System.out.println("updateCodeTypeCode");
    // 1. 先將 OP_P.DRUG_NO 長度=10 的列為藥品類
    // UPDATE OP_P SET PAY_CODE_TYPE = '7' WHERE LENGTH(DRUG_NO) = 10 
    // 2. 再將 OP_P.DRUG_NO 長度>10 的列為不分類
    // UPDATE OP_P SET PAY_CODE_TYPE = '20' WHERE LENGTH(DRUG_NO) > 10
    // 3. 其餘找 OP_P.DRUG_NO = PAY_CODE.CODE 的 CODE_TYPE
   
    List<Object[]> codeListOP = payCodeDao.findOPAllPayCodeAndTypeCode();
    HashMap<String, String> payCodeType = new HashMap<String, String>();
    for (Object[] objects : codeListOP) {
      payCodeType.put((String)objects[0], (String) objects[1]);
    }
    
    List<String> codes = new ArrayList<String>();
    List<Object[]> oppDrugNo = oppDao.findDistinctDrugNo();
    for (Object[] objects : oppDrugNo) {
      codes.add((String)objects[0]);
    }
    for (String code : codes) {
      System.out.println("update " + payCodeType.get(code) + "," + code);
      if (payCodeType.get(code) == null) {
        // 20 不分類
        oppDao.updatePayCodeType("20", code);
      } else {
        oppDao.updatePayCodeType(payCodeType.get(code), code);
      }
    }
    // 4. 最後剩下沒有支付代碼類別的全部設為不分類
    // UPDATE OP_P SET PAY_CODE_TYPE = '20' WHERE PAY_CODE_TYPE IS NULL
    oppDao.updatePayCodeType20();
    
    // 5. 先將 IP_P.ORDER_CODE 長度=10 的列為藥品類
    // UPDATE IP_P SET PAY_CODE_TYPE ='7' WHERE LENGTH (ORDER_CODE) = 10
    // 6. 再將 IP_P.ORDER_CODE 長度>10 的列為不分類
    // UPDATE IP_P SET PAY_CODE_TYPE = '20' WHERE LENGTH(ORDER_CODE) > 10
    // 7. 其餘找 IP_P.ORDER_CODE = PAY_CODE.CODE 的 CODE_TYPE
    
    List<Object[]> codeListIP = payCodeDao.findIPAllPayCodeAndTypeCode();
    System.out.println("codeListIP size=" + codeListIP.size());
    payCodeType = new HashMap<String, String>();
    for (Object[] objects : codeListIP) {
      payCodeType.put((String)objects[0], (String) objects[1]);
    }
    
    codes = new ArrayList<String>();
    List<Object[]> ippOrderCode = ippDao.findDistinctOrderCode();
    for (Object[] objects : ippOrderCode) {
      codes.add((String)objects[0]);
    }
    for (String code : codes) {
      System.out.println("update " + payCodeType.get(code) + "," + code);
      if (payCodeType.get(code) == null) {
        // 20 不分類
        ippDao.updatePayCodeType("20", code);
      } else {
        ippDao.updatePayCodeType(payCodeType.get(code), code);
      }
    }
    
    // 8. 最後剩下沒有支付代碼類別的全部設為不分類
    // UPDATE IP_P SET PAY_CODE_TYPE = '20' WHERE PAY_CODE_TYPE IS NULL 
    ippDao.updatePayCodeType20();
  }
  
  private int getMaxId() {
    HashOperations<String, String, String> hashOp = redisTemplate.opsForHash();
    Set<String> fields = hashOp.keys(RedisService.DATA_KEY);
    int result = -1;
    for (String field : fields) {
      int id = Integer.parseInt(field);
      if (id > result) {
        result = id;
      }
    }
    return result;
  }
  
  public void importExcelToRedisNew(String collectionName, String filename, String category) {
    ZSetOperations<String, Object> op = redisTemplate.opsForZSet();
    // 取得目前已存在 redis 和 DB 中的支付標準代碼
    HashMap<String, String> keys = getRedisId(collectionName + "-data", category);
    System.out.println("keys size=" + keys.size());
    
//    for (String string : keys.keySet()) {
//      System.out.println(string +":" + keys.get(string));
//    }
//    if (keys.size() > 0) {
//      return;
//    }
    File file = new File(filename);
    WriteToRedisThreadPool wtrPool = new WriteToRedisThreadPool();
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setSerializationInclusion(Include.NON_NULL);
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      HashOperations<String, String, String> hashOp = redisTemplate.opsForHash();

      int total = 0;
      XSSFSheet sheet = workbook.getSheetAt(0);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      DecimalFormat df = new DecimalFormat("#");
      for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
      //   for (int j = 1; j < 3; j++) {
        XSSFRow row = sheet.getRow(j);
        if (row == null || row.getCell(0) == null) {
          // System.out.println("sheet:" + i + ", row=" + j + " is null");
          continue;
        }
        String code = null;
        if (row.getCell(0).getCellType() == CellType.NUMERIC) {
          code = df.format(row.getCell(0).getNumericCellValue());
        } else {
          code = row.getCell(0).getStringCellValue().trim().toLowerCase();
        }
        if (code.length() == 0) {
          break;
        }
        OrderCode oc = getOrderCodyByExcelRowNew(row, sdf, df);
        saveOrderCodeToRedis(oc, keys);
        total++;
      }
      System.out.println("finish total:" + total);
      workbook.close();
    } catch (ParseException e) {
      logger.error("import excel failed", e);
      e.printStackTrace();
    } catch (InvalidFormatException e) {
      logger.error("import excel failed", e);
      e.printStackTrace();
    } catch (IOException e) {
      logger.error("import excel failed", e);
      e.printStackTrace();
    }
  }

  private void saveOrderCodeToRedis(OrderCode oc, HashMap<String, String> keys) {
    PAY_CODE payCode = PAY_CODE.convertFromOrderCode(oc);

    String json = null;
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    ZSetOperations<String, Object> op = redisTemplate.opsForZSet();
    try {
      json = objectMapper.writeValueAsString(oc);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    String sId = keys.get(oc.getCode());
    if (sId == null) {
      oc.setId(maxId);
      payCode.setRedisId((int) maxId);
      maxId++;
      redisService.putHash(RedisService.DATA_KEY, String.valueOf(oc.getId()), json);
      redisService.addIndexToRedisIndex(RedisService.INDEX_KEY, String.valueOf(oc.getId()),
          oc.getCode());
    } else {
      oc.setId(Long.parseLong(sId));
      payCode.setRedisId(Integer.parseInt(sId));
      redisService.putHash(RedisService.DATA_KEY, sId, json);
      redisService.addIndexToRedisIndex(RedisService.INDEX_KEY, sId, oc.getCode());
    }
    savePayCode(payCode);
  }

  private void savePayCode(PAY_CODE code) {
    List<PAY_CODE> codes = payCodeDao.findByCode(code.getCode());
    if (codes == null || codes.size() == 0) {
      payCodeDao.save(code);
    } else {
      boolean isFound = false;
      for (PAY_CODE old : codes) {
        if (old.getStartDate().getTime() == code.getStartDate().getTime()) {
          isFound = true;
          old.setAtc(code.getAtc());
          old.setCodeType(code.getCodeType());
          old.setEndDate(code.getEndDate());
          old.setStartDate(code.getStartDate());
          old.setName(code.getName());
          old.setHospLevel(code.getHospLevel());
          old.setPoint(code.getPoint());
          old.setUpdateAt(new Date());
          old.setRedisId(code.getRedisId());
          old.setNameEn(code.getNameEn());
          old.setOwnExpense(code.getOwnExpense());
          payCodeDao.save(old);
          break;
        }
      }
      if (!isFound) {
        payCodeDao.save(code);
      }
    }
  }

  /**
   * 取得 key 此 hashmap 裡面 category為cat的code/id對應表
   * 
   * @param key
   * @param cat
   * @return HashMap<code, id>
   */
  private HashMap<String, String> getRedisId(String key, String cat) {
    HashMap<String, String> result = new HashMap<String, String>();
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);
    List<Object> list = redisTemplate.opsForHash().values(key);
    System.out.println(key + "-" + cat + " size:" + list.size());
    for (Object object : list) {
      try {
        String value = (String) object;
        if (value.indexOf("\"category\":\"" + cat + "\"") < 0) {
          continue;
        }
        if (value.indexOf("detailCat") > 0 || value.indexOf("sDate") > 0
            || value.indexOf("level") > 0 || value.indexOf("law") > 0) {
          OrderCode oc = mapper.readValue(value, OrderCode.class);
          if (cat.equals(oc.getCategory())) {
            result.put(oc.getCode(), oc.getId().toString());
          }
        } else {
          CodeBaseLongId cb = mapper.readValue(value, CodeBaseLongId.class);
          if (cat.equals(cb.getCategory())) {
            result.put(cb.getCode(), cb.getId().toString());
          }
        }
      } catch (JsonMappingException e) {
        e.printStackTrace();
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }
    return result;
  }
  
  private OrderCode getOrderCodyByExcelRowNew(XSSFRow row, SimpleDateFormat sdf, DecimalFormat df)
      throws ParseException {
    String code = null;
    if (row.getCell(0).getCellType() == CellType.NUMERIC) {
      code = String.valueOf(df.format(row.getCell(3).getNumericCellValue()));
    } else if (row.getCell(0).getStringCellValue().length() > 0) {
      code = row.getCell(0).getStringCellValue().trim();
    } else {
      System.err.println("error!");
      return null;
    }

    String descEn = row.getCell(5).getStringCellValue().trim();
    String descTw = null;
    if (row.getCell(4) != null) {
      descTw = row.getCell(4).getStringCellValue();
      if (descTw != null) {
        descTw = descTw.trim();
      }
    }

    // addCode1(collectionName, code);
    OrderCode oc = new OrderCode(maxId, code, descTw, descEn);

    oc.setP((int) row.getCell(1).getNumericCellValue());
    oc.setsDate(getDateFromCell(row.getCell(2), sdf, df));
    if (row.getCell(22) != null) {
      // 22:刪除日期
      oc.seteDate(getDateFromCell(row.getCell(22), sdf, df));
    } else {
      oc.seteDate(getDateFromCell(row.getCell(3), sdf, df));
    }
    System.out.println(oc.getCode() + ":" + oc.getsDate() + "-" + oc.geteDate());

    if (row.getCell(7) != null) {
      oc.setDetail(row.getCell(7).getStringCellValue());
    }
    if (row.getCell(8) != null) {
      oc.setDetailCat(row.getCell(8).getStringCellValue());
    }
    if (row.getCell(9) != null) {
      String[] level = row.getCell(9).getStringCellValue().split(",");
      oc.setLevel(getHospLevel(level));
    }
    if (row.getCell(10) != null) {
      oc.setOutIsland(row.getCell(10).getStringCellValue());
    }
    return oc;
  }
  
  private Date getDateFromCell(XSSFCell cell, SimpleDateFormat sdf, DecimalFormat df)
      throws ParseException {
    if (cell != null) {
      if (cell.getCellType() == CellType.NUMERIC) {
        double value = cell.getNumericCellValue();
        if (value > 10000000) {
          // yyyyMMdd 格式
          return sdf.parse(String.valueOf(df.format(value)));
        } else {
          return cell.getDateCellValue();
        }
      } else if (cell.getStringCellValue().length() > 0) {
        return sdf.parse(cell.getStringCellValue());
      }
    }
    return null;
  }
  
  private String getHospLevel(String[] s) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < s.length; i++) {
      for (int j = 0; j < HOSP_LEVEL.length; j++) {
        if (s[i].equals(HOSP_LEVEL[j])) {
          sb.append(j);
          sb.append(",");
          break;
        }
      }
    }
    if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }
}
