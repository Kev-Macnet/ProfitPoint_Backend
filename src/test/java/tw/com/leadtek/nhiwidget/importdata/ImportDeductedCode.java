/**
 * Created on 2021/3/29.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
public class ImportDeductedCode {
  private Logger logger = LogManager.getLogger();

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d");
  
  private final static String CATEGORY = "DEDUCTED";

  /**
   * 存放在 HashSet 的 id
   */
  private long maxId = 0;

  /**
   * 匯入核減代碼(支付代碼)
   */
  // @Ignore
  @Test
  public void importCode() {
    System.out.println("importDeductedCode");
    // maxId = 163869;
    maxId = redisTemplate.opsForHash().size("ICD10" + "-data");
    System.out.println("maxId:" + maxId);
//    importExcelToRedis("ICD10",
//        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\(行政審查)全民健康保險檔案分析審查異常不予支付指標及處理方式2001228.xlsx",
//        CATEGORY, 1);
    importExcelToRedis("ICD10",
        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\(專業審查)不予支付理由_20201218.xlsx",
        CATEGORY, 0);
  }

  public void importExcelToRedis(String collectionName, String filename, String category, int titleRow) {
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
      String sheetName = "";
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        XSSFSheet sheet = workbook.getSheetAt(i);
        // 存放代碼欄位順序
        int codeIndex = getDeductedCodeFieldIndex(sheet.getRow(titleRow), new String[] {"代碼"});
        if (codeIndex < 0) {
          System.out.println(sheet.getSheetName() + "無代碼");
          continue;
        }

        // 存放中文說明欄位順序
        int descIndex = getDeductedCodeFieldIndex(sheet.getRow(titleRow), new String[] {"中文說明", "不予支付理由"});
        // 存放檢核類別欄位順序
        int detailCategoryIndex = getDeductedCodeFieldIndex(sheet.getRow(titleRow), new String[] {"分類", "類別"});
        // 存放法源欄位順序
        int lawIndex = getDeductedCodeFieldIndex(sheet.getRow(titleRow), new String[] {"法源"});
        int count = 0;

        System.out.println(sheet.getSheetName() + ", desc:" + descIndex + ",detailCategory:"
            + detailCategoryIndex + ",law:" + lawIndex);
        String detailCategory = null;
        for (int j = 2; j < sheet.getPhysicalNumberOfRows(); j++) {
          XSSFRow row = sheet.getRow(j);
          if (row == null || row.getCell(codeIndex) == null) {
            // System.out.println("sheet:" + i + ", row=" + j + " is null");
            continue;
          }
          String code = row.getCell(codeIndex).getStringCellValue().trim().toLowerCase();
          System.out.println("save " + code);
          if (code.length() == 0) {
            break;
          }
          OrderCode oc = getOrderCodyByExcelRow(row, codeIndex,descIndex, detailCategoryIndex, detailCategory, lawIndex );
          if (oc.getDetailCat() != null && !oc.getDetailCat().equals(detailCategory)) {
            detailCategory = oc.getDetailCat();
          }
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

  private OrderCode getOrderCodyByExcelRow(XSSFRow row, int codeIndex, int descIndex, int detailCategoryIndex, 
      String detailCategory, int lawIndex) {
    String code = row.getCell(codeIndex).getStringCellValue().trim().toLowerCase();

    String descTw = row.getCell(descIndex).getStringCellValue().trim();

    // addCode1(collectionName, code);
    OrderCode oc = new OrderCode(++maxId, code, descTw, null);
    oc.setCategory(CATEGORY);
    if (row.getCell(detailCategoryIndex) != null && row.getCell(detailCategoryIndex).getStringCellValue().length() >0) {
      String newDetailCategory = row.getCell(detailCategoryIndex).getStringCellValue();
      if (newDetailCategory.indexOf('.') > -1) {
        newDetailCategory = newDetailCategory.split("\\.")[1];
      }
      oc.setDetailCat(newDetailCategory);
    } else {
      oc.setDetailCat(detailCategory);
    }
    if (lawIndex > -1) {
      if (row.getCell(lawIndex).getCellType() == CellType.NUMERIC) {
        oc.setLaw(String.format("%.0f", row.getCell(lawIndex).getNumericCellValue()));
      } else {
        oc.setLaw(row.getCell(lawIndex).getStringCellValue());
      }
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

  /**
   * 取得 keyWords 是否在 titleRow 出現，若有則回傳欄位順序，否則回傳-1.
   * 
   * @param titleRow
   * @param keyWords
   * @return
   */
  private int getDeductedCodeFieldIndex(XSSFRow titleRow, String[] keyWords) {
    for (int i = 0; i < titleRow.getPhysicalNumberOfCells(); i++) {
      for (String s : keyWords) {
        if (titleRow.getCell(i).getCellType() == CellType.NUMERIC) {
          continue;
        }
        if (titleRow.getCell(i).getStringCellValue().indexOf(s) > -1) {
          System.out.println(i + ":" + titleRow.getCell(i).getStringCellValue());
          return i;
        }
      }
    }
    return -1;
  }
}
