/**
 * Created on 2021/4/19.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.TestParameterService;
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.dao.ICD10Dao;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.ICD10;
import tw.com.leadtek.nhiwidget.model.redis.CodeBaseLongId;
import tw.com.leadtek.nhiwidget.model.redis.OrderCode;

/**
 * 處理法定傳染病
 * @author kenlai
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class Infectious {

  private Logger logger = LogManager.getLogger();
  
  private final static String CATEGORY = "INFECTIOUS";
  
  @Autowired
  private RedisTemplate<String, Object> redis;
  
  @Autowired
  private CODE_TABLEDao ctDao;
  
  @Autowired
  private ICD10Dao icdDao;

  @Ignore
  @Test
  public void getICDDescription() {
    HashSet<String> codes = new HashSet<String>();
    System.out.println("getICDDescription");
    try {
      BufferedReader br = new BufferedReader(new FileReader(new File("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\法定傳染病\\icd.txt")));
      BufferedWriter bw =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\法定傳染病\\icd_desc.txt"), "BIG5"));
      String s = null;
      while ((s = br.readLine()) != null) {
        if (codes.contains(s.trim())) {
          System.out.println(s + " duplicate");
          continue;
        }
        codes.add(s.trim());
        codes.add(s.trim());
        CodeBaseLongId oc = search(s.trim().toLowerCase(), "ICD10-CM");
        if (oc != null) {
          //System.out.println(oc.getCode() + ":" + oc.getDescEn() + "(" + oc.getDesc() +")");
          bw.write(oc.getCode());
          bw.write('|');
          bw.write(oc.getDesc());
          bw.write('|');
          bw.write(oc.getDescEn());       
          bw.newLine();
        } else {
          bw.write(s.trim());
          bw.write('|');
          bw.write('|');
          bw.newLine();
        }
      }
      br.close();
      bw.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public CodeBaseLongId search(String searchKey, String category) {
    String key = "ICD10-data";
    String indexKey = "ICD10-index:";

    ObjectMapper mapper = new ObjectMapper();
    ZSetOperations<String, Object> zsetOp = (ZSetOperations<String, Object>) redis.opsForZSet();
    HashOperations<String, String, String> hashOp = redis.opsForHash();
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
  
  @Test
  public void importInfectious() {
    String filename = TestParameterService.FILE_PATH + "法定傳染病_ICD代碼_1090511.xlsx";
    File file = new File(filename);
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
          ct.setCat(CATEGORY);
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
          CODE_TABLE ctDB = ctDao.findByCodeAndCat(ct.getCode(), CATEGORY);
          if (ctDB != null) {
            ct.setId(ctDB.getId());
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
  
  public void updateICD10(CODE_TABLE ct) {
    ICD10 icd = icdDao.findByCode(ct.getCode());
    if (icd == null) {
      return;
    }
    icd.setInfectious(1);
    icd.setInfCat(ct.getParentCode());
    icdDao.save(icd);
  }
}
