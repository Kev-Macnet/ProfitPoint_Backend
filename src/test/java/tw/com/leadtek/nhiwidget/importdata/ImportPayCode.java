/**
 * Created on 2021/2/2.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.model.redis.CodeBaseLongId;
import tw.com.leadtek.nhiwidget.model.redis.OrderCode;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class ImportPayCode {

  private Logger logger = LogManager.getLogger();

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d");

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

  //@Ignore
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

    String descEn = null;
    if (row.getCell(4) != null) {
      descEn = row.getCell(4).getStringCellValue();
      if (descEn != null) {
        descEn = descEn.trim();
      }
    }
    String descTw = row.getCell(5).getStringCellValue().trim();

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
}
