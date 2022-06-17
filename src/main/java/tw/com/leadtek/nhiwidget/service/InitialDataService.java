/**
 * Created on 2022/5/5.
 */
package tw.com.leadtek.nhiwidget.service;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tw.com.leadtek.nhiwidget.constant.XMLConstant;
import tw.com.leadtek.nhiwidget.controller.SystemController;
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.dao.PARAMETERSDao;
import tw.com.leadtek.nhiwidget.dao.PAY_CODEDao;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
import tw.com.leadtek.nhiwidget.model.redis.CodeBaseLongId;
import tw.com.leadtek.nhiwidget.model.redis.OrderCode;
import tw.com.leadtek.tools.ExcelUtil;

@Service
public class InitialDataService {

  private Logger logger = LogManager.getLogger();
  
  private final static String[] HOSP_LEVEL = new String[] {"基層院所", "醫學中心", "區域醫院", "地區醫院"};
  
  private final static String NO_TYPE = "不分類";
  
  @Autowired
  private PARAMETERSDao parametersDao;

  @Autowired
  private ParametersService parametersService;
  
  @Autowired
  private UserService userService;
  
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;
  
  @Autowired
  private RedisService redisService;
  
  @Autowired
  private PAY_CODEDao payCodeDao;
  
  @Autowired
  private CODE_TABLEDao ctDao;
  
  @Autowired
  private CodeTableService codeTableService;
  
//  @Autowired
//  private CODE_THRESHOLDDao codeThresholdDao;


  /**
   * 匯入PARAMETERS.xlsx 設定檔
   */
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
          if (row == null || row.getCell(0) == null) {
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
    if ("HOSP_EXPIRE".equals(p.getName())) {
      p.setValue(userService.encrypt(p.getValue()));
    }
    //System.out.println("upsertParameters " + p.getName() + ":" + p.getValue());
    List<PARAMETERS> list = parametersDao.findByName(p.getName());
    if (list != null) {
      boolean isFound = false;
      for (PARAMETERS parameters : list) {
        if (!p.getCat().equals(parameters.getCat())) {
          continue;
        }
        if (p.getStartDate() != null && p.getStartDate().equals(parameters.getStartDate())) {
          parameters.setValue(p.getValue());
          parameters.setNote(p.getNote());
          parameters.setCat(p.getCat());
          parameters.setDataType(p.getDataType());
          parameters.setEndDate(p.getEndDate());
          parameters.setUpdateAt(new Date());
          parametersDao.save(parameters);
          //System.out.println("found " + p.getName());
          isFound = true;
          break;
        }
      }
      if (!isFound) {
        parametersDao.save(p);
        //System.out.println("not found " + p.getName());
      }
    } else {
      parametersDao.save(p);
    }
  }
  
