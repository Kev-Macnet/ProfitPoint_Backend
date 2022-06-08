/**
 * Created on 2021/2/2.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.commons.text.StringEscapeUtils;
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
import tw.com.leadtek.nhiwidget.TestParameterService;
import tw.com.leadtek.nhiwidget.dao.PAY_CODEDao;
import tw.com.leadtek.nhiwidget.local.InitialEnvironment;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
import tw.com.leadtek.nhiwidget.model.redis.CodeBaseLongId;
import tw.com.leadtek.nhiwidget.model.redis.OrderCode;
import tw.com.leadtek.nhiwidget.service.RedisService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class ImportPayCode {

  private Logger logger = LogManager.getLogger();

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private RedisService redisService;

  @Autowired
  private PAY_CODEDao payCodeDao;

  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d");

  private final static String[] HOSP_LEVEL = new String[] {"基層院所", "醫學中心", "區域醫院", "地區醫院"};

  /**
   * 存放在 HashSet 的 id
   */
  private long maxId = 0;

  /**
   * 匯入醫令代碼(支付代碼)
   */
  @Ignore
  @Test
  public void importPayCode() {
    System.out.println("importPayCode");


    maxId = 163661;
    importExcelToRedis("ICD10",
        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\醫令\\2-1-2至2-1-3.xlsx", "ORDER");
    importExcelToRedis("ICD10",
        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\醫令\\2-1-4至2-1-7.xlsx", "ORDER");
    importExcelToRedis("ICD10",
        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\醫令\\2-2-3 注射Injection (39001～39025).xlsx",
        "ORDER");
    importExcelToRedis("ICD10",
        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\醫令\\2-2-5 精神醫療治療費 Psychiatric Treatment Fee (45001～45102).xlsx",
        "ORDER");
  }

  @Ignore
  @Test
  public void verifyImportDataCorrect() {
    compareExcelToRedis("ICD10",
        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\醫令\\2-1-2至2-1-3.xlsx");
    compareExcelToRedis("ICD10",
        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\醫令\\2-1-4至2-1-7.xlsx");
    compareExcelToRedis("ICD10",
        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\醫令\\2-2-3 注射Injection (39001～39025).xlsx");
    compareExcelToRedis("ICD10",
        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\醫令\\2-2-5 精神醫療治療費 Psychiatric Treatment Fee (45001～45102).xlsx");
  }

  public void importExcelToRedis(String collectionName, String filename, String category) {
    // HashOperations<String, String, Object> hashOp = ;
    // long maxId = redisTemplate.opsForHash().size(collectionName + "-data");
    // 前面 163661 筆是 ICD10 診斷碼 + 處置碼

    File file = new File(filename);
    int addCount = 0;
    WriteToRedisThreadPool wtrPool = new WriteToRedisThreadPool();
    Thread poolThread = new Thread(wtrPool);
    try {
      ZSetOperations<String, Object> op = redisTemplate.opsForZSet();
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setSerializationInclusion(Include.NON_NULL);
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      HashOperations<String, String, String> hashOp = redisTemplate.opsForHash();
      // DataFormatter formatter = new DataFormatter();

      poolThread.start();
      int total = 0;
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        XSSFSheet sheet = workbook.getSheetAt(i);
        if (sheet.getRow(0).getCell(0).getStringCellValue().indexOf("代碼") < 0) {
          System.out.println("無代碼");
          continue;
        }
        int count = 0;
        for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
          XSSFRow row = sheet.getRow(j);
          if (row == null || row.getCell(0) == null) {
            // System.out.println("sheet:" + i + ", row=" + j + " is null");
            continue;
          }
          String code = row.getCell(0).getStringCellValue().trim().toLowerCase();
          System.out.println("save " + code);
          if (code.length() == 0) {
            break;
          }
          OrderCode oc = getOrderCodyByExcelRow(row);

          // addCount += addCode3(op, objectMapper, collectionName, cb);
          addCodeByThread(wtrPool, collectionName, oc, false);
          System.out.println("add(" + maxId + ")[" + addCount + "]" + code + ":" + oc.getDescEn());
          count++;
          total++;
          if (wtrPool.getThreadCount() > 1000) {
            do {
              try {
                Thread.sleep(2000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            } while (wtrPool.getThreadCount() > 1000);
          }
        }
      }
      wtrPool.setFinished(true);
      System.out.println("finish total:" + total);
      workbook.close();

      do {
        try {
          Thread.sleep(200);
          // System.out.println("elapsed:" + wtrPool.getThreadCount());
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } while (wtrPool.isRunning());
    } catch (InvalidFormatException e) {
      logger.error("import excel failed", e);
      e.printStackTrace();
    } catch (IOException e) {
      logger.error("import excel failed", e);
      e.printStackTrace();
    }
  }

  private OrderCode getOrderCodyByExcelRow(XSSFRow row) {
    String code = row.getCell(0).getStringCellValue().trim().toLowerCase();

    String descEn = row.getCell(5).getStringCellValue().trim();
    String descTw = null;
    if (row.getCell(4) != null) {
      descTw = row.getCell(4).getStringCellValue();
      if (descTw != null) {
        descTw = descTw.trim();
      }
    }

    // addCode1(collectionName, code);
    OrderCode oc = new OrderCode(++maxId, code, descTw, descEn);

    oc.setP((int) row.getCell(1).getNumericCellValue());
    oc.setsDate(row.getCell(2).getDateCellValue());
    oc.seteDate(row.getCell(3).getDateCellValue());
    if (row.getCell(6) != null) {
      oc.setDetail(row.getCell(6).getStringCellValue());
    }
    if (row.getCell(7) != null) {
      oc.setDetailCat(row.getCell(7).getStringCellValue());
    }
    if (row.getCell(8) != null) {
      oc.setLevel(row.getCell(8).getStringCellValue());
    }
    if (row.getCell(9) != null) {
      oc.setOutIsland(row.getCell(9).getStringCellValue());
    }
    if (row.getCell(10) != null) {
      oc.setLaw(row.getCell(10).getStringCellValue());
    }
    if (row.getCell(11) != null) {
      oc.setCon(row.getCell(11).getStringCellValue());
    }
    return oc;
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
    OrderCode oc = new OrderCode(++maxId, code, descTw, descEn);

    oc.setP((int) row.getCell(1).getNumericCellValue());
    oc.setsDate(getDateFromCell(row.getCell(2), sdf, df));
    if (row.getCell(22) != null) {
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

  private void addCodeByThread(WriteToRedisThreadPool pool, String key, CodeBaseLongId cb,
      boolean isSearch) {
    WriteToRedisThread thread = new WriteToRedisThread(redisTemplate, pool);
    thread.setKey(key);
    thread.setCb(cb);
    if (isSearch) {
      thread.setSearchKey(cb.getCode());
    }
    pool.addThread(thread);
  }

  public void compareExcelToRedis(String collectionName, String filename) {
    // HashOperations<String, String, Object> hashOp = ;
    long maxId = redisTemplate.opsForHash().size(collectionName + "-data");
    File file = new File(filename);
    int addCount = 0;
    WriteToRedisThreadPool wtrPool = new WriteToRedisThreadPool();
    Thread poolThread = new Thread(wtrPool);
    try {
      ZSetOperations<String, Object> op = redisTemplate.opsForZSet();
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setSerializationInclusion(Include.NON_NULL);
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      // DataFormatter formatter = new DataFormatter();

      poolThread.start();
      int total = 0;
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        XSSFSheet sheet = workbook.getSheetAt(i);
        if (sheet.getRow(0).getCell(0).getStringCellValue().indexOf("代碼") < 0) {
          System.out.println("無代碼");
          continue;
        }
        int count = 0;
        for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
          XSSFRow row = sheet.getRow(j);
          if (row == null || row.getCell(0) == null) {
            // System.out.println("sheet:" + i + ", row=" + j + " is null");
            continue;
          }
          String code = row.getCell(0).getStringCellValue().trim();
          System.out.println("code=" + code);
          // System.out.println("save " + code);
          if (code.length() == 0) {
            break;
          }
          OrderCode oc = getOrderCodyByExcelRow(row);
          addCodeByThread(wtrPool, collectionName, oc, true);
          // System.out.println("add(" + maxId + ")[" + addCount + "]" + code + ":" + descEn);
          count++;
          total++;
          if (wtrPool.getThreadCount() > 1000) {
            do {
              try {
                Thread.sleep(2000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            } while (wtrPool.getThreadCount() > 1000);
          }
        }
        System.out.println("sheet " + sheet.getSheetName() + ":" + total);
      }
      System.out.println(filename + "finish total:" + total);
      workbook.close();

      do {
        try {
          Thread.sleep(200);
          // System.out.println("elapsed:" + wtrPool.getThreadCount());
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } while (wtrPool.isRunning());
    } catch (InvalidFormatException e) {
      logger.error("import excel failed", e);
      e.printStackTrace();
    } catch (IOException e) {
      logger.error("import excel failed", e);
      e.printStackTrace();
    }
  }

  /**
   * 匯入醫令代碼(支付代碼)
   */
  @Ignore
  @Test
  public void importPayCodeNew() {
    System.out.println("importPayCode");

    maxId = getMaxId() + 1;
    System.out.println("maxid=" + maxId);
     importExcelToRedisNew("ICD10",
    		 InitialEnvironment.FILE_PATH +  "醫療服務給付項目(1100701執行).xlsx",
     "ORDER");
     importDrugHtmlExcelToRedis("ICD10",
    		 InitialEnvironment.FILE_PATH +  "藥品\\20211020183522-用藥品項查詢結果.xls",
     "ORDER");
    importDrugHtmlExcelToRedis("ICD10",
    		InitialEnvironment.FILE_PATH +  "藥品\\20211020184556-用藥品項查詢結果.xls",
        "ORDER");
    importDrugHtmlExcelToRedis("ICD10",
    		InitialEnvironment.FILE_PATH +  "藥品\\20211020184620-用藥品項查詢結果.xls",
        "ORDER");
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
    HashMap<String, String> keys = getRedisId(collectionName + "-data", category);
    System.out.println("keys size=" + keys.size());
    for (String string : keys.keySet()) {
      System.out.println(string +":" + keys.get(string));
    }
    if (keys.size() > 0) {
      return;
    }
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

  private void savePayCodeToRedis(PAY_CODE code, HashMap<String, String> keys) {
    OrderCode oc = code.toOrderCode();
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
    System.out.println("saveOrderCode:" + oc.getCode() + "," + sId);
    if (sId == null) {
      oc.setId(maxId);
      code.setRedisId((int) maxId);
      maxId++;
      redisService.putHash(RedisService.DATA_KEY, String.valueOf(oc.getId()), json);
      redisService.addIndexToRedisIndex(RedisService.INDEX_KEY, String.valueOf(oc.getId()),
          oc.getCode());
    } else {
      System.out.println("save " + oc.getCode());
      oc.setId(Long.parseLong(sId));
      code.setRedisId(Integer.parseInt(sId));
      redisService.putHash(RedisService.DATA_KEY, sId, json);
      redisService.addIndexToRedisIndex(RedisService.INDEX_KEY, sId, oc.getCode());
    }
    savePayCode(code);
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

  private boolean saveExistToFile(String collectionName, String catogory) {
    HashMap<String, String> keys = getRedisId(collectionName + "-data", "ORDER");
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter("ORDER.txt"));
      for (String string : keys.keySet()) {
        bw.write(string);
        bw.write(",");
        bw.write(keys.get(string));
        bw.newLine();
      }
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return keys.keySet().size() > 0;
  }

  /**
   * 匯入健保局用藥品項查詢結果.xls
   * 
   * @param collectionName
   * @param filename
   * @param category
   */
  public void importDrugHtmlExcelToRedis(String collectionName, String filename, String category) {

    ZSetOperations<String, Object> op = redisTemplate.opsForZSet();
    HashMap<String, String> keys = getRedisId(collectionName + "-data", category);

    File file = new File(filename);
    try {
      BufferedReader br = new BufferedReader(
          new InputStreamReader(new FileInputStream(new File(filename)), "UTF-8"));
      String codePrefix = " title=\"藥政處網站(開新窗)\">";
      String nameEnPrefix = "lblName\">";
      String namePrefix = "lblNameChinese\">";
      String pricePrefix = "lblPrice\">";
      String startEndDate = "lblstartEndDate\">";
      String classGroupName = "lblClassGroupName\">";
      String atc = "</td><td>";
      String line = null;
      int index = 0;
      PAY_CODE pc = null;
      boolean isATCString = false;
      while ((line = br.readLine()) != null) {
        if (isATCString) {
          isATCString = false;
          index = line.indexOf(atc);
          if (index > -1) {
            String s = line.substring(index + atc.length());
            pc.setAtc(s.substring(0, s.indexOf("</td>")));
          }
          continue;
        }
        if (line.indexOf(codePrefix) > 0) {
          if (pc != null) {
            System.out.println(
                pc.getCode() + "," + pc.getName() + "," + pc.getNameEn() + "," + pc.getOwnExpense()
                    + "," + pc.getStartDate() + pc.getEndDate() + "," + pc.getAtc());
            savePayCodeToRedis(pc, keys);
          }
          String s = line.substring(line.indexOf(codePrefix) + codePrefix.length());
          index = s.indexOf("</a>");
          String code = s.substring(0, index);
          pc = new PAY_CODE();
          pc.setCode(code);
          pc.setInhCode(code);
          pc.setCodeType("藥品費");
          pc.setUpdateAt(new Date());
        } else if (line.indexOf(nameEnPrefix) > 0) {
          String s = line.substring(line.indexOf(nameEnPrefix) + nameEnPrefix.length());
          index = s.indexOf("</span");
          pc.setNameEn(StringEscapeUtils.unescapeHtml4(s.substring(0, index)));
        } else if (line.indexOf(namePrefix) > 0) {
          String s = line.substring(line.indexOf(namePrefix) + namePrefix.length());
          index = s.indexOf("</span>");
          pc.setName(StringEscapeUtils.unescapeHtml4((s.substring(0, index))));
          pc.setInhName(pc.getName());
        } else if (line.indexOf(pricePrefix) > 0) {
          String s = line.substring(line.indexOf(pricePrefix) + pricePrefix.length());
          index = s.indexOf("</span>");
          String price = StringEscapeUtils.unescapeHtml4((s.substring(0, index)));
          if ("-".equals(price)) {
            pc.setOwnExpense(0.0);
          } else {
            pc.setOwnExpense(Double.parseDouble(price));
          }
        } else if (line.indexOf(startEndDate) > 0) {
          String s = line.substring(line.indexOf(startEndDate) + startEndDate.length());
          index = s.indexOf("<br />");
          String startDate = StringEscapeUtils.unescapeHtml4((s.substring(0, index)));
          pc.setStartDate(getDateFromChineseDotString(startDate));
          s = s.substring(index + 6);
          index = s.indexOf("<br />");
          s = s.substring(index + 6);
          index = s.indexOf("</span>");
          String endDate = StringEscapeUtils.unescapeHtml4((s.substring(0, index)));
          pc.setEndDate(getDateFromChineseDotString(endDate));
        } else if (line.indexOf(classGroupName) > 0) {
          isATCString = true;
        }
      }
      if (pc != null) {
        savePayCodeToRedis(pc, keys);
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // try {
    // ObjectMapper objectMapper = new ObjectMapper();
    // objectMapper.setSerializationInclusion(Include.NON_NULL);
    // XSSFWorkbook workbook = new XSSFWorkbook(file);
    // HashOperations<String, String, String> hashOp = redisTemplate.opsForHash();
    //
    // int total = 0;
    // XSSFSheet sheet = workbook.getSheetAt(0);
    // SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    // DecimalFormat df = new DecimalFormat("#");
    // for (int j = 2; j < sheet.getPhysicalNumberOfRows(); j++) {
    // XSSFRow row = sheet.getRow(j);
    // if (row == null || row.getCell(1) == null) {
    // // System.out.println("sheet:" + i + ", row=" + j + " is null");
    // continue;
    // }
    // // 存到redis要用小寫
    // String code = row.getCell(1).getStringCellValue().trim().toLowerCase();
    // System.out.println("code=" + code);
    // if (code.length() == 0) {
    // break;
    // }
    //// OrderCode oc = getDrugOrderCodyByExcelRow(row, sdf, df);
    //// saveOrderCode(oc, keys);
    // total++;
    // }
    // System.out.println("finish total:" + total);
    // workbook.close();
    // } catch (InvalidFormatException e) {
    // logger.error("import excel failed", e);
    // e.printStackTrace();
    // } catch (IOException e) {
    // logger.error("import excel failed", e);
    // e.printStackTrace();
    // }
  }

  private Date getDateFromChineseDotString(String s) {
    Calendar cal = Calendar.getInstance();
    if (s.indexOf('.') > 0) {
      String[] ss = s.split("\\.");
      if (ss.length == 3) {
        cal.set(1911 + Integer.parseInt(ss[0]), Integer.parseInt(ss[1]) - 1,
            Integer.parseInt(ss[2]));
        return cal.getTime();
      }
    }
    cal.set(2099, 11, 31);
    return cal.getTime();
  }

  /**
   * 匯入健保局用藥品項查詢結果.xls
   * 
   * @param collectionName
   * @param filename
   * @param category
   */
  public void importDrugExcelToRedis(String collectionName, String filename, String category) {

    ZSetOperations<String, Object> op = redisTemplate.opsForZSet();
    HashMap<String, String> keys = getRedisId(collectionName + "-data", category);

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
      for (int j = 2; j < sheet.getPhysicalNumberOfRows(); j++) {
        XSSFRow row = sheet.getRow(j);
        if (row == null || row.getCell(1) == null) {
          // System.out.println("sheet:" + i + ", row=" + j + " is null");
          continue;
        }
        // 存到redis要用小寫
        String code = row.getCell(1).getStringCellValue().trim().toLowerCase();
        System.out.println("code=" + code);
        if (code.length() == 0) {
          break;
        }
        // OrderCode oc = getDrugOrderCodyByExcelRow(row, sdf, df);
        // saveOrderCode(oc, keys);
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

  @Ignore
  @Test
  public void testExcel() {
    File file = new File("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\高雄霖園醫院\\202205\\202205- op-0519.xlsx");    
    try {
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      int total = 0;
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        XSSFSheet sheet = workbook.getSheetAt(i);
        System.out.println(sheet.getRow(0).getCell(0));
      }
    } catch (InvalidFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
}
