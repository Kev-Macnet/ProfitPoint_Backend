/**
 * Created on 2020/12/24.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.constant.XMLConstant;
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.model.CodeBase;
import tw.com.leadtek.nhiwidget.model.CodeMap;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.xml.OutPatient;
import tw.com.leadtek.nhiwidget.xml.Simple;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class TestXML {

  private Logger logger = LogManager.getLogger();

  //@Autowired
  //private MongoTemplate mongo;

  @Autowired
  private CODE_TABLEDao ctDao;

  /**
   * 測試讀取申報檔XML並寫至 mongo DB
   */
  @Ignore
  @Test
  public void readNHIXML() {
    ObjectMapper xmlMapper = new XmlMapper();
    try {
      OutPatient op = xmlMapper.readValue(readXMLFile(
          "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\醫療費用點數申報格式與說明_版1090813\\上傳格式作業說明\\sample.xml"),
          OutPatient.class);

      System.out.println(checkOP(op));

      //saveToMongo(op);
      System.out.println(printOP(op));
      outputFile(op, "test2.xml");

      // InPatient ip = xmlMapper.readValue(readXMLFile(
      // "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\醫療費用點數申報格式與說明_版1090813\\上傳格式作業說明\\sample_inpatient.xml"),
      // InPatient.class);
      //
      // outputFile(ip, "test_inpatient.xml");
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  private String readXMLFile(String filename) {
    StringBuffer sb = new StringBuffer();
    try {
      BufferedReader br = new BufferedReader(
          new InputStreamReader(new FileInputStream(new File(filename)), "BIG5"));
      String line = null;
      while ((line = br.readLine()) != null) {
        // System.out.println(line);
        sb.append(line);
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  private void outputFile(Object obj, String filename) {
    StringWriter stringWriter = new StringWriter();
    XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
    try {
      XMLStreamWriter sw = xmlOutputFactory.createXMLStreamWriter(stringWriter);
      XmlMapper xmlMapper = new XmlMapper();
      // 空的 tag 不輸出
      xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
      xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
      sw.writeStartDocument("Big5", "1.0");
      xmlMapper.writeValue(sw, obj);
      sw.writeEndDocument();
      String xml = stringWriter.toString();

      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
          "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\醫療費用點數申報格式與說明_版1090813\\上傳格式作業說明\\"
              + filename),
          "UTF-8"));
      bw.write(xml);
      bw.close();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (XMLStreamException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void TestSimpleXML() {
    ObjectMapper xmlMapper = new XmlMapper();
    xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
    try {
      Simple value = xmlMapper.readValue("<Simple><tx>1</tx><ty>2</ty></Simple>", Simple.class);

      outputFile(value, "simple.xml");
      // String xml = xmlMapper.writeValueAsString(new Simple());
      // // or
      // // xmlMapper.writeValue(new Simple());
      // try {
      // BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
      // "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\醫療費用點數申報格式與說明_版1090813\\上傳格式作業說明\\simple.xml"),
      // "UTF-8"));
      // bw.write(xml);
      // bw.close();
      // } catch (IOException e) {
      // e.printStackTrace();
      // }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  /**
   * 檢查門診申報檔案內容是否有問題
   * 
   * @param op
   */
  private String checkOP(OutPatient op) {
    StringBuffer sb = new StringBuffer();
    if (!XMLConstant.DATA_FORMAT_OP.equals(op.getTdata().getDATA_FORMAT())) {
      sb.append("t1 資料格式有誤，應為" + XMLConstant.DATA_FORMAT_OP + "," + op.getTdata().getDATA_FORMAT());
      sb.append("\r\n");
    }

    return sb.toString();
  }

  /**
   * 檢查門診申報檔案內容是否有問題
   * 
   * @param op
   */
  private String printOP(OutPatient op) {
    StringBuffer sb = new StringBuffer();
    sb.append("t1 資料格式:門診");
    sb.append("\r\n");

    sb.append("t2 服務機構代號:" + op.getTdata().getHOSP_ID());
    sb.append("\r\n");
    sb.append("t3 費用年月:" + op.getTdata().getFEE_YM());
    return sb.toString();
  }

  /**
   * 匯入XML tag定義檔，至用到的常數
   */
  // @Ignore
  @Test
  public void readExcel() {
    // importXMLTag("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\inpatient.xlsx", "IP");
    // importXMLTag("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\outpatient.xlsx", "OP");

    // importConstants("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\OutIn compare.xlsx");
    importConstantsToRDB("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\OutIn compare.xlsx");
//    importCODE_TABLEToRDB(
//        "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\CODE_TABLE.xlsx");
  }

  public void importXMLTag(String filename, String dataFormat) {
    File file = new File(filename);

    try {
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      HashMap<String, List<CodeBase>> codes = new HashMap<String, List<CodeBase>>();
      processSheet(workbook, codes, "t", 0);
      // printCode(codes, "t");
      processSheet(workbook, codes, "d", 1);
      // printCode(codes, "d");
      processSheet(workbook, codes, "p", 2);
      // printCode(codes, "p");

      workbook.close();
      //saveXMLTagToMongo(codes, dataFormat);
    } catch (InvalidFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 讀excel檔匯入資料至mongo DB
   * 
   * @param filename
   */
  private void importConstants(String filename) {
    File file = new File(filename);

    try {
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      HashMap<String, HashMap<String, CodeBase>> codesBook =
          new HashMap<String, HashMap<String, CodeBase>>();

      processCodeSheet(workbook, codesBook, "FUNC_TYPE");
      // printCodeBook(codesBook, "FUNC_TYPE");
      processCodeSheet(workbook, codesBook, "PAY_TYPE");
      // printCodeBook(codesBook, "PAY_TYPE");
      processCodeSheet(workbook, codesBook, "OP_CASE_TYPE");
      processCodeSheet(workbook, codesBook, "IP_CASE_TYPE");
      // printCodeBook(codesBook, "CASE_TYPE");
      processCodeSheet(workbook, codesBook, "TRAN_CODE");
      // printCodeBook(codesBook, "TRAN_CODE");
      processCodeSheet(workbook, codesBook, "IP_TW_DRGS_SUIT_MARK");
      // printCodeBook(codesBook, "TW_DRGS_SUIT_MARK");
      processCodeSheet(workbook, codesBook, "ORDER_TYPE");
      // printCodeBook(codesBook, "ORDER_TYPE");
      processCodeSheet(workbook, codesBook, "PART_CODE");
      // printCodeBook(codesBook, "PART_CODE");
      processCodeSheet(workbook, codesBook, "IP_PATIENT_SOURCE");
      processCodeSheet(workbook, codesBook, "OP_CURE_ITEM");
      processCodeSheet(workbook, codesBook, "OP_MED_TYPE");
      workbook.close();
      //saveConstansToMongo(codesBook);
    } catch (InvalidFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 讀excel檔匯入資料至 RDB
   * 
   * @param filename
   */
  private void importConstantsToRDB(String filename) {
    System.out.println("importConstantsToRDB " + filename);
    File file = new File(filename);

    try {
      XSSFWorkbook workbook = new XSSFWorkbook(file);

//      processCodeSheetForRDB(workbook, "FUNC_TYPE");
//      processCodeSheetForRDB(workbook, "PAY_TYPE");
//      processCodeSheetForRDB(workbook, "OP_CASE_TYPE");
//      processCodeSheetForRDB(workbook, "IP_CASE_TYPE");
//      processCodeSheetForRDB(workbook, "TRAN_CODE");
//      processCodeSheetForRDB(workbook, "IP_TW_DRGS_SUIT_MARK");
//      processCodeSheetForRDB(workbook, "ORDER_TYPE");
//      processCodeSheetForRDB(workbook, "PART_CODE");
//      processCodeSheetForRDB(workbook, "IP_PATIENT_SOURCE");
      processCodeSheetForRDB(workbook, "OP_CURE_ITEM");
      //processCodeSheetForRDB(workbook, "OP_MED_TYPE");
      workbook.close();
    } catch (InvalidFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void processSheet(XSSFWorkbook workbook, HashMap<String, List<CodeBase>> codes,
      String groupName, int sheetIndex) {
    XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
    List<CodeBase> list = new ArrayList<CodeBase>();
    codes.put(groupName, list);
    for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
      XSSFRow row = sheet.getRow(i);
      String[] ss = getAllCellStringValue(row);
      if (ss == null) {
        break;
      }
      if (ss[0] != null && ss[0].length() == 0) {
        continue;
      }
      CodeBase cb = CodeBase.initial(ss, 1);
      if (cb != null) {
        list.add(cb);
      }
    }
  }

  public void printCode(HashMap<String, List<CodeBase>> codes, String key) {
    List<CodeBase> list = codes.get(key);
    if (list == null) {
      System.out.println(key + " is empty");
      return;
    }
    for (CodeBase codeBase : list) {
      System.out
          .println(codeBase.getCode() + ":" + codeBase.getDescEn() + ":" + codeBase.getDesc());
    }
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

  /**
   * 處理儲放代碼的sheet
   * 
   * @param workbook
   * @param codes
   * @param groupName
   * @param sheetIndex
   */
  private void processCodeSheet(XSSFWorkbook workbook,
      HashMap<String, HashMap<String, CodeBase>> codes, String groupName) {
    XSSFSheet sheet = getSheetByName(workbook, groupName);
    if (sheet == null) {
      System.out.println(groupName + " not exist.");
      return;
    }
    HashMap<String, CodeBase> map = new HashMap<String, CodeBase>();
    codes.put(groupName, map);
    for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
      XSSFRow row = sheet.getRow(i);
      String[] ss = getAllCellStringValue(row);
      if (ss == null) {
        break;
      }
      if (ss[0] != null && ss[0].length() == 0) {
        continue;
      }
      CodeMap cb = CodeMap.initial(ss, 0);
      if (cb == null || cb.getCode() == null) {
        continue;
      }
      cb.setCat(groupName);
      if (groupName.equals("FUNC_TYPE")) {
        addCodeMapChild(map, cb);
      }
      cb.setDescEn(null);
      map.put(cb.getCode(), cb);
    }

    // System.out.println(
    // "================================" + groupName + "===================================");
    // printCodeBook(codes, groupName);
  }

  /**
   * 處理儲放代碼的sheet
   * 
   * @param workbook
   * @param codes
   * @param groupName
   * @param sheetIndex
   */
  private void processCodeSheetForRDB(XSSFWorkbook workbook, String groupName) {
    XSSFSheet sheet = getSheetByName(workbook, groupName);
    if (sheet == null) {
      System.out.println(groupName + " not exist.");
      return;
    }
    for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
      XSSFRow row = sheet.getRow(i);
      String[] ss = getAllCellStringValue(row);
      if (ss == null) {
        break;
      }
      if (ss[0] != null && ss[0].length() == 0) {
        continue;
      }
      CODE_TABLE ct = CODE_TABLE.initial(ss, 0);

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
      if (ctDao.findByCodeAndCat(ct.getCode(), groupName) == null) {
        ctDao.save(ct);
      }
    }
  }

  private void addCodeMapChild(HashMap<String, CodeBase> map, CodeMap cm) {
    if (cm.getDescEn() == null || cm.getDescEn().length() == 0) {
      return;
    }
    for (String code : map.keySet()) {
      CodeMap codeMap = (CodeMap) map.get(code);
      if (codeMap.getDesc().equals(cm.getDescEn())) {
        List<CodeMap> list = codeMap.getChildren();
        if (list == null) {
          list = new ArrayList<CodeMap>();
          codeMap.setChildren(list);
        }
        list.add(cm);
        break;
      }
    }
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

  private void printCodeBook(HashMap<String, HashMap<String, CodeBase>> codesBook,
      String groupName) {
    HashMap<String, CodeBase> map = codesBook.get(groupName);
    for (String code : map.keySet()) {
      System.out.println(code + ":" + map.get(code).getDesc());
    }
  }

  /*
  private void saveToMongo(OutPatient op) {
    checkCollection("OP_t");
    OutPatientT opt = mongo.save(op.getTdata(), "OP_t");
    System.out.println("opt.id=" + opt.getId());

    checkCollection("OP_d");
    for (OutPatientDData opDData : op.getDdata()) {
      opDData.setTid(opt.getId().toString());
      mongo.save(opDData, "OP_d");
    }
  }

  private void checkCollection(String collectionName) {
    MongoCollection<Document> collection = mongo.getCollection(collectionName);
    if (collection == null) {
      System.out.println("collection '" + collectionName + "' is null");
      collection = mongo.createCollection(collectionName);
    }
  }

  private void saveConstansToMongo(HashMap<String, HashMap<String, CodeBase>> codes) {
    String collectionName = "code_map";
    checkCollection(collectionName);
    for (String group : codes.keySet()) {
      HashMap<String, CodeBase> map = codes.get(group);
      for (String code : map.keySet()) {
        CodeMap codeMap = (CodeMap) map.get(code);
        mongo.upsert(getUpsertQuery(codeMap.getCode(), "cat", group), initialUpdate(codeMap),
            collectionName);
      }
    }
  }*/

  /**
   * 儲存申報檔的XML tag至Mongo DB
   * 
   * @param codes

  private void saveXMLTagToMongo(HashMap<String, List<CodeBase>> codes, String dataFormat) {
    String collectionName = "xml_tag";
    checkCollection(collectionName);
    for (String tagGroup : codes.keySet()) {
      List<CodeBase> cbs = codes.get(tagGroup);
      for (CodeBase codeBase : cbs) {
        // System.out.println(codeBase.getCode() + ",cat," + dataFormat);
        mongo.upsert(getUpsertQuery(codeBase.getCode(), "cat", dataFormat),
            initialUpdate(dataFormat, codeBase.getCode(), codeBase.getDescEn(), codeBase.getDesc()),
            collectionName);
      }
    }
  }
     */

  /**
   * 取得要 Upsert 至 mongoDB 的查詢條件
   * 
   * @param fieldList 表單所需欄位
   * @param dbObject 要更新的object
   * @return
  
  private Query getUpsertQuery(String code, String key, String dataFormat) {
    Query result = new Query();
    result.addCriteria(Criteria.where("code").is(code));
    result.addCriteria(Criteria.where(key).is(dataFormat));
    return result;
  }
   */

  /**
   * 初始化寫入mongo DB的 xml tag
   * 
   * @param dataFormat 資料格式，IP:住院，OP:門診
   * @param code tag name
   * @param descEn 對應的變數名稱
   * @param descTw 中文說明
   * @return
 
  private Update initialUpdate(String dataFormat, String code, String descEn, String descTw) {
    Update update = new Update();
    update.set("cat", dataFormat);
    update.set("code", code);
    if (descEn != null) {
      update.set("descEn", descEn);
    }
    update.set("desc", descTw);
    update.set("updateAt", new Date());

    return update;
  }
    */

  /**
   * 初始化寫入mongo DB的常數
   * 
   * @param dataFormat 資料格式，IP:住院，OP:門診
   * @param code tag name
   * @param descEn 對應的變數名稱
   * @param descTw 中文說明
   * @return

  private Update initialUpdate(CodeMap cb) {
    Update update = new Update();
    update.set("cat", cb.getCat());
    update.set("code", cb.getCode());
    if (cb.getDescEn() != null) {
      update.set("descEn", cb.getDescEn());
    }
    if (cb.getChildren() != null) {
      update.set("children", cb.getChildren());
    }
    update.set("desc", cb.getDesc());
    update.set("updateAt", new Date());

    return update;
  }
     */

  /**
   * 讀excel檔匯入資料至 RDB
   * 
   * @param filename
   */
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
      ct.setCode(row.getCell(2).getStringCellValue());
      if (row.getCell(3) == null) {
        System.err.println(ct.getCat() + "," + ct.getCode() + " no DESC_CHI");
        ct.setDescChi("");
        //break;
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


  public static void main(String[] args) {
    // new TestXML().test();
    // new TestXML().TestSimpleXML();

    new TestXML().readExcel();
  }

}
