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
import tw.com.leadtek.nhiwidget.dao.ATCDao;
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.dao.DEPARTMENTDao;
import tw.com.leadtek.nhiwidget.dao.ICD10Dao;
import tw.com.leadtek.nhiwidget.dao.PARAMETERSDao;
import tw.com.leadtek.nhiwidget.dao.PAY_CODEDao;
import tw.com.leadtek.nhiwidget.dao.USERDao;
import tw.com.leadtek.nhiwidget.dao.USER_DEPARTMENTDao;
import tw.com.leadtek.nhiwidget.model.rdb.ATC;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.DEPARTMENT;
import tw.com.leadtek.nhiwidget.model.rdb.ICD10;
import tw.com.leadtek.nhiwidget.model.rdb.PARAMETERS;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
import tw.com.leadtek.nhiwidget.model.rdb.USER;
import tw.com.leadtek.nhiwidget.model.rdb.USER_DEPARTMENT;
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
  
  @Autowired
  private ATCDao atcDao;
  
  @Autowired
  private ICD10Dao icd10Dao;
  
  @Autowired
  private DEPARTMENTDao departmentDao;

  @Autowired
  private USERDao userDao;
  
  @Autowired
  private USER_DEPARTMENTDao userDepartmentDao;

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
//    for (String string : keys.keySet()) {
//      System.out.println(string +":" + keys.get(string));
//    }
    try {
      // 讀到 "基本診療 - 門診診察費" 要轉成 "門診診察費"
      List<PARAMETERS> payCodeType = parametersService.getByCat("PAY_CODE_TYPE_" + fileFormat);
      if (payCodeType == null || payCodeType.size() == 0) {
          parametersService.getByCat("PAY_CODE_TYPE");
      }
      
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setSerializationInclusion(Include.NON_NULL);
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      HashOperations<String, String, String> hashOp = redisTemplate.opsForHash();

      int total = 0;
      int add = 0;
      int update = 0;
      XSSFSheet sheet = workbook.getSheetAt(0);
      HashMap<Integer, String> columnMap = ExcelUtil.readTitleRow(sheet.getRow(titleRow),
          parametersService.getByCat("PAY_CODE_" + fileFormat));
      HashMap<String, String> values = null;
      SimpleDateFormat sdf = (SystemController.INIT_FILE_PAY_CODE_POHAI.equals(fileFormat))
          ? new SimpleDateFormat("yyyy/M/d")
          : new SimpleDateFormat("yyyyMMdd");
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
        total++;
        OrderCode oc = getOrderCodyByMap(values, sdf, df, maxId, payCodeType);
        PAY_CODE pc = PAY_CODE.convertFromOrderCode(oc);
        if (pc.getHospLevel() != null && pc.getHospLevel().length() > 11) {
          logger.error("PAY_CODE " + pc.getCode() + ",inhCode=" + pc.getInhCode() + "," + pc.getHospLevel());
        }
        if (values.get("OE_POINT") != null) {
          pc.setOwnExpense(Double.parseDouble(values.get("OE_POINT")));
        }
        if (values.get("INH_CODE") != null) {
          pc.setInhCode(values.get("INH_CODE"));
        }
        if (values.get("INH_NAME") != null) {
          pc.setInhName(values.get("INH_NAME"));
          if (pc.getInhName().length() > 180) {
            pc.setInhName(pc.getInhName().substring(0, 179));
          }
        }
        if (values.get("ATC") != null) {
          pc.setAtc(values.get("ATC"));
        }
        if (pc.getNameEn() != null && pc.getNameEn().length() > 100) {
          pc.setNameEn(pc.getNameEn().substring(0, 99));
        }
        if (pc.getCodeType() == null || NO_TYPE.equals(pc.getCodeType())) {
          String codeDrug = pc.getCode() != null ? pc.getCode() : pc.getInhCode();
            if (codeDrug.length() == 10) {
              pc.setCodeType("藥費");
            } else if (codeDrug.length() == 12) {
              pc.setCodeType("衛材品項");
            }
        }
        if (pc.getCodeType() == null) {
          pc.setCodeType(NO_TYPE);
        }
        PAY_CODE pcDB = checkPayCode(pc);
        if (pcDB != null) {
          update++;
          payCodeBatch.add(pcDB);
        } else {
          add++;
          payCodeBatch.add(saveOrderCodeToRedis(oc, pc, keys, maxId));
          if (payCodeBatch.size() > XMLConstant.BATCH) {
            payCodeDao.saveAll(payCodeBatch);
            payCodeBatch.clear();
          }
          if (oc.getId().longValue() == maxId) {
            maxId++;
          }
        }
      }
      workbook.close();
      
      if (payCodeBatch.size() > 0) {
        payCodeDao.saveAll(payCodeBatch);
        payCodeBatch.clear();
      }
      logger.info("total:" + total + ", add:" + add + ", update=" + update +"," + file.getAbsolutePath());
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
  
  /**
   * 同一健保碼 code 有可能對應該二組以上不同的院內碼 inhCode
   * @param pc
   * @return
   */
  private PAY_CODE checkPayCode(PAY_CODE pc) {
    List<PAY_CODE> list = null;
    if (pc.getCode() == null || pc.getCode().length() == 0) {
      list = payCodeDao.findByInhCodeOrderByStartDateDesc(pc.getInhCode());
    } else {
      list = payCodeDao.findByCodeOrderByStartDateDesc(pc.getCode());                
    }
    if (list == null || list.size() == 0) {
      return null;
    }
    for (PAY_CODE pay_CODE : list) {
      if (pay_CODE.getStartDate() == null || pay_CODE.getEndDate() == null
          || pc.getEndDate() == null || pc.getStartDate() == null
          || (pay_CODE.getStartDate().getTime() <= pc.getStartDate().getTime()
              && pay_CODE.getEndDate().getTime() >= pc.getStartDate().getTime()
              && pay_CODE.getStartDate().getTime() <= pc.getEndDate().getTime()
              && pay_CODE.getEndDate().getTime() >= pc.getEndDate().getTime())) {
        if (pc.getInhCode() != null && pc.getInhCode().length() > 0) {
          if (pay_CODE.getInhCode() != null && !pay_CODE.getInhCode().equals(pc.getInhCode())) {
            continue;
          }
        }
        pay_CODE.setInhCode(pc.getInhCode());
        if (pc.getInhName() != null) {
          if (pc.getInhName().length() > 180) {
            pc.setInhName(pc.getInhName().substring(0, 179));
          }
          pay_CODE.setInhName(pc.getInhName());
        }
        pay_CODE.setOwnExpense(pc.getOwnExpense());
        pay_CODE.setPoint(pc.getPoint());
        pay_CODE.setName(pc.getName());
        if (pay_CODE.getCodeType() == null || NO_TYPE.equals(pay_CODE.getCodeType())) {
          pay_CODE.setCodeType(pc.getCodeType());
        }
        if (pc.getAtc() != null && pc.getAtc().length() > 0) {
          pay_CODE.setAtc(pc.getAtc());
        }
        pay_CODE.setUpdateAt(new Date());
        return pay_CODE;
      }
    }
    return null;
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
//    if (code.getStartDate() == null) {
//      System.out.println("getStartDate null,code=" + code.getCode() + "," + code.getInhCode() +"," + code.getName());
//    }
//    List<PAY_CODE> codes = payCodeDao.findByCode(code.getCode());
//    if (codes == null || codes.size() == 0) {
//      return code;
//    } else {
//      for (PAY_CODE old : codes) {
//        if (code.getAtc() != null) {
//          old.setAtc(code.getAtc());
//        }
//        if (code.getCodeType() != null) {
//          old.setCodeType(code.getCodeType());
//        }
//        if (code.getEndDate() != null) {
//          old.setEndDate(code.getEndDate());
//        }
//        if (code.getStartDate() != null) {
//          old.setStartDate(code.getStartDate());
//        }
//        if (code.getName() != null) {
//          old.setName(code.getName());
//          old.setInhName(code.getName());
//        }
//        if (code.getHospLevel() != null) {
//          old.setHospLevel(code.getHospLevel());
//        }
//        if (code.getPoint() != null) {
//          old.setPoint(code.getPoint());
//        }
//        old.setUpdateAt(new Date());
//        if (code.getRedisId() != null) {
//          old.setRedisId(code.getRedisId());
//        }
//        if (code.getNameEn() != null) {
//          old.setNameEn(code.getNameEn());
//        }
//        if (code.getOwnExpense() != null) {
//          old.setOwnExpense(code.getOwnExpense());
//        }
//        if (code.getInhCode() != null) {
//          old.setInhCode(code.getCode());
//        }
//        return old;
//      }
//    }
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
            || value.indexOf("level") > 0 || value.indexOf("law") > 0 || 
            value.indexOf("\"p\"") > 0) {
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
      boolean isFound = false;
      for (int j = 0; j < HOSP_LEVEL.length; j++) {
        if (s[i].equals(HOSP_LEVEL[j])) {
          sb.append(j);
          sb.append(",");
          isFound = true;
          break;
        }
      }
      if (!isFound && s[i].length() == 1) {
        sb.append(s[i]);
        sb.append(",");
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
    System.out.println("import CODE_TABLE To RDB " + file.getName());

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
      List<CODE_TABLE> ctList = ctDao.findByCodeAndCat(ct.getCode(), groupName);
      if (ctList == null || ctList.size() == 0) {
        ctDao.save(ct);
      } else {
        for (CODE_TABLE ctDB : ctList) {
          ctDB.setDescChi(ct.getDescChi());
          ctDB.setDescEn(ct.getDescEn());
          ctDao.save(ctDB);
        }
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
    if (date.indexOf(' ') > 0) {
      date = date.split(" ")[0];
    }
    if (date.length() == 8) {
      return sdf.parse(date);
    } else if (date.indexOf('/') > 0) {
      String[] ss = date.split("/");
      if (ss[0].length() == 3) {
        // 民國年 111/01/01
        int year = Integer.parseInt(ss[0]) + 1911;
        return sdf.parse(String.valueOf(year) + ss[1] + ss[2]);
      } else {
        SimpleDateFormat sdf2 = null;
        if (ss[1].length() == 1 || ss[2].length() == 1) {
          sdf2 = new SimpleDateFormat("yyyy/M/d");
          return sdf2.parse(date);
        } else if (ss[1].length() == 2 && ss[2].length() == 2) {
          sdf2 = new SimpleDateFormat("yyyy/MM/dd");
          return sdf2.parse(date);
        } else {
          return sdf.parse(date);
        }
      }
    }
    return null;
  }
  
  public void importDRGFromExcel(File file, String sheetName) {
    
  }
  
  public void importICD10ToRedis(File file, String category) {
    System.out.println("importICD10ToRedis");
    long start = System.currentTimeMillis();
     importExcelToRedis("ICD10", file, category); // "ICD10-CM");
            // InitialEnvironment.FILE_PATH + "1.1 中文版ICD-10-CM(106.07.19更新)_Chapter.xlsx",
  
//     importExcelToRedis("ICD10",
//             InitialEnvironment.FILE_PATH + "1.2 中文版ICD-10-PCS(106.07.19更新).xlsx",
//     "ICD10-PCS");

    long usedTime = System.currentTimeMillis() - start;
    System.out.println("usedTime:" + usedTime);
    System.out.println(String.format("use time: %.2f", (float) usedTime / (float) 1000));
  }
  
  public void importExcelToRedis(String collectionName, File file, String category) {
    // HashOperations<String, String, Object> hashOp = ;
    // long maxId = redisTemplate.opsForHash().size(collectionName + "-data");
    long maxId = (long) redisService.getMaxId();
    if (maxId == 0) {
      maxId = 1;
    }
 
    int addCount = 0;
    //WriteToRedisThreadPool wtrPool = new WriteToRedisThreadPool();
    //Thread poolThread = new Thread(wtrPool);
    try {
      ZSetOperations<String, Object> op = redisTemplate.opsForZSet();
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setSerializationInclusion(Include.NON_NULL);
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      HashOperations<String, String, String> hashOp = redisTemplate.opsForHash();
      // DataFormatter formatter = new DataFormatter();

      //poolThread.start();
      System.out.println("maxId:" + maxId + ", sheet count=" + workbook.getNumberOfSheets());
      int total = 0;
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        XSSFSheet sheet = workbook.getSheetAt(i);
        // System.out.println("sheet " + i + " :" +
        // sheet.getRow(1).getCell(0).getStringCellValue());
        if (!sheet.getRow(1).getCell(0).getStringCellValue().equals("代碼")) {
          continue;
        }
        System.out.println("sheet name:" + sheet.getSheetName() + "," + sheet.getPhysicalNumberOfRows());
        int count = 0;
        for (int j = 2; j < sheet.getPhysicalNumberOfRows() - 1; j = j + 2) {
          XSSFRow row = sheet.getRow(j);
          if (row.getCell(0) == null) {
            // System.out.println("sheet:" + i + ", row=" + j + " is null");
            continue;
          }
          String code = row.getCell(0).getStringCellValue().trim();
          // System.out.println("save " + code);
          if (code.length() == 0) {
            break;
          }
          
          String descEn = row.getCell(1).getStringCellValue().trim();
          row = sheet.getRow(j + 1);
          String descTw = row.getCell(1).getStringCellValue().trim();
          //System.out.println("code=" + code +",en=" + descEn + ",tw=" + descTw);
          //logger.info("save icd code=" + code +",en=" + descEn + ",tw=" + descTw);
          long dataId = getCodeId(code, category, op, hashOp, objectMapper);
          if (dataId > 0) {
            saveICD10DB(code, category, descTw, descEn, dataId);
            continue;
          } else {
            //System.out.println("not found in redis:" + code);
          }

          // addCode1(collectionName, code);
          CodeBaseLongId cb = new CodeBaseLongId(++maxId, code, descTw, descEn);
          String s = hashOp.get(RedisService.DATA_KEY, String.valueOf(maxId));
          if (s != null && s.indexOf(code) > 0) {
            // DB 有這筆資料，換下一筆
            // maxId++;
            continue;
          }

          cb.setCategory(category);
          // addCount += addCode3(op, objectMapper, collectionName, cb);
          addCodeByThread(null, collectionName, cb, false);
          //System.out.println("add(" + maxId + ")[" + addCount + "]" + code + ":" + descEn);
          saveICD10DB(code, category, descTw, descEn, cb.getId());
          //System.out.println("save " + code);
          count++;
          total++;
//          if (wtrPool.getThreadCount() > 1000) {
//            do {
//              try {
//                Thread.sleep(2000);
//              } catch (InterruptedException e) {
//                e.printStackTrace();
//              }
//            } while (wtrPool.getThreadCount() > 1000);
//          }
        }
      }
      //wtrPool.setFinished(true);
      System.out.println("finish total:" + total);
      workbook.close();

//      do {
//        try {
//          Thread.sleep(200);
//          System.out.println("elapsed:" + wtrPool.getThreadCount());
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
//      } while (wtrPool.getThreadCount() > 0);
    } catch (InvalidFormatException e) {
      logger.error("import excel failed", e);
      e.printStackTrace();
    } catch (IOException e) {
      logger.error("import excel failed", e);
      e.printStackTrace();
    }
  }

  /**
   * 取得 code 在ICD10-data 中的id
   * @param code
   * @param category
   * @param zsetOp
   * @param hashOp
   * @param mapper
   * @return
   */
  public long getCodeId(String code, String category, ZSetOperations<String, Object> zsetOp,
      HashOperations<String, String, String> hashOp, ObjectMapper mapper) {
    // ZSetOperations<String, Object> zsetOp =
    // (ZSetOperations<String, Object>) redisTemplate.opsForZSet();
    Set<String> set = (Set<String>) (Set<?>) zsetOp.range(RedisService.INDEX_KEY + code.toLowerCase(), 0, -1);
    List<String> values = hashOp.multiGet(RedisService.DATA_KEY, set);
    for (String string : values) {
      if (string == null) {
        continue;
      }
      try {
        if (string.indexOf("detailCat") > 0 || string.indexOf("sDate") > 0
            || string.indexOf("level") > 0 || string.indexOf("law") > 0) {
          //OrderCode oc = mapper.readValue(string, OrderCode.class);
        } else {
          CodeBaseLongId cb = mapper.readValue(string, CodeBaseLongId.class);
          if (cb.getCode().toLowerCase().equals(code.toLowerCase())) {
            return cb.getId();
          }
        }
      } catch (JsonMappingException e) {
        e.printStackTrace();
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }
    return -1;
  }

  public void saveICD10DB(String code, String category, String descTw, String descEn, long redisId) {
    List<ICD10> icdList = icd10Dao.findByCode(code.toUpperCase());
    if (icdList != null && icdList.size() > 0) {
      return;
    }
    ICD10 icd = new ICD10();
    icd.setCat(category.split("-")[1]);
    icd.setCode(code.toUpperCase());
    icd.setDescChi(descTw);
    icd.setDescEn(descEn);
    if (descEn != null && descEn.length() > 200) {
        icd.setDescEn(descEn.substring(0, 199));
    }
    icd.setRedisId(redisId);
    icd.setInfectious(0);
    icd10Dao.save(icd);
    //System.out.println("save db:" + icd.getCode());
  }
  
  private void addCodeByThread(WriteToRedisThreadPool pool, String key, CodeBaseLongId cb,
      boolean isSearch) {
    WriteToRedisThread thread = new WriteToRedisThread(redisTemplate, pool);
    thread.setKey(key);
    thread.setCb(cb);
    if (isSearch) {
      thread.setSearchKey(cb.getCode());
    }
    thread.run();
    //pool.addThread(thread);
  }
  
  public void importATC(File file) {
    String cat = "ATC";
    // 存放處理過的 ATC code，避免因來源檔案資料重複，而重複insert
    HashMap<String, String> duplicate = new HashMap<String, String>();
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    long maxId = getMaxId() + 1;
    long initialId = maxId;
    try {
      ZSetOperations<String, Object> op = redisTemplate.opsForZSet();
      removeRedisHashByCat(op, RedisService.DATA_KEY, cat);

      XSSFWorkbook workbook = new XSSFWorkbook(file);
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        XSSFSheet sheet = workbook.getSheetAt(i);
        // System.out.println("sheet " + i + " :" +
        // sheet.getRow(1).getCell(0).getStringCellValue());
        if (sheet.getRow(1) == null || sheet.getRow(1).getCell(7) == null) {
          continue;
        }
        for (int j = 1; j < sheet.getPhysicalNumberOfRows() - 1; j++) {
          XSSFRow row = sheet.getRow(j);
          if (row == null || row.getCell(7) == null) {
            // System.out.println("sheet:" + i + ", row=" + j + " is null");
            continue;
          }

          CodeBaseLongId cb = getCode(row, 0, maxId, duplicate);
          if (cb != null) {
            maxId++;
            cb.setCategory("ATC");
            addATCCode(objectMapper, op, cb);
          }

          cb = getCode(row, 2, maxId, duplicate);
          if (cb != null) {
            maxId++;
            cb.setCategory("ATC");
            addATCCode(objectMapper, op, cb);
          }

          cb = getCode(row, 4, maxId, duplicate);
          if (cb != null) {
            maxId++;
            cb.setCategory("ATC");
            addATCCode(objectMapper, op, cb);
          }

          cb = getCode(row, 6, maxId, duplicate);
          if (cb != null) {
            maxId++;
            cb.setCategory("ATC");
            addATCCode(objectMapper, op, cb);
          }
        }
      }
      workbook.close();
    } catch (InvalidFormatException e) {
      logger.error("import excel failed", e);
      e.printStackTrace();
    } catch (IOException e) {
      logger.error("import excel failed", e);
      e.printStackTrace();
    }
    int addCount = (int) maxId - (int) initialId;
    System.out.println("initial id:" + initialId + ", add=" + addCount);
  }

  private void removeRedisHashByCat(ZSetOperations<String, Object> op, String key, String cat) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);
    Set<Object> set = redisTemplate.opsForHash().keys(key);
    System.out.println(key + " size:" + set.size());
    for (Object object : set) {
      try {
        String value = (String) redisTemplate.opsForHash().get(key, object);
        if (value.indexOf("detailCat") > 0 || value.indexOf("sDate") > 0
            || value.indexOf("level") > 0 || value.indexOf("law") > 0) {
          OrderCode oc = mapper.readValue(value, OrderCode.class);
          if (cat.equals(oc.getCategory())) {
            redisTemplate.opsForHash().delete(key, oc.getId().toString());
            removeIndexToRedisIndex(op, RedisService.INDEX_KEY, oc.getCode(), oc.getId().intValue());
          }
        } else {
          CodeBaseLongId cb = mapper.readValue(value, CodeBaseLongId.class);
          if (cat.equals(cb.getCategory())) {
            redisTemplate.opsForHash().delete(key, cb.getId().toString());
            removeIndexToRedisIndex(op, RedisService.INDEX_KEY, cb.getCode(), cb.getId().intValue());
          }
        }
      } catch (JsonMappingException e) {
        e.printStackTrace();
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }

    }
  }
  
  private void removeIndexToRedisIndex(ZSetOperations<String, Object> op, String prefix,
      String name, int removeId) {
    for (int i = 2; i <= name.length(); i++) {
      String key = name.substring(0, i);
      Set<Object> set = op.range(prefix + key, 0, -1);
      for (Object object : set) {
        if (Integer.parseInt((String) object) == removeId) {
          op.remove(prefix + ":" + key, object);
        }
      }
    }
  }
  
  private void addATCCode(ObjectMapper objectMapper, ZSetOperations<String, Object> op,
      CodeBaseLongId code) {
    System.out.println("add:" + code.getId() + "," + code.getCode() + "," + code.getDesc() + ","
        + code.getDescEn() + ".");
    try {
      String json = objectMapper.writeValueAsString(code);
      // 1. save to data
      redisTemplate.opsForHash().put("ICD10" + "-data", String.valueOf(code.getId()), json);
      // 2. save code to index for search
      redisService.addIndexToRedisIndex(RedisService.INDEX_KEY, String.valueOf(code.getId()),
          code.getCode().toLowerCase());
      saveATCDB(code);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  private void saveATCDB(CodeBaseLongId code) {
    ATC atc = new ATC();
    atc.setCode(code.getCode());
    if (code.getDesc() != null) {
      atc.setNote(code.getDesc() + "（" + code.getDescEn() + "）");
    } else {
      atc.setNote(code.getDescEn());
    }
    atc.setRedisId(code.getId().intValue());
    atc.setLeng(code.getCode().length());
    atcDao.save(atc);
  }

  private CodeBaseLongId getCode(XSSFRow row, int startIndex, long id,
      HashMap<String, String> duplicate) {
    if (row.getCell(startIndex) == null) {
      return null;
    }
    String code = row.getCell(startIndex).getStringCellValue().trim();
    if (code.length() == 0) {
      return null;
    }
    if (duplicate.get(code) != null) {
      // 有重複的 code
      return null;
    }
    duplicate.put(code, "");
    String desc = row.getCell(startIndex + 1).getStringCellValue().trim();
    if (desc.indexOf('\n') > 0) {
      System.out.print("before:" + desc);
      desc = desc.replaceAll("\r\n", "");
      System.out.println(", after:" + desc);
    }

    String descTw = null;
    String descEn = null;
    if (desc.indexOf("（") > 0) {
      descTw = desc.substring(0, desc.indexOf("（"));
      descEn = desc.substring(desc.indexOf("（") + 1, desc.length() - 1);
    } else {
      descEn = desc;
    }

    return new CodeBaseLongId(id, code, descTw, descEn);
  }

  public void importInfectious(File file) {
    try {
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      DecimalFormat df = new DecimalFormat("#.######");

      int total = 0;
      XSSFSheet sheet = workbook.getSheetAt(0);
      for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
        String category = null;
        XSSFRow row = sheet.getRow(j);
        if (row == null || row.getCell(0) == null) {
          // System.out.println("sheet:" + i + ", row=" + j + " is null");
          continue;
        }
        if (row.getCell(0).getCellType() == CellType.NUMERIC) {
          category = df.format(row.getCell(0).getNumericCellValue());
        } else {
          category = row.getCell(0).getStringCellValue().trim();
        }
        
        String[] codes = row.getCell(2).getStringCellValue().trim().split(",");
        for (String string : codes) {
          CODE_TABLE ct = new CODE_TABLE();
          ct.setParentCode(category);
          ct.setCode(string.trim());
          ct.setCat("INFECTIOUS");
          CodeBaseLongId oc = search(ct.getCode().toLowerCase(), "ICD10-CM");
          if (oc != null) {
            ct.setDescChi(oc.getDesc());
            ct.setDescEn(oc.getDescEn());
            if (ct.getDescEn().length() > 100) {
                ct.setDescEn(ct.getDescEn().substring(0, 99));
            }
          } else {
            System.out.println(ct.getCode() + ", redis not found");
          }
          if (ct.getDescChi() == null) {
            ct.setDescChi(row.getCell(1).getStringCellValue().trim());
          }
          List<CODE_TABLE> ctList = ctDao.findByCodeAndCat(ct.getCode(), "INFECTIOUS");
          if (ctList != null && ctList.size() > 0) {
            ct.setId(ctList.get(0).getId());
          }
          ctDao.save(ct);
          updateICD10(ct);
        }
        total++;
      }
      System.out.println("finish total:" + total);
      workbook.close();
    } catch (InvalidFormatException e) {
      logger.error("import excel failed", e);
      e.printStackTrace();
    } catch (IOException e) {
      logger.error("import excel failed", e);
      e.printStackTrace();
    }
  }
  
  public CodeBaseLongId search(String searchKey, String category) {
    String key = "ICD10-data";
    String indexKey = "ICD10-index:";

    ObjectMapper mapper = new ObjectMapper();
    ZSetOperations<String, Object> zsetOp = (ZSetOperations<String, Object>) redisTemplate.opsForZSet();
    HashOperations<String, String, String> hashOp = redisTemplate.opsForHash();
    Set<Object> rangeSet = zsetOp.range(indexKey + searchKey, 0, -1);

    boolean isFound = false;
    for (Object object : rangeSet) {
      // 找到 ICD10-data 的 index
      String s = hashOp.get(key, object);
      if (s == null) {
        continue;
      }
      //System.out.println(s);

      try {
        if (s.indexOf("\"p\"") > 0) {
          OrderCode oc =  mapper.readValue(s, OrderCode.class);
          if (oc.getCode().toLowerCase().equals(searchKey) && oc.getCategory().equals(category)) {
            return oc;
          }
        } else {
          CodeBaseLongId cbRedis = mapper.readValue(s, CodeBaseLongId.class);
          if (cbRedis.getCode().toLowerCase().equals(searchKey) && cbRedis.getCategory().equals(category)) {
            return cbRedis;
          }
        }
      } catch (JsonMappingException e) {
        e.printStackTrace();
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }
    return null;
  }
  
  public void updateICD10(CODE_TABLE ct) {
    List<ICD10> icdList = icd10Dao.findByCode(ct.getCode());
    if (icdList == null || icdList.size() == 0) {
      return;
    }
    ICD10 icd = icdList.get(0);
    icd.setInfectious(1);
    icd.setInfCat(ct.getParentCode());
    icd10Dao.save(icd);
  }
  
  /**
   * 匯入醫院提供的部門檔案 DEPARTMENT.xls
   * @param file
   * @param sheetName
   * @param titleRow
   */
  public void importDepartmentFile(File file, int titleRow) {
    try {
      XSSFWorkbook workbook = new XSSFWorkbook(file);

      XSSFSheet sheet = workbook.getSheetAt(0);
      HashMap<Integer, String> columnMap = ExcelUtil.readTitleRow(sheet.getRow(titleRow),
          parametersService.getByCat("DEPARTMENT"));
      HashMap<String, String> values = null;
      for (int i = titleRow + 1; i < sheet.getPhysicalNumberOfRows(); i++) {
        XSSFRow row = sheet.getRow(i);
        if (row == null || row.getCell(0) == null) {
          // System.out.println("sheet:" + i + ", row=" + j + " is null");
          continue;
        }
        values = ExcelUtil.readCellValue(columnMap, row);
        String code = values.get("CODE");
        if (code == null || code.length() == 0) {
          break;
        }
        DEPARTMENT department = new DEPARTMENT();
        department.setCode(code.trim());
        department.setName(values.get("NAME").trim());
        List<DEPARTMENT> list = departmentDao.findByName(department.getName());
        if (list == null || list .size() == 0) {
          departmentDao.save(department);
        } else {
          for (DEPARTMENT d : list) {
            if (!department.getCode().equals(d.getCode())) {
              d.setCode(department.getCode());
              departmentDao.save(d);
            }
          }
        }
      }
      workbook.close();
    } catch (InvalidFormatException e) {
      logger.error("importDepartmentFile failed", e);
      e.printStackTrace();
    } catch (IOException e) {
      logger.error("importDepartmentFile failed", e);
      e.printStackTrace();
    }
  }
  
  /**
   * 匯入醫院提供的部門檔案 DEPARTMENT.xls
   * @param file
   * @param sheetName
   * @param titleRow
   */
  public void importUserFile(File file, int titleRow) {
    try {
      List<DEPARTMENT> departments = departmentDao.findAll();
      List<USER> users = userDao.findAll();
      XSSFWorkbook workbook = new XSSFWorkbook(file);

      XSSFSheet sheet = workbook.getSheetAt(0);
      HashMap<Integer, String> columnMap = ExcelUtil.readTitleRow(sheet.getRow(titleRow),
          parametersService.getByCat("USER"));
      HashMap<String, String> values = null;
      for (int i = titleRow + 1; i < sheet.getPhysicalNumberOfRows(); i++) {
        XSSFRow row = sheet.getRow(i);
        if (row == null || row.getCell(0) == null) {
          // System.out.println("sheet:" + i + ", row=" + j + " is null");
          continue;
        }
        values = ExcelUtil.readCellValue(columnMap, row);
        String username = values.get("USERNAME");
        if (username == null || username.length() == 0) {
          break;
        }
        DEPARTMENT department =  findDepartment(departments, values);
        USER dbUser = findUser(users, values);
        if (dbUser == null) {
          dbUser = new USER();
        }
        if (values.get("INH_ID") != null && dbUser.getInhId() == null) {
          dbUser.setInhId(values.get("INH_ID"));
        }
        if (dbUser.getInhId() != null) {
          dbUser.setUsername(dbUser.getInhId());
        }
        if (values.get("DISPLAY_NAME") != null) {
          dbUser.setDisplayName(values.get("DISPLAY_NAME"));
        }
        if (values.get("ROC_ID") != null) {
          dbUser.setRocId(values.get("ROC_ID"));
        }
        if (values.get("ROLE") != null) {
          dbUser.setRole(values.get("ROLE"));
        }
        if (dbUser.getRole() == null) {
          // 醫護人員
          dbUser.setRole("E");
        }
        if (values.get("EMAIL") != null) {
          dbUser.setEmail(values.get("EMAIL"));
        }
        dbUser = userDao.save(dbUser);
        if (department != null) {
          List<USER_DEPARTMENT> udList = userDepartmentDao.findByUserIdOrderByDepartmentId(dbUser.getId());
          if (udList == null || udList.size() == 0) {
            USER_DEPARTMENT ud = new USER_DEPARTMENT();
            ud.setDepartmentId(department.getId());
            ud.setUserId(department.getId());
            userDepartmentDao.save(ud);
          } else {
            boolean isFound = false;
            for (USER_DEPARTMENT ud : udList) {
              if (ud.getDepartmentId().longValue() == department.getId().longValue()) {
                isFound = true;
                break;
              }
            }
            if (!isFound) {
              USER_DEPARTMENT ud = new USER_DEPARTMENT();
              ud.setDepartmentId(department.getId());
              ud.setUserId(department.getId());
              userDepartmentDao.save(ud);
            }
          }
        }
      }
      workbook.close();
    } catch (InvalidFormatException e) {
      logger.error("importDepartmentFile failed", e);
      e.printStackTrace();
    } catch (IOException e) {
      logger.error("importDepartmentFile failed", e);
      e.printStackTrace();
    }
  }
  
  private DEPARTMENT findDepartment(List<DEPARTMENT> departments, HashMap<String, String> values) {
    if (values.get("DEPARTMENT") == null || values.get("DEPARTMENT").length() == 0) {
      return null;
    }
    DEPARTMENT result = null;
    for (DEPARTMENT department : departments) {
      if (values.get("CODE") != null && values.get("CODE").length() > 0) {
        if (values.get("CODE").equals(department.getCode())) {
          result = department;
          break;
        }
      }
      if (values.get("DEPARTMENT").equals(department.getName())) {
        result = department;
        break;
      }
    }
    if (result == null) {
      if (values.get("CODE") == null) {
        // 要有部門代碼及部門名稱才新增
        return null;
      }
      DEPARTMENT department = new DEPARTMENT();
      department.setCode(values.get("CODE"));
      department.setName(values.get("DEPARTMENT"));
      result = departmentDao.save(department);
      departments.add(result);
    }
    return result;
  }
  
  private USER findUser(List<USER> users, HashMap<String, String> values) {
    if (values.get("DISPLAY_NAME") == null || values.get("DISPLAY_NAME").length() == 0) {
      return null;
    }
    USER result = null;
    for (USER user : users) {
      if (values.get("INH_ID") != null && values.get("INH_ID").length() > 0) {
        if (values.get("INH_ID").equals(user.getInhId())) {
          result = user;
          break;
        }
      }
      if (values.get("DISPLAY_NAME").equals(user.getDisplayName())) {
        result = user;
        break;
      }
    }
    return result;
  }
}