  /**
   * 匯入醫療服務給付項目(支付代碼)相關資料
   * @param file
   * @param sheetName
   * @param titleRow
   */
  public void importPayCode(File file, String fileFormat, int titleRow) {
    int maxId = getMaxId() + 1;
    String collectionName = "ICD10";
    String category = "ORDER";
    ZSetOperations<String, Object> op = redisTemplate.opsForZSet();
    HashMap<String, String> keys = getRedisId(collectionName + "-data", category);
    System.out.println("keys size=" + keys.size());
//    for (String string : keys.keySet()) {
//      System.out.println(string +":" + keys.get(string));
//    }
    try {
      List<PARAMETERS> payCodeType = parametersService.getByCat("PAY_CODE_TYPE");
      
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setSerializationInclusion(Include.NON_NULL);
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      HashOperations<String, String, String> hashOp = redisTemplate.opsForHash();

      int total = 0;
      XSSFSheet sheet = workbook.getSheetAt(0);
      HashMap<Integer, String> columnMap = ExcelUtil.readTitleRow(sheet.getRow(titleRow),
          parametersService.getByCat("PAY_CODE_" + fileFormat));
      HashMap<String, String> values = null;
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      DecimalFormat df = new DecimalFormat("#");
      List<PAY_CODE> payCodeBatch = new ArrayList<PAY_CODE>();
      for (int j = titleRow + 1; j < sheet.getPhysicalNumberOfRows(); j++) {
      //   for (int j = 1; j < 3; j++) {
        XSSFRow row = sheet.getRow(j);
        if (row == null || row.getCell(0) == null) {
          // System.out.println("sheet:" + i + ", row=" + j + " is null");
          continue;
        }
        values = ExcelUtil.readCellValue(columnMap, row, df);
        String code = values.get("CODE");
        if (SystemController.INIT_FILE_PAY_CODE.equals(fileFormat) && code.length() == 0) {
          break;
        }
        OrderCode oc = getOrderCodyByMap(values, sdf, df, maxId, payCodeType);
        PAY_CODE pc = PAY_CODE.convertFromOrderCode(oc);
        if (values.get("OE_POINT") != null) {
          pc.setOwnExpense(Double.parseDouble(values.get("OE_POINT")));
        }
        if (values.get("INH_CODE") != null) {
          pc.setInhCode(values.get("INH_CODE"));
        }
        if (values.get("ATC") != null) {
          pc.setAtc(values.get("ATC"));
        }
        payCodeBatch.add(saveOrderCodeToRedis(oc, pc, keys, maxId));
        if (payCodeBatch.size() > XMLConstant.BATCH) {
          payCodeDao.saveAll(payCodeBatch);
          payCodeBatch.clear();
        }
        if (oc.getId().longValue() == maxId) {
          maxId++;
        }
        total++;
      }
      workbook.close();
      
      if (payCodeBatch.size() > 0) {
        payCodeDao.saveAll(payCodeBatch);
        payCodeBatch.clear();
      }
    } catch (ParseException e) {
      logger.error("importPayCode failed", e);
      e.printStackTrace();
    } catch (InvalidFormatException e) {
      logger.error("importPayCode failed", e);
      e.printStackTrace();
    } catch (IOException e) {
      logger.error("importPayCode failed", e);
      e.printStackTrace();
    }
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
  
  /**
   * 將支付代碼存至Redis及DB
   * @param oc
   * @param keys
   * @param maxId
   * @return true: redis 無此代碼，已新增，false: redis 已存在此代碼，不需新增
   */
  private PAY_CODE saveOrderCodeToRedis(OrderCode oc, PAY_CODE payCode, HashMap<String, String> keys, long maxId) {
    String json = null;
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    try {
      json = objectMapper.writeValueAsString(oc);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    String sId = keys.get(oc.getCode());
    if (sId == null) {
      oc.setId(maxId);
      payCode.setRedisId((int) maxId);
      redisService.putHash(RedisService.DATA_KEY, String.valueOf(oc.getId()), json);
      if (oc.getCode() == null) {
        redisService.addIndexToRedisIndex(RedisService.INDEX_KEY, String.valueOf(oc.getId()),
            payCode.getInhCode());
      } else {
        redisService.addIndexToRedisIndex(RedisService.INDEX_KEY, String.valueOf(oc.getId()),
            oc.getCode());
      }
    } else {
      oc.setId(Long.parseLong(sId));
      payCode.setRedisId(Integer.parseInt(sId));
//      redisService.putHash(RedisService.DATA_KEY, sId, json);
//      redisService.addIndexToRedisIndex(RedisService.INDEX_KEY, sId, oc.getCode());
    }
    return savePayCode(payCode);
  }

  private PAY_CODE savePayCode(PAY_CODE code) {
    if (code.getStartDate() == null) {
      System.out.println("getStartDate null,code=" + code.getCode() + "," + code.getInhCode() +"," + code.getName());
    }
    List<PAY_CODE> codes = payCodeDao.findByCode(code.getCode());
    if (codes == null || codes.size() == 0) {
      return code;
    } else {
      for (PAY_CODE old : codes) {
        if (code.getAtc() != null) {
          old.setAtc(code.getAtc());
        }
        if (code.getCodeType() != null) {
          old.setCodeType(code.getCodeType());
        }
        if (code.getEndDate() != null) {
          old.setEndDate(code.getEndDate());
        }
        if (code.getStartDate() != null) {
          old.setStartDate(code.getStartDate());
        }
        if (code.getName() != null) {
          old.setName(code.getName());
          old.setInhName(code.getName());
        }
        if (code.getHospLevel() != null) {
          old.setHospLevel(code.getHospLevel());
        }
        if (code.getPoint() != null) {
          old.setPoint(code.getPoint());
        }
        old.setUpdateAt(new Date());
        if (code.getRedisId() != null) {
          old.setRedisId(code.getRedisId());
        }
        if (code.getNameEn() != null) {
          old.setNameEn(code.getNameEn());
        }
        if (code.getOwnExpense() != null) {
          old.setOwnExpense(code.getOwnExpense());
        }
        if (code.getInhCode() != null) {
          old.setInhCode(code.getCode());
        }
        return old;
      }
    }
    return code;
  }

  /**
   * 取得 key 此 hashset 裡面 category為cat的code/id對應表
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
  
  private OrderCode getOrderCodyByExcelRowNew(XSSFRow row, SimpleDateFormat sdf, 
      DecimalFormat df, long maxId, List<PARAMETERS> parameters)
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

    if (row.getCell(7) != null) {
      oc.setDetail(row.getCell(7).getStringCellValue());
      oc.setDetail(getPayCodeType(oc.getDetail(), parameters));
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
  
  private OrderCode getOrderCodyByMap(HashMap<String, String> values, SimpleDateFormat sdf, 
      DecimalFormat df, long maxId, List<PARAMETERS> parameters)
      throws ParseException {

    OrderCode oc = new OrderCode(maxId, values.get("CODE"), values.get("DESC"), values.get("DESC_EN"));
    if (values.get("POINT") != null) {
      String point = values.get("POINT");
      if (point.indexOf('.') > 0) {
        point = point.substring(0, point.indexOf('.'));
      }
      oc.setP(Integer.parseInt(point));  
    }
    //System.out.println("code=" + values.get("CODE") + ", startDate=" + values.get("START_DATE"));
    oc.setsDate(getDateFromExcelValue(values.get("START_DATE"), sdf));
    if (values.get("DELETE_DATE") != null) {
      // 刪除日期
      oc.seteDate(getDateFromExcelValue(values.get("DELETE_DATE"), sdf));
    } else {
      oc.seteDate(getDateFromExcelValue(values.get("END_DATE"), sdf));
    }
    
    if (values.get("PAY_CODE_TYPE") != null) {
      oc.setDetail(values.get("PAY_CODE_TYPE") );
      oc.setDetail(getPayCodeType(oc.getDetail(), parameters));
    }
    
    oc.setDetailCat(values.get("DETAIL_CAT"));
  
    if (values.get("HOSP_LEVEL") != null) {
      String[] level = values.get("HOSP_LEVEL").split(",");
      oc.setLevel(getHospLevel(level));
    }
    oc.setOutIsland(values.get("OUT_ISLAND"));
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
  
  private String getPayCodeType(String type, List<PARAMETERS> parameters) {
    if (type == null || type.trim().length() == 0) {
      return NO_TYPE;
    }
    for (PARAMETERS p : parameters) {
      if (p.getName().equals(type)) {
        return p.getValue();
      }
    }
    return NO_TYPE;
  }
  
  /**
   * 匯入 CODE_TABLE.xlsx 檔
   * @param filename
   */
  public void importCODE_TABLEToRDB(File file, String sheetName) {
    System.out.println("importCODE_TABLEToRDB " + file.getName());

    try {
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      processCODE_TABLE(workbook, sheetName);
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
  private void processCODE_TABLE(XSSFWorkbook workbook, String sheetName) {
    XSSFSheet sheet = getSheetByName(workbook, sheetName);
    if (sheet == null) {
      System.out.println(sheetName + " not exist.");
      return;
    }
//    SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
//    Date startDate = null;
//    Date endDate = null;
//    try {
//      startDate = sdf.parse("2019/01/01");
//      endDate = sdf.parse(DateTool.MAX_DATE);
//    } catch (ParseException e) {
//      e.printStackTrace();
//    }
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
      CODE_TABLE ctInDB = ctDao.findByCodeAndCat(ct.getCode(), groupName);
      if (ctInDB == null || ctInDB.getDescChi() == null) {
        ctDao.save(ct);
      }
    }
    codeTableService.refreshCodes();
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
  
  private int getColumnIndex(HashMap<Integer, String> columnMap, String columnName) {
    for (Integer key : columnMap.keySet()) {
      if (columnName.equals(columnMap.get(key))) {
        return key.intValue();
      }
    }
    return -1;
  }
  
  private Date getDateFromExcelValue(String date, SimpleDateFormat sdf) throws ParseException {
    if (date == null || date.length() < 8) {
      return null;
    }
    if (date.length() == 8) {
      return sdf.parse(date);
    } else if (date.indexOf('/') > 0) {
      // 民國年 111/01/01
      String[] ss = date.split("/");
      int year = Integer.parseInt(ss[0]) + 1911;
      return sdf.parse(String.valueOf(year) + ss[1] + ss[2]);
    }
    return null;
  }
  
  public void importDRGFromExcel(File file, String sheetName) {
    
  }
}
