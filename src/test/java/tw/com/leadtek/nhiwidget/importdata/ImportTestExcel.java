/**
 * Created on 2021/7/7.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.IP_TDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.OP_TDao;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.IP_P;
import tw.com.leadtek.nhiwidget.model.rdb.IP_T;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.nhiwidget.model.rdb.OP_P;
import tw.com.leadtek.nhiwidget.model.rdb.OP_T;
import tw.com.leadtek.nhiwidget.service.CodeTableService;
import tw.com.leadtek.nhiwidget.service.NHIWidgetXMLService;
import tw.com.leadtek.tools.DateTool;
import tw.com.leadtek.tools.ExcelUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class ImportTestExcel {

  /**
   * 存放已存在總表的年月，避免重複 query DB.
   */
  private HashMap<String, Object> existT = null;

  @Autowired
  private IP_TDao ipTDao;

  @Autowired
  private OP_TDao opTDao;

  @Autowired
  private OP_DDao opDDao;

  @Autowired
  private IP_DDao ipDDao;

  @Autowired
  private OP_PDao opPDao;

  @Autowired
  private IP_PDao ipPDao;

  @Autowired
  private MRDao mrDao;

  @Autowired
  private CodeTableService cts;

  @Autowired
  private NHIWidgetXMLService xmlService;

  private int MIN_SEQ_NO = 37000;

  private List<OP_D> opdSet = new ArrayList<OP_D>();

  private List<OP_P> oppList = new ArrayList<OP_P>();

  private List<IP_D> ipdList = new ArrayList<IP_D>();

  private HashMap<Integer, Long> OP_DKeyID = null;

  @Ignore
  @Test
  public void readExcel() {
    System.out.println("readExcel");
    // importOPExcel("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\opdte_假資料用欄位.xlsx", 4);
    importIPExcel("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\ipdte_假資料用欄位.xlsx", 4);
  }

  public void importOPExcel(String filename, int titleRow) {
    // HashOperations<String, String, Object> hashOp = ;
    // long maxId = redisTemplate.opsForHash().size(collectionName + "-data");
    // 前面 163661 筆是 ICD10 診斷碼 + 處置碼
    existT = new HashMap<String, Object>();
    File file = new File(filename);
    try {
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      // DataFormatter formatter = new DataFormatter();

      int total = 0;
      String sheetName = "";
      XSSFSheet sheet = workbook.getSheetAt(0);
      HashMap<Integer, String> columnMap = ExcelUtil.readTitleRow(sheet.getRow(0));
      int count = 0;

      OP_T opT = null;
      OP_D opD = null;
      MR mr = null;
      for (int i = titleRow; i < sheet.getPhysicalNumberOfRows(); i++) {
        XSSFRow row = sheet.getRow(i);
        if (row == null) {
          // System.out.println("sheet:" + i + ", row=" + j + " is null");
          continue;
        }
        HashMap<String, String> values = ExcelUtil.readCellValue(columnMap, row);
        if (values.get("d9") == null) {
          break;
        }
        // start
        if (values.get("t3") != null) {
          if (opT != null) {
            opDDao.save(opD);
          }
          opT = (OP_T) getTID(values.get("t3"), values.get("t5"), values.get("t6"), false);
          opD = new OP_D();
          System.out.println(values.get("t3") + "," + values.get("t6") + "," + values.get("d1")
              + "," + values.get("d2"));
          opD.setOptId(opT.getId());
          opD.setCaseType(values.get("d1"));
          opD.setSeqNo(Integer.parseInt(values.get("d2")));
          opD.setRocId(values.get("d3"));
          opD.setCureItemNo1(values.get("d4"));
          opD.setFuncType(values.get("d8"));
          opD.setFuncDate(values.get("d9"));
          opD.setFuncEndDate(values.get("d10"));
          opD.setIdBirthYmd(values.get("d11"));
          opD.setPayType(values.get("d14"));
          opD.setCardSeqNo(values.get("d29"));
          opD.setPartNo(values.get("d15"));
          opD.setShareMark(values.get("d16"));
          opD.setShareHospId(values.get("d17"));
          opD.setPatTranOut(values.get("d18"));
          opD.setIcdCm1(values.get("d19"));
          opD.setIcdCm2(values.get("d20"));
          opD.setPrsnId(values.get("d30"));

          if (values.get("d39") != null) {
            opD.setTotalDot(Integer.parseInt(values.get("d39")));
          }
          if (values.get("d40") != null) {
            opD.setPartDot(Integer.parseInt(values.get("d40")));
          }
          if (values.get("d41") != null) {
            opD.setTotalApplDot(Integer.parseInt(values.get("d41")));
          }
          opD.setName(values.get("d49"));
          opD.setHospId(values.get("d54"));

          mr = new MR(opD);
          mr.setStatus(MR_STATUS.NO_CHANGE.value());
          mr = mrDao.save(mr);
          CODE_TABLE ct = cts.getCodeTable("INFECTIOUS", opD.getIcdCm1());
          mr.setInfectious((ct == null) ? 0 : 1);
          opD.setMrId(mr.getId());
          opD = opDDao.save(opD);

          mr.setdId(opD.getId());
          mr = mrDao.save(mr);
        }
        OP_P opp = new OP_P();
        opp.setOpdId(opD.getId());
        opp.setMrId(mr.getId());
        if (values.get("d27") != null) {
          opD.setDrugDay(Integer.parseInt(values.get("d27")));
          opp.setDrugDay(Integer.parseInt(values.get("d27")));
        }
        if (values.get("d28") != null) {
          String medType = values.get("d28");
          if (medType.length() == 2 && medType.charAt(0) == '0') {
            medType = medType.substring(1);
          }
          opD.setMedType(medType);
          opp.setMedType(medType);
        }
        if (values.get("d31") != null) {
          opD.setPharId(values.get("d31"));
        }
        if (values.get("d32") != null) {
          opD.setDrugDot(Integer.parseInt(values.get("d32")));
        }
        if (values.get("d33") != null) {
          opD.setTreatDot(Integer.parseInt(values.get("d33")));
        }
        if (values.get("d34") != null) {
          opD.setMetrDot(Integer.parseInt(values.get("d34")));
        }
        if (values.get("d35") != null) {
          opD.setTreatCode(values.get("d35"));
        }
        if (values.get("d36") != null) {
          opD.setDiagDot(Integer.parseInt(values.get("d36")));
        }
        if (values.get("d37") != null) {
          opD.setDsvcNo(values.get("d37"));
        }
        if (values.get("d38") != null) {
          opD.setDsvcDot(Integer.parseInt(values.get("d38")));
        }
        opp.setOrderType(values.get("p3"));
        opp.setOrderSeqNo(Integer.parseInt(values.get("p13")));
        opp.setDrugNo(values.get("p4"));
        opp.setDrugUse(Double.parseDouble(values.get("p5")));
        opp.setTotalQ(Double.parseDouble(values.get("p10")));
        opp.setUnitP(Float.parseFloat(values.get("p11")));
        opp.setTotalDot(Integer.parseInt(values.get("p12")));
        opp.setStartTime(values.get("p14"));
        opp.setEndTime(values.get("p15"));
        if (values.get("p16") != null) {
          opp.setPrsnId(values.get("p16"));
        }
        opPDao.save(opp);
        // end
      }
      if (opD != null) {
        opDDao.save(opD);
      }
    } catch (InvalidFormatException e) {
      System.out.println("import excel failed");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("import excel failed");
      e.printStackTrace();
    }
  }

  private HashMap<String, Integer> readFieldNameAndIndex(XSSFRow row) {
    HashMap<String, Integer> result = new HashMap<String, Integer>();
    for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
      if (row.getCell(i) == null) {
        continue;
      }
      String cellValue = row.getCell(i).getStringCellValue();
      if (cellValue != null && cellValue.length() > 1) {
        if (cellValue.indexOf(',') > -1) {
          cellValue = cellValue.split(",")[0];
        }
        result.put(cellValue, Integer.valueOf(i));
      }
    }
    return result;
  }

  /**
   * 取得總表ID
   * 
   * @param ym
   * @param isIP
   * @return
   */
  public Object getTID(String ym, String applyType, String applDate, boolean isIP) {
    String feeYM = ym.length() > 5 ? ym.substring(0, 5) : ym;
    if (existT.containsKey(feeYM)) {
      return existT.get(feeYM);
    }
    if (ym.startsWith("2")) {
      feeYM = DateTool.convertToChineseYear(feeYM);
    }
    if (isIP) {
      List<IP_T> list = ipTDao.findByFeeYmOrderById(feeYM);
      if (list == null || list.size() == 0) {
        IP_T ipt = new IP_T();
        ipt.setFeeYm(feeYM);
        ipt.setApplType(applyType);
        ipt.setApplDate(applDate);
        ipt.setUpdateAt(new Date());
        ipt = ipTDao.save(ipt);
        existT.put(ym, ipt);
        return ipt;
      } else {
        existT.put(ym, list.get(0));
        return list.get(0);
      }
    } else {
      List<OP_T> list = opTDao.findByFeeYmOrderById(feeYM);
      if (list == null || list.size() == 0) {
        OP_T opt = new OP_T();
        opt.setFeeYm(feeYM);
        opt.setApplType(applyType);
        opt.setApplDate(applDate);
        opt.setUpdateAt(new Date());
        opt = opTDao.save(opt);
        existT.put(ym, opt);
        return opt;
      } else {
        existT.put(ym, list.get(0));
        return list.get(0);
      }
    }
  }

  public void importIPExcel(String filename, int titleRow) {
    // HashOperations<String, String, Object> hashOp = ;
    // long maxId = redisTemplate.opsForHash().size(collectionName + "-data");
    // 前面 163661 筆是 ICD10 診斷碼 + 處置碼
    existT = new HashMap<String, Object>();
    File file = new File(filename);
    try {
      XSSFWorkbook workbook = new XSSFWorkbook(file);
      // DataFormatter formatter = new DataFormatter();

      int total = 0;
      String sheetName = "";
      XSSFSheet sheet = workbook.getSheetAt(0);
      HashMap<Integer, String> columnMap = ExcelUtil.readTitleRow(sheet.getRow(0));
      int count = 0;

      IP_T ipT = null;
      IP_D ipD = null;
      MR mr = null;
      for (int i = titleRow; i < sheet.getPhysicalNumberOfRows(); i++) {
        XSSFRow row = sheet.getRow(i);
        if (row == null) {
          // System.out.println("sheet:" + i + ", row=" + j + " is null");
          continue;
        }
        HashMap<String, String> values = ExcelUtil.readCellValue(columnMap, row);
        System.out.println(values.get("t3") + "," + values.get("t6") + "," + values.get("p1") + ","
            + values.get("d17"));
        if (values.get("d17") == null || values.get("p1") == null) {
          break;
        }
        // start
        if (values.get("t3") != null) {
          if (ipT != null) {
            ipDDao.save(ipD);
          }
          ipT = (IP_T) getTID(values.get("t3"), values.get("t5"), values.get("t6"), true);
          ipD = new IP_D();
          ipD.setIptId(ipT.getId());
          ipD.setCaseType(values.get("d1"));
          ipD.setSeqNo(Integer.parseInt(values.get("d2")));
          ipD.setRocId(values.get("d3"));
          ipD.setPartNo(values.get("d4"));
          ipD.setIdBirthYmd(values.get("d6"));
          ipD.setPayType(values.get("d7"));
          ipD.setFuncType(values.get("d9"));
          ipD.setInDate(values.get("d10"));
          ipD.setOutDate(values.get("d11"));
          ipD.setApplStartDate(values.get("d12"));
          ipD.setApplEndDate(values.get("d13"));
          ipD.setEbedDay(Integer.parseInt(values.get("d14")));
          ipD.setSbedDay(Integer.parseInt(values.get("d15")));
          ipD.setPatientSource(values.get("d16"));
          ipD.setCardSeqNo(values.get("d17"));
          ipD.setTwDrgCode(values.get("d18"));
          ipD.setTwDrgPayType(values.get("d19"));
          ipD.setPrsnId(values.get("d20"));
          ipD.setCaseDrgCode(values.get("d21"));
          ipD.setIcdCm1(values.get("d25"));
          ipD.setIcdCm2(values.get("d26"));
          ipD.setIcdCm3(values.get("d27"));
          ipD.setIcdCm4(values.get("d28"));
          ipD.setIcdCm5(values.get("d29"));
          ipD.setIcdOpCode1(values.get("d45"));
          ipD.setIcdOpCode2(values.get("d46"));
          ipD.setIcdOpCode3(values.get("d47"));
          ipD.setIcdOpCode4(values.get("d48"));
          ipD.setIcdOpCode5(values.get("d49"));
          if (values.get("d65") != null) {
            ipD.setOrderQty(Integer.parseInt(values.get("d65")));
          }
          if (values.get("d83") != null) {
            ipD.setMedDot(Integer.parseInt(values.get("d83")));
          }
          if (values.get("d85") != null) {
            ipD.setApplDot(Integer.parseInt(values.get("d85")));
          }
          if (values.get("d92") != null) {
            ipD.setSbAppl30Dot(Integer.parseInt(values.get("d92")));
          }
          ipD.setName(values.get("d103"));

          mr = new MR(ipD);
          mr.setStatus(MR_STATUS.NO_CHANGE.value());
          mr = mrDao.save(mr);
          CODE_TABLE ct = cts.getCodeTable("INFECTIOUS", ipD.getIcdCm1());
          mr.setInfectious((ct == null) ? 0 : 1);
          ipD.setMrId(mr.getId());
          ipD = ipDDao.save(ipD);

          mr.setdId(ipD.getId());
          mr = mrDao.save(mr);
        }

        if (values.get("d66") != null) {
          ipD.setDiagDot(Integer.parseInt(values.get("d66")));
        }
        if (values.get("d67") != null) {
          ipD.setRoomDot(Integer.parseInt(values.get("d67")));
        }
        if (values.get("d68") != null) {
          ipD.setMealDot(Integer.parseInt(values.get("d68")));
        }
        if (values.get("d69") != null) {
          ipD.setAminDot(Integer.parseInt(values.get("d69")));
        }
        if (values.get("d70") != null) {
          ipD.setRadoDot(Integer.parseInt(values.get("d70")));
        }
        if (values.get("d71") != null) {
          ipD.setThrpDot(Integer.parseInt(values.get("d71")));
        }
        if (values.get("d72") != null) {
          ipD.setSgryDot(Integer.parseInt(values.get("d72")));
        }
        if (values.get("d73") != null) {
          ipD.setPhscDot(Integer.parseInt(values.get("d73")));
        }
        if (values.get("d76") != null) {
          ipD.setAneDot(Integer.parseInt(values.get("d76")));
        }
        if (values.get("d77") != null) {
          ipD.setMetrDot(Integer.parseInt(values.get("d77")));
        }
        if (values.get("d78") != null) {
          ipD.setDrugDot(Integer.parseInt(values.get("d78")));
        }
        if (values.get("d79") != null) {
          ipD.setDsvcDot(Integer.parseInt(values.get("d79")));
        }
        if (values.get("d80") != null) {
          ipD.setNrtpDot(Integer.parseInt(values.get("d80")));
        }
        if (values.get("d80") != null) {
          ipD.setNrtpDot(Integer.parseInt(values.get("d80")));
        }
        if (values.get("d81") != null) {
          ipD.setInjtDot(Integer.parseInt(values.get("d81")));
        }

        IP_P ipp = new IP_P();
        ipp.setIpdId(ipD.getId());
        ipp.setMrId(mr.getId());
        ipp.setOrderSeqNo(Integer.parseInt(values.get("p1")));
        ipp.setOrderType(values.get("p2"));
        ipp.setOrderCode(values.get("p3"));
        ipp.setStartTime(values.get("p14"));
        ipp.setEndTime(values.get("p15"));
        ipp.setFuncType(ipD.getFuncType());
        ipp.setPayRate("1");
        ipp.setDrugUse(Double.parseDouble(values.get("p5")));
        ipp.setDrugFre(values.get("p6"));
        if (values.get("p16") != null) {
          ipp.setTotalQ(Double.parseDouble(values.get("p16")));
        }
        if (values.get("p17") != null) {
          ipp.setUnitP(Float.parseFloat(values.get("p17")));
        }
        if (values.get("p18") != null) {
          ipp.setTotalDot(Integer.parseInt(values.get("p18")));
        }
        ipp.setConFuncType(values.get("p8"));
        if (values.get("p11") != null) {
          ipp.setTwDrgsCalcu(Double.parseDouble(values.get("p11")));
        }
        ipp.setPrsnId(values.get("p20"));
        ipPDao.save(ipp);
        // end
      }
      if (ipD != null) {
        ipDDao.save(ipD);
      }
    } catch (

    InvalidFormatException e) {
      System.out.println("import excel failed");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("import excel failed");
      e.printStackTrace();
    }
  }

  //@Ignore
  @Test
  public void importExcelData() {
    File[] files = new File("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\羅東博愛醫院").listFiles();
    HSSFWorkbook workbook = null;
    for (File file : files) {
      if (!file.getName().endsWith(".xls")) {
        continue;
      }
      try {
       // System.out.println("file name=" + file.getName());
        if (file.getName().toUpperCase().indexOf("OPD") > -1) {
          // workbook = new HSSFWorkbook(new FileInputStream(file));
          // if (workbook.getSheetAt(0).getRow(0).getPhysicalNumberOfCells() > 10) {
          // xmlService.readOpdSheet(workbook.getSheetAt(0));
          // }
        } else if (file.getName().toUpperCase().indexOf("IPD") > -1) {
//          workbook = new HSSFWorkbook(new FileInputStream(file));
//          if (workbook.getSheetAt(0).getRow(0).getPhysicalNumberOfCells() > 10) {
//            xmlService.readIpdSheet(workbook.getSheetAt(0));
//          }
        }
        if (workbook != null) {
          workbook.close();
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    for (File file : files) {
      if (!file.getName().endsWith(".xls")) {
        continue;
      }
      try {
        //System.out.println("file name=" + file.getName());
        if (file.getName().toUpperCase().indexOf("OPP") > -1) {
          // workbook = new HSSFWorkbook(new FileInputStream(file));
          // if (workbook.getSheetAt(0).getRow(0).getPhysicalNumberOfCells() > 10) {
          // xmlService.readOppHSSFSheet(workbook.getSheetAt(0));
          // }
        } else if (file.getName().toUpperCase().indexOf("IPP") > -1) {
//          workbook = new HSSFWorkbook(new FileInputStream(file));
//          if (workbook.getSheetAt(0).getRow(0).getPhysicalNumberOfCells() > 10) {
//            xmlService.readIppHSSFSheet(workbook.getSheetAt(0));
//          }
        }
        if (workbook != null) {
          workbook.close();
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    for (File file : files) {
      if (!file.getName().endsWith(".xls")) {
        continue;
      }
      try {
        System.out.println("file name=" + file.getName());
        if (file.getName().toUpperCase().indexOf("SOP") > -1) {
           workbook = new HSSFWorkbook(new FileInputStream(file));
           if (workbook.getSheetAt(0).getRow(0).getPhysicalNumberOfCells() == 3) {
             xmlService.readSOPSheet(workbook.getSheetAt(0));
           }
        } 
        if (workbook != null) {
          workbook.close();
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public HashMap<String, Integer> getExcelFieldNumber(String sheetName) {
    File file = new File("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\羅東博愛醫院\\麗臺HIS檔案匯出格式v2.xlsx");
    XSSFWorkbook workbook = null;
    try {
      workbook = new XSSFWorkbook(file);
      // DataFormatter formatter = new DataFormatter();

      int total = 0;

      XSSFSheet sheet = workbook.getSheet(sheetName);
      return readFieldNameAndIndex(sheet.getRow(0));
    } catch (InvalidFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (workbook != null) {
        try {
          workbook.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

}
