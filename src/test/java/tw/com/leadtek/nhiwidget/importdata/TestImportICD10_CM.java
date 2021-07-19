/**
 * Created on 2020/12/16.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.DefaultTypedTuple;
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
import tw.com.leadtek.nhiwidget.constant.RedisConstants;
import tw.com.leadtek.nhiwidget.model.redis.CodeBaseLongId;
import tw.com.leadtek.nhiwidget.model.redis.OrderCode;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class TestImportICD10_CM {

  private Logger logger = LogManager.getLogger();

  //@Autowired
  //private MongoTemplate mongo;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  private static List<String> ignoreWords = Arrays.asList("other", "the", "to", "of", "and");

  public TestImportICD10_CM() {

  }

  /**
   * 將ICD10診斷碼、處置碼匯入至 mongo DB
   
  @Ignore
  @Test
  public void importICD10() {
    importExcel("icd10_cm",
        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\ICD10\\1.1 中文版ICD-10-CM(106.07.19更新)_Chapter.xlsx");
    importExcel("icd10_pcs",
        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\ICD10\\1.2 中文版ICD-10-PCS(106.07.19更新).xlsx");
  }*/

  /**
   * 將excel檔案資料匯至 mongo DB
   * 
   * @param collectionName
   * @param filename

  public void importExcel(String collectionName, String filename) {
    checkAndCreate(collectionName);
    File file = new File(filename);
    try {
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      // DataFormatter formatter = new DataFormatter();

      int total = 0;
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        XSSFSheet sheet = workbook.getSheetAt(i);
        // System.out.println("sheet " + i + " :" +
        // sheet.getRow(1).getCell(0).getStringCellValue());
        if (!sheet.getRow(1).getCell(0).getStringCellValue().equals("代碼")) {
          continue;
        }
        BulkOperations ops = mongo.bulkOps(BulkMode.ORDERED, collectionName);
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
          // Update update = initialUpdate(code, descEn, descTw);
          // System.out.println("update=" + update);

          // mongo.getCollection(collectionName).;
          // mongo.insert(update, collectionName);
          ops.upsert(Query.query(Criteria.where("code").is(code)),
              initialUpdate(code, descEn, descTw));
          count++;
          total++;
          if (count > 4999) {
            BulkWriteResult bwr = ops.execute();
            System.out.println("total:" + total + ", insert:" + bwr.getInsertedCount() + ", update:"
                + bwr.getModifiedCount() + ", match:" + bwr.getMatchedCount() + ", upsert:"
                + bwr.getUpserts().size());

            count = 0;
            // execute 之後 ops 就不能再使用
            ops = mongo.bulkOps(BulkMode.ORDERED, collectionName);
          }
          // UpdateResult result = mongo.upsert(Query.query(Criteria.where("code").is(code)),
          // initialUpdate(code, descEn, descTw), collectionName);
          // System.out.println("count=" + result.getModifiedCount());
        }
        if (count > 0) {
          ops.execute();
        }
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

  private Update initialUpdate(String code, String descEn, String descTw) {
    Update update = new Update();
    update.set("code", code);
    update.set("descEn", descEn);
    update.set("desc", descTw);
    update.set("updateAt", new Date());

    return update;
  }

  private void checkAndCreate(String collectionName) {
    MongoCollection<Document> collection = mongo.getCollection(collectionName);
    if (collection == null) {
      mongo.createCollection(collectionName);
    }
  }
     */

  /**
   * 將excel檔案資料匯至 Redis
   */
  @Ignore
  @Test
  public void importICD10ToRedis() {
    System.out.println("importICD10ToRedis");
    long start = System.currentTimeMillis();
    // importExcelToRedis("ICD10",
    // "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\ICD10\\1.1
    // 中文版ICD-10-CM(106.07.19更新)_Chapter.xlsx",
    // "ICD10-CM");
    importExcelToRedis("ICD10",
        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\ICD10\\1.2 中文版ICD-10-PCS(106.07.19更新).xlsx",
        "ICD10-PCS");

    long usedTime = System.currentTimeMillis() - start;
    System.out.println("usedTime:" + usedTime);
    System.out.println(String.format("use time: %.2f", (float) usedTime / (float) 1000));
  }

  @Ignore
  @Test
  public void testImportICD10ToRedis() {
    System.out.println("testImportICD10ToRedis");
    long start = System.currentTimeMillis();
    // compareExcelToRedis("ICD10",
    // "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\ICD10\\1.1
    // 中文版ICD-10-CM(106.07.19更新)_Chapter.xlsx");
    compareExcelToRedis("ICD10",
        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\ICD10\\1.1 中文版ICD-10-CM(106.07.19更新)_Chapter.xlsx");
    long usedTime = System.currentTimeMillis() - start;
    System.out.println("usedTime:" + usedTime);
    System.out.println(String.format("use time: %.2f", (float) usedTime / (float) 1000));
  }


  @Ignore
  @Test
  public void removeAllKeysInRedis() {
    System.out.println("removeAllKeysInRedis");
    redisTemplate.delete("ICD10-data");
    String key = "ICD10";
    ZSetOperations<String, Object> zsetOp =
        (ZSetOperations<String, Object>) redisTemplate.opsForZSet();
    Set<String> set = redisTemplate.keys("ICD10*");
    int count = 0;
    for (String string : set) {
      RemoveInRedisThread remove = new RemoveInRedisThread(redisTemplate);
      remove.setKey(string);
      new Thread(remove).start();
      count++;
      if (count % 500 == 0) {
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Ignore
  @Test
  public void testICD10ToRedis() {
    String key = "ICD10-data";
    String indexKey = "ICD10-index:";
    String searchValue = "A01.92";

    ObjectMapper mapper = new ObjectMapper();
    ZSetOperations<String, Object> zsetOp =
        (ZSetOperations<String, Object>) redisTemplate.opsForZSet();
    HashOperations<String, String, String> hashOp = redisTemplate.opsForHash();
    // Set<Object> rangeSet = zsetOp.range(indexKey + searchValue, 0, -1);
    Set<String> set = (Set<String>) (Set<?>) zsetOp.range(indexKey + searchValue, 0, -1);
    List<String> values = hashOp.multiGet(key, set);
    for (String string : values) {
      try {
        CodeBaseLongId cb = mapper.readValue(string, CodeBaseLongId.class);
        System.out.println("name=" + cb.getCode() + "," + cb.getDesc() + "," + cb.getDescEn());
      } catch (JsonMappingException e) {
        e.printStackTrace();
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }

    // Long rank = zsetOp.rank(key, searchValue);
    // System.out.println("rank=" + rank);
    // List<String> result = new ArrayList<String>();
    // Set<Object> set = zsetOp.range(key, rank, rank + 50);
    // for (Object object : set) {
    // String s = (String) object;
    // if (!s.startsWith(searchValue)) {
    // break;
    // }
    // if (s.endsWith("*")) {
    // result.add(s.substring(0, s.length() - 1));
    // System.out.println(s.substring(0, s.length() - 1));
    // }
    // }
  }

  private CodeBaseLongId getCodeBaseLongId(String s) {
    return null;
  }

  public void importExcelToRedis(String collectionName, String filename, String category) {
    // HashOperations<String, String, Object> hashOp = ;
    // long maxId = redisTemplate.opsForHash().size(collectionName + "-data");
    long maxId = 91737;
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
        // System.out.println("sheet " + i + " :" +
        // sheet.getRow(1).getCell(0).getStringCellValue());
        if (!sheet.getRow(1).getCell(0).getStringCellValue().equals("代碼")) {
          continue;
        }
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

          // addCode1(collectionName, code);
          CodeBaseLongId cb = new CodeBaseLongId(++maxId, code, descTw, descEn);
          String s = hashOp.get(collectionName + "-data", String.valueOf(maxId));
          if (s != null && s.indexOf(code) > 0) {
            // DB 有這筆資料，換下一筆
            // maxId++;
            continue;
          }

          cb.setCategory(category);
          // addCount += addCode3(op, objectMapper, collectionName, cb);
          addCodeByThread(wtrPool, collectionName, cb, false);
          System.out.println("add(" + maxId + ")[" + addCount + "]" + code + ":" + descEn);
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
          System.out.println("elapsed:" + wtrPool.getThreadCount());
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } while (wtrPool.getThreadCount() > 0);
    } catch (InvalidFormatException e) {
      logger.error("import excel failed", e);
      e.printStackTrace();
    } catch (IOException e) {
      logger.error("import excel failed", e);
      e.printStackTrace();
    }
  }

  private void addCode1(String key, String code) {
    Set<ZSetOperations.TypedTuple<Object>> typedTupleSet =
        new HashSet<ZSetOperations.TypedTuple<Object>>();
    for (int i = 1; i < code.length(); i++) {
      String prefix = code.substring(0, i);
      ZSetOperations.TypedTuple<Object> typedTuple = new DefaultTypedTuple<Object>(prefix, 0.0);
      typedTupleSet.add(typedTuple);
    }
    ZSetOperations.TypedTuple<Object> typedTuple = new DefaultTypedTuple<Object>(code + "*", 0.0);
    typedTupleSet.add(typedTuple);
    redisTemplate.opsForZSet().add(key, typedTupleSet);
  }

  private int addIndexToRedisIndex(ZSetOperations<String, Object> op, String prefix, String index,
      String name) {
    int result = 0;
    for (int i = 2; i < name.length(); i++) {
      String key = name.substring(0, i);
      op.add(prefix + ":" + key, index, 0.0);
      result++;
    }
    return result;
  }

  private int addCode3(ZSetOperations<String, Object> op, ObjectMapper objectMapper, String key,
      CodeBaseLongId cb) {
    int result = 0;
    try {
      String json = objectMapper.writeValueAsString(cb);
      // 1. save to data
      redisTemplate.opsForHash().put(key + "-data", String.valueOf(cb.getId()), json);
      result++;
      // 2. save code to index for search
      result += addIndexToRedisIndex(op, key + "-index", String.valueOf(cb.getId()), cb.getCode());

      String[] descList = cb.getDescEn().split(" ");
      for (String string : descList) {
        if (ignoreWords.contains(string.toLowerCase())) {
          continue;
        }
        String newKey = removeCharacter(string);
        if (newKey.length() < 2) {
          continue;
        }
        result += addIndexToRedisIndex(op, key + "-index", String.valueOf(cb.getId()), newKey);
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    // Set<ZSetOperations.TypedTuple<Object>> typedTupleSet =
    // new HashSet<ZSetOperations.TypedTuple<Object>>();
    // for (int i = 1; i < code.length(); i++) {
    // String prefix = code.substring(0, i);
    // ZSetOperations.TypedTuple<Object> typedTuple = new DefaultTypedTuple<Object>(prefix, 0.0);
    // typedTupleSet.add(typedTuple);
    // }
    // ZSetOperations.TypedTuple<Object> typedTuple = new DefaultTypedTuple<Object>(code + "*",
    // 0.0);
    // typedTupleSet.add(typedTuple);
    // redisTemplate.opsForZSet().add(key, typedTupleSet);
    return result;
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

  public static String removeCharacter(String s) {
    StringBuffer sb = new StringBuffer(s);
    for (int i = s.length() - 1; i >= 0; i--) {
      if (s.charAt(i) == '{' || s.charAt(i) == '}' || s.charAt(i) == '(' || s.charAt(i) == ')'
          || s.charAt(i) == '[' || s.charAt(i) == ']' || s.charAt(i) == ',') {
        sb.deleteCharAt(i);
      }
    }
    return sb.toString();
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
        // System.out.println("sheet " + i + " :" +
        // sheet.getRow(1).getCell(0).getStringCellValue());
        if (!sheet.getRow(1).getCell(0).getStringCellValue().equals("代碼")) {
          continue;
        }
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

          // addCode1(collectionName, code);
          CodeBaseLongId cb = new CodeBaseLongId(++maxId, code, descTw, descEn);
          // addCount += addCode3(op, objectMapper, collectionName, cb);
          addCodeByThread(wtrPool, collectionName, cb, true);
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
      }
      System.out.println("finish total:" + total);
      workbook.close();

      do {
        try {
          Thread.sleep(200);
          System.out.println("elapsed:" + wtrPool.getThreadCount());
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } while (wtrPool.getThreadCount() > 0);
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
  public void testLogic() {
    String key = "ICD10-index:Electromagneti";
    String lowerKey = key.substring(RedisConstants.ICD10_INDEX.length());
    if (lowerKey.toLowerCase().equals(lowerKey)) {
      System.out.println("nothing");
    } else {
      System.out.println(lowerKey);
    }
  }

  /**
   * 將有大小寫的 SortedSet key 改成小寫的
   */
  // @Ignore
  // @Test
  public void convertZsetToLowerCase() {
    Set<String> keys = redisTemplate.keys(RedisConstants.ICD10_INDEX + "*");
//    ZSetOperations<String, Object> zsetOp =
//        (ZSetOperations<String, Object>) redisTemplate.opsForZSet();
    for (String key : keys) {
      String lowerKey = key.substring(RedisConstants.ICD10_INDEX.length());
      if (lowerKey.toLowerCase().equals(lowerKey)) {
        System.out.println("equals:" + lowerKey.toLowerCase() + lowerKey);
        continue;
      }
      redisTemplate.rename(key, RedisConstants.ICD10_INDEX + lowerKey.toLowerCase());
    }

  }

  /**
   * 取得 ICD10-data 裡面的所有 category
   */
  //@Ignore
  @Test
  public void getAllCategory() {
    System.out.println("getAllCategory");
    String key = "ICD10-data";
    HashSet<String> categories = new HashSet<String>();
    ObjectMapper mapper = new ObjectMapper();
    long start = System.currentTimeMillis();
    HashOperations<String, String, String> hashOp = redisTemplate.opsForHash();
    // Set<Object> rangeSet = zsetOp.range(indexKey + searchValue, 0, -1);
    Set<String> fields = hashOp.keys(key);
    for (String field : fields) {
      try {
        String s = hashOp.get(key, field);
        OrderCode cb = mapper.readValue(s, OrderCode.class);
        // System.out.println("name=" + cb.getCode() + "," + cb.getDesc() + "," + cb.getDescEn() +
        // "," + cb.getCategory());
        if (!categories.contains(cb.getCategory())) {
          categories.add(cb.getCategory());
        }
      } catch (JsonMappingException e) {
        e.printStackTrace();
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }
    long usedTime = System.currentTimeMillis() - start;
    System.out.println("usedTime:" + usedTime);
    
    for (String string : categories) {
      System.out.println(string);
    }
  }
  
  /**
   * 取得 ICD10-data 的所有醫令
   */
  @Ignore
  @Test
  public void getAllOrder() {
    String key = "ICD10-data";
    HashSet<String> categories = new HashSet<String>();
    ObjectMapper mapper = new ObjectMapper();
    HashOperations<String, String, String> hashOp = redisTemplate.opsForHash();
    // Set<Object> rangeSet = zsetOp.range(indexKey + searchValue, 0, -1);
    Set<String> fields = hashOp.keys(key);
    for (String field : fields) {
      try {
        String s = hashOp.get(key, field);
        OrderCode cb = mapper.readValue(s, OrderCode.class);
        // System.out.println("name=" + cb.getCode() + "," + cb.getDesc() + "," + cb.getDescEn() +
        // "," + cb.getCategory());
        if ("ORDER".equals(cb.getCategory())) {
          System.out.println(cb);
        }
//        if ("DEDUCTED".equals(cb.getCategory())) {
//          System.out.println(cb);
//        }
      } catch (JsonMappingException e) {
        e.printStackTrace();
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }
    
    for (String string : categories) {
      System.out.println(string);
    }
  }
  
  /**
   * 將DEDUCTED 的類別由 ORDER改為 DEDUCTED
   */
  @Ignore
  @Test
  public void changeOrderToDeducted() {
    String key = "ICD10-data";
    HashSet<String> categories = new HashSet<String>();
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);
    HashOperations<String, String, String> hashOp = redisTemplate.opsForHash();
    // Set<Object> rangeSet = zsetOp.range(indexKey + searchValue, 0, -1);
    Set<String> fields = hashOp.keys(key);
    for (String field : fields) {
      if (Integer.parseInt(field) < 163868) {
        continue;
      }
      try {
        String s = hashOp.get(key, field);
        OrderCode cb = mapper.readValue(s, OrderCode.class);
        // System.out.println("name=" + cb.getCode() + "," + cb.getDesc() + "," + cb.getDescEn() +
        // "," + cb.getCategory());
        if ("ORDER".equals(cb.getCategory()) && cb.getDescEn() == null) {
          cb.setCategory("DEDUCTED");
          System.out.println(s);
          String json = mapper.writeValueAsString(cb);
          // 1. save to data
          redisTemplate.opsForHash().put(key, String.valueOf(cb.getId()), json);
        }
//        if ("DEDUCTED".equals(cb.getCategory())) {
//          System.out.println(cb);
//        }
      } catch (JsonMappingException e) {
        e.printStackTrace();
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }
    
    for (String string : categories) {
      System.out.println(string);
    }
  }
}